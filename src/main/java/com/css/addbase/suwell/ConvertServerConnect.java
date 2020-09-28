package com.css.addbase.suwell;

import com.css.addbase.AppConfig;
import com.css.addbase.appconfig.entity.BaseAppConfig;
import com.css.addbase.appconfig.service.BaseAppConfigService;
import com.css.addbase.constant.AppConstant;
import com.css.base.utils.SpringContextUtils;
import com.css.base.utils.StringUtils;
import com.suwell.ofd.custom.agent.HTTPAgent;

/**
 * 转换服务初始化连接类
 * @author mashuwen
 *
 */
public class ConvertServerConnect {
	private static AppConfig appConfig = SpringContextUtils.getBean(AppConfig.class);
	private static BaseAppConfigService baseAppConfigService = SpringContextUtils.getBean(BaseAppConfigService.class);
	// 初始化转换服务连接对象
	private static HTTPAgent ha = null;
	// 文件的临时存放目录
	private static String tmpFilePath = null;
	
	// 本地路径
	private static String tmpFileOFDPath = null;
	
	/**
	 * 初始化存放文件的本地路径
	 * @return
	 */
	public static String initTmpOFDFilePath() {
		if (StringUtils.isBlank(tmpFileOFDPath)) {
			tmpFileOFDPath = appConfig.getLocalOFDFilePath();
		}
		return tmpFileOFDPath;
	}
	
	/**
	 * 初始化存放文件的临时目录
	 * @return
	 */
/*	public static String initTmpFilePath() {
		if (StringUtils.isBlank(tmpFilePath)) {
			tmpFilePath = appConfig.getLocalFilePath();
		}
		return tmpFilePath;
	}*/
	
	/**
	 * 初始化连接转换服务
	 * @return
	 */
	public static HTTPAgent initConvertServer() {
		if (ha == null) {
			String convertServiceUrl = "";
			BaseAppConfig config = baseAppConfigService.queryObject(AppConstant.APP_CONVERT_SERVER);
			if(config != null){
				convertServiceUrl = config.getValue();
			}
			if (StringUtils.isNotBlank(convertServiceUrl)) {
				ha = new HTTPAgent(convertServiceUrl);
			}
		}
		return ha;
	}
	
}
