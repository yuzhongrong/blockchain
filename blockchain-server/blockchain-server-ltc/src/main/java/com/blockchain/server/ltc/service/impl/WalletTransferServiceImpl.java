package com.blockchain.server.ltc.service.impl;

import com.blockchain.common.base.constant.BaseConstant;
import com.blockchain.common.base.dto.PageDTO;
import com.blockchain.common.base.dto.ResultDTO;
import com.blockchain.common.base.dto.WalletChangeDTO;
import com.blockchain.common.base.dto.WalletOrderDTO;
import com.blockchain.common.base.dto.user.UserBaseInfoDTO;
import com.blockchain.common.base.dto.wallet.InternalTopUpDTO;
import com.blockchain.common.base.dto.wallet.WalletTxDTO;
import com.blockchain.common.base.dto.wallet.WalletTxParamsDTO;
import com.blockchain.common.base.enums.BaseResultEnums;
import com.blockchain.common.base.exception.BaseException;
import com.blockchain.common.base.exception.RPCException;
import com.blockchain.common.base.util.ExceptionPreconditionUtils;
import com.blockchain.server.base.controller.BaseController;
import com.blockchain.server.ltc.common.constants.OutTxConstants;
import com.blockchain.server.ltc.common.constants.TxTypeConstants;
import com.blockchain.server.ltc.common.enums.WalletEnums;
import com.blockchain.server.ltc.common.exception.WalletException;
import com.blockchain.server.ltc.dto.WalletDTO;
import com.blockchain.server.ltc.dto.WalletTransferDTO;
import com.blockchain.server.ltc.dto.WalletTxBillDTO;
import com.blockchain.server.ltc.entity.Token;
import com.blockchain.server.ltc.entity.Wallet;
import com.blockchain.server.ltc.entity.WalletTransfer;
import com.blockchain.server.ltc.feign.UserFeign;
import com.blockchain.server.ltc.mapper.WalletMapper;
import com.blockchain.server.ltc.mapper.WalletTransferMapper;
import com.blockchain.server.ltc.service.ITokenService;
import com.blockchain.server.ltc.service.IWalletService;
import com.blockchain.server.ltc.service.IWalletTransferService;
import com.blockchain.server.ltc.service.WalletOutService;
import com.codingapi.tx.annotation.ITxTransaction;
import com.codingapi.tx.annotation.TxTransaction;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
public class WalletTransferServiceImpl extends BaseController implements IWalletTransferService, ITxTransaction {

    @Autowired
    private WalletTransferMapper transferMapper;
    @Autowired
    private IWalletService walletService;
    @Autowired
    private ITokenService tokenService;
    @Autowired
    private WalletOutService walletOutService;
    @Autowired
    private UserFeign userFeign;

    @Autowired
    WalletMapper walletMapper;

    @Override
    public Integer insertTransfer(WalletTransfer walletTransfer) {
        return transferMapper.insertSelective(walletTransfer);
    }

    @Override
    public WalletTransfer findById(String txId) {
        ExceptionPreconditionUtils.checkStringNotBlank(txId, new WalletException(WalletEnums.NULL_TXID));
        WalletTransfer transfer = transferMapper.selectByPrimaryKey(txId);
        ExceptionPreconditionUtils.checkNotNull(transfer, new WalletException(WalletEnums.NULL_TX));
        return transfer;
    }

    @Override
    public WalletTxDTO findOutTransfer(String txId) {
        WalletTxDTO walletTxDTO = transferMapper.findOutTx(txId);
        UserBaseInfoDTO userBaseInfoDTO = selectUserByUserId(walletTxDTO.getUserId());
        walletTxDTO.setUserBaseInfoDTO(userBaseInfoDTO);
        return fillUserInfo(walletTxDTO);
    }

    @Override
    public WalletTxDTO findInTransfer(String txId) {
        WalletTxDTO walletTxDTO = transferMapper.findInTx(txId);
        UserBaseInfoDTO userBaseInfoDTO = selectUserByUserId(walletTxDTO.getUserId());
        walletTxDTO.setUserBaseInfoDTO(userBaseInfoDTO);
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
    public WalletTxBillDTO selectByAddrAndTime(String addr, String tokenAddr, Date startDate, Date endDate) {
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
            if (addr.equalsIgnoreCase(row.getFromAddr())) {
                fromAmount = fromAmount.add(this.fillData(formMap, row));
            }
            if (addr.equalsIgnoreCase(row.getToAddr())) {
                toAmount = toAmount.add(this.fillData(toMap, row));
            }
        }
        WalletTxBillDTO ethWalletTxBillDTO = new WalletTxBillDTO();
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
        if (row == 0) throw new WalletException(WalletEnums.SERVER_IS_TOO_BUSY);
        return eosWalletTransfer;
    }

