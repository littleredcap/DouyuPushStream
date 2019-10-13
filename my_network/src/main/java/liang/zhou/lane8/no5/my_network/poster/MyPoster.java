package liang.zhou.lane8.no5.my_network.poster;

import android.support.annotation.NonNull;
import android.util.Log;

import liang.zhou.lane8.no5.my_network.Callback;

public final class MyPoster {

    private Poster poster=new StreamingPushPoster();
    private @NonNull String serverURL=null;

    /**
     * This method has been in thread
     * @param data
     * @param callback
     */
    public void posterData(final byte data[], final Callback callback){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("MyPoster","onRun()");
                poster.post(data,callback,serverURL);
            }
        }).start();
    }
    public void setServerURL(String url){
        serverURL=url;
    }

}
