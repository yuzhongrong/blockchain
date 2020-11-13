package com.blockchain.server.ltc.service.impl;

import com.blockchain.server.ltc.entity.ConfigWalletParam;
import com.blockchain.server.ltc.mapper.ConfigWalletParamMapper;
import com.blockchain.server.ltc.service.IConfigWalletParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConfigWalletParamServiceImpl implements IConfigWalletParamService {

    @Autowired
    ConfigWalletParamMapper configWalletParamMapper;

    public static final String PARAM_LTC_GASWALLET = "ltc_gas_wallet"; // ltc油费钱包

    @Override
    public String getGas() {
        return configWalletParamMapper.selectOne(where(PARAM_LTC_GASWALLET)).getParamValue();
    }

    /**
     * 条件方法
     *
     * @param paramName
     * @return
     */
    private ConfigWalletParam where(String paramName) {
        ConfigWalletParam configWalletParam = new ConfigWalletParam();
        configWalletParam.setParamName(paramName);
        return configWalletParam;
    }
}
