package main.acn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class PetugasDetail extends AppCompatActivity {

    private String warga_id;
    private Button btn_back,btn_confirm;
    private EditText txt_nama,txt_username,txt_password;
    private TextView lbl_title;
    private Petugas data;
    private DatabaseReference db;
    private Bundle bun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_petugas_detail);
        db = FirebaseDatabase.getInstance().getReference();
        txt_nama = findViewById(R.id.txt_nama);
        txt_username = findViewById(R.id.txt_username);
        txt_password = findViewById(R.id.txt_password);
        lbl_title = findViewById(R.id.lbl_title);


        btn_back = findViewById(R.id.btn_back);
        btn_confirm = findViewById(R.id.btn_confirm);
        bun = getIntent().getExtras();


        warga_id = "Kosong";
        initbtnEdit();
        if (bun.getParcelable("Petugas") != null){
            data = bun.getParcelable("Petugas");
            warga_id = data.getId();
            lbl_title.setText("Edit Data");
            fillField();
            if(bun.getString("ViewOnly").equals("Yes")){
                initDisplayOnly();
                initbtnView();
            }
        }


    }

    private void fillField(){
        txt_nama.setText(data.getNama());
        txt_username.setText(data.getAccount().getUsername());
        txt_password.setText(data.getAccount().getPassword());
    }

    private void initDisplayOnly(){
        lbl_title.setText("View Data");
        txt_nama.setEnabled(false);
        txt_username.setEnabled(false);
        txt_password.setEnabled(false);
    }

    private void initbtnView(){
        btn_back.setText("Back");
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_confirm.setText("Home");
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent bearsense = new Intent(PetugasDetail.this,MainActivity.class);
                startActivity(bearsense);
            }
        });
    }

    private void initbtnEdit(){
        btn_back.setText("Cancel");
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if(bun.getParcelable("Petugas") != null){
            btn_confirm.setText("Confirm Edit");
        }else{
            btn_confirm.setText("Register");
        }
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateItem();
                Toast.makeText(PetugasDetail.this,"Please Wait 3 Second",Toast.LENGTH_LONG).show();
                Handler hand = new Handler();
                Runnable run = new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                };
                hand.postDelayed(run,3000);

            }
        });
    }


    private boolean isEmpty(EditText txt){
        boolean status = txt.getText().toString().isEmpty();
        return status;
    }

    private void updateItem(){
        if(isEmpty(txt_nama)||isEmpty(txt_username)||isEmpty(txt_password)){
            Toast.makeText(PetugasDetail.this,"Masih ada Data Kosong",Toast.LENGTH_LONG).show();
        }else {
            boolean status_lelang = false;
            if (warga_id.equals("Kosong")) {
                warga_id = db.push().getKey();
                status_lelang = false;
            }else{
                status_lelang = true;
            }
            String nama = txt_nama.getText().toString();
            String username = txt_username.getText().toString();
            String password = txt_password.getText().toString();
            Account account = new Account(username,password);
            Level lvl = new Level("4002","Petugas");
            final Petugas item = new Petugas(warga_id, nama,lvl,account);
            if (status_lelang) {
                System.out.println("Trying to Update the lelang");
                db.child("lelang").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot bearss : dataSnapshot.getChildren()) {
                            Barang stuff = bearss.child("barang").getValue(Barang.class);
                            if (stuff.getId().equals(warga_id)) {
                                bearss.child("penyelesai_lelang").getRef().setValue(item);
                                System.out.println("Lelang updated");
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
            db.child("petugas").child(warga_id).setValue(item);
            Toast.makeText(PetugasDetail.this,"Data Berhasil Dimasukan",Toast.LENGTH_LONG).show();

        }
    }


}

