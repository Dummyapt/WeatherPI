#include <RF24Network.h>
#include <RF24.h>
#include <SPI.h>

RF24 radio(7, 8);
RF24Network network(radio);
const uint16_t this_node = 00;
//const uint16_t node01 = 01;
//const uint16_t node02 = 02;
//const uint16_t node03 = 03;
//const uint16_t node04 = 04;
//const uint16_t node05 = 05;

struct Payload {
  int id;
  float temperature;
  float humidity;
};

void setup() {
  Serial.begin(115200);
  SPI.begin();
  radio.begin();
  radio.setDataRate(RF24_2MBPS);
  network.begin(90, this_node);
}

void loop() {
  network.update();
  while (network.available()) {
    RF24NetworkHeader header;
    Payload payload;
    network.read(header, &payload, sizeof(payload));
    Serial.println(payload.id);
    Serial.println(payload.temperature);
    Serial.println(payload.humidity);
  }
}
