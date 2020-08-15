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
    public static final String VALUE_SUBJECT_WITH_CONTROLADO = "[CONTROLADO] Whatomail: Conversas Recebidas via WhatsApp";
    //Dados da Conta de Email que faz o envio
    //************* Niterói
    public static final String VALUE_EMAIL_FROM = "drogavetniteroi2@gmail.com";
    public static final String VALUE_PASSWORD = "dv280213";
    //************* Vitória
    //public static final String VALUE_EMAIL_FROM = "drogavetvitoria@gmail.com";
    //public static final String VALUE_PASSWORD = "Drogavetvt2547*";
    //*********** João Pessoa
    //public static final String VALUE_EMAIL_FROM = "receitaswhatsapp@gmail.com";
    //public static final String VALUE_PASSWORD = "999999999";
    //**********************
    public static final String VALUE_SERVER_PORT = "587";
    public static final String VALUE_SERVER_SMTP = "smtp.gmail.com";
    //Destinatários
    public static final String VALUE_EMAIL_COPY = "whatomaildrogavetniteroi@gmail.com";
    public static final String VALUE_EMAIL_TO = "drogavetniteroi@drogavet.com";
    //public static final String VALUE_EMAIL_COPY = "whatomaildrogavetniteroi@gmail.com";
    //public static final String VALUE_EMAIL_TO = "drogavetniteroi@drogavet.com";
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
    //*********** Niter[oi
    public static final String apiToken = "deJFCMfs4Zy6SdzlpNJl";
    public static final String apiEndpoint = "http://drogavetniteroi.freshdesk.com";
    //************** Vit[oria
    //public static final String apiToken = "EhWLwrQNpSJd1UWSVtW8";
    //public static final String apiEndpoint = "http://drogavetvitoria.freshdesk.com";
    //************* João Pessoa
    //public static final String apiToken = "Fao7blZR6HFH5sTfU3Xi";
    //public static final String apiEndpoint = "http://drogavetjoaopessoa.freshdesk.com";
}
