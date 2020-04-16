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
import android.widget.Spinner;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class LelangDetail extends AppCompatActivity {

    private String lelang_id;
    private Button btn_back,btn_confirm;
    private EditText txt_tanggal;
    private Spinner spinner;
    private TextView lbl_title;
    private Lelang data;
    private DatabaseReference db;
    private Bundle bun;
    private Calendar myCalendar;
    private DatePickerDialog.OnDateSetListener date;
    private ArrayList<Barang> list_b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lelang_detail);
        db = FirebaseDatabase.getInstance().getReference();
        txt_tanggal = findViewById(R.id.txt_tanggal);
        lbl_title = findViewById(R.id.lbl_title);

        spinner = findViewById(R.id.spinner);

        list_b = new ArrayList<>();
        btn_back = findViewById(R.id.btn_back);
        btn_confirm = findViewById(R.id.btn_confirm);
        bun = getIntent().getExtras();


        lelang_id = "Kosong";
        initbtnEdit();
        if (bun.getParcelable("Lelang") != null){
            data = bun.getParcelable("Lelang");
            lelang_id = data.getId();
            lbl_title.setText("Edit Data");
            fillField();
            if(bun.getString("ViewOnly").equals("Yes")){
                initDisplayOnly();
                initbtnView();
            }
        }

        initDatePicker();


    }

    private void fillField(){
        txt_tanggal.setText(data.getTanggal_lelang());
    }

    private void initDisplayOnly(){
        lbl_title.setText("View Data");
        txt_tanggal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        txt_tanggal.setEnabled(false);

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
                Intent bearsense = new Intent(LelangDetail.this,MainActivity.class);
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
        if(bun.getParcelable("Lelang") != null){
            btn_confirm.setText("Confirm Edit");
        }else{
            btn_confirm.setText("Register");
        }
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateItem();
                Toast.makeText(LelangDetail.this,"Please Wait 3 Second",Toast.LENGTH_LONG).show();
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

    private boolean isEmpty(Spinner spin){
        boolean status = false;
        if(spinner.getSelectedItem() !=null){
            status = true;
        }
        System.out.println("Spin Bool : "+status);
        return !status;
    }

    private void updateItem(){
        if(isEmpty(txt_tanggal)||isEmpty(spinner)){
            Toast.makeText(LelangDetail.this,"Masih ada Data Kosong",Toast.LENGTH_LONG).show();
        }else {
            Masyarakat pemenang;
            Barang barang;
            Petugas penyelesai;
            int harga_akhir;
            String tanggal;
            if (lelang_id.equals("Kosong")) {
                lelang_id = db.push().getKey();
                Account acc = new Account("Kosong","Kosong");
                Level lvl = new Level("Kosong","Kosong");
                pemenang = new Masyarakat("Kosong","Kosong","Kosong",acc);
                barang = list_b.get(spinner.getSelectedItemPosition());
                System.out.println("Spinner "+spinner.getSelectedItemPosition());
                penyelesai = new Petugas("Kosong","Kosong",lvl,acc);
                harga_akhir = 0;
                tanggal = txt_tanggal.getText().toString();

            }else{
                pemenang = data.getPemenang();
                barang = list_b.get(spinner.getSelectedItemPosition());
                System.out.println("Spinner "+spinner.getSelectedItemPosition());
                penyelesai = data.getPenyelesai_lelang();
                harga_akhir = data.getHarga_akhir();
                tanggal = txt_tanggal.getText().toString();
            }


            final Lelang item = new Lelang(lelang_id,tanggal,harga_akhir,pemenang,penyelesai,barang);

            db.child("lelang").child(lelang_id).setValue(item);
            Toast.makeText(LelangDetail.this,"Data Berhasil Dimasukan",Toast.LENGTH_LONG).show();

        }
    }

    private void initDatePicker(){
        myCalendar = Calendar.getInstance();

        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        txt_tanggal.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(LelangDetail.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void updateLabel() {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);

        txt_tanggal.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    protected void onStart() {
        super.onStart();
        db.child("barang").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list_b.clear();
                for(DataSnapshot bearss: dataSnapshot.getChildren()){
                    Barang bear = bearss.getValue(Barang.class);
                    list_b.add(bear);
                }

                ArrayList<String> temp = new ArrayList<>();
                for(int i = 0;i<list_b.size();i++){
                    temp.add(list_b.get(i).getNama());
                }
                List<String> list_item = temp;
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(LelangDetail.this,android.R.layout.simple_spinner_item,list_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

