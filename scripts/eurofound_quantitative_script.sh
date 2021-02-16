#!/bin/bash
set +v
echo Starting script process
#name of the file received from the form
export fileName=$1
#extension of the file received
#export file_type=$2
#year of the form received
export pYear=$2
#directory where the file uploaded is saved
export input_directory=$3
#directory of the ETL where the uploaded file should be moved
export output_directory=$4
#directory where the job is
export job_directory=$5
#spoon main directory
export spoon_directory=$6
#spoon logs directory
export logs_directory=$7
#current date for the ETL logs
export today=`date '+%Y_%m_%d'`

echo ====================================================

#if [ ${fileName} != EU_OSHA_Barometer_Eurofound_Dataset ]
#then
	#echo Copying file from ${input_directory}
#	echo Renaming file ${input_directory}${fileName}${file_type} to ${input_directory}EU_OSHA_Barometer_Eurofound_Dataset${file_type}
#	mv ${input_directory}${fileName}${file_type} ${input_directory}EU_OSHA_Barometer_Eurofound_Dataset${file_type}
#fi
echo Moving file to ${output_directory}
#move file to a determine directory to make the ETL read it and process it
#mv ${input_directory}EU_OSHA_Barometer_Eurofound_Dataset${file_type} ${output_directory}
mv ${input_directory}${fileName} ${output_directory}

#call ETL
#kitchen.sh -option=value ${pYear}
echo ${spoon_directory}kitchen.sh -file=${job_directory}osha_dvt_barometer_eurofound_data.kjb -param:pYear=${pYear} -logfile=${logs_directory}SPOON_LOG_${today}.txt
sh ${spoon_directory}kitchen.sh -file=${job_directory}osha_dvt_barometer_eurofound_data.kjb -param:pYear=${pYear} -logfile=${logs_directory}SPOON_LOG_${today}.txt
echo ====================================================
#sleep
exit