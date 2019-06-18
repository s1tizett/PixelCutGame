package com.example.zettelmeier.hackingchallenge;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.RegionIterator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * TODO: document your custom view class.
 */
public class GameView extends View {

    PathMeasure pm = new PathMeasure(null,false);
    private float maxHeight;
    private float maxWidth;
    boolean cross = false;
    //Region of GameArea
    Region region = new Region();
    //Region around Path for Method
    Region surround = null;
    //Paintbrushes
    Paint red_paintbrush_fill,blue_paintbrush_fill,green_paintbrush_fill;
    Paint red_paintbrush_stroke,blue_paintbrush_stroke,green_paintbrush_stroke;
    //Coordinate of Dot
    float dot_x;
    float dot_y;

    float dot_x_before;
    float dot_y_before;
    //Distance of Path
    float movehelper = 0.0f;
    //Spped of Dot
    float speed  = 2.f;
    //Path
    Path border = new Path();
    Path crossPath = null;
    Region crossRegion = new Region();
    ActionType action = ActionType.INIT;
    int playerDir[] = {0,0};
    //Evil Dots and their Directions
    Rect evil1 = null;
    Region enemy1 = null;
    int evil1Dir[] = {2,2};
    Rect evil2 = null;
    Region enemy2 = null;
    int evil2Dir[] = {2,-1};
    //helper for animate dot
    float afP [] = {0.0f,0.0f};
    //helper fpr computing new path
    ArrayList<OutLine> regionBord;
    ArrayList<OutLine> crossLines = new ArrayList<>();

    public GameView(Context context){
        super(context);
        setBackgroundResource(R.drawable.orangebackground);
        red_paintbrush_fill = new Paint();
        red_paintbrush_fill.setColor(Color.RED);
        red_paintbrush_fill.setStyle(Paint.Style.FILL_AND_STROKE);

        blue_paintbrush_fill = new Paint();
        blue_paintbrush_fill.setColor(Color.BLUE);
        blue_paintbrush_fill.setStyle(Paint.Style.STROKE);
        blue_paintbrush_fill.setStrokeWidth(10.f);

        green_paintbrush_fill = new Paint();
        green_paintbrush_fill.setColor(Color.GREEN);
        green_paintbrush_fill.setStyle(Paint.Style.FILL);

        red_paintbrush_stroke = new Paint();
        red_paintbrush_stroke.setColor(Color.RED);
        red_paintbrush_stroke.setStyle(Paint.Style.STROKE);
        red_paintbrush_stroke.setStrokeWidth(10);

        green_paintbrush_stroke = new Paint();
        green_paintbrush_stroke.setColor(Color.GRAY);
        green_paintbrush_stroke.setStyle(Paint.Style.FILL_AND_STROKE);
        green_paintbrush_stroke.setStrokeJoin(Paint.Join.ROUND);



    }

