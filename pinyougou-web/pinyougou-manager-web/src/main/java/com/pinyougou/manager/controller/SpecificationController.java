package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.pojo.Specification;
import com.pinyougou.service.SpecificationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

/**
 * 规格控制器
 */
@RestController
@RequestMapping("/specification")
public class SpecificationController {

    @Reference(timeout = 10000)
    private SpecificationService specificationService;

    /** 带条件分页查询规格 */
    @GetMapping("/findByPage")
    public PageResult findByPage(Specification specification, Integer page, Integer rows){
        //get请求中文乱码
        try {
            if (StringUtils.isNoneBlank(specification.getSpecName())) {
                specification.setSpecName(new String(specification.getSpecName().getBytes("ISO8859-1"),"utf-8"));
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return specificationService.findByPage(specification,page,rows);
    }

    /** 添加规格 */
    @PostMapping("/save")
    public boolean save(@RequestBody Specification specification){
        try {
            specificationService.save(specification);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /** 查询规格列表(id与name) */
    @GetMapping("/findSpecList")
    public List<Map<String,Object>> findSpecList(){
        return specificationService.findAllByIdAndName();
    }
}
