package com.blockchain.server.eth.service.impl;

import com.blockchain.server.eth.common.enums.EthWalletEnums;
import com.blockchain.server.eth.common.exception.EthWalletException;
import com.blockchain.server.eth.entity.EthCollectionTransfer;
import com.blockchain.server.eth.mapper.EthCollectionTransferMapper;
import com.blockchain.server.eth.service.IEthCollectionTransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 以太坊钱包归集记录表——业务接口
 *
 * @author YH
 * @date 2019年2月16日17:09:19
 */
@Service
public class EthCollectionTransferServiceImpl implements IEthCollectionTransferService {
    @Autowired
    EthCollectionTransferMapper collectionTransferMapper;

    @Override
    public void insert(EthCollectionTransfer collectionTransfer) {
        int row = collectionTransferMapper.insert(collectionTransfer);
        if (row == 0) {
            throw new EthWalletException(EthWalletEnums.SERVER_IS_TOO_BUSY);
        }
    }
}
