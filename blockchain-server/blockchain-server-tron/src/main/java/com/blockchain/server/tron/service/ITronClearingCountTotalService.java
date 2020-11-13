package com.blockchain.server.tron.service;

import com.blockchain.server.tron.dto.wallet.TronCountTotalInfoDTO;
import com.blockchain.server.tron.entity.TronClearingCountTotal;

import java.util.List;

/**
 * 期初期末记录表——业务接口
 *
 * @author YH
 * @date 2019年2月16日17:09:19
 */
public interface ITronClearingCountTotalService {

    /**
     * 获取最新的记录
     *
     * @return
     */
    TronClearingCountTotal findTotalLast(String coinName);

    /**
     * 所有币种最新统计的记录
     *
     */
    List<TronCountTotalInfoDTO> selectInfoAll();

    /**
     * 生成用户财务记录快照
     *
     * @param coinName 代币名称
     */
    void insertTotal(String coinName);

    /**
     * 生成用户财务记录快照(多个)
     *
     */
    List<TronCountTotalInfoDTO> insertTotals();

    /**
     * 插入一条记录
     *
     * @param countTotal
     */
    void insert(TronClearingCountTotal countTotal);
}
