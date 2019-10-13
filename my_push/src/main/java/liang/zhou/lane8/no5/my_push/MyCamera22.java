package liang.zhou.lane8.no5.my_push;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraDevice.StateCallback;

import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.media.Image;
import android.media.ImageReader;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Surface;

import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.util.Arrays;

import javax.microedition.khronos.opengles.GL10;

public class MyCamera22 {

    private CameraManager cameraManager;
    private CameraDevice cameraDevice;
    private WeakReference<Context> weakContext;
    private int previewWidth=1080;
    private int previewHeight=1920;

    public void setCallback(CallbackFromReader callback) {
        this.callback = callback;
    }

    private CallbackFromReader callback;


    public MyCamera22(Context context){
        weakContext=new WeakReference<Context>(context);
    }

    private void initCamera() {
        Log.d("onOpened","init1");
        cameraManager= (CameraManager) weakContext.get().getSystemService(Context.CAMERA_SERVICE);
        try {
            if (ActivityCompat.checkSelfPermission(weakContext.get(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                Log.d("onOpened","init2");
                cameraManager.openCamera(cameraManager.getCameraIdList()[0], new StateCallback() {
                    @Override
                    public void onOpened(@NonNull CameraDevice camera) {
                        Log.d("onOpened","opened");
                        //callback.cameraOpened(camera);
                        cameraDevice=camera;
                        startPreview();
                    }

                    @Override
                    public void onDisconnected(@NonNull CameraDevice camera) {
                        Log.d("onOpened","disconnected");
                        camera.close();
                    }

                    @Override
                    public void onError(@NonNull CameraDevice camera, int error) {
                        Log.d("onOpened","error");
                        camera.close();
                    }
                },null);
            }else{
                ActivityCompat.requestPermissions((Activity) weakContext.get(),new String[]{Manifest.permission.RECORD_AUDIO,Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},0);
                initCamera();
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }



    public void startPreview(){
        if(cameraDevice==null){
            initCamera();
            return;
        }
        Log.d("startPreviewData","access");
        Surface surface=getImageReader().getSurface();
        CaptureRequest.Builder builder=null;
        try {
            builder=cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            builder.addTarget(surface);
            final CaptureRequest request=builder.build();
            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    Log.d("getPreviewData","configured");
                    try {
                        session.setRepeatingRequest(request, new CameraCaptureSession.CaptureCallback() {
                            @Override
                            public void onCaptureStarted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, long timestamp, long frameNumber) {
                                super.onCaptureStarted(session, request, timestamp, frameNumber);
                                Log.d("getPreviewData","CaptureStarted");
                            }

                            @Override
                            public void onCaptureProgressed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureResult partialResult) {
                                super.onCaptureProgressed(session, request, partialResult);
                                Log.d("getPreviewData","CaptureProgressed");
                            }

                            @Override
                            public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                                super.onCaptureCompleted(session, request, result);
                                Log.d("getPreviewData","CaptureCompleted");
                            }

                            @Override
                            public void onCaptureFailed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureFailure failure) {
                                super.onCaptureFailed(session, request, failure);
                                Log.d("getPreviewData","CaptureFailed");
                            }

                            @Override
                            public void onCaptureSequenceCompleted(@NonNull CameraCaptureSession session, int sequenceId, long frameNumber) {
                                super.onCaptureSequenceCompleted(session, sequenceId, frameNumber);
                                Log.d("getPreviewData","CaptureSequenceCompleted");
                            }

                            @Override
                            public void onCaptureSequenceAborted(@NonNull CameraCaptureSession session, int sequenceId) {
                                super.onCaptureSequenceAborted(session, sequenceId);
                                Log.d("getPreviewData","CaptureSequenceAborted");
                            }

                            @Override
                            public void onCaptureBufferLost(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull Surface target, long frameNumber) {
                                super.onCaptureBufferLost(session, request, target, frameNumber);
                                Log.d("getPreviewData","CaptureBufferLost");
                            }
                        }, null);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                    Log.d("getPreviewData","CaptureBufferLost");
                }
            },null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    private ImageReader getImageReader() {
        Log.d("getImageReader", "outOfCallback");
        ImageReader imageReader = ImageReader.newInstance(previewWidth, previewHeight, ImageFormat.JPEG, 1);
        imageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader reader) {
                Log.d("getImageReader", "intoCallback");
                Image image = reader.acquireLatestImage();
                ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                byte[] data = new byte[buffer.remaining()];
                buffer.get(data);
                if(callback!=null) {
                    callback.handleDataFromImageReader(data);
                }
                image.close();
                Log.d("getImageReader", data.length + "");
            }
        }, null);
        return imageReader;
    }
    private int getTexName() {
        int texture[]=new int[1];
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
        return texture[0];
    }
}
