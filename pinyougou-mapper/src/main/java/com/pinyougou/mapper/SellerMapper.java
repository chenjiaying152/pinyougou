package com.pinyougou.mapper;

import com.pinyougou.pojo.Seller;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * SellerMapper 数据访问接口
 * @date 2019-04-02 16:57:15
 * @version 1.0
 */
public interface SellerMapper extends Mapper<Seller>{

    /** 多条件查询商家 */
    List<Seller> findAll(Seller seller);

    /** 审核商家 */
    @Update("update tb_seller set status = #{status} where seller_id = #{sellerId}")
    void updateStatus(@Param("sellerId") String sellerId, @Param("status")String status);
}