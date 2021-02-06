package jcz.imageviewsprite.GameTool;

import android.os.Handler;
import android.os.Looper;

import jcz.imageviewsprite.GameTool.Util.MathUtil;

public class Motor {
    //Velocidad motor
    float speedInicial=0.1f;
    float aceleration=0;
    float maxSpeed=0;
    float speedXFinal=0;
    float speedYFinal=0;
    float angleDirection=0;

    boolean encendido=false;
    boolean pause=false;
    float time=-1;

    boolean stop=false;

    boolean frenar=false;
    float freno=1.5f;

    boolean apagar=false;
    float apagadoFreno=1.1f;
    boolean frenarAlApagar=false;

    Thread motor;
    private float timeOff=500;

    public Motor() {
        motorRunnate();
    }
    public Motor(float speedStart,float aceleration,float maxSpeed,float angleDirection){
        speedInicial=speedStart;
        this.aceleration=aceleration;
        this.maxSpeed=maxSpeed;
        this.angleDirection=angleDirection;
        motorRunnate();
    }
    //asigno velocidades
    public void setVelocity(float speedStart,float aceleration,float maxSpeed,float angleDirection){
        speedInicial=speedStart;
        this.aceleration=aceleration;
        this.maxSpeed=maxSpeed;
        this.angleDirection=angleDirection;
    }
    //asigno freno al apagar
    public void setBrakeWhemOff(float freno){//freno al terminar
        this.apagadoFreno=freno;//esta variable divide la velocidad luego de salir del bucle, cuando se esta apagando
    }
    //asigno freno de mano
    public void setBrake(float freno){
        this.freno=freno;//esta variable divide la velocidad cuando esta encendido
    }

    public void brake(boolean active){//activa freno, esto en vez de aumentar velocidad, comenzara a disminuir
        frenar=active;
    }
    public void setTime(float time){
        this.time=time;//asigno el tiempo de encendido del motor
        //-1 es tiempo infinito
    }
    public void setTimeWhenOff(float time){
        this.timeOff=time;
    }
    public void setActiveBrakeWhenOff(boolean active){
        frenarAlApagar=active;
    }//frenar al apagar
    public void off(){
        apagar=true;
    }//apagar motor
    public void stop(){
        encendido=false;
        pause=false;
        apagar=false;
        stop=true;
    }
    //Inicio Motor
    public void start(){
     //   if(motor.isAlive())return;
        if(encendido)return;
        else {

            System.out.println("Enciendendo otra vez...");
            try {
                motorRunnate();
                motor.start();
                encendido=true;
                stop=false;
            }catch (Throwable ex){ex.printStackTrace();}
        }
    }
    //reinicio motor
    public void restart(){
        stop();
        start();
    }
    //pongo en pausa motor
    public void pause(boolean pause){this.pause=pause;}

    //freno motor



    private void motorRunnate(){
        motor=new Thread(new Runnable() {
            @Override
            public void run() {



                    float tempSpeed = speedInicial;
                    float time_cont = 0;
                    encendido = true;
                    stop = false;
                    boolean st=false;
                    System.out.println("Hilo iniciado");
                    while (encendido && (time_cont < time || time == -1)) {//mientras time cont sea menor, o time-1
                        if (!frenar) {
                            if (tempSpeed <= 0) tempSpeed = speedInicial;
                            tempSpeed *= aceleration;//multiplico aceleracion
                        } else {
                            if (tempSpeed <= 0) tempSpeed = speedInicial;
                            tempSpeed /= freno;//si esta frenando divido aceleracion
                        }
                        if (tempSpeed > maxSpeed) tempSpeed = maxSpeed;//limito velocidad
                        else if (tempSpeed < 0) tempSpeed = 0;//limito velocidad minima 0

                        float x = tempSpeed;
                        float y = 0;//posiciono
                        MathUtil.Chords c = MathUtil.rotateMatriz2D(angleDirection, x, y);//obtengo posicion segun angulo 0 es derecha 90 gira abajo
                        speedXFinal = c.getX();//asigno posicion velocidad
                        speedYFinal = c.getY();//asigno posicion velocidad
                        //espera tiempo
                        while (pause) {
                            speedXFinal = 0;
                            speedYFinal = 0;
                        }
                        try {
                            Thread.sleep((long) (1000 / GameBase.FPS));
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                        time_cont += 1000 / GameBase.FPS;

                        if (apagar) break;//si se llamo a apagar, apago el motor , me salgo
                        if (stop) {
                            speedYFinal = 0;
                            speedXFinal = 0;
                            return;}
                    }
                    time_cont = 0;
                    if (apagar || frenarAlApagar && !st) {//apago el motor , dividiendo por el freno de apagado, Si se apago o si esta activado frenar al apagar
                        System.out.println("apagando");

                        apagandoListener();//aviso que se esta apagando
                        while (time_cont < timeOff && encendido && !stop) {
                            tempSpeed /= apagadoFreno;
                            if (frenar) {
                                tempSpeed /= freno;//si esta frenando divido aceleracion
                            }
                            float x = tempSpeed;
                            float y = 0;//posiciono
                            MathUtil.Chords c = MathUtil.rotateMatriz2D(angleDirection, x, y);//obtengo posicion segun angulo 0 es derecha 90 gira abajo
                            speedXFinal = c.getX();//asigno posicion velocidad
                            speedYFinal = c.getY();//asigno posicion velocidad
                            try {
                                Thread.sleep((long) (1000 / GameBase.FPS));
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }
                            while (pause) {
                                speedXFinal = 0;
                                speedYFinal = 0;
                            }

                            if (stop) {
                                speedYFinal = 0;
                                speedXFinal = 0;
                                return;}
                            time_cont += 1000 / GameBase.FPS;
                        }
                    }

                    speedYFinal = 0;
                    speedXFinal = 0;
                    stop();//pongo en falso todas las variables
                    stopListener();

            }
        });
    }

    OnStopListener stopListener;
    OnApagarListener turningOffListener;//listener cuando empezo a apagarse el motor con desaceleracion

    public void setTurningOffListener(OnApagarListener turningOffListener) {
        this.turningOffListener = turningOffListener;
    }
    public void setStopListener(OnStopListener stopListener) {
        this.stopListener = stopListener;
    }

    private void stopListener(){
        if (stopListener!=null){
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override public void run() {
                    stopListener.onStop();
                }
            });
        }
    }
    private void apagandoListener(){
        if(turningOffListener !=null){
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override public void run() {
                turningOffListener.onApagandose();
                }
            });
        }
    }
    public interface OnStopListener{
        void onStop();
    }
    public interface OnApagarListener{
        void onApagandose();
    }


}