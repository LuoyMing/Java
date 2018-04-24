package com.seud.fyb.article.utils;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.wfw.common.utils.StringUtils;

public class CommonUtils {

    public static List<Integer> spiltIds(String ids) {
        List<Integer> idList = new ArrayList<>();
        if(StringUtils.isNotEmpty(ids)) {
            String [] idArray = ids.split(",");
            for (int i = 0; i < idArray.length; i++) {
                Integer id = Integer.valueOf(idArray[i]);
                idList.add(id);
            }
        }
        return idList;
    }
    
    public static List<String> spiltStringIds(String ids) {
        List<String> idList = new ArrayList<>();
        if(StringUtils.isNotEmpty(ids)) {
            String [] idArray = ids.split(",");
            for (int i = 0; i < idArray.length; i++) {
            	String id = String.valueOf(idArray[i]);
                idList.add(id);
            }
        }
        return idList;
    }
    
	public static String intListAsString(List<Integer> userIdList) {
		if (userIdList == null || userIdList.isEmpty()) {
			return "";
		}

		StringBuilder sb = new StringBuilder();
		int size = userIdList.size();
		for (int i = 0; i < size; i++) {
			sb.append(String.valueOf(userIdList.get(i)));
			if (i + 1 != size) {
				sb.append(",");
			}
		}
		return sb.toString();
	}

	public static String listToString(List<String> stringList) {
		if (stringList == null) {
			return null;
		}
		StringBuilder result = new StringBuilder();
		boolean flag = false;
		for (String string : stringList) {
			if (flag) {
				result.append(",");
			} else {
				flag = true;
			}
			result.append("'" + string + "'");
		}
		return result.toString();
	}
	
	public static String getPwd() {
		String pwd = "";
		char[] r = getChar();
		Random rr = new Random();
		char[] pw = new char[6];
		for (int i = 0; i < pw.length; i++) {
			int num = rr.nextInt(62);
			pw[i] = r[num];
			pwd = pwd + pw[i];
		}
		return pwd;
	}

	public static char[] getChar() {
		char[] passwordLit = new char[62];
		char fword = 'A';
		char mword = 'a';
		char bword = '0';
		for (int i = 0; i < 62; i++) {
			if (i < 26) {
				passwordLit[i] = fword;
				fword++;
			} else if (i < 52) {
				passwordLit[i] = mword;
				mword++;
			} else {
				passwordLit[i] = bword;
				bword++;
			} // 方法的抽取，按功能
				// System.out.println(passwordLit[i]);
		}
		return passwordLit;
	}
}