    @Override
    @Transactional
    public void handleOut(String id, int status) {
        ExceptionPreconditionUtils.checkStringNotBlank(id, new WalletException(WalletEnums.NULL_TXID));
        WalletTransfer tx = transferMapper.findByIdForUpdate(id);
        ExceptionPreconditionUtils.checkNotNull(tx, new WalletException(WalletEnums.NULL_TX));
        if (tx.getStatus() != OutTxConstants.RECHECK) {
            return;
        }
        String hash = walletOutService.blockTransfer(tx);
        tx.setStatus(status);
        tx.setHash(hash);
        tx.setUpdateTime(new Date());
        int row = transferMapper.updateByPrimaryKeySelective(tx);
        if (row == 0) throw new WalletException(WalletEnums.SERVER_IS_TOO_BUSY);
    }

    @Override
    @Transactional
    @TxTransaction
    public WalletDTO handleOrder(WalletOrderDTO walletOrderDTO) {
        Token token = tokenService.findByTokenName(walletOrderDTO.getTokenName());
        //查询用户钱包
        WalletDTO walletDTO = walletService.findByUserIdAndTokenAddrAndWalletType(walletOrderDTO.getUserId(), token.getTokenId().toString(), walletOrderDTO.getWalletType());

        if (walletDTO.getFreeBalance().add(walletOrderDTO.getFreeBalance()).compareTo(BigDecimal.ZERO) < 0
                || walletDTO.getFreezeBalance().add(walletOrderDTO.getFreezeBalance()).compareTo(BigDecimal.ZERO) < 0) {
            throw new WalletException(WalletEnums.NUMBER_INSUFFICIENT_ERROR);
        }

        //加减钱包可用余额、冻结余额，之间转换
        walletService.updateBalance(walletDTO.getUserOpenId(), walletDTO.getTokenId().toString(),
                walletDTO.getWalletType(), walletOrderDTO.getFreeBalance(), walletOrderDTO.getFreezeBalance(), new Date());

        //返回加减余额后的数据
        return walletService.findByUserIdAndTokenAddrAndWalletType(walletOrderDTO.getUserId(), token.getTokenId().toString(), walletOrderDTO.getWalletType());
    }

    @Override
    @Transactional
    @TxTransaction
    public Integer handleChange(WalletChangeDTO walletChangeDTO) {
        BigDecimal freeBalance = walletChangeDTO.getFreeBalance();
        BigDecimal freezeBalance = walletChangeDTO.getFreezeBalance();
        BigDecimal totalBalance = freeBalance.add(freezeBalance);

        Date now = new Date();

        Token token = tokenService.findByTokenName(walletChangeDTO.getTokenName());
        //查询用户钱包
        WalletDTO walletDTO = walletService.findByUserIdAndTokenAddrAndWalletType(walletChangeDTO.getUserId(), token.getTokenId().toString(), walletChangeDTO.getWalletType());

        if (walletDTO.getFreeBalance().add(walletChangeDTO.getFreeBalance()).compareTo(BigDecimal.ZERO) < 0
                || walletDTO.getFreezeBalance().add(walletChangeDTO.getFreezeBalance()).compareTo(BigDecimal.ZERO) < 0) {
            throw new WalletException(WalletEnums.NUMBER_INSUFFICIENT_ERROR);
        }

        if (freeBalance.compareTo(BigDecimal.ZERO) != 0 || freezeBalance.compareTo(BigDecimal.ZERO) != 0) {
            //加减钱包可用余额、冻结余额、总额
            walletService.updateBalance(walletDTO.getUserOpenId(), walletDTO.getTokenId().toString(),
                    walletDTO.getWalletType(), walletChangeDTO.getFreeBalance(), walletChangeDTO.getFreezeBalance(), new Date());
        }

        //插入一条交易记录
        WalletTransfer walletTransfer = new WalletTransfer();
        if (totalBalance.compareTo(BigDecimal.ZERO) < 0) {
            walletTransfer.setFromAddr(walletDTO.getAddr());
            walletTransfer.setToAddr(null);
        } else {
            walletTransfer.setFromAddr(null);
            walletTransfer.setToAddr(walletDTO.getAddr());
        }
        walletTransfer.setId(UUID.randomUUID().toString());
        walletTransfer.setHash(walletChangeDTO.getRecordId());

        walletTransfer.setAmount(totalBalance.abs());
        walletTransfer.setTokenId(token.getTokenId());
        walletTransfer.setTokenSymbol(walletChangeDTO.getTokenName());
        walletTransfer.setGasPrice(walletChangeDTO.getGasBalance());
        walletTransfer.setTransferType(walletChangeDTO.getWalletType());
        walletTransfer.setStatus(TxTypeConstants.SUCCESS);
        walletTransfer.setCreateTime(now);
        walletTransfer.setUpdateTime(now);
        int countIt = insertTransfer(walletTransfer);

        if (countIt != 1) {
            throw new WalletException(WalletEnums.TRANSFER_ERROR);
        }

        return 1;
    }

