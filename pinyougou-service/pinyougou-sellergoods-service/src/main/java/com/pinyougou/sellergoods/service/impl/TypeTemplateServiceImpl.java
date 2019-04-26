package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.mapper.SpecificationOptionMapper;
import com.pinyougou.mapper.TypeTemplateMapper;
import com.pinyougou.pojo.SpecificationOption;
import com.pinyougou.pojo.TypeTemplate;
import com.pinyougou.service.TypeTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 类型模板服务接口实现类
 */
@Service(interfaceName = "com.pinyougou.service.TypeTemplateService")
@Transactional
public class TypeTemplateServiceImpl implements TypeTemplateService {

    /** 注入数据访问接口代理对象*/
    @Autowired
    private TypeTemplateMapper typeTemplateMapper;
    @Autowired
    private SpecificationOptionMapper specificationOptionMapper;

    @Override
    public void save(TypeTemplate typeTemplate) {
        try {
            typeTemplateMapper.insertSelective(typeTemplate);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void update(TypeTemplate typeTemplate) {
        try {
            typeTemplateMapper.updateByPrimaryKeySelective(typeTemplate);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Serializable id) {

    }

    /** 批量删除类型模板 */
    @Override
    public void deleteAll(Serializable[] ids) {
        try {
            typeTemplateMapper.deleteAll(ids);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public TypeTemplate findOne(Serializable id) {
        return typeTemplateMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<TypeTemplate> findAll() {
        return null;
    }

    @Override
    public PageResult findByPage(TypeTemplate typeTemplate, int page, int rows) {

        try {
            PageInfo<TypeTemplate> pageInfo = PageHelper.startPage(page, rows).doSelectPageInfo(new ISelect() {
                @Override
                public void doSelect() {
                    typeTemplateMapper.findAll(typeTemplate);
                }
            });
            return new PageResult(pageInfo.getTotal(),pageInfo.getList());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /** 根据类型模板id,查询规格选项数据 */
    @Override
    public List<Map> findSpecByTemplateId(Long id) {
        try {
            /**   需求数据格式
             * [{"id":27,"text":"网络","options" : [{},{}]},
               {"id":32,"text":"机身内存","options" : [{},{}]}]
             */
            TypeTemplate typeTemplate = findOne(id);
            //1.获取规格json字符串数据  [{"id":27,"text":"网络"},{"id":32,"text":"机身内存"}]
            //JSON.parseArray()  : 能获取的对应格式为: [{},{}]
            //JSON.parseObject() :  能获取的对应格式为: {}
            String specIds = typeTemplate.getSpecIds();
            //把specIds转化成List<Map>
            List<Map> specList = JSON.parseArray(specIds, Map.class);

            //迭代specList
            for (Map spec : specList){
                //获取id
                Long specId = Long.valueOf(spec.get("id").toString());
                SpecificationOption so = new SpecificationOption();
                so.setSpecId(specId);
                List<SpecificationOption> options = specificationOptionMapper.select(so);

                spec.put("options",options);
            }
            return specList;
        } catch (NumberFormatException e) {
            throw new RuntimeException(e);
        }

    }
}
