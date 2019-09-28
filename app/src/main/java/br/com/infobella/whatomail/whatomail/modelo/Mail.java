package br.com.infobella.whatomail.whatomail.modelo;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import java.util.List;

/**
 * Created by HENRI on 09/04/2017.
 */

public class Mail {

    public void sendSimpleMail(String text) throws MessagingException {
        Properties props = new Properties();
        props.put(Config.PROPS_MAIL_SMTP_HOST, Config.VALUE_SERVER_SMTP);
        props.put(Config.PROPS_MAIL_SMTP_AUTH, Config.VALUE_TRUE);
        props.setProperty(Config.PROPS_MAIL_SMTP_QUIT_WAIT, Config.VALUE_FALSE);
        props.put(Config.PROPS_MAIL_SMTP_START_TLS_ENABLE, Config.VALUE_TRUE);
        props.put(Config.PROPS_MAIL_SMTP_PORT, Config.VALUE_SERVER_PORT);
        props.put(Config.PROPS_MAIL_SMTP_SSL_TRUST, Config.VALUE_SERVER_SMTP);

        Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator(){
                    protected PasswordAuthentication getPasswordAuthentication(){
                        return new PasswordAuthentication(Config.VALUE_EMAIL_FROM, Config.VALUE_PASSWORD);
                    }
                });

        MimeMessage mm = new MimeMessage(session);
        mm.setFrom(new InternetAddress(Config.VALUE_EMAIL_FROM));
        mm.addRecipient(Message.RecipientType.TO, new InternetAddress(Config.VALUE_EMAIL_TO));
        mm.setSubject(Config.VALUE_SUBJECT);

        BodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setText(text);

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);

        /*
        messageBodyPart = new MimeBodyPart();
        DataSource source = new FileDataSource(filePath);
        messageBodyPart.setDataHandler(new DataHandler(source));
        messageBodyPart.setFileName(filePath);
        multipart.addBodyPart(messageBodyPart);
        */

        mm.setContent(multipart);

        Transport.send(mm);
    }

    public void sendMailHtml(Contact contact, String text, String dataHora, List<String> files) throws MessagingException {

        Properties props = new Properties();
        props.put(Config.PROPS_MAIL_SMTP_HOST, Config.VALUE_SERVER_SMTP);
        props.put(Config.PROPS_MAIL_SMTP_AUTH, Config.VALUE_TRUE);
        props.setProperty(Config.PROPS_MAIL_SMTP_QUIT_WAIT, Config.VALUE_FALSE);
        props.put(Config.PROPS_MAIL_SMTP_START_TLS_ENABLE, Config.VALUE_TRUE);
        props.put(Config.PROPS_MAIL_SMTP_PORT, Config.VALUE_SERVER_PORT);
        props.put(Config.PROPS_MAIL_SMTP_SSL_TRUST, Config.VALUE_SERVER_SMTP);

        Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator(){
                    protected PasswordAuthentication getPasswordAuthentication(){
                        return new PasswordAuthentication(Config.VALUE_EMAIL_FROM, Config.VALUE_PASSWORD);
                    }
                });

        MimeMessage mm = new MimeMessage(session);
        InternetAddress internetAddress;
        try {
            internetAddress = new InternetAddress(Config.VALUE_EMAIL_FROM, Config.VALUE_SENDER, "charset=utf-8");
        } catch (UnsupportedEncodingException e) {
            internetAddress = new InternetAddress(Config.VALUE_EMAIL_FROM);
        }
        mm.setFrom(internetAddress);
        mm.addRecipient(Message.RecipientType.TO, new InternetAddress(Config.VALUE_EMAIL_TO));
        mm.addRecipient(Message.RecipientType.CC, new InternetAddress(Config.VALUE_EMAIL_COPY));
        mm.setSubject(Config.VALUE_SUBJECT);

        String newText = text.replaceAll("\n", "<br>");
        // configura a mensagem para o formato HTML
        String htmlText = "<html> "
                + "<p style='font-family:Calibri, Arial; font-size:20px; color: rgb(31, 73, 125);'>"
                + "Mensagen recebida via WhatsApp"
                + "<br>"
                + "<br>"
                + "<em style='font-weight:bold;'>Nome do Contato:</em> " + contact.getContactName() + "<br>"
                + "<em style='font-weight:bold;'>Telefone do Contato:</em> " + contact.getPhoneFormatted()
                + "<br>"
                + "<br>"
                + "<em style='font-weight:bold;'>Conversas</em><br><br>"
                + newText
                + "<br>"
                + "<br>"
                + "Data e Hora do recebimento da última conversa: " + dataHora
                + "<br>"
                + "<br>"
                + "<em style='font-weight:bold;'>Dados Whatomail</em><br>"
                + "#whatomail-jid:" + contact.getJidWhatsApp() + "<br>"
                + "</html>"
                + "<br>";

        // configure uma mensagem alternativa caso o servidor não suporte HTML
        String simpleText = "Mensagem recebida via WhatsApp"
                + "\n"
                + "\n"
                + "Nome do Contato:  " + contact.getContactName() + "\n"
                + "Telefone do Contato: " + contact.getPhoneFormatted()
                + "\n"
                + "\n"
                + "Conversas"
                + "\n"
                + "\n"
                + text
                + "\n"
                + "\n"
                + "Data e Hora do recebimento da última conversa: " + dataHora
                + "\n"
                + "\n"
                + "Dados WhatoMail"
                + "#whatomail-jid:"+contact.getJidWhatsApp()
                + "\n";

        BodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setText(simpleText);
        messageBodyPart.setContent(htmlText, "text/html; charset=utf-8");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);

        //Adiciona os anexos
        for (String file : files){
            File f = new File(file);
            if (f.exists()) {
                int pos = file.lastIndexOf("/");
                String fileName = file.substring(pos + 1);
                messageBodyPart = new MimeBodyPart();
                DataSource source = new FileDataSource(file);
                messageBodyPart.setDataHandler(new DataHandler(source));
                messageBodyPart.setFileName(fileName);
                multipart.addBodyPart(messageBodyPart);
            }
        }

        //Adiciona o Multipart
        mm.setContent(multipart);
        // envia o e-mail
        Transport.send(mm);
    }
}
