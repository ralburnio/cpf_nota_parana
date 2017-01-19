package com.google.android.gms.samples.vision.ocrreader;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Calendar;

/**
 * Created by ronaldo.alburnio on 05/01/2017.
 */

public class DadosNuvem {

    public static int salva(final String texto, String prefixo, String ong_cnpj){

        // Create a reference
        FirebaseStorage storage = FirebaseStorage.getInstance();

        // Create a storage reference from our app
        StorageReference storageRef = storage.getReferenceFromUrl("gs://ocr-reader-complete.appspot.com");

        // Create a reference to file name
        Calendar date = Calendar.getInstance();
        String fileName =
                ong_cnpj + "/" +
                prefixo +
                FirebaseInstanceId.getInstance().getId() +
                Integer.toString(date.get(Calendar.YEAR))+
                Integer.toString(date.get(Calendar.MONTH) + 1) +
                Integer.toString(date.get(Calendar.DAY_OF_MONTH)) +
                Integer.toString(date.get(Calendar.HOUR_OF_DAY)) +
                Integer.toString(date.get(Calendar.MINUTE)) +
                Integer.toString(date.get(Calendar.SECOND)) +
                ".txt";
        StorageReference qrCodeRef = storageRef.child(fileName);

        String qrCode = texto;

        InputStream stream = new ByteArrayInputStream(qrCode.getBytes());

        UploadTask uploadTask = qrCodeRef.putStream(stream);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
            }
        });

        return 0;
    }

    public static int salva_dados(final String texto, String prefixo, String ong_cnpj){

        //Removendo caracteres especiais para gerar o nome da instancia
        String nome = texto.replaceAll(" ","").replaceAll("/","").replaceAll("-","").replaceAll(",","").replaceAll("\\.","");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReferenceFromUrl("https://ocr-reader-complete.firebaseio.com").child(ong_cnpj).child(prefixo+nome);
        ref.setValue(texto);

        return 0;
    }
}
