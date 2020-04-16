package main.acn;

import android.os.Parcel;
import android.os.Parcelable;

public class Masyarakat implements Parcelable {
    private String id,nama,telepon;
    private Account account;

    public Masyarakat(){

    }

    public Masyarakat(String id,String nama,String telepon,Account account){
        this.id = id;
        this.nama = nama;
        this.telepon = telepon;
        this.account = account;
    }

    public String getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public String getTelepon() {
        return telepon;
    }

    public Account getAccount() {
        return account;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.nama);
        dest.writeString(this.telepon);
        dest.writeParcelable(this.account, flags);
    }

    protected Masyarakat(Parcel in) {
        this.id = in.readString();
        this.nama = in.readString();
        this.telepon = in.readString();
        this.account = in.readParcelable(Account.class.getClassLoader());
    }

    public static final Parcelable.Creator<Masyarakat> CREATOR = new Parcelable.Creator<Masyarakat>() {
        @Override
        public Masyarakat createFromParcel(Parcel source) {
            return new Masyarakat(source);
        }

        @Override
        public Masyarakat[] newArray(int size) {
            return new Masyarakat[size];
        }
    };
}
