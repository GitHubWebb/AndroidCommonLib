package com.framelibrary.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;

import com.file.zip.ZipEntry;
import com.file.zip.ZipFile;

/**
 * @author fanming
 *
 */
public class UpZIPUtils {


	public static void upZipFile(File file,String folderPath) throws IOException{
		if(StringUtils.isBlank(folderPath)){
			folderPath = file.getParent();
		}
		File fileFolder = new File(folderPath);
		if(!fileFolder.exists()){
			fileFolder.mkdirs();
		}
	    ZipFile zipFile = new ZipFile(file, "GBK");//设置压缩文件的编码方式为GBK
	    Enumeration<ZipEntry> entris = zipFile.getEntries();
	    ZipEntry zipEntry = null;
	    File tmpFile = null;
	    BufferedOutputStream bos = null;
	    InputStream is = null;
	    byte[] buf = new byte[1024];
	    int len = 0;
	    while (entris.hasMoreElements()) {
	        zipEntry = entris.nextElement();
	        String tmpFilePath = folderPath + File.separator + zipEntry.getName();
	        // 不进行文件夹的处理,些为特殊处理
	        tmpFile = new File(tmpFilePath);
	        if (zipEntry.isDirectory()) {//当前文件为目录
	            if (!tmpFile.exists()) {
	                tmpFile.mkdirs();
	            }
	        } else {
	        	String tmpfilePath = tmpFilePath.substring(0, tmpFilePath.lastIndexOf(File.separator));
	        	File tmpFileDir = new File(tmpfilePath);
	        	if(!tmpFileDir.exists()){
	        		tmpFileDir.mkdirs();
	        	}
	            if (!tmpFile.exists()) {
	                tmpFile.createNewFile();
	            }
	            is = zipFile.getInputStream(zipEntry);
	            bos = new BufferedOutputStream(new FileOutputStream(tmpFile));
	            while ((len = is.read(buf)) > 0) {
	                bos.write(buf, 0, len);
	            }
	            bos.flush();
	            bos.close();
	        }
	    }

	}
}  
