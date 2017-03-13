package pw.androidthanatos.checkpermission;

import android.Manifest;
import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import pw.androidthanatos.library.IPermission;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static String[] PERMISSIONS=new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    private IPermission iPermission;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.w(TAG, "onCreate: " );
        iPermission=IPermission.Builder.build(this).addPermissions(PERMISSIONS).startCheck().onNext(new IPermission.CallBack() {


            @Override
            public void allAllow() {
                Toast.makeText(MainActivity.this, "恭喜你，成功入坑", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void error(AlertDialog defaultDialog, AlertDialog customDialog, String... args) {
                defaultDialog.show();
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        iPermission.checkResult(requestCode, permissions, grantResults);

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.w(TAG, "onStart: " );

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        iPermission.reCheck();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.w(TAG, "onResume: " );

    }

}
