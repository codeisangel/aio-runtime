package com.aio.runtime.record.log.service.impl.lucene.pool.writer;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.lucene.index.IndexWriter;

/**
 * @author lzm
 * @desc Lucene写索引缓存池
 * @date 2024/07/31
 */
public class LuceneIndexWriterPool {
    private GenericObjectPool<IndexWriter> pool;
    public LuceneIndexWriterPool(){
        LuceneIndexWriterPooledFactory factory = new LuceneIndexWriterPooledFactory();
        pool = new GenericObjectPool<>(factory);
        pool.setMaxTotal(10); // 最大池大小
        pool.setMinIdle(2); // 最小空闲对象
        pool.setMaxIdle(5); // 最大空闲对象
    }

}
