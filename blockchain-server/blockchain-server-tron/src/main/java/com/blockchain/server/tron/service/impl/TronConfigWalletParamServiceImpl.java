package com.blockchain.server.tron.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.blockchain.common.base.dto.wallet.GasDTO;
import com.blockchain.common.base.util.JsonUtils;
import com.blockchain.server.tron.common.constants.TronConstant;
import com.blockchain.server.tron.common.enums.TronWalletEnums;
import com.blockchain.server.tron.common.exception.TronWalletException;
import com.blockchain.server.tron.dto.wallet.ConfigWalletParamDto;
import com.blockchain.server.tron.entity.ConfigWalletParam;
import com.blockchain.server.tron.mapper.ConfigWalletParamMapper;
import com.blockchain.server.tron.service.TronConfigWalletParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author: Liusd
 * @create: 2019-03-22 11:53
 **/
@Service
public class TronConfigWalletParamServiceImpl implements TronConfigWalletParamService {

    @Autowired
    ConfigWalletParamMapper configWalletParamMapper;

    @Override
    public List<ConfigWalletParamDto> tronConfigWalletParamList() {
        List<ConfigWalletParam> list = configWalletParamMapper.selectAllByParamNameContainsGasConfig();
        List<ConfigWalletParamDto> configWalletParamDtos = new ArrayList<>();
        for (ConfigWalletParam configWalletParam : list){
            ConfigWalletParamDto configWalletParamDto = new ConfigWalletParamDto();
            configWalletParamDto.setId(configWalletParam.getId());
            configWalletParamDto.setModuleType(configWalletParam.getModuleType());
            configWalletParamDto.setCreateTime(configWalletParam.getCreateTime());
            configWalletParamDto.setModifyTime(configWalletParam.getModifyTime());
            configWalletParamDto.setParamDescr(configWalletParam.getParamDescr());
            configWalletParamDto.setParamName(configWalletParam.getParamName());
            configWalletParamDto.setStatus(configWalletParam.getStatus());
            configWalletParamDto.setParamValue(JsonUtils.jsonToPojo(configWalletParam.getParamValue(), GasDTO.class));
            configWalletParamDtos.add(configWalletParamDto);
        }
        return configWalletParamDtos;
    }

    @Transactional
    @Override
    public void updateConfigWallet(BigDecimal gasPrice, BigDecimal minWdAmount, Integer id) {
        ConfigWalletParam configWalletParam = configWalletParamMapper.selectByPrimaryKey(id);
        if (configWalletParam == null){
            throw new TronWalletException(TronWalletEnums.NULL_TX);
        }
        GasDTO gasDTO = JsonUtils.jsonToPojo(configWalletParam.getParamValue(), GasDTO.class);
        gasDTO.setGasPrice(gasPrice);
        gasDTO.setMinWdAmount(minWdAmount);
        configWalletParam.setParamValue(JSONObject.toJSONString(gasDTO));
        configWalletParamMapper.updateByPrimaryKey(configWalletParam);
    }

    /**
     * 查询资金归集钱包地址
     * @param tokenSymbol
     * @return
     */
    @Override
    public List<ConfigWalletParam> getCollectionWallet(String tokenSymbol) {
        String paramName = TronConstant.ConfigWallet.PARAM_NAME + tokenSymbol.toLowerCase();
        List<ConfigWalletParam> paramList = configWalletParamMapper.selectByParamName(TronConstant.ConfigWallet.MODULE_TYPE, paramName);
        return paramList;
    }

    /**
     * 修改配置表归集地址
     * @param id
     * @param addr
     * @param describe
     * @return
     */
    @Override
    public Integer updateCollectionWallet(Integer id, String addr, String describe) {
        ConfigWalletParam configWalletParam = new ConfigWalletParam();
        configWalletParam.setId(id);
        configWalletParam.setParamValue(addr);
        configWalletParam.setParamDescr(describe);
        configWalletParam.setModifyTime(new Date());
        return configWalletParamMapper.updateByPrimaryKeySelective(configWalletParam);
    }
}
