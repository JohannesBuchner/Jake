#! /bin/sh
make mrproper
cd availablelater
mvn clean compile install -Dmaven.test.skip=true
cd ../
make fss start
