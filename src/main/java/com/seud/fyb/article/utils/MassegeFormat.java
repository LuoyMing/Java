package com.seud.fyb.article.utils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 模板内容填充
 * Created By luoyiming On 2017-08-09
 */
public class MassegeFormat {

	private static final Pattern pattern = Pattern.compile("\\#~(.*?)\\~#");
	private static Matcher matcher;

	/**
	 * 格式化字符串 字符串中使用#~key~#表示占位符
	 * @param sourStr  需要匹配的字符串
	 * @param param  参数集
	 * @return
	 */
	public static String stringFormat(String sourStr, Map<String, String> param) {
	    String tagerStr = sourStr;
	    if (param == null){
	    	 return tagerStr;
	    }
	    matcher = pattern.matcher(tagerStr);
	    while (matcher.find()) {
	        String key = matcher.group();
	        String keyclone = key.substring(2, key.length() - 2).trim();
	        Object value = param.get(keyclone);
	        if (value != null){
	            tagerStr = tagerStr.replace(key, value.toString());
	        }    
	    }
	    return tagerStr;
	}

	/**
	 * 格式化字符串 字符串中使用#~key~#表示占位符 利用反射 自动获取对象属性值 (必须有get方法)
	 * @param sourStr  需要匹配的字符串
	 * @param param  参数集
	 * @return
	 */
	public static String stringFormat(String sourStr, Object obj) {
	    String tagerStr = sourStr;
	    matcher = pattern.matcher(tagerStr);
	    if (obj == null){
	        return tagerStr;
	    }    
	    PropertyDescriptor pd;
	    Method getMethod;
	    // 匹配#~~#中间的内容 包括括号
	    while (matcher.find()) {
	        String key = matcher.group();
	        String keyclone = key.substring(2, key.length() - 2).trim();
	        try {
	            pd = new PropertyDescriptor(keyclone, obj.getClass());
	            getMethod = pd.getReadMethod();// 获得get方法
	            Object value = getMethod.invoke(obj);
	            if (value != null){
	                tagerStr = tagerStr.replace(key, value.toString());
	            }    
	        } catch (Exception e) {
	        	return null;
	        }
	    }
	    return tagerStr;
	}

	/**
	 * 格式化字符串 (替换所有) 字符串中使用#~key~#表示占位符
	 * @param sourStr  需要匹配的字符串
	 * @param param  参数集
	 * @return
	 */
	public static String stringFormatAll(String sourStr, Map<String, Object> param) {
	    String tagerStr = sourStr;
	    if (param == null){
	        return tagerStr;
	    }    
	    matcher = pattern.matcher(tagerStr);
	    while (matcher.find()) {
	        String key = matcher.group();
	        String keyclone = key.substring(2, key.length() - 2).trim();
	        Object value = param.get(keyclone);
	        if (value != null){
	            tagerStr = tagerStr.replace(key, value.toString());
	        }   
	    }
	    return tagerStr;
	}
}
