package com.saif.streamless;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity {

    WebView webView;
    private ValueCallback<Uri[]> file_uri;
    private final static int FILE_CHOOSER_RESULT_CODE = 1;
    private static final int AUDIO_CAPTURE_CODE = 2 ;
    private static final int CAMERA_CAPTURE_CODE = 3 ;
    private PermissionRequest my_request;
    
    public static final String USER_AGENT = "Mozilla/5.0 (Linux; Android 4.1.1; Galaxy Nexus Build/JRO03C) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166 Mobile Safari/535.19";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        webView = findViewById(R.id.WebView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setSupportMultipleWindows(true);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setDomStorageEnabled(true);
        webSettings.setUserAgentString(USER_AGENT);
        webView.loadUrl(getIntent().getStringExtra("URL"));

        webView.setWebChromeClient(new WebChromeClient(){

            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                if (file_uri != null) {
                    file_uri.onReceiveValue(null);
                    file_uri = null;
                }
                file_uri = filePathCallback;

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                startActivityForResult(Intent.createChooser(intent,"File Chooser"),FILE_CHOOSER_RESULT_CODE);
                return true;
            }

            @Override
            public void onPermissionRequest(PermissionRequest request) {
                super.onPermissionRequest(request);
                my_request = request;
                for(String permission: my_request.getResources()){
                    switch (permission){
                        case "android.webkit.resource.AUDIO_CAPTURE":{
                            askPermission(my_request.getOrigin().toString(), Manifest.permission.RECORD_AUDIO,AUDIO_CAPTURE_CODE);
                            break;
                        }

                        case "android.webkit.resource.VIDEO_CAPTURE":{
                            askPermission(my_request.getOrigin().toString(), Manifest.permission.CAMERA,CAMERA_CAPTURE_CODE);
                            break;
                        }
                    }
                }

            }
        });
    }

    private void askPermission(String origin, String permission, int requestCode) {
        Log.d("WebView", "inside askForPermission for" + origin + "with" + permission);
        if (ContextCompat.checkSelfPermission(getApplicationContext(), permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(HomeActivity.this, new String[]{permission}, requestCode);
        }
        else {
            my_request.grant(my_request.getResources());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case AUDIO_CAPTURE_CODE: {
                Log.d("WebView", "PERMISSION FOR AUDIO");
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    my_request.grant(my_request.getResources());

                } else {
                    Toast.makeText(HomeActivity.this,"AUDIO permission denied",Toast.LENGTH_SHORT).show();
                }
            }

            case CAMERA_CAPTURE_CODE: {
                Log.d("WebView", "PERMISSION FOR CAMERA");
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    my_request.grant(my_request.getResources());

                } else {
                    Toast.makeText(HomeActivity.this,"CAMERA permission denied",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if(requestCode == FILE_CHOOSER_RESULT_CODE){
            if(null == file_uri || intent == null || resultCode != MainActivity.RESULT_OK){
                return;
            }

            Uri[] result = null;
            String dataString = intent.getDataString();
            if(dataString != null){
                result = new Uri[]{Uri.parse(dataString)};
            }

            file_uri.onReceiveValue(result);
            file_uri = null;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if(webView.canGoBack()){
            webView.goBack();
        }
        else{
            super.onBackPressed();
        }

    }

}
