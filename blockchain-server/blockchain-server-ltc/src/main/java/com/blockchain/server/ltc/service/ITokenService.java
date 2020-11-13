package com.blockchain.server.ltc.service;

import com.blockchain.server.ltc.entity.Token;

import java.util.List;

/**
 * 以太坊币种表——业务接口
 *
 * @author YH
 * @date 2019年2月16日17:09:19
 */
public interface ITokenService {
    /**
     * 根据币种名称获取以太坊的币种信息
     *
     * @param tokenName 币种名称
     * @return
     */
    Token findByTokenName(String tokenName);

    /**
     * 根据主键获取以太坊的币种信息
     *
     * @param tokenId 主键
     * @return
     */
    Token findByTokenId(Integer tokenId);

    /**
     * 查询所有币种信息
     *
     * @return
     */
    List<Token> selectAll();
}
