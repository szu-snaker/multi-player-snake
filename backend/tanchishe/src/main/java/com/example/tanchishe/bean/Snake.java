package com.example.tanchishe.bean;

import java.util.LinkedList;

/**
 * @Author: pjj
 * @Classname: Snake
 * @Date: 2022/03/08
 * @Description: 蛇对象
 */
public class Snake {

    String userName;         // 蛇的玩家ID
    int direction;  // 蛇的朝向
    LinkedList<int[]> snakeBody = new LinkedList<int[]>();  // 蛇的身体下标数组

    public Snake(String userName, int direction, LinkedList<int[]> snakeBody) {
        this.userName = userName;
        this.direction = direction;
        this.snakeBody = snakeBody;
    }

    @Override
    public String toString() {
        return "Snake{" +
                "userName=" + userName +
                ", direction=" + direction +
                ", snakeBody=" + snakeBody +
                '}';
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public LinkedList<int[]> getSnakeBody() {
        return snakeBody;
    }

    public void setSnakeBody(LinkedList<int[]> snakeBody) {
        this.snakeBody = snakeBody;
    }
}
