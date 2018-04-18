package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.entity.PageResult;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrandExample.Criteria;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbBrandExample;
import com.pinyougou.sellergoods.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    private TbBrandMapper tbBrandMapper;

    @Override
    public List<TbBrand> findAll() {
        return tbBrandMapper.selectByExample(null);
    }

    public PageResult findPage(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        Page<TbBrand> page = (Page<TbBrand>) tbBrandMapper.selectByExample(null);
        return new PageResult(page.getTotal(),page.getResult());
    }

    public void insert(TbBrand brand) {
        tbBrandMapper.insert(brand);
    }

    public void update(TbBrand brand) {
        tbBrandMapper.updateByPrimaryKey(brand);
    }

    public TbBrand findOne(Long id) {
        return tbBrandMapper.selectByPrimaryKey(id);
    }

    public void delete(Long[] ids) {
        if (ids != null) {
            for (Long id : ids) {
                tbBrandMapper.deleteByPrimaryKey(id);
            }
        }
    }

    public PageResult findPage(TbBrand brand, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        TbBrandExample example = new TbBrandExample();
        Criteria criteria = example.createCriteria();
        if (brand != null){
            if (brand.getName() != null && brand.getName() != ""){
                criteria.andNameLike("%"+brand.getName()+"%");
                System.out.println("name");
            }
            if (brand.getFirstChar() != null && brand.getFirstChar() != ""){
                criteria.andFirstCharLike("%"+brand.getFirstChar()+"%");
                System.out.println("firstChar");
            }
        }
        Page<TbBrand> page = (Page<TbBrand>) tbBrandMapper.selectByExample(example);
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public List<Map> selectOptionList() {
        return tbBrandMapper.selectOptionList();
    }
}
