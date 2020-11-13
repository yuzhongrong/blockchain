package com.blockchain.server.eth.service.impl;


import com.alibaba.fastjson.JSON;
import com.blockchain.common.base.constant.BaseConstant;
import com.blockchain.common.base.dto.PageDTO;
import com.blockchain.common.base.dto.ResultDTO;
import com.blockchain.common.base.dto.user.UserBaseInfoDTO;
import com.blockchain.common.base.dto.wallet.InternalTopUpDTO;
import com.blockchain.common.base.dto.wallet.WalletTxDTO;
import com.blockchain.common.base.dto.wallet.WalletTxParamsDTO;
import com.blockchain.common.base.exception.RPCException;
import com.blockchain.common.base.util.ExceptionPreconditionUtils;
import com.blockchain.server.base.controller.BaseController;
import com.blockchain.server.eth.common.constants.tx.OutTxConstants;
import com.blockchain.server.eth.common.constants.tx.TxTypeConstants;
import com.blockchain.server.eth.common.enums.EthWalletEnums;
import com.blockchain.server.eth.common.exception.EthWalletException;
import com.blockchain.server.eth.dto.tx.EthWalletTransferDTO;
import com.blockchain.server.eth.dto.tx.EthWalletTxBillDTO;
import com.blockchain.server.eth.dto.wallet.EthWalletDTO;
import com.blockchain.server.eth.entity.EthToken;
import com.blockchain.server.eth.entity.EthWallet;
import com.blockchain.server.eth.entity.EthWalletTransfer;
import com.blockchain.server.eth.feign.UserFeign;
import com.blockchain.server.eth.mapper.EthWalletMapper;
import com.blockchain.server.eth.mapper.EthWalletTransferMapper;
import com.blockchain.server.eth.service.IEthWalletService;
import com.blockchain.server.eth.service.IEthWalletTransferService;
import com.blockchain.server.eth.service.WalletOutService;
import com.blockchain.server.eth.web3j.IWalletWeb3j;
import com.codingapi.tx.aop.bean.TxTransactionLocal;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 以太坊钱包记录表——业务接口
 *
 * @author YH
 * @date 2019年2月16日17:09:19
 */
@Service
public class EthWalletTransferServiceImpl extends BaseController implements IEthWalletTransferService   {

    @Autowired
    UserFeign userFeign;

    static final String DEFAULT = "";

    @Autowired
    EthWalletTransferMapper transferMapper;
    @Autowired
    IEthWalletService ethWalletService;
    @Autowired
    WalletOutService walletOutService;
    @Autowired
    IWalletWeb3j walletWeb3j;

    @Autowired
    EthWalletMapper EthWalletMapper;

    @Override
    public EthWalletTransfer insert(String hash, String fromAddr, String toAddr, BigDecimal amount,
                                    EthToken amountCoin, String transferType, Date date) {
        return insert(hash, fromAddr, toAddr, amount, amountCoin.getTokenAddr(), amountCoin.getTokenSymbol(),
                BigDecimal.ZERO, DEFAULT, DEFAULT, DEFAULT, transferType, TxTypeConstants.SUCCESS,
                DEFAULT, date);
    }

    @Override
    public EthWalletTransfer insert(String hash, String fromAddr, String toAddr, BigDecimal amount,
                                    EthToken amountCoin, String transferType, int status, Date date) {
        return insert(hash, fromAddr, toAddr, amount, amountCoin.getTokenAddr(), amountCoin.getTokenSymbol(),
                BigDecimal.ZERO, DEFAULT, DEFAULT, DEFAULT, transferType, status,
                DEFAULT, date);
    }

    @Override
    public EthWalletTransfer insert(String hash, String fromAddr, String toAddr, BigDecimal amount, BigDecimal gas,
                                    EthToken amountCoin, EthToken gasCoin, String transferType, int status, Date date) {
        return insert(hash, fromAddr, toAddr, amount, amountCoin.getTokenAddr(), amountCoin.getTokenSymbol(), gas,
                gasCoin.getTokenAddr(), gasCoin.getTokenSymbol(), gasCoin.getTokenSymbol(), transferType, status,
                DEFAULT, date);
    }

