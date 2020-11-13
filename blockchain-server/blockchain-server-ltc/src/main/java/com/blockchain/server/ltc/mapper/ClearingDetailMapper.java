package com.blockchain.server.ltc.mapper;

import com.blockchain.server.ltc.entity.ClearingDetail;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

/**
 * ClearingDetailMapper 数据访问类
 * @date 2019-02-16 15:44:06
 * @version 1.0
 */
@Repository
public interface ClearingDetailMapper extends Mapper<ClearingDetail> {
}