package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.Seller;
import com.pinyougou.service.SellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商家控制器
 */
@RestController
@RequestMapping("/seller")
public class SellerController {

    /** 注入商家服务接口代理对象 */
    @Reference(timeout = 10000)
    private SellerService sellerService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /** 商家入驻 */
    @PostMapping("/save")
    public Boolean save(@RequestBody Seller seller){
        try {
            String password = passwordEncoder.encode(seller.getPassword());
            seller.setPassword(password);
            sellerService.save(seller);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
