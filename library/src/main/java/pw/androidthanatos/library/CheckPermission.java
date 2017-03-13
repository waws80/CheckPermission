package pw.androidthanatos.library;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2017/3/13.
 * 作者：by thanatos
 * 作用：检查权限
 */

class CheckPermission {

    private Context mContext;

    CheckPermission(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * 判断权限集合
     * @param args  权限集合
     * @return  true   表示为需要授权
     */
     List<String> checkPermissions(String ... args){
        List<String> permissions=new ArrayList<>();
        for (String permission: args ) {
            if (checkPermission(permission)){
                permissions.add(permission);
            }
        }
        return  permissions;
    }


    /**
     * 判断当前权限是否需要授权
     * @param permission 权限
     * @return
     */
    private boolean checkPermission(String permission){
        return ContextCompat.checkSelfPermission(mContext,permission)== PackageManager.PERMISSION_DENIED;
    }
}
