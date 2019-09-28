package br.com.infobella.whatomail.whatomail.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.mail.MessagingException;

import br.com.infobella.whatomail.whatomail.controller.TicketController;
import br.com.infobella.whatomail.whatomail.modelo.Config;
import br.com.infobella.whatomail.whatomail.modelo.Contact;
import br.com.infobella.whatomail.whatomail.modelo.ContactTicket;
import br.com.infobella.whatomail.whatomail.modelo.dao.DaoManager;
import br.com.infobella.whatomail.whatomail.modelo.ConversationReceived;
import br.com.infobella.whatomail.whatomail.modelo.FileMessage;
import br.com.infobella.whatomail.whatomail.modelo.FilesMessagePack;
import br.com.infobella.whatomail.whatomail.modelo.LastFiles;
import br.com.infobella.whatomail.whatomail.modelo.Mail;
import br.com.infobella.whatomail.whatomail.modelo.MessageReceived;
import utils.FileUtils;
import utils.KeyUtils;
import utils.LogUtils;

/*
 * Created by HENRI on 27/03/2017.
 */

public class StorageMessageReceivedService extends IntentService {

    public StorageMessageReceivedService() {
        super("StorageMessageReceived");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        LogUtils.writeLog(this, LogUtils.TAG_STORAGE, "StorageService onHandleIntent Start");
        DaoManager daoManager = new DaoManager();
        Bundle bundle = intent.getExtras();
        int key = bundle.getInt(KeyUtils.KEY_ACTION_STORAGE);
        if (key == KeyUtils.KEY_ACTION_STORAGE_WRITE) {
            LogUtils.writeLog(this, LogUtils.TAG_STORAGE, "StorageService onHandleIntent WRITE");
            MessageReceived messageReceived = (MessageReceived) bundle.get(KeyUtils.KEY_MESSAGE_PACK);
            //LogUtils.writeLog(this, LogUtils.TAG_STORAGE, "StorageService MSG: " + messageReceived);
            Contact contact = null;
            if (messageReceived != null) {
                contact = messageReceived.getContact();
            }
            Contact contactStorage;
            if (contact != null) {
                contactStorage = daoManager.contactDB.findByIdWhatsApp(contact.getJidWhatsApp());

                long idContact;
                if (contactStorage != null) {
                    idContact = contactStorage.getId();
                } else {
                    idContact = daoManager.contactDB.save(contact);
                }
                contact.setId(idContact);
                daoManager.contactDB.save(contact);
                LogUtils.writeLog(this, LogUtils.TAG_STORAGE, "StorageService Contact: " + contact);
                if (idContact > 0) {
                    messageReceived.setIdContact(idContact);
                    MessageReceived messageReceivedStorage = daoManager.messageReceivedDB.findMessagePendindByIdContact(idContact);
                    long idMessage;
                    if (messageReceivedStorage != null) {
                        idMessage = messageReceivedStorage.getId();
                    } else {
                        idMessage = daoManager.messageReceivedDB.save(messageReceived);
                    }
                    messageReceived.setId(idMessage);
                    daoManager.messageReceivedDB.save(messageReceived);
                    LogUtils.writeLog(this, LogUtils.TAG_STORAGE, "StorageService MessageReceived: " + messageReceived);
                    if (idMessage > 0) {
                        Set<String> set = daoManager.convarsationReceivedDB.getSetByIdMessage(idMessage);
                        for (ConversationReceived c : messageReceived.getConversations()) {
                            if (c.getType() == ConversationReceived.Type.Text) {
                                if (!set.contains(c.getText())) {
                                    int maxOrder = daoManager.convarsationReceivedDB.findMaxOrderByIdMessage(idMessage);
                                    c.setOrder(maxOrder + 1);
                                    c.setIdMessage(idMessage);
                                    long idConversation = daoManager.convarsationReceivedDB.save(c);
                                    c.setId(idConversation);
                                    LogUtils.writeLog(this, LogUtils.TAG_STORAGE, "StorageService Conversation: " + c);
                                }
                            } else {
                                int maxOrder = daoManager.convarsationReceivedDB.findMaxOrderByIdMessage(idMessage);
                                c.setOrder(maxOrder + 1);
                                c.setIdMessage(idMessage);
                                long idConversation = daoManager.convarsationReceivedDB.save(c);
                                c.setId(idConversation);
                                LogUtils.writeLog(this, LogUtils.TAG_STORAGE, "StorageService Conversation: " + c);
                            }
                        }
                        messageReceived.setStatus(MessageReceived.Status.Waiting);
                        daoManager.messageReceivedDB.save(messageReceived);
                    }
                }
            }
        } else if (key == KeyUtils.KEY_ACTION_STORAGE_READ){
            LogUtils.writeLog(this, LogUtils.TAG_STORAGE, "StorageService onHandleIntent read messages");
            Mail mail = new Mail();
            List<MessageReceived> messageReceivedList;
            if (Config.SEND_FRESHDESK){
                messageReceivedList = daoManager.messageReceivedDB.findByStatus(MessageReceived.Status.Waiting);
            } else {
                messageReceivedList = daoManager.messageReceivedDB.findNextForSend();
            }
            for (MessageReceived messageReceived : messageReceivedList){
                Contact contact = daoManager.contactDB.findById(messageReceived.getIdContact());
                List<ConversationReceived> conversationReceivedList = daoManager.convarsationReceivedDB.findByIdMessage(messageReceived.getId());
                String text = "";
                List<String> files = new ArrayList<>();
                for (ConversationReceived conversationReceived : conversationReceivedList){
                    text = text + "\n" + conversationReceived.getText();
                    if (conversationReceived.getFile() != null && !conversationReceived.getFile().contains(KeyUtils.NO_FILE)){
                        files.add(conversationReceived.getFile());
                    }
                }
                LogUtils.writeLog(this, LogUtils.TAG_STORAGE, "Contact: " + contact.toString());
                LogUtils.writeLog(this, LogUtils.TAG_STORAGE, "Message: " + messageReceived.toString());
                LogUtils.writeLog(this, LogUtils.TAG_STORAGE, "Conversation: " + text);
                LogUtils.writeLog(this, LogUtils.TAG_STORAGE, "Files: " + files);
                Date date = new Date();
                date.setTime(messageReceived.getTimeStamp());

                LogUtils.writeLog(this, LogUtils.TAG_STORAGE, "contact ticket: " + contact.getTicket());

                Set<String> setJid = daoManager.veterinaryDB.getSetJid();

                if (Config.SEND_FRESHDESK) {
                    if (contact.getTicket() == null || setJid.contains(contact.getJidWhatsApp())){
                        try {
                            Map<String, Object> map = TicketController.createTicketWithAttachments(contact, text, date.toString(), files);
                            LogUtils.writeLog(this, LogUtils.TAG_STORAGE, "Map: " + map);
                            int result = (Integer) map.get("result");
                            LogUtils.writeLog(this, LogUtils.TAG_STORAGE, "result: " + result);
                            if (result == 200){
                                messageReceived.setStatus(MessageReceived.Status.Sent);
                                daoManager.messageReceivedDB.save(messageReceived);
                                Double ticket = (Double) map.get("display_id");
                                LogUtils.writeLog(this, LogUtils.TAG_STORAGE, "ticket received: " + ticket);
                                if (ticket != null) {
                                    contact.setTicket(ticket.intValue());
                                    daoManager.contactDB.save(contact);
                                    ContactTicket contactTicket = new ContactTicket();
                                    contactTicket.setIdContact(contact.getId());
                                    contactTicket.setTicket(ticket.intValue());
                                    daoManager.contactTicketDB.save(contactTicket);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            Map<String, Object> map = TicketController.getTicket(contact.getTicket());
                            int result = (Integer) map.get("result");
                            if (result == 200){
                                boolean deleted = (Boolean) map.get("deleted");
                                if (deleted){
                                    //Create new Ticket
                                    Map<String, Object> map2 = TicketController.createTicketWithAttachments(contact, text, date.toString(), files);
                                    int result2 = (Integer) map2.get("result");
                                    if (result2 == 200){
                                        messageReceived.setStatus(MessageReceived.Status.Sent);
                                        daoManager.messageReceivedDB.save(messageReceived);
                                        Double ticket = (Double) map.get("display_id");
                                        LogUtils.writeLog(this, LogUtils.TAG_STORAGE, "ticket received: " + ticket);
                                        if (ticket != null) {
                                            contact.setTicket(ticket.intValue());
                                            daoManager.contactDB.save(contact);
                                            ContactTicket contactTicket = new ContactTicket();
                                            contactTicket.setIdContact(contact.getId());
                                            contactTicket.setTicket(ticket.intValue());
                                            daoManager.contactTicketDB.save(contactTicket);
                                        }
                                    }
                                } else {
                                    Double status = (Double) map.get("status");
                                    if (status.intValue() == 4 || status.intValue() == 5){
                                        //Create new Ticket
                                        Map<String, Object> map2 = TicketController.createTicketWithAttachments(contact, text, date.toString(), files);
                                        int result2 = (Integer) map2.get("result");
                                        if (result2 == 200){
                                            messageReceived.setStatus(MessageReceived.Status.Sent);
                                            daoManager.messageReceivedDB.save(messageReceived);
                                            Double ticket = (Double) map.get("display_id");
                                            LogUtils.writeLog(this, LogUtils.TAG_STORAGE, "ticket received: " + ticket);
                                            if (ticket != null) {
                                                contact.setTicket(ticket.intValue());
                                                daoManager.contactDB.save(contact);
                                                ContactTicket contactTicket = new ContactTicket();
                                                contactTicket.setIdContact(contact.getId());
                                                contactTicket.setTicket(ticket.intValue());
                                                daoManager.contactTicketDB.save(contactTicket);
                                            }
                                        }
                                    } else {
                                        //Add note
                                        int result3 = TicketController.addNoteForTicketWithAttachments(contact.getTicket(), text, files);
                                        if (result3 == 200){
                                            messageReceived.setStatus(MessageReceived.Status.Sent);
                                            daoManager.messageReceivedDB.save(messageReceived);
                                        }
                                    }
                                }
                            } else {
                                //Create new Ticket
                                Map<String, Object> map2 = TicketController.createTicketWithAttachments(contact, text, date.toString(), files);
                                int result2 = (Integer) map2.get("result");
                                if (result2 == 200){
                                    messageReceived.setStatus(MessageReceived.Status.Sent);
                                    daoManager.messageReceivedDB.save(messageReceived);
                                    Double ticket = (Double) map.get("display_id");
                                    LogUtils.writeLog(this, LogUtils.TAG_STORAGE, "ticket received: " + ticket);
                                    if (ticket != null) {
                                        contact.setTicket(ticket.intValue());
                                        daoManager.contactDB.save(contact);
                                        ContactTicket contactTicket = new ContactTicket();
                                        contactTicket.setIdContact(contact.getId());
                                        contactTicket.setTicket(ticket.intValue());
                                        daoManager.contactTicketDB.save(contactTicket);
                                    }
                                }
                            }
                        } catch (Exception  e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    //Send for email
                    try {
                        mail.sendMailHtml(contact, text, date.toString(), files);
                        messageReceived.setStatus(MessageReceived.Status.Sent);
                        daoManager.messageReceivedDB.save(messageReceived);
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else if (key == KeyUtils.KEY_ACTION_STORAGE_READ_NOW){
            LogUtils.writeLog(this, LogUtils.TAG_STORAGE, "StorageService onHandleIntent read NOW messages");
            Mail mail = new Mail();
            List<MessageReceived> messageReceivedList;
            if (Config.SEND_FRESHDESK){
                messageReceivedList = daoManager.messageReceivedDB.findByStatus(MessageReceived.Status.Waiting);
            } else {
                messageReceivedList = daoManager.messageReceivedDB.findNextForSend();
            }
            for (MessageReceived messageReceived : messageReceivedList){
                Contact contact = daoManager.contactDB.findById(messageReceived.getIdContact());
                List<ConversationReceived> conversationReceivedList = daoManager.convarsationReceivedDB.findByIdMessage(messageReceived.getId());
                String text = "";
                List<String> files = new ArrayList<>();
                for (ConversationReceived conversationReceived : conversationReceivedList){
                    text = text + "\n" + conversationReceived.getText();
                    if (conversationReceived.getFile() != null && !conversationReceived.getFile().contains(KeyUtils.NO_FILE)){
                        files.add(conversationReceived.getFile());
                    }
                }
                LogUtils.writeLog(this, LogUtils.TAG_STORAGE, "Contact: " + contact.toString());
                LogUtils.writeLog(this, LogUtils.TAG_STORAGE, "Message: " + messageReceived.toString());
                LogUtils.writeLog(this, LogUtils.TAG_STORAGE, "Conversation: " + text);
                LogUtils.writeLog(this, LogUtils.TAG_STORAGE, "Files: " + files);
                Date date = new Date();
                date.setTime(messageReceived.getTimeStamp());

                LogUtils.writeLog(this, LogUtils.TAG_STORAGE, "contact ticket: " + contact.getTicket());

                Set<String> setJid = daoManager.veterinaryDB.getSetJid();

                if (Config.SEND_FRESHDESK) {
                    if (contact.getTicket() == null || setJid.contains(contact.getJidWhatsApp())){
                        try {
                            Map<String, Object> map = TicketController.createTicketWithAttachments(contact, text, date.toString(), files);
                            LogUtils.writeLog(this, LogUtils.TAG_STORAGE, "Map: " + map);
                            int result = (Integer) map.get("result");
                            LogUtils.writeLog(this, LogUtils.TAG_STORAGE, "result: " + result);
                            if (result == 200){
                                messageReceived.setStatus(MessageReceived.Status.Sent);
                                daoManager.messageReceivedDB.save(messageReceived);
                                Double ticket = (Double) map.get("display_id");
                                LogUtils.writeLog(this, LogUtils.TAG_STORAGE, "ticket received: " + ticket);
                                if (ticket != null) {
                                    contact.setTicket(ticket.intValue());
                                    daoManager.contactDB.save(contact);
                                    ContactTicket contactTicket = new ContactTicket();
                                    contactTicket.setIdContact(contact.getId());
                                    contactTicket.setTicket(ticket.intValue());
                                    daoManager.contactTicketDB.save(contactTicket);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            Map<String, Object> map = TicketController.getTicket(contact.getTicket());
                            int result = (Integer) map.get("result");
                            if (result == 200){
                                boolean deleted = (Boolean) map.get("deleted");
                                if (deleted){
                                    //Create new Ticket
                                    Map<String, Object> map2 = TicketController.createTicketWithAttachments(contact, text, date.toString(), files);
                                    int result2 = (Integer) map2.get("result");
                                    if (result2 == 200){
                                        messageReceived.setStatus(MessageReceived.Status.Sent);
                                        daoManager.messageReceivedDB.save(messageReceived);
                                        Double ticket = (Double) map.get("display_id");
                                        LogUtils.writeLog(this, LogUtils.TAG_STORAGE, "ticket received: " + ticket);
                                        if (ticket != null) {
                                            contact.setTicket(ticket.intValue());
                                            daoManager.contactDB.save(contact);
                                            ContactTicket contactTicket = new ContactTicket();
                                            contactTicket.setIdContact(contact.getId());
                                            contactTicket.setTicket(ticket.intValue());
                                            daoManager.contactTicketDB.save(contactTicket);
                                        }
                                    }
                                } else {
                                    Double status = (Double) map.get("status");
                                    if (status.intValue() == 4 || status.intValue() == 5){
                                        //Create new Ticket
                                        Map<String, Object> map2 = TicketController.createTicketWithAttachments(contact, text, date.toString(), files);
                                        int result2 = (Integer) map2.get("result");
                                        if (result2 == 200){
                                            messageReceived.setStatus(MessageReceived.Status.Sent);
                                            daoManager.messageReceivedDB.save(messageReceived);
                                            Double ticket = (Double) map.get("display_id");
                                            LogUtils.writeLog(this, LogUtils.TAG_STORAGE, "ticket received: " + ticket);
                                            if (ticket != null) {
                                                contact.setTicket(ticket.intValue());
                                                daoManager.contactDB.save(contact);
                                                ContactTicket contactTicket = new ContactTicket();
                                                contactTicket.setIdContact(contact.getId());
                                                contactTicket.setTicket(ticket.intValue());
                                                daoManager.contactTicketDB.save(contactTicket);
                                            }
                                        }
                                    } else {
                                        //Add note
                                        int result3 = TicketController.addNoteForTicketWithAttachments(contact.getTicket(), text, files);
                                        if (result3 == 200){
                                            messageReceived.setStatus(MessageReceived.Status.Sent);
                                            daoManager.messageReceivedDB.save(messageReceived);
                                        }
                                    }
                                }
                            } else {
                                //Create new Ticket
                                Map<String, Object> map2 = TicketController.createTicketWithAttachments(contact, text, date.toString(), files);
                                int result2 = (Integer) map2.get("result");
                                if (result2 == 200){
                                    messageReceived.setStatus(MessageReceived.Status.Sent);
                                    daoManager.messageReceivedDB.save(messageReceived);
                                    Double ticket = (Double) map.get("display_id");
                                    LogUtils.writeLog(this, LogUtils.TAG_STORAGE, "ticket received: " + ticket);
                                    if (ticket != null) {
                                        contact.setTicket(ticket.intValue());
                                        daoManager.contactDB.save(contact);
                                        ContactTicket contactTicket = new ContactTicket();
                                        contactTicket.setIdContact(contact.getId());
                                        contactTicket.setTicket(ticket.intValue());
                                        daoManager.contactTicketDB.save(contactTicket);
                                    }
                                }
                            }
                        } catch (Exception  e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    //Send for email
                    try {
                        mail.sendMailHtml(contact, text, date.toString(), files);
                        messageReceived.setStatus(MessageReceived.Status.Sent);
                        daoManager.messageReceivedDB.save(messageReceived);
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else if (key == KeyUtils.KEY_ACTION_STORAGE_FILES){
            LogUtils.writeLog(this, LogUtils.TAG_STORAGE, "StorageService onHandleIntent process files");
            FilesMessagePack filesMessagePack = (FilesMessagePack) bundle.getSerializable(KeyUtils.KEY_FILES_MESSAGE);
            LastFiles lastFiles;
            lastFiles = (LastFiles) bundle.getSerializable(KeyUtils.KEY_LAST_FILES);
            if (lastFiles == null){
                lastFiles = new LastFiles();
            }
            if (filesMessagePack != null){
                List<FileMessage> fileMessages = filesMessagePack.getList();
                if (fileMessages != null){
                    List<FileMessage> filesAudios = new ArrayList<>();
                    List<FileMessage> filesImages = new ArrayList<>();
                    List<FileMessage> filesGifs = new ArrayList<>();
                    List<FileMessage> filesDocuments = new ArrayList<>();
                    List<FileMessage> filesVoiceNotes = new ArrayList<>();
                    List<FileMessage> filesVideos = new ArrayList<>();
                    for (FileMessage fm : fileMessages){
                        LogUtils.writeLog(this, LogUtils.TAG_STORAGE, "FileMessage: " + fm);
                        if (fm.getType() == ConversationReceived.Type.Audio){
                            filesAudios.add(fm);
                        } else if (fm.getType() == ConversationReceived.Type.Document){
                            filesDocuments.add(fm);
                        } else if (fm.getType() == ConversationReceived.Type.Gif){
                            filesGifs.add(fm);
                        } else if (fm.getType() == ConversationReceived.Type.Image){
                            filesImages.add(fm);
                        } else if (fm.getType() == ConversationReceived.Type.VoiceNote){
                            filesVoiceNotes.add(fm);
                        } else if (fm.getType() == ConversationReceived.Type.Video){
                            filesVideos.add(fm);
                        }
                    }
                    int posAudios = filesAudios.size()-1;
                    int posDocuments = filesDocuments.size()-1;
                    int posImages = filesImages.size()-1;
                    int posGifs = filesGifs.size()-1;
                    int posVoiceNotes = filesVoiceNotes.size()-1;
                    int posVideos = filesVideos.size()-1;
                    LogUtils.writeLog(this, LogUtils.TAG_STORAGE, "posAudio: " + posAudios);
                    LogUtils.writeLog(this, LogUtils.TAG_STORAGE, "posDocuments: " + posDocuments);
                    LogUtils.writeLog(this, LogUtils.TAG_STORAGE, "posGisf: " + posGifs);
                    LogUtils.writeLog(this, LogUtils.TAG_STORAGE, "posImages: " + posImages);
                    LogUtils.writeLog(this, LogUtils.TAG_STORAGE, "posVideos: " + posVideos);
                    LogUtils.writeLog(this, LogUtils.TAG_STORAGE, "posVoiceNotes: " + posVoiceNotes);
                    //Verificar se todos os arquivos foram baixados
                    boolean gifValid = true;
                    boolean imageValid = true;
                    boolean documentValid = true;
                    boolean voiceNoteValid = true;
                    boolean videoValid = true;
                    boolean audioValid = true;
                    for (int i=0; i<filesAudios.size(); i++){
                        String file = FileUtils.getNameLastFileAudio(i);
                        if (file.equals(lastFiles.getLastFileAudio())){
                            audioValid = false;
                            break;
                        }
                    }
                    for (int i=0; i<filesDocuments.size(); i++){
                        String file = FileUtils.getNameLastFileDocuments(i);
                        if (file.equals(lastFiles.getLastFileDocument())){
                            documentValid = false;
                            break;
                        }
                    }
                    for (int i=0; i<filesGifs.size(); i++){
                        String file = FileUtils.getNameLastFileAnimatedGifs(i);
                        if (file.equals(lastFiles.getLastFileGif())){
                            gifValid = false;
                            break;
                        }
                    }
                    for (int i=0; i<filesImages.size(); i++){
                        String file = FileUtils.getNameLastFileImage(i);
                        if (file.equals(lastFiles.getLastFileImage())){
                            imageValid = false;
                            break;
                        }
                    }
                    for (int i=0; i<filesVideos.size(); i++){
                        String file = FileUtils.getNameLastFileVideo(i);
                        if (file.equals(lastFiles.getLastFileVideo())){
                            videoValid = false;
                            break;
                        }
                    }
                    for (int i=0; i<filesVoiceNotes.size(); i++){
                        String file = FileUtils.getNameLastFileVoiceNotes(i);
                        if (file.equals(lastFiles.getLastFileVoiceNote())){
                            voiceNoteValid = false;
                            break;
                        }
                    }
                    //
                    Set<Long> idsMessages = new HashSet<>();
                    if (audioValid) {
                        for (FileMessage fm : filesAudios) {
                            String file = FileUtils.getNameLastFileAudio(posAudios);
                            String jids = "Prováveis Contatos: ";
                            boolean achou = false;
                            for (FileMessage fm1 : filesAudios) {
                                if (!fm1.getKey().equals(fm.getKey()) && fm.getRoud() == fm1.getRoud() && !fm.getJid().equals(fm1.getJid())) {
                                    achou = true;
                                    jids = jids + fm1.getJid() + "\n";
                                }
                            }
                            if (achou) {
                                LogUtils.writeLog(this, LogUtils.TAG_STORAGE, "inconsistencia Audio-> jid: " + fm.getJid() + " file: " + file);
                                ConversationReceived cr = daoManager.convarsationReceivedDB.findByKey(fm.getKey());
                                cr.setText(cr.getText() + " - Arquivo não Associado!\n\n" + jids);
                                daoManager.convarsationReceivedDB.save(cr);
                                idsMessages.add(cr.getIdMessage());
                                //
                                saveContactMessageConversation(file, ConversationReceived.Type.Audio, daoManager);
                            } else {
                                LogUtils.writeLog(this, LogUtils.TAG_STORAGE, "associado Audio-> jid: " + fm.getJid() + " file: " + file);
                                ConversationReceived cr = daoManager.convarsationReceivedDB.findByKey(fm.getKey());
                                cr.setFile(file);
                                daoManager.convarsationReceivedDB.save(cr);
                                idsMessages.add(cr.getIdMessage());
                            }
                            posAudios--;
                        }
                    } else {
                        for (FileMessage fm : filesAudios) {
                            LogUtils.writeLog(this, LogUtils.TAG_STORAGE, "inconsistencia Audio-> jid: " + fm.getJid());
                            ConversationReceived cr = daoManager.convarsationReceivedDB.findByKey(fm.getKey());
                            cr.setText(cr.getText() + " - Arquivo não Baixado!\n");
                            daoManager.convarsationReceivedDB.save(cr);
                            idsMessages.add(cr.getIdMessage());
                        }
                    }
                    if (documentValid) {
                        for (FileMessage fm : filesDocuments) {
                            String file = FileUtils.getNameLastFileDocuments(posDocuments);
                            String jids = "Prováveis Contatos: ";
                            boolean achou = false;
                            for (FileMessage fm1 : filesDocuments) {
                                if (!fm1.getKey().equals(fm.getKey()) && fm.getRoud() == fm1.getRoud() && !fm.getJid().equals(fm1.getJid())) {
                                    achou = true;
                                    jids = jids + fm1.getJid() + "\n";
                                }
                            }
                            if (achou) {
                                LogUtils.writeLog(this, LogUtils.TAG_STORAGE, "inconsistencia Document-> jid: " + fm.getJid() + " file: " + file);
                                ConversationReceived cr = daoManager.convarsationReceivedDB.findByKey(fm.getKey());
                                cr.setText(cr.getText() + " - Arquivo não Associado!\n\n" + jids);
                                daoManager.convarsationReceivedDB.save(cr);
                                idsMessages.add(cr.getIdMessage());
                                //
                                saveContactMessageConversation(file, ConversationReceived.Type.Document, daoManager);
                            } else {
                                LogUtils.writeLog(this, LogUtils.TAG_STORAGE, "associado Document-> jid: " + fm.getJid() + " file: " + file);
                                ConversationReceived cr = daoManager.convarsationReceivedDB.findByKey(fm.getKey());
                                cr.setFile(file);
                                daoManager.convarsationReceivedDB.save(cr);
                                idsMessages.add(cr.getIdMessage());
                            }
                            posDocuments--;
                        }
                    } else {
                        for (FileMessage fm : filesDocuments) {
                            LogUtils.writeLog(this, LogUtils.TAG_STORAGE, "inconsistencia Document-> jid: " + fm.getJid());
                            ConversationReceived cr = daoManager.convarsationReceivedDB.findByKey(fm.getKey());
                            cr.setText(cr.getText() + " - Arquivo não Baixado!\n");
                            daoManager.convarsationReceivedDB.save(cr);
                            idsMessages.add(cr.getIdMessage());
                        }
                    }
                    if (gifValid) {
                        for (FileMessage fm : filesGifs) {
                            String file = FileUtils.getNameLastFileAnimatedGifs(posGifs);
                            String jids = "Prováveis Contatos: ";
                            boolean achou = false;
                            for (FileMessage fm1 : filesGifs) {
                                if (!fm1.getKey().equals(fm.getKey()) && fm.getRoud() == fm1.getRoud() && !fm.getJid().equals(fm1.getJid())) {
                                    achou = true;
                                    jids = jids + fm1.getJid() + "\n";
                                }
                            }
                            if (achou) {
                                LogUtils.writeLog(this, LogUtils.TAG_STORAGE, "inconsistencia Gif-> jid: " + fm.getJid() + " file: " + file);
                                ConversationReceived cr = daoManager.convarsationReceivedDB.findByKey(fm.getKey());
                                cr.setText(cr.getText() + " - Arquivo não Associado!\n\n" + jids);
                                daoManager.convarsationReceivedDB.save(cr);
                                idsMessages.add(cr.getIdMessage());
                                //
                                saveContactMessageConversation(file, ConversationReceived.Type.Gif, daoManager);
                            } else {
                                LogUtils.writeLog(this, LogUtils.TAG_STORAGE, "associado Gif-> jid: " + fm.getJid() + " file: " + file);
                                ConversationReceived cr = daoManager.convarsationReceivedDB.findByKey(fm.getKey());
                                cr.setFile(file);
                                daoManager.convarsationReceivedDB.save(cr);
                                idsMessages.add(cr.getIdMessage());
                            }
                            posGifs--;
                        }
                    } else {
                        for (FileMessage fm : filesGifs) {
                            LogUtils.writeLog(this, LogUtils.TAG_STORAGE, "inconsistencia Gif-> jid: " + fm.getJid());
                            ConversationReceived cr = daoManager.convarsationReceivedDB.findByKey(fm.getKey());
                            cr.setText(cr.getText() + " - Arquivo não Baixado!\n");
                            daoManager.convarsationReceivedDB.save(cr);
                            idsMessages.add(cr.getIdMessage());
                        }
                    }
                    if (imageValid) {
                        for (FileMessage fm : filesImages) {
                            String file = FileUtils.getNameLastFileImage(posImages);
                            String jids = "Prováveis Contatos: ";
                            boolean achou = false;
                            for (FileMessage fm1 : filesImages) {
                                if (!fm1.getKey().equals(fm.getKey()) && fm.getRoud() == fm1.getRoud() && !fm.getJid().equals(fm1.getJid())) {
                                    achou = true;
                                    jids = jids + fm1.getJid() + "\n";
                                }
                            }
                            if (achou) {
                                LogUtils.writeLog(this, LogUtils.TAG_STORAGE, "inconsistencia Image-> jid: " + fm.getJid() + " file: " + file);
                                ConversationReceived cr = daoManager.convarsationReceivedDB.findByKey(fm.getKey());
                                cr.setText(cr.getText() + " - Arquivo não Associado!\n\n" + jids);
                                daoManager.convarsationReceivedDB.save(cr);
                                idsMessages.add(cr.getIdMessage());
                                //
                                saveContactMessageConversation(file, ConversationReceived.Type.Image, daoManager);
                            } else {
                                LogUtils.writeLog(this, LogUtils.TAG_STORAGE, "associado Image-> jid: " + fm.getJid() + " file: " + file);
                                ConversationReceived cr = daoManager.convarsationReceivedDB.findByKey(fm.getKey());
                                cr.setFile(file);
                                daoManager.convarsationReceivedDB.save(cr);
                                idsMessages.add(cr.getIdMessage());
                            }
                            posImages--;
                        }
                    } else {
                        for (FileMessage fm : filesImages) {
                            LogUtils.writeLog(this, LogUtils.TAG_STORAGE, "inconsistencia Image-> jid: " + fm.getJid());
                            ConversationReceived cr = daoManager.convarsationReceivedDB.findByKey(fm.getKey());
                            cr.setText(cr.getText() + " - Arquivo não Baixado!\n");
                            daoManager.convarsationReceivedDB.save(cr);
                            idsMessages.add(cr.getIdMessage());
                        }
                    }
                    if (videoValid) {
                        for (FileMessage fm : filesVideos) {
                            String file = FileUtils.getNameLastFileVideo(posVideos);
                            String jids = "Prováveis Contatos: ";
                            boolean achou = false;
                            for (FileMessage fm1 : filesVideos) {
                                if (!fm1.getKey().equals(fm.getKey()) && fm.getRoud() == fm1.getRoud() && !fm.getJid().equals(fm1.getJid())) {
                                    achou = true;
                                    jids = jids + fm1.getJid() + "\n";
                                }
                            }
                            if (achou) {
                                LogUtils.writeLog(this, LogUtils.TAG_STORAGE, "inconsistencia Video-> jid: " + fm.getJid() + " file: " + file);
                                ConversationReceived cr = daoManager.convarsationReceivedDB.findByKey(fm.getKey());
                                cr.setText(cr.getText() + " - Arquivo não Associado!\n\n" + jids);
                                daoManager.convarsationReceivedDB.save(cr);
                                idsMessages.add(cr.getIdMessage());
                                //
                                saveContactMessageConversation(file, ConversationReceived.Type.Video, daoManager);
                            } else {
                                LogUtils.writeLog(this, LogUtils.TAG_STORAGE, "associado Video-> jid: " + fm.getJid() + " file: " + file);
                                ConversationReceived cr = daoManager.convarsationReceivedDB.findByKey(fm.getKey());
                                cr.setFile(file);
                                daoManager.convarsationReceivedDB.save(cr);
                                idsMessages.add(cr.getIdMessage());
                            }
                            posVideos--;
                        }
                    } else {
                        for (FileMessage fm : filesVideos) {
                            LogUtils.writeLog(this, LogUtils.TAG_STORAGE, "inconsistencia Video-> jid: " + fm.getJid());
                            ConversationReceived cr = daoManager.convarsationReceivedDB.findByKey(fm.getKey());
                            cr.setText(cr.getText() + " - Arquivo não Baixado!\n");
                            daoManager.convarsationReceivedDB.save(cr);
                            idsMessages.add(cr.getIdMessage());
                        }
                    }
                    if (voiceNoteValid) {
                        for (FileMessage fm : filesVoiceNotes) {
                            String file = FileUtils.getNameLastFileVoiceNotes(posVoiceNotes);
                            String jids = "Prováveis Contatos: ";
                            boolean achou = false;
                            for (FileMessage fm1 : filesVoiceNotes) {
                                if (!fm1.getKey().equals(fm.getKey()) && fm.getRoud() == fm1.getRoud() && !fm.getJid().equals(fm1.getJid())) {
                                    achou = true;
                                    jids = jids + fm1.getJid() + "\n";
                                }
                            }
                            if (achou) {
                                LogUtils.writeLog(this, LogUtils.TAG_STORAGE, "inconsistencia VoiceNote-> jid: " + fm.getJid() + " file: " + file);
                                ConversationReceived cr = daoManager.convarsationReceivedDB.findByKey(fm.getKey());
                                cr.setText(cr.getText() + " - Arquivo não Associado!\n\n" + jids);
                                daoManager.convarsationReceivedDB.save(cr);
                                idsMessages.add(cr.getIdMessage());
                                //
                                saveContactMessageConversation(file, ConversationReceived.Type.VoiceNote, daoManager);
                            } else {
                                LogUtils.writeLog(this, LogUtils.TAG_STORAGE, "associado VoiceNote-> jid: " + fm.getJid() + " file: " + file);
                                ConversationReceived cr = daoManager.convarsationReceivedDB.findByKey(fm.getKey());
                                cr.setFile(file);
                                daoManager.convarsationReceivedDB.save(cr);
                                idsMessages.add(cr.getIdMessage());
                            }
                            posVoiceNotes--;
                        }
                    } else {
                        for (FileMessage fm : filesVoiceNotes) {
                            LogUtils.writeLog(this, LogUtils.TAG_STORAGE, "inconsistencia VoiceNote-> jid: " + fm.getJid());
                            ConversationReceived cr = daoManager.convarsationReceivedDB.findByKey(fm.getKey());
                            cr.setText(cr.getText() + " - Arquivo não Baixado!\n");
                            daoManager.convarsationReceivedDB.save(cr);
                            idsMessages.add(cr.getIdMessage());
                        }
                    }
                    for (Long id : idsMessages){
                        MessageReceived messageReceived = daoManager.messageReceivedDB.findById(id);
                        if (messageReceived != null){
                            messageReceived.setStatus(MessageReceived.Status.Waiting);
                            daoManager.messageReceivedDB.save(messageReceived);
                        }
                    }
                }
            }
            //
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
            builder.setSmallIcon(android.support.v7.appcompat.R.drawable.notification_icon_background);
            builder.setContentTitle("PendindIntent onRead");
            builder.setContentText("Send All PendindIntent onRead");
            builder.setTicker(KeyUtils.KEY_ACTION_PENDIND_INTENT_SEND);
            builder.setAutoCancel(true);
            NotificationManagerCompat nm = NotificationManagerCompat.from(this);
            nm.notify(KeyUtils.ID_PENDIND_INTENT_ON_READ, builder.build());
        }
        LogUtils.writeLog(this, LogUtils.TAG_STORAGE, "StorageService onHandleIntent Stop");

    }

    @Override
    public void onCreate() {
        LogUtils.writeLog(this, LogUtils.TAG_STORAGE, " -------------------------- StorageService: onCreate --------------------------------------");
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        LogUtils.writeLog(this, LogUtils.TAG_STORAGE, " -------------------------- StorageService: onDestroy ----------------------------------------");
        super.onDestroy();
    }

    private void saveContactMessageConversation(String file, ConversationReceived.Type type, DaoManager daoManager){
        Contact contact = new Contact();
        contact.setContactName("Arquivo não Encontrado");
        contact.setJidWhatsApp("9999999999999@s.whatsapp.net");
        MessageReceived messageReceived = new MessageReceived();
        Date date = new Date();
        messageReceived.setTimeStamp(date.getTime());
        ConversationReceived conversationReceived = new ConversationReceived();
        conversationReceived.setText("Arquivo de "+type.name()+" não associado!");
        conversationReceived.setType(type);
        conversationReceived.setFile(file);
        messageReceived.setContact(contact);
        messageReceived.addConversation(conversationReceived);
        Contact contactStorage = daoManager.contactDB.findByIdWhatsApp(contact.getJidWhatsApp());
        long idContact;
        if (contactStorage != null) {
            idContact = contactStorage.getId();
        } else {
            idContact = daoManager.contactDB.save(contact);
        }
        contact.setId(idContact);
        LogUtils.writeLog(this, LogUtils.TAG_STORAGE, "StorageService Contact: " + contact);
        if (idContact > 0) {
            messageReceived.setIdContact(idContact);
            MessageReceived messageReceivedStorage = daoManager.messageReceivedDB.findMessagePendindByIdContact(idContact);
            long idMessage;
            if (messageReceivedStorage != null) {
                idMessage = messageReceivedStorage.getId();
            } else {
                idMessage = daoManager.messageReceivedDB.save(messageReceived);
            }
            messageReceived.setId(idMessage);
            daoManager.messageReceivedDB.save(messageReceived);
            LogUtils.writeLog(this, LogUtils.TAG_STORAGE, "StorageService MessageReceived: " + messageReceived);
            if (idMessage > 0) {
                conversationReceived.setOrder(1);
                conversationReceived.setIdMessage(idMessage);
                long idConversation = daoManager.convarsationReceivedDB.save(conversationReceived);
                conversationReceived.setId(idConversation);
                LogUtils.writeLog(this, LogUtils.TAG_STORAGE, "StorageService Conversation: " + conversationReceived);
                messageReceived.setStatus(MessageReceived.Status.Waiting);
                daoManager.messageReceivedDB.save(messageReceived);
            }
        }
    }
}
