package com.css.addbase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
@Configuration
public class AppConfig {
	
	@Autowired
	private Environment env;
	
	/**
	 * 系统取默认应用的token
	 */
	public static String accessToken;
	
	/**
	 * 根据appId和app的secret自取token
	 */
	public static String inviteAccessToken;
	
	/**
	 * 获取单点登录网关地址
	 * @return
	 */
	public String getZuul() {
		return env.getProperty("");
	}
	
	/**
	 * 单点登录接口
	 * @return
	 */
	public String getSso() {
		return env.getProperty("csse.mircoservice.sso");
	}
	
	/**
	 * 组织机构接口
	 * @return
	 */
	public String getOrg() {
		return env.getProperty("csse.mircoservice.org");
	}
	
	/**
	 * 消息服务接口
	 * @return
	 */
	public String getMsg() {
		return env.getProperty("csse.mircoservice.msg");
	}
	
	/**
	 * 短信服务接口
	 * @return
	 */
	public String getSms() {
		return env.getProperty("csse.mircoservice.smsUrl");
	}
	
	/**
	 * 鉴权服务接口
	 * @return
	 */
	public String getUaa() {
		return env.getProperty("csse.mircoservice.uaa");
	}
	
	/**
	 * 获得文件服务下载到应用服务器本地的路径
	 * @return
	 */
/*	public String getLocalFilePath(){
		return env.getProperty("css.fileServcie.file.tempPath");
	}*/
	/**
	 * 语音预定 会议室 路径头（点击此路径里链接到语音预定的会议室）
	 * @return
	 */
	public String getVoicePreMeetUrl(){
		return env.getProperty("voicePreMeetUrl");
	}
	/**
	 * 获取AppId
	 * @return
	 */
	public String getAppId(){
		return env.getProperty("");
	}
	
	
	/**
	 * 获得下载到本地项目下文件夹文件部分路径
	 * @return
	 */
	public String getLocalOFDFilePath(){
		return env.getProperty("css.file.uploadFile.path");
	}
}
