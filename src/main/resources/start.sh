#!/bin/sh

source /etc/profile

jarPath=`dirname "$(pwd)/${0}"`

JAVA_OPTS="-Djava.awt.headless=true -Xverify:none -Dfile.encoding=UTF8 -server -Xmx512m -Xms512m -Xmn128m -XX:PermSize=128m -XX:MaxPermSize=128m
    -XX:-DisableExplicitGC -verbose:gc -XX:ParallelGCThreads=8 -XX:MaxTenuringThreshold=5 -XX:-UseAdaptiveSizePolicy -XX:TargetSurvivorRatio=90
    -XX:+ScavengeBeforeFullGC -XX:+PrintGC -XX:+PrintGCDetails -XX:+PrintGCTimeStamps
    -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=${jarPath}/oom-error.log -Xloggc:${jarPath}/gc.log"

java ${JAVA_OPTS} -jar ${jarPath}/gameserver-1.0-SNAPSHOT.jar
