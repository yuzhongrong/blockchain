package com.blockchain.server.eth.entity;

import com.blockchain.common.base.entity.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * EthApplication 数据传输类
 * @date 2019-02-16 15:44:06
 * @version 1.0
 */
@Table(name = "dapp_eth_paradrop")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EthParadrop extends BaseModel {
	@Id
	@Column(name = "id")
	private String id;
	@Column(name = "send_status")
	private String sendStatus;
	@Column(name = "send_count")
	private BigDecimal sendCount;
	@Column(name = "send_amount")
	private BigDecimal sendAmount;
	@Column(name = "send_ratio")
	private BigDecimal sendRatio;
}