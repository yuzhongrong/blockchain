package com.blockchain.server.ltc.service.impl;


import com.blockchain.common.base.util.ExceptionPreconditionUtils;
import com.blockchain.server.ltc.common.enums.WalletEnums;
import com.blockchain.server.ltc.common.exception.WalletException;
import com.blockchain.server.ltc.entity.Application;
import com.blockchain.server.ltc.mapper.ApplicationMapper;
import com.blockchain.server.ltc.service.IApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 以太坊币种表——业务接口
 *
 * @author YH
 * @date 2019年2月16日17:25:02
 */
@Service
public class ApplicationServiceImpl implements IApplicationService {

    @Autowired
    ApplicationMapper applicationMapper;

    @Override
    public void CheckWalletType(String appId) {
        ExceptionPreconditionUtils.checkStringNotBlank(appId, new WalletException(WalletEnums.NULL_WALLETTYPE));
        List<Application> list = selectAll();
        for (Application row : list) {
            if (appId.equalsIgnoreCase(row.getAppId())) {
                return;
            }
        }
        throw new WalletException(WalletEnums.INEXISTENCE_WALLETTYPE);
    }


    @Override
    public List<Application> selectAll() {
        return applicationMapper.selectAll();
    }
}
