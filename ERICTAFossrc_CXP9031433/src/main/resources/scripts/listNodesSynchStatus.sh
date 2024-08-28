#!/bin/bash
i=-1
j=0
NAME=()
NODEVERSION=""
var="$(/opt/ericsson/nms_cif_cs/etc/unsupported/bin/cstest -s Seg_masterservice_CS lt MeContext -f '$.mirrorMIBsynchStatus==5 OR $.mirrorMIBsynchStatus==3' -an userLabel | while read line
	do
		i=$(($(($i+1))%2))
        if [ "${i}" -eq "1" ]; then
			j=$(($j+1))
            NAME[$j]=${line##*:} 
			NAME[$j]="`echo "${NAME[$j]}" | sed 's/"//g'`"
			echo "${NAME[j]}"
        fi
	
	done)"
unsynchvar="$(/opt/ericsson/nms_cif_cs/etc/unsupported/bin/cstest -s Seg_masterservice_CS lt MeContext -f '$.mirrorMIBsynchStatus==1 OR $.mirrorMIBsynchStatus==2 OR $.mirrorMIBsynchStatus==4' -an userLabel | while read line
	do
		i=$(($(($i+1))%2))
        if [ "${i}" -eq "1" ]; then
			j=$(($j+1))
            NAME[$j]=${line##*:} 
			NAME[$j]="`echo "${NAME[$j]}" | sed 's/"//g'`"
			echo "${NAME[j]}"
        fi
	
	done)"


printf  "%-15s %-15s %-15s \n" "Node" "nodeVersion" "Synch status"
i=-1
NODENAME=""
NODEVERSION=""
/opt/ericsson/nms_cif_cs/etc/unsupported/bin/cstest -s ONRM_CS lt ManagedElement -an userLabel nodeVersion | while read line
do
i=$(($(($i+1))%3))
        if [ "${i}" -eq "1" ]; then
            NODENAME=${line##*:}
			NODENAME[$j]="`echo "${NODENAME[$j]}" | sed 's/"//g'`"
        fi
        if [ "${i}" -eq "2" ]; then
	        NODEVERSION=${line##*:}
			NODEVERSION[$j]="`echo "${NODEVERSION[$j]}" | sed 's/"//g'`"
			for element in ${var[@]}
			do
				if [ ${element} == ${NODENAME} ]; then
					printf "%-15s %-15s %-15s \n" "$NODENAME" "$NODEVERSION" "SYNCHED"
				fi
				
			done
			
        fi
done

unsynchvar="$(/opt/ericsson/nms_cif_cs/etc/unsupported/bin/cstest -s Seg_masterservice_CS lt MeContext -f '$.mirrorMIBsynchStatus==1 OR $.mirrorMIBsynchStatus==2 OR $.mirrorMIBsynchStatus==4' -an userLabel | while read line
	do
		i=$(($(($i+1))%2))
        if [ "${i}" -eq "1" ]; then
			j=$(($j+1))
            NAME[$j]=${line##*:} 
			NAME[$j]="`echo "${NAME[$j]}" | sed 's/"//g'`"
			echo "${NAME[j]}"
        fi
	
	done)"

i=-1
NODENAME=""
NODEVERSION=""
/opt/ericsson/nms_cif_cs/etc/unsupported/bin/cstest -s ONRM_CS lt ManagedElement -an userLabel nodeVersion | while read line
do
i=$(($(($i+1))%3))
        if [ "${i}" -eq "1" ]; then
            NODENAME=${line##*:}
			NODENAME[$j]="`echo "${NODENAME[$j]}" | sed 's/"//g'`"
        fi
        if [ "${i}" -eq "2" ]; then
	        NODEVERSION=${line##*:}
			NODEVERSION[$j]="`echo "${NODEVERSION[$j]}" | sed 's/"//g'`"
			for element in ${unsynchvar[@]}
			do
				if [ ${element} == ${NODENAME} ]; then
					printf "%-15s %-15s %-15s \n" "$NODENAME" "$NODEVERSION" "UNSYNCHED"
				fi
				
			done
			
        fi
done	
	

