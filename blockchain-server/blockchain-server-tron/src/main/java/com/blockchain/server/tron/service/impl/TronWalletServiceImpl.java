package com.blockchain.server.tron.service.impl;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.blockchain.common.base.constant.BaseConstant;
import com.blockchain.common.base.constant.WalletConstant;
import com.blockchain.common.base.dto.ResultDTO;
import com.blockchain.common.base.dto.SessionUserDTO;
import com.blockchain.common.base.dto.WalletChangeDTO;
import com.blockchain.common.base.dto.WalletOrderDTO;
import com.blockchain.common.base.dto.user.UserBaseInfoDTO;
import com.blockchain.common.base.dto.wallet.WalletBaseDTO;
import com.blockchain.common.base.dto.wallet.WalletParamsDTO;
import com.blockchain.common.base.exception.RPCException;
import com.blockchain.common.base.util.ExceptionPreconditionUtils;
import com.blockchain.common.base.util.SecurityUtils;
import com.blockchain.server.tron.common.constants.TronConstant;
import com.blockchain.server.tron.common.constants.tx.OutTxConstants;
import com.blockchain.server.tron.common.constants.tx.TxTypeConstants;
import com.blockchain.server.tron.common.constants.wallet.WalletTypeConstants;
import com.blockchain.server.tron.common.enums.TronWalletEnums;
import com.blockchain.server.tron.common.exception.TronWalletException;
import com.blockchain.server.tron.dto.wallet.TronWalletDTO;
import com.blockchain.server.tron.entity.*;
import com.blockchain.server.tron.feign.UserFeign;
import com.blockchain.server.tron.mapper.TronWalletMapper;
import com.blockchain.server.tron.service.*;
import com.codingapi.tx.annotation.ITxTransaction;
import com.codingapi.tx.annotation.TxTransaction;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * 以太坊钱包表——业务接口
 *
 * @author YH
 * @date 2019年2月16日17:25:02
 */
@Service
public class TronWalletServiceImpl implements ITronWalletService, ITxTransaction {
    @Autowired
    UserFeign userFeign;
    @Autowired
    TronWalletMapper tronWalletMapper;
    @Autowired
    ITronTokenService tronTokenService;
    @Autowired
    ITronWalletTransferService tronWalletTransferService;
    @Autowired
    ITronClearingTotalService tronClearingTotalService;
    @Autowired
    ITronWalletKeyService tronWalletKeyService;
    @Autowired
    ITronCollectionTransferService tronCollectionTransferService;
    @Autowired
    private TronWalletUtilService tronWalletUtilService;
    @Autowired
    private TronApplicationService tronApplicationService;


