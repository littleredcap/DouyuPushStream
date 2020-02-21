package liang.zhou.lane8.no5.my_push.audio_raw_data_handler;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

public class AudioHelper {

    public static int sample_rate=44100;//采样率
    public static int channel= AudioFormat.CHANNEL_IN_STEREO;//声道
    public static int sample_format=AudioFormat.ENCODING_PCM_16BIT;//采样精度
    public static int bufferSize=AudioRecord.getMinBufferSize(sample_rate,channel,sample_format);
    private int audio_source;
    private byte raw_audio_data[];
    private AudioRecord recorder;
    private PCMListener pcmListener;

    private int init_failed_counter=3;

    public interface PCMListener{
        /**
         * 运行于子线程
         * @param perFrame
         */
        void pcmArrived(byte perFrame[],int size);
    }

    public AudioHelper(){
        audio_source= MediaRecorder.AudioSource.VOICE_COMMUNICATION;
        initRecorder();
        raw_audio_data=new byte[4096];
    }
    private void initRecorder(){
        if(recorder==null) {
            recorder = new AudioRecord(audio_source, sample_rate, channel, sample_format, bufferSize);
        }
    }
    public void fetchPCM(PCMListener pcmListener){
        this.pcmListener=pcmListener;
    }
    public void startRecord(){
        if(recorder.getState()==AudioRecord.STATE_INITIALIZED){
            new Thread(new Runnable() {
                int size;
                @Override
                public void run() {
                    while(true) {
                        size=recorder.read(raw_audio_data, 0, raw_audio_data.length);
                        if(pcmListener!=null&&raw_audio_data!=null){
                            pcmListener.pcmArrived(raw_audio_data,size);
                        }
                        Log.d("onAudioCollecting",raw_audio_data.length+"");
                    }
                }
            }).start();
            recorder.startRecording();
        }else{
            if(init_failed_counter==0){
                return;
            }
            init_failed_counter--;
            initRecorder();
            startRecord();
        }
    }
    public void release(){
        recorder.stop();
        recorder.release();
        recorder=null;
    }
    public void stopRecord(){
        recorder.stop();
    }
}
