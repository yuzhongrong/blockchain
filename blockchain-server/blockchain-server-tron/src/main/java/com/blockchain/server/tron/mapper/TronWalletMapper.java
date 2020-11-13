package com.blockchain.server.tron.mapper;

import com.blockchain.common.base.dto.wallet.WalletBaseDTO;
import com.blockchain.common.base.dto.wallet.WalletParamsDTO;
import com.blockchain.server.tron.dto.wallet.TronWalletDTO;
import com.blockchain.server.tron.entity.TronWallet;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * TronWalletMapper 数据访问类
 *
 * @version 1.0
 * @date 2019-02-16 15:44:06
 */
@Repository
public interface TronWalletMapper extends Mapper<TronWallet> {

    /**
     * 根据钱包地址、币种地址、钱包类型查询钱包信息
     *
     * @param addr      钱包地址
     * @param tokenAddr 币种地址
     * @return
     */
    TronWalletDTO selectByAddrAndTokenAddr(@Param("addr") String addr,
                                           @Param("tokenAddr") String tokenAddr);

    /**
     * 修改用户钱包
     *
     * @param balance
     * @param freeBalance
     * @param freezeBalance
     * @param userOpenId
     * @param tokenAddr
     * @param walletType
     * @return
     */
    Integer updateWalletAllBalanceInRowLock(@Param("balance") BigDecimal balance,
                                            @Param("freeBalance") BigDecimal freeBalance,
                                            @Param("freezeBalance") BigDecimal freezeBalance,
                                            @Param("userOpenId") String userOpenId,
                                            @Param("tokenAddr") String tokenAddr,
                                            @Param("walletType") String walletType,
                                            @Param("modifyTime") Date modifyTime);

    /**
     * 根据符号查询钱包
     *
     * @param userId
     * @param tokenSymbol
     * @param walletType
     * @return
     */
    TronWallet selectWalletByTokenSymbol(@Param("userId") String userId, @Param("tokenSymbol") String tokenSymbol, @Param("walletType") String walletType);

    /**
     * 根据钱包地址、币种地址、钱包类型查询钱包信息
     *
     * @param addr       钱包地址
     * @param tokenAddr  币种地址
     * @param walletType 钱包类型
     * @return
     */
    TronWalletDTO selectByAddrAndTokenAddrAndWalletType(@Param("addr") String addr,
                                                        @Param("tokenAddr") String tokenAddr,
                                                        @Param("walletType") String walletType);

    /**
     * 根据用户ID、币种地址、钱包类型查询钱包信息
     *
     * @param userOpenId 用户ID
     * @param tokenAddr  币种地址
     * @param walletType 钱包类型
     * @return
     */
    TronWalletDTO selectByUserOpenIdAndTokenAddrAndWalletType(@Param("userOpenId") String userOpenId,
                                                              @Param("tokenAddr") String tokenAddr,
                                                              @Param("walletType") String walletType);

    /**
     * 根据钱包地址、钱包类型查询钱包信息
     *
     * @param addr       钱包地址
     * @param walletType 钱包类型
     * @return
     */
    List<TronWalletDTO> selectByAddrAndWalletType(@Param("addr") String addr,
                                                  @Param("walletType") String walletType);

    /**
     * 根据用户ID、钱包类型查询钱包信息
     *
     * @param userOpenId 用户ID
     * @param walletType 钱包类型
     * @return
     */
    List<TronWalletDTO> selectByUserOpenIdAndWalletType(@Param("userOpenId") String userOpenId,
                                                        @Param("walletType") String walletType);


    /**
     * 修改用户指定钱包的金额
     *
     * @param userOpenId    用户标识
     * @param tokenAddr     币种地址
     * @param walletType    应用钱包
     * @param balance       总金额
     * @param freeBalance   可用金额
     * @param freezeBalance 冻结金额
     * @return
     */
    int updateBalanceByUserIdInRowLock(@Param("userOpenId") String userOpenId,
                                       @Param("tokenAddr") String tokenAddr,
                                       @Param("walletType") String walletType,
                                       @Param("balance") BigDecimal balance,
                                       @Param("freeBalance") BigDecimal freeBalance,
                                       @Param("freezeBalance") BigDecimal freezeBalance,
                                       @Param("updateTime") Date updateTime);

    /**
     * 修改用户指定钱包的金额
     *
     * @param addr          钱包地址
     * @param tokenAddr     币种地址
     * @param walletType    应用钱包
     * @param balance       总金额
     * @param freeBalance   可用金额
     * @param freezeBalance 冻结金额
     * @return
     */
    int updateBalanceByAddrInRowLock(@Param("addr") String addr,
                                     @Param("tokenAddr") String tokenAddr,
                                     @Param("walletType") String walletType,
                                     @Param("balance") BigDecimal balance,
                                     @Param("freeBalance") BigDecimal freeBalance,
                                     @Param("freezeBalance") BigDecimal freezeBalance,
                                     @Param("updateTime") Date updateTime);

    /**
     * 钱包查询
     *
     * @param paramsDTO 条件参数
     * @return
     */
    List<WalletBaseDTO> selectQuery(@Param("params") WalletParamsDTO paramsDTO);
}