package com.example.zettelmeier.hackingchallenge;

import android.graphics.Outline;
import android.graphics.Path;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class OutLine {
    float sX;
    float sY;
    float endX;
    float endY;

    public OutLine(float startX,float startY,float endX,float endY){
        sX = startX;
        sY = startY;
        this.endX = endX;
        this.endY = endY;
    }

    public static ArrayList<OutLine> addStartToBorder(float dot_x, float dot_y,ArrayList<OutLine> list) {
        for(int i = 0;i < list.size();i++){
            if(list.get(i).isOnLine(dot_x,dot_y)){
                OutLine part1 = list.get(i);
                OutLine part2 = new OutLine(dot_x,dot_y,part1.endX,part1.endY);
                list.set(i,part2);
                part1.endX = dot_x;
                part1.endY = dot_y;
                list.add(i,part1);
                return list;
            }
        }


        return list;
    }

    public boolean isOnLine(float x, float y){
        if ((Math.abs(sX-endX) < 10 )&& (Math.abs(x-endX) < 10)){
            if(y > sY+1){
                if(y < endY-10){
                    return true;
                }
            }else {
                if(y > endY+10){
                    return true;
                }
            }
        }

        if ((Math.abs(sY-endY) < 3 )&&( Math.abs(y-endY) <3)){
            if(x > sX+1){
                if(x < endX-1){
                    return true;
                }
            }else {
                if(x > endX+1){
                    return true;
                }
            }
        }
        return false;
    }

    public List<OutLine> split(float x1, float y1){
        ArrayList<OutLine> list = new ArrayList<>();
        list.add(new OutLine(sX,sY,x1,y1));
        list.add(new OutLine(x1,y1,endX,endY));
        return list;
    }

    public static Path pathGen(List<OutLine> list){
        Path path = new Path();
        path.moveTo(list.get(0).sX,list.get(0).sY);
        for (OutLine o: list) {
            path.lineTo(o.endX,o.endY);
        }
        return path;
    }

    public OutLine reverse(){
       float hx = sX;
       float hy = sY;
        sX = endX;
        sY = endY;
        endX = hx;
        endY = hy;
        return this;
    }

    @Override
    public String toString() {
        return "Start:["+this.sX+"|"+this.sY+"]; END:["+this.endX+"|"+this.endY+"]";
    }

    public OutLine copy(){
        return new OutLine(sX,sY,endX,endY);
    }
}
