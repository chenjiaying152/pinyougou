package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.service.ItemSearchService;
import com.pinyougou.solr.SolrItem;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.data.solr.core.query.result.ScoredPage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 搜索服务器 */

@Service(interfaceName = "com.pinyougou.service.ItemSearchService")
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    /** 搜索的方法 */
    @Override
    public Map<String, Object> search(Map<String, Object> params) {

            /** 创建Map集合封装返回的数据 */
            Map<String, Object> data = new HashMap<>();
            /** 获取检索关键字 */
            String keywords = (String) params.get("keywords");

            /** 获取当前页码 */
            Integer page = (Integer) params.get("page");
            if (page == null){
                /** 默认第一页 */
                page = 1;
            }

            /** 获取每页显示的记录数 */
            Integer rows = (Integer) params.get("rows");
            if (rows == null){
                /** 默认20条记录 */
                rows = 20;
            }

            /** 判断检索关键字是否为空 */
            if (StringUtils.isNoneBlank(keywords)){  //高亮显示

                /** 创建高亮查询对象 */
                SimpleHighlightQuery highlightQuery = new SimpleHighlightQuery();

                /** 创建高亮选项对象 */
                HighlightOptions highlightOptions = new HighlightOptions();

                /** 设置高亮域 */
                highlightOptions.addField("title");
                /** 设置高亮前缀 */
                highlightOptions.setSimplePrefix("<font color='red'>");
                /** 设置高亮后缀 */
                highlightOptions.setSimplePostfix("</font>");

                /** 设置高亮选项 */
                highlightQuery.setHighlightOptions(highlightOptions);

                /** 创建查询条件对象 */
                Criteria criteria = new Criteria("keywords").is(keywords);  //分词
                /** 添加查询条件(关键字) */
                highlightQuery.addCriteria(criteria);

                //{"keywords":"华为","category":"手机","brand":"苹果",
                // "price":"0-500元","spec":{"网络":"移动3G","机身内存":"16G"}}
                //1.按商品分类过滤
                if (!"".equals(params.get("category"))){
                    Criteria criteria1 = new Criteria("category").is(params.get("category"));//不会做分词
                    /** 添加过滤条件 */
                    highlightQuery.addFilterQuery(new SimpleFilterQuery(criteria1));
                }
                //2.按商品品牌过滤
                if (!"".equals(params.get("brand"))){
                    Criteria criteria1 = new Criteria("brand").is(params.get("brand"));
                    /** 添加过滤条件 */
                    highlightQuery.addFilterQuery(new SimpleFilterQuery(criteria1));
                }
                //3.按商品规格过滤
                if (params.get("spec") != null){
                    Map<String, String> specMap = (Map) params.get("spec");
                    for (String key : specMap.keySet()){
                        Criteria criteria1 = new Criteria("spec_" + key).is(specMap.get(key));
                        /** 添加过滤条件 */
                        highlightQuery.addFilterQuery(new SimpleFilterQuery(criteria1));
                    }
                }
                //4.按商品价格过滤
                if (!"".equals(params.get("price"))){
                    /** 得到价格范围数组 */
                    String[] price = params.get("price").toString().split("-");
                    /** 如果价格区间起点不等于0 */
                    if (!price[0].equals("0")){
                        Criteria criteria1 = new Criteria("price").greaterThanEqual(price[0]);
                        /** 添加过滤条件 */
                        highlightQuery.addFilterQuery(new SimpleFilterQuery(criteria1));
                    }
                    /** 如果价格区间终点不等于星号 */
                    if (!price[1].equals("*")){
                        Criteria criteria1 = new Criteria("price").lessThanEqual(price[1]);
                        /** 添加过滤条件 */
                        highlightQuery.addFilterQuery(new SimpleFilterQuery(criteria1));
                    }

                }

                /** 添加排序 */
                String sortValue = (String) params.get("sort");  // ASC  DESC
                String sortField = (String) params.get("sortField");  // 排序域
                if (StringUtils.isNoneBlank(sortValue) && StringUtils.isNoneBlank(sortField)){
                    Sort sort = new Sort("ASC".equalsIgnoreCase(sortValue) ?
                            Sort.Direction.ASC : Sort.Direction.DESC, sortField);
                    /** 增加排序 */
                    highlightQuery.addSort(sort);
                }


                /** 设置起始记录查询记录数 */
                highlightQuery.setOffset((page - 1) * rows);
                /** 设置每页显示记录数 */
                highlightQuery.setRows(rows);

                /** 分页查询, 得到高亮分页查询对象 */
                HighlightPage<SolrItem> highlightPage =
                        solrTemplate.queryForHighlightPage(highlightQuery, SolrItem.class);
                /** 循环高亮项集合 */
                for (HighlightEntry<SolrItem> he : highlightPage.getHighlighted()) {
                    /** 获取检索到的原实体 */
                    SolrItem solrItem = he.getEntity();
                    /** 判断高亮集合及集合中第一个Feild的高亮内容 */
                    if (he.getHighlights().size() > 0 && he.getHighlights().get(0).getSnipplets().size() >0 ){
                        /** 设置高亮的结果 */
                        solrItem.setTitle(he.getHighlights().get(0).getSnipplets().get(0));
                    }

                }
                data.put("rows",highlightPage.getContent());

                /** 设置总页数 */
                data.put("totalPages", highlightPage.getTotalPages());
                /** 设置总记录数 */
                data.put("total", highlightPage.getTotalElements());

            }else { //简单查询
                /** 创建简单查询对象 */
                SimpleQuery simpleQuery = new SimpleQuery("*:*");
                /** 设置起始记录查询数 */
                simpleQuery.setOffset((page - 1) * rows);
                /** 设置每页显示记录数 */
                simpleQuery.setRows(rows);

                /** 分页检索 */
                ScoredPage<SolrItem> scoredPage = solrTemplate.queryForPage(simpleQuery, SolrItem.class);
                /** 获取内容 */
                data.put("rows",scoredPage.getContent());

                /** 设置总页数 */
                data.put("totalPages",scoredPage.getTotalPages());
                /** 设置总记录数 */
                data.put("total", scoredPage.getTotalElements());

                return data;
            }

        return data;
    }

    /** 添加或修改商品索引 */
    @Override
    public void saveOrUpdate(List<SolrItem> solrItems) {
        try {
            UpdateResponse updateResponse = solrTemplate.saveBeans(solrItems);
            if (updateResponse.getStatus() == 0){
                solrTemplate.commit();
            }else {
                solrTemplate.rollback();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /** 删除商品索引 */
    public void delete(List<Long> goodsIds){
        Query query = new SimpleQuery();
        Criteria criteria = new Criteria("goodsId").in(goodsIds);
        query.addCriteria(criteria);
        UpdateResponse updateResponse = solrTemplate.delete(query);
        if (updateResponse.getStatus() == 0){
            solrTemplate.commit();
        }else {
            solrTemplate.rollback();
        }
    }
}
