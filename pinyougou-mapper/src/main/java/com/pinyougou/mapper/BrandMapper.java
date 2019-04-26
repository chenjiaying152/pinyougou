package com.pinyougou.mapper;

import com.pinyougou.pojo.Brand;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 数据访问接口
 */
public interface BrandMapper extends Mapper<Brand> {

    /** 多条件查询品牌 */
    List<Brand> findAll(Brand brand);

    /** 批量删除品牌 */
    void deleteAll(Serializable[] ids);

    /** 查询品牌列表 */
    @Select("select id, name as text from tb_brand")
    List<Map<String,Object>> findAllByIdAndName();

}
