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
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
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

public class BarangDetail extends AppCompatActivity {

    private StorageReference sr;
    private String item_id;
    private Button btn_gambar,btn_back,btn_confirm;
    private ImageView img;
    private InputStream stream;
    private boolean status_gambar;
    private EditText txt_nama,txt_harga,txt_desc,txt_tanggal;
    private DatePickerDialog.OnDateSetListener date;
    private Calendar myCalendar;
    private Barang data;
    private DatabaseReference db;
    private Uri imageUri;
    private Bundle bun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barang_detail);
        sr = FirebaseStorageRef.getRef();
        db = FirebaseDatabase.getInstance().getReference();
        status_gambar = false;
        txt_nama = findViewById(R.id.txt_nama_barang);
        txt_harga = findViewById(R.id.txt_harga_barang);
        txt_desc = findViewById(R.id.txt_desc_barang);
        txt_tanggal = findViewById(R.id.txt_tanggal);
        img = findViewById(R.id.gambar);
        btn_gambar = findViewById(R.id.btn_image);
        btn_back = findViewById(R.id.btn_back);
        btn_confirm = findViewById(R.id.btn_confirm);
        bun = getIntent().getExtras();


        item_id = "Kosong";
        initbtnEdit();
        if (bun.getParcelable("Barang") != null){
            data = bun.getParcelable("Barang");
            item_id = data.getId();
            fillField();
            if(bun.getString("ViewOnly").equals("Yes")){
                initDisplayOnly();
                initbtnView();
            }
        }



        initDatePicker();



        btn_gambar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pilih();
            }
        });

    }

    private void fillField(){
        txt_nama.setText(data.getNama());
        txt_harga.setText(String.valueOf(data.getHarga_awal()));
        txt_desc.setText(data.getDeskripsi());
        txt_tanggal.setText(data.getTanggal_produksi());
        getPicDatabase(data.getGambar(),img);
    }

    private void initDisplayOnly(){
        btn_gambar.setVisibility(View.INVISIBLE);
        txt_nama.setEnabled(false);
        txt_desc.setEnabled(false);
        txt_harga.setEnabled(false);
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
                Intent bearsense = new Intent(BarangDetail.this,MainActivity.class);
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
        if(bun.getParcelable("Barang") != null){
            btn_confirm.setText("Confirm Edit");
        }else{
            btn_confirm.setText("Register");
        }
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateItem();
                Toast.makeText(BarangDetail.this,"Please Wait 3 Second",Toast.LENGTH_LONG).show();
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

    private void Pilih() {
        final Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            //First we gotta make sure to add the images to
                try {
                    status_gambar = true;
                    //try uploading it
                    imageUri = data.getData();
                    stream = getContentResolver().openInputStream(imageUri);

                    Bitmap selectedImage = BitmapFactory.decodeStream(stream);
                    img.setImageBitmap(selectedImage);

                }catch(FileNotFoundException e){
                    System.out.println(e);
                }
                }

    }

    private void sendImgToStorage(){
        System.out.println("Trying to Upload Image");
        if (status_gambar){
            StorageReference imageStorage = sr.child("image").child(item_id);
            System.out.println("Stream : "+stream);
            Uri file = imageUri;

            imageStorage.putFile(file)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get a URL to the uploaded content
//                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            System.out.println("Berhasil Gambar");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            // ...
                            System.out.println("Gagal Gambar");
                        }
                    });
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
                new DatePickerDialog(BarangDetail.this, date, myCalendar
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

    private void getPicDatabase(String nama_gambar,final ImageView imv){
        FirebaseStorageRef.getRef().child("image").child(nama_gambar).getBytes(1024*1024*5).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap picture = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imv.setImageBitmap(picture);
                System.out.println("Bearhasil");
                Toast.makeText(BarangDetail.this,"Gambar Berhasil Dipilih",Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("Ngambil Gambar Gagal");
                Toast.makeText(BarangDetail.this,"Gambar Gagal Dipilih",Toast.LENGTH_LONG).show();
            }
        });

    }

    private boolean isEmpty(EditText txt){
        boolean status = txt.getText().toString().isEmpty();
        return status;
    }

    private void updateItem(){
        if(isEmpty(txt_nama)||isEmpty(txt_desc)||isEmpty(txt_harga)||isEmpty(txt_tanggal)){
            Toast.makeText(BarangDetail.this,"Masih ada Data Kosong",Toast.LENGTH_LONG).show();
        }else {
            boolean status_lelang = false;
            if (item_id.equals("Kosong")) {
                item_id = db.push().getKey();
                status_lelang = false;
            }else{
                status_lelang = true;
            }
            String nama = txt_nama.getText().toString();
            int harga = Integer.parseInt(txt_harga.getText().toString());
            String tanggal = txt_tanggal.getText().toString();
            String desc = txt_desc.getText().toString();
            String gambar = item_id;
            final Barang item = new Barang(item_id, nama, harga, tanggal, desc, gambar);
            if (status_lelang) {
                System.out.println("Trying to Update the lelang");
                db.child("lelang").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int total = 0;
                        for (DataSnapshot bearss : dataSnapshot.getChildren()) {
                                Barang stuff = bearss.child("barang").getValue(Barang.class);
                                if (stuff.getId().equals(item_id)) {
                                    bearss.child("barang").getRef().setValue(item);
                                    System.out.println("Lelang updated");
                                }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
            db.child("barang").child(item_id).setValue(item);
            Toast.makeText(BarangDetail.this,"Data Berhasil Dimasukan",Toast.LENGTH_LONG).show();
            sendImgToStorage();

        }
    }

    private String currencyFormatter(String num) {
        double m = Double.parseDouble(num);
        DecimalFormat formatter = new DecimalFormat("###,###,###");
        return formatter.format(m);
    }



    }

