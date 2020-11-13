package com.blockchain.server.tron.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 托管钱包表 dapp_tron_wallet
 *
 * @author ruoyi
 * @date 2019-06-29
 */
@Table(name = "dapp_tron_wallet")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TronWallet {
    private static final long serialVersionUID = 1L;

    @Column(name = "addr")
    private String addr;
    @Column(name = "hex_addr")
    private String hexAddr;
    @Column(name = "token_addr")
    private String tokenAddr;
    @Column(name = "user_open_id")
    private String userOpenId;
    @Column(name = "token_symbol")
    private String tokenSymbol;
    @Column(name = "token_decimal")
    private Integer tokenDecimal;
    @Column(name = "balance")
    private BigDecimal balance;
    @Column(name = "free_balance")
    private BigDecimal freeBalance;
    @Column(name = "freeze_balance")
    private BigDecimal freezeBalance;
    @Column(name = "create_time")
    private Date createTime;
    @Column(name = "update_time")
    private Date updateTime;
    @Column(name = "wallet_type")
    private String walletType;

}
