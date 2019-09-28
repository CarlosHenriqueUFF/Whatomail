package utils;



/**
 * Created by Henrique on 12/07/2016.
 */
public class UrlUtils {

    public static final String URL_PERSIST_USUARIO = "http://infobella-android.appspot.com/persistUsuario";
    public static final String URL_PERSIST_PROFISSIONAL = "http://infobella-android.appspot.com/persistProfissional";
    public static final String URL_PERSIST_SALAO = "http://infobella-android.appspot.com/persistSalao";
    public static final String URL_PHOTO_PERFIL = "http://infobella-android.appspot.com/photoPerfil";
    public static final String URL_PHOTO_PERFIL_SALAO = "http://infobella-android.appspot.com/photoPerfilSalao";
    public static final String ACTION_DOWNLOAD = "?action=download";
    public static final String ACTION_UPLOAD = "?action=upload";
    public static final String GET_USUARIO_BY_ID_WEB = "?idUsuarioWeb=";
    public static final String GET_USUARIO_PROFISSIONAL_BY_ID_WEB = "?idProfissionalWeb=";
    public static final String GET_USUARIO_SALAO_BY_ID_WEB = "?idSalaoWeb=";

    /*
    public static String getUrlPhotoPerfilDownload(String id){
        HttpUtils<Void, RespostaUrl> http = new HttpUtils<>(RespostaUrl.class);
        RespostaUrl respostaUrl = http.getUrlImagePerfil(ACTION_DOWNLOAD, id);
        if (respostaUrl.isConfirmacao()){
            return respostaUrl.getUrl();
        }
        return null;
    }

    public static String getUrlPhotoPerfilUpload(){
        HttpUtils<Void, RespostaUrl> http = new HttpUtils<>(RespostaUrl.class);
        RespostaUrl respostaUrl = http.getUrlImagePerfil(ACTION_UPLOAD, null);
        if (respostaUrl.isConfirmacao()){
            return respostaUrl.getUrl();
        }
        return null;
    }

    public static String getUrlPhotoPerfilSalaoDownload(String id, String position){
        HttpUtils<Void, RespostaUrl> http = new HttpUtils<>(RespostaUrl.class);
        RespostaUrl respostaUrl = http.getUrlImagePerfilSalao(ACTION_DOWNLOAD, id, position);
        if (respostaUrl.isConfirmacao()){
            return respostaUrl.getUrl();
        }
        return null;
    }

    public static String getUrlPhotoPerfilSalaoUpload(){
        HttpUtils<Void, RespostaUrl> http = new HttpUtils<>(RespostaUrl.class);
        RespostaUrl respostaUrl = http.getUrlImagePerfilSalao(ACTION_UPLOAD, null, null);
        if (respostaUrl.isConfirmacao()){
            return respostaUrl.getUrl();
        }
        return null;
    }
    */
}
