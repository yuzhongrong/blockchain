package com.blockchain.server.eth.service.impl;


import com.blockchain.server.eth.common.constants.StatusConstants;
import com.blockchain.server.eth.common.enums.EthWalletEnums;
import com.blockchain.server.eth.common.exception.EthWalletException;
import com.blockchain.server.eth.entity.EthParadropAddr;
import com.blockchain.server.eth.mapper.EthParadropAddrMapper;
import com.blockchain.server.eth.service.IEthParadropAddrService;
import com.blockchain.server.eth.web3j.IWalletWeb3j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.crypto.Credentials;

import java.util.List;
import java.util.Random;

@Service
public class EthParadropAddrServiceImpl implements IEthParadropAddrService {

    @Autowired
    EthParadropAddrMapper ethParadropAddrMapper;

    @Override
    public EthParadropAddr getEnable() {
        List<EthParadropAddr> list = list();
        if (list.size() == 0) {
            throw new EthWalletException(EthWalletEnums.SERVER_IS_TOO_BUSY);
        } else {
            Random random = new Random();
            Integer ratio = random.nextInt(list.size());
            if (ratio > list.size()) {
                ratio = list.size() - 1;
            }
            System.out.println("ratio ===> "+ratio);
            return list.get(ratio);
        }
    }

    @Override
    public List<EthParadropAddr> list() {
        return ethParadropAddrMapper.selectAll();
    }

    @Override
    @Transactional
    public void updateStatusAndDesc(String addr, String status, String desc) {
        EthParadropAddr ethParadropAddr = ethParadropAddrMapper.selectByPrimaryKey(addr);
        ethParadropAddr.setStatus(status);
        ethParadropAddr.setDesc(desc);
        int row = ethParadropAddrMapper.updateByPrimaryKeySelective(ethParadropAddr);
        if (row == 0) throw new EthWalletException(EthWalletEnums.SERVER_IS_TOO_BUSY);
    }

    @Override
    @Transactional
    public void delectAddr(String addr) {
        int row = ethParadropAddrMapper.deleteByPrimaryKey(addr);
        if (row == 0) throw new EthWalletException(EthWalletEnums.SERVER_IS_TOO_BUSY);
    }

    @Override
    @Transactional
    public void inserWallet(String addr, String pk) {
        String address = Credentials.create(pk).getAddress();
        if (!addr.equalsIgnoreCase(address)) {
            throw new EthWalletException(EthWalletEnums.NULL_WALLETS);
        }
        EthParadropAddr ethParadropAddr = new EthParadropAddr();
        ethParadropAddr.setSendAddr(addr);
        ethParadropAddr.setSendPk(pk);
        ethParadropAddr.setStatus(StatusConstants.Y);
        int row = ethParadropAddrMapper.insertSelective(ethParadropAddr);
        if (row == 0) throw new EthWalletException(EthWalletEnums.SERVER_IS_TOO_BUSY);
    }
}