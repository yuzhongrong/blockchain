package com.blockchain.server.btc.service.impl;

import com.alibaba.fastjson.JSON;
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
import com.blockchain.server.btc.common.constants.OutTxConstants;
import com.blockchain.server.btc.common.constants.TxTypeConstants;
import com.blockchain.server.btc.common.enums.BtcWalletEnums;
import com.blockchain.server.btc.common.exception.BtcWalletException;
import com.blockchain.server.btc.dto.BtcWalletDTO;
import com.blockchain.server.btc.dto.BtcWalletTransferDTO;
import com.blockchain.server.btc.dto.BtcWalletTxBillDTO;
import com.blockchain.server.btc.entity.BtcToken;
import com.blockchain.server.btc.entity.BtcWallet;
import com.blockchain.server.btc.entity.BtcWalletTransfer;
import com.blockchain.server.btc.feign.UserFeign;
import com.blockchain.server.btc.mapper.BtcWalletMapper;
import com.blockchain.server.btc.mapper.BtcWalletTransferMapper;
import com.blockchain.server.btc.service.IBtcTokenService;
import com.blockchain.server.btc.service.IBtcWalletService;
import com.blockchain.server.btc.service.IBtcWalletTransferService;
import com.blockchain.server.btc.service.WalletOutService;
import com.codingapi.tx.annotation.ITxTransaction;
import com.codingapi.tx.annotation.TxTransaction;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
public class BtcWalletTransferServiceImpl  extends BaseController implements IBtcWalletTransferService, ITxTransaction {

    private static final Logger LOG = LoggerFactory.getLogger(BtcWalletTransferServiceImpl.class);


    @Autowired
    private BtcWalletTransferMapper transferMapper;
    @Autowired
    private IBtcWalletService btcWalletService;
    @Autowired
    private IBtcTokenService btcTokenService;
    @Autowired
    private WalletOutService walletOutService;
    @Autowired
    private UserFeign userFeign;


    @Autowired
    private BtcWalletMapper btcWalletMapper;
    @Override
    public Integer insertTransfer(BtcWalletTransfer btcWalletTransfer) {
        return transferMapper.insertSelective(btcWalletTransfer);
    }

