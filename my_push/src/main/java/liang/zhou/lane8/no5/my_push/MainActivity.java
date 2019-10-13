package liang.zhou.lane8.no5.my_push;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.github.faucamp.simplertmp.RtmpHandler;
import com.qiniu.pili.droid.streaming.demo.filter.CameraFilterBeauty;

import net.ossrs.yasea.SrsCameraView;
import net.ossrs.yasea.SrsEncodeHandler;
import net.ossrs.yasea.SrsPublisher;
import net.ossrs.yasea.SrsRecordHandler;

import java.io.IOException;
import java.net.SocketException;

import liang.zhou.lane8.no5.my_push.cameraLib.CameraPreviewFrameView;
import liang.zhou.lane8.no5.my_push.cameraLib.ICamera2;
import liang.zhou.lane8.no5.my_push.cameraLib.ScreenCapture;


public class MainActivity extends AppCompatActivity implements SrsEncodeHandler.SrsEncodeListener,
        RtmpHandler.RtmpListener, SrsRecordHandler.SrsRecordListener {

    private ICamera2 iCamera2;
    private CameraPreviewFrameView previewFrameView;
    private SrsPublisher publisher;
    private SrsCameraView cameraView;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("MainActivity", "create1");
        setContentView(R.layout.activity_main);
        Log.d("MainActivity", "create2");

        //previewFrameView=findViewById(R.id.preview);
        //iCamera2=previewFrameView.getCameraProxy();
        Log.d("MainActivity", "create3");


        cameraView=findViewById(R.id.activity_main_camera);
        publisher = new SrsPublisher(cameraView);
        publisher.setEncodeHandler(new SrsEncodeHandler(this));
        publisher.setRtmpHandler(new RtmpHandler(this));
        publisher.setRecordHandler(new SrsRecordHandler(this));
        publisher.setPreviewResolution(640, 360);
        publisher.setOutputResolution(360, 640);
        publisher.setVideoSmoothMode();
        requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("MainActivity",grantResults[1]+""+grantResults[0]);
        if(grantResults[1]==PackageManager.PERMISSION_GRANTED&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
            Log.d("MainActivity","permission_granted");
            //iCamera2.startPreview();
            publisher.startCamera();
            publisher.startPublish("rtmp://192.168.88.107:1935/hls/test");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //iCamera2.turnOffCamera();
    }

    @Override
    public void onNetworkWeak() {

    }

    @Override
    public void onNetworkResume() {

    }

    @Override
    public void onEncodeIllegalArgumentException(IllegalArgumentException e) {

    }

    @Override
    public void onRtmpConnecting(String msg) {

    }

    @Override
    public void onRtmpConnected(String msg) {

    }

    @Override
    public void onRtmpVideoStreaming() {

    }

    @Override
    public void onRtmpAudioStreaming() {

    }

    @Override
    public void onRtmpStopped() {

    }

    @Override
    public void onRtmpDisconnected() {

    }

    @Override
    public void onRtmpVideoFpsChanged(double fps) {

    }

    @Override
    public void onRtmpVideoBitrateChanged(double bitrate) {

    }

    @Override
    public void onRtmpAudioBitrateChanged(double bitrate) {

    }

    @Override
    public void onRtmpSocketException(SocketException e) {

    }

    @Override
    public void onRtmpIOException(IOException e) {

    }

    @Override
    public void onRtmpIllegalArgumentException(IllegalArgumentException e) {

    }

    @Override
    public void onRtmpIllegalStateException(IllegalStateException e) {

    }

    @Override
    public void onRecordPause() {

    }

    @Override
    public void onRecordResume() {

    }

    @Override
    public void onRecordStarted(String msg) {

    }

    @Override
    public void onRecordFinished(String msg) {

    }

    @Override
    public void onRecordIllegalArgumentException(IllegalArgumentException e) {

    }

    @Override
    public void onRecordIOException(IOException e) {

    }
}
