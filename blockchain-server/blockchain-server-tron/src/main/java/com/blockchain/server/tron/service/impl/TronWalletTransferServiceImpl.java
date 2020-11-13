package com.blockchain.server.tron.service.impl;


import com.blockchain.common.base.constant.BaseConstant;
import com.blockchain.common.base.dto.PageDTO;
import com.blockchain.common.base.dto.ResultDTO;
import com.blockchain.common.base.dto.user.UserBaseInfoDTO;
import com.blockchain.common.base.dto.wallet.InternalTopUpDTO;
import com.blockchain.common.base.dto.wallet.WalletTxDTO;
import com.blockchain.common.base.dto.wallet.WalletTxParamsDTO;
import com.blockchain.common.base.enums.BaseResultEnums;
import com.blockchain.common.base.exception.BaseException;
import com.blockchain.common.base.exception.RPCException;
import com.blockchain.common.base.util.ExceptionPreconditionUtils;
import com.blockchain.server.base.controller.BaseController;
import com.blockchain.server.tron.common.constants.tx.OutTxConstants;
import com.blockchain.server.tron.common.constants.tx.TxTypeConstants;
import com.blockchain.server.tron.common.enums.TronWalletEnums;
import com.blockchain.server.tron.common.exception.TronWalletException;
import com.blockchain.server.tron.dto.tx.TronWalletTransferDTO;
import com.blockchain.server.tron.dto.tx.TronWalletTxBillDTO;
import com.blockchain.server.tron.dto.wallet.TronWalletDTO;
import com.blockchain.server.tron.entity.TronToken;
import com.blockchain.server.tron.entity.TronWallet;
import com.blockchain.server.tron.entity.TronWalletTransfer;
import com.blockchain.server.tron.feign.UserFeign;
import com.blockchain.server.tron.mapper.TronWalletMapper;
import com.blockchain.server.tron.mapper.TronWalletTransferMapper;
import com.blockchain.server.tron.service.ITronWalletService;
import com.blockchain.server.tron.service.ITronWalletTransferService;
import com.blockchain.server.tron.service.TronWalletOutService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * 以太坊钱包记录表——业务接口
 *
 * @author YH
 * @date 2019年2月16日17:09:19
 */
@Service
public class TronWalletTransferServiceImpl extends BaseController implements ITronWalletTransferService {
    @Autowired
    UserFeign userFeign;

    static final String DEFAULT = "";

    @Autowired
    TronWalletTransferMapper transferMapper;
    @Autowired
    ITronWalletService tronWalletService;
    @Autowired
    TronWalletOutService tronWalletOutService;
    @Autowired
    TronWalletMapper tronWalletMapper;
    @Override
    public TronWalletTransfer insert(String hash, String fromAddr, String toAddr, BigDecimal amount,
                                     TronToken amountCoin, String transferType, Date date) {
        return insert(hash, fromAddr, toAddr, amount, amountCoin.getTokenAddr(), amountCoin.getTokenSymbol(),
                BigDecimal.ZERO, DEFAULT, DEFAULT, DEFAULT, transferType, TxTypeConstants.SUCCESS,
                DEFAULT, date);
    }

    /**
     * 插入交易记录
     *
     * @param tronWalletTransfer
     * @return
     */
    @Override
    @Transactional
    public Integer insertWalletTransfer(TronWalletTransfer tronWalletTransfer) {
        ExceptionPreconditionUtils.notEmpty(tronWalletTransfer);
        return transferMapper.insertSelective(tronWalletTransfer);
    }


    @Override
    public TronWalletTransfer insert(String hash, String fromAddr, String toAddr, BigDecimal amount,
                                     TronToken amountCoin, String transferType, int status, Date date) {
        return insert(hash, fromAddr, toAddr, amount, amountCoin.getTokenAddr(), amountCoin.getTokenSymbol(),
                BigDecimal.ZERO, DEFAULT, DEFAULT, DEFAULT, transferType, status,
                DEFAULT, date);
    }

