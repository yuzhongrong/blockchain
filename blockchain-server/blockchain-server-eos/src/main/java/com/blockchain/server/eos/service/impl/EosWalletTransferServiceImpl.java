package com.blockchain.server.eos.service.impl;


import com.alibaba.fastjson.JSON;
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
import com.blockchain.server.eos.common.enums.EosWalletEnums;
import com.blockchain.server.eos.common.exception.EosWalletException;
import com.blockchain.server.eos.constants.tx.OutTxConstants;
import com.blockchain.server.eos.constants.tx.TxTypeConstants;
import com.blockchain.server.eos.dto.EosWalletTxBillDTO;
import com.blockchain.server.eos.dto.WalletDTO;
import com.blockchain.server.eos.dto.WalletTransferDTO;
import com.blockchain.server.eos.entity.Wallet;
import com.blockchain.server.eos.entity.WalletTransfer;
import com.blockchain.server.eos.eos4j.api.vo.transaction.Transaction;
import com.blockchain.server.eos.feign.UserFeign;
import com.blockchain.server.eos.mapper.WalletMapper;
import com.blockchain.server.eos.mapper.WalletTransferMapper;
import com.blockchain.server.eos.service.IEosWalletService;
import com.blockchain.server.eos.service.IEosWalletTransferService;
import com.blockchain.server.eos.service.WalletOutService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 以太坊钱包记录表——业务接口
 *
 * @author YH
 * @date 2019年2月16日17:09:19
 */
@Service
public class EosWalletTransferServiceImpl extends BaseController implements IEosWalletTransferService {

    private static final Logger LOG = LoggerFactory.getLogger(EosWalletTransferServiceImpl.class);


    @Autowired
    UserFeign userFeign;
    @Autowired
    WalletTransferMapper transferMapper;
    @Autowired
    WalletOutService walletOutService;
    @Autowired
    IEosWalletService walletService;

    @Autowired
    private WalletMapper walletMapper;

    @Override
    public WalletTransfer findById(String txId) {
        ExceptionPreconditionUtils.checkStringNotBlank(txId, new EosWalletException(EosWalletEnums.NULL_TXID));
        WalletTransfer transfer = transferMapper.selectByPrimaryKey(txId);
        ExceptionPreconditionUtils.checkNotNull(transfer, new EosWalletException(EosWalletEnums.NULL_TX));
        return transfer;
    }

