package com.blockchain.server.ltc.service.impl;


import com.blockchain.server.ltc.common.constants.DetailTxConstants;
import com.blockchain.server.ltc.common.enums.WalletEnums;
import com.blockchain.server.ltc.common.exception.WalletException;
import com.blockchain.server.ltc.entity.ClearingCountDetail;
import com.blockchain.server.ltc.entity.ClearingCountTotal;
import com.blockchain.server.ltc.mapper.ClearingCountDetailMapper;
import com.blockchain.server.ltc.service.IClearingCountDetailService;
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
public class ClearingCountDetailServiceImpl implements IClearingCountDetailService {

    @Autowired
    ClearingCountDetailMapper clearingCountDetailMapper;


    @Override
    public List<ClearingCountDetail> selectByTotalId(String totalId) {
        ClearingCountDetail detail = new ClearingCountDetail();
        detail.setTotalId(totalId);
        return clearingCountDetailMapper.select(detail);
    }

    @Override
    @Transactional
    public void insert(ClearingCountTotal total, Map<String, BigDecimal> fromMap, Map<String, BigDecimal> toMap) {
        insert(total, fromMap, DetailTxConstants.FROM);
        insert(total, toMap, DetailTxConstants.TO);
    }

    @Override
    @Transactional
    public void insert(ClearingCountTotal countTotal, Map<String, BigDecimal> map, String type) {
        for (Map.Entry<String, BigDecimal> dataMap : map.entrySet()) {
            ClearingCountDetail detail = new ClearingCountDetail();
            detail.setId(UUID.randomUUID().toString());
            detail.setTotalId(countTotal.getId());
            detail.setCreateTime(countTotal.getCreateTime());
            BigDecimal transferAmount = type.equalsIgnoreCase(DetailTxConstants.FROM) ?
                    dataMap.getValue().negate() : dataMap.getValue();
            detail.setTransferAmount(transferAmount);
            detail.setTransferType(dataMap.getKey());
            int row = clearingCountDetailMapper.insert(detail);
            if (row == 0) {
                throw new WalletException(WalletEnums.SERVER_IS_TOO_BUSY);
            }
        }
    }

    @Override
    @Transactional
    public void insert(String totalId, String txType, BigDecimal amount, Date endDate) {
        ClearingCountDetail detail = new ClearingCountDetail();
        detail.setId(UUID.randomUUID().toString());
        detail.setTotalId(totalId);
        detail.setTransferType(txType);
        detail.setTransferAmount(amount);
        detail.setCreateTime(endDate);
        int row = clearingCountDetailMapper.insert(detail);
        if (row == 0) {
            throw new WalletException(WalletEnums.SERVER_IS_TOO_BUSY);
        }
    }




}
