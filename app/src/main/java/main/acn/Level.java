package main.acn;

import android.os.Parcel;
import android.os.Parcelable;

public class Level implements Parcelable {
    private String id,nama;

    public Level(){}

    public Level(String id,String nama){
        this.id = id;
        this.nama = nama;
    }

    public String getNama() {
        return nama;
    }

    public String getId() {
        return id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.nama);
    }

    protected Level(Parcel in) {
        this.id = in.readString();
        this.nama = in.readString();
    }

    public static final Parcelable.Creator<Level> CREATOR = new Parcelable.Creator<Level>() {
        @Override
        public Level createFromParcel(Parcel source) {
            return new Level(source);
        }

        @Override
        public Level[] newArray(int size) {
            return new Level[size];
        }
    };
}
