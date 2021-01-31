package jcz.imageviewsprite.GameTool;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;

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

    Colision colision;
    OnPositionListener onPositionListener;
    OnPositionChangeListener onPositionChangeListener;
    @Override
    void draw(Bitmap bitmap_final) {
       // System.out.println(yPosition);
        setVelocity();//hago operaciones de velocidad, suma

        //borrar esto, es solo temporal
        if(yPosition>maxYPosition) {
            yPosition = maxYPosition;
            speed_vertical=0;
        }


        listeners();
        xPositionBefore=xPosition;//asigno posicion anterior
        yPositionBefore=yPosition;//asigno posicion anterior
    }
    private void listeners (){

        if(onPositionListener!=null) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override public void run() {
                onPositionListener.onPosition(xPosition, yPosition);                 }
            });
        }
            if(onPositionChangeListener!=null){
         if(xPosition!=xPositionBefore || yPosition!=yPositionBefore)//si la posicion anterior no es igual a la actual
             new Handler(Looper.getMainLooper()).post(new Runnable() {
                 @Override public void run() {
                     onPositionChangeListener.onPositionChange(xPosition,yPosition);
                 }
             });
        }
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

                    speed_vertical=0;//Velocidad vertical a 0
                    jump=true;//indico que esta saltando
                    int cont_Time=0;//
                    int c= (int) (acceleration/(time/GameBase.FPS));//obtengo distancia para calcular el tiempo

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
                        tempAceler-=c;  //la aceleracion baja mientras va saltando
                        if(tempAceler<0)tempAceler=0;
                    }
                    speed_vertical=0;//velocidad vertical a 0
                    //la gravedad general se pone a la gravedad inicial de esta forma cae segun la gravedad
                    setGravityStart();

                    // gravity_speed=g;
                    stopJump();
                }
            });h_jump.start();
        }
    }

    public void setGravityStart(){
        //Pone la velocidad de la gravedad al inicio
        //es decir caera con aceleracion
        tGravity=gravity_speed;
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
    public void selectGroupSpriteAnimation(int index){
        sprites=list_sprites.get(index).sprites;
    }
    public int getGroupSpriteSize(){return list_sprites.size();}


    //COLISIONES
    public Colision getColision() {return colision;}
    public void setColision(Colision colision) {this.colision = colision;}

    public boolean isColisionWith(GameObject gameObject1,GameObject gameObject2){
        //si la colision esta dentro del area de otro objeto
        if(gameObject1.colision==null || gameObject2.colision==null)return false;//retorna false si uno de los objetos tiene colision nula
        else{
            return Colision.isColision(Colision.getTemp(gameObject1.colision,gameObject1.xPosition,gameObject1.yPosition),
                    Colision.getTemp(gameObject2.colision,gameObject2.xPosition,gameObject2.yPosition));
        }
    }

    //listener setters and getters
    public OnPositionChangeListener getOnPositionChangeListener() {return onPositionChangeListener;}
    public void setOnPositionChangeListener(OnPositionChangeListener onPositionChangeListener) {this.onPositionChangeListener = onPositionChangeListener;}
    public OnPositionListener getOnPositionListener() {return onPositionListener;}
    public void setOnPositionListener(OnPositionListener onPositionListener) {this.onPositionListener = onPositionListener;}
    //clase de grupo de sprites
    class Sprite{
        Bitmap[]sprites;
        public Sprite(Bitmap ...bitmap){
            sprites=bitmap;
        }
    }

    public static class Colision{
        float left=0,right=0,top=0,bottom=0;
        float posicion=0;
        public Colision(GameObject g){
            right=g.getWidht()/2;
            bottom=g.getHeight()/2;
        }
        public Colision(float bottom, float top, float right, float left) {
            this.bottom = bottom;
            this.top = top;
            this.right = right;
            this.left = left;
        }
        public float getBottom() {
            return bottom;
        }
        public void setBottom(float bottom) {
            this.bottom = bottom;
        }
        public float getTop() {
            return top;
        }
        public void setTop(float top) {
            this.top = top;
        }
        public float getRight() {
            return right;
        }
        public void setRight(float right) {
            this.right = right;
        }
        public float getLeft() {
            return left;
        }
        public void setLeft(float left) {
            this.left = left;
        }
        public void showConsole(){
            System.out.println("left: "+left+" right: "+right+" top: "+top+" bottom: "+bottom);
        }

        public Colision(){
        }
        public static boolean isColision(Colision colision1,Colision colision2){
            colision1.showConsole();
            colision2.showConsole();
            //verifica si el objeto esta fuera deel rango derecha o isquierda y si esta retorna falso por que no esta colisionando
            if((colision1.left>colision2.right && colision1.right>colision2.right) ||
                    (colision1.left<colision2.left && colision1.right<colision2.left)
                    )return false;
            //verifica si el objeto esta fuera del rango arriba o abajo
            else if ((colision1.top>colision2.bottom && colision1.bottom>colision2.bottom) ||
                    (colision1.top<colision2.top && colision1.bottom<colision2.top))
            return false;//retorna falso si esta fuera del rango
            else return true;//si no , entonces esta colisionando
        }
        public static Colision getTemp(Colision colision,int x,int y){
            return new Colision(colision.bottom+y,colision.top+y,colision.right+x,colision.left+x);
           // return new Colision(colision.bottom,colision.top,colision.right,colision.left);
        }

    }
    public interface OnPositionListener{
        void onPosition(int x,int y);
    }
    public interface OnPositionChangeListener{
        void onPositionChange(int x,int y);
    }
}