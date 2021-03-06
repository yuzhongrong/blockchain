package com.blockchain.server.ltc.service.impl;


import com.blockchain.server.ltc.common.constants.DetailTxConstants;
import com.blockchain.server.ltc.common.enums.WalletEnums;
import com.blockchain.server.ltc.common.exception.WalletException;
import com.blockchain.server.ltc.entity.ClearingDetail;
import com.blockchain.server.ltc.entity.ClearingTotal;
import com.blockchain.server.ltc.mapper.ClearingDetailMapper;
import com.blockchain.server.ltc.service.IClearingDetailService;
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
public class ClearingDetailServiceImpl implements IClearingDetailService {

    @Autowired
    ClearingDetailMapper clearingDetailMapper;

    @Override
    public List<ClearingDetail> selectByTotalId(String totalId) {
        ClearingDetail detail = new ClearingDetail();
        detail.setTotalId(totalId);
        return clearingDetailMapper.select(detail);
    }

    @Override
    @Transactional
    public void insert(ClearingTotal total, Map<String, Map<String, BigDecimal>> fromMap, Map<String, Map<String,
            BigDecimal>> toMap) {
        insert(total, fromMap, DetailTxConstants.FROM);
        insert(total, toMap, DetailTxConstants.TO);
    }

    @Override
    @Transactional
    public void insert(ClearingTotal total, Map<String, Map<String, BigDecimal>> map, String type) {
        for (Map.Entry<String, Map<String, BigDecimal>> tokenMap : map.entrySet()) {
            for (Map.Entry<String, BigDecimal> dataMap : tokenMap.getValue().entrySet()) {
                ClearingDetail detail = new ClearingDetail();
                detail.setId(UUID.randomUUID().toString());
                detail.setTotalId(total.getId());
                detail.setCreateTime(total.getCreateTime());
                BigDecimal transferAmount = type.equalsIgnoreCase(DetailTxConstants.FROM) ?
                        dataMap.getValue().negate() : dataMap.getValue();
                detail.setTransferAmount(transferAmount);
                detail.setTransferType(dataMap.getKey());
                int row = clearingDetailMapper.insert(detail);
                if (row == 0) {
                    throw new WalletException(WalletEnums.SERVER_IS_TOO_BUSY);
                }
            }
        }
    }

    @Override
    @Transactional
    public void insert(String totalId, String txType, BigDecimal amount, Date endDate) {
        ClearingDetail detail = new ClearingDetail();
        detail.setId(UUID.randomUUID().toString());
        detail.setTotalId(totalId);
        detail.setTransferType(txType);
        detail.setTransferAmount(amount);
        detail.setCreateTime(endDate);
        int row = clearingDetailMapper.insert(detail);
        if (row == 0) {
            throw new WalletException(WalletEnums.SERVER_IS_TOO_BUSY);
        }
    }

}
