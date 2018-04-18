package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.entity.PageResult;
import com.pinyougou.entity.Result;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class BrandController {

    @Reference
    private BrandService brandService;

    @RequestMapping("findAllBrands")
    public List<TbBrand> findAll(){
        return brandService.findAll();
    }

    @RequestMapping("findPage")
    public PageResult findPageData(Integer page,Integer rows){
        return brandService.findPage(page,rows);
    }

    @RequestMapping("save")
    public Result insert(@RequestBody TbBrand brand) {
        try {
            brandService.insert(brand);
            return new Result(true, "添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "添加失败");
        }
    }

    @RequestMapping("findOne")
    public TbBrand findOne(Long id){
        return brandService.findOne(id);
    }

    @RequestMapping("update")
    public Result updateBrand(@RequestBody TbBrand brand){
        try {
            brandService.update(brand);
            return new Result(true,"修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"修改失败");
        }
    }

    @RequestMapping("delete")
    public Result delete(Long[] ids){
        try {
            brandService.delete(ids);
            return new Result(true,"删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"删除成功");
        }
    }

    @RequestMapping("search")
    public PageResult findPage(TbBrand brand, Integer page, Integer rows){
        return brandService.findPage(brand, page, rows);
    }

    @RequestMapping("selectOptionList")
    public List<Map> findOption(){
        return brandService.selectOptionList();
    }

}
