package br.com.infobella.whatomail.whatomail.activity;

import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Observable;

import br.com.infobella.whatomail.whatomail.modelo.Veterinary;
import br.com.infobella.whatomail.whatomail.R;
import utils.KeyUtils;
import utils.LogUtils;

public class AddVeterinaryActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_veterinary);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setTitle(R.string.toolbar_title_add_veterinary);
        }

        Button btnCadastrar = (Button) findViewById(R.id.btn_add_vet);
        btnCadastrar.setOnClickListener(onClickCadastrar());

    }

    private View.OnClickListener onClickCadastrar() {
        return new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tDdd = (TextView) findViewById(R.id.vet_ddd);
                TextView tPhone = (TextView) findViewById(R.id.vet_fone);
                TextView tName = (TextView) findViewById(R.id.vet_name);

                boolean valido = true;
                String sDdd = (String) tDdd.getText().toString();
                String sPhone = (String) tPhone.getText().toString();
                String sName = (String) tName.getText().toString();

                Log.v(LogUtils.TAG_GERAL, "DDD: " + sDdd);
                Log.v(LogUtils.TAG_GERAL, "Celular: " + sPhone);
                Log.v(LogUtils.TAG_GERAL, "Nome: " + sName);

                if (sDdd.trim().equals("") || sDdd.length() < 2) {
                    alertCurto("Digite o DDD");
                    tDdd.setSelected(true);
                    valido = false;
                } else if (sPhone.trim().equals("")) {
                    alertCurto("Digite o Celular");
                    tPhone.setSelected(true);
                    valido = false;
                } else if (sName.equals("")) {
                    alertCurto("Digite o Nome");
                    tName.setSelected(true);
                    valido = false;
                }

                if (valido){
                    String phone = sDdd + sPhone;

                    Veterinary veterinary = new Veterinary();
                    veterinary.setName(sName);
                    veterinary.setPhone(phone);

                    long id = daoManager.veterinaryDB.save(veterinary);
                    if (id > 0){
                        //Atualizar a lista

                        myObservable.setChangedObservable();
                        myObservable.notifyMyObservers(KeyUtils.MSG_OBSERVABLE_UPDATE_LIST_VIEW_VETERINARY);

                        alertDialog("Mensagem", "Veterinário adicionado com sucesso!");
                        finish();
                    } else {
                        alertDialog("Ops! Houve um Erro", "Não foi possível salver o veterinário!");
                    }
                }
            }
        };
    }

    @Override
    public void update(Observable o, Object arg) {

    }
}
