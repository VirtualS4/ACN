package main.acn;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;

public class RegisterActivity extends AppCompatActivity {

    private EditText txt_username, txt_password, txt_nama, txt_telepon;
    private Button btn_back, btn_confirm;
    private boolean petugas;
    private DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        petugas = false;
        txt_username = findViewById(R.id.txt_username);
        txt_password = findViewById(R.id.txt_password);
        txt_nama = findViewById(R.id.txt_nama);
        txt_telepon = findViewById(R.id.txt_telp);

        db = FirebaseDatabase.getInstance().getReference("");

        btn_back = findViewById(R.id.btn_back);
        btn_confirm = findViewById(R.id.btn_confirm);


        Bundle bun = getIntent().getExtras();

        if (bun.getString("Tipe Registrasi").equals("Petugas")) {
            petugas = true;
            txt_telepon.setVisibility(View.INVISIBLE);
        } else {

        }

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmDialog();
            }
        });
    }

    private void showConfirmDialog() {
        AlertDialog.Builder db = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dv = inflater.inflate(R.layout.dialog_register_confirm, null);
        db.setView(dv);
        final AlertDialog dialog = db.create();

        TextView lbl_username = dv.findViewById(R.id.lbl_username);
        TextView lbl_password = dv.findViewById(R.id.lbl_password);
        TextView lbl_nama = dv.findViewById(R.id.lbl_nama);
        TextView lbl_telepon = dv.findViewById(R.id.lbl_telp);
        TextView desc_telepon = dv.findViewById(R.id.desc_telp);

        Button btn_yes = dv.findViewById(R.id.btn_confirm);
        Button btn_no = dv.findViewById(R.id.btn_cancel);

        if (petugas) {
            desc_telepon.setVisibility(View.INVISIBLE);
            lbl_telepon.setVisibility(View.INVISIBLE);
        }

        lbl_username.setText(txt_username.getText());
        lbl_password.setText(txt_password.getText());
        lbl_nama.setText(txt_nama.getText());
        lbl_telepon.setText(phoneFormatter(txt_telepon.getText().toString()));

        dialog.show();
        btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accountRegister();
                Intent intent = new Intent();
                if (petugas) {
                    intent = new Intent(RegisterActivity.this, MainActivity.class);
                } else {
                    intent = new Intent(RegisterActivity.this, LoginActivity.class);
                }

                startActivity(intent);

            }
        });
    }

    private String phoneFormatter(String num) {
        double m = Double.parseDouble(num);
        DecimalFormat formatter = new DecimalFormat("####-####-####");
        return formatter.format(m);
    }

    private void accountRegister() {
        String id = db.push().getKey();
        if (petugas) {
            Level lvl = new Level("4002", "Petugas");
            Account acc = new Account(txt_username.getText().toString(), txt_password.getText().toString());
            Petugas petugas = new Petugas(id, txt_nama.getText().toString(), lvl, acc);
            db.child("petugas").child(id).setValue(petugas);
        } else {
            Account acc = new Account(txt_username.getText().toString(), txt_password.getText().toString());
            Masyarakat masyarakat = new Masyarakat(id, txt_nama.getText().toString(), txt_telepon.getText().toString(), acc);
            db.child("masyarakat").child(id).setValue(masyarakat);
        }
        Toast.makeText(this, "Akun Berhasil Dibuat", Toast.LENGTH_LONG).show();
    }
}
