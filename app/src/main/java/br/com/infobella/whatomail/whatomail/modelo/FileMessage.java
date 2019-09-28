package br.com.infobella.whatomail.whatomail.modelo;

import java.io.Serializable;

/**
 * Created by HENRI on 21/04/2017.
 */

public class FileMessage implements Serializable{

    private String jid;
    private String key;
    private ConversationReceived.Type type;
    private int roud;

    public FileMessage() {

    }

    public String getJid() {
        return jid;
    }

    public void setJid(String jid) {
        this.jid = jid;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public ConversationReceived.Type getType() {
        return type;
    }

    public void setType(ConversationReceived.Type type) {
        this.type = type;
    }

    public int getRoud() {
        return roud;
    }

    public void setRoud(int roud) {
        this.roud = roud;
    }

    @Override
    public String toString() {
        return "FileMessage{" +
                "jid='" + jid + '\'' +
                ", key='" + key + '\'' +
                ", type=" + type +
                ", roud=" + roud +
                '}';
    }
}
