package br.com.infobella.whatomail.whatomail.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Observable;

import br.com.infobella.whatomail.whatomail.modelo.Contact;
import br.com.infobella.whatomail.whatomail.modelo.ConversationReceived;
import br.com.infobella.whatomail.whatomail.modelo.dao.DaoManager;
import br.com.infobella.whatomail.whatomail.modelo.MessageReceived;
import br.com.infobella.whatomail.whatomail.modelo.MessageReply;
import br.com.infobella.whatomail.whatomail.R;
import utils.FileUtils;

public class MainActivity extends BaseActivity implements View.OnTouchListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.v("TAG", "onCreate MainActivity");

        Button btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(onClickEnviarAgora());

        Button btnVeterinary = (Button) findViewById(R.id.btn_veterynary);
        btnVeterinary.setOnClickListener(onClickVeterinary());

        Button btnAnswer = (Button) findViewById(R.id.btn_answer);
        btnAnswer.setOnClickListener(onClickAnswer());
    }

    @Override
    public boolean onTouch(View v, MotionEvent event){
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        //Log.v("DADOS", event.toString());
        if (event.getAction() == MotionEvent.ACTION_UP){
            if ((event.getEventTime() - event.getDownTime()) > (5*1000)){
                printDados();
                Toast toast = Toast.makeText(
                        getApplicationContext(),
                        "Print Dados",
                        Toast.LENGTH_SHORT
                );
                toast.show();
            }
        }
        return true;
    }

    private void printDados(){
        FileOutputStream fileOutputStream = null;
        OutputStreamWriter outputStreamWriter = null;
        File fileLog = FileUtils.getFileDados();
        try {
            fileOutputStream = new FileOutputStream(fileLog);
            outputStreamWriter = new OutputStreamWriter(fileOutputStream);
            DaoManager daoManager = new DaoManager();
            List<Contact> contatos = daoManager.contactDB.findAll();
            outputStreamWriter.append("------------------------------------ CONTATOS ----------------------------------------------------\r\n");
            for (Contact contact : contatos){
                outputStreamWriter.append(contact.toString()+"\r\n");
            }
            List<MessageReceived> messages = daoManager.messageReceivedDB.findAll();
            outputStreamWriter.append("----------------------------------- MENSAGENS -----------------------------------------------------\r\n");
            for (MessageReceived message : messages){
                outputStreamWriter.append(message.toString()+"\r\n");
            }
            List<ConversationReceived> conversas = daoManager.convarsationReceivedDB.findAll();
            outputStreamWriter.append("--------------------------------- CONVERSAS -------------------------------------------------------\r\n");
            for (ConversationReceived conversa : conversas){
                outputStreamWriter.append(conversa.toString()+"\r\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (outputStreamWriter != null) {
            try {
                outputStreamWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (fileOutputStream != null){
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private View.OnClickListener onClickEnviarAgora() {
        return new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("FRESHDESK", "freshdesk init");

                //Forçar envio de mensagens por email
                //Intent intent = new Intent(getContext(), StorageMessageReceivedService.class);
                //intent.putExtra(KeyUtils.KEY_ACTION_STORAGE, KeyUtils.KEY_ACTION_STORAGE_READ_NOW);
                //startService(intent);

                List<MessageReply> list = daoManager.messageReplyDB.findAll();
                for (MessageReply m : list){
                    Log.v("TESTE_APP_CH", m.toString());
                }
            }
        };
    }

    private View.OnClickListener onClickAnswer() {
        return new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("FRESHDESK", "Responder");
                Intent intent = new Intent(getContext(), AnswerActivity.class);
                startActivity(intent);
            }
        };
    }

    private View.OnClickListener onClickVeterinary() {
        return new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("FRESHDESK", "Veterinárias");
                Intent intent = new Intent(getContext(), VeterinaryActivity.class);
                startActivity(intent);
            }
        };
    }

    @Override
    public void update(Observable o, Object arg) {

    }


}