    @Override
    @Transactional
    public void handPackError(String id) {
        // 修改记录为失败状态
        WalletTransfer tx = this.updateStatus(id, OutTxConstants.ERROR);
        //打包失败，冻结余额回退回可用余额
        Wallet wallet = walletService.findByAddrAndCoinName(tx.getFromAddr(), tx.getTokenSymbol());
        walletService.updateBalance(wallet.getUserOpenId(), wallet.getTokenId().toString(), wallet.getWalletType(),
                tx.getAmount(), tx.getAmount().negate(), tx.getUpdateTime());

    }

    @Override
    @Transactional
    public void handPackSuccess(String id) {
        // 修改记录为失败状态
        WalletTransfer tx = this.updateStatus(id, OutTxConstants.SUCCESS);
        // 打包成功，扣除冻结金额
        Wallet wallet = walletService.findByAddrAndCoinName(tx.getFromAddr(), tx.getTokenSymbol());
        walletService.updateBalance(wallet.getUserOpenId(), wallet.getTokenId().toString(), wallet.getWalletType(),
                BigDecimal.ZERO, tx.getAmount().negate(), tx.getUpdateTime());
    }

    @Override
    public void handReject(String id) {
        // 修改记录为失败状态
        WalletTransfer tx = this.updateStatus(id, OutTxConstants.REJECT);
        //驳回，解除冻结金额
        Wallet wallet = walletService.findByAddrAndCoinName(tx.getFromAddr(), tx.getTokenSymbol());
        walletService.updateBalance(wallet.getUserOpenId(), wallet.getTokenId().toString(), wallet.getWalletType(),
                tx.getAmount(), tx.getAmount().negate(), tx.getUpdateTime());
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
                    WalletDTO ethWallet = walletMapper.selectByAddrAndTokenAddr(obj.getFrom(),obj.getCoinType());
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
    private BigDecimal fillData(Map<String, Map<String, BigDecimal>> dataMap, WalletTransferDTO row) {
        //1、数据取值加载，累加值
        String tokenAddr = row.getTokenId().toString(); // 币种标识
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
     * @param list
     * @return
     */
    private List<WalletTxDTO> fillUserInfos(List<WalletTxDTO> list) {
        Set<String> userIds = new HashSet<>();
        for (WalletTxDTO row : list) {
            if (StringUtils.isNotBlank(row.getUserId())) {
                userIds.add(row.getUserId());
            }
        }
        if (userIds.size() == 0) return list;
        //获取用户信息
        ResultDTO<Map<String, UserBaseInfoDTO>> result = userFeign.userInfos(userIds);
        if (result == null) throw new BaseException(BaseResultEnums.BUSY);
        if (result.getCode() != BaseConstant.REQUEST_SUCCESS) throw new RPCException(result);
        Map<String, UserBaseInfoDTO> userMap = result.getData();
        if (userMap.size() == 0) return list;
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
        if (result == null) throw new WalletException(WalletEnums.SERVER_IS_TOO_BUSY);
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

    /***
     * 根据userId查询用户信息
     * @param userId
     * @return
     */
    private UserBaseInfoDTO selectUserByUserId(String userId) {
        ResultDTO<UserBaseInfoDTO> resultDTO = userFeign.selectUserInfoByUserName(userId);
        return resultDTO.getData();
    }
}
