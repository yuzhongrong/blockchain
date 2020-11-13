package com.blockchain.server.ltc.service;


import com.blockchain.server.ltc.dto.TotalInfoDTO;
import com.blockchain.server.ltc.dto.WalletDTO;
import com.blockchain.server.ltc.entity.ClearingTotal;
import com.blockchain.server.ltc.entity.Wallet;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 期初期末记录表——业务接口
 *
 * @author YH
 * @date 2019年2月16日17:09:19
 */
public interface IClearingTotalService {

    /**
     * 获取钱包最新的统计记录
     *
     * @param addr        钱包地址
     * @param tokenSymbol 代币名称
     * @param walletType  钱包类型
     * @return
     */
    ClearingTotal findNewByWallet(String addr, String tokenSymbol, String walletType);

    /**
     * 获取统计记录
     *
     * @param id
     * @return
     */
    ClearingTotal findById(String id);

    /**
     * 获取用户所有币种最新统计的记录
     *
     * @param userId
     */
    List<TotalInfoDTO> selectInfoAll(String userId);

    /**
     * 更正统计记录的资金
     *
     * @param id     统计记录ID
     * @param amount 金额
     */
    void updateCorr(String id, BigDecimal amount);

    /**
     * 生成用户财务记录快照
     *
     * @param endDate 期末时间
     * @param wallet  钱包对象
     */
    void insertTotal(WalletDTO wallet, Date endDate);

    /**
     * 生成用户财务记录快照
     *
     * @param wallet  钱包对象
     * @param endDate 期末时间
     */
    void insertTotal(Wallet wallet, Date endDate);

    /**
     * 生成用户财务记录快照(多个)
     *
     * @param userId 用户ID
     */
    List<TotalInfoDTO> insertTotals(String userId);

    /**
     * 插入数据
     *
     * @param clearingTotal
     */
    void insert(ClearingTotal clearingTotal);

}
