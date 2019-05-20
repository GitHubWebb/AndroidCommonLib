package com.framelibrary.event;

import com.framelibrary.event.*;

/**
 * Alipay支付结果更改动作
 *
 * @author wangwx
 */

public class PayResultChangeEvent extends InfoChangeEvent {
    public static final int SDK_AliPAY_FLAG = 1; //支付宝支付标记
    public static final int SDK_WechatPAY_FLAG = 2; //微信支付标记
    public static final int ALERT_PAY_FLAY = 3; //弹窗支付标记

    private final int type;

    private final String resultStatus; //返回结果

    public PayResultChangeEvent(int type, String resultStatus) {
        this.type = type;
        this.resultStatus = resultStatus;
    }

    public int getType() {
        return type;
    }

    public String getResultStatus() {
        return resultStatus;
    }
}
