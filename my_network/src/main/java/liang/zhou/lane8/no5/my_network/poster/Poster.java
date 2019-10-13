package liang.zhou.lane8.no5.my_network.poster;

import android.support.annotation.NonNull;

import org.json.JSONObject;

import liang.zhou.lane8.no5.my_network.Callback;

public abstract class Poster {

    protected abstract void post(byte postData[], Callback callback,@NonNull String serverURL);

}
