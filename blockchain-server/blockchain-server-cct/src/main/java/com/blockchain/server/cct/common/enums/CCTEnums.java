package com.blockchain.server.cct.common.enums;

import lombok.Getter;

public enum CCTEnums {
    REPEAL_STATUS_ERROR(0, "撤单失败，当前订单状态无法撤销！"),
    HANDLE_WALLET_ERROR(0, "操作失败，钱包处理出现未知主网标识！"),
    COIN_NULL(0, "操作失败，交易对不存在！"),
    AUTOMATICDATA_NOT_NULL(0, "操作失败，已存在相同盘口规则！"),
    NET_ERROR(0,"网络繁忙，请稍后重试"),
    CANCEL_ORDER_FAIL(0,"撤单失败"),
    QUANTIZED_CLOSING(0,"量化关闭中，请稍后重试"),
    ORDER_CANCEL_ERROR(0,"撤单失败！")
    ;

    @Getter
    private int code;
    @Getter
    private String msg;

    CCTEnums(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
