#!/bin/sh


wget -O $4/$1.pkg "http://eselivm2v214l.lmera.ericsson.se:8081/nexus/service/local/artifact/maven/redirect?r=releases&g=$3&a=$1&v=$2&e=pkg"
scp $4/$1.pkg root@192.168.0.5:$4

