package com.blockchain.server.tron.dto.wallet;

import lombok.Data;

/**
 * @author: Liusd
 * @create: 2019-03-27 09:32
 **/
@Data
public class TronWalletOutDto {
    private String id;
    private String addr;
    private String tokenSymbol;
    private Integer tokenDecimals;
    private String remark;
}
