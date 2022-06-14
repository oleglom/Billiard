package com.example.billiard;

import android.util.Log;

import java.util.ArrayList;
public class Bank {
    String save_ind = "rembo300";
    MainActivity main;
    public Bank(MainActivity main){
        this.main = main;
    }
    String now_save = "";
    String base_code(){
        String result = "";
        //coins,turs,avas,now_ava
        result = result+(main.coins + ";");
        result = result + all_chm_info(main.tur_s)+";";
        for (int i = 0; i<main.ava.length;i++){
            if (main.ava[i]){
                result = result+"1";
            }
            else {
                result = result+"0";
            }
        }
        result = result+";";
        result = result+main.now_ava+";";

        for (int i = 0; i<main.kiy_s.length;i++){
            if (main.kiy_s[i]){
                result = result+"1";
            }
            else {
                result = result+"0";
            }
        }
        result = result+";";
        result = result+main.now_kiy+";";

        result = result+main.name+";";
        return result;
    }
    String chm_code(Champ chm){
        // номер места go;(0-3);тип(1/0);state;1:1:1:1(int[]);#fd,gg,0#....
        String result = "";
        if (chm.go) {
            result = result + "1;";
        }
        else {
            result = result + "0;";
        }
        result = result+chm.place+";";
        result = result + chm.type+";";
        result = result + chm.state + ";";
        int[] map = chm.getCord();
        result = result+map[0]+map[1]+map[2]+map[3]+";";
        ArrayList wins = (ArrayList) chm.array_champ.get(0);
        ArrayList loses = (ArrayList) chm.array_champ.get(1);
        ArrayList fb = (ArrayList) chm.array_champ.get(2);

        for (int i = 0; i<wins.size();i++){
            for (int j = 0; j<((String[][])(wins.get(i))).length; j++){
                result = result + ((String[][])(wins.get(i)))[j][0]+
                        ","+((String[][])(wins.get(i)))[j][1]+","+((String[][])(wins.get(i)))[j][2]+ " "+ "#";
            }
        }
        for (int i = 0; i<loses.size();i++){
            for (int j = 0; j<((String[][])(loses.get(i))).length; j++){
                result = result + ((String[][])(loses.get(i)))[j][0]+
                        ","+((String[][])(loses.get(i)))[j][1]+","+((String[][])(loses.get(i)))[j][2]+ " "+ "#";

            }
        }
        for (int i = 0; i<fb.size();i++){
            for (int j = 0; j<((String[][])(fb.get(i))).length; j++){
                result = result +((String[][])(fb.get(i)))[j][0]+
                        ","+((String[][])(fb.get(i)))[j][1]+","+((String[][])(fb.get(i)))[j][2] + " " + "#";
            }
        }

        return result + ";";
    }
    String all_chm_info(boolean[] turs){
        String tur_s = "";
        if (turs[0]){
            tur_s = tur_s + "1";
        }
        else {
            tur_s = tur_s + "0";
        }
        if (turs[1]){
            tur_s = tur_s + "1";
        }
        else {
            tur_s = tur_s + "0";
        }
        if (turs[2]){
            tur_s = tur_s + "1";
        }
        else {
            tur_s = tur_s + "0";
        }
        if (turs[3]){
            tur_s = tur_s + "1";
        }
        else {
            tur_s = tur_s + "0";
        }
        return  tur_s;

    }
    Champ code_chm(String code){
        // номер места go;(0-3);тип(1/0);state;1:1:1:1(int[]);#fd,gg,0#....
        Champ chm = new Champ("Вы", 0);
        boolean go = false;
        int place = 0;
        int type = 0;
        chm.setArray();
        String buffer = "";
        int p = -1;
        int[] id = new int[1];
        for (int i = 0; i<code.length(); i++){
            if ((""+code.charAt(i)).equals(";")){
                if (p==-1){
                    go = Integer.parseInt(buffer)==1;
                }
                if (p==0){
                    place = Integer.parseInt(buffer);
                }
                if (p==1){
                    chm=new Champ("Вы",Integer.parseInt(buffer));
                    chm.go=go;
                    chm.place=place;
                }
                if (p==2){
                    chm.state = Integer.parseInt(buffer);
                }
                if (p==3){
                    id = new int[]{Integer.parseInt(String.valueOf(buffer.charAt(0))), Integer.parseInt(String.valueOf(buffer.charAt(1))),
                            Integer.parseInt(String.valueOf(buffer.charAt(2))), Integer.parseInt(String.valueOf(buffer.charAt(3)))};
                }
                if (p==4){
                    ArrayList wins = (ArrayList) chm.array_champ.get(0);
                    ArrayList loses = (ArrayList) chm.array_champ.get(1);
                    ArrayList fb = (ArrayList) chm.array_champ.get(2);
                    int nb = 0;
                    String sub_buf = "";
                    for (int j = 0; j < buffer.length(); j++) {
                        if (!(""+buffer.charAt(j)).equals("#")){
                            sub_buf = sub_buf + buffer.charAt(j);

                        }
                        else{
                            String gret = "";
                            int ppt = 0;
                            String[] now_r = new String[3];
                            for (int s = 0; s<sub_buf.length(); s++){
                                if (!(""+sub_buf.charAt(s)).equals(",")){
                                    gret = gret+sub_buf.charAt(s);
                                }
                                else{
                                    //Log.i("ddd", gret);
                                    now_r[ppt]=gret;
                                    gret = "";
                                    ppt++;

                                }
                            }
                            Log.i("ddd", now_r[0] + "," + now_r[1] + "," + now_r[2]);
                            //винера
                            if (nb<=3){
                                ((String[][])wins.get(0))[nb] = now_r.clone();
                            }
                            if (nb>3 & nb<=5){
                                ((String[][])wins.get(1))[nb-4] = now_r.clone();
                            }
                            if (nb>5 & nb<7){
                                ((String[][])wins.get(2))[0] = now_r.clone();
                            }
                            //лузера
                            if (nb>=7 & nb<=8){
                                ((String[][])loses.get(0))[nb-7] = now_r.clone();
                            }
                            if (nb>=9 & nb<=10){
                                ((String[][])loses.get(1))[nb-9] = now_r.clone();
                            }
                            if (nb==11){
                                ((String[][])loses.get(2))[0] = now_r.clone();
                            }
                            if (nb==12){
                                ((String[][])loses.get(3))[0] = now_r.clone();
                            }
                            //финал
                            if (nb==13){
                                ((String[][])fb.get(0))[0] = now_r.clone();
                            }
                            nb = nb + 1;
                            sub_buf = "";
                        }
                    }
                }



                p++;
                buffer="";
            }
            else {
                buffer = buffer + code.charAt(i);
            }

        }
        chm.block_by_cords(id);
        return chm;
    }
    void new_now_save(){
        String sub_save = "";
        sub_save = sub_save + base_code() + "$";
        sub_save = sub_save + chm_code(main.now_champ) + "$";
        sub_save = sub_save + game_code(main.game) + "$";
        now_save = sub_save;

    }
    void load_in_main(){
        String buff = "";
        int state = 0;
        for (int i = 0; i<now_save.length(); i++){
            if ((""+now_save.charAt(i)).equals("$")){
                if (state==0){
                    Log.i("vinc", buff);
                    int n = 0;
                    String sb = "";
                    for (int j = 0; j<buff.length();j++) {
                        if ((""+buff.charAt(j)).equals(";")) {
                            if (n==0) {
                                main.coins = Integer.parseInt(sb);
                            }
                            if (n==1) {
                                boolean tur_s[] = new boolean[4];
                                if (("" + sb.charAt(0)).equals("1")) {
                                    tur_s[0] = true;
                                } else {
                                    tur_s[0] = false;
                                }
                                if (("" + sb.charAt(1)).equals("1")) {
                                    tur_s[1] = true;
                                } else {
                                    tur_s[1] = false;
                                }
                                if (("" + sb.charAt(2)).equals("1")) {
                                    tur_s[2] = true;
                                } else {
                                    tur_s[2] = false;
                                }
                                if (("" + sb.charAt(3)).equals("1")) {
                                    tur_s[3] = true;
                                } else {
                                    tur_s[3] = false;
                                }
                                main.tur_s = tur_s;
                            }
                            if (n==2) {
                                boolean ava[] = new boolean[main.ava.length];
                                if (("" + sb.charAt(0)).equals("1")) {
                                    ava[0] = true;
                                } else {
                                    ava[0] = false;
                                }
                                if (("" + sb.charAt(1)).equals("1")) {
                                    ava[1] = true;
                                } else {
                                    ava[1] = false;
                                }
                                if (("" + sb.charAt(2)).equals("1")) {
                                    ava[2] = true;
                                } else {
                                    ava[2] = false;
                                }
                                if (("" + sb.charAt(3)).equals("1")) {
                                    ava[3] = true;
                                } else {
                                    ava[3] = false;
                                }
                                if (("" + sb.charAt(4)).equals("1")) {
                                    ava[4] = true;
                                } else {
                                    ava[4] = false;
                                }
                                main.ava = ava;
                            }
                            if (n==3){
                                main.now_ava=Integer.parseInt(sb);
                            }
                            if (n==4) {
                                boolean kiy[] = new boolean[main.kiy_s.length];
                                if (("" + sb.charAt(0)).equals("1")) {
                                    kiy[0] = true;
                                } else {
                                    kiy[0] = false;
                                }
                                if (("" + sb.charAt(1)).equals("1")) {
                                    kiy[1] = true;
                                } else {
                                    kiy[1] = false;
                                }
                                if (("" + sb.charAt(2)).equals("1")) {
                                    kiy[2] = true;
                                } else {
                                    kiy[2] = false;
                                }
                                main.kiy_s = kiy;
                            }
                            if (n==5) {
                                main.now_kiy = Integer.parseInt(sb);
                            }
                            if (n==6) {
                                main.name = sb;
                            }
                            n++;
                            sb="";
                        }
                        else {
                            sb = sb+buff.charAt(j);
                        }
                    }

                }
                else if (state==1){
                    Log.i("mendel", "buff: " + buff);
                    main.now_champ = this.code_chm(buff);
                    Log.i("mendel2", "buff: " + main.now_champ.go);
                }

                else if (state==2){

                    code_game(main.game, buff);
                }
                state = state+1;
                buff = "";
            }
            else {
                buff = buff + now_save.charAt(i);
            }
        }
        //Log.i("geopat", now_save);
    }
    String ball_code(Ball b){
        //num, rad, x,y, pb[0], pb[1], pb[2], vx, vy, on, zkt, finish, start sc
        String result = "";
        result = result + b.num + ",";
        result = result + b.rad + ",";
        result = result + (int)b.x + "," + (int)b.y + ",";
        result = result + (int)(b.pb[0]*100) + "," + (int)(b.pb[1]*100) + "," + (int)(b.pb[2]*100) + ",";
        result = result + (int)b.vx + "," + (int)b.vy + ",";
        if (b.on){
            result = result + "1,";
        }
        else {
            result = result + "0,";
        }
        if (b.zkt){
            result = result + "1,";
        }
        else {
            result = result + "0,";
        }
        if (b.finish){
            result = result + "1,";
        }
        else {
            result = result + "0,";
        }
        if (b.start_sc){
            result = result + "1,";
        }
        else {
            result = result + "0,";
        }





        return  result+",";
    }
    Ball code_ball (String code){
        //num, rad, x,y, pb[0], pb[1], pb[2], vx, vy, on, zkt, finish, start sc
        Ball b= new Ball(0,0,0,0);
        int num = 0;
        int rad = 0;
        int x = 0;
        int y = 0;
        int state = 0;
        String buf = "";
        Log.i("geopat", code);
        for(int i = 0; i<code.length(); i++){
            if (("" + code.charAt(i)).equals(",")){
                if (state==0){
                    num = Integer.parseInt(buf);
                }
                else if (state==1){
                    rad = Integer.parseInt(buf);
                }
                else if (state==2){
                    x = Integer.parseInt(buf);
                }
                else if (state==3){
                    y = Integer.parseInt(buf);
                    b = new Ball(num, rad, x, y);
                }
                else if (state==4){
                    b.pb[0] = (double) Integer.parseInt(buf)/100;
                }
                else if (state==5){
                    b.pb[1] = (double) Integer.parseInt(buf)/100;
                }
                else if (state==6){
                    b.pb[2] = (double) Integer.parseInt(buf)/100;
                }
                else if (state==7){
                    b.vx = Integer.parseInt(buf);
                }
                else if (state==8){
                    b.vy = Integer.parseInt(buf);
                }
                else if (state==9){
                    int r = Integer.parseInt(buf);
                    if (r==1){
                        b.on = true;
                    }
                    else {
                        b.on = false;
                    }
                }
                else if (state==10){
                    int r = Integer.parseInt(buf);
                    if (r==1){
                        b.zkt = true;
                    }
                    else {
                        b.zkt = false;
                    }
                }
                else if (state==11){
                    int r = Integer.parseInt(buf);
                    if (r==1){
                        b.finish = true;
                    }
                    else {
                        b.finish = false;
                    }
                }
                else if (state==12){
                    int r = Integer.parseInt(buf);
                    if (r==1){
                        b.start_sc = true;
                    }
                    else {
                        b.start_sc = false;
                    }
                }
                state = state + 1;
                buf = "";
            }
            else {
                buf = buf + code.charAt(i);
            }

        }
        b.color = b.cls[b.num];
        b.pb2[0] = - b.pb[0];
        b.pb2[1] = - b.pb[1];
        b.pb2[2] = - b.pb[2];
        return b;
    }
    String game_code(Game game){
        String code = "";
        //player; play_way; game_state; sc_steck; first_num; fiels.sc_block_x*100; id.name; id.balls_type; id.ready_black; enemy_id..;
        code = code + game.player+";";
        code = code + game.play_way+";";
        code = code + game.game_state+";";
        code = code + game.sc_steck+";";
        code = code+ game.first_num+";";
        code = code+(int)(game.field.sc_block_x*100)+";";
        code = code+game.id.name+";";
        code = code+game.id.balls_type+";";
        if (game.id.ready_black){
            code = code  +"1;";
        }
        else {
            code = code + "0;";
        }
        code = code+game.enemy_id.name+";";
        code = code+game.enemy_id.balls_type+";";
        if (game.enemy_id.ready_black){
            code = code  +"1;";
        }
        else {
            code = code + "0;";
        }
        for (int i = 0; i< game.balls.length;i++){
            code = code+ball_code(game.balls[i]) + ";";
        }
        //Log.i("gamer", code);
        return code+"$";
    }
    void code_game(Game game, String code){
        //play_way; game_state; sc_steck; first_num; fiels.sc_block_x*100; id.name; id.balls_type; id.ready_black; enemy_id..;
        int state = -1;
        String buf = "";
        for (int i = 0; i<code.length(); i++){
            if (("" + code.charAt(i)).equals(";")){
                if (state==-1){
                    game.player = Integer.parseInt(buf);
                }
                if (state==0){
                    game.play_way = Integer.parseInt(buf);
                }
                if (state==1){
                    game.game_state= Integer.parseInt(buf);
                }
                if (state==2){
                    game.sc_steck= Integer.parseInt(buf);
                }
                if (state==3){
                    game.first_num= Integer.parseInt(buf);
                }
                if (state==4){
                    game.field.sc_block_x = (double) Integer.parseInt(buf)/100;
                }
                if (state==5){
                    game.id.name = buf;
                }
                if (state==6){
                    game.id.balls_type = Integer.parseInt(buf);
                }
                if (state==7){
                    if (Integer.parseInt(buf)==1) {
                        game.id.ready_black = true;
                    }
                    else {
                        game.id.ready_black = false;
                    }
                }
                if (state==8){
                    game.enemy_id.name = buf;
                }
                if (state==9){
                    game.enemy_id.balls_type = Integer.parseInt(buf);
                }
                if (state==10){
                    if (Integer.parseInt(buf)==1) {
                        game.enemy_id.ready_black = true;
                    }
                    else {
                        game.enemy_id.ready_black = false;
                    }
                }
                if (state>10){
                    Log.i("geopat", "tetra: "+ buf);
                    game.balls[state-11] = code_ball(buf);
                }
                state = state + 1;
                buf = "";
            }
            else {
                buf = buf+code.charAt(i);
            }

        }

    }

}
