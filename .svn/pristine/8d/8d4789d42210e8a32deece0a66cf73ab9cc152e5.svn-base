package com.framelibrary.event;

import com.framelibrary.event.*;

/**
 * 微信登录结果
 */

public class WechatLoginResultEvent extends InfoChangeEvent {

    private int resultCode;

    private String code;

    public static int status = 0; //0 login 1 bind

    public WechatLoginResultEvent(String code, int resultCode) {
        this.code = code;
        this.resultCode = resultCode;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

}
