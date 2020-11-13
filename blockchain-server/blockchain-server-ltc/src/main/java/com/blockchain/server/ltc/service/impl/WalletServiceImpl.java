package com.blockchain.server.ltc.service.impl;

import com.blockchain.common.base.constant.BaseConstant;
import com.blockchain.common.base.constant.WalletConstant;
import com.blockchain.common.base.dto.ResultDTO;
import com.blockchain.common.base.dto.SessionUserDTO;
import com.blockchain.common.base.dto.user.UserBaseInfoDTO;
import com.blockchain.common.base.dto.wallet.WalletBaseDTO;
import com.blockchain.common.base.dto.wallet.WalletParamsDTO;
import com.blockchain.common.base.exception.RPCException;
import com.blockchain.common.base.util.ExceptionPreconditionUtils;
import com.blockchain.common.base.util.SecurityUtils;
import com.blockchain.server.ltc.common.constants.OutTxConstants;
import com.blockchain.server.ltc.common.constants.TxTypeConstants;
import com.blockchain.server.ltc.common.constants.WalletConstants;
import com.blockchain.server.ltc.common.enums.WalletEnums;
import com.blockchain.server.ltc.common.exception.WalletException;
import com.blockchain.server.ltc.dto.WalletDTO;
import com.blockchain.server.ltc.entity.CollectionTransfer;
import com.blockchain.server.ltc.entity.Wallet;
import com.blockchain.server.ltc.entity.WalletTransfer;
import com.blockchain.server.ltc.feign.UserFeign;
import com.blockchain.server.ltc.mapper.WalletMapper;
import com.blockchain.server.ltc.rpc.CoinUtils;
import com.blockchain.server.ltc.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * 以太坊钱包表——业务接口
 *
 * @author YH
 * @date 2019年2月16日17:25:02
 */
@Service
public class WalletServiceImpl implements IWalletService {

    @Autowired
    UserFeign userFeign;
    @Autowired
    WalletMapper walletMapper;
    @Autowired
    IWalletTransferService walletTransferService;
    @Autowired
    IClearingTotalService clearingTotalService;
    @Autowired
    IConfigWalletParamService configWalletParamService;
    @Autowired
    ICollectionTransferService collectionTransferService;
    @Autowired
    CoinUtils coinUtils;

    @Override
    public List<Wallet> selectByUserId(String userId) {
        ExceptionPreconditionUtils.checkNotNull(userId, new WalletException(WalletEnums.NULL_USEROPENID));
        Wallet where = new Wallet();
        where.setUserOpenId(userId);
        List<Wallet> wallets = walletMapper.select(where);
        if (wallets.size() <= 0) {
            throw new WalletException(WalletEnums.NULL_TOKENADDR);
        }
        return wallets;
    }

    @Override
    public List<WalletBaseDTO> selectBlock(WalletParamsDTO paramsDTO) {
        List<WalletBaseDTO> list = this.select(paramsDTO);
        for (WalletBaseDTO row : list) {
            row.setFreeBalance(null);
            row.setFreezeBalance(null);
            BigDecimal amount = BigDecimal.ZERO;
            try {
                amount = coinUtils.getBalanceByAddr(row.getAddr());
            } catch (Exception e) {
            }
            row.setBalance(amount.toPlainString());
        }
        return list;
    }

    @Override
    public Wallet findByAddrAndCoinName(String addr, String coinName) {
        Wallet wallet = new Wallet();
        wallet.setAddr(addr);
        wallet.setTokenSymbol(coinName);
        Wallet wallet1 = walletMapper.selectOne(wallet);
        if (wallet == null) {
            throw new WalletException(WalletEnums.NULL_TOKENADDR);
        }
        return wallet1;
    }

    @Override
    public WalletDTO findByUserIdAndTokenAddrAndWalletType(String userId, String tokenAddr, String walletType) {
        return walletMapper.selectByUserOpenIdAndTokenAddrAndWalletType(userId, tokenAddr, walletType);
    }

