package com.blockchain.server.tron.service.impl;


import com.blockchain.server.tron.common.constants.tx.DetailTxConstants;
import com.blockchain.server.tron.common.enums.TronWalletEnums;
import com.blockchain.server.tron.common.exception.TronWalletException;
import com.blockchain.server.tron.entity.TronClearingDetail;
import com.blockchain.server.tron.entity.TronClearingTotal;
import com.blockchain.server.tron.mapper.TronClearingDetailMapper;
import com.blockchain.server.tron.service.ITronClearingDetailService;
import com.blockchain.server.tron.common.constants.tx.DetailTxConstants;
import com.blockchain.server.tron.common.enums.TronWalletEnums;
import com.blockchain.server.tron.common.exception.TronWalletException;
import com.blockchain.server.tron.entity.TronClearingDetail;
import com.blockchain.server.tron.entity.TronClearingTotal;
import com.blockchain.server.tron.service.ITronClearingDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 财务详情记录——业务接口
 *
 * @author YH
 * @date 2019年2月16日17:09:19
 */
@Service
public class TronClearingDetailServiceImpl implements ITronClearingDetailService {

    @Autowired
    TronClearingDetailMapper tronClearingDetailMapper;

    @Override
    public List<TronClearingDetail> selectByTotalId(String totalId) {
        TronClearingDetail detail = new TronClearingDetail();
        detail.setTotalId(totalId);
        return tronClearingDetailMapper.select(detail);
    }

    @Override
    @Transactional
    public void insert(TronClearingTotal total, Map<String, Map<String, BigDecimal>> fromMap, Map<String, Map<String,
            BigDecimal>> toMap) {
        insert(total, fromMap, DetailTxConstants.FROM);
        insert(total, toMap, DetailTxConstants.TO);
    }

    @Override
    @Transactional
    public void insert(TronClearingTotal total, Map<String, Map<String, BigDecimal>> map, String type) {
        for (Map.Entry<String, Map<String, BigDecimal>> tokenMap : map.entrySet()) {
            for (Map.Entry<String, BigDecimal> dataMap : tokenMap.getValue().entrySet()) {
                TronClearingDetail detail = new TronClearingDetail();
                detail.setId(UUID.randomUUID().toString());
                detail.setTotalId(total.getId());
                detail.setCreateTime(total.getCreateTime());
                BigDecimal transferAmount = type.equalsIgnoreCase(DetailTxConstants.FROM) ?
                        dataMap.getValue().negate() : dataMap.getValue();
                detail.setTransferAmount(transferAmount);
                detail.setTransferType(dataMap.getKey());
                int row = tronClearingDetailMapper.insert(detail);
                if (row == 0) {
                    throw new TronWalletException(TronWalletEnums.SERVER_IS_TOO_BUSY);
                }
            }
        }
    }

    @Override
    @Transactional
    public void insert(String totalId, String txType, BigDecimal amount, Date endDate) {
        TronClearingDetail detail = new TronClearingDetail();
        detail.setId(UUID.randomUUID().toString());
        detail.setTotalId(totalId);
        detail.setTransferType(txType);
        detail.setTransferAmount(amount);
        detail.setCreateTime(endDate);
        int row = tronClearingDetailMapper.insert(detail);
        if (row == 0) {
            throw new TronWalletException(TronWalletEnums.SERVER_IS_TOO_BUSY);
        }
    }

}
