package com.blockchain.server.tron.mapper;

import com.blockchain.server.tron.entity.TronToken;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.Set;

/**
 * EthTokenMapper 数据访问类
 *
 * @version 1.0
 * @date 2019-02-16 15:44:06
 */
@Repository
public interface TronTokenMapper extends Mapper<TronToken> {

    /**
     * 获取所有的币种地址
     *
     * @return
     */
    Set<String> selectAllTokenAddr();
}