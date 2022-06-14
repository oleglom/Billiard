package com.example.billiard;

import android.util.Log;

public class Game {
    MainActivity main;
    //переуд полной зарядки (ms)
    double time_load = 3000;
    //константа громкости
    double loud = 0.8;
    //высота освещения
    double high_lamp = 1.3;

    /////////////////////
    //помощь с сменой игрока
    boolean dpsave = false;
    //информация о мышке/пальце
    double mouse_x = 0;
    double mouse_y = 0;
    int mouse_sost = 0;

    // -2 - разбивает бот, 0 - ожидание остановки шаров, -3 - проигрыш
    int game_state = -2;
    // 0 - идет разбивка, 1 - открытый стол
    int play_way = 0;
    //0 - ход противника, 1 - мой ход
    int player = 0;
    //размеры окна
    double window_w;
    double window_h;
    //классы
    Field field;
    Img imgs;
    //переменные
    //радиус шара
    int rad;
    double zkt_speed = 2;
    //отношение радиуса шара к ширине поля
    double radH = 0.0215;
    //параметры физики
    //потеря энергии при столкновении с бортом (оставшаяся часть)
    double en_los12 = 0.9;
    //точка расстановки пирамиды
    double polygonX;
    double polygonXW = 0.69;
    double polygonY;
    double polygonYH = 0.5;
    double epf = 0.45; //потеря энергии за пиксель пути
    //основной массив шаров
    Ball[] balls = new Ball[16];
    //параметры бортов
    //определение краев бортов
    double lx;
    double rx;
    double uy;
    double dy;
    //информация о стадии игры
    String state_info = "";
    //разрешение на удар из дома
    boolean okey = false;
    //проигрыш
    int player_w;
    String reason = "";
    int sc = 4000;
    //создание формул прямых у краев
    double lh_h_k;
    double lh_h_b;
    double rh_h_b;
    double rh_h_k;
    double rh_d_k;
    double rh_d_b;
    double lh_d_k;
    double lh_d_b;
    double ld_h_k;
    double ld_h_b;
    double rd_h_k;
    double rd_h_b;
    double ld_d_k;
    double ld_d_b;
    double rd_d_k;
    double rd_d_b;
    //предел скорости вне остановки
    double cry = 0.2;
    //максимальная сорость удара
    double m_hurt = 80;
    //на данный момент удар
    double now_hurt = 0;
    //fps
    int fps;
    //максимальное количество кадров до проигрыша
    int mx_sc;
    //скорость заряда
    double dob;
    //информация о пользователе и противнике
    Id id;
    Id enemy_id;
    //расстояние от длины поля - дом
    double dom = 0.3;
    //класс  звуков
    Music mP;
    //время между прогрыванием музыки
    int pause_ms = 0;
    int time_ms = 33;
    //состояние активности шаров перед ударом
    boolean[] proto_matrix = {true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true};
    //разрешение на звук удара
    double soud_p = 0;
    //класс искуссственного интелекта
    AI_bot Ai;
    //номер шара о который был перый удар
    int first_num = -1;
    //время заката 1 шара
    double z_time = 3000;
    int sc_steck = 0;
    //время полета шара максимальное от края до края
    double ml_time = 600;
    public Game(double w, double h, Img imgs, int fps, Id id, Id enemy_id, Music mP, int loud, int load, int lamp, MainActivity main) {
        //главный класс
        this.main = main;
        //получение настроек
        this.loud = (double) loud / 100;
        this.time_load = 1000 + 2000 * (double) load / 100;
        this.high_lamp = 0.5 + (double) (100 - lamp) / 100 * 2;
        //класс музыки
        this.mP = mP;
        //расчет звуков
        this.time_ms = this.time_ms / fps;
        //информация о пользователе и противнике
        this.id = id;
        this.enemy_id = enemy_id;

        //получение fps
        this.fps = fps;
        //получение размеров экрана из main активити
        {
            window_w = w;
            window_h = h;
        }
        this.imgs = imgs;
        //инициализация поля
        field = new Field(w, h, imgs, high_lamp, radH);
        //расчет параметров поля
        {
            //расчет радиуса шара
            this.rad = (int) (field.h * radH);
            //расчет точки расстановки пирмиды
            polygonX = field.w * polygonXW + field.hx;
            polygonY = field.h * polygonYH + field.hy;
        }
        //расчет параметров бортов
        {//определение краев бортов
            lx = field.hx + field.bt + field.gb;
            rx = field.hx - field.bt + field.w - field.gb;
            uy = field.hy + field.bt + field.gb;
            dy = field.hy - field.bt + field.h - field.gb;
            //определение формул прямых у краев
            lh_h_k = Math.tan(field.bd_ang);
            lh_h_b = (field.hy + field.bt) - Math.tan(field.bd_ang) * (field.hx + field.bt * field.cx + field.lb);
            lh_d_k = Math.tan(field.bd_ang);
            lh_d_b = field.hy + field.bt * field.cy + field.lb - lh_h_k * (field.hx + field.bt * field.cx);

            rh_h_k = Math.tan(-field.bd_ang);
            rh_h_b = (field.hy + field.bt) - Math.tan(-field.bd_ang) * (field.hx + field.w - field.bt * field.cx - field.lb);
            rh_d_k = Math.tan(-field.bd_ang);
            rh_d_b = (field.hy + field.bt + field.lb) - Math.tan(-field.bd_ang) * (field.hx + field.w - field.bt);

            ld_h_k = Math.tan(-field.bd_ang);
            ld_h_b = (field.hy + field.h - field.bt - field.lb) - Math.tan(-field.bd_ang) * (field.hx + field.bt);
            ld_d_k = Math.tan(-field.bd_ang);
            ld_d_b = (field.hy + field.h - field.bt) - Math.tan(-field.bd_ang) * (field.hx + field.bt + field.lb);

            rd_h_k = Math.tan(field.bd_ang);
            rd_h_b = (field.hy + field.h - field.bt - field.lb) - Math.tan(field.bd_ang) * (field.hx + field.w - field.bt);
            rd_d_k = Math.tan(field.bd_ang);
            rd_d_b = (field.hy + field.h - field.bt) - Math.tan(field.bd_ang) * (field.hx + field.w - field.bt - field.lb);
        }
        //расчет времени на проигрыш
        this.sc = (int) (sc / fps);
        this.mx_sc = sc;
        //расстановка шаров
        setBalls();
        //искусственный интелект
        Ai = new AI_bot(this);

        //расчет времени заката
        this.zkt_speed = field.sc_w / (z_time/fps);
        //расчет максимальной скорости шара
        this.m_hurt = field.w/(ml_time/fps);
        //расчет времени удара
        double all = (int) ((double)time_load / fps);
        this.dob = m_hurt / all;
    }
    //расстановка шаров в пирамиде
    void setBalls() {
        //белый шар
        balls[0] = new Ball(0, rad, field.hx + field.w * (1 - polygonXW), polygonY);
        balls[0].on = false;
        //последовательность расстановки
        int[] position = new int[16];
        {
            position[0] = 0;
            position[1] = 1;
            position[2] = 10;
            position[3] = 2;
            position[4] = 5;
            position[5] = 8;
            position[6] = 14;
            position[7] = 12;
            position[8] = 3;
            position[9] = 13;
            position[10] = 6;
            position[11] = 7;
            position[12] = 15;
            position[13] = 4;
            position[14] = 9;
            position[15] = 11;
        }

        //выставление
        double now_rd = 1;
        for (int i = 1; i < 16; i++) {
            if (i > (now_rd + 1) / 2 * now_rd) {
                now_rd = now_rd + 1;
            }
            double x = polygonX + rad * 2 * now_rd - rad + 1;
            double y = polygonY + now_rd * rad - rad - (1 + rad * 2) * (i - ((now_rd - 1 + 1) / 2 * (now_rd - 1)) - 1);
            balls[i] = new Ball(position[i], rad, x, y);
            //Log.i("int", now_rd + ";");

        }


    }
    //проверка шара на касание с бортиком и проработка отскока
    void los_aft_wall(Ball b){
        double nq = (b.vx*b.vx + b.vy*b.vy)*en_los12;
        if (nq>0){
            double k = b.kof_by_param(Math.sqrt(nq), b.vx, b.vy, 0);
            b.vx = k*b.vx;
            b.vy = k * b.vy;
        }
        else{
            b.vx = 0;
            b.vy = 0;
        }
    }
    void bd_strike(Ball b) {
        //проверка шара на ударения о борты


        double razd_x = field.hx + field.lb+field.cx*field.bt + field.otp_d;
        double razd_y = field.hy +  field.lb+field.cy*field.bt + field.otp_d;
        double razd_xr = field.hx + field.w- field.lb-field.cx*field.bt - field.otp_d;
        double razd_yd = field.hy + field.h - field.lb-field.cy*field.bt - field.otp_d;
        //ударение о верхний борт не скраю UUUUU
        if (b.on & (b.y - b.rad <= uy & b.x-b.rad>=razd_x & b.x+b.rad<=field.w+field.hx-field.cx*field.bt-field.lb & (b.x < field.hx + (float)field.w / 2 - field.lb | b.x > field.hx + (float) field.w / 2 + field.lb))) {

            b.y = uy + rad + 1;
            b.vy = -b.vy;
            los_aft_wall(b);
        }
        //верхний левый верхний наклон
        else if (b.on & (b.y - b.rad <= uy & b.x - b.rad <= razd_x & b.x+b.rad >= razd_x)) {
            Log.i("OPPO", "Netro");
            if (b.x < razd_x) {
                if ((b.y - lh_h_k*b.x - lh_h_b)*Math.cos(field.bd_ang)<b.rad ){
                    Log.i("popi", Math.sqrt(b.vx*b.vx + b.vy*b.vy)+";");
                    double nap_zn = Math.abs(b.vx*(-20) + b.vy*(20*lh_h_k + lh_h_b - 40*lh_h_k - lh_h_b))/(b.vx*(-20) + b.vy*(20*lh_h_k + lh_h_b - 40*lh_h_k - lh_h_b));
                    double vec_x = nap_zn*(20 - 40);
                    double vec_y = nap_zn*(20*lh_h_k + lh_h_b - 40*lh_h_k - lh_h_b);
                    double l = 2*Math.sqrt(b.vx*b.vx + b.vy*b.vy) * Math.sin(field.bd_ang);
                    double nvx = b.kof_by_param(l, vec_x, vec_y, 0)*vec_x;
                    double nvy = b.kof_by_param(l, vec_x, vec_y, 0)*vec_y;
                    b.vx = nvx-b.vx;
                    b.vy = nvy - b.vy;
                    double nl = b.rad / Math.cos(field.bd_ang);
                    b.y = b.x*lh_h_k+lh_h_b + nl;
                }
            }
            if (b.x > razd_x) {
                b.y = uy + rad + 1;
                b.vy = -b.vy;
                los_aft_wall(b);
            }
        } //удар в переходной зоне
        else if (b.on & (b.y - b.rad <= uy & b.x - b.rad <= razd_x & b.x+b.rad < razd_x)) {
            Log.i("OPPO", "Netro");
            if ((b.y - lh_h_k*b.x - lh_h_b)*Math.cos(field.bd_ang)<b.rad ){
                Log.i("popi", Math.sqrt(b.vx*b.vx + b.vy*b.vy)+";");
                double nap_zn = Math.abs(b.vx*(-20) + b.vy*(20*lh_h_k + lh_h_b - 40*lh_h_k - lh_h_b))/(b.vx*(-20) + b.vy*(20*lh_h_k + lh_h_b - 40*lh_h_k - lh_h_b));
                double vec_x = nap_zn*(20 - 40);
                double vec_y = nap_zn*(20*lh_h_k + lh_h_b - 40*lh_h_k - lh_h_b);
                double l = 2*Math.sqrt(b.vx*b.vx + b.vy*b.vy) * Math.sin(field.bd_ang);
                double nvx = b.kof_by_param(l, vec_x, vec_y, 0)*vec_x;
                double nvy = b.kof_by_param(l, vec_x, vec_y, 0)*vec_y;
                b.vx = nvx-b.vx;
                b.vy = nvy - b.vy;
                double nl = b.rad / Math.cos(field.bd_ang);
                b.y = b.x*lh_h_k+lh_h_b + nl;
            }
        }//удар по наклону полностью

        //верхний правый верхний наклон
        else if (b.on & (b.y - b.rad <= uy & b.x - b.rad <= razd_xr & b.x+b.rad >= razd_xr)) {
            if (b.x >= razd_xr) {
                if ((b.y - rh_h_k*b.x - rh_h_b)*Math.cos(field.bd_ang)<b.rad ){
                    //Log.i("popi", Math.sqrt(b.vx*b.vx + b.vy*b.vy)+";");
                    double nap_zn = Math.abs(b.vx*(-20) + b.vy*(20*rh_h_k + rh_h_b - 40*rh_h_k - rh_h_b))/(b.vx*(-20) + b.vy*(20*rh_h_k + rh_h_b - 40*rh_h_k - rh_h_b));
                    double vec_x = nap_zn*(20 - 40);
                    double vec_y = nap_zn*(20*rh_h_k + rh_h_b - 40*rh_h_k - rh_h_b);
                    double l = 2*Math.sqrt(b.vx*b.vx + b.vy*b.vy) * Math.sin(field.bd_ang);
                    double nvx = b.kof_by_param(l, vec_x, vec_y, 0)*vec_x;
                    double nvy = b.kof_by_param(l, vec_x, vec_y, 0)*vec_y;
                    b.vx = nvx-b.vx;
                    b.vy = nvy - b.vy;
                    double nl = b.rad / Math.cos(field.bd_ang);
                    b.y = b.x*rh_h_k+rh_h_b + nl;
                    //Log.i("popi", Math.sqrt(b.vx*b.vx + b.vy*b.vy)+";");
                }
            }
            if (b.x < razd_xr) {
                b.y = uy + rad + 1;
                b.vy = -b.vy;
                los_aft_wall(b);
            }
        } //удар в переходной зоне
        else if (b.on & (b.y - b.rad > uy & b.x - b.rad >= razd_xr & b.x+b.rad >= razd_xr)) {
            if ((b.y - rh_h_k*b.x - rh_h_b)*Math.cos(field.bd_ang)<b.rad ){
                //Log.i("popi", Math.sqrt(b.vx*b.vx + b.vy*b.vy)+";");
                double nap_zn = Math.abs(b.vx*(-20) + b.vy*(20*rh_h_k + rh_h_b - 40*rh_h_k - rh_h_b))/(b.vx*(-20) + b.vy*(20*rh_h_k + rh_h_b - 40*rh_h_k - rh_h_b));
                double vec_x = nap_zn*(20 - 40);
                double vec_y = nap_zn*(20*rh_h_k + rh_h_b - 40*rh_h_k - rh_h_b);
                double l = 2*Math.sqrt(b.vx*b.vx + b.vy*b.vy) * Math.sin(field.bd_ang);
                double nvx = b.kof_by_param(l, vec_x, vec_y, 0)*vec_x;
                double nvy = b.kof_by_param(l, vec_x, vec_y, 0)*vec_y;
                b.vx = nvx-b.vx;
                b.vy = nvy - b.vy;
                double nl = b.rad / Math.cos(field.bd_ang);
                b.y = b.x*rh_h_k+rh_h_b + nl;
                //Log.i("popi", Math.sqrt(b.vx*b.vx + b.vy*b.vy)+";");
            }
        }//удар по наклону полностью
        //ударение о левый борт не скраю    LLLLLL
        if (b.on & (b.x - b.rad <= lx & b.y - b.rad >= field.hy + field.lb+field.cy*field.bt + field.otp_d& b.y+b.rad <= field.hy + field.h -
        field.cy*field.bt - field.lb-field.otp_d)) {
            b.x = lx + rad + 1;
            b.vx = -b.vx;
            los_aft_wall(b);
        }
        //ударение о верхний левый нижний наклон
        else if (b.on & (b.x - b.rad <= lx ) & b.y + b.rad >= field.hy + field.lb+field.cy*field.bt + field.otp_d & b.y - b.rad <= field.lb +field.hy + field.cy*field.bt + field.otp_d) { //верхняя чать перехода
            if (b.y < field.hy + field.cy*field.bt + field.otp_d + field.lb) {
                if ((lh_d_k*b.x + lh_d_b - b.y)*Math.cos(field.bd_ang)<b.rad ){
                    Log.i("OPPO", "1");
                    //Log.i("popi", Math.sqrt(b.vx*b.vx + b.vy*b.vy)+";");
                    double nap_zn = Math.abs(b.vx*(-20) + b.vy*(20*lh_d_k + lh_d_b - 40*lh_d_k - lh_d_b))/(b.vx*(-20) + b.vy*(20*lh_d_k + lh_d_b - 40*lh_d_k - lh_d_b));
                    double vec_x = nap_zn*(20 - 40);
                    double vec_y = nap_zn*(20*lh_d_k + lh_d_b - 40*lh_d_k - lh_d_b);
                    double l = 2*Math.sqrt(b.vx*b.vx + b.vy*b.vy) * Math.sin(field.bd_ang);
                    double nvx = b.kof_by_param(l, vec_x, vec_y, 0)*vec_x;
                    double nvy = b.kof_by_param(l, vec_x, vec_y, 0)*vec_y;
                    b.vx = nvx-b.vx;
                    b.vy = nvy - b.vy;
                    double nl = b.rad / Math.cos(field.bd_ang);
                    b.y = b.x*lh_d_k+lh_d_b - nl;
                    //Log.i("popi", Math.sqrt(b.vx*b.vx + b.vy*b.vy)+";");
                }
            }
            else if (b.y  >= field.hy + field.cy*field.bt + field.otp_d + field.lb) {
                Log.i("OPPO", "N");
                b.x = lx + rad + 1;
                b.vx = -b.vx;
                los_aft_wall(b);
            }


        }
        else if (b.on & (b.x - b.rad <= lx ) & b.y + b.rad < field.hy + field.lb+field.cy*field.bt + field.otp_d & b.y - b.rad <= field.lb +field.hy + field.cy*field.bt + field.otp_d) { //верхняя чать перехода
            if ((lh_d_k*b.x + lh_d_b - b.y)*Math.cos(field.bd_ang)<b.rad ){
                Log.i("OPPO", "2");
                double nap_zn = Math.abs(b.vx*(-20) + b.vy*(20*lh_d_k + lh_d_b - 40*lh_d_k - lh_d_b))/(b.vx*(-20) + b.vy*(20*lh_d_k + lh_d_b - 40*lh_d_k - lh_d_b));
                double vec_x = nap_zn*(20 - 40);
                double vec_y = nap_zn*(20*lh_d_k + lh_d_b - 40*lh_d_k - lh_d_b);
                double l = 2*Math.sqrt(b.vx*b.vx + b.vy*b.vy) * Math.sin(field.bd_ang);
                double nvx = b.kof_by_param(l, vec_x, vec_y, 0)*vec_x;
                double nvy = b.kof_by_param(l, vec_x, vec_y, 0)*vec_y;
                b.vx = nvx-b.vx;
                b.vy = nvy - b.vy;
                double nl = b.rad / Math.cos(field.bd_ang);
                b.y = b.x*lh_d_k+lh_d_b - nl;
                //Log.i("popi", Math.sqrt(b.vx*b.vx + b.vy*b.vy)+";");
            }


        }
        //ударение о нижний левый верхний наклон
        else if (b.on & (b.x - b.rad <= lx ) & b.y + b.rad >= razd_yd & b.y - b.rad <= razd_yd) { //верхняя чать перехода
            if (b.y > razd_yd) {
                if ((b.y - ld_h_k*b.x - ld_h_b)*Math.cos(field.bd_ang)<b.rad ){
                    //Log.i("popi", Math.sqrt(b.vx*b.vx + b.vy*b.vy)+";");
                    double nap_zn = Math.abs(b.vx*(-20) + b.vy*(20*ld_h_k + ld_h_b - 40*ld_h_k - ld_h_b))/(b.vx*(-20) + b.vy*(20*ld_h_k + ld_h_b - 40*ld_h_k - ld_h_b));
                    double vec_x = nap_zn*(20 - 40);
                    double vec_y = nap_zn*(20*ld_h_k + ld_h_b - 40*ld_h_k - ld_h_b);
                    double l = 2*Math.sqrt(b.vx*b.vx + b.vy*b.vy) * Math.sin(field.bd_ang);
                    double nvx = b.kof_by_param(l, vec_x, vec_y, 0)*vec_x;
                    double nvy = b.kof_by_param(l, vec_x, vec_y, 0)*vec_y;
                    b.vx = nvx-b.vx;
                    b.vy = nvy - b.vy;
                    double nl = b.rad / Math.cos(field.bd_ang);
                    b.y = b.x*ld_h_k+ld_h_b + nl;
                    //Log.i("popi", Math.sqrt(b.vx*b.vx + b.vy*b.vy)+";");
                }
            }
            else if (b.y  <= razd_yd) {
                b.x = lx + rad + 1;
                b.vx = -b.vx;
                los_aft_wall(b);
            }


        }
        else if (b.on & (b.x - b.rad <= lx ) & b.y + b.rad > razd_yd & b.y - b.rad >= razd_yd) { //верхняя чать перехода
            if ((b.y - ld_h_k*b.x - ld_h_b)*Math.cos(field.bd_ang)<b.rad ){
                //Log.i("popi", Math.sqrt(b.vx*b.vx + b.vy*b.vy)+";");
                double nap_zn = Math.abs(b.vx*(-20) + b.vy*(20*ld_h_k + ld_h_b - 40*ld_h_k - ld_h_b))/(b.vx*(-20) + b.vy*(20*ld_h_k + ld_h_b - 40*ld_h_k - ld_h_b));
                double vec_x = nap_zn*(20 - 40);
                double vec_y = nap_zn*(20*ld_h_k + ld_h_b - 40*ld_h_k - ld_h_b);
                double l = 2*Math.sqrt(b.vx*b.vx + b.vy*b.vy) * Math.sin(field.bd_ang);
                double nvx = b.kof_by_param(l, vec_x, vec_y, 0)*vec_x;
                double nvy = b.kof_by_param(l, vec_x, vec_y, 0)*vec_y;
                b.vx = nvx-b.vx;
                b.vy = nvy - b.vy;
                double nl = b.rad / Math.cos(field.bd_ang);
                b.y = b.x*ld_h_k+ld_h_b + nl;
                //Log.i("popi", Math.sqrt(b.vx*b.vx + b.vy*b.vy)+";");
            }


        }
        //ударение о нижний борт не с краю DDDDDDDD
        if (b.on & (b.y + b.rad >= dy & b.x-b.rad>razd_x & b.x+b.rad<razd_xr & (b.x < field.hx + field.w / 2 - field.lb | b.x > field.hx + (float) field.w / 2 + field.lb))) {
            b.y = dy - rad - 1;
            b.vy = -b.vy;
            los_aft_wall(b);
        }
        //нижний левый нижний наклон
        else if (b.on & (b.y + b.rad >= dy & b.x - b.rad <= razd_x & b.x+b.rad >= razd_x)) {
            if (b.x < razd_x) {
                if ((ld_d_k*b.x + ld_d_b - b.y)*Math.cos(field.bd_ang)<b.rad ){
                    //Log.i("popi", Math.sqrt(b.vx*b.vx + b.vy*b.vy)+";");
                    double nap_zn = Math.abs(b.vx*(-20) + b.vy*(20*ld_d_k + ld_d_b - 40*ld_d_k - ld_d_b))/(b.vx*(-20) + b.vy*(20*ld_d_k + ld_d_b - 40*ld_d_k - ld_d_b));
                    double vec_x = nap_zn*(20 - 40);
                    double vec_y = nap_zn*(20*ld_d_k + ld_d_b - 40*ld_d_k - ld_d_b);
                    double l = 2*Math.sqrt(b.vx*b.vx + b.vy*b.vy) * Math.sin(field.bd_ang);
                    double nvx = b.kof_by_param(l, vec_x, vec_y, 0)*vec_x;
                    double nvy = b.kof_by_param(l, vec_x, vec_y, 0)*vec_y;
                    b.vx = nvx-b.vx;
                    b.vy = nvy - b.vy;
                    double nl = b.rad / Math.cos(field.bd_ang);
                    b.y = b.x*ld_d_k+ld_d_b - nl;
                    //Log.i("popi", Math.sqrt(b.vx*b.vx + b.vy*b.vy)+";");
                }
            }
            if (b.x > razd_x) {
                b.y = dy - rad - 1;
                b.vy = -b.vy;
                los_aft_wall(b);
            }
        } //удар в переходной зоне
        else if (b.on & (b.y + b.rad >= dy & b.x - b.rad <= razd_x & b.x+b.rad < razd_x)) {
            if ((ld_d_k*b.x + ld_d_b - b.y)*Math.cos(field.bd_ang)<b.rad ){
                //Log.i("popi", Math.sqrt(b.vx*b.vx + b.vy*b.vy)+";");
                double nap_zn = Math.abs(b.vx*(-20) + b.vy*(20*ld_d_k + ld_d_b - 40*ld_d_k - ld_d_b))/(b.vx*(-20) + b.vy*(20*ld_d_k + ld_d_b - 40*ld_d_k - ld_d_b));
                double vec_x = nap_zn*(20 - 40);
                double vec_y = nap_zn*(20*ld_d_k + ld_d_b - 40*ld_d_k - ld_d_b);
                double l = 2*Math.sqrt(b.vx*b.vx + b.vy*b.vy) * Math.sin(field.bd_ang);
                double nvx = b.kof_by_param(l, vec_x, vec_y, 0)*vec_x;
                double nvy = b.kof_by_param(l, vec_x, vec_y, 0)*vec_y;
                b.vx = nvx-b.vx;
                b.vy = nvy - b.vy;
                double nl = b.rad / Math.cos(field.bd_ang);
                b.y = b.x*ld_d_k+ld_d_b - nl;
                //Log.i("popi", Math.sqrt(b.vx*b.vx + b.vy*b.vy)+";");
            }
        }//удар по наклону полностью
        //нижний правый нижний наклон
        else if (b.on & (b.y + b.rad >= dy & b.x + b.rad >= razd_xr & b.x-b.rad < razd_xr)) {
            Log.i("OPPO", "PEPE");
            if (b.x > razd_xr) {
                if ((rd_d_k*b.x + rd_d_b - b.y)*Math.cos(field.bd_ang)<b.rad ){
                    Log.i("OPPO", "PEPE1");
                    double nap_zn = Math.abs(b.vx*(-20) + b.vy*(20*rd_d_k + rd_d_b - 40*rd_d_k - rd_d_b))/(b.vx*(-20) + b.vy*(20*rd_d_k + rd_d_b - 40*rd_d_k - rd_d_b));
                    double vec_x = nap_zn*(20 - 40);
                    double vec_y = nap_zn*(20*rd_d_k + rd_d_b - 40*rd_d_k - rd_d_b);
                    double l = 2*Math.sqrt(b.vx*b.vx + b.vy*b.vy) * Math.sin(field.bd_ang);
                    double nvx = b.kof_by_param(l, vec_x, vec_y, 0)*vec_x;
                    double nvy = b.kof_by_param(l, vec_x, vec_y, 0)*vec_y;
                    b.vx = nvx-b.vx;
                    b.vy = nvy - b.vy;
                    double nl = b.rad / Math.cos(field.bd_ang);
                    b.y = b.x*rd_d_k+rd_d_b - nl;
                    //Log.i("popi", Math.sqrt(b.vx*b.vx + b.vy*b.vy)+";");
                }
            }
            if (b.x <= razd_xr) {
                b.y = dy - rad - 1;
                b.vy = -b.vy;
                los_aft_wall(b);
            }
        } //удар в переходной зоне
        else if (b.on & (b.y + b.rad >= dy & b.x + b.rad >= razd_xr & b.x-b.rad >= razd_xr)) {
            if ((rd_d_k*b.x + rd_d_b - b.y)*Math.cos(field.bd_ang)<b.rad ){
                //Log.i("popi", Math.sqrt(b.vx*b.vx + b.vy*b.vy)+";");
                double nap_zn = Math.abs(b.vx*(-20) + b.vy*(20*rd_d_k + rd_d_b - 40*rd_d_k - rd_d_b))/(b.vx*(-20) + b.vy*(20*rd_d_k + rd_d_b - 40*rd_d_k - rd_d_b));
                double vec_x = nap_zn*(20 - 40);
                double vec_y = nap_zn*(20*rd_d_k + rd_d_b - 40*rd_d_k - rd_d_b);
                double l = 2*Math.sqrt(b.vx*b.vx + b.vy*b.vy) * Math.sin(field.bd_ang);
                double nvx = b.kof_by_param(l, vec_x, vec_y, 0)*vec_x;
                double nvy = b.kof_by_param(l, vec_x, vec_y, 0)*vec_y;
                b.vx = nvx-b.vx;
                b.vy = nvy - b.vy;
                double nl = b.rad / Math.cos(field.bd_ang);
                b.y = b.x*rd_d_k+rd_d_b - nl;
                //Log.i("popi", Math.sqrt(b.vx*b.vx + b.vy*b.vy)+";");
            }
        }//удар по наклону полностью
        //ударение о правый борт не с краю RRRRRRRR
        if (b.on & (b.x + b.rad >= rx & b.y-b.rad> razd_y & b.y+b.rad <= razd_yd)) {
            b.x = rx - rad - 1;
            b.vx = -b.vx;
            los_aft_wall(b);
        }
        //ударение о нижний правый верхний наклон
        else if (b.on & (b.x + b.rad >= rx ) & b.y + b.rad >= razd_yd & b.y - b.rad <= razd_yd) { //верхняя чать перехода
            if (b.y > razd_yd) {
                if ((b.y - rd_h_k*b.x - rd_h_b)*Math.cos(field.bd_ang)<b.rad ){
                    //Log.i("popi", Math.sqrt(b.vx*b.vx + b.vy*b.vy)+";");
                    double nap_zn = Math.abs(b.vx*(-20) + b.vy*(20*rd_h_k + rd_h_b - 40*rd_h_k - rd_h_b))/(b.vx*(-20) + b.vy*(20*rd_h_k + rd_h_b - 40*rd_h_k - rd_h_b));
                    double vec_x = nap_zn*(20 - 40);
                    double vec_y = nap_zn*(20*rd_h_k + rd_h_b - 40*rd_h_k - rd_h_b);
                    double l = 2*Math.sqrt(b.vx*b.vx + b.vy*b.vy) * Math.sin(field.bd_ang);
                    double nvx = b.kof_by_param(l, vec_x, vec_y, 0)*vec_x;
                    double nvy = b.kof_by_param(l, vec_x, vec_y, 0)*vec_y;
                    b.vx = nvx-b.vx;
                    b.vy = nvy - b.vy;
                    double nl = b.rad / Math.cos(field.bd_ang);
                    b.y = b.x*rd_h_k+rd_h_b + nl;
                    //Log.i("popi", Math.sqrt(b.vx*b.vx + b.vy*b.vy)+";");
                }
            }
            else if (b.y  <= razd_yd) {
                b.x = rx - rad - 1;
                b.vx = -b.vx;
                los_aft_wall(b);
            }


        }
        else if (b.on & (b.x + b.rad >= rx ) & b.y + b.rad > razd_yd & b.y - b.rad >= razd_yd) { //верхняя чать перехода
            if ((b.y - rd_h_k*b.x - rd_h_b)*Math.cos(field.bd_ang)<b.rad ){
                //Log.i("popi", Math.sqrt(b.vx*b.vx + b.vy*b.vy)+";");
                double nap_zn = Math.abs(b.vx*(-20) + b.vy*(20*rd_h_k + rd_h_b - 40*rd_h_k - rd_h_b))/(b.vx*(-20) + b.vy*(20*rd_h_k + rd_h_b - 40*rd_h_k - rd_h_b));
                double vec_x = nap_zn*(20 - 40);
                double vec_y = nap_zn*(20*rd_h_k + rd_h_b - 40*rd_h_k - rd_h_b);
                double l = 2*Math.sqrt(b.vx*b.vx + b.vy*b.vy) * Math.sin(field.bd_ang);
                double nvx = b.kof_by_param(l, vec_x, vec_y, 0)*vec_x;
                double nvy = b.kof_by_param(l, vec_x, vec_y, 0)*vec_y;
                b.vx = nvx-b.vx;
                b.vy = nvy - b.vy;
                double nl = b.rad / Math.cos(field.bd_ang);
                b.y = b.x*rd_h_k+rd_h_b + nl;
                //Log.i("popi", Math.sqrt(b.vx*b.vx + b.vy*b.vy)+";");
            }


        }
        //ударение о верхний правый нижний наклон
        else if (b.on & (b.x + b.rad >= rx ) & b.y + b.rad > razd_y & b.y - b.rad <= razd_y) { //верхняя чать перехода
            if (b.y < razd_y) {
                if ((rh_d_k*b.x + rh_d_b - b.y)*Math.cos(field.bd_ang)<b.rad ){
                    //Log.i("popi", Math.sqrt(b.vx*b.vx + b.vy*b.vy)+";");
                    double nap_zn = Math.abs(b.vx*(-20) + b.vy*(20*rh_d_k + rh_d_b - 40*rh_d_k - rh_d_b))/(b.vx*(-20) + b.vy*(20*rh_d_k + rh_d_b - 40*rh_d_k - rh_d_b));
                    double vec_x = nap_zn*(20 - 40);
                    double vec_y = nap_zn*(20*rh_d_k + rh_d_b - 40*rh_d_k - rh_d_b);
                    double l = 2*Math.sqrt(b.vx*b.vx + b.vy*b.vy) * Math.sin(field.bd_ang);
                    double nvx = b.kof_by_param(l, vec_x, vec_y, 0)*vec_x;
                    double nvy = b.kof_by_param(l, vec_x, vec_y, 0)*vec_y;
                    b.vx = nvx-b.vx;
                    b.vy = nvy - b.vy;
                    double nl = b.rad / Math.cos(field.bd_ang);
                    b.y = b.x*rh_d_k+rh_d_b - nl;
                    //Log.i("popi", Math.sqrt(b.vx*b.vx + b.vy*b.vy)+";");
                }
            }
            else if (b.y >= razd_y) {
                b.x = rx - rad - 1;
                b.vx = -b.vx;
                los_aft_wall(b);
            }


        }
        else if (b.on & (b.x + b.rad >= rx ) & b.y + b.rad < razd_y & b.y - b.rad <= razd_y) { //верхняя чать перехода
            if ((rh_d_k*b.x + rh_d_b - b.y)*Math.cos(field.bd_ang)<b.rad ){
                //Log.i("popi", Math.sqrt(b.vx*b.vx + b.vy*b.vy)+";");
                double nap_zn = Math.abs(b.vx*(-20) + b.vy*(20*rh_d_k + rh_d_b - 40*rh_d_k - rh_d_b))/(b.vx*(-20) + b.vy*(20*rh_d_k + rh_d_b - 40*rh_d_k - rh_d_b));
                double vec_x = nap_zn*(20 - 40);
                double vec_y = nap_zn*(20*rh_d_k + rh_d_b - 40*rh_d_k - rh_d_b);
                double l = 2*Math.sqrt(b.vx*b.vx + b.vy*b.vy) * Math.sin(field.bd_ang);
                double nvx = b.kof_by_param(l, vec_x, vec_y, 0)*vec_x;
                double nvy = b.kof_by_param(l, vec_x, vec_y, 0)*vec_y;
                b.vx = nvx-b.vx;
                b.vy = nvy - b.vy;
                double nl = b.rad / Math.cos(field.bd_ang);
                b.y = b.x*rh_d_k+rh_d_b - nl;
                //Log.i("popi", Math.sqrt(b.vx*b.vx + b.vy*b.vy)+";");
            }


        }
        //ударения при падении в серед лузы
        if (b.on & b.x>field.hx+(double)field.w/2-field.lb& b.x<field.hx+(double)field.w/2+field.lb  & b.y-b.rad<uy){
            //левая вертикальная пряма
            double lx = field.hx + (double)field.w/2 - field.lb;
            //правая вертикальная прямая
            double rx = field.hx + (double)field.w/2 + field.lb;
            //модуль "косинуса"
            double h_y = b.y - b.rad*0.7;
            if (h_y>uy) {
                if (Math.abs(b.x - lx) < b.rad) {
                    double ab = Math.abs(b.x - lx);
                    double hy = -Math.sqrt(b.rad * b.rad - ab * ab) + b.y;
                    if (hy < uy) {
                        b.y = b.y + (uy - hy);
                        b.vy = -b.vy;
                    }

                }
                if (Math.abs(b.x - rx) < b.rad) {
                    double ab = Math.abs(b.x - rx);
                    double hy = -Math.sqrt(b.rad * b.rad - ab * ab) + b.y;
                    if (hy < uy) {
                        b.y = b.y + (uy - hy);
                        b.vy = -b.vy;
                    }

                }
            }
            if (h_y<=uy) {
                if (Math.abs(b.x - lx) < b.rad) {
                    double ab = Math.abs(b.x - lx);
                    double hy = -Math.sqrt(b.rad * b.rad - ab * ab) + b.y;
                    if (hy < uy) {
                        b.x = b.x + (b.rad-ab);
                        b.vx = -b.vx;
                    }

                }
                if (Math.abs(b.x - rx) < b.rad) {
                    double ab = Math.abs(b.x - rx);
                    double hy = -Math.sqrt(b.rad * b.rad - ab * ab) + b.y;
                    if (hy < uy) {
                        b.x = b.x - (b.rad-ab);
                        b.vx = -b.vx;
                    }

                }
            }


        }
        if (b.on & b.x>field.hx+(double)field.w/2-field.lb& b.x<field.hx+(double)field.w/2+field.lb  & b.y+b.rad>dy){
            //левая вертикальная пряма
            double lx = field.hx + (double)field.w/2 - field.lb;
            //правая вертикальная прямая
            double rx = field.hx + (double)field.w/2 + field.lb;
            //модуль "косинуса"
            double d_y = b.y + b.rad*0.7;
            if (d_y<=dy) {
                if (Math.abs(b.x - lx) < b.rad) {
                    double ab = Math.abs(b.x - lx);
                    double hy = Math.sqrt(b.rad * b.rad - ab * ab) + b.y;
                    if (hy > dy) {
                        b.y = b.y - (hy - dy);
                        b.vy = -b.vy;
                    }

                }
                if (Math.abs(b.x - rx) < b.rad) {
                    double ab = Math.abs(b.x - rx);
                    double hy = Math.sqrt(b.rad * b.rad - ab * ab) + b.y;
                    if (hy > dy) {
                        b.y = b.y - (hy - dy);
                        b.vy = -b.vy;
                    }

                }
            }
            if (d_y>dy) {
                if (Math.abs(b.x - lx) < b.rad) {
                    double ab = Math.abs(b.x - lx);
                    double hy = Math.sqrt(b.rad * b.rad - ab * ab) + b.y;
                    if (hy > dy) {
                        b.x = b.x + (b.rad - ab);
                        b.vx = -b.vx;
                    }

                }
                if (Math.abs(b.x - rx) < b.rad) {
                    double ab = Math.abs(b.x - rx);
                    double hy = Math.sqrt(b.rad * b.rad - ab * ab) + b.y;
                    if (hy > dy) {
                        b.x = b.x - (b.rad - ab);
                        b.vx = -b.vx;
                    }

                }
            }
        }

    }
    void lose(int player_num, String reason2) {
        game_state = 3;
        this.reason = reason2;
        this.player_w = player_num;
        id.smile = player_num;
        enemy_id.smile = 1 - player_num;

    }
    //соударение шаров
    void b_strike() {
        //Прохождение по всем шарам
        for (int i = 0; i < balls.length; i++) {
            for (int j = 0; j < balls.length; j++) {
                if (i != j & balls[i].on & balls[j].on & (balls[i].x - balls[j].x) * (balls[i].x - balls[j].x) + (balls[i].y - balls[j].y) * (balls[i].y - balls[j].y) <= 4 * rad * rad) {

                    Ball b1 = balls[i];
                    Ball b2 = balls[j];
                    //первый удар
                    if (first_num == -1 & (b1.num == 0 || b2.num == 0)){
                        first_num = b1.num + b2.num;
                    }
                    double x1 = b1.x;
                    double y1 = b1.y;
                    double x2 = b2.x;
                    double y2 = b2.y;
                    double vx1 = b1.vx;
                    double vy1 = b1.vy;
                    double vx2 = b2.vx;
                    double vy2 = b2.vy;
                    //вектора передачи энергии
                    double px1 = x2 - x1;
                    double py1 = y2 - y1;
                    double px2 = -px1;
                    double py2 = -py1;
                    //углы между осью центров и векторами
                    double cos1 = 1;
                    if (vx1 != 0 | vy1 != 0) {
                        cos1 = (vx1 * px1 + vy1 * py1) / (Math.sqrt(vx1 * vx1 + vy1 * vy1) * Math.sqrt(px1 * px1 + py1 * py1));
                    }
                    double cos2 = 1;
                    if (vx2 != 0 | vy2 != 0) {
                        cos2 = (vx2 * px2 + vy2 * py2) / (Math.sqrt(vx2 * vx2 + vy2 * vy2) * Math.sqrt(px2 * px2 + py2 * py2));
                    }
                    //вектора передачи
                    double l1 = 0;
                    if (vx1 != 0 | vy1 != 0) {
                        if (cos1 > 0) {
                            l1 = Math.sqrt(vx1 * vx1 + vy1 * vy1) * cos1;
                        }
                    }
                    double l2 = 0;
                    if (vx2 != 0 | vy2 != 0) {
                        if (cos2 > 0) {
                            l1 = Math.sqrt(vx2 * vx2 + vy2 * vy2) * cos2;
                        }
                    }
                    double dx2 = b1.kof_by_param(l1, px1, py1, 0) * px1;
                    double dy2 = b1.kof_by_param(l1, px1, py1, 0) * py1;
                    double dx1 = b1.kof_by_param(l2, px2, py2, 0) * px2;
                    double dy1 = b1.kof_by_param(l2, px2, py2, 0) * py2;
                    soud_p = ((loud * 40));

                    //прибавление новых векторов скорости
                    b1.vx = b1.vx + dx1 - dx2;
                    b1.vy = b1.vy + dy1 - dy2;
                    b2.vx = b2.vx + dx2 - dx1;
                    b2.vy = b2.vy + dy2 - dy1;


                }

            }
        }
    }
    //смена координат шаров
    void new_ps_balls() {
        //Определение значение детальности времени
        double spx = 1;
        for (int i = 0; i < balls.length; i++) {
            if (balls[i].on) {
                if (Math.sqrt(balls[i].vx * balls[i].vx + balls[i].vy * balls[i].vy) > spx) {
                    spx = Math.sqrt(balls[i].vx * balls[i].vx + balls[i].vy * balls[i].vy);
                }
            }
        }
        spx = (int) spx;
        //перевод скорости в систему spx
        for (int i = 0; i < balls.length; i++) {
            if (balls[i].on) {
                balls[i].vx = balls[i].vx / spx;
                balls[i].vy = balls[i].vy / spx;
            }
        }
        //передвижение шаров за фпс и проверка отскоков
        for (int s = 0; s < spx; s++) {
            for (int i = 0; i < balls.length; i++) {
                if (balls[i].on) {
                    balls[i].x = balls[i].x + balls[i].vx;
                    balls[i].y = balls[i].y + balls[i].vy;
                    balls[i].setWhite(balls[i].vx, balls[i].vy);
                    bd_strike(balls[i]);
                    b_strike();
                    balls_out();

                }
            }
        }
        //вывод шаров из системы spx
        for (int i = 0; i < balls.length; i++) {
            if (balls[i].on) {
                balls[i].vx = balls[i].vx * spx;
                balls[i].vy = balls[i].vy * spx;
            }
        }

    }
    double quadro_los(double vx, double vy){
        double nq = vx*vx + vy*vy;
        double s = Math.sqrt(vx * vx + vy * vy);
        nq = nq - epf * s;
        return  nq;
    }
    void energy_los() {
        for (int i = 0; i < balls.length; i++) {
            Ball b = balls[i];
            if (b.on){
                double nq = quadro_los(b.vx, b.vy);
                if (nq>0) {
                    double k = b.kof_by_param(Math.sqrt(nq), b.vx, b.vy, 0);
                    b.vx = k * b.vx;
                    b.vy = k * b.vy;
                    //Log.i("neno", b.vx+" "+  b.vy + ";" + Math.sqrt(b.vx * b.vx + b.vy * b.vy - 2) + ";" + (b.vx * b.vx + b.vy * b.vy - 2));
                }
                else {
                    b.vx = 0;
                    b.vy = 0;
                }
            }
            if (false&b.on){
                if (Math.sqrt(b.vx*b.vx+b.vy*b.vy) - Math.sqrt(b.vx*b.vx+b.vy*b.vy) * epf>0){
                    b.vx = b.vx * (1-epf);
                    b.vy = b.vy * (1-epf);
                }
                else{
                    b.vx = 0;
                    b.vy = 0;
                }
            }
        }
    }
    boolean stop_check(int type) {
        boolean flag = true;
        for (int i = 0; i < balls.length; i++) {
            if (balls[i].on) {
                if (Math.abs(balls[i].vx) > cry | Math.abs(balls[i].vy) > cry) {
                    flag = false;
                    return false;
                }
            }
        }

        if (flag & type==1){
            for (int i = 1; i<balls.length; i++){
                if (!balls[i].on & !balls[i].finish){
                    return false;
                }
            }
        }
        return true;
    }
    void total_stop() {
        for (int i = 0; i < balls.length; i++) {
            if (balls[i].on) {
                balls[i].vx = 0;
                balls[i].vy = 0;
            }

        }
    }
    void load_ud() {
        now_hurt = now_hurt + dob;
        if (now_hurt >= m_hurt) {
            now_hurt = m_hurt - dob;
            dob = -dob;
        } else if (now_hurt <= 0) {
            now_hurt = 0;
            dob = -dob;
        }
    }
    void hurt(int type) {
        //0 - удар кием обычный
        //1 - разбив
        //2 - удар кием бота

        if (type == 0){
            //считаем вектор удара
            Ball b = balls[0];
            double vec_x = mouse_x - b.x;
            double vec_y = mouse_y - b.y;
            double range_vec = Math.sqrt(vec_x * vec_x + vec_y * vec_y);
            //координыты реального вектора удара
            double v_x = vec_x * now_hurt / range_vec;
            double v_y = vec_y * now_hurt / range_vec;
            b.vx = v_x;
            b.vy = v_y;
            now_hurt = 0;
        }
        if (type == 1){
            //считаем вектор удара
            Ball b = balls[0];
            double vec_x = Ai.raz_x - b.x;
            double vec_y = Ai.raz_y - b.y;
            double range_vec = Math.sqrt(vec_x * vec_x + vec_y * vec_y);
            //координыты реального вектора удара
            double v_x = vec_x * (Ai.raz_ud*m_hurt) / range_vec;
            double v_y = vec_y * (Ai.raz_ud*m_hurt) / range_vec;
            b.vx = v_x;
            b.vy = v_y;
            now_hurt = 0;
        }
        if (type == 2){
            Ball b = balls[0];
            b.vx = Ai.vx;
            b.vy = Ai.vy;
            now_hurt = 0;
        }
        if (type == 1 || type == 0 || type == 2){
            proto_balls_on();
            game_state = 0;
        }


    }
    void balls_out() {
        for (int i = 0; i < balls.length; i++) {
            if (balls[i].on) {
                Ball b = balls[i];
                for (int u = 0; u < field.luns.length; u++) {
                    double x = field.luns[u].x;
                    double y = field.luns[u].y;
                    double r = field.luns[u].rad;
                    if ((b.x - x) * (b.x - x) + (b.y - y) * (b.y - y) <= r * r) {
                        b.on = false;
                        //balls[u+1].on = false;
                        if (b.num!=0 & !Ai.in_think){
                            b.zkt = true;
                            //balls[u+1].zkt = true;
                        }
                    }
                }
            }
        }
    }
    void pose_wh() {
        Ball b = balls[0];
        double x = mouse_x;
        double y = mouse_y;
        boolean flag = true;
        if (x < field.hx - field.bt - field.gb + field.w - b.rad & x > b.rad + field.hx + field.bt + field.gb & y > b.rad + field.hy + field.bt + field.gb & y < field.h - b.rad + field.hy - field.bt - field.gb) {
            for (int i = 1; i < balls.length; i++) {
                Ball n = balls[i];
                if ((x - n.x) * (x - n.x) + (y - n.y) * (y - n.y) < n.rad * n.rad * 4) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                b.on = true;
                okey = true;
                b.x = x;
                b.y = y;
                if (mouse_sost==2){
                    mouse_sost = 0;
                    game_state = 1;
                }

            }
            else{
                b.on = false;
            }
        }
    }
    void proto_balls_on(){
        for (int i = 0; i<balls.length; i++){
            proto_matrix[i] = balls[i].on;
        }
    }
    boolean check_ready(){
        boolean f1 = false;
        boolean f2 = false;
        if (id.balls_type==0){
            if (!balls[1].on & !balls[3].on & !balls[4].on & !balls[8].on & !balls[10].on & !balls[11].on & !balls[13].on){
                f1 = true;
            }
        }
        if (id.balls_type==1){
            if (!balls[2].on & !balls[6].on & !balls[7].on & !balls[9].on & !balls[12].on & !balls[14].on & !balls[15].on){
                f1 = true;
            }
        }
        if (enemy_id.balls_type==0){
            if (!balls[1].on & !balls[3].on & !balls[4].on & !balls[8].on & !balls[10].on & !balls[11].on & !balls[13].on){
                f2 = true;
            }
        }
        if (enemy_id.balls_type==1){
            if (!balls[2].on & !balls[6].on & !balls[7].on & !balls[9].on & !balls[12].on & !balls[14].on & !balls[15].on){
                f2 = true;
            }
        }
        id.ready_black = f1;
        enemy_id.ready_black = f2;

        return ((f1 || f2));
    }
    synchronized void after_stop(){
        Log.i("ferter", "ok");
        if (game_state==0) {
            mouse_sost = 0;
            if (play_way == 0) {
                boolean flag_fall = false;
                boolean flag_lose = false;
                for (int i = 0; i < balls.length; i++) {
                    Ball b = balls[i];
                    if (!b.on & proto_matrix[i]) {
                        if (b.num == 0) {
                            flag_fall = true;
                        }
                        if (b.num == 8) {
                            flag_lose = true;
                        }
                    }

                }
                if (flag_lose) {
                    state_info = "Игра окончена";
                    lose(1, "Черный шар рано забит противником");
                    game_state = 3;
                }
                if (!flag_lose & flag_fall) {
                    state_info = "Фол. " + id.name + " размещвет биток";
                    game_state = 2;
                    play_way = 1;
                    id.smile = 1 - player;
                    enemy_id.smile = player;
                    player = 1;

                }
                if (!flag_lose & !flag_fall) {
                    state_info = "Бьет " + id.name;
                    game_state = 1;
                    play_way = 1;
                    id.smile = 1;
                    enemy_id.smile = 1;
                    player = 1;
                }
            }
            else if (play_way == 1) {
                boolean flag_fall = false;
                boolean flag_lose = false;
                boolean flag_bt = false;
                int ball_type = 3;
                for (int i = 0; i < balls.length; i++) {
                    Ball b = balls[i];
                    if (!b.on & proto_matrix[i]) {
                        if (b.num == 0) {
                            flag_fall = true;
                        }
                        if (b.num == 8) {
                            flag_lose = true;
                        }
                        if (b.num != 0 & b.num < 8 & !flag_bt) {
                            flag_bt = true;
                            ball_type = 0;
                        }
                        if (b.num != 0 & b.num > 8 & !flag_bt) {
                            flag_bt = true;
                            ball_type = 1;
                        }
                    }
                }
                if (flag_lose) {
                    state_info = "Игра окончена";
                    if (player == 1) {
                        lose(0, "Вы рано забили черный шар");
                        game_state = 3;
                    }
                    if (player == 0) {
                        lose(1, "Черный шар рано забит противником");
                        game_state = 3;
                    }
                }
                if (!flag_lose & flag_fall & !flag_bt) {
                    if (player == 0) {
                        state_info = "Фол. " + id.name + " размещвет биток";
                        game_state = 2;
                        id.smile = 1 - player;
                        enemy_id.smile = player;
                        player = 1;

                    }
                    else if (player == 1) {
                        state_info = "Фол. " + enemy_id.name + " размещвет биток";
                        game_state = 2;
                        id.smile = 1 - player;
                        enemy_id.smile = player;
                        player = 0;
                    }
                }
                if (!flag_lose & flag_fall & flag_bt) {
                    if (player == 0) {
                        enemy_id.balls_type = ball_type;
                        id.balls_type = 1 - ball_type;
                        state_info = "Фол. " + id.name + " размещает биток";
                        game_state = 2;
                        id.smile = 1 - player;
                        enemy_id.smile = player;
                        id.smile = player;
                        enemy_id.smile = 1 -player;
                        player = 1;
                    } else if (player == 1) {
                        id.balls_type = ball_type;
                        enemy_id.balls_type = 1 - ball_type;
                        state_info = "Фол. " + enemy_id.name + " размещает биток";
                        game_state = 2;
                        id.smile = player;
                        enemy_id.smile = 1 -player;
                        player = 0;
                    }
                }
                if (!flag_lose & !flag_fall & !flag_bt) {
                    if (player == 0) {
                        state_info = "Бьет " + id.name;
                        game_state = 1;
                        id.smile = 1-player;
                        enemy_id.smile = player;
                        player = 1;
                    } else if (player == 1) {
                        state_info = "Бьет " + enemy_id.name;
                        game_state = 1;
                        id.smile = 1-player;
                        enemy_id.smile = player;
                        player = 0;

                    }

                }
                if (!flag_lose & !flag_fall & flag_bt) {

                    if (player == 0) {
                        enemy_id.balls_type = ball_type;
                        id.balls_type = 1 - ball_type;
                        state_info = "Бьет " + enemy_id.name;
                        id.smile = player;
                        enemy_id.smile = 1-player;
                        play_way = 2;
                    } else if (player == 1) {
                        id.balls_type = ball_type;
                        enemy_id.balls_type = 1 - ball_type;
                        state_info = "Бьет " + id.name;
                        game_state = 1;
                        play_way = 2;
                        id.smile = player;
                        enemy_id.smile = 1-player;
                    }

                }

            }
            else if (play_way == 2) {
                boolean flag_fall = false;
                boolean flag_lose = false;
                boolean flag_norm = false;
                boolean first_ud = false;
                if (player == 0) {
                    if (first_num > 0) {
                        if (enemy_id.balls_type == 0 & first_num < 8) {
                            first_ud = true;
                        }
                        if (enemy_id.balls_type == 1 & first_num > 8) {
                            first_ud = true;
                        }
                    }
                }
                if (player == 1) {
                    if (first_num > 0) {
                        if (id.balls_type == 0 & first_num < 8) {
                            first_ud = true;
                        }
                        if (id.balls_type == 1 & first_num > 8) {
                            first_ud = true;
                        }
                    }
                }
                for (int i = 0; i < balls.length; i++) {
                    Ball b = balls[i];
                    if (!b.on & proto_matrix[i]) {
                        if (b.num == 0) {
                            flag_fall = true;
                        }
                        if (b.num == 8) {
                            flag_lose = true;
                        }
                        if (b.num != 0 & b.num < 8) {
                            if (player == 1 & id.balls_type == 0) {
                                flag_norm = true;
                            }
                            if (player == 0 & enemy_id.balls_type == 0) {
                                flag_norm = true;
                            }
                        }
                        if (b.num != 0 & b.num > 8) {
                            if (player == 1 & id.balls_type == 1) {
                                flag_norm = true;
                            }
                            if (player == 0 & enemy_id.balls_type == 1) {
                                flag_norm = true;
                            }
                        }

                    }
                }
                if (flag_lose) {
                    state_info = "Игра окончена";
                    if (player == 1) {
                        lose(0, "Вы рано забили черный шар");
                        game_state = 3;
                    }
                    if (player == 0) {
                        lose(1, "Черный шар рано забит противником");
                        game_state = 3;
                    }
                }
                if (!flag_lose & (flag_fall || !first_ud)) {
                    if (player == 0) {
                        state_info = "Фол. " + id.name + " размещвет биток";
                        game_state = 2;
                        id.smile = 1 - player;
                        enemy_id.smile = player;
                        player = 1;
                    } else if (player == 1) {
                        state_info = "Фол. " + enemy_id.name + " размещвет биток";
                        game_state = 2;
                        id.smile = 1 - player;
                        enemy_id.smile = player;
                        player = 0;
                    }
                }
                if (!flag_lose & !flag_fall & first_ud & !flag_norm) {
                    if (player == 0) {
                        state_info = "Бьет " + id.name;
                        game_state = 1;
                        id.smile = 1 - player;
                        enemy_id.smile = player;
                        player = 1;
                    } else if (player == 1) {
                        state_info = "Бьет " + enemy_id.name;
                        id.smile = 1 - player;
                        enemy_id.smile = player;
                        game_state = 1;
                        player = 0;
                    }

                }
                if (!flag_lose & !flag_fall & first_ud & flag_norm) {
                    Log.i("ferter", "333333333333 " + player);
                    if (player == 0) {
                        state_info = "Бьет " + id.name;
                        id.smile = player;
                        enemy_id.smile = 1-player;
                        game_state = 1;
                    } else if (player == 1) {
                        state_info = "Бьет " + enemy_id.name;
                        id.smile = player;
                        enemy_id.smile = 1-player;
                        game_state = 1;
                    }

                }
                if (check_ready()){
                    play_way = 3;
                }

            }
            else if (play_way == 3) {
                boolean flag_fall = false;
                boolean flag_lose = false;
                boolean flag_norm = false;
                boolean first_ud = false;
                if (player == 0 & !enemy_id.ready_black) {
                    if (first_num > 0) {
                        if (enemy_id.balls_type == 0 & first_num < 8) {
                            first_ud = true;
                        }
                        if (enemy_id.balls_type == 1 & first_num > 8) {
                            first_ud = true;
                        }
                    }
                }
                if (player == 0 & enemy_id.ready_black) {
                    if (first_num == 8) {
                        first_ud = true;
                    }
                }
                if (player == 1 & !id.ready_black) {
                    if (first_num > 0) {
                        if (id.balls_type == 0 & first_num < 8) {
                            first_ud = true;
                        }
                        if (id.balls_type == 1 & first_num > 8) {
                            first_ud = true;
                        }
                    }
                }
                if (player == 1 & id.ready_black) {
                    if (first_num == 8) {
                        first_ud = true;
                        }
                    }
                for (int i = 0; i < balls.length; i++) {
                    Ball b = balls[i];
                    if (!b.on & proto_matrix[i]) {
                        if (b.num == 0) {
                            flag_fall = true;
                        }
                        if (b.num == 8) {
                            flag_lose = true;
                        }
                        if (b.num != 0 & b.num < 8) {
                            if (player == 1 & id.balls_type == 0) {
                                flag_norm = true;
                            }
                            if (player == 0 & enemy_id.balls_type == 0) {
                                flag_norm = true;
                            }
                        }
                        if (b.num != 0 & b.num > 8) {
                            if (player == 1 & id.balls_type == 1) {
                                flag_norm = true;
                            }
                            if (player == 0 & enemy_id.balls_type == 1) {
                                flag_norm = true;
                            }
                        }

                    }
                }
                if (flag_lose) {
                    state_info = "Игра окончена";
                    if (player == 1) {
                        if (!id.ready_black) {
                            lose(0, "Вы рано забили черный шар");
                            game_state = 3;
                        }
                        if (id.ready_black) {
                            lose(1, "Вы забили все шары своего цвета и восьмерку");
                            game_state = 3;
                        }
                    }
                    if (player == 0) {
                        if (!enemy_id.ready_black) {
                            lose(1, "Черный шар рано забит противником");
                            game_state = 3;
                        }
                        if (enemy_id.ready_black) {
                            lose(0, "Противник забил все шары своего цвета и восьмерку");
                            game_state = 3;
                        }
                    }
                }
                if (!flag_lose & (flag_fall || !first_ud)) {
                    if (player == 0) {
                        state_info = "Фол. " + id.name + " размещает биток";
                        game_state = 2;
                        id.smile = 1 - player;
                        enemy_id.smile = player;
                        player = 1;
                    } else if (player == 1) {
                        state_info = "Фол. " + enemy_id.name + " размещает биток";
                        game_state = 2;
                        id.smile = 1 - player;
                        enemy_id.smile = player;
                        player = 0;
                    }
                }
                if (!flag_lose & !flag_fall & first_ud & !flag_norm) {
                    Log.i("ferter", "22222222222 " + flag_norm);
                    if (player == 0) {
                        state_info = "Бьет " + id.name;
                        game_state = 1;
                        id.smile = 1 - player;
                        enemy_id.smile = player;
                        player = 1;
                    } else if (player == 1) {
                        state_info = "Бьет " + enemy_id.name;
                        game_state = 1;
                        id.smile = 1 - player;
                        enemy_id.smile = player;
                        player = 0;
                    }

                }
                if (!flag_lose & !flag_fall & first_ud & flag_norm) {
                    if (player == 0) {
                        state_info = "Бьет " + id.name;
                        game_state = 1;
                        id.smile = player;
                        enemy_id.smile = 1-player;
                    } else if (player == 1) {
                        state_info = "Бьет " + enemy_id.name;
                        game_state = 1;
                        id.smile = player;
                        enemy_id.smile = 1-player;
                    }

                }
                check_ready();

            }
            if (id.balls_type==2){
                id.smile=1;
                enemy_id.smile=1;
            }
            if (id.balls_type==0){
                int k1 = 0;
                int k2 = 0;
                for (int i = 1; i<balls.length; i++){
                    if (!balls[i].on){
                        if (balls[i].num<8){
                            k1++;
                        }
                        if (balls[i].num>8){
                            k2++;
                        }
                    }
                }
                if (k1 > k2){
                    id.smile = 1;
                    enemy_id.smile = 0;
                }
                if (k1 == k2){
                    id.smile = 1;
                    enemy_id.smile = 1;
                }
                if (k1 < k2){
                    id.smile = 0;
                    enemy_id.smile = 1;
                }
            }
            if (id.balls_type==1){
                int k1 = 0;
                int k2 = 0;
                for (int i = 1; i<balls.length; i++){
                    if (!balls[i].on){
                        if (balls[i].num<8){
                            k1++;
                        }
                        if (balls[i].num>8){
                            k2++;
                        }
                    }
                }
                if (k1 < k2){
                    id.smile = 1;
                    enemy_id.smile = 0;
                }
                if (k1 == k2){
                    id.smile = 1;
                    enemy_id.smile = 1;
                }
                if (k1 > k2){
                    id.smile = 0;
                    enemy_id.smile = 1;
                }
            }

        }
        first_num=-1;
    }
    synchronized void game_controller() {
        Ai.hard = main.hard_Ai[main.now_champ.type][0] + main.now_champ.state*main.hard_Ai[main.now_champ.type][1];
        if (!Ai.in_think){
            main.save_game();
        }
        zkt();
        //периуд времени
        if (game_state == 3) {
            state_info = "Игра окончена";
            sc = sc - 1;
        }
        if (game_state==0){
            Ai.in_razb = false;
            Ai.in_think = false;
            Ai.in_pose = false;
            state_info = "Ожидание остановки всех шаров";
            new_ps_balls();
            energy_los();
            balls_out();
            if (stop_check(1)) {
                total_stop();
                if (true) {
                    after_stop();
                }
                else {
                    game_state = 2;
                    mouse_sost = 0;
                    player = 1;
                }
                first_num = -1;
            }
        }
        if (game_state==-2){
            player=0;
            state_info = enemy_id.name + " разбивает пирамиду";
            Ai.razbiv();
        }
        if (game_state==2){
            if (player==1) {
                state_info = "Фол. " + id.name + " размещает биток";
                pose_wh();
            }
            if (player==0) {
                state_info = "Фол. " + enemy_id.name + " размещает биток";
                Ai.pose();

            }
        }
        if (game_state==1){
            if (player==1){
                state_info = "Бьет " + id.name;
                load_ud();
                if (mouse_sost==2){
                    hurt(0);
                }
            }
            if (player==0){
                state_info = "Бьет " + enemy_id.name;
                Ai.think();
            }
        }

    }
    void Ai_sym() {
        //new_ps_balls();
        {
            double spx = 1;
            for (int i = 0; i < balls.length; i++) {
                if (balls[i].on) {
                    if (Math.sqrt(balls[i].vx * balls[i].vx + balls[i].vy * balls[i].vy) > spx) {
                        spx = Math.sqrt(balls[i].vx * balls[i].vx + balls[i].vy * balls[i].vy);
                    }
                }
            }
            spx = (int) spx;
            //перевод скорости в систему spx
            for (int i = 0; i < balls.length; i++) {
                if (balls[i].on) {
                    balls[i].vx = balls[i].vx / spx;
                    balls[i].vy = balls[i].vy / spx;
                }
            }
            //передвижение шаров за фпс и проверка отскоков
            for (int s = 0; s < spx; s++) {
                for (int i = 0; i < balls.length; i++) {
                    if (balls[i].on) {
                        balls[i].x = balls[i].x + balls[i].vx;
                        balls[i].y = balls[i].y + balls[i].vy;
                        //balls[i].setWhite(balls[i].vx, balls[i].vy);
                        bd_strike(balls[i]);
                        b_strike();
                        balls_out();
                        for (int l = 0; l < balls.length; l++) {
                            for (int j = 0; j < balls.length; j++) {
                                if (l != j & balls[l].on & balls[j].on & (balls[l].x - balls[j].x) * (balls[l].x - balls[j].x) + (balls[l].y - balls[j].y) * (balls[l].y - balls[j].y) <= 4 * rad * rad) {
                                    Ball b1 = balls[l];
                                    Ball b2 = balls[j];

                                    double x1 = b1.x;
                                    double y1 = b1.y;
                                    double x2 = b2.x;
                                    double y2 = b2.y;
                                    double vx1 = b1.vx;
                                    double vy1 = b1.vy;
                                    double vx2 = b2.vx;
                                    double vy2 = b2.vy;
                                    //вектора передачи энергии
                                    double px1 = x2 - x1;
                                    double py1 = y2 - y1;
                                    double px2 = -px1;
                                    double py2 = -py1;
                                    //углы между осью центров и векторами
                                    double cos1 = 1;
                                    if (vx1 != 0 | vy1 != 0) {
                                        cos1 = (vx1 * px1 + vy1 * py1) / (Math.sqrt(vx1 * vx1 + vy1 * vy1) * Math.sqrt(px1 * px1 + py1 * py1));
                                    }
                                    double cos2 = 1;
                                    if (vx2 != 0 | vy2 != 0) {
                                        cos2 = (vx2 * px2 + vy2 * py2) / (Math.sqrt(vx2 * vx2 + vy2 * vy2) * Math.sqrt(px2 * px2 + py2 * py2));
                                    }
                                    //вектора передачи
                                    double l1 = 0;
                                    if (vx1 != 0 | vy1 != 0) {
                                        if (cos1 > 0) {
                                            l1 = Math.sqrt(vx1 * vx1 + vy1 * vy1) * cos1;
                                        }
                                    }
                                    double l2 = 0;
                                    if (vx2 != 0 | vy2 != 0) {
                                        if (cos2 > 0) {
                                            l1 = Math.sqrt(vx2 * vx2 + vy2 * vy2) * cos2;
                                        }
                                    }
                                    double dx2 = b1.kof_by_param(l1, px1, py1, 0) * px1;
                                    double dy2 = b1.kof_by_param(l1, px1, py1, 0) * py1;
                                    double dx1 = b1.kof_by_param(l2, px2, py2, 0) * px2;
                                    double dy1 = b1.kof_by_param(l2, px2, py2, 0) * py2;

                                    //прибавление новых векторов скорости
                                    b1.vx = b1.vx + dx1 - dx2;
                                    b1.vy = b1.vy + dy1 - dy2;
                                    b2.vx = b2.vx + dx2 - dx1;
                                    b2.vy = b2.vy + dy2 - dy1;


                                }

                            }
                        }

                    }
                }
            }
            //вывод шаров из системы spx
            for (int i = 0; i < balls.length; i++) {
                if (balls[i].on) {
                    balls[i].vx = balls[i].vx * spx;
                    balls[i].vy = balls[i].vy * spx;
                }
            }
        }
        energy_los();
        //balls_out();
        {
            for (int i = 0; i < balls.length; i++) {
                if (balls[i].on) {
                    Ball b = balls[i];
                    for (int u = 0; u < field.luns.length; u++) {
                        double x = field.luns[u].x;
                        double y = field.luns[u].y;
                        double r = field.luns[u].rad;
                        if ((b.x - x) * (b.x - x) + (b.y - y) * (b.y - y) <= r * r) {
                            b.on = false;
                        }
                    }
                }
            }
        }

    }
    boolean double_click(double last, double x, double y){
        if (System.currentTimeMillis()-last<1000){
            if (x>field.avx&x<field.avx + field.avw & y>field.avy & y<field.avy+field.avw){
                return true;
            }
        }
        return false;
    }
    void zkt(){
        if (!Ai.in_think) {
            for (int i = 0; i < balls.length; i++) {
                if (!balls[i].on & balls[i].zkt & !balls[i].finish) {
                    Ball b = balls[i];
                    if (!b.start_sc) {
                        b.y = field.sc_y - 2*b.rad;
                        b.x = field.sc_x + field.sc_t + b.rad;
                        b.vx = 0;
                        b.vy = 0;
                        if (sc_steck <= 0) {
                            b.start_sc = true;
                            sc_steck = sc_steck + 1;
                        }

                    }
                    if (b.y < field.sc_y + field.sc_h - field.sc_t - b.rad & b.start_sc) {
                        b.vx = 0;
                        b.vy = zkt_speed;
                        b.y = b.y + b.vy;
                        balls[i].setWhite(balls[i].vx, balls[i].vy);
                    }
                    if (b.y >= field.sc_y + field.sc_h - field.sc_t - b.rad & b.x < field.sc_block_x) {
                        if (b.vx == 0) {
                            sc_steck = sc_steck - 1;
                        }
                        b.vx = zkt_speed;
                        b.vy = 0;
                        b.x = b.vx + b.x;
                        b.y = field.sc_y + field.sc_h - field.sc_t - b.rad;
                        balls[i].setWhite(balls[i].vx, balls[i].vy);

                    }
                    if (b.x > field.sc_block_x) {
                        b.vx = 0;
                        b.vy = 0;
                        b.x = field.sc_block_x;
                        ;
                        b.finish = true;
                        field.sc_block_x = field.sc_block_x - 2 * b.rad;
                        Log.i("ttt", "ooooo " + b.num + " " + b.finish + " " + Ai.in_think);
                    }

                }
            }
        }
    }

}

