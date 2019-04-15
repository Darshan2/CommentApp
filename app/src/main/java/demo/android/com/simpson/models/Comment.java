package demo.android.com.simpson.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Comment implements Parcelable {
     String comment_id;
     String comment_body;
     String user_id;
     String parent_id;
     String thread_id;
     String num_replies;
     String time;

    public Comment() {
    }

    public Comment(String comment_id, String comment_body, String user_id, String parent_id, String thread_id, String num_replies) {
        this.comment_id = comment_id;
        this.comment_body = comment_body;
        this.user_id = user_id;
        this.parent_id = parent_id;
        this.thread_id = thread_id;
        this.num_replies = num_replies;
    }

    protected Comment(Parcel in) {
        comment_id = in.readString();
        comment_body = in.readString();
        user_id = in.readString();
        parent_id = in.readString();
        thread_id = in.readString();
        num_replies = in.readString();
    }

    public static final Creator<Comment> CREATOR = new Creator<Comment>() {
        @Override
        public Comment createFromParcel(Parcel in) {
            return new Comment(in);
        }

        @Override
        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }

    public String getComment_body() {
        return comment_body;
    }

    public void setComment_body(String comment_body) {
        this.comment_body = comment_body;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public String getThread_id() {
        return thread_id;
    }

    public void setThread_id(String thread_id) {
        this.thread_id = thread_id;
    }

    public String getNum_replies() {
        return num_replies;
    }

    public void setNum_replies(String num_replies) {
        this.num_replies = num_replies;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "comment_id='" + comment_id + '\'' +
                ", comment_body='" + comment_body + '\'' +
                ", user_id='" + user_id + '\'' +
                ", parent_id='" + parent_id + '\'' +
                ", thread_id='" + thread_id + '\'' +
                ", num_replies='" + num_replies + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(comment_id);
        dest.writeString(comment_body);
        dest.writeString(user_id);
        dest.writeString(parent_id);
        dest.writeString(thread_id);
        dest.writeString(num_replies);
    }
}