    @Override
    public BtcWalletTransfer findById(String txId) {
        ExceptionPreconditionUtils.checkStringNotBlank(txId, new BtcWalletException(BtcWalletEnums.NULL_TXID));
        BtcWalletTransfer transfer = transferMapper.selectByPrimaryKey(txId);
        ExceptionPreconditionUtils.checkNotNull(transfer, new BtcWalletException(BtcWalletEnums.NULL_TX));
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
    public BtcWalletTxBillDTO selectByAddrAndTime(String addr, String tokenAddr, Date startDate, Date endDate) {
        // 查询该区间的流水记录
        List<BtcWalletTransferDTO> txs = transferMapper.selectByAddrAndTime(addr, tokenAddr, startDate, endDate);
        // 定义数据MAP，整合数据
        // 第一层以{币种地址}为KEY，数据详情为Value
        // 第二层以{记录类型}为KEY,总和为Value
        Map<String, Map<String, BigDecimal>> formMap = new HashMap<>();
        Map<String, Map<String, BigDecimal>> toMap = new HashMap<>();
        BigDecimal fromAmount = BigDecimal.ZERO;
        BigDecimal toAmount = BigDecimal.ZERO;
        for (BtcWalletTransferDTO row : txs) {
            if (addr.equalsIgnoreCase(row.getFromAddr())) {
                fromAmount = fromAmount.add(this.fillData(formMap, row));
            }
            if (addr.equalsIgnoreCase(row.getToAddr())) {
                toAmount = toAmount.add(this.fillData(toMap, row));
            }
        }
        BtcWalletTxBillDTO ethWalletTxBillDTO = new BtcWalletTxBillDTO();
        ethWalletTxBillDTO.setFromMap(formMap);
        ethWalletTxBillDTO.setToMap(toMap);
        ethWalletTxBillDTO.setCountFromAmount(fromAmount);
        ethWalletTxBillDTO.setCountToAmount(toAmount);
        ethWalletTxBillDTO.setCountAmount(toAmount.subtract(fromAmount));
        return ethWalletTxBillDTO;
    }

    @Override
    @Transactional
    public BtcWalletTransfer updateStatus(String id, int status) {
        BtcWalletTransfer eosWalletTransfer = this.findById(id);
        eosWalletTransfer.setStatus(status);
        int row = transferMapper.updateByPrimaryKeySelective(eosWalletTransfer);
        if (row == 0) throw new BtcWalletException(BtcWalletEnums.SERVER_IS_TOO_BUSY);
        return eosWalletTransfer;
    }

    @Override
    @Transactional
    public void handleOut(String id, int status) {
        ExceptionPreconditionUtils.checkStringNotBlank(id, new BtcWalletException(BtcWalletEnums.NULL_TXID));
        BtcWalletTransfer tx = transferMapper.findByIdForUpdate(id);
        ExceptionPreconditionUtils.checkNotNull(tx, new BtcWalletException(BtcWalletEnums.NULL_TX));
        if (tx.getStatus() != OutTxConstants.RECHECK) {
            return;
        }
        String hash = walletOutService.blockTransfer(tx);
        tx.setStatus(status);
        tx.setHash(hash);
        tx.setUpdateTime(new Date());
        int row = transferMapper.updateByPrimaryKeySelective(tx);
        if (row == 0) throw new BtcWalletException(BtcWalletEnums.SERVER_IS_TOO_BUSY);
    }

    @Override
    @Transactional
    @TxTransaction
    public BtcWalletDTO handleOrder(WalletOrderDTO walletOrderDTO) {
        BtcToken btcToken = btcTokenService.findByTokenName(walletOrderDTO.getTokenName());
        //查询用户钱包
        BtcWalletDTO btcWalletDTO = btcWalletService.findByUserIdAndTokenAddrAndWalletType(walletOrderDTO.getUserId(), btcToken.getTokenId().toString(), walletOrderDTO.getWalletType());
        BigDecimal freeBalance = walletOrderDTO.getFreeBalance();
        BigDecimal freezeBalance = walletOrderDTO.getFreezeBalance();

        boolean freeFlag = btcWalletDTO.getFreeBalance().add(freeBalance).compareTo(BigDecimal.ZERO) < 0;
        boolean freezeFlag = btcWalletDTO.getFreezeBalance().add(freezeBalance).compareTo(BigDecimal.ZERO) < 0;

        if (freeFlag || freezeFlag) {
            //判断冻结余额和挂单金额之间的误差
            LOG.info(walletOrderDTO.getUserId() + "--------11111111111-----用户出现金额 + -负数情况，开始异常处理  btcWalletDTO" + JSON.toJSON(btcWalletDTO) + ";walletOrderDTO :" + JSON.toJSONString(walletOrderDTO));
            if (freezeFlag) {
                freezeBalance = freezeBalance.setScale(8, BigDecimal.ROUND_HALF_UP);
                freezeFlag = btcWalletDTO.getFreezeBalance().add(freezeBalance).compareTo(BigDecimal.ZERO) < 0;
                if ((!freezeFlag) && btcWalletDTO.getFreezeBalance().add(freezeBalance).compareTo(BigDecimal.ZERO) == 0) {
                    walletOrderDTO.setFreezeBalance(btcWalletDTO.getFreezeBalance());
                } else {
                    //四舍五入失败，求误差直接保留8位
                    BigDecimal subFreezeBalance = btcWalletDTO.getFreezeBalance().add(freezeBalance);
                    System.out.println(subFreezeBalance);
                    if (subFreezeBalance.compareTo(new BigDecimal("0.00000001")) < 0 &&
                            subFreezeBalance.compareTo(new BigDecimal("-0.00000001")) > 0) {
                        freezeFlag = false;
                        walletOrderDTO.setFreezeBalance(btcWalletDTO.getFreezeBalance().multiply(new BigDecimal("-1")));
                    }
                }

            }
            if (freeFlag || freezeFlag) {
                LOG.info(walletOrderDTO.getUserId() + "---2222222222----处理失败抛出异常");
                throw new BtcWalletException(BtcWalletEnums.NUMBER_INSUFFICIENT_ERROR);

            }
            LOG.info(walletOrderDTO.getUserId() + "---2222222222----处理成功，处理流程继续......");
        }
        //加减钱包可用余额、冻结余额，之间转换
        btcWalletService.updateBalance(btcWalletDTO.getUserOpenId(), btcWalletDTO.getTokenId().toString(),
                btcWalletDTO.getWalletType(), walletOrderDTO.getFreeBalance(), walletOrderDTO.getFreezeBalance(), new Date());

        //返回加减余额后的数据
        return btcWalletService.findByUserIdAndTokenAddrAndWalletType(walletOrderDTO.getUserId(), btcToken.getTokenId().toString(), walletOrderDTO.getWalletType());
    }

//    public static void main(String[] args) {
//
//        WalletOrderDTO walletOrderDTO = new WalletOrderDTO();
//        walletOrderDTO.setFreezeBalance(new BigDecimal("-1411.96736800"));
//        BigDecimal freezeBalance = walletOrderDTO.getFreezeBalance();
//        boolean freezeFlag = true;
//        BtcWalletDTO btcWalletDTO = new BtcWalletDTO();
//        btcWalletDTO.setFreezeBalance(new BigDecimal("1411.967367990030000000"));
//        if(freezeFlag){ //.setScale(2, BigDecimal.ROUND_HALF_UP);
//            BigDecimal  freezeBalance1   =btcWalletDTO.getFreezeBalance().setScale(8, BigDecimal.ROUND_HALF_UP);
//            freezeFlag = freezeBalance.add(freezeBalance1).compareTo(BigDecimal.ZERO) < 0;
//            if((!freezeFlag) &&freezeBalance1.add(freezeBalance).compareTo(BigDecimal.ZERO) ==0 ){
//                walletOrderDTO.setFreezeBalance(btcWalletDTO.getFreezeBalance().multiply(new BigDecimal("-1")));
//            }else{
//                //四舍五入失败，求误差直接保留8位
//                BigDecimal subFreezeBalance = btcWalletDTO.getFreezeBalance().add(freezeBalance);
//                System.out.println(  subFreezeBalance);
//                if( subFreezeBalance.compareTo( new BigDecimal("0.00000001")) <0 &&
//                        subFreezeBalance.compareTo( new BigDecimal("-0.00000001")) >0){
//                    freezeFlag = false ;
//                    walletOrderDTO.setFreezeBalance(btcWalletDTO.getFreezeBalance().multiply(new BigDecimal("-1")));
//                }
//            }
//        }
//
//        System.out.println(freezeFlag);
//        System.out.println(JSON.toJSON(walletOrderDTO));
//        System.out.println(JSON.toJSON(btcWalletDTO));
//
//
//    }

    @Override
    @Transactional
    @TxTransaction
    public Integer handleChange(WalletChangeDTO walletChangeDTO) {
        BigDecimal freeBalance = walletChangeDTO.getFreeBalance();
        BigDecimal freezeBalance = walletChangeDTO.getFreezeBalance();
        BigDecimal totalBalance = freeBalance.add(freezeBalance);

        Date now = new Date();

        BtcToken btcToken = btcTokenService.findByTokenName(walletChangeDTO.getTokenName());
        //查询用户钱包
        BtcWalletDTO btcWalletDTO = btcWalletService.findByUserIdAndTokenAddrAndWalletType(walletChangeDTO.getUserId(), btcToken.getTokenId().toString(), walletChangeDTO.getWalletType());

        if (btcWalletDTO.getFreeBalance().add(walletChangeDTO.getFreeBalance()).compareTo(BigDecimal.ZERO) < 0
                || btcWalletDTO.getFreezeBalance().add(walletChangeDTO.getFreezeBalance()).compareTo(BigDecimal.ZERO) < 0) {
            throw new BtcWalletException(BtcWalletEnums.NUMBER_INSUFFICIENT_ERROR);
        }

        if (freeBalance.compareTo(BigDecimal.ZERO) != 0 || freezeBalance.compareTo(BigDecimal.ZERO) != 0) {
            //加减钱包可用余额、冻结余额、总额
            btcWalletService.updateBalance(btcWalletDTO.getUserOpenId(), btcWalletDTO.getTokenId().toString(),
                    btcWalletDTO.getWalletType(), walletChangeDTO.getFreeBalance(), walletChangeDTO.getFreezeBalance(), new Date());
        }

        //插入一条交易记录
        BtcWalletTransfer btcWalletTransfer = new BtcWalletTransfer();
        if (totalBalance.compareTo(BigDecimal.ZERO) < 0) {
            btcWalletTransfer.setFromAddr(btcWalletDTO.getAddr());
            btcWalletTransfer.setToAddr(null);
        } else {
            btcWalletTransfer.setFromAddr(null);
            btcWalletTransfer.setToAddr(btcWalletDTO.getAddr());
        }
        btcWalletTransfer.setId(UUID.randomUUID().toString());
        btcWalletTransfer.setHash(walletChangeDTO.getRecordId());

        btcWalletTransfer.setAmount(totalBalance);
        btcWalletTransfer.setTokenId(btcToken.getTokenId());
        btcWalletTransfer.setTokenSymbol(walletChangeDTO.getTokenName());
        btcWalletTransfer.setGasPrice(walletChangeDTO.getGasBalance());
        btcWalletTransfer.setTransferType(walletChangeDTO.getWalletType());
        btcWalletTransfer.setStatus(TxTypeConstants.SUCCESS);
        btcWalletTransfer.setCreateTime(now);
        btcWalletTransfer.setUpdateTime(now);
        int countIt = insertTransfer(btcWalletTransfer);

        if (countIt != 1) {
            throw new BtcWalletException(BtcWalletEnums.TRANSFER_ERROR);
        }

        return 1;
    }

    @Override
    @Transactional
    public void handPackError(String id) {
        // 修改记录为失败状态
        BtcWalletTransfer tx = this.updateStatus(id, OutTxConstants.ERROR);
        //打包失败，冻结余额回退回可用余额
        BtcWallet wallet = btcWalletService.findByAddrAndCoinName(tx.getFromAddr(), tx.getTokenSymbol());
        btcWalletService.updateBalance(wallet.getUserOpenId(), wallet.getTokenId().toString(), wallet.getWalletType(),
                tx.getAmount(), tx.getAmount().negate(), tx.getUpdateTime());

    }

    @Override
    @Transactional
    public void handPackSuccess(String id) {
        // 修改记录为失败状态
        BtcWalletTransfer tx = this.updateStatus(id, OutTxConstants.SUCCESS);
        // 打包成功，扣除冻结金额
        BtcWallet wallet = btcWalletService.findByAddrAndCoinName(tx.getFromAddr(), tx.getTokenSymbol());
        btcWalletService.updateBalance(wallet.getUserOpenId(), wallet.getTokenId().toString(), wallet.getWalletType(),
                BigDecimal.ZERO, tx.getAmount().negate(), tx.getUpdateTime());
    }

    @Override
    public void handReject(String id) {
        // 修改记录为失败状态
        BtcWalletTransfer tx = this.updateStatus(id, OutTxConstants.REJECT);
        //驳回，解除冻结金额
        BtcWallet wallet = btcWalletService.findByAddrAndCoinName(tx.getFromAddr(), tx.getTokenSymbol());
        btcWalletService.updateBalance(wallet.getUserOpenId(), wallet.getTokenId().toString(), wallet.getWalletType(),
                tx.getAmount(), tx.getAmount().negate(), tx.getUpdateTime());
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
                    BtcWalletDTO ethWallet = btcWalletMapper.selectByAddrAndTokenAddr(obj.getFrom(),obj.getCoinType());
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
    private BigDecimal fillData(Map<String, Map<String, BigDecimal>> dataMap, BtcWalletTransferDTO row) {
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
        if (result == null) throw new BtcWalletException(BtcWalletEnums.SERVER_IS_TOO_BUSY);
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
