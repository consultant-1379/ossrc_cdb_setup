#!/bin/sh
#set -xv
echo '0,5,10,15,20,25,30,35,40,45,50,55 * * * * /bin/date >> /var/opt/ericsson/log/CPUUsage.txt 2>&1;echo "==========================">> /var/opt/ericsson/log/CPUUsage.txt 2>&1;/bin/ps -eo pcpu,pid,user,pmem,vsz,rss,args | sort -k 1 -r | head -100 >> /var/opt/ericsson/log/CPUUsage.txt 2>&1' | tee -a /var/spool/cron/crontabs/root
