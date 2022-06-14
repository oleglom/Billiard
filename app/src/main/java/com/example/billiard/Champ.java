package com.example.billiard;

import android.graphics.Color;
import android.util.Log;

import java.util.ArrayList;

public class Champ {
    boolean go = false;
    int place = 0;
    //список русских имен
    String[] Russians_names=  {"Игорь", "Олег", "Сергей", "Денис", "Прохор", "Дмитрий", "Виталий",
            "Павел", "Петр", "Некита", "Роман", "Иван", "Антон", "Борис", "Матвей", "Максим", "Тимур", "Валерий", "Виктор"};
    String[] Indian_names=  {"Рахул", "Амрит", "Базант", "Нила",  "Лалит",  "Махатма", "Ратна", "Готам",
            "Чандр", "Ситара", "Серендра", "Базу", "Рав", "Асим", "Шехар"};

    //раунд по счету
    int state = 0;
    //подсетка в которой герой
    String[] block;
    int num = 0;
    boolean in = true;
    int type = 0;//0-русский, 1-индийский

    //сетка турнира
    ArrayList array_champ = new ArrayList();
    //имя главного герой
    String name = "";
    public Champ(String name, int type){
        this.name = name;
        this.type = type;
        setArray();
    }
    void setArray(){
        //винера
        ArrayList wins = new ArrayList();
        String[][] first_wins = new String[4][3];
        for (int i = 0; i<4; i++){
            if (i!=0) {

                if (type < 2) {


                    first_wins[i] = new String[]{Russians_names[(int) (Math.random() * (Russians_names.length - 1))], Russians_names[(int) (Math.random() * (Russians_names.length - 1))], ""};
                }
                if (type >= 2) {


                    first_wins[i] = new String[]{Indian_names[(int) (Math.random() * (Indian_names.length - 1))],
                            Indian_names[(int) (Math.random() * (Indian_names.length - 1))], ""};
                }
            }
            if (i==0) {
                if (type>=2) {
                    first_wins[i] = new String[]{this.name, Indian_names[(int) (Math.random() * (Indian_names.length - 1))], ""};
                }
                if (type<2) {
                    first_wins[i] = new String[]{this.name,Russians_names[(int) (Math.random() * (Russians_names.length - 1))], ""};
                }
            }
        }
        String[][] second_wins = new String[2][3];
        String[][] last_wins = new String[1][3];
        wins.add(first_wins);
        wins.add(second_wins);
        wins.add(last_wins);
        //лузера
        ArrayList loses = new ArrayList();
        String[][] first_loses = new String[2][3];
        block = first_wins[0];
        String[][] second_loses = new String[2][3];
        String[][] third_loses = new String[1][3];
        String[][] last_loses = new String[1][3];
        loses.add(first_loses);
        loses.add(second_loses);
        loses.add(third_loses);
        loses.add(last_loses);
        //финал
        ArrayList fbb= new ArrayList();
        String[][] fb = new String[1][3];
        fbb.add(fb);
        //лузера и винера 0 - винера, 1 - лузера, 2 - финал
        this.array_champ.add(wins);
        this.array_champ.add(loses);
        this.array_champ.add(fbb);
    }
    void shag(boolean win){
        int restart = 0;
        if (win){
            block[2] = (num) + "";
        }
        else {
            block[2] = (1-num) + "";
        }
        //сетка винеров
        {
            if (state<3) {
                String[][] now_bar = (String[][]) ((ArrayList) array_champ.get(0)).get(state);
                String[][] next_bar = new String[1][1];
                String[][] next_l = new String[1][1];
                if (state < 2) {
                    next_bar = (String[][]) ((ArrayList) array_champ.get(0)).get(state + 1);
                    next_l = (String[][]) ((ArrayList) array_champ.get(1)).get(state);
                } else if (state == 2) {
                    next_bar = (String[][]) ((ArrayList) array_champ.get(2)).get(0);
                    next_l = (String[][]) ((ArrayList) array_champ.get(1)).get(state+1);

                }
                for (int u = 0; u < now_bar.length; u++) {
                    boolean flag = false;
                    if (block!=now_bar[u]) {
                        boolean c = Math.random() > 0.325;
                        String st = "1";
                        if (c) {
                            st = "0";
                        }
                        now_bar[u][2] = st;
                    } else {
                        flag = true;
                    }

                    next_bar[u / 2][u % 2] = now_bar[u][Integer.parseInt(now_bar[u][2])];
                    String[] pere = new String[1];
                    if (state==0) {
                        next_l[u / 2][u % 2] = now_bar[u][1 - Integer.parseInt(now_bar[u][2])];
                        pere = next_l[u / 2];
                    }
                    if (state>0) {
                        next_l[u][1] = now_bar[u][1 - Integer.parseInt(now_bar[u][2])];
                        pere = next_l[u];
                        if (state>=2 & flag & num==Integer.parseInt(now_bar[u][2])){
                            restart = 2;
                        }
                    }
                    if (num!=Integer.parseInt(now_bar[u][2]) & flag){
                        if (state==1 || state==2) {
                            num = 1;
                        }
                        if (state==2){
                            restart = 1;
                        }
                        block = pere;

                    }
                    else if (flag){
                        block = next_bar[u / 2];
                    }

                }
            }
        }
        //сетка лузеров
        {
            if (state>0 & state<5) {
                String[][] now_bar = (String[][]) ((ArrayList) array_champ.get(1)).get(state-1);
                String[][] next_bar = new String[1][1];
                if (state < 4) {
                    next_bar = (String[][]) ((ArrayList) array_champ.get(1)).get(state);
                    //now_bar[0][0] = "jjjjjj";
                } else if (state == 4) {
                    next_bar = (String[][]) ((ArrayList) array_champ.get(2)).get(0);

                }
                for (int u = 0; u < now_bar.length; u++) {
                    boolean flag = false;
                    if (block!=now_bar[u]) {
                        boolean c = Math.random() > 0.1;
                        String st = "0";
                        if (c) {
                            st = "1";
                        }
                        now_bar[u][2] = st;
                    } else {
                        flag = true;
                        Log.i("dedro", flag + ";" + u);



                    }
                    Log.i("dedro", "n:" + num);
                    if (state==1) {
                        next_bar[u][0] = now_bar[u][Integer.parseInt(now_bar[u][2])];
                        if (flag) {
                            block = next_bar[u];
                        }
                    }
                    if (state==2 || state==3){
                        next_bar[0][u%2] = now_bar[u][Integer.parseInt(now_bar[u][2])];
                        if (flag&state==2){
                            num = 0;
                        }
                        if (flag) {
                            block = next_bar[0];
                        }
                    }
                    if (state==4){
                        next_bar[0][1] = now_bar[u][Integer.parseInt(now_bar[u][2])];
                        if (flag & (num==Integer.parseInt(now_bar[u][2]))){
                            num = 0;
                        }
                        if (flag){
                            block = next_bar[0];
                        }
                    }
                    if (flag & (num!=Integer.parseInt(now_bar[u][2]))){
                        in = false;
                        //block = new  String[1];
                    }


                }
            }
        }
        state = state + 1;
        for (int i = 0; i<restart; i++){
            shag(true);
        }

    }
    int[] getCord(){
        ArrayList wins = (ArrayList) array_champ.get(0);
        ArrayList loses = (ArrayList) array_champ.get(1);
        ArrayList fb = (ArrayList) array_champ.get(2);

        for (int i = 0; i<wins.size();i++){
            for (int j = 0; j<((String[][])(wins.get(i))).length; j++){
                if (((String[][])(wins.get(i)))[j]==block){
                    return new int[]{0,i,j,num};
                }
            }
        }
        for (int i = 0; i<loses.size();i++){
            for (int j = 0; j<((String[][])(loses.get(i))).length; j++){
                if (((String[][])(loses.get(i)))[j]==block){
                    return new int[]{1,i,j,num};
                }
            }
        }
        for (int i = 0; i<fb.size();i++){
            for (int j = 0; j<((String[][])(fb.get(i))).length; j++){
                if (((String[][])(fb.get(i)))[j]==block){
                    return new int[]{2,i,j,num};
                }
            }
        }
        return new int[]{0,0,0,0};
    }
    void block_by_cords(int[] cords){
        ArrayList wins = (ArrayList) array_champ.get(0);
        ArrayList loses = (ArrayList) array_champ.get(1);
        ArrayList fb = (ArrayList) array_champ.get(2);
        block=((String[][])((ArrayList) array_champ.get(cords[0])).get(cords[1]))[cords[2]];

    }
}
