package com.example.billiard;

public class Luza {
    double x;
    double y;
    double rad;
    int id;
    double up;
    int[] keep = new  int[16];
    public Luza(double x, double y, double rad, int id){
        for (int i = 0; i<keep.length; i++){
            keep[i] = 0;
        }
        this.x = x;
        this.y = y;
        this.rad = rad;
        this.id = id;
        if (this.id == 1 | this.id == 4){
            this.up = 1.3*1.3;
        }
        else {
            this.up = 1.3*1.3;
        }
    }
}
