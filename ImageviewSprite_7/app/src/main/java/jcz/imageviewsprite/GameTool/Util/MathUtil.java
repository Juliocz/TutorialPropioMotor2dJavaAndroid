/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jcz.imageviewsprite.GameTool.Util;



import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 *
 * @author pc 9
 */
public class MathUtil {
    public static float getHipotenusa(float x,float y)
    {
        float h=exp(x,2)+exp(y,2);
        h=(float) Math.sqrt(h);
        return h;
    }
    /*/         -   -   -
            /             |
         /                |y
    angle                 |
    -------------------
        x*/
    public static float getAngleX(float x,float y)
    {   //1rad=57,296grados
        //1rad*180/pi=57,296
        float h=getHipotenusa(x, y);
        return convertRadiantoAngle((float) Math.asin(y/h));//revuelve en radianes
    }
     public static float getAngleY(float x,float y)
    {
        float h=getHipotenusa(x, y);
        return convertRadiantoAngle((float) Math.asin(x/h));//revuelve en radianes
    }
    //adaptado al juego angulo
     public static float getAngleXgl(float x,float y)
    {   
        float h=getHipotenusa(x, y);
        float r= convertRadiantoAngle((float) Math.asin(y/h));//revuelve en radianes
        if(x>0 && y>0)return 90+(90-r);//significa que esta abajo y al frente
        if(x>0 && y<0)return -r;//significa que esta abajo y al frente
        //En OpenGl rotacion solo hay hazta 90 luego se buguea o en reversa
        else return r;
    }
      public static float getAngleYgl(float x,float y)
    {   
        float h=getHipotenusa(x, y);
        float r= convertRadiantoAngle((float) Math.asin(x/h));//revuelve en radianes
        if(x>0 && y>0)return 90+(90-r);//significa que esta abajo y al frente
        if(x>0 && y<0)return -r;//significa que esta abajo y al frente
        //En OpenGl rotacion solo hay hazta 90 luego se buguea o en reversa
        else return r;
    }
    
    public static float exp(float n,int exp)
    {   if(exp==0)return 1;
        float temp=n;
        for(int i=1;i<exp;i++)
        n*=temp;
        return n;
    }
    public static float convertRadiantoAngle(float radian)
    {   return (float) (radian/180*Math.PI);
        
    }



    

    //Metodos para rotar matriz----
    public static FloatBuffer rotateMatriz(Chords posicionCenterRotate,float angle_x,float angle_y,float angle_z,FloatBuffer matrix)
    {
        FloatBuffer newMatrix=cloneBuffer(matrix);
        newMatrix.position(0);
        System.out.println(newMatrix.limit());
        for(int i=0;i<newMatrix.limit();i+=3)
        {
            Chords vertex=new Chords();
            vertex.setX(matrix.get(i));
            vertex.setY(matrix.get(i+1));
            vertex.setZ(matrix.get(i+2));
            
            rotatePointer(posicionCenterRotate, angle_x, angle_y, angle_z, vertex);
            
            newMatrix.put(vertex.getX());
            newMatrix.put(vertex.getY());
            newMatrix.put(vertex.getZ());
           // System.out.println(i);
          //  System.out.println(i++);
          //  System.out.println(i++);
           
        }
        return newMatrix;
    }
    //METODOS PARA MOTOR 2D
    //rota vertex 2d
    public static Chords rotateMatriz2D(float angle_y,float x,float y){
        Chords vertex=new Chords(x,y,0);
        rotatePointer(new Chords(),0,angle_y,0,vertex);
        return vertex;
    }
    //rota posicion 2d y se puede poner punto de rotacion
    public static Chords rotateMatriz2D(float xPivot,float yPivot,float angle_y,float x,float y){
        Chords vertex=new Chords(x,y,0);
        rotatePointer(new Chords(xPivot,yPivot,0),0,angle_y,0,vertex);
        return vertex;
    }



    public static FloatBuffer rotateMatriz(float angle_x,float angle_y,float angle_z,FloatBuffer matrix)
    {
        FloatBuffer newMatrix=cloneBuffer(matrix);
        newMatrix.position(0);
        System.out.println(newMatrix.limit());
        for(int i=0;i<newMatrix.limit();i+=3)
        {
            Chords vertex=new Chords();
            vertex.setX(matrix.get(i));
            vertex.setY(matrix.get(i+1));
            vertex.setZ(matrix.get(i+2));
            
            rotatePointer(new Chords(), angle_x, angle_y, angle_z, vertex);
            
            newMatrix.put(vertex.getX());
            newMatrix.put(vertex.getY());
            newMatrix.put(vertex.getZ());
           // System.out.println(i);
          //  System.out.println(i++);
          //  System.out.println(i++);
           
        }
        return newMatrix;
    }

