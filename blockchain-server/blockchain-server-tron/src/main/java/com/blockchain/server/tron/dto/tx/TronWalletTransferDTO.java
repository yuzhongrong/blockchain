package com.blockchain.server.tron.dto.tx;

import lombok.Data;

import java.math.BigDecimal;

/**
 * EthWalletTransfer 数据传输类
 *
 * @version 1.0
 * @date 2019-02-16 15:44:06
 */
@Data
public class TronWalletTransferDTO {
    private String id;
    private String hash;
    private String fromHexAddr;
    private String toHexAddr;
    private BigDecimal amount;
    private String tokenAddr;
    private String tokenSymbol;
    private BigDecimal gasPrice;
    private String gasTokenType;
    private String gasTokenName;
    private String gasTokenSymbol;
    private String transferType;
    private Integer status;
    private String remark;
    private java.util.Date createTime;
    private java.util.Date updateTime;

}