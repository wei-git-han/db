package com.css.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/webSocket/{userId}")
@Component
public class ProductWebSocket {

    private final Logger logger = LoggerFactory.getLogger(ProductWebSocket.class);

    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;

    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。若要实现服务端与单一客户端通信的话，可以使用Map来存放，其中Key可以为用户id
    private static ConcurrentHashMap<String, List<ProductWebSocket>> webSocketSet = new ConcurrentHashMap<>();

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    //当前发消息的人员编号
    private String userId = "";

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
        List<ProductWebSocket> productWebSockets = webSocketSet.get(userId);
        if(null == productWebSockets){
            ArrayList<ProductWebSocket> objects = new ArrayList<>();
            objects.add(this);
            webSocketSet.put(param, objects);//加入线程安全map中
        }else{
            List<ProductWebSocket> productWebSockets1 = webSocketSet.get(param);
            productWebSockets1.add(this);
        }
        addOnlineCount();           //在线数加1
        this.pushLog("用户id：" + param + "加入连接！当前在线人数为" + getOnlineCount());
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        if (!userId.equals("")) {
            webSocketSet.remove(userId);  //根据用户id从ma中删除
            subOnlineCount();           //在线数减1
            this.pushLog("用户id：" + userId + "关闭连接！当前在线人数为" + getOnlineCount());
        }
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
        //给指定的人发消息
        sendToUser(sendUserId, sendMessage);
        this.pushLog("来自客户端的消息:" + message);
    }

    /**
     * 给指定的人发送消息
     *
     * @param message
     */
    public void sendToUser(String sendUserId, String message) {
        try {
            if (webSocketSet.get(sendUserId) != null) {
                List<ProductWebSocket> productWebSockets = webSocketSet.get(sendUserId);
                for (ProductWebSocket socket: productWebSockets) {
                    socket.sendMessage(userId + "发送消息，消息内容为--->>" + message);
                    this.pushLog(userId + "发送消息，消息内容为--->>" + message);
                }
            } else {
                this.pushLog("消息接受人:" + sendUserId + "已经离线！");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 管理员发送消息
     *
     * @param message
     */
    public void systemSendToUser(String sendUserId, String message) {
        try {
            if (webSocketSet.get(sendUserId) != null) {
                List<ProductWebSocket> productWebSockets = webSocketSet.get(sendUserId);
                for (ProductWebSocket socket: productWebSockets) {
                    socket.sendMessage(sendUserId+"发送消息，消息内容为--->>" + message);
                    this.pushLog(sendUserId+"发送消息，消息内容为--->>" + message);
                }
            } else {
                this.pushLog("消息接受人:" + sendUserId + "已经离线！");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 给所有人发消息
     *
     * @param message
     */
    public void sendAll(String message) {
        String sendMessage = message.split(",")[0];
        //遍历HashMap
        for (String key : webSocketSet.keySet()) {
            try {
                //判断接收用户是否是当前发消息的用户
                if (!userId.equals(key)) {
                    List<ProductWebSocket> productWebSockets = webSocketSet.get(key);
                    for (ProductWebSocket socket: productWebSockets) {
                        socket.sendMessage("用户:" + userId + "发来消息：" + " <br/> " + sendMessage);
                        this.pushLog("用户:" + userId + "发来消息：" + " <br/> " + sendMessage);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 发生错误时调用
     *
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        this.pushLog("发生错误，控制台已打印，错误信息："+error.getMessage());
        error.printStackTrace();
    }

    /**
     * 发送消息
     *
     * @param message
     * @throws IOException
     */
    public void sendMessage(String message) throws IOException {
        //发送
        this.session.getBasicRemote().sendText(message);
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
