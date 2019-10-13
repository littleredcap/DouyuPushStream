package liang.zhou.lane8.no5.my_push.cameraLib;

import android.graphics.SurfaceTexture;

public interface ICamera2 {

    public void setSurface(SurfaceTexture texture);
    public void startPreview();
    public void turnOffCamera();
    public int getDefinition();
    public void setDefinition(int definition);
}
