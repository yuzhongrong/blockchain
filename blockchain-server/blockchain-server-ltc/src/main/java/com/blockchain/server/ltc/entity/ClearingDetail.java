package com.blockchain.server.ltc.entity;

import com.blockchain.common.base.entity.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * ClearingDetail 数据传输类
 *
 * @version 1.0
 * @date 2019-02-16 15:44:06
 */
@Table(name = "dapp_ltc_clearing_detail")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClearingDetail extends BaseModel {
    @Id
    @Column(name = "id")
    private String id;
    @Column(name = "total_id")
    private String totalId;
    @Column(name = "transfer_type")
    private String transferType;
    @Column(name = "transfer_amount")
    private BigDecimal transferAmount;
    @Column(name = "create_time")
    private Date createTime;
}