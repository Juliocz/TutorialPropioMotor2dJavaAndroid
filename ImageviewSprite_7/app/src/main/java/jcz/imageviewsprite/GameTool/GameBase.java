package jcz.imageviewsprite.GameTool;

import android.graphics.Bitmap;
import android.widget.ImageView;

public class GameBase {
    ImageView img;
    public static float FPS=60;
    public GameBase(ImageView img) {
        this.img=img;
        //lienzo= Bitmap.createBitmap(img.getWidth(),img.getHeight(),Bitmap.Config.ARGB_8888);
    }
    public GameBase(){
       // lienzo= Bitmap.createBitmap(200,200,Bitmap.Config.ARGB_8888);
    }
    public void setSizeLienzo(int x,int y){
      //  lienzo= Bitmap.createBitmap(x,y,Bitmap.Config.ARGB_8888);
    }

}