package com.blockchain.server.ltc.service;

import com.blockchain.server.ltc.entity.ClearingCorr;

import java.util.List;

/**
 * 更正记录表——业务接口
 *
 * @author YH
 * @date 2019年2月16日17:09:19
 */
public interface IClearingCorrService {

    /**
     * 查询统计记录的更正记录
     *
     * @param totalId
     * @return
     */
    List<ClearingCorr> selectByTotalId(String totalId);

    /**
     * 插入一条更正数据
     *
     * @param clearingCorr
     */
    void insert(ClearingCorr clearingCorr);


}
