package br.com.infobella.whatomail.whatomail.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Set;

import br.com.infobella.whatomail.whatomail.controller.TicketController;
import br.com.infobella.whatomail.whatomail.modelo.Contact;
import br.com.infobella.whatomail.whatomail.modelo.Void;
import br.com.infobella.whatomail.whatomail.R;
import utils.FileUtils;
import utils.HttpUtils;
import utils.LogUtils;

public class ShareFreshdeskActivity extends BaseActivity implements View.OnClickListener {

    private static String fileName;
    private ProgressDialog dialog;
    private boolean newTicket;
    private TextView textViewTicket;
    private EditText editTextTicket;
    private String msg;

    private TextView textViewNomeCliente;
    private EditText editTextNomeCliente;
    private TextView textViewTelefoneCliente;
    private EditText editTextTelefoneCliente;

    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_freshdesk_share);
        LogUtils.writeLog(getActivity(), LogUtils.TAG_GERAL, "ShareFreshdeskActivity onCreate");

        newTicket = false;

        Button btn = (Button) findViewById(R.id.btn_freshdesk);
        btn.setOnClickListener(this);

        textViewTicket = (TextView) findViewById(R.id.label_ticket_id);
        editTextTicket = (EditText) findViewById(R.id.ticket_id);

        textViewNomeCliente = (TextView) findViewById(R.id.label_nome_cliente);
        textViewNomeCliente.setVisibility(View.GONE);
        editTextNomeCliente = (EditText) findViewById(R.id.nome_cliente);
        editTextNomeCliente.setVisibility(View.GONE);

        textViewTelefoneCliente = (TextView) findViewById(R.id.label_telefone_cliente);
        textViewTelefoneCliente.setVisibility(View.GONE);
        editTextTelefoneCliente = (EditText) findViewById(R.id.telefone_cliente);
        editTextTelefoneCliente.setVisibility(View.GONE);

        CheckBox checkBox = (CheckBox) findViewById(R.id.check_ticket);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox checkBox = (CheckBox) v;
                if (checkBox.isChecked()){
                    newTicket = true;
                    textViewTicket.setVisibility(View.GONE);
                    editTextTicket.setVisibility(View.GONE);
                    textViewNomeCliente.setVisibility(View.VISIBLE);
                    editTextNomeCliente.setVisibility(View.VISIBLE);
                    textViewTelefoneCliente.setVisibility(View.VISIBLE);
                    editTextTelefoneCliente.setVisibility(View.VISIBLE);
                } else {
                    newTicket = false;
                    textViewTicket.setVisibility(View.VISIBLE);
                    editTextTicket.setVisibility(View.VISIBLE);
                    textViewNomeCliente.setVisibility(View.GONE);
                    editTextNomeCliente.setVisibility(View.GONE);
                    textViewTelefoneCliente.setVisibility(View.GONE);
                    editTextTelefoneCliente.setVisibility(View.GONE);

                }
            }
        });

        fileName = null;

        Intent intent = getIntent();

        if (intent != null){
            LogUtils.writeLog(this, LogUtils.TAG_GERAL, "intent: " + intent.toString());
            if (intent.getExtras() != null) {
                Bundle bundle = intent.getExtras();
                if(bundle.containsKey("android.intent.extra.TEXT")){
                    String text  = bundle.getString("android.intent.extra.TEXT");
                    LogUtils.writeLog(this, LogUtils.TAG_GERAL, "extras-text: " + text);
                    EditText editTextTicket = (EditText) findViewById(R.id.ticket_msg);
                    editTextTicket.setText(text);
                }
            }
            /*String selectedImage = "Arquivo Não Encontrado!";
            if (intent.getData() != null){
                selectedImage = intent.getData().getPath();
                LogUtils.writeLog(getActivity(), LogUtils.TAG_GERAL, "uri: "+intent.getData());
                LogUtils.writeLog(getActivity(), LogUtils.TAG_GERAL, "uri-path: "+intent.getData().getPath());
                LogUtils.writeLog(getActivity(), LogUtils.TAG_GERAL, "uri-authorit: "+intent.getData().getAuthority());
                LogUtils.writeLog(getActivity(), LogUtils.TAG_GERAL, "uri-encode-authority: "+intent.getData().getEncodedAuthority());
                LogUtils.writeLog(getActivity(), LogUtils.TAG_GERAL, "uri-encode-path: "+intent.getData().getEncodedPath());
                LogUtils.writeLog(getActivity(), LogUtils.TAG_GERAL, "uri-encode-fragment: "+intent.getData().getEncodedFragment());
                LogUtils.writeLog(getActivity(), LogUtils.TAG_GERAL, "uri-encode-query: "+intent.getData().getEncodedQuery());
                LogUtils.writeLog(getActivity(), LogUtils.TAG_GERAL, "uri-encode-scheme-specific-path: "+intent.getData().getEncodedSchemeSpecificPart());
                LogUtils.writeLog(getActivity(), LogUtils.TAG_GERAL, "uri-encode-user-info: "+intent.getData().getEncodedUserInfo());
                LogUtils.writeLog(getActivity(), LogUtils.TAG_GERAL, "uri-fragment: "+intent.getData().getFragment());
                LogUtils.writeLog(getActivity(), LogUtils.TAG_GERAL, "uri-host: "+intent.getData().getHost());
                LogUtils.writeLog(getActivity(), LogUtils.TAG_GERAL, "uri-last-path-segment: "+intent.getData().getLastPathSegment());
                LogUtils.writeLog(getActivity(), LogUtils.TAG_GERAL, "uri-scheme: "+intent.getData().getScheme());
                LogUtils.writeLog(getActivity(), LogUtils.TAG_GERAL, "uri-scheme-specific: "+intent.getData().getSchemeSpecificPart());
                LogUtils.writeLog(getActivity(), LogUtils.TAG_GERAL, "uri-user-info: "+intent.getData().getUserInfo());
                LogUtils.writeLog(this, LogUtils.TAG_GERAL, "uri: " + intent.getData().toString());
                if (FileUtils.copyFileByUri(intent.getData(), file, getContext())){
                    if (file != null && file.exists()) {
                        fileName = file.getAbsolutePath();
                    }
                }
                TextView textViewFile = (TextView) findViewById(R.id.file_name);
                textViewFile.setText(selectedImage);
                TextView textViewLabelFile = (TextView) findViewById(R.id.label_file_name);
                textViewLabelFile.setVisibility(View.VISIBLE);
                textViewFile.setVisibility(View.VISIBLE);
            } else {
                TextView textViewLabelFile = (TextView) findViewById(R.id.label_file_name);
                textViewLabelFile.setVisibility(View.GONE);
                TextView textViewFile = (TextView) findViewById(R.id.file_name);
                textViewFile.setVisibility(View.GONE);
            }
            */
            ClipData clipData = intent.getClipData();
            if (clipData != null && clipData.getItemCount() > 0){
                //for (int i=0; i<clipData.getItemCount(); i++) {
                    ClipData.Item item = clipData.getItemAt(0);
                    ClipDescription clipDescription = clipData.getDescription();
                    String mymeType = clipDescription.getMimeType(0);
                    int index = mymeType.indexOf("/");
                    String type = null;
                    if (index != -1){
                        type = mymeType.substring(index+1);
                        LogUtils.writeLog(getActivity(), LogUtils.TAG_GERAL, "type: "+type);
                    }
                    LogUtils.writeLog(getActivity(), LogUtils.TAG_GERAL, "mime type: "+clipDescription.getMimeType(0));
                    LogUtils.writeLog(getActivity(), LogUtils.TAG_GERAL, "item-clip: "+clipData.getDescription());
                    LogUtils.writeLog(getActivity(), LogUtils.TAG_GERAL, "item-clip: "+item.toString());
                    Uri uri = item.getUri();
                    LogUtils.writeLog(getActivity(), LogUtils.TAG_GERAL, "uri: "+uri);
                    LogUtils.writeLog(getActivity(), LogUtils.TAG_GERAL, "uri-path: "+uri.getPath());
                    LogUtils.writeLog(getActivity(), LogUtils.TAG_GERAL, "uri-authorit: "+uri.getAuthority());
                    LogUtils.writeLog(getActivity(), LogUtils.TAG_GERAL, "uri-encode-authority: "+uri.getEncodedAuthority());
                    LogUtils.writeLog(getActivity(), LogUtils.TAG_GERAL, "uri-encode-path: "+uri.getEncodedPath());
                    LogUtils.writeLog(getActivity(), LogUtils.TAG_GERAL, "uri-encode-fragment: "+uri.getEncodedFragment());
                    LogUtils.writeLog(getActivity(), LogUtils.TAG_GERAL, "uri-encode-query: "+uri.getEncodedQuery());
                    LogUtils.writeLog(getActivity(), LogUtils.TAG_GERAL, "uri-encode-scheme-specific-path: "+uri.getEncodedSchemeSpecificPart());
                    LogUtils.writeLog(getActivity(), LogUtils.TAG_GERAL, "uri-encode-user-info: "+uri.getEncodedUserInfo());
                    LogUtils.writeLog(getActivity(), LogUtils.TAG_GERAL, "uri-fragment: "+uri.getFragment());
                    LogUtils.writeLog(getActivity(), LogUtils.TAG_GERAL, "uri-host: "+uri.getHost());
                    LogUtils.writeLog(getActivity(), LogUtils.TAG_GERAL, "uri-last-path-segment: "+uri.getLastPathSegment());
                    LogUtils.writeLog(getActivity(), LogUtils.TAG_GERAL, "uri-scheme: "+uri.getScheme());
                    LogUtils.writeLog(getActivity(), LogUtils.TAG_GERAL, "uri-scheme-specific: "+uri.getSchemeSpecificPart());
                    LogUtils.writeLog(getActivity(), LogUtils.TAG_GERAL, "uri-user-info: "+uri.getUserInfo());

                    String selectedImage = "Arquivo Não Encontrado!";
                    if (uri != null) {
                        selectedImage = uri.getEncodedSchemeSpecificPart();
                        if (uri.toString().startsWith("file://")) {
                            selectedImage = uri.getPath();
                            fileName = uri.getPath();
                        } else {
                            String fileNameOrigem = null;
                            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                            if (cursor != null) {
                                try {
                                    if (cursor.moveToFirst()) {
                                        fileNameOrigem = cursor.getString(cursor.getColumnIndex("_display_name"));
                                        //if (uri.toString().startsWith("content://com.whatsapp")) {
                                            /*String[] names = cursor.getColumnNames();
                                            for (String n : names) {
                                                int idx = cursor.getColumnIndex(n);
                                                String v = cursor.getString(idx);
                                                LogUtils.writeLog(getActivity(), LogUtils.TAG_GERAL, n + ": " + v);
                                            }*/
                                            /*String fileName = cursor.getString(cursor.getColumnIndex("_display_name"));
                                            LogUtils.writeLog(getActivity(), LogUtils.TAG_GERAL, "display name: " + fileName);
                                            if (fileName != null) {
                                                selectedImage = FileUtils.getAbsolutePathImageBusines() + fileName;
                                                File file = new File(selectedImage);
                                                if (!file.exists()) {
                                                    selectedImage = FileUtils.getAbsolutePathImage() + fileName;
                                                }
                                            }
                                        /*} else {
                                            selectedImage = cursor.getString(cursor.getColumnIndex("_data"));
                                            LogUtils.writeLog(getActivity(), LogUtils.TAG_GERAL, "data: " + selectedImage);
                                        }*/
                                    }
                                } finally {
                                    cursor.close();
                                }
                            }


                            if (fileNameOrigem != null && type != null) {
                                if (!fileNameOrigem.contains(type)) {
                                    if (type.equals("jpeg") && fileNameOrigem.contains(".jpg")) {
                                        String newType = "." + type;
                                        fileNameOrigem = fileNameOrigem.replace(".jpg", newType);
                                    } else {
                                        fileNameOrigem = fileNameOrigem + "." + type;
                                    }
                                }
                            }
                            file = FileUtils.getFileName(fileNameOrigem);
                            LogUtils.writeLog(this, LogUtils.TAG_GERAL, "file download: " + file);
                            fileName = selectedImage;
                            if (FileUtils.copyFileByUri(uri, file, getContext())) {
                                if (file != null && file.exists()) {
                                    fileName = file.getAbsolutePath();
                                }
                            }
                        }
                        LogUtils.writeLog(this, LogUtils.TAG_GERAL, "file name: "+fileName);
                        TextView textViewFile = (TextView) findViewById(R.id.file_name);
                        textViewFile.setText(selectedImage);
                        TextView textViewLabelFile = (TextView) findViewById(R.id.label_file_name);
                        textViewLabelFile.setVisibility(View.VISIBLE);
                        textViewFile.setVisibility(View.VISIBLE);
                    } else {
                        TextView textViewLabelFile = (TextView) findViewById(R.id.label_file_name);
                        textViewLabelFile.setVisibility(View.GONE);
                        TextView textViewFile = (TextView) findViewById(R.id.file_name);
                        textViewFile.setVisibility(View.GONE);
                    }
                //}
            } else {
                TextView textViewLabelFile = (TextView) findViewById(R.id.label_file_name);
                textViewLabelFile.setVisibility(View.GONE);
                TextView textViewFile = (TextView) findViewById(R.id.file_name);
                textViewFile.setVisibility(View.GONE);
            }

        }
    }

    @Override
    public void onClick(View v) {
        LogUtils.writeLog(this, LogUtils.TAG_GERAL, "btn send freshdesk press");
        EditText editTextTicket = (EditText) findViewById(R.id.ticket_id);
        EditText editTextTicketMsg = (EditText) findViewById(R.id.ticket_msg);
        msg = editTextTicketMsg.getText().toString();
        String ticket = editTextTicket.getText().toString();
        String nome = editTextNomeCliente.getText().toString();
        String telefone = editTextTelefoneCliente.getText().toString();
        if (!newTicket) {
            if (ticket.equals("")) {
                alertDialog("Alerta", "Digite o #Ticket");
            } else {
                if (fileName != null) {
                    File file = new File(fileName);
                    if (file.exists()) {
                        if (HttpUtils.isNetworkAvailable(this)) {
                            new AddNoteForTichectTask().execute(ticket);
                        } else {
                            alertDialog("Error", "Sem conexão com a internet!");
                        }
                    } else {
                        alertDialog("Error", "Erro ao anexar o arquivo, arquivo não encontrado!");
                    }
                }else if (!msg.isEmpty()) {
                    if (HttpUtils.isNetworkAvailable(this)) {
                        new AddNoteForTichectTask().execute(ticket);
                    } else {
                        alertDialog("Error", "Sem conexão com a internet!");
                    }
                } else {
                    alertDialog("Error", "Erro ao anexar arquivo e nenhuma mensagem inserida!");
                }
            }
        } else {
            if (telefone.isEmpty()){
                alertDialog("Alerta", "Digite o Telefone do Cliente");
            } else {
                if (fileName != null) {
                    File file = new File(fileName);
                    if (file.exists()) {
                        if (HttpUtils.isNetworkAvailable(this)) {
                            new AddNoteForTichectTask().execute(ticket);
                        } else {
                            alertDialog("Error", "Sem conexão com a internet!");
                        }
                    } else {
                        alertDialog("Error", "Erro ao anexar o arquivo, arquivo não encontrado!");
                    }
                }else if (!msg.isEmpty()) {
                    if (HttpUtils.isNetworkAvailable(this)) {
                        new AddNoteForTichectTask().execute(ticket);
                    } else {
                        alertDialog("Error", "Sem conexão com a internet!");
                    }
                } else {
                    alertDialog("Error", "Erro ao anexar arquivo e nenhuma mensagem inserida!");
                }
            }
        }
    }

    @Override
    public void update(Observable o, Object arg) {

    }

    @SuppressLint("StaticFieldLeak")
    protected class AddNoteForTichectTask extends AsyncTask<String, Void, Integer> {
        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(getContext());
            dialog.setTitle("Whatomail");
            dialog.setMessage("Enviando arquivo para o FreshDesk");
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Integer doInBackground(String... strings) {
            if (newTicket){
                String nome = editTextNomeCliente.getText().toString();
                String telefone = editTextTelefoneCliente.getText().toString();
                Contact contact = new Contact();
                contact.setContactName(nome);
                contact.setPhone(telefone);
                contact.setJidFromPhone(telefone);
                LogUtils.writeLog(getContext(), LogUtils.TAG_GERAL, "Contato: " + contact.toString());
                try {
                    List<String> files = null;
                    if (fileName != null){
                        files = new ArrayList<>();
                        files.add(fileName);
                    }
                    Map<String, Object> map = TicketController.createTicketWithAttachments(contact, msg, dateToString(new Date()), files);
                    LogUtils.writeLog(getContext(), LogUtils.TAG_GERAL, "Map: " + map);
                    int result = (Integer) map.get("result");
                    LogUtils.writeLog(getContext(), LogUtils.TAG_GERAL, "result: " + result);
                    return result;
                } catch (IOException | URISyntaxException e) {
                    e.printStackTrace();
                    return -1;
                }
            } else {
                String ticket = strings[0];
                try {
                    List<String> files = null;
                    if (fileName != null){
                        files = new ArrayList<>();
                        files.add(fileName);
                    }
                    return TicketController.addNoteForTicketWithAttachments(ticket, msg, files);
                } catch (IOException | URISyntaxException e) {
                    e.printStackTrace();
                    return -1;
                }
            }
        }

        protected void onProgressUpdate(Void... progress) {
        }

        @Override
        protected void onPostExecute(Integer result) {
            dialog.dismiss();
            Log.v("FRESHDESK", "result: "+result);
            if (result == -1) {
                alertDialog("Error", "Não foi possível enviar o arquivo, tente novamente!");
            } else if (result == 200){
                alertDialogFinishActivity("Mensagem", "Arquivo enviado com sucesso!", getActivity());
            } else {
                alertDialog("Error", "O servidor reportou o seguinte código: "+result);
            }
            if (file != null && file.exists()){
                LogUtils.writeLog(getContext(), LogUtils.TAG_GERAL, "delete file: " + file.delete());
            }
        }
    }

    private Activity getActivity(){
        return this;
    }
}
