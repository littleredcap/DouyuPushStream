package liang.zhou.lane8.no5.my_push;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.util.ArrayMap;

import java.lang.ref.WeakReference;

public class MyPermissionHelper {

    private WeakReference<Context> weakReferenceCtx;
    private ArrayMap<String,Integer> permissions;
    private String permission;

    public MyPermissionHelper(Context context,String permission){
        weakReferenceCtx = new WeakReference<>(context);
        permissions=new ArrayMap<>();
        this.permission=permission;
    }

    public boolean reqAndCheckPermission(){
        if(ActivityCompat.checkSelfPermission(weakReferenceCtx.get(),permission)!=
                PackageManager.PERMISSION_GRANTED){
            request();
        }
        if(ActivityCompat.checkSelfPermission(weakReferenceCtx.get(),permission)!=
                PackageManager.PERMISSION_GRANTED){
        }
        return false;
    }
    private void request(){
        ActivityCompat.requestPermissions((Activity) weakReferenceCtx.get(),
                new String[]{permission},0);
    }
}