    @Override
    public TronWalletTransfer insert(String hash, String fromAddr, String toAddr, BigDecimal amount, BigDecimal gas,
                                     TronToken amountCoin, TronToken gasCoin, String transferType, int status, Date date) {
        return insert(hash, fromAddr, toAddr, amount, amountCoin.getTokenAddr(), amountCoin.getTokenSymbol(), gas,
                gasCoin.getTokenAddr(), gasCoin.getTokenSymbol(), gasCoin.getTokenSymbol(), transferType, status,
                DEFAULT, date);
    }

    @Override
    public TronWalletTransfer findById(String txId) {
        ExceptionPreconditionUtils.checkStringNotBlank(txId, new TronWalletException(TronWalletEnums.NULL_TXID));
        TronWalletTransfer transfer = transferMapper.selectByPrimaryKey(txId);
        ExceptionPreconditionUtils.checkNotNull(transfer, new TronWalletException(TronWalletEnums.NULL_TX));
        return transfer;
    }

    @Override
    public WalletTxDTO findOutTransfer(String txId) {
        WalletTxDTO walletTxDTO = transferMapper.findOutTx(txId);
        ResultDTO<UserBaseInfoDTO> result = userFeign.selectUserInfoById(walletTxDTO.getUserId());
        if (result.getCode() == BaseConstant.REQUEST_SUCCESS) {
            BeanUtils.copyProperties(result.getData(), walletTxDTO);
        }
        return fillUserInfo(walletTxDTO);
    }

    @Override
    public WalletTxDTO findInTransfer(String txId) {
        WalletTxDTO walletTxDTO = transferMapper.findInTx(txId);
        return fillUserInfo(walletTxDTO);
    }

    @Override
    public List<WalletTxDTO> selectOutTransfer(WalletTxParamsDTO params) {
        //如果查询条件存在用户名，则调用feign查询用户id
        if (StringUtils.isNotBlank(params.getUserName())) {
            UserBaseInfoDTO user = selectUserByUserName(params.getUserName());
            if (user == null) {
                return null;
            }
            params.setUserId(user.getUserId());
        }
        params.setTxType(TxTypeConstants.OUT);
        List<WalletTxDTO> list = transferMapper.selectOutTx(params);
        if (list.size() == 0) return list;
        return fillUserInfos(list);
    }

    @Override
    public List<WalletTxDTO> selectInTransfer(WalletTxParamsDTO params) {
        //如果查询条件存在用户名，则调用feign查询用户id
        if (StringUtils.isNotBlank(params.getUserName())) {
            UserBaseInfoDTO user = selectUserByUserName(params.getUserName());
            if (user == null) {
                return new ArrayList<WalletTxDTO>(0);
            }
            params.setUserId(user.getUserId());
        }
        params.setTxType(TxTypeConstants.IN);
        List<WalletTxDTO> list = transferMapper.selectInTx(params);
        if (list.size() == 0) return list;
        return fillUserInfos(list);
    }

    @Override
    public TronWalletTxBillDTO selectByAddrAndTime(String addr, String tokenAddr, Date startDate, Date endDate) {
        // 查询该区间的流水记录
        List<TronWalletTransferDTO> txs = transferMapper.selectByAddrAndTime(addr, tokenAddr, startDate, endDate);
        // 定义数据MAP，整合数据
        // 第一层以{币种地址}为KEY，数据详情为Value
        // 第二层以{记录类型}为KEY,总和为Value
        Map<String, Map<String, BigDecimal>> formMap = new HashMap<>();
        Map<String, Map<String, BigDecimal>> toMap = new HashMap<>();
        BigDecimal fromAmount = BigDecimal.ZERO;
        BigDecimal toAmount = BigDecimal.ZERO;
        for (TronWalletTransferDTO row : txs) {
            if (addr.equalsIgnoreCase(row.getFromHexAddr())) {
                fromAmount = fromAmount.add(this.fillData(formMap, row));
            }
            if (addr.equalsIgnoreCase(row.getToHexAddr())) {
                toAmount = toAmount.add(this.fillData(toMap, row));
            }
        }
        TronWalletTxBillDTO tronWalletTxBillDTO = new TronWalletTxBillDTO();
        tronWalletTxBillDTO.setFromMap(formMap);
        tronWalletTxBillDTO.setToMap(toMap);
        tronWalletTxBillDTO.setCountFromAmount(fromAmount);
        tronWalletTxBillDTO.setCountToAmount(toAmount);
        tronWalletTxBillDTO.setCountAmount(toAmount.subtract(fromAmount));
        return tronWalletTxBillDTO;
    }

