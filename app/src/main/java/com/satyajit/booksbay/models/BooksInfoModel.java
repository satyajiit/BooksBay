package com.satyajit.booksbay.models;

public class BooksInfoModel {

    private String name, genre, uploader,  author, intro;

    private long time, download_count;

    long[] stars;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }




    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }


    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getGenre() {
       return genre;
    }


    public void setUploader(String uploader) {
        this.uploader = uploader;
    }

    public String getUploader() {
        return uploader;
    }



    public void setDownload_count(long download_count) {
        this.download_count = download_count;
    }

    public long getDownload_count() {
        return download_count;
    }


    public void setStars(long[] stars) {
        this.stars = stars;
    }




    public void setTime(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    public float[] getRating(){

            float x[] = new float[2];

            float n = 0f, d=0f;

            for(int i=0;i<5;i++){

                n = n+stars[i]*(i+1);
                d = d + stars[i];

            }

            x[0] = n/d;
            x[1] = d;

            return x;



    }

}