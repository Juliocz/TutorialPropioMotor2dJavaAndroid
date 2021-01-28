package jcz.imageviewsprite;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import jcz.imageviewsprite.GameTool.BitmapUtil;
import jcz.imageviewsprite.GameTool.GameBase;
import jcz.imageviewsprite.GameTool.GameObject;
import jcz.imageviewsprite.GameTool.Lienzo;

public class MainActivity extends AppCompatActivity implements TextWatcher {
    ImageView img;
    Thread h;
    EditText frame;
    GameObject pj,fondo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        img= (ImageView) findViewById(R.id.imageView);
        frame=(EditText)findViewById(R.id.editTextFrame);
        frame.addTextChangedListener(this);
        System.out.println("hola mundo");

        prepararSprite();//preparo los sprites o lienzo
    }
    public void clickGravity(View v){
        pj.setGravity(10,0.5f,30);//asigno gravedad
        pj.activeGravity(!pj.isActiveGravity());//activo gravedad hilo
    }
    public void clickJump(View v){
        pj.jump(GameObject.UP,300,30,4);//activo salto del personaje
    }


    public void clickReverseUp(View v){
        pj.setRotateFlip(pj.getXRotateFlip(),!pj.getYRotateFlip());
    }
    public void clickReverseDer(View v){
        pj.setRotateFlip(!pj.getXRotateFlip(),pj.getYRotateFlip());//roto
    }
    public void clickDerecha(View v){ //muevo a diferente direcciones
        //fondo.setDrawPosition(fondo.getXPositiondraw()+3,fondo.getXPositiondraw());
        pj.setPositioninParent(pj.getXPositioninParent()+20,pj.getYPositioninParent());
        pj.setRotateFlip(false,false);
    }
    public void clickIsquierda(View v){
            //fondo.setDrawPosition(fondo.getXPositiondraw()-3,fondo.getXPositiondraw());
        pj.setPositioninParent(pj.getXPositioninParent()-20,pj.getYPositioninParent());
        pj.setRotateFlip(true,false);
    }
    public void clickArriba(View v){
        //fondo.setDrawPosition(fondo.getXPositiondraw()-3,fondo.getXPositiondraw());
        pj.setPositioninParent(pj.getXPositioninParent(),pj.getYPositioninParent()-20);
    }
    public void clickAbajo(View v){
        //fondo.setDrawPosition(fondo.getXPositiondraw()-3,fondo.getXPositiondraw());
        pj.setPositioninParent(pj.getXPositioninParent(),pj.getYPositioninParent()+20);
    }

    private void prepararSprite(){

        //Preparo los sprites
        pj= new GameObject();
        Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.sprite);
        pj.setSprites(BitmapUtil.getSprites(image,2,8));
        pj.startAnimationSprites();
        pj.runDraw();

        Bitmap image1 = BitmapFactory.decodeResource(getResources(), R.drawable.ff);
        fondo= new GameObject();
        fondo.setSprites(BitmapUtil.getSprites(image1,5,5));
        fondo.startAnimationSprites();
        fondo.runDraw();//corro el objeto o le doy vida
        fondo.add(pj);// agrego el objeto a este otro

    }
    public void clickIniciar(View v){
        fondo.setImgView(img,this);//simplemente asigno donde se renderizara

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        try {
            GameBase.FPS = Integer.parseInt(frame.getText().toString());
        }catch (Throwable ex){ex.printStackTrace();}
        }
}
