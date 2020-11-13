package com.blockchain.server.ltc.service;


import com.blockchain.server.ltc.entity.CollectionTransfer;

/**
 * 以太坊钱包记录表——业务接口
 *
 * @author YH
 * @date 2019年2月16日17:09:19
 */
public interface ICollectionTransferService {

    /**
     * 插入一条数据
     * @param collectionTransfer
     */
    void insert(CollectionTransfer collectionTransfer);
}
