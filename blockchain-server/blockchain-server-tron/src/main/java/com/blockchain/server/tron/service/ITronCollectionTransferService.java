package com.blockchain.server.tron.service;


import com.blockchain.server.tron.entity.TronCollectionTransfer;

/**
 * 以太坊钱包记录表——业务接口
 *
 * @author YH
 * @date 2019年2月16日17:09:19
 */
public interface ITronCollectionTransferService {

    /**
     * 插入一条数据
     * @param collectionTransfer
     */
    void insert(TronCollectionTransfer collectionTransfer);
}
