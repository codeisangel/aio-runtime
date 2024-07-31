package com.aio.runtime.record.log.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aio.runtime.record.log.domain.MappingRecordBo;
import com.aio.runtime.record.log.domain.MappingRecordVo;
import com.aio.runtime.record.log.domain.QueryRecordParams;
import com.aio.runtime.record.log.domain.RecordInfoVo;
import com.aio.runtime.record.log.domain.constants.MappingLogFieldConstant;
import com.aio.runtime.record.log.service.AbstractMappingLogService;
import com.alibaba.fastjson.JSONObject;
import com.kgo.flow.common.domain.constants.ProjectWorkSpaceConstants;
import com.kgo.flow.common.domain.page.KgoPage;
import com.kgo.flow.common.domain.page.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.NRTCachingDirectory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @author lzm
 * @desc Lucene记录存储服务
 * @date 2024/05/13
 */
@Service
@Slf4j
public class LuceneMappingLogService extends AbstractMappingLogService {
    public static final String MAPPING_LOG_CATALOGUE_NAME = "mappingLog";
    @Value(ProjectWorkSpaceConstants.CONFIG_PATH_SPEL)
    private String projectWorkspace;

    @Override
    public void batchSave(List<MappingRecordBo> recordList) {

        if (ObjectUtil.isEmpty(recordList)) {
            return;
        }
        if (!FileUtil.exist(projectWorkspace)) {
            log.error("项目工作空间目录不存在 ： {} ", projectWorkspace);
            return;
        }

        Collection<Document> docs = buildDocuments(recordList);
        if (ObjectUtil.isEmpty(docs)) {
            return;
        }

        IndexWriter indexWriter = null;
        try {
            File file = FileUtil.file(projectWorkspace);
            Path path = Paths.get(file.getAbsolutePath()).resolve(MAPPING_LOG_CATALOGUE_NAME);
            Directory directory = FSDirectory.open(path);
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
                }catch (IOException ioe){
                    log.error("关闭Lucene 异常，{} ",ioe.getMessage());
                }
            }
        }


    }

    @Override
    public PageResult searchRecords(QueryRecordParams params, KgoPage page) {
        File file = FileUtil.file(projectWorkspace);
        Path path = Paths.get(file.getAbsolutePath()).resolve(MAPPING_LOG_CATALOGUE_NAME);

        BooleanQuery.Builder builder = builderQuery(params);

        Sort sort = new Sort();
        SortField sortField = new SortField("createTimestamp", SortField.Type.LONG, true);
        sort.setSort(sortField);

        int start = (page.getPageNum() - 1) * page.getPageSize();
        int needTotal = page.getPageNum() * page.getPageSize();
        PageResult pageResult = new PageResult<>();
        log.debug("查询条件 ： {} ", builder.build().toString());
        try {
            Directory directory = FSDirectory.open(path);
            DirectoryReader reader = DirectoryReader.open(directory);
            IndexSearcher searcher = new IndexSearcher(reader);
            TopDocs topDocs = searcher.search(builder.build(), needTotal, sort);
            ScoreDoc[] scoreDocs = topDocs.scoreDocs;
            List<MappingRecordVo> mappingRecordList = new ArrayList<>();
            pageResult.setTotal(topDocs.totalHits);

            if (start > topDocs.totalHits) {
                return pageResult;
            }

            long end = Math.min(topDocs.totalHits,needTotal);
            log.debug("查询数量 ： 分页参数[ {} ]开始[ {} - {} ] {} ",page,start,end, topDocs.totalHits);
            for (int i = start; i < end; i++) {
                Document doc = searcher.doc(scoreDocs[i].doc);
                mappingRecordList.add(docToRecord(doc));
            }

            pageResult.setList(mappingRecordList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return pageResult;
    }

    @Override
    public Object getRecordInfo() {
        File file = FileUtil.file(projectWorkspace);
        Path path = Paths.get(file.getAbsolutePath()).resolve(MAPPING_LOG_CATALOGUE_NAME);
        File mappingFile = path.toFile();
        long size = FileUtil.size(mappingFile);
        long kbSize = size / 1024;
        RecordInfoVo recordInfoVo = new RecordInfoVo();
        recordInfoVo.setSize(kbSize);
        return recordInfoVo;
    }

    private BooleanQuery.Builder builderQuery(QueryRecordParams params) {
        BooleanQuery.Builder builder = new BooleanQuery.Builder();

        if (StringUtils.isNotBlank(params.getUserId())) {
            TermQuery termQuery = new TermQuery(new Term(MappingLogFieldConstant.USER_ID, params.getUserId()));
            builder.add(termQuery, BooleanClause.Occur.MUST);
        }

        if (StringUtils.isNotBlank(params.getCompanyId())) {
            TermQuery termQuery = new TermQuery(new Term(MappingLogFieldConstant.COMPANY_ID, params.getCompanyId()));
            builder.add(termQuery, BooleanClause.Occur.MUST);
        }

        if (StringUtils.isNotBlank(params.getUserName())) {
            FuzzyQuery termQuery = new FuzzyQuery(new Term(MappingLogFieldConstant.USER_NAME, params.getUserName()));
            builder.add(termQuery, BooleanClause.Occur.MUST);
        }

        if (StringUtils.isNotBlank(params.getDeprecated())) {
            FuzzyQuery termQuery = new FuzzyQuery(new Term(MappingLogFieldConstant.DEPRECATED, params.getDeprecated()));
            builder.add(termQuery, BooleanClause.Occur.MUST);
        }

        if (StringUtils.isNotBlank(params.getCompanyName())) {
            FuzzyQuery termQuery = new FuzzyQuery(new Term(MappingLogFieldConstant.COMPANY_NAME, params.getCompanyName()));
            builder.add(termQuery, BooleanClause.Occur.MUST);
        }
        // 表达式模糊查询
        if (StringUtils.isNotBlank(params.getMappingClass())) {
            WildcardQuery wildcardQuery = new WildcardQuery(new Term(MappingLogFieldConstant.MAPPING_CLASS, StrUtil.format("*{}*", params.getMappingClass())));
            builder.add(wildcardQuery, BooleanClause.Occur.MUST);
        }
        if (StringUtils.isNotBlank(params.getUrl())) {
            WildcardQuery wildcardQuery = new WildcardQuery(new Term(MappingLogFieldConstant.URL, StrUtil.format("*{}*", params.getUrl())));
            builder.add(wildcardQuery, BooleanClause.Occur.MUST);
        }

        if (StringUtils.isNotBlank(params.getSuccess())) {
            TermQuery termQuery = new TermQuery(new Term(MappingLogFieldConstant.SUCCESS, params.getSuccess()));
            builder.add(termQuery, BooleanClause.Occur.MUST);
        }
        if (StringUtils.isNotBlank(params.getTraceId())) {
            TermQuery termQuery = new TermQuery(new Term(MappingLogFieldConstant.TRACE_ID, params.getTraceId()));
            builder.add(termQuery, BooleanClause.Occur.MUST);
        }
        if (StringUtils.isNotBlank(params.getHttpMethod())) {
            TermQuery termQuery = new TermQuery(new Term(MappingLogFieldConstant.HTTP_METHOD, params.getHttpMethod()));
            builder.add(termQuery, BooleanClause.Occur.MUST);
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

        Query range = NumericDocValuesField.newSlowRangeQuery(MappingLogFieldConstant.CREATE_TIMESTAMP, params.getCreateFromTime(), params.getCreateToTime());
        builder.add(range, BooleanClause.Occur.MUST);

        return builder;
    }

    private MappingRecordVo docToRecord(Document doc) {
        MappingRecordVo recordBo = new MappingRecordVo();
        if (ObjectUtil.isEmpty(doc)) {
            return recordBo;
        }
        if (StringUtils.isNotBlank(doc.get("id"))) {
            recordBo.setId(doc.get("id"));
        }

        if (StringUtils.isNotBlank(doc.get("companyName"))) {
            recordBo.setCompanyName(doc.get("companyName"));
        }
        if (StringUtils.isNotBlank(doc.get("companyId"))) {
            recordBo.setCompanyId(doc.get("companyId"));
        }

        if (StringUtils.isNotBlank(doc.get("userName"))) {
            recordBo.setUserName(doc.get("userName"));
        }
        if (StringUtils.isNotBlank(doc.get("userId"))) {
            recordBo.setUserId(doc.get("userId"));
        }
        if (StringUtils.isNotBlank(doc.get("params"))) {
            recordBo.setParams(doc.get("params"));
        }
        if (StringUtils.isNotBlank(doc.get(MappingLogFieldConstant.DEPRECATED))) {
            recordBo.setDeprecated(doc.get(MappingLogFieldConstant.DEPRECATED));
        }
        if (StringUtils.isNotBlank(doc.get("traceId"))) {
            recordBo.setTraceId(doc.get("traceId"));
        }

        if (StringUtils.isNotBlank(doc.get("url"))) {
            recordBo.setUrl(doc.get("url"));
        }

        if (StringUtils.isNotBlank(doc.get("mappingMethod"))) {
            recordBo.setMappingMethod(doc.get("mappingMethod"));
        }

        if (StringUtils.isNotBlank(doc.get("mappingClass"))) {
            recordBo.setMappingClass(doc.get("mappingClass"));
        }
        if (StringUtils.isNotBlank(doc.get("httpMethod"))) {
            recordBo.setHttpMethod(doc.get("httpMethod"));
        }
        if (StringUtils.isNotBlank(doc.get("success"))) {
            recordBo.setSuccess(doc.get("success"));
        }
        if (StringUtils.isNotBlank(doc.get("result"))) {
            recordBo.setResult(doc.get("result"));
        }

        if (StringUtils.isNotBlank(doc.get("stackTrace"))) {
            Object parse = JSONObject.parse(doc.get("stackTrace"));
            recordBo.setStackTrace(parse);
        }

        if (StringUtils.isNotBlank(doc.get("throwable"))) {
            recordBo.setThrowable(doc.get("throwable"));
        }
        if (StringUtils.isNotBlank(doc.get("exceptionMsg"))) {
            recordBo.setExceptionMsg(doc.get("exceptionMsg"));
        }


        if (StringUtils.isNotBlank(doc.get("createTimestamp"))) {
            Long createTimestamp = Long.valueOf(doc.get("createTimestamp"));
            recordBo.setCreateTime(DateUtil.formatDateTime(new Date(createTimestamp)));
        }

        return recordBo;
    }

    private Collection<Document> buildDocuments(List<MappingRecordBo> recordList) {
        Collection<Document> docs = new ArrayList<>();
        if (ObjectUtil.isEmpty(recordList)) {
            return docs;
        }
        for (MappingRecordBo record : recordList) {
            Document document = new Document();
            document.add(new StringField(MappingLogFieldConstant.ID, StringUtils.isBlank(record.getId()) ? IdUtil.objectId() : record.getId(), Field.Store.YES));

            document.add(new NumericDocValuesField("createTimestamp", record.getCreateTimestamp()));
            document.add(new StoredField("createTimestamp", record.getCreateTimestamp()));

            document.add(new StringField("httpMethod", StringUtils.isBlank(record.getHttpMethod()) ? "" : record.getHttpMethod(), Field.Store.YES));
            document.add(new StringField("url", StringUtils.isBlank(record.getUrl()) ? "" : record.getUrl(), Field.Store.YES));

            document.add(new StringField("userId", StringUtils.isBlank(record.getUserId()) ? "" : record.getUserId(), Field.Store.YES));
            document.add(new StringField("userName", StringUtils.isBlank(record.getUserName()) ? "" : record.getUserName(), Field.Store.YES));
            document.add(new StringField("companyId", StringUtils.isBlank(record.getCompanyId()) ? "" : record.getCompanyId(), Field.Store.YES));
            document.add(new StringField("companyName", StringUtils.isBlank(record.getCompanyName()) ? "" : record.getCompanyName(), Field.Store.YES));

            document.add(new StringField("mappingClass", StringUtils.isBlank(record.getMappingClass()) ? "" : record.getMappingClass(), Field.Store.YES));
            document.add(new StringField("mappingMethod", StringUtils.isBlank(record.getMappingMethod()) ? "" : record.getMappingMethod(), Field.Store.YES));
            document.add(new TextField("params", StringUtils.isBlank(record.getParams()) ? "" : record.getParams(), Field.Store.YES));
            document.add(new StringField("success", ObjectUtil.isNotNull(record.getSuccess()) && record.getSuccess() ? "YES" : "NO", Field.Store.YES));
            document.add(new StringField("stackTrace", StringUtils.isBlank(record.getStackTrace()) ? "" : record.getStackTrace(), Field.Store.YES));
            document.add(new StringField("exceptionMsg", StringUtils.isBlank(record.getExceptionMsg()) ? "" : record.getExceptionMsg(), Field.Store.YES));
            document.add(new StringField("throwable", StringUtils.isBlank(record.getThrowable()) ? "" : record.getThrowable(), Field.Store.YES));

            document.add(new StringField("traceId", StringUtils.isBlank(record.getTraceId()) ? "" : record.getTraceId(), Field.Store.YES));

            document.add(new StringField(MappingLogFieldConstant.DEPRECATED, ObjectUtil.isNotNull(record.getDeprecated()) && record.getDeprecated() ? "YES" : "NO", Field.Store.YES));

            document.add(new TextField(MappingLogFieldConstant.RESULT, StringUtils.isBlank(record.getResult()) ? "" : record.getResult(), Field.Store.YES));
            docs.add(document);
        }
        return docs;
    }
}
