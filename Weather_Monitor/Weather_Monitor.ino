#include <SFE_BMP180.h>
#include <Wire.h>
#include "FirebaseESP8266.h"
#include <ESP8266WiFi.h>
#include "DHT.h"
#include <NTPClient.h>
#include <WiFiUdp.h>

DHT dht(D3, DHT11);
SFE_BMP180 bmp;
double T, P;
char status;
WiFiClient client;


#define FIREBASE_HOST "weathereye-a1f30-default-rtdb.firebaseio.com"
#define FIREBASE_AUTH "Jd522ABF66huUL1C3gVwbaCCTNRiDwdmEmwcOdm8"


String apiKey = "3NUSQ8VPT23VNPKE";
//const char *ssid =  "Tenda-001C92";
//const char *pass =  "89172731";
const char *ssid =  "S10+";
const char *pass =  "family44ever";
//const char *ssid =  "HUAWEI MatePad";
//const char *pass =  "DhiyaDanial";
const char* server = "api.thingspeak.com";

FirebaseData firebaseData;
FirebaseJson json;

const long utcOffsetInSeconds = 28800;
//----------------------------------------Define NTP Client to get time
WiFiUDP ntpUDP;
NTPClient timeClient(ntpUDP, "pool.ntp.org", utcOffsetInSeconds);

void setup() {
  Serial.begin(115200);
  delay(10);
  bmp.begin();
  Wire.begin();
  dht.begin();
  WiFi.begin(ssid, pass);


  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("");
  Serial.println("WiFi connected");
  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
}

