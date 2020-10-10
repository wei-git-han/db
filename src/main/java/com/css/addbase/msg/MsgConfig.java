package com.css.addbase.msg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.css.base.utils.StringUtils;

/**
 * 消息配置类
 * @author lenovo
 *
 */
@Configuration
public class MsgConfig {
	
	@Autowired
	private Environment env;
	
	/**
	 * 获取单点登录网关地址
	 * @return
	 */
	public String getMsgUrl() {
		return env.getProperty("csse.mircoservice.zuul")+env.getProperty("csse.mircoservice.msg");
	}	
	
	/**
	 * 获取消息字符串
	 * @param title
	 * @param content
	 * @return
	 */
	public JSONObject getMsgJson(String title,String content,String redirect,String appId) {
		JSONObject msgContent = new JSONObject();
		msgContent.put("type", env.getProperty("msg.type"));
		msgContent.put("title",title);
		msgContent.put("content",content);
		msgContent.put("appid",appId);
		//设置消息时间戳
		String time = String.valueOf(System.currentTimeMillis());
		msgContent.put("timestamp",Long.valueOf(time.substring(0, 10)));
		JSONObject action = new JSONObject();
		if (StringUtils.isNotEmpty(env.getProperty("msg.action.anstore"))) {
			if (StringUtils.equals(env.getProperty("msg.action.anstore"), "true")) {
       		 action.put("anstore", true);
       	    } else {
       		 action.put("anstore", false);
       	    }
		} else {
			action.put("anstore", false);
		}
         if (StringUtils.isNotEmpty(env.getProperty("msg.action.deployment"))) {
        	 if (StringUtils.equals(env.getProperty("msg.action.deployment"), "true")) {
        		 action.put("deployment", true);
        	 } else {
        		 action.put("deployment", false);
        	 }
		} else {
			action.put("deployment", false);
		}
        action.put("redirect", redirect);
        msgContent.put("action", action);
        
        
        JSONObject display = new JSONObject();
        if (StringUtils.isNotEmpty(env.getProperty("msg.display.msgbox"))) {
        	if (StringUtils.equals(env.getProperty("msg.display.msgbox"), "true")) {
        		display.put("msgbox", true);
        	} else {
        		display.put("msgbox", false);
        	}
        } else {
        	display.put("msgbox", true);
        }
        if (StringUtils.isNotEmpty(env.getProperty("msg.display.notification"))) {
        	if (StringUtils.equals(env.getProperty("msg.display.notification"), "true")) {
        		display.put("notification", true);
        	} else {
        		display.put("notification", false);
        	}
        } else {
        	display.put("notification", true);
        }
        if (StringUtils.isNotEmpty(env.getProperty("msg.display.system"))) {
        	if (StringUtils.equals(env.getProperty("msg.display.system"), "true")) {
        		display.put("system", true);
        	} else {
        		display.put("system", false);
        	}
        } else {
        	display.put("system", false);
        }
        msgContent.put("display", display);
		return msgContent;
	}
	
	/**
	 * 获取消息字符串：新版接口，可分组的接口
	 * @param title
	 * @param content
	 * @return
	 */
	public JSONObject getMsgJson(String title,String content,String redirect,String appId, String groupName, String groupRedirect,String value) {
		JSONObject msgContent = new JSONObject();
		msgContent.put("type", env.getProperty("msg.type"));
		msgContent.put("title",title);
		msgContent.put("content",content);
		msgContent.put("appid",appId);
		// 消息体链接
		msgContent.put("redirect", redirect);
		// 消息接收方的平台；x86,arm64,amd64,win32等终端；
		msgContent.put("platform", env.getProperty("msg.platform"));
		// 消息是否在消息盒子可见；
		msgContent.put("visible", env.getProperty("msg.visible"));
		// 消息显示的图标，默认由桌面提供
		msgContent.put("icon", env.getProperty("msg.icon"));
		// 设置消息时间戳
		String time = String.valueOf(System.currentTimeMillis());
		msgContent.put("timestamp",Long.valueOf(time.substring(0, 10)));
        JSONObject group = new JSONObject();
        // 分组名称
        if (StringUtils.isEmpty(env.getProperty("msg.group.name"))) {
        	group.put("name", groupName);
        } else {
        	group.put("name", env.getProperty("msg.group.name"));
        }
        // 分组消息跳转的地址
        if (StringUtils.isEmpty(env.getProperty("msg.group.redirect"))) {
        	group.put("redirect", groupRedirect);
        } else {
        	group.put("redirect", env.getProperty("msg.group.redirect"));
        }
        msgContent.put("group", group);
        JSONArray array = new JSONArray();
        JSONObject operations = new JSONObject();
        operations.put("type", "update_todo_count");
        operations.put("value", true);
        array.add(operations);
        msgContent.put("operations", array);
        
		return msgContent;
	}

	/**
	 * 获取消息字符串：新版接口，可分组的接口
     * 以下是发送消息提醒，但桌面不显示，就是为了触发角标更新。
	 * @param title
	 * @param content
	 * @return
	 */
	public JSONObject getMsgJsonUnvisible(String title,String content,String redirect,String appId, String groupName, String groupRedirect,String value) {
		JSONObject msgContent = new JSONObject();
		msgContent.put("type", env.getProperty("msg.type"));
		msgContent.put("title",title);
		msgContent.put("content",content);
		msgContent.put("appid",appId);
		//msgContent.put("sound","false");
		// 消息体链接
		msgContent.put("redirect", redirect);
		// 消息接收方的平台；x86,arm64,amd64,win32等终端；
		msgContent.put("platform", env.getProperty("msg.platform"));
		// 消息是否在消息盒子可见；
		msgContent.put("visible", false);
		// 消息显示的图标，默认由桌面提供
		msgContent.put("icon", env.getProperty("msg.icon"));
		// 设置消息时间戳
		String time = String.valueOf(System.currentTimeMillis());
		msgContent.put("timestamp",Long.valueOf(time.substring(0, 10)));
		JSONObject group = new JSONObject();
		// 分组名称
		if (StringUtils.isEmpty(env.getProperty("msg.group.name"))) {
			group.put("name", groupName);
		} else {
			group.put("name", env.getProperty("msg.group.name"));
		}
		// 分组消息跳转的地址
		if (StringUtils.isEmpty(env.getProperty("msg.group.redirect"))) {
			group.put("redirect", groupRedirect);
		} else {
			group.put("redirect", env.getProperty("msg.group.redirect"));
		}
		msgContent.put("group", group);
		JSONArray array = new JSONArray();
		JSONObject operations = new JSONObject();
		operations.put("type", "update_todo_count");
		operations.put("value", true);
		array.add(operations);
		msgContent.put("operations", array);

		return msgContent;
	}

}
