package com.blockchain.server.tron.dto.wallet;

import com.blockchain.common.base.dto.BaseDTO;
import lombok.Data;

import java.math.BigDecimal;

/**
 * EthWallet 数据传输类
 *
 * @version 1.0
 * @date 2019-02-16 15:44:06
 */
@Data
public class TronWalletDTO extends BaseDTO {
    private String addr;
    private String hexAddr;
    private String tokenAddr;
    private String userOpenId;
    private String tokenSymbol;
    private int tokenDecimal;
    private BigDecimal balance;
    private BigDecimal freeBalance;
    private BigDecimal freezeBalance;
    private String walletType;
    private java.util.Date createTime;
    private java.util.Date updateTime;
}