package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.pojo.TypeTemplate;
import com.pinyougou.service.TypeTemplateService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

/**
 * 类型模板控制器
 */
@RestController
@RequestMapping("/typeTemplate")
public class TypeTemplateController {

    @Reference(timeout = 10000)
    private TypeTemplateService typeTemplateService;

    /**
     * 多条件分页查询类型模板
     */
    @GetMapping("/findByPage")
    public PageResult findByPage(TypeTemplate typeTemplate, Integer page, Integer rows) {
        //get请求中文乱码
        try {
            if (StringUtils.isNoneBlank(typeTemplate.getName())) {
                typeTemplate.setName(new String(typeTemplate.getName().getBytes("ISO8859-1"), "utf-8"));
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return typeTemplateService.findByPage(typeTemplate, page, rows);
    }

    /**
     * 添加类型模板
     */
    @PostMapping("/save")
    public Boolean save(@RequestBody TypeTemplate typeTemplate) {
        try {
            typeTemplateService.save(typeTemplate);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /** 修改类型模板 */
    @PostMapping("/update")
    public Boolean update(@RequestBody TypeTemplate typeTemplate){
        try {
            typeTemplateService.update(typeTemplate);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /** 删除类型模板 */
    @GetMapping("/delete")
    public Boolean delete(Long[] ids){
        try {
            typeTemplateService.deleteAll(ids);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
