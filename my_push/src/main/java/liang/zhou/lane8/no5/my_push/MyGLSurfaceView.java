package liang.zhou.lane8.no5.my_push;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL;
import javax.microedition.khronos.opengles.GL10;

import liang.zhou.lane8.no5.my_push.cameraLib.ICamera2;
import liang.zhou.lane8.no5.my_push.cameraLib.MyCamera2;

public class MyGLSurfaceView extends GLSurfaceView implements GLSurfaceView.Renderer,
        SurfaceTexture.OnFrameAvailableListener {

    private ICamera2 iCamera2;
    private SurfaceTexture surfaceTexture;
    private int texName;



    public MyGLSurfaceView(Context context) {
        this(context,null);
    }

    public MyGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);

        iCamera2= new MyCamera2(context);
    }

    private float[] vertexes= {
            -1.0f,1.0f,
            -1.0f,-1.0f,
            1.0f,-1.0f,
            1.0f,1.0f
    };
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        texName=initTex();
        surfaceTexture=new SurfaceTexture(texName);
        surfaceTexture.setOnFrameAvailableListener(this);
        iCamera2.setSurface(surfaceTexture);
        program=createProgram();
        mPositionHandler=GLES20.glGetAttribLocation(program,"vPosition");
        vertexBuffer= ByteBuffer.allocateDirect(vertexes.length*4).
                order(ByteOrder.nativeOrder()).asFloatBuffer();
        //iCamera2.launchCamera();
    }

    private int initTex() {
        int texture[]=new int[1];
        GLES20.glGenTextures(1,texture,0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,texture[0]);
        return texture[0];
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        surfaceTexture.updateTexImage();
        drawPreview();
    }

    private int program;
    private int vertexSize=2;
    private int vertexStride=vertexSize*4;
    private int mPositionHandler;
    private FloatBuffer vertexBuffer;
    private void drawPreview() {
        GLES20.glUseProgram(program);
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,texName);
        GLES20.glEnableVertexAttribArray(mPositionHandler);
        GLES20.glVertexAttribPointer(mPositionHandler,vertexSize,GLES20.GL_FLOAT,
                false,vertexStride,vertexBuffer);
    }

    private String vertexSource=
            "attribute vec4 vPosition;" +
                    "attribute vec2 inputTextureCoordinate;"+
                    "varying vec2 textureCoordinate;"+
                    "void main(){"+
                    "gl_Position=vPosition;"+
                    "textureCoordinate=inputTextureCoordinate;"+
                    "}";
    private String fragmentSource="";
    private int createProgram() {
        int vertexShader=loadShader(GLES20.GL_VERTEX_SHADER,vertexSource);
        if(vertexShader==GLES20.GL_NONE){
            return vertexShader;
        }
        int fragmentShader=loadShader(GLES20.GL_FRAGMENT_SHADER,fragmentSource);
        if(fragmentShader==GLES20.GL_NONE){
            return fragmentShader;
        }
        int program=GLES20.glCreateProgram();
        if(program==GLES20.GL_NONE){
            return program;
        }
        GLES20.glAttachShader(program,vertexShader);
        GLES20.glAttachShader(program,fragmentShader);
        GLES20.glDeleteShader(vertexShader);
        GLES20.glDeleteShader(fragmentShader);
        GLES20.glLinkProgram(program);
        int link[]=new int[1];
        GLES20.glGetProgramiv(program,GLES20.GL_LINK_STATUS,link,0);
        if(link[0]== GLES20.GL_FALSE){
            GLES20.glDeleteProgram(program);
            return GLES20.GL_NONE;
        }
        return program;
    }
    private int loadShader(int type,String shaderSource) {
        int shader=GLES20.glCreateShader(type);
        if(shader==GLES20.GL_NONE){
            return GLES20.GL_NONE;
        }
        GLES20.glShaderSource(shader,shaderSource);
        GLES20.glCompileShader(shader);
        int compiled[]=new int[1];
        GLES20.glGetShaderiv(shader,GLES20.GL_COMPILE_STATUS,compiled,0);
        if(compiled[0]==GLES20.GL_FALSE){
            GLES20.glDeleteShader(shader);
            return GLES20.GL_NONE;
        }
        return shader;
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        requestRender();
    }
}
