package com.example.billiard;

import android.graphics.Color;
import android.os.Handler;
import android.util.Log;

public class AI_bot {
    boolean in_think = false;
    boolean in_razb = false;
    boolean in_pose = false;
    int var_pl = 20; //точность подбора позиции поле фола
    int post_pst = 3000;
    int hard = 40; //сложность в процентах
    Game game;
    Handler handler = new Handler();
    int r_var = 700;
    double vx = 0;
    double vy = 0;
    double now_a = 0;
    Field pole; //поле игры
    double time_for_razb = 2000;//время на рабив
    double raz_x = 0; //точка разбива
    double raz_y = 0; //точка разбива
    double raz_ud = 1; //сила удара разбива от максимума
    double raz_ang = 0;
    double ud_vars = 3.14/180 * 20;
    int kiy_color = Color.argb(180, 255, 255, 255);
    public AI_bot(Game pl){
        this.game = pl;
        this.pole = pl.field;
    }
    public Ball front_b(double x1, double y1, double ang){
        //центральная прямая
        double vec_x = Math.cos(ang);
        double vec_y = Math.sin(ang);
        for (int t=0; t<game.window_w/Math.abs(vec_x); t++){
            double x = x1 + t * vec_x;
            double y = y1 + t * vec_y;
            for (int i = 1; i<game.balls.length; i++){
                Ball b = game.balls[i];
                if (b.num!=0 & b.on & ((b.x -x)* (b.x-x) + (b.y -y)* (b.y-y)<=b.rad*b.rad*4)){
                    //Log.i("check1", x+"; " + y + "   " + vec_x + ";" + t + ";" + ang * 180 / 3.14);
                    return b;
                }
            }
        }
        return game.balls[0];

    }
    boolean[] hurt_analize(double vx, double vy, Ball b){
        game.balls[0].on = true;
        Ball[] real_b = game.balls.clone();
        for (int i = 0; i<real_b.length; i++){
            Ball op = new Ball(real_b[i].num, real_b[i].rad, real_b[i].x, real_b[i].y);
            op.on = real_b[i].on;
            op.zkt = real_b[i].zkt;
            op.start_sc = real_b[i].start_sc;
            op.finish = real_b[i].finish;
            op.pb = real_b[i].pb.clone();
            op.pb2 = real_b[i].pb2.clone();
            real_b[i] = op;

        }
        game.balls[0].vx = vx;
        game.balls[0].vy = vy;
        while (!game.stop_check(2)){
            game.Ai_sym();
        }
        if (game.balls[5].on & !game.enemy_id.ready_black) {
            for (int u=0; u<real_b.length; u++){
            }
            for (int i = 1; i < real_b.length; i++) {
                if (game.balls[i].num > 8 & (game.balls[i].on != real_b[i].on) & game.enemy_id.balls_type == 1) {
                    if (game.balls[0].on) {
                        game.balls = real_b.clone();
                        return new boolean[]{true, true};
                    }
                    else{
                        game.balls = real_b.clone();
                        return new boolean[]{true, false};
                    }
                }
                if (game.balls[i].num < 8 & (game.balls[i].on != real_b[i].on) & game.enemy_id.balls_type == 0) {
                    if (game.balls[0].on) {
                        game.balls = real_b.clone();
                        return new boolean[]{true, true};
                    }
                    else{
                        game.balls = real_b.clone();
                        return new boolean[]{true, false};
                    }
                }
                if ((game.balls[i].on != real_b[i].on) & game.enemy_id.balls_type == 2) {
                    if (game.balls[0].on) {
                        game.balls = real_b.clone();
                        return new boolean[]{true, true};
                    }
                    else{
                        game.balls = real_b.clone();
                        return new boolean[]{true, false};
                    }
                }
            }
        }
        if (!game.balls[5].on & game.enemy_id.ready_black) {
            if (game.balls[0].on) {
                game.balls = real_b.clone();
                return new boolean[]{true, true};
            }
            else{
                game.balls = real_b.clone();
                return new boolean[]{true, true};
            }
        }
        game.balls = real_b.clone();
        return new boolean[]{false, false};
    }
    public void think(){
        if (!in_think){
            in_think = true;
            Runnable runnable = new Runnable() {
                int i = 0;
                boolean flag_know = false;
                boolean rezerv = false;
                boolean rezerv_prio = false;
                double rez_vx = 0;
                double rez_vy = 0;
                double rez_vx_vip = 0;
                double rez_vy_vip = 0;

                @Override
                public void run() {
                    //запуск игрового процесса
                    if (game.game_state==1 ){
                        Ball b = game.balls[0];
                        if (i<r_var & !flag_know) {
                            i++;
                            double ang = i * 3.14 * 2 / r_var;
                            now_a = ang;
                            Ball bn = front_b(b.x, b.y, ang);
                            if ((b.x - bn.x)*(b.x - bn.x) + (b.y - bn.y)*(b.y - bn.y)<16*b.rad*b.rad){
                                i = i + 2;
                            }
                            if ((b.x - bn.x)*(b.x - bn.x) + (b.y - bn.y)*(b.y - bn.y)<6*b.rad*b.rad){
                                i = i + 2;
                            }
                            boolean f = false;
                            if (bn.num != 0 & bn.num > 8 & game.enemy_id.balls_type== 1& !game.enemy_id.ready_black) {
                                f = true;
                                double vx1 = b.kof_by_param(game.m_hurt , Math.cos(ang), Math.sin(ang), 0) * Math.cos(ang);
                                double vy1 = b.kof_by_param(game.m_hurt , Math.cos(ang), Math.sin(ang), 0) * Math.sin(ang);
                                boolean[] dec = hurt_analize(vx1, vy1, bn);
                                if (dec[0] & dec[1]) {
                                    vx = vx1;
                                    vy = vy1;
                                    flag_know = true;
                                }
                                if (dec[0]&!dec[1]){
                                    rezerv_prio = true;
                                    rez_vx_vip = vx1;
                                    rez_vy_vip = vy1;
                                }
                                if (!dec[0]&!dec[1]){
                                    rezerv = true;
                                    rez_vx = vx1;
                                    rez_vy = vy1;
                                }
                            }
                            else if (bn.num != 0 & bn.num < 8 & game.enemy_id.balls_type== 0& !game.enemy_id.ready_black) {
                                f = true;
                                double vx1 = b.kof_by_param(game.m_hurt , Math.cos(ang), Math.sin(ang), 0) * Math.cos(ang);
                                double vy1 = b.kof_by_param(game.m_hurt , Math.cos(ang), Math.sin(ang), 0) * Math.sin(ang);
                                boolean[] dec = hurt_analize(vx1, vy1, bn);
                                if (dec[0] & dec[1]) {
                                    vx = vx1;
                                    vy = vy1;
                                    flag_know = true;
                                }
                                if (dec[0]&!dec[1]){
                                    rezerv_prio = true;
                                    rez_vx_vip = vx1;
                                    rez_vy_vip = vy1;
                                }
                                if (!dec[0]&!dec[1]){
                                    rezerv = true;
                                    rez_vx = vx1;
                                    rez_vy = vy1;
                                }
                            }
                            else if (bn.num != 0 & bn.num != 8 & game.enemy_id.balls_type == 2& !game.enemy_id.ready_black) {
                                f = true;
                                double vx1 = b.kof_by_param(game.m_hurt , Math.cos(ang), Math.sin(ang), 0) * Math.cos(ang);
                                double vy1 = b.kof_by_param(game.m_hurt , Math.cos(ang), Math.sin(ang), 0) * Math.sin(ang);

                                boolean[] dec = hurt_analize(vx1, vy1, bn);
                                if (dec[0] & dec[1]) {
                                    vx = vx1;
                                    vy = vy1;
                                    flag_know = true;
                                }
                                if (dec[0]&!dec[1]){
                                    rezerv_prio = true;
                                    rez_vx_vip = vx1;
                                    rez_vy_vip = vy1;
                                }
                                if (!dec[0]&!dec[1]){
                                    rezerv = true;
                                    rez_vx = vx1;
                                    rez_vy = vy1;
                                }
                            }
                            else if (bn.num == 8 & game.enemy_id.ready_black) {
                                f = true;
                                rezerv = true;
                                double vx1 = b.kof_by_param(game.m_hurt , Math.cos(ang), Math.sin(ang), 0) * Math.cos(ang);
                                double vy1 = b.kof_by_param(game.m_hurt , Math.cos(ang), Math.sin(ang), 0) * Math.sin(ang);
                                rez_vx = vx1;
                                rez_vy = vy1;
                                if (hurt_analize(vx1, vy1, bn)[0]) {
                                    vx = vx1;
                                    vy = vy1;
                                    flag_know = true;
                                }
                            }
                            if (!f){
                                i = i + 2;
                            }
                            handler.postDelayed(this, 1/2);
                        }
                        else {
                            if (!flag_know){
                                if (!rezerv_prio) {
                                    if (!rezerv) {
                                        vx = 2 * (Math.random() * game.m_hurt - (game.m_hurt / 2));
                                        double zn = Math.random();
                                        zn = zn / (Math.abs(zn));
                                        vy = zn * (Math.random() * Math.sqrt(game.m_hurt * game.m_hurt - vx * vx));
                                        game.hurt(2);
                                    }
                                    if (rezerv) {
                                        vx = rez_vx;
                                        vy = rez_vy;
                                        game.hurt(2);
                                    }
                                }
                                if (rezerv_prio) {
                                    vx = rez_vx_vip;
                                    vy = rez_vy_vip;
                                    game.hurt(2);
                                }
                            }
                            if (flag_know){
                                if (i>r_var){
                                    if (rezerv || rezerv_prio){
                                        double vb = Math.random()*100;
                                        Log.i("kakra", vb+"");
                                        if (vb>hard){

                                            if (!rezerv_prio) {
                                                if (Math.random()*100>hard){
                                                    vx = rez_vx + Math.random()*game.m_hurt*0.2;
                                                    vy = rez_vy + Math.random()*game.m_hurt*0.2;
                                                }
                                                else {
                                                    vx = rez_vx;
                                                    vy = rez_vy;
                                                }
                                            }
                                            else{
                                                vx = rez_vx_vip;
                                                vy = rez_vy_vip;
                                            }
                                        }
                                    }
                                    game.hurt(2);
                                }
                                else {
                                    double ang = i * 3.14 * 2 / r_var;
                                    now_a = ang;
                                    i++;
                                    handler.postDelayed(this, 1);
                                }
                            }
                            //handler.removeCallbacks(this);
                        }
                    }
                    else {
                        handler.postDelayed(this, 10);
                    }
                }
            };runnable.run();
        }
    }
    public void pose(){
        if (!in_pose){
            in_pose = true;
            double x = pole.hx + pole.bt + pole.gb + game.balls[0].rad + Math.random()*(pole.w-2*(pole.bt + pole.gb + 3 * game.balls[0].rad));
            double y = pole.hy + pole.bt + pole.gb + game.balls[0].rad + Math.random()*(pole.h-2*(pole.bt + pole.gb + 3 * game.balls[0].rad));
            while(!acc(x, y)) {
                x = pole.hx + pole.bt + pole.gb + game.balls[0].rad + Math.random() * (pole.w - 2 * (pole.bt + pole.gb + 3 * game.balls[0].rad));
                y = pole.hy + pole.bt + pole.gb + game.balls[0].rad + Math.random() * (pole.h - 2 * (pole.bt + pole.gb + 3 * game.balls[0].rad));
                game.balls[0].x = x;
                game.balls[0].y = y;
            }
            Runnable runnable = new Runnable() {
                int r = var_pl;
                int ppt = post_pst;
                double l = game.window_w * game.window_w;
                @Override
                public void run() {
                    if (r>0) {

                        Ball b = game.balls[0];
                        //Log.i("yert", b.x+";"+b.y);
                        b.on = false;
                        Ball bl = game.balls[5];
                        //b.on=false;
                        double x = pole.hx + pole.bt + pole.gb + b.rad + Math.random()*(pole.w-2*(pole.bt + pole.gb + 3 * b.rad));
                        double y = pole.hy + pole.bt + pole.gb + b.rad + Math.random()*(pole.h-2*(pole.bt + pole.gb + 3 * b.rad));
                        while(!acc(x, y)){
                            x = pole.hx + pole.bt + pole.gb + b.rad + Math.random()*(pole.w-2*(pole.bt + pole.gb + 3 * b.rad));
                            y = pole.hy + pole.bt + pole.gb + b.rad + Math.random()*(pole.h-2*(pole.bt + pole.gb + 3 * b.rad));
                            Log.i("erevan", "pob " + x + " " + y);
                        }
                        if (game.enemy_id.ready_black){
                            if (l>(x-bl.x)*(x-bl.x) + (y-bl.y)*(y-bl.y)){
                                b.x = x;
                                b.y = y;
                                l = (x-bl.x)*(x-bl.x) + (y-bl.y)*(y-bl.y);
                            }
                        }
                        if (!game.enemy_id.ready_black & game.enemy_id.balls_type==0){
                            for (int i = 1; i<game.balls.length; i++){
                                Ball br = game.balls[i];
                                if (br.on & br.num < 8 & l>(x-br.x)*(x-br.x) + (y-br.y)*(y-br.y)){
                                    b.x = x;
                                    b.y = y;
                                    l = (x-br.x)*(x-br.x) + (y-br.y)*(y-br.y);
                                }
                            }
                        }
                        if (!game.enemy_id.ready_black & game.enemy_id.balls_type==1){
                            for (int i = 1; i<game.balls.length; i++){
                                Ball br = game.balls[i];
                                if (br.on & br.num > 8 & l>(x-br.x)*(x-br.x) + (y-br.y)*(y-br.y)){
                                    b.x = x;
                                    b.y = y;
                                    l = (x-br.x)*(x-br.x) + (y-br.y)*(y-br.y);
                                }
                            }
                        }
                        if (!game.enemy_id.ready_black & game.enemy_id.balls_type==2){
                            for (int i = 1; i<game.balls.length; i++){
                                Ball br = game.balls[i];
                                if (br.on & br.num != 8 & l>((x-br.x)*(x-br.x) + (y-br.y)*(y-br.y))){
                                    b.x = x;
                                    b.y = y;
                                    l = (x-br.x)*(x-br.x) + (y-br.y)*(y-br.y);
                                }
                            }
                        }
                        r = r - 1;
                        handler.postDelayed(this, 10);
                    }
                    if(r<=0) {
                        if (ppt>0){
                            game.balls[0].on = true;
                            ppt = ppt-30;
                            handler.postDelayed(this, 30);
                        }
                        else {
                            game.game_state = 1;
                            Log.i("gggg", "lllll");
                        }
                    }
                }
            };runnable.run();
        }
    }
    public void razbiv(){
        if (!in_razb) {
            in_razb = true;
            //случайное положение битка
            {
                Ball b = game.balls[0];
            b.on = true;
            b.x = pole.hx + pole.bt + pole.gb + b.rad;
            b.x = b.x + (pole.hx + pole.w * game.dom - b.x) * Math.random();
            if (Math.random()>0.5) {
                b.y = pole.hy + pole.bt + pole.gb + b.rad;
                b.y = b.y + (pole.h - 2 * pole.bt - 2 * pole.gb - 2 * b.rad)/3 * Math.random();
            }
            else {
                b.y = pole.hy - pole.bt - pole.gb - b.rad + pole.h;
                b.y = b.y - (pole.h - 2 * pole.bt - 2 * pole.gb - 2 * b.rad)/3 * Math.random();
            }
            }
            int sub_fps = 33;
            int i = 7;//(int)(Math.random() * (game.balls.length-2)) + 1;
            raz_x = game.balls[i].x;
            raz_y = game.balls[i].y;
            raz_ang = Math.atan((raz_y - game.balls[0].y)/(raz_x - game.balls[0].x)) + ud_vars;
            //Log.i("rerere", raz_ang*180/3.14 + "");
            Runnable runnable = new Runnable() {

                int ugl_temps = (int)(time_for_razb/sub_fps);
                double plus_an = ud_vars/ugl_temps;
                double sum = 0;
                @Override
                public void run() {
                    //запуск игрового процесса
                    if (time_for_razb>0){
                        raz_ang = raz_ang - plus_an;
                        time_for_razb = time_for_razb - sub_fps;
                        handler.postDelayed(this, sub_fps);
                    }
                    else {
                        game.hurt(1);
                        game.game_state = 0;

                    }



                }
            };runnable.run();
        }

    }
    boolean acc (double x, double y){
        for (int i = 1; i<game.balls.length; i++){
            Ball b = game.balls[0];
            Ball b1 = game.balls[i];
            if (b1.on& (x - b1.x) * (x - b1.x) + (y - b1.y)*(y - b1.y)<4*b.rad*b.rad){
                return false;
            }
        }
        return true;
    }
}
