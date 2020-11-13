package com.blockchain.server.tron.service.impl;


import com.blockchain.common.base.constant.DetailConstant;
import com.blockchain.common.base.util.ExceptionPreconditionUtils;
import com.blockchain.server.tron.common.constants.tx.TotalTxConstants;
import com.blockchain.server.tron.common.enums.TronWalletEnums;
import com.blockchain.server.tron.common.exception.TronWalletException;
import com.blockchain.server.tron.dto.tx.TronWalletTxBillDTO;
import com.blockchain.server.tron.dto.wallet.TronTotalInfoDTO;
import com.blockchain.server.tron.dto.wallet.TronWalletDTO;
import com.blockchain.server.tron.mapper.TronClearingTotalMapper;
import com.blockchain.server.tron.entity.*;
import com.blockchain.server.tron.service.*;
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
public class TronClearingTotalServiceImpl implements ITronClearingTotalService {
    @Autowired
    TronClearingTotalMapper tronClearingTotalMapper;
    @Autowired
    ITronWalletService tronWalletService;
    @Autowired
    ITronTokenService tronTokenService;
    @Autowired
    ITronWalletTransferService tronWalletTransferService;
    @Autowired
    ITronClearingDetailService tronClearingDetailService;
    @Autowired
    ITronClearingCorrService tronClearingCorrService;

    @Override
    public TronClearingTotal findNewByWallet(String addr, String tokenSymbol, String walletType) {
        return tronClearingTotalMapper.selectNewestOne(addr, tokenSymbol, walletType);
    }

    @Override
    public TronClearingTotal findById(String id) {
        ExceptionPreconditionUtils.checkStringNotBlank(id, new TronWalletException(TronWalletEnums.NULL_TOTALID));
        TronClearingTotal total = tronClearingTotalMapper.selectByPrimaryKey(id);
        ExceptionPreconditionUtils.checkNotNull(total, new TronWalletException(TronWalletEnums.NULL_TOKENALL));
        return total;
    }

