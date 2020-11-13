package com.blockchain.server.ltc.service.impl;


import com.blockchain.server.ltc.common.enums.WalletEnums;
import com.blockchain.server.ltc.common.exception.WalletException;
import com.blockchain.server.ltc.entity.CollectionTransfer;
import com.blockchain.server.ltc.mapper.CollectionTransferMapper;
import com.blockchain.server.ltc.service.ICollectionTransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 以太坊钱包归集记录表——业务接口
 *
 * @author YH
 * @date 2019年2月16日17:09:19
 */
@Service
public class CollectionTransferServiceImpl implements ICollectionTransferService {
    @Autowired
    CollectionTransferMapper collectionTransferMapper;

    @Override
    public void insert(CollectionTransfer collectionTransfer) {
        int row = collectionTransferMapper.insertSelective(collectionTransfer);
        if(row == 0){
            throw new WalletException(WalletEnums.SERVER_IS_TOO_BUSY);
        }
    }
}
