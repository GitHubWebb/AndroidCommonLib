package com.framelibrary.util;

import org.wlf.filedownloader.FileDownloader;
import org.wlf.filedownloader.listener.OnFileDownloadStatusListener;

/**
 * @author fanming
 *
 */
public class FileDownloadUtils {

	public static void download(String downloadUrl,OnFileDownloadStatusListener onFileDownloadStatusListener){
		FileDownloader.registerDownloadStatusListener(onFileDownloadStatusListener);
		FileDownloader.start(downloadUrl);
	}
	
}
