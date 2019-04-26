package com.pinyougou.mapper;

import com.pinyougou.pojo.ItemCat;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * ItemCatMapper 数据访问接口
 * @date 2019-04-02 16:57:15
 * @version 1.0
 */
public interface ItemCatMapper extends Mapper<ItemCat>{

    /** 根据父级id查询商品分类 */
    @Select("select * from tb_item_cat where parent_id = #{parentId}")
    List<ItemCat> findItemCatByParentId(Long parentId);
}