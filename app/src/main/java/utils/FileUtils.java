package utils;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/*
 * Created by Henrique on 05/07/2016.
 */
public class FileUtils {

    private static final String pathWhatoMailImg = "Whatomail";
    private static final String pathWhatoMail =  "/Whatomail";
    private static final String pathLog = "/Whatomail/Log";
    //
    private static final String pathGif = "/WhatsApp Business/Media/WhatsApp Business Animated Gifs";
    private static final String pathDocuments = "/WhatsApp Business/Media/WhatsApp Business Documents";
    private static final String pathImage = "/WhatsApp Business/Media/WhatsApp Images";
    private static final String pathImageBusines = "/WhatsApp Business/Media/WhatsApp Business Images";
    private static final String pathVideo = "/WhatsApp Business/Media/WhatsApp Business Video";
    private static final String pathAudio = "/WhatsApp Business/Media/WhatsApp Business Audio";
    private static final String pathVoiceNotes = "/WhatsApp Business/Media/WhatsApp Business Voice Notes";

    private static final int RELATIVE = 0;
    private static final int ABSOLUTE = 1;

    private static int quantFile;

    public static void criarPastas(){
        File sdcardDir = android.os.Environment.getExternalStorageDirectory();
        File fileRaizApp = new File(sdcardDir.getAbsoluteFile() + pathWhatoMail);
        if (!fileRaizApp.exists()) {
            fileRaizApp.mkdir();
        }
        File fileLog = new File(sdcardDir.getAbsoluteFile() + pathLog);
        if (!fileLog.exists()) {
            fileLog.mkdir();
        }
    }

