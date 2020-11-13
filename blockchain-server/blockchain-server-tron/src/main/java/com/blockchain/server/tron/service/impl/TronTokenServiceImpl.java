package com.blockchain.server.tron.service.impl;


import com.blockchain.common.base.util.ExceptionPreconditionUtils;
import com.blockchain.server.tron.common.enums.TronWalletEnums;
import com.blockchain.server.tron.common.exception.TronWalletException;
import com.blockchain.server.tron.entity.TronToken;
import com.blockchain.server.tron.mapper.TronTokenMapper;
import com.blockchain.server.tron.service.ITronTokenService;
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
public class TronTokenServiceImpl implements ITronTokenService {

    @Autowired
    TronTokenMapper tronTokenMapper;

    @Override
    public TronToken findByTokenName(String tokenName) {
        ExceptionPreconditionUtils.checkStringNotBlank(tokenName,
                new TronWalletException(TronWalletEnums.NULL_TOKENADDR));
        TronToken where = new TronToken();
        where.setTokenSymbol(tokenName);
        TronToken tronToken = null;
        try {
            tronToken = tronTokenMapper.selectOne(where);
        } catch (Exception e) {
            throw new TronWalletException(TronWalletEnums.IEXIST_TOKENADDRS);
        }
        if (tronToken == null) {
            throw new TronWalletException(TronWalletEnums.INEXISTENCE_TOKENADDR);
        }
        return tronToken;
    }

    @Override
    public List<TronToken> selectAll() {
        return tronTokenMapper.selectAll();
    }

    /**
     * 通过币种地址获取币种信息
     * @param tokenAddr
     * @return
     */
    @Override
    public TronToken findByTokenAddr(String tokenAddr) {
        TronToken example = new TronToken();
        example.setTokenAddr("TRX");
        return tronTokenMapper.selectOne(example);
    }

}
