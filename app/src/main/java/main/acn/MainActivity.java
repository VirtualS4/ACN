package main.acn;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference db;
    private ArrayList<Lelang> listlelang,listongoing;
    private ListView longoing,lupcoming;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private TextView txt_nama;
    private Masyarakat masyarakat;
    private Petugas petugas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sharedPref = getSharedPreferences("account",Context.MODE_PRIVATE);
        db = FirebaseDatabase.getInstance().getReference();

        if (sharedPref.getString("level","Empty").equals("Empty")){
            Intent back = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(back);
            System.out.println(sharedPref.getString("level","Empty"));
            System.out.println("Im Back");
        }

        longoing = findViewById(R.id.list_ongoing);
        lupcoming = findViewById(R.id.list_upcoming);
        listlelang = new ArrayList<>();
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent bearsense = new Intent(MainActivity.this, ManageMenu.class);
                startActivity(bearsense);
            }
        });

        Button btn_logout = findViewById(R.id.btn_logout);
        Button btn_rekap = findViewById(R.id.btn_rekap);



        editor = sharedPref.edit();
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString("level", "Empty");
                editor.putString("id", "Empty");
                editor.putString("nama", "Empty");
                editor.apply();
                Intent back = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(back);
                finish();
            }
        });

        btn_rekap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back = new Intent(MainActivity.this, Rekap.class);
                startActivity(back);
            }
        });

        txt_nama = findViewById(R.id.lbl_nama);

        txt_nama.setText(sharedPref.getString("nama","Empty"));

        if(sharedPref.getString("level","Empty").equals("4000")){
            fab.hide();
            btn_rekap.setText("Rekap");
            btn_rekap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent bearsense = new Intent(MainActivity.this,Rekap.class);
                    startActivity(bearsense);
                }
            });
        }

        longoing.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent bearsense = new Intent(MainActivity.this,LelangActivity.class);
                Lelang lelang = listongoing.get(position);
                bearsense.putExtra("Lelang",lelang);
                startActivity(bearsense);
            }
        });

        lupcoming.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent bearsense = new Intent(MainActivity.this,BarangDetail.class);
                Lelang lelang = listlelang.get(position);
                bearsense.putExtra("Barang",lelang.getBarang());
                bearsense.putExtra("ViewOnly","Yes");
                startActivity(bearsense);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        db.child("lelang").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listlelang.clear();
                for(DataSnapshot bearss: dataSnapshot.getChildren()){
                    Lelang bear = bearss.getValue(Lelang.class);
                    listlelang.add(bear);
                }
                ArrayList<Lelang> temp = new ArrayList<>();
                Date date = new Date();
                SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
                String tanggal = dateFormatter.format(date);
                for (int i = 0;i<listlelang.size();i++){
                    if(listlelang.get(i).getTanggal_lelang().equalsIgnoreCase(tanggal)&&listlelang.get(i).getHarga_akhir() == 0){
                        temp.add(listlelang.get(i));
                        System.out.println("Items :" + listlelang.get(i));
                        listlelang.remove(i);
                        i--;
                    }
                }

                for (int i = 0;i<listlelang.size();i++){
                    try{
                        Date lelang_date = dateFormatter.parse(listlelang.get(i).getTanggal_lelang());
                        if(lelang_date.before(date)){
                            listlelang.remove(i);
                            i--;
                        }
                    }catch (ParseException ex){
                        System.out.println(ex);
                    }
                }


                listongoing = temp;
                ///
                ArrayAdapter adapterongoing = new AdapterBarang(MainActivity.this, temp);
                ArrayAdapter adapterupcoming = new AdapterBarang(MainActivity.this, listlelang);
                longoing.setAdapter(adapterongoing);
                lupcoming.setAdapter(adapterupcoming);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        if(sharedPref.getString("level","Empty").equals("4000")){
            db.child("masyarakat").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot bearss: dataSnapshot.getChildren()){
                        Masyarakat bear = bearss.getValue(Masyarakat.class);
                        if (bear.getId().equals(sharedPref.getString("id","Empty"))){
                            masyarakat = bear;
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else{
            db.child("petugas").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot bearss: dataSnapshot.getChildren()){
                        Petugas bear = bearss.getValue(Petugas.class);
                        if (bear.getId().equals(sharedPref.getString("id","Empty"))){
                            petugas = bear;
                        }

                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }

    private String currencyFormatter(String num) {
        double m = Double.parseDouble(num);
        DecimalFormat formatter = new DecimalFormat("###,###,###");
        return formatter.format(m);
    }
}
