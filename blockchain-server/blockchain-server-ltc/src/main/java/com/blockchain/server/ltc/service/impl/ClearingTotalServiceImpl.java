package com.blockchain.server.ltc.service.impl;


import com.blockchain.common.base.constant.DetailConstant;
import com.blockchain.common.base.util.ExceptionPreconditionUtils;
import com.blockchain.server.ltc.common.constants.TotalTxConstants;
import com.blockchain.server.ltc.common.enums.WalletEnums;
import com.blockchain.server.ltc.common.exception.WalletException;
import com.blockchain.server.ltc.dto.TotalInfoDTO;
import com.blockchain.server.ltc.dto.WalletDTO;
import com.blockchain.server.ltc.dto.WalletTxBillDTO;
import com.blockchain.server.ltc.entity.*;
import com.blockchain.server.ltc.mapper.ClearingTotalMapper;
import com.blockchain.server.ltc.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 期初期末记录表——业务接口
 *
 * @author YH
 * @date 2019年2月16日17:09:19
 */
@Service
public class ClearingTotalServiceImpl implements IClearingTotalService {
    @Autowired
    ClearingTotalMapper clearingTotalMapper;
    @Autowired
    IWalletService walletService;
    @Autowired
    ITokenService tokenService;
    @Autowired
    IWalletTransferService walletTransferService;
    @Autowired
    IClearingDetailService clearingDetailService;
    @Autowired
    IClearingCorrService clearingCorrService;

    @Override
    public ClearingTotal findNewByWallet(String addr, String tokenSymbol, String walletType) {
        return clearingTotalMapper.selectNewestOne(addr, tokenSymbol, walletType);
    }

    @Override
    public ClearingTotal findById(String id) {
        ExceptionPreconditionUtils.checkStringNotBlank(id, new WalletException(WalletEnums.NULL_TOTALID));
        ClearingTotal total = clearingTotalMapper.selectByPrimaryKey(id);
        ExceptionPreconditionUtils.checkNotNull(total, new WalletException(WalletEnums.NULL_TOKENALL));
        return total;
    }

    @Override
    public List<TotalInfoDTO> selectInfoAll(String userId) {
        List<TotalInfoDTO> infos = new ArrayList<>();
        List<Wallet> wallets = walletService.selectByUserId(userId);
        for (Wallet wallet : wallets) {
            // 查询最新的统计记录
            ClearingTotal total = this.findNewByWallet(wallet.getAddr(), wallet.getTokenSymbol(),
                    wallet.getWalletType());
            if(null == total) continue;
            // 查询统计记录的流水记录
            List<ClearingDetail> details = clearingDetailService.selectByTotalId(total.getId());
            // 查询统计的更正记录
            List<ClearingCorr> corrs = clearingCorrService.selectByTotalId(total.getId());
            TotalInfoDTO info = new TotalInfoDTO();
            info.setTotal(total);
            info.setCorrs(corrs);
            info.setDetails(details);
            infos.add(info);
        }
        return infos;
    }


    @Override
    @Transactional
    public void updateCorr(String id, BigDecimal amount) {
        Date date = new Date();
        ClearingTotal total = this.findById(id); // 获取记录详情
        Token ethToken = tokenService.findByTokenName(total.getTokenSymbol()); // 获取币种信息
        // 计算更正的额度
        BigDecimal balance = amount.subtract(total.getBalance());
        // 更正余额
        walletService.updateFreeBalance(total.getUserId(), ethToken.getTokenId().toString(), total.getWalletType()
                , balance, date);
        // 添加更正记录
        ClearingCorr corr = getCorr(total, amount, date);
        clearingCorrService.insert(corr);

        total.setBalance(amount);
        total.setDiffBalance(amount.subtract(total.getRealBalance()).abs());
        total.setStatus(TotalTxConstants.CORR);
        clearingTotalMapper.updateByPrimaryKeySelective(total);
    }

    /**
     * 封装更正记录对象的方法
     *
     * @param total   统计对象数据
     * @param amount  更正后的值
     * @param endDate 结束时间
     * @return
     */
    private ClearingCorr getCorr(ClearingTotal total, BigDecimal amount, Date endDate) {
        ClearingCorr corr = new ClearingCorr();
        corr.setTotalId(total.getId());
        corr.setUserId(total.getUserId());
        corr.setTokenSymbol(total.getTokenSymbol());
        corr.setWalletType(total.getWalletType());
        corr.setPreBalance(total.getBalance());
        corr.setAfterBalance(amount);
        corr.setClearingTime(total.getCreateTime());
        corr.setCreateTime(endDate);
        return corr;
    }

    @Override
    public void insert(ClearingTotal clearingTotal) {
        int row = clearingTotalMapper.insertSelective(clearingTotal);
        if (row == 0) {
            throw new WalletException(WalletEnums.SERVER_IS_TOO_BUSY);
        }
    }

