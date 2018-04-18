package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(timeout = 30000)
public class ItemSearchServiceImpl implements ItemSearchService{

    @Autowired
    private SolrTemplate solrTemplate;

    @Override
    public Map<String, Object> search(Map searchMap) {
        Map map = new HashMap();
        String keywords = (String) searchMap.get("keywords");
        searchMap.put("keywords",keywords.replaceAll(" ",""));
        //1存入搜索结果
        map.putAll(searchData(searchMap));
        //2根据搜索查询分类,存入分类结果
        List<String> list = searchCategoryList(searchMap);
        map.put("categoryList",list);
        //3存入从缓存中查询的品牌和规格结果
        String category = (String) searchMap.get("category");
        if (!"".equals(category)){
            //如果有分类条件
            map.putAll(searchBrandAndSpecByRedis(category));
        }else {
            //如果没有分类条件,按默认第一个条件查询
            if (list.size() > 0){
                map.putAll(searchBrandAndSpecByRedis(list.get(0)));
            }
        }

        return map;
    }

    @Override
    public void importItemList(List list) {
        solrTemplate.saveBeans(list);
        solrTemplate.commit();
    }

    /**
     *高亮搜索查询抽取出来的方法
     */
    private Map searchData(Map searchMap){
        Map map = new HashMap();
        /** Query query = new SimpleQuery("*:*");
         //这是使用复制域keywords,包含了brand seller title category
         Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
         query.addCriteria(criteria);
         ScoredPage<TbItem> items = solrTemplate.queryForPage(query, TbItem.class);
         */
        HighlightQuery query = new SimpleHighlightQuery();
        HighlightOptions highligthtOption = new HighlightOptions().addField("item_title");//设置高亮域
        //设置高亮前缀
        highligthtOption.setSimplePrefix("<em style='color:red'>");
        //设置高亮后缀
        highligthtOption.setSimplePostfix("</em>");
        //设置高亮选项
        query.setHighlightOptions(highligthtOption);
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);

        /** 这里是按照商品过滤查询 */
        if (!"".equals(searchMap.get("category"))){
            FilterQuery filterQuery = new SimpleFilterQuery();
            Criteria filterCriteria = new Criteria("item_category").is(searchMap.get("category"));
            filterQuery.addCriteria(filterCriteria);
            query.addFilterQuery(filterQuery);
        }

        /** 这里是按照品牌过滤查询 */
        if (!"".equals(searchMap.get("brand"))){
            Criteria filterCriteria = new Criteria("item_brand").is(searchMap.get("brand"));
            FilterQuery filterQuery = new SimpleFilterQuery().addCriteria(filterCriteria);
            query.addFilterQuery(filterQuery);
        }

        /** 这里是按照规格过滤查询 */
        if (searchMap.get("spec") != null){
            Map<String,String> specMap = (Map<String,String>) searchMap.get("spec");
            for (String key:specMap.keySet()){
                FilterQuery filterQuery = new SimpleFilterQuery();
                Criteria filterCriteria = new Criteria("item_spec_"+key).is(specMap.get(key));
                filterQuery.addCriteria(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
        }
        /**这里是按照价格区间过滤查询 */
        if (!"".equals(searchMap.get("price"))){
            String[] price = ((String) searchMap.get("price")).split("-");
            if (!price[0].equals("0")){//区间起始值不为0
                Criteria filterCriteria = new Criteria("item_price").greaterThanEqual(price[0]);
                FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
            if (!price[1].equals("*")){//区间结尾值不为*
                Criteria filterCriteria = new Criteria("item_price").lessThanEqual(price[1]);
                FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
        }

        /** 分页设置 */
        Integer pageNum = (Integer) searchMap.get("pageNum");//页码
        if (pageNum == null) {
            pageNum = 1;
        }
        Integer pageRows = (Integer) searchMap.get("pageRows");//每页条数
        if (pageRows == null){
            pageRows = 20;
        }
        query.setOffset((pageNum -1 ) * pageRows);//设置分页起始索引
        query.setRows(pageRows);

        /** 价格、商品上架 排序 */
        String sortValue = (String) searchMap.get("sort");
        String srotFiled = (String) searchMap.get("sortFiled");
        if (sortValue != null && sortValue != ""){
            if (sortValue.equals("ASC")){
                Sort sort = new Sort(Sort.Direction.ASC,"item_"+srotFiled);
                query.addSort(sort);
            }
            if (sortValue.equals("DESC")){
                Sort sort = new Sort(Sort.Direction.DESC,"item_"+srotFiled);
                query.addSort(sort);
            }
        }

        /** 这是结果  */
        HighlightPage<TbItem> items = solrTemplate.queryForHighlightPage(query, TbItem.class);
        for (HighlightEntry<TbItem> h: items.getHighlighted()) {//高亮集合入口
            TbItem tbItem = h.getEntity();
            if (h.getHighlights().size()>0 && h.getHighlights().get(0).getSnipplets().size()>0 ){
                tbItem.setTitle(h.getHighlights().get(0).getSnipplets().get(0));
            }
        }
        map.put("rows",items.getContent());
        map.put("totalPages",items.getTotalPages());//总页数
        map.put("totalSize",items.getTotalElements());//总记录数
        return map;
    }

    /**
     * 分类
     */
    private List<String> searchCategoryList(Map searchMap){
        List<String> list=new ArrayList();
        Query query=new SimpleQuery();
        //按照关键字查询
        Criteria criteria=new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        //设置分组选项
        GroupOptions groupOptions=new GroupOptions().addGroupByField("item_category");
        query.setGroupOptions(groupOptions);
        //得到分组页
        GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);
        //根据列得到分组结果集
        GroupResult<TbItem> groupResult = page.getGroupResult("item_category");
        //得到分组结果入口页
        Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
        //得到分组入口集合
        List<GroupEntry<TbItem>> content = groupEntries.getContent();
        for(GroupEntry<TbItem> entry:content){
            list.add(entry.getGroupValue());//将分组结果的名称封装到返回值中
        }
        return list;
    }

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 从缓存中查询品牌和规格
     * @param category
     * @return
     */
    private Map searchBrandAndSpecByRedis(String category){
       Map map = new HashMap();
       Long templateId = (Long) redisTemplate.boundHashOps("itemCat").get(category);
       if (templateId != null){
           //根据模板id从缓存中查询品牌列表
           List brandList = (List) redisTemplate.boundHashOps("brandList").get(templateId);
           map.put("brandList",brandList);
           //根据模板id从缓存中查询规格列表
           List specList = (List) redisTemplate.boundHashOps("sepcList").get(templateId);
           map.put("specList",specList);
       }
        return map;
    }
}
