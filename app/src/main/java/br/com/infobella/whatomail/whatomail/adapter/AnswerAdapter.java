package br.com.infobella.whatomail.whatomail.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import br.com.infobella.whatomail.whatomail.modelo.MessageReply;
import br.com.infobella.whatomail.whatomail.R;
import utils.FormatterUtils;

/**
 * Created by HENRI on 21/05/2017.
 */

public class AnswerAdapter extends RecyclerView.Adapter<AnswerAdapter.MessageReplyViewHolder> {

    private final List<MessageReply> messageReplies;
    private final Context context;

    private final MessageReplyOnClickListener onClickListener;

    public interface MessageReplyOnClickListener {
        public void onClickMessageReply(View view, int idx);
    }

    public AnswerAdapter(Context context, List<MessageReply> messageReplies, MessageReplyOnClickListener onClickListener) {
        this.messageReplies = messageReplies;
        this.context = context;
        this.onClickListener = onClickListener;
    }

    @Override
    public MessageReplyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.answer_recyler_view, viewGroup, false);

        MessageReplyViewHolder holder = new MessageReplyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MessageReplyViewHolder holder, final int position) {
        MessageReply messageReply = messageReplies.get(position);

        holder.vTicket.setText(messageReply.getTicket());
        holder.vCliente.setText(messageReply.getContactName());
        holder.vText.setText(messageReply.getText());
        String dthrReceived = FormatterUtils.dateToString(messageReply.getDataTimeReceived()) + " as " + FormatterUtils.timeToString(messageReply.getDataTimeReceived());
        holder.vDtrhReceived.setText(dthrReceived);
        if (messageReply.getDataTimeSent() != null) {
            String dthrSent = FormatterUtils.dateToString(messageReply.getDataTimeSent()) + " as " + FormatterUtils.timeToString(messageReply.getDataTimeSent());
            holder.vDtrhSent.setText(dthrSent);
        } else {
            holder.vDtrhSent.setText(" ");
        }

        if (onClickListener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener(){
               @Override
                public void onClick(View v){
                   onClickListener.onClickMessageReply(holder.view, position);
               }
            });
        }
    }

    @Override
    public int getItemCount(){
        return this.messageReplies != null ? this.messageReplies.size() : 0;
    }

    public static class MessageReplyViewHolder extends RecyclerView.ViewHolder {
        public TextView vTicket;
        public TextView vCliente;
        public TextView vText;
        public TextView vDtrhReceived;
        public TextView vDtrhSent;

        private View view;

        public MessageReplyViewHolder(View view){
            super(view);
            this.view = view;

            vTicket = (TextView) view.findViewById(R.id.campo_answer_ticket);
            vCliente = (TextView) view.findViewById(R.id.campo_answer_cliente);
            vText = (TextView) view.findViewById(R.id.campo_answer_text);
            vDtrhReceived = (TextView) view.findViewById(R.id.campo_answer_dthrReceived);
            vDtrhSent = (TextView) view.findViewById(R.id.campo_answer_dthrSent);
        }
    }
}
