package com.blockchain.server.tron.entity;

import com.blockchain.common.base.entity.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * EthApplication 数据传输类
 * @date 2019-02-16 15:44:06
 * @version 1.0
 */
@Table(name = "dapp_tron_application")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TronApplication extends BaseModel {
	@Column(name = "app_id")
	private String appId;
	@Column(name = "app_name")
	private String appName;

}