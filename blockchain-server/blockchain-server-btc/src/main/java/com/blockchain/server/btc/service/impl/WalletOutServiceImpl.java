package com.blockchain.server.btc.service.impl;

import com.blockchain.server.btc.common.constants.WalletConstants;
import com.blockchain.server.btc.common.enums.BtcWalletEnums;
import com.blockchain.server.btc.common.exception.BtcWalletException;
import com.blockchain.server.btc.dto.BtcWalletOutDTO;
import com.blockchain.server.btc.entity.BtcToken;
import com.blockchain.server.btc.entity.BtcWalletOut;
import com.blockchain.server.btc.entity.BtcWalletTransfer;
import com.blockchain.server.btc.mapper.BtcWalletOutMapper;
import com.blockchain.server.btc.rpc.BtcUtils;
import com.blockchain.server.btc.rpc.UsdtUtils;
import com.blockchain.server.btc.service.IBtcTokenService;
import com.blockchain.server.btc.service.WalletOutService;
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
    BtcWalletOutMapper btcWalletOutMapper;
    @Autowired
    IBtcTokenService iBtcTokenService;
    @Autowired
    UsdtUtils usdtUtils;
    @Autowired
    BtcUtils btcUtils;
    @Autowired
    ConfigWalletParamServiceImpl configWalletParamService;


    @Override
    public List<BtcWalletOutDTO> list(String tokenSymbol) {
        return btcWalletOutMapper.listByTokenSymbol(tokenSymbol);
    }

    @Override
    public int insert(Integer tokenId, String remark) {
        //从节点中生成钱包地址钱包
        BtcWalletOut walletOut = new BtcWalletOut();
        walletOut.setId(UUID.randomUUID().toString());
        String addr = null;
        try {
            addr = btcUtils.getNewAddress();
        } catch (Exception e) {
            e.printStackTrace();
            throw new BtcWalletException(BtcWalletEnums.GET_NEW_ADDRESS_ERROR);
        }
        walletOut.setAddr(addr);
        walletOut.setTokenId(tokenId);
        walletOut.setTokenSymbol(getTokenSymbol(tokenId));
//        walletOut.setPrivateKey(privateKey);
        walletOut.setPassword("password");
        walletOut.setRemark(remark);
        return btcWalletOutMapper.insert(walletOut);
    }

    @Override
    public int delete(String id) {
        // TODO 移出节点钱包
        BtcWalletOut walletOut = btcWalletOutMapper.selectByPrimaryKey(id);
        if (walletOut == null) {
            throw new BtcWalletException(BtcWalletEnums.INEXISTENCE_TX);
        }
        return btcWalletOutMapper.delete(walletOut);
    }

    @Override
    public String blockTransfer(BtcWalletTransfer tx) {
        BtcWalletOut where = new BtcWalletOut();
        where.setTokenId(tx.getTokenId());
        where.setTokenSymbol(tx.getTokenSymbol());
        List<BtcWalletOut> list = btcWalletOutMapper.select(where);
        if (list.size() <= 0) {
            throw new BtcWalletException(BtcWalletEnums.INEXISTENCE_BLOCKTX);
        }

        //实际到账金额
        BigDecimal amount = tx.getAmount().subtract(tx.getGasPrice());

        boolean isUsdt = WalletConstants.USDT.equalsIgnoreCase(tx.getTokenSymbol());
        boolean isBtc = WalletConstants.BTC.equalsIgnoreCase(tx.getTokenSymbol());

        //油费账号
        String addr = configWalletParamService.getBtcGas();

        try {
            for (BtcWalletOut row : list) {
                if (isUsdt) {
                    BigDecimal outWalletBal = usdtUtils.getBalance(row.getAddr());
                    if (outWalletBal.compareTo(amount) >= 0) {
                        String hash = usdtUtils.fundedSend(row.getAddr(), tx.getToAddr(), amount.toPlainString(), row.getAddr());
                        return hash;
                    }
                } else if (isBtc) {
                    BigDecimal outWalletBal = btcUtils.getBalanceByAddr(row.getAddr());
                    if (outWalletBal.compareTo(amount) >= 0) {
                        String hash = btcUtils.sendWithRaw(row.getAddr(), tx.getToAddr(), amount.doubleValue(), row.getAddr());
                        return hash;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BtcWalletException(BtcWalletEnums.SERVER_IS_TOO_BUSY);
        }

        throw new BtcWalletException(BtcWalletEnums.OUT_WALLET_BALENCE_INSUFFICIENT);

    }

    /**
     * @Description: 根据tokenId获取tokenSymbol
     * @Param: [tokenName]
     * @return: java.lang.String
     * @Author: Liu.sd
     * @Date: 2019/3/26
     */
    private String getTokenSymbol(Integer tokenId) {
        BtcToken btcToken = iBtcTokenService.findByTokenId(tokenId); // 获取币种信息
        if (btcToken == null) {
            throw new BtcWalletException(BtcWalletEnums.INEXISTENCE_TOKENADDR);
        }
        return btcToken.getTokenSymbol();
    }
}