    @Override
    @Transactional
    public void insertTotal(WalletDTO wallet, Date endDate) {
        endDate = endDate == null ? new Date() : endDate;
        // 查询最后一次统计结果
        ClearingTotal totalLast = this.findNewByWallet(wallet.getAddr(), wallet.getTokenSymbol(),
                wallet.getWalletType());
        endDate = wallet.getUpdateTime().compareTo(endDate) > 0 ? wallet.getUpdateTime() : endDate; // 期末时间
        Date startDate = totalLast == null ? wallet.getCreateTime() : totalLast.getCreateTime(); // 期初时间
        WalletTxBillDTO txData = walletTransferService.selectByAddrAndTime(wallet.getAddr(),
                wallet.getTokenId().toString(), startDate, endDate);
        BigDecimal lastAmount = totalLast == null ? BigDecimal.ZERO : totalLast.getRealBalance(); // 期初金额
        BigDecimal currentAmount = wallet.getBalance(); // 钱包当前的余额
        BigDecimal realBalance = lastAmount.add(txData.getCountAmount()); // 实际金额
        BigDecimal diffBalance = currentAmount.subtract(realBalance).abs(); // 资金偏差
        // 插入统计总记录
        ClearingTotal total = new ClearingTotal();
        String id = UUID.randomUUID().toString();
        total.setId(id);
        total.setAddr(wallet.getAddr());
        total.setBalance(currentAmount);
        total.setRealBalance(realBalance);
        total.setDiffBalance(diffBalance);
        total.setCreateTime(endDate);
        total.setModifyTime(endDate);
        total.setPreBalance(lastAmount);
        total.setPreTime(startDate);
        total.setStatus(TotalTxConstants.NEW);
        total.setTokenSymbol(wallet.getTokenSymbol());
        total.setUserId(wallet.getUserOpenId());
        total.setWalletLastTime(wallet.getUpdateTime());
        total.setWalletType(wallet.getWalletType());
        this.insert(total);
        clearingDetailService.insert(total, txData.getFromMap(), txData.getToMap());
        clearingDetailService.insert(total.getId(), DetailConstant.FREE,wallet.getFreeBalance(),total.getCreateTime());
        clearingDetailService.insert(total.getId(), DetailConstant.FREERE,wallet.getFreezeBalance(),total.getCreateTime());
    }

    @Override
    @Transactional
    public void insertTotal(Wallet wallet, Date endDate) {
        endDate = endDate == null ? new Date() : endDate;
        // 查询最后一次统计结果
        ClearingTotal totalLast = this.findNewByWallet(wallet.getAddr(), wallet.getTokenSymbol(),
                wallet.getWalletType());
        endDate = wallet.getUpdateTime().compareTo(endDate) > 0 ? wallet.getUpdateTime() : endDate; // 期末时间
        Date startDate = totalLast == null ? wallet.getCreateTime() : totalLast.getCreateTime(); // 期初时间
        WalletTxBillDTO txData = walletTransferService.selectByAddrAndTime(wallet.getAddr(),
                wallet.getTokenId().toString(), startDate, endDate);

        BigDecimal lastAmount = totalLast == null ? BigDecimal.ZERO : totalLast.getRealBalance(); // 期初金额
        BigDecimal currentAmount = wallet.getBalance(); // 钱包当前的余额
        BigDecimal realBalance = lastAmount.add(txData.getCountAmount()); // 实际金额
        BigDecimal diffBalance = currentAmount.subtract(realBalance).abs(); // 资金偏差

        // 插入统计总记录
        ClearingTotal total = new ClearingTotal();
        String id = UUID.randomUUID().toString();
        total.setId(id);
        total.setAddr(wallet.getAddr());
        total.setBalance(currentAmount);
        total.setRealBalance(realBalance);
        total.setDiffBalance(diffBalance);
        total.setCreateTime(endDate);
        total.setModifyTime(endDate);
        total.setPreBalance(lastAmount);
        total.setPreTime(startDate);
        total.setStatus(TotalTxConstants.NEW);
        total.setTokenSymbol(wallet.getTokenSymbol());
        total.setUserId(wallet.getUserOpenId());
        total.setWalletLastTime(wallet.getUpdateTime());
        total.setWalletType(wallet.getWalletType());
        this.insert(total);
        clearingDetailService.insert(total, txData.getFromMap(), txData.getToMap());
        clearingDetailService.insert(total.getId(), DetailConstant.FREE, wallet.getFreeBalance(),total.getCreateTime());
        clearingDetailService.insert(total.getId(), DetailConstant.FREERE, wallet.getFreezeBalance(),total.getCreateTime());
    }


    @Override
    @Transactional
    public List<TotalInfoDTO> insertTotals(String userId) {
        Date endDate = new Date();
        List<Wallet> wallets = walletService.selectByUserId(userId);
        for (Wallet wallet : wallets) {
            this.insertTotal(wallet, endDate);
        }
        List<TotalInfoDTO> list = this.selectInfoAll(userId);
        return list;
    }


}
