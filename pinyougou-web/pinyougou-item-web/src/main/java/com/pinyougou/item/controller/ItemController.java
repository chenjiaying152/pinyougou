package com.pinyougou.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.service.GoodsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@Controller
public class ItemController {

    @Reference(timeout = 1000)
    private GoodsService goodsService;

    /** 根据id查询商品信息 */
    @GetMapping("/{goodsId}")
    public String getGoods(@PathVariable("goodsId")Long goodsId, Model model){
        //model: 就是FreeMarker的数据模型
        //springmvc 会根据视图解析器把model中的数据存放的位置不一样
        Map<String, Object> data = goodsService.getGoods(goodsId);
        model.addAllAttributes(data);

        return "item";
    }
}
