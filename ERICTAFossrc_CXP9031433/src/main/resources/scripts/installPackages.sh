#!/bin/sh
/opt/ericsson/sck/bin/ist_run -d $1 -pa -auto
/opt/ericsson/nms_cif_ist/bin/cist -notification -send 