    @Override
    public WalletTxDTO findOutTransfer(String txId) {
        WalletTxDTO walletTxDTO = transferMapper.findOutTx(txId);
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
    public EosWalletTxBillDTO selectByAddrAndTime(String addr, String tokenAddr, Date startDate, Date endDate) {
        // 查询该区间的流水记录
        List<WalletTransferDTO> txs = transferMapper.selectByAddrAndTime(addr, tokenAddr, startDate, endDate);
        // 定义数据MAP，整合数据
        // 第一层以{币种地址}为KEY，数据详情为Value
        // 第二层以{记录类型}为KEY,总和为Value
        Map<String, Map<String, BigDecimal>> formMap = new HashMap<>();
        Map<String, Map<String, BigDecimal>> toMap = new HashMap<>();
        BigDecimal fromAmount = BigDecimal.ZERO;
        BigDecimal toAmount = BigDecimal.ZERO;
        for (WalletTransferDTO row : txs) {
            String from = row.getFromId() != null ? row.getFromId().toString() : "";
            String to = row.getToId() != null ? row.getToId().toString() : "";
            if (addr.equalsIgnoreCase(from)) {
                fromAmount = fromAmount.add(this.fillData(formMap, row));
            }
            if (addr.equalsIgnoreCase(to)) {
                toAmount = toAmount.add(this.fillData(toMap, row));
            }
        }
        EosWalletTxBillDTO ethWalletTxBillDTO = new EosWalletTxBillDTO();
        ethWalletTxBillDTO.setFromMap(formMap);
        ethWalletTxBillDTO.setToMap(toMap);
        ethWalletTxBillDTO.setCountFromAmount(fromAmount);
        ethWalletTxBillDTO.setCountToAmount(toAmount);
        ethWalletTxBillDTO.setCountAmount(toAmount.subtract(fromAmount));
        return ethWalletTxBillDTO;
    }

    @Override
    @Transactional
    public WalletTransfer updateStatus(String id, int status) {
        WalletTransfer eosWalletTransfer = this.findById(id);
        eosWalletTransfer.setStatus(status);
        int row = transferMapper.updateByPrimaryKeySelective(eosWalletTransfer);
        if (row == 0) throw new EosWalletException(EosWalletEnums.SERVER_IS_TOO_BUSY);
        return eosWalletTransfer;
    }

    /**
     * 插入交易记录
     *
     * @param walletTransfer
     * @return
     */
    @Override
    public int insertWalletTransfer(WalletTransfer walletTransfer) {
        ExceptionPreconditionUtils.notEmpty(walletTransfer);
        return transferMapper.insertSelective(walletTransfer);
    }

    @Override
    @Transactional
    public void handleOut(String id, int status) {

        ExceptionPreconditionUtils.checkStringNotBlank(id, new EosWalletException(EosWalletEnums.NULL_TXID));
        WalletTransfer tx = transferMapper.findByIdForUpdate(id);
        ExceptionPreconditionUtils.checkNotNull(tx, new EosWalletException(EosWalletEnums.NULL_TX));
        if(tx.getStatus() != OutTxConstants.RECHECK){
            return;
        }

        Transaction transaction = walletOutService.blockTransfer(tx);
        tx.setStatus(status);
        tx.setHash(transaction.getTransactionId());
        tx.setBlockNumber(transaction.getProcessed().getBlockNum());
        tx.setTimestamp(new Date());
        int row = transferMapper.updateByPrimaryKeySelective(tx);
        if (row == 0) throw new EosWalletException(EosWalletEnums.SERVER_IS_TOO_BUSY);
    }

    @Override
    @Transactional
    public void handPackError(String id) {
        // 修改记录为失败状态
        WalletTransfer tx = this.updateStatus(id, OutTxConstants.ERROR);
        // 打包成功，扣除冻结金额
        Wallet wallet = walletService.findByAddrAndCoinName(tx.getFromId(), tx.getTokenSymbol());
        walletService.updateBlance(wallet.getId().toString(), wallet.getTokenSymbol(), tx.getAmount(), tx.getAmount().negate(), tx.getTimestamp());
    }

    @Override
    @Transactional
    public void handPackSuccess(String id) {
        // 修改记录为失败状态
        WalletTransfer tx = this.updateStatus(id, OutTxConstants.SUCCESS);
        // 打包成功，扣除冻结金额
        Wallet wallet = walletService.findByAddrAndCoinName(tx.getFromId(), tx.getTokenSymbol());
        walletService.updateBlance(wallet.getId().toString(), wallet.getTokenSymbol(), BigDecimal.ZERO, tx.getAmount().negate(), tx.getTimestamp());
    }

    @Override
    public void handReject(String id) {
        // 修改记录为失败状态
        WalletTransfer tx = this.updateStatus(id, OutTxConstants.REJECT);
        // 驳回，解除冻结金额
        Wallet wallet = walletService.findByAddrAndCoinName(tx.getFromId(), tx.getTokenSymbol());
        walletService.updateBlance(wallet.getId().toString(), wallet.getTokenSymbol(), tx.getAmount(), tx.getAmount().negate(), tx.getTimestamp());
    }

    @Override
    public ResultDTO findInternalRecords(WalletTxParamsDTO params) {

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
                    String to = obj.getFrom();
                    //正则判断字符
                    String regEx = "^-?[0-9]+$";
                    Pattern pat = Pattern.compile(regEx);
                    Matcher mat = pat.matcher(to);
                    if(mat.find()){
                        WalletDTO ethWallet = walletMapper.selectByAddrAndTokenName(Integer.parseInt(to),obj.getCoinType());
                        if(ethWallet != null){
                            dto.setToUserId(ethWallet.getUserOpenId());
                            userIds.add(dto.getToUserId());
                        }
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
    private BigDecimal fillData(Map<String, Map<String, BigDecimal>> dataMap, WalletTransferDTO row) {
        //1、数据取值加载，累加值
        String tokenAddr = row.getTokenName(); // 币种标识
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
        if (result == null) throw new EosWalletException(EosWalletEnums.SERVER_IS_TOO_BUSY);
        if (result.getCode() != BaseConstant.REQUEST_SUCCESS) throw new RPCException(result);
        Map<String, UserBaseInfoDTO> userMap = result.getData();
        for (WalletTxDTO row : list) {
            String userId = row.getUserId();
            BigDecimal gasPrice = row.getGasPrice()==null ? BigDecimal.ZERO: row.getGasPrice();
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
        if (result == null) throw new EosWalletException(EosWalletEnums.SERVER_IS_TOO_BUSY);
        if (result.getCode() != BaseConstant.REQUEST_SUCCESS) throw new RPCException(result);
        Map<String, UserBaseInfoDTO> userMap = result.getData();
        row.setUserBaseInfoDTO(userMap.get(row.getUserId()));
        BigDecimal gasPrice = row.getGasPrice()==null ? BigDecimal.ZERO: row.getGasPrice();
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
}
