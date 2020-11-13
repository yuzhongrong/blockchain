package com.blockchain.server.ltc.service.impl;


import com.blockchain.common.base.constant.WalletConstant;
import com.blockchain.common.base.dto.SessionUserDTO;
import com.blockchain.common.base.util.SecurityUtils;
import com.blockchain.server.ltc.common.enums.WalletEnums;
import com.blockchain.server.ltc.common.exception.WalletException;
import com.blockchain.server.ltc.entity.ClearingCorr;
import com.blockchain.server.ltc.mapper.ClearingCorrMapper;
import com.blockchain.server.ltc.service.IClearingCorrService;
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
public class ClearingCorrServiceImpl implements IClearingCorrService {
    @Autowired
    ClearingCorrMapper clearingCorrMapper;

    @Override
    public List<ClearingCorr> selectByTotalId(String totalId) {
        ClearingCorr corr = new ClearingCorr();
        corr.setTotalId(totalId);
        return clearingCorrMapper.select(corr);
    }

    @Override
    public void insert(ClearingCorr clearingCorr) {
        SessionUserDTO user = SecurityUtils.getUser();
        clearingCorr.setSystemUserId( null != user ? user.getId(): WalletConstant.SAVE);
        clearingCorr.setId(UUID.randomUUID().toString());
        int row = clearingCorrMapper.insert(clearingCorr);
        if (row == 0) {
            throw new WalletException(WalletEnums.SERVER_IS_TOO_BUSY);
        }
    }
}
