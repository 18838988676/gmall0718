package com.atguigu.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.BaseSaleAttr;
import com.atguigu.gmall.bean.SpuInfo;
import com.atguigu.gmall.service.SpuService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class SpuController {

    @Reference
    SpuService spuService;

    //获得spu列表数据
    @RequestMapping("spuList")
    @ResponseBody
    public List<SpuInfo> spuList(String catalog3Id){
        List<SpuInfo>  spuInfos= spuService.spuList(catalog3Id);
        return  spuInfos;
    }

//获得 销售属性 下拉框
    @RequestMapping("baseSaleAttrList")
    @ResponseBody
    public List<BaseSaleAttr> baseSaleAttrList( ){
        List<BaseSaleAttr>  spuInfos= spuService.baseSaleAttrList();
        return  spuInfos;
    }

    //编辑spu后 最下面的保存
    @RequestMapping("saveSpu")
    @ResponseBody
    public String saveSpu( SpuInfo spuInfo){
        return  "success";
    }
}
