package com.example.billiard;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Bank bank;
    int coins = 380;
    int spin_time = 4000;
    String name = "Игрок";
    Champ now_champ  = new Champ(name, 0);
    int [][] hard_Ai = {{15, 4}, {30, 4}, {45, 4}, {60, 4}};
    int choice_tur = -1;
    int now_ava = 0;
    int now_kiy = 0;
    //список доступных аватарок
    boolean ava[] = {true, false, false, false, false};
    //список доступных турниров
    boolean[] tur_s = {true, false, false, false};
    //список доступных киев
    boolean[] kiy_s = {true, false, false};
    //максимальное число раундов
    int n_match = 6;
    //загрузка настроек
    int loud;
    int load;
    int lamp;

    //список выйгрыша в турнири
    int[] money_prize = {100, 200, 300, 400};
    //класс звуков
    Music m;
    //стоимость вращения
    int spin_cost = 400;

    //класс информации о пользователе
    Id id = new Id("Игрок1", 0);
    //класс информации о противнике
    Id enemy_id = new Id("Игрок2", 0);
    //размеры экрана
    Point size;
    //осевой класс
    Game game;
    //класс изображений
    Img imgs = new Img();
    //создание хандлера
    Handler handler = new Handler();
    //задержка в главном цикле
    int fps = 20;
    //размеры экрана
    double window_w;
    double window_h;
    int game_place = 0;//0-меню, 1 - игра
    double millis_for_dc;

    //загрузка изображений
    void load_img(){
        //бильярдная доска
        imgs.bl_field[0] = BitmapFactory.decodeResource(getResources(), R.drawable.deskr);
        imgs.bl_field[1] = BitmapFactory.decodeResource(getResources(), R.drawable.deski);
        //аватарки
        {
            imgs.avatars[0] = BitmapFactory.decodeResource(getResources(), R.drawable.f0);
            imgs.avatars[1] = BitmapFactory.decodeResource(getResources(), R.drawable.f1);
            imgs.avatars[2] = BitmapFactory.decodeResource(getResources(), R.drawable.f2);
            imgs.avatars[3] = BitmapFactory.decodeResource(getResources(), R.drawable.f3);
            imgs.avatars[4] = BitmapFactory.decodeResource(getResources(), R.drawable.f4);
            imgs.avatars[5] = BitmapFactory.decodeResource(getResources(), R.drawable.enemy_f);
        }
        //иконки руки
        {
            imgs.hand[0] = BitmapFactory.decodeResource(getResources(), R.drawable.grab_my);
            imgs.hand[1] = BitmapFactory.decodeResource(getResources(), R.drawable.grab_enemy);
            imgs.hand[2] = BitmapFactory.decodeResource(getResources(), R.drawable.prc);
        }
        //кий
        {
            imgs.kiy[0] = BitmapFactory.decodeResource(getResources(), R.drawable.kiy1);
            imgs.kiy[1] = BitmapFactory.decodeResource(getResources(), R.drawable.kiy2);
            imgs.kiy[2] = BitmapFactory.decodeResource(getResources(), R.drawable.kiy3);
        }

    }
    //загрузка данных
    void load_all(){

        SharedPreferences sPref = getPreferences(MODE_PRIVATE);
        String sets = (sPref.getString(bank.save_ind, ""));
        if (sets.length()>0){
            bank.now_save = sets;
            Log.i("mendel2", "take: " +sets);
            bank.load_in_main();
        }


    }
    int n_prize(Champ c, int mx){
        int[] map = c.getCord();
        //Log.i("maprick", map[0] + ";" + map[1]);
        if (map[0]==0){
            return (int)(mx * c.state/(8));
        }
        if (map[0]==1){
            return (int)(mx * (c.state-1)/(24));
        }
        if (map[0]==2){
            return (int)(mx/2);
        }
        return  0;
    }
    //загрузка раздела с турнирвми
    void load_tur_s() {
        //картинки турниров
        {
            ImageView[] view_tur = {findViewById(R.id.img_r1),findViewById(R.id.img_r2),findViewById(R.id.img_i1),findViewById(R.id.img_i2)};
            //их прорисовка и доступность
            for (int i = 0; i<tur_s.length; i++){
                //Log.i("mendel", view_tur[i] + "");
                view_tur[i].setClickable(tur_s[i]);
                if (tur_s[i]){
                    if (i==0){
                        view_tur[i].setImageBitmap(Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.r1)));
                    }
                    if (i==1){
                        view_tur[i].setImageBitmap(Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.r2)));
                    }
                    if (i==2){
                        view_tur[i].setImageBitmap(Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.i1)));
                    }
                    if (i==3){
                        view_tur[i].setImageBitmap(Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.i2)));
                    }
                }
                else {
                    view_tur[i].setImageBitmap((BitmapFactory.decodeResource(getResources(), R.drawable.empty_t)));
                }
            }
        }
        //информация о турнире

        {
            String z_lh = "Приз за победу: \n";
            String z_ld = "Раундов: 6";
            String z_rh = "Участие: \n";
            String z_rd = "Выйгрыш: \n";
            float in_active_alp = (float) 0.3;
            Button high_info = findViewById(R.id.high_info);
            Button high_cont = findViewById(R.id.turs_cont);
            Button leftH_info = findViewById(R.id.t_cost);
            Button leftD_info = findViewById(R.id.short_info);
            Button low_restart = findViewById(R.id.turs_restart);
            Button rightH_info = findViewById(R.id.in_tur);
            Button rightD_info = findViewById(R.id.now_prize);
            Button low_pose_info = findViewById(R.id.low_pose_info);
            Button vers = findViewById(R.id.versus);
            Button[] v_p = {findViewById(R.id.v_p1), findViewById(R.id.v_p2)};
            int text_big_size = (int)(window_h/75);
            high_info.setTextSize(text_big_size);
            high_cont.setTextSize(text_big_size*2/3);
            low_restart.setTextSize(text_big_size*2/3);
            leftD_info.setTextSize(text_big_size*2/3);
            leftH_info.setTextSize(text_big_size*2/3);
            rightD_info.setTextSize(text_big_size*2/3);
            rightH_info.setTextSize(text_big_size*2/3);
            low_pose_info.setTextSize(text_big_size*2/3);
            v_p[0].setTextSize(text_big_size*2/3);
            v_p[1].setTextSize(text_big_size*2/3);
            vers.setTextSize(text_big_size*2/3);
            high_info.setTextColor(Color.argb(180, 118, 152, 133));
            int in_tur_color = Color.argb(255, 141, 186, 45);
            int in_tur_color2 = Color.argb(255, 186, 95, 95);
            rightH_info.setTextColor(in_tur_color);
            if (true){
               high_cont.setVisibility(View.INVISIBLE);
               low_restart.setVisibility(View.INVISIBLE);
                leftD_info.setVisibility(View.INVISIBLE);
                leftH_info.setVisibility(View.INVISIBLE);
                rightD_info.setVisibility(View.INVISIBLE);
                rightH_info.setVisibility(View.INVISIBLE);
                low_pose_info.setVisibility(View.INVISIBLE);
                v_p[0].setVisibility(View.INVISIBLE);
                v_p[1].setVisibility(View.INVISIBLE);
                vers.setVisibility(View.INVISIBLE);
                high_cont.setClickable(false);
               high_info.setText("Выберете турнир");
               high_info.setTextColor(Color.argb(180, 129, 129, 129));
               if (now_champ.go) {
                   rightH_info.setTextColor(in_tur_color);
                   rightH_info.setText(z_rh + "Да");
               }
               else {
                   rightH_info.setTextColor(in_tur_color2);
                   rightH_info.setText(z_rh + "Нет");

               }
            }
            if (choice_tur==0){
                rightH_info.setTextColor(in_tur_color2);
                high_info.setText("Турнир Санкт-Петербург");
                high_cont.setVisibility(View.VISIBLE);
                low_restart.setVisibility(View.VISIBLE);
                leftD_info.setVisibility(View.VISIBLE);
                leftH_info.setVisibility(View.VISIBLE);
                leftH_info.setText(z_lh+money_prize[choice_tur]);
                leftD_info.setText(z_ld);
                rightD_info.setVisibility(View.INVISIBLE);
                rightH_info.setVisibility(View.VISIBLE);
                if (now_champ.place!=choice_tur) {
                    rightH_info.setTextColor(in_tur_color2);
                    high_cont.setAlpha(in_active_alp);
                    rightH_info.setText(z_rh + "Нет");


                }
                if (now_champ.place==choice_tur) {
                    if (!now_champ.go) {
                        rightH_info.setTextColor(in_tur_color2);
                        high_cont.setAlpha(in_active_alp);
                        rightH_info.setText(z_rh + "Нет");
                    }
                    if (now_champ.go) {
                        high_cont.setClickable(true);
                        low_pose_info.setVisibility(View.VISIBLE);
                        if (now_champ.getCord()[0]==0){
                            low_pose_info.setText("Сетка победителей, Тур " + (now_champ.state + 1));
                        }
                        if (now_champ.getCord()[0]==1){
                            low_pose_info.setText("Сетка проигравших, Тур " + (now_champ.state + 1));
                        }
                        if (now_champ.getCord()[0]==2){
                            low_pose_info.setText("Финал");
                        }
                        vers.setVisibility(View.VISIBLE);
                        for (int i = 0; i<v_p.length; i++){
                            v_p[i].setText(now_champ.block[i]);
                            v_p[i].setVisibility(View.VISIBLE);
                        }
                        rightH_info.setTextColor(in_tur_color);
                        high_cont.setAlpha(1);
                        rightH_info.setText(z_rh + "Да");
                        rightD_info.setVisibility(View.VISIBLE);
                        rightD_info.setText(z_rd+n_prize(now_champ, money_prize[choice_tur]));
                    }


                }




            }
            if (choice_tur==1){
                rightH_info.setTextColor(in_tur_color2);
                high_info.setText("Турнир Москва");
                high_cont.setVisibility(View.VISIBLE);
                low_restart.setVisibility(View.VISIBLE);
                leftD_info.setVisibility(View.VISIBLE);
                leftH_info.setVisibility(View.VISIBLE);
                leftH_info.setText(z_lh+money_prize[choice_tur]);
                leftD_info.setText(z_ld);
                rightD_info.setVisibility(View.INVISIBLE);
                rightH_info.setVisibility(View.VISIBLE);
                if (now_champ.place!=choice_tur) {
                    rightH_info.setTextColor(in_tur_color2);
                    high_cont.setAlpha(in_active_alp);
                    rightH_info.setText(z_rh + "Нет");


                }
                if (now_champ.place==choice_tur) {
                    if (!now_champ.go) {
                        rightH_info.setTextColor(in_tur_color2);
                        high_cont.setAlpha(in_active_alp);
                        rightH_info.setText(z_rh + "Нет");
                    }
                    if (now_champ.go) {
                        high_cont.setClickable(true);
                        low_pose_info.setVisibility(View.VISIBLE);
                        if (now_champ.getCord()[0]==0){
                            low_pose_info.setText("Сетка победителей, Тур " + (now_champ.state + 1));
                        }
                        if (now_champ.getCord()[0]==1){
                            low_pose_info.setText("Сетка проигравших, Тур " + (now_champ.state + 1));
                        }
                        if (now_champ.getCord()[0]==2){
                            low_pose_info.setText("Финал");
                        }
                        vers.setVisibility(View.VISIBLE);
                        for (int i = 0; i<v_p.length; i++){
                            v_p[i].setText(now_champ.block[i]);
                            v_p[i].setVisibility(View.VISIBLE);
                        }
                        rightH_info.setTextColor(in_tur_color);
                        high_cont.setAlpha(1);
                        rightH_info.setText(z_rh + "Да");
                        rightD_info.setVisibility(View.VISIBLE);
                        rightD_info.setText(z_rd+n_prize(now_champ, money_prize[choice_tur]));
                    }


                }




            }
            if (choice_tur==2){
                rightH_info.setTextColor(in_tur_color2);
                high_info.setText("Турнир Мумбай");
                high_cont.setVisibility(View.VISIBLE);
                low_restart.setVisibility(View.VISIBLE);
                leftD_info.setVisibility(View.VISIBLE);
                leftH_info.setVisibility(View.VISIBLE);
                leftH_info.setText(z_lh+money_prize[choice_tur]);
                leftD_info.setText(z_ld);
                rightD_info.setVisibility(View.INVISIBLE);
                rightH_info.setVisibility(View.VISIBLE);
                if (now_champ.place!=choice_tur) {
                    rightH_info.setTextColor(in_tur_color2);
                    high_cont.setAlpha(in_active_alp);
                    rightH_info.setText(z_rh + "Нет");


                }
                if (now_champ.place==choice_tur) {
                    if (!now_champ.go) {
                        rightH_info.setTextColor(in_tur_color2);
                        high_cont.setAlpha(in_active_alp);
                        rightH_info.setText(z_rh + "Нет");
                    }
                    if (now_champ.go) {
                        high_cont.setClickable(true);
                        low_pose_info.setVisibility(View.VISIBLE);
                        if (now_champ.getCord()[0]==0){
                            low_pose_info.setText("Сетка победителей, Тур " + (now_champ.state + 1));
                        }
                        if (now_champ.getCord()[0]==1){
                            low_pose_info.setText("Сетка проигравших, Тур " + (now_champ.state + 1));
                        }
                        if (now_champ.getCord()[0]==2){
                            low_pose_info.setText("Финал");
                        }
                        vers.setVisibility(View.VISIBLE);
                        for (int i = 0; i<v_p.length; i++){
                            v_p[i].setText(now_champ.block[i]);
                            v_p[i].setVisibility(View.VISIBLE);
                        }
                        rightH_info.setTextColor(in_tur_color);
                        high_cont.setAlpha(1);
                        rightH_info.setText(z_rh + "Да");
                        rightD_info.setVisibility(View.VISIBLE);
                        rightD_info.setText(z_rd+n_prize(now_champ, money_prize[choice_tur]));
                    }


                }




            }
            if (choice_tur==3){
                rightH_info.setTextColor(in_tur_color2);
                high_info.setText("Турнир Агра");
                high_cont.setVisibility(View.VISIBLE);
                low_restart.setVisibility(View.VISIBLE);
                leftD_info.setVisibility(View.VISIBLE);
                leftH_info.setVisibility(View.VISIBLE);
                leftH_info.setText(z_lh+money_prize[choice_tur]);
                leftD_info.setText(z_ld);
                rightD_info.setVisibility(View.INVISIBLE);
                rightH_info.setVisibility(View.VISIBLE);
                if (now_champ.place!=choice_tur) {
                    rightH_info.setTextColor(in_tur_color2);
                    high_cont.setAlpha(in_active_alp);
                    rightH_info.setText(z_rh + "Нет");


                }
                if (now_champ.place==choice_tur) {
                    if (!now_champ.go) {
                        rightH_info.setTextColor(in_tur_color2);
                        high_cont.setAlpha(in_active_alp);
                        rightH_info.setText(z_rh + "Нет");
                    }
                    if (now_champ.go) {
                        high_cont.setClickable(true);
                        low_pose_info.setVisibility(View.VISIBLE);
                        if (now_champ.getCord()[0]==0){
                            low_pose_info.setText("Сетка победителей, Тур " + (now_champ.state + 1));
                        }
                        if (now_champ.getCord()[0]==1){
                            low_pose_info.setText("Сетка проигравших, Тур " + (now_champ.state + 1));
                        }
                        if (now_champ.getCord()[0]==2){
                            low_pose_info.setText("Финал");
                        }
                        vers.setVisibility(View.VISIBLE);
                        for (int i = 0; i<v_p.length; i++){
                            v_p[i].setText(now_champ.block[i]);
                            v_p[i].setVisibility(View.VISIBLE);
                        }
                        rightH_info.setTextColor(in_tur_color);
                        high_cont.setAlpha(1);
                        rightH_info.setText(z_rh + "Да");
                        rightD_info.setVisibility(View.VISIBLE);
                        rightD_info.setText(z_rd+n_prize(now_champ, money_prize[choice_tur]));
                    }


                }




            }
        }
    }
    void end_tur(int[] cords, boolean win){
        now_champ = new Champ(name, 0);

    }
    void after_game(boolean win){
        int[] cords = now_champ.getCord();
        if (win){
            if (cords[0]==2){
                if (win){
                    coins = coins+money_prize[now_champ.place];
                }
                end_tur(cords, win);
            }
            else {
                now_champ.shag(true);
            }

        }
        if (!win){
            if (cords[0]==2){
                coins = coins + n_prize(now_champ, money_prize[now_champ.place]);
                end_tur(cords, win);
            }
            if (cords[0]==1){
                coins = coins + n_prize(now_champ, money_prize[now_champ.place]);
                end_tur(cords, win);
            }
            if (cords[0]==0){
                now_champ.shag(false);
            }
        }
        save_game();
    }
    void clear_game(){
        game = new Game(window_w, window_h, imgs, fps, id, enemy_id, m, loud, load, lamp, this);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //создание банка
        bank = new Bank(this);
        //now_champ.shag(true);

        //запись времени
        millis_for_dc = System.currentTimeMillis();
        //загрузка соранений

        //запуск загрузки изображений
        load_img();
        //звуки
        m = new Music(this, (AudioManager) getSystemService(Context.AUDIO_SERVICE));
        //установка только горизонтального положения экрана
        {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        //получение размеров экрана
        {
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            window_w = metrics.widthPixels;
            window_h = metrics.heightPixels;
        }
        clear_game();
        load_all();

        setContentView(R.layout.championships);
        //загрузка раздела с турнирами
        load_tur_s();
    }


    //функция прогрузки игрового процесса
    void game(){

        game.game_controller();
        save_game();
        if (game.game_state==3){
            game_place = 0;
            after_game(game.player_w==1);
            setContentView(R.layout.championships);
            clear_game();
            load_tur_s();

        }
        else{
            {
                DrawView draw = new DrawView(this, game);
                setContentView(draw);

            }
        }

    }
    void start_game(Game game){
        game_place = 1;
        //главный цикл игры
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                //запуск игрового процесса
                //game();
                if (game_place==1) {
                    game();
                    handler.postDelayed(this, fps);
                }

            }

        };runnable.run();

    }
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (game_place == 1){
                    game.mouse_sost = 1;
                    game.mouse_x = motionEvent.getX();
                    game.mouse_y = motionEvent.getY();
                }
                break;
            case MotionEvent.ACTION_UP:
                if (game_place == 1){
                    if (game.double_click(millis_for_dc, motionEvent.getX(), motionEvent.getY())){
                        game_place = 0;
                        setContentView(R.layout.championships);

                    }
                    millis_for_dc = System.currentTimeMillis();
                    game.mouse_sost = 2;
                    /*if (play.dpsave) {
                        play.player_sm();
                        Log.i("hhh", play.player + ";" );
                    }*/

                    game.mouse_x = motionEvent.getX();
                    game.mouse_y = motionEvent.getY();
                }

        }
        return true;
    }
    public void go_menu_fs(View view) {
        setContentView(R.layout.championships);
        load_tur_s();
    }
    void save_game(){
        bank.new_now_save();

        SharedPreferences sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(bank.save_ind, bank.now_save);
        Log.i("mendel3", "rrrr: " + bank.now_save);
        ed.commit();
    }

    public void r1_choice(View view) {
        choice_tur = 0;
        load_tur_s();
    }

    public void r2_choice(View view) {
        choice_tur = 1;
        load_tur_s();
    }

    public void i1_choice(View view) {
        choice_tur = 2;
        load_tur_s();

    }

    public void i2_choice(View view) {
        choice_tur = 3;
        load_tur_s();
    }

    public void cont_tur(View view) {
        now_champ.name = name;
        now_champ.block[now_champ.num]=name;
        id.name = now_champ.block[now_champ.num];
        enemy_id.name = now_champ.block[1-now_champ.num];
        game.id = id;
        game.enemy_id = enemy_id;
        //clear_game();
        start_game(game);
    }

    public void restart_tur(View view) {
        coins = coins + n_prize(now_champ, money_prize[now_champ.place]);
        now_champ = new Champ(name, (int)(choice_tur));
        now_champ.state = 0;
        now_champ.place = choice_tur;
        id.name = now_champ.block[now_champ.num];
        enemy_id.name = now_champ.block[1-now_champ.num];
        now_champ.go = true;
        clear_game();
        save_game();
        load_tur_s();

    }

    public void go_chm(View view) {
        setContentView(R.layout.championships);
        load_tur_s();
    }
    void load_shop() {
        ImageView round = findViewById(R.id.fortune);
        Button gold = findViewById(R.id.gold);
        Button spin = findViewById(R.id.spin);
        int ts = (int) (window_h/65);
        gold.setTextSize(ts);
        spin.setTextSize(ts);
        gold.setText("У вас: " + coins);
        spin.setText("Вращать: " + spin_cost);
        if (coins>spin_cost){
            spin.setClickable(true);
            spin.setAlpha(1);
        }
        else {
            spin.setClickable(false);
            spin.setAlpha((float) 0.3);
        }
    }
    public void go_shop(View view) {
        setContentView(R.layout.shop);
        save_game();
        load_shop();
    }
    void spin_finish(double ang){
        ImageView round = findViewById(R.id.fortune);
        Button gold = findViewById(R.id.gold);
        Button spin = findViewById(R.id.spin);
        ang = ang/180 * 3.14;
        if (Math.cos(ang)<Math.cos(0) & Math.cos(ang)>Math.cos(2*3.14/3) & Math.sin(ang)>0){
            Log.i("interapk", "ticket");
            addNew(0);
        }
        if (Math.cos(ang)<Math.cos(0)& Math.cos(ang)>Math.cos(2*3.14/3) & Math.sin(ang)<0){
            Log.i("interapk", "kiy");
            addNew(1);

        }
        if (Math.cos(ang)<Math.cos(0) & Math.cos(ang)<Math.cos(2*3.14/3)){
            Log.i("interapk", "avatar");
            addNew(2);
        }
        load_shop();
    }
    void addNew(int obj){//0 - ticket, 1 - kiy, 2 - avatar
        if (obj==0){
            for (int i = 0; i<tur_s.length; i++){
                if (!tur_s[i]){
                    tur_s[i] = true;
                    save_game();
                    return;
                }
            }
        }
        if (obj==1){
            for (int i = 0; i<kiy_s.length; i++){
                if (!kiy_s[i]){
                    int nn = (int) (Math.random()*100)%3;
                    while (kiy_s[nn]){
                        nn = (int) (Math.random()*100)%3;
                    }
                    kiy_s[nn] = true;
                    save_game();
                    return;
                }
            }
        }
        if (obj==2) {
            for (int i = 0; i < ava.length; i++) {
                if (!ava[i]) {
                    int nn = (int) (Math.random() * 100) % 5;
                    while (ava[nn]) {
                        nn = (int) (Math.random() * 100) % 5;
                    }
                    ava[nn] = true;
                    save_game();
                    return;
                }
            }
        }
    }
    public void spin_f(View view) {

        coins = coins-spin_cost;
        save_game();
        load_shop();
        ImageView round = findViewById(R.id.fortune);
        Button gold = findViewById(R.id.gold);
        Button spin = findViewById(R.id.spin);
        spin.setClickable(false);
        Runnable runnable = new Runnable() {
            int s_t = (int) (spin_time + Math.random()*(spin_time*0.4));
            double speed = 50 + Math.random()*20;
            double minus = (double) speed/((double) s_t/fps);
            double ang = 0;
            @Override
            public void run() {
                        ang = ang+speed;
                        speed = speed-minus;
                        s_t = s_t-fps;
                        round.setRotation((float) ang);
                        if (s_t>0) {
                            handler.postDelayed(this, fps);
                        }
                        else {
                            spin_finish(ang);
                        }

                }
        };runnable.run();

    }
    boolean norm_name(String n){
        for (int i = 0; i<n.length(); i++){
            if ((""+n.charAt(i)).equals(";") || (""+n.charAt(i)).equals(",") || (""+n.charAt(i)).equals("$")){
                return  false;
            }
        }
        return  true;
    }
    void load_set(){
        EditText ent_n = findViewById(R.id.enter_name);
        Button save_name = findViewById(R.id.save_name);
        ImageView global_ava = findViewById(R.id.global_ava);
        ImageView show_k = findViewById(R.id.show_kiy);
        global_ava.setImageBitmap(imgs.avatars[now_ava]);
        ent_n.setText(name);
        show_k.setImageBitmap(imgs.kiy[now_kiy]);

        ImageView[] ava_s = {findViewById(R.id.a0), findViewById(R.id.a1), findViewById(R.id.a2), findViewById(R.id.a3), findViewById(R.id.a4)};
        for (int i = 0; i<ava.length; i++){
            if (ava[i]){
                ava_s[i].setImageBitmap(imgs.avatars[i]);
                ava_s[i].setClickable(true);
            }
            else {
                ava_s[i].setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.lock));
                ava_s[i].setClickable(false);
            }
        }
        ImageView[] kiys = {findViewById(R.id.k0), findViewById(R.id.k1), findViewById(R.id.k2)};
        for (int i = 0; i<kiy_s.length; i++){
            if (kiy_s[i]){
                kiys[i].setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.by_kiy));
                kiys[i].setClickable(true);
            }
            else {
                kiys[i].setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.lock));
                kiys[i].setClickable(false);
            }
        }

    }
    public void go_set(View view) {
        setContentView(R.layout.settings);
        load_set();

    }

    public void change_name(View view) {
        EditText ent_n = findViewById(R.id.enter_name);
        Button save_name = findViewById(R.id.save_name);
        if (norm_name(String.valueOf(ent_n.getText()))) {
            name = String.valueOf(ent_n.getText());
            now_champ.block[now_champ.num] = name;
            save_game();
        }
        load_set();
    }

    public void c_ava0(View view) {
        now_ava = 0;
        save_game();
        load_set();
    }
    public void c_ava1(View view) {
        now_ava = 1;
        save_game();
        load_set();
    }
    public void c_ava2(View view) {
        now_ava = 2;
        save_game();
        load_set();
    }
    public void c_ava3(View view) {
        now_ava = 3;
        save_game();
        load_set();
    }
    public void c_ava4(View view) {
        now_ava = 4;
        save_game();
        load_set();
    }


    public void c_k0(View view) {
        now_kiy = 0;
        save_game();
        load_set();
    }

    public void c_k1(View view) {
        now_kiy = 1;
        save_game();
        load_set();
    }

    public void c_k2(View view) {
        now_kiy = 2;
        save_game();
        load_set();
    }
}