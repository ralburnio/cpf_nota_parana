package com.google.android.gms.samples.vision.ocrreader;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.text.NumberFormat;

public class ValidarDadosActivity extends Activity {

    private EditText cnpj_text, coo_text, data_text, valor_text;
    private int year_x, month_x, day_x;
    private String ong_cnpj_string;
    static final int DIALOG_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validar_dados);

        ong_cnpj_string = getIntent().getExtras().getString("ong_cnpj_string");

        cnpj_text = (EditText) findViewById(R.id.cnpj_text);
        cnpj_text.addTextChangedListener(CpfCnpjMasks.insert(cnpj_text));
        cnpj_text.setText(getIntent().getExtras().getString("cnpj"));

        coo_text = (EditText) findViewById(R.id.coo_text);
        if (getIntent().getExtras().getString("coo").equals("COO"))
            coo_text.setText("");
        else
            coo_text.setText(getIntent().getExtras().getString("coo"));

        valor_text = (EditText) findViewById(R.id.valor_text);
        valor_text.addTextChangedListener(new MascaraMonetaria(valor_text));
        valor_text.setText(getIntent().getExtras().getString("valor"));

        final Calendar cal = Calendar.getInstance();
        year_x = cal.get(Calendar.YEAR);
        month_x = cal.get(Calendar.MONTH) + 1;
        day_x = cal.get(Calendar.DAY_OF_MONTH);

        data_text = (EditText) findViewById(R.id.data_text);
        if (getIntent().getExtras().getString("data").equals("DATA"))
            data_text.setText(day_x + "/" + month_x + "/" + year_x);
        else
            data_text.setText(getIntent().getExtras().getString("data"));

        Intent intent = getIntent();
        Bundle bundle = new Bundle();

        bundle.putString("data", data_text.getText().toString());
        intent.putExtras(bundle);

        showDialogOnTextClick();
    }

    public void showDialogOnTextClick() {
        data_text.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    showDialog(DIALOG_ID);

                }
            }
        );
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_ID)
            return new DatePickerDialog(this, dpickerListener, year_x, month_x, day_x);

        return null;
    }

    private DatePickerDialog.OnDateSetListener dpickerListener
            = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            year_x = year;
            month_x = month + 1;
            day_x = dayOfMonth;

            data_text.setText(day_x + "/" + month_x + "/" + year_x);

        }
    };

    private class MascaraMonetaria implements TextWatcher{

        final EditText campo;

        public MascaraMonetaria(EditText campo) {
            super();
            this.campo = campo;
        }

        private boolean isUpdating = false;
        // Pega a formatacao do sistema, se for brasil R$ se EUA US$
        private NumberFormat nf = NumberFormat.getCurrencyInstance();

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int after) {
            // Evita que o método seja executado varias vezes.
            // Se tirar ele entre em loop
            if (isUpdating) {
                isUpdating = false;
                return;
            }

            isUpdating = true;
            String str = s.toString();
            // Verifica se já existe a máscara no texto.
            boolean hasMask = ((str.indexOf("R$") > -1 || str.indexOf("$") > -1) &&
                    (str.indexOf(".") > -1 || str.indexOf(",") > -1));
            // Verificamos se existe máscara
            if (hasMask) {
                // Retiramos a máscara.
                str = str.replaceAll("[R$]", "").replaceAll("[,]", "")
                        .replaceAll("[.]", "");
            }

            try {
                // Transformamos o número que está escrito no EditText em
                // monetário.
                str = nf.format(Double.parseDouble(str) / 100);
                campo.setText(str);
                campo.setSelection(campo.getText().length());
            } catch (NumberFormatException e) {
                s = "";
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // Não utilizado
        }

        @Override
        public void afterTextChanged(Editable s) {
            // Não utilizado
        }
    }

    public void enviar_dados(View view) {

        DadosNuvem.salva(
                cnpj_text.getText() + " " +
                coo_text.getText() + " " +
                data_text.getText() + " " +
                valor_text.getText().subSequence(2, valor_text.getText().length()),
                "CUPOM",
                ong_cnpj_string);
        Toast.makeText(this, "Enviado com sucesso",Toast.LENGTH_LONG).show();
    }
}

