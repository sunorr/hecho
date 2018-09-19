package robotwalle.deified.com;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import robot6.com.deified.enviroment.advertizement.utiles.Utilities;
import robot6.com.deified.enviroment.advertizement.view.AdDialogHelper;
import robotwalle.deified.com.Utilities.FileHelper;
import robotwalle.deified.com.Utilities.QRCode;
import robotwalle.deified.com.backend.services.ChatIntentService;

public class MainActivity extends AppCompatActivity {

    private Button mQQloginButton;
    private AlertDialog mLoadingDialog;
    private AdDialogHelper mAdDialogHelper;
    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        //TextView tv = (TextView) findViewById(R.id.sample_text);
        //tv.setText(stringFromJNI());

        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }

        register();
        //QRCode.showQRCode(MainActivity.this, "/data/user/0/robotwalle.deified.com/files/tmp/09294d5a13af47fbab6064e620081bae.png");
        //new AdDialogHelper(this).showErrorDialog("");
        mAdDialogHelper = new AdDialogHelper(this);

        mQQloginButton = findViewById(R.id.qqLoginButton);
        mQQloginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.cancel();
                }
                mLoadingDialog = mAdDialogHelper.showLoadingDialog(10, null);
                startService(new Intent(getApplicationContext(), ChatIntentService.class));
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregister();
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    private ChatServiceBroadCastReceiver mChatServiceBroadCastReceiver;
    private void register() {
        mChatServiceBroadCastReceiver = new ChatServiceBroadCastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ChatIntentService.QQ_STATUS);
        registerReceiver(mChatServiceBroadCastReceiver, filter);
    }

    private void unregister() {
        unregisterReceiver(mChatServiceBroadCastReceiver);
    }

    public class ChatServiceBroadCastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case ChatIntentService.QQ_STATUS:
                    handleQQStatus(context, intent);
                    break;
            }
        }
    }

    private AlertDialog mShowQrCodeDialog;
    private void handleQQStatus(Context context, Intent intent) {
        switch (intent.getIntExtra("status", 0)) {
            case ChatIntentService.QQ_STATUS_SCAN_QRCODE:
                if (mShowQrCodeDialog != null && mShowQrCodeDialog.isShowing()) {
                    mShowQrCodeDialog.cancel();
                }

                mAdDialogHelper.cancelLoadingDialog(mLoadingDialog);
                String png = intent.getStringExtra("info");
                mShowQrCodeDialog = QRCode.showQRCode(MainActivity.this, png);
                Utilities.delete(png);
                break;
            default:
                break;
        }
    }
}
