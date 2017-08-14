package tw.brad.android.games.mynetwork;

import android.Manifest;
import android.app.ProgressDialog;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ImageView img;
    private Bitmap bmp;
    private UIHander hander;
    private boolean isPermissionOK;
    private File sdroot, savePDF;
    private ProgressDialog progressDialog;

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

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setTitle("Downloading...");


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
        progressDialog.show();
        new Thread(){
            @Override
            public void run() {
                getWebPDF("http://pdfmyurl.com/?url=http://www.gamer.com.tw");

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
            hander.sendEmptyMessage(1);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void test4(View view){
        new Thread(){
            @Override
            public void run() {
                super.run();
                getJSONString("http://opendata2.epa.gov.tw/AQX.json");
            }
        }.start();
    }

    private void getJSONString(String urlString){
        try{
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.connect();
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String jsonString = br.readLine();
            Log.i("brad", jsonString);
            parseJSONString(jsonString);

        }catch(Exception e){
            Log.i("brad", e.toString());
        }
    }

    private void parseJSONString(String json){
        try {
            JSONArray root = new JSONArray(json);
            for (int i=0; i<root.length(); i++){
                JSONObject row = root.getJSONObject(i);
                String country = row.getString("County");
                String sitename = row.getString("SiteName");
                String pm25 = row.getString("PM2.5");
                Log.i("brad", country + ":" + sitename + ":" + pm25);
            }


        } catch (JSONException e) {
            Log.i("brad", e.toString());
        }
    }

    public void test5(View view){
        new Thread(){
            @Override
            public void run() {
                try {
                    URL url = new URL("http://10.0.1.1/brad01.php?cname=tony&tel=0919123456&birthday=2000-02-09");
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    conn.connect();
                    conn.getInputStream();

                }catch (Exception e){
                    Log.i("brad", e.toString());
                }
            }
        }.start();
    }

    public void test6(View view){
        new Thread(){
            @Override
            public void run() {
                try {
                    MultipartUtility mu = new MultipartUtility(
                            "http://10.0.1.1/brad02.php", "UTF-8");
                    mu.addFormField("data1", "1111");
                    mu.addFormField("data2", "2222");
                    List<String> ret = mu.finish();

                    for (String line : ret){
                        Log.i("brad", line);
                    }

                } catch (IOException e) {
                    Log.i("brad", e.toString());
                }

            }
        }.start();
    }

    private class UIHander extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what){
                case 0: img.setImageBitmap(bmp); break;
                case 1:
                    progressDialog.dismiss();
                    break;
            }

        }
    }

}
