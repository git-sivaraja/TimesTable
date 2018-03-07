package com.aathis.timestable;

import android.os.Parcelable;



import java.util.Date;

/**
 * Created by SivarajKumar on 01/03/2018.
 */

public class Player implements Parcelable{

    private int id;
    private String name;
    private Date datesPlayed;
    private int table;
    private int completionScale;
    private long timeTakenInMillis;

    protected Player(android.os.Parcel in) {
        id = in.readInt();
        name = in.readString();
        table = in.readInt();
        completionScale = in.readInt();
        timeTakenInMillis = in.readLong();
        long tmpDate = in.readLong();
        this.datesPlayed = tmpDate == -1 ? null : new Date(tmpDate);
    }

    public Player() {

    }

    @Override
    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeInt(table);
        dest.writeInt(completionScale);
        dest.writeLong(timeTakenInMillis);
        dest.writeLong(datesPlayed != null ? datesPlayed.getTime() : -1);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Player> CREATOR = new Creator<Player>() {
        @Override
        public Player createFromParcel(android.os.Parcel in) {
            return new Player(in);
        }

        @Override
        public Player[] newArray(int size) {
            return new Player[size];
        }
    };

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDatesPlayed() {
        return datesPlayed;
    }

    public void setDatesPlayed(Date datesPlayed) {
        this.datesPlayed = datesPlayed;
    }

    public int getTable() {
        return table;
    }

    public void setTable(int table) {
        this.table = table;
    }

    public int getCompletionScale() {
        return completionScale;
    }

    public void setCompletionScale(int completionScale) {
        this.completionScale = completionScale;
    }

    public long getTimeTakenInMillis() {
        return timeTakenInMillis;
    }

    public void setTimeTakenInMillis(long timeTakenInMillis) {
        this.timeTakenInMillis = timeTakenInMillis;
    }

    public void setId(int id) {
        this.id = id;

    }



}
