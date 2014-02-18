#!/bin/bash 
CURRENT_DIR=`dirname $0`
LINKTARGET=`readlink -f $CURRENT_DIR/opticsswitcher`
DIR=`dirname $LINKTARGET`
cd $DIR/../lib/openxal && 
java -cp "openxal.apps.opticsswitcher-1.0.0-SNAPSHOT.jar:*" xal.app.opticsswitcher.Main
