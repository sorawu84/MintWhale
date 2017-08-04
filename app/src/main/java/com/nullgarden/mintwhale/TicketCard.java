package com.nullgarden.mintwhale;

/**
 * Created by sora_wu on 25/07/2017.
 */

public class TicketCard {

        private String Title,Content,Images,Poster, Date, Time, Name;

        public TicketCard(){

        }

    public TicketCard(String title, String content, String images, String poster, String time, String date, String name) {
        Title = title;
        Content = content;
        Images = images;
        Poster = poster;
        Date = date;
        Time = time;
        Name = name;
    }

    public String getName(){ return Name; }

    public void setName(String name){ Name = name; }

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

    public String getImages() {
        return Images;
    }

    public void setImages(String images) {
        Images = images;
    }

    public String getPoster() {
        return Poster;
    }

    public void setPoster(String poster) {
        Poster = poster;
    }

    public String getDate() { return Date;}

    public void setDate(String date) { Date = date;}

    public String getTime() { return Time;}

    public void setTime(String time) { Time = time;}
}
