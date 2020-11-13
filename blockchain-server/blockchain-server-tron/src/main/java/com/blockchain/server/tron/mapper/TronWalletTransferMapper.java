package com.blockchain.server.tron.mapper;

import com.blockchain.common.base.dto.wallet.WalletTxDTO;
import com.blockchain.common.base.dto.wallet.WalletTxParamsDTO;
import com.blockchain.server.tron.dto.tx.TronWalletTransferDTO;
import com.blockchain.server.tron.entity.TronWalletTransfer;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;
import java.util.List;

/**
 * TronWalletTransferMapper 数据访问类
 *
 * @version 1.0
 * @date 2019-02-16 15:44:06
 */
@Repository
public interface TronWalletTransferMapper extends Mapper<TronWalletTransfer> {

    /**
     * 钱包提现查询
     *
     * @param id
     * @return
     */
    WalletTxDTO findOutTx(@Param("id") String id);

    /**
     * 钱包提现查询
     *
     * @param id
     * @return
     */
    TronWalletTransfer findByIdForUpdate(@Param("id") String id);

    /**
     * 钱包充值查询
     *
     * @param id
     * @return
     */
    WalletTxDTO findInTx(@Param("id") String id);

    /**
     * 钱包提现查询
     *
     * @param params 参数条件
     * @return
     */
    List<WalletTxDTO> selectOutTx(@Param("params") WalletTxParamsDTO params);

    /**
     * 钱包充值查询
     *
     * @param params 参数条件
     * @return
     */
    List<WalletTxDTO> selectInTx(@Param("params") WalletTxParamsDTO params);

    /**
     * 查询多个地址，在某个时间区间成功的记录
     *
     * @param addr      地址
     * @param tokenAddr 代币地址
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @return
     */
    List<TronWalletTransferDTO> selectByAddrAndTime(@Param("addr") String addr,
                                                    @Param("tokenAddr") String tokenAddr,
                                                    @Param("startDate") Date startDate,
                                                    @Param("endDate") Date endDate);

}