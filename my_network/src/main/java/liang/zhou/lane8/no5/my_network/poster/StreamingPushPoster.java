package liang.zhou.lane8.no5.my_network.poster;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import liang.zhou.lane8.no5.my_network.Callback;
import liang.zhou.lane8.no5.my_network.IOOperation;

class StreamingPushPoster extends Poster {

    private HttpURLConnection huc=null;

    @Override
    protected void post(byte[] postData, Callback callback,@NonNull String serverURL) {
        HttpURLConnection connection=buildConnection(serverURL);
        try {
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK||connection.getContentLength()<=0){
                callback.onFailure();
                return;
            }
            byte[] result=new byte[connection.getContentLength()];
            IOOperation.readFromStream(connection.getInputStream(),result);
            callback.onSuccess(result);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private HttpURLConnection buildConnection(String serverURL) {
        HttpURLConnection connection=null;
        URL url=null;
        try {
            url=new URL(serverURL);
            connection= (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(4500);
            connection.setReadTimeout(10000);
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            //connection.setConnectTimeout(5000);
            connection.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }

}
