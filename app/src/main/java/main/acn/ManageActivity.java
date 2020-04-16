package main.acn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ManageActivity extends AppCompatActivity {

    private ListView lv;
    private DatabaseReference db;
    private Bundle bun;
    private Button tambah;
    private String tipe;
    private ArrayList<Masyarakat> list_m;
    private ArrayList<Petugas> list_p;
    private ArrayList<Barang> list_b;
    private ArrayList<Lelang> list_l;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);

        tambah = findViewById(R.id.btn_tambah);
        list_b = new ArrayList<>();
        list_m = new ArrayList<>();
        list_p = new ArrayList<>();
        list_l = new ArrayList<>();
        lv = findViewById(R.id.container);

        bun = getIntent().getExtras();
        tipe = bun.getString("tipe");
        db = FirebaseDatabase.getInstance().getReference(tipe);

        tambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(tipe.equals("barang")){
                    Intent bearsense = new Intent(ManageActivity.this,BarangDetail.class);
                    bearsense.putExtra("ViewOnly","No");
                    startActivity(bearsense);
                }else if(tipe.equals("masyarakat")){
                    Intent bearsense = new Intent(ManageActivity.this,MasyarakatDetail.class);
                    bearsense.putExtra("ViewOnly","No");
                    startActivity(bearsense);
                }else if(tipe.equals("petugas")){
                    Intent bearsense = new Intent(ManageActivity.this,PetugasDetail.class);
                    bearsense.putExtra("ViewOnly","No");
                    startActivity(bearsense);
                }else if(tipe.equals("lelang")){
                    Intent bearsense = new Intent(ManageActivity.this,LelangDetail.class);
                    bearsense.putExtra("ViewOnly","No");
                    startActivity(bearsense);
                }
            }
        });
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                if (tipe.equals("barang")){
                    Intent bearsense = new Intent(ManageActivity.this,BarangDetail.class);
                    Barang bear = list_b.get(position);
                    bearsense.putExtra("Barang",bear);
                    bearsense.putExtra("ViewOnly","No");
                    startActivity(bearsense);
                }else if (tipe.equals("masyarakat")){
                    Intent bearsense = new Intent(ManageActivity.this,MasyarakatDetail.class);
                    Masyarakat bear = list_m.get(position);
                    bearsense.putExtra("Masyarakat",bear);
                    bearsense.putExtra("ViewOnly","No");
                    startActivity(bearsense);
                }else if (tipe.equals("petugas")){
                    Intent bearsense = new Intent(ManageActivity.this,PetugasDetail.class);
                    Petugas bear = list_p.get(position);
                    bearsense.putExtra("Petugas",bear);
                    bearsense.putExtra("ViewOnly","No");
                    startActivity(bearsense);
                }else if (tipe.equals("lelang")){
                    Intent bearsense = new Intent(ManageActivity.this,LelangDetail.class);
                    Lelang bear = list_l.get(position);
                    bearsense.putExtra("Lelang",bear);
                    bearsense.putExtra("ViewOnly","No");
                    startActivity(bearsense);
                }
                return false;
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list_b.clear();
                list_p.clear();
                list_m.clear();
                list_l.clear();
                for(DataSnapshot bearss: dataSnapshot.getChildren()){
                    if(tipe.equals("barang")){
                        Barang bear = bearss.getValue(Barang.class);
                        list_b.add(bear);
                    }else if(tipe.equals("masyarakat")){
                        Masyarakat bear = bearss.getValue(Masyarakat.class);
                        list_m.add(bear);
                    }else if(tipe.equals("petugas")){

                        Petugas bear = bearss.getValue(Petugas.class);
                        if (!bear.getLevel().getNama().equals("admin")){
                            list_p.add(bear);
                        }
                    }else if(tipe.equals("lelang")){
                        Lelang bear = bearss.getValue(Lelang.class);
                        list_l.add(bear);
                    }
                }

                ArrayAdapter adapter;
                if(tipe.equals("barang")){
                    adapter = new AdapterBarang(ManageActivity.this,list_b);
                }else if(tipe.equals("masyarakat")){
                    adapter = new AdapterBarang(ManageActivity.this,list_m,"");
                }else if(tipe.equals("petugas")){
                    adapter = new AdapterBarang(ManageActivity.this,list_p,"","");
                }else if(tipe.equals("lelang")){
                    adapter = new AdapterBarang(ManageActivity.this,list_l);
                }else{
                    adapter = new AdapterBarang(ManageActivity.this,list_b);
                }
                lv.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
