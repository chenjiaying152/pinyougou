package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.pojo.Goods;
import com.pinyougou.service.GoodsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

/**
 * 商品控制器
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Reference(timeout = 10000)
    private GoodsService goodsService;
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private Destination solrQueue;
    @Autowired
    private Destination solrDeleteQueue;
    @Autowired
    private Destination pageTopic;
    @Autowired
    private Destination pageDeleteTopic;

    /**
     * 添加商品
     */
    @PostMapping("/save")
    public boolean save(@RequestBody Goods goods){
        try {
            //获取登录用户名
            String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
            goods.setSellerId(sellerId);
            goodsService.save(goods);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 按条件分页查询商品
     */
    @GetMapping("/findByPage")
    public PageResult findByPage(Goods goods, Integer page, Integer rows ){
        /** 获取登录商家编号 */
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
        /** 添加查询条件 */
        goods.setSellerId(sellerId);
        /**  get 请求中文转码 */
        if (StringUtils.isNoneBlank(goods.getGoodsName())){
            try {
                goods.setGoodsName(new String(goods.getGoodsName().getBytes("ISO8859-1"),"utf-8"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        /** 调用服务层方法查询 */
       return goodsService.findByPage(goods,page,rows);
    }

    /** 上下架商品 */
    @GetMapping("/updateMarketable")
    public boolean updateMarketable(Long[] ids,String status ){
        try {
                goodsService.updateStatus(status,ids,"is_marketable");


            //判断商品上下架状态
            if ("1".equals(status)){//表示商品上架
                //发送消息,生成商品索引
                jmsTemplate.send(solrQueue, new MessageCreator() {
                    @Override
                    public Message createMessage(Session session) throws JMSException {
                        return session.createObjectMessage(ids);
                    }
                });

                /** 发送消息, 生成静态网页 */
                for (Long goodsId : ids) {
                    jmsTemplate.send(pageTopic, new MessageCreator() {
                        @Override
                        public Message createMessage(Session session) throws JMSException {
                            return session.createTextMessage(goodsId.toString());
                        }
                    });
                }

            }else {//表示商品下架
                //发送消息, 删除商品索引
                jmsTemplate.send(solrDeleteQueue, new MessageCreator() {
                    @Override
                    public Message createMessage(Session session) throws JMSException {
                        return session.createObjectMessage(ids);
                    }
                });

                //发送消息, 删除商品索引
                jmsTemplate.send(pageDeleteTopic, new MessageCreator() {
                    @Override
                    public Message createMessage(Session session) throws JMSException {
                        return session.createObjectMessage(ids);
                    }
                });
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
