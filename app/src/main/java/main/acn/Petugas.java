package main.acn;

import android.os.Parcel;
import android.os.Parcelable;

public class Petugas implements Parcelable {
    private String id,nama;
    private Account account;
    private Level level;

    public Petugas(){

    }

    public Petugas(String id,String nama,Level level,Account account){
        this.id = id;
        this.nama = nama;
        this.level = level;
        this.account = account;
    }

    public String getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public Level getLevel() {
        return level;
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
        dest.writeParcelable(this.account, flags);
        dest.writeParcelable(this.level, flags);
    }

    protected Petugas(Parcel in) {
        this.id = in.readString();
        this.nama = in.readString();
        this.account = in.readParcelable(Account.class.getClassLoader());
        this.level = in.readParcelable(Level.class.getClassLoader());
    }

    public static final Parcelable.Creator<Petugas> CREATOR = new Parcelable.Creator<Petugas>() {
        @Override
        public Petugas createFromParcel(Parcel source) {
            return new Petugas(source);
        }

        @Override
        public Petugas[] newArray(int size) {
            return new Petugas[size];
        }
    };
}
