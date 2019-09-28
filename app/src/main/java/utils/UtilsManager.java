package utils;

import java.io.IOException;

/**
 * Created by HENRI on 08/01/2017.
 */

public class UtilsManager {

    public static FileUtils fileUtils;
    public static HttpUtils httpUtils;
    public static ImageUtils imageUtils;
    public static IOUtils ioUtils;
    public static LogUtils logUtils;
    public static MultipartUtils multipartUtils;
    public static UrlUtils urlUtils;
    public static ContatoUtils contatoUtils;
    public static KeyUtils keyUtils;

    /*
    public UtilsManager() {
        this.fileUtils = new FileUtils();
        this.generateCode = new GenerateCode();
        this.imageUtils = new ImageUtils();
        this.ioUtils = new IOUtils();
        this.keyUtils = new KeyUtils();
        this.logUtils = new LogUtils();
        this.msgUtils = new MsgUtils();
        this.positionUtils = new PositionUtils();
        this.urlUtils = new UrlUtils();
    }
    */

    public static void initMultipartUtils(String requestURL, String charset) throws IOException {
        multipartUtils = new MultipartUtils(requestURL, charset);
    }


}
