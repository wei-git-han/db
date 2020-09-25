package com.css.app.db.config.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.css.addbase.msg.MsgTipUtil;
import com.css.app.db.business.entity.SubDocInfo;
import com.css.app.db.business.service.SubDocInfoService;
import com.css.app.db.config.entity.RemindAdministration;
import com.css.app.db.config.service.RemindAdministrationService;
import com.css.base.utils.Response;

/**
 * 提醒消息定时任务
 */
@Component
public class RemindAdministrationTimingTask {
	// java 定时器
	private Timer timer = null;
	// 定时器任务
	private static TimerTask timerTask = null;
	// 定时器状态：true：定时器开启；false：定时器关闭
	private static boolean status = true;
	@Autowired
	private RemindAdministrationService remindAdministrationService;
	@Autowired
	private SubDocInfoService subDocInfoService;
	@Autowired
	private MsgTipUtil msgUtil;
	//@Value("${csse.dccb.appId}")
	private String appId;
	//@Value("${csse.dccb.appSecret}")
	private String clientSecret;

	/**
	 * 启动程序时默认启动定时同步 120000代表项目启动2分钟后开始启动定时程序 600000代表每隔10分钟定时程序扫描一次数据
	 */
	public RemindAdministrationTimingTask() {
		if (timer == null) {
			timer = new Timer();
		}
		timer.scheduleAtFixedRate(getInstance(), 120000 , 600000);
	}

	/**
	 * 启动定时器
	 */
	public void start() {
		if (!status) {
			timer.purge();
			timer = new Timer();
			timer.scheduleAtFixedRate(getInstance(), 3600000, 3600000);
		}
	}

	/**
	 * 获取定时器状态
	 */
	public void status() {
		if (status) {
			// 定时器开启
			Response.json("status", true);
		} else {
			// 定时器关闭
			Response.json("status", false);
		}
	}

	/**
	 * 停止定时器
	 */
	public void calcel() {
		timer.cancel();
		status = false;
	}

	/**
	 * 获取定时任务
	 * 
	 * @return
	 */
	public TimerTask getInstance() {
		if (timerTask == null || !status) {
			status = true;
			timerTask = new TimerTask() {
				@Override
				public void run() {
					timingTask();
				}
			};
		}
		return timerTask;
	}

