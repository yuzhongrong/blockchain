package com.blockchain.server.ltc.service.impl;

import com.blockchain.server.ltc.common.enums.WalletEnums;
import com.blockchain.server.ltc.common.exception.WalletException;
import com.blockchain.server.ltc.dto.WalletOutDTO;
import com.blockchain.server.ltc.entity.Token;
import com.blockchain.server.ltc.entity.WalletOut;
import com.blockchain.server.ltc.entity.WalletTransfer;
import com.blockchain.server.ltc.mapper.WalletOutMapper;
import com.blockchain.server.ltc.rpc.CoinUtils;
import com.blockchain.server.ltc.service.ITokenService;
import com.blockchain.server.ltc.service.WalletOutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * @author: Liusd
 * @create: 2019-03-27 10:13
 **/
@Service
public class WalletOutServiceImpl implements WalletOutService {

    @Autowired
    WalletOutMapper walletOutMapper;
    @Autowired
    ITokenService iTokenService;
    @Autowired
    CoinUtils coinUtils;


    @Override
    public List<WalletOutDTO> list(String tokenSymbol) {

        return walletOutMapper.listByTokenSymbol(tokenSymbol);
    }

    @Override
    public int insert(String addr, Integer tokenId, String privateKey, String remark) {
        // TODO 导入节点钱包
        WalletOut walletOut = new WalletOut();
        walletOut.setId(UUID.randomUUID().toString());
        walletOut.setAddr(addr);
        walletOut.setTokenId(tokenId);
        walletOut.setTokenSymbol(getTokenSymbol(tokenId));
        walletOut.setPrivateKey(privateKey);
        walletOut.setPassword("password");
        walletOut.setRemark(remark);
        return walletOutMapper.insert(walletOut);
    }

    @Override
    public int delete(String id) {
        // TODO 移出节点钱包
        WalletOut walletOut = walletOutMapper.selectByPrimaryKey(id);
        if (walletOut == null) {
            throw new WalletException(WalletEnums.INEXISTENCE_TX);
        }
        return walletOutMapper.delete(walletOut);
    }

    @Override
    public String blockTransfer(WalletTransfer tx) {
//        WalletOut where = new WalletOut();
//        where.setTokenId(tx.getTokenId());
//        where.setTokenSymbol(tx.getTokenSymbol());
//
//        List<WalletOut> list = walletOutMapper.select(where);
//        if (list.size() <= 0) {
//            throw new WalletException(WalletEnums.INEXISTENCE_BLOCKTX);
//        }
//
//        //实际到账金额
//        BigDecimal amount = tx.getAmount().subtract(tx.getGasPrice());
//
//        for (WalletOut row : list) {
//            try {
//                String hash = coinUtils.sendWithRaw(row.getAddr(), tx.getToAddr(), amount.doubleValue(), row.getAddr());
//                if (null != hash) {
//                    return hash;
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }

        //实际到账金额
        BigDecimal amount = tx.getAmount().subtract(tx.getGasPrice());
        try {
            String hash = coinUtils.sendToAddress(tx.getToAddr(), amount.doubleValue());
            if (null != hash) {
                return hash;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        throw new WalletException(WalletEnums.SERVER_IS_TOO_BUSY);
    }

    /**
     * @Description: 根据tokenId获取tokenSymbol
     * @Param: [tokenName]
     * @return: java.lang.String
     * @Author: Liu.sd
     * @Date: 2019/3/26
     */
    private String getTokenSymbol(Integer tokenId) {
        Token token = iTokenService.findByTokenId(tokenId); // 获取币种信息
        if (token == null) {
            throw new WalletException(WalletEnums.INEXISTENCE_TOKENADDR);
        }
        return token.getTokenSymbol();
    }
}
