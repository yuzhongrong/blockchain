package com.blockchain.server.eth.service.impl;


import com.blockchain.server.eth.dto.web3j.Web3jWalletDTO;
import com.blockchain.server.eth.entity.EthToAddr;
import com.blockchain.server.eth.mapper.EthToAddrMapper;
import com.blockchain.server.eth.service.IEthToAddrService;
import com.blockchain.server.eth.web3j.IWalletWeb3j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EthToAddrServiceImpl implements IEthToAddrService {

    @Autowired
    IWalletWeb3j walletWeb3j;
    @Autowired
    EthToAddrMapper ethToAddrMapper;

    @Override
    public String inserWallet() {
        Web3jWalletDTO web3jWalletDTO = walletWeb3j.creationEthWallet();
        EthToAddr ethToAddr = new EthToAddr();
        ethToAddr.setAddr(web3jWalletDTO.getAddr());
        ethToAddr.setPk(web3jWalletDTO.getPrivateKey());
        ethToAddrMapper.insertSelective(ethToAddr);
        return web3jWalletDTO.getAddr();
    }
}