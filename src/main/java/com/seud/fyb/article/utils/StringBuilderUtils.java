package com.seud.fyb.article.utils;

import com.wfw.common.utils.StringUtils;


public class StringBuilderUtils {
	public static final String T_TITLE = "{{title}}";
	public static final String S_TITLE = "{{title-show}}";
	public static final String E_TITLE = "{{/title-show}}";

	public static final String T_TIME = "{{time}}";
	public static final String S_TIME = "{{time-show}}";
	public static final String E_TIME = "{{/time-show}}";

	public static final String T_SOURCE = "{{source}}";
	public static final String S_SOURCE = "{{source-show}}";
	public static final String E_SOURCE = "{{/source-show}}";

	public static final String T_WRITER = "{{writer}}";
	public static final String S_WRITER = "{{writer-show}}";
	public static final String E_WRITER = "{{/writer-show}}";

	public static final String T_DIGEST = "{{digest}}";
	public static final String S_DIGEST = "{{digest-show}}";
	public static final String E_DIGEST = "{{/digest-show}}";

	public static final String T_CONTENT = "{{content}}";
	public static final String S_CONTENT = "{{content-show}}";
	public static final String E_CONTENT = "{{/content-show}}";

	public static final String T_IMG = "{{img}}";
	public static final String S_IMG = "{{img-show}}";
	public static final String E_IMG = "{{/img-show}}";

	public static final String T_TRANSACTION = "{{transaction}}";
	public static final String S_TRANSACTION = "{{transaction-show}}";
	public static final String E_TRANSACTION = "{{/transaction-show}}";

	public static final String T_CODE = "{{code}}";
	public static final String S_CODE = "{{code-show}}";
	public static final String E_CODE = "{{/code-show}}";
	
	public static final String T_BREED = "{{breed}}";
	public static final String S_BREED = "{{breed-show}}";
	public static final String E_BREED = "{{/breed-show}}";

	public static final String T_FILENAME = "{{filename}}";
	public static final String S_FILENAME = "{{filename-show}}";
	public static final String E_FILENAME = "{{/filename-show}}";

	public static StringBuilder replaceForLaber(StringBuilder stringBuilder, String title, String time, String source,
			String writer,String digest,String content,String img,String transaction,String code,String breed,String fileName) {
		stringBuilder = replaceLaberOne(stringBuilder, T_TITLE, S_TITLE, E_TITLE, title);
		stringBuilder = replaceLaberOne(stringBuilder, T_TIME, S_TIME, E_TIME, time);
		stringBuilder = replaceLaberOne(stringBuilder, T_SOURCE, S_SOURCE, E_SOURCE, source);
		stringBuilder = replaceLaberOne(stringBuilder, T_WRITER, S_WRITER, E_WRITER, writer);
		stringBuilder = replaceLaberOne(stringBuilder, T_DIGEST, S_DIGEST, E_DIGEST, digest);
		stringBuilder = replaceLaberOne(stringBuilder, T_CONTENT, S_CONTENT, E_CONTENT, content);
		stringBuilder = replaceLaberOne(stringBuilder, T_IMG, S_IMG, E_IMG, img);
		stringBuilder = replaceLaberOne(stringBuilder, T_TRANSACTION, S_TRANSACTION, E_TRANSACTION, transaction);
		stringBuilder = replaceLaberOne(stringBuilder, T_CODE, S_CODE, E_CODE, code);
		stringBuilder = replaceLaberOne(stringBuilder, T_BREED, S_BREED, E_BREED, breed);
//		stringBuilder = replaceLaberOne(stringBuilder, T_FILENAME, S_FILENAME, E_FILENAME, fileName);
		return stringBuilder;
	}
	
	public static StringBuilder replaceLaberOne(StringBuilder stringBuilder, String sourceStr,String startStr, String endStr, String replaceStr) {
		if(StringUtils.isEmpty(replaceStr)){
			stringBuilder = replaceScopeStr(stringBuilder, startStr, endStr, "");
		}else{
			stringBuilder = replaceStr(stringBuilder, startStr, "");
			stringBuilder = replaceStr(stringBuilder, sourceStr, replaceStr);
			stringBuilder = replaceStr(stringBuilder, endStr, "");
		}
		return stringBuilder;
	}
	
	public static StringBuilder replaceStr(StringBuilder stringBuilder, String sourceStr, String replaceStr) {
		if (!(null == stringBuilder || 0 == stringBuilder.length())) {
			for (int i = 0; i < 1000; i++) {
				int index = stringBuilder.indexOf(sourceStr);
				if(index>=0){
					stringBuilder = stringBuilder.replace(index, index + sourceStr.length(), replaceStr);
				}else{
					break;
				}
			}
		}
		return stringBuilder;
	}

	/**
	 * 替换中间范围数据
	 * 
	 * @param stringBuilder
	 * @param startStr
	 * @param endStr
	 * @param replaceStr
	 * @return
	 */
	public static StringBuilder replaceScopeStr(StringBuilder stringBuilder, String startStr, String endStr,
			String replaceStr) {
		if (!(null == stringBuilder || 0 == stringBuilder.length())) {
			for (int i = 0; i < 1000; i++) {
				int start = stringBuilder.indexOf(startStr);
				if(start<0){break;}
				int end = stringBuilder.indexOf(endStr);
				if(end<0){break;}
				if (start <= end) {
					stringBuilder =  stringBuilder.replace(start, end + endStr.length(), replaceStr);
				}else{
					break;
				}
			}
		}
		return stringBuilder;
	}

}
