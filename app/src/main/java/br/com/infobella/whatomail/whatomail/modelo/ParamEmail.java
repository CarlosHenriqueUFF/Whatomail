package br.com.infobella.whatomail.whatomail.modelo;

import java.util.List;

/**
 * Created by HENRI on 11/04/2017.
 */

public class ParamEmail {

    private Contact contact;
    private String text;
    private String dateTime;
    private List<String> files;

    public ParamEmail(Contact contact, String text, String dateTime, List<String> files) {
        this.contact = contact;
        this.text = text;
        this.dateTime = dateTime;
        this.files = files;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public List<String> getFiles() {
        return files;
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }

    @Override
    public String toString() {
        return "ParamEmail{" +
                "contact=" + contact +
                ", text='" + text + '\'' +
                ", dateTime='" + dateTime + '\'' +
                ", files=" + files +
                '}';
    }
}
