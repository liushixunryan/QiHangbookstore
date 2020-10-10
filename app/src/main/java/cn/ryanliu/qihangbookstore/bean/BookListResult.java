package cn.ryanliu.qihangbookstore.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BookListResult {
    //序列化重命名
    @SerializedName("status")
    public int mStatus;
    @SerializedName("msg")
    public String mMessage;

    public int getStatus() {
        return mStatus;
    }

    public void setStatus(int status) {
        mStatus = status;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    public List<BookData> getData() {
        return mData;
    }

    public void setData(List<BookData> data) {
        mData = data;
    }

    @SerializedName("data")
    public List<BookData> mData;

    public class BookData {
        String bookname;
        String bookfile;

        public String getBookname() {
            return bookname;
        }

        public void setBookname(String bookname) {
            this.bookname = bookname;
        }

        public String getBookfile() {
            return bookfile;
        }

        public void setBookfile(String bookfile) {
            this.bookfile = bookfile;
        }
    }


}
