package br.com.infobella.whatomail.whatomail.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.Date;
import java.util.List;
import java.util.Observable;

import br.com.infobella.whatomail.whatomail.adapter.AnswerAdapter;
import br.com.infobella.whatomail.whatomail.modelo.MessageReply;
import br.com.infobella.whatomail.whatomail.R;
import utils.KeyUtils;
import utils.LogUtils;

public class AnswerActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private static List<MessageReply> messageReplyList;
    private long idMessage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.toolbar_title_answer_pending);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_reply);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        messageReplyList = daoManager.messageReplyDB.findByStatus(MessageReply.Status.Waiting);
        recyclerView.setAdapter(new AnswerAdapter(this, messageReplyList, onClickMessageReply()));

        if (savedInstanceState != null) {
            idMessage = savedInstanceState.getLong(KeyUtils.KEY_ID_MESSAGE_REPLY);
            if (idMessage != 0) {
                updateMessage();
            }
        }
    }

    private void confirmSent(){
        MessageReply messageReply = daoManager.messageReplyDB.findById(idMessage);
        if (messageReply != null) {
            messageReply.setStatus(MessageReply.Status.Sent);
            messageReply.setDataTimeSent(new Date());
            daoManager.messageReplyDB.save(messageReply);
            messageReplyList = daoManager.messageReplyDB.findByStatus(MessageReply.Status.Waiting);
            recyclerView.setAdapter(new AnswerAdapter(this, messageReplyList, onClickMessageReply()));
            idMessage = 0;
        }
    }

    private void updateMessage(){
        MessageReply messageReply = daoManager.messageReplyDB.findById(idMessage);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Mensagem");
        builder.setMessage("A mensagem para  "+messageReply.getContactName()+ " já foi enviada pelo ZAP?");
        builder.setPositiveButton(R.string.sim,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        confirmSent();
                        dialog.dismiss();
                    }
                }
        );
        builder.setNegativeButton(R.string.nao,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                }
        );
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private AnswerAdapter.MessageReplyOnClickListener onClickMessageReply() {
        return new AnswerAdapter.MessageReplyOnClickListener() {
            @Override
            public void onClickMessageReply(View view, int idx) {
                Log.v(LogUtils.TAG_GERAL, "você clicou idx : "+idx);
                if (messageReplyList != null) {
                    final MessageReply messageReply = messageReplyList.get(idx);
                    //Log.v(LogUtils.TAG_GERAL, "você clicou em : " + messageReply.toString());

                    idMessage = messageReply.getId();

                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.setType("text/plain");
                    sendIntent.setPackage("com.whatsapp");
                    sendIntent.putExtra(Intent.EXTRA_TEXT, messageReply.getText());
                    sendIntent.putExtra("jid", messageReply.getContactJid());
                    startActivity(sendIntent);

                } else {
                    Log.v(LogUtils.TAG_GERAL, "você clicou em : null");
                }
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_answer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            finish();
        } else if (id == R.id.action_view_all){
            Log.v(LogUtils.TAG_GERAL, "menu");
            Intent intent = new Intent(this, MessageReplyActivityz.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putLong(KeyUtils.KEY_ID_MESSAGE_REPLY, idMessage);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (idMessage != 0){
            updateMessage();
        }
        super.onResume();
    }

    @Override
    public void update(Observable o, Object arg) {

    }
}
