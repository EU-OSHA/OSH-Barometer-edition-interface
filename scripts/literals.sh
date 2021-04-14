#!/bin/bash
set +v
echo Starting script process
#directory where the job is
export job_directory=$1
#spoon main directory
export spoon_directory=$2
#spoon logs directory
export logs_directory=$3
#current date for the ETL logs
export today=`date '+%Y_%m_%d'`

#call ETL
#kitchen.sh -option=value ${pYear}
echo ${spoon_directory}kitchen.sh -file=${job_directory}osha_dvt_barometer_create_literals_and_links.kjb -logfile=${logs_directory}SPOON_LOG_${today}.txt
sh ${spoon_directory}kitchen.sh -file=${job_directory}osha_dvt_barometer_create_literals_and_links.kjb -logfile=${logs_directory}SPOON_LOG_${today}.txt
echo ====================================================
#sleep
exit