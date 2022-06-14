package com.example.billiard;

public class Id {
    String name = "Гость";
    int avatar_num = 0;
    int balls_type = 2; //0-сплошные 1 - полосатые
    boolean ready_black = false;
    int smile = 1;
    public Id (String name, int avatar){
        this.name = name;
        this.avatar_num = avatar;
    }
}
