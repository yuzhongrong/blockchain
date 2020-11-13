package com.blockchain.server.tron.mapper;

import com.blockchain.server.tron.dto.wallet.TronWalletOutDto;
import com.blockchain.server.tron.entity.TronWalletOut;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * TronWalletOutMapper 数据访问类
 * @date 2019-02-16 15:44:06
 * @version 1.0
 */
@Repository
public interface TronWalletOutMapper extends Mapper<TronWalletOut> {
    List<TronWalletOutDto> listByTokenSymbol(@Param("tokenSymbol") String coinName, @Param("status") String status);
}