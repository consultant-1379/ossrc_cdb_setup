#!/bin/bash

	ATTEMPT=1
	echo "INFO: Waiting until Oss is online\n" 

  while [ $ATTEMPT -le 360 ]
    do
		echo "INFO: Attempt number $ATTEMPT of 360 to check Oss online status \n"
		hagrp -display Oss -sys ossmaster | grep ONLINE >  /dev/null 2>&1
		if [ $? -ne 0 ]
		then
			echo "INFO: Oss is not online yet, waiting for 5 seconds before trying again\n"
			sleep 5
		else
			echo "INFO: Oss is online now\n"
			hastatus -sum
			exit 0
		fi
		ATTEMPT=`expr $ATTEMPT + 1`
	done
	exit 1
