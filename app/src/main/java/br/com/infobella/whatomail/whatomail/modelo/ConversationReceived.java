package br.com.infobella.whatomail.whatomail.modelo;

import java.io.Serializable;

/**
 * Created by HENRI on 26/03/2017.
 */

public class ConversationReceived implements Serializable{

    public static enum Type {
        Text, Document, Video, Gif, Audio, VoiceNote, Image
    };

    private Long id;
    private Long idMessage;
    private int order;
    private String text;
    private Type type;
    private String file;
    private String key;

    public static final  String TABLE = "ConversationReceived";

    public static final String ID_SQL_LONG = "_id";
    public static final String ID_MESSAGE_LONG = "idMessage";
    public static final String ORDER_INT = "ordenation";
    public static final String TEXT_TEXT = "text";
    public static final String TYPE_TEXT = "type";
    public static final String FILE_TEXT = "file";
    public static final String KEY_TEXT = "key";

    public ConversationReceived() {

    }

    public Type setTextReturnType(String text){
        type = Type.Text;
        this.text = text;
        if (text.length() >= 2){
            int firstKey =  text.charAt(0);
            int secondKey = (int) text.charAt(1);
            if (firstKey == 55356){
                switch (secondKey){
                    case 57252:
                        if (text.contains("Mensagem de voz")){
                            type = Type.VoiceNote;
                            this.text = text.substring(3);
                            //file = FileUtils.getNameLastFileVoiceNotes();
                        }
                        break;
                    case 57269:
                        if (text.contains("Áudio")){
                            type = Type.Audio;
                            this.text = text.substring(3);
                            //file = FileUtils.getNameLastFileAudio();
                        }
                        break;
                    case 57253:
                        if (text.contains("Vídeo")){
                            type = Type.Video;
                            this.text = text.substring(3);
                            //file = FileUtils.getNameLastFileVideo();
                        }
                        break;
                }
            } else if (firstKey == 55357){
                switch (secondKey){
                    case 56567:
                        if (text.contains("Foto")){
                            type = Type.Image;
                            this.text = text.substring(3);
                            //file = FileUtils.getNameLastFileImage();
                        } else {
                            type = Type.Image;
                            this.text = "Foto com Legenda: " + text.substring(3);
                        }
                        break;
                    case 56516:
                        if (text.contains("páginas)")){
                            type = Type.Document;
                            this.text = text.substring(3);
                            //file = FileUtils.getNameLastFileDocuments();
                        }
                        break;
                    case 56446:
                        if (text.contains("GIF")){
                            type = Type.Gif;
                            this.text = text.substring(3);
                            //file = FileUtils.getNameLastFileAnimatedGifs();
                        }
                        break;
                }
            }
        }
        return this.type;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setIdMessage(Long idMessage) {
        this.idMessage = idMessage;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public Long getId() {
        return id;
    }

    public Long getIdMessage() {
        return idMessage;
    }

    public int getOrder() {
        return order;
    }

    public String getText() {
        return text;
    }

    public Type getType() {
        return type;
    }

    public String getFile() {
        return file;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void incORder(int inc){
        this.order = this.order + inc;
    }

    @Override
    public String toString() {
        return "ConversationReceived{" +
                "id=" + id +
                ", idMessage=" + idMessage +
                ", order=" + order +
                ", text='" + text + '\'' +
                ", type=" + type +
                ", file='" + file + '\'' +
                ", key='" + key + '\'' +
                '}';
    }
}
