package com.css.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/webSocket/{userId}")
@Component
public class ProductWebSocket {

    private final Logger logger = LoggerFactory.getLogger(ProductWebSocket.class);

    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;

    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。若要实现服务端与单一客户端通信的话，可以使用Map来存放，其中Key可以为用户id
    private static ConcurrentHashMap<String, ProductWebSocket> webSocketSet = new ConcurrentHashMap<>();

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    //当前发消息的人员编号
    private String userId = "";

    private static ApplicationContext applicationContext;
    //解决spring实例化websocket，只注入一次对象
    public static void setApplicationContext(ApplicationContext applicationContext){
        ProductWebSocket.applicationContext = applicationContext;
    }

    /**
     * 线程安全的统计在线人数
     *
     * @return
     */
    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        ProductWebSocket.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        ProductWebSocket.onlineCount--;
    }

    /**
     * 连接建立成功调用的方法
     *
     * @param param   用户唯一标识
     * @param session 可选的参数。session为与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    @OnOpen
    public void onOpen(@PathParam(value = "userId") String param, Session session) {
        this.userId = param;//接收到发送消息的人员编号
        this.session = session;
        webSocketSet.put(param + session.getId(), this);//加入线程安全map中
        addOnlineCount();           //在线数加1
        this.pushLog("用户id：" + param + "加入连接！当前在线人数为" + getOnlineCount());
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(@PathParam(value = "userId") String param, Session session) {
        webSocketSet.remove(param+session.getId());  //根据用户id从ma中删除
        subOnlineCount();           //在线数减1
        this.pushLog("用户id：" + userId + "关闭连接！当前在线人数为" + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     * @param session 可选的参数
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        //要发送人的用户uuid
        String sendUserId = message.split(",")[1];
        //发送的信息
        String sendMessage = message.split(",")[0];
        //定时5分钟发送待办数
        String isSend = message.split(",")[2];
        if("true".equals(isSend)){
            WebSocketHandle bean = applicationContext.getBean(WebSocketHandle.class);
            String waitCount = bean.pushWaitCount(sendUserId);
            sendMessage += ","+waitCount;
        }
        //给指定的人发消息
        sendToUser(sendUserId+session.getId(), sendMessage);
        this.pushLog("来自客户端的消息:" + message);
    }

    /**
     * 给指定的人发送消息
     *
     * @param message
     */
    public void sendToUser(String sendUserId, String message) {
            for (String userIdKey:webSocketSet.keySet()) {
                if(userIdKey.contains(sendUserId)) {
                    webSocketSet.get(userIdKey).sendMessage(userId + "发送消息，消息内容为--->>" + message);
                    this.pushLog(userId + "发送消息，消息内容为--->>" + message);
                }
            }
    }

    /**
     * 管理员发送消息
     *
     * @param message
     */
    public void systemSendToUser(String sendUserId, String message) {
        for (String userIdKey: webSocketSet.keySet()) {
            if(userIdKey.contains(sendUserId)) {
                webSocketSet.get(userIdKey).sendMessage(sendUserId + "发送消息，消息内容为--->>" + message);
                this.pushLog(sendUserId + "发送消息，消息内容为--->>" + message);
            }
        }
    }

    /**
     * 给所有人发消息
     *
     * @param message
     */
    public void sendAll(String message) {
        String sendMessage = message.split(",")[0];
        for (String userIdKey: webSocketSet.keySet()) {
            webSocketSet.get(userIdKey).sendMessage("用户:" + userId + "发来消息：" + " <br/> " + sendMessage);
            this.pushLog("用户:" + userId + "发来消息：" + " <br/> " + sendMessage);
        }

    }


    /**
     * 发生错误时调用
     *
     * @param session
     * @param error
     */
    @OnError
    public void onError(@PathParam(value = "userId") String param, Session session, Throwable error) {
        this.pushLog("发生错误，控制台已打印，错误信息："+error.getMessage()+"用户id:"+param);
        error.printStackTrace();
    }

    /**
     * 发送消息
     *
     * @param message
     * @throws IOException
     */
    public void sendMessage(String message){
        //发送
        try {
            this.session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.pushLog("发送消息："+message);
    }

    /**
     * 添加日志输出
     */
    public void pushLog(String message){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        logger.info(message+"当前时间为:"+format.format(new Date()));
    }
}
