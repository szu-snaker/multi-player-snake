package com.example.tanchishe.service;


import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.example.tanchishe.bean.Food;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: pjj
 * @Classname: WebSocketServer
 * @Date: 2022/03/05
 * @Description: websocket服务类
 */
@ServerEndpoint("/ws/{userId}")
@Component
public class WebSocketServer {

    // concurrent包的线程安全Map，用来存放每个客户端对应的MyWebSocket对象。
    private static ConcurrentHashMap<String, WebSocketServer> webSocketMap = new ConcurrentHashMap<String, WebSocketServer>();

    // 匹配中玩家
    private static String ready = "";

    // 与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    // 接收userId
    private String userId;

    // 对应的食物
    private Food food;

    // 匹配对方玩家ID
    private String rivalId;

    /**
     *	连接建立成功调用的方法
     *	@Author: pjj
     *	@Param: session 与客户端的连接
     *	@Param: userId 用户ID
     *	@Date: 2022/03/05
     *	@return null
     **/
    public static ConcurrentHashMap<String, WebSocketServer> getWebSocketMap() {
        return webSocketMap;
    }

    /**
     *	连接建立成功调用的方法
     *	@Author: pjj
     *	@Param: session 与客户端的连接
     *	@Param: userId 用户ID
     *	@Date: 2022/03/05
     *	@return null
     **/
    public static void setWebSocketMap(ConcurrentHashMap<String, WebSocketServer> webSocketMap) {
        WebSocketServer.webSocketMap = webSocketMap;
    }

    /**
     *	连接建立成功调用的方法
     *	@Author: pjj
     *	@Param: session 与客户端的连接
     *	@Param: userId 用户ID
     *	@Date: 2022/03/05
     *	@return null
     **/
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        this.session = session;
        this.userId = userId;
        // 将用户ID和它对应的连接存储到map中
        if (webSocketMap.containsKey(userId)) {
            webSocketMap.remove(userId);
            webSocketMap.put(userId, this);
        } else {
            webSocketMap.put(userId, this);
        }
    }

    /**
     *	连接关闭调用的方法
     *	@Author: pjj
     *	@Param: null
     *	@Date: 2022/03/05
     *	@return null
     **/
    @OnClose
    public void onClose() {
        if (webSocketMap.containsKey(userId)) {
            webSocketMap.remove(userId);
        }
    }

    /**
     *	收到客户端消息后调用的方法
     *	@Author: pjj
     *	@Param: message 客户端发送过来的消息
     *	@Date: 2022/03/05
     *	@return null
     **/
    @OnMessage
    public void onMessage(String message, Session session) {
        if (StrUtil.isNotBlank(message)) {
            try {
                // 存储json信息
                JSONObject jsonObject = JSONObject.parseObject(message);
                // 获取指令
                String order = jsonObject.getString("order");
                if(order.equals("find")){
                    findPlayer();
                }else if(order.equals("eat")){
                    food = buildFood();
                    JSONObject jsonObject1 = new JSONObject();
                    JSONObject jsonObject2 = new JSONObject();
                    jsonObject1.put("newFood", food);
                    jsonObject2.put("rivalNewFood", food);
                    sendMessage(jsonObject1.toJSONString());
                    webSocketMap.get(rivalId).sendMessage(jsonObject1.toJSONString());
                }else{
                    changeDirection(order);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *	收到客户端消息后调用的方法
     *	@Author: pjj
     *	@Param: session 连接
     *  @Parm: 	error 错误
     *	@Date: 2022/03/05
     *	@return null
     **/
    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println("用户错误:" + this.userId + ",原因:" + error.getMessage());
        error.printStackTrace();
    }

    /**
     *	实现服务器主动推送
     *	@Author: pjj
     *	@Param: message 推送信息
     *	@Date: 2022/03/05
     *	@return null
     **/
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    /**
     *	匹配对手
     *	@Author: pjj
     *	@Param: null
     *	@Date: 2022/03/05
     *	@return null
     **/
    public void findPlayer() throws IOException, InterruptedException {
        // 如果当前没有其他玩家进行匹配则进入等待
        synchronized(ready){
            if(ready.equals("")){
                ready = userId;
            }else{
                rivalId = ready;
                ready = "";
                webSocketMap.get(rivalId).rivalId = userId;
                JSONObject jsonObject1 = new JSONObject();
                JSONObject jsonObject2 = new JSONObject();
                jsonObject1.put("order", "start");
                jsonObject2.put("order", "start");
                food = buildFood();
                webSocketMap.get(rivalId).food = buildFood();
                jsonObject1.put("food", food);
                jsonObject2.put("food", webSocketMap.get(rivalId).food);
                sendMessage(jsonObject1.toJSONString());
                webSocketMap.get(rivalId).sendMessage(jsonObject2.toJSONString());
            }
        }
    }

    /**
     *	告诉对手自己改变了方向
     *	@Author: pjj
     *	@Param: direction 改变后的方向
     *	@Date: 2022/03/05
     *	@return null
     **/
    public void changeDirection(String direction) throws IOException {
        webSocketMap.get(rivalId).sendMessage(direction);
    }

    /**
     *	随机生成食物
     *	@Author: pjj
     *	@Param: null
     *	@Date: 2022/03/05
     *	@return null
     **/
    public Food buildFood() throws IOException {
        Random random = new Random();
        int x = random.nextInt(25);
        int y = random.nextInt(19);
        return new Food(x, y);
    }

}