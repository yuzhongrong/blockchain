package com.blockchain.server.tron.entity;

import com.blockchain.common.base.entity.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * EthApplication 数据传输类
 *
 * @version 1.0
 * @date 2019-02-16 15:44:06
 */
@Table(name = "config_wallet_param")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConfigWalletParam extends BaseModel {
    @Id
    @Column(name = "id")
    private Integer id;
    @Column(name = "module_type")
    private String moduleType;
    @Column(name = "param_name")
    private String paramName;
    @Column(name = "param_value")
    private String paramValue;
    @Column(name = "param_descr")
    private String paramDescr;
    @Column(name = "status")
    private Integer status;
    @Column(name = "create_time")
    private java.util.Date createTime;
    @Column(name = "modify_time")
    private java.util.Date modifyTime;
}