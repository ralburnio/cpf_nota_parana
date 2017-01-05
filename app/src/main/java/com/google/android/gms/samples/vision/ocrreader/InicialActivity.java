package com.google.android.gms.samples.vision.ocrreader;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class InicialActivity extends Activity {

    private Button scan_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scan_button = (Button) findViewById(R.id.scan_button);
        final Activity activity = this;
        scan_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(activity,"mensagem enviada",Toast.LENGTH_LONG).show();

                FirebaseMessaging fm = FirebaseMessaging.getInstance();
                fm.send(new RemoteMessage.Builder(/*SENDER_ID + */"@gcm.googleapis.com")
                        .setMessageId(Integer.toString(1/*msgId.incrementAndGet()*/))
                        .addData("my_message", "Hello World")
                        .addData("my_action","SAY_HELLO")
                        .build());

                IntentIntegrator integrator = new IntentIntegrator(activity);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt("Scan");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();
            }
        });
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

        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null){
            if(result.getContents()==null){
                Toast.makeText(this, "You cancelled the scanning", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(this, result.getContents(),Toast.LENGTH_LONG).show();

                String qrCode = result.getContents().toString();
                Pattern pattern = Pattern.compile("chNFe=(.*?)&nVersao");
                Matcher matcher = pattern.matcher(qrCode);
                if (matcher.find()) {
                    qrCode = matcher.group(1);
                }

                DadosNuvem.salva(qrCode);
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
