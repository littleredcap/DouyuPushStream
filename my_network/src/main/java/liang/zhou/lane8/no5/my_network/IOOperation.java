package liang.zhou.lane8.no5.my_network;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IOOperation {

    public static void writeIntoStream(OutputStream output,byte data[]){
        try(DataOutputStream dos=new DataOutputStream(output)) {
            dos.write(data);
            dos.flush();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void readFromStream(InputStream input,byte result[]){
        try(BufferedInputStream bis=new BufferedInputStream(input)) {
            bis.read(result);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
