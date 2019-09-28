package br.com.infobella.whatomail.whatomail.modelo;

import android.content.Context;

import java.io.Serializable;

import utils.ContatoUtils;

/**
 * Created by HENRI on 26/03/2017.
 */

public class Contact implements Serializable{

    private Long id;
    private String jidWhatsApp;
    private String contactName;
    private String phone;
    private String ticket;

    public static final  String TABLE = "Contact";

    public static final String ID_SQL_LONG = "_id";
    public static final String JID_WHATSAPP_TEXT = "jidWhatsApp";
    public static final String CONTACT_NAME_TEXT = "contactName";
    public static final String PHONE_TEXT = "phone";
    public static final String TICKET_TEXT = "ticket";

    public Contact() {
    }

    public String getJidWhatsApp() {
        return jidWhatsApp;
    }

    public void setJidWhatsApp(String jidWhatsApp) {
        this.jidWhatsApp = jidWhatsApp;
        int pos = jidWhatsApp.indexOf("-");
        if (pos > -1){
            phone = this.jidWhatsApp.substring(0, pos);
        } else {
            pos = jidWhatsApp.indexOf("@");
            if (pos > -1) {
                this.phone = this.jidWhatsApp.substring(0, pos);
            }
        }
        String name = ContatoUtils.getNameByFone(WhatomailApplication.getContext(), phone);
        if (name != null){
            this.setContactName(name);
        }
    }

    public void setJidFromPeople(Context context, String people){
        int pos = people.lastIndexOf("/");
        if (pos != -1){
            String idPeople = people.substring(pos+1);
            String fone = ContatoUtils.getFoneWhatsapp(context, idPeople);
            if (fone != null){
                this.jidWhatsApp = fone +"@s.whatsapp.net";
                this.phone = fone;
            }
        }
    }

    public void setJidFromPhone(String number){
        this.jidWhatsApp = "+55" + number +"@s.whatsapp.net";
    }

    public String getPhoneFormatted(){
        String fone = "";
        int tam = this.phone.length();
        if (tam == 13){
            fone = "(" + phone.substring(2, 4) + ")" + phone.substring(4,9) + "-" + phone.substring(9);
        } else if (tam == 11) {
            fone = "(" + phone.substring(0, 2) + ")" + phone.substring(2,7) + "-" + phone.substring(7);
        } else if (tam == 9) {
            fone = phone.substring(0,5) + "-" + phone.substring(5);
        }
        return fone;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public void setTicket(Integer ticket) {
        this.ticket = ticket.toString();
    }

    @Override
    public String toString() {
        return "Contact{" +
                "id=" + id +
                ", jidWhatsApp='" + jidWhatsApp + '\'' +
                ", contactName='" + contactName + '\'' +
                ", phone='" + phone + '\'' +
                ", ticket='" + ticket + '\'' +
                '}';
    }
}
