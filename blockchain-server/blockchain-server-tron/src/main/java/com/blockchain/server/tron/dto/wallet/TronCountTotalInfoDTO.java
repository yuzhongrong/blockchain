package com.blockchain.server.tron.dto.wallet;

import com.blockchain.common.base.dto.BaseDTO;
import com.blockchain.server.tron.entity.TronClearingCountDetail;
import com.blockchain.server.tron.entity.TronClearingCountTotal;
import lombok.Data;

import java.util.List;

/**
 * TronWallet 数据传输类
 *
 * @version 1.0
 * @date 2019-02-16 15:44:06
 */
@Data
public class TronCountTotalInfoDTO extends BaseDTO {
    private TronClearingCountTotal total;
    private List<TronClearingCountDetail> details;
}