void loop() {

  //Get time from the internet and format the display.
  timeClient.update();
   String hr, mn, sc;
   if (timeClient.getHours() < 10) {
      hr = "0" + String(timeClient.getHours());
    }
   else {
      hr = String(timeClient.getHours());
    }
    
   if (timeClient.getMinutes() < 10) {
      mn = "0" + String(timeClient.getMinutes());
    }
   else {
      mn = String(timeClient.getMinutes());
    }
  
   if (timeClient.getSeconds() < 10) {
      sc = "0" + String(timeClient.getSeconds());
    }
   else {
      sc = String(timeClient.getSeconds());
    }
    
  String TimeNow = hr + ":" + mn + ":" + sc;

  //Get Date from internet
  unsigned long epochTime = timeClient.getEpochTime();
  struct tm *ptm = gmtime ((time_t *)&epochTime);

//  int monthDay = ptm->tm_mday+1;
//  int currentMonth = ptm->tm_mon-1;
//  int currentYear = ptm->tm_year+1900;

  //Temporary Fix for Date
  int monthDay = ptm->tm_mday-4;
  int currentMonth = ptm->tm_mon+7;
  int currentYear = ptm->tm_year-5988;
  String currentDate = String(monthDay) + "-" + String(currentMonth) + "-" + String(currentYear);
  
  //BMP180 sensor
  status =  bmp.startTemperature();
  if (status != 0) {
    delay(status);
    status = bmp.getTemperature(T);

    status = bmp.startPressure(3);// 0 to 3
    if (status != 0) {
      delay(status);
      status = bmp.getPressure(P, T);
      if (status != 0) {

      }
    }
  }

  //DHT11 sensor
  float h = dht.readHumidity();
  float t = dht.readTemperature();

  if (isnan(h) || isnan(t)) {
    Serial.println("Failed to read from DHT sensor!");
    return;
  }

  //Rain sensor
  int r = analogRead(A0);
  r = map(r, 0, 1024, 0, 100);


  if (client.connect(server, 80)) {
    String postStr = apiKey;
    postStr += "&field1=";
    postStr += String(t);
    postStr += "&field2=";
    postStr += String(h);
    postStr += "&field3=";
    postStr += String(P, 2);
    postStr += "&field4=";
    postStr += String(r);
    postStr += "&field5=";
    postStr += String("NULL");
    postStr += "\r\n\r\n\r\n\r\n";

    client.print("POST /update HTTP/1.1\n");
    client.print("Host: api.thingspeak.com\n");
    client.print("Connection: close\n");
    client.print("X-THINGSPEAKAPIKEY: " + apiKey + "\n");
    client.print("Content-Type: application/x-www-form-urlencoded\n");
    client.print("Content-Length: ");
    client.print(postStr.length());
    client.print("\n\n\n\n");
    client.print(postStr);

    Serial.print("Temperature: ");
    Serial.println(t);
    Serial.print("Humidity: ");
    Serial.println(h);
    Serial.print("absolute pressure: ");
    Serial.print(P, 2);
    Serial.println("mb");
    Serial.print("Rain: ");
    Serial.println(r);

    //Weather History in Firebase

    //Add Time and Date variable
//    if (Firebase.setString(firebaseData, "/WeatherEye/History/Date", currentDate + "/Time", TimeNow))
//    {
//      Serial.println("SetDate PASSED");
//      Serial.println("PATH: " + firebaseData.dataPath());
//      Serial.println("TYPE: " + firebaseData.dataType());
//      Serial.println("ETag: " + firebaseData.ETag());
//      Serial.println("------------------------------------");
//      Serial.println();
//    }
//    else
//    {
//      Serial.println("SetDate FAILED");
//      Serial.println("REASON: " + firebaseData.errorReason());
//      Serial.println("------------------------------------");
//      Serial.println();
//    }

//    Serial.println("Date: " + currentDate);
//    
//    if (Firebase.setFloat(firebaseData, "/WeatherEye/History/" + currentDate + "/" + TimeNow + "/Temperature", t))
//    {
//      Serial.println("Temperature PASSED");
//      Serial.println("PATH: " + firebaseData.dataPath());
//      Serial.println("TYPE: " + firebaseData.dataType());
//      Serial.println("ETag: " + firebaseData.ETag());
//      Serial.println("------------------------------------");
//      Serial.println();
//    }
//    else
//    {
//      Serial.println("Temperature FAILED");
//      Serial.println("REASON: " + firebaseData.errorReason());
//      Serial.println("------------------------------------");
//      Serial.println();
//    }
//
//    if (Firebase.setFloat(firebaseData, "/WeatherEye/History/" + currentDate + "/" + TimeNow + "/Humidity", h))
//    {
//      Serial.println("Humidity PASSED");
//      Serial.println("PATH: " + firebaseData.dataPath());
//      Serial.println("TYPE: " + firebaseData.dataType());
//      Serial.println("ETag: " + firebaseData.ETag());
//      Serial.println("------------------------------------");
//      Serial.println();
//    }
//    else
//    {
//      Serial.println("Humidity FAILED");
//      Serial.println("REASON: " + firebaseData.errorReason());
//      Serial.println("------------------------------------");
//      Serial.println();
//    }
//    
//    if (Firebase.setFloat(firebaseData, "/WeatherEye/History/" + currentDate + "/" + TimeNow + "/Raindrop", r))
//    {
//      Serial.println("Raindrop PASSED");
//      Serial.println("PATH: " + firebaseData.dataPath());
//      Serial.println("TYPE: " + firebaseData.dataType());
//      Serial.println("ETag: " + firebaseData.ETag());
//      Serial.println("------------------------------------");
//      Serial.println();
//    }
//    else
//    {
//      Serial.println("Raindrop FAILED");
//      Serial.println("REASON: " + firebaseData.errorReason());
//      Serial.println("------------------------------------");
//      Serial.println();
//    }
//    
//    if (Firebase.setString(firebaseData, "/WeatherEye/History/" + currentDate + "/" + TimeNow + "/Time", TimeNow))
//    {
//      Serial.println("Time PASSED");
//      Serial.println("PATH: " + firebaseData.dataPath());
//      Serial.println("TYPE: " + firebaseData.dataType());
//      Serial.println("ETag: " + firebaseData.ETag());
//      Serial.println("------------------------------------");
//      Serial.println();
//    }
//    else
//    {
//      Serial.println("Time FAILED");
//      Serial.println("REASON: " + firebaseData.errorReason());
//      Serial.println("------------------------------------");
//      Serial.println();
//    }
//
//    if (Firebase.setString(firebaseData, "/WeatherEye/History/" + currentDate + "/" + TimeNow + "/Date", currentDate))
//    {
//      Serial.println("Date PASSED");
//      Serial.println("PATH: " + firebaseData.dataPath());
//      Serial.println("TYPE: " + firebaseData.dataType());
//      Serial.println("ETag: " + firebaseData.ETag());
//      Serial.println("------------------------------------");
//      Serial.println();
//    }
//    else
//    {
//      Serial.println("Date FAILED");
//      Serial.println("REASON: " + firebaseData.errorReason());
//      Serial.println("------------------------------------");
//      Serial.println();
//    }


    //Weather Live Data
    if (Firebase.setFloat(firebaseData, "/Live Monitoring/Temperature", t))
    {
      Serial.println("Live Temp PASSED");
      Serial.println("PATH: " + firebaseData.dataPath());
      Serial.println("TYPE: " + firebaseData.dataType());
      Serial.println("ETag: " + firebaseData.ETag());
      Serial.println("------------------------------------");
      Serial.println();
    }
    else
    {
      Serial.println("Live Temp FAILED");
      Serial.println("REASON: " + firebaseData.errorReason());
      Serial.println("------------------------------------");
      Serial.println();
    }

    if (Firebase.setFloat(firebaseData, "/Live Monitoring/Humidity", h))
    {
      Serial.println("Live Humid PASSED");
      Serial.println("PATH: " + firebaseData.dataPath());
      Serial.println("TYPE: " + firebaseData.dataType());
      Serial.println("ETag: " + firebaseData.ETag());
      Serial.println("------------------------------------");
      Serial.println();
    }
    else
    {
      Serial.println("Live Humid FAILED");
      Serial.println("REASON: " + firebaseData.errorReason());
      Serial.println("------------------------------------");
      Serial.println();
    }
    
    if (Firebase.setFloat(firebaseData, "/Live Monitoring/Raindrop", r))
    {
      Serial.println("Live Rain PASSED");
      Serial.println("PATH: " + firebaseData.dataPath());
      Serial.println("TYPE: " + firebaseData.dataType());
      Serial.println("ETag: " + firebaseData.ETag());
      Serial.println("------------------------------------");
      Serial.println();
    }
    else
    {
      Serial.println("Live Rain FAILED");
      Serial.println("REASON: " + firebaseData.errorReason());
      Serial.println("------------------------------------");
      Serial.println();
    }
    
    if (Firebase.setString(firebaseData, "/Live Monitoring/Time", TimeNow))
    {
      Serial.println("Live Time PASSED");
      Serial.println("PATH: " + firebaseData.dataPath());
      Serial.println("TYPE: " + firebaseData.dataType());
      Serial.println("ETag: " + firebaseData.ETag());
      Serial.println("------------------------------------");
      Serial.println();
    }
    else
    {
      Serial.println("Live Time FAILED");
      Serial.println("REASON: " + firebaseData.errorReason());
      Serial.println("------------------------------------");
      Serial.println();
    }

  }
  client.stop();
  delay(300000);
}
