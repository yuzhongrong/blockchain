package com.blockchain.server.tron.inner;

/**
 * @author Harvey
 * @date 2019/2/19 18:10
 * @user WIN10
 */
public class TronTransferInnerApi {
    public static final String TRON_WALLET_API = "钱包控制器";

    public static class Order {
        public static final String MATHOD_API_NAME = "冻结与解冻接口";
        public static final String MATHOD_API_NOTE = "冻结与解冻接口";

        public static final String MATHOD_API_WALLET_ORDER_DTO = "对象参数";
    }

    public static class Change {
        public static final String MATHOD_API_NAME = "钱包余额变动接口";
        public static final String MATHOD_API_NOTE = "钱包余额变动接口";

        public static final String MATHOD_API_WALLET_CHANGE_DTO = "对象参数";
    }

}
