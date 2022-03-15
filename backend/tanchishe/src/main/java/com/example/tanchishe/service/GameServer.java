package com.example.tanchishe.service;

import com.alibaba.fastjson.JSONObject;
import com.example.tanchishe.bean.Food;
import com.example.tanchishe.bean.Snake;

import javax.websocket.Session;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;

public class GameServer implements Runnable {

    int playerNumber;       // 房间玩家数量

    String[] useNames;         // 玩家name

    Session sessions[];      // 与玩家的套接字接口数组

    Snake snakes[];          // 玩家的蛇

    boolean changeFlag[];   // 蛇改变方向

    Food foods[];            // 食物

    boolean[][] f;      // 地图是否可行

    boolean playing;    // 游戏是否进行

    public GameServer(int playerNumber, Session[] sessions, String[] useNames) throws IOException {
        this.playerNumber = playerNumber;
        // 玩家name
        this.useNames = useNames;
        // 玩家的连接
        this.sessions = sessions;
        // 初始化地图
        f = new boolean[27][20];
        changeFlag = new boolean[playerNumber];
        // 游戏中
        playing = true;
        // 玩家的蛇头初始化
        int[][] t = new int[][]{{4, 1}, {22, 18}};
        LinkedList<int[]>[] snakeBody = new LinkedList[playerNumber];
        for(int i = 0; i < playerNumber; i ++){
            snakeBody[i] = new LinkedList<int[]>();
        }
        for(int i = 0; i < playerNumber; i ++){
            f[t[i][0]][t[i][1]] = true;
            snakeBody[i].add(t[i].clone());
        }
        // 玩家的蛇身初始化
        for(int i = 0; i < 3; i ++){
            t[0][0] --;
            snakeBody[0].add(t[0].clone());
            t[1][0] ++;
            snakeBody[1].add(t[1].clone());
            f[t[0][0]][t[0][1]] = true;
            f[t[1][0]][t[1][1]] = true;
        }
        snakes = new Snake[playerNumber];
        snakes[0] = new Snake(useNames[0], 1, snakeBody[0]);
        snakes[1] = new Snake(useNames[1], 3, snakeBody[1]);
        // 生成食物
        foods = new Food[playerNumber];
        foods[0] = buildFood();
        foods[1] = buildFood();
        // 如果一开始食物就在蛇头就重新生成
        for(int i = 0; i < playerNumber; i ++){
            while((foods[i].getX() == 4 && foods[i].getY() == 1) || (foods[i].getX() == 22 && foods[i].getY() == 18)){
                foods[i] = buildFood();
            }
        }
        // 发送游戏初始信息给客户端
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("buildSnakes", snakes);
        jsonObject.put("food", foods);
        for(int i = 0; i < playerNumber; i ++){
            sessions[i].getBasicRemote().sendText(jsonObject.toJSONString());
        }
    }

