package com.blockchain.server.tron.service.impl;

import com.blockchain.common.base.util.RSACoderUtils;
import com.blockchain.server.tron.common.constants.TronConstant;
import com.blockchain.server.tron.common.constants.wallet.WalletTypeConstants;
import com.blockchain.server.tron.common.enums.TronWalletEnums;
import com.blockchain.server.tron.common.exception.TronWalletException;
import com.blockchain.server.tron.dto.wallet.TronWalletOutDto;
import com.blockchain.server.tron.entity.TronToken;
import com.blockchain.server.tron.entity.TronWalletOut;
import com.blockchain.server.tron.entity.TronWalletTransfer;
import com.blockchain.server.tron.mapper.TronWalletOutMapper;
import com.blockchain.server.tron.service.ITronTokenService;
import com.blockchain.server.tron.service.ITronWalletKeyService;
import com.blockchain.server.tron.service.TronWalletOutService;
import com.blockchain.server.tron.service.TronWalletUtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.UUID;

/**
 * @author: Liusd
 * @create: 2019-03-27 09:27
 **/
@Service
public class WalletOutServiceImpl implements TronWalletOutService {

    @Autowired
    TronWalletOutMapper tronWalletOutMapper;
    @Autowired
    ITronTokenService tronTokenService;
    @Autowired
    private TronWalletUtilService tronWalletUtilService;
    @Autowired
    private ITronWalletKeyService tronWalletKeyService;

    @Override
    public List<TronWalletOutDto> list(String coinName, String status) {
        return tronWalletOutMapper.listByTokenSymbol(coinName, status);
    }

    @Override
    public int insert(String addr, String tokenAddr, String privateKey, String remark) {
        TronWalletOut walletOut = new TronWalletOut();
        walletOut.setId(UUID.randomUUID().toString());
        walletOut.setAddr(addr);
        String hexAddr = tronWalletUtilService.tryToHexAddr(addr);

        walletOut.setHexAddr(hexAddr);
        walletOut.setTokenAddr(tokenAddr);
        TronToken tronToken = tronTokenService.findByTokenAddr(tokenAddr); // 获取币种信息
        if (tronToken == null) {
            throw new TronWalletException(TronWalletEnums.INEXISTENCE_WALLETTYPE);
        }
        walletOut.setTokenSymbol(tronToken.getTokenSymbol());
        walletOut.setTokenDecimals(tronToken.getTokenDecimal());
        walletOut.setPrivateKey(RSACoderUtils.encryptPassword(privateKey));
        walletOut.setPassword("password");
        walletOut.setStatus(TronConstant.WalletCreateStatus.USEABLE);
        walletOut.setRemark(remark);
        return tronWalletOutMapper.insert(walletOut);
    }

    @Override
    public int delete(String id) {
        TronWalletOut walletOut = tronWalletOutMapper.selectByPrimaryKey(id);
        if (walletOut == null) {
            throw new TronWalletException(TronWalletEnums.INEXISTENCE_TX);
        }
        return tronWalletOutMapper.delete(walletOut);
    }

    @Override
    public String blockTransfer(TronWalletTransfer tx) {
        TronWalletOut where = new TronWalletOut();
        where.setTokenSymbol(tx.getTokenSymbol());
        where.setTokenAddr(tx.getTokenAddr());
        List<TronWalletOut> list = tronWalletOutMapper.select(where);
        if (list.size() <= 0) {
            throw new TronWalletException(TronWalletEnums.INEXISTENCE_BLOCKTX);
        }
        TronWalletOut tronWalletOut = list.get(0);
        TronToken tronToken = tronTokenService.findByTokenName(tx.getTokenSymbol()); // 获取币种信息
        String privateKey = RSACoderUtils.decryptPassword(tronWalletOut.getPrivateKey());
        String hash = null;
        BigInteger balance = tx.getAmount().subtract(tx.getGasPrice()).multiply(BigDecimal.TEN.pow(tronToken.getTokenDecimal())).toBigInteger();
        // 判断币种 ==> 对应转账方式
        if (WalletTypeConstants.TRON_TRX.equalsIgnoreCase(tx.getTokenSymbol())) {
            hash = tronWalletUtilService.handleTransactionTRX(tronWalletOut.getHexAddr(), privateKey, tx.getToHexAddr(), balance);
        } else if (WalletTypeConstants.TRON_BTT.equalsIgnoreCase(tx.getTokenSymbol()) || WalletTypeConstants.TRON_TBC.equalsIgnoreCase(tx.getTokenSymbol())) {
            hash = tronWalletUtilService.handleTransactionBTT(tronWalletOut.getHexAddr(), privateKey, tx.getToHexAddr(), balance, tronToken.getTokenHexAddr());
        } else if (WalletTypeConstants.TRON_ACE.equalsIgnoreCase(tx.getTokenSymbol())) {
            hash = tronWalletUtilService.handleTransactionACE(tronWalletOut.getHexAddr(), privateKey, tx.getToHexAddr(), balance, tronToken.getTokenHexAddr());
        } else throw new TronWalletException(TronWalletEnums.INEXISTENCE_TOKENADDR);
        if (hash != null) return hash;
        throw new TronWalletException(TronWalletEnums.SERVER_IS_TOO_BUSY);
    }

}
