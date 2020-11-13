package com.blockchain.server.tron.service;

import com.alibaba.fastjson.JSONObject;

import java.math.BigInteger;

/**
 * @author Harvey Luo
 * @date 2019/6/10 15:23
 */
public interface TronWalletUtilService {

    /**
     * 查询tron出币
     *
     * @param hashId
     * @return
     */
    JSONObject getTransaction(String hashId);

    /**
     * 生成地址
     *
     * @return
     */
    String createAccountAddress();

    /**
     * 创建账号交易
     *
     * @param owner
     * @param account
     * @return
     */
    String createAccount(String owner, String account);


    /**
     * 签名
     *
     * @param signParam
     * @return
     */
    String getSign(String signParam, String privateKey) throws Exception;

    /**
     * 广播交易
     *
     * @param broadcastParam
     */
    String getBroadcast(String broadcastParam);

    /**
     * 获取账号信息
     * @param hexAddr
     * @return
     */
    String getAccount(String hexAddr);

    /**
     * 获取16进制地址
     * @param account
     * @return
     */
    String getAccountByBase58(String account);

    /**
     * 资金归集TRX
     * @param owner
     * @param privateKey
     * @param to
     * @param amount
     * @return
     */
    String handleTransactionTRX(String owner, String privateKey, String to, BigInteger amount);

    /**
     * 资金归集TRC10
     * @param owner
     * @param privateKey
     * @param to
     * @param amount
     * @param tokenHexAddr
     * @return
     */
    String handleTransactionBTT(String owner, String privateKey, String to, BigInteger amount, String tokenHexAddr);

    /**
     * 资金归集TRC20
     * @param owner
     * @param privateKey
     * @param to
     * @param amount
     * @param tokenHexAddr
     * @return
     */
    String handleTransactionACE(String owner, String privateKey, String to, BigInteger amount, String tokenHexAddr);

    /**
     * 获取TRC20链上余额
     * @param hexAddr
     * @param tokenHexAddr
     * @return
     */
    String getAccountTRC20Balance(String hexAddr, String tokenHexAddr);


    /**
     * base58获取hex16进制地址
     * @param addr
     * @return
     */
    String tryToHexAddr(String addr);
}
