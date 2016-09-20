package com.sample.wechat.util;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Json格式字符串转换的工具类
 * @author Administrator
 *
 */
public class JsonUtil {
	private static final String noData = "{\"result\":null}";
	private static ObjectMapper mapper;
	
	static {
		mapper = new ObjectMapper();
		//如果对象的属性值为NULL，则不生成该属性
		mapper.setSerializationInclusion(Include.NON_NULL);
	}
	
	//javabean 转为json 的字符串
	public static String parseJson(Object object) {
		if(object == null){
			return noData;
		}
		try {
			return mapper.writeValueAsString(object);
		} catch (Exception e) {
			e.printStackTrace();
			return noData;
		}
	}
	//json字符串转化为jsonnode
	public static JsonNode JSON2Object(String json){
		try {
			return mapper.readTree(json);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
