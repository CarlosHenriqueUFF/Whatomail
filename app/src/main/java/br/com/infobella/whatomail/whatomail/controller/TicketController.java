package br.com.infobella.whatomail.whatomail.controller;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.infobella.whatomail.whatomail.modelo.Config;
import br.com.infobella.whatomail.whatomail.modelo.Contact;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpHost;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.auth.AuthScope;
import cz.msebera.android.httpclient.auth.UsernamePasswordCredentials;
import cz.msebera.android.httpclient.client.AuthCache;
import cz.msebera.android.httpclient.client.CredentialsProvider;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.config.AuthSchemes;
import cz.msebera.android.httpclient.client.config.RequestConfig;
import cz.msebera.android.httpclient.client.methods.HttpUriRequest;
import cz.msebera.android.httpclient.client.methods.RequestBuilder;
import cz.msebera.android.httpclient.client.protocol.HttpClientContext;
import cz.msebera.android.httpclient.entity.ContentType;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.entity.mime.HttpMultipartMode;
import cz.msebera.android.httpclient.entity.mime.MultipartEntityBuilder;
import cz.msebera.android.httpclient.impl.auth.AuthSchemeBase;
import cz.msebera.android.httpclient.impl.auth.BasicScheme;
import cz.msebera.android.httpclient.impl.client.BasicAuthCache;
import cz.msebera.android.httpclient.impl.client.BasicCredentialsProvider;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;


/*
 * Created by HENRI on 11/05/2017.
 */

public class TicketController {

    public static Map<String, Object> createTicketWithAttachments(Contact contact, String text, String dataHora, List<String> files, boolean receitaControlada) throws IOException, URISyntaxException {
        final HttpClientBuilder hcBuilder = HttpClientBuilder.create();
        final RequestBuilder reqBuilder = RequestBuilder.post();
        final RequestConfig.Builder rcBuilder = RequestConfig.custom();

        // URL object from API endpoint:
        URL url = new URL(Config.apiEndpoint + "/helpdesk/tickets.json");
        final String urlHost = url.getHost();
        final int urlPort = url.getPort();
        final String urlProtocol = url.getProtocol();
        reqBuilder.setUri(url.toURI());

        // Authentication:
        List<String> authPrefs = new ArrayList<>();
        authPrefs.add(AuthSchemes.BASIC);
        rcBuilder.setTargetPreferredAuthSchemes(authPrefs);
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
                new AuthScope(urlHost, urlPort, AuthScope.ANY_REALM),
                new UsernamePasswordCredentials(Config.apiToken, "X"));
        hcBuilder.setDefaultCredentialsProvider(credsProvider);
        AuthCache authCache = new BasicAuthCache();
        AuthSchemeBase authScheme = new BasicScheme();
        authCache.put(new HttpHost(urlHost, urlPort, urlProtocol), authScheme);
        HttpClientContext hccContext = HttpClientContext.create();
        hccContext.setAuthCache(authCache);

        // Body:
        MultipartEntityBuilder meb = MultipartEntityBuilder.create();
        meb.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

        String newText = text.replaceAll("\n", "<br>");
        // configura a mensagem para o formato HTML
        String htmlText = "<html> "
                + "<p style='font-family:Calibri, Arial; font-size:20px; color: rgb(31, 73, 125);'>"
                + "Mensagen recebida via WhatsApp"
                + "<br>"
                + "<br>"
                + "<em style='font-weight:bold;'>Nome do Contato:</em> " + contact.getContactName() + "<br>"
                + "<em style='font-weight:bold;'>Telefone do Contato:</em> " + contact.getPhone()
                + "<br>"
                + "<br>"
                + "<em style='font-weight:bold;'>Conversas</em><br><br>"
                + newText
                + "<br>"
                + "<br>"
                + "<em style='font-weight:bold;'>Dados Whatomail</em><br>"
                + "#whatomail-jid:" + contact.getJidWhatsApp() + "<br>"
                + "</html>"
                + "<br>";

