package com.example.spamdelete;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks,MessageListener  {
    private static final int RC_PERM_SMS = 125;
    private static final int RC_SETTINGS = 126;

    private String[] wantedPerm = {Manifest.permission.SEND_SMS,
            Manifest.permission.RECEIVE_SMS};

    private Activity activity = MainActivity.this;
    private String TAG = MainActivity.class.getSimpleName();
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         tv = findViewById(R.id.tv);

         {
            tv.setText("You can send SMS!");
            if (EasyPermissions.hasPermissions(activity, wantedPerm)) {
                Log.d(TAG, "Already have permission, do the thing");

                //Register sms listener
                MessageReceiver.bindListener(this);
            } else {
                EasyPermissions.requestPermissions(activity, "This app needs access to send SMS.", RC_PERM_SMS, wantedPerm);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, activity);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Log.d(TAG, "onPermissionsGranted:" + requestCode + ":" + perms.size());
        //Register sms listener
        MessageReceiver.bindListener(this);
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.d(TAG, "onPermissionsDenied:" + requestCode + ":" + perms.size());
        if (EasyPermissions.somePermissionPermanentlyDenied(activity, perms)) {
            new AppSettingsDialog.Builder(activity)
                    .setTitle("Permissions Required")
                    .setPositiveButton("Settings")
                    .setNegativeButton("Cancel")
                    .setRequestCode(RC_SETTINGS)
                    .build()
                    .show();
        }
    }

    @AfterPermissionGranted(RC_PERM_SMS)
    private void methodRequireLocationPermission() {
        if (EasyPermissions.hasPermissions(this, wantedPerm)) {
            Log.d(TAG, "Already have permission, do the thing");
        } else {
            Log.d(TAG, "Do not have permission, request them now");
            EasyPermissions.requestPermissions(activity, "This app needs access to send SMS.", RC_PERM_SMS, wantedPerm);
        }
    }

    @Override
    public void messageReceived(String message) {
        Toast.makeText(this, "New Message Received: " + message, Toast.LENGTH_SHORT).show();
        tv.setText(" " +message);
    }

}