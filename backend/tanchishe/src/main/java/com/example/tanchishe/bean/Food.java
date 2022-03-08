package com.example.tanchishe.bean;

/**
 * @Author: pjj
 * @Classname: Food
 * @Date: 2022/03/08
 * @Description: 食物对象
 */
public class Food {
    private int x; // 食物的x坐标
    private int y; // 食物的y坐标

    public Food(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "Food{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
