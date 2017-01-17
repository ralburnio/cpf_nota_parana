package com.google.android.gms.samples.vision.ocrreader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class InicialActivity extends Activity {

    private static Button scan_button, cupom_button;
    private static EditText ong_cnpj_text;
    private String ong_cnpj_string;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scan_button  = (Button) findViewById(R.id.scan_button);
        cupom_button = (Button) findViewById(R.id.cupom_button);

        //scan_button.setVisibility(View.INVISIBLE);
        //cupom_button.setVisibility(View.INVISIBLE);
        final Activity activity = this;
        scan_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator integrator = new IntentIntegrator(activity);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt("Scan");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(true);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();
            }
        });

        ong_cnpj_text = (EditText) findViewById(R.id.ong_cnpj_text);
        ong_cnpj_text.addTextChangedListener(CpfCnpjMasks.insert(ong_cnpj_text));
    }

    public void ir_camera(View view) {
        Intent intent = new Intent(this, OcrCaptureActivity.class);
        startActivity(intent);
    }

    public void digitar_cupom(View view) {
        Intent intent = new Intent(this, ValidarDadosActivity.class);
        Bundle bundle = new Bundle();

        bundle.putString("cnpj", "");
        intent.putExtras(bundle);

        bundle.putString("coo", "");
        intent.putExtras(bundle);

        bundle.putString("data", "DATA");
        intent.putExtras(bundle);

        bundle.putString("valor", "");
        intent.putExtras(bundle);

        bundle.putString("ong_cnpj_string", ong_cnpj_string);
        intent.putExtras(bundle);

        startActivity(intent);
    }

    public void enviar_ong_cnpj(View view) {
        if ( ong_cnpj_text.getText().length() >= 18 ) {
            scan_button.setVisibility(View.VISIBLE);
            cupom_button.setVisibility(View.VISIBLE);

            ong_cnpj_string = ong_cnpj_text.getText().toString().replace("/", "")
                                                                .replace(".", "")
                                                                .replace("-", "");
        }
        else {
            scan_button.setVisibility(View.INVISIBLE);
            cupom_button.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "Preencha o CNPJ da ONG", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null){
            if(result.getContents()==null){
                Toast.makeText(this, "Cancelado", Toast.LENGTH_LONG).show();
            }
            else {
                String qrCode = result.getContents().toString();
                Pattern pattern = Pattern.compile("chNFe=(.*?)&nVersao");
                Matcher matcher = pattern.matcher(qrCode);
                if (matcher.find()) {
                    qrCode = matcher.group(1);
                }

                DadosNuvem.salva(qrCode, "QR", ong_cnpj_string);
                Toast.makeText(this, "Enviado com sucesso",Toast.LENGTH_LONG).show();
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
