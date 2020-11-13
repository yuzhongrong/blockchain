package com.blockchain.server.ltc.mapper;

import com.blockchain.server.ltc.dto.WalletOutDTO;
import com.blockchain.server.ltc.entity.WalletOut;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * WalletOutMapper 数据访问类
 * @date 2019-02-16 15:08:16
 * @version 1.0
 */
@Repository
public interface WalletOutMapper extends Mapper<WalletOut> {
    List<WalletOutDTO> listByTokenSymbol(@Param("tokenSymbol") String tokenSymbol);
}