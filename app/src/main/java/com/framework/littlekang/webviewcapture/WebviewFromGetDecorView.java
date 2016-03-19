package com.framework.littlekang.webviewcapture;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import java.io.FileOutputStream;

/**
 * Created by heshaokang on 2016/3/18.
 */
public class WebviewFromGetDecorView extends AppCompatActivity{

    private static final String TAG = "WebviewFromGetDecorView";
    private Button btn_capture;
    private WebView webView;
    private Bitmap bitmap;
    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 200;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkSdkVersion();
        setContentView(R.layout.activity_webview_capture);

        requestPermission();

        btn_capture = (Button) findViewById(R.id.btn_capture);
        webView = (WebView) findViewById(R.id.wv_webview);
        initWebview();
        btn_capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSnapshot();
            }
        });
    }

    /**
     * 当系统版本大于5.0时 开启enableSlowWholeDocumentDraw 获取整个html文档内容
     */
    private void checkSdkVersion() {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
            WebView.enableSlowWholeDocumentDraw();
        }
    }

    /**
     * 当build target为23时，需要动态申请权限
     */
    private void requestPermission() {
        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.LOLLIPOP_MR1) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                //申请WRITE_EXTERNAL_STORAGE权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch(requestCode){
            case 200:
                boolean writeAccepted = grantResults[0]== PackageManager.PERMISSION_GRANTED;
                Log.d(TAG,"writeAcceped--"+writeAccepted);
                break;

        }
    }

    private void initWebview() {

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(true); //支持缩放
        webView.requestFocusFromTouch();
        webView.setDrawingCacheEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }


        });
        webView.loadUrl("http://www.baidu.com");

    }

    private void getSnapshot() {


        View view  = WebviewFromGetDecorView.this.getWindow().getDecorView();
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        Log.d(TAG,"bitmap--"+bitmap);
        try {
            String fileName = Environment.getExternalStorageDirectory().getPath()+"/webview_capture4.jpg";
            FileOutputStream fos = new FileOutputStream(fileName);
            //压缩bitmap到输出流中
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, fos);
            fos.close();
            Toast.makeText(WebviewFromGetDecorView.this, "截屏成功", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }finally {
            if(bitmap!=null) {
                bitmap.recycle();
            }

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if((keyCode==KeyEvent.KEYCODE_BACK)&&webView.canGoBack()) {
            webView.goBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
