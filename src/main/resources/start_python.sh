#!/bin/sh
screen -AmdS WeatherPI watch -n 10 python3 /home/pi/Arduino/Projektwoche/Master/update_database.py
