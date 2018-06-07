#include <MRF24G.h>
#include <DEIPcK.h>
#include <DEWFcK.h>
#include <dht11.h>

const char * szIPServer = "115.29.109.104";
unsigned short portServer = 6516;
const char * szSsid = "TP-LINK_311";

#define USE_WPA2_PASSPHRASE

#if defined(USE_WPA2_PASSPHRASE)
const char * szPassPhrase = "s311s311";                                          //your SSID PASSWORD
#define WiFiConnectMacro() deIPcK.wfConnect(szSsid, szPassPhrase)
#endif

typedef enum
{
  NONE = 0,
  CONNECT,
  TCPCONNECT,
  WRITE,
  READ,
  CLOSE,
  DONE,
} STATE;

STATE state = CONNECT;
IPSTATUS    status;
TCPSocket tcpClient;
dht11   DHT11;
int cbRead = 0;
int v = 0;
int a;
int d;
String comedata = "";
String rec = "";
String pake = "buct#";
String on = "1";
String off = "0";
String esc = "buct#shutdown";
float temp, humi;
boolean DHT11OK = false;
boolean mq_2 = false;
unsigned long t0 = 0, t1 = 0, t2 = 0, t3 = 0;

void setup() {
  // put your setup code here, to run once:
  pinMode(8, INPUT);//mq-2 D0
  pinMode(9, INPUT);//mq-2 A0
  pinMode(10, OUTPUT);//pwm1
  pinMode(11, OUTPUT);//pwm2
  pinMode(12, OUTPUT);//pwm1 GND
  pinMode(13, OUTPUT);//pwm2 GND
  pinMode(26, OUTPUT);//dht11 VCC
  pinMode(27, INPUT);//dht11 DATA
  pinMode(28, OUTPUT);//dht11 GND
  pinMode(32, OUTPUT);//蜂鸣器GND
  pinMode(33, OUTPUT);//蜂鸣器VCC
  pinMode(34, OUTPUT);//led1 en
  pinMode(35, OUTPUT);//led2 en
  pinMode(36, OUTPUT);//led3 en
  pinMode(37, OUTPUT);//led4 en
  pinMode(38, OUTPUT);//led1 GND
  pinMode(39, OUTPUT);//led2 GND
  pinMode(40, OUTPUT);//led3 GND
  pinMode(41, OUTPUT);//led4 GND
  pinMode(43, OUTPUT);//system  status led1
  pinMode(44, OUTPUT);//system  status led2
  pinMode(45, OUTPUT);//system  status led3

  digitalWrite(12, LOW);
  digitalWrite(13, LOW);
  digitalWrite(26, HIGH);
  digitalWrite(28, LOW);
  digitalWrite(32, LOW);
  digitalWrite(38, LOW);
  digitalWrite(39, LOW);
  digitalWrite(40, LOW);
  digitalWrite(41, LOW);

  Serial.begin(9600);
  deIPcK.begin();
}