    @Override
    @Transactional
    public void updateFreeBalance(String userId, String tokenAddr, String walletType, BigDecimal amount, Date date) {
        WalletDTO wallet = this.findByUserIdAndTokenAddrAndWalletType(userId, tokenAddr, walletType);
        int row = walletMapper.updateBalanceByUserIdInRowLock(userId, tokenAddr, walletType, amount, amount,
                BigDecimal.ZERO, date);
        if (row == 0) {
            throw new WalletException(WalletEnums.NUMBER_INSUFFICIENT_ERROR);
        }
    }

    @Override
    @Transactional
    public void updateBalance(String userId, String tokenAddr, String walletType, BigDecimal freeBalance, BigDecimal freezeBalance, Date date) {
        WalletDTO wallet = this.findByUserIdAndTokenAddrAndWalletType(userId, tokenAddr, walletType);
        int row = walletMapper.updateBalanceByUserIdInRowLock(userId, tokenAddr, walletType, freeBalance.add(freezeBalance), freeBalance, freezeBalance, date);
        if (row == 0) {
            throw new WalletException(WalletEnums.NUMBER_INSUFFICIENT_ERROR);
        }
    }

    @Override
    public List<WalletBaseDTO> select(WalletParamsDTO paramsDTO) {
        if (paramsDTO != null && paramsDTO.getUserName() != null && !paramsDTO.getUserName().equals("")) {
            UserBaseInfoDTO userBaseInfoDTO = ConditionsUserId(paramsDTO.getUserName());
            //用户存在，添加条件
            if (userBaseInfoDTO != null) {
                paramsDTO.setUserId(userBaseInfoDTO.getUserId());
            } else {
                //用户不存在，返回空
                return null;
            }
        }
        List<WalletBaseDTO> wallets = walletMapper.selectQuery(paramsDTO);
        return fillUserInfos(wallets);
    }

    @Override
    @Transactional
    public void updateBlance(String addr, String coinName, String freeBlance, String freezeBlance, String txType) {
        Date date = new Date();
        // 验证余额数据格式
        BigDecimal _freeBlance = BigDecimal.ZERO;
        BigDecimal _freezeBlance = BigDecimal.ZERO;
        BigDecimal total = BigDecimal.ZERO;
        try {
            _freeBlance = new BigDecimal(freeBlance);
            _freezeBlance = new BigDecimal(freezeBlance);
            total = _freeBlance.add(_freezeBlance);
        } catch (Exception e) {
            throw new WalletException(WalletEnums.DATA_EXCEPTION_ERROR);
        }
        if (total.compareTo(BigDecimal.ZERO) == 0) {
            throw new WalletException(WalletEnums.DATA_EXCEPTION_ERROR);
        }
        // 获取用户钱包
        Wallet wallet = this.findByAddrAndCoinName(addr, coinName);
        this.updateBalance(wallet.getUserOpenId(), wallet.getTokenId().toString(), wallet.getWalletType(), _freeBlance, _freezeBlance, date);

        // 插入加币减币的记录
        String userId = null == SecurityUtils.getUser() ? WalletConstant.BACKEND : SecurityUtils.getUser().getId();
        WalletTransfer tx = new WalletTransfer();
        tx.setId(UUID.randomUUID().toString());
        tx.setStatus(TxTypeConstants.SUCCESS);
        tx.setAmount(total.abs());
        tx.setTokenId(wallet.getTokenId());
        tx.setTokenSymbol(wallet.getTokenSymbol());
        tx.setTransferType(txType);
        tx.setUpdateTime(date);
        tx.setCreateTime(date);
        int insertRow = 0;
        if (total.compareTo(BigDecimal.ZERO) >= 0) {
            tx.setToAddr(wallet.getAddr());
            tx.setFromAddr(userId);
            insertRow = walletTransferService.insertTransfer(tx);
        } else {
            tx.setToAddr(userId);
            tx.setFromAddr(wallet.getAddr());
            insertRow = walletTransferService.insertTransfer(tx);
        }
        if (insertRow == 0) {
            throw new WalletException(WalletEnums.SERVER_IS_TOO_BUSY);
        }
    }

    @Override
    public Map<String, String> totalBalance() {
        Map<String, String> map = new HashMap<>();
        map.put("LTC", "0");
        try {
            Double amount = coinUtils.getBalance(null);
            map.put("LTC", amount.toString());
        } catch (Exception e) {
        }
        return map;
    }

