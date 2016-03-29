/**
 * 2015 Hangzhou LongKui software Inc. All rights reserved.
 */
package com.niuchart.demo;

import android.content.Context;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IOUtility {
    private static final String TAG = IOUtility.class.getSimpleName();

    public static void copyFile(Context context, String filename, String copyToExternalBaseDir) {
        InputStream in;
        OutputStream out;
        String newFileName = null;
        try {
            in = context.getAssets().open(filename);
            newFileName = copyToExternalBaseDir + File.separator + filename;
            out = new FileOutputStream(newFileName);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
        } catch (Exception e) {
            Log.e(TAG, "Exception in copyFileFromAsset() of " + newFileName);
            Log.e(TAG, "Exception in copyFileFromAsset() " + e.toString());
        }
    }

    public static void copyFileOrDir(Context context, String path, String copyToExternalBaseDir) {
        String assets[] = null;
        try {
            assets = context.getAssets().list(path);
            if (assets.length == 0) {
                copyFile(context, path, copyToExternalBaseDir);
            } else {
                String fullPath = copyToExternalBaseDir + File.separator + path;
                File dir = new File(fullPath);
                if (!dir.exists())
                    if (!dir.mkdirs()) ;
                for (int i = 0; i < assets.length; ++i) {
                    String p;
                    if (path.equals("")) {
                        p = "";
                    } else {
                        p = path + "/";
                    }
                    copyFileOrDir(context, p + assets[i], copyToExternalBaseDir);
                }
            }
        } catch (IOException ex) {
            Log.e(TAG, "I/O Exception", ex);
        }
    }

    public static byte[] readBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    /**
     * 把该目录下的文件以及文件夹全删掉
     *
     * @param fileOrDirectory
     */
    public static void deleteFolderOrFile(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles()) {
                deleteFolderOrFile(child);
            }
        deleteFileSafely(fileOrDirectory);
    }

    /**
     * 安全删除文件.
     *
     * @param file
     * @return
     */
    public static boolean deleteFileSafely(File file) {
        if (file != null) {
            String tmpPath = file.getParent() + File.separator + System.currentTimeMillis();
            File tmp = new File(tmpPath);
            file.renameTo(tmp);
            return tmp.delete();
        }
        return false;
    }
}
