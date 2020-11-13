package com.blockchain.server.tron.mapper;

import com.blockchain.server.tron.entity.TronClearingCountDetail;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

/**
 * EthClearingCountDetail 数据访问类
 * @date 2019-02-16 15:44:06
 * @version 1.0
 */
@Repository
public interface TronClearingCountDetailMapper extends Mapper<TronClearingCountDetail> {
}