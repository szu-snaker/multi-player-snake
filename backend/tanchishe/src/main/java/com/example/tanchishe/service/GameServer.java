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

    Food food;          // 食物

    boolean[][] f;      // 地图是否可行

    boolean playing;    // 游戏是否进行

    public GameServer(Session session1, Session session2) throws IOException {
        this.session1 = session1;
        this.session2 = session2;
        f = new boolean[25][19];
        playing = true;
        int[] t1 = new int[]{4,1};
        int[] t2 = new int[]{20,17};
        LinkedList<int[]> snakeBody1 = new LinkedList<int[]>();
        LinkedList<int[]> snakeBody2 = new LinkedList<int[]>();
        f[t1[0]][t1[1]] = true;
        f[t2[0]][t2[1]] = true;
        snakeBody1.add(t1.clone());
        snakeBody2.add(t2.clone());
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
        food = buildFood();
        while((food.getX() == 4 && food.getY() == 1) || (food.getX() == 20 && food.getY() == 17)){
            food = buildFood();
        }
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("snake", snake1);
        jsonObject1.put("rivalSnake", snake2);
        jsonObject1.put("food", food);
        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.put("snake", snake2);
        jsonObject2.put("rivalSnake", snake1);
        jsonObject2.put("food", food);
        session1.getBasicRemote().sendText(jsonObject1.toJSONString());
        session2.getBasicRemote().sendText(jsonObject2.toJSONString());
    }

    @Override
    public void run() {
        while(playing){
            LinkedList<int[]> snakeBody1 = snake1.getSnakeBody();
            int direction1 = snake1.getDirection();
            int[] t1 = snakeBody1.get(0).clone();
            LinkedList<int[]> snakeBody2 = snake2.getSnakeBody();
            int direction2 = snake2.getDirection();
            int[] t2 = snakeBody2.get(0).clone();
            if(direction1 == 0){
                t1[1] --;
                if(t1[1] < 0){
                    try {
                        ganmeOver(false);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }else if(direction1 == 1){
                t1[0] ++;
                if(t1[0] == 25){
                    try {
                        ganmeOver(false);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }else if(direction1 == 2){
                t1[1] ++;
                if(t1[1] == 19){
                    try {
                        ganmeOver(false);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }else{
                t1[0] --;
                if(t1[0] < 0){
                    try {
                        ganmeOver(false);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
            if(direction2 == 0){
                t2[1] --;
                if(t2[1] < 0){
                    try {
                        ganmeOver(true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }else if(direction2 == 1){
                t2[0] ++;
                if(t2[0] == 25){
                    try {
                        ganmeOver(true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }else if(direction2 == 2){
                t2[1] ++;
                if(t2[1] == 19){
                    try {
                        ganmeOver(true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }else{
                t2[0] --;
                if(t2[0] < 0){
                    try {
                        ganmeOver(true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
            if(f[t1[0]][t1[1]]){
                try {
                    ganmeOver(false);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
            snake1.getSnakeBody().addFirst(t1);
            f[t1[0]][t1[1]] = true;
            boolean f1 = false;
            if(!(t1[0] == food.getX() && t1[1] == food.getY())){
                int[] temp = snake1.getSnakeBody().removeLast();
                f[temp[0]][temp[1]] = false;
            }else{
                f1 = true;
            }
            if(f[t2[0]][t2[1]]){
                try {
                    ganmeOver(true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
            snake2.getSnakeBody().addFirst(t2);
            f[t2[0]][t2[1]] = true;
            boolean f2 = false;
            if(!(t2[0] == food.getX() && t2[1] == food.getY())){
                int[] temp = snake2.getSnakeBody().removeLast();
                f[temp[0]][temp[1]] = false;
            }else{
                f2 = true;
            }
            Map<String, Object> newSnake1 = new HashMap<String, Object>();
            Map<String, Object> newSnake2 = new HashMap<String, Object>();

            newSnake1.put("newHeadX", t1[0]);
            newSnake1.put("newHeadY", t1[1]);
            newSnake1.put("eat", f1);
            newSnake1.put("direction", snake1.getDirection());

            newSnake2.put("newHeadX", t2[0]);
            newSnake2.put("newHeadY", t2[1]);
            newSnake2.put("eat", f2);
            newSnake2.put("direction", snake2.getDirection());

            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("mySnake", newSnake1);
            jsonObject1.put("rivalSnake", newSnake2);

            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("mySnake", newSnake2);
            jsonObject2.put("rivalSnake", newSnake1);

            if(f1 || f2){
                Map<String, Object> newFood = new HashMap<String, Object>();
                food = buildFood();
                newFood.put("X", food.getX());
                newFood.put("Y", food.getY());
                jsonObject1.put("newFood", newFood);
                jsonObject2.put("newFood", newFood);
            }
            try {
                session1.getBasicRemote().sendText(jsonObject1.toJSONString());
                session2.getBasicRemote().sendText(jsonObject2.toJSONString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(1000);
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
            session2.getBasicRemote().sendText(jsonObject.toJSONString());
        }else{
            session1.getBasicRemote().sendText(jsonObject.toJSONString());
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
        }else{
            // 改变P2玩家蛇的朝向
            synchronized (snake2) {
                snake2.setDirection(newDirection);
            }
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
