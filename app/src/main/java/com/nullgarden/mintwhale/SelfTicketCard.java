package com.nullgarden.mintwhale;

/**
 * Created by sora_wu on 27/07/2017.
 */

public class SelfTicketCard {

    private String Time, Date, Images, Title,Content;

    public SelfTicketCard(){

    }

    public SelfTicketCard(String time, String date, String images, String title, String content) {
        Time = time;
        Date = date;
        Images = images;
        Title = title;
        Content = content;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getImages() {
        return Images;
    }

    public void setImages(String images) {
        Images = images;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }
}
