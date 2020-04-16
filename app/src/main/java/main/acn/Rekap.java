package main.acn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class Rekap extends AppCompatActivity {

    private DatabaseReference db;
    private ArrayList<Lelang> list_lelang;
    private ListView lv;
    private TextView lbl_total,lbl_desc;
    private boolean status_petugas;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Masyarakat warga;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rekap);
        db = FirebaseDatabase.getInstance().getReference();
        status_petugas = false;
        sharedPreferences = getSharedPreferences("account", Context.MODE_PRIVATE);
        Button btn_akun = findViewById(R.id.btn_akun);
        btn_akun.setVisibility(View.INVISIBLE);
        lbl_desc = findViewById(R.id.lbl_desc);
        if(sharedPreferences.getString("level",null).equals("4000")){
            btn_akun.setVisibility(View.VISIBLE);
            lbl_desc.setText("Total Transaksi");
            status_petugas = false;
        }else{
            status_petugas = true;
        }

        btn_akun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent bearsense = new Intent(Rekap.this,MasyarakatDetail.class);
                bearsense.putExtra("Masyarakat",warga);
                bearsense.putExtra("ViewOnly","No");
                startActivity(bearsense);
            }
        });

        list_lelang = new ArrayList<>();
        lv = findViewById(R.id.container);
        lbl_total = findViewById(R.id.lbl_total);

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Intent bearsense = new Intent(Rekap.this,RekapDetail.class);
                Lelang bear = list_lelang.get(position);
                bearsense.putExtra("Lelang",bear);
                System.out.println(bear.getBarang().getGambar());
                startActivity(bearsense);
                return false;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        db.child("lelang").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list_lelang.clear();
                int total = 0;
                for(DataSnapshot bearss: dataSnapshot.getChildren()){
                    Lelang bear = bearss.getValue(Lelang.class);
                    if(status_petugas){
                        list_lelang.add(bear);
                        total = total + bear.getHarga_akhir();
                    }else{
                        if(bear.getPemenang().getId().equals(sharedPreferences.getString("id","Kosong"))){
                            list_lelang.add(bear);
                            total = total + bear.getHarga_akhir();
                        }
                    }
                }
                lbl_total.setText("Rp. "+currencyFormatter(String.valueOf(total)));
                ArrayAdapter adapter = new AdapterBarang(Rekap.this,list_lelang);
                lv.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        db.child("masyarakat").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                warga = null;
                for(DataSnapshot bearss: dataSnapshot.getChildren()){
                    Masyarakat bear = bearss.getValue(Masyarakat.class);
                    if(bear.getId().equals(sharedPreferences.getString("id","Kosong"))){
                        warga = bear;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private String currencyFormatter(String num) {
        double m = Double.parseDouble(num);
        DecimalFormat formatter = new DecimalFormat("###,###,###");
        return formatter.format(m);
    }

}
