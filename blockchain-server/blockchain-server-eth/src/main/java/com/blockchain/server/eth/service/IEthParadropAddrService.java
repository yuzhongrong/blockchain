package com.blockchain.server.eth.service;


import com.blockchain.server.eth.entity.EthParadropAddr;

import java.util.List;

/**
 * 以太坊钱包主要信息表——业务接口
 *
 * @author YH
 * @date 2019年2月16日17:09:19
 */
public interface IEthParadropAddrService {
    /**
     * 获取启用的钱包，随机抽取一个
     *
     * @return
     */
    EthParadropAddr getEnable();

    /**
     * 获取钱包列表
     *
     * @return
     */
    List<EthParadropAddr> list();

    /**
     * 修改钱包信息
     *
     * @param addr   钱包地址
     * @param status 钱包状态
     * @param desc   钱包备注
     */
    void updateStatusAndDesc(String addr, String status, String desc);

    /**
     * 删除地址
     *
     * @param addr
     */
    void delectAddr(String addr);

    /**
     * 删除地址
     *
     * @param addr
     */
    void inserWallet(String addr,String pk);
}