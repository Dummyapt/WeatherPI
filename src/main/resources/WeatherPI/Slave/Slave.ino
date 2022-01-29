#include <RF24Network.h>
#include <RF24.h>
#include <SPI.h>
#include <DHT.h>

const int id = 1;
const uint16_t this_node = 01;
const uint16_t other_node = 00;

DHT dht(A0, DHT22);
RF24 radio(7, 8);
RF24Network network(radio);

const unsigned long interval = 2000;

unsigned long last_sent;
unsigned long packets_sent;

struct Payload {
  int id;
  float temperature;
  float humidity;
};

void setup() {
  dht.begin();
  SPI.begin();
  radio.begin();
  radio.setDataRate(RF24_2MBPS);
  network.begin(90, this_node);
}

void loop() {
  network.update();
  unsigned long now = millis();
  if (now - last_sent >= interval) {
    last_sent = now;
    Payload payload = {id, dht.readTemperature(), dht.readHumidity()};
    RF24NetworkHeader header(other_node);
    network.write(header, &payload, sizeof(payload));
  }
}
