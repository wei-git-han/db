package com.css.addbase.msg;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import com.css.addbase.PcSendUtil;
import com.css.addbase.SmsUtil;
import com.css.addbase.apporgan.service.BaseAppUserService;
import com.css.addbase.apporgmapped.service.BaseAppOrgMappedService;
import com.css.addbase.constant.AppConstant;
import com.css.addbase.constant.AppInterfaceConstant;
import com.css.addbase.msg.entity.MsgTip;
import com.css.addbase.msg.service.MsgTipService;
import com.css.addbase.token.TokenConfig;
import com.css.base.utils.CrossDomainUtil;
import com.css.base.utils.CurrentUser;

/**
 * 消息服务接口
 * 
 * @author gengds
 */
@Service
public class MsgTipUtil {
	
	@Autowired
	private MsgConfig msgConfig;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private MsgTipService msgService;

	@Autowired
	private BaseAppOrgMappedService baseAppOrgMappedService;
	@Autowired
	private BaseAppUserService baseAppUserService;
	@Autowired
	private SmsUtil smsUtil;
	@Autowired
	private PcSendUtil pcSendUtil;
	// 日志记录
    private static Logger logger = LoggerFactory.getLogger(MsgTipUtil.class);
	/**
	 * 给消息服务推送消息，进行消息提醒；
	 * @param title
	 * @param content
	 * @param userIds
	 * @return
	 */
	public String sendMsg(String title, String content, String url, String userIds, String appId, String appSecret){
		System.out.println("sendMsg_userIds==================: "+userIds);
		if (StringUtils.isBlank(userIds) || StringUtils.equals(userIds, ",")) {// 没有消息接收人，就不用提醒了
			return "fail";
		}
		if (StringUtils.isBlank(url)) {
			url = "";
		}
		// 获取指定应用的token
		String accessToken = "";
		if (StringUtils.isNotBlank(appId) && StringUtils.isNotBlank(appSecret)) {
			accessToken = TokenConfig.token(appId, appSecret);
		}
		if (StringUtils.isBlank(accessToken)) {
			accessToken = TokenConfig.token();
		}
		HttpHeaders headers = new HttpHeaders();
		MediaType type = MediaType.parseMediaType("multipart/form-data");
		headers.setContentType(type);
		LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<String,Object>();
		map.add("content", msgConfig.getMsgJson(title,content,url,appId));
		System.out.println(map.toString());
        HttpEntity<LinkedMultiValueMap<String, Object>> formEntity = new HttpEntity<LinkedMultiValueMap<String, Object>>(map, headers);
        String msgUrl = msgConfig.getMsgUrl() + "/message/user/" + userIds + "?access_token=";
		Object result = null;
		try {
			logger.info("消息请求路径:", msgUrl + accessToken);
			result = restTemplate.postForEntity(msgUrl+ accessToken, formEntity, String.class);
		} catch (Exception e) {
			System.out.println(e);
			TokenConfig.removeToken(accessToken);
			if (StringUtils.isNotBlank(appId) && StringUtils.isNotBlank(appSecret)) {
				accessToken = TokenConfig.token(appId, appSecret);
			}
			if (StringUtils.isBlank(accessToken)) {
				accessToken = TokenConfig.token();
			}
			logger.info("消息请求路径:", msgUrl + accessToken);
			result = restTemplate.postForEntity(msgUrl + accessToken, formEntity, String.class);
		}
		logger.info("消息返回结果:{}", result);
		if (result != null) {
			return "success";
		} else {
			return "fail";
		}
		
	}
	
	/**
	 * 给消息服务推送消息，进行消息提醒；新版接口，带有分组的消息接口
	 * @param title
	 * @param content
	 * @param userIds
	 * @return
	 */
	public String sendMsg(String title, String content, String url, String userIds, String appId, String appSecret, String groupName, String groupRedirect, String smsg,String value){
//		if (StringUtils.isBlank(userIds) || StringUtils.equals(userIds, ",")) {// 没有消息接收人，就不用提醒了
//			return "fail";
//		}
//		if (StringUtils.isBlank(url)) {
//			url = "";
//		}
//		// 获取指定应用的token
//		String accessToken = "";
//		if (StringUtils.isNotBlank(appId) && StringUtils.isNotBlank(appSecret)) {
//			accessToken = TokenConfig.token(appId, appSecret);
//		}
//		if (StringUtils.isBlank(accessToken)) {
//			accessToken = TokenConfig.token();
//		}
//		HttpHeaders headers = new HttpHeaders();
//		MediaType type = MediaType.parseMediaType("multipart/form-data");
//		headers.setContentType(type);
//		LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<String,Object>();
//		map.add("content", msgConfig.getMsgJson(title,content,url,appId,groupName, groupRedirect,value));
//		System.out.println(map.toString() + "*****发给1*****" + userIds);
//        HttpEntity<LinkedMultiValueMap<String, Object>> formEntity = new HttpEntity<LinkedMultiValueMap<String, Object>>(map, headers);
//        String msgUrl = msgConfig.getMsgUrl() + "/message/user/" + userIds + "?access_token=";
//			logger.info("消息请求路径:{}", msgUrl + accessToken);
//			ResponseEntity<String> postForEntity = restTemplate.postForEntity(msgUrl+ accessToken, formEntity, String.class);
//			logger.info("消息请求返回:{}",postForEntity.getBody());
//			//pcSendUtil.sendPC(msgUrl,accessToken,formEntity,String.class,appId,appSecret);
//		String[] ids = StringUtils.split(userIds,",");
//		String phone="";
	/*	for(String id : ids){
			//判断短信开关表中是否有该人信息    判断开关是否打开 判断短信内容是否为空
			if(getSet(id).equals("1") && smsg!=""){
				System.out.println("==================个人短信开关已打开==========================");
				phone=baseAppUserService.queryObject(id).getMobile();
				if(StringUtils.isNotEmpty(phone)){
				    System.out.println("===================电话号码为:::"+phone);
					smsUtil.send(smsg, phone);
				}
				
			}else {
				System.out.println("====================个人短信开关已关闭=========================");
			}
		}*/
		return "success";
	}

