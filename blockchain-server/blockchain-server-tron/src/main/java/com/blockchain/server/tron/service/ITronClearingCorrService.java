package com.blockchain.server.tron.service;

import com.blockchain.server.tron.entity.TronClearingCorr;

import java.util.List;

/**
 * 更正记录表——业务接口
 *
 * @author YH
 * @date 2019年2月16日17:09:19
 */
public interface ITronClearingCorrService {

    /**
     * 查询统计记录的更正记录
     *
     * @param totalId
     * @return
     */
    List<TronClearingCorr> selectByTotalId(String totalId);

    /**
     * 插入一条更正数据
     *
     * @param ethClearingCorr
     */
    void insert(TronClearingCorr ethClearingCorr);


}
