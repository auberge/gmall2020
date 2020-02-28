package com.auberge.gmall.config;

import org.redisson.config.Config;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.File;

public class RedisUtil {
    //创建连接池
    private JedisPool jedisPool;

    //host,port等参数可以配置在application.properties
    //初始化连接池
    //数据默认存放在0号库中
    public void initJedisPool(String host, int port, int database) {
        //直接创建一个连接池的配置类
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        //设置连接池最大连接数
        jedisPoolConfig.setMaxTotal(200);
        //设置等待时间
        jedisPoolConfig.setMaxWaitMillis(10 * 100);
        //设置最小连接池剩余数
        jedisPoolConfig.setMinIdle(10);
        //开启获取连接池的缓冲池
        jedisPoolConfig.setBlockWhenExhausted(true);
        //当用户获取到一个连接池之后，自检是否可以使用
        jedisPoolConfig.setTestOnBorrow(true);
        //连接池配置类。host,port,timeout,password
        jedisPool = new JedisPool(jedisPoolConfig, host, port, 20 * 100);
    }

    //获取Jedis
    public Jedis getJedis() {
        return jedisPool.getResource();
    }
}
