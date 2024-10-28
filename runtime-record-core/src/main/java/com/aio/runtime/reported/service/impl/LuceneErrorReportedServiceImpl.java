package com.aio.runtime.reported.service.impl;

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
import com.aio.runtime.log.domain.AioLogBo;
import com.aio.runtime.record.log.domain.constants.MappingLogFieldConstant;
import com.aio.runtime.reported.domain.dao.ErrorReportDo;
import com.aio.runtime.reported.domain.params.QueryErrorParams;
import com.aio.runtime.reported.domain.vo.ErrorReportVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.NRTCachingDirectory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @author lzm
 * @desc 错误上报服务
 * @date 2024/10/26
 */
@Service
@Slf4j
public class LuceneErrorReportedServiceImpl extends AbstractErrorReportedServiceImpl {
    @Value("${project.workspace.path}")
    private String projectWorkspace;
    public static final String MONTH_FORMAT = "yyyyMM";
    public static final String LOG_CATALOGUE_NAME = "errorReported";
    private String getIndexName() {
        return StrUtil.format("{}{}", "error_reported_", DateUtil.format(new Date(), MONTH_FORMAT));
    }
    @Override
    public PageResult getPage(QueryErrorParams params, KgoPage page) {
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
            List<ErrorReportVo> mappingRecordList = new ArrayList<>();
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
            log.error("错误上报记录查询失败。 异常类[ {} ] 异常信息 {} ", e.getClass(), e.getMessage());
        }

