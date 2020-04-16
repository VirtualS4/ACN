package main.acn;

import android.os.Parcel;
import android.os.Parcelable;

public class History implements Parcelable {
    private String id,id_lelang;
    private Masyarakat bidder;
    private int penawaran;

    public History(){

    }

    public History(String id,String id_lelang,int penawaran,Masyarakat bidder){
        this.id = id;
        this.id_lelang = id_lelang;
        this.penawaran = penawaran;
        Account acc = new Account("Kosong","Kosong");
        this.bidder = new Masyarakat(bidder.getId(),bidder.getNama(),"Kosong",acc);

    }

    public String getId() {
        return id;
    }

    public String getId_lelang() {
        return id_lelang;
    }

    public Masyarakat getBidder() {
        return bidder;
    }

    public int getPenawaran() {
        return penawaran;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.id_lelang);
        dest.writeParcelable(this.bidder, flags);
        dest.writeInt(this.penawaran);
    }

    protected History(Parcel in) {
        this.id = in.readString();
        this.id_lelang = in.readString();
        this.bidder = in.readParcelable(Masyarakat.class.getClassLoader());
        this.penawaran = in.readInt();
    }

    public static final Parcelable.Creator<History> CREATOR = new Parcelable.Creator<History>() {
        @Override
        public History createFromParcel(Parcel source) {
            return new History(source);
        }

        @Override
        public History[] newArray(int size) {
            return new History[size];
        }
    };
}
