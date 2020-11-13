package com.blockchain.server.tron.service;

import com.blockchain.common.base.dto.WalletChangeDTO;
import com.blockchain.common.base.dto.WalletOrderDTO;
import com.blockchain.common.base.dto.wallet.WalletBaseDTO;
import com.blockchain.common.base.dto.wallet.WalletParamsDTO;
import com.blockchain.server.tron.dto.wallet.TronWalletDTO;
import com.blockchain.server.tron.entity.TronWallet;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 以太坊钱包表——业务接口
 *
 * @author YH
 * @date 2019年2月16日17:09:19
 */
public interface ITronWalletService {


    /**
     * 查询所有用户的钱包信息
     *
     * @param paramsDTO
     * @return
     */
    List<WalletBaseDTO> select(WalletParamsDTO paramsDTO);

    /**
     * 查询所有用户的链上钱包信息
     *
     * @param paramsDTO
     * @return
     */
    List<WalletBaseDTO> selectBlock(WalletParamsDTO paramsDTO);


    /**
     * 查询用户的所有钱包
     *
     * @param userId 用户ID
     * @return
     */
    List<TronWallet> selectByUserId(String userId);

    /**
     * 查询用户某个钱包
     *
     * @param userId     用户ID
     * @param tokenAddr  代币地址
     * @param walletType 钱包类型
     * @return
     */
    TronWalletDTO findByUserIdAndTokenAddrAndWalletType(String userId, String tokenAddr, String walletType);

    /**
     * 查询用户某个钱包
     *
     * @param hexAddr     钱包地址
     * @param coinName 币种名称
     * @return
     */
    TronWallet findByAddrAndCoinName(String hexAddr, String coinName);

    /**
     * 修改可用余额
     *
     * @param userId     用户ID
     * @param tokenAddr  代币地址
     * @param walletType 钱包类型
     * @param amount     修改金额
     */
    void updateFreeBalance(String userId, String tokenAddr, String walletType, BigDecimal amount, Date date);

    /**
     * 钱包冻结与可用余额转换
     *
     * @param orderDTO
     */
    void updateBlanceTransform(WalletOrderDTO orderDTO);

    /**
     * 钱包余额变动
     *
     * @param changeDTO
     */
    void updateBlance(WalletChangeDTO changeDTO);

    /**
     * 钱包余额变动
     *
     * @param addr         钱包地址
     * @param coinName     币种名称
     * @param freeBlance   可用余额
     * @param freezeBlance 冻结余额
     * @param txType       记录类型
     */
    void updateBlance(String addr, String coinName, String freeBlance, String freezeBlance, String txType);

    /**
     * 钱包余额变动
     *
     * @param hexAddr         钱包地址
     * @param coinName     币种名称
     * @param freeBlance   可用余额
     * @param freezeBlance 冻结余额
     * @param date         时间
     */
    void updateBlance(String hexAddr, String coinName, BigDecimal freeBlance, BigDecimal freezeBlance, Date date);

    /**
     * WEB3J转账
     *
     * @param fromAddr  支付地址
     * @param toAddr    收款地址
     * @param tokenName 代币名称
     */
    void collection(String fromAddr, String toAddr, String tokenName);

    /**
     * 估算手续费
     *
     * @param fromAddr
     * @param toAddr
     * @param tokenName
     * @return
     */
    Map<String, BigDecimal> getGas(String fromAddr, String toAddr, String tokenName);

    /**
     * 冻结与解冻方法
     *
     * @param walletOrderDTO
     * @return
     */
    Integer updateWalletOrder(WalletOrderDTO walletOrderDTO);

    /**
     * 钱包余额变动方法
     *
     * @param walletChangeDTO
     */
    Integer handleWalletChange(WalletChangeDTO walletChangeDTO);

    /**
     * 根据符号查询钱包
     *
     * @param userId
     * @param tokenSymbol
     * @param walletType
     * @return
     */
    TronWallet selectWalletByTokenSymbol(String userId, String tokenSymbol, String walletType);

    /**
     * 打油费
     *
     * @param userId
     * @param walletType
     * @param coinName
     */
    // void addGas(String userId, String walletType, String coinName, String amount);
}
