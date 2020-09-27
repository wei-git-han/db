//package com.css.addbase.orgservice;
//
//import java.util.List;
//import java.util.Timer;
//import java.util.TimerTask;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.ResponseBody;
//import org.springframework.web.client.RestTemplate;
//import com.css.addbase.AppConfig;
//import com.css.addbase.apporgan.entity.BaseAppOrgan;
//import com.css.addbase.apporgan.entity.BaseAppUser;
//import com.css.addbase.apporgan.service.BaseAppOrganService;
//import com.css.addbase.apporgan.service.BaseAppUserService;
//import com.css.addbase.orgservice.Organ;
//import com.css.addbase.orgservice.UserInfo;
//import com.css.addbase.token.TokenConfig;
//import com.css.base.utils.Response;
//import com.css.base.utils.StringUtils;
//
///**
// * 组织机构增量同步接口
// * @author gengds
// */
//@Component
//@RequestMapping("/app/timer")
//public class SyncOrganUtil {
//
//	//@Value("${csse.mircoservice.zuul}")
//	private String zuul;
//
//	@Value("${csse.mircoservice.syncdepartments}")
//	private  String syncdepartments;
//
//	@Autowired
//	private BaseAppOrganService baseAppOrganService;
//
//	@Autowired
//	private BaseAppUserService baseAppUserService;
//
//	@Autowired
//	private AppConfig appConfig;
//
//	@Autowired
//	private RestTemplate restTemplate;
//
//	//自动同步时间
//	private static Long starttime;
//	//java 定时器
//	private Timer timer = null;
//	//定时器任务
//	private static TimerTask timerTask = null;
//	//定时器状态：true：定时器开启；false：定时器关闭
//	private static boolean status = true;
//
//
//	/**
//	 * 启动程序时默认启动定时同步
//	 */
//	public SyncOrganUtil() {
//		if (timer == null) {
//			 timer = new Timer();
//		}
//		timer.scheduleAtFixedRate(getInstance(), 120000,120000);
//	}
//	/**
//	 * 获取定时任务
//	 * @return
//	 */
//	public  TimerTask getInstance() {
//		if (timerTask == null || !status) {
//			status = true;
//			timerTask = new TimerTask(){
//				@Override
//				public void run() {
//					SyncOrgan();
//				}
//			};
//		}
//		return timerTask;
//	}
//
//	/**
//	 * 启动定时器
//	 */
//	@ResponseBody
//	@RequestMapping("/start.htm")
//	public void start() {
//		if (!status) {
//			timer.purge();
//			timer = new Timer();
//			timer.scheduleAtFixedRate(getInstance(), 120000,120000);
//		}
//	}
//	/**
//	 * 停止定时器
//	 */
//	@ResponseBody
//	@RequestMapping("/cancel.htm")
//	public void calcel() {
//		timer.cancel();
//		status = false;
//	}
//
//	/**
//	 * 获取定时器状态
//	 */
//	@ResponseBody
//	@RequestMapping("/status.htm")
//	public void status() {
//		if (status) {
//			//定时器开启
//			Response.json("status", true);
//		} else {
//			//定时器关闭
//			Response.json("status",false);
//		}
//	}
//
//	/**
//	 * 实现增量同步接口
//	 */
//	public void SyncOrgan() {
//		if (starttime == null) {
//			//设置消息时间戳
//			String time = String.valueOf(System.currentTimeMillis());
//			starttime = Long.valueOf(time.substring(0, 10));
//		}
//		System.out.println("同步地址："+zuul+syncdepartments+"?starttime="+starttime+"&access_token=" + TokenConfig.token());
//		try {
//			SyncOrgan syncOrgan = (SyncOrgan) restTemplate.getForObject(zuul+syncdepartments+"?starttime="+starttime+"&access_token=" + TokenConfig.token(),
//					SyncOrgan.class, new Object[0]);
//			starttime = syncOrgan.getTimestamp();
//			List<Organ> organs = syncOrgan.getOrg();
//			int syncNum = 0;
//			if (organs != null && organs.size() > 0) {
//				SyncOrgan(organs);
//				syncNum += organs.size();
//				System.out.println("同步部门数:"+organs.size());
//			}
//			List<UserInfo> userInfos= syncOrgan.getUser();
//			if (userInfos != null && userInfos.size() > 0) {
//				SyncUser(userInfos);
//				syncNum += userInfos.size();
//				System.out.println("同步用户数:"+userInfos.size());
//			}
//			System.out.println("组织机构同步成功：同步总数："+syncNum);
//		} catch (Exception e) {
//			System.out.println("组织机构同步失败");
//			System.out.println(e);
//		}
//	}
//
//	/**
//	 * 同步组织机构
//	 * @param organs
//	 */
//    private void SyncOrgan(List<Organ> organs){
//
//    	for (Organ organ:organs) {
//
//    		if (StringUtils.equals(organ.getType(),"0")) {
//    			//部门删除
//    			baseAppOrganService.delete(organ.getOrganId());
//    		} else if (StringUtils.equals(organ.getType(),"1") || StringUtils.equals(organ.getType(),"2")) {
//    			//部门新增或编辑
//    			BaseAppOrgan baseAppOrgantemp = baseAppOrganService.queryObject(organ.getOrganId());
//    			BaseAppOrgan baseAppOrgan = new BaseAppOrgan();
//    			baseAppOrgan.setId(organ.getOrganId());
//    			baseAppOrgan.setName(organ.getOrganName());
//    			baseAppOrgan.setParentId(organ.getFatherId());
//    			baseAppOrgan.setTreePath(organ.getP());
//    			baseAppOrgan.setSort(organ.getOrderId());
//    			baseAppOrgan.setIsdelete(organ.getIsDelete());
//    			if(baseAppOrgantemp!=null){
//    				baseAppOrganService.update(baseAppOrgan);
//    			}else{
//    				baseAppOrganService.save(baseAppOrgan);
//    			}
//    		}
//    	}
//
//    }
//
//    /**
//     * 同步用户
//     * @param userInfos
//     */
//    private void SyncUser(List<UserInfo> userInfos){
//
//    	for (UserInfo userInfo:userInfos) {
//
//            if (StringUtils.equals(userInfo.getType(),"0")) {
//            	//人员删除
//            	baseAppUserService.deleteByUserId(userInfo.getUserid());
//    		} else if (StringUtils.equals(userInfo.getType(),"1") || StringUtils.equals(userInfo.getType(),"2")) {
//    			//人员新增或编辑
//    			BaseAppUser baseAppUsertemp = baseAppUserService.queryObject(userInfo.getUserid());
//    			BaseAppUser baseAppUser=new BaseAppUser();
//    			baseAppUser.setId(userInfo.getUserid());
//    			baseAppUser.setUserId(userInfo.getUserid());
//    			baseAppUser.setAccount(userInfo.getAccount());
//    			baseAppUser.setTruename(userInfo.getFullname());
//    			baseAppUser.setMobile(userInfo.getMobile());
//    			baseAppUser.setOrganid(userInfo.getOrganId());
//    			baseAppUser.setSort(userInfo.getOrderId());
//    			baseAppUser.setSex(userInfo.getSex());
//    			baseAppUser.setTelephone(userInfo.getTel());
//    			baseAppUser.setIsdelete(userInfo.getIsDelete());
//    			if(baseAppUsertemp!=null){
//    				baseAppUserService.update(baseAppUser);
//    			}else{
//    				baseAppUserService.save(baseAppUser);
//    			}
//    		}
//    	}
//
//    }
//
//}
