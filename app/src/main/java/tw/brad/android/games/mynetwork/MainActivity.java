package tw.brad.android.games.mynetwork;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private ImageView img;
    private Bitmap bmp;
    private UIHander hander;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hander = new UIHander();
        img = (ImageView)findViewById(R.id.img);
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

    private class UIHander extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            img.setImageBitmap(bmp);
        }
    }

}
