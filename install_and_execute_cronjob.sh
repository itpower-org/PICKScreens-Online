#!/bin/bash
# build a jar of the crontab with dependencies and execute it with current configuration
# author: daniel.oltmanns@it-power.org
# since: 03/22/2013

mvn clean install
if test $? -ne 0; then exit 1; fi;
cd pickscreens-cron
mvn clean assembly:assembly
if test $? -ne 0; then exit 1; fi;
java -jar target/pickscreens-jar-with-dependencies.jar
