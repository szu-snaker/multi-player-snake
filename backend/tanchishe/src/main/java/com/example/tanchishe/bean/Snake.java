package com.example.tanchishe.bean;

import java.util.LinkedList;

/**
 * @Author: pjj
 * @Classname: Snake
 * @Date: 2022/03/08
 * @Description: 蛇对象
 */
public class Snake {

    LinkedList<int[]> snakeBody = new LinkedList<int[]>();
    int direction;  // 蛇的朝向

    public Snake(LinkedList<int[]> snakeBody, int direction) {
        this.snakeBody = snakeBody;
        this.direction = direction;
    }

    @Override
    public String toString() {
        return "Snake{" +
                "snakeBody=" + snakeBody.toString() +
                ", direction=" + direction +
                '}';
    }

    public LinkedList<int[]> getSnakeBody() {
        return snakeBody;
    }

    public void setSnakeBody(LinkedList<int[]> snakeBody) {
        this.snakeBody = snakeBody;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }
}
