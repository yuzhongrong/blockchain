package com.blockchain.server.tron.common.util;

import com.blockchain.server.tron.entity.TronToken;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 缓存所有钱包用户的地址
 */
public class RedisWalletAddrUtil {
    //  储存所有钱包地址对象的key值
    static final String TRON_WALLET_ADDRS = "tron:wallet:addrs";
    //  储存所有代币地址对象的key值
    static final String TRON_WALLET_COINADDRS = "tron:wallet:coinAddrs";

    /**
     * 添加新的钱包地址
     *
     * @param addr          钱包地址
     * @param redisTemplate
     */
    public static void setWalletAddr(String addr, RedisTemplate redisTemplate) {
        if (!redisTemplate.opsForSet().isMember(TRON_WALLET_ADDRS, addr)) {
            redisTemplate.opsForSet().add(TRON_WALLET_ADDRS, addr);
        }
    }

    /**
     * 获取缓存中所有用户的钱包地址
     *
     * @param redisTemplate
     * @return
     */
    public static Set<String> getWalletAddrs(RedisTemplate redisTemplate) {
        return redisTemplate.opsForSet().members(TRON_WALLET_ADDRS);
    }

    /**
     * 缓存中所有用户的钱包地址
     *
     * @param addrs
     * @param redisTemplate
     */
    public static void setWalletAddrs(Set<String> addrs, RedisTemplate redisTemplate) {
        redisTemplate.opsForSet().add(TRON_WALLET_ADDRS, addrs.toArray());
    }

    /**
     * 添加新币种地址
     *
     * @param coin          代币对象
     * @param redisTemplate
     */
    public static void setWalletCoinAddr(TronToken coin, RedisTemplate redisTemplate) {
        redisTemplate.opsForHash().put(TRON_WALLET_COINADDRS, coin.getTokenAddr(), coin);
    }

    /**
     * 获取缓存中代币地址
     *
     * @param redisTemplate
     * @return
     */
    public static Map<String, TronToken> getWalletCoinAddrs(RedisTemplate redisTemplate) {
        return redisTemplate.opsForHash().entries(TRON_WALLET_COINADDRS);
    }

    /**
     * 缓存中所有币种地址
     *
     * @param coinAddrs
     * @param redisTemplate
     */
    public static Map<String, TronToken> setWalletCoinAddrs(List<TronToken> coinAddrs, RedisTemplate redisTemplate) {
        for (TronToken coin : coinAddrs) {
            coin.setDescr("");
            redisTemplate.opsForHash().put(TRON_WALLET_COINADDRS, coin.getTokenAddr(), coin);
        }
        return redisTemplate.opsForHash().entries(TRON_WALLET_COINADDRS);
    }
}
