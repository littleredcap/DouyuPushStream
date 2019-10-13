package liang.zhou.lane8.no5.my_push;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import liang.zhou.lane8.no5.my_push.cameraLib.CameraPreviewFrameView;
import liang.zhou.lane8.no5.my_push.cameraLib.ICamera2;

public class PushActivity extends AppCompatActivity {

    private ICamera2 camera;
    private CameraPreviewFrameView previewFrameView;
    private TextView definition_tv;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push);
        previewFrameView=findViewById(R.id.preview);
        definition_tv=findViewById(R.id.activity_push_definition);

        definition_tv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                setDefinition();
                return false;
            }
        });
        camera=previewFrameView.getCameraProxy();

        requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }

    private void setDefinition(){
        if(camera.getDefinition()==1) {
            camera.setDefinition(2);
            definition_tv.setText("高清");
        }else if(camera.getDefinition()==2){
            camera.setDefinition(1);
            definition_tv.setText("流畅");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("MainActivity",grantResults[1]+""+grantResults[0]);
        if(grantResults[1]== PackageManager.PERMISSION_GRANTED&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
            Log.d("MainActivity","permission_granted");
            camera.startPreview();
        }
    }
}
