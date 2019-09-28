package utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;

import java.io.IOException;

import br.com.infobella.whatomail.whatomail.modelo.WhatomailApplication;


/**
 * Created by Henrique on 10/07/2016.
 */
public class ImageUtils {

    public static Bitmap getResizedImage(Uri uriFile, int width, int height, boolean fixMatrix) {
        try {
            // Configura o BitmapFactory para apenas ler o tamanho da imagem (sem carregá-la em memória)
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;

            // Faz o decode da imagem
            BitmapFactory.decodeFile(uriFile.getPath(), opts);
            // Lê a largura e altura do arquivo
            int w = opts.outWidth;
            int h = opts.outHeight;

            if (width == 0 || height == 0) {
                width = w / 2;
                height = h / 2;
            }

            //Log.d(LogUtils.TAG_GERAL, "Resize img, w:" + w + " / h:" + h + ", to w:" + width + " / h:" + height);

            // Fator de escala
            int scaleFactor = Math.min(w / width, h / height);
            opts.inSampleSize = scaleFactor;
            //Log.d(LogUtils.TAG_GERAL, "inSampleSize:" + opts.inSampleSize);
            // Agora deixa carregar o bitmap completo
            opts.inJustDecodeBounds = false;

            Bitmap bitmap = BitmapFactory.decodeFile(uriFile.getPath(), opts);

            //Log.d(LogUtils.TAG_GERAL, "Resize OK, w:" + bitmap.getWidth() + " / h:" + bitmap.getHeight() + "isRecicled: " + bitmap.isRecycled());

            if (fixMatrix) {
                Bitmap newBitmap = fixMatrix(uriFile, bitmap);
                //Log.v(LogUtils.TAG_GERAL, "Resize - newBitmap is Recicled: " + newBitmap.isRecycled());
                //Log.v(LogUtils.TAG_GERAL, "Resize - bitmap is Recicled: " + bitmap.isRecycled());
                //bitmap.recycle();

                return newBitmap;
            } else {
                return bitmap;
            }

        } catch (RuntimeException ex) {
            LogUtils.writeLog(WhatomailApplication.getContext(),LogUtils.TAG_IMAGE, ex.toString());
        } catch (IOException ex) {
            LogUtils.writeLog(WhatomailApplication.getContext(),LogUtils.TAG_IMAGE, ex.toString());
        }
        return null;
    }

    private static Bitmap fixMatrix(Uri uriFile, Bitmap bitmapOld) throws IOException {
        Matrix matrix = new Matrix();

        /**
         * Classe para ler tags escritas no JPEG
         * Para utilizar esta classe precisa de Android 2.2 ou superior
         */
        ExifInterface exif = new ExifInterface(uriFile.getPath());

        // Lê a orientação que foi salva a foto
        int orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL);

        boolean fix = false;

        // Rotate bitmap
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.postRotate(180);
                fix = true;
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.postRotate(90);
                fix = true;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.postRotate(270);
                fix = true;
                break;
            default:
                // ORIENTATION_ROTATE_0
                fix = false;
                break;
        }

        if (!fix) {
            //Log.v(LogUtils.TAG_GERAL, "FixMatrix - bitmap is Recicled: " + bitmapOld.isRecycled());
            return bitmapOld;
        }

        // Corrige a orientação (passa a matrix)
        Bitmap newBitmapFix = Bitmap.createBitmap(bitmapOld, 0, 0, bitmapOld.getWidth(), bitmapOld.getHeight(), matrix, true);

        //bitmapOld.recycle();
        //Log.v(LogUtils.TAG_GERAL, "Fix Matrix - newBitmap is Recicled: " + newBitmapFix.isRecycled());

        return newBitmapFix;
    }
}
