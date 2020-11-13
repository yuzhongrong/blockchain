package com.blockchain.server.tron.service;

import com.blockchain.server.tron.dto.wallet.ConfigWalletParamDto;
import com.blockchain.server.tron.entity.ConfigWalletParam;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author: Liusd
 * @create: 2019-03-22 11:52
 **/
public interface TronConfigWalletParamService {
    /** 
    * @Description: 提现手续费列表 
    * @Param: [] 
    * @return: java.util.List<com.blockchain.server.eth.dto.wallet.ConfigWalletParamDto> 
    * @Author: Liu.sd 
    * @Date: 2019/3/22 
    */ 
    List<ConfigWalletParamDto> tronConfigWalletParamList();

    /** 
    * @Description: 修改提现手续费
    * @Param: [gasPrice, minWdAmount, id] 
    * @return: void 
    * @Author: Liu.sd 
    * @Date: 2019/3/22 
    */ 
    void updateConfigWallet(BigDecimal gasPrice, BigDecimal minWdAmount, Integer id);

    /**
     * 查询资金归集钱包地址
     * @param tokenSymbol
     * @return
     */
    List<ConfigWalletParam> getCollectionWallet(String tokenSymbol);

    /**
     * 修改配置表归集地址
     * @param id
     * @param addr
     * @param describe
     * @return
     */
    Integer updateCollectionWallet(Integer id, String addr, String describe);
}
