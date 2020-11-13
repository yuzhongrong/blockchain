package com.blockchain.server.ltc.dto;

import com.blockchain.common.base.dto.BaseDTO;
import com.blockchain.server.ltc.entity.ClearingCountDetail;
import com.blockchain.server.ltc.entity.ClearingCountTotal;
import lombok.Data;

import java.util.List;

/**
 * Wallet 数据传输类
 *
 * @version 1.0
 * @date 2019-02-16 15:44:06
 */
@Data
public class CountTotalInfoDTO extends BaseDTO {
    private ClearingCountTotal total;
    private List<ClearingCountDetail> details;
}