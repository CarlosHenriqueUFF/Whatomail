package br.com.infobella.whatomail.whatomail.modelo;

import java.io.Serializable;

/**
 * Created by HENRI on 03/05/2017.
 */

public class LastFiles implements Serializable{

    private String lastFileImage;
    private String lastFileGif;
    private String lastFileDocument;
    private String lastFileVoiceNote;
    private String lastFileAudio;
    private String lastFileVideo;

    public LastFiles() {
        this.lastFileImage = "";
        this.lastFileGif = "";
        this.lastFileDocument = "";
        this.lastFileVoiceNote = "";
        this.lastFileAudio = "";
        this.lastFileVideo = "";
    }

    public String getLastFileImage() {
        return lastFileImage;
    }

    public void setLastFileImage(String lastFileImage) {
        this.lastFileImage = lastFileImage;
    }

    public String getLastFileGif() {
        return lastFileGif;
    }

    public void setLastFileGif(String lastFileGif) {
        this.lastFileGif = lastFileGif;
    }

    public String getLastFileDocument() {
        return lastFileDocument;
    }

    public void setLastFileDocument(String lastFileDocument) {
        this.lastFileDocument = lastFileDocument;
    }

    public String getLastFileVoiceNote() {
        return lastFileVoiceNote;
    }

    public void setLastFileVoiceNote(String lastFileVoiceNote) {
        this.lastFileVoiceNote = lastFileVoiceNote;
    }

    public String getLastFileAudio() {
        return lastFileAudio;
    }

    public void setLastFileAudio(String lastFileAudio) {
        this.lastFileAudio = lastFileAudio;
    }

    public String getLastFileVideo() {
        return lastFileVideo;
    }

    public void setLastFileVideo(String lastFileVideo) {
        this.lastFileVideo = lastFileVideo;
    }

    @Override
    public String toString() {
        return "LastFiles{" +
                "lastFileImage='" + lastFileImage + '\'' +
                ", lastFileGif='" + lastFileGif + '\'' +
                ", lastFileDocument='" + lastFileDocument + '\'' +
                ", lastFileVoiceNote='" + lastFileVoiceNote + '\'' +
                ", lastFileAudio='" + lastFileAudio + '\'' +
                ", lastFileVideo='" + lastFileVideo + '\'' +
                '}';
    }
}
