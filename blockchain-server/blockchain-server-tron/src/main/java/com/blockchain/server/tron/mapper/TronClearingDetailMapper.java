package com.blockchain.server.tron.mapper;

import com.blockchain.server.tron.entity.TronClearingDetail;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

/**
 * EthClearingDetailMapper 数据访问类
 * @date 2019-02-16 15:44:06
 * @version 1.0
 */
@Repository
public interface TronClearingDetailMapper extends Mapper<TronClearingDetail> {
}