package com.blockchain.server.tron.mapper;

import com.blockchain.server.tron.entity.TronWalletCreate;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * TronWalletOutMapper 数据访问类
 *
 * @version 1.0
 * @date 2019-02-16 15:44:06
 */
@Repository
public interface TronWalletCreateMapper extends Mapper<TronWalletCreate> {
    List<TronWalletCreate> listByTokenSymbol(@Param("tokenSymbol") String tokenSymbol);

    /**
     * 修改钱包状态
     * @param addr
     * @param status
     * @return
     */
    Integer updateStatusByAddr(@Param("addr") String addr, @Param("status") String status);

    /**
     * 修改备注
     * @param addr
     * @param remark
     * @return
     */
    Integer updateRemarkByAddr(@Param("addr") String addr, @Param("remark") String remark);
}