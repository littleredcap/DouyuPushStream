package liang.zhou.lane8.no5.my_push;

import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class OpenGLOperator {

    private int program;
    private int mPositionHandle;
    private int mTextureHandle;
    private FloatBuffer mVertexBuffer;
    private FloatBuffer mBackTextureBuffer;
    private FloatBuffer mFrontTextureBuffer;
    private ByteBuffer mDrawListBuffer;

    public final String VERTEX_SHADER = "" +
            "attribute vec4 vPosition;" +
            "attribute vec2 inputTextureCoordinate;" +
            "varying vec2 textureCoordinate;" +
            "void main()" +
            "{" +
            "gl_Position = vPosition;" +
            "textureCoordinate = inputTextureCoordinate;" +
            "}";
    public final String FRAGMENT_SHADER = "" +
            "#extension GL_OES_EGL_image_external : require\n" +
            "precision mediump float;" +
            "varying vec2 textureCoordinate;\n" +
            "uniform samplerExternalOES s_texture;\n" +
            "void main() {" +
            "  gl_FragColor = texture2D( s_texture, textureCoordinate );\n" +
            "}";

    private static final float VERTEXES[] = {
            -1.0f, 1.0f,
            -1.0f, -1.0f,
            1.0f, -1.0f,
            1.0f, 1.0f,
    };

    // 后置摄像头使用的纹理坐标
    private static final float TEXTURE_BACK[] = {
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
    };

    // 前置摄像头使用的纹理坐标
    private static final float TEXTURE_FRONT[] = {
            1.0f, 1.0f,
            0.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 0.0f,
    };
    private final int VERTEX_SIZE = 2;
    private final int VERTEX_STRIDE = VERTEX_SIZE * 4;
    private static final byte VERTEX_ORDER[] = {0, 1, 2, 3};

    public OpenGLOperator(){
        program=GLES20.glCreateProgram();
        mPositionHandle = GLES20.glGetAttribLocation(program, "vPosition");
        mTextureHandle = GLES20.glGetAttribLocation(program, "inputTextureCoordinate");
    }

    public int getExternalOESTextureID() {
        int[] texture = new int[1];
        GLES20.glGenTextures(1, texture, 0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
        return texture[0];
    }

    public void draw(int textureId) {
        GLES20.glUseProgram(program); // 指定使用的program
        GLES20.glEnable(GLES20.GL_CULL_FACE); // 启动剔除
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId); // 绑定纹理
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle, VERTEX_SIZE, GLES20.GL_FLOAT, false, VERTEX_STRIDE, mVertexBuffer);

        GLES20.glEnableVertexAttribArray(mTextureHandle);

        GLES20.glVertexAttribPointer(mTextureHandle, VERTEX_SIZE, GLES20.GL_FLOAT, false, VERTEX_STRIDE, mBackTextureBuffer);

        // 真正绘制的操作
        GLES20.glDrawElements(GLES20.GL_TRIANGLE_FAN, VERTEX_ORDER.length, GLES20.GL_UNSIGNED_BYTE, mDrawListBuffer);

        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mTextureHandle);
    }
}
