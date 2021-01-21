package com.css.websocket;

import com.alibaba.fastjson.JSONObject;
import com.css.app.db.business.service.SubDocInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.PostConstruct;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class WebSocketHandle {


    @Autowired
    private ProductWebSocket productWebSocket;

    @Autowired
    private SubDocInfoService subDocInfoService;

    private static Map<String,SendPojo> userDataMap = new ConcurrentHashMap<>();

    private static Logger logger = LoggerFactory.getLogger(WebSocketHandle.class);

    private ExecutorService executorService = Executors.newFixedThreadPool(3);

    public static Thread initThread;
    /**
     *  接收督办业务推送数据
     * @param userId
     * @param menuType 0公文、1阅件、2阅知、3办件
     * @param isSerf 是否是当前人
     */
    public void addSendMap(String userId,int menuType,Boolean isSerf){
        logger.info("addMap,用户id:"+userId+",菜单类型:"+menuType+",是否是当前人:+"+isSerf);
        if(null == userDataMap.get(userId)){
            SendPojo sendPojo = new SendPojo();
            sendPojo.setSendPojo(userId,menuType,isSerf);
            userDataMap.put(userId,sendPojo);
            logger.info("addSendMap 接收到需要参数：userId:{}, menuType:{}, isSerf:{}", userId, menuType, isSerf);
        }else{
            SendPojo sendPojo = userDataMap.get(userId);
            sendPojo.setSendPojo(userId,menuType,isSerf);
            logger.info("addSendMap 接收到需要参数：userId:{}, menuType:{}, isSerf:{}", userId, menuType, isSerf);
        }
    }

    /**
     * 监听是否有消息待发送
     */
    @PostConstruct
    public void messageListener(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                initRun();
            }
        });
        thread.start();
    }

    public void initRun(){
        while (true) {
            logger.info("服务端，监听进程运行中");
            if (0 < userDataMap.size()) {
                logger.info("服务端，监听到新的任务");
                Iterator<String> iterator = userDataMap.keySet().iterator();
                while (iterator.hasNext()) {
                    logger.info("服务端，监听开始发送新消息");
                    String next = iterator.next();
                    executorService.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                sendMessageByUserId(next);
                            }catch (Exception e){
                                e.printStackTrace();
                                logger.info("服务端，发送消息失败，错误信息为："+e.getStackTrace());
                            }
                        }
                    });
                    logger.info("服务端，监听发送成功");
                    userDataMap.remove(next);
                    logger.info("服务端，移除已处理任务");
                }
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 获取待办数并发送
     * @param userId
     */
    public void sendMessageByUserId(String userId) throws  Exception{
        logger.info("服务端，准备消息内容");
        SendPojo sendPojo = userDataMap.get(userId);
        JSONObject json = new JSONObject();
        logger.info("服务端，获取待办数");
        json.put("data",sendPojo);
        logger.info("服务端，调用websocket发送");
        json.put("count",subDocInfoService.sendMsgByWebSocket(userId));
        productWebSocket.sendToUser(userId, JSONObject.toJSONString(json));
        logger.info("服务端，获取待办数并发送成功");
        userDataMap.remove(userId);
    }

    /**
     * 定时推送 5分钟
     * @param userId
     * @return
     */
    public String pushWaitCount(String userId){
        logger.info("服务端，5分钟 获取待办数并发送成功");
        JSONObject waitCount = (JSONObject) this.getWaitCount(userId);
        return JSONObject.toJSONString(waitCount);
    }

    /**
     * 查询待办数
     * @param userId
     * @return
     */
    public Object getWaitCount(String userId){
        logger.info("服务端，查询待办数");
        return subDocInfoService.sendMsgByWebSocket(userId);
    }

}