        meb.addTextBody("helpdesk_ticket[email]", Config.VALUE_EMAIL_FROM);
        if (receitaControlada){
            meb.addTextBody("helpdesk_ticket[subject]", Config.VALUE_SUBJECT_WITH_CONTROLADO);
        } else {
            meb.addTextBody("helpdesk_ticket[subject]", Config.VALUE_SUBJECT);
        }
        meb.addTextBody("helpdesk_ticket[description_html]", htmlText);
        meb.addTextBody("helpdesk_ticket[priority]", "1");
        meb.addTextBody("helpdesk_ticket[status]", "2");
        //meb.addTextBody("helpdesk_ticket[source]", "Ticket description.");
        if (files != null) {
            for (String file : files) {
                File attach1 = new File(file);
                if (attach1.exists()) {
                    meb.addBinaryBody("helpdesk_ticket[attachments][][resource]", attach1,
                            ContentType.TEXT_PLAIN.withCharset("utf-8"), attach1.getName());
                }
            }
        }
        reqBuilder.setEntity(meb.build());

        // Execute:
        RequestConfig rc = rcBuilder.build();
        reqBuilder.setConfig(rc);

        HttpClient hc = hcBuilder.build();
        HttpUriRequest req = reqBuilder.build();
        HttpResponse response = hc.execute(req, hccContext);

