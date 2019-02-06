package com.lwh.pedometer.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtils {

	/**
	 * 检测用户名格式的合法性
	 * @param username
	 * @return
	 */
	public static boolean checkUsername(String username) {
		if (username.length() > 2 && username.length() <= 20) {
			if (checkHasNoSensitiveWord(username)) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}
	
	public static boolean checkPassword(String password){
		if (password.length()>=6) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 检测是否包含敏感字符
	 * @param word
	 * @return
	 */
	private static boolean checkHasNoSensitiveWord(String word) {
		return true;
	}

	/**
	 * 检测电话号码格式的合法性
	 * @param phoneNum
	 * @return
	 */
	public static boolean checkPhoneNum(String phoneNum) {
		if (!checkEmpty(phoneNum)) {
			Pattern p = Pattern.compile("^1[3578]\\d{9}$");
			Matcher m = p.matcher(phoneNum);
			if (m.matches()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 检测年龄格式的合法性
	 * @param age
	 * @return
	 */
	public static boolean checkAge(short age) {
		if (age >= 0 && age <= 100) {
			return true;
		}
		return false;
	}

	/**
	 * 检测QQ号格式的合法性
	 * @param qq
	 * @return
	 */
	public static boolean checkQQ(String qq) {
		if (!checkEmpty(qq)) {
			Pattern p = Pattern.compile("^[1-9]\\d{4,9}$");
			Matcher m = p.matcher(qq);
			if (m.matches()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 空字符串检查
	 * @param text
	 * @return
	 */
	public static boolean checkEmpty(CharSequence text) {
		if ((text.equals("")) || (text == null)) {
			return true;
		}
		return false;
	}
	
	/**
	 * 判断两个文本是否相等
	 * @param text1  文本1
	 * @param text2  文本2
	 * @return
	 */
	public static boolean equalTo(String text1,String text2){
		if (text1.equals(text2)) {
			return true;
		}
		return false;
	}/**
	 * 通过一个InputStream获取内容
	 * @param inputStream
	 * @return
	 */
	public static String getString(FileInputStream inputStream) {
		InputStreamReader inputStreamReader = null;
		try {
			inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		BufferedReader reader = new BufferedReader(inputStreamReader);
		StringBuffer sb = new StringBuffer("");
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line);
				sb.append("\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	/**
	 * 通过txt文件的路径获取其内容
	 * @param path
	 * @return
	 */
	public static String getString(String path) {
		File file = new File(path);
		FileInputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return getString(fileInputStream);
	}
	/**
	 * 获取UUID
	 * @return 32UUID小写字符串
	 */
	public static String gainUUID(){
		String strUUID = UUID.randomUUID().toString();
		strUUID = strUUID.replaceAll("-", "").toLowerCase();
		return strUUID;
	}
	
	/**
	 * 判断字符串是否非空非null
	 * @param strParm 需要判断的字符串
	 * @return 真假
	 */
    public static boolean isNoBlankAndNoNull(String strParm)
    {
      return ! ( (strParm == null) || (strParm.equals("")));
    }
    
    /**
     * 将流转成字符串
     * @param is 输入流
     * @return
     * @throws Exception
     */
    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        return sb.toString();
    }

    /**
     * 将文件转成字符串
     * @param file 文件
     * @return
     * @throws Exception
     */
    public static String getStringFromFile(File file) throws Exception {
        FileInputStream fin = new FileInputStream(file);
        String ret = convertStreamToString(fin);
        //Make sure you close all streams.
        fin.close();
        return ret;
    }
    
}
