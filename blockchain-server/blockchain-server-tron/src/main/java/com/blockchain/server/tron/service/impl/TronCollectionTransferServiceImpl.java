package com.blockchain.server.tron.service.impl;


import com.blockchain.server.tron.common.enums.TronWalletEnums;
import com.blockchain.server.tron.common.exception.TronWalletException;
import com.blockchain.server.tron.entity.TronCollectionTransfer;
import com.blockchain.server.tron.mapper.TronCollectionTransferMapper;
import com.blockchain.server.tron.service.ITronCollectionTransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 以太坊钱包归集记录表——业务接口
 *
 * @author YH
 * @date 2019年2月16日17:09:19
 */
@Service
public class TronCollectionTransferServiceImpl implements ITronCollectionTransferService {
    @Autowired
    TronCollectionTransferMapper collectionTransferMapper;

    @Override
    public void insert(TronCollectionTransfer collectionTransfer) {
        int row = collectionTransferMapper.insert(collectionTransfer);
        if(row == 0){
            throw new TronWalletException(TronWalletEnums.SERVER_IS_TOO_BUSY);
        }
    }
}
