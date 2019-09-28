package br.com.infobella.whatomail.whatomail.modelo;

import java.io.Serializable;

/**
 * Created by HENRI on 23/06/2017.
 */

public class ContactTicket implements Serializable {

    private Long id;
    private Long idContact;
    private String ticket;

    public static final  String TABLE = "ContactTicket";

    public static final String ID_SQL_LONG = "_id";
    public static final String ID_CONTACT_LONG = "idContact";
    public static final String TICKET_TEXT = "ticket";

    public ContactTicket() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdContact() {
        return idContact;
    }

    public void setIdContact(Long idContact) {
        this.idContact = idContact;
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
        return "ContactTicket{" +
                "id=" + id +
                ", idContact=" + idContact +
                ", ticket='" + ticket + '\'' +
                '}';
    }
}
