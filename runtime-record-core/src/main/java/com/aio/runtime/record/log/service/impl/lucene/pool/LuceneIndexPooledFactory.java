package com.aio.runtime.record.log.service.impl.lucene.pool;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.lucene.index.MultiReader;

/**
 * @author lzm
 * @desc lucene索引对象缓存池工厂
 * @date 2024/07/31
 */
public class LuceneIndexPooledFactory extends BasePooledObjectFactory<MultiReader> {
    @Override
    public MultiReader create() throws Exception {
        return null;
    }

    @Override
    public PooledObject<MultiReader> wrap(MultiReader multiReader) {
        return new DefaultPooledObject<>(multiReader);
    }

    @Override
    public void destroyObject(PooledObject<MultiReader> p) throws Exception {
        super.destroyObject(p);
        p.getObject().close();
    }
}