    private void initBoard() {
        border.moveTo((int)maxWidth*0.1f,(int)maxHeight*0.1f);
        border.lineTo((int)maxWidth*0.8f,(int)maxHeight*0.1f);
        regionBord.add(new OutLine(((int)maxWidth*0.1f),((int)maxHeight*0.1f),((int)maxWidth*0.8f),((int)maxHeight*0.1f)));
       // border.lineTo((int)maxWidth*0.8f,(int)maxHeight*0.3f);
      //  regionBord.add(new OutLine(((int)(maxWidth*0.8f)),((int)(maxHeight*0.1f)),((int)(maxWidth*0.8f)),(int)(maxHeight*0.3f)));
      //  border.lineTo((int)maxWidth*0.6f,(int)maxHeight*0.3f);
      //  regionBord.add(new OutLine(((int)(maxWidth*0.8f)),(int)(maxHeight*0.3f),(int)(maxWidth*0.6f),(int)(maxHeight*0.3f)));
      //  border.lineTo((int)maxWidth*0.6f,(int)maxHeight*0.5f);
      //  regionBord.add(new OutLine((int)(maxWidth*0.6f),(int)(maxHeight*0.3f),(int)(maxWidth*0.6f),(int)(maxHeight*0.5f)));
      //  border.lineTo((int)maxWidth*0.6f,(int)maxHeight*0.6f);
       // regionBord.add(new OutLine((int)(maxWidth*0.6f),(int)(maxHeight*0.5f),(int)(maxWidth*0.6f),(int)(maxHeight*0.6f)));
        //border.lineTo((int)maxWidth*0.8f,(int)maxHeight*0.6f);
      //  regionBord.add(new OutLine((int)(maxWidth*0.6f),(int)(maxHeight*0.6f),(int)(maxWidth*0.8f),(int)(maxHeight*0.6f)));
        border.lineTo((int)maxWidth*0.8f,(int)maxHeight*0.7f);
        regionBord.add(new OutLine((int)(maxWidth*0.8f),(int)(maxHeight*0.1f),(int)(maxWidth*0.8f),(int)(maxHeight*0.7f)));
        border.lineTo((int)maxWidth*0.1f,(int)maxHeight*0.7f);
        regionBord.add(new OutLine((int)(maxWidth*0.8f),(int)(maxHeight*0.7f),(int)(maxWidth*0.1f),(int)(maxHeight*0.7f)));
        border.lineTo((int)maxWidth*0.1f,(int)maxHeight*0.1f);
        regionBord.add(new OutLine((int)(maxWidth*0.1f),(int)(maxHeight*0.7f),(int)(maxWidth*0.1f),(int)(maxHeight*0.1f)));
        border.close();

        pm.setPath(border,false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(action == ActionType.INIT){
            setVariables(canvas);
        }
        //split part and set cross = true add also init 2nd path (make 2nd path is start end reversed and always add at [0])set cross false at end
        //
        /*
        Path border = new Path();       !!!!!!!!!!!!!
        Path crossPath = new Path();    !!!!!!!!!!!!!
        ActionType action
        int playerDir[] = {0,0};        !!!!!!!!!!!!!
        //helper for animate dot
        float afP [] = {0.0f,0.0f};
        //helper fpr computing new path
        ArrayList<OutLine> regionBord;   !!!!!!!!!!!!!
         */
        //IF DOT HAS TO TAKE CORNER
        if(action == ActionType.MOVE_RIGHT||action == ActionType.MOVE_LEFT){
            //TURN BY ACTIONCODE
            int turn[] = turnCircle(action);
            //CHECK IF ITS POSSIBLE TO DRAW CUT
            if(region.contains((int)(dot_x+turn[0]*10),(int)(dot_y+turn[1]*10))){
                int rBsize = crossLines.size();

                //FOR FIRSTCUT
                if(rBsize == 0) {
                    crossLines.add(new OutLine(dot_x, dot_y, (int) (dot_x + turn[0]), (int) (dot_y + turn[1])));
                    regionBord = OutLine.addStartToBorder(dot_x,dot_y,regionBord);
                    border = OutLine.pathGen(regionBord);
                    crossPath = OutLine.pathGen(crossLines);

                //SECOND CUT
                }else{
                    OutLine outline = crossLines.remove(rBsize-1);
                    outline.endX =dot_x;
                    outline.endY =dot_y;
                    crossLines.add(outline);
                    crossLines.add(new OutLine(dot_x, dot_y, (int) (dot_x + turn[0]), (int) (dot_y + turn[1])));
                    crossPath = OutLine.pathGen(crossLines);
                }

                action = ActionType.CUTTING;
                playerDir = turn;
            }
            if(action != ActionType.CUTTING){
                action = ActionType.NORMAL;
                cross =false;
            }
        }


        //move path forward and check if crossed draw line of path and set actiontype to merge
        if(action == ActionType.CUTTING){
            dot_x = dot_x+playerDir[0];
            dot_y = dot_y+playerDir[1];
            dot_x = dot_x+playerDir[0];
            dot_y = dot_y+playerDir[1];
           if( region.contains((int)(dot_x+playerDir[0]),(int)(dot_y+playerDir[1]))){
               crossPath.lineTo(dot_x+playerDir[0],dot_y+playerDir[1]);
               crossRegion.setPath(crossPath,surround);
               dot_x = dot_x+playerDir[0];
               dot_y = dot_y+playerDir[1];
               OutLine outLine =crossLines.remove(crossLines.size()-1);
               outLine.endX = dot_x;
               outLine.endY = dot_y;
               crossLines.add(outLine);

           }else{
               boolean added = false;
               action = ActionType.MERGE;
               for(int i = 0;i<Math.abs(playerDir[0]);i++){
                   for(OutLine o: regionBord ){
                       if(o.isOnLine(dot_x+i,dot_y)){
                           added = true;
                           OutLine.addStartToBorder(dot_x+i,dot_y,this.regionBord);
                           OutLine outLine =crossLines.remove(crossLines.size()-1);
                           outLine.endX = dot_x+i;
                           dot_x = dot_x+i;
                           crossLines.add(outLine);
                           break;
                       }
                   }
                   if (added){
                       added =false;
                       break;
                   }
               }
               for(int i = 0;i<Math.abs(playerDir[1]);i++){
                   for(OutLine o: regionBord ){
                       if(o.isOnLine(dot_x,dot_y+i)){
                           added = true;
                           OutLine.addStartToBorder(dot_x,dot_y+i,this.regionBord);
                           OutLine outLine =crossLines.remove(crossLines.size()-1);
                           outLine.endY = dot_y+i;
                           dot_y = dot_y+i;
                           crossLines.add(outLine);

                           break;
                       }
                   }
                   if (added){
                       break;
                   }
               }

           //    pm.setPath(null,false);
           }
           crossPath = OutLine.pathGen(crossLines);
           cross = false;
        }
        //check if crossed wirh border and compare areas rules with evils ask if evils are erased check win
        if(action == ActionType.MERGE){
            action = ActionType.NORMAL;
            ArrayList<OutLine> solutionOne = new ArrayList<>();
            ArrayList<OutLine> solutionTwo = new ArrayList<>();
            OutLine start =crossLines.get(0);
            for(int i = 0;i < regionBord.size();i++) {
                if (Math.abs(regionBord.get(i).sX - dot_x) < 20 && Math.abs(regionBord.get(i).sY - dot_y) < 20) {
                    for (int j = 0; j < regionBord.size(); j++) {
                        solutionTwo.add(regionBord.get((i + j) % regionBord.size()));

                        if (Math.abs(regionBord.get((i + j) % regionBord.size()).endX - start.sX) < 20 && Math.abs(regionBord.get((i + j) % regionBord.size()).endY - start.sY) < 20) {
                            break;
                        }
                    }
                    for(OutLine o: crossLines){
                        solutionTwo.add(o.copy());
                    }
                    break;
                }
            }

            for(int i = 0;i < regionBord.size();i++){
                if(Math.abs(regionBord.get(i).sX - start.sX) < 20 &&  Math.abs(regionBord.get(i).sY - start.sY )< 20) {
                    for(int j = 0;j<regionBord.size();j++){
                        solutionOne.add(regionBord.get((i+j)%regionBord.size()).reverse());
                        Log.i("SOLONE-FORLOOP",regionBord.get((i+j)%regionBord.size()).reverse().toString()+ " i|j = " +i+"|"+ j);

                       // Log.i("SOLONE-FORLOOP", regionBord.get((i + j) % regionBord.size()).reverse().toString() + " i|j = " + i + "|" + j+" END["+regionBord.get((i + j) % regionBord.size()).reverse().endX+"|"+regionBord.get((i + j) % regionBord.size()).reverse().endY+"]"+" StartFin["+start.endX+"|"+start.endY+"] IF="+(Math.abs(regionBord.get((i+j)%regionBord.size()).endX - dot_x) < 20 && Math.abs(regionBord.get((i+j)%regionBord.size()).endY - dot_y) < 20)
//
                  //      );
                        if (Math.abs(regionBord.get((i+j)%regionBord.size()).endX - dot_x) < 20 && Math.abs(regionBord.get((i+j)%regionBord.size()).endY - dot_y) < 20) {
                            break;
                        }
                    }
                    break;
                }
            }

            for(int i = 0;i < crossLines.size();i++){
                solutionOne.add(0,crossLines.get(i).reverse());
            }
            for(OutLine o: regionBord) {
                Log.i("TESTBOARD2",o.toString());
            }
            for(int i = 0;i< solutionOne.size();i++){
                solutionOne.get(i).endX = solutionOne.get((i+1)%solutionOne.size()).sX;
                solutionOne.get(i).endY = solutionOne.get((i+1)%solutionOne.size()).sY;
            }
            for(int i = 0;i< solutionTwo.size();i++){
                solutionTwo.get(i).endX = solutionTwo.get((i+1)%solutionTwo.size()).sX;
                solutionTwo.get(i).endY = solutionTwo.get((i+1)%solutionTwo.size()).sY;
            }
            Log.i("SOLTWO-15:27","DONE");
            Path sol1 = OutLine.pathGen(solutionOne);
            sol1.close();
            Path sol2 = OutLine.pathGen(solutionTwo);
            sol2.close();
            Region solu1 = new Region();
            solu1.setPath(sol1,surround);
            Region solu2 = new Region();
            solu2.setPath(sol2,surround);

            region.setPath(border,surround);
            for(OutLine o: solutionOne){
                Log.i("AREA1--PATH",o.toString());
            }for(OutLine o: solutionTwo){
                Log.i("Area2--PATH",o.toString());
            }

            if(calculateArea(solu1)<calculateArea(solu2)) {
                region.setPath(sol1, surround);
                border = sol1;
                regionBord = null;
                this.regionBord = solutionOne;
                pm.setPath(border, false);
                movehelper = 0.f;
                for (OutLine o : solutionOne) {
                    Log.i("SOLONEFINAL", o.toString());
                }
                crossLines = new ArrayList<>();
                crossPath = null;

                crossRegion = new Region();
            }else{
                region = new Region();
                region.setPath(sol2,surround);
                border = sol2;
                regionBord = null;
                this.regionBord = solutionTwo;
                pm.setPath(border,false);
                movehelper = 0.f;
                for(OutLine o: solutionTwo){
                    Log.i("SOLTWOFINAL", o.toString());
                }
                crossLines  = new ArrayList<>();
                crossPath = null;
                crossRegion = new Region();
           }
            action = ActionType.NORMAL;
            afP[0] = 0.f;
            afP[1] = 0.f;
        }


        canvas.drawPath(border,green_paintbrush_stroke);
        if(crossPath != null){
            canvas.drawPath(crossPath,blue_paintbrush_fill);
        }
        //Dot moves around the path
        if(action == ActionType.NORMAL) {
            if (movehelper < 0.0) {
                movehelper = movehelper + pm.getLength();
            }

            if (movehelper < pm.getLength()) {

                movehelper = movehelper + speed;
                pm.getPosTan(movehelper, afP, null);
                updatePlayerDirection(afP);
                dot_x_before = dot_x;
                dot_y_before = dot_y;
                dot_x = afP[0];
                dot_y = afP[1];
                canvas.drawCircle(afP[0], afP[1], 20, green_paintbrush_fill);
              //  canvas.translate(afP[0], afP[1]);
                afP[0] = 0.f;
                afP[1] = 0.f;
                invalidate();
            } else {
                movehelper = 0.0f;
            }
        }
        /*
            ANIMATION OF EVIL DOTS
         */

        //Proof Collision
        region.setPath(border,surround);
        if(!region.contains(evil1.centerX()+evil1Dir[0],evil1.centerY())){
            evil1Dir[0] = evil1Dir[0]*-1;
        }

        if(!region.contains(evil1.centerX(),evil1.centerY()+evil1Dir[1])){
            evil1Dir[1] = evil1Dir[1]*-1;
        }
        enemy1.translate(evil1Dir[0],evil1Dir[1]);
        if(!region.contains(evil2.centerX()+evil2Dir[0],evil2.centerY())){
            evil2Dir[0] = evil2Dir[0]*-1;
        }

        if(!region.contains(evil2.centerX(),evil2.centerY()+evil2Dir[1])){
            evil2Dir[1] = evil2Dir[1]*-1;
        }
        enemy2.translate(evil2Dir[0],evil2Dir[1]);
        canvas.drawRect(enemy1.getBounds(),red_paintbrush_fill);
        canvas.drawRect(enemy2.getBounds(),red_paintbrush_fill);
        evil1 = enemy1.getBounds();
        evil2 = enemy2.getBounds();
        /*
            ANIMATION OF EVIL DOTS END
         */


        if (crossRegion.op(enemy1,Region.Op.INTERSECT)){
            Toast.makeText(getContext(),"COLLISION",Toast.LENGTH_LONG).show();
        }
        if(action != ActionType.ENDING_FAIL||action != ActionType.ENDING_WIN)
         invalidate();
    }

    private void updatePlayerDirection(float[] afP) {
        if (dot_x_before < dot_x-1.f){
            playerDir[0] = 2;
            playerDir[1] = 0;
        }else if(dot_x_before> dot_x+1.f){
            playerDir[0] = -2;
            playerDir[1] = 0;
        }else{
            playerDir[0] = 0;
            if (dot_y_before < dot_y-0.1f){
                playerDir[1] = 2;
            }else if(dot_y_before> dot_y+1.f){
                playerDir[1] = -2;
            }

        }
    }

    private int[] turnCircle(ActionType type) {
        int a = playerDir[0];
        int b = playerDir[1];
        int direc [] = new int [2];
        switch (a){
            case -2:
                if (type == ActionType.MOVE_LEFT){
                    return new int[] {0, 2};
                }else {
                    return new int[] {0, -2};
                }
            case 0:
                if(b == 2){
                    if(type == ActionType.MOVE_LEFT){
                        return new int[]{2,0};
                    }
                        return new int[]{-2,0};
                }
                    if(type != ActionType.MOVE_LEFT){
                        return new int[]{2,0};
                    }
                    return new int[]{-2,0};

            case 2:
                if (type == ActionType.MOVE_LEFT){
                    return new int[] {0, -2};
                }else {
                    return new int[] {0, 2};
                }

                default:
                    return playerDir;

        }

    }

    private void updatePlayerDirection() {
        if((int) (afP[0] - dot_x)>1) {
            playerDir[0] = 2;
        }else if((int)(afP[0] - dot_x)<(-1)){
            playerDir[0] = -2;
        }else {
            playerDir[0] = 0;
        }
        if((int) (afP[1] - dot_y)>1) {
            playerDir[1] = 2;
        }else if((int)(afP[1] - dot_y)<(-1)){
            playerDir[1] = -2;
        }else {
            playerDir[1] = 0;
        }
    }

    private void setVariables(Canvas canvas) {
        maxHeight = canvas.getHeight();
        maxWidth = canvas.getWidth();
        //Init setting of Variables and getting canvas wifth and height

        surround = new Region(0,0,(int)maxWidth,(int)maxHeight);
        green_paintbrush_stroke.setStrokeWidth(maxWidth/40);
        green_paintbrush_stroke.setStrokeWidth(maxWidth/40);
        evil1 = new Rect(((int)(maxWidth*0.1f)+10),((int)(maxHeight*0.1f)+10),((int)(maxWidth*0.1f+30)),((int)(maxHeight*0.1f+30)));
        enemy1 = new Region(evil1);
        evil2 = new Rect(((int)(maxWidth*0.1f)+10),((int)(maxHeight*0.1f)+10),((int)(maxWidth*0.1f+30)),((int)(maxHeight*0.1f+30)));
        enemy2 = new Region(evil2);
        regionBord = new ArrayList<>();
        action = ActionType.NORMAL;
        initBoard();
    }

    public void switchDirec(){
        speed = speed* (-1.f);
    }

    public  boolean onTouchEvent (MotionEvent event) {
        Log.i("AREAAA",calculateArea(region)+"");
        Log.i("AREAADIREC",playerDir[0]+"   "+playerDir[1]);

        for(OutLine o: this.regionBord){
            Log.i("AREAAAREGION",o.toString());
        }

        float X =event.getX();
        float Y =event.getY();

        int i = 0;
        if((Y/maxHeight)>(700.f/925.f)){

            if(X/maxWidth>0.5f && !cross){
               // Toast.makeText(getContext(),dot_x + "-Right-"+ dot_y,Toast.LENGTH_LONG).show();

                action = ActionType.MOVE_RIGHT;
                cross = true;
            }else {
               // Toast.makeText(getContext(),"Left",Toast.LENGTH_LONG).show();
                action = ActionType.MOVE_LEFT;
                cross = true;
            }
        }else{
           //switchDirec();
          //  Toast.makeText(getContext(),region.contains((int)X,(int)Y)+"",Toast.LENGTH_LONG).show();
          //  Toast.makeText(getContext(),region.toString()+"",Toast.LENGTH_LONG).show();
            if(action == ActionType.CUTTING||action == ActionType.NORMAL);{
                action = ActionType.MOVE_RIGHT;
                cross = true;
            }
        }



        return false;
    }

    private float calculateArea(Region region) {

        RegionIterator regionIterator = new RegionIterator(region);

        int size = 0; // amount of Rects
        float area = 0; // units of area

        Rect tmpRect= new Rect();

        while (regionIterator.next(tmpRect)) {
            size++;
            area += tmpRect.width() * tmpRect.height();
        }
        return area;
    }
}

enum ActionType{
    INIT,MERGE,MOVE_RIGHT,MOVE_LEFT,NORMAL,ENDING_WIN,ENDING_FAIL,CUTTING
}

