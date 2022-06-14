package com.example.billiard;

import android.graphics.Color;

public class Ball {
    double x;
    double y;
    int rad;
    int num;
    //находится ли шар в игре
    boolean on = true;
    //массив цветов и их соответствия индекксу шара
    int[] cls = new int[16];
    //цвет шара
    int color;
    //скорость шара пикселей/обновление
    double vx = 0;//2.5 - Math.random() * 5;//-15;
    double vy = 0;//2.5 - Math.random() * 5;//- Math.random() * 10;
    //белые области по краям (только у полосатых) доля от реального радиуса
    double mp_rad = 0.7;
    double[] pb = new double[3];
    double[] pb2 = new double[3];
    //разброс рандома наклона при стартовом положении (2 - максимум, 0 - минимум)
    double kof_rand = 1.2;
    //закат шара
    boolean zkt = false;
    boolean finish = false;
    boolean start_sc = false;
    public Ball(int num, int rad, double x, double y){
        //получение положения шара
        this.x = x;
        this.y = y;
        //загрузка цветов шаров
        {cls[0]=Color.argb(255, 242,243,230);
        cls[1] = Color.argb(255, 194, 165, 82);
        cls[2] = Color.argb(255, 48, 116, 159);
        cls[3] = Color.argb(255, 164, 51, 70);
        cls[4] = Color.argb(255, 94, 71, 136);
        cls[5] = Color.argb(255, 231, 134, 87);
        cls[6] = Color.argb(255, 80, 152, 104);
        cls[7] = Color.argb(255, 134, 72, 74);
        cls[8] = Color.argb(255, 29, 28, 36);
        cls[9] = cls[1];
        cls[10] = cls[2];
        cls[11] = cls[3];
        cls[12] = cls[4];
        cls[13] = cls[5];
        cls[14] = cls[6];
        cls[15] = cls[7];


        }
        //определение номера шара
        this.num = num;
        //определение цвета по номеру
        this.color = cls[num];
        //стартовая установка белых областей
        {
        pb[0]=1-Math.random() * kof_rand;
        pb[1]=Math.sqrt(1-pb[0]*pb[0]) - Math.random() *  kof_rand * Math.sqrt(1-pb[0]*pb[0]);
        pb[2]=Math.sqrt(1 - pb[0]*pb[0] - pb[1]*pb[1]);
        pb2[0]=-pb[0];
        pb2[1]=-pb[1];
        pb2[2]=-pb[2];
        }
        //получение радиуса
        this.rad = rad;
        this.mp_rad = this.rad * this.mp_rad;

    }
    //подсчет белых областей
    void setWhite(double speed_x, double speed_y){
        //модуль угол поворта
        double ang = Math.sqrt(speed_x*speed_x + speed_y*speed_y)/(this.rad*2*3.14) * 6.28;
        if (ang!=0){
            //знак знак угла поворота
            if (speed_x<0){
                ang = -ang;
            }
            //получение реальных положений центра области
            double x1 = this.rad * this.pb[0]; double y1 = this.rad * this.pb[1]; double z = this.rad * this.pb[2];
            //координаты главного вектора движения
            double fx = speed_x; double fy = speed_y;
            //приведение к положительному вектору
            if (speed_x<0){
                fx = -speed_x;
                fy = -speed_y;
            }
            //расчет центра и радиуса окружности движения
            double k = 0;
            double k2 = 0;
            {double c = (x1*x1 + y1*y1) - this.rad*this.rad;
                double a = fx*fx + fy*fy;
                double b1 = 2*(fx*x1 + fy*y1);

                if (a!=0){
                    k = (-b1 + Math.sqrt(b1*b1 - 4*a*c))/(2*a);
                    k2 = (-b1 - Math.sqrt(b1*b1 - 4*a*c))/(2*a);
                }


            }
            double r_paral = Math.sqrt(fx*fx*k2*k2 + fy*fy*k2*k2) + Math.sqrt(fx*fx*k*k + fy*fy*k*k);
            r_paral = r_paral/2;
            double ex = x1 + k2*fx;
            double ey = y1 + k2*fy;

            double p_k = kof_by_param(r_paral, fx, fy, 0);
            double gx = ex + fx*p_k;
            double gy = ey + fy*p_k;

            //расчет координат точки относительно окружности
            double sin_o = this.pb[2];
            if (r_paral!=0){
                sin_o = this.pb[2] * this.rad / r_paral;}


            double cos_o = 0;
            if (x1>gx){
                cos_o = Math.sqrt(1-sin_o*sin_o);
            }
            if (x1<gx){
                cos_o = -Math.sqrt(1-sin_o*sin_o);
            }
            if (x1==gx){
                if ((pb[1]*rad-gy)*fy>=0){
                    cos_o = Math.sqrt(1-sin_o*sin_o);}
                else{
                    cos_o = -Math.sqrt(1-sin_o*sin_o);
                }
            }

            double new_cos = cos_o*Math.cos(ang) + sin_o*Math.sin(ang);
            double new_sin = sin_o*Math.cos(ang) - cos_o*Math.sin(ang);
            double l = new_cos * r_paral;
            this.pb[2]  = new_sin*r_paral/this.rad;
            this.pb[0]  = (gx + kof_by_param(l, fx, fy, 0)*fx)/rad;
            this.pb[1] = (gy + kof_by_param(l, fx, fy, 0)*fy)/rad;
            this.pb2[0] = -this.pb[0];
            this.pb2[1] = -this.pb[1];
            this.pb2[2] = -this.pb[2];

            }


    }
    //расчет белых областей каждый кадр
    //вспомогательная функция подсчета белых областей
    double kof_by_param(double l, double fx, double fy, double fz){
        if (fx==0 & fy==0 & fz == 0){
            return 1;
        }
        double k = Math.sqrt(l*l / (fx*fx + fy*fy + fz*fz));
        if (l<0){
            k = -k;
        }
        return  k;
    }


}