    public static final String DEFAULT = "";
    public static final String TRON = "TRON";
    private static final BigInteger DEFAULT_GAS = new BigInteger("210000000000000");


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
        List<WalletBaseDTO> wallets = tronWalletMapper.selectQuery(paramsDTO);
        return fillUserInfos(wallets);
    }

    @Override
    public List<WalletBaseDTO> selectBlock(WalletParamsDTO paramsDTO) {
        List<WalletBaseDTO> list = this.select(paramsDTO);
        // boolean isTron = WalletTypeConstants.TRON.equalsIgnoreCase(paramsDTO.getCoinName());
        for (WalletBaseDTO row : list) {
            row.setFreezeBalance(null);
            row.setFreeBalance(null);
            row.setBalance(this.getBalance(row.getHexAddr(), row.getCoinType()));
        }
        return list;
    }

    /**
     * 获取用户链上余额
     *
     * @param addr     钱包地址
     * @param coinAddr 合约地址
     * @return
     */
    private String getBalance(String addr, String coinAddr) {
        TronToken tronToken = tronTokenService.findByTokenAddr(coinAddr);
        return getBalance(addr, tronToken);
    }

    private String getBalance(String addr, TronToken tronToken) {
        BigDecimal amount = null;
        // 获取用户链上余额
        if (!WalletTypeConstants.TRON_ACE.equalsIgnoreCase(tronToken.getTokenSymbol())) {
            String account = tronWalletUtilService.getAccount(addr);
            if (account == null) throw new TronWalletException(TronWalletEnums.SERVER_IS_TOO_BUSY);
            JSONObject body = JSONObject.parseObject(account);
            if (tronToken.getTokenSymbol().equalsIgnoreCase(TronConstant.TRX_TOKEN_SYMBOL)) {
                amount = body.getBigDecimal("balance");
            } else if (tronToken.getTokenSymbol().equalsIgnoreCase(TronConstant.BTT_TOKEN_SYMBOL) || tronToken.getTokenSymbol().equalsIgnoreCase(TronConstant.TBC_TOKEN_SYMBOL)) {
                JSONArray assetV2 = body.getJSONArray("assetV2");
                if (assetV2 == null) amount = BigDecimal.ZERO;
                else
                    for (int i = 0; i < assetV2.size(); i++) {
                        if (tronToken.getTokenAddr().equalsIgnoreCase(assetV2.getJSONObject(i).getString("key"))) {
                            amount = assetV2.getJSONObject(i).getBigDecimal("value");
                        }
                    }
            }
            if (amount != null) amount = amount.divide(BigDecimal.TEN.pow(tronToken.getTokenDecimal()));
        } else if (WalletTypeConstants.TRON_ACE.equalsIgnoreCase(tronToken.getTokenSymbol())) {
            // 获取TRC20代币链上余额
            String hexStrAmount = tronWalletUtilService.getAccountTRC20Balance(addr, tronToken.getTokenHexAddr());
            if (hexStrAmount != null && !"".equals(hexStrAmount)) {
                String hexAmount = hexStrAmount.replaceAll("^(0+)", "");
                if (!"".equals(hexAmount))
                    amount = new BigDecimal(Long.parseLong(hexAmount, 16)).divide(BigDecimal.TEN.pow(tronToken.getTokenDecimal()));
                else amount = BigDecimal.ZERO;
            }
        }
        if (amount == null) amount = BigDecimal.ZERO;
        return amount.toString();
    }

    @Override
    public List<TronWallet> selectByUserId(String userId) {
        ExceptionPreconditionUtils.checkNotNull(userId, new TronWalletException(TronWalletEnums.NULL_USEROPENID));
        TronWallet where = new TronWallet();
        where.setUserOpenId(userId);
        List<TronWallet> wallets = tronWalletMapper.select(where);
        if (wallets.size() <= 0) {
            throw new TronWalletException(TronWalletEnums.NULL_TOKENADDR);
        }
        return wallets;
    }

    @Override
    public TronWallet findByAddrAndCoinName(String hexAddr, String coinName) {
        TronWallet where = new TronWallet();
        where.setHexAddr(hexAddr);
        where.setTokenSymbol(coinName);
        TronWallet tronWallet = tronWalletMapper.selectOne(where);
        if (null == tronWallet) {
            throw new TronWalletException(TronWalletEnums.INEXISTENCE_WALLET);
        }
        return tronWallet;
    }

    @Override
    @Transactional
    public void updateFreeBalance(String userId, String tokenAddr, String walletType, BigDecimal amount, Date date) {
        TronWalletDTO wallet = this.findByUserIdAndTokenAddrAndWalletType(userId, tokenAddr, walletType);
        int row = tronWalletMapper.updateBalanceByUserIdInRowLock(userId, tokenAddr, walletType, amount, amount,
                BigDecimal.ZERO, date);
        if (row == 0) {
            throw new TronWalletException(TronWalletEnums.NUMBER_INSUFFICIENT_ERROR);
        }
    }

    @Override
    @TxTransaction
    @Transactional
    public void updateBlanceTransform(WalletOrderDTO orderDTO) {
        ExceptionPreconditionUtils.checkNotNull(orderDTO, new TronWalletException(TronWalletEnums.NULL_ERROR));
        TronToken tronToken = tronTokenService.findByTokenName(orderDTO.getTokenName());
        ExceptionPreconditionUtils.checkNotNull(orderDTO.getFreeBalance(),
                new TronWalletException(TronWalletEnums.NULL_FREEBLANCE));
        ExceptionPreconditionUtils.checkNotNull(orderDTO.getFreezeBalance(),
                new TronWalletException(TronWalletEnums.NULL_FREEZEBLANCE));
        if (orderDTO.getFreeBalance().compareTo(orderDTO.getFreezeBalance().negate()) != 0) {
            throw new TronWalletException(TronWalletEnums.DATA_EXCEPTION_ERROR);
        }
        this.updateBalanceByUserOpenId(orderDTO.getUserId(), tronToken.getTokenAddr(), orderDTO.getWalletType(),
                orderDTO.getFreeBalance(), orderDTO.getFreezeBalance(), new Date());
    }

    @Override
    @TxTransaction
    @Transactional
    public void updateBlance(WalletChangeDTO changeDTO) {
        ExceptionPreconditionUtils.checkNotNull(changeDTO, new TronWalletException(TronWalletEnums.NULL_ERROR));
        TronToken tronToken = tronTokenService.findByTokenName(changeDTO.getTokenName());
        Date date = new Date();
        TronWalletDTO tronWalletDTO = this.updateBalanceByUserOpenId(changeDTO.getUserId(), tronToken.getTokenAddr(),
                changeDTO.getWalletType(), changeDTO.getFreeBalance(), changeDTO.getFreezeBalance(), date);
        BigDecimal count = changeDTO.getFreeBalance().add(changeDTO.getFreezeBalance());
        if (count.compareTo(BigDecimal.ZERO) >= 0) {
            tronWalletTransferService.insert(changeDTO.getRecordId(), DEFAULT, tronWalletDTO.getAddr(), count.abs(),
                    tronToken, TxTypeConstants.CCT, date);
        } else {
            tronWalletTransferService.insert(changeDTO.getRecordId(), tronWalletDTO.getAddr(), DEFAULT, count.abs(),
                    tronToken, TxTypeConstants.CCT, date);
        }
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
            throw new TronWalletException(TronWalletEnums.DATA_EXCEPTION_ERROR);
        }
        if (total.compareTo(BigDecimal.ZERO) == 0) {
            throw new TronWalletException(TronWalletEnums.DATA_EXCEPTION_ERROR);
        }
        // 获取用户钱包
        TronWallet wallet = this.findByAddrAndCoinName(tronWalletUtilService.tryToHexAddr(addr), coinName);
        TronToken token = tronTokenService.findByTokenName(coinName);
        this.updateBalanceByUserOpenId(wallet.getUserOpenId(), wallet.getTokenAddr(), wallet.getWalletType(),
                _freeBlance, _freezeBlance, date);
        // 插入加币减币的记录
        String userId = null == SecurityUtils.getUser() ? WalletConstant.BACKEND : SecurityUtils.getUser().getId();
        if (total.compareTo(BigDecimal.ZERO) >= 0) {
            tronWalletTransferService.insert(UUID.randomUUID().toString(), userId, wallet.getAddr(), total.abs(), token, txType, date);
        } else {
            tronWalletTransferService.insert(UUID.randomUUID().toString(), wallet.getAddr(), userId, total.abs(), token, txType, date);
        }
    }

    @Override
    @Transactional
    public void updateBlance(String hexAddr, String coinName, BigDecimal freeBlance, BigDecimal freezeBlance, Date date) {
        // 获取用户钱包
        TronWallet wallet = this.findByAddrAndCoinName(hexAddr, coinName);
        TronToken token = tronTokenService.findByTokenName(coinName);
        this.updateBalanceByUserOpenId(wallet.getUserOpenId(), wallet.getTokenAddr(), wallet.getWalletType(),
                freeBlance, freezeBlance, date);
    }

    @Override
    @Transactional
    public void collection(String fromAddr, String toAddr, String tokenName) {
        ExceptionPreconditionUtils.checkStringNotBlank(toAddr, new TronWalletException(TronWalletEnums.NULL_ADDR));
        Date endDate = new Date();
        // 验证地址是否存在
        TronWalletKey walletKey = tronWalletKeyService.findByAddr(fromAddr);
        // 请求代币信息
        TronToken tronToken = tronTokenService.findByTokenName(tokenName);
        // 请求钱包TRON,查询链上余额
        BigInteger balance = new BigDecimal(this.getBalance(walletKey.getHexAddr(), tronToken)).multiply(BigDecimal.TEN.pow(tronToken.getTokenDecimal())).toBigInteger();
        String toHexAddr = tronWalletUtilService.tryToHexAddr(toAddr);
        if (toHexAddr == null) throw new TronWalletException(TronWalletEnums.ACCOUNT_ERROR);

        // 判断币种 ==> 对应转账方式
        String hash = null;
        if (WalletTypeConstants.TRON_TRX.equalsIgnoreCase(tronToken.getTokenSymbol())) {
            hash = tronWalletUtilService.handleTransactionTRX(walletKey.getHexAddr(), walletKey.getPrivateKey(), toHexAddr, balance);
        } else if (WalletTypeConstants.TRON_BTT.equalsIgnoreCase(tronToken.getTokenSymbol()) || WalletTypeConstants.TRON_TBC.equalsIgnoreCase(tronToken.getTokenSymbol())) {
            hash = tronWalletUtilService.handleTransactionBTT(walletKey.getHexAddr(), walletKey.getPrivateKey(), toHexAddr, balance, tronToken.getTokenHexAddr());
        } else if (WalletTypeConstants.TRON_ACE.equalsIgnoreCase(tronToken.getTokenSymbol())) {
            hash = tronWalletUtilService.handleTransactionACE(walletKey.getHexAddr(), walletKey.getPrivateKey(), toHexAddr, balance, tronToken.getTokenHexAddr());
        } else throw new TronWalletException(TronWalletEnums.INEXISTENCE_TOKENADDR);
        TronCollectionTransfer tx = new TronCollectionTransfer();

        if (hash == null) {
            throw new TronWalletException(TronWalletEnums.SERVER_IS_TOO_BUSY);
        } else {
            SessionUserDTO userDTO = SecurityUtils.getUser();
            tx.setCreateTime(endDate);
            tx.setUpdateTime(endDate);
            tx.setFromAddr(fromAddr);
            tx.setToAddr(toAddr);
            tx.setAmount(new BigDecimal(balance));
            tx.setTokenAddr(tronToken.getTokenAddr());
            tx.setUserId(userDTO == null ? "BAM" : userDTO.getId());
            tx.setTokenSymbol(tronToken.getTokenSymbol());
            tx.setHash(hash);
            tx.setId(UUID.randomUUID().toString());
            tx.setStatus(OutTxConstants.PACK);
            tronCollectionTransferService.insert(tx);
        }
    }

    /**
     * 冻结与解冻方法
     *
     * @param walletOrderDTO
     * @return
     */
    @Override
    @Transactional
    public Integer updateWalletOrder(WalletOrderDTO walletOrderDTO) {
        ExceptionPreconditionUtils.notEmpty(walletOrderDTO);
        BigDecimal freeBalance = walletOrderDTO.getFreeBalance();
        BigDecimal freezeBalance = walletOrderDTO.getFreezeBalance();
        ExceptionPreconditionUtils.notEmpty(freeBalance);
        ExceptionPreconditionUtils.notEmpty(freezeBalance);
        // 判断传过来的可用余额和冻结余额是否有误
        if (freeBalance.add(freezeBalance).compareTo(BigDecimal.ZERO) != 0)
            throw new TronWalletException(TronWalletEnums.PARAM_ERROR);
        // 判断是否存在该钱包
        TronWallet tronWallet = this.selectWalletByTokenSymbol(walletOrderDTO.getUserId(), walletOrderDTO.getTokenName(), walletOrderDTO.getWalletType());
        // 对比可用余额和冻结余额
        if (freeBalance.add(tronWallet.getFreeBalance()).compareTo(BigDecimal.ZERO) < 0)
            throw new TronWalletException(TronWalletEnums.BALANCE_AMOUNT_ERROR);
        if (freezeBalance.add(tronWallet.getFreezeBalance()).compareTo(BigDecimal.ZERO) < 0)
            throw new TronWalletException(TronWalletEnums.BALANCE_AMOUNT_ERROR);
        return tronWalletMapper.updateWalletAllBalanceInRowLock(BigDecimal.ZERO, freeBalance, freezeBalance, walletOrderDTO.getUserId(), tronWallet.getTokenAddr(), walletOrderDTO.getWalletType(), new Date());
    }

    /**
     * 钱包余额变动方法
     *
     * @param walletChangeDTO
     * @return
     */
    @Override
    @Transactional
    public Integer handleWalletChange(WalletChangeDTO walletChangeDTO) {
        // 判断参数是否为空
        ExceptionPreconditionUtils.notEmpty(walletChangeDTO);
        BigDecimal freeBalance = walletChangeDTO.getFreeBalance();
        BigDecimal freezeBalance = walletChangeDTO.getFreezeBalance();
        BigDecimal gasBalance = walletChangeDTO.getGasBalance();
        ExceptionPreconditionUtils.notEmpty(freeBalance);
        ExceptionPreconditionUtils.notEmpty(freezeBalance);
        ExceptionPreconditionUtils.notEmpty(gasBalance);
        // 判断是否存在该钱包
        TronWallet tronWallet = this.selectWalletByTokenSymbol(walletChangeDTO.getUserId(), walletChangeDTO.getTokenName(), walletChangeDTO.getWalletType());
        BigDecimal dtoBalance = tronWallet.getBalance();
        BigDecimal dtoFreeBalance = tronWallet.getFreeBalance();
        BigDecimal dtoFreezeBalance = tronWallet.getFreezeBalance();
        // 判断余额是否充足
        if (dtoFreezeBalance.add(freezeBalance).compareTo(BigDecimal.ZERO) < 0)
            throw new TronWalletException(TronWalletEnums.BALANCE_AMOUNT_ERROR);
        if (dtoFreeBalance.add(freeBalance).compareTo(BigDecimal.ZERO) < 0)
            throw new TronWalletException(TronWalletEnums.BALANCE_AMOUNT_ERROR);
//        if (dtoBalance.compareTo(dtoFreeBalance.add(dtoFreezeBalance).add(freeBalance.add(freezeBalance))) < 0)
//            throw new TronWalletException(TronWalletEnums.BALANCE_AMOUNT_ERROR);
        //记录总额改动
        BigDecimal balance = BigDecimal.ZERO;
        Date now = new Date();
        // 添加记录
        TronWalletTransfer tronWalletTransfer = new TronWalletTransfer();
        tronWalletTransfer.setId(UUID.randomUUID().toString());
        tronWalletTransfer.setHash(walletChangeDTO.getRecordId());
        balance = freeBalance.add(freezeBalance);
        // 可用余额增加
        if (balance.compareTo(BigDecimal.ZERO) > 0) tronWalletTransfer.setToHexAddr(tronWallet.getHexAddr());
        else tronWalletTransfer.setFromHexAddr(tronWallet.getHexAddr());
        tronWalletTransfer.setAmount(balance);
        tronWalletTransfer.setTokenAddr(tronWallet.getTokenAddr());
        tronWalletTransfer.setTokenSymbol(tronWallet.getTokenSymbol());
        tronWalletTransfer.setGasPrice(gasBalance.abs());
        tronWalletTransfer.setGasTokenName(tronWallet.getTokenSymbol());
        tronWalletTransfer.setGasTokenSymbol(tronWallet.getTokenSymbol());
        tronWalletTransfer.setGasTokenType(tronWallet.getTokenAddr());
            tronWalletTransfer.setTransferType(walletChangeDTO.getWalletType());
        tronWalletTransfer.setStatus(TronConstant.TransferStatus.OUT_SUCCESS);
        tronWalletTransfer.setCreateTime(now);
        tronWalletTransfer.setUpdateTime(now);
        int insert = tronWalletTransferService.insertWalletTransfer(tronWalletTransfer);
        if (insert != 1) throw new TronWalletException(TronWalletEnums.GET_PUBLICKEY_ERROR);
        return tronWalletMapper.updateWalletAllBalanceInRowLock(balance, freeBalance, freezeBalance, walletChangeDTO.getUserId(), tronWallet.getTokenAddr(), walletChangeDTO.getWalletType(), now);
    }

    @Override
    public Map<String, BigDecimal> getGas(String fromAddr, String toAddr, String tokenName) {
        // 验证地址是否存在
        TronWalletKey walletKey = tronWalletKeyService.findByAddr(fromAddr);
        // 请求代币信息
        TronToken tronToken = tronTokenService.findByTokenName(tokenName);
        // 请求钱包TRON,代币余额与手续费估算
        /*BigInteger tronAmount = walletWeb3j.getTronBalance(fromAddr);
        BigInteger tokenAmount = BigInteger.ZERO;
        BigInteger gasAmount = BigInteger.ZERO;
        boolean isTron = WalletTypeConstants.TRON.equalsIgnoreCase(tronToken.getTokenSymbol());
        if (!isTron) {
            tokenAmount = walletWeb3j.getTokenBalance(fromAddr, tronToken.getTokenAddr());
            gasAmount = walletWeb3j.estimateGas(fromAddr, toAddr, tronToken.getTokenAddr(), tokenAmount);
        } else {
            gasAmount = walletWeb3j.estimateGas(fromAddr, toAddr, tronToken.getTokenAddr(), tronAmount);
        }
        Map<String, BigDecimal> map = new HashMap<>();
        map.put("gas", DataCheckUtil.bitToBigDecimal(gasAmount));
        map.put("tron", DataCheckUtil.bitToBigDecimal(tronAmount));
        return map;*/
        return null;
    }
    /**
     * 根据符号查询钱包
     *
     * @param userId
     * @param tokenSymbol
     * @param walletType
     * @return
     */
    @Override
    public TronWallet selectWalletByTokenSymbol(String userId, String tokenSymbol, String walletType) {
        ExceptionPreconditionUtils.notEmpty(walletType);
        Integer count = tronApplicationService.selectWalletCountByWalletType(walletType);
        if (count < 1) throw new TronWalletException(TronWalletEnums.TRONWALLET_GETWALLET_ERROR);
        TronWallet tronWallet = tronWalletMapper.selectWalletByTokenSymbol(userId, tokenSymbol, walletType);
        if (tronWallet == null) throw new TronWalletException(TronWalletEnums.TRONWALLET_GETWALLET_ERROR);
        return tronWallet;
    }

    /*@Override
    public void addGas(String userId, String walletType, String coinName, String amount) {
        // 用户信息查询
        TronToken token = tronTokenService.findByTokenName(coinName);
        TronWalletDTO walletDTO = this.findByUserIdAndTokenAddrAndWalletType(userId, token.getTokenAddr(), walletType);
        if (walletDTO == null) {
            throw new TronWalletException(TronWalletEnums.NULL_WALLETS);
        }
        List<TronGasWallet> list = tronGasWalletService.select();
        if (list.size() <= 0) {
            throw new TronWalletException(TronWalletEnums.NULL_GASWALLETNULL);
        }
        BigInteger tokenBalance = walletWeb3j.getTokenBalance(walletDTO.getAddr(), walletDTO.getTokenAddr());
        // TRON油费查询
        BigInteger tronGas = (new BigDecimal(amount).abs().multiply(BigDecimal.TEN.pow(18))).toBigInteger();
        for (TronGasWallet gasWallet : list) {
            String pk = RSACoderUtils.decryptPassword(gasWallet.getPrivateKey());
            String hash = walletWeb3j.tronWalletTransfer(gasWallet.getAddr(), pk, walletDTO.getAddr(), tronGas);
            if (hash == null) {
                throw new TronWalletException(TronWalletEnums.SERVER_IS_TOO_BUSY);
            }
            return;
        }

    }*/


    @Override
    public TronWalletDTO findByUserIdAndTokenAddrAndWalletType(String userId, String tokenAddr, String walletType) {
        return tronWalletMapper.selectByUserOpenIdAndTokenAddrAndWalletType(userId, tokenAddr, walletType);
    }

    /**
     * 填充用户详情
     *
     * @param list
     * @return
     */
    private List<WalletBaseDTO> fillUserInfos(List<WalletBaseDTO> list) {
        Set<String> userIds = new HashSet<>();
        for (WalletBaseDTO row : list) {
            userIds.add(row.getUserId());
        }
        //  获取用户信息
        ResultDTO<Map<String, UserBaseInfoDTO>> result = userFeign.userInfos(userIds);
        if (result == null) throw new TronWalletException(TronWalletEnums.SERVER_IS_TOO_BUSY);
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
     * @Param: [userName]
     * @return: com.blockchain.common.base.dto.user.UserBaseInfoDTO
     * @Author: Liu.sd
     * @Date: 2019/3/23
     */
    private UserBaseInfoDTO ConditionsUserId(String userName) {
        //  获取用户信息
        ResultDTO<UserBaseInfoDTO> result = userFeign.selectUserInfoByUserName(userName);
        if (result == null) throw new TronWalletException(TronWalletEnums.SERVER_IS_TOO_BUSY);
        if (result.getCode() != BaseConstant.REQUEST_SUCCESS) throw new RPCException(result);
        return result.getData();
    }

    /**
     * 修改指定应用钱包修改余额的简洁方法
     *
     * @param userOpenId
     * @param tokenAddr
     * @param walletType
     * @param tokenAddr
     * @param walletType
     */
    private TronWalletDTO updateBalanceByUserOpenId(String userOpenId, String tokenAddr, String walletType,
                                                    BigDecimal freeBlance, BigDecimal freezeBlance, Date date) {
        // 数据验证
        ExceptionPreconditionUtils.checkNotNull(freeBlance, new TronWalletException(TronWalletEnums.NULL_FREEBLANCE));
        ExceptionPreconditionUtils.checkNotNull(freezeBlance, new TronWalletException(TronWalletEnums.NULL_FREEZEBLANCE));
        TronWalletDTO walletDTO = findByUserIdAndTokenAddrAndWalletType(userOpenId, tokenAddr, walletType);
        if (freeBlance.add(walletDTO.getFreeBalance()).compareTo(BigDecimal.ZERO) < 0) {
            throw new TronWalletException(TronWalletEnums.NUMBER_INSUFFICIENT_ERROR);
        }
        if (freezeBlance.add(walletDTO.getFreezeBalance()).compareTo(BigDecimal.ZERO) < 0) {
            throw new TronWalletException(TronWalletEnums.NUMBER_INSUFFICIENTZE_ERROR);
        }
        // 进行行锁转账（应用钱包-应用钱包）
        int row = tronWalletMapper.updateBalanceByAddrInRowLock(walletDTO.getAddr(), tokenAddr, walletType,
                freeBlance.add(freezeBlance),
                freeBlance, freezeBlance, date);
        if (row == 0) {

            throw new TronWalletException(TronWalletEnums.SERVER_IS_TOO_BUSY);
        }
        return findByUserIdAndTokenAddrAndWalletType(userOpenId, tokenAddr, walletType);
    }


}
