package com.blockchain.server.eth.service;


import com.blockchain.server.eth.entity.EthParadrop;

import java.math.BigDecimal;

/**
 * 以太坊钱包主要信息表——业务接口
 *
 * @author YH
 * @date 2019年2月16日17:09:19
 */
public interface IEthToAddrService {
    /**
     * 创建新钱包
     *
     * @return
     */
    String inserWallet();
}