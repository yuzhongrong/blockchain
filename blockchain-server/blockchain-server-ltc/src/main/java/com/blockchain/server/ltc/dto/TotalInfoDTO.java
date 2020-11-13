package com.blockchain.server.ltc.dto;

import com.blockchain.common.base.dto.BaseDTO;
import com.blockchain.server.ltc.entity.ClearingCorr;
import com.blockchain.server.ltc.entity.ClearingDetail;
import com.blockchain.server.ltc.entity.ClearingTotal;
import lombok.Data;

import java.util.List;

/**
 * Wallet 数据传输类
 *
 * @version 1.0
 * @date 2019-02-16 15:44:06
 */
@Data
public class TotalInfoDTO extends BaseDTO {
    private ClearingTotal total;
    private List<ClearingCorr> corrs;
    private List<ClearingDetail> details;
}