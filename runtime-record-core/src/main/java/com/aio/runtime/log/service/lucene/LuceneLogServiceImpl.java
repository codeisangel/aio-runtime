package com.aio.runtime.log.service.lucene;

import cn.aio1024.framework.basic.domain.page.KgoPage;
import cn.aio1024.framework.basic.domain.page.PageResult;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.func.LambdaUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aio.runtime.log.domain.AioLogAppendProperties;
import com.aio.runtime.log.domain.AioLogBo;
import com.aio.runtime.log.domain.AioLogVo;
import com.aio.runtime.log.domain.QueryLogParams;
import com.aio.runtime.log.service.AbstractAioLogService;
import com.aio.runtime.record.log.domain.constants.MappingLogFieldConstant;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.NRTCachingDirectory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.util.*;

/**
 * @author lzm
 * @desc lucene日志处理服务实现方式
 * @date 2024/08/08
 */
@Service
@Slf4j
public class LuceneLogServiceImpl extends AbstractAioLogService {

    // 日志目录名称
    public static final String LOG_CATALOGUE_NAME = "log";
    private static final String DAY_FORMAT = "yyyyMMdd";
    private static final String HOUR_FORMAT = "yyyyMMdd_HH";

    private static class Format {
        public static final String DAY = "yyyyMMdd";
        public static final String HOUR = "yyyyMMdd_HH";

        public static final String WEEK = "yyyyMM";
    }

    private static class Prefix {
        public static final String HOUR = "aio_log_hour_";
        public static final String DAY = "aio_log_day_";
        public static final String WEEK = "aio_log_week_";
    }

    private Date getHourIndexTime(String fileName) {
        if (!StringUtils.startsWith(fileName, Prefix.HOUR)) {
            return null;
        }
        String dateStr = StringUtils.substring(fileName, Prefix.HOUR.length(), Prefix.HOUR.length() + Format.HOUR.length());
        log.info("获取小时索引库 ： {} ", dateStr);
        return DateUtil.parse(dateStr, Format.HOUR);
    }
    private Date getDayIndexTime(String fileName) {
        if (!StringUtils.startsWith(fileName, Prefix.DAY)) {
            return null;
        }
        String dateStr = StringUtils.substring(fileName, Prefix.DAY.length(), Prefix.DAY.length() + Format.DAY.length());
        log.info("获取天索引库 ： {} ", dateStr);
        return DateUtil.parse(dateStr, Format.DAY);
    }
    private Date getWeekIndexTime(String fileName) {
        if (!StringUtils.startsWith(fileName, Prefix.WEEK)) {
            return null;
        }
        String dateStr = StringUtils.substring(fileName, Prefix.WEEK.length(), Prefix.WEEK.length() + Format.WEEK.length());
        log.info("获取周索引库 ： {} ", dateStr);
        return DateUtil.parse(dateStr, Format.WEEK);
    }

    @Autowired
    private AioLogAppendProperties logAppendProperties;

    // 项目工作目录
    @Value("${project.workspace.path}")
    private String projectWorkspace;

    private String getIndexName() {
        String indexName = StringUtils.equals(logAppendProperties.getIndexPeriod(), "hour")
                ? StrUtil.format("{}{}", Prefix.HOUR, DateUtil.format(new Date(), HOUR_FORMAT))
                : StrUtil.format("{}{}", Prefix.DAY, DateUtil.format(new Date(), DAY_FORMAT));
        return indexName;
    }


    private Collection<Document> loadDocs(List<AioLogBo> list) {
        Collection<Document> docs = new ArrayList<>();
        for (AioLogBo logBo : list) {
            Document document = new Document();
            document.add(new StringField("id", StringUtils.isBlank(logBo.getId()) ? IdUtil.getSnowflakeNextIdStr() : logBo.getId(), Field.Store.YES));

            document.add(new StringField("className", (logBo.getClassName() == null) ? "" : logBo.getClassName(), Field.Store.YES));
            document.add(new StringField("methodName", (logBo.getMethodName() == null) ? "" : logBo.getMethodName(), Field.Store.YES));
            document.add(new StringField("threadName", (logBo.getThreadName() == null) ? "" : logBo.getThreadName(), Field.Store.YES));
            document.add(new StringField("level", (logBo.getLevel() == null) ? "" : logBo.getLevel(), Field.Store.YES));
            document.add(new StringField("traceId", (logBo.getTraceId() == null) ? "" : logBo.getTraceId(), Field.Store.YES));

            document.add(new TextField("message", (logBo.getMessage() == null) ? "" : logBo.getMessage(), Field.Store.YES));

            document.add(new NumericDocValuesField("createTime", logBo.getCreateTime()));
            document.add(new StoredField("createTime", logBo.getCreateTime()));

            document.add(new StringField("marker", (logBo.getMarker() == null) ? "" : logBo.getMarker(), Field.Store.YES));

            if (ObjectUtil.isNotEmpty(logBo.getMdc())) {
                logBo.getMdc().forEach((k, v) -> {
                    if (StringUtils.isNoneBlank(k, v)) {
                        document.add(new StringField("mdc." + k, v, Field.Store.YES));
                    }
                });
            }

            docs.add(document);
        }
        return docs;
    }



