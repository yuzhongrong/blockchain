package com.blockchain.server.eth.mapper;

import com.blockchain.common.base.dto.wallet.WalletParamsDTO;
import com.blockchain.server.eth.dto.wallet.EthPrivateBalanceDTO;
import com.blockchain.server.eth.entity.EthPrivateBalance;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Repository
public interface EthPrivateBalanceMapper extends Mapper<EthPrivateBalance> {

    /**
     * 查询私募资金列表
     */
    List<EthPrivateBalanceDTO> list(WalletParamsDTO paramsDTO);

    /**
     * 扣减私募资金
     */
    Integer deduct(@Param("id") String id, @Param("privateBalance") BigDecimal privateBalance, @Param("date") Date date);

}