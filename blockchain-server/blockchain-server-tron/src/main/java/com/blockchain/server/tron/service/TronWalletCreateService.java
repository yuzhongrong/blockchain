package com.blockchain.server.tron.service;

import com.blockchain.server.tron.entity.TronWalletCreate;

import java.util.List;

/**
 * @author: Liusd
 * @create: 2019-03-27 09:27
 **/
public interface TronWalletCreateService {
    List<TronWalletCreate> list(String status);

    int insert(String addr, String tokenAddr, String privateKey, String remark);

    int delete(String id);

    /**
     * 修改钱包
     * @param addr
     * @param remark
     * @param status
     * @return
     */
    Integer update(String addr, String remark, String status);
}
