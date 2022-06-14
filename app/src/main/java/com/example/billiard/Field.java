package com.example.billiard;

import android.graphics.Bitmap;
import android.graphics.Color;

public class Field {
    //верхний левый угол поля
    int hx = 10;
    int hy = 10;
    //размеры экрана
    double window_w;
    double window_h;
    //размер борта в процентах от ширины
    double bt = 0.085;
    //размер поля (отношение длин д/ш)
    double s_p =2;
    //реальные размеры поля
    int w;
    int h;
    //размер отступа в соотношении с высотой
    double pn_hu = 0.18; //отступ сверху
    double pn_hd = 0.09; //отступ снизу
    //отношение размеров темного бортика ко всему
    double gb = 0.368;
    //отношение радиуса лунки к длине борта
    double lb = 0.55;
    //расположение лунки относительно борта
    double cx = 0.96;
    double cy = 0.85;
    //угол наклона бортика у крайних лунок (угол между наклоном и осью абсцисс)
    double bd_ang = 3.14/4;
    //создание массива с лунками
    Luza[] luns = new Luza[6];
    //цвета поля
    int bg = Color.argb(255, 37, 36, 32);
    //int bg = Color.argb(255, 64, 50, 34);
    //создание освещения
    Lamp lamp;
    //коорнинаты аватарки
    double avx = 0; // от шириня
    double avy = 0.1; // от оставшейся высоты без поля
    //размеры аватарки
    double avw = 0.9; //от отведенного размера (высота без поля - игрик)
    //отступ наклона от длины
    double otp_d = 0.015;
    //размеры кинки руки от диаметра лузы
    double handw = 1;
    //параметры ската шаров
    double sc_x;
    double sc_y;
    double sc_w = 0.35;//от длины
    double sc_h = 0.4; //расчет через радиус шаров (тут отступ от радиуса)
    double sc_t; //расчитанная толщина борта
    double sc_block_x;
    double sc_sub_t;
    double sc_sub_arc = 0.9; //от диаметра шара
    int sc_color = Color.argb(255, 73, 36, 28);
    int sc_color_bg = Color.argb(255, 54, 36, 32);
    //блок загрузки силы удара
    double load_x;
    double load_y;
    double load_w;
    double load_h;
    double load_t;
    int load_ob_color = Color.argb(255, 116, 71, 37);
    int load_in_color = Color.argb(255, 161, 115, 66);
    //параметры кия
    double kiy_w = 0.4; //от длины поля
    double kiy_h = 0.02687; //от длины кия
    double mx_ot = 0.3; // отступ при ударе от длины кия
    public Field(double ww, double wh, Img imgs, double hight, double radH) {
        lamp = new Lamp(this, hight);
        //получение размеров окон
        {
        this.window_w = ww;
        this.window_h = wh;}
        //расчет размеров игрового поля
        {hy = (int)(wh * pn_hu);
        pn_hd = wh * pn_hd;
        h = (int)(wh - hy - pn_hd);
        w = (int)ww;
        while(h*s_p>w){
            h = h - 1;
        }
        w  = (int)(h *s_p);
        //центрирование
        hx = (int)((ww-w)/2);
        }
        //расчет размеров лунок и бортов
        {
        bt = h*bt;
        lb = bt*lb;
        gb = gb*bt;
        otp_d = otp_d * w;}
        //расчет gui
        {
            //расчет аватрок
            avx = this.hx;
            avy = this.hy * avy;
            avw = (this.hy - this.avy) * avw;
            //расчет иконок руки
            handw = lb * 2 * handw;
        }
        //создание лунок
        {
            luns[0] = new Luza((float) (this.cx * this.bt  + hx), (float) (this.cy * this.bt + hy), (float) this.lb, 0);
            luns[1] = new Luza((float) (this.w / 2 + hx), (float) (this.cy * this.bt + hy), (float) this.lb, 1);
            luns[2] = new Luza((float) (this.w - this.cx * this.bt + hx), (float) (this.cy * this.bt + hy), (float) this.lb, 2);
            luns[3] = new Luza((float) (this.cx * this.bt + hx), (float) (this.h - this.cy * this.bt + hy), (float) this.lb, 3);
            luns[4] = new Luza((float) (this.w / 2 + hx), (float) (this.h - this.cy * this.bt + hy), (float) this.lb, 4);
            luns[5] = new Luza((float) (this.w - this.cx * this.bt + hx), (float) (this.h - this.cy * this.bt + hy), (float) this.lb, 5);
        }
        //расчет кия
        {
            kiy_w = this.w * kiy_w;
            kiy_h = kiy_h*kiy_w;
            mx_ot = kiy_w*mx_ot;
        }
        //редактирование размеров изображений
        {
            imgs.bl_field[0] = Bitmap.createScaledBitmap(imgs.bl_field[0], w, h, true);
            imgs.bl_field[1] = Bitmap.createScaledBitmap(imgs.bl_field[1], w, h, true);
            //аватарки
            for (int i =0; i <imgs.avatars.length; i++){
                imgs.avatars[i] = Bitmap.createScaledBitmap(imgs.avatars[i], (int)avw, (int)avw, true);
            }
            //иконки во время игры
            imgs.hand[0] = Bitmap.createScaledBitmap(imgs.hand[0], (int)handw, (int)handw, true);
            imgs.hand[1] = Bitmap.createScaledBitmap(imgs.hand[1], (int)handw, (int)handw, true);
            imgs.hand[2] = Bitmap.createScaledBitmap(imgs.hand[2], (int)(radH*this.h*2), (int)(radH*this.h*2), true);
            //кии
            for (int i = 0; i < imgs.kiy.length; i++){
                imgs.kiy[i] = Bitmap.createScaledBitmap(imgs.kiy[i], (int)kiy_w, (int)kiy_h, true);
            }


        };
        //расчет освещения
        {
            lamp.h = lamp.h*this.h;
            lamp.x = this.hx + this.w * lamp.x;
            lamp.y = this.hy + this.h * lamp.y;
        }
        //расчет ската шаров
        {
            sc_w = this.w*sc_w;
            sc_h = this.h*radH * (2 + sc_h);
            sc_t = sc_h - this.h*radH*2;
            sc_sub_t = sc_t/0.9;
            sc_x = this.hx + this.cx*this.bt+this.lb + this.w/6;
            sc_y = this.hy + this.h;
            sc_block_x = sc_x + sc_w - this.h*radH - sc_t;
        }
        //расчет поля загрузки удара
        {
            load_x = this.hx + this.w*0.7;
            load_y = this.hy+this.h;
            load_w = this.w*0.25;
            load_h = sc_h;
            load_t = 0.1*load_h;
        }

    }

}
