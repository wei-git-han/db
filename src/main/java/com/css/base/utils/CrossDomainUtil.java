package com.css.base.utils;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.css.base.filter.SSOAuthFilter;

/**
 * 跨域请求数据
 * @author mashuwen
 * @date 2017-10-12 21:17:28
 */
public class CrossDomainUtil {

	public static final JSONArray resultJsonArray = new JSONArray();

	public static JSONObject getJsonData(String url) {
		return getJsonData(url, null);
	}

	/**
	 * 跨域post请求
	 * @param url 请求地址
	 * @param map 请求参数集合
	 * @return 返回JSON数据
	 */
	public static JSONObject getJsonData(String url,LinkedMultiValueMap<String, Object> map){
		//设置消息头
		HttpHeaders headers = new HttpHeaders();
		MediaType mediaType = MediaType.parseMediaType("application/x-www-form-urlencoded;charset=UTF-8");
		headers.setContentType(mediaType);
		//设置请求参数
        HttpEntity<LinkedMultiValueMap<String, Object>> formEntity = new HttpEntity<LinkedMultiValueMap<String, Object>>(map, headers);
        //创建RestTemplate对象
        RestTemplate restTemplate = new RestTemplate();
        url+="?access_token=" + SSOAuthFilter.getToken();
        try{
        	//发送post请求
        	ResponseEntity<JSONObject> data = restTemplate.postForEntity(url, formEntity, JSONObject.class);
        	//返回请求数据
        	return data.getBody();
        }catch(Exception e){
        	System.out.println("【报错信息】"+e.getMessage()+"，url="+url);
        }
        return null;
	}
	
	public static JSONArray getJsonArrayData(String url,LinkedMultiValueMap<String, Object> map){
		//设置消息头
		HttpHeaders headers = new HttpHeaders();
		MediaType mediaType = MediaType.parseMediaType("application/x-www-form-urlencoded;charset=UTF-8");
		headers.setContentType(mediaType);
		//设置请求参数
        HttpEntity<LinkedMultiValueMap<String, Object>> formEntity = new HttpEntity<LinkedMultiValueMap<String, Object>>(map, headers);
        //创建RestTemplate对象
        RestTemplate restTemplate = new RestTemplate();
        url+="?access_token=" + SSOAuthFilter.getToken();
        try{
        	//发送post请求
        	ResponseEntity<JSONArray> data = restTemplate.postForEntity(url, formEntity, JSONArray.class);
        	//返回请求数据
        	return data.getBody();
        }catch(Exception e){
        	System.out.println("【报错信息】"+e.getMessage()+"，url="+url);
        }
        return null;
	}

	@Async
	public static void getJsonArrayDatal(String url,LinkedMultiValueMap<String, Object> map){
		//设置消息头
		HttpHeaders headers = new HttpHeaders();
		MediaType mediaType = MediaType.parseMediaType("application/x-www-form-urlencoded;charset=UTF-8");
		headers.setContentType(mediaType);
		//设置请求参数
        HttpEntity<LinkedMultiValueMap<String, Object>> formEntity = new HttpEntity<LinkedMultiValueMap<String, Object>>(map, headers);
        //创建RestTemplate对象
        RestTemplate restTemplate = new RestTemplate();
        url+="?access_token=" + SSOAuthFilter.getToken();
        try{
        	//发送post请求
        	ResponseEntity<JSONArray> data = restTemplate.postForEntity(url, formEntity, JSONArray.class);
			JSONArray dataResult = data.getBody();
			if (null == dataResult || dataResult.size() == 0) {
				resultJsonArray.add(new JSONArray());
			} else {
				//返回请求数据
				resultJsonArray.add(data.getBody());
			}
        }catch(Exception e){
        	System.out.println("【报错信息】"+e.getMessage()+"，url="+url);
        }
	}

	public static JSONArray getNewJsonArrayData(String url,LinkedMultiValueMap<Object, Object> map){
		//设置消息头
		HttpHeaders headers = new HttpHeaders();
		MediaType mediaType = MediaType.parseMediaType("application/x-www-form-urlencoded;charset=UTF-8");
		headers.setContentType(mediaType);
		//设置请求参数
		HttpEntity<LinkedMultiValueMap<Object, Object>> formEntity = new HttpEntity<LinkedMultiValueMap<Object, Object>>(map, headers);
		//创建RestTemplate对象
		RestTemplate restTemplate = new RestTemplate();
		url+="?access_token=" + SSOAuthFilter.getToken();
		try{
			//发送post请求
			ResponseEntity<JSONArray> data = restTemplate.postForEntity(url, formEntity, JSONArray.class);
			//返回请求数据
			return data.getBody();
		}catch(Exception e){
			System.out.println("【报错信息】"+e.getMessage()+"，url="+url);
		}
		return null;
	}

	public static JSONArray getJsonArrayData1(String url,LinkedMultiValueMap<String, Object> map){
		//设置消息头
		HttpHeaders headers = new HttpHeaders();
		MediaType mediaType = MediaType.parseMediaType("application/x-www-form-urlencoded;charset=UTF-8");
		headers.setContentType(mediaType);
		//设置请求参数
		HttpEntity<LinkedMultiValueMap<String, Object>> formEntity = new HttpEntity<LinkedMultiValueMap<String, Object>>(map, headers);
		//创建RestTemplate对象
		RestTemplate restTemplate = new RestTemplate();
		url+="?access_token=" + SSOAuthFilter.getToken();
		try{
			//发送post请求
			ResponseEntity<JSONArray> data = restTemplate.postForEntity(url, formEntity, JSONArray.class);
			//返回请求数据
			return data.getBody();
		}catch(Exception e){
			System.out.println("【报错信息】"+e.getMessage()+"，url="+url);
		}
		return null;
	}

}
