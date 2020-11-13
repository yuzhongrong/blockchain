package com.blockchain.server.tron.service.impl;


import com.blockchain.server.tron.common.constants.tx.DetailTxConstants;
import com.blockchain.server.tron.common.enums.TronWalletEnums;
import com.blockchain.server.tron.common.exception.TronWalletException;
import com.blockchain.server.tron.entity.TronClearingCountDetail;
import com.blockchain.server.tron.entity.TronClearingCountTotal;
import com.blockchain.server.tron.mapper.TronClearingCountDetailMapper;
import com.blockchain.server.tron.service.ITronClearingCountDetailService;
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
public class TronClearingCountDetailServiceImpl implements ITronClearingCountDetailService {

    @Autowired
    TronClearingCountDetailMapper tronClearingCountDetailMapper;


    @Override
    public List<TronClearingCountDetail> selectByTotalId(String totalId) {
        TronClearingCountDetail detail = new TronClearingCountDetail();
        detail.setTotalId(totalId);
        return tronClearingCountDetailMapper.select(detail);
    }

    @Override
    @Transactional
    public void insert(TronClearingCountTotal total, Map<String, BigDecimal> fromMap, Map<String, BigDecimal> toMap) {
        insert(total, fromMap, DetailTxConstants.FROM);
        insert(total, toMap, DetailTxConstants.TO);
    }

    @Override
    @Transactional
    public void insert(TronClearingCountTotal countTotal, Map<String, BigDecimal> map, String type) {
        for (Map.Entry<String, BigDecimal> dataMap : map.entrySet()) {
            TronClearingCountDetail detail = new TronClearingCountDetail();
            detail.setId(UUID.randomUUID().toString());
            detail.setTotalId(countTotal.getId());
            detail.setCreateTime(countTotal.getCreateTime());
            BigDecimal transferAmount = type.equalsIgnoreCase(DetailTxConstants.FROM) ?
                    dataMap.getValue().negate() : dataMap.getValue();
            detail.setTransferAmount(transferAmount);
            detail.setTransferType(dataMap.getKey());
            int row = tronClearingCountDetailMapper.insert(detail);
            if (row == 0) {
                throw new TronWalletException(TronWalletEnums.SERVER_IS_TOO_BUSY);
            }
        }
    }

    @Override
    @Transactional
    public void insert(String totalId, String txType, BigDecimal amount, Date endDate) {
        TronClearingCountDetail detail = new TronClearingCountDetail();
        detail.setId(UUID.randomUUID().toString());
        detail.setTotalId(totalId);
        detail.setTransferType(txType);
        detail.setTransferAmount(amount);
        detail.setCreateTime(endDate);
        int row = tronClearingCountDetailMapper.insert(detail);
        if (row == 0) {
            throw new TronWalletException(TronWalletEnums.SERVER_IS_TOO_BUSY);
        }
    }

}
