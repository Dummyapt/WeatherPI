import mariadb
import sys
import serial

try:
    arduino = serial.Serial('/dev/ttyACM0', 115200, timeout=5)
except:
    print('Please check the port')

try:
    conn = mariadb.connect(
        user="weatherpi",
        password="G1M1RU",
        host="::1",
        port=3306,
        database="weatherpi"
    )
except mariadb.Error as e:
    print(f"Error connecting to MariaDB Platform: {e}")
    sys.exit(1)

cur = conn.cursor()
rawdata = []
count = 0

while count < 3:
    rawdata.append(str(arduino.readline()))
    count += 1


def clean(L):
    newl = []
    for i in range(len(L)):
        temp = L[i][2:]
        newl.append(temp[:-5])
    return newl


cleandata = clean(rawdata)


def write(L):
    file = open('data.txt', 'w')
    for i in range(len(L)):
        file.write(L[i] + '\n')
    file.close()


def write_to_database(arduino, temp, humi):
    cur.execute(f'UPDATE monitoring SET temperature = {temp}, humidity = {humi} WHERE arduino = {arduino}')
    conn.commit()
    conn.close()
    return

write_to_database(int(cleandata[0]), float(cleandata[1]), float(cleandata[2]))
