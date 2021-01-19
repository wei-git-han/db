package com.css.websocket;

import com.alibaba.fastjson.JSONObject;
import com.css.app.db.business.service.SubDocInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.PostConstruct;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class WebSocketHandle {


    @Autowired
    private ProductWebSocket productWebSocket;

    @Autowired
    private SubDocInfoService subDocInfoService;

    private static Map<String,SendPojo> userDataMap = new ConcurrentHashMap<>();

    /**
     *  接收督办业务推送数据
     * @param userId
     * @param menuType 0公文、1阅件、2阅知、3办件
     * @param isSerf 是否是当前人
     */
    public void addSendMap(String userId,int menuType,Boolean isSerf){
        if(null == userDataMap.get(userId)){
            SendPojo sendPojo = new SendPojo();
            sendPojo.setSendPojo(userId,menuType,isSerf);
            userDataMap.put(userId,sendPojo);
        }else{
            SendPojo sendPojo = userDataMap.get(userId);
            sendPojo.setSendPojo(userId,menuType,isSerf);
        }
    }

    /**
     * 监听是否有消息待发送
     */
    @PostConstruct
    public void messageListener(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (0 < userDataMap.size()) {
                        Iterator<String> iterator = userDataMap.keySet().iterator();
                        while (iterator.hasNext()){
                            String next = iterator.next();
                            sendMessageByUserId(next);
                            userDataMap.remove(next);
                        }
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();
    }

    /**
     * 获取待办数并发送
     * @param userId
     */
    public void sendMessageByUserId(String userId){
        SendPojo sendPojo = userDataMap.get(userId);
        JSONObject json = new JSONObject();
        json.put("data",sendPojo);
        json.put("count",subDocInfoService.sendMsgByWebSocket(userId));
        productWebSocket.sendToUser(userId, JSONObject.toJSONString(json));
        userDataMap.remove(userId);
    }

    /**
     * 定时推送 5分钟
     * @param userId
     * @return
     */
    public String pushWaitCount(String userId){
        JSONObject waitCount = (JSONObject) this.getWaitCount(userId);
        return JSONObject.toJSONString(waitCount);
    }

    /**
     * 查询待办数
     * @param userId
     * @return
     */
    public Object getWaitCount(String userId){
        return subDocInfoService.sendMsgByWebSocket(userId);
    }

}
