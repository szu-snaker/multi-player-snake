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

    Session session1;   // P1的连接
    Session session2;   // P2的连接

    Snake snake1;       // P1的蛇
    Snake snake2;       // P2的蛇

    boolean changeFlag1;// P1的蛇改变方向
    boolean changeFlag2;// P2的蛇改变方向

    Food food;          // 食物

    boolean[][] f;      // 地图是否可行

    boolean playing;    // 游戏是否进行

    public GameServer(Session session1, Session session2) throws IOException {
        // 玩家的连接
        this.session1 = session1;
        this.session2 = session2;
        // 初始化地图
        f = new boolean[27][20];
        // 游戏中
        playing = true;
        // 玩家的蛇头初始化
        int[] t1 = new int[]{4,1};
        int[] t2 = new int[]{22,18};
        LinkedList<int[]> snakeBody1 = new LinkedList<int[]>();
        LinkedList<int[]> snakeBody2 = new LinkedList<int[]>();
        f[t1[0]][t1[1]] = true;
        f[t2[0]][t2[1]] = true;
        snakeBody1.add(t1.clone());
        snakeBody2.add(t2.clone());
        // 玩家的蛇身初始化
        for(int i = 0; i < 3; i ++){
            t1[0] --;
            snakeBody1.add(t1.clone());
            t2[0] ++;
            snakeBody2.add(t2.clone());
            f[t1[0]][t1[1]] = true;
            f[t2[0]][t2[1]] = true;
        }
        snake1 = new Snake(snakeBody1, 1);
        snake2 = new Snake(snakeBody2, 3);
        // 生成食物
        food = buildFood();
        // 如果一开始食物就在蛇头就重新生成
        while((food.getX() == 4 && food.getY() == 1) || (food.getX() == 22 && food.getY() == 18)){
            food = buildFood();
        }
        // 发送游戏初始信息给客户端
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("mySnakeInit", snake1);
        jsonObject1.put("rivalSnakeInit", snake2);
        jsonObject1.put("food", food);
        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.put("mySnakeInit", snake2);
        jsonObject2.put("rivalSnakeInit", snake1);
        jsonObject2.put("food", food);
        session1.getBasicRemote().sendText(jsonObject1.toJSONString());
        session2.getBasicRemote().sendText(jsonObject2.toJSONString());
    }

    @Override
    public void run() {
        while(playing){
            // 获取蛇头朝向
            LinkedList<int[]> snakeBody1 = snake1.getSnakeBody();
            int direction1 = snake1.getDirection();
            int[] t1 = snakeBody1.get(0).clone();

            LinkedList<int[]> snakeBody2 = snake2.getSnakeBody();
            int direction2 = snake2.getDirection();
            int[] t2 = snakeBody2.get(0).clone();

            // 更新新的蛇头位置
            if(direction1 == 0){ // P1蛇头朝上
                t1[1] --;
                if(t1[1] < 0){ // 撞墙游戏结束
                    try {
                        ganmeOver(false);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }else if(direction1 == 1){ // P1蛇头朝右
                t1[0] ++;
                if(t1[0] == 27){ // 撞墙游戏结束
                    try {
                        ganmeOver(false);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }else if(direction1 == 2){ // P1蛇头朝下
                t1[1] ++;
                if(t1[1] == 20){ // 撞墙游戏结束
                    try {
                        ganmeOver(false);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }else{ // P1蛇头朝左
                t1[0] --;
                if(t1[0] < 0){ // 撞墙游戏结束
                    try {
                        ganmeOver(false);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }

            if(direction2 == 0){ // P2蛇头朝上
                t2[1] --;
                if(t2[1] < 0){ // 撞墙游戏结束
                    try {
                        ganmeOver(true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }else if(direction2 == 1){ // P2蛇头朝右
                t2[0] ++;
                if(t2[0] == 27){ // 撞墙游戏结束
                    try {
                        ganmeOver(true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }else if(direction2 == 2){ // P2蛇头朝下
                t2[1] ++;
                if(t2[1] == 20){ // 撞墙游戏结束
                    try {
                        ganmeOver(true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }else{ // P2蛇头朝左
                t2[0] --;
                if(t2[0] < 0){ // 撞墙游戏结束
                    try {
                        ganmeOver(true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }

            if(f[t1[0]][t1[1]]){ // P1蛇头撞到蛇身
                try {
                    ganmeOver(false);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
            // 更新P1的蛇的状态
            snake1.getSnakeBody().addFirst(t1);
            f[t1[0]][t1[1]] = true;
            boolean f1 = false;
            // P1的蛇没有吃到食物，保持蛇的原本长度，原本蛇移动要整个数组整体移动 == 旧蛇尾下标去掉
            if(!(t1[0] == food.getX() && t1[1] == food.getY())){
                int[] temp = snake1.getSnakeBody().removeLast();
                f[temp[0]][temp[1]] = false;
            }else{
                f1 = true;
            }
            if(f[t2[0]][t2[1]]){ // P2蛇头撞到蛇身
                try {
                    ganmeOver(true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
            // 更新P2的蛇的状态
            snake2.getSnakeBody().addFirst(t2);
            f[t2[0]][t2[1]] = true;
            boolean f2 = false;
            // P1的蛇没有吃到食物，保持蛇的原本长度，原本蛇移动要整个数组整体移动 == 旧蛇尾下标去掉
            if(!(t2[0] == food.getX() && t2[1] == food.getY())){
                int[] temp = snake2.getSnakeBody().removeLast();
                f[temp[0]][temp[1]] = false;
            }else{
                f2 = true;
            }
            Map<String, Object> newSnake1 = new HashMap<String, Object>();
            Map<String, Object> newSnake2 = new HashMap<String, Object>();
            // 新蛇头
            newSnake1.put("newHeadX", t1[0]);
            newSnake1.put("newHeadY", t1[1]);
            if(f1) { // 食到食物则告诉客户端
                newSnake1.put("eat", f1);
            }
            if(changeFlag1) { // 改变方向则告诉客户端
                changeFlag1 = false;
                newSnake1.put("direction", snake1.getDirection());
            }
            // 新蛇头
            newSnake2.put("newHeadX", t2[0]);
            newSnake2.put("newHeadY", t2[1]);
            if(f2) { // 食到食物则告诉客户端
                newSnake2.put("eat", f2);
            }
            if(changeFlag2) { // 改变方向则告诉客户端
                changeFlag2 = false;
                newSnake2.put("direction", snake2.getDirection());
            }
            // 封装两条蛇新的状态
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("mySnake", newSnake1);
            jsonObject1.put("rivalSnake", newSnake2);

            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("mySnake", newSnake2);
            jsonObject2.put("rivalSnake", newSnake1);
            // 吃到食物就生成新食物并告知客户端
            if(f1 || f2){
                food = buildFood();
                jsonObject1.put("food", food);
                jsonObject2.put("food", food);
            }
            try { // 发送数据给客户端
                session1.getBasicRemote().sendText(jsonObject1.toJSONString());
                session2.getBasicRemote().sendText(jsonObject2.toJSONString());
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
    public void ganmeOver(boolean flag) throws IOException {
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("gameOver", "win");
        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.put("gameOver", "lose");
        if(flag){
            session1.getBasicRemote().sendText(jsonObject1.toJSONString());
            session2.getBasicRemote().sendText(jsonObject2.toJSONString());
        }else{
            session2.getBasicRemote().sendText(jsonObject1.toJSONString());
            session1.getBasicRemote().sendText(jsonObject2.toJSONString());
        }
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
            if(session2.isOpen()) {
                session2.getBasicRemote().sendText(jsonObject.toJSONString());
            }
        }else{
            if(session1.isOpen()) {
                session1.getBasicRemote().sendText(jsonObject.toJSONString());
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
        if(snakeID == 0){
            // 改变P1玩家蛇的朝向
            synchronized (snake1) {
                snake1.setDirection(newDirection);
            }
            changeFlag1 = true;
        }else{
            // 改变P2玩家蛇的朝向
            synchronized (snake2) {
                snake2.setDirection(newDirection);
            }
            changeFlag2 = true;
        }
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
