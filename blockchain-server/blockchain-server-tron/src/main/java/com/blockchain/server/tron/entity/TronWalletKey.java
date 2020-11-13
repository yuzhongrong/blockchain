package com.blockchain.server.tron.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Date;

/**
 * 币种表 dapp_tron_token
 *
 * @author ruoyi
 * @date 2019-06-29
 */

@Table(name = "dapp_tron_wallet_key")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TronWalletKey {
    private static final long serialVersionUID = 1L;

    @Column(name = "user_open_id")
    private String userOpenId;
    @Column(name = "addr")
    private String addr;
    @Column(name = "hex_addr")
    private String hexAddr;
    @Column(name = "private_key")
    private String privateKey;
    @Column(name = "keystore")
    private String keystore;
    @Column(name = "create_time")
    private String createTime;
    @Column(name = "update_time")
    private Date updateTime;
}
