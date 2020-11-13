package com.blockchain.server.tron.service;

import com.blockchain.server.tron.entity.TronToken;

import java.util.List;

/**
 * 以太坊币种表——业务接口
 *
 * @author YH
 * @date 2019年2月16日17:09:19
 */
public interface ITronTokenService {

    /**
     * 根据币种名称获取币种信息
     *
     * @param tokenName 币种名称
     * @return
     */
    TronToken findByTokenName(String tokenName);

    /**
     * 查询所有币种信息
     *
     * @return
     */
    List<TronToken> selectAll();

    /**
     * 通过币种地址获取币种信息
     * @param tokenAddr
     * @return
     */
    TronToken findByTokenAddr(String tokenAddr);
}
