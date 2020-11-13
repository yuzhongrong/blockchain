package com.blockchain.server.tron.service.impl;

import com.blockchain.common.base.util.RSACoderUtils;
import com.blockchain.server.tron.common.constants.TronConstant;
import com.blockchain.server.tron.common.enums.TronWalletEnums;
import com.blockchain.server.tron.common.exception.TronWalletException;
import com.blockchain.server.tron.entity.TronToken;
import com.blockchain.server.tron.entity.TronWalletCreate;
import com.blockchain.server.tron.mapper.TronWalletCreateMapper;
import com.blockchain.server.tron.service.ITronTokenService;
import com.blockchain.server.tron.service.TronWalletCreateService;
import com.blockchain.server.tron.service.TronWalletUtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * @author: Liusd
 * @create: 2019-03-27 09:27
 **/
@Service
public class TronWalletCreateServiceImpl implements TronWalletCreateService {

    @Autowired
    TronWalletCreateMapper tronWalletCreateMapper;
    @Autowired
    ITronTokenService tronTokenService;
    @Autowired
    private TronWalletUtilService tronWalletUtilService;

    @Override
    public List<TronWalletCreate> list(String status) {
        return tronWalletCreateMapper.listByTokenSymbol(status);
    }

    @Override
    public int insert(String addr, String tokenAddr, String privateKey, String remark) {
        TronToken tronToken = tronTokenService.findByTokenAddr(tokenAddr); // 获取币种信息
        String hexAddr = tronWalletUtilService.tryToHexAddr(addr);
        TronWalletCreate walletCreate = new TronWalletCreate();
        walletCreate.setId(UUID.randomUUID().toString());
        walletCreate.setAddr(addr);
        walletCreate.setHexAddr(hexAddr);
        walletCreate.setTokenAddr(tokenAddr);
        if (tronToken == null) throw new TronWalletException(TronWalletEnums.INEXISTENCE_WALLETTYPE);
        walletCreate.setTokenSymbol(tronToken.getTokenSymbol());
        walletCreate.setPrivateKey(RSACoderUtils.encryptPassword(privateKey));
        walletCreate.setRemark(remark);
        walletCreate.setStatus(TronConstant.WalletCreateStatus.DISABLE);
        return tronWalletCreateMapper.insert(walletCreate);
    }

    @Override
    public int delete(String id) {
        TronWalletCreate walletCreate = tronWalletCreateMapper.selectByPrimaryKey(id);
        if (walletCreate == null) {
            throw new TronWalletException(TronWalletEnums.INEXISTENCE_TX);
        }
        return tronWalletCreateMapper.delete(walletCreate);
    }

    /**
     * 修改钱包
     *
     * @param addr
     * @param remark
     * @param status
     * @return
     */
    @Override
    @Transactional
    public Integer update(String addr, String remark, String status) {
        // 获取可用钱包
        TronWalletCreate example = new TronWalletCreate();
        TronWalletCreate walletCreate = null;
        int row = 0;
        if (status.equalsIgnoreCase(TronConstant.WalletCreateStatus.USEABLE)) {
            example.setStatus(TronConstant.WalletCreateStatus.USEABLE);
            List<TronWalletCreate> createList = tronWalletCreateMapper.select(example);
            if (createList.size() > 1)
                throw new TronWalletException(TronWalletEnums.WALLET_CREATE_USEABLE_IS_NOT_ONLY);
            if (createList.size() == 1) {
                walletCreate = createList.get(0);
            }
            if (walletCreate == null) row = tronWalletCreateMapper.updateStatusByAddr(addr, status);
            else if (tronWalletCreateMapper.updateStatusByAddr(walletCreate.getAddr(), TronConstant.WalletCreateStatus.DISABLE) == 1)
                row = tronWalletCreateMapper.updateStatusByAddr(addr, status);
        } else {
            row = tronWalletCreateMapper.updateStatusByAddr(addr, status);
        }
        if (row != 1)
            throw new TronWalletException(TronWalletEnums.WALLET_CREATE_USEABLE_IS_EDIT_ERROR);

        // 修改备注
        tronWalletCreateMapper.updateRemarkByAddr(addr, remark);

        return row;
    }

}
