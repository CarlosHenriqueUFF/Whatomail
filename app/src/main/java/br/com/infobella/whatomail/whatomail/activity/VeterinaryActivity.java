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

import java.util.List;
import java.util.Observable;

import br.com.infobella.whatomail.whatomail.adapter.VeterinaryAdapter;
import br.com.infobella.whatomail.whatomail.modelo.MyObservable;
import br.com.infobella.whatomail.whatomail.modelo.Veterinary;
import br.com.infobella.whatomail.whatomail.R;
import utils.KeyUtils;
import utils.LogUtils;

public class VeterinaryActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private static List<Veterinary> veterinaryLst;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_veterinary);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.toolbar_title_veterinary);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_vet);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        veterinaryLst = daoManager.veterinaryDB.findAll();
        recyclerView.setAdapter(new VeterinaryAdapter(this, veterinaryLst, onClickVeterinary()));

        myObservable.addObserver(this);
    }

    private void removeVeterinary(Veterinary veterinary){
        daoManager.veterinaryDB.delete(veterinary);
        veterinaryLst = daoManager.veterinaryDB.findAll();
        recyclerView.setAdapter(new VeterinaryAdapter(this, veterinaryLst, onClickVeterinary()));
    }

    private VeterinaryAdapter.VeterinaryOnClickListener onClickVeterinary() {
        return new VeterinaryAdapter.VeterinaryOnClickListener() {
            @Override
            public void onClickVeterinary(View view, int idx) {
                Log.v(LogUtils.TAG_GERAL, "você clicou idx : "+idx);
                if (veterinaryLst != null) {
                    final Veterinary veterinary = veterinaryLst.get(idx);
                    Log.v(LogUtils.TAG_GERAL, "você clicou em : " + veterinary.toString());

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Mensagem");
                    builder.setMessage("Deseja remover o Veterinário "+veterinary.getName()+ " da Lista?");
                    builder.setPositiveButton(R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    removeVeterinary(veterinary);
                                    dialog.dismiss();
                                 }
                            }
                    );
                    builder.setNegativeButton(R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.dismiss();
                                }
                            }
                    );
                    AlertDialog dialog = builder.create();
                    dialog.show();

                } else {
                    Log.v(LogUtils.TAG_GERAL, "você clicou em : null");
                }
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_veterinary, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            finish();
        } else if (id == R.id.action_add){
            Log.v(LogUtils.TAG_GERAL, "action add");
            Intent intent = new Intent(this, AddVeterinaryActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void update(Observable observable, Object arg) {
        if (observable instanceof MyObservable){
            String msg = (String) arg;
            if (msg.equals(KeyUtils.MSG_OBSERVABLE_UPDATE_LIST_VIEW_VETERINARY)){
                Log.v(LogUtils.TAG_GERAL, "Veterinary Activy: update: ");
                veterinaryLst = daoManager.veterinaryDB.findAll();
                recyclerView.setAdapter(new VeterinaryAdapter(this, veterinaryLst, onClickVeterinary()));
            }
        }
    }

}
