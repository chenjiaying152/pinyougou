package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.pojo.Goods;
import com.pinyougou.service.GoodsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商品控制器
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Reference(timeout = 10000)
    private GoodsService goodsService;

    /** 根据条件分页查询待审核的商品 */
    @GetMapping("/findByPage")
    public PageResult findByPage(Goods goods, Integer page, Integer rows){
        //设置状态码
        goods.setAuditStatus("0");

        if (StringUtils.isNoneBlank(goods.getGoodsName())){
            try {
                goods.setGoodsName(new String(goods.getGoodsName().getBytes("ISO8859-1"),"utf-8"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return goodsService.findByPage(goods,page,rows);
    }

    /** 商品审核与驳回 */
    @GetMapping("/updateStatus")
    public boolean updateStatus(String status, Long[] ids){

        try {
            goodsService.updateStatus(status,ids,"audit_status");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /** 删除商品(修改商品删除的状态码) */
    @GetMapping("/delete")
    public boolean delete(Long[] ids){
        try {
            goodsService.updateStatus("1",ids,"is_delete");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
