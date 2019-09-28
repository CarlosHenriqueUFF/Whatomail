package br.com.infobella.whatomail.whatomail.modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by HENRI on 26/03/2017.
 */

public class MessageReceived implements Serializable{

    public static enum Status {

        Storing, Waiting, Sending, Sent, WaitingFile
    };

    private Long id;
    private Long idContact;
    private Status status;
    private Long timeStamp;
    private List<ConversationReceived> conversations;
    private Contact contact;

    public static final  String TABLE = "MessageReceived";

    public static final String ID_SQL_LONG = "_id";
    public static final String ID_CONTACT_LONG = "idContact";
    public static final String TIMESTAMP_LONG = "timeStamp";
    public static final String STATUS_TEXT = "status";

    private transient int order;

    public MessageReceived() {
        this.conversations = new ArrayList<>();
        this.status = Status.Storing;
        this.order = 1;
    }

    public List<ConversationReceived> getConversations() {
        return conversations;
    }

    public void setConversations(List<ConversationReceived> conversations) {
        this.conversations = conversations;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
        if (contact.getId() != null){
            this.idContact = contact.getId();
        }
    }

    public boolean isValido() {
        boolean valido = false;
        if (this.contact != null && this.contact.getJidWhatsApp() != null && !this.contact.getJidWhatsApp().equals("")){
            if (!this.conversations.isEmpty()){
                if (this.contact.getContactName() != null && !this.contact.getContactName().equals("")){
                    if (this.timeStamp > 0){
                        valido = true;
                    }
                }
            }
        }
        return valido;
    }



    public void addConversation(ConversationReceived conversationReceived){
        //ConversationReceived conversationReceived = new ConversationReceived();
        conversationReceived.setIdMessage(this.id);
        //conversationReceived.setOrder(this.order);
        //.Type type =  conversationReceived.setTextReturnType(conversation);
        this.conversations.add(conversationReceived);
        //this.order++;
        //return type;
    }

    public Date getDateTime() {
        Date dateTime = new Date(timeStamp);
        return dateTime;
    }

    @Override
    public String toString() {
        return "MessageReceived{" +
                "id=" + id +
                ", idContact=" + idContact +
                ", status=" + status +
                ", timeStamp=" + timeStamp +
                ", conversations=" + conversations +
                ", contact=" + contact +
                ", order=" + order +
                '}';
    }
}
