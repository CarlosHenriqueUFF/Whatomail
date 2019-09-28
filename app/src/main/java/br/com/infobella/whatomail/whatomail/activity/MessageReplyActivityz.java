package br.com.infobella.whatomail.whatomail.activity;

import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import java.util.List;
import java.util.Observable;

import br.com.infobella.whatomail.whatomail.adapter.AnswerAdapter;
import br.com.infobella.whatomail.whatomail.modelo.MessageReply;
import br.com.infobella.whatomail.whatomail.R;
import utils.LogUtils;

public class MessageReplyActivityz extends BaseActivity {

    private RecyclerView recyclerView;
    private static List<MessageReply> messageReplyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_reply_activityz);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.toolbar_title_answer_sent);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_reply_all);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        messageReplyList = daoManager.messageReplyDB.findByStatus(MessageReply.Status.Sent);
        recyclerView.setAdapter(new AnswerAdapter(this, messageReplyList, onClickMessageReply()));
    }


    private AnswerAdapter.MessageReplyOnClickListener onClickMessageReply() {
        return new AnswerAdapter.MessageReplyOnClickListener() {
            @Override
            public void onClickMessageReply(View view, int idx) {
                Log.v(LogUtils.TAG_GERAL, "você clicou idx : "+idx);
                if (messageReplyList != null) {
                    final MessageReply messageReply = messageReplyList.get(idx);
                    Log.v(LogUtils.TAG_GERAL, "você clicou em : " + messageReply.toString());
                } else {
                    Log.v(LogUtils.TAG_GERAL, "você clicou em : null");
                }
            }
        };
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void update(Observable o, Object arg) {

    }
}
