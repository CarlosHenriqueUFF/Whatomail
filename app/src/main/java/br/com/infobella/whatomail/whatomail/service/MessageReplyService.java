package br.com.infobella.whatomail.whatomail.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import br.com.infobella.whatomail.whatomail.modelo.Contact;
import br.com.infobella.whatomail.whatomail.modelo.ContactTicket;
import br.com.infobella.whatomail.whatomail.modelo.dao.DaoManager;
import br.com.infobella.whatomail.whatomail.modelo.MessageReply;
import utils.KeyUtils;
import utils.LogUtils;

/*
 * Created by HENRI on 28/05/2017.
 */

public class MessageReplyService extends IntentService {

    public MessageReplyService() {
        super("MessageReplyService");
    }

    @Override
    public void onCreate() {
        Log.v(LogUtils.TAG_LOG, " -------------------------- MessageReplyService: onCreate --------------------------------------");
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Log.v(LogUtils.TAG_LOG, " -------------------------- MessageReplyService: onDestroy ----------------------------------------");
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.v(LogUtils.TAG_LOG, " -------------------------- MessageReplyService: onHandleIntent --------------------------------------");
        DaoManager daoManager = new DaoManager();
        Bundle bundle = intent.getExtras();
        String ticket = bundle.getString(KeyUtils.KEY_REPLY_TICKET);
        Log.v(LogUtils.TAG_LOG, "ticket: "+ticket);
        String text = bundle.getString(KeyUtils.KEY_REPLY_TEXT);
        Log.v(LogUtils.TAG_LOG, "text: "+text);
        ContactTicket contactTicket = daoManager.contactTicketDB.findByTicket(ticket);
        if (contactTicket != null) {
            Contact contact = daoManager.contactDB.findById(contactTicket.getIdContact());
            if (contact != null) {
                Log.v(LogUtils.TAG_LOG, "contact: " + contact.toString());
                MessageReply messageReply = new MessageReply();
                messageReply.setText(text);
                messageReply.setTicket(ticket);
                messageReply.setContactJid(contact.getJidWhatsApp());
                messageReply.setContactName(contact.getContactName());
                daoManager.messageReplyDB.save(messageReply);
            } else {
                Log.v(LogUtils.TAG_LOG, "contact null ");
            }
        } else {
            Contact contact = daoManager.contactDB.findByTicket(ticket);
            if (contact != null){
                Log.v(LogUtils.TAG_LOG, "contact: "+contact.toString());
                MessageReply messageReply = new MessageReply();
                messageReply.setText(text);
                messageReply.setTicket(ticket);
                messageReply.setContactJid(contact.getJidWhatsApp());
                messageReply.setContactName(contact.getContactName());
                daoManager.messageReplyDB.save(messageReply);
            } else {
                Log.v(LogUtils.TAG_LOG, "contact null ");
            }
        }
    }
}
