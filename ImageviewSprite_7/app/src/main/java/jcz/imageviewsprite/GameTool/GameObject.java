package jcz.imageviewsprite.GameTool;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;

import jcz.imageviewsprite.GameTool.Util.MathUtil;

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


    float speedGravity=0;
    float speed_jump=0;
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

    GameObject parent;
    GameObject camera;
    ConfigParent config_parent=new ConfigParent();

    ArrayList<Motor>list_motor=new ArrayList<>();
    @Override
    void draw(Bitmap bitmap_final) {
       // System.out.println(yPosition);

        aplicPositionParent();//aplico posicion segun el padre
        aplicPositionCamera();//muevo la camara hijo
        aplicMotors();//aplico velocidades del motor
        setVelocity();//sumo las velocidades a la posicion, y reinicio la velocidad
        //borrar esto, es solo temporal
        if(getYPosition()>maxYPosition) {
            yPosition = maxYPosition;
            //speed_vertical=0;
    }



        listeners();
        xPositionBefore= getXPosition();//asigno posicion anterior
        yPositionBefore= getYPosition();//asigno posicion anterior
    }
    private void listeners (){

        if(onPositionListener!=null) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override public void run() {
                onPositionListener.onPosition((int)xPosition, (int)yPosition);                 }
            });
        }
            if(onPositionChangeListener!=null){
         if(xPosition!=xPositionBefore || yPosition!=yPositionBefore)//si la posicion anterior no es igual a la actual
             new Handler(Looper.getMainLooper()).post(new Runnable() {
                 @Override public void run() {
                     onPositionChangeListener.onPositionChange((int)xPosition,(int)yPosition);
                 }
             });
        }
    }
    //metodo privado para sumar velocidad
    private void setVelocity(){
        //verifico velocidad maxima
      //  if(speed_vertical>max_vertical)speed_vertical=max_vertical;
       // if(speed_horizontal>max_horizontal)speed_horizontal=max_horizontal;

        //if(this.speed_vertical==0)setGravityStart();//reinicio la velocidad de gravedad
        //speed_vertical=
        //Si la velocidad vertical es igual a 0, es decir ya no esta recibiendo velocidades  de motores


       this.speed_vertical+=speedGravity+speed_jump;
        //sumo velocidades

        yPosition+=this.speed_vertical;
        xPosition+=this.speed_horizontal;

        this.speed_vertical=0;
        this.speed_horizontal=0;
    }
    //metodo privado parents le suma posicion deel padre
    private void aplicPositionParent(){
        if(parent==null)return;
        else{
            //aplica posicion deel padre segun configuracion dada, por defecto todo es true
            if(config_parent.parentPosition){
            xPositionParent=parent.getXPosition();
            yPositionParent=parent.getYPosition();}
            if(config_parent.rotateParent)RotationParent=parent.getRotation();
        }
    }

    //metodo que movera la camara hijo
    private void aplicPositionCamera(){
        if(camera==null)return;
        else{//mueve la camara segun este objeto
            camera.cameraXPositionParent= (int) getXPosition();
            camera.cameraYPositionParent= (int) getYPosition();
            //System.out.println(xPosition);
            //System.out.println("camera position: "+camera.getCameraXPosition());
        }
    }

    //aplico posicion motor
    private void aplicMotors(){
        if(!list_motor.isEmpty()){
            for (Motor m:list_motor){
                if(m!=null){
                    speed_horizontal+=m.speedXFinal;
                    speed_vertical+=m.speedYFinal;


                }
            }
        }
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
    public void resetSpeedGravity(){//Pone la velocidad de la gravedad a como empezo a acelerar
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


                        if(tGravity<=0)resetSpeedGravity();
                       // float tempSpeed=tGravity;//velocidad normal

                        tGravity*=gravity_acceleration;//se le suma la aceleracion
                        if(tGravity>gravity_speed_max)tGravity=gravity_speed_max;//si pasa la velocidad maxima se asigna la velocidad maxima

                        //System.out.println("Velocidad gravedad: "+tGravity);
                        //if((speed_vertical+tempSpeed>maxYPosition))yPosition=maxYPosition;
                        //else
                            speedGravity= tGravity;//la posicion se suma la velocidad
                        try {Thread.sleep((long) (1000/GameBase.FPS));} catch (Throwable e) {e.printStackTrace();}
                       // tempAceler+=gravity_acceleration;//la aceleracion va subiendo
                    }
                    speedGravity=0;
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


                    jump=true;//indico que esta saltando
                    int cont_Time=0;//
                    int c= (int) (acceleration/(time/GameBase.FPS));//obtengo distancia para calcular el tiempo

                    float tempAceler=acceleration;
                    float tempSpeed=speed;
                    while(cont_Time<time && jump) {
                        //sumo la velocidad vertical abajo
                        tempSpeed/=acceleration;//freno aceleration
                       // System.out.println("Velocidad salto: "+tempSpeed);
                        speed_jump= -(int) tempSpeed;
                        //speed_vertical/=acceleration;
                        try {Thread.sleep((long) (1000 / GameBase.FPS));} catch (Throwable e) {e.printStackTrace();}
                        cont_Time+= 1000 / GameBase.FPS;
                        tempAceler-=c;  //la aceleracion baja mientras va saltando
                        if(tempAceler<0)tempAceler=0;
                    }
                    speed_jump=0;//velocidad vertical a 0
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
    public Sprite getSprites(){
     if(sprites!=null)
        return new Sprite(sprites);
        else return null;
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
    public Sprite getGroupSprite(int index){return list_sprites.get(index);}
    public void setListGroupSprite(ArrayList<Sprite>listGroupSprite){this.list_sprites=listGroupSprite;}
    public ArrayList<Sprite> getListGroupSprite(){return this.list_sprites;}


    //COLISIONES
    public Colision getColision() {return colision;}
    public void setColision(Colision colision) {this.colision = colision;}

    public boolean isColisionWith(GameObject gameObject1,GameObject gameObject2){
        //si la colision esta dentro del area de otro objeto
        //toma en cuenta la posicion y la posicion del padre afectando, pero no la posicion relativa
        if(gameObject1.colision==null || gameObject2.colision==null)return false;//retorna false si uno de los objetos tiene colision nula
        else{
            return Colision.isColision(Colision.getTemp(gameObject1.colision,gameObject1.getXPosition(),gameObject1.getYPosition()),
                    Colision.getTemp(gameObject2.colision,gameObject2.getXPosition(),gameObject2.getYPosition()));
        }
    }
    public boolean isColisionWithAbsolutePosition(GameObject gameObject1,GameObject gameObject2){
        //si la colision esta dentro del area de otro objeto
        //toma en cuenta la posicion y la posicion del padre afectando, pero si la posicion relativa, esta posicion es mas usada estetico pero por si se desea usar
        if(gameObject1.colision==null || gameObject2.colision==null)return false;//retorna false si uno de los objetos tiene colision nula
        else{
            return Colision.isColision(Colision.getTemp(gameObject1.colision,gameObject1.getXPositionAbsolute(),gameObject1.getYPositionAbsolute()),
                    Colision.getTemp(gameObject2.colision,gameObject2.getXPositionAbsolute(),gameObject2.getYPositionAbsolute()));
        }
    }


    //rotate


    @Override
    public float getXPosition() {
        if(parent!=null && config_parent.rotateParentPosition) {//si hay padre y si configparent tiene rotacion parent activada
            MathUtil.Chords tempPostParent = MathUtil.rotateMatriz2D(parent.getXPosition(), parent.getYPosition(), parent.getRotation(), super.getXPosition(), super.getYPosition());
            return tempPostParent.getX();
        }return super.getXPosition();
    }
    @Override
    public float getYPosition() {
        if(parent!=null && config_parent.rotateParentPosition) {
            MathUtil.Chords tempPostParent = MathUtil.rotateMatriz2D(parent.getXPosition(), parent.getYPosition(), parent.getRotation(), super.getXPosition(), super.getYPosition());
            return tempPostParent.getY();
        }else
            return super.getYPosition();
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


    //parent
    public void setParent(GameObject gameObject_parent){
        //metodo parent, este metodo agrega un objeto padre el cual afectara la posicion de este objeto
        this.parent=gameObject_parent;
    }
    //child camera
    public void setChildCamera(GameObject gameObject_camera){
        //metodo el cual agarra un objeto y movera la camara segun la posicion
        this.camera=gameObject_camera;
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
           // colision1.showConsole();
            //colision2.showConsole();
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
        public static Colision getTemp(Colision colision,float x,float y){
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

    public void setConfigParent(ConfigParent config){
        if(config==null){this.config_parent=new ConfigParent(false,false,false);}//si es nulo, el parent no afecta nada
        else this.config_parent=config;
    }

    public static class ConfigParent{
        boolean rotateParent=true;
        boolean parentPosition=true;
        boolean rotateParentPosition=true;
        public ConfigParent(boolean parentPosition,boolean rotateParentPosition,boolean rotateParent){
            this.rotateParent=rotateParent;//Rotacion afecta la rotacion del hijo
            this.parentPosition=parentPosition;//Afecta la posicion del padre al hijo
            this.rotateParentPosition=rotateParentPosition;//la rotacion del padre afecta la posicion deel hijo
        }
        public ConfigParent(){

        }
    }

    //motores
    public void addMotors(Motor motor){
        list_motor.add(motor);
    }
    public void deleteAllMotor(){
        list_motor.clear();
    }
    //tener activo solo 1 motor
    public Motor getMotors(int index){
        return list_motor.get(index);
    }
    public ArrayList<Motor>getAllMotor(){return list_motor;}

    public void pauseAllMotor(){
        for(Motor m:list_motor)m.pause(true);
    }
    public void stopAllMotor(){
        for(Motor m:list_motor)m.stop();
    }
}