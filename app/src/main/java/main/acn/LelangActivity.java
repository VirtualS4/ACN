package main.acn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class LelangActivity extends AppCompatActivity {
    private TextView lbl_pemenang,lbl_harga_akhir,hidden;
    private EditText txt_tawar;
    private Button tombol;
    private ListView lv;
    private Bundle bun;
    private Lelang lelang;
    private SharedPreferences sharedPreferences;
    private DatabaseReference db;
    private ArrayList<History> list_h;
    private Masyarakat wins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lelang);
        db = FirebaseDatabase.getInstance().getReference();
        lv = findViewById(R.id.container);
        hidden = findViewById(R.id.hidden_id);
        lbl_pemenang = findViewById(R.id.winner);
        lbl_harga_akhir = findViewById(R.id.price);
        txt_tawar = findViewById(R.id.txt_tawar);
        tombol = findViewById(R.id.button);
        sharedPreferences = getSharedPreferences("account", Context.MODE_PRIVATE);
        list_h = new ArrayList<>();
        bun = getIntent().getExtras();

        lelang =  bun.getParcelable("Lelang");


        txt_tawar.setVisibility(View.INVISIBLE);

        tombol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LelangActivity.this,"Lelang Diberhentikan",Toast.LENGTH_LONG).show();
                initWinner();
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

        tombol.setText("Stop Lelang");

        if(sharedPreferences.getString("level","Kosong").equals("4000")){
            txt_tawar.setVisibility(View.VISIBLE);
            tombol.setText("Tawar");
            tombol.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(LelangActivity.this,"Tawaran Masuk",Toast.LENGTH_LONG).show();
                    String id = sharedPreferences.getString("id","Kosong");
                    String nama = sharedPreferences.getString("nama","Kosong");
                    int tawaran = Integer.parseInt(txt_tawar.getText().toString());
                    String id_lel = lelang.getId();
                    String id_his = db.push().getKey();
                    Account acc = new Account("Kosong","Kosong");
                    History newhistory = new History(id_his,id_lel,tawaran,new Masyarakat(id,nama,"Kosong",acc));
                    db.child("history").child(id_lel).child(id_his).setValue(newhistory);
                }
            });

        }else if(sharedPreferences.getString("level","Kosong").equals("4001")){
            tombol.setVisibility(View.INVISIBLE);
        }


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent bearsense = new Intent(LelangActivity.this,BarangDetail.class);
                Barang item = lelang.getBarang();
                bearsense.putExtra("Barang",item);
                bearsense.putExtra("ViewOnly","Yes");
                startActivity(bearsense);
            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();
        db.child("history").child(lelang.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list_h.clear();
                int price = lelang.getBarang().getHarga_awal();
                String pemenang = "Kosong";
                for(DataSnapshot bearss: dataSnapshot.getChildren()){
                    History bear = bearss.getValue(History.class);
                    list_h.add(bear);
                }
                Barang item = lelang.getBarang();
                ArrayList<Barang> temp = new ArrayList<>();
                temp.add(item);
                ArrayAdapter adapter = new AdapterBarang(LelangActivity.this,temp);
                lv.setAdapter(adapter);

                for (int i = 0;i<list_h.size();i++){
                    if(price<list_h.get(i).getPenawaran()){
                        price = list_h.get(i).getPenawaran();
                        pemenang = list_h.get(i).getBidder().getNama();
                        hidden.setText(list_h.get(i).getBidder().getId());
                    }
                }

                if(list_h.size()==0){
                    lbl_pemenang.setText("None");
                }else{
                    lbl_pemenang.setText(pemenang);
                }

                lbl_harga_akhir.setText("Rp. "+currencyFormatter(String.valueOf(price)));
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
    private String removeCurrency(String str){
       str = str.replaceAll("\\D+","");
       return str;
    }

    private void initWinner(){
        System.out.println("Hidden Data"+hidden.getText().toString());
        db.child("masyarakat").child(hidden.getText().toString()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Masyarakat bear = dataSnapshot.getValue(Masyarakat.class);
                    wins = bear;

                initPetugas();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initPetugas(){
        db.child("petugas").child(sharedPreferences.getString("id","Kosong")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Petugas penyelesai = null;
                    Petugas bear = dataSnapshot.getValue(Petugas.class);
                    penyelesai = bear;

                String id = lelang.getId();
                String tanggal = lelang.getTanggal_lelang();
                int harga_akhir = Integer.parseInt(removeCurrency(lbl_harga_akhir.getText().toString()));
                Masyarakat pemenang = wins;
                Barang item = lelang.getBarang();
                Lelang newLelang = new Lelang(id,tanggal,harga_akhir,pemenang,penyelesai,item);
                db.child("lelang").child(id).setValue(newLelang);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
