package br.com.infobella.whatomail.whatomail.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationManagerCompat;
import android.text.SpannableString;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.com.infobella.whatomail.whatomail.modelo.Config;
import br.com.infobella.whatomail.whatomail.modelo.Contact;
import br.com.infobella.whatomail.whatomail.modelo.ConversationReceived;
import br.com.infobella.whatomail.whatomail.modelo.FileMessage;
import br.com.infobella.whatomail.whatomail.modelo.FilesMessagePack;
import br.com.infobella.whatomail.whatomail.modelo.LastFiles;
import br.com.infobella.whatomail.whatomail.modelo.MessageReceived;
import utils.AlarmUtils;
import utils.FileUtils;
import utils.GenerateCode;
import utils.HttpUtils;
import utils.JobUtils;
import utils.KeyUtils;
import utils.LogUtils;

/*
 * Created by HENRI on 14/03/2017.
 */

public class NotificationListenerService extends android.service.notification.NotificationListenerService {

    public static List<PendingIntent> pendingIntentList;
    Intent intentAlarm;
    Set<String> numbers = new HashSet<>();

    private int countRound;

    private Map<String, ConversationReceived.Type[]> mapsTypes;
    private Map<String, Long> mapsTimes;
    private List<FileMessage> filesMessages;

    private boolean waitConnected;
    private boolean waitDisconnected;

    private LastFiles lastFiles;

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.writeLog(this, LogUtils.TAG_LISTENER, "Inside on create");
        pendingIntentList = new ArrayList<>();

        countRound = 0;

        intentAlarm = new Intent(KeyUtils.ACTION_PENDIND_INTENT_ON_READ);
        AlarmUtils.scheduleRepeat(this, intentAlarm, AlarmUtils.getTime(3 * 60), 3 * 60 * 1000);

        FileUtils.criarPastas();

        mapsTypes = new HashMap<>();
        mapsTimes = new HashMap<>();
        filesMessages = new ArrayList<>();

        waitConnected = false;
        waitDisconnected = false;

        lastFiles = new LastFiles();
        lastFiles.setLastFileAudio(FileUtils.getNameLastFileAudio(0));
        lastFiles.setLastFileDocument(FileUtils.getNameLastFileDocuments(0));
        lastFiles.setLastFileGif(FileUtils.getNameLastFileAnimatedGifs(0));
        lastFiles.setLastFileImage(FileUtils.getNameLastFileImage(0));
        lastFiles.setLastFileVoiceNote(FileUtils.getNameLastFileVoiceNotes(0));
        lastFiles.setLastFileVideo(FileUtils.getNameLastFileVideo(0));

        numbers.add("1");
        numbers.add("2");
        numbers.add("3");
        numbers.add("4");
        numbers.add("5");
        numbers.add("6");
        numbers.add("7");
        numbers.add("8");
        numbers.add("9");
        numbers.add("0");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.writeLog(this, LogUtils.TAG_LISTENER, "Inside on destroy");
        AlarmUtils.cancel(this, intentAlarm);
        JobUtils.calcelAll(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        LogUtils.writeLog(this, LogUtils.TAG_LISTENER, "onBind: " + intent.toString());
        return super.onBind(intent);
    }