    @Override
    public void run() {
        while(playing){
            // 获取蛇头下标和朝向
            LinkedList<int[]>[] snakeBody = new LinkedList[playerNumber];
            int direction[] = new int[playerNumber];
            for(int i = 0; i < playerNumber; i ++){
                snakeBody[i] = snakes[i].getSnakeBody();
                direction[i] = snakes[i].getDirection();
            }
            int[][] t = new int[playerNumber][];
            for(int i = 0; i < playerNumber; i ++){
                t[i] = snakeBody[i].get(0).clone();
            }
            // 更新新的蛇头位置
            for(int i = 0; i <playerNumber; i ++){
                if(direction[i] == 0){ // P1蛇头朝上
                    t[i][1] --;
                    if(t[i][1] < 0){ // 撞墙游戏结束
                        try {
                            ganmeOver(i);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return ;
                    }
                }else if(direction[i] == 1){ // P1蛇头朝右
                    t[i][0] ++;
                    if(t[i][0] == 27){ // 撞墙游戏结束
                        try {
                            ganmeOver(i);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return ;
                    }
                }else if(direction[i] == 2){ // P1蛇头朝下
                    t[i][1] ++;
                    if(t[i][1] == 20){ // 撞墙游戏结束
                        try {
                            ganmeOver(i);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return ;
                    }
                }else{ // P1蛇头朝左
                    t[i][0] --;
                    if(t[i][0] < 0){ // 撞墙游戏结束
                        try {
                            ganmeOver(i);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return ;
                    }
                }
            }
            // 推送给客户端的消息
            JSONObject jsonObject = new JSONObject();
            Map<String, Object>[] newSnakes = new Map[2];
            // 是否有食物被池
            boolean flag = false;
            // 判断蛇有没有撞死
            for(int i = 0; i < playerNumber; i ++) {
                if (f[t[i][0]][t[i][1]]) { // 蛇头撞到蛇身
                    try { // 游戏结束
                        ganmeOver(i);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                // 更新地图状态
                f[t[i][0]][t[i][1]] = true;
                newSnakes[i] = new HashMap<String, Object>();
                // 更新蛇的状态
                snakes[i].getSnakeBody().addFirst(t[i]);
                // 蛇的玩家名
                newSnakes[i].put("userName", snakes[i].getUserName());
                // 发送客户端新的蛇头位置
                newSnakes[i].put("newHeadX", t[i][0]);
                newSnakes[i].put("newHeadY", t[i][1]);
                int j = 0;
                for (; j < playerNumber; j++) {
                    // 吃到食物
                    if (t[i][0] == foods[j].getX() && t[i][1] == foods[j].getY()) {
                        newSnakes[i].put("eat", true);
                        foods[j] = buildFood();
                        flag = true;
                        break;
                    }
                }
                // 没吃到食物，去掉蛇尾
                if (j == playerNumber) {
                    int[] temp = snakes[i].getSnakeBody().removeLast();
                    f[temp[0]][temp[1]] = false;
                }
                // 判断蛇有没有改变了朝向
                if (changeFlag[i]) { // 改变方向则告诉客户端
                    changeFlag[i] = false;
                    newSnakes[i].put("direction", snakes[i].getDirection());
                }
            }
            // 封装蛇新的状态
            jsonObject.put("snakes", newSnakes);
            // 如果有食物被吃更新食物状态
            if(flag) {
                jsonObject.put("food", foods);
            }
            try { // 发送数据给客户端
                for(int i = 0; i < playerNumber; i ++){
                    sessions[i].getBasicRemote().sendText(jsonObject.toJSONString());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *	游戏结束
     *	@Author: pjj
     *	@Param: flag 玩家1是否获胜
     *	@Date: 2022/03/08
     *	@return: null
     **/
    public void ganmeOver(int i) throws IOException {
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("gameOver", "win");
        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.put("gameOver", "lose");
        if(i == 1){
            sessions[0].getBasicRemote().sendText(jsonObject1.toJSONString());
            sessions[1].getBasicRemote().sendText(jsonObject2.toJSONString());
        }else{
            sessions[1].getBasicRemote().sendText(jsonObject1.toJSONString());
            sessions[0].getBasicRemote().sendText(jsonObject2.toJSONString());
        }
        WebSocketServer.getWebSocketMap().get(useNames[0]).setGameServer(null);
        WebSocketServer.getWebSocketMap().get(useNames[1]).setGameServer(null);
    }

    /**
     *	游戏中断
     *	@Author: pjj
     *	@Param: snakeID 中断的玩家蛇ID
     *	@Date: 2022/03/08
     *	@return: null
     **/
    public void gameStop(int snakeID) throws IOException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("gameOver", "rivalExit");
        if(snakeID == 0){
            if(sessions[1].isOpen()) {
                sessions[1].getBasicRemote().sendText(jsonObject.toJSONString());
            }
        }else{
            if(sessions[0].isOpen()) {
                sessions[0].getBasicRemote().sendText(jsonObject.toJSONString());
            }
        }
        playing = false;
    }

    /**
     *	设置蛇的朝向
     *	@Author: pjj
     *	@Param: snakeID 蛇的编号
     *	@Param: newDirection 新的朝向
     *	@Date: 2022/03/08
     *	@return: null
     **/
    public void setSnakeDirection(int snakeID, int newDirection){
        synchronized (snakes[snakeID]) {
            snakes[snakeID].setDirection(newDirection);
        }
        changeFlag[snakeID] = true;
    }

    /**
     *	随机生成食物
     *	@Author: pjj
     *	@Param: null
     *	@Date: 2022/03/05
     *	@return: null
     **/
    public Food buildFood() {
        Random random = new Random();
        int x = random.nextInt(25);
        int y = random.nextInt(19);
        return new Food(x, y);
    }
}