	/**
	 * 给消息服务推送消息，进行消息提醒；新版接口，带有分组的消息接口
	 * 以下是发送消息提醒，但桌面不显示，就是为了触发角标更新。
	 * @param title
	 * @param content
	 * @param userIds
	 * @return
	 */
	public String sendMsgUnvisible(String title, String content, String url, String userIds, String appId, String appSecret, String groupName, String groupRedirect, String smsg,String value){
		if (StringUtils.isBlank(userIds) || StringUtils.equals(userIds, ",")) {// 没有消息接收人，就不用提醒了
			return "fail";
		}
		if (StringUtils.isBlank(url)) {
			url = "";
		}
		// 获取指定应用的token
		String accessToken = "";
		if (StringUtils.isNotBlank(appId) && StringUtils.isNotBlank(appSecret)) {
			accessToken = TokenConfig.token(appId, appSecret);
		}
		if (StringUtils.isBlank(accessToken)) {
			accessToken = TokenConfig.token();
		}
		HttpHeaders headers = new HttpHeaders();
		MediaType type = MediaType.parseMediaType("multipart/form-data");
		headers.setContentType(type);
		LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<String,Object>();
		map.add("content", msgConfig.getMsgJsonUnvisible(title,content,url,appId,groupName, groupRedirect,value));
		System.out.println(map.toString() + "*****发给1*****" + userIds);
		HttpEntity<LinkedMultiValueMap<String, Object>> formEntity = new HttpEntity<LinkedMultiValueMap<String, Object>>(map, headers);
		String msgUrl = msgConfig.getMsgUrl() + "/message/user/" + userIds + "?access_token=";
		logger.info("消息请求路径:{}", msgUrl + accessToken);
		ResponseEntity<String> postForEntity = restTemplate.postForEntity(msgUrl+ accessToken, formEntity, String.class);
		logger.info("消息请求返回:{}",postForEntity.getBody());
		//pcSendUtil.sendPC(msgUrl,accessToken,formEntity,String.class,appId,appSecret);
		String[] ids = StringUtils.split(userIds,",");
		String phone="";
	/*	for(String id : ids){
			//判断短信开关表中是否有该人信息    判断开关是否打开 判断短信内容是否为空
			if(getSet(id).equals("1") && smsg!=""){
				System.out.println("==================个人短信开关已打开==========================");
				phone=baseAppUserService.queryObject(id).getMobile();
				if(StringUtils.isNotEmpty(phone)){
				    System.out.println("===================电话号码为:::"+phone);
					smsUtil.send(smsg, phone);
				}

			}else {
				System.out.println("====================个人短信开关已关闭=========================");
			}
		}*/
		return "success";
	}
	/**
	 * 查询单个人员的开关
	 * @param id
	 * @return
	 */
	public String getSet(String id){
		String elecPath = baseAppOrgMappedService.getWebUrl(AppConstant.APP_GWCL, AppInterfaceConstant.WEB_INTERFACE_DZBMS_TO_GWCL_SET);
		LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<String,Object>();
		String jsonData = "";
		map.add("userId",id);	
//		String url = elecPath + "?access_token=" + SSOAuthFilter.getToken();
		if(CrossDomainUtil.getJsonData(elecPath,map)!=null){
			jsonData = CrossDomainUtil.getJsonData(elecPath,map).getString("set");
		}
		
		return jsonData;
		
	}
	/**
	 * 发送消息
	 * @param path 发送路径
	 * @param title 消息标题
	 * @param content 消息内容
	 * @return
	 */
//	public  String sengMsg(String path,String msgId) {
//		MsgTip msg = msgService.queryObject(msgId);
//		if (msg == null) {
//			System.out.println("消息ID错误");
//			return "fail";
//		}
//		try{
//			HttpHeaders headers = new HttpHeaders();
//			MediaType type = MediaType.parseMediaType("multipart/form-data");
//			headers.setContentType(type);
////			headers.add("Accept", MediaType.APPLICATION_JSON.toString());
////			headers.add("access_token", TokenConfig.token());
//			LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<String,Object>();
//			System.out.println(appConfig.getMsgJson(msg.getMsgTitle(), msg.getMsgContent(),msg.getMsgRedirect()));
//			map.add("content", appConfig.getMsgJson(msg.getMsgTitle(), msg.getMsgContent(),msg.getMsgRedirect()));
//	        HttpEntity<LinkedMultiValueMap<String, Object>> formEntity = new HttpEntity<LinkedMultiValueMap<String, Object>>(map, headers);
//	        System.out.println(map.toString());
//	        String msgUrl = appConfig.getZuul()+appConfig.getMsg()+path+"?access_token="+TokenConfig.token();
//			Object result1 = restTemplate.postForEntity(msgUrl, formEntity,String.class);
//			System.out.println(result1);
//		}catch(Exception e) {
//			System.out.println(e);
//			try{
//			AppConfig.accessToken = "";
//			HttpHeaders headers = new HttpHeaders();
//			MediaType type = MediaType.parseMediaType("multipart/form-data");
//			headers.setContentType(type);
////			headers.add("Accept", MediaType.APPLICATION_JSON_UTF8_VALUE.toString());
////			headers.add("access_token", TokenConfig.token());
//			LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<String,Object>();
//			System.out.println(appConfig.getMsgJson(msg.getMsgTitle(), msg.getMsgContent(),msg.getMsgRedirect()));
//			map.add("content", appConfig.getMsgJson(msg.getMsgTitle(), msg.getMsgContent(),msg.getMsgRedirect()));
//	        HttpEntity<LinkedMultiValueMap<String, Object>> formEntity = new HttpEntity<LinkedMultiValueMap<String, Object>>(map, headers);
//	        System.out.println(map.toString());
//	        String msgUrl = appConfig.getZuul()+appConfig.getMsg()+path+"?access_token="+TokenConfig.token();
//			Object result1 = restTemplate.postForEntity(msgUrl, formEntity,String.class);
//			System.out.println(result1);
//			}catch(Exception ex) {
//				System.out.println(ex);
//				return "fail";
//			}
//		}
//		return "success";
//	}
	/**
	 * 发送消息封装
	 * @param msgId
	 * @param userId
	 * @param appType
	 */
	public void sendMsg(String msgId, String userId, String appType) {
		String appId = "";
		String appSecret = "";
		Map<String, Object> appMap = baseAppOrgMappedService.getAppIdAndSecret(userId,appType);
		if (appMap != null && appMap.size() > 0) {
			appId = (String) appMap.get("appId");
			appSecret = (String) appMap.get("appSecret");
		}

		MsgTip msg = msgService.queryObject(msgId);
		if (msg != null) {
			sendMsg(msg.getMsgTitle(), msg.getMsgContent(), msg.getMsgRedirect(), userId, appId, appSecret);
		}
	}
	
