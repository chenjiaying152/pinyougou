package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.mapper.BrandMapper;
import com.pinyougou.pojo.Brand;
import com.pinyougou.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 *品牌服务接口实现类
 */
@Service(interfaceName = "com.pinyougou.service.BrandService")
@Transactional
public class BrandServiceImpl implements BrandService {

    /** 注入数据访问接口代理对象*/
    @Autowired
    private BrandMapper brandMapper;


    @Override
    public void save(Brand brand) {
        brandMapper.insertSelective(brand);
    }

    @Override
    public void update(Brand brand) {
        brandMapper.updateByPrimaryKeySelective(brand);
    }

    @Override
    public void delete(Serializable id) {

    }

    @Override
    public void deleteAll(Serializable[] ids) {
        try {
            brandMapper.deleteAll(ids);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Brand findOne(Serializable id) {
        return null;
    }

    @Override
    public List<Brand> findAll() {
        return brandMapper.selectAll();
    }

    @Override
    public PageResult findByPage(Brand brand, int page, int rows) {
        //开启分页
        try {
            PageInfo<Brand> pageInfo = PageHelper.startPage(page, rows).doSelectPageInfo(new ISelect() {
                @Override
                public void doSelect() {
                    brandMapper.findAll(brand);
                }
            });
            return new PageResult(pageInfo.getTotal(),pageInfo.getList());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /** 查询品牌列表 */
    @Override
    public List<Map<String, Object>> findAllByIdAndName() {
        try {

            return brandMapper.findAllByIdAndName();
            /*
            //集合中添加了两次,太过于浪费内存资源, 不推荐使用

            List<Brand> brandList = findAll();

            List<Map<String, Object>> data = new ArrayList<>();

            for (Brand brand : brandList){
                Map<String, Object> map = new HashMap<>();
                map.put("id",brand.getId());
                map.put("text",brand.getName());

                data.add(map);
            }
            return data;
            */
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
