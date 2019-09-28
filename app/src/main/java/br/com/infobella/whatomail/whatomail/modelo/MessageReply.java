package br.com.infobella.whatomail.whatomail.modelo;

import java.util.Date;

/**
 * Created by HENRI on 28/05/2017.
 */

public class MessageReply {

    public static enum Status {

        Waiting, Sending, Sent
    };

    private Long id;
    private String ticket;
    private String text;
    private Status status;
    private Date dataTimeReceived;
    private Date dataTimeSent;
    private String contactJid;
    private String contactName;

    public static final  String TABLE = "MessageReply";

    public static final String ID_SQL_LONG = "_id";
    public static final String TICKET_TEXT = "ticket";
    public static final String TEXT_TEXT = "text";
    public static final String STATUS_TEXT = "status";
    public static final String DATE_TIME_RECEIVED_DATE = "dateTimeReceived";
    public static final String DATE_TIME_SENT_DATE = "dateTimeSent";
    public static final String CONTACT_JID_TEXT = "jidContaco";
    public static final String CONTACT_NAME_TEXT = "nameContact";

    public MessageReply() {
        this.status = Status.Waiting;
        this.dataTimeReceived = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Date getDataTimeReceived() {
        return dataTimeReceived;
    }

    public void setDataTimeReceived(Date dataTimeReceived) {
        this.dataTimeReceived = dataTimeReceived;
    }

    public Date getDataTimeSent() {
        return dataTimeSent;
    }

    public void setDataTimeSent(Date dataTimeSent) {
        this.dataTimeSent = dataTimeSent;
    }

    public String getContactJid() {
        return contactJid;
    }

    public void setContactJid(String contactJid) {
        this.contactJid = contactJid;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    @Override
    public String toString() {
        return "MessageReply{" +
                "id=" + id +
                ", ticket='" + ticket + '\'' +
                ", text='" + text + '\'' +
                ", status=" + status +
                ", dataTimeReceived=" + dataTimeReceived +
                ", dataTimeSent=" + dataTimeSent +
                ", contactJid='" + contactJid + '\'' +
                ", contactName='" + contactName + '\'' +
                '}';
    }
}
