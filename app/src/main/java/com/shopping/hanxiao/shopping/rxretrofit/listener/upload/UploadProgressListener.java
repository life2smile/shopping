package com.shopping.hanxiao.shopping.rxretrofit.listener.upload;

/**
 * 上传进度回调类
 */

public interface UploadProgressListener {
    /**
     * 上传进度
     * @param currentBytesCount
     * @param totalBytesCount
     */
    void onProgress(long currentBytesCount, long totalBytesCount);
}