	/**
	 * 发送消息封装：新版消息服务接口，带有分组的接口；
	 * @param msgId
	 * @param userId
	 * @param appType
	 */
	public void sendMsg(String msgId, String userId, String appType, String groupName, String groupRedirect) {
		String appId = "";
		String appSecret = "";
		Map<String, Object> appMap = baseAppOrgMappedService.getAppIdAndSecret(userId,appType);
		if (appMap != null && appMap.size() > 0) {
			appId = (String) appMap.get("appId");
			appSecret = (String) appMap.get("appSecret");
		}

		MsgTip msg = msgService.queryObject(msgId);
		if (msg != null) {
			String smsg = "";
			sendMsg(msg.getMsgTitle(), msg.getMsgContent(), msg.getMsgRedirect(), userId, appId, appSecret, groupName, groupRedirect, smsg,"true");
		}
	}
	
	/**
	 * 发送消息封装
	 * @param msgId
	 * @param userId
	 * @param appType
	 */
	public void sendMsgToMany(String msgId, String userId, String appType) {
		String appId = "";
		String appSecret = "";
		Map<String, Object> appMap = baseAppOrgMappedService.getAppIdAndSecret(CurrentUser.getUserId(),appType);
		if (appMap != null && appMap.size() > 0) {
			appId = (String) appMap.get("appId");
			appSecret = (String) appMap.get("appSecret");
		}

		MsgTip msg = msgService.queryObject(msgId);
		if (msg != null) {
			sendMsg(msg.getMsgTitle(), msg.getMsgContent(), msg.getMsgRedirect(), userId, appId, appSecret);
		}
	}
	
	/**
	 * 发送消息封装(部级)
	 * @param msgId
	 * @param userId
	 * @param appType
	 */
	public void sendMsgToManyBj(String msgId, String userId, String appType) {
		String appId = "";
		String appSecret = "";
		Map<String, Object> appMap = baseAppOrgMappedService.getAppIdAndSecret(null,appType);
		if (appMap != null && appMap.size() > 0) {
			appId = (String) appMap.get("appId");
			appSecret = (String) appMap.get("appSecret");
		}

		MsgTip msg = msgService.queryObject(msgId);
		if (msg != null) {
			sendMsg(msg.getMsgTitle(), msg.getMsgContent(), msg.getMsgRedirect(), userId, appId, appSecret);
		}
	}

}
