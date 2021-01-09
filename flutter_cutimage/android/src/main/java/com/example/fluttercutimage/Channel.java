package com.example.fluttercutimage;

/**
 * Created Date: 2020/7/22
 * Description:
 */
public class Channel {
    public  String id;
    public String ench;
    public String x;
    public String y;
    public String w;
    public String h;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEnch() {
        return ench;
    }

    public void setEnch(String ench) {
        this.ench = ench;
    }

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public String getY() {
        return y;
    }

    public void setY(String y) {
        this.y = y;
    }

    public String getW() {
        return w;
    }

    public void setW(String w) {
        this.w = w;
    }

    public String getH() {
        return h;
    }

    public void setH(String h) {
        this.h = h;
    }

    @Override
    public String toString() {
        return "Channel{" +
                "id='" + id + '\'' +
                ", ench='" + ench + '\'' +
                ", x='" + x + '\'' +
                ", y='" + y + '\'' +
                ", w='" + w + '\'' +
                ", h='" + h + '\'' +
                '}';
    }
}
