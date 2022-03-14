package com.example.tanchishe.service;


import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.*;

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

    // 存储游戏对战线程的线程池
    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(100, 100, 0, TimeUnit.SECONDS,
                                                                                    new ArrayBlockingQueue<Runnable>(512), new ThreadPoolExecutor.CallerRunsPolicy());

    // 匹配中玩家
    private static String ready = "";

    // 与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    // 接收userName
    private String userName;

    // 游戏
    private GameServer gameServer;

    // 几号蛇
    private int snakeId;

    /**
     * 获取所有的连接
     *
     * @Author: pjj
     * @Param: null
     * @Date: 2022/03/05
     * @return: getWebSocketMap所有与客户端的连接
     **/
    public static ConcurrentHashMap<String, WebSocketServer> getWebSocketMap() {
        return webSocketMap;
    }

    /**
     * 设置所有的连接
     *
     * @Author: pjj
     * @Param: webSocketMap 所有与客户端的连接
     * @Date: 2022/03/05
     * @return: null
     **/
    public static void setWebSocketMap(ConcurrentHashMap<String, WebSocketServer> webSocketMap) {
        WebSocketServer.webSocketMap = webSocketMap;
    }

    /**
     * 连接建立成功调用的方法
     *
     * @Author: pjj
     * @Param: session 与客户端的连接
     * @Param: userId 用户ID
     * @Date: 2022/03/05
     * @return: null
     **/
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        this.session = session;
        this.userName = userId;
        System.out.println("open connection:" + userId);
        // 将用户ID和它对应的连接存储到map中
        if (webSocketMap.containsKey(userId)) {
            webSocketMap.remove(userId);
            webSocketMap.put(userId, this);
        } else {
            webSocketMap.put(userId, this);
        }
    }

    /**
     * 连接关闭调用的方法
     *
     * @Author: pjj
     * @Param: null
     * @Date: 2022/03/05
     * @return: null
     **/
    @OnClose
    public void onClose() throws IOException {
        gameServer.gameStop(snakeId);
        if (webSocketMap.containsKey(userName)) {
            webSocketMap.remove(userName);
        }
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @Author: pjj
     * @Param: message 客户端发送过来的消息
     * @Date: 2022/03/05
     * @return: null
     **/
    @OnMessage
    public void onMessage(String message, Session session) {
        if (StrUtil.isNotBlank(message)) {
            try {
                System.out.println("onmessage:" + message);
                // 存储json信息
                JSONObject jsonObject = JSONObject.parseObject(message);
                // 获取指令
                String order = jsonObject.getString("order");
                if (order.equals("find")) {
                    findPlayer();
                } else {
                    if (order.equals("left")) {
                        gameServer.setSnakeDirection(snakeId, 3);
                    } else if (order.equals("right")) {
                        gameServer.setSnakeDirection(snakeId, 1);
                    } else if (order.equals("up")) {
                        gameServer.setSnakeDirection(snakeId, 0);
                    } else if (order.equals("down")) {
                        gameServer.setSnakeDirection(snakeId, 2);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @Author: pjj
     * @Param: session 连接
     * @Parm: error 错误
     * @Date: 2022/03/05
     * @return: null
     **/
    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    /**
     * 实现服务器主动推送
     *
     * @Author: pjj
     * @Param: message 推送信息
     * @Date: 2022/03/05
     * @return: null
     **/
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    /**
     * 匹配对手
     *
     * @Author: pjj
     * @Param: null
     * @Date: 2022/03/05
     * @return: null
     **/
    public void findPlayer() throws IOException, InterruptedException {
        // 如果当前没有其他玩家进行匹配则进入等待
        String rivalId = "";
        synchronized (ready) {
            if (ready.equals("")) {
                ready = userName;
                return;
            } else {
                rivalId = ready;
                ready = "";
            }
        }
        // 游戏初始化
        Session[] sessions = new Session[2];
        String[] userNames = new String[2];
        sessions[0] = webSocketMap.get(rivalId).session;
        sessions[1] = this.session;
        userNames[0] = rivalId;
        userNames[1] = userName;
        gameServer = new GameServer(2, sessions, userNames);
        webSocketMap.get(rivalId).gameServer = gameServer;
        snakeId = 1;
        webSocketMap.get(rivalId).snakeId = 0;
        Thread.sleep(3000);
        // 游戏开始
        threadPoolExecutor.execute(gameServer);
    }

}
