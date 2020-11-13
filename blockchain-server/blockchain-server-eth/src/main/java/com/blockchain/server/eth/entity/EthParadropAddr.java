package com.blockchain.server.eth.entity;

import com.blockchain.common.base.entity.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * EthApplication 数据传输类
 * @date 2019-02-16 15:44:06
 * @version 1.0
 */
@Table(name = "dapp_eth_paradrop_addr")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EthParadropAddr extends BaseModel {
	@Id
	@Column(name = "send_addr")
	private String sendAddr;
	@Column(name = "send_pk")
	private String sendPk;
	@Column(name = "status")
	private String status;
	@Column(name = "`desc`")
	private String desc;
}