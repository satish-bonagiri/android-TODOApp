package com.satti.android.todo.model;


import android.os.Parcel;
import android.os.Parcelable;

public class Task implements Parcelable{

    private long id;
    private String name;
    private int priority;
    private long dateTimeStamp;

    public Task() {
    }

    public Task(String name, int priority, long dateTimeStamp) {
        this.name = name;
        this.priority = priority;
        this.dateTimeStamp = dateTimeStamp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public long getDateTimeStamp() {
        return dateTimeStamp;
    }

    public void setDateTimeStamp(long dateTimeStamp) {
        this.dateTimeStamp = dateTimeStamp;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    private Task(Parcel in){
        this.id = in.readLong();
        this.name = in.readString();
        this.priority = in.readInt();
        this.dateTimeStamp = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeInt(priority);
        dest.writeLong(dateTimeStamp);

    }

    public static final Parcelable.Creator<Task> CREATOR = new Parcelable.Creator<Task>() {

        @Override
        public Task createFromParcel(Parcel source) {
            return new Task(source);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

//    @Override
//    public String toString() {
//        return "Task{" +
//                "id=" + id +
//                ", name='" + name + '\'' +
//                ", priority=" + priority +
//                ", dateTimeStamp=" + dateTimeStamp +
//                '}';
//    }
}