        return null;
    }

    private BooleanQuery.Builder builderQuery(QueryErrorParams params) {
        BooleanQuery.Builder builder = new BooleanQuery.Builder();


        if (StringUtils.isNotBlank(params.getTraceId())) {
            TermQuery termQuery = new TermQuery(new Term(MappingLogFieldConstant.TRACE_ID, params.getTraceId()));
            builder.add(termQuery, BooleanClause.Occur.MUST);
        }
        if (StringUtils.isNotBlank(params.getType())) {
            TermQuery termQuery = new TermQuery(new Term(LambdaUtil.getFieldName(ErrorReportDo::getType), params.getType()));
            builder.add(termQuery, BooleanClause.Occur.MUST);
        }
        if (StringUtils.isNotBlank(params.getPlatform())) {
            TermQuery termQuery = new TermQuery(new Term(LambdaUtil.getFieldName(ErrorReportDo::getPlatform), params.getPlatform()));
            builder.add(termQuery, BooleanClause.Occur.MUST);
        }

        if (StringUtils.isNotBlank(params.getTerminal())) {
            TermQuery termQuery = new TermQuery(new Term(LambdaUtil.getFieldName(ErrorReportDo::getTerminal), params.getTerminal()));
            builder.add(termQuery, BooleanClause.Occur.MUST);
        }
        if (StringUtils.isNotBlank(params.getLevel())) {
            TermQuery termQuery = new TermQuery(new Term(LambdaUtil.getFieldName(ErrorReportDo::getLevel), params.getLevel()));
            builder.add(termQuery, BooleanClause.Occur.MUST);
        }

        if (StringUtils.isNotBlank(params.getRemark())) {
            SmartChineseAnalyzer analyzer = new SmartChineseAnalyzer();
            String queryCondition = String.format("\"%s\"", params.getRemark());
            QueryParser parser = new QueryParser(LambdaUtil.getFieldName(ErrorReportDo::getRemark), analyzer);
            try {
                Query contentQuery = parser.parse(queryCondition);
                builder.add(contentQuery, BooleanClause.Occur.MUST);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

        }
        if (StringUtils.isNotBlank(params.getApiUrl())) {
            WildcardQuery wildcardQuery = new WildcardQuery(new Term(LambdaUtil.getFieldName(ErrorReportDo::getApiUrl), StrUtil.format("*{}*", params.getApiUrl())));
            builder.add(wildcardQuery, BooleanClause.Occur.MUST);
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
            QueryParser parser = new QueryParser(LambdaUtil.getFieldName(ErrorReportDo::getMessage), analyzer);
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
    private ErrorReportVo docToRecord(Document doc) {
        ErrorReportVo reportVo = new ErrorReportVo();
        if (ObjectUtil.isEmpty(doc)) {
            return reportVo;
        }
        if (StringUtils.isNotBlank(doc.get("token"))) {
            reportVo.setToken(doc.get("token"));
        }
        if (StringUtils.isNotBlank(doc.get("apiUrl"))) {
            reportVo.setApiUrl(doc.get("apiUrl"));
        }
        if (StringUtils.isNotBlank(doc.get("id"))) {
            reportVo.setId(doc.get("id"));
        }
        if (StringUtils.isNotBlank(doc.get("type"))) {
            reportVo.setType(doc.get("type"));
        }
        if (StringUtils.isNotBlank(doc.get("terminal"))) {
            reportVo.setTerminal(doc.get("terminal"));
        }
        if (StringUtils.isNotBlank(doc.get("terminalInfo"))) {
            reportVo.setTerminalInfo(doc.get("terminalInfo"));
        }
        if (StringUtils.isNotBlank(doc.get("level"))) {
            reportVo.setLevel(doc.get("level"));
        }
        if (StringUtils.isNotBlank(doc.get("traceId"))) {
            reportVo.setTraceId(doc.get("traceId"));
        }
        if (StringUtils.isNotBlank(doc.get("message"))) {
            reportVo.setMessage(doc.get("message"));
        }
        if (StringUtils.isNotBlank(doc.get("platform"))) {
            reportVo.setPlatform(doc.get("platform"));
        }
        if (StringUtils.isNotBlank(doc.get("remark"))) {
            reportVo.setRemark(doc.get("remark"));
        }
        if (StringUtils.isNotBlank(doc.get("createTime"))) {
            String createTime = doc.get("createTime");
            if (NumberUtil.isNumber(createTime)) {
                Long timestamp = Long.valueOf(createTime);
                reportVo.setCreateTime(new Date(timestamp));
            }
        }

        return reportVo;
    }

    @Override
    public void batchSaveError(List<ErrorReportDo> recordList) {
        if (ObjectUtil.isEmpty(recordList)) {
            return;
        }
        String indexPath = StrUtil.format("{}/{}/data/{}", projectWorkspace, LOG_CATALOGUE_NAME, getIndexName());
        if (!FileUtil.exist(indexPath)) {
            FileUtil.mkdir(indexPath);
        }
        Collection<Document> docs =  loadDocs(recordList);
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

    private Collection<Document> loadDocs(List<ErrorReportDo> list) {
        Collection<Document> docs = new ArrayList<>();
        for (ErrorReportDo reportDo : list) {
            if (ObjectUtil.isNull(reportDo)){
                continue;
            }
            Document document = new Document();
            document.add(new StringField("id", StringUtils.isBlank(reportDo.getId()) ? IdUtil.getSnowflakeNextIdStr() : reportDo.getId(), Field.Store.YES));

            document.add(new StringField("level", (reportDo.getLevel() == null) ? "" : reportDo.getLevel(), Field.Store.YES));
            document.add(new StringField("type", (reportDo.getType() == null) ? "" : reportDo.getType(), Field.Store.YES));
            document.add(new StringField("apiUrl", (reportDo.getApiUrl() == null) ? "" : reportDo.getApiUrl(), Field.Store.YES));
            document.add(new StringField("token", (reportDo.getToken() == null) ? "" : reportDo.getToken(), Field.Store.YES));
            document.add(new StringField("traceId", (reportDo.getTraceId() == null) ? "" : reportDo.getTraceId(), Field.Store.YES));
            document.add(new StringField("terminal", (reportDo.getTerminal() == null) ? "" : reportDo.getTerminal(), Field.Store.YES));

            document.add(new StringField("platform", (reportDo.getPlatform() == null) ? "" : reportDo.getPlatform(), Field.Store.YES));

            document.add(new TextField("message", (reportDo.getMessage() == null) ? "" : reportDo.getMessage(), Field.Store.YES));
            document.add(new TextField("terminalInfo", (reportDo.getTerminalInfo() == null) ? "" : reportDo.getTerminalInfo(), Field.Store.YES));
            document.add(new TextField("remark", (reportDo.getRemark() == null) ? "" : reportDo.getRemark(), Field.Store.YES));

            document.add(new NumericDocValuesField("createTime", reportDo.getCreateTime().getTime()));
            document.add(new StoredField("createTime", reportDo.getCreateTime().getTime()));

            docs.add(document);
        }
        return docs;
    }
}
