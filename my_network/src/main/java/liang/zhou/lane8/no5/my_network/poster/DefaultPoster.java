package liang.zhou.lane8.no5.my_network.poster;

import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import liang.zhou.lane8.no5.my_network.Callback;
import liang.zhou.lane8.no5.my_network.IOOperation;

class DefaultPoster extends Poster{

    private HttpURLConnection huc=null;

    private HttpURLConnection buildConnection(String serverURL){
        HttpURLConnection connection=null;
        URL url=null;
        try {
            url=new URL(serverURL);
            connection= (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Content-Type","application/json;charset=UTF-8");
            connection.setRequestProperty("Accept","application/json");
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

    @Override
    protected void post(byte postData[], Callback callback,String serverURL) {
        Log.d("DefaultPoster","beforeBuildConnection");
        huc=buildConnection(serverURL);
        Log.d("DefaultPoster","afterBuildConnection");

        try {
            OutputStream output=huc.getOutputStream();
            IOOperation.writeIntoStream(output,postData);
            Log.d("DefaultPoster","afterBuildConnection"+huc.getResponseCode());
            if(huc.getResponseCode()==200){
                Log.d("DefaultPoster","afterBuildConnection"+huc.getContentLength());
                byte result[]=new byte[huc.getContentLength()];

                IOOperation.readFromStream(huc.getInputStream(),result);
                callback.onSuccess(result);
            }else{
                callback.onFailure();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        huc.disconnect();
    }
}