    public static void copyFile(File fonte, File destino) {
        if (fonte.exists()) {
            if (destino.exists()){
                destino.delete();
            }
            OutputStream out = null;
            InputStream in = null;
            try {
                in = new FileInputStream(fonte);
                out = new FileOutputStream(destino);
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                if (out != null) {
                    try {
                        out.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (in != null) {
                    try {
                        in.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    public static File getFileLog(){
        File sdcardDir = android.os.Environment.getExternalStorageDirectory();
        return new File(sdcardDir.getAbsoluteFile() + pathLog + "/"+FormatterUtils.dateToStringFileLog()+".txt");
    }

    public static File getFileDados(){
        File sdcardDir = android.os.Environment.getExternalStorageDirectory();
        return new File(sdcardDir.getAbsoluteFile() + pathLog + "/dados.txt");
    }

    public static String getNameLastFileImage(int pos){
        String file =  getNameLastFileBase(pos, pathImage, RELATIVE);
        while (file.toUpperCase().contains("JPEG")){
            pos--;
            file =  getNameLastFileBase(pos, pathImage, RELATIVE);
        }
        return file;
    }

    public static String getNameLastFileAnimatedGifs(int pos) {
        return getNameLastFileBase(pos, pathGif, RELATIVE);
    }

    public static String getNameLastFileAudio(int pos){
        return getNameLastFileBase(pos, pathAudio, RELATIVE);
    }

    public static String getNameLastFileDocuments(int pos){
        return getNameLastFileBase(pos, pathDocuments, RELATIVE);
    }

    public static String getNameLastFileVideo(int pos){
        return getNameLastFileBase(pos, pathVideo, RELATIVE);
    }

    public static String getNameLastFileVoiceNotes(int pos){
        String lastDirectory = getNameLastFileBase(pathVoiceNotes);
        if (lastDirectory.equals(KeyUtils.NO_FILE)){
            return KeyUtils.NO_FILE;
        } else {
            int posDirectory = 1;
            String file = getNameLastFileBase(pos, lastDirectory, ABSOLUTE);
            while ((file.equals(KeyUtils.NO_FILE)) && (!lastDirectory.equals(KeyUtils.NO_FILE))){
                pos = pos - quantFile;
                lastDirectory = getNameLastFileBase(posDirectory, pathVoiceNotes, RELATIVE);
                if (pos < 0){
                    file = KeyUtils.NO_FILE;
                } else {
                    file = getNameLastFileBase(pos, lastDirectory, ABSOLUTE);
                }
                posDirectory++;
            }
            return file;
        }
    }

    private static String getNameLastFileBase(int pos, String path, int typeDirectory){
        quantFile = 0;
        File sdcardDir = android.os.Environment.getExternalStorageDirectory();
        File diretorio = new File(sdcardDir.getAbsoluteFile() + path);
        if (typeDirectory == ABSOLUTE) {
            diretorio = new File(path);
        }
        if (diretorio.exists()) {
            File listFiles[] = diretorio.listFiles();
            File sent = new File(sdcardDir.getAbsoluteFile() + path + "/Sent");
            if (typeDirectory == ABSOLUTE) {
                sent = new File(path + "/Sent");
            }
            if (sent.exists()){
                quantFile = listFiles.length - 1;
            } else {
                quantFile = listFiles.length;
            }
            for (int j =0 ; j<listFiles.length; j++){
                if (listFiles[j].getAbsolutePath().contains(".nomedia")){
                    quantFile = quantFile - 1;
                }
            }
            if (listFiles.length > 0){
                int tam = listFiles.length-1;
                boolean trocou = true;
                while (trocou && tam > 0){
                    trocou = false;
                    for (int i=0; i<tam; i++){
                        File img1 = listFiles[i];
                        File img2 = listFiles[i+1];
                        if (((img1.lastModified() < img2.lastModified()) && (!img2.getAbsolutePath().contains(".nomedia"))) || ((img1.getName().equals("Sent")) || (img1.getAbsolutePath().contains(".nomedia")))){
                            listFiles[i] = listFiles[i+1];
                            listFiles[i+1] = img1;
                            trocou = true;
                        }
                    }
                    tam--;
                }
                if (pos < listFiles.length){
                    File last = listFiles[pos];
                    if (last.getName().equals("Sent")) {
                        if  (last.getAbsolutePath().contains(".nomedia")) {
                            //quantFile--;
                        }
                        return KeyUtils.NO_FILE;
                    } else {
                        if  (last.getAbsolutePath().contains(".nomedia")) {
                            //quantFile--;
                            return KeyUtils.NO_FILE;
                        } else {
                            String fileReturn  = sdcardDir.getAbsoluteFile() + path + "/" + last.getName();
                            if (typeDirectory == ABSOLUTE) {
                                fileReturn  = path + "/" + last.getName();
                            }
                            return fileReturn;
                        }
                    }
                } else {
                    return KeyUtils.NO_FILE;
                }
            } else {
                return KeyUtils.NO_FILE;
            }
        } else {
            return KeyUtils.NO_FILE;
        }
    }

    private static String getNameLastFileBase(String path){
        File last = null;
        long maxTime = 0;
        File sdcardDir = android.os.Environment.getExternalStorageDirectory();
        File diretorio = new File(sdcardDir.getAbsoluteFile() + path);
        if (diretorio.exists()) {
            File listFiles[] = diretorio.listFiles();
            for (int i=0; i<listFiles.length; i++){
                File img = listFiles[i];
                if (img.lastModified() > maxTime){
                    last = img;
                    maxTime = img.lastModified();
                }
            }
        }
        if (last != null) {
            if  (last.getName().contains(".nomedia")) {
                return KeyUtils.NO_FILE;
            } else {
                return sdcardDir.getAbsoluteFile() + path + "/" + last.getName();
            }
        } else {
            return KeyUtils.NO_FILE;
        }
    }

    public static String getAbsolutePathImage(){
        File sdcardDir = android.os.Environment.getExternalStorageDirectory();
        File diretorio = new File(sdcardDir.getAbsoluteFile() + pathImage);
        return diretorio.getAbsolutePath()+"/";
    }

    public static String getAbsolutePathImageBusines(){
        File sdcardDir = android.os.Environment.getExternalStorageDirectory();
        File diretorio = new File(sdcardDir.getAbsoluteFile() + pathImageBusines);
        return diretorio.getAbsolutePath()+"/";
    }

    public static boolean copyFileByUri(Uri fonte, File destino, Context context) {
        boolean copiado = false;
        if (fonte != null && destino != null && context != null) {
            //Log.v(LogUtils.TAG_GERAL, "File copy: "+destino.getAbsolutePath());
            if (!destino.exists() || (destino.exists() && destino.delete())) {

                OutputStream out = null;
                InputStream in = null;
                try {
                    in = context.getContentResolver().openInputStream(fonte);
                    out = new FileOutputStream(destino);
                    byte[] buf = new byte[1024];
                    int len;
                    if (in != null) {
                        while ((len = in.read(buf)) > 0) {
                            out.write(buf, 0, len);
                        }
                        copiado = true;
                    }
                } catch (NullPointerException | IOException ex) {
                    ex.printStackTrace();
                    LogUtils.writeLog(context, LogUtils.TAG_GERAL, "falha copia: "+ex.toString());
                } finally {
                    if (out != null) {
                        try {
                            out.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (in != null) {
                        try {
                            in.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        LogUtils.writeLog(context, LogUtils.TAG_GERAL, "file copiado: "+copiado);
        return copiado;
    }

    public static File getFileName(String typeFile){
        File directory = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS),"");
        boolean existe = false;
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                Log.e(LogUtils.TAG_ERROR, "Diretorio não criado: " + directory.getAbsolutePath());
            } else {
                existe = true;
            }
        } else {
            existe = true;
        }
        if (existe){
            return new File(directory.getAbsoluteFile()  + "/"+typeFile);
        }
        return null;
    }

}
