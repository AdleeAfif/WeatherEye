package com.example.WeatherEye;

public class ForecastList {

    private String fTime;
    private String fTemp;
    private String fHumid;
    private String fRain;

    public ForecastList(String fTime, String fRain) {
        this.fTime = fTime;
        this.fRain = fRain;
    }

    public String getfTime() {
        return fTime;
    }

    public void setfTime(String fTime) {
        this.fTime = fTime;
    }

    public String getfRain() {
        return fRain;
    }

    public void setfRain(String fRain) {
        this.fRain = fRain;
    }
}
