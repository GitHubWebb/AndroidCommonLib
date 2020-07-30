package com.framelibrary.util;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioRecord;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileUtil {

    /**
     * 错误日志文件
     */
    private static final String LOGFILE_NAME = "errorlog.txt";
    /**
     * 正常日志文件
     */
    private static final String LOGCAT_FILE_NAME = "logcat.txt";
    /**
     * 文件压缩成功
     */
    public static final int COMPRESS_NORMAL = 0;
    /**
     * 文件压缩异常
     */
    public static final int COMPRESS_EXCEPTION = 1;


    public static String readFileData(String filpath, String filename) {
        StringBuffer stringBuffer = new StringBuffer();
        // 判断存储设备是否已就绪
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return "";
        }
        BufferedReader bufferedReader = null;
        try {
            File dir = new File(Environment.getExternalStorageDirectory(), filpath);
            if (!dir.exists() || !dir.isDirectory()) {
                dir.mkdirs();
            }
            File file = new File(dir, filename);
            if (!file.exists() || !file.isFile()) {
                file.createNewFile();
                return "";
            }
            bufferedReader = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                    bufferedReader = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return stringBuffer.toString();
    }


    /**
     * 保存数据到文件
     *
     * @param excp
     */
    public static void saveDataToFile(String filepath, String filename, String data, boolean append) {
        // 判断存储设备是否已就绪
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return;
        }
        FileWriter fw = null;
        PrintWriter pw = null;
        try {
            // 创建目录
            final File dir = new File(Environment.getExternalStorageDirectory(), filepath);
            if (!dir.exists()) {
                dir.mkdirs();

            }
            // 打开文件
            final File logFile = new File(dir.getAbsolutePath(), filename);
            if (!logFile.isFile() || !logFile.exists()) {
                logFile.createNewFile();
            }
            fw = new FileWriter(logFile, append);
            pw = new PrintWriter(fw);
            pw.append(data);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (pw != null) {
                pw.close();
            }
            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 保存异常日志
     *
     * @param excp
     */
    public static void appendErrorLog(Throwable throwable, String logfileDirectory) {
        // 判断存储设备是否已就绪
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return;
        }
        FileWriter fw = null;
        PrintWriter pw = null;
        try {
            // 创建目录
            final File dir = new File(Environment.getExternalStorageDirectory(), logfileDirectory);
            if (!dir.exists()) {
                dir.mkdirs();

            }
            // 打开文件
            final File logFile = new File(dir.getAbsolutePath(), LOGFILE_NAME);
            if (!logFile.isFile() || !logFile.exists()) {
                logFile.createNewFile();
            }
            fw = new FileWriter(logFile, true);
            pw = new PrintWriter(fw);
            // 时间
            pw.println();
            pw.println(String.format("---- %s ----", DateUtils.formatDateHasMs(Calendar.getInstance().getTimeInMillis())));
            pw.append(throwable.toString());
            // 异常信息
            throwable.printStackTrace(pw);
            pw.println("---- <END> ----");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (pw != null) {
                pw.close();
            }
            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 创建并获取文件夹绝对路径
     *
     * @param dir
     * @param fileName
     * @return
     */
    public static String getStorageDirectory(String dir, String fileName) {
        // 判断存储设备是否已就绪
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return "";
        }

        File fileDir = new File(Environment.getExternalStorageDirectory(), dir);
        if (!fileDir.getParentFile().exists()) {
            fileDir.getParentFile().mkdirs();
        }
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        File file = new File(fileDir.getAbsoluteFile(), fileName);
        if (!file.isFile() || !file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return "";
            }
        }
        return file.getAbsolutePath();
    }

    //保存Bitmap到本地
    public static void saveMyBitmap(Activity activity, String bitName, Bitmap mBitmap) throws IOException {

        String storageDirectory = getStorageDirectory(Environment.DIRECTORY_PICTURES, bitName + ".png");
        File f = new File(storageDirectory);
        f.createNewFile();
        try {
            FileOutputStream out = new FileOutputStream(f);
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            Log.i("TAG", e.toString());
        }
    }


    //保存Raw到本地
    public static void saveMyRaw(Activity activity, String rawName, byte[] audiodata, int readsize, boolean mIsLoopExit) throws IOException {

        String storageDirectory = getStorageDirectory(Environment.DIRECTORY_MUSIC, rawName + ".raw");
        File file = new File(storageDirectory);
        if (file.exists()) file.delete();

        file.createNewFile();
        try {
            FileOutputStream out = new FileOutputStream(file);

            while (!mIsLoopExit) {
                if (AudioRecord.ERROR_INVALID_OPERATION != readsize) {

                    try {
                        Log.i("saveMyRaw", "writeDateTOFile readsize" + readsize);
                        //fos.write(audiodata);
                        out.write(audiodata, 0, readsize);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            Log.i("TAG", e.toString());
        }
    }


    /**
     * 是否正在删除文件
     */
    private static boolean isDeteling;

    /**
     * 删除过期日志，过期日志为30天
     *
     * @param dirFile 文件夹对象
     */
    private static void deteleExpireDirLogcat(File dirFile) {
        isDeteling = true;
        long currentTime = System.currentTimeMillis();
        File files[] = dirFile.listFiles();
        for (File file : files) {
            if (isNeedDetele(currentTime, file.lastModified())) {
                file.delete();
            }
        }
        isDeteling = false;
    }

    /**
     * 是否需要删除
     *
     * @param currentTime 当前时间
     * @param lastTime    文件最后修改时间
     * @return 文件是否是30天以前的
     */
    private static boolean isNeedDetele(long currentTime, long lastTime) {
        return (currentTime - lastTime) / 1000 >= 30 * 24 * 60 * 60;
    }


    public static void appendLogToFile(String dirPath, String fileName, String log) {
        // 判断存储设备是否已就绪
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return;
        }
        FileWriter fw = null;
        PrintWriter pw = null;
        try {
            // 创建目录
            final File dir = new File(Environment.getExternalStorageDirectory(), dirPath);
            if (!dir.exists()) {
                dir.mkdirs();

            }
            // 打开文件
            final File logFile = new File(dir.getAbsolutePath(), fileName);
            if (!logFile.isFile() || !logFile.exists()) {
                logFile.createNewFile();
            }
            fw = new FileWriter(logFile, true);
            pw = new PrintWriter(fw);
            // 时间
            //					pw.println();
            String tempLog = DateUtils.formatDateHasMs(System.currentTimeMillis()) + log + "\n";
            pw.append(new String(tempLog.getBytes(), "UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (pw != null) {
                pw.close();
            }
            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static boolean openDownApp(Activity activity, String authority, String downPath) {
        File file = new File(downPath);
        if (!file.exists()) return false;
        PermissionCheckUtils.checkIsAndroidO(activity);
        Intent install = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //判读版本是否在7.0以上 新的启动方法
            Uri apkUri = FileProvider.getUriForFile(activity, authority, new File(downPath));//在AndroidManifest中的android:authorities值
            install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            install.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            //以前的启动方法
            install.setDataAndType(Uri.fromFile(new File(downPath)), "application/vnd.android.package-archive");
            install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        activity.startActivity(install);
//        file.delete();
        return true;
    }

    /**
     * 添加日志到文件
     *
     * @param dirPath  文件夹路径
     * @param fileName 文件名称
     * @param log      日志
     */
    public static void appendToFile(String dirPath, String fileName, String log) {
        // 判断存储设备是否已就绪
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return;
        }
        String resultFileName = DateUtils.formatDatePattern(System.currentTimeMillis()) + fileName;
        FileWriter fw = null;
        PrintWriter pw = null;
        try {
            // 创建目录
            final File dir = new File(Environment.getExternalStorageDirectory(), dirPath);
            if (!dir.exists()) {
                dir.mkdirs();

            }
            if (!isDeteling) {
                deteleExpireDirLogcat(dir);
            }
            // 打开文件
            final File logFile = new File(dir.getAbsolutePath(), resultFileName);
            if (!logFile.isFile() || !logFile.exists()) {
                logFile.createNewFile();
            }
            fw = new FileWriter(logFile, true);
            pw = new PrintWriter(fw);
            // 时间
//			pw.println();
            String tempLog = DateUtils.formatDateHasMs(System.currentTimeMillis()) + log + "\n";
            pw.append(new String(tempLog.getBytes(), "UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (pw != null) {
                pw.close();
            }
            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static File createNewFile(String fileName) {
        // 判断存储设备是否已就绪
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return null;
        }
        File file = new File(Environment.getExternalStorageDirectory(), fileName);
        if (file.exists()) {
            file.delete();
        }
        return file;
    }


    /**
     * 添加log日志到本地文件
     *
     * @param log
     * @param logfileDirectory
     */
    public static void appendLogcatToFile(String log, String logfileDirectory) {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return;
        }
        String fileName = DateUtils.formatDatePattern(System.currentTimeMillis()) + LOGCAT_FILE_NAME;
        FileWriter fw = null;
        PrintWriter pw = null;
        try {
            // 创建目录
            final File dir = new File(Environment.getExternalStorageDirectory(), logfileDirectory);
            if (!dir.exists()) {
                dir.mkdirs();

            }
            if (!isDeteling) {
                deteleExpireDirLogcat(dir);
            }
            // 打开文件
            final File logFile = new File(dir.getAbsolutePath(), fileName);
            if (!logFile.isFile() || !logFile.exists()) {
                logFile.createNewFile();
            }
            fw = new FileWriter(logFile, true);
            pw = new PrintWriter(fw);
            // 时间
//			pw.println();
            String tempLog = DateUtils.formatDateHasMs(System.currentTimeMillis()) + log + "\n";
            pw.append(new String(tempLog.getBytes(), "UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (pw != null) {
                pw.close();
            }
            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static int count = 0;

    /**
     * 将传入路径中的文件或者文件夹所有的不包含zip的文件，压缩成zip文件
     *
     * @param zipPath  压缩后的zip存储路径+文件名
     * @param filePath 需要压缩的文件或者文件夹路径(如果是文件，需要指定文件名)
     * @return 压缩结果，如果为空或者""，则说明压缩成功，否则压缩失败
     */
    public static String compressZipFile(String zipPath, String filePath) {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return "存储设备不可用！";
        }
        String storageDirectory = Environment.getExternalStorageDirectory() + File.separator;
        File file = new File(storageDirectory + filePath);
        StringBuffer stringBuffer = new StringBuffer();
        File zipFile = new File(storageDirectory + zipPath);
        if (zipFile.exists()) {  // 如果zip文件存在，则删除
            zipFile.delete();
        }
        count = 0;
        ZipOutputStream zipOutputStream = null;
        try {
            zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFile));
            if (file.isDirectory()) {  // 如果是文件夹，则遍历文件夹中的所有不包含.zip的文件，并且压缩
                File[] fileList = file.listFiles();
                for (File files : fileList) {
                    if (files.getName().indexOf(".zip") < 0) {
                        int i = compressZipFile(zipOutputStream, files);
                        if (i != COMPRESS_NORMAL) {
                            stringBuffer.append(files.getName() + "文件压缩失败！");
                        }
                    } else {
                        stringBuffer.append("");
                    }
                }
            } else if (file.isFile()) {  // 如果是文件，直接压缩
                if (file.getName().indexOf(".zip") < 0) {
                    int i = compressZipFile(zipOutputStream, file);
                    if (i != COMPRESS_NORMAL) {
                        stringBuffer.append(file.getName() + "文件压缩失败！");
                    }
                }
            } else {
                stringBuffer.append("传入的参数有误！");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            stringBuffer.append("文件压缩失败！");
        } finally {
            try {
                if (zipOutputStream != null && count > 0) {
                    zipOutputStream.close();
                } else {
                    stringBuffer.append("没有日志文件，文件未压缩！");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return stringBuffer.toString();
    }


    /**
     * @param zipOutputStream zip输入流
     * @param file            需要压缩的文件
     * @return 压缩状态  0为压缩正常，1为压缩异常
     */
    private static int compressZipFile(ZipOutputStream zipOutputStream, File file) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            zipOutputStream.putNextEntry(new ZipEntry(file.getName()));
            byte[] bytes = new byte[inputStream.available()];
            int length = 0;
            while ((length = inputStream.read(bytes)) > 0) {
                zipOutputStream.write(bytes, 0, length);
            }
            count++;
            zipOutputStream.closeEntry();
            inputStream.close();
            return COMPRESS_NORMAL;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return COMPRESS_EXCEPTION;
    }


    public static String deteleDirectoryAllFile(String filePath) {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return "存储设备不可用！";
        }
        String storageDirectory = Environment.getExternalStorageDirectory() + File.separator;
        File files = new File(filePath.startsWith(storageDirectory) ? filePath : storageDirectory + filePath);
        if (files.isDirectory()) {
            File[] fileList = files.listFiles();
            for (File file : fileList) {
                file.delete();
            }
        } else {
            files.delete();
        }
        return "文件删除成功！";
    }


    public static void readFileList(File filePath, List<String> filelist) {
        if (filePath.isDirectory()) {
            File[] fileList = filePath.listFiles();
            if (fileList == null) {
            }
            for (File file2 : fileList) {
                readFileList(file2, filelist);
            }
        } else {
            filelist.add(filePath.getAbsolutePath());
        }
    }

    public static int CopySdcardFile(String fromFile, String toFile) {
        try {
            InputStream fosfrom = new FileInputStream(fromFile);
            OutputStream fosto = new FileOutputStream(toFile);
            byte bt[] = new byte[1024];
            int c;
            while ((c = fosfrom.read(bt)) > 0) {
                fosto.write(bt, 0, c);
            }
            fosfrom.close();
            fosto.close();
            return 0;

        } catch (Exception ex) {
            return -1;
        }
    }


}
