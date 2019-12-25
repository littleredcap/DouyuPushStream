package liang.zhou.lane8.no5.my_push.cameraLib;


import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.Surface;

import com.github.faucamp.simplertmp.DefaultRtmpPublisher;
import com.github.faucamp.simplertmp.RtmpHandler;
import com.github.faucamp.simplertmp.RtmpPublisher;

import net.ossrs.yasea.SrsFlvMuxer;
import net.ossrs.yasea.SrsPublisher;

import java.io.IOException;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.concurrent.ArrayBlockingQueue;

import static android.media.MediaCodec.BUFFER_FLAG_CODEC_CONFIG;
import static android.media.MediaCodec.BUFFER_FLAG_KEY_FRAME;

public class VideoEncoder {

    private MediaCodec mediaCodec;
    private MediaFormat mediaFormat;
    private Surface surface;
    private byte[] videoData;
    private byte[] videoData_compact;
    private boolean endOfStream = false;
    private Handler.Callback callback;
    private HandlerThread handlerThread;
    private ArrayBlockingQueue<byte[]> inputQueue,outputQueue;
    private SrsPublisher srsPublisher;
    private SrsFlvMuxer srsFlvMuxer;
    private int videoFlvTrack;

    private ScreenCapture capture;

    private long presentationTimeUs = 0;
    private RtmpPublisher publisher;

    private Handler handler;
    private final int CONNECT_TO_SERVER=0;
    private final int UPDATE_VIDEO_DATA=1;
    private int dts=1000;
    private RtmpHandler rtmpHandler;
    public byte[] configbyte;
    private MediaMuxer muxer;

