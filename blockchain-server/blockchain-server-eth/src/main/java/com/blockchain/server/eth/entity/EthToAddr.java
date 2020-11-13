package com.blockchain.server.eth.entity;

import com.blockchain.common.base.entity.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * EthToAddr 数据传输类
 * @date 2019-02-16 15:44:06
 * @version 1.0
 */
@Table(name = "dapp_eth_to_addr")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EthToAddr extends BaseModel {
	@Id
	@Column(name = "addr")
	private String addr;
	@Column(name = "pk")
	private String pk;

}