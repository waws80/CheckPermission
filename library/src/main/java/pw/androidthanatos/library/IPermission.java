package pw.androidthanatos.library;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2017/3/13.
 * 作者：by thanatos
 * 作用：权限封装
 */

public class IPermission {

    private Activity mActivity;

    private static final String TAG = "IPermission";

    private  String[] PERMISSIONS ={};
    private  int requestCode =0x2017;

    private static final String PACKAGE_URL_SCHEME = "package:";

    private CallBack mCallBack;

    private IPermission(Activity activity) {
        this.mActivity=activity;
    }

    public static  class Builder{

        public static IPermission build(@NonNull Activity activity){
            return new IPermission(activity);
        }
    }

    /**
     * 注意：权限不能为权限组   eg:  Manifest.permission_group.LOCATION
     * @param permissions  权限集合
     * @return
     */
    public IPermission addPermissions(@NonNull String ... permissions){
        PERMISSIONS=permissions;
        return this;
    }


    /**
     * 开始检测所有权限
     */
    public IPermission startCheck(){
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if (PERMISSIONS.length==0) throw new NullPointerException("请先添加权限集合");

            CheckPermission mCheckPermission = new CheckPermission(mActivity);
            List<String> checkPermissions = mCheckPermission.checkPermissions(PERMISSIONS);
            if (checkPermissions.size()>0){
                String[] permissions = checkPermissions.toArray(new String[checkPermissions.size()]);
                for (String permission : permissions) {
                    Log.w(TAG, "onResume: " + permission);
                }

                requestPermissions(permissions);
            }

        }
        return  this;
    }

    /**
     * 申请权限后的回调监听
     * @param callBack 事件回调
     */
    public IPermission onNext(CallBack callBack){
        this.mCallBack=callBack;
        return this;
    }

    /**
     * 当用户在设置界面设置权限后，重新检测是否权限全部被允许
     */
    public void reCheck(){
        startCheck();
    }


    /**
     * 开始向系统请求所需权限
     * @param permissions  权限集合
     */
    private void requestPermissions(@NonNull String... permissions) {
        ActivityCompat.requestPermissions(mActivity, permissions, requestCode);
    }


    /**
     * 获取请求结果
     *    使用在界面Class的onRequestPermissionsResult方法中
     * @param requestCode  请求码
     * @param permissions  权限集合
     * @param grantResults 是否被允许  0 标识被允许   -1标识被拒绝
     */
    public  void checkResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        List<String> refuels=new ArrayList<>();
        if (this.requestCode == requestCode ) {
            for (int i = 0; i <permissions.length ; i++) {
                if (grantResults[i]==-1){
                    refuels.add(permissions[i]);
                }
            }
        }
        isAllAllow(refuels);
    }


    /**
     * 判断是否允许了所有的权限
     * @param refuels 被拒绝的权限集合
     */
    private void  isAllAllow(List<String> refuels){


        //获取被拒绝的权限的个数
        if (refuels.size()>0){

            AlertDialog.Builder permissionDialog=new AlertDialog.Builder(mActivity);
            AlertDialog defaultDialog = permissionDialog.create();
            AlertDialog customDialog = permissionDialog.create();
            defaultDialog.setTitle("您拒绝了如下权限：");
            StringBuffer stringBuffer=new StringBuffer();
            for (int i = 0; i < refuels.size(); i++) {
                for (String PERMISSION : PERMISSIONS) {
                    if (refuels.get(i).equals(PERMISSION)) {
                        stringBuffer.append(refuels.get(i) + "\n");
                    }
                }
            }

            stringBuffer.append("请点击\"设置\"-\"权限\"-打开所需权限。\n最后点击返回按钮即可返回程序。");
            defaultDialog.setMessage(stringBuffer);
            defaultDialog.setButton(DialogInterface.BUTTON1, "设置", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    startAppSettings();

                }
            });
            defaultDialog.setButton(DialogInterface.BUTTON3, "退出", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(mActivity.getApplicationContext(), "您拒绝了所需权限，程序将要退出。", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    mActivity.finish();
                }
            });
            if (mCallBack!=null){
                mCallBack.error(defaultDialog,customDialog,refuels.toArray(new String[refuels.size()]));
            }

        }else {
            if (mCallBack!=null){
                mCallBack.allAllow();
            }
        }
    }

    /**
     * 启动当前APP的设置程序
     */
    private void startAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse(PACKAGE_URL_SCHEME + mActivity.getPackageName()));
        mActivity.startActivity(intent);
    }



    public interface CallBack{
        void allAllow();
        void error(AlertDialog defaultDialog,AlertDialog customDialog,String ...args);
    }


}
