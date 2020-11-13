package com.blockchain.server.eth.service.impl;

import com.blockchain.common.base.constant.BaseConstant;
import com.blockchain.common.base.dto.ResultDTO;
import com.blockchain.common.base.dto.user.UserBaseInfoDTO;
import com.blockchain.common.base.dto.wallet.WalletParamsDTO;
import com.blockchain.common.base.exception.RPCException;
import com.blockchain.common.base.util.ExceptionPreconditionUtils;
import com.blockchain.server.eth.common.constants.tx.TxTypeConstants;
import com.blockchain.server.eth.common.enums.EthWalletEnums;
import com.blockchain.server.eth.common.exception.EthWalletException;
import com.blockchain.server.eth.dto.wallet.EthPrivateBalanceDTO;
import com.blockchain.server.eth.entity.EthPrivateBalance;
import com.blockchain.server.eth.feign.UserFeign;
import com.blockchain.server.eth.mapper.EthPrivateBalanceMapper;
import com.blockchain.server.eth.service.IEthPrivateBalanceService;
import com.blockchain.server.eth.service.IEthWalletService;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
public class EthPrivateBalanceServiceImpl implements IEthPrivateBalanceService {

    @Autowired
    private EthPrivateBalanceMapper privateBalanceMapper;
    @Autowired
    private IEthWalletService walletService;
    @Autowired
    private UserFeign userFeign;

    @Override
    @Transactional
    public void insert(EthPrivateBalance privateBalance) {
        //校验参数
        ExceptionPreconditionUtils.notEmpty(privateBalance.getAddr(), privateBalance.getTokenSymbol(), privateBalance.getUserOpenId(),
                privateBalance.getPrivateBalance(), privateBalance.getWalletType());

        //新增私募资金
        privateBalance.setId(UUID.randomUUID().toString());
        if (privateBalance.getReleaseBalance() == null) {
            privateBalance.setReleaseBalance(privateBalance.getPrivateBalance().multiply(new BigDecimal("0.01")));
        }
        privateBalance.setCreateTime(new Date());
        privateBalance.setModifyTime(privateBalance.getCreateTime());
        privateBalanceMapper.insertSelective(privateBalance);

        //增加用户冻结余额
        walletService.updateBlance(privateBalance.getAddr(), privateBalance.getTokenSymbol(), "0",
                privateBalance.getPrivateBalance().toPlainString(), TxTypeConstants.PRIVATE_BALANCE);
    }

    @Override
    public List<EthPrivateBalanceDTO> list(WalletParamsDTO paramsDTO, Integer pageNum, Integer pageSize) {
        if (paramsDTO != null && StringUtils.isNotEmpty(paramsDTO.getUserName())) {
            UserBaseInfoDTO userBaseInfoDTO = conditionsUserId(paramsDTO.getUserName());
            //用户不存在，返回空
            if (userBaseInfoDTO == null) return null;
            //用户存在，添加条件
            paramsDTO.setUserId(userBaseInfoDTO.getUserId());
        }

        PageHelper.startPage(pageNum, pageSize);
        List<EthPrivateBalanceDTO> privateBalanceDTOList = privateBalanceMapper.list(paramsDTO);

        Set<String> userIds = new HashSet<>();
        for (EthPrivateBalanceDTO pb : privateBalanceDTOList) {
            userIds.add(pb.getUserOpenId());
        }
        //获取用户信息
        ResultDTO<Map<String, UserBaseInfoDTO>> result = userFeign.userInfos(userIds);
        if (result == null) throw new EthWalletException(EthWalletEnums.SERVER_IS_TOO_BUSY);
        if (result.getCode() != BaseConstant.REQUEST_SUCCESS) throw new RPCException(result);
        Map<String, UserBaseInfoDTO> userMap = result.getData();
        //填充用户信息
        for (EthPrivateBalanceDTO pb : privateBalanceDTOList) {
            pb.setUserBaseInfo(userMap.get(pb.getUserOpenId()));
        }
        return privateBalanceDTOList;
    }

    @Override
    @Transactional
    public void deduct(EthPrivateBalance privateBalance) {
        EthPrivateBalance before = privateBalanceMapper.selectByPrimaryKey(privateBalance.getId());
        if (before == null) return;
        if (privateBalance.getPrivateBalance() == null) return;
        if (privateBalance.getPrivateBalance().compareTo(before.getPrivateBalance()) > 0) {
            throw new EthWalletException(EthWalletEnums.PRIVATEBALANCE_NOT_ENOUGH);
        }

        //扣减私募资金
        walletService.updateBlance(privateBalance.getAddr(), privateBalance.getTokenSymbol(), "0",
                privateBalance.getPrivateBalance().negate().toPlainString(), TxTypeConstants.PRIVATE_BALANCE);

        //扣减私募资金
        privateBalanceMapper.deduct(privateBalance.getId(), privateBalance.getPrivateBalance().negate(), new Date());
    }


    /**
     * @Description: 根据手机号获取用户信息
     * @Param: [userName]
     * @return: com.blockchain.common.base.dto.user.UserBaseInfoDTO
     * @Author: Liu.sd
     * @Date: 2019/3/23
     */
    private UserBaseInfoDTO conditionsUserId(String userName) {
        //获取用户信息
        ResultDTO<UserBaseInfoDTO> result = userFeign.selectUserInfoByUserName(userName);
        if (result == null) throw new EthWalletException(EthWalletEnums.SERVER_IS_TOO_BUSY);
        if (result.getCode() != BaseConstant.REQUEST_SUCCESS) throw new RPCException(result);
        return result.getData();
    }

}