        // Print out:
        HttpEntity body = response.getEntity();
        InputStream is = body.getContent();
        BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("utf-8")));
        String line;
        String lines = "";
        while((line=br.readLine())!=null) {
            lines = lines + line;
        }

        lines = lines.substring(19, lines.length()-1);

        Gson gson = new Gson();
        Map<String, Object> map = new HashMap<>();
        try {
            map = (Map<String, Object>) gson.fromJson(lines, map.getClass());
        } catch (Exception e){
            map = new HashMap<>();
        }
        map.put("result", response.getStatusLine().getStatusCode());

        return map;
    }

    public int createTicket(String apiToken, String apiEndpoint) throws IOException, URISyntaxException {
        final HttpClientBuilder hcBuilder = HttpClientBuilder.create();
        final RequestBuilder reqBuilder = RequestBuilder.post();
        final RequestConfig.Builder rcBuilder = RequestConfig.custom();

        // URL object from API endpoint:
        URL url = new URL(apiEndpoint + "/helpdesk/tickets.json");
        final String urlHost = url.getHost();
        final int urlPort = url.getPort();
        final String urlProtocol = url.getProtocol();
        reqBuilder.setUri(url.toURI());

        // Authentication:
        List<String> authPrefs = new ArrayList<>();
        authPrefs.add(AuthSchemes.BASIC);
        rcBuilder.setTargetPreferredAuthSchemes(authPrefs);
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
                new AuthScope(urlHost, urlPort, AuthScope.ANY_REALM),
                new UsernamePasswordCredentials(apiToken, "X"));
        hcBuilder.setDefaultCredentialsProvider(credsProvider);
        AuthCache authCache = new BasicAuthCache();
        AuthSchemeBase authScheme = new BasicScheme();
        authCache.put(new HttpHost(urlHost, urlPort, urlProtocol), authScheme);
        HttpClientContext hccContext = HttpClientContext.create();
        hccContext.setAuthCache(authCache);

        // Body:
        final String jsonBody = "{\"helpdesk_ticket\":{\"description\":\"Some details on the issue ...\",\"subject\":\"Support needed..\",\"email\":\"tom@outerspace.com\",\"priority\":1,\"status\":2},\"cc_emails\":\"ram@freshdesk.com,diana@freshdesk.com\"}";
        HttpEntity entity = new StringEntity(jsonBody, ContentType.APPLICATION_JSON.withCharset(Charset.forName("utf-8")));

        reqBuilder.setEntity(entity);

        // Execute:
        RequestConfig rc = rcBuilder.build();
        reqBuilder.setConfig(rc);

        HttpClient hc = hcBuilder.build();
        HttpUriRequest req = reqBuilder.build();
        HttpResponse response = hc.execute(req, hccContext);

        // Print out:
        HttpEntity body = response.getEntity();
        InputStream is = body.getContent();
        BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("utf-8")));
        String line;
        StringBuilder sb = new StringBuilder();
        while((line=br.readLine())!=null) {
            sb.append(line);
        }
        System.out.println("Body:\n");
        System.out.println(sb.toString());

        return response.getStatusLine().getStatusCode();
    }

    public int addNoteForTicket(String apiToken, String apiEndpoint, String ticketId) throws IOException, URISyntaxException {
        final HttpClientBuilder hcBuilder = HttpClientBuilder.create();
        final RequestBuilder reqBuilder = RequestBuilder.post();
        final RequestConfig.Builder rcBuilder = RequestConfig.custom();

        // URL object from API endpoint:
        URL url = new URL(apiEndpoint + "/helpdesk/tickets/"+ticketId+"/conversations/note.json");
        final String urlHost = url.getHost();
        final int urlPort = url.getPort();
        final String urlProtocol = url.getProtocol();
        reqBuilder.setUri(url.toURI());

        // Authentication:
        List<String> authPrefs = new ArrayList<>();
        authPrefs.add(AuthSchemes.BASIC);
        rcBuilder.setTargetPreferredAuthSchemes(authPrefs);
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
                new AuthScope(urlHost, urlPort, AuthScope.ANY_REALM),
                new UsernamePasswordCredentials(apiToken, "X"));
        hcBuilder.setDefaultCredentialsProvider(credsProvider);
        AuthCache authCache = new BasicAuthCache();
        AuthSchemeBase authScheme = new BasicScheme();
        authCache.put(new HttpHost(urlHost, urlPort, urlProtocol), authScheme);
        HttpClientContext hccContext = HttpClientContext.create();
        hccContext.setAuthCache(authCache);

        // Body:
        final String jsonBody = "{\"helpdesk_note\": {" +
                "    \"body\":\"Hi tom, Still Angry\"," +
                "    \"private\":false" +
                "  }" +
                "}";
        HttpEntity entity = new StringEntity(jsonBody, ContentType.APPLICATION_JSON.withCharset(Charset.forName("utf-8")));

        reqBuilder.setEntity(entity);

        // Execute:
        RequestConfig rc = rcBuilder.build();
        reqBuilder.setConfig(rc);

        HttpClient hc = hcBuilder.build();
        HttpUriRequest req = reqBuilder.build();
        HttpResponse response = hc.execute(req, hccContext);

        // Print out:
        HttpEntity body = response.getEntity();
        InputStream is = body.getContent();
        BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("utf-8")));
        String line;
        StringBuilder sb = new StringBuilder();
        while((line=br.readLine())!=null) {
            sb.append(line);
        }
        System.out.println("Body:\n");
        System.out.println(sb.toString());

        return response.getStatusLine().getStatusCode();
    }

    public static int addNoteForTicketWithAttachments(String ticketId, String text, List<String> files) throws IOException, URISyntaxException {
        final HttpClientBuilder hcBuilder = HttpClientBuilder.create();
        final RequestBuilder reqBuilder = RequestBuilder.post();
        final RequestConfig.Builder rcBuilder = RequestConfig.custom();

        // URL object from API endpoint:
        URL url = new URL(Config.apiEndpoint + "/helpdesk/tickets/"+ticketId+"/conversations/note.json");
        final String urlHost = url.getHost();
        final int urlPort = url.getPort();
        final String urlProtocol = url.getProtocol();
        reqBuilder.setUri(url.toURI());

        // Authentication:
        List<String> authPrefs = new ArrayList<>();
        authPrefs.add(AuthSchemes.BASIC);
        rcBuilder.setTargetPreferredAuthSchemes(authPrefs);
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
                new AuthScope(urlHost, urlPort, AuthScope.ANY_REALM),
                new UsernamePasswordCredentials(Config.apiToken, "X"));
        hcBuilder.setDefaultCredentialsProvider(credsProvider);
        AuthCache authCache = new BasicAuthCache();
        AuthSchemeBase authScheme = new BasicScheme();
        authCache.put(new HttpHost(urlHost, urlPort, urlProtocol), authScheme);
        HttpClientContext hccContext = HttpClientContext.create();
        hccContext.setAuthCache(authCache);

        // Body:
        MultipartEntityBuilder meb = MultipartEntityBuilder.create();
        meb.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

        String msgFile = "";
        if (files != null && !files.isEmpty()){
            msgFile = " com arquivo anexado";
        }

        String bodyText = "Nota criada via Whatomail"+msgFile+"." + "\n\n" + text;

        meb.addTextBody("helpdesk_note[body]", bodyText);
        meb.addTextBody("helpdesk_note[private]", "true");

        if (files != null) {
            for (String fileAttach : files) {
                File attach = new File(fileAttach);
                if (attach.exists()) {
                    meb.addBinaryBody("helpdesk_note[attachments][][resource]", attach,
                            ContentType.TEXT_PLAIN.withCharset("utf-8"), attach.getName());
                }
            }
        }
        reqBuilder.setEntity(meb.build());

        // Execute:
        RequestConfig rc = rcBuilder.build();
        reqBuilder.setConfig(rc);

        HttpClient hc = hcBuilder.build();
        HttpUriRequest req = reqBuilder.build();
        HttpResponse response = hc.execute(req, hccContext);

        // Print out:
        HttpEntity body = response.getEntity();
        InputStream is = body.getContent();
        BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("utf-8")));
        String line;
        StringBuilder sb = new StringBuilder();
        while((line=br.readLine())!=null) {
            sb.append(line);
        }

        return response.getStatusLine().getStatusCode();
    }

    public static Map<String, Object> getTicket(String ticketId) throws IOException, URISyntaxException {
        final HttpClientBuilder hcBuilder = HttpClientBuilder.create();
        final RequestBuilder reqBuilder = RequestBuilder.get();
        final RequestConfig.Builder rcBuilder = RequestConfig.custom();

        // URL object from API endpoint:
        URL url = new URL(Config.apiEndpoint + "/helpdesk/tickets/"+ticketId+".json");
        final String urlHost = url.getHost();
        final int urlPort = url.getPort();
        final String urlProtocol = url.getProtocol();
        reqBuilder.setUri(url.toURI());

        // Authentication:
        List<String> authPrefs = new ArrayList<>();
        authPrefs.add(AuthSchemes.BASIC);
        rcBuilder.setTargetPreferredAuthSchemes(authPrefs);
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
                new AuthScope(urlHost, urlPort, AuthScope.ANY_REALM),
                new UsernamePasswordCredentials(Config.apiToken, "X"));
        hcBuilder.setDefaultCredentialsProvider(credsProvider);
        AuthCache authCache = new BasicAuthCache();
        AuthSchemeBase authScheme = new BasicScheme();
        authCache.put(new HttpHost(urlHost, urlPort, urlProtocol), authScheme);
        HttpClientContext hccContext = HttpClientContext.create();
        hccContext.setAuthCache(authCache);

        // Execute:
        RequestConfig rc = rcBuilder.build();
        reqBuilder.setConfig(rc);

        HttpClient hc = hcBuilder.build();
        HttpUriRequest req = reqBuilder.build();
        HttpResponse response = hc.execute(req, hccContext);

        // Print out:
        HttpEntity body = response.getEntity();
        InputStream is = body.getContent();
        BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("utf-8")));
        String line;
        String lines = "";
        while((line=br.readLine())!=null) {
            lines = lines + line;
        }

        lines = lines.substring(19, lines.length()-1);

        Gson gson = new Gson();
        Map<String, Object> map = new HashMap<>();
        try {
            map = (Map<String, Object>) gson.fromJson(lines, map.getClass());
        } catch (Exception e){
            map = new HashMap<>();
        }
        map.put("result", response.getStatusLine().getStatusCode());

        return map;
    }
}
