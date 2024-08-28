#!/bin/sh


wget -O $4/$1.rpm "http://cifwk-oss.lmera.ericsson.se:8081/nexus/service/local/artifact/maven/redirect?r=releases&g=$3&a=$1&v=$2&e=rpm"
