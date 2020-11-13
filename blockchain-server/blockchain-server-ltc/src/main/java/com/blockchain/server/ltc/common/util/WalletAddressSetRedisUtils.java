package com.blockchain.server.ltc.common.util;

import com.blockchain.server.ltc.service.IWalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class WalletAddressSetRedisUtils {

    //redis 存储地址列表的key值，用于解析用户充值区块信息
    public static final String ADDRESS_KEY = "ltc:adderss:addressSet";
    //redis 存储地址列表的时间，天数
    public static final int ADDRESS_TIME = 30;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IWalletService walletService;

    /**
     * 保存地址集合到redis
     *
     * @param address 新增地址
     */
    public void insert(String address) {
        redisTemplate.opsForSet().add(ADDRESS_KEY, address);
    }

    /**
     * 从redis中获取地址集合
     *
     * @return
     */
    public Set<String> get() {
        return redisTemplate.opsForSet().members(ADDRESS_KEY);
    }

    /**
     * 判断缓存中是否存在该地址
     *
     * @param addr 地址
     * @return
     */
    public boolean isExistsAddr(String addr) {
        Set<String> addressSet = get();
        if (addressSet == null || addressSet.size() == 0) {
            addressSet = walletService.getAllWalletAddr();
            redisTemplate.opsForSet().add(ADDRESS_KEY, addressSet.toArray());
        }

        return addressSet.contains(addr);
    }

}
