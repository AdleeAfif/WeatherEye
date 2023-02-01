package com.example.WeatherEye;

public class HistoryList {
    private String Time;
    private String Date;
    private String Temperature;
    private String Humidity;
    private String Raindrop;
    private String ID;

    public HistoryList(String ID, String Time, String Date, String Temperature, String Humidity, String Raindrop) {
        this.Time = Time;
        this.Date = Date;
        this.Temperature = Temperature;
        this.Humidity = Humidity;
        this.Raindrop = Raindrop;
        this.ID = ID;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getTemperature() {
        return Temperature;
    }

    public void setTemperature(String Temperature) {
        this.Temperature = Temperature;
    }

    public String getHumidity() {
        return Humidity;
    }

    public void setHumidity(String Humidity) {
        this.Humidity = Humidity;
    }

    public String getRaindrop() {
        return Raindrop;
    }

    public void setRaindrop(String Raindrop) {
        this.Raindrop = Raindrop;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        this.Time = time;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String Date) {
        this.Date = Date;
    }

    public String toString(){
        return this.Time + " " + this.Date + "\n" + this.Temperature + " " + this.Humidity + " " + this.Raindrop;
    }
}
