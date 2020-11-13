package com.blockchain.server.ltc.mapper;

import com.blockchain.server.ltc.entity.ClearingTotal;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

/**
 * ClearingTotalMapper 数据访问类
 *
 * @version 1.0
 * @date 2019-02-16 15:44:06
 */
@Repository
public interface ClearingTotalMapper extends Mapper<ClearingTotal> {

    /**
     * 查询最新的资金记录
     *
     * @param addr        钱包地址
     * @param tokenSymbol 代币名称
     * @param walletType  钱包类型
     * @return
     */
    ClearingTotal selectNewestOne(@Param("addr") String addr,
                                  @Param("tokenSymbol") String tokenSymbol,
                                  @Param("walletType") String walletType);
}