#! /bin/sh
make mrproper
cd availablelater
mvn clean compile install -Dmaven.test.skip=true
cd ../
cd fss-tests-base
mvn clean compile install
cd ../
cd fss
mvn clean compile install -Dmaven.test.skip=true
cd ../
make start