    @Override
    public List<TronTotalInfoDTO> selectInfoAll(String userId) {
        List<TronTotalInfoDTO> infos = new ArrayList<>();
        List<TronWallet> wallets = tronWalletService.selectByUserId(userId);
        for (TronWallet wallet : wallets) {
            // 查询最新的统计记录
            TronClearingTotal total = this.findNewByWallet(wallet.getAddr(), wallet.getTokenSymbol(),
                    wallet.getWalletType());
            if(null == total) continue;
            // 查询统计记录的流水记录
            List<TronClearingDetail> details = tronClearingDetailService.selectByTotalId(total.getId());
            // 查询统计的更正记录
            List<TronClearingCorr> corrs = tronClearingCorrService.selectByTotalId(total.getId());
            TronTotalInfoDTO info = new TronTotalInfoDTO();
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
        TronClearingTotal total = this.findById(id); // 获取记录详情
        TronToken tronToken = tronTokenService.findByTokenName(total.getTokenSymbol()); // 获取币种信息
        // 计算更正的额度
        BigDecimal balance = amount.subtract(total.getBalance());
        // 更正余额
        tronWalletService.updateFreeBalance(total.getUserId(), tronToken.getTokenAddr(), total.getWalletType(), balance
                , date);

        // 添加更正记录
        TronClearingCorr corr = getCorr(total, amount, date);
        tronClearingCorrService.insert(corr);
        total.setBalance(amount);
        total.setDiffBalance(amount.subtract(total.getRealBalance()).abs());
        total.setStatus(TotalTxConstants.CORR);
        tronClearingTotalMapper.updateByPrimaryKeySelective(total);
    }

    /**
     * 封装更正记录对象的方法
     *
     * @param total   统计对象数据
     * @param amount  更正后的值
     * @param endDate 结束时间
     * @return
     */
    private TronClearingCorr getCorr(TronClearingTotal total, BigDecimal amount, Date endDate) {
        TronClearingCorr corr = new TronClearingCorr();
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
    public void insert(TronClearingTotal tronClearingTotal) {
        int row = tronClearingTotalMapper.insertSelective(tronClearingTotal);
        if (row == 0) {
            throw new TronWalletException(TronWalletEnums.SERVER_IS_TOO_BUSY);
        }
    }

    @Override
    @Transactional
    public void insertTotal(TronWalletDTO tronWallet, Date endDate) {
        endDate = endDate == null ? new Date() : endDate;
        // 查询最后一次统计结果
        TronClearingTotal totalLast = this.findNewByWallet(tronWallet.getAddr(), tronWallet.getTokenSymbol(),
                tronWallet.getWalletType());
        endDate = tronWallet.getUpdateTime().compareTo(endDate) > 0 ? tronWallet.getUpdateTime() : endDate; // 期末时间
        Date startDate = totalLast == null ? tronWallet.getCreateTime() : totalLast.getCreateTime(); // 期初时间
        TronWalletTxBillDTO txData = tronWalletTransferService.selectByAddrAndTime(tronWallet.getAddr(),
                tronWallet.getTokenAddr(), startDate, endDate);
        BigDecimal lastAmount = totalLast == null ? BigDecimal.ZERO : totalLast.getRealBalance(); // 期初金额
        BigDecimal currentAmount = tronWallet.getBalance(); // 钱包当前的余额
        BigDecimal realBalance = lastAmount.add(txData.getCountAmount()); // 实际金额
        BigDecimal diffBalance = currentAmount.subtract(realBalance).abs(); // 资金偏差
        // 插入统计总记录
        TronClearingTotal total = new TronClearingTotal();
        String id = UUID.randomUUID().toString();
        total.setId(id);
        total.setAddr(tronWallet.getAddr());
        total.setBalance(currentAmount);
        total.setRealBalance(realBalance);
        total.setDiffBalance(diffBalance);
        total.setCreateTime(endDate);
        total.setModifyTime(endDate);
        total.setPreBalance(lastAmount);
        total.setPreTime(startDate);
        total.setStatus(TotalTxConstants.NEW);
        total.setTokenSymbol(tronWallet.getTokenSymbol());
        total.setUserId(tronWallet.getUserOpenId());
        total.setWalletLastTime(tronWallet.getUpdateTime());
        total.setWalletType(tronWallet.getWalletType());
        this.insert(total);
        tronClearingDetailService.insert(total, txData.getFromMap(), txData.getToMap());
        tronClearingDetailService.insert(total.getId(), DetailConstant.FREE,tronWallet.getFreeBalance(),total.getCreateTime());
        tronClearingDetailService.insert(total.getId(), DetailConstant.FREERE,tronWallet.getFreezeBalance(),total.getCreateTime());
    }

    @Override
    @Transactional
    public void insertTotal(TronWallet tronWallet, Date endDate) {
        endDate = endDate == null ? new Date() : endDate;
        // 查询最后一次统计结果
        TronClearingTotal totalLast = this.findNewByWallet(tronWallet.getAddr(), tronWallet.getTokenSymbol(),
                tronWallet.getWalletType());
        endDate = tronWallet.getUpdateTime().compareTo(endDate) > 0 ? tronWallet.getUpdateTime() : endDate; // 期末时间
        Date startDate = totalLast == null ? tronWallet.getCreateTime() : totalLast.getCreateTime(); // 期初时间
        TronWalletTxBillDTO txData = tronWalletTransferService.selectByAddrAndTime(tronWallet.getAddr(),
                tronWallet.getTokenAddr(), startDate, endDate);
        BigDecimal lastAmount = totalLast == null ? BigDecimal.ZERO : totalLast.getRealBalance(); // 期初金额
        BigDecimal currentAmount = tronWallet.getBalance(); // 钱包当前的余额
        BigDecimal realBalance = lastAmount.add(txData.getCountAmount()); // 实际金额
        BigDecimal diffBalance = currentAmount.subtract(realBalance).abs(); // 资金偏差
        // 插入统计总记录
        TronClearingTotal total = new TronClearingTotal();
        String id = UUID.randomUUID().toString();
        total.setId(id);
        total.setAddr(tronWallet.getAddr());
        total.setBalance(currentAmount);
        total.setRealBalance(realBalance);
        total.setDiffBalance(diffBalance);
        total.setCreateTime(endDate);
        total.setModifyTime(endDate);
        total.setPreBalance(lastAmount);
        total.setPreTime(startDate);
        total.setStatus(TotalTxConstants.NEW);
        total.setTokenSymbol(tronWallet.getTokenSymbol());
        total.setUserId(tronWallet.getUserOpenId());
        total.setWalletLastTime(tronWallet.getUpdateTime());
        total.setWalletType(tronWallet.getWalletType());
        this.insert(total);
        tronClearingDetailService.insert(total, txData.getFromMap(), txData.getToMap());
        tronClearingDetailService.insert(total.getId(), DetailConstant.FREE,tronWallet.getFreeBalance(),total.getCreateTime());
        tronClearingDetailService.insert(total.getId(), DetailConstant.FREERE,tronWallet.getFreezeBalance(),total.getCreateTime());
    }


    @Override
    @Transactional
    public List<TronTotalInfoDTO> insertTotals(String userId) {
        Date endDate = new Date();
        List<TronWallet> wallets = tronWalletService.selectByUserId(userId);
        for (TronWallet wallet : wallets) {
            this.insertTotal(wallet, endDate);
        }
        List<TronTotalInfoDTO> list = this.selectInfoAll(userId);
        return list;
    }


}
