package br.com.infobella.whatomail.whatomail.modelo;

/**
 * Created by HENRI on 23/04/2017.
 */

public class Config {
    //Ativação de Log
    public static final boolean LOG = true;
    public static final boolean LOG_FILE = false;
    //Valores padrão do Whatomail
    public static final String VALUE_SENDER = "Whatomail";
    public static final String VALUE_SUBJECT = "Whatomail: Conversas Recebidas via WhatsApp";
    //Dados da Conta de Email que faz o envio
    //Valores Padrões de configuração
    public static final String VALUE_TRUE = "true";
    public static final String VALUE_FALSE = "false";
    //Propriedades
    public static final String PROPS_MAIL_SMTP_HOST = "mail.smtp.host";
    public static final String PROPS_MAIL_SMTP_AUTH = "mail.smtp.auth";
    public static final String PROPS_MAIL_SMTP_QUIT_WAIT = "mail.smtp.quitwait";
    public static final String PROPS_MAIL_SMTP_START_TLS_ENABLE = "mail.smtp.starttls.enable";
    public static final String PROPS_MAIL_SMTP_PORT = "mail.smtp.port";
    public static final String PROPS_MAIL_SMTP_SSL_TRUST = "mail.smtp.ssl.trust";
    //Configuração de Acesso FreshDesk
    public static final boolean SEND_FRESHDESK = true;

}