    //metodo que clona un float buffer
    public static FloatBuffer cloneBuffer(FloatBuffer f)
    {   FloatBuffer newBuffer;
        ByteBuffer vbb = ByteBuffer.allocateDirect(f.limit()*4);//asignar tamaño de cantidad de vertex *4bytes buffer, me daba error en el for por no poner *3 cantidad de vertex pasaba el limite
        vbb.order(ByteOrder.nativeOrder()); // Use native byte order//usa orden nativo en bytebuffer
        newBuffer = vbb.asFloatBuffer();
        f.position(0);
        for(int i=0;i<newBuffer.limit();i++)
        {
            newBuffer.put(f.get(i));
        }
        newBuffer.position(0);
        return newBuffer;
    }


    /*Operaciones a nivel float unico valor*/
    /*public static float scaleMatrixPointer(float scale,float pointer)
    {return pointer*scale;}*/
    public static void rotatePointer(Chords posicion,float angle_x,float angle_y,float angle_z,Chords vertex)
    {       //1er parametro es la posicion desde donde va ser rotado, angulos a rotar los vertex

        //Posiciono al punto donde rotara
            vertex.setX(vertex.getX()-posicion.getX());
            vertex.setY(vertex.getY()-posicion.getY());
            vertex.setZ(vertex.getZ()-posicion.getZ());
         //POR ALGUNA RAZON 360GRADOS EQUIVALE A 2PI AL HACER LA OPERACION
    //Angle=Angle/360*6.283185f;
    //angle_x=angle_x/360*6.283185f;
    angle_x=convertRadiantoAngle(angle_x);
    angle_y=convertRadiantoAngle(angle_y);    
    angle_z=convertRadiantoAngle(angle_z);
    
   // angle_x=convertRadiantoAngle(angle_x);
   // angle_y=convertRadiantoAngle(angle_y);    
   // angle_z=convertRadiantoAngle(angle_z);
        float x=vertex.getX();
        float y=vertex.getY();
        float z=vertex.getZ();
        
        //rotar en angulo X
            float newy=(float) ((y*Math.cos(angle_x))-(z*Math.sin(angle_x)));
            float newz=(float) ((z*Math.cos(angle_x))+(y*Math.sin(angle_x)));
            
            float newxx=(float) ((x*Math.cos(angle_y))-(newy*Math.sin(angle_y)));
            float newyy=(float) ((newy*Math.cos(angle_y))+(x*Math.sin(angle_y)));
            
            float newzz=(float) ((newz*Math.cos(angle_z))-(newxx*Math.sin(angle_z)));
            float newxxx=(float) ((newxx*Math.cos(angle_z))+(newz*Math.sin(angle_z)));
        //rotar en angulo Y
           /* x=(float) ((x*Math.cos(angle_x))-(y*Math.sin(angle_x)));
            y=(float) ((y*Math.cos(angle_x))+(x*Math.sin(angle_x)));
        //rotar en angulo z
            z=(float) ((z*Math.cos(angle_x))-(x*Math.sin(angle_x)));
            x=(float) ((x*Math.cos(angle_x))+(z*Math.sin(angle_x)));*/
   //Posiciono al punto donde rotara
            vertex.setX(newxxx);
            vertex.setY(newyy);
            vertex.setZ(newzz);
            
            /*vertex.setX(newxx);
            vertex.setY(newyy);
            vertex.setZ(newz);*/
   //Posicion luego de haber hecho la rotacion
            vertex.setX(vertex.getX()+posicion.getX());
            vertex.setY(vertex.getY()+posicion.getY());
            vertex.setZ(vertex.getZ()+posicion.getZ());
            
    }




        public static class Chords{

        public float getX() {
            return x;
        }

        public void setX(float x) {
            this.x = x;
        }

        public float getY() {
            return y;
        }

        public void setY(float y) {
            this.y = y;
        }

        public float getZ() {
            return z;
        }

        public void setZ(float z) {
            this.z = z;
        }
            float x=0;
            float y=0;
            float z=0;

            public Chords(float x, float y, float z) {
                this.z = z;
                this.y = y;
                this.x = x;
            }

            public Chords() {}

        }



}
