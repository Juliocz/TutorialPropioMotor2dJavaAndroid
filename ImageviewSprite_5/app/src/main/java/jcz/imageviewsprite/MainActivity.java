package jcz.imageviewsprite;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import jcz.imageviewsprite.GameTool.BitmapUtil;
import jcz.imageviewsprite.GameTool.GameBase;
import jcz.imageviewsprite.GameTool.GameObject;

public class MainActivity extends AppCompatActivity implements TextWatcher {
    ImageView img;
    Thread h;
    EditText frame,cameraPosX,cameraPosY;
    Button b;
    GameObject pj,fuego,fondo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        img= (ImageView) findViewById(R.id.imageView);
        frame=(EditText)findViewById(R.id.editTextFrame);
        cameraPosX=(EditText)findViewById(R.id.editTextCameraX);
        cameraPosY=(EditText)findViewById(R.id.editTextCameraY);
        b=(Button)findViewById(R.id.button);
        frame.addTextChangedListener(this);
        cameraPosX.addTextChangedListener(this);
        cameraPosY.addTextChangedListener(this);



        //System.out.println("hola mundo");

        prepararSprite();//preparo los sprites o lienzo
        touchIsq();
        touchDer();

    }
    private void touchDer(){
        findViewById(R.id.b_der).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                pj.selectGroupSpriteAnimation(1);
                pj.setPositioninParent(pj.getXPositioninParent()+5,pj.getYPositioninParent());
                pj.setRotateFlip(false,false);
                return true;
            }
        });
    }
    private void touchIsq(){
        findViewById(R.id.b_isq).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //System.out.println("move isq");
                pj.selectGroupSpriteAnimation(1);
                pj.setPositioninParent(pj.getXPositioninParent()-5,pj.getYPositioninParent());
                pj.setRotateFlip(true,false);
                return true;
            }
        });
        findViewById(R.id.b_up).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //System.out.println("move isq");
                pj.setPositioninParent(pj.getXPositioninParent(),pj.getYPositioninParent()-3);
                pj.setRotateFlip(false,false);
                return true;
            }
        });
    }
    public void clickGravity(View v){
            pj.setGravity(0.5f,1.1f,0.3f);//asigno gravedad
        pj.activeGravity(!pj.isActiveGravity());//activo gravedad hilo
    }
    public void clickJump(View v){
       // pj.jump(GameObject.UP,500,,20,40);//activo salto del personaje
        pj.jump(GameObject.UP,300,4,1.25f);//activo salto del personaje
        pj.selectGroupSpriteAnimation(2);
    }


    public void clickReverseUp(View v){
        pj.setRotateFlip(pj.getXRotateFlip(),!pj.getYRotateFlip());
    }
    public void clickReverseDer(View v){
        pj.setRotateFlip(!pj.getXRotateFlip(),pj.getYRotateFlip());//roto
    }
    public void clickDerecha(View v){ //muevo a diferente direcciones
        //fondo.setDrawPosition(fondo.getXPositiondraw()+3,fondo.getXPositiondraw());
        pj.selectGroupSpriteAnimation(1);
        pj.setPositioninParent(pj.getXPositioninParent()+20,pj.getYPositioninParent());
        pj.setRotateFlip(false,false);
    }
    public void clickIsquierda(View v){
            //fondo.setDrawPosition(fondo.getXPositiondraw()-3,fondo.getXPositiondraw());
        pj.selectGroupSpriteAnimation(1);
        pj.setPositioninParent(pj.getXPositioninParent()-20,pj.getYPositioninParent());
        pj.setRotateFlip(true,false);

    }
    public void clickArriba(View v){
        //fondo.setDrawPosition(fondo.getXPositiondraw()-3,fondo.getXPositiondraw());
        pj.setPositioninParent(pj.getXPositioninParent(),pj.getYPositioninParent()-20);
    }
    public void clickAbajo(View v){
        //fondo.setDrawPosition(fondo.getXPositiondraw()-3,fondo.getXPositiondraw());
        pj.selectGroupSpriteAnimation(0);
        pj.setPositioninParent(pj.getXPositioninParent(),pj.getYPositioninParent()+20);
    }

    private void prepararSprite(){

        //Preparo los sprites
       /* pj= new GameObject();
        Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.sprite_parado);
        pj.setSprites(BitmapUtil.getSprites(image,2,8));
        pj.startAnimationSprites();
        pj.runDraw();*/

        pj= new GameObject();
        Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.sprite_parado);
        pj.setSprites(BitmapUtil.getSprites(image,1,3));
        pj.addSprite(BitmapUtil.getSprites(BitmapFactory.decodeResource(getResources(), R.drawable.sprite_corriendo),1,9));
        pj.addSprite(BitmapUtil.getSprites(BitmapFactory.decodeResource(getResources(), R.drawable.sprite_salto),1,9));
        pj.startAnimationSprites();
        pj.setSpriteFps(10);
        pj.runDraw();

        Bitmap image1 = BitmapFactory.decodeResource(getResources(), R.drawable.fuego);
        fuego= new GameObject();
        fuego.setSprites(BitmapUtil.getSprites(image1,5,5));
        fuego.setBlendMode(new PorterDuffXfermode(PorterDuff.Mode.LIGHTEN));
        fuego.startAnimationSprites();
        fuego.runDraw();//corro el objeto o le doy vida
       // fuego.add(pj);// agrego el objeto a este otro
        fuego.setColision(new GameObject.Colision(fuego));

        Bitmap imagefondo = BitmapFactory.decodeResource(getResources(), R.drawable.fondo);
        fondo= new GameObject();
        fondo.setSpriteOnly(imagefondo);//asigno solo sprite
        //fondo.startAnimationSprites();
        fondo.runDraw();//corro el objeto o le doy vida
        fondo.add(fuego);
        fondo.add(pj);

        pj.setColision(new GameObject.Colision(pj));


        pj.setChildCamera(fondo);//la camara del fondo o del padre de todos se movera junto al pj
        fondo.setCamera(-30,-50,0,0);
        fuego.setParent(pj);//el fuego ahora tendra de padre al personaje, esto quiere decir que su posicion se vera afectada


        //Existen 3 variables para la posicion en el lienzo

        //1-Posicion in Parent lienzo, es la posicion general
        //2-Posicion relativa esta es la variable posicion relativa, es mas estetico, no afecta en colision,
        //3-posicion del padre es la variable que recibe la posicion deel padre
        //4-posicion absoluta, es la suma de todas las anteriores

        //en colision se puede tomar en cuenta solo la posicion, o posicion absoluta(se toma relativo tambien)
        //la posicion relativa es mas que todo estetico, para acomodar el sprite u otro objeto si es necesario

        //Pronto estaremos creando una Clase llamada motor, el cual se encargara de dar ajustes de velocidad a nuestro personaje
        //Poner DIRECCION
        //Encenderlo
        //Apagarlo
        //Encenderlo por un momento
        //Este mismo motor servira para un sin fin, para saltar, deslizarse, lanzar objetos etc etc

        //Otra cosa que aun falta es la animacion de deslizar, esto sera muy util para nubes o fondos que necesiten animacion deslizandose
        //con lo que hemos logrado ya podemos armar un simple juego pero aun agregaremos unas cosillas mas


        pj.setOnPositionChangeListener(new GameObject.OnPositionChangeListener() {
            @Override
            public void onPositionChange(int x, int y) {
                //System.out.println("Posicion cambiada: x: "+x+"  y: "+y);

                if(pj.isColisionWith(pj,fuego))
                   // System.out.println("Fuego con PJ colisionando");
                b.setText("Colisionando...");
                else //System.out.println("Sin colision");
                b.setText("Sin colision...");
            }
        });
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
            fondo.setCamera(Integer.parseInt(cameraPosX.getText().toString()),
                    Integer.parseInt(cameraPosY.getText().toString()),1,1
                    );
        }catch (Throwable ex){ex.printStackTrace();}
        }
}
