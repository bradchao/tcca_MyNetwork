package tw.brad.android.games.mynetwork;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private ImageView img;
    private Bitmap bmp;
    private UIHander hander;
    private boolean isPermissionOK;
    private File sdroot, savePDF;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hander = new UIHander();
        img = (ImageView)findViewById(R.id.img);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            // no
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    123);
        }else {
            isPermissionOK = true;
            init();
        }

    }

    private void init(){
        if (!isPermissionOK) {
            finish();
        }else{
            go();
        }
    }

    private void go(){
        sdroot = Environment.getExternalStorageDirectory();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 123){
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                isPermissionOK = true;

            }
            init();
        }
    }

    public void test1(View view){
        new Thread(){
            @Override
            public void run() {
                try {
                    URL url = new URL("http://www.tcca.org.tw");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.connect();
                    InputStream in = conn.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));
                    String line;
                    while ((line = br.readLine()) != null){
                        Log.i("brad", line);
                    }
                    in.close();


                }catch(Exception e){
                    Log.i("brad", e.toString());
                }
            }
        }.start();


    }
    public void test2(View view){
        new Thread(){
            @Override
            public void run() {
                try {
                    URL url = new URL("http://www.tcca.org.tw/img/t_03.jpg");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.connect();
                    InputStream in = conn.getInputStream();

                    bmp = BitmapFactory.decodeStream(in);
                    hander.sendEmptyMessage(0);
                }catch(Exception e){
                    Log.i("brad", e.toString());
                }
            }
        }.start();


    }

    public void test3(View view){
        new Thread(){
            @Override
            public void run() {
                getWebPDF("http://www.gamer.com.tw");

            }
        }.start();
    }

    private void getWebPDF(String urlString){
        try {

            savePDF = new File(sdroot, "myweb.pdf");
            FileOutputStream fout = new FileOutputStream(savePDF);

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.connect();
            InputStream in = conn.getInputStream();
            byte[] buf = new byte[4096]; int len = 0;
            while ( (len = in.read(buf)) != -1){
                fout.write(buf, 0, len);
            }

            fout.flush();
            fout.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    private class UIHander extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            img.setImageBitmap(bmp);
        }
    }

}
