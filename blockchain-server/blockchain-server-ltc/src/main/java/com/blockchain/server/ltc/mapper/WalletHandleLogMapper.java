package com.blockchain.server.ltc.mapper;

import com.blockchain.server.ltc.entity.WalletHandleLog;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

/**
 * WalletHandleLog 数据访问类
 *
 * @version 1.0
 * @date 2019-02-16 15:44:06
 */
@Repository
public interface WalletHandleLogMapper extends Mapper<WalletHandleLog> {
}