package com.example.billiard;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;

class DrawView extends View {
    //инициализация инструментов рисовки
    Paint p;
    Rect rect;
    Canvas canvas;
    //инициализация сторонних классов
    Game game;
    Field pole;
    public DrawView(Context context, Game game) {
        super(context);
        //получение управляющего класса игры
        {
        this.game = game;}
        //создание инструментоа для рисовки
        {p = new Paint();
        rect = new Rect();}
        //упрощение ссылки на класс поля
        {
        pole = this.game.field;}

    }

    @Override
    protected void onDraw(Canvas canvas) {

        //если идет игровой процесс
        if (true){
            //рисовка поля
            //залив пола
            canvas.drawColor(pole.bg);
            sc_p(canvas, p);
            load_bar_p(canvas, p);
            sc_balls_p(canvas, p);
            field_p(canvas, p);

            //рисовка теней шаров
            for (int i = 0; i< game.balls.length; i++) {
                shadow_ball_p(canvas, p, game.balls[i]);
            }
            //рисовка шаров
            for (int i = 0; i< game.balls.length; i++) {
                ball_p(canvas, p, game.balls[i]);
            }
            if (game.game_state==1 & game.mouse_sost==1){
                load_p(canvas, p);
            }
            if (game.game_state!=3){
                hand_p(canvas, p);
            }
            gui_p(canvas, p);
            if (game.game_state==3) {
                finish_p(canvas, p);
            }
            if (game.game_state==-2){
                raz_ai_p(canvas, p);
            }

        }
    }
    void hand_p(Canvas canvas, Paint p){
        //рисовка после фола
        {
            if (game.game_state == 2 & game.balls[0].on) {
                if (game.player == 1) {
                    p.setColor(Color.argb(180, 0, 0, 0));
                    Ball b = game.balls[0];
                    canvas.drawBitmap(game.imgs.hand[0], (float) (b.x - pole.handw / 2), (float) (b.y - pole.handw / 2), p);
                }
                if (game.player == 0) {
                    p.setColor(Color.argb(180, 0, 0, 0));
                    Ball b = game.balls[0];
                    canvas.drawBitmap(game.imgs.hand[1], (float) (b.x - pole.handw / 2), (float) (b.y - pole.handw / 2), p);
                }
            }
        }
        //рисовка ожидания удара
        Ball b = game.balls[0];
        double sm_h = (float)2*b.rad / 3;
        double sm_l = sm_h * 2.5;
        double yplus = b.rad/4;
        if (game.game_state==1 & game.player==0){
            p.setColor(Color.argb(170, 255, 255, 255));
            canvas.drawRect((float) (b.x - sm_l/2), (float) (b.y + b.rad + yplus),
                    (float) (b.x + sm_l/2), (float) (yplus + b.y + sm_h + b.rad), p );
            p.setColor(Color.argb(170, 0, 0, 0));
            double w1 = sm_l*0.8;
            double w = w1 * 0.5 * game.Ai.now_a/3.14;
            double h = sm_h/2;
            canvas.drawRect((float) (b.x - sm_l/2 + (sm_l-w1)/2), (float) (b.y + b.rad + yplus + (sm_h-h)/2),
                    (float) (b.x - sm_l/2 + (sm_l-w1)/2 + w), (float) (b.y + b.rad + yplus + (sm_h-h)/2 + h), p );

        }
    }
    void raz_ai_p(Canvas canvas, Paint p){
        p.setColor(game.Ai.kiy_color);
        Ball b = game.balls[0];
        //canvas.drawLine((float)b.x, (float) b.y, (float) (b.x + play.window_w), (float) (b.y + Math.tan(play.Ai.raz_ang)*(play.window_w)), p);
    }
    void finish_p(Canvas canvas, Paint p){
        int mx_dark = 200;
        if ((double) game.sc/ game.mx_sc > 0.75){
            mx_dark =(int)((float)(game.mx_sc - game.sc)*4/(float) game.mx_sc * mx_dark);

        }
        p.setColor(Color.argb(mx_dark, 0, 0, 0));
        canvas.drawRect(0, 0, (float)(game.window_w * 1.5), (float)(game.window_h*1.5), p);
        if ((double) game.sc/ game.mx_sc <= 0.75) {
            p.setTextAlign(Paint.Align.CENTER);
            p.setColor(Color.argb(180, 255, 255, 255));
            if (game.player_w == 1) {
                p.setTextSize((int) (game.window_h / 15));
                canvas.drawText("Победил " + game.id.name, (float) (game.window_w / 2), (float) (game.window_h / 3), p);
            }
            else {
                if (true) {
                    p.setTextSize((int) (game.window_h / 15));
                    canvas.drawText("Победил " + game.enemy_id.name, (float) (game.window_w / 2), (float) (game.window_h / 3), p);
                }
            }
            p.setTextSize((int) (game.window_h / 20));
            canvas.drawText(game.reason, (float) (game.window_w / 2), (float) (game.window_h / 3 + game.window_h / 15 ), p);
        }
    }
    void gui_p(Canvas canvas, Paint p){
        //рисовка аватарки
        {
            p.setColor(Color.argb(255, 255, 255, 255));
            canvas.drawBitmap(game.imgs.avatars[game.main.now_ava], (float) pole.avx, (float) pole.avy, p);
            canvas.drawBitmap(game.imgs.avatars[5], (float) (pole.avx + pole.w - pole.avw), (float) pole.avy, p);


        }
        //имена игроков
        int textS = (int) (pole.avw / 3);
        {
            p.setTextSize(textS);
            p.setTextAlign(Paint.Align.LEFT);
            if (game.player==1) {
                p.setColor(Color.argb(189, 255, 255, 255));
            }
            else{
                p.setColor(Color.argb(80, 255, 255, 255));
            }
            canvas.drawText(game.id.name, (float) (pole.avx + pole.avw + pole.avw / 10), (float) (pole.avy + textS), p);
            p.setTextAlign(Paint.Align.RIGHT);
            if (game.player==1) {
                p.setColor(Color.argb(80, 255, 255, 255));
            }
            else{
                p.setColor(Color.argb(189, 255, 255, 255));
            }
            canvas.drawText(game.enemy_id.name, (float) (pole.avx + pole.w - pole.avw - pole.avw / 10), (float) (pole.avy + textS), p);
        }
        //тип шаров
        {
            int textSm = (int) (pole.avw / 6);
            p.setTextSize(textSm);
            p.setTextAlign(Paint.Align.LEFT);
            p.setColor(Color.argb(189, 255, 255, 255));
            String p1 ="" ;
            if (game.id.balls_type==0){
                p1 = "Тип шаров: сплошные";
            }
            if (game.id.balls_type==1){
                p1 = "Тип шаров: полосатые";
            }
            if (game.id.balls_type==2){
                p1 = "Тип шаров: не определен";
            }
            canvas.drawText(p1, (float) (pole.avx + pole.avw + pole.avw / 10), (float) (pole.avy + textSm + textS + pole.avw/10), p);
            p.setTextAlign(Paint.Align.RIGHT);
            String p2="" ;
            if (game.enemy_id.balls_type==0){
                p2 = "Тип шаров: сплошные";
            }
            if (game.enemy_id.balls_type==1){
                p2 = "Тип шаров: полосатые";
            }
            if (game.enemy_id.balls_type==2){
                p2 = "Тип шаров: не определен";
            }
            canvas.drawText(p2, (float) (pole.avx + pole.w - pole.avw - pole.avw / 10), (float) (pole.avy + textSm+ textS + pole.avw/10), p);

        }
        //полоска с шарами
        {
            int balls_rad = (int)(pole.avw/8);
            int  cl = Color.argb(180, 0, 0, 0); //пустое поле шара
            //игрок
            float y = (float) (pole.avy + pole.avw * 0.9 - balls_rad);
            p.setStrokeWidth((int)(balls_rad/10));
            if (game.id.balls_type == 2) {
                double x = pole.avx + pole.avw * 1.1 + balls_rad;

                p.setColor(cl);
                for (int i = 0; i < 7; i++) {
                    canvas.drawCircle((float) x, y, balls_rad, p);
                    x = x + balls_rad * 2 + pole.avw * 0.05;
                }
            }
            if (game.id.balls_type == 0) {
                double x = pole.avx + pole.avw * 1.1 + balls_rad;

                for (int i = 1; i < game.balls.length; i++) {
                    if (game.balls[i].num < 8) {
                        p.setColor(cl);
                        canvas.drawCircle((float) x, y, balls_rad, p);
                        if (game.balls[i].on) {

                        } else {
                            p.setColor(game.balls[i].color);
                            canvas.drawLine((float) (x-balls_rad/2), (float) (y-balls_rad/2), (float) (x+balls_rad/2), (float) (y+balls_rad/2), p);
                            canvas.drawLine((float) (x+balls_rad/2), (float) (y-balls_rad/2), (float) (x-balls_rad/2), (float) (y+balls_rad/2), p);

                        }
                        x = x + balls_rad * 2 + pole.avw * 0.05;
                    }
                }
            }
            if (game.id.balls_type == 1) {
                double x = pole.avx + pole.avw * 1.1 + balls_rad;

                for (int i = 1; i < game.balls.length; i++) {
                    if (game.balls[i].num > 8) {
                        p.setColor(cl);
                        canvas.drawCircle((float) x, y, balls_rad, p);
                        if (game.balls[i].on) {

                        } else {
                            p.setColor(game.balls[i].color);
                            canvas.drawLine((float) (x-balls_rad/2), (float) (y-balls_rad/2), (float) (x+balls_rad/2), (float) (y+balls_rad/2), p);
                            p.setColor(Color.argb(255, 255, 255, 255));
                            canvas.drawLine((float) (x+balls_rad/2), (float) (y-balls_rad/2), (float) (x-balls_rad/2), (float) (y+balls_rad/2), p);

                        }
                        x = x + balls_rad * 2 + pole.avw * 0.05;
                    }
                }
            }
            //противник
            if (game.enemy_id.balls_type == 2) {
                double x = pole.avx + pole.w - pole.avw * 1.1 - balls_rad;
                p.setColor(cl);
                for (int i = 0; i < 7; i++) {
                    canvas.drawCircle((float) x, y, balls_rad, p);
                    x = x - balls_rad * 2 - pole.avw * 0.05;
                }
            }
            if (game.enemy_id.balls_type == 0) {
                double x = pole.avx + pole.w - pole.avw * 1.1 - balls_rad;
                for (int i = 1; i < game.balls.length; i++) {
                    if (game.balls[i].num < 8) {
                        p.setColor(cl);
                        canvas.drawCircle((float) x, y, balls_rad, p);
                        if (game.balls[i].on) {

                        } else {
                            p.setColor(game.balls[i].color);
                            canvas.drawLine((float) (x-balls_rad/2), (float) (y-balls_rad/2), (float) (x+balls_rad/2), (float) (y+balls_rad/2), p);
                            canvas.drawLine((float) (x+balls_rad/2), (float) (y-balls_rad/2), (float) (x-balls_rad/2), (float) (y+balls_rad/2), p);

                        }

                        x = x - balls_rad * 2 - pole.avw * 0.05;
                    }

                }
            }
            if (game.enemy_id.balls_type == 1) {
                double x = pole.avx + pole.w - pole.avw * 1.1 - balls_rad;
                for (int i = 1; i < game.balls.length; i++) {
                    if (game.balls[i].num > 8) {
                        p.setColor(cl);
                        canvas.drawCircle((float) x, y, balls_rad, p);
                        if (game.balls[i].on) {

                        } else {
                            p.setColor(game.balls[i].color);
                            canvas.drawLine((float) (x-balls_rad/2), (float) (y-balls_rad/2), (float) (x+balls_rad/2), (float) (y+balls_rad/2), p);
                            p.setColor(Color.argb(255, 255, 255, 255));
                            canvas.drawLine((float) (x+balls_rad/2), (float) (y-balls_rad/2), (float) (x-balls_rad/2), (float) (y+balls_rad/2), p);

                        }

                        x = x - balls_rad * 2 - pole.avw * 0.05;
                    }

                }
            }
        }
        //информационное табло
        int ts = (int)(pole.avw/7);
        p.setColor(Color.argb(80, 0, 0, 0));
        canvas.drawRect((float) (game.window_w/2 - game.state_info.length()/2 * ts*0.7), (float) (pole.hy/2 - ts),
                (float) (game.window_w/2 + game.state_info.length()/2 * ts*0.7), (float) (pole.hy/2 +  ts/2), p);
        p.setTextAlign(Paint.Align.CENTER);
        p.setTextSize(ts);
        p.setColor(Color.argb(180, 160, 219, 121));
        canvas.drawText(game.state_info, (float) (game.window_w/2), (float) (pole.hy/2), p);
    }
    void field_p(Canvas canvas, Paint p){

        //рисовка большого борта
        p.setColor(Color.argb(255, 0, 0, 0));
        canvas.drawBitmap(game.imgs.bl_field[game.main.now_champ.type/2], pole.hx, pole.hy,  p);
        if (false& game.Ai.in_think){
            p.setColor(Color.argb(255, 255, 255, 255));
            Ball b = game.balls[0];
            canvas.drawLine((float)b.x, (float)b.y, (float)(b.x + Math.cos(game.Ai.now_a) *10* game.window_w), (float)(b.y + Math.sin(game.Ai.now_a) *10* game.window_w), p);
        }
        //рисовка полоски дома если надо
        {
            p.setStrokeWidth((int) (pole.w / 800 + 1));
            p.setColor(Color.argb(80, 240, 240, 240));
            canvas.drawLine((float) (pole.hx + game.dom * pole.w + pole.bt), (float) (pole.hy + pole.bt+pole.gb), (float) (pole.hx + game.dom * pole.w + pole.bt), (float) (pole.hy + pole.h - pole.bt-pole.gb), p);

        }
    }
    void wp_draw(Canvas canvas, Paint p, double[] wb, Ball b, double radius){
        p.setColor(Color.argb(255, 255, 255, 255));
        for (int x1 = -(int) (b.rad); x1 < (int) (b.rad); x1++) {
            for (int y1 = -(int) (b.rad); y1< (int) (b.rad); y1++) {
                if (x1 * x1 + y1 * y1 <= b.rad * b.rad) {
                    double z = Math.sqrt(game.rad* game.rad - x1 * x1 - y1 * y1);
                    if ((x1 - wb[0]*b.rad) * (x1 - wb[0]*b.rad) + (y1 - wb[1]*b.rad) * (y1 - wb[1]*b.rad) + (z - wb[2]*b.rad) * (z - wb[2]*b.rad) <= radius*radius) {
                        canvas.drawCircle((float)(x1 + b.x), (float)(y1 + b.y), 1, p);
                    }
                }
            }
        }
    }
    void ball_p(Canvas canvas, Paint p, Ball b){
        if (b.on) {
            //закраска самого шара
            p.setColor(Color.argb(100, 0, 0, 0));
            canvas.drawCircle((float) b.x, (float) b.y, game.rad + 2, p);
            p.setColor(b.color);
            canvas.drawCircle((float) b.x, (float) b.y, game.rad, p);
            //p.setColor(Color.argb(255, 255, 255, 255));
            //p.setTextSize(40);
            //canvas.drawText(b.num+"", (float) b.x, (float) (b.y-b.rad), p);
            p.setColor(Color.argb(255, 255, 255, 255));
            //закраска боковых белых частей (только у полосатых)
            if (b.num > 8) {
                {
                    p.setColor(Color.argb(255, 255, 255, 255));
                    wp_draw(canvas, p, b.pb, b, b.mp_rad);
                    p.setColor(Color.argb(255, 255, 255, 255));
                    wp_draw(canvas, p, b.pb2, b, b.mp_rad);
                }
            }
            //рисовка блика
            p.setColor(pole.lamp.light_color);
            double[] xy = pole.lamp.getLight(b);
            canvas.drawCircle((float) xy[0], (float) xy[1], (float) (b.rad * pole.lamp.light_rad), p);
            canvas.drawCircle((float) xy[0], (float) xy[1], (float) (b.rad * pole.lamp.light_rad / 1.5), p);
            canvas.drawCircle((float) xy[0], (float) xy[1], (float) (b.rad * pole.lamp.light_rad / 2), p);
            canvas.drawCircle((float) xy[0], (float) xy[1], (float) (b.rad * pole.lamp.light_rad / 3), p);

        }
    }
    void shadow_ball_p(Canvas canvas, Paint p, Ball b) {
        if (b.on) {
            Lamp lamp = this.game.field.lamp;
            p.setColor(lamp.sh_color);
            double[] xy = lamp.getPose(b);
            double x = xy[0];
            double y = xy[1];
            double i = xy[2];
            //если тень не на борту
            if (i == 0) {
                canvas.drawCircle((float) x, (float) y, game.rad, p);
            }
            //если тень накладывается на борт
            if (i == 1) {
                for (int xp = (int) (x - game.rad); xp < x + game.rad; xp++) {
                    for (int yp = (int) (y - game.rad); yp < y + game.rad; yp++) {
                        if ((xp - x) * (xp - x) + (yp - y) * (yp - y) < game.rad * game.rad) {
                            boolean c1 = xp > pole.hx + pole.bt + pole.gb & xp < pole.hx + pole.w - pole.bt - pole.gb & yp > pole.hy + pole.bt + pole.gb & yp < pole.hy + pole.h - pole.bt - pole.gb;
                            boolean s_hl = (yp<pole.hy+pole.cy*pole.bt+pole.lb+pole.otp_d*Math.tan(pole.bd_ang) & xp<pole.hx+pole.cx*pole.bt+pole.lb+pole.otp_d);
                            boolean s_hr = (yp<pole.hy+pole.cy*pole.bt+pole.lb+pole.otp_d*Math.tan(pole.bd_ang) & xp>pole.hx-pole.cx*pole.bt-pole.lb-pole.otp_d + pole.w);
                            boolean s_dr = (yp>pole.hy-pole.cy*pole.bt-pole.lb-pole.otp_d*Math.tan(pole.bd_ang)+pole.h & xp>pole.hx-pole.cx*pole.bt-pole.lb-pole.otp_d + pole.w);
                            boolean s_dl = (yp>pole.hy-pole.cy*pole.bt-pole.lb-pole.otp_d*Math.tan(pole.bd_ang)+pole.h & xp<pole.hx+pole.cx*pole.bt+pole.lb+pole.otp_d);
                            boolean mid = (xp>pole.hx+pole.w/2-pole.lb &xp<pole.hx+pole.w/2+pole.lb);
                            if (c1) {
                                canvas.drawRect((float) xp, (float) yp, (float) xp + 1, (float) yp + 1,  p);
                            }
                            if (!c1){
                                if (mid){
                                    canvas.drawRect((float) xp, (float) yp, (float) xp + 1, (float) yp + 1,  p);
                                }
                                if (s_hl&(xp * game.lh_h_k + game.lh_h_b<yp)&(xp * game.lh_d_k + game.lh_d_b>yp)){
                                    canvas.drawRect((float) xp, (float) yp, (float) xp + 1, (float) yp + 1,  p);
                                }
                                if (s_hr&(xp * game.rh_h_k + game.rh_h_b<yp)&(xp * game.rh_d_k + game.rh_d_b>yp)){
                                    canvas.drawRect((float) xp, (float) yp, (float) xp + 1, (float) yp + 1,  p);
                                }
                                if (s_dr&(xp * game.rd_h_k + game.rd_h_b<yp)&(xp * game.rd_d_k + game.rd_d_b>yp)){
                                    canvas.drawRect((float) xp, (float) yp, (float) xp + 1, (float) yp + 1,  p);
                                }
                                if (s_dl&(xp * game.ld_h_k + game.ld_h_b<yp)&(xp * game.ld_d_k + game.ld_d_b>yp)){
                                    canvas.drawRect((float) xp, (float) yp, (float) xp + 1, (float) yp + 1,  p);
                                }
                            }
                        }

                    }
                }
            }
        }
    }
    void load_p(Canvas canvas, Paint p){
        //кий
        if (game.game_state==1 & game.player==1){
            p.setColor(Color.argb(255, 0,0,0));
            Ball ba = game.balls[0];

            canvas.save();
            double vec_x1 = -(game.mouse_x-ba.x)/Math.sqrt((game.mouse_x-ba.x)*(game.mouse_x-ba.x) +
                    (game.mouse_y-ba.y)*(game.mouse_y-ba.y));
            double vec_y1 = -(game.mouse_y-ba.y)/Math.sqrt((game.mouse_x-ba.x)*(game.mouse_x-ba.x) +
                    (game.mouse_y-ba.y)*(game.mouse_y-ba.y));
            double range = pole.kiy_w + pole.mx_ot*(game.now_hurt/game.m_hurt);
            double nv = ba.kof_by_param(range, vec_x1, vec_y1, 0);
            double nvx = vec_x1 * nv;
            double nvy = vec_y1 * nv;
            double alp = 0;
            if (game.mouse_y>=ba.y){
                alp = Math.acos(-vec_x1)*180/3.14;
            }
            else {
                alp = 360 - Math.acos(-vec_x1)*180/3.14;
            }
            Log.i("kurama", alp+"");
            double x = ba.x + vec_x1*ba.rad;
            double y = ba.y + vec_y1*ba.rad;
            canvas.rotate((float) alp, (float) (x + nvx), (float) (y + nvy));
            canvas.drawBitmap(game.imgs.kiy[game.main.now_kiy], (float) (x + nvx), (float) (y + nvy-pole.kiy_h/2), p);
            canvas.restore();
        }
        //прицел
        if (game.game_state==1 & game.player==1) {
            Ball b = game.balls[0];

            double vec_x = (game.mouse_x - b.x) / Math.sqrt((game.mouse_x - b.x) * (game.mouse_x - b.x) + (game.mouse_y - b.y) * (game.mouse_y - b.y));
            double vec_y = (game.mouse_y - b.y) / Math.sqrt((game.mouse_x - b.x) * (game.mouse_x - b.x) + (game.mouse_y - b.y) * (game.mouse_y - b.y));
            for (double t = 0; t < game.window_w*2 / Math.abs(vec_x); t=t+(game.window_w/1500)) {
                double x = b.x + t * vec_x;
                double y = b.y + t * vec_y;
                for (int i = 1; i < game.balls.length; i++) {
                    Ball b1 = game.balls[i];
                    if (x>pole.hx + pole.w - pole.bt-pole.gb + b.rad || x < pole.hx + pole.bt+pole.gb + b.rad
                    || y<pole.hy+pole.gb+pole.bt+b.rad || y>pole.hy+pole.h-pole.bt-pole.gb-b.rad){
                        p.setStrokeWidth((float) (b.rad/4));
                        p.setColor(Color.argb(255, 50, 50, 50));
                        canvas.drawLine((float) (b.x + b.rad*vec_x), (float) (b.y+vec_y*b.rad), (float) (x-vec_x*b.rad), (float) (y-b.rad*vec_y), p);
                        p.setStrokeWidth((float) (b.rad/9));
                        p.setColor(Color.argb(255, 255, 255, 255));
                        canvas.drawLine((float) (b.x + b.rad*vec_x), (float) (b.y+vec_y*b.rad), (float) (x-vec_x*b.rad), (float) (y-b.rad*vec_y), p);
                        p.setColor(Color.argb(255, 255, 255, 255));
                        canvas.drawBitmap(game.imgs.hand[2], (float)(x-b.rad), (float)(y-b.rad),  p);
                        return;
                    }
                    if ((b1.num != 0 & b1.on & ((b1.x - x) * (b1.x - x) + (b1.y - y) * (b1.y - y) <= b.rad * b.rad * 4)) ) {
                        p.setStrokeWidth((float) (b.rad/4));
                        p.setColor(Color.argb(255, 50, 50, 50));
                        canvas.drawLine((float) (b.x + b.rad*vec_x), (float) (b.y+vec_y*b.rad), (float) (x-vec_x*b.rad), (float) (y-b.rad*vec_y), p);
                        p.setStrokeWidth((float) (b.rad/9));
                        p.setColor(Color.argb(255, 255, 255, 255));
                        canvas.drawLine((float) (b.x + b.rad*vec_x), (float) (b.y+vec_y*b.rad), (float) (x-vec_x*b.rad), (float) (y-b.rad*vec_y), p);
                        p.setColor(Color.argb(255, 255, 255, 255));
                        canvas.drawBitmap(game.imgs.hand[2], (float)(x-b.rad), (float)(y-b.rad),  p);
                        p.setStrokeWidth((float) (b.rad/5));
                        canvas.drawLine((float) (3 * b1.x-2 * x), (float) (3*b1.y-2*y), (float) (x), (float) (y), p);
                        return;
                    }
                }
            }
        }

    }
    void sc_p(Canvas canvas, Paint p){
        int rad = game.balls[0].rad;
        //main
        p.setColor(pole.sc_color);
        canvas.drawRect((float) (pole.sc_x), (float) (pole.sc_y), (float) (pole.sc_x + pole.sc_t),
                (float) (pole.sc_y + pole.sc_h), p);
        // sub
        p.setColor(pole.sc_color_bg);
        canvas.drawRect((float) (pole.sc_x + pole.sc_t), (float) (pole.sc_y), (float) (pole.sc_x + pole.sc_t + pole.sc_sub_t), (float) (pole.sc_y + pole.sc_h - pole.sc_t ), p);
        //canvas.drawRect((float) (pole.sc_x + pole.sc_t + pole.sc_sub_arc*2*rad), (float) (pole.sc_y), (float) (pole.sc_x + pole.sc_t + pole.sc_sub_arc*2*rad + pole.sc_sub_t), (float) (pole.sc_y + pole.sc_sub_t), p);

        //main
        p.setColor(pole.sc_color);
        canvas.drawRect((float) (pole.sc_x), (float) (pole.sc_y + pole.sc_h-pole.sc_t), (float) (pole.sc_x + pole.sc_w), (float) (pole.sc_y + pole.sc_h ), p);
        //sub
        p.setColor(pole.sc_color_bg);
        canvas.drawRect((float) (pole.sc_x + pole.sc_t), (float) (pole.sc_y + pole.sc_h-pole.sc_t-pole.sc_sub_t),
                (float) (pole.sc_x + pole.sc_w - pole.sc_t), (float) (pole.sc_y + pole.sc_h-pole.sc_t), p);
        canvas.drawRect((float) (pole.sc_x + pole.sc_t  + pole.sc_sub_arc*2*rad), (float) (pole.sc_y), (float) (pole.sc_x + pole.sc_w - pole.sc_t), (float) (pole.sc_y + pole.sc_sub_t), p);

        //main
        p.setColor(pole.sc_color);
        canvas.drawRect((float) (pole.sc_x + pole.sc_w - pole.sc_t),
                (float) (pole.sc_y), (float) (pole.sc_x + pole.sc_w), (float) (pole.sc_y + pole.sc_h), p);
        //sub
        p.setColor(pole.sc_color_bg);
        canvas.drawRect((float) (pole.sc_x + pole.sc_w - pole.sc_t-pole.sc_sub_t),
                (float) (pole.sc_y), (float) (pole.sc_x + pole.sc_w - pole.sc_t),
                (float) (pole.sc_y + pole.sc_h-pole.sc_t), p);






    }
    void load_bar_p(Canvas canvas, Paint p){
        p.setColor(pole.load_ob_color);
        canvas.drawRect((float) pole.load_x, (float) pole.load_y, (float) (pole.load_x + pole.load_w),
                (float) (pole.load_y+pole.load_h), p) ;
        p.setColor(pole.bg);
        canvas.drawRect((float) (pole.load_x + pole.load_t), (float) (pole.load_y + pole.load_t), (float) (pole.load_x + pole.load_w - pole.load_t),
                (float) (pole.load_y+pole.load_h - pole.load_t), p) ;
        if (game.game_state==1 & game.player==1 & game.mouse_sost==1){
            p.setColor(pole.load_in_color);
            canvas.drawRect((float) (pole.load_x + pole.load_t), (float) (pole.load_y + pole.load_t), (float) (pole.load_x + (game.now_hurt/game.m_hurt)*(pole.load_w - pole.load_t)),
                    (float) (pole.load_y+pole.load_h - pole.load_t), p) ;
        }






    }
    void sc_balls_p(Canvas canvas, Paint p){
        for (int i = 0; i< game.balls.length; i++){
            if (game.balls[i].zkt & !game.balls[i].on){
                Ball b = game.balls[i];
                //закраска самого шара
                p.setColor(b.color);
                canvas.drawCircle((float) b.x, (float) b.y, game.rad, p);
                p.setColor(Color.argb(255, 255, 255, 255));
                //закраска боковых белых частей (только у полосатых)
                if (b.num > 8) {
                    {
                        p.setColor(Color.argb(255, 255, 255, 255));
                        wp_draw(canvas, p, b.pb, b, b.mp_rad);
                        p.setColor(Color.argb(255, 255, 255, 255));
                        wp_draw(canvas, p, b.pb2, b, b.mp_rad);
                    }
                }
                p.setColor(pole.lamp.light_color);
                double[] xy = pole.lamp.getLight(b);
                canvas.drawCircle((float) xy[0], (float) xy[1], (float) (b.rad * pole.lamp.light_rad), p);
                canvas.drawCircle((float) xy[0], (float) xy[1], (float) (b.rad * pole.lamp.light_rad / 2), p);
            }
        }




    }

}