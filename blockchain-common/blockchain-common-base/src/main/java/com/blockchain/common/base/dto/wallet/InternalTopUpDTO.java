package com.blockchain.common.base.dto.wallet;

import com.blockchain.common.base.dto.BaseDTO;
import com.blockchain.common.base.dto.user.UserBaseInfoDTO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Datetime: 2020/5/20   19:00
 * @Author: Xia rong tao
 * @title  内部充值dto
 */
@Data
public class InternalTopUpDTO  extends WalletTxDTO {


  private String toUserId;
  UserBaseInfoDTO toUserBaseInfoDTO; // 转入用户信息

}
