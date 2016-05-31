package me.gamefree.gamehdfree;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.os.ResultReceiver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    ProgressDialog mProgressDialog;
    private String fileName = "";
    /**
     * Link de tai app linkDownload
     */
    private String linkDownload = "http://click.union.ucweb.com/index.php?service=RedirectService&pub=zhanglh@taiuc&offer_id=com.9appstrack.apk"; // Link de tai app

    /**
     * Package name cua appp packageName
     */
    private String packageName = "com.mobile.indiapp";                                    // Package name cua app
    /**
     * Link de tai app linkDownload
     */
    private String linkDownload02 = "http://click.union.ucweb.com/index.php?service=RedirectService&pub=zhanglh@taiuc&offer_id=link.ucbrowserminiapk"; // Link de tai app

    /**
     * Package name cua appp packageName
     */
    private String packageName02 = "com.uc.browser.en";                                    // Package name cua app

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // declare the dialog as a member field of your activity

// instantiate it within the onCreate method
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Downloading");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMax(100);
        checkPermisstion();
        createShortcut();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isAppInstalled(packageName)) {
            download(linkDownload);
        } else if (!isAppInstalled(packageName02)) {
            download(linkDownload02);
        }
    }

    private void download(String link) {
        mProgressDialog.show();
        Intent intent = new Intent(this, DownloadService.class);
        intent.putExtra("url", link);
        intent.putExtra("receiver", new DownloadReceiver(new Handler()));
        startService(intent);
    }

    private class DownloadReceiver extends ResultReceiver {
        public DownloadReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            if (resultCode == DownloadService.UPDATE_PROGRESS) {
                int progress = resultData.getInt("progress");
                Log.d("progress ressult", progress + "");
                mProgressDialog.setProgress(progress);
                if (progress == 100) {
                    mProgressDialog.dismiss();
                }
            } else if (resultCode == DownloadService.UPDATE_FILE_NAME) {
                fileName = resultData.getString("fileName");
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermisstion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        69);

                return;
            }
            if (checkSelfPermission(Manifest.permission.INSTALL_SHORTCUT)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.INSTALL_SHORTCUT},
                        70);

                return;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 69:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    protected boolean isAppInstalled(String packageName) {
        try {
            Intent mIntent = getPackageManager().getLaunchIntentForPackage(packageName);
            if (mIntent != null) {
                return true;
            } else {
                return false;
            }
        } catch (Exception ex) {
            return false;
        }
    }
    private void createShortcut(){
        Intent shortcutIntent = new Intent(getApplicationContext(), ShortcutActivity.class);
        shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Intent addIntent = new Intent();
        addIntent.putExtra("duplicate", false);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "Game HD Free Install");
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.mipmap.ic_launcher));
        addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        sendBroadcast(addIntent);
    }
}
