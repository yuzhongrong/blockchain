package com.blockchain.server.btc.entity;

import com.blockchain.common.base.entity.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * BtcWalletKeyDTO 数据传输类
 *
 * @version 1.0
 * @date 2019-02-16 15:08:16
 */
@Table(name = "dapp_btc_wallet_key")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BtcWalletKey extends BaseModel {
    @Id
    @Column(name = "addr")
    private String addr;
    @Column(name = "private_key")
    private String privateKey;

}