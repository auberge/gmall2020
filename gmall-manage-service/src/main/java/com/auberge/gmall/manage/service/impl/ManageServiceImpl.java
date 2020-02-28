package com.auberge.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.auberge.gmall.bean.*;
import com.auberge.gmall.manage.constant.ManageConst;
import com.auberge.gmall.config.RedisUtil;
import com.auberge.gmall.manage.mapper.*;
import com.auberge.gmall.service.ManageService;
import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class ManageServiceImpl implements ManageService {
    @Autowired
    private BaseCatalog1Mapper baseCatalog1Mapper;
    @Autowired
    private BaseCatalog2Mapper baseCatalog2Mapper;
    @Autowired
    private BaseCatalog3Mapper baseCatalog3Mapper;
    @Autowired
    private BaseAttrInfoMapper baseAttrInfoMapper;
    @Autowired
    private BaseAttrValueMapper baseAttrValueMapper;
    @Autowired
    private SpuInfoMapper spuInfoMapper;
    @Autowired
    private BaseSaleAttrMapper baseSaleAttrMapper;
    @Autowired
    private SpuSaleAttrMapper spuSaleAttrMapper;
    @Autowired
    private SpuSaleAttrValueMapper spuSaleAttrValueMapper;
    @Autowired
    private SpuImageMapper spuImageMapper;
    @Autowired
    private SkuInfoMapper skuInfoMapper;
    @Autowired
    private SkuImageMapper skuImageMapper;
    @Autowired
    private SkuAttrValueMapper skuAttrValueMapper;
    @Autowired
    private SkuSaleAttrValueMapper skuSaleAttrValueMapper;
    @Autowired
    private RedisUtil redisUtil;


    @Override
    public List<BaseCatalog1> getCatalog1() {
        return baseCatalog1Mapper.selectAll();
    }

    @Override
    public List<BaseCatalog2> getCatalog2(String catalog1Id) {
        BaseCatalog2 baseCatalog2 = new BaseCatalog2();
        baseCatalog2.setCatalog1Id(catalog1Id);
        return baseCatalog2Mapper.select(baseCatalog2);
    }

    @Override
    public List<BaseCatalog3> getCatalog3(String catalog2Id) {
        BaseCatalog3 baseCatalog3 = new BaseCatalog3();
        baseCatalog3.setCatalog2Id(catalog2Id);
        return baseCatalog3Mapper.select(baseCatalog3);
    }

    @Override
    public List<BaseAttrInfo> getAttrList(String catalog3Id) {
        BaseAttrInfo baseAttrInfo = new BaseAttrInfo();
        baseAttrInfo.setCatalog3Id(catalog3Id);
        List<BaseAttrInfo> baseAttrInfoList = baseAttrInfoMapper.select(baseAttrInfo);
        for (BaseAttrInfo attrInfo : baseAttrInfoList) {
            BaseAttrValue baseAttrValue = new BaseAttrValue();
            baseAttrValue.setAttrId(attrInfo.getId());
            attrInfo.setAttrValueList(baseAttrValueMapper.select(baseAttrValue));
        }
        return baseAttrInfoList;
    }

    @Transactional
    @Override
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo) {
        //修改操作
        if (baseAttrInfo.getId() != null || baseAttrInfo.getId().length() > 0) {
            baseAttrInfoMapper.updateByPrimaryKeySelective(baseAttrInfo);
        } else {
            //保存数据 baseAttrInfo
            baseAttrInfoMapper.insertSelective(baseAttrInfo);
        }

        //baseAttrValue?先清空数据，再插入数据
        //清空数据的条件，根据attrId为依据
        //delete from baseAttrValue where attrId = baseAttrInfo.getId();
        BaseAttrValue baseAttrValueDel = new BaseAttrValue();
        baseAttrValueDel.setAttrId(baseAttrInfo.getId());
        baseAttrValueMapper.delete(baseAttrValueDel);

        //baseAttrValue
        List<BaseAttrValue> attrValuesList = baseAttrInfo.getAttrValueList();
        //if (attrValuesList.size()>0&&attrValuesList!=null) 报空指针
        if (attrValuesList != null && attrValuesList.size() > 0) {
            for (BaseAttrValue baseAttrValue : attrValuesList) {
                //private String id;
                //private String valueName; 前台页面传递过来的
                //private String attrId; attrId=baseAttrInfo.getId
                //前提条件baseAttrInfo对象中的主键必须能够获取自增的值
                baseAttrValue.setAttrId(baseAttrInfo.getId());
                baseAttrValueMapper.insertSelective(baseAttrValue);

            }
        }
    }

    @Override
    public List<BaseAttrValue> getAttrValueList(String attrId) {
        BaseAttrValue baseAttrValue = new BaseAttrValue();
        baseAttrValue.setAttrId(attrId);
        return baseAttrValueMapper.select(baseAttrValue);
    }

    @Override
    public BaseAttrInfo getAttrInfo(String attrId) {
        //baseAttrInfo.id=baseAttrValue.getAttrId();
        BaseAttrInfo baseAttrInfo = baseAttrInfoMapper.selectByPrimaryKey(attrId);
        //需要将平台属性值集合放入平台属性中
        //select * from baseAttrValue where attrId = ?
        BaseAttrValue baseAttrValue = new BaseAttrValue();
        baseAttrValue.setAttrId(attrId);
        List<BaseAttrValue> baseAttrValueList = baseAttrValueMapper.select(baseAttrValue);
        baseAttrInfo.setAttrValueList(baseAttrValueList);
        return baseAttrInfo;

    }

    @Override
    public List<SpuInfo> getSpuInfoList(SpuInfo spuInfo) {
        return spuInfoMapper.select(spuInfo);
    }

    @Override
    public List<BaseSaleAttr> getBaseSaleAttrList() {
        return baseSaleAttrMapper.selectAll();
    }

    @Transactional
    @Override
    public void saveSpuInfo(SpuInfo spuInfo) {
        //保存数据
        spuInfoMapper.insertSelective(spuInfo);
        List<SpuImage> spuImageList = spuInfo.getSpuImageList();
        if (spuImageList != null && spuImageList.size() > 0) {
            for (SpuImage spuImage : spuImageList) {
                //设置spuId
                spuImage.setSpuId(spuInfo.getId());
                spuImageMapper.insertSelective(spuImage);
            }
        }
        List<SpuSaleAttr> spuSaleAttrList = spuInfo.getSpuSaleAttrList();
        if (spuSaleAttrList != null && spuSaleAttrList.size() > 0) {
            for (SpuSaleAttr spuSaleAttr : spuSaleAttrList) {
                spuSaleAttr.setSpuId(spuInfo.getId());
                spuSaleAttrMapper.insertSelective(spuSaleAttr);
                List<SpuSaleAttrValue> spuSaleAttrValueList = spuSaleAttr.getSpuSaleAttrValueList();
                if (spuSaleAttrValueList != null && spuSaleAttrValueList.size() > 0) {
                    for (SpuSaleAttrValue spuSaleAttrValue : spuSaleAttrValueList) {
                        spuSaleAttrValue.setSpuId(spuInfo.getId());
                        spuSaleAttrValueMapper.insertSelective(spuSaleAttrValue);
                    }
                }
            }
        }
    }

    @Override
    public List<SpuSaleAttr> getSpuSaleAttrList(SpuSaleAttr spuSaleAttr) {
        List<SpuSaleAttr> spuSaleAttrList = spuSaleAttrMapper.select(spuSaleAttr);
        for (SpuSaleAttr saleAttr : spuSaleAttrList) {
            SpuSaleAttrValue spuSaleAttrValue = new SpuSaleAttrValue();
            spuSaleAttrValue.setSaleAttrId(saleAttr.getSaleAttrId());
            spuSaleAttrValue.setSpuId(spuSaleAttr.getSpuId());
            saleAttr.setSpuSaleAttrValueList(spuSaleAttrValueMapper.select(spuSaleAttrValue));
        }
        return spuSaleAttrList;
    }

    @Override
    public List<SpuImage> getSpuImageList(SpuImage spuImage) {
        return spuImageMapper.select(spuImage);
    }

    @Transactional
    @Override
    public void saveSkuInfo(SkuInfo skuInfo) {
        skuInfoMapper.insertSelective(skuInfo);
        List<SkuImage> skuImageList = skuInfo.getSkuImageList();
        if (skuImageList != null && skuImageList.size() > 0) {
            for (SkuImage skuImage : skuImageList) {
                skuImage.setSkuId(skuInfo.getId());
                skuImageMapper.insertSelective(skuImage);
            }
        }
        List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();
        if (skuAttrValueList != null && skuAttrValueList.size() > 0) {
            for (SkuAttrValue skuAttrValue : skuAttrValueList) {
                skuAttrValue.setSkuId(skuInfo.getId());
                skuAttrValueMapper.insertSelective(skuAttrValue);
            }
        }
        List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
        if (skuSaleAttrValueList != null && skuSaleAttrValueList.size() > 0) {
            for (SkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValueList) {
                skuSaleAttrValue.setSkuId(skuInfo.getId());
                skuSaleAttrValueMapper.insertSelective(skuSaleAttrValue);
            }
        }
    }

    @Override
    public SkuInfo getSkuInfo(String skuId) {
        return getSkuInfoRedisson(skuId);
    }

    private SkuInfo getSkuInfoRedisson(String skuId) {
        //放入业务逻辑代码
        SkuInfo skuInfo = null;
        Jedis jedis = null;
        RLock lock = null;
        try {
            Config config = new Config();
            config.useSingleServer().setAddress("redis://192.168.88.129:6379");
            RedissonClient redissonClient = Redisson.create(config);
            //使用redisson调用getLock
            lock = redissonClient.getLock("yourLock");
            //加锁
            lock.lock(10, TimeUnit.SECONDS);
            jedis = redisUtil.getJedis();
            String skuKey = ManageConst.SKUKEY_PREFIX + skuId + ManageConst.SKUKEY_SUFFIX;
            //判断缓存中是否有key
            if (jedis.exists(skuKey)) {
                String skuJson = jedis.get(skuKey);
                skuInfo = JSON.parseObject(skuJson, SkuInfo.class);
                return skuInfo;
            } else {
                skuInfo = getSkuInfoDB(skuId);
                jedis.setex(skuKey, ManageConst.SKUKEY_TIMEOUT, JSON.toJSONString(skuInfo));
                return skuInfo;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                jedis.close();
            }
            //释放锁
            lock.unlock();
        }
        return getSkuInfoDB(skuId);
    }

    private SkuInfo getSkuInfoJedis(String skuId) {
        SkuInfo skuInfo = null;
        Jedis jedis = null;
        try {
            jedis = redisUtil.getJedis();
            String skuKey = ManageConst.SKUKEY_PREFIX + skuId + ManageConst.SKUKEY_SUFFIX;
            //判断缓存中是否有key
            if (jedis.exists(skuKey)) {
                String skuJson = jedis.get(skuKey);
                if (skuJson == null || skuJson.length() == 0) {
                    String skuLockKey = ManageConst.SKUKEY_PREFIX + skuId + ManageConst.SKULOCK_SUFFIX;
                    String lockKey = jedis.set(skuLockKey, "good", "NX", "PX", ManageConst.SKULOCK_EXPIRE_PX);
                    if ("OK".equals(lockKey)) {
                        //此时加锁成功
                        skuInfo = getSkuInfoDB(skuId);
                        String skuRedisStr = JSON.toJSONString(skuInfo);
                        jedis.setex(skuKey, ManageConst.SKUKEY_TIMEOUT, skuRedisStr);
                        jedis.del(skuLockKey);
                        return skuInfo;
                    } else {
                        Thread.sleep(1000);
                        return getSkuInfo(skuId);
                    }
                }
            }
            skuInfo = getSkuInfoDB(skuId);
            jedis.setex(skuKey, ManageConst.SKUKEY_TIMEOUT, JSON.toJSONString(skuInfo));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                jedis.close();
            }
            skuInfo = getSkuInfoDB(skuId);
        }
        return skuInfo;
    }

    private SkuInfo getSkuInfoDB(String skuId) {
        SkuInfo skuInfo = skuInfoMapper.selectByPrimaryKey(skuId);
        SkuImage skuImage = new SkuImage();
        skuImage.setSkuId(skuId);
        skuInfo.setSkuImageList(skuImageMapper.select(skuImage));
        //查询平台属性值集合
        SkuAttrValue skuAttrValue = new SkuAttrValue();
        skuAttrValue.setSkuId(skuId);
        skuInfo.setSkuAttrValueList(skuAttrValueMapper.select(skuAttrValue));
        return skuInfo;
    }


    @Override
    public List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(SkuInfo skuInfo) {
        //使用那个mapper调用查询接口
        return spuSaleAttrMapper.selectSpuSaleAttrListCheckBySku(skuInfo.getId(), skuInfo.getSpuId());
    }

    @Override
    public List<SkuSaleAttrValue> getSkuSaleAttrValueListBySpu(String spuId) {
        return skuSaleAttrValueMapper.selectSkuSaleAttrValueListBySpu(spuId);
    }

    @Override
    public List<BaseAttrInfo> getAttrList(List<String> attrValueIdList) {
        //将集合变成字符串
        String valueIds = StringUtils.join(attrValueIdList.toArray(), ",");
        List<BaseAttrInfo> baseAttrInfoList = baseAttrInfoMapper.selectAttrInfoListByIds(valueIds);
        return baseAttrInfoList;
    }
}
