package br.com.infobella.whatomail.whatomail.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import br.com.infobella.whatomail.whatomail.modelo.Veterinary;
import br.com.infobella.whatomail.whatomail.R;

/**
 * Created by HENRI on 21/05/2017.
 */

public class VeterinaryAdapter extends RecyclerView.Adapter<VeterinaryAdapter.VeterinaryViewHolder> {

    private final List<Veterinary> veterinaries;
    private final Context context;

    private final VeterinaryOnClickListener onClickListener;

    public interface VeterinaryOnClickListener {
        public void onClickVeterinary(View view, int idx);
    }

    public VeterinaryAdapter(Context context, List<Veterinary> veterinaries, VeterinaryOnClickListener onClickListener) {
        this.veterinaries = veterinaries;
        this.context = context;
        this.onClickListener = onClickListener;
    }

    @Override
    public VeterinaryViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.veterinary_recyler_view, viewGroup, false);

        VeterinaryViewHolder holder = new VeterinaryViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final VeterinaryViewHolder holder, final int position) {
        Veterinary veterinary = veterinaries.get(position);

        holder.vName.setText(veterinary.getName());
        holder.vPhone.setText(veterinary.getPhone());

        if (onClickListener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener(){
               @Override
                public void onClick(View v){
                   onClickListener.onClickVeterinary(holder.view, position);
               }
            });
        }
    }

    @Override
    public int getItemCount(){
        return this.veterinaries != null ? this.veterinaries.size() : 0;
    }

    public static class VeterinaryViewHolder extends RecyclerView.ViewHolder {
        public TextView vName;
        public TextView vPhone;

        private View view;

        public VeterinaryViewHolder(View view){
            super(view);
            this.view = view;

            vName = (TextView) view.findViewById(R.id.campo_list_vet_name);
            vPhone = (TextView) view.findViewById(R.id.campo_list_vet_phone);
        }
    }
}
