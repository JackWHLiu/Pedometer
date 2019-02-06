package com.lwh.pedometer.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;

/**
 * 文件工具
 * 
 * @author lwh
 */
public class FileUtils {

	/**
	 * 创建单个文件
	 * 
	 * @param file
	 *            一个目录
	 * @param name
	 *            文件名
	 * @return 是否创建成功
	 */
	public static boolean createFile(File file, String name) {
		if (file.isDirectory()) {
			try {
				return new File(file, name).createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		} else
			throw new IllegalStateException("The file exists.");
	}

	/**
	 * 创建单个文件夹
	 * 
	 * @param file
	 *            一个目录
	 * @param name
	 *            文件夹名
	 * @return 是否创建成功
	 */
	public static boolean createFolder(File file, String name) {
		if (file.isDirectory()) {
			return new File(file, name).mkdir();
		} else
			throw new IllegalStateException("The folder exists.");
	}

	/**
	 * 创建单个文件或文件夹
	 * 
	 * @param file
	 *            一个目录
	 * @param name
	 *            文件名或文件夹名
	 * @return 是否创建成功
	 */
	public static boolean create(File file, String name) {
		if (file.isDirectory()) {
			if (name.contains(".")) {
				return createFile(file, name);
			} else {
				return createFolder(file, name);
			}
		} else
			throw new IllegalStateException("The file or folder exists.");
	}

	/**
	 * 复制单个文件
	 * 
	 * @param file
	 *            一个具体的文件
	 * @param target
	 *            目标
	 * @return 是否复制成功
	 */
	public static boolean copyFile(File file, String target) {
		File targetFile = new File(target);
		if (!file.isFile() || !targetFile.isDirectory()) {
			return false;
		}
		try {
			InputStream inStream = new FileInputStream(file);
			OutputStream outStream = new BufferedOutputStream(
					new FileOutputStream(new File(targetFile, file.getName())));
			int len;
			byte[] buf = new byte[1024];
			while ((len = inStream.read(buf)) != -1) {
				outStream.write(buf, 0, len);
			}
			outStream.flush();
			if (outStream != null) {
				outStream.close();
			}
			if (inStream != null) {
				inStream.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 复制单个文件夹
	 * 
	 * @param file
	 *            文件夹
	 * @param target
	 *            目标
	 * @return 是否复制成功
	 */
	public static boolean copyFolder(File file, String target) {
		File targetFile = new File(target);
		if (!file.isDirectory() || !targetFile.isDirectory()) {
			return false;
		}
		if (file.list() != null) {
			String[] children = file.list();
			for (int i = 0; i < children.length; i++) {
				File newDirFile = new File(target + File.separator + file.getName());
				if (!newDirFile.exists()) {
					newDirFile.mkdir();
				}
				copy(new File(file, children[i]), target + File.separator + file.getName());
			}
		}
		return true;
	}

	/**
	 * 复制单个文件或文件夹
	 * 
	 * @param file
	 *            可能是一个文件，也可能是一个文件夹
	 * @param target
	 *            目标
	 * @return 是否复制成功
	 */
	public static boolean copy(File file, String target) {
		File targetFile = new File(target);
		if (!targetFile.isDirectory()) {
			return false;
		}
		if (file.isFile()) {
			return copyFile(file, target);
		} else {
			return copyFolder(file, target);
		}
	}

	/**
	 * 删除单个文件
	 * 
	 * @param file
	 *            一个文件
	 * @return 是否删除成功
	 */
	public static boolean deleteFile(File file) {
		if (!file.isFile()) {
			return false;
		}
		return file.delete();
	}

	/**
	 * 删除单个文件夹
	 * 
	 * @param file
	 *            一个文件夹
	 * @return 是否删除成功
	 */
	public static boolean deleteFolder(File file) {
		if (!file.isDirectory()) {
			return false;
		} else {
			if (file.list() != null) {
				String[] children = file.list();
				for (int i = 0; i < children.length; i++) {
					delete(new File(file, children[i]));
				}
			}
			return file.delete();
		}
	}

	/**
	 * 删除单个文件或文件夹
	 * 
	 * @param file
	 *            一个文件或文件夹
	 * @return 是否删除成功
	 */
	public static boolean delete(File file) {
		if (file.isFile()) {
			return deleteFile(file);
		} else {
			return deleteFolder(file);
		}
	}

	/**
	 * 剪切单个文件
	 * 
	 * @param file
	 *            一个文件
	 * @param target
	 *            目标
	 * @return 是否剪切成功
	 */
	public static boolean cutFile(File file, String target) {
		File targetFile = new File(target);
		if (!file.isFile() || !targetFile.isDirectory()) {
			return false;
		} else {
			copyFile(file, target);
			deleteFile(file);
			return true;
		}
	}

	/**
	 * 剪切单个文件夹
	 * 
	 * @param file
	 *            一个文件夹
	 * @param target
	 *            目标
	 * @return 是否剪切成功
	 */
	public static boolean cutFolder(File file, String target) {
		File targetFile = new File(target);
		if (!file.isDirectory() || !targetFile.isDirectory()) {
			return false;
		} else {
			if (copyFolder(file, target)) {
				return deleteFolder(file);
			} else {
				return false;
			}
		}
	}

	/**
	 * 剪切单个文件或文件夹
	 * 
	 * @param file
	 *            一个文件或文件夹
	 * @param target
	 *            目标
	 * @return 是否剪切成功
	 */
	public static boolean cut(File file, String target) {
		File targetFile = new File(target);
		if (!targetFile.isDirectory()) {
			return false;
		}
		if (file.isFile()) {
			return cutFile(file, target);
		} else {
			if (copy(file, target)) {
				return delete(file);
			} else {
				return false;
			}
		}
	}

	/**
	 * 重命名单个文件
	 * 
	 * @param file
	 *            一个文件
	 * @param name
	 *            新文件名
	 * @return 是否重命名成功
	 */
	public static boolean renameFile(File file, String name) {
		if (!file.isFile()) {
			return false;
		} else {
			String parent = file.getParent();
			return file.renameTo(new File(parent, name));
		}
	}

	/**
	 * 重命名单个文件夹
	 * 
	 * @param file
	 *            一个文件夹
	 * @param name
	 *            新文件夹名
	 * @return 是否重命名成功
	 */
	public static boolean renameFolder(File file, String name) {
		if (!file.isDirectory()) {
			return false;
		} else {
			String parent = file.getParent();
			return file.renameTo(new File(parent, name));
		}
	}

	/**
	 * 重命名单个文件或文件夹
	 * 
	 * @param file
	 *            一个文件或文件夹
	 * @param name
	 *            新文件名或新文件夹名
	 * @return 是否重命名成功
	 */
	public static boolean rename(File file, String name) {
		if (file.isFile()) {
			return renameFile(file, name);
		} else {
			return renameFolder(file, name);
		}
	}

	/** 格式化文件大小 */
	public static String formatFileSize(Context context, File file) {
		FileChannel fc = null;
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			fc = fis.getChannel();
			long size = fc.size();
			return Formatter.formatFileSize(context, size);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fc.close();
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return "未知";
	}

	/**
	 * 获取一个目录下的文件或文件夹数目
	 * 
	 * @param file
	 *            一个文件夹
	 * @return 一个目录下的文件或文件夹数目
	 */
	public static int getSubCount(File file) {
		if (!file.isDirectory()) {
			return Integer.MAX_VALUE;
		} else {
			return file.list().length;
		}
	}

	/** 获取机身内存 */
	public static String getRomTotalSize(Context context) {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getBlockCount();
		return Formatter.formatFileSize(context, blockSize * totalBlocks);
	}

	/** 获取机身可用内存 */
	public static String getRomAvailableSize(Context context) {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return Formatter.formatFileSize(context, blockSize * availableBlocks);
	}

	/**
	 * 检测SD卡是否准备就绪
	 * 
	 * @return
	 */
	public static boolean sdMounted() {
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 获取SD卡剩余容量（单位Byte）
	 * 
	 * @return
	 */
	public static long gainSDFreeSize() {
		if (sdMounted()) {
			// 取得SD卡文件路径
			File path = Environment.getExternalStorageDirectory();
			StatFs sf = new StatFs(path.getPath());
			// 获取单个数据块的大小(Byte)
			long blockSize = sf.getBlockSize();
			// 空闲的数据块的数量
			long freeBlocks = sf.getAvailableBlocks();

			// 返回SD卡空闲大小
			return freeBlocks * blockSize; // 单位Byte
		} else {
			return 0;
		}
	}

	/**
	 * 以行为单位读取文件内容，一次读一整行，常用于读面向行的格式化文件
	 * @param filePath
	 *            文件路径
	 */
	public static String readFileByLine(String filePath) throws IOException {
		BufferedReader reader = null;
		StringBuffer sb = new StringBuffer();
		try {
			reader = new BufferedReader(
					new InputStreamReader(new FileInputStream(filePath),System.getProperty("file.encoding")));
			String tempString = null;
			while ((tempString = reader.readLine()) != null) {
				sb.append(tempString);
				sb.append("\n");
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
		return sb.toString();
	}

	/**
	 * 写入文件
	 * @param filePath 文件路径
	 * @param content 文件内容
	 * @throws IOException
	 */
	public static void saveToFile(String filePath, String content) throws IOException {
		saveToFile(filePath, content, System.getProperty("file.encoding"));
	}

	public static void saveToFile(String filePath, String content, String encoding) throws IOException {
		BufferedWriter writer = null;
		File file = new File(filePath);
		try {
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), encoding));
			writer.write(content);
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

	/**
	 * 追加文本
	 * @param content
	 * @param file
	 * @throws IOException
	 */
	public static void appendToFile(String content, File file) throws IOException {
		appendToFile(content, file, System.getProperty("file.encoding"));
	}

	/**
	 * 追加文本
	 * @param content
	 * @param file
	 * @param encoding
	 * @throws IOException
	 */
	public static void appendToFile(String content, File file, String encoding) throws IOException {
		BufferedWriter writer = null;
		try {
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), encoding));
			writer.write(content);
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

	/**
	 * 判断文件是否存在
	 * 
	 * @param filePath
	 *            文件路径
	 * @return 是否存在
	 * @throws Exception
	 */
	public static Boolean isExsit(String filePath) {
		Boolean flag = false;
		try {
			File file = new File(filePath);
			if (file.exists()) {
				flag = true;
			}
		} catch (Exception e) {
			System.out.println("判断文件失败-->" + e.getMessage());
		}

		return flag;
	}

	/**
	 * 快速读取程序应用包下的文件内容
	 * 
	 * @param context
	 *            上下文
	 * @param filename
	 *            文件名称
	 * @return 文件内容
	 * @throws IOException
	 */
	public static String read(Context context, String filename) throws IOException {
		FileInputStream inStream = context.openFileInput(filename);
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		byte[] data = outStream.toByteArray();
		return new String(data);
	}

	/**
	 * 读取指定目录文件的文件内容
	 * 
	 * @param fileName
	 *            文件名称
	 * @return 文件内容
	 * @throws Exception
	 */
	public static String read(String fileName) throws IOException {
		FileInputStream inStream = new FileInputStream(fileName);
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		byte[] data = outStream.toByteArray();
		return new String(data);
	}

	/***
	 * 以行为单位读取文件内容，一次读一整行，常用于读面向行的格式化文件
	 * 
	 * @param fileName
	 *            文件名称
	 * @param encoding
	 *            文件编码
	 * @return 字符串内容
	 * @throws IOException
	 */
	public static String read(String fileName, String encoding) throws IOException {
		BufferedReader reader = null;
		StringBuffer sb = new StringBuffer();
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), encoding));
			String tempString = null;
			while ((tempString = reader.readLine()) != null) {
				sb.append(tempString);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				reader.close();
			}
		}

		return sb.toString();
	}

	/**
	 * 读取raw目录的文件内容
	 * 
	 * @param context
	 *            内容上下文
	 * @param rawFileId
	 *            raw文件名id
	 * @return
	 */
	public static String readRawValue(Context context, int rawFileId) {
		String result = "";
		try {
			InputStream is = context.getResources().openRawResource(rawFileId);
			int len = is.available();
			byte[] buffer = new byte[len];
			is.read(buffer);
			result = new String(buffer, "UTF-8");
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 读取assets目录的文件内容
	 * 
	 * @param context
	 *            内容上下文
	 * @param fileName
	 *            文件名称，包含扩展名
	 * @return
	 */
	public static String readAssetsValue(Context context, String fileName) {
		String result = "";
		try {
			InputStream is = context.getResources().getAssets().open(fileName);
			int len = is.available();
			byte[] buffer = new byte[len];
			is.read(buffer);
			result = new String(buffer, "UTF-8");
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 读取assets目录的文件内容
	 * 
	 * @param context
	 *            内容上下文
	 * @param fileName
	 *            文件名称，包含扩展名
	 * @return
	 */
	public static List<String> readAssetsListValue(Context context, String fileName) {
		List<String> list = new ArrayList<String>();
		try {
			InputStream in = context.getResources().getAssets().open(fileName);
			BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			String str = null;
			while ((str = br.readLine()) != null) {
				list.add(str);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 获取SharedPreferences文件内容
	 * 
	 * @param context
	 *            上下文
	 * @param fileNameNoExt
	 *            文件名称（不用带后缀名）
	 * @return
	 */
	public static Map<String, ?> readShrePerface(Context context, String fileNameNoExt) {
		SharedPreferences preferences = context.getSharedPreferences(fileNameNoExt, Context.MODE_PRIVATE);
		return preferences.getAll();
	}

	/**
	 * 写入SharedPreferences文件内容
	 * 
	 * @param context
	 *            上下文
	 * @param fileNameNoExt
	 *            文件名称（不用带后缀名）
	 * @param values
	 *            需要写入的数据Map(String,Boolean,Float,Long,Integer)
	 * @return
	 */
	public static void writeShrePerface(Context context, String fileNameNoExt, Map<String, ?> values) {
		try {
			SharedPreferences preferences = context.getSharedPreferences(fileNameNoExt, Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = preferences.edit();
			for (Iterator iterator = values.entrySet().iterator(); iterator.hasNext();) {
				Map.Entry<String, ?> entry = (Map.Entry<String, ?>) iterator.next();
				if (entry.getValue() instanceof String) {
					editor.putString(entry.getKey(), (String) entry.getValue());
				} else if (entry.getValue() instanceof Boolean) {
					editor.putBoolean(entry.getKey(), (Boolean) entry.getValue());
				} else if (entry.getValue() instanceof Float) {
					editor.putFloat(entry.getKey(), (Float) entry.getValue());
				} else if (entry.getValue() instanceof Long) {
					editor.putLong(entry.getKey(), (Long) entry.getValue());
				} else if (entry.getValue() instanceof Integer) {
					editor.putInt(entry.getKey(), (Integer) entry.getValue());
				}
			}
			editor.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 写入应用程序包files目录下文件
	 * 
	 * @param context
	 *            上下文
	 * @param fileName
	 *            文件名称
	 * @param content
	 *            文件内容
	 */
	public static void write(Context context, String fileName, String content) {
		try {

			FileOutputStream outStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
			outStream.write(content.getBytes());
			outStream.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 写入应用程序包files目录下文件
	 * 
	 * @param context
	 *            上下文
	 * @param fileName
	 *            文件名称
	 * @param content
	 *            文件内容
	 */
	public static void write(Context context, String fileName, byte[] content) {
		try {

			FileOutputStream outStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
			outStream.write(content);
			outStream.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 写入应用程序包files目录下文件
	 * 
	 * @param context
	 *            上下文
	 * @param fileName
	 *            文件名称
	 * @param modeType
	 *            文件写入模式（Context.MODE_PRIVATE、Context.MODE_APPEND、Context.
	 *            MODE_WORLD_READABLE、Context.MODE_WORLD_WRITEABLE）
	 * @param content
	 *            文件内容
	 */
	public static void write(Context context, String fileName, byte[] content, int modeType) {
		try {

			FileOutputStream outStream = context.openFileOutput(fileName, modeType);
			outStream.write(content);
			outStream.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 指定编码将内容写入目标文件
	 * 
	 * @param target
	 *            目标文件
	 * @param content
	 *            文件内容
	 * @param encoding
	 *            写入文件编码
	 * @throws Exception
	 */
	public static void write(File target, String content, String encoding) throws IOException {
		BufferedWriter writer = null;
		try {
			if (!target.getParentFile().exists()) {
				target.getParentFile().mkdirs();
			}
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(target, false), encoding));
			writer.write(content);

		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

	/**
	 * 将byte数据写入文件
	 * @param content
	 * @param filePath
	 * @throws IOException
	 */
	public static void write(byte[] content,String filePath) throws IOException {
		FileOutputStream fos = null;

		try {
			File file = new File(filePath);
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			fos = new FileOutputStream(file);
			fos.write(content);
			fos.flush();
		} finally {
			if (fos != null) {
				fos.close();
			}
		}
	}

	/**
	 * 将文件流数据写入文件
	 * @param inputStream
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public static File write(InputStream inputStream, String filePath) throws IOException {
		OutputStream outputStream = null;
		// 在指定目录创建一个空文件并获取文件对象
		File mFile = new File(filePath);
		if (!mFile.getParentFile().exists())
			mFile.getParentFile().mkdirs();
		try {
			outputStream = new FileOutputStream(mFile);
			byte buffer[] = new byte[4 * 1024];
			int lenght = 0;
			while ((lenght = inputStream.read(buffer)) > 0) {
				outputStream.write(buffer, 0, lenght);
			}
			outputStream.flush();
			return mFile;
		} catch (IOException e) {
			throw e;
		} finally {
			try {
				inputStream.close();
				outputStream.close();
			} catch (IOException e) {
			}
		}
	}

	/**
	 * 将Bitmap保存到jpeg图片中
	 * @param bitmap
	 * @param filePath
	 * @throws IOException
	 */
	public static void saveAsJpeg(Bitmap bitmap, String filePath) throws IOException {
		FileOutputStream fos = null;

		try {
			File file = new File(filePath);
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			fos = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
			fos.flush();
		} finally {
			if (fos != null) {
				fos.close();
			}
		}
	}

	/**
	 * 将Bitmap保存到png图片中
	 * @param bitmap
	 * @param filePath
	 * @throws IOException
	 */
	public static void saveAsPng(Bitmap bitmap, String filePath) throws IOException {
		FileOutputStream fos = null;
		try {
			File file = new File(filePath);
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			fos = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
			fos.flush();
		} finally {
			if (fos != null) {
				fos.close();
			}
		}
	}
}
