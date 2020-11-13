package com.blockchain.server.tron.service;

import com.blockchain.server.tron.dto.wallet.TronWalletOutDto;
import com.blockchain.server.tron.dto.wallet.TronWalletOutDto;
import com.blockchain.server.tron.entity.TronWalletTransfer;

import java.util.List;

/**
 * @author: Liusd
 * @create: 2019-03-27 09:27
 **/
public interface TronWalletOutService {
    List<TronWalletOutDto> list(String coinName, String status);

    int insert(String addr, String tokenAddr, String privateKey, String remark);

    int delete(String id);

    /**
     * 转账
     *
     * @param tx 记录
     * @return
     */
    String blockTransfer(TronWalletTransfer tx);
}
