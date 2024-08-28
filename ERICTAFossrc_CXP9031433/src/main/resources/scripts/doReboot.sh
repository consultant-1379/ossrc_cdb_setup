#!/bin/bash

#Set some variables
SSH="/usr/bin/ssh -o StrictHostKeyChecking=no"

function wait_until_sshable ()
{
	local SERVER=$1
	local ATTEMPT=1
	echo "INFO: Waiting until $SERVER is sshable\n" 

        while [[ $ATTEMPT -le 360 ]]
        do
		echo "INFO: Attempt number $ATTEMPT of 360 to ssh to $SERVER\n"
		$SSH $SERVER : > /dev/null 2>&1
		if [[ $? -ne 0 ]]
		then
			echo "INFO: $SERVER is not sshable yet, waiting for 5 seconds before trying again\n"
			sleep 5
		else
			echo "INFO: SSH working towards $SERVER\n"
			#message "OK\n" INFO
			return 0
		fi
		let ATTEMPT=ATTEMPT+1
	done
	exit 1
}

function reboot_server ()
{
	local SERVER=$1
    $SSH -qt $SERVER "reboot"
  #  wait_until_not_pingable $SERVER
    wait_until_sshable $SERVER
	echo "Ossmaster rebooted successfully"

}


#Main programme starts here

reboot_server $1
