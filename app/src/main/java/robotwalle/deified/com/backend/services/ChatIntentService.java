package robotwalle.deified.com.backend.services;

import android.app.AlertDialog;
import android.app.IntentService;
import android.content.Intent;
import android.content.res.AssetManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import robot6.com.deified.enviroment.advertizement.utiles.Utilities;
import robotwalle.deified.com.MainActivity;
import robotwalle.deified.com.Utilities.FileHelper;
import robotwalle.deified.com.Utilities.QRCode;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class ChatIntentService extends IntentService {

    public static String QQ_PLUGIN_PATH;
    public static String QQ_WORK_PATH;
    public static final String QQ_STATUS = "QQSTATUS";
    public static final int QQ_STATUS_SCAN_QRCODE = 1;


    public ChatIntentService() {
        super("ChatIntentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init() {
        QQ_PLUGIN_PATH = getFilesDir() + "/qqplugins";
        QQ_WORK_PATH = getFilesDir() + "/qqwork";
        Utilities.createDir(QQ_PLUGIN_PATH);
        copyAssetsToDir("source/chatBot.py", QQ_PLUGIN_PATH);
        //Utilities.copyFile();
    }

    private void sendServiceStatus(String action, int status, String info) {
        Intent intent = new Intent(action);
        intent.putExtra("status", status);
        intent.putExtra("info", info);
        sendBroadcast(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            loginQQ();
        }
    }

    private void loginQQ() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 先删除不必要的过期的qrcode
                List<String> deletePngList = FileHelper.getAllFiles(QQ_WORK_PATH,"png");
                if (deletePngList != null && deletePngList.size() != 0) {
                    for (String name: deletePngList) {
                        Utilities.delete(name);
                    }
                }

                while (true) {
                    final List<String> pngList = FileHelper.getAllFiles(QQ_WORK_PATH,"png");
                    if (pngList != null && pngList.size() != 0) {
                        Log.e("list", pngList.toString());

                        sendServiceStatus(QQ_STATUS, QQ_STATUS_SCAN_QRCODE, pngList.get(0));
                    }
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Python py = Python.getInstance();
                PyObject test = py.getModule("test");
                PyObject builtins = py.getBuiltins();
                PyObject args = builtins.callAttr("list");

                args.callAttr("append", "-b");
                args.callAttr("append", QQ_WORK_PATH);

                args.callAttr("append", "-pp");
                args.callAttr("append", QQ_PLUGIN_PATH);

                args.callAttr("append", "-pl");
                args.callAttr("append", "chatBot");

                test.callAttr("run", args);
            }
        }).start();
    }

    private void copyAssetsToDir(String asset, String dir) {
        AssetManager assetManager = getAssets();
        String[] splitFiles = asset.split("/");
        String fileName = "xxx";
        if (splitFiles.length > 1) {
            fileName = splitFiles[splitFiles.length-1];
        } else {
            fileName = asset;
        }
        try {
            InputStream in = assetManager.open(asset);
            OutputStream out = new FileOutputStream(dir + "/" + fileName);
            byte[] buffer = new byte[1024];
            int read = in.read(buffer);
            while (read != -1) {
                out.write(buffer, 0, read);
                read = in.read(buffer);
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }
}
