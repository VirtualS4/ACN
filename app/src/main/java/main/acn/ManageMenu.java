package main.acn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ManageMenu extends AppCompatActivity {
    private Button btn_barang,btn_petugas,btn_warga,btn_lelang;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_items);

        btn_barang = findViewById(R.id.btn_barang);
        btn_petugas = findViewById(R.id.btn_akun_petugas);
        btn_warga = findViewById(R.id.btn_akun_masyarakat);
        btn_lelang = findViewById(R.id.btn_lelang);

        initBtn(btn_barang,"barang");
        initBtn(btn_petugas,"petugas");
        initBtn(btn_warga,"masyarakat");
        initBtn(btn_lelang,"lelang");


    }

    private void initBtn(Button btn,final String tipe){
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent bearsense = new Intent(ManageMenu.this,ManageActivity.class);
                bearsense.putExtra("tipe",tipe);
                startActivity(bearsense);
            }
        });
    }
}
