package com.example.swapnil.youkids.pojo;

/**
 * Created by Swapnil.Patel on 23-10-2017.
 */

public class VideoLIst {

    int id;
    String vid;
    String date;
    String title;
    String video;
    String image;
    String download;
    String view;

    public VideoLIst() {
    }

    public VideoLIst(String vid, String date, String title, String video, String image, String download, String view) {
        this.vid = vid;
        this.date = date;
        this.title = title;
        this.video = video;
        this.image = image;
        this.download = download;
        this.view = view;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDownload() {
        return download;
    }

    public void setDownload(String download) {
        this.download = download;
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    @Override
    public String toString() {
        return "VideoLIst{" +
                "id=" + id +
                ", vid='" + vid + '\'' +
                ", date='" + date + '\'' +
                ", title='" + title + '\'' +
                ", video='" + video + '\'' +
                ", image='" + image + '\'' +
                ", download='" + download + '\'' +
                ", view='" + view + '\'' +
                '}';
    }
}
