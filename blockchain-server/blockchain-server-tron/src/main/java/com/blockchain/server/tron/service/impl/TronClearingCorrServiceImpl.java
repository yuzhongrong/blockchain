package com.blockchain.server.tron.service.impl;


import com.blockchain.common.base.constant.WalletConstant;
import com.blockchain.common.base.dto.SessionUserDTO;
import com.blockchain.common.base.util.SecurityUtils;
import com.blockchain.server.tron.common.enums.TronWalletEnums;
import com.blockchain.server.tron.common.exception.TronWalletException;
import com.blockchain.server.tron.entity.TronClearingCorr;
import com.blockchain.server.tron.mapper.TronClearingCorrMapper;
import com.blockchain.server.tron.service.ITronClearingCorrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * 以太坊钱包表——业务接口
 *
 * @author YH
 * @date 2019年2月16日17:25:02
 */
@Service
public class TronClearingCorrServiceImpl implements ITronClearingCorrService {
    @Autowired
    TronClearingCorrMapper tronClearingCorrMapper;

    @Override
    public List<TronClearingCorr> selectByTotalId(String totalId) {
        TronClearingCorr corr = new TronClearingCorr();
        corr.setTotalId(totalId);
        return tronClearingCorrMapper.select(corr);
    }

    @Override
    public void insert(TronClearingCorr tronClearingCorr) {
        SessionUserDTO user = SecurityUtils.getUser();
        tronClearingCorr.setSystemUserId( null != user ? user.getId(): WalletConstant.SAVE);
        tronClearingCorr.setId(UUID.randomUUID().toString());
        int row = tronClearingCorrMapper.insert(tronClearingCorr);
        if (row == 0) {
            throw new TronWalletException(TronWalletEnums.SERVER_IS_TOO_BUSY);
        }
    }
}
