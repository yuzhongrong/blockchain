package com.blockchain.server.eth.dto.wallet;

import com.blockchain.common.base.dto.user.UserBaseInfoDTO;
import com.blockchain.common.base.entity.BaseModel;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class EthPrivateBalanceDTO extends BaseModel {

    //用户基本信息
    private UserBaseInfoDTO userBaseInfo;

    private String id;
    private String userOpenId;
    private String addr;
    private String tokenAddr;
    private String tokenSymbol;
    private BigDecimal privateBalance;
    private BigDecimal releaseBalance;
    private String walletType;
    private java.util.Date createTime;
    private java.util.Date modifyTime;

}