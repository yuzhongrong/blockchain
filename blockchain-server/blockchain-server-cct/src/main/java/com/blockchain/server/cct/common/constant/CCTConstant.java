package com.blockchain.server.cct.common.constant;

public class CCTConstant {

    //x 订单状态
    public static final String STATUS_NEW = "NEW"; //x 新建
    public static final String STATUS_MATCH = "MATCH"; //x 已撮合
    public static final String STATUS_FINISH = "FINISH"; //x 已完成
    public static final String STATUS_CANCEL = "CANCEL"; //x 撤销
    public static final String STATUS_CANCELING = "CANCELING"; //x 撤销中

    //公共配置状态
    public static final String STATUS_YES = "Y"; //可用
    public static final String STATUS_NO = "N"; //禁用

    //订单状态
    public static final String TYPE_BUY = "BUY"; //买单
    public static final String TYPE_SELL = "SELL"; //卖单

    //发布状态
    public static final String TYPE_MARKET = "MARKET"; //市价交易
    public static final String TYPE_LIMIT = "LIMIT"; //限价交易

    //交易类型
    public static final String TYPE_MAKER = "MAKER"; //挂单
    public static final String TYPE_TAKER = "TAKER"; //吃单

    //交易币对操作状态
    public static final String TYPE_ADD = "ADD"; //新增
    public static final String TYPE_DELETE = "DELETE"; //删除
    public static final String TYPE_UPDATE = "UPDATE"; //更新
    public static final String TYPE_START = "START"; //启用
    public static final String TYPE_DISABLE = "DISABLE"; //禁用
}
