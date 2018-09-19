package robotwalle.deified.com.Utilities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Hashtable;

import robot6.com.deified.enviroment.advertizement.view.AdDialogHelper;
import robot6.com.deified.enviroment.advertizement.view.AdHelper;
import robotwalle.deified.com.R;

/**
 * Created by sunorr on 2017/3/2.
 */

public class QRCode {

    private static final int FOREGROUND = 0xff000000;
    private static final int BACKGROUND = 0xffffffff;

    public static Bitmap createNoLogoQRCode(String context, int widthPix, int heightPix) {
        Bitmap bitmap = null;
        try {
            Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            BitMatrix bitMatrix = new MultiFormatWriter().encode(context, BarcodeFormat.QR_CODE, widthPix, heightPix, hints);
            int[] pixels = new int[widthPix * heightPix];
            for (int y = 0; y < heightPix; y++) {
                for (int x = 0; x < widthPix; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * widthPix + x] = FOREGROUND;
                    } else {
                        pixels[y * widthPix + x] = BACKGROUND;
                    }
                }
            }
            bitmap = Bitmap.createBitmap(widthPix, heightPix, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, widthPix, 0, 0, widthPix, heightPix);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }
    public static boolean createNoLogoQRCode(String context, int widthPix, int heightPix, String path) {
        Bitmap bitmap = createNoLogoQRCode(context, widthPix, heightPix);
        if (bitmap != null) {
            try {
                return bitmap.compress(Bitmap.CompressFormat.JPEG, 30, new FileOutputStream(path));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    public static AlertDialog showQRCode(final Context context, final String codeImagePath) {
        View inflateView = new View(context);
        android.app.AlertDialog.Builder builder =
                new AdHelper(context).adDialogBuilder(R.layout.dialog_scancode, inflateView);
        builder.setTitle("登录二维码");
        builder.setPositiveButton("分享", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("image/*");
                File file  = new File(codeImagePath);
                Uri uri = null;
                if (Build.VERSION.SDK_INT < 24) {
                    uri = Uri.fromFile(new File(codeImagePath));
                } else {
                    uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
                }
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                context.startActivity(shareIntent);
            }
        });
        android.app.AlertDialog dialog = builder.create();
        dialog.show();

        ImageView imageView = (ImageView) dialog.findViewById(R.id.image_scan_code);
        if (imageView != null) {
            imageView.setImageBitmap(BitmapFactory.decodeFile(codeImagePath));
        }

        return dialog;
    }
}
