package com.vyagoo.faceid.util;

import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.util.Log;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;

import static android.os.Environment.getExternalStorageDirectory;

/**
 * SD卡相关的辅助类
 * 
 * @author zhy
 * 
 */
public class SDCardUtils
{
	private static final String TAG = "SDCardUtils";
    private static FileInputStream in;

    private SDCardUtils()
	{
		/* cannot be instantiated */
		throw new UnsupportedOperationException("cannot be instantiated");
	}

	/**
	 * 判断SDCard是否可用
	 * 
	 * @return
	 */
	public static boolean isSDCardEnable()
	{
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);

	}

	/**
	 * 获取SD卡路径
	 * 
	 * @return
	 */
	public static String getSDCardPath()
	{
		return getExternalStorageDirectory().getAbsolutePath()
				+ File.separator;
	}

	/**
	 * 获取SD卡的剩余容量 单位byte
	 * 
	 * @return
	 */
	public static long getSDCardAllSize()
	{
		if (isSDCardEnable())
		{
			StatFs stat = new StatFs(getSDCardPath());
			// 获取空闲的数据块的数量
			long availableBlocks = (long) stat.getAvailableBlocks() - 4;
			// 获取单个数据块的大小（byte）
			long freeBlocks = stat.getAvailableBlocks();
			return freeBlocks * availableBlocks;
		}
		return 0;
	}

	/**
	 * 获取指定路径所在空间的剩余可用容量字节数，单位byte
	 * 
	 * @param filePath
	 * @return 容量字节 SDCard可用空间，内部存储可用空间
	 */
	public static long getFreeBytes(String filePath)
	{
		// 如果是sd卡的下的路径，则获取sd卡可用容量
		if (filePath.startsWith(getSDCardPath()))
		{
			filePath = getSDCardPath();
		} else
		{// 如果是内部存储的路径，则获取内存存储的可用容量
			filePath = Environment.getDataDirectory().getAbsolutePath();
		}
		StatFs stat = new StatFs(filePath);
		long availableBlocks = (long) stat.getAvailableBlocks() - 4;
		return stat.getBlockSize() * availableBlocks;
	}

	/**
	 * 获取系统存储路径
	 * 
	 * @return
	 */
	public static String getRootDirectoryPath()
	{
		return Environment.getRootDirectory().getAbsolutePath();
	}

	public  static boolean isFileExist(String path){
		File file = new File(getSDCardPath() + path);
		return file.exists();
	}

	public static String getImagePath(){
		return  SDCardUtils.getSDCardPath()+"G360/Image/";
	}
	public static String getVideoPath(){
		return  SDCardUtils.getSDCardPath()+"G360/Video/";
	}

	public static String getGPath(){
		return  SDCardUtils.getSDCardPath()+"G360/G/";
	}


	/**
	 * 创建文件或文件夹
	 *
	 * @param fileName
	 *            文件名或问文件夹名
	 */
	public static void createFile(String fileName) {
		File file = new File(getSDCardPath() + fileName);
		if (fileName.indexOf(".") != -1) {
			// 说明包含，即使创建文件, 返回值为-1就说明不包含.,即使文件
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("创建了文件");
		} else {
			// 创建文件夹
			file.mkdir();
			System.out.println("创建了文件夹");
		}

	}


	/**
	 * 写文件
	 * @param content
	 * @return
	 */
	public static boolean write(String path, String content) {
		return write(path, content, false);
	}

	public static boolean write(String path, String content, boolean append) {
		return write(new File(path), content, append);
	}

	public static boolean write(File file, String content) {
		return write(file, content, false);
	}

	public static boolean write(File file, String content, boolean append) {
		if (file == null || TextUtils.isEmpty(content)) {
			return false;
		}
		if (!file.exists()) {
			file = createNewFile(file);
		}
		FileOutputStream ops = null;
		try {
			ops = new FileOutputStream(file, append);
			ops.write(content.getBytes());
		} catch (Exception e) {
			Log.e(TAG, "", e);
			return false;
		} finally {
			try {
				ops.close();
			} catch (IOException e) {
				Log.e(TAG, "", e);
			}
			ops = null;
		}
		return true;
	}

	/**
	 * ����ļ���?
	 *
	 * @param path
	 * @return
	 */
	public static String getFileName(String path) {
		if (TextUtils.isEmpty(path)) {
			return null;
		}
		File f = new File(path);
		String name = f.getName();
		f = null;
		return name;
	}

	/**
	 * 读文件
	 *
	 */
	public static List<String> readFile(File file, int startLine, int lineCount) {
		if (file == null || startLine < 1 || lineCount < 1) {
			return null;
		}
		if (!file.exists()) {
			return null;
		}
		FileReader fileReader = null;
		List<String> list = null;
		try {
			list = new ArrayList<String>();
			fileReader = new FileReader(file);
			LineNumberReader lnr = new LineNumberReader(fileReader);
			boolean end = false;
			for (int i = 1; i < startLine; i++) {
				if (lnr.readLine() == null) {
					end = true;
					break;
				}
			}
			if (end == false) {
				for (int i = startLine; i < startLine + lineCount; i++) {
					String line = lnr.readLine();
					if (line == null) {
						break;
					}
					list.add(line);
				}
			}
		} catch (Exception e) {
			Log.e(TAG, "read log error!", e);
		} finally {
			if (fileReader != null) {
				try {
					fileReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}
	/**
	 * 可以解决乱码问题
	 *
	 * @param fileName
	 * @return
	 */
	public static String readFile(String fileName, String code) {
        try {


//一次读多个字节

            byte[] tempbytes = new byte[120];

            int byteread = 0;

            in = new FileInputStream(fileName);

//            ReadFromFile.showAvailableBytes(in);

//读入多个字节到字节数组中，byteread为一次读入的字节数

            while ((byteread = in.read(tempbytes)) != -1){

//                System.out.write(tempbytes, 0, byteread);
            }

        } catch (Exception e1) {

            e1.printStackTrace();

        } finally {

            if (in != null){

                try {

                    in.close();

                } catch (IOException e1) {

                }

            }

        }
        return null;
    }


	/**
	 * 创建新文件
	 *
	 * @param file
	 * @return
	 */
	public static File createNewFile(File file) {
		try {
			if (file.exists()) {
				return file;
			}
			File dir = file.getParentFile();
			if (!dir.exists()) {
				dir.mkdirs();
			}
			if (!file.exists()) {
				file.createNewFile();
			}
		} catch (IOException e) {
			Log.e(TAG, "e=", e);
			return null;
		}
		return file;
	}
}
