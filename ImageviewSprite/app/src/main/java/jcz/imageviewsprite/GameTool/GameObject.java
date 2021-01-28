package jcz.imageviewsprite.GameTool;

import android.graphics.Bitmap;

public class GameObject extends Lienzo {
    float gravity_speed,gravity_acceleration,gravity_speed_max;
    boolean gravity;
    boolean jump;
    //int tempXPosition, tempYPosition;
    //hilos
    Thread h_gravity;
    Thread h_jump;

    public static final int UP=0;
    public static final int DOWN=1;
    public static final int RIGHT=2;
    public static final int LEFT=3;

    int maxYPosition=110; //borrar esto
    @Override
    void draw(Bitmap bitmap_final) {
    }

    public boolean isActiveGravity(){return gravity;}
    //Asigna gravedad
    public void setGravity(float gravity_speed,float gravity_acceleration,float gravity_speed_max){
        this.gravity_speed=gravity_speed;
        this.gravity_acceleration=gravity_acceleration;
        this.gravity_speed_max=gravity_speed_max;
    }
    //activa la gravedad
    public void activeGravity(boolean activeGravity){
        stopGravity();
        this.gravity=activeGravity;
        if(activeGravity){
            h_gravity=new Thread(new Runnable() {
                @Override
                public void run() {
                    float tempAceler=0;
                    while(gravity){

                        float tempSpeed=gravity_speed;
                        tempSpeed+=tempAceler;
                        if(tempSpeed>gravity_speed_max)tempSpeed=gravity_speed_max;
                        if(!(yPosition+tempSpeed>maxYPosition))
                        yPosition+= tempSpeed;
                        else yPosition=maxYPosition;
                        try {Thread.sleep(1000/GameBase.FPS);} catch (Throwable e) {e.printStackTrace();}
                        tempAceler+=gravity_acceleration;
                    }
                }
            });h_gravity.start();
        }else stopGravity();
    }
    //detiene gravedad
    public void stopGravity(){
        gravity=false;
        try{
            h_gravity.stop();h_gravity.destroy();
        }catch (Throwable ex){ex.printStackTrace();}
    }//detiene salto predeterminado
    private void stopJump(){
        jump=false;
            try{
                h_jump.stop();h_jump.destroy();
            }catch (Throwable ex){ex.printStackTrace();}

    }


    //acciones predeterminadas
    public void jump(int DIRECTION, final int time, final float speed, final float acceleration){
        stopJump();
        jump=true;
        switch (DIRECTION){
            case 0:h_jump=new Thread(new Runnable() {
                @Override
                public void run() {
                    int cont_Time=0;
                    float tempAceler=0;
                    while(cont_Time<time && jump) {
                        float tempSpeed=yPosition;
                        tempSpeed-=speed;
                        tempAceler+=acceleration;
                        tempSpeed-=tempAceler;
                        yPosition= (int) tempSpeed;
                        try {Thread.sleep(1000 / GameBase.FPS);} catch (Throwable e) {e.printStackTrace();}
                        cont_Time+= 1000 / GameBase.FPS;

                    }
                    stopJump();
                }
            });h_jump.start();
        }
    }
    public boolean isJump(){return jump;}
    public void stopJump(boolean activeJump){jump=activeJump;}

}