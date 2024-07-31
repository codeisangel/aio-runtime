package com.aio.runtime.record.log.service.impl.lucene.pool.writer;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.lucene.index.IndexWriter;

/**
 * @author lzm
 * @desc lucene索引对象缓存池工厂
 * @date 2024/07/31
 */
public class LuceneIndexWriterPooledFactory extends BasePooledObjectFactory<IndexWriter> {
    @Override
    public IndexWriter create() throws Exception {
        return null;
    }

    @Override
    public PooledObject<IndexWriter> wrap(IndexWriter indexWriter) {
        return new DefaultPooledObject<>(indexWriter);
    }

    @Override
    public void destroyObject(PooledObject<IndexWriter> p) throws Exception {
        super.destroyObject(p);
        p.getObject().close();
    }
}