    @Override
    @Transactional
    public TronWalletTransfer updateStatus(String id, int status) {
        TronWalletTransfer tronWalletTransfer = this.findById(id);
        tronWalletTransfer.setStatus(status);
        int row = transferMapper.updateByPrimaryKeySelective(tronWalletTransfer);
        if (row == 0) throw new TronWalletException(TronWalletEnums.SERVER_IS_TOO_BUSY);
        return tronWalletTransfer;
    }

    @Override
    @Transactional
    public void handleOut(String id, int status) {
        ExceptionPreconditionUtils.checkStringNotBlank(id, new TronWalletException(TronWalletEnums.NULL_TXID));
        TronWalletTransfer tx = transferMapper.findByIdForUpdate(id);
        ExceptionPreconditionUtils.checkNotNull(tx, new TronWalletException(TronWalletEnums.NULL_TX));

        if (tx.getStatus() != OutTxConstants.RECHECK) {
            return;
        }

        String hash = tronWalletOutService.blockTransfer(tx);
        tx.setStatus(status);
        tx.setHash(hash);
        tx.setUpdateTime(new Date());
        int row = transferMapper.updateByPrimaryKeySelective(tx);
        if (row == 0) throw new TronWalletException(TronWalletEnums.SERVER_IS_TOO_BUSY);
    }

    @Override
    @Transactional
    public void handPackError(String id) {
        // 修改记录为失败状态
        TronWalletTransfer tx = this.updateStatus(id, OutTxConstants.ERROR);
        // 打包成功，扣除冻结金额
        TronWallet wallet = tronWalletService.findByAddrAndCoinName(tx.getFromHexAddr(), tx.getTokenSymbol());
        tronWalletService.updateBlance(wallet.getHexAddr(), wallet.getTokenSymbol(), tx.getAmount(), tx.getAmount().negate(), tx.getUpdateTime());
    }

    @Override
    @Transactional
    public void handPackSuccess(String id) {
        // 修改记录为失败状态
        TronWalletTransfer tx = this.updateStatus(id, OutTxConstants.SUCCESS);
        // 打包成功，扣除冻结金额
        TronWallet wallet = tronWalletService.findByAddrAndCoinName(tx.getFromHexAddr(), tx.getTokenSymbol());
        tronWalletService.updateBlance(wallet.getHexAddr(), wallet.getTokenSymbol(), BigDecimal.ZERO, tx.getAmount().negate(), tx.getUpdateTime());
    }

    @Override
    @Transactional
    public void handReject(String id) {
        // 修改记录为失败状态
        TronWalletTransfer tx = this.updateStatus(id, OutTxConstants.REJECT);
        // 驳回，解除冻结金额
        TronWallet wallet = tronWalletService.findByAddrAndCoinName(tx.getFromHexAddr(), tx.getTokenSymbol());
        tronWalletService.updateBlance(wallet.getHexAddr(), wallet.getTokenSymbol(), tx.getAmount(), tx.getAmount().negate(), tx.getUpdateTime());
    }

