package com.blockchain.server.eth.service;

import com.blockchain.common.base.dto.wallet.WalletParamsDTO;
import com.blockchain.server.eth.dto.wallet.EthPrivateBalanceDTO;
import com.blockchain.server.eth.entity.EthPrivateBalance;

import java.util.List;

public interface IEthPrivateBalanceService {

    /**
     * 新增私募资金
     */
    void insert(EthPrivateBalance privateBalance);

    /**
     * 查询私募资金列表
     */
    List<EthPrivateBalanceDTO> list(WalletParamsDTO paramsDTO, Integer pageNum, Integer pageSize);

    /**
     * 扣减私募资金
     */
    void deduct(EthPrivateBalance privateBalance);

}
