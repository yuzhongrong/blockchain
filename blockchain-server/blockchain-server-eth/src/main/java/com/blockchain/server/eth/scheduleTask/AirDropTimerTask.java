package com.blockchain.server.eth.scheduleTask;


import com.blockchain.server.eth.common.constants.RedisKeyConstants;
import com.blockchain.server.eth.common.constants.StatusConstants;
import com.blockchain.server.eth.common.util.DataCheckUtil;
import com.blockchain.server.eth.entity.EthParadrop;
import com.blockchain.server.eth.entity.EthParadropAddr;
import com.blockchain.server.eth.service.IEthParadropAddrService;
import com.blockchain.server.eth.service.IEthParadropService;
import com.blockchain.server.eth.service.IEthToAddrService;
import com.blockchain.server.eth.web3j.IWalletWeb3j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Random;

/**
 * 空投业务处理的定时器
 */
@Component
public class AirDropTimerTask {
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    IEthToAddrService ethToAddrService;
    @Autowired
    IEthParadropService ethParadropService;
    @Autowired
    IEthParadropAddrService ethParadropAddrService;
    @Autowired
    IWalletWeb3j walletWeb3j;

    /**
     * 发币
     */
    @Scheduled(cron = "0/5 * * * * ?")
    public void sendVoid() {
        EthParadrop ethParadrop = ethParadropService.getInfo();
        if (ethParadrop.getSendStatus().equalsIgnoreCase(StatusConstants.Y)) {
            // 需要发送的金额
            BigDecimal sendAmount = ethParadrop.getSendAmount().multiply(ratio());
            // 累计发送的金额
            BigDecimal sendCountAmount = BigDecimal.ZERO;
            if (redisTemplate.hasKey(RedisKeyConstants.SEND_COUNT_AMOUNT)) {
                sendCountAmount = (BigDecimal) redisTemplate.opsForValue().get(RedisKeyConstants.SEND_COUNT_AMOUNT);
            }
            if (sendCountAmount.compareTo(ethParadrop.getSendCount()) == 0) {
                return;
            }
            // 若累计发送金额与待发送金额超过总额，则剩下的全部发送出去；
            if (sendAmount.add(sendCountAmount).compareTo(ethParadrop.getSendCount()) >= 0) {
                redisTemplate.opsForValue().set(RedisKeyConstants.SEND_COUNT_AMOUNT, BigDecimal.ZERO);
                sendAmount = ethParadrop.getSendCount().subtract(sendCountAmount);
                ethParadropService.updateStatus(StatusConstants.N);
            } else {
                //增加累计发币数量
                redisTemplate.opsForValue().set(RedisKeyConstants.SEND_COUNT_AMOUNT, sendCountAmount.add(sendAmount));
            }
            // 获取账号
            EthParadropAddr ethParadropAddr = ethParadropAddrService.getEnable();
            String addr = ethToAddrService.inserWallet();
            BigInteger sendAmountInt = (sendAmount.multiply(BigDecimal.TEN.pow(18))).toBigInteger();
            String coinAddr = "0xf6fe745e5647298639072d942e0eb4f2e3930ecb";

            System.out.println("ethParadropAddr.getSendAddr() ===> "+ethParadropAddr.getSendAddr());
            System.out.println("ethParadropAddr.getSendPk() ===> "+ethParadropAddr.getSendPk());
            System.out.println("addr ===> "+addr);
            System.out.println("sendAmountInt ===> "+sendAmountInt);

            String hash = walletWeb3j.ethWalletTokenTransfer(ethParadropAddr.getSendAddr(), coinAddr, ethParadropAddr.getSendPk(),addr , sendAmountInt);
            System.out.println(hash);
        }
    }

    /**
     * 获取发放的百分比
     *
     * @return
     */
    private BigDecimal ratio() {
        Random random = new Random();
        int symbol = random.nextInt(100);
        Integer ratio = random.nextInt(51);
        BigDecimal rodalInfo = symbol % 2 == 0 ? new BigDecimal(ratio.toString()).divide(new BigDecimal("1000")) : new BigDecimal(ratio.toString()).negate().divide(new BigDecimal("1000"));
        BigDecimal ratioBig = BigDecimal.ONE.add(rodalInfo);
        System.out.println("ratioBig ===> "+ratioBig);
        return ratioBig;
    }

}

