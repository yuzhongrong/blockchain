package com.blockchain.server.tron.dto.wallet;

import com.blockchain.common.base.dto.BaseDTO;
import com.blockchain.server.tron.entity.TronClearingCorr;
import com.blockchain.server.tron.entity.TronClearingDetail;
import com.blockchain.server.tron.entity.TronClearingTotal;
import lombok.Data;

import java.util.List;

/**
 * TronWallet 数据传输类
 *
 * @version 1.0
 * @date 2019-02-16 15:44:06
 */
@Data
public class TronTotalInfoDTO extends BaseDTO {
    private TronClearingTotal total;
    private List<TronClearingCorr> corrs;
    private List<TronClearingDetail> details;
}