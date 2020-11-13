package com.blockchain.server.tron.service;

import com.blockchain.server.tron.entity.TronWalletKey;

import java.util.Set;

/**
 * 以太坊钱包主要信息表——业务接口
 *
 * @author YH
 * @date 2019年2月16日17:09:19
 */
public interface ITronWalletKeyService {

    TronWalletKey findByUserOpenId(String userOpenId);

    TronWalletKey findByAddr(String addr);

    /**
     * 是否存在密码
     *
     * @param userOpenId
     * @return
     */
    boolean existsPass(String userOpenId);

    TronWalletKey selectOne(TronWalletKey ethWalletKey);

    int insert(TronWalletKey ethWalletKey);

    TronWalletKey insert(String userOpenId);

    int update(TronWalletKey ethWalletKey);

    Set<String> selectAddrs();

    /**
     * 通过16进制查询数据
     * @param fromHexAddr
     * @return
     */
    TronWalletKey findByHexAddr(String fromHexAddr);

    /**
     * 返回地址
     **/
    // String isPassword(String userOpenId, String password);

    // void updatePassword(String userOpenId, String password);

}
