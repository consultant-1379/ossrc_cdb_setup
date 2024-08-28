#!/bin/bash
#set -xv
machine="https://cifwk-oss.lmera.ericsson.se/"

#Usage function showing how to use the script
usage ()
{
  echo "Usage: $cmd [-D for Debug] [-a GAVs_success] [-P product][-i intendedDrop] [-t packageType] [-u user] [-e email] " 1>&2
  printf " -D To run the script in DEBUG mode \n"
  printf " -g GroupId of the poackage eg: com.ericsson.oss.common \n"
  printf " -a Artifactlist in GAVs format : ERICsimnetWRAN_CXP9027449\n"
  printf " -r version of the package eg : R1A03\n"
  printf " -P product eg: Netsim/Simnet\n"
  printf " -i Intended Drop eg: 16.0.1\n"
  printf " -t packageType or type of the file eg: zip\n"
  printf " -u signum ID of the user eg: xjagset\n"
  printf " -e email ID of the user eg: jagadish.sethi@wipro.com\n"
  exit 1
}

# Parse arguments

[ $# -gt 0 ] || usage
while getopts ":a:P:i:t:u:e:D" opt; do
  case $opt in
    a)
      GAVs_success=$OPTARG
      ;;
    P)
      product=$OPTARG
      ;;
    i)
      intendedDrop=$OPTARG
      ;;
    t)
      packageM2Type=$OPTARG
      ;;
    u)
      user=$OPTARG
      ;;
    e)
      email=$OPTARG
      ;;
    D)
      echo "Script is running in DEBUG mode."
      set -xv
      ;;
    \?)
      echo "Invalid option: -$OPTARG" >&2
      usage
      exit 1
      ;;
    :)
      echo "Option -$OPTARG requires an argument." >&2
      usage
      exit 1
      ;;
  esac
done



#GAVs_success="#ERICfmav_CXC1722406::29.2.1::com.ericsson.oss.i386#ERICfmaa_CXC1722416::29.2.1::com.ericsson.oss.i386#ERICfmcor_CXC1722402::29.1.2::com.ericsson.oss.i386"


set -- "$GAVs_success"
IFS="#"; declare -a Array=($*)

for(( i = 1 ; i < ${#Array[@]} ; i++ ))
do

gav=${Array[i]}
echo $gav
set -- "$gav"
IFS="::"; declare -a data=($*)

echo "< ${data[@]}"
artifactId=${data[0]}
rstate=${data[2]}
groupId=${data[4]}

echo "artifactId $artifactId"
echo "rstate $rstate"
echo "groupId $groupId"


/usr/bin/wget -q -O - --no-check-certificate --post-data="packageName=${artifactId}&version=${rstate}&drop=${intendedDrop}&product=${product}&type=${packageM2Type}&email=${email}" https://$machine/deliverToDrop/

  if [ $? -ne 0 ]; then
    echo "Updation in deliver to drop failed for ${artifactId}"
    exit 1;
  fi
  echo "Updation in deliver to drop passed for ${artifactId}"

done