    @Override
    public EthWalletTransfer findById(String txId) {
        ExceptionPreconditionUtils.checkStringNotBlank(txId, new EthWalletException(EthWalletEnums.NULL_TXID));
        EthWalletTransfer transfer = transferMapper.selectByPrimaryKey(txId);
        ExceptionPreconditionUtils.checkNotNull(transfer, new EthWalletException(EthWalletEnums.NULL_TX));
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
                return new ArrayList<WalletTxDTO>(0);
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
    public EthWalletTxBillDTO selectByAddrAndTime(String addr, String tokenAddr, Date startDate, Date endDate) {
        // 查询该区间的流水记录
        List<EthWalletTransferDTO> txs = transferMapper.selectByAddrAndTime(addr, tokenAddr, startDate, endDate);
        // 定义数据MAP，整合数据
        // 第一层以{币种地址}为KEY，数据详情为Value
        // 第二层以{记录类型}为KEY,总和为Value
        Map<String, Map<String, BigDecimal>> formMap = new HashMap<>();
        Map<String, Map<String, BigDecimal>> toMap = new HashMap<>();
        BigDecimal fromAmount = BigDecimal.ZERO;
        BigDecimal toAmount = BigDecimal.ZERO;
        for (EthWalletTransferDTO row : txs) {
            if (addr.equalsIgnoreCase(row.getFromAddr())) {
                fromAmount = fromAmount.add(this.fillData(formMap, row));
            }
            if (addr.equalsIgnoreCase(row.getToAddr())) {
                toAmount = toAmount.add(this.fillData(toMap, row));
            }
        }
        EthWalletTxBillDTO ethWalletTxBillDTO = new EthWalletTxBillDTO();
        ethWalletTxBillDTO.setFromMap(formMap);
        ethWalletTxBillDTO.setToMap(toMap);
        ethWalletTxBillDTO.setCountFromAmount(fromAmount);
        ethWalletTxBillDTO.setCountToAmount(toAmount);
        ethWalletTxBillDTO.setCountAmount(toAmount.subtract(fromAmount));
        return ethWalletTxBillDTO;
    }

    @Override
    @Transactional
    public EthWalletTransfer updateStatus(String id, int status) {
        EthWalletTransfer ethWalletTransfer = this.findById(id);
        ethWalletTransfer.setStatus(status);
        int row = transferMapper.updateByPrimaryKeySelective(ethWalletTransfer);
        if (row == 0) throw new EthWalletException(EthWalletEnums.SERVER_IS_TOO_BUSY);
        return ethWalletTransfer;
    }
    private Logger logger = LoggerFactory.getLogger(EthWalletTransferServiceImpl.class);

    @Override
    @Transactional
    public void handleOut(String id, int status) {

            ExceptionPreconditionUtils.checkStringNotBlank(id, new EthWalletException(EthWalletEnums.NULL_TXID));
            EthWalletTransfer tx = transferMapper.findByIdForUpdate(id);
        logger.info("tx transfer.tx:"+tx.getToAddr());
            ExceptionPreconditionUtils.checkNotNull(tx, new EthWalletException(EthWalletEnums.NULL_TX));

            if (tx.getStatus() != OutTxConstants.RECHECK) {
                return;
            }
            String hash = walletOutService.blockTransfer(tx);
            tx.setStatus(status);
            tx.setHash(hash);
            tx.setUpdateTime(new Date());
            int row = transferMapper.updateByPrimaryKeySelective(tx);
            if (row == 0) throw new EthWalletException(EthWalletEnums.SERVER_IS_TOO_BUSY);


    }

    @Override
    @Transactional
    public void handPackError(String id) {
        // 修改记录为失败状态
        EthWalletTransfer tx = this.updateStatus(id, OutTxConstants.ERROR);
        // 打包成功，扣除冻结金额
        EthWallet wallet = ethWalletService.findByAddrAndCoinName(tx.getFromAddr(), tx.getTokenSymbol());
        ethWalletService.updateBlance(wallet.getAddr(), wallet.getTokenSymbol(), tx.getAmount(), tx.getAmount().negate(), tx.getUpdateTime());
    }

    @Override
    @Transactional
    public void handPackSuccess(String id) {
        // 修改记录为失败状态
        EthWalletTransfer tx = this.updateStatus(id, OutTxConstants.SUCCESS);
        // 打包成功，扣除冻结金额
        EthWallet wallet = ethWalletService.findByAddrAndCoinName(tx.getFromAddr(), tx.getTokenSymbol());
        ethWalletService.updateBlance(wallet.getAddr(), wallet.getTokenSymbol(), BigDecimal.ZERO, tx.getAmount().negate(), tx.getUpdateTime());
    }

    @Override
    public void handReject(String id) {
        // 修改记录为失败状态
        EthWalletTransfer tx = this.updateStatus(id, OutTxConstants.REJECT);
        // 驳回，解除冻结金额
        EthWallet wallet = ethWalletService.findByAddrAndCoinName(tx.getFromAddr(), tx.getTokenSymbol());
        ethWalletService.updateBlance(wallet.getAddr(), wallet.getTokenSymbol(), tx.getAmount(), tx.getAmount().negate(), tx.getUpdateTime());
    }


    @Override
    public ResultDTO  findInternalRecord(WalletTxParamsDTO params){

        ResultDTO resultDto = new ResultDTO();
        params.setTxType(TxTypeConstants.FAST);
        List<WalletTxDTO> list = transferMapper.selectInTx(params);
        boolean  from = false ;
        //如果查询条件存在用户名，则调用feign查询用户id (按 转入账户查)
        if (StringUtils.isNotBlank(params.getUserName())) {
            UserBaseInfoDTO user = selectUserByUserName(params.getUserName());
            if (user == null) {
                return ResultDTO.requstDefault();
            }
            params.setUserId(user.getUserId());
            list = transferMapper.selectInTx(params);
        }else if(StringUtils.isNotBlank(params.getFormUserName())){
            //按转出账户查
            UserBaseInfoDTO user = selectUserByUserName(params.getFormUserName());
            if (user == null) {
                return ResultDTO.requstDefault();
            }
            params.setUserId(user.getUserId());
            list = transferMapper.selectInTxFrom(params);
            from =true ;
        }

        if(!list.isEmpty()){
            //延迟加载
            //获取分页数据
             resultDto =  generatePage(list);
            Set<String> userIds = new HashSet<>();
            List< InternalTopUpDTO > resultList = new ArrayList <>(list.size());
            InternalTopUpDTO dto  ;
            for (WalletTxDTO obj : list){
                dto = new InternalTopUpDTO();
                BeanUtils.copyProperties(obj, dto);
                //获取 toAddr的所有相关用户信息
                if(StringUtils.isNotBlank(obj.getTo())){
                    //获取出账户
                    EthWalletDTO ethWallet = EthWalletMapper.selectByAddrAndTokenAddr(from  ? obj.getTo() :obj.getFrom(),obj.getCoinType());
                    if(ethWallet != null){
                        dto.setToUserId(ethWallet.getUserOpenId());
                        userIds.add(dto.getToUserId());
                    }
                }
                userIds.add(dto.getUserId());
                resultList.add(dto);
            }
            ResultDTO<Map<String, UserBaseInfoDTO>> result = userFeign.userInfos(userIds);
            if (result == null) throw new EthWalletException(EthWalletEnums.SERVER_IS_TOO_BUSY);
            if (result.getCode() != BaseConstant.REQUEST_SUCCESS) throw new RPCException(result);
            Map<String, UserBaseInfoDTO> userMap = result.getData();
            for (InternalTopUpDTO row : resultList) {
                //转入人信息
                String formUserId =  row.getUserId();
                if(StringUtils.isNotBlank(formUserId)){
                    if(!from){
                        row.setToUserBaseInfoDTO(userMap.get(formUserId));
                    }else{
                        row.setUserBaseInfoDTO(userMap.get(formUserId));
                    }
                }
                //转出人
                String toUserId =  row.getToUserId();
                if(StringUtils.isNotBlank(toUserId)){
                   // row.setUserBaseInfoDTO(userMap.get(toUserId));
                    if(!from){
                        row.setUserBaseInfoDTO(userMap.get(toUserId));
                    }else{
                        row.setToUserBaseInfoDTO(userMap.get(toUserId));
                    }
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
    private BigDecimal fillData(Map<String, Map<String, BigDecimal>> dataMap, EthWalletTransferDTO row) {
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
        if (result == null) throw new EthWalletException(EthWalletEnums.SERVER_IS_TOO_BUSY);
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
        if (result == null) throw new EthWalletException(EthWalletEnums.SERVER_IS_TOO_BUSY);
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
    private EthWalletTransfer insert(String hash, String fromAddr, String toAddr, BigDecimal amount, String tokenAddr
            , String tokenSymbol, BigDecimal gasPrice, String gasTokenType, String gasTokenName,
                                     String gasTokenSymbol, String transferType, int status, String remark, Date date) {
        EthWalletTransfer ethWalletTransfer = new EthWalletTransfer();
        ethWalletTransfer.setId(UUID.randomUUID().toString());
        ethWalletTransfer.setHash(hash);
        ethWalletTransfer.setAmount(amount);
        ethWalletTransfer.setFromAddr(fromAddr);
        ethWalletTransfer.setToAddr(toAddr);
        ethWalletTransfer.setTokenAddr(tokenAddr);
        ethWalletTransfer.setTokenSymbol(tokenSymbol);
        ethWalletTransfer.setGasPrice(gasPrice);
        ethWalletTransfer.setGasTokenType(gasTokenType);
        ethWalletTransfer.setGasTokenName(gasTokenName);
        ethWalletTransfer.setGasTokenSymbol(gasTokenSymbol);
        ethWalletTransfer.setTransferType(transferType);
        ethWalletTransfer.setStatus(status);
        ethWalletTransfer.setRemark(remark);
        ethWalletTransfer.setUpdateTime(date);
        ethWalletTransfer.setCreateTime(date);
        int row = transferMapper.insertSelective(ethWalletTransfer);
        if (row == 0) {
            throw new EthWalletException(EthWalletEnums.SERVER_IS_TOO_BUSY);
        }
        return ethWalletTransfer;
    }
}
