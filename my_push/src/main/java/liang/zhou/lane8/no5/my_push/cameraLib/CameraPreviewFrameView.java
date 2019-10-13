package liang.zhou.lane8.no5.my_push.cameraLib;

import android.app.Activity;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.AttributeSet;

import com.seu.magicfilter.base.gpuimage.GPUImageFilter;
import com.seu.magicfilter.utils.MagicFilterFactory;
import com.seu.magicfilter.utils.MagicFilterType;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import liang.zhou.lane8.no5.my_push.myLib.CameraDrawer;
import liang.zhou.lane8.no5.my_push.myLib.OpenGLUtils;

public class CameraPreviewFrameView extends GLSurfaceView implements GLSurfaceView.Renderer,
        SurfaceTexture.OnFrameAvailableListener {

    private static final String TAG = "Camera2GLSurfaceView";
    private MyCamera2 mCameraProxy;
    private SurfaceTexture mSurfaceTexture;
    private CameraDrawer mDrawer;
    private int mRatioWidth = 0;
    private int mRatioHeight = 0;
    private int mTextureId = -1;
    private GPUImageFilter filter;
    private float[] mProjectionMatrix = new float[16];
    private float[] mSurfaceMatrix = new float[16];
    private float[] mTransformMatrix = new float[16];

    public CameraPreviewFrameView(Context context) {
        this(context,null);
    }

    public CameraPreviewFrameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mCameraProxy = new MyCamera2(context);
        setEGLContextClientVersion(2);
        setRenderer(this);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        filter= MagicFilterFactory.initFilters(MagicFilterType.BEAUTY);
        filter.init(getContext().getApplicationContext());
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mTextureId = OpenGLUtils.getExternalOESTextureID();
        mSurfaceTexture = new SurfaceTexture(mTextureId);
        mSurfaceTexture.setDefaultBufferSize(1080,1920);
        mSurfaceTexture.setOnFrameAvailableListener(this);
        mCameraProxy.setSurface(mSurfaceTexture);
        mDrawer = new CameraDrawer();
        mCameraProxy.launchCamera();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //Log.d(TAG, "onSurfaceChanged. thread: " + Thread.currentThread().getName());
        //Log.d(TAG, "onSurfaceChanged. width: " + width + ", height: " + height);

        //GLES20.glViewport(0, 0, 1080, 1920);
        //filter.onInputSizeChanged(width, width);
        //filter.onDisplaySizeChanged(width, width);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        mSurfaceTexture.updateTexImage();

        mSurfaceTexture.getTransformMatrix(mSurfaceMatrix);
        Matrix.multiplyMM(mTransformMatrix, 0, mSurfaceMatrix, 0, mProjectionMatrix, 0);
        filter.setTextureTransformMatrix(mTransformMatrix);
        filter.onDrawFrame(mTextureId);

        mDrawer.draw(mTextureId,false,filter.getGLFboBuffer());
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        requestRender();
    }

    public ICamera2 getCameraProxy() {
        return mCameraProxy;
    }

    public SurfaceTexture getSurfaceTexture() {
        return mSurfaceTexture;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (0 == mRatioWidth || 0 == mRatioHeight) {
            setMeasuredDimension(width, height);
        } else {
            if (width < height * mRatioWidth / mRatioHeight) {
                setMeasuredDimension(width, width * mRatioHeight / mRatioWidth);
            } else {
                setMeasuredDimension(height * mRatioWidth / mRatioHeight, height);
            }
        }
    }

}