void loop() {
  // put your main code here, to run repeatedly:
  switch (state)
  {
    case CONNECT:
      if (WiFiConnectMacro()) {
        Serial.println("Connected to wifi");
        digitalWrite(43, HIGH);
        state = TCPCONNECT;
      }
      break;
    case TCPCONNECT:
      if (deIPcK.tcpConnect(szIPServer, portServer, tcpClient, &status))
      {
        Serial.println("Connect to server");
        digitalWrite(44, HIGH);
        digitalWrite(33, HIGH);
        delay(2000);
        digitalWrite(33, LOW);
        state = WRITE;
      }
      break;
    case WRITE:
      if (tcpClient.isEstablished())
      {
        getDHT11();
        if (DHT11OK) {
          tcpClient.println("buct#" + (String)temp + "#" + (String)humi + "#" + "1#");
          Serial.println("buct#" + (String)temp + "#" + (String)humi + "#" + "1#");
          digitalWrite(45, HIGH);
          Serial.println(d);
          state = READ;
        }
      }
      break;
    case READ:
      while ((cbRead = tcpClient.available()) > 0)
      {
        rec += char(tcpClient.read());
      }
      if ((rec.length() != 0) && (pake.equals(rec.substring(0, 5))))
      {
        Serial.println(rec);
        v = rec.substring(9, 11).toInt();
        v = (255 * v) / 100;
        Serial.println(v);
        analogWrite(10, v);
        analogWrite(11, v);
        if (on.equals(rec.substring(5, 6)))
        {
          digitalWrite(36, HIGH);
        }
        if (off.equals(rec.substring(5, 6)))
        {
          digitalWrite(36, LOW);
        }
        if (on.equals(rec.substring(6, 7)))
        {
          digitalWrite(37, HIGH);
        }
        if (off.equals(rec.substring(6, 7)))
        {
          digitalWrite(37, LOW);
        }
        if (on.equals(rec.substring(7, 8)))
        {
          digitalWrite(34, HIGH);
        }
        if (off.equals(rec.substring(7, 8)))
        {
          digitalWrite(34, LOW);
        }
        if (on.equals(rec.substring(8, 9)))
        {
          digitalWrite(35, HIGH);
        }
        if (off.equals(rec.substring(8, 9)))
        {
          digitalWrite(35, LOW);
        }
        if (esc.equals(rec))
        {
          Serial.println("Shutdown");
          digitalWrite(44, LOW);
          digitalWrite(45, LOW);
          state = CLOSE;
        }
        rec = "";
      }
      t1 = millis();
      if ((t1 - t0) >= 60000) {
        t0 = t1;
        state = WRITE;
      }
      getMq_2();
      if (d == 1) {
        mq_2 = false;
      }
      if (d == 0) {
        mq_2 = true;
      }
      if (!mq_2) {
        t2 = millis();
      }
      if (mq_2) {
        t3 = millis();
        if ((t3 - t2) >= 10000) {
          if(tcpClient.isEstablished()){
            getDHT11();
            if (DHT11OK) {
              tcpClient.println("buct#" + (String)temp + "#" + (String)humi + "#" + "0#");
              Serial.println("buct#" + (String)temp + "#" + (String)humi + "#" + "0#");
            }
          }
          while (mq_2) {
            getMq_2();
            if (d == 1) {
              mq_2 = false;
            }
            if (d == 0) {
              mq_2 = true;
            }
            beep();
          }
        }
      }
      break;
    case CLOSE:
      tcpClient.close();
      Serial.println("Logout,Closing TcpClient");
      state = DONE;
      break;
    case DONE:
    default:
      break;
  }
  DEIPcK::periodicTasks();
  while (Serial.available() > 0) {
    comedata += char(Serial.read());
    delay(2);
  }
  if (comedata.length() != 0)
  {
    Serial.println(comedata);
    comedata = "";
  }
}
void getDHT11() {
  uint8_t count = 5;
  Serial.print("Read DHT11 Sensor: ");
  while ((DHT11.read(27) != DHTLIB_OK) && (count--)) {
    delay(250);
  }
  if (!count) {
    DHT11OK = false;
    Serial.println("Sensor was not detected!");
  }
  else {
    Serial.println("Sensor Read OK!");
  }
  temp = (float)DHT11.temperature;
  humi = (float)DHT11.humidity;
  if (checkfloatequalzero(temp) && checkfloatequalzero(humi)) {
    DHT11OK = false;
  }
  else {
    DHT11OK = true;
  }
}
boolean checkfloatequalzero(float x)
{
  if ((x < 0.000001f) && (x > -0.000001f))
    return true;
  else return false;
}

void getMq_2()
{
  a = analogRead(9);
  d = digitalRead(8);
}

void beep()
{
  digitalWrite(33, HIGH);
  delay(333);
  digitalWrite(33, LOW);
  delay(333);
  digitalWrite(33, HIGH);
  delay(333);
  digitalWrite(33, LOW);
  delay(1000);
}

