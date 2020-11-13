package com.blockchain.server.tron.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 创建资金钱包(用于创建钱包)表 dapp_tron_wallet_create
 *
 * @author ruoyi
 * @date 2019-06-29
 */
@Table(name = "dapp_tron_wallet_create")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TronWalletCreate {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;
    @Column(name = "addr")
    private String addr;
    @Column(name = "hex_addr")
    private String hexAddr;
    @Column(name = "token_addr")
    private String tokenAddr;
    @Column(name = "token_symbol")
    private String tokenSymbol;
    @Column(name = "private_key")
    private String privateKey;
    @Column(name = "remark")
    private String remark;
    @Column(name = "status")
    private String status;

}
