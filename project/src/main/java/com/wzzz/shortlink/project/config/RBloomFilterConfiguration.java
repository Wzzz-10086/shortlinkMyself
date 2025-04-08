package com.wzzz.shortlink.project.config;

import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 布隆过滤器配置
 */

/**
 * 存在误判，本身库中没有该用户名，但布隆过滤器认为有，影响不大
 * 防止用户不断请求一个已注册的用户名，导致数据库压力过大
 */
@Configuration
public class RBloomFilterConfiguration {

    /**
     * 防止用户注册查询数据库的布隆过滤器
     */
    @Bean
    public RBloomFilter<String> ShortLinkCreateCachePenetrationBloomFilter(RedissonClient redissonClient) {
        RBloomFilter<String> cachePenetrationBloomFilter = redissonClient.getBloomFilter("ShortLinkCreateCachePenetrationBloomFilter");
        cachePenetrationBloomFilter.tryInit(1000000, 0.001);
        return cachePenetrationBloomFilter;
    }
}