	/**
	 * 提醒消息业务代码
	 */
	public void timingTask() {
		Map<String, Object> map = new HashMap<>();
		map.put("state", "true");
		List<RemindAdministration> queryList = remindAdministrationService.queryList(map);
		if (queryList != null && queryList.size() > 0) {
			for (RemindAdministration remindAdministration : queryList) {
				if (remindAdministration.getType().equals("3")) {// 催填提醒
					System.out.println("进催填提醒---------------------------");
					boolean b = this.getMinusTime(remindAdministration.getStartTime(), remindAdministration.getEndTime());
					if (b) {// 在规定的时间范围内才发送
						System.out.println("催填提醒---------------------------");
						String remindTime = remindAdministration.getRemindTime();
						boolean minute= this.getMinusDate(remindTime);
						if (minute) {
							List<SubDocInfo> queryTmingTaskList = subDocInfoService.queryTmingTaskList(map);
							if (queryTmingTaskList != null && queryTmingTaskList.size() > 0) {
								for (SubDocInfo subDocInfo : queryTmingTaskList) {
									if (StringUtils.isNotBlank(subDocInfo.getUndertaker())) {
										String msgUrl = "/app/db/document/grdb/html/grdb.html?fileFrom=grdb";
										this.setMsg(subDocInfo.getUndertaker(), msgUrl,
												remindAdministration.getRemindContent());
									}
								}
							}
						}
					}
				} else if (remindAdministration.getType().equals("2")) {// 未承办和首轮未反馈
					System.out.println("未承办或首轮未反馈---------------------------");
					String remindTime = remindAdministration.getRemindTime();
					boolean minute= this.getMinusDate(remindTime);
					if (minute) {
						List<SubDocInfo> firstNoFeedbackTmingTaskList = subDocInfoService
								.firstNoFeedbackTmingTaskList();
						if (firstNoFeedbackTmingTaskList != null && firstNoFeedbackTmingTaskList.size() > 0) {
							for (SubDocInfo subDocInfo : firstNoFeedbackTmingTaskList) {
								if (StringUtils.isNotBlank(subDocInfo.getUndertaker())) {
									String msgUrl = "/app/db/document/grdb/html/grdb.html?fileFrom=grdb";
									this.setMsg(subDocInfo.getUndertaker(), msgUrl,
											remindAdministration.getRemindContent());
								}
							}
						}
						List<SubDocInfo> noFeedbackTmingTaskList = subDocInfoService.NoFeedbackTmingTaskList();
						if (noFeedbackTmingTaskList != null && noFeedbackTmingTaskList.size() > 0) {
							for (SubDocInfo subDocInfo : noFeedbackTmingTaskList) {
								if (StringUtils.isNotBlank(subDocInfo.getUndertaker())) {
									String msgUrl = "/app/db/document/grdb/html/grdb.html?fileFrom=grdb";
									this.setMsg(subDocInfo.getUndertaker(), msgUrl,
											remindAdministration.getRemindContent());
								}
							}
						}
					}
				} else if (remindAdministration.getType().equals("1")) {// 局未转办
					System.out.println("局未转办---------------------------");
					String remindTime = remindAdministration.getRemindTime();
					boolean minute= this.getMinusDate(remindTime);
					if (minute) {
						List<SubDocInfo> notTransferredTmingTaskList = subDocInfoService.notTransferredTmingTaskList();
						if (notTransferredTmingTaskList != null && notTransferredTmingTaskList.size() > 0) {
							for (SubDocInfo subDocInfo : notTransferredTmingTaskList) {
								if (StringUtils.isNotBlank(subDocInfo.getUndertaker())) {
									String msgUrl = "/app/db/document/jndb/html/jndb.html?fileFrom=jndb";
									this.setMsg(subDocInfo.getUndertaker(), msgUrl,
											remindAdministration.getRemindContent());
								}
							}
						}
					}

				}

			}
		}
	}

	/*private String getNewDate() {
		Date toDay = new Date();
		SimpleDateFormat f = new SimpleDateFormat("HH:mm");
		String format = f.format(toDay);
		return format;
	}*/

	private boolean  getMinusDate(String remind){
		if(StringUtils.isEmpty(remind)){
			return false;
		}
		int minute = 0;
		SimpleDateFormat format  = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		SimpleDateFormat dayformat  = new SimpleDateFormat("yyyy-MM-dd");
		String curDay = dayformat.format(new Date());
		String remindDate=curDay+" "+remind;
		System.out.println("配置时间："+remindDate);
		String curDate = format.format(new Date());
		System.out.println("当前时间："+curDate);
		try {
			long remindTime = format.parse(remindDate).getTime();
			long curTime = format.parse(curDate).getTime();
			long  minusTime = curTime - remindTime;
			minute = (int) minusTime/(1000*60);
			System.out.println("当前时间减配置时间所差分钟数："+minute);
			if(minute<10 && minute>=0){
				return true;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return false;

	}
	private void setMsg(String userId, String msgUrl, String content) {
		if (userId != null) {
			msgUtil.sendMsg("督查催办", content, msgUrl, userId, appId, clientSecret, "督办", "", "", "true");
		}
	}

	private boolean getMinusTime(String sTime, String eTime) {
		System.out.println("配置开始时间："+sTime+"配置结束时间："+eTime);
		Date date = new Date();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String nowDate = simpleDateFormat.format(date);
		System.out.println("当前时间："+nowDate);
		Date now;
		Date start = null;
		Date end = null;
		try {
			now = simpleDateFormat.parse(nowDate);
			if (!StringUtils.isEmpty(sTime)) {
				start = simpleDateFormat.parse(sTime);
			}
			if (!StringUtils.isEmpty(eTime)) {
				end = simpleDateFormat.parse(eTime);
			}
			long nowTime = now.getTime();
			long startTime = start.getTime();
			long endTime = end.getTime();
			if (nowTime>=startTime && nowTime <= endTime) {
				return true;
			} else {
				return false;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return false;
	}

}
