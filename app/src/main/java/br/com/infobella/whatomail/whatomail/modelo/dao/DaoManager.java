package br.com.infobella.whatomail.whatomail.modelo.dao;

import android.content.Context;

import br.com.infobella.whatomail.whatomail.modelo.WhatomailApplication;
import utils.UtilsManager;

/*
 * Created by Henrique on 01/07/2016.
 */
public class DaoManager {

    protected static UtilsManager utilsManager;
    public MessageReceivedDB messageReceivedDB;
    public ConvarsationReceivedDB convarsationReceivedDB;
    public ContactDB contactDB;
    public VeterinaryDB veterinaryDB;
    public MessageReplyDB messageReplyDB;
    public ContactTicketDB contactTicketDB;

    public DaoManager() {
        Context context = WhatomailApplication.getInstance().getApplicationContext();
        this.messageReceivedDB = new MessageReceivedDB(context);
        this.convarsationReceivedDB = new ConvarsationReceivedDB(context);
        this.contactDB = new ContactDB(context);
        this.veterinaryDB = new VeterinaryDB(context);
        this.messageReplyDB = new MessageReplyDB(context);
        this.contactTicketDB = new ContactTicketDB(context);
    }
}
