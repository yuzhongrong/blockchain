package com.blockchain.server.tron.mapper;

import com.blockchain.server.tron.entity.TronWalletKey;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.Set;

/**
 * TronWalletKeyMapper 数据访问类
 *
 * @version 1.0
 * @date 2019-02-16 15:44:06
 */
@Repository
public interface TronWalletKeyMapper extends Mapper<TronWalletKey> {
    /**
     * 获取所有用户钱包地址
     *
     * @return
     */
    Set<String> selectAllAddrs();
}