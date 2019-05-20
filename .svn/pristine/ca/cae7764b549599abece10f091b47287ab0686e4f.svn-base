package com.framelibrary.util.down;

public interface DownListener {
    /**
     * 开始下载
     */
    void downStart();

    /***
     * 下载进度，和速度
     * @param progress
     * @param speed
     */
    void downProgress(int progress, long speed);

    /***
     * 下载完成
     * @param downUrl
     */
    void downSuccess(String downUrl);

    /***
     * 下载失败
     * @param failedDesc
     */
    void downFailed(String failedDesc);

}