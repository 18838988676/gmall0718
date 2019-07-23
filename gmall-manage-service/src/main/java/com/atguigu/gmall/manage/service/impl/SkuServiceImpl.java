package com.atguigu.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.SkuAttrValue;
import com.atguigu.gmall.bean.SkuImage;
import com.atguigu.gmall.bean.SkuInfo;
import com.atguigu.gmall.bean.SkuSaleAttrValue;
import com.atguigu.gmall.manage.mapper.SkuAttrValueMapper;
import com.atguigu.gmall.manage.mapper.SkuImageMapper;
import com.atguigu.gmall.manage.mapper.SkuInfoMapper;
import com.atguigu.gmall.manage.mapper.SkuSaleAttrValueMapper;
import com.atguigu.gmall.service.SkuService;
import com.atguigu.gmall.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.util.List;

@Service
public class SkuServiceImpl implements SkuService {
    @Autowired
    SkuInfoMapper skuInfoMapper;

    @Autowired
    SkuAttrValueMapper skuAttrValueMapper;

    @Autowired
    SkuSaleAttrValueMapper skuSaleAttrValueMapper;

    @Autowired
    SkuImageMapper skuImageMapper;

    @Autowired
    RedisUtil redisUtil;
    @Override
    public List<SkuInfo> getSkuListBySpu(String spuId) {
        SkuInfo skuInfo=new SkuInfo();
        skuInfo.setSpuId(spuId);
        List<SkuInfo> select = skuInfoMapper.select(skuInfo);
        return select;
    }

    @Override
    public void saveSku(SkuInfo skuInfo) {
        skuInfoMapper.insertSelective(skuInfo);

        String skuId = skuInfo.getId();

        List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();

        for (SkuAttrValue skuAttrValue : skuAttrValueList) {
            skuAttrValue.setSkuId(skuId);
            skuAttrValueMapper.insert(skuAttrValue);
        }

        List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();

        for (SkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValueList) {
            skuSaleAttrValue.setSkuId(skuId);
            skuSaleAttrValueMapper.insert(skuSaleAttrValue);
        }


        List<SkuImage> skuImageList = skuInfo.getSkuImageList();
        for (SkuImage skuImage : skuImageList) {
            skuImage.setSkuId(skuId);
            skuImageMapper.insert(skuImage);
        }

    }

    @Override
    public SkuInfo getSkuById(String skuId) {
        Jedis jedis = redisUtil.getJedis();
        SkuInfo skuInfo = null;

        // 查询redis缓存
        String key = "sku:" + skuId + ":info";
        String val = jedis.get(key);
        skuInfo = JSON.parseObject(val, SkuInfo.class);

        if (skuInfo == null) {
            // 查询db
            skuInfo = getSkuByIdFormDb(skuId);

            // 同步缓存
            jedis.set(key, JSON.toJSONString(skuInfo));
        }
        return skuInfo;
    }
    private SkuInfo getSkuByIdFormDb(String skuId) {

        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setId(skuId);
        SkuInfo skuInfo1 = skuInfoMapper.selectOne(skuInfo);

        SkuImage skuImage = new SkuImage();
        skuImage.setSkuId(skuId);
        List<SkuImage> select = skuImageMapper.select(skuImage);

        skuInfo1.setSkuImageList(select);


        SkuSaleAttrValue skuSaleAttrValue = new SkuSaleAttrValue();
        skuSaleAttrValue.setSkuId(skuId);
        List<SkuSaleAttrValue> select1 = skuSaleAttrValueMapper.select(skuSaleAttrValue);
        skuInfo1.setSkuSaleAttrValueList(select1);

        return skuInfo1;
    }

}