    public void onNotificationPosted(StatusBarNotification sbn) {
        if (sbn.getPackageName().equals("com.whatsapp")) {
            Notification notification = sbn.getNotification();
            if (sbn.getTag() != null) {
                if (notification.extras != null) {
                    Bundle bundle = notification.extras;
                    LogUtils.writeLog(this, LogUtils.TAG_LISTENER, "#1 ------------------------------- DADOS VÁLIDOS ----------------------------------------");
                    LogUtils.writeLog(this, LogUtils.TAG_LISTENER, "countRound: " + countRound);
                    Contact contact = new Contact();
                    MessageReceived messageReceived = new MessageReceived();
                    String contentTitle = bundle.getString(Notification.EXTRA_TITLE);
                    contact.setContactName(contentTitle);
                    contact.setJidWhatsApp(sbn.getTag());
                    LogUtils.writeLog(this, LogUtils.TAG_LISTENER, "sbn Tag: " + sbn.getTag());
                    LogUtils.writeLog(this, LogUtils.TAG_LISTENER, "contentTitle: " + contentTitle);
                    Bundle bundleCar = bundle.getBundle("android.car.EXTENSIONS");
                    if (bundleCar != null) {
                        Bundle bundleConversation = bundleCar.getBundle("car_conversation");
                        if (bundleConversation != null) {
                            long lastTime;
                            ConversationReceived.Type [] types;
                            if (mapsTypes.containsKey(sbn.getTag())){
                                types = mapsTypes.get(sbn.getTag());
                            } else {
                                types = new ConversationReceived.Type[]{null, null, null, null, null, null, null};
                            }
                            if (mapsTimes.containsKey(sbn.getTag())){
                                lastTime = mapsTimes.get(sbn.getTag());
                            } else {
                                lastTime = 0L;
                            }
                            LogUtils.writeLog(this, LogUtils.TAG_LISTENER, "lasttime antes: " + lastTime);
                            LogUtils.writeLog(this, LogUtils.TAG_LISTENER, "types antes: " + printTypes(types));
                            Long timestamp = (Long) bundleConversation.get("timestamp");
                            if (timestamp != null) {
                                messageReceived.setTimeStamp(timestamp);
                                if (lastTime == 0L){
                                    mapsTimes.put(sbn.getTag(), timestamp);
                                    lastTime = timestamp;
                                } else {
                                    if (lastTime != timestamp){
                                        mapsTimes.put(sbn.getTag(), timestamp);
                                        types = move(types);
                                    }
                                }
                            }
                            LogUtils.writeLog(this, LogUtils.TAG_LISTENER, "lasttime depois: " + lastTime);
                            LogUtils.writeLog(this, LogUtils.TAG_LISTENER, "types meio: " + printTypes(types));
                            Parcelable[] messages = (Parcelable[]) bundleConversation.get("messages");
                            if (messages != null) {
                                int countMessage = 0;
                                for (Parcelable m : messages) {
                                    Bundle bundleMessages = (Bundle) m;
                                    if (bundleMessages != null) {
                                        String text = (String) bundleMessages.get("text");
                                        ConversationReceived conversationReceived = new ConversationReceived();
                                        ConversationReceived.Type type =  conversationReceived.setTextReturnType(text);
                                        conversationReceived.setType(type);
                                        if (type == ConversationReceived.Type.Text){
                                            messageReceived.addConversation(conversationReceived);
                                            if (types[countMessage] == null){
                                                types[countMessage] = type;
                                            }
                                        } else {
                                            if (types[countMessage] == null) {
                                                types[countMessage] = type;
                                                FileMessage fileMessage = new FileMessage();
                                                fileMessage.setType(type);
                                                fileMessage.setRoud(countRound);
                                                fileMessage.setJid(sbn.getTag());
                                                String key = getNextKey(sbn.getTag());
                                                LogUtils.writeLog(this, LogUtils.TAG_LISTENER, "key: " + key);
                                                fileMessage.setKey(key);
                                                conversationReceived.setKey(key);
                                                messageReceived.setStatus(MessageReceived.Status.WaitingFile);
                                                messageReceived.addConversation(conversationReceived);
                                                filesMessages.add(fileMessage);
                                            }
                                        }
                                        countMessage++;
                                        LogUtils.writeLog(this, LogUtils.TAG_LISTENER, "BundleConversation-" + "messages" + ": " + "text: " + text);
                                    }//BundleMessages diferente de Null
                                }//Para toda Message
                            }//Messages diferente de Null
                            LogUtils.writeLog(this, LogUtils.TAG_LISTENER, "types depois: " + printTypes(types));
                            messageReceived.setContact(contact);
                            if (messageReceived.isValido()) {
                                mapsTypes.put(sbn.getTag(), types);
                                LogUtils.writeLog(this, LogUtils.TAG_LISTENER, "MSG: " + messageReceived.toString());
                                PendingIntent on_read = (PendingIntent) bundleConversation.get("on_read");
                                if (on_read != null) {
                                    pendingIntentList.add(on_read);
                                }
                                LogUtils.writeLog(this, LogUtils.TAG_LISTENER, "Chamar Service Storage ");
                                Intent intent = new Intent(this, StorageMessageReceivedService.class);
                                intent.putExtra(KeyUtils.KEY_ACTION_STORAGE, KeyUtils.KEY_ACTION_STORAGE_WRITE);
                                intent.putExtra(KeyUtils.KEY_MESSAGE_PACK, messageReceived);
                                startService(intent);
                            } else {
                                Set<String> keys = new HashSet<>();
                                List<ConversationReceived> list = messageReceived.getConversations();
                                for (ConversationReceived c : list){
                                    if (c.getKey() != null){
                                        keys.add(c.getKey());
                                    }
                                }
                                List<FileMessage> newList = new ArrayList<>();
                                for (FileMessage fm: filesMessages){
                                    if (!keys.contains(fm.getKey())){
                                        newList.add(fm);
                                    }
                                }
                                filesMessages = newList;
                            }
                        }//BundleConversation NULO
                    }//BundleCar NULO
                }
            } else {
                if (notification.extras != null) {
                    Bundle bundle = notification.extras;
                    String template = bundle.getString(Notification.EXTRA_TEMPLATE);
                    if (template != null && !template.equals("android.app.Notification$InboxStyle")) {
                        LogUtils.writeLog(this, LogUtils.TAG_LISTENER, "##################### New Roud (Start) ###############################");
                        if (notification.tickerText != null) {
                            String tickerText = (String) notification.tickerText;
                            int pos = tickerText.indexOf(" @ ");
                            if (pos == -1) {
                                LogUtils.writeLog(this, LogUtils.TAG_LISTENER, "#2 ------------------------------- DADOS VÁLIDOS ----------------------------------------");
                                LogUtils.writeLog(this, LogUtils.TAG_LISTENER, "countRound: " + countRound);
                                Contact contact = new Contact();
                                MessageReceived messageReceived = new MessageReceived();
                                String contentTitle = bundle.getString(Notification.EXTRA_TITLE);
                                LogUtils.writeLog(this, LogUtils.TAG_LISTENER, "contentTitle: " + contentTitle);
                                contact.setContactName(contentTitle);
                                String[] people = (String[]) bundle.get(Notification.EXTRA_PEOPLE);
                                if (people != null) {
                                    for (String aPeople : people) {
                                        contact.setJidFromPeople(this, aPeople);
                                        LogUtils.writeLog(this, LogUtils.TAG_LISTENER, "people: " + aPeople);
                                    }
                                }
                                Bundle bundleCar = bundle.getBundle("android.car.EXTENSIONS");
                                if (bundleCar != null) {
                                    Bundle bundleConversation = bundleCar.getBundle("car_conversation");
                                    if (bundleConversation != null) {
                                        long lastTime;
                                        ConversationReceived.Type [] types;
                                        if (mapsTypes.containsKey(contact.getJidWhatsApp())){
                                            types = mapsTypes.get(contact.getJidWhatsApp());
                                        } else {
                                            types = new ConversationReceived.Type[]{null, null, null, null, null, null, null};
                                        }
                                        if (mapsTimes.containsKey(contact.getJidWhatsApp())){
                                            lastTime = mapsTimes.get(contact.getJidWhatsApp());
                                        } else {
                                            lastTime = 0L;
                                        }
                                        LogUtils.writeLog(this, LogUtils.TAG_LISTENER, "lasttime antes: " + lastTime);
                                        LogUtils.writeLog(this, LogUtils.TAG_LISTENER, "types antes: " + printTypes(types));
                                        Long timestamp = (Long) bundleConversation.get("timestamp");
                                        if (timestamp != null) {
                                            messageReceived.setTimeStamp(timestamp);
                                            if (lastTime == 0L){
                                                mapsTimes.put(contact.getJidWhatsApp(), timestamp);
                                                lastTime = timestamp;
                                            } else {
                                                if (lastTime != timestamp){
                                                    mapsTimes.put(contact.getJidWhatsApp(), timestamp);
                                                    types = move(types);
                                                }
                                            }
                                        }
                                        LogUtils.writeLog(this, LogUtils.TAG_LISTENER, "lasttime depois: " + lastTime);
                                        LogUtils.writeLog(this, LogUtils.TAG_LISTENER, "types meio: " + printTypes(types));
                                        Parcelable[] messages = (Parcelable[]) bundleConversation.get("messages");
                                        if (messages != null) {
                                            int countMessage = 0;
                                            for (Parcelable m : messages) {
                                                Bundle bundleMessages = (Bundle) m;
                                                if (bundleMessages != null) {
                                                    String text = (String) bundleMessages.get("text");
                                                    ConversationReceived conversationReceived = new ConversationReceived();
                                                    ConversationReceived.Type type =  conversationReceived.setTextReturnType(text);
                                                    conversationReceived.setType(type);
                                                    if (type == ConversationReceived.Type.Text){
                                                        messageReceived.addConversation(conversationReceived);
                                                        if (types[countMessage] == null){
                                                            types[countMessage] = type;
                                                        }
                                                    } else {
                                                        if (types[countMessage] == null) {
                                                            types[countMessage] = type;
                                                            FileMessage fileMessage = new FileMessage();
                                                            fileMessage.setType(type);
                                                            fileMessage.setRoud(countRound);
                                                            fileMessage.setJid(contact.getJidWhatsApp());
                                                            String key = getNextKey(contact.getJidWhatsApp());
                                                            LogUtils.writeLog(this, LogUtils.TAG_LISTENER, "key: " + key);
                                                            fileMessage.setKey(key);
                                                            conversationReceived.setKey(key);
                                                            messageReceived.setStatus(MessageReceived.Status.WaitingFile);
                                                            messageReceived.addConversation(conversationReceived);
                                                            filesMessages.add(fileMessage);
                                                        }
                                                    }
                                                    countMessage++;
                                                    LogUtils.writeLog(this, LogUtils.TAG_LISTENER, "BundleConversation-" + "messages" + ": " + "text: " + text);
                                                }
                                            }
                                        }
                                        LogUtils.writeLog(this, LogUtils.TAG_LISTENER, "types depois: " + printTypes(types));
                                        messageReceived.setContact(contact);
                                        //LogUtils.writeLog(this, LogUtils.TAG_LISTENER, messageReceived.toString());
                                        if (messageReceived.isValido()) {
                                            mapsTypes.put(contact.getJidWhatsApp(), types);
                                            LogUtils.writeLog(this, LogUtils.TAG_LISTENER, "MSG: " + messageReceived.toString());
                                            PendingIntent on_read = (PendingIntent) bundleConversation.get("on_read");
                                            if (on_read != null) {
                                                pendingIntentList.add(on_read);
                                            }
                                            LogUtils.writeLog(this, LogUtils.TAG_LISTENER, "Chamar Service Storage ");
                                            Intent intent = new Intent(this, StorageMessageReceivedService.class);
                                            intent.putExtra(KeyUtils.KEY_ACTION_STORAGE, KeyUtils.KEY_ACTION_STORAGE_WRITE);
                                            intent.putExtra(KeyUtils.KEY_MESSAGE_PACK, messageReceived);
                                            startService(intent);
                                        } else {
                                            Set<String> keys = new HashSet<>();
                                            List<ConversationReceived> list = messageReceived.getConversations();
                                            for (ConversationReceived c : list){
                                                if (c.getKey() != null){
                                                    keys.add(c.getKey());
                                                }
                                            }
                                            List<FileMessage> newList = new ArrayList<>();
                                            for (FileMessage fm: filesMessages){
                                                if (!keys.contains(fm.getKey())){
                                                    newList.add(fm);
                                                }
                                            }
                                            filesMessages = newList;
                                        }
                                    }//BundleConversation NULO
                                }//BundleCar NULO
                                countRound++;
                            }//pos != -1 ou seja é uma mensagem de grupo
                        }//TickerText NULO
                        LogUtils.writeLog(this, LogUtils.TAG_LISTENER, "##################### New Roud (Stop) ###############################");
                    } else {//Template NULO ou InboxStyle
                        if (template != null && template.equals("android.app.Notification$InboxStyle")) {
                            countRound++;
                        }
                        LogUtils.writeLog(this, LogUtils.TAG_LISTENER, "##################### New Roud ###############################");
                    }
                }//Notification NULO
            }//Senão TAG NULO

            //

        } else if (sbn.getPackageName().equals("br.com.infobella.whatomail.whatomail")) {
            Notification notification = sbn.getNotification();
            LogUtils.writeLog(this, LogUtils.TAG_LISTENER, "Notification Whatomail Recebida ");
            String tickerText = (String) notification.tickerText;
            NotificationManagerCompat nm = NotificationManagerCompat.from(this);
            nm.cancel(sbn.getId());
            if (tickerText.equals(KeyUtils.KEY_ACTION_PENDIND_INTENT_ON_READ)) {
                LogUtils.writeLog(this, LogUtils.TAG_LISTENER, "Notification ON_READ -> NET_OFF() ");
                //this.netOff();
                this.processFilesMessages();
            } else if (tickerText.equals(KeyUtils.KEY_ACTION_PENDIND_INTENT_NET_ON) && waitConnected){
                LogUtils.writeLog(this, LogUtils.TAG_LISTENER, "Notification NET_ON -> NET_ON() ");
                this.netOn();
            } else if (tickerText.equals(KeyUtils.KEY_ACTION_PENDIND_INTENT_NET_OFF) && waitDisconnected){
                LogUtils.writeLog(this, LogUtils.TAG_LISTENER, "Notification ON_FF -> PROCESS_FILES_MESSAGES() ");
                //this.processFilesMessages();
            } else if (tickerText.equals(KeyUtils.KEY_ACTION_PENDIND_INTENT_SEND)){
                LogUtils.writeLog(this, LogUtils.TAG_LISTENER, "Notification SEND -> CONFIRM_MESSAGE_RECEIVED() ");
                this.confirmMessageReceived();
            }
        } else if (sbn.getPackageName().equals("com.google.android.gm")) {
            Notification notification = sbn.getNotification();
            if (notification != null && notification.extras != null) {
                Bundle bundle = notification.extras;
                String[] people = (String[]) bundle.get(Notification.EXTRA_PEOPLE);
                if (people != null && people.length > 0) {
                    String remetente = people[0]; // equals: 'mailto:drogavetniteroi@drogavet.com'
                    String mailto = "mailto:"+ Config.VALUE_EMAIL_TO;
                    if (remetente.equals(mailto)){
                        String subText = bundle.getString(Notification.EXTRA_SUB_TEXT);
                        if (subText != null && subText.equals(Config.VALUE_EMAIL_FROM)){
                            String text = null;
                            try {
                                android.text.SpannableString tx = (SpannableString) bundle.get(Notification.EXTRA_TEXT);
                                if (tx != null) {
                                    text = tx.toString();
                                }
                            } catch (Exception ex) {
                                text = bundle.getString(Notification.EXTRA_TEXT);
                            }
                            if (text != null) {
                                String bigText = null;// contains: '#TICKET: 16667'
                                try {
                                    android.text.SpannableString tx = (SpannableString) bundle.get(Notification.EXTRA_BIG_TEXT);
                                    if (tx != null) {
                                        bigText = tx.toString();
                                    }
                                } catch (Exception ex) {
                                    bigText = bundle.getString(Notification.EXTRA_BIG_TEXT);
                                }
                                if (bigText != null){
                                    int posTickect = bigText.indexOf("#TICKET:");
                                    if (posTickect != -1){
                                        posTickect = posTickect + 9;
                                        int posTicketFim = posTickect;
                                        while (numbers.contains(bigText.substring(posTicketFim, posTicketFim+1))){
                                            posTicketFim++;
                                        }
                                        if (posTicketFim != posTickect) {
                                            String ticket = bigText.substring(posTickect, posTicketFim);
                                            if (text.contains("Resposta Drogavet -")){
                                                int posSubject = text.indexOf("Resposta Drogavet -");
                                                String subject = text.substring(posSubject + 20);
                                                int posIni = posTicketFim + 3 + subject.length();
                                                int posFim = bigText.indexOf("Niterói - RJ");
                                                String textMsg;
                                                if (posFim != -1){
                                                    textMsg = bigText.substring(posIni, posFim);
                                                } else {
                                                    textMsg = bigText.substring(posIni);
                                                }
                                                Intent intent = new Intent(this, MessageReplyService.class);
                                                intent.putExtra(KeyUtils.KEY_REPLY_TICKET, ticket);
                                                intent.putExtra(KeyUtils.KEY_REPLY_TEXT, textMsg);
                                                startService(intent);
                                                //Arquivar
                                                Notification.Action[] actions;
                                                if (notification.actions != null) {
                                                    actions = notification.actions;
                                                    for (Notification.Action action : actions) {
                                                        String actionTitle = (String) action.title;
                                                        if (actionTitle.equals("Arquivar")) {
                                                            try {
                                                                action.actionIntent.send();
                                                            } catch (PendingIntent.CanceledException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    }
                                                }
                                            } else if (text.contains("Ticket atualizado -")){
                                                int posIni = bigText.indexOf("Comentário do TICKET");
                                                posIni = posIni + 20;
                                                int posFim = bigText.indexOf("Niterói - RJ");
                                                String textMsg;
                                                if (posFim != -1){
                                                    textMsg = bigText.substring(posIni, posFim);
                                                } else {
                                                    textMsg = bigText.substring(posIni);
                                                }
                                                Intent intent = new Intent(this, MessageReplyService.class);
                                                intent.putExtra(KeyUtils.KEY_REPLY_TICKET, ticket);
                                                intent.putExtra(KeyUtils.KEY_REPLY_TEXT, textMsg);
                                                startService(intent);
                                                //Arquivar
                                                Notification.Action[] actions;
                                                if (notification.actions != null) {
                                                    actions = notification.actions;
                                                    for (Notification.Action action : actions) {
                                                        String actionTitle = (String) action.title;
                                                        if (actionTitle.equals("Arquivar")) {
                                                            try {
                                                                action.actionIntent.send();
                                                            } catch (PendingIntent.CanceledException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        //TAG = "onNotificationRemoved";
        //LogUtils.writeLog(this, LogUtils.TAG_LISTENER, " id = " + sbn.getId() + " Package Name: " + sbn.getPackageName() +
        //        " Post time = " + sbn.getPostTime() + " Tag = " + sbn.getTag());

    }

    private void processFilesMessages(){
        LogUtils.writeLog(this, LogUtils.TAG_LISTENER, "netOff ");
        if (!pendingIntentList.isEmpty()) {
            waitDisconnected = true;
            waitConnected = false;
            while (HttpUtils.isNetworkAvailable(this)) {
                HttpUtils.updateAPN(this, false);
                HttpUtils.updateWifi(this, false);
            }

            LogUtils.writeLog(this, LogUtils.TAG_LISTENER, "ProcessFileMessages ");
            Intent intent = new Intent(this, StorageMessageReceivedService.class);
            intent.putExtra(KeyUtils.KEY_ACTION_STORAGE, KeyUtils.KEY_ACTION_STORAGE_FILES);
            intent.putExtra(KeyUtils.KEY_FILES_MESSAGE, new FilesMessagePack(filesMessages));
            intent.putExtra(KeyUtils.KEY_LAST_FILES, lastFiles);
            startService(intent);
        } else {
            if (HttpUtils.isNetworkAvailable(this)) {
                Intent intent = new Intent(this, StorageMessageReceivedService.class);
                intent.putExtra(KeyUtils.KEY_ACTION_STORAGE, KeyUtils.KEY_ACTION_STORAGE_READ);
                startService(intent);
            } else {
                lastFiles.setLastFileAudio(FileUtils.getNameLastFileAudio(0));
                lastFiles.setLastFileDocument(FileUtils.getNameLastFileDocuments(0));
                lastFiles.setLastFileGif(FileUtils.getNameLastFileAnimatedGifs(0));
                lastFiles.setLastFileImage(FileUtils.getNameLastFileImage(0));
                lastFiles.setLastFileVoiceNote(FileUtils.getNameLastFileVoiceNotes(0));
                lastFiles.setLastFileVideo(FileUtils.getNameLastFileVideo(0));

                HttpUtils.updateWifi(this, true);
            }
        }
    }

    private void confirmMessageReceived(){
        LogUtils.writeLog(this, LogUtils.TAG_LISTENER, "ConfirmMessageReceived ");
        for (PendingIntent pendingIntent: pendingIntentList){
            try {
                pendingIntent.send();
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }
        }

        pendingIntentList = new ArrayList<>();

        mapsTimes = new HashMap<>();
        mapsTypes = new HashMap<>();
        filesMessages = new ArrayList<>();

        if (countRound == 999999999){
            countRound = 0;
        }

        waitConnected = true;
        waitDisconnected = false;

        lastFiles.setLastFileAudio(FileUtils.getNameLastFileAudio(0));
        lastFiles.setLastFileDocument(FileUtils.getNameLastFileDocuments(0));
        lastFiles.setLastFileGif(FileUtils.getNameLastFileAnimatedGifs(0));
        lastFiles.setLastFileImage(FileUtils.getNameLastFileImage(0));
        lastFiles.setLastFileVoiceNote(FileUtils.getNameLastFileVoiceNotes(0));
        lastFiles.setLastFileVideo(FileUtils.getNameLastFileVideo(0));

        JobUtils.scheduleWhifiConnected(this, 1);
        HttpUtils.updateWifi(this, true);
        //HttpUtils.updateAPN(this, true);

    }

    private void netOn() {
        LogUtils.writeLog(this, LogUtils.TAG_LISTENER, "netOn ");
        waitConnected = false;
        waitDisconnected = false;

        JobUtils.cancel(this, 1);

        Intent intent = new Intent(this, StorageMessageReceivedService.class);
        intent.putExtra(KeyUtils.KEY_ACTION_STORAGE, KeyUtils.KEY_ACTION_STORAGE_READ);
        startService(intent);
    }
    /*
    private void netOff(){
        LogUtils.writeLog(this, LogUtils.TAG_LISTENER, "netOff ");
        if (!pendingIntentList.isEmpty()) {
            waitDisconnected = true;
            waitConnected = false;

            HttpUtils.updateAPN(this, false);
            HttpUtils.updateWifi(this, false);
        }
    }
    */
    private ConversationReceived.Type[] move(ConversationReceived.Type [] types){
        LogUtils.writeLog(this, LogUtils.TAG_LISTENER, "move antes: "+printTypes(types));
        System.arraycopy(types, 1, types, 0, types.length - 1);
        types[types.length-1] = null;
        LogUtils.writeLog(this, LogUtils.TAG_LISTENER, "move depois: "+printTypes(types));
        return types;
    }

    private String getNextKey(String jid) {
        Date date = new Date();
        String time = String.valueOf(date.getTime());
        return jid + "@" + time + "@" + GenerateCode.getNextCode();
    }

    private String printTypes(ConversationReceived.Type[] types){
        String f = "[";
        for (int i=0; i<types.length; i++){
            if (i==0){
                if (types[i] != null)
                    f = f + types[i].name();
                else
                    f = f + "null";
            } else {
                if (types[i] != null)
                    f = f + ", " + types[i].name();
                else
                    f = f + ", null";
            }
        }
        f = f + "]";
        return f;
    }
}