    @Override
    public ResultDTO findInternalRecord(WalletTxParamsDTO params) {

        ResultDTO resultDto = new ResultDTO();
        //如果查询条件存在用户名，则调用feign查询用户id
        if (StringUtils.isNotBlank(params.getUserName())) {
            UserBaseInfoDTO user = selectUserByUserName(params.getUserName());
            if (user == null) {
                return ResultDTO.requstDefault();
            }
            params.setUserId(user.getUserId());
        }
        params.setTxType(TxTypeConstants.FAST);
        List<WalletTxDTO> list = transferMapper.selectInTx(params);
        if(!list.isEmpty()){
            //延迟加载
            //获取分页数据
            resultDto =  generatePage(list);

            Set<String> userIds = new HashSet<>();
            List<InternalTopUpDTO> resultList = new ArrayList <>(list.size());
            InternalTopUpDTO dto  ;
            for (WalletTxDTO obj : list){
                dto = new InternalTopUpDTO();
                BeanUtils.copyProperties(obj, dto);
                //获取 toAddr的所有相关用户信息
                if(StringUtils.isNotBlank(obj.getTo())){
                    TronWalletDTO ethWallet = tronWalletMapper.selectByAddrAndTokenAddr(obj.getFrom(),obj.getCoinType());
                    if(ethWallet != null){
                        dto.setToUserId(ethWallet.getUserOpenId());
                        userIds.add(dto.getToUserId());
                    }
                }
                userIds.add(dto.getUserId());
                resultList.add(dto);
            }
            //System.out.println(JSON.toJSON(resultList));

            ResultDTO<Map<String, UserBaseInfoDTO>> result = userFeign.userInfos(userIds);
            if (result == null) throw new BaseException(BaseResultEnums.BUSY);
            if (result.getCode() != BaseConstant.REQUEST_SUCCESS) throw new RPCException(result);

            Map<String, UserBaseInfoDTO> userMap = result.getData();
            for (InternalTopUpDTO row : resultList) {
                //转入人信息
                String formUserId =  row.getUserId();
                if(StringUtils.isNotBlank(formUserId)){
                    row.setToUserBaseInfoDTO(userMap.get(formUserId));
                }
                //转出人
                String toUserId =  row.getToUserId();
                if(StringUtils.isNotBlank(toUserId)){
                    row.setUserBaseInfoDTO(userMap.get(toUserId));

                }
            }
            PageDTO resultData = (PageDTO)resultDto.getData();
            resultData.setRows(resultList);
            resultDto.setData(resultData);
            return resultDto;
        }
        return resultDto;
    }


    /**
     * 数据整合
     *
     * @param dataMap 数据集合
     * @param row     行
     */
    private BigDecimal fillData(Map<String, Map<String, BigDecimal>> dataMap, TronWalletTransferDTO row) {
        //1、数据取值加载，累加值
        String tokenAddr = row.getTokenAddr(); // 币种标识
        String txType = row.getTransferType(); // 转账类型
        BigDecimal amount = row.getAmount(); // 流动额度
        // 不存在该币种，则加入新币种
        Map<String, BigDecimal> txMap;
        if (!dataMap.containsKey(tokenAddr)) {
            txMap = new HashMap<String, BigDecimal>();
        } else {
            txMap = dataMap.get(tokenAddr);
        }
        if (!txMap.containsKey(txType)) {
            txMap.put(txType, amount);
        } else {
            txMap.put(txType, txMap.get(txType).add(amount));
        }
        dataMap.put(tokenAddr, txMap);
        return amount;
    }


    /**
     * 填充用户详情
     *
     * @param walletTxDTO
     * @return
     */
    private WalletTxDTO fillUserInfos(WalletTxDTO walletTxDTO) {
        UserBaseInfoDTO userBaseInfoDTO = new UserBaseInfoDTO();
        walletTxDTO.setUserBaseInfoDTO(userBaseInfoDTO);
        return walletTxDTO;
    }

