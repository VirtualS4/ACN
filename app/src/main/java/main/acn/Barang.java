package main.acn;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class Barang implements Parcelable {
    private String id,nama,tanggal_produksi,deskripsi;
    private int harga_awal;
    private String gambar;

    public Barang(){

    }

    public Barang(String id,String nama,int harga_awal,String tanggal_produksi,String deskripsi,String gambar){
        this.id = id;
        this.nama = nama;
        this.harga_awal = harga_awal;
        this.tanggal_produksi = tanggal_produksi;
        this.deskripsi = deskripsi;
        this.gambar = gambar;
    }

    public String getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public int getHarga_awal() {
        return harga_awal;
    }

    public String getTanggal_produksi() {
        return tanggal_produksi;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public String getGambar() {
        return gambar;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.nama);
        dest.writeString(this.tanggal_produksi);
        dest.writeString(this.deskripsi);
        dest.writeInt(this.harga_awal);
        dest.writeString(this.gambar);
    }

    protected Barang(Parcel in) {
        this.id = in.readString();
        this.nama = in.readString();
        this.tanggal_produksi = in.readString();
        this.deskripsi = in.readString();
        this.harga_awal = in.readInt();
        this.gambar = in.readString();
    }

    public static final Parcelable.Creator<Barang> CREATOR = new Parcelable.Creator<Barang>() {
        @Override
        public Barang createFromParcel(Parcel source) {
            return new Barang(source);
        }

        @Override
        public Barang[] newArray(int size) {
            return new Barang[size];
        }
    };
}
