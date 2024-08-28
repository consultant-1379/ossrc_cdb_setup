#!/bin/sh
#set -xv
rpm_full_path=$1
rpm_name=`basename $rpm_full_path`
rpm_name=`echo $rpm_name | cut -d'.' -f1`

echo $rpm_name
#Checking whether the RPM installed already or not
check=`rpm -qa | grep -i $rpm_name`

if [ ! -z "$check" ]; then
 echo "Upgrade"
 rpm -Uvh $rpm_full_path
else
 echo "install"
 rpm -ivh $rpm_full_path
fi
 
#Chekcing new RPM version
#rpm -qi /vnfpackages/ERICwfmgrui_CXP9032371.rpm
