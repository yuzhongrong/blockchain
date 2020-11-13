package com.blockchain.server.ltc.service;

import com.blockchain.server.ltc.dto.WalletOutDTO;
import com.blockchain.server.ltc.entity.WalletTransfer;

import java.util.List;

/**
 * @author: Liusd
 * @create: 2019-03-27 10:13
 **/
public interface WalletOutService {
    List<WalletOutDTO> list(String tokenSymbol);

    int insert(String addr, Integer tokenId, String privateKey, String remark);

    int delete(String id);

    /**
     * 区块转账
     *
     * @param tx 记录
     * @return
     */
    String blockTransfer(WalletTransfer tx);
}
