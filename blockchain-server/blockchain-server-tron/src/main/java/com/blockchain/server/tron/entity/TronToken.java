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

@Table(name = "dapp_tron_token")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TronToken {
    private static final long serialVersionUID = 1L;

    @Column(name = "token_addr")
    private String tokenAddr;
    @Column(name = "token_hex_addr")
    private String tokenHexAddr;
    @Column(name = "token_symbol")
    private String tokenSymbol;
    @Column(name = "token_decimal")
    private Integer tokenDecimal;
    @Column(name = "issue_time")
    private Date issueTime;
    @Column(name = "total_supply")
    private String totalSupply;
    @Column(name = "total_circulation")
    private String totalCirculation;
    @Column(name = "descr")
    private String descr;

}
