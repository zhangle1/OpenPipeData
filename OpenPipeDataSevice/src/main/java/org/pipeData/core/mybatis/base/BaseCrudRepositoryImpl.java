package org.pipeData.core.mybatis.base;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.pipeData.core.entity.BaseEntity;
import org.pipeData.security.manager.OpenPipeSecurityManager;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;

public class BaseCrudRepositoryImpl<M extends BaseMapper<T>, T extends BaseEntity>
        extends ServiceImpl<M, T> implements BaseCrudRepository<T> {


    @Override
    public boolean save(T entity) {
        return super.save(entity);
    }

    @Override
    public boolean saveBatch(Collection<T> entityList) {
        return super.saveBatch(entityList);
    }

    @Override
    public boolean updateById(T entity) {
        return super.updateById(entity);
    }


    @Override
    public boolean updateBatchById(Collection<T> entityList) {
        return super.updateBatchById(entityList);
    }

    @Override
    public boolean saveOrUpdate(T entity) {
        return super.saveOrUpdate(entity);
    }


    @Override
    public boolean saveOrUpdateBatch(Collection<T> entityList) {
        return super.saveOrUpdateBatch(entityList);
    }
}
