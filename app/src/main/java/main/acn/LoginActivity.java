package main.acn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {
    DatabaseReference db;
    private EditText txt_user,txt_password;
    private Button btn_register,btn_login;
    private ArrayList<Masyarakat> listm;
    private ArrayList<Petugas> listp;
    private boolean status_data;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        String owo = " ";
        System.out.println("Is OwO Empty? : "+ owo.isEmpty());


        sharedPref = getSharedPreferences("account",Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        FirebaseStorageRef.initStorage();

        db = FirebaseDatabase.getInstance().getReference();
        listm = new ArrayList<>();
        listp = new ArrayList<>();

        txt_user = findViewById(R.id.txt_username);
        txt_password = findViewById(R.id.txt_password);
        btn_login = findViewById(R.id.btn_login);
        btn_register = findViewById(R.id.btn_register);
        status_data = false;

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLogin();
                System.out.println("Status After : "+status_data);
                if(status_data){
                    loginSuccess();
                }else{
                    Toast.makeText(LoginActivity.this,"Username/Password Salah",Toast.LENGTH_LONG).show();
                }
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,MasyarakatDetail.class);
                intent.putExtra("ViewOnly","No");
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        db.child("masyarakat").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listm.clear();
                for(DataSnapshot bearss: dataSnapshot.getChildren()){
                    Masyarakat bear = bearss.getValue(Masyarakat.class);
                    listm.add(bear);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        db.child("petugas").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listp.clear();
                for(DataSnapshot bearss: dataSnapshot.getChildren()){
                    Petugas bear = bearss.getValue(Petugas.class);
                    listp.add(bear);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkLogin(){
        String username = txt_user.getText().toString();
        String password = txt_password.getText().toString();

        for(int i = 0;i<listm.size();i++){
            Account check = listm.get(i).getAccount();
            if (check.getUsername().equalsIgnoreCase(username)&&check.getPassword().equals(password)){
                status_data = true;
                editor.putString("id", listm.get(i).getId());
                editor.putString("nama", listm.get(i).getNama());
                editor.putString("level","4000");
                editor.commit();
                break;
            }else{

            }
        }
        System.out.println("Status Before : "+status_data);
        if(!status_data){
            for(int i = 0;i<listp.size();i++){
                Account check = listp.get(i).getAccount();

                if (check.getUsername().equalsIgnoreCase(username)&&check.getPassword().equals(password)){
                    status_data = true;
                        editor.putString("id", listp.get(i).getId());
                        editor.putString("nama", listp.get(i).getNama());
                        editor.putString("level", listp.get(i).getLevel().getId());
                        editor.commit();
                    break;
                }
            }
        }
    }

    private void loginSuccess(){
        Handler hand = new Handler();
        Runnable run = new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        };
        System.out.println(sharedPref.getString("level","Empty"));

        hand.postDelayed(run,500);


    }
}
