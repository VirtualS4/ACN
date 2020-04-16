package main.acn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DecimalFormat;

public class RekapDetail extends AppCompatActivity {

    private Bundle data;
    private TextView lbl_barang,lbl_masyarakat,lbl_petugas,lbl_tanggal,lbl_total;
    private Button btn_barang,btn_masyarakat,btn_petugas,btn_back,btn_home;
    private Lelang lelang;
    private ImageView gambar;
    private StorageReference dbs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rekap_detail);

        dbs = FirebaseStorage.getInstance().getReference();

        lbl_barang = findViewById(R.id.lbl_barang);
        lbl_masyarakat = findViewById(R.id.lbl_pemenang);
        lbl_petugas = findViewById(R.id.lbl_petugas);
        lbl_tanggal = findViewById(R.id.lbl_tanggal);
        lbl_total = findViewById(R.id.lbl_total);
        gambar = findViewById(R.id.gambar);

        data = getIntent().getExtras();
        lelang = data.getParcelable("Lelang");

        lbl_barang.setText(lelang.getBarang().getNama());
        lbl_masyarakat.setText(lelang.getPemenang().getNama());
        lbl_petugas.setText(lelang.getPenyelesai_lelang().getNama());
        lbl_tanggal.setText(lelang.getTanggal_lelang());
        lbl_total.setText("Rp. "+ currencyFormatter(String.valueOf(lelang.getHarga_akhir())));

        btn_back = findViewById(R.id.btn_back);
        btn_home = findViewById(R.id.btn_home);

        btn_barang = findViewById(R.id.btn_barang);
        btn_masyarakat = findViewById(R.id.btn_pemenang);
        btn_petugas = findViewById(R.id.btn_petugas);


        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent bearsense = new Intent(RekapDetail.this,MainActivity.class);
                startActivity(bearsense);
            }
        });

        getPicDatabase(lelang.getBarang().getGambar());

        btn_barang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Barang item = lelang.getBarang();
                Intent bearsense = new Intent(RekapDetail.this,BarangDetail.class);
                bearsense.putExtra("Barang",item);
                bearsense.putExtra("ViewOnly","Yes");
                startActivity(bearsense);
            }
        });

        btn_petugas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Petugas item = lelang.getPenyelesai_lelang();
                Intent bearsense = new Intent(RekapDetail.this,PetugasDetail.class);
                bearsense.putExtra("Petugas",item);
                bearsense.putExtra("ViewOnly","Yes");
                startActivity(bearsense);
            }
        });

        btn_masyarakat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent bearsense = new Intent(RekapDetail.this,MasyarakatDetail.class);
                Masyarakat masyarakat = lelang.getPemenang();
                bearsense.putExtra("Masyarakat",masyarakat);
                bearsense.putExtra("ViewOnly","Yes");
                startActivity(bearsense);
            }
        });

    }

    private void getPicDatabase(String nama_gambar){
        FirebaseStorageRef.getRef().child("image").child(nama_gambar).getBytes(1024*1024*5).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap picture = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                gambar.setImageBitmap(picture);
                System.out.println("Bearhasil");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("Ngambil Gambar Gagal");
            }
        });

    }

    private String currencyFormatter(String num) {
        double m = Double.parseDouble(num);
        DecimalFormat formatter = new DecimalFormat("###,###,###");
        return formatter.format(m);
    }
}
