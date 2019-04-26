package com.pinyougou.mapper;

import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import com.pinyougou.pojo.TypeTemplate;

import java.io.Serializable;
import java.util.List;

/**
 * TypeTemplateMapper 数据访问接口
 * @date 2019-04-02 16:57:15
 * @version 1.0
 */
public interface TypeTemplateMapper extends Mapper<TypeTemplate>{

    /** 多条件分页查询类型模板 */
    List<TypeTemplate> findAll(TypeTemplate typeTemplate);

    /** 批量删除模板 */
    void deleteAll(@Param("ids") Serializable[] ids);
}