    @Override
    public Map<String, String> getGasWallet() {
        Map<String, String> map = new HashMap<>();
        String addr = configWalletParamService.getGas();
        try {
            BigDecimal amount = coinUtils.getBalanceByAddr(addr);
            map.put("addr", addr);
            map.put("amount", amount.toPlainString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

//    @Override
//    public void rpcTx(String fromAddr, String toAddr) {
//        ExceptionPreconditionUtils.checkStringNotBlank(toAddr, new WalletException(WalletEnums.NULL_ADDR));
//        try {
//            coinUtils.validateAddress(fromAddr);
//            coinUtils.validateAddress(toAddr);
//            // 获取USDT
//            BigDecimal tokenAmout = usdtUtils.getBalance(fromAddr);
//            // 获取手续费账号
//            String gasAddr = configWalletParamService.getGas();
//            // 产生一笔LTC 代币转账
//            String hash = usdtUtils.fundedSendAll(fromAddr, toAddr, UsdtConstans.NET, gasAddr);
//        } catch (WalletException e) {
//            throw e;
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new WalletException(WalletEnums.SERVER_IS_TOO_BUSY);
//        }
//    }

    @Override
    public String getBalance(String addr) {
        BigDecimal balance = BigDecimal.ZERO;
        try {
            balance = coinUtils.getBalanceByAddr(addr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return balance.toPlainString();
    }

    @Override
    public Set<String> getAllWalletAddr() {
        return walletMapper.getAllWalletAddr();
    }

    @Override
    public void goCoinTx(String toAddr, Double amount) {
        Date endDate = new Date();
        try {
            coinUtils.validateAddress(toAddr);
//            double coinAmount = coinUtils.getBalance(null);
            String hash = coinUtils.sendToAddress(toAddr, amount);
            CollectionTransfer tx = new CollectionTransfer();
            SessionUserDTO userDTO = SecurityUtils.getUser();
            tx.setCreateTime(endDate);
            tx.setUpdateTime(endDate);
            tx.setFromAddr("ALL");
            tx.setToAddr(toAddr);
            tx.setTokenAddr(WalletConstants.LTC);
            tx.setUserId(userDTO == null ? "BAM" : userDTO.getId());
            tx.setTokenSymbol(WalletConstants.LTC);
            tx.setHash(hash);
            tx.setId(UUID.randomUUID().toString());
            tx.setStatus(OutTxConstants.PACK);
            tx.setAmount(new BigDecimal(amount.toString()));
            collectionTransferService.insert(tx);
        } catch (WalletException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @Description: 获取用户信息
     * @Param: [list]
     * @return: java.util.List<com.blockchain.common.base.dto.wallet.WalletBaseDTO>
     * @Author: Liu.sd
     * @Date: 2019/3/23
     */
    private List<WalletBaseDTO> fillUserInfos(List<WalletBaseDTO> list) {
        Set<String> userIds = new HashSet<>();
        for (WalletBaseDTO row : list) {
            userIds.add(row.getUserId());
        }
        //  获取用户信息
        ResultDTO<Map<String, UserBaseInfoDTO>> result = userFeign.userInfos(userIds);
        if (result == null) throw new WalletException(WalletEnums.SERVER_IS_TOO_BUSY);
        if (result.getCode() != BaseConstant.REQUEST_SUCCESS) throw new RPCException(result);
        Map<String, UserBaseInfoDTO> userMap = result.getData();
        for (WalletBaseDTO row : list) {
            String userId = row.getUserId();
            if (userMap.containsKey(userId)) row.setUserBaseInfoDTO(userMap.get(userId));
        }
        return list;
    }

    /**
     * @Description: 根据手机号获取用户信息
     * @Param: [mobilePhone]
     * @return: com.blockchain.common.base.dto.user.UserBaseInfoDTO
     * @Author: Liu.sd
     * @Date: 2019/3/23
     */
    private UserBaseInfoDTO ConditionsUserId(String userName) {
        //  获取用户信息
        ResultDTO<UserBaseInfoDTO> result = userFeign.selectUserInfoByUserName(userName);
        if (result == null) throw new WalletException(WalletEnums.SERVER_IS_TOO_BUSY);
        if (result.getCode() != BaseConstant.REQUEST_SUCCESS) throw new RPCException(result);
        return result.getData();
    }


}