    private void mergeIndex(Date date) {
        String todayStr = DateUtil.format(date, DAY_FORMAT);
        String indexPath = StrUtil.format("{}/{}/data/", projectWorkspace, LOG_CATALOGUE_NAME);
        String targetPath = StrUtil.format("{}/{}/data/aio_log_merge_{}", projectWorkspace, LOG_CATALOGUE_NAME, todayStr);
        if (FileUtil.exist(targetPath)) {
            log.info("合并日志索引，合并日志文件[ {} ]已存在", targetPath);
            return;
        }

        List<File> files = FileUtil.loopFiles(Paths.get(indexPath), 1, new FileFilter() {
            @Override
            public boolean accept(File logFile) {
                return StringUtils.contains(logFile.getName(), todayStr) && (!StringUtils.contains(logFile.getName(), "_log_merge"));
            }
        });
        if (files.isEmpty() || files.size() < 2) {
            log.info("合并日志索引，未发现日志索引文件。");
            return;
        }
        Set<String> fileNames = new HashSet<>();
        for (File file : files) {
            fileNames.add(file.getAbsolutePath());
        }

        try {
            // 创建目标索引目录
            Directory targetDir = FSDirectory.open(Paths.get(targetPath));
            // 配置 IndexWriter
            IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
            IndexWriter writer = new IndexWriter(targetDir, config);

            // 打开源索引目录
            List<Directory> sourceDirList = new ArrayList<>();
            for (String fileName : fileNames) {
                FSDirectory directory = FSDirectory.open(Paths.get(fileName));
                sourceDirList.add(directory);
                IndexReader reader = DirectoryReader.open(directory);
                int refCount = reader.numDocs();
                log.info("获取日志[ {} ]数量 ： {} ", fileName, refCount);
            }

            // 合并索引
            writer.addIndexes(sourceDirList.toArray(new Directory[sourceDirList.size()]));

            // 提交更改并关闭 IndexWriter
            writer.commit();
            writer.close();

            targetDir.close();
            for (Directory directory : sourceDirList) {
                directory.close();
            }

/*            for (File file : files) {
                boolean del = FileUtil.del(file);
                log.info("日志索引合并完成，删除索引库路径 ： {} ,删除结果 :[ {} ]",file.getName(),del);
            }*/

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 每天执行一次周索引合并，并且删除合并前的索引文件
     */
    @Scheduled(cron = "* * 2 * * ? ")
    private void mergeIndex4Week() {
        Date now = new Date();

        String indexPath = StrUtil.format("{}/{}/data/", projectWorkspace, LOG_CATALOGUE_NAME);
        //
        Map<Date, String> timeFileMap = new TreeMap<>();
        List<File> files = FileUtil.loopFiles(Paths.get(indexPath), 1, new FileFilter() {
            @Override
            public boolean accept(File logFile) {
                return (!StringUtils.contains(logFile.getName(), Prefix.WEEK));
            }
        });

        if (files.isEmpty() || files.size() < 2) {
            log.info("合并日志索引，未发现日志索引文件。");
            return;
        }

        for (File file : files) {
            if (StringUtils.startsWith(file.getName(), Prefix.WEEK)) {
                Date dayTime = getWeekIndexTime(file.getName());
                timeFileMap.put(dayTime, file.getAbsolutePath());
            }else if (StringUtils.startsWith(file.getName(), Prefix.DAY)) {
                Date dayTime = getDayIndexTime(file.getName());
                timeFileMap.put(dayTime, file.getAbsolutePath());
            }else if (StringUtils.startsWith(file.getName(), Prefix.HOUR)) {
                Date dayTime = getHourIndexTime(file.getName());
                timeFileMap.put(dayTime, file.getAbsolutePath());
            }
        }

        Map<String, Set<String>> weekMergeMap = new HashMap<>();
        for (Date date : timeFileMap.keySet()) {
            if (DateUtil.betweenDay(now,date,true) < 2){
                continue;
            }
            String month = DateUtil.format(date, Format.WEEK);
            int weekOfMonth = DateUtil.weekOfMonth(date);
            String targetPath = StrUtil.format("{}/{}/data/aio_log_week_{}_{}", projectWorkspace, LOG_CATALOGUE_NAME ,month ,weekOfMonth );
            if (weekMergeMap.containsKey(targetPath)) {
                weekMergeMap.get(targetPath).add(timeFileMap.get(date));
            }else {
                Set<String> sourcePathSet = new HashSet<>();
                sourcePathSet.add(timeFileMap.get(date));
                weekMergeMap.put(targetPath, sourcePathSet);
            }
        }

        log.info("周索引合并配置 ： {} ", JSON.toJSONString(weekMergeMap));
        for (String target : weekMergeMap.keySet()) {
            mergeIndex(target, weekMergeMap.get(target));
        }

    }
    private void mergeIndex(String targetPath, Set<String> sourcePathSet){
        try {
            // 创建目标索引目录
            Directory targetDir = FSDirectory.open(Paths.get(targetPath));
            // 配置 IndexWriter
            IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
            IndexWriter writer = new IndexWriter(targetDir, config);

            // 打开源索引目录
            List<Directory> sourceDirList = new ArrayList<>();
            for (String fileName : sourcePathSet) {
                FSDirectory directory = FSDirectory.open(Paths.get(fileName));
                sourceDirList.add(directory);
                IndexReader reader = DirectoryReader.open(directory);
                int refCount = reader.numDocs();
                log.info("获取日志[ {} ]数量 ： {} ", fileName, refCount);
            }

            // 合并索引
            writer.addIndexes(sourceDirList.toArray(new Directory[sourceDirList.size()]));

            // 提交更改并关闭 IndexWriter
            writer.commit();
            writer.close();

            targetDir.close();
            for (Directory directory : sourceDirList) {
                directory.close();
            }

            for (String file : sourcePathSet) {
                boolean del = FileUtil.del(file);
                log.info("日志索引合并完成，删除索引库路径 ： {} ,删除结果 :[ {} ]",file,del);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void batchSaveLogs(List<AioLogBo> aioLogList) {
        if (ObjectUtil.isEmpty(aioLogList)) {
            return;
        }
        String indexPath = StrUtil.format("{}/{}/data/{}", projectWorkspace, LOG_CATALOGUE_NAME, getIndexName());
        if (!FileUtil.exist(indexPath)) {
            FileUtil.mkdir(indexPath);
        }
        Collection<Document> docs = loadDocs(aioLogList);
        IndexWriter indexWriter = null;
        try {
            Directory directory = FSDirectory.open(FileSystems.getDefault().getPath(indexPath));
            NRTCachingDirectory nrtCachingDirectory = new NRTCachingDirectory(directory, 5, 60);
            SmartChineseAnalyzer smartChineseAnalyzer = new SmartChineseAnalyzer();
            IndexWriterConfig indexWriterConfig = new IndexWriterConfig(smartChineseAnalyzer);
            indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
            indexWriter = new IndexWriter(nrtCachingDirectory, indexWriterConfig);
            indexWriter.addDocuments(docs);
            indexWriter.forceMerge(1);
            indexWriter.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (indexWriter != null) {
                try {
                    indexWriter.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }

    @Override
    public void clearLogs() {
        String indexPath = StrUtil.format("{}/{}/data/", projectWorkspace, LOG_CATALOGUE_NAME);
        List<File> files = FileUtil.loopFiles(Paths.get(indexPath), 1, null);
        Date now = new Date();
        Date deadline = DateUtil.offsetDay(now, (0 - logAppendProperties.getPastDay()));
        for (File file : files) {
            String name = file.getName();
            name = name.replaceFirst("aio_log_", "");
            name = name.substring(0, 8);
            if (!NumberUtil.isNumber(name)) {
                continue;
            }
            Date indexTime = DateUtil.parse(name, DAY_FORMAT);
            if (ObjectUtil.isEmpty(indexTime)) {
                continue;
            }
            // 日志是否过期
            if (indexTime.before(deadline)) {
                boolean del = FileUtil.del(file);
                log.info("删除日志索引 ：索引目录名[ {} ] 是否删除成功[ {} ]", file.getName(), del);
            }

        }
    }

    @Override
    public PageResult<AioLogBo> getPage(QueryLogParams params, KgoPage page) {
        String indexPath = StrUtil.format("{}/{}/data/", projectWorkspace, LOG_CATALOGUE_NAME);
        List<File> files = FileUtil.loopFiles(Paths.get(indexPath), 1, null);
        List<IndexReader> indexReaderList = new ArrayList<>();
        try {
            for (File file : files) {
                Directory directory = FSDirectory.open(FileSystems.getDefault().getPath(file.getAbsolutePath()));
                IndexReader reader = DirectoryReader.open(directory);
                indexReaderList.add(reader);
            }
            IndexReader[] indexReaders = indexReaderList.toArray(new IndexReader[indexReaderList.size()]);
            MultiReader multiReader = new MultiReader(indexReaders);
            IndexSearcher searcher = new IndexSearcher(multiReader);

            BooleanQuery.Builder builder = builderQuery(params);

            Sort sort = new Sort();
            SortField sortField = new SortField("createTime", SortField.Type.LONG, true);
            sort.setSort(sortField);


            int start = (page.getPageNum() - 1) * page.getPageSize();
            int needTotal = page.getPageNum() * page.getPageSize();
            PageResult pageResult = new PageResult<>();
            log.debug("查询条件 ： {}  总数： {} ", builder.build().toString(), needTotal);

            TopDocs topDocs = searcher.search(builder.build(), needTotal, sort);
            ScoreDoc[] scoreDocs = topDocs.scoreDocs;
            List<AioLogVo> mappingRecordList = new ArrayList<>();
            pageResult.setTotal(topDocs.totalHits);

            if (start > topDocs.totalHits) {
                return pageResult;
            }

            long end = Math.min(topDocs.totalHits, needTotal);
            log.debug("查询数量 ： 分页参数[ {} ]开始[ {} - {} ] {} ", page, start, end, topDocs.totalHits);
            for (int i = start; i < end; i++) {
                Document doc = searcher.doc(scoreDocs[i].doc);
                mappingRecordList.add(docToRecord(doc));
            }

            pageResult.setList(mappingRecordList);
            return pageResult;
        } catch (Exception e) {
            log.error("日志查询失败。 异常类[ {} ] 异常信息 {} ", e.getClass(), e.getMessage());
        }

        return null;
    }

    private AioLogVo docToRecord(Document doc) {
        AioLogVo logBo = new AioLogVo();
        if (ObjectUtil.isEmpty(doc)) {
            return logBo;
        }
        if (StringUtils.isNotBlank(doc.get("id"))) {
            logBo.setId(doc.get("id"));
        }
        if (StringUtils.isNotBlank(doc.get("className"))) {
            logBo.setClassName(doc.get("className"));
        }
        if (StringUtils.isNotBlank(doc.get("methodName"))) {
            logBo.setMethodName(doc.get("methodName"));
        }
        if (StringUtils.isNotBlank(doc.get("threadName"))) {
            logBo.setThreadName(doc.get("threadName"));
        }
        if (StringUtils.isNotBlank(doc.get("level"))) {
            logBo.setLevel(doc.get("level"));
        }
        if (StringUtils.isNotBlank(doc.get("traceId"))) {
            logBo.setTraceId(doc.get("traceId"));
        }
        if (StringUtils.isNotBlank(doc.get("message"))) {
            logBo.setMessage(doc.get("message"));
        }
        if (StringUtils.isNotBlank(doc.get("marker"))) {
            logBo.setMarker(doc.get("marker"));
        }
        if (StringUtils.isNotBlank(doc.get("createTime"))) {
            String createTime = doc.get("createTime");
            if (NumberUtil.isNumber(createTime)) {
                Long timestamp = Long.valueOf(createTime);
                logBo.setCreateTime(new Date(timestamp));
                logBo.setCreateTimestamp(timestamp);
            }
        }
        setMdc(logBo, doc);
        return logBo;
    }

    private void setMdc(AioLogVo logBo, Document doc) {
        List<IndexableField> fields = doc.getFields();
        if (ObjectUtil.isEmpty(fields)) {
            return;
        }
        Map<String, String> mdc = new HashMap<>();
        for (IndexableField field : fields) {
            if (!StringUtils.startsWith(field.name(), "mdc.")) {
                continue;
            }
            String value = field.stringValue();
            String mdcName = StringUtils.remove(field.name(), "mdc.");
            mdc.put(mdcName, value);
        }
        if (ObjectUtil.isNotEmpty(mdc)) {
            logBo.setMdc(mdc);
        }

    }

    private BooleanQuery.Builder builderQuery(QueryLogParams params) {
        BooleanQuery.Builder builder = new BooleanQuery.Builder();


        if (StringUtils.isNotBlank(params.getTraceId())) {
            TermQuery termQuery = new TermQuery(new Term(MappingLogFieldConstant.TRACE_ID, params.getTraceId()));
            builder.add(termQuery, BooleanClause.Occur.MUST);
        }
        if (StringUtils.isNotBlank(params.getLevel())) {
            TermQuery termQuery = new TermQuery(new Term(LambdaUtil.getFieldName(AioLogBo::getLevel), params.getLevel()));
            builder.add(termQuery, BooleanClause.Occur.MUST);
        }
        if (StringUtils.isNotBlank(params.getMethodName())) {
            TermQuery termQuery = new TermQuery(new Term(LambdaUtil.getFieldName(AioLogBo::getMethodName), params.getMethodName()));
            builder.add(termQuery, BooleanClause.Occur.MUST);
        }

        if (StringUtils.isNotBlank(params.getThreadName())) {
            TermQuery termQuery = new TermQuery(new Term(LambdaUtil.getFieldName(AioLogBo::getThreadName), params.getThreadName()));
            builder.add(termQuery, BooleanClause.Occur.MUST);
        }

        if (StringUtils.isNotBlank(params.getMarker())) {
            TermQuery termQuery = new TermQuery(new Term(LambdaUtil.getFieldName(AioLogBo::getMarker), params.getMarker()));
            builder.add(termQuery, BooleanClause.Occur.MUST);
        }

        if (StringUtils.isNotBlank(params.getClassName())) {
            WildcardQuery wildcardQuery = new WildcardQuery(new Term(LambdaUtil.getFieldName(AioLogBo::getClassName), StrUtil.format("*{}*", params.getClassName())));
            builder.add(wildcardQuery, BooleanClause.Occur.MUST);
        }

        if (ObjectUtil.isNotEmpty(params.getMdc())) {
            params.getMdc().forEach((k, v) -> {
                WildcardQuery wildcardQuery = new WildcardQuery(new Term("mdc." + k, StrUtil.format("*{}*", v)));
                builder.add(wildcardQuery, BooleanClause.Occur.MUST);
            });
        }

        if (ObjectUtil.isNotEmpty(params.getKeywords())) {
            SmartChineseAnalyzer analyzer = new SmartChineseAnalyzer();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < params.getKeywords().size(); i++) {
                String keyword = params.getKeywords().get(i);
                if (i == 0) {
                    sb.append(String.format("\"%s\"", keyword));
                } else {
                    sb.append(String.format(" AND \"%s\"", keyword));
                }
            }
            QueryParser parser = new QueryParser(LambdaUtil.getFieldName(AioLogBo::getMessage), analyzer);
            String queryConditions = sb.toString();
            // 创建查询对象
            try {
                Query contentQuery = parser.parse(queryConditions);

                builder.add(contentQuery, BooleanClause.Occur.MUST);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

        }


        String today = DateUtil.today();
        if (ObjectUtil.isEmpty(params.getCreateToTime())) {
            DateTime dateTime = DateUtil.offsetDay(DateUtil.parseDate(today), 1);
            params.setCreateToTime(dateTime.getTime());
        }
        if (ObjectUtil.isEmpty(params.getCreateFromTime())) {
            DateTime dateTime = DateUtil.parseDate(today);
            params.setCreateFromTime(dateTime.getTime());
        }

        Query range = NumericDocValuesField.newSlowRangeQuery(LambdaUtil.getFieldName(AioLogBo::getCreateTime), params.getCreateFromTime(), params.getCreateToTime());
        builder.add(range, BooleanClause.Occur.MUST);

        return builder;
    }
}
