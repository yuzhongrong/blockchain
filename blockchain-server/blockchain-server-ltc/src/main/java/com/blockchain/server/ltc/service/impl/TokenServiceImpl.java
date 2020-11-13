package com.blockchain.server.ltc.service.impl;


import com.blockchain.common.base.util.ExceptionPreconditionUtils;
import com.blockchain.server.ltc.common.enums.WalletEnums;
import com.blockchain.server.ltc.common.exception.WalletException;
import com.blockchain.server.ltc.entity.Token;
import com.blockchain.server.ltc.mapper.TokenMapper;
import com.blockchain.server.ltc.service.ITokenService;
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
public class TokenServiceImpl implements ITokenService {

    @Autowired
    TokenMapper tokenMapper;


    @Override
    public Token findByTokenName(String tokenName) {
        ExceptionPreconditionUtils.checkStringNotBlank(tokenName,
                new WalletException(WalletEnums.NULL_TOKENADDR));
        Token where = new Token();
        where.setTokenSymbol(tokenName);
        Token ethToken = null;
        try {
            ethToken = tokenMapper.selectOne(where);
        } catch (Exception e) {
            throw new WalletException(WalletEnums.IEXIST_TOKENADDRS);
        }
        if (ethToken == null) {
            throw new WalletException(WalletEnums.INEXISTENCE_TOKENADDR);
        }
        return ethToken;
    }

    @Override
    public Token findByTokenId(Integer tokenId) {
        Token where = new Token();
        where.setTokenId(tokenId);
        Token ethToken = null;
        try {
            ethToken = tokenMapper.selectOne(where);
        } catch (Exception e) {
            throw new WalletException(WalletEnums.IEXIST_TOKENADDRS);
        }
        if (ethToken == null) {
            throw new WalletException(WalletEnums.INEXISTENCE_TOKENADDR);
        }
        return ethToken;
    }

    @Override
    public List<Token> selectAll() {
        return tokenMapper.selectAll();
    }

}
