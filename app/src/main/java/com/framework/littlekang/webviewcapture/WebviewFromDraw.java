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
public class WebviewFromDraw extends AppCompatActivity{

    private static final String TAG = "WebviewFromDraw";
    private Button btn_capture;
    private WebView webView;
    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 200;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //检查版本
        checkSdkVersion();
        setContentView(R.layout.activity_webview_capture);
        //申请权限
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

    /**
     * 申请权限的回调
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch(requestCode){
            case 200:
                boolean writeAccepted = grantResults[0]== PackageManager.PERMISSION_GRANTED;
                Log.d(TAG,"writeAcceped--"+writeAccepted);
                break;

        }
    }

    /**
     * 设置webview
     */
    private void initWebview() {

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(true); //支持缩放
        webView.requestFocusFromTouch();
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }


        });
        webView.loadUrl("http://www.baidu.com");

    }

    /**
     * 获得快照
     */
    private void getSnapshot() {

            float scale = webView.getScale();
            int webViewHeight = (int) (webView.getContentHeight()*scale+0.5);
            Bitmap bitmap = Bitmap.createBitmap(webView.getWidth(),webViewHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            webView.draw(canvas);
            try {
                String fileName = Environment.getExternalStorageDirectory().getPath()+"/webview_capture2.jpg";
                FileOutputStream fos = new FileOutputStream(fileName);
                //压缩bitmap到输出流中
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, fos);
                fos.close();
                Toast.makeText(WebviewFromDraw.this, "截屏成功", Toast.LENGTH_LONG).show();
                bitmap.recycle();
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }


    }

    /**
     * 重写返回键事件 可以使webview回退
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if((keyCode==KeyEvent.KEYCODE_BACK)&&webView.canGoBack()) {
            webView.goBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
