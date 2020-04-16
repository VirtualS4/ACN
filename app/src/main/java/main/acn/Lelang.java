package main.acn;

import android.os.Parcel;
import android.os.Parcelable;

public class Lelang implements Parcelable {

    String id,tanggal_lelang;
    int harga_akhir;
    Masyarakat pemenang;
    Petugas penyelesai_lelang;
    Barang barang;

    public Lelang(){

    }

    public Lelang(String id,String tanggal_lelang,int harga_akhir,Masyarakat pemenang,Petugas penyelesai_lelang,Barang barang){
        this.id= id;
        this.tanggal_lelang = tanggal_lelang;
        this.harga_akhir = harga_akhir;
        this.pemenang = pemenang;
        this.penyelesai_lelang = penyelesai_lelang;
        this.barang = barang;
    }

    public String getId() {
        return id;
    }

    public String getTanggal_lelang() {
        return tanggal_lelang;
    }

    public int getHarga_akhir() {
        return harga_akhir;
    }

    public Masyarakat getPemenang() {
        return pemenang;
    }

    public Petugas getPenyelesai_lelang() {
        return penyelesai_lelang;
    }

    public Barang getBarang() {
        return barang;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.tanggal_lelang);
        dest.writeInt(this.harga_akhir);
        dest.writeParcelable(this.pemenang, flags);
        dest.writeParcelable(this.penyelesai_lelang, flags);
        dest.writeParcelable(this.barang, flags);
    }

    protected Lelang(Parcel in) {
        this.id = in.readString();
        this.tanggal_lelang = in.readString();
        this.harga_akhir = in.readInt();
        this.pemenang = in.readParcelable(Masyarakat.class.getClassLoader());
        this.penyelesai_lelang = in.readParcelable(Petugas.class.getClassLoader());
        this.barang = in.readParcelable(Barang.class.getClassLoader());
    }

    public static final Parcelable.Creator<Lelang> CREATOR = new Parcelable.Creator<Lelang>() {
        @Override
        public Lelang createFromParcel(Parcel source) {
            return new Lelang(source);
        }

        @Override
        public Lelang[] newArray(int size) {
            return new Lelang[size];
        }
    };
}
