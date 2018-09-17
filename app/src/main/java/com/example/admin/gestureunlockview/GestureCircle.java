package com.example.admin.gestureunlockview;

/**
 *
 * Created by xiesuichao on 2018/4/18.
 */

public class GestureCircle {

    private float x;
    private float y;
    private int position;
    private boolean checked;

    public GestureCircle() {
    }

    GestureCircle(float x, float y, int position, boolean checked) {
        this.x = x;
        this.y = y;
        this.position = position;
        this.checked = checked;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @Override
    public String toString() {
        return "GestureCircle{" +
                "x=" + x +
                ", y=" + y +
                ", position=" + position +
                ", checked='" + checked + '\'' +
                '}';
    }
}
