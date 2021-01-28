package jcz.imageviewsprite.GameTool;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.widget.ImageView;

import java.util.ArrayList;

public class Lienzo {
    Bitmap bitmap_final;
    Bitmap bitmap;//lienzo principal
    Bitmap spriteOnly;//sprite solo o fondo
    Bitmap[]sprites;//sprites reales
    Bitmap bitmapSpriteTemp;//sprites intercambiandose
    Thread h_animation_sprites;
    Thread h_run;
    Activity activity;
    int x_pos=0;
    int y_pos=0;
    int xPosition=0;
    int yPosition=0;
    ArrayList<Lienzo>list=new ArrayList<>();
    Lienzo parent;
    boolean runDraw=false;

    boolean xFlip,yFlip;

    ImageView imgView;



    public Lienzo() {bitmap= Bitmap.createBitmap(300,300,Bitmap.Config.ARGB_8888);}
    public Lienzo(int witdh,int height) {bitmap= Bitmap.createBitmap(witdh,height,Bitmap.Config.ARGB_8888);}

    public void setActivity(Activity activity){this.activity=activity;}
    //digo si esta rotado o no
    public void setRotateFlip(boolean xFlip,boolean yFlip){this.xFlip=xFlip;this.yFlip=yFlip;}
    public boolean getXRotateFlip(){return xFlip;}
    public boolean getYRotateFlip(){return yFlip;}
    //asigno sprite solo para fondo
    public void setSpriteOnly(Bitmap bitmap){
        spriteOnly=bitmap;
        bitmap=spriteOnly;
    }
    //limpia buffer anterior
    public void cleanBuffer(){
        bitmap=Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(),Bitmap.Config.ARGB_8888);
    }
    //posicion en el padre
    public void setPositioninParent(float xPorcent,float yPorcent){
        if(parent!=null){
            yPosition= (int) (parent.getHeight()*(yPorcent*0.01f));
            xPosition= (int) (parent.getWidht()*(xPorcent*0.01f));
        }
    }
    public void setPositioninParent(int x,int y){
        xPosition=x;
        yPosition=y;
    }
    public int getXPositioninParent(){return xPosition;}
    public int getYPositioninParent(){return yPosition;}


    public int getWidht(){return bitmap.getWidth();}
    public int getHeight(){return bitmap.getHeight();}

    //cambia resolucion del lienzo
    public void setResolution(int x,int y){
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scaleWidth = ((float) x) / width;
        float scaleHeight = ((float) y) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(x,y);
        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bitmap, 0, 0, x,y, matrix, false);
        bitmap=resizedBitmap;
    }
    //Selecciono posicion donde dibujara
    public void setDrawPosition(int x,int y){
        y_pos=y;
        x_pos=x;
    }
    //en porcentaje
    public void setDrawPosition(float x_porcent,float y_porcent){
        x_pos= (int) (bitmap.getWidth()*(x_porcent*0.01));
        y_pos= (int) (bitmap.getWidth()*(y_porcent*0.01));
    }
    //Obtengo posicion del lienzo donde dibujara
    public int getXPositiondraw(){return x_pos;}
    public int getYPositiondraw(){return y_pos;}
    public float getXPositionporcent(){return (x_pos*100)/bitmap.getWidth();}
    public float getYPositionporcent(){return (y_pos*100)/bitmap.getHeight();}

    //Obtengo bitmap
    public Bitmap getBitmap(){return bitmap_final;}

    //Voy a dibujajr un Bitmap en el lienzo
    public void drawBitmap(Bitmap bitmap){
        if(bitmap==null){System.out.println("Bitmap Nulo");return;}


        Bitmap result = Bitmap.createBitmap(this.bitmap.getWidth(),this.bitmap.getHeight(), this.bitmap.getConfig());
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(this.bitmap, 0f, 0f, null);
        canvas.drawBitmap(bitmap,getXPositionporcent(),getYPositionporcent(), null);

        this.bitmap=result;
    }
    public void drawBitmap(Bitmap bitmap,int x,int y){
        if(bitmap==null){System.out.println("Bitmap Nulo");return;}

        Bitmap result = Bitmap.createBitmap(this.bitmap.getWidth(),this.bitmap.getHeight(), this.bitmap.getConfig());
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(this.bitmap, 0f, 0f, null);
        canvas.drawBitmap(bitmap,(x*100)/this.bitmap.getWidth(),(y*100)/this.bitmap.getHeight(), null);
        this.bitmap=result;
    }
    public void drawLienzo(Lienzo lienzo){
        drawBitmap(lienzo.getBitmap(),lienzo.getXPositioninParent(),lienzo.getYPositioninParent());
    }
    //asigno sprites
    public void setSprites(Bitmap...bitmaps){
        sprites=bitmaps;
        bitmap=sprites[0];
    }

    //Metodo que dibuja constantemente
    public void runDraw(){
        runDraw=true;
        stopAnimationRun();
        h_run=new Thread(new Runnable() {
            @Override
            public void run() {
                while(runDraw){
                    cleanBuffer();//limpia anterior dibujo
                    drawSpriteOnly();//dibujo fondo o sprite solo
                    drawSpriteTemp();//dibujo sprite si hay
                    drawList();//dibujo lienzo de los demas que agrege
                    drawImageView();//dibujo en el imageview si existe
                    drawReverseFlip();//rota segun sus variables en espejo u otro solo estetico
                    bitmap_final=bitmap;//El bitmap final
                    draw(bitmap_final);
                    try {Thread.sleep(1000/GameBase.FPS);} catch (Throwable e) {e.printStackTrace();}
                }
            }
        }); h_run.start();

    }
    void draw(Bitmap bitmap_final){};
    //Inicio animacion
    public void startAnimationSprites(){
        stopAnimationSprites();
        h_animation_sprites=new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    for(Bitmap b:sprites) {
                        bitmapSpriteTemp=b;
                        try {Thread.sleep(1000/GameBase.FPS);} catch (Throwable e) {e.printStackTrace();}
                    }
                }
            }
        });h_animation_sprites.start();
    }
    public void startAnimationReverse(){
        stopAnimationSprites();
        h_animation_sprites=new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    for(int i=sprites.length-1;i>=0;i--)
                        bitmapSpriteTemp=sprites[i];
                    try {Thread.sleep(1000/GameBase.FPS);} catch (Throwable e) {e.printStackTrace();}
                    }
                }
        });h_animation_sprites.start();
    }
    //inicio animacion solo
    public void startAnimationNoRepeat(){
        stopAnimationSprites();
        h_animation_sprites=new Thread(new Runnable() {
            @Override
            public void run() {
                    for(Bitmap b:sprites) {
                        bitmapSpriteTemp=b;
                        try {Thread.sleep(1000/GameBase.FPS);} catch (InterruptedException e) {e.printStackTrace();}
                    }
            }
        });h_animation_sprites.start();
    }
    public void startAnimationReverseNorepeat(){
        stopAnimationSprites();
        h_animation_sprites=new Thread(new Runnable() {
            @Override
            public void run() {
                    for(int i=sprites.length-1;i>=0;i--)
                        bitmapSpriteTemp=sprites[i];
                try {Thread.sleep(1000/GameBase.FPS);} catch (Throwable e) {e.printStackTrace();}
            }
        });h_animation_sprites.start();
    }




    //detengo animacion de sprites
    public void stopAnimationSprites(){
        try {
            h_animation_sprites.stop();
            h_animation_sprites.destroy();
        }catch (Throwable ex){ex.printStackTrace();}
    }
    public void stopAnimationRun(){
        try {
            h_run.stop();
            h_run.destroy();
        }catch (Throwable ex){ex.printStackTrace();}
    }



    //metodo que dibuja las demas capas
    private void drawList(){
        if(!list.isEmpty()){
            for(Lienzo l:list){
                drawLienzo(l);
            }
        }
    }
    //metodo que dibuja los sprites temporales
    private void drawSpriteTemp(){
        if(bitmapSpriteTemp!=null){
            drawBitmap(bitmapSpriteTemp);
        }
    }
    private void drawSpriteOnly(){
        if(spriteOnly!=null){
            drawBitmap(spriteOnly);
        }
    }
    //rota el bitmap reversa flip
    private void drawReverseFlip(){
        if(xFlip==false && yFlip==false)return;
        bitmap=BitmapUtil.createFlippedBitmap(bitmap,xFlip,yFlip);
    }

    //agrego hijo lienzo
    public void add(Lienzo lienzo){
        lienzo.parent=this;
        list.add(lienzo);
    }
    //asigna imageview
    public void setImgView(ImageView imgView,Activity activity){
        this.imgView=imgView;
        setActivity(activity);
    }

    private void drawImageView(){
        if(imgView!=null && activity!=null){
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    imgView.setImageBitmap(getBitmap());
                }
            });
        }
    }
}