    /**
     * 填充用户详情
     *
     * @param list
     * @return
     */
    private List<WalletTxDTO> fillUserInfos(List<WalletTxDTO> list) {
        Set<String> userIds = new HashSet<>();
        for (WalletTxDTO row : list) {
            userIds.add(row.getUserId());
        }
        //  获取用户信息
        ResultDTO<Map<String, UserBaseInfoDTO>> result = userFeign.userInfos(userIds);
        if (result == null) throw new TronWalletException(TronWalletEnums.SERVER_IS_TOO_BUSY);
        if (result.getCode() != BaseConstant.REQUEST_SUCCESS) throw new RPCException(result);
        Map<String, UserBaseInfoDTO> userMap = result.getData();
        for (WalletTxDTO row : list) {
            String userId = row.getUserId();
            BigDecimal gasPrice = row.getGasPrice() == null ? BigDecimal.ZERO : row.getGasPrice();
            row.setRelAmount(row.getAmount().subtract(gasPrice));
            if (userMap.containsKey(userId)) row.setUserBaseInfoDTO(userMap.get(userId));
        }

        return list;
    }

    private WalletTxDTO fillUserInfo(WalletTxDTO row) {
        Set<String> userIds = new HashSet<>();
        userIds.add(row.getUserId());
        //  获取用户信息
        ResultDTO<Map<String, UserBaseInfoDTO>> result = userFeign.userInfos(userIds);
        if (result == null) throw new TronWalletException(TronWalletEnums.SERVER_IS_TOO_BUSY);
        if (result.getCode() != BaseConstant.REQUEST_SUCCESS) throw new RPCException(result);
        Map<String, UserBaseInfoDTO> userMap = result.getData();
        row.setUserBaseInfoDTO(userMap.get(row.getUserId()));
        BigDecimal gasPrice = row.getGasPrice() == null ? BigDecimal.ZERO : row.getGasPrice();
        row.setRelAmount(row.getAmount().subtract(gasPrice));
        return row;
    }


    /***
     * 根据userName查询用户信息
     * @param userName
     * @return
     */
    private UserBaseInfoDTO selectUserByUserName(String userName) {
        ResultDTO<UserBaseInfoDTO> resultDTO = userFeign.selectUserInfoByUserName(userName);
        return resultDTO.getData();
    }


    /**
     * 插入一条记录数据的通用方法
     */
    private TronWalletTransfer insert(String hash, String fromAddr, String toAddr, BigDecimal amount, String tokenAddr
            , String tokenSymbol, BigDecimal gasPrice, String gasTokenType, String gasTokenName,
                                      String gasTokenSymbol, String transferType, int status, String remark, Date date) {
        TronWalletTransfer tronWalletTransfer = new TronWalletTransfer();
        tronWalletTransfer.setId(UUID.randomUUID().toString());
        tronWalletTransfer.setHash(hash);
        tronWalletTransfer.setAmount(amount);
        tronWalletTransfer.setFromHexAddr(fromAddr);
        tronWalletTransfer.setToHexAddr(toAddr);
        tronWalletTransfer.setTokenAddr(tokenAddr);
        tronWalletTransfer.setTokenSymbol(tokenSymbol);
        tronWalletTransfer.setGasPrice(gasPrice);
        tronWalletTransfer.setGasTokenType(gasTokenType);
        tronWalletTransfer.setGasTokenName(gasTokenName);
        tronWalletTransfer.setGasTokenSymbol(gasTokenSymbol);
        tronWalletTransfer.setTransferType(transferType);
        tronWalletTransfer.setStatus(status);
        tronWalletTransfer.setRemark(remark);
        tronWalletTransfer.setUpdateTime(date);
        tronWalletTransfer.setCreateTime(date);
        int row = transferMapper.insertSelective(tronWalletTransfer);
        if (row == 0) {
            throw new TronWalletException(TronWalletEnums.SERVER_IS_TOO_BUSY);
        }
        return tronWalletTransfer;
    }
}
