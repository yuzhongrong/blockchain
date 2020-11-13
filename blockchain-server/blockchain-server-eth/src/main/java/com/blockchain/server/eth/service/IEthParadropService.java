package com.blockchain.server.eth.service;


import com.blockchain.server.eth.entity.EthParadrop;

import java.math.BigDecimal;

/**
 * 以太坊钱包主要信息表——业务接口
 *
 * @author YH
 * @date 2019年2月16日17:09:19
 */
public interface IEthParadropService {

    /**
     * 获取配置详情
     *
     * @return
     */
    EthParadrop getInfo();

    /**
     * 获取发放的信息
     *
     * @param sendCount
     * @param sendAmount
     * @return
     */
    EthParadrop updateInfo(BigDecimal sendCount, BigDecimal sendAmount);

    /**
     * 修改发放状态
     *
     * @param status
     */
    void updateStatus(String status);
}