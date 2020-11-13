package com.blockchain.server.eth.mapper;

import com.blockchain.server.eth.dto.wallet.EthWalletOutDto;
import com.blockchain.server.eth.entity.EthParadrop;
import com.blockchain.server.eth.entity.EthWalletOut;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * EthParadropMapper 数据访问类
 * @date 2019-02-16 15:44:06
 * @version 1.0
 */
@Repository
public interface EthParadropMapper extends Mapper<EthParadrop> {
}