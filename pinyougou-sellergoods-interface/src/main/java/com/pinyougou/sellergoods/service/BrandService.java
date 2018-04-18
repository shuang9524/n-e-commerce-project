package com.pinyougou.sellergoods.service;

import com.pinyougou.entity.PageResult;
import com.pinyougou.pojo.TbBrand;

import java.util.List;
import java.util.Map;

public interface BrandService {
    List<TbBrand> findAll();
    PageResult findPage(Integer pageNum,Integer pageSize);
    void insert(TbBrand brand);
    void update(TbBrand brand);
    TbBrand findOne(Long id);
    void delete(Long[] id);
    PageResult findPage(TbBrand brand, Integer pageNum, Integer pageSize);
    List<Map> selectOptionList();
}
