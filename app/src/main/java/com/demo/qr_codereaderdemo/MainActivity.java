package com.demo.qr_codereaderdemo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.qrcode.Constant;
import com.example.qrcode.ScannerActivity;
import com.google.zxing.BarcodeFormat;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private final int REQUEST_PERMISION_CODE_CAMARE = 0;
    private final int RESULT_REQUEST_CODE = 1;
    private static final String TAG = "MainActivity";
    private Button mScanner;
    private TextView tx;
    private int h;

    private HashMap<String, Set> mHashMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mScanner = (Button) findViewById(R.id.btn);
        tx = findViewById(R.id.textView);
        h = px2dip(this, getScreenHeight(this));
        mScanner.setOnClickListener(mScannerListener);

        Set<BarcodeFormat> codeFormats = EnumSet.of(BarcodeFormat.QR_CODE
                , BarcodeFormat.CODE_128
                , BarcodeFormat.CODE_93
                , BarcodeFormat.CODE_39
                , BarcodeFormat.AZTEC
                , BarcodeFormat.CODABAR
                , BarcodeFormat.DATA_MATRIX
                , BarcodeFormat.EAN_8
                , BarcodeFormat.EAN_13
                , BarcodeFormat.ITF
                , BarcodeFormat.MAXICODE
                , BarcodeFormat.RSS_14
                , BarcodeFormat.RSS_EXPANDED
                , BarcodeFormat.UPC_A
                , BarcodeFormat.UPC_E
                , BarcodeFormat.UPC_EAN_EXTENSION
                , BarcodeFormat.PDF_417);
        mHashMap.put(ScannerActivity.BARCODE_FORMAT, codeFormats);
    }

    private View.OnClickListener mScannerListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                goScanner();
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISION_CODE_CAMARE);
            }
        }
    };

    private void goScanner() {
        Intent intent = new Intent(this, ScannerActivity.class);
        //这里可以用intent传递一些参数，比如扫码聚焦框尺寸大小，支持的扫码类型。
        //设置扫码框的宽
        intent.putExtra(Constant.EXTRA_SCANNER_FRAME_WIDTH, 600);
        //设置扫码框的高
        intent.putExtra(Constant.EXTRA_SCANNER_FRAME_HEIGHT, 600);
        //设置扫码框距顶部的位置
        intent.putExtra(Constant.EXTRA_SCANNER_FRAME_TOP_PADDING, h / 2);
        Bundle bundle = new Bundle();
        //设置支持的扫码类型
        bundle.putSerializable(Constant.EXTRA_SCAN_CODE_TYPE, mHashMap);
        intent.putExtras(bundle);
        startActivityForResult(intent, RESULT_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISION_CODE_CAMARE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    goScanner();
                } else {

                }
                return;
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RESULT_REQUEST_CODE:
                    if (data == null) {
                        return;
                    }
                    String type = data.getStringExtra(Constant.EXTRA_RESULT_CODE_TYPE);
                    String content = data.getStringExtra(Constant.EXTRA_RESULT_CONTENT);
                    tx.setText("CODE_TYPE:  " + type + "\n" + "CONTENT:  " + content);
//                    Toast.makeText(MainActivity.this,"codeType:" + type
//                            + "-----content:" + content,Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * 获得屏幕宽度
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    /**
     * 获得屏幕高度
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
