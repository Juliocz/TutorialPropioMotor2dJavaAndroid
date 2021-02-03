package jcz.imageviewsprite.GameTool;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class BitmapUtil {
    private static float fromPorcent(float value,float porcent){
        //retorna el valor segun el porcentaje
        return (float) (value*(porcent*0.01));
    }
    //obtiene array de bitmaps recortando de un bitmap
    public static Bitmap[]getSprites(Bitmap bitmap,int row,int columns){
        Bitmap sprites[]=new Bitmap[row*columns];
        int widhtSprite=bitmap.getWidth()/columns;
        int heightSprite=bitmap.getHeight()/row;

        int c=0;
        for(int y=0;y<row;y++){
            for(int x=0;x<columns;x++){
                sprites[c]=cropImage(bitmap,x*widhtSprite,y*heightSprite,widhtSprite,heightSprite);
                c++;
            }
        }
        return sprites;
    }

    //recorta imagen, requiere la posicion del punto de partida isquierda y arriba empieza, y de ahi toma la iamgen segun resolucion
    public static Bitmap cropImage(Bitmap bitmap,int x,int y,int width,int height){

            Bitmap result = Bitmap.createBitmap(bitmap
                    , x //X posicion donde recortara
                    , y //y posicion donde recortara
                    , width //x tamaño desde el punto de partida
                    , height); // y tamaño desde el puntdo de partida
           /* if (result != bitmap) {
                original.recycle();
            }*/
            return result;
    }

    //rota imagen spejo
    public static Bitmap createFlippedBitmap(Bitmap source, boolean xFlip, boolean yFlip) {
        Matrix matrix = new Matrix();
        matrix.postScale(xFlip ? -1 : 1, yFlip ? -1 : 1, source.getWidth() / 2f, source.getHeight() / 2f);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }
} 