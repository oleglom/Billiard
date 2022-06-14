package com.example.billiard;

import android.graphics.Color;

public class Lamp {
    //высота освещения над столом в соотношении от ширины стола
    double h = 1;
    //координаты освещения(х от длины и y от ширины)
    double x = 0.5;
    double y = 0.5;
    //игровое поле
    Field field;
    //цвет тени
    int sh_color = Color.argb(150, 0, 0, 0);
    //цвет блика
    int light_color = Color.argb(10, 255, 255, 255);
    //радиус блика (от радиуса шара)
    double light_rad = 0.5;
    public Lamp(Field field, double hight){
        //получение поля
        this.field = field;
        this.h = hight;

    }
    //расчет тени
    double[] getPose(Ball b){
        double[] xyi = new double[3];
        //формула пространсвенной прямой по тсамой высшей точке шара и светильнику
        // z->x
        double plus;
        double umn;
        if (x!=b.x){
        umn = (h-b.rad)/(x-b.x);
        plus = - (b.x*umn) + b.rad;
        xyi[0] = (-plus)/umn;}
        else{
            xyi[0] = x;
        }
        // z->y
        if (y!=b.y){
        umn = (h-b.rad)/(y-b.y);
        plus = - (b.y*umn) + b.rad;
        xyi[1] = (-plus)/umn;}
        else{
            xyi[1] = y;
        }
        //проверка на наложение на бортик
        double x = xyi[0];
        double y = xyi[1];
        if (x - b.rad<field.hx + field.bt + field.gb | x + b.rad>field.hx - field.bt - field.bt + field.w | y - b.rad<field.hy + field.bt + field.bt | y+ b.rad>field.hy - field.bt -field.bt + field.h){
            xyi[2] = 1;
        }
        else {
            xyi[2] = 0;
        }
        return xyi;

    }
    double[] getLight(Ball b){
        double[] xy = new double[2];
        //Вектор шар -> лампа
        double x = this.x - b.x;
        double y = this.y - b.y;
        double z = this.h - b.rad;
        //нужный вектор
        double k = b.kof_by_param(b.rad, x, y, z);
        xy[0] = x*k + b.x;
        xy[1] = y*k + b.y;
        return xy;

    }

}
