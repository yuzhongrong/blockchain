package com.blockchain.server.eth.web3j.impl;

import com.alibaba.fastjson.JSONObject;
import com.blockchain.server.eth.common.config.EthConfig;
import com.blockchain.server.eth.common.constants.wallet.WalletTypeConstants;
import com.blockchain.server.eth.common.enums.EthWalletEnums;
import com.blockchain.server.eth.common.exception.EthWalletException;
import com.blockchain.server.eth.dto.web3j.Web3jWalletDTO;
import com.blockchain.server.eth.web3j.IFkWeb3j;
import com.blockchain.server.eth.web3j.IWalletWeb3j;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.*;
import org.web3j.protocol.ObjectMapperFactory;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 关于以太坊钱包操作的工具类
 */
@Component
public class IFkWeb3jImpl extends BaseWeb3jImpl implements IFkWeb3j {

    // 查询代币余额参数
    static final String BALANCE_OF = "balanceOf";
    // 代币销毁
    static final String CONSUME = "consume";
    // 托管钱包地址前缀
    static final String ADDRESS_HEADER = "0x";


    // ETH转账的手续费
    @Value("${wallert.eth.gasLimit}")
    BigInteger ETH_GAS_LIMIT;
    @Value("${wallert.eth.gasPrice}")
    BigInteger ETH_GAS_PRICE;
    // TOKEN转账的手续费
    @Value("${wallert.token.gasLimit}")
    BigInteger ETH_TOKEN_GAS_LIMIT;
    @Value("${wallert.token.gasPrice}")
    BigInteger ETH_TOKEN_GAS_PRICE;
    @Value("${wallert.consume.coinAdder}")
    String coinAdder;
    @Value("${wallert.consume.walletAddr}")
    String consumeAddr;
    @Value("${wallert.consume.walletPk}")
    String consumePk;

    public IFkWeb3jImpl(EthConfig ethConfig) {
        super(ethConfig);
    }


    @Override
    public void destruction() {
        String methodName = BALANCE_OF;
        List inputParameters = new ArrayList<>();
        List outputParameters = new ArrayList<>();
        Address address = new Address(consumeAddr);
        inputParameters.add(address);
        TypeReference<Uint256> typeReference = new TypeReference<Uint256>() {
        };
        outputParameters.add(typeReference);
        Function function = new Function(methodName, inputParameters, outputParameters);
        String data = FunctionEncoder.encode(function);
        Transaction transaction = Transaction.createEthCallTransaction(consumeAddr, coinAdder, data);
        try {
            EthCall ethCall = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).send();
            List<Type> results = FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters());
            BigInteger allFk = (BigInteger) results.get(0).getValue();
            if(allFk.compareTo(BigInteger.ZERO)==0){
                throw new EthWalletException(EthWalletEnums.DATA_EXCEPTION_ERROR);
            }
            List<Type> destructionParam = Arrays.asList(new Uint256(allFk));
            transact(consumeAddr, consumePk, coinAdder, CONSUME, ETH_TOKEN_GAS_PRICE, ETH_TOKEN_GAS_LIMIT, destructionParam);
        } catch (Exception e) {
            throw new EthWalletException(EthWalletEnums.SERVER_IS_TOO_BUSY);
        }

    }

}
