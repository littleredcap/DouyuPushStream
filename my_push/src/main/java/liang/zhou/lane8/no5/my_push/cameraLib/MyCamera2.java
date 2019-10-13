package liang.zhou.lane8.no5.my_push.cameraLib;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.YuvImage;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Surface;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.github.faucamp.simplertmp.RtmpHandler;
import com.seu.magicfilter.base.gpuimage.GPUImageFilter;

import net.ossrs.yasea.SrsEncodeHandler;
import net.ossrs.yasea.SrsEncoder;
import net.ossrs.yasea.SrsFlvMuxer;
import net.ossrs.yasea.SrsMp4Muxer;
import net.ossrs.yasea.SrsRecordHandler;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class MyCamera2 implements ICamera2 {

    private CameraManager cameraManager;
    private CameraDevice cameraDevice;
    private CaptureRequest.Builder reqBuilder;
    private int defaultCamera = CameraCharacteristics.LENS_FACING_BACK;
    private Handler cameraHandler;
    private HandlerThread cameraThread;
    private Handler frameHandler;
    private Surface previewSurface, post_surface;
    private CameraCaptureSession cameraSession;
    private CaptureRequest previewReq;
    private ImageReader imageReader;
    private int previewWidth = 1080;
    private int previewHeight = 1920;
    private VideoEncoder encoder;
    private SrsEncoder srsEncoder;
    private SrsEncodeHandler encodeHandler;
    private SrsFlvMuxer mFlvMuxer;
    private SrsMp4Muxer mMp4Muxer;
    private int definition=1;


    private WeakReference<Context> weakCtx;

    public MyCamera2(Context context) {
        weakCtx = new WeakReference<>(context);
        cameraManager = (CameraManager) weakCtx.get().getSystemService(Context.CAMERA_SERVICE);
        frameHandler = new Handler();
        Log.d("myCamera", "into");
        createEncoder();
        //post_surface=encoder.getSurface();
        imageReader = getImageReader();
        //encoder.encode();

    }

    private void createEncoder() {
        encodeHandler = new SrsEncodeHandler(new SrsEncodeHandler.SrsEncodeListener() {
            @Override
            public void onNetworkWeak() {
            }

            @Override
            public void onNetworkResume() {

            }

            @Override
            public void onEncodeIllegalArgumentException(IllegalArgumentException e) {

            }
        });
        srsEncoder = new SrsEncoder(encodeHandler);
        mMp4Muxer = new SrsMp4Muxer(new SrsRecordHandler(new SrsRecordHandler.SrsRecordListener() {
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
        }));
        mFlvMuxer = new SrsFlvMuxer(new RtmpHandler(new RtmpHandler.RtmpListener() {
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
        }));
        mFlvMuxer.setVideoResolution(640, 360);
        mFlvMuxer.start("rtmp://192.168.88.107:1935/hls/test");
        srsEncoder.setVideoHDMode();
        srsEncoder.setMp4Muxer(mMp4Muxer);
        srsEncoder.setFlvMuxer(mFlvMuxer);
        //srsEncoder.setPortraitResolution(1200,1600);
        srsEncoder.setScreenOrientation(Configuration.ORIENTATION_PORTRAIT);
        //srsEncoder.setPortraitResolution(previewWidth, previewHeight);
        //srsEncoder.setLandscapeResolution(previewWidth, previewHeight);

        srsEncoder.start();
    }
    public int getDefinition(){
        return definition;
    }
    public void setDefinition(int definition){
        this.definition=definition;
        srsEncoder.stop();

        if(definition==1){
            srsEncoder.setVideoHDMode();
        }else if(definition==2){
            srsEncoder.setVideoSmoothMode();
        }
        srsEncoder.start();
    }

    private ImageReader getImageReader() {
        final Rect rect = new Rect();
        Log.d("getImageReader", "outOfCallback");
        ImageReader imageReader = ImageReader.newInstance(640,
                360, ImageFormat.YUV_420_888, 1);
        imageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader reader) {

                Image image = reader.acquireLatestImage();
                Log.d("getImageReader", "width:" + image.getWidth() + "height:" + image.getHeight());
                rect.set(0, 0, image.getHeight(), image.getWidth());
                /*ByteBuffer y_buffer = image.getPlanes()[0].getBuffer();
                ByteBuffer u_buffer = image.getPlanes()[1].getBuffer();
                ByteBuffer v_buffer = image.getPlanes()[2].getBuffer();
                Log.d("getImageReader", "y:"+y_buffer.remaining());
                Log.d("getImageReader", "u:"+u_buffer.remaining());
                Log.d("getImageReader", "v:"+v_buffer.remaining());
                byte[] yuv_y = new byte[y_buffer.remaining()];
                byte[] yuv_u = new byte[u_buffer.remaining()];
                byte[] yuv_v = new byte[v_buffer.remaining()];
                y_buffer.get(yuv_y);
                u_buffer.get(yuv_u);
                v_buffer.get(yuv_v);*/
                //byte nv21[] = YUVUtil.getBytesFromImageAsType(image, YUVUtil.NV21);//恢复
                byte yuv420sp[] = YUVUtil.getBytesFromImageAsType(image, YUVUtil.YUV420SP);
                mirror(yuv420sp,rect.bottom,rect.right);
                int rgb[]=yuv420sp2rgb(rotateYUV420Degree90(yuv420sp,rect.bottom,rect.right,90),
                        rect.bottom,rect.right);
                //byte[] rawYuv=yuv420p(image,image.getWidth(),image.getHeight());
                //buffer.get(rawYuv);
                //byte[] after_crop=new byte[data.length];
                //crop_yuv(data,after_crop,image.getWidth(),image.getHeight(),480,800);
                /*byte[] after_rotate = rotateYUVDegree90(rawYuv,image.getHeight()*8/10
                        ,image.getWidth()*8/10);*/
                //byte[] after_rotate=new byte[nv21.length];
                //rotateYUV420Degree90(nv21, after_rotate, 1600, 1200);
                /*if (callback != null) {
                    callback.handleDataFromImageReader(data);
                }*/
                //encoder.setVideoData(data);
                //byte nv21[]=new byte[image.getWidth()*image.getHeight()];
                //mirror(nv21,image.getHeight(),image.getWidth());
                //YV12toNV21(rawYuv,nv21,image.getHeight()*8/10,image.getWidth()*8/10);
                //NV21_mirror(nv21,rect.bottom,rect.right);
                //byte after_rotate[]=rotateYUV420Degree90(nv21,rect.bottom,rect.right,270);恢复
                //srsEncoder.onGetYuvNV21Frame(after_rotate, rect.bottom, rect.right, rect);恢复
                srsEncoder.onGetArgbFrame(rgb,rect.bottom,rect.right);
                //image.close();
            }
        }, frameHandler);
        return imageReader;
    }

    private int[] yuv420sp2rgb(byte[] yuv420sp, int width, int height) {
        final int frameSize = width * height;
        int rgb[] = new int[frameSize];
        for (int j = 0, yp = 0; j < height; j++) {
            int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
            for (int i = 0; i < width; i++, yp++) {
                int y = (0xff & ((int) yuv420sp[yp])) - 16;
                if (y < 0)
                    y = 0;
                if ((i & 1) == 0) {
                    v = (0xff & yuv420sp[uvp++]) - 128;
                    u = (0xff & yuv420sp[uvp++]) - 128;
                }
                int y1192 = 1192 * y;
                int r = (y1192 + 1634 * v);
                int g = (y1192 - 833 * v - 400 * u);
                int b = (y1192 + 2066 * u);
                if (r < 0)
                    r = 0;
                else if (r > 262143)
                    r = 262143;
                if (g < 0)
                    g = 0;
                else if (g > 262143)
                    g = 262143;
                if (b < 0)
                    b = 0;
                else if (b > 262143)
                    b = 262143;
                rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000)
                        | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
            }
        }
        return rgb;
    }

    private void NV21_mirror(byte[] nv21_data, int width, int height) {
        int i;
        int left, right;
        byte temp;
        int startPos = 0;

        for (i = 0; i < height; i++) {
            left = startPos;
            right = startPos + width - 1;
            while (left < right) {
                temp = nv21_data[left];
                nv21_data[left] = nv21_data[right];
                nv21_data[right] = temp;
                left++;
                right--;
            }
            startPos += width;
        }
        int offset = width * height;
        startPos = 0;
        for (i = 0; i < height / 2; i++) {
            left = offset + startPos;
            right = offset + startPos + width - 2;
            while (left < right) {
                temp = nv21_data[left];
                nv21_data[left] = nv21_data[right];
                nv21_data[right] = temp;
                left++;
                right--;

                temp = nv21_data[left];
                nv21_data[left] = nv21_data[right];
                nv21_data[right] = temp;
                left++;
                right--;
            }
            startPos += width;
        }
    }

    private void mirror(byte[] src, int w, int h) { //src是原始yuv数组
        int i;
        int index;
        byte temp;
        int a, b;
        //mirror y
        for (i = 0; i < h; i++) {
            a = i * w;
            b = (i + 1) * w - 1;
            while (a < b) {
                temp = src[a];
                src[a] = src[b];
                src[b] = temp;
                a++;
                b--;
            }
        }
        // mirror u and v
        index = w * h;
        for (i = 0; i < h / 2; i++) {
            a = i * w;
            b = (i + 1) * w - 2;
            while (a < b) {
                temp = src[a + index];
                src[a + index] = src[b + index];
                src[b + index] = temp;

                temp = src[a + index + 1];
                src[a + index + 1] = src[b + index + 1];
                src[b + index + 1] = temp;
                a += 2;
                b -= 2;
            }
        }
    }

    private void crop_yuv(byte[] data, byte[] dst, int width, int height,
                          int goalWidth, int goalHeight) {
        int i, j;
        int h_div = 0, w_div = 0;
        w_div = (width - goalWidth) / 2;
        if (w_div % 2 != 0) {
            w_div--;
        }
        h_div = (height - goalHeight) / 2;
        if (h_div % 2 != 0) {
            h_div--;
        }
        //u_div = (height-goalheight)/4;
        int src_y_length = width * height;
        int dst_y_length = goalWidth * goalWidth;
        for (i = 0; i < goalHeight; i++)
            for (j = 0; j < goalWidth; j++) {
                dst[i * goalWidth + j] = data[(i + h_div) * width + j + w_div];
            }
        int index = dst_y_length;
        int src_begin = src_y_length + h_div * width / 4;
        int src_u_length = src_y_length / 4;
        int dst_u_length = dst_y_length / 4;
        for (i = 0; i < goalHeight / 2; i++) {
            for (j = 0; j < goalWidth / 2; j++) {
                int p = src_begin + i * (width >> 1) + (w_div >> 1) + j;
                dst[index] = data[p];
                dst[dst_u_length + index++] = data[p + src_u_length];
            }
        }
    }

    public static byte[] rotateYUV420Degree90(byte[] input, int width, int height, int rotation) {
        int frameSize = width * height;
        int qFrameSize = frameSize / 4;
        byte[] output = new byte[frameSize + 2 * qFrameSize];
        boolean swap = (rotation == 90 || rotation == 270);
        boolean yflip = (rotation == 90 || rotation == 180);
        boolean xflip = (rotation == 270 || rotation == 180);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int xo = x, yo = y;
                int w = width, h = height;
                int xi = xo, yi = yo;
                if (swap) {
                    xi = w * yo / h;
                    yi = h * xo / w;
                }
                if (yflip) {
                    yi = h - yi - 1;
                }
                if (xflip) {
                    xi = w - xi - 1;
                }
                output[w * yo + xo] = input[w * yi + xi];
                int fs = w * h;
                int qs = (fs >> 2);
                xi = (xi >> 1);
                yi = (yi >> 1);
                xo = (xo >> 1);
                yo = (yo >> 1);
                w = (w >> 1);
                h = (h >> 1);
                int ui = fs + (w * yi + xi) * 2;
                int uo = fs + (w * yo + xo) * 2;
                int vi = ui + 1;
                int vo = uo + 1;
                output[uo] = input[ui];
                output[vo] = input[vi];
            }
        }
        return output;
    }

    private void rotateYUV420Degree90(byte[] src, byte[] des, int m_nWidth, int m_nHeight) {
        int width = m_nHeight;
        int height = m_nWidth;
        int wh = width * height;
        //旋转Y
        int k = 0;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                des[k] = src[width * j + i];
                k++;
            }
        }

        for (int i = 0; i < width; i += 2) {
            for (int j = 0; j < height / 2; j++) {
                des[k] = src[wh + width * j + i];
                des[k + 1] = src[wh + width * j + i + 1];
                k += 2;
            }
        }
    }

    private byte[] rotateYUVDegree90(byte[] data, int imageWidth, int imageHeight) {
        byte[] yuv = new byte[imageWidth * imageHeight * 3 / 2];
        // Rotate the Y luma
        int i = 0;
        for (int x = 0; x < imageWidth; x++) {
            for (int y = imageHeight - 1; y >= 0; y--) {
                yuv[i] = data[y * imageWidth + x];
                i++;
            }
        }
        // Rotate the U and V color components
        i = imageWidth * imageHeight * 3 / 2 - 1;
        for (int x = imageWidth - 1; x > 0; x = x - 2) {
            for (int y = 0; y < imageHeight / 2; y++) {
                yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth) + x];
                i--;
                yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth) + (x - 1)];
                i--;
            }
        }
        return yuv;
    }

    private byte[] yuv420p(Image image, int width, int height) {
        Image.Plane planes[] = image.getPlanes();
        ByteBuffer buffer = planes[0].getBuffer();
        int ysize = buffer.remaining();
        byte[] yData = new byte[width * height];
        buffer.get(yData);

        //拼接两段uv数据
        byte[] uvData = new byte[width * height / 2];
        int uSize = width * height / 4;
        int vSize = uSize;

        //取出planes[1]中的数据  此中的数据一定为U
        ByteBuffer uvBuffer1 = image.getPlanes()[1].getBuffer();
        int uvsize1 = uvBuffer1.remaining();//此处大小为width*height/2.0  因为步幅为2
        byte[] uvBuffData1 = new byte[uvsize1];
        uvBuffer1.get(uvBuffData1);
        for (int i = 0; i < uSize; i++) {
            uvData[i] = uvBuffData1[i * planes[1].getPixelStride()];
        }

        //取出planes[2]中的数据 此中的数据一定为V
        ByteBuffer uvBuffer2 = image.getPlanes()[2].getBuffer();
        int uvsize2 = uvBuffer2.remaining();//此处大小为width*height/2.0  因为步幅为2
        byte[] uvBuffData2 = new byte[uvsize2];
        uvBuffer2.get(uvBuffData2);
        for (int i = 0; i < vSize; i++) {
            uvData[uSize + i] = uvBuffData2[i * planes[2].getPixelStride()];
        }
        return uvData;
    }

    private void YV12toNV21(final byte[] input, final byte[] output,
                            final int width, final int height) {
        long startMs = System.currentTimeMillis();
        final int frameSize = width * height;
        final int qFrameSize = frameSize / 4;
        final int tempFrameSize = frameSize * 5 / 4;

        System.arraycopy(input, 0, output, 0, frameSize); // Y

        for (int i = 0; i < qFrameSize; i++) {
            output[frameSize + i * 2] = input[frameSize + i]; // Cb (U)
            output[frameSize + i * 2 + 1] = input[tempFrameSize + i]; // Cr (V)
        }
    }

    private void swapYV12toNV12(byte[] yv12bytes, byte[] nv12bytes, int width, int height) {
        int nLenY = width * height;
        int nLenU = nLenY / 4;

        System.arraycopy(yv12bytes, 0, nv12bytes, 0, width * height);
        for (int i = 0; i < nLenU; i++) {
            nv12bytes[nLenY + 2 * i + 1] = yv12bytes[nLenY + i];
            nv12bytes[nLenY + 2 * i] = yv12bytes[nLenY + nLenU + i];
        }
    }

    private void launchCameraThread() {
        if (cameraThread == null) {
            cameraThread = new HandlerThread("cameraThread");
            cameraThread.start();
            if (cameraHandler == null) {
                cameraHandler = new Handler(cameraThread.getLooper());
            }
        }
    }

    private void initPreviewReq() {
        try {
            reqBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            reqBuilder.addTarget(previewSurface);
            reqBuilder.addTarget(imageReader.getSurface());
            cameraDevice.createCaptureSession(Arrays.asList(previewSurface,
                    imageReader.getSurface()), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    cameraSession = session;
                    previewReq = reqBuilder.build();
                    startPreview();
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {

                }
            }, cameraHandler);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setSurface(SurfaceTexture texture) {
        previewSurface = new Surface(texture);
    }

    @SuppressLint("MissingPermission")
    public void launchCamera() {
        launchCameraThread();
        try {
            cameraManager.openCamera(Integer.toString(defaultCamera), new CameraDevice.StateCallback() {
                @Override
                public void onOpened(@NonNull CameraDevice camera) {
                    cameraDevice = camera;
                    initPreviewReq();
                }

                @Override
                public void onDisconnected(@NonNull CameraDevice camera) {
                    turnOffCamera();
                }

                @Override
                public void onError(@NonNull CameraDevice camera, int error) {

                }
            }, cameraHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startPreview() {
        if (cameraSession == null || reqBuilder == null) {
            return;
        }
        try {
            cameraSession.setRepeatingRequest(previewReq, null, cameraHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void turnOffCamera() {
        if (cameraSession != null) {
            cameraSession.close();
            cameraSession = null;
        }
        if (cameraDevice != null) {
            cameraDevice.close();
            cameraDevice = null;
        }
        cameraThread.quitSafely();
    }
}
