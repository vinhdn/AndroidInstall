package me.gamefree.gamehdfree;

import android.app.IntentService;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.os.ResultReceiver;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by vinh on 5/28/16.
 */
public class DownloadService extends IntentService {
    public static final int UPDATE_PROGRESS = 8344;
    public static final int UPDATE_FILE_NAME = 8345;

    public DownloadService() {
        super("DownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String urlToDownload = intent.getStringExtra("url");
        ResultReceiver receiver = intent.getParcelableExtra("receiver");
        try {
            URL url = new URL(urlToDownload);
            URLConnection connection = url.openConnection();
            connection.connect();
            // this will be useful so that you can show a typical 0-100% progress bar
            int fileLength = connection.getContentLength();

            // download the file
            InputStream input = new BufferedInputStream(connection.getInputStream());
            String fileName = Environment.getExternalStorageDirectory().getPath() + "/" + System.currentTimeMillis() / 1000 + "-install.apk";
            Bundle resultFile = new Bundle();
            resultFile.putString("fileName", fileName);
            receiver.send(UPDATE_FILE_NAME, resultFile);
            OutputStream output = new FileOutputStream(fileName);

            byte data[] = new byte[1024];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                total += count;
                // publishing the progress....
                Bundle resultData = new Bundle();
                resultData.putInt("progress", (int) (total * 100 / fileLength));
                Log.d("progress", "" + total);
                receiver.send(UPDATE_PROGRESS, resultData);
                output.write(data, 0, count);
            }

            output.flush();
            output.close();
            input.close();
            openFileApk(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Bundle resultData = new Bundle();
        resultData.putInt("progress", 100);
        receiver.send(UPDATE_PROGRESS, resultData);
    }

    private void openFileApk(String fileName){
        Intent newIntent = new Intent(Intent.ACTION_VIEW);
        newIntent.setDataAndType(Uri.fromFile(new File(fileName)), "application/vnd.android.package-archive");
        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(newIntent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "No handler for this type of file.", Toast.LENGTH_LONG).show();
        }
    }

}
