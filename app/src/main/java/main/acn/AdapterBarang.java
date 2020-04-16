package main.acn;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

public class AdapterBarang extends ArrayAdapter {
    private Activity con;
    private List<Lelang> lelang;
    private List<Barang> barang;
    private List<Masyarakat> masyarakat;
    private List<Petugas> petugas;
    private boolean list_lelang = false,list_petugas = false,list_masyarakat = false;

    public AdapterBarang(@NonNull Activity con, List<Lelang> list){
        super(con, R.layout.item_layout,list);
        this.con = con;
        this.lelang = list;
        list_lelang = true;
        System.out.println("Ini List : " + list_lelang);
    }

    public AdapterBarang(@NonNull Activity con, ArrayList<Barang> list){
        super(con, R.layout.item_layout,list);
        this.con = con;
        this.barang = list;
        list_lelang = false;
        System.out.println("Ini ArrayList : "+list_lelang);
    }

    public AdapterBarang(@NonNull Activity con, ArrayList<Masyarakat> list,String masyarakat){
        super(con, R.layout.item_layout,list);
        this.con = con;
        this.masyarakat = list;
        list_masyarakat = true;
        System.out.println("Ini ArrayList : "+list_masyarakat);
    }

    public AdapterBarang(@NonNull Activity con, ArrayList<Petugas> list,String temp,String petugas){
        super(con, R.layout.item_layout,list);
        this.con = con;
        this.petugas = list;
        list_petugas = true;
        System.out.println("Ini ArrayList : "+list_petugas);
    }


    @NonNull
    @Override
    public View getView(int position, View cview, ViewGroup parent){
        final LayoutInflater inflater = con.getLayoutInflater();

        View ListViewBear = inflater.inflate(R.layout.item_layout,null,true);

        if(list_lelang){
            TextView txt_harga = ListViewBear.findViewById(R.id.lbl_harga);
            TextView txt_nama_barang = ListViewBear.findViewById(R.id.lbl_nama);
            TextView txt_tanggal = ListViewBear.findViewById(R.id.lbl_tanggal);

            ImageView gambar = ListViewBear.findViewById(R.id.gambar);

            final Lelang bear = lelang.get(position);

            txt_nama_barang.setText(bear.getBarang().getNama());
            txt_harga.setText("Rp ."+currencyFormatter(String.valueOf(bear.getHarga_akhir())));
            txt_tanggal.setText(bear.getTanggal_lelang());

            getPicDatabase(bear.getBarang().getGambar(),gambar);
        }else if(list_masyarakat){
            TextView txt_harga = ListViewBear.findViewById(R.id.lbl_harga);
            TextView txt_nama_barang = ListViewBear.findViewById(R.id.lbl_nama);
            TextView txt_tanggal = ListViewBear.findViewById(R.id.lbl_tanggal);

            ImageView gambar = ListViewBear.findViewById(R.id.gambar);

            final Masyarakat bear = masyarakat.get(position);

            txt_nama_barang.setText(bear.getNama());
            txt_harga.setText("Masyarakat");
            txt_tanggal.setText(bear.getTelepon());

            getPicDatabase("Kosong",gambar);
        }else if(list_petugas){
            TextView txt_harga = ListViewBear.findViewById(R.id.lbl_harga);
            TextView txt_nama_barang = ListViewBear.findViewById(R.id.lbl_nama);
            TextView txt_tanggal = ListViewBear.findViewById(R.id.lbl_tanggal);

            ImageView gambar = ListViewBear.findViewById(R.id.gambar);

            final Petugas bear = petugas.get(position);

            txt_nama_barang.setText(bear.getNama());
            txt_harga.setText("Petugas");
            txt_tanggal.setText("-");

            getPicDatabase("Kosong",gambar);
        }else{
            TextView txt_harga = ListViewBear.findViewById(R.id.lbl_harga);
            TextView txt_nama_barang = ListViewBear.findViewById(R.id.lbl_nama);
            TextView txt_tanggal = ListViewBear.findViewById(R.id.lbl_tanggal);

            ImageView gambar = ListViewBear.findViewById(R.id.gambar);

            final Barang bear = barang.get(position);

            txt_nama_barang.setText(bear.getNama());
            txt_harga.setText("Rp ."+ currencyFormatter(String.valueOf(bear.getHarga_awal())));
            txt_tanggal.setText(bear.getTanggal_produksi());

//            String ds = bear.getGambar();
//            byte[] decodedString = ds.getBytes();
//            Bitmap picture = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
//            gambar.setImageBitmap(picture);

            getPicDatabase(bear.getGambar(),gambar);
        }

        return ListViewBear;
    }

    private void getPicDatabase(String nama_gambar,final ImageView imv){
        System.out.println("Ref : " + FirebaseStorageRef.getRef());
        FirebaseStorageRef.getRef().child("image").child(nama_gambar).getBytes(1024*1024*5).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap picture = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imv.setImageBitmap(picture);
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
