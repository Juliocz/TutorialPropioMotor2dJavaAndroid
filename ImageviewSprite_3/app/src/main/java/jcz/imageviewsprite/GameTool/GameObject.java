package jcz.imageviewsprite.GameTool;

import android.graphics.Bitmap;

import java.util.ArrayList;

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

    int maxYPosition= (int) (526/3.6f); //borrar esto

    //velocidad vertical horizontal
    float speed_vertical=0;
    float speed_horizontal=0;
    float max_vertical=8;
    float max_horizontal=5000;

    float tGravity;
    ArrayList<Sprite>list_sprites=new ArrayList<>();
    @Override
    void draw(Bitmap bitmap_final) {
       // System.out.println(yPosition);
        setVelocity();//hago operaciones de velocidad, suma

        if(yPosition>maxYPosition) {
            yPosition = maxYPosition;
            speed_vertical=0;
        }
        //System.out.println(speed_vertical);
    }
    //metodo privado para sumar velocidad
    private void setVelocity(){
        //verifico velocidad maxima
      //  if(speed_vertical>max_vertical)speed_vertical=max_vertical;
       // if(speed_horizontal>max_horizontal)speed_horizontal=max_horizontal;

        //sumo velocidades
        yPosition+=speed_vertical;
        xPosition+=speed_horizontal;

    }

    //velocidad  vertical ,horizontal maxima afectado por gravedad, salto pero no por caminata
    public void maxVelocity(float max_horizontal, float max_vertical){
        this.max_vertical=max_vertical;
        this.max_horizontal=max_horizontal;
    }
    public boolean isActiveGravity(){return gravity;}
    //Asigna gravedad
    public void setGravity(float gravity_speed,float gravity_acceleration,float gravity_speed_max){
        this.gravity_speed=gravity_speed;
        this.gravity_acceleration=gravity_acceleration;
        this.gravity_speed_max=gravity_speed_max;
        this.tGravity=gravity_speed;
    }
    //activa la gravedad
    public void activeGravity(boolean activeGravity){
        stopGravity();
        this.gravity=activeGravity;
        if(activeGravity){
            h_gravity=new Thread(new Runnable() {
                @Override
                public void run() {
                    while(gravity){


                       // float tempSpeed=tGravity;//velocidad normal

                        tGravity*=gravity_acceleration;//se le suma la aceleracion
                        if(tGravity>gravity_speed_max)tGravity=gravity_speed_max;//si pasa la velocidad maxima se asigna la velocidad maxima

                        //System.out.println("Velocidad gravedad: "+tGravity);
                        //if((speed_vertical+tempSpeed>maxYPosition))yPosition=maxYPosition;
                        //else
                            speed_vertical+= tGravity;//la posicion se suma la velocidad
                        try {Thread.sleep(1000/GameBase.FPS);} catch (Throwable e) {e.printStackTrace();}
                       // tempAceler+=gravity_acceleration;//la aceleracion va subiendo
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
        //Speed significa la velocidad constante, aceleration es la velocidad que se suma a la constante,pero se va restando mientras salto hazta llegar a 0 y solo queda la velocidad constante
        stopJump();

        switch (DIRECTION){
            case 0:h_jump=new Thread(new Runnable() {
                @Override
                public void run() {
                    //float g=gravity_speed;
                    //gravity_speed=0;

                    jump=true;
                    int cont_Time=0;
                    int c= (int) (acceleration/(time/GameBase.FPS));
                    float tempAceler=acceleration;
                    float tempSpeed=speed;
                    while(cont_Time<time && jump) {
                        //sumo la velocidad vertical abajo
                        tempSpeed/=acceleration;//freno aceleration
                       // System.out.println("Velocidad salto: "+tempSpeed);
                        speed_vertical-= (int) tempSpeed;
                        //speed_vertical/=acceleration;
                        try {Thread.sleep(1000 / GameBase.FPS);} catch (Throwable e) {e.printStackTrace();}
                        cont_Time+= 1000 / GameBase.FPS;
                        tempAceler-=c;
                        if(tempAceler<0)tempAceler=0;
                    }
                    speed_vertical=0;
                    tGravity=gravity_speed;


                    // gravity_speed=g;
                    stopJump();
                }
            });h_jump.start();
        }
    }
    public boolean isJump(){return jump;}
    public void stopJump(boolean activeJump){jump=activeJump;}


    @Override
    public void setSprites(Bitmap... bitmaps) {
        super.setSprites(bitmaps);
        if(!list_sprites.isEmpty())
        list_sprites.set(0,new Sprite(sprites));
        else list_sprites.add(new Sprite(sprites));
    }

    //agrego sprites
    public void addSprite(Bitmap...bitmaps){
        list_sprites.add(new Sprite(bitmaps));
    }
    //selecciono sprites a reproducir
    public void selectSpriteAnimation(int index){
        sprites=list_sprites.get(index).sprites;
    }


    class Sprite{
        Bitmap[]sprites;
        public Sprite(Bitmap ...bitmap){
            sprites=bitmap;
        }
    }
}