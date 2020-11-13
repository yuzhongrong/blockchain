package com.blockchain.server.tron.service.impl;


import com.blockchain.common.base.util.ExceptionPreconditionUtils;
import com.blockchain.common.base.util.RSACoderUtils;
import com.blockchain.server.tron.common.enums.TronWalletEnums;
import com.blockchain.server.tron.common.exception.TronWalletException;
import com.blockchain.server.tron.common.util.RedisPrivateUtil;
import com.blockchain.server.tron.common.util.RedisWalletAddrUtil;
import com.blockchain.server.tron.entity.TronWalletKey;
import com.blockchain.server.tron.mapper.TronWalletKeyMapper;
import com.blockchain.server.tron.service.ITronWalletKeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Set;

/**
 * 以太坊钱包表——业务接口
 *
 * @author YH
 * @date 2019年2月16日17:25:02
 */
@Service
public class TronWalletKeyServiceImpl implements ITronWalletKeyService {

    @Autowired
    TronWalletKeyMapper tronWalletKeyMapper;
    @Autowired
    RedisTemplate redisTemplate;

    @Override
    public TronWalletKey findByUserOpenId(String userOpenId) {
        TronWalletKey tronWalletKey = tronWalletKeyMapper.selectByPrimaryKey(userOpenId);
        return tronWalletKey;
    }

    @Override
    public TronWalletKey findByAddr(String addr) {
        TronWalletKey key = new TronWalletKey();
        key.setAddr(addr);
        TronWalletKey walletKey = tronWalletKeyMapper.selectOne(key);
        if (walletKey == null) {
            throw new TronWalletException(TronWalletEnums.INEXISTENCE_WALLET);
        }
        walletKey.setPrivateKey(RSACoderUtils.decryptPassword(walletKey.getPrivateKey())); // 获取解密后的私钥;
        return walletKey;
    }

    @Override
    public boolean existsPass(String userOpenId) {
        TronWalletKey tronWalletKey = tronWalletKeyMapper.selectByPrimaryKey(userOpenId);
        String keystore = tronWalletKey.getKeystore();
        return keystore != null;
    }

    @Override
    public TronWalletKey selectOne(TronWalletKey tronWalletKey) {
        TronWalletKey _tronWalletKey = tronWalletKeyMapper.selectOne(tronWalletKey);
        return _tronWalletKey;
    }

    @Override
    @Transactional
    public int insert(TronWalletKey tronWalletKey) {
        RedisWalletAddrUtil.setWalletAddr(tronWalletKey.getAddr(), redisTemplate);
        return tronWalletKeyMapper.insertSelective(tronWalletKey);
    }

    @Override
    @Transactional
    public TronWalletKey insert(String userOpenId) {
        TronWalletKey initRow = findByUserOpenId(userOpenId); // 查询钱包是否初始化过
        // 创建以太坊钱包
        TronWalletKey tronWalletKey = new TronWalletKey();
        Date date = new Date();
        tronWalletKey.setUserOpenId(userOpenId);
        /*Web3jWalletDTO wallet = walletWeb3j.creationTronWallet("123456");
        tronWalletKey.setAddr(wallet.getAddr());
        tronWalletKey.setPrivateKey(RSACoderUtils.encryptPassword(wallet.getPrivateKey())); // 私钥加密
        tronWalletKey.setUpdateTime(date);
        tronWalletKey.setCreateTime(date);*/
        int row = tronWalletKeyMapper.insertSelective(tronWalletKey);
        if (row == 0) {
            throw new TronWalletException(TronWalletEnums.SERVER_IS_TOO_BUSY);
        } else {
            RedisWalletAddrUtil.setWalletAddr(tronWalletKey.getAddr(), redisTemplate); // 存入缓存
            return tronWalletKey;
        }
    }

    @Override
    @Transactional
    public int update(TronWalletKey tronWalletKey) {
        return tronWalletKeyMapper.updateByPrimaryKey(tronWalletKey);
    }

    @Override
    public Set<String> selectAddrs() {
        Set<String> addrs = RedisWalletAddrUtil.getWalletAddrs(redisTemplate);
        if (addrs == null || addrs.size() == 0) {
            addrs = tronWalletKeyMapper.selectAllAddrs();
            RedisWalletAddrUtil.setWalletAddrs(addrs, redisTemplate);
        }
        return addrs;
    }

    /**
     * 通过私钥查询钱包私钥对象
     * @param fromHexAddr
     * @return
     */
    @Override
    public TronWalletKey findByHexAddr(String fromHexAddr) {
        TronWalletKey example = new TronWalletKey();
        example.setHexAddr(fromHexAddr);
        return tronWalletKeyMapper.selectOne(example);
    }

    /*@Override
    public String isPassword(String userOpenId, String password) {
        // 数据验证
        ExceptionPreconditionUtils.checkStringNotBlank(userOpenId,
                new TronWalletException(TronWalletEnums.NULL_USEROPENID));
        TronWalletKey tronWalletKey = tronWalletKeyMapper.selectByPrimaryKey(userOpenId);
        if (tronWalletKey == null) {
            throw new TronWalletException(TronWalletEnums.INEXISTENCE_WALLET);
        }
        if (tronWalletKey.getKeystore() == null) {
            throw new TronWalletException(TronWalletEnums.NOT_WALLETPASSWORD);
        }
        String _pass = getPassword(userOpenId, password);
        walletWeb3j.isPassword(tronWalletKey.getKeystore(), _pass);
        return tronWalletKey.getAddr();
    }*/

    /*@Override
    @Transactional
    public void updatePassword(String userOpenId, String password) {
        // 数据验证
        ExceptionPreconditionUtils.checkStringNotBlank(userOpenId,
                new TronWalletException(TronWalletEnums.NULL_USEROPENID));
        TronWalletKey tronWalletKey = tronWalletKeyMapper.selectByPrimaryKey(userOpenId);
        if (tronWalletKey == null) {
            throw new TronWalletException(TronWalletEnums.INEXISTENCE_WALLET);
        }
        // 重置密码 TODO
        String _pass = getPassword(userOpenId, password);
        String privateKey = RSACoderUtils.decryptPassword(tronWalletKey.getPrivateKey()); // 获取解密后的私钥
        Web3jWalletDTO walletDTO = walletWeb3j.updateTronWallet(privateKey, _pass);
        tronWalletKey.setKeystore(walletDTO.getKeystore());
        tronWalletKey.setUpdateTime(new Date());
        int row = tronWalletKeyMapper.updateByPrimaryKeySelective(tronWalletKey);
        if (row == 0) {
            throw new TronWalletException(TronWalletEnums.SERVER_IS_TOO_BUSY);
        }
    }*/

    /**
     * 获取解密后的密码
     *
     * @param userOpenId 用户ID
     * @param password   加密后的密码
     * @return
     */
    private String getPassword(String userOpenId, String password) {
        ExceptionPreconditionUtils.checkStringNotBlank(password, new TronWalletException(TronWalletEnums.NULL_PASSWORD));
        String key = RedisPrivateUtil.getPrivateKey(userOpenId, redisTemplate);    // 获取私钥
        String _pass = RSACoderUtils.decryptPassword(password, key);    // 获取解密后的密码
        return _pass;
    }
}