    public VideoEncoder(Surface surface) {
        //this.surface=surface;
        Log.d("videoEncoder","into");
        capture=new ScreenCapture();
        try {
            muxer=new MediaMuxer("temp.mp4", MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        } catch (IOException e) {
            e.printStackTrace();
        }
        rtmpHandler=new RtmpHandler(new RtmpHandler.RtmpListener() {
            @Override
            public void onRtmpConnecting(String msg) {
                Log.d("rtmpConnecting",msg);
            }

            @Override
            public void onRtmpConnected(String msg) {
                Log.d("rtmpConnected",msg);
                //srsPublisher.startPublish("rtmp://192.168.88.107:1935/hls/test");
                srsFlvMuxer.start("rtmp://192.168.88.107:1935/hls/test");
            }

            @Override
            public void onRtmpVideoStreaming() {
                Log.d("onRtmpVideoStreaming","into");
            }

            @Override
            public void onRtmpAudioStreaming() {

            }

            @Override
            public void onRtmpStopped() {

            }

            @Override
            public void onRtmpDisconnected() {
                Log.d("disConnected","true");
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
        });
        srsFlvMuxer=new SrsFlvMuxer(rtmpHandler);
        publisher=new DefaultRtmpPublisher(rtmpHandler);
        publisher.setVideoResolution(1080,1920);
        inputQueue=new ArrayBlockingQueue<>(25);
        outputQueue=new ArrayBlockingQueue<>(25);
        handlerThread=new HandlerThread("put2BlockingQ");
        handlerThread.start();
        handler=new Handler(handlerThread.getLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what==CONNECT_TO_SERVER){
                    Log.d("handleMessage","connect_to_server");
                    if (publisher.connect("rtmp://192.168.88.107:1935/hls/test")) {
                        publisher.publish("live");
                    }
                }else if(msg.what==UPDATE_VIDEO_DATA){
                    byte[] raw_data = (byte[]) msg.obj;

                    try {
                        inputQueue.put(raw_data);
                        Log.d("handleMessage",inputQueue.size()+"handleMessage");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        handler.sendEmptyMessage(CONNECT_TO_SERVER);
        initMediaCodec();
        launchReadThread();
        launchSendThread();
    }

    private void launchSendThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    //outputQueue.
                }
            }
        }).start();
    }

    private void launchReadThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    byte data[]=inputQueue.poll();
                    boolean is_key_frame=false;
                    if(data!=null){
                        int inputBufferIndex = mediaCodec.dequeueInputBuffer(-1);

                        Log.d("setVideoData",inputBufferIndex+"inputBufferIndex");
                        if (inputBufferIndex >= 0) {
                            ByteBuffer inputBuffer = mediaCodec.getInputBuffer(inputBufferIndex);
                            inputBuffer.clear();
                            inputBuffer.put(data);
                            Log.d("setVideoData",inputBuffer.remaining()+"inputBuffer_length");
                            mediaCodec.queueInputBuffer(inputBufferIndex, 0,
                                    data.length, presentationTimeUs, 0);
                            //Log.d("setVideoData",videoData.length+"");
                            presentationTimeUs+=1;
                        }
                        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
                        int outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo,2000);
                        Log.d("setVideoData",outputBufferIndex+"outputBufferIndex");
                        while (outputBufferIndex >= 0) {
                            ByteBuffer outputBuffer = mediaCodec.getOutputBuffer(outputBufferIndex);
                            byte []compact_data=new byte[bufferInfo.size];

                            outputBuffer.get(compact_data);
                            //srsFlvMuxer.writeSampleData(videoFlvTrack,outputBuffer,bufferInfo);
                            if(bufferInfo.flags == BUFFER_FLAG_CODEC_CONFIG){
                                //保存pps和sps 只有刚开始第一帧里面有
                                configbyte = new byte[bufferInfo.size];
                                configbyte = compact_data;

                            }else if(bufferInfo.flags == BUFFER_FLAG_KEY_FRAME){
                                is_key_frame=true;
                                //关键帧- 都要加上pps和sps
                                /*byte[] keyframe = new byte[bufferInfo.size + configbyte.length];
                                System.arraycopy(configbyte, 0, keyframe, 0, configbyte.length);
                                System.arraycopy(compact_data, 0, keyframe, configbyte.length, compact_data.length);*/
                            }
                            /*if(compact_data!=null) {
                                publisher.publishVideoData(compact_data, compact_data.length, dts++);
                            }*/
                            //capture.push_screen(compact_data,presentationTimeUs,is_key_frame);
                            mediaCodec.releaseOutputBuffer(outputBufferIndex, false);
                            Log.d("setVideoData",compact_data.length+"compact_data_length");
                            outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 2000);
                        }
                    }
                }
            }
        }).start();
    }

    public void setVideoData(byte[] videoData) {
        this.videoData = videoData;
        Message msg=Message.obtain();
        msg.what=UPDATE_VIDEO_DATA;
        msg.obj=videoData;
        handler.sendMessage(msg);
        /*try {
            inputQueue.put(videoData);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        //Log.d("setVideoData",videoData_compact.length+"");
    }

    public byte[] getVideoData_compact() {
        return videoData_compact;
    }

    public Surface getSurface() {
        return surface;
    }

    private void initMediaCodec() {
        try {
            mediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC);
            mediaFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC,
                    1080, 1920);
            videoFlvTrack=srsFlvMuxer.addTrack(mediaFormat);
            mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT,
                    MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible);
            mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, 200 * 200);
            mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 20);
            mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);
            mediaFormat.setInteger(MediaFormat.KEY_REPEAT_PREVIOUS_FRAME_AFTER, 1000 / 30);
            //mediaCodec.setInputSurface(surface);
            /*mediaCodec.setCallback(new MediaCodec.Callback() {
                @Override
                public void onInputBufferAvailable(@NonNull MediaCodec codec, final int index) {
                    ByteBuffer inputBuffer = codec.getInputBuffer(index);
                    //Log.d("inputBuffer", videoData.length + "");
                    if(videoData!=null) {
                        inputBuffer.put(videoData);
                        mediaCodec.queueInputBuffer(index, 0, videoData.length,
                                0, 0);
                    }
                }

                @Override
                public void onOutputBufferAvailable(@NonNull MediaCodec codec, int index,
                                                    @NonNull MediaCodec.BufferInfo info) {



                }

                @Override
                public void onError(@NonNull MediaCodec codec, @NonNull MediaCodec.CodecException e) {

                }

                @Override
                public void onOutputFormatChanged(@NonNull MediaCodec codec, @NonNull MediaFormat format) {

                }
            });*/
            mediaCodec.configure(mediaFormat, null, null,
                    MediaCodec.CONFIGURE_FLAG_ENCODE);
            mediaCodec.start();
            //launchReadThread();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void encode() {

    }
}
