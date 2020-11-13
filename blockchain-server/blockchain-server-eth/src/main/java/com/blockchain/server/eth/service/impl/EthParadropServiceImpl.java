package com.blockchain.server.eth.service.impl;


import com.blockchain.server.eth.common.constants.RedisKeyConstants;
import com.blockchain.server.eth.common.constants.StatusConstants;
import com.blockchain.server.eth.entity.EthParadrop;
import com.blockchain.server.eth.mapper.EthParadropMapper;
import com.blockchain.server.eth.service.IEthParadropService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class EthParadropServiceImpl implements IEthParadropService {
    @Autowired
    EthParadropMapper ethParadropMapper;
    @Autowired
    RedisTemplate redisTemplate;

    @Override
    public EthParadrop getInfo() {
        return ethParadropMapper.selectByPrimaryKey(1);
    }

    @Override
    @Transactional
    public EthParadrop updateInfo(BigDecimal sendCount, BigDecimal sendAmount) {
        EthParadrop ethParadrop = ethParadropMapper.selectByPrimaryKey(1);
        ethParadrop.setSendCount(sendCount);
        ethParadrop.setSendAmount(sendAmount);
        ethParadrop.setSendStatus(StatusConstants.N);
        ethParadropMapper.updateByPrimaryKeySelective(ethParadrop);
        redisTemplate.opsForValue().set(RedisKeyConstants.SEND_COUNT_AMOUNT, BigDecimal.ZERO);
        return ethParadrop;
    }

    @Override
    @Transactional
    public void updateStatus(String status) {
        EthParadrop ethParadrop = ethParadropMapper.selectByPrimaryKey(1);
        ethParadrop.setSendStatus(status);
        ethParadropMapper.updateByPrimaryKeySelective(ethParadrop);
        redisTemplate.opsForValue().set(RedisKeyConstants.SEND_COUNT_AMOUNT, BigDecimal.ZERO);
    }
}