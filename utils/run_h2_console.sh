#!/bin/sh

export URL="jdbc:h2:${1}jvoiddb;AUTO_RECONNECT=TRUE;AUTO_SERVER=TRUE"
export USERNAME="jvoid"
export PASSWORD="jvoid"
echo "JDBC URL $URL"

java -cp ~/.m2/repository/com/jvoid/jvoid/1.0.0-SNAPSHOT/jvoid-1.0.0-SNAPSHOT.jar  org.h2.tools.Console -url $URL -user $USERNAME -password $PASSWORD
