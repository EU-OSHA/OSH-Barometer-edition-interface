#!/bin/bash
set +v
echo Starting script process
# name of the file received from the form
export fileName=$1
# file extension
#export file_type=$2
# indicator of the form received
export indicator=$2
# directory where the file uploaded is saved
export input_directory=$3
# directory of the ETL where the uploaded file should be moved
export output_directory=$4
# year from of the form received
export yearFrom=$5
# year to of the form received
export yearTo=$6
#directory where the job is
export job_directory=$7
#spoon main directory
export spoon_directory=$8
#spoon logs directory
export logs_directory=$9
# todays date for LOG
export today=`date '+%Y_%m_%d'`

echo ====================================================

#if [ ${fileName} != EU_OSHA_Barometer_Eurostat ]
#then
#	echo Renaming file from ${input_directory}${fileName}${file_type} to ${input_directory}EU_OSHA_Barometer_Eurostat${file_type}
	# rename the duplicated file
#	mv ${input_directory}"${fileName}"${file_type} ${input_directory}EU_OSHA_Barometer_Eurostat${file_type}

#fi
# move file to a determine directory to make the ETL read it and process it
echo moving ${input_directory}${fileName} to ${output_directory}
mv ${input_directory}${fileName} ${output_directory}

# call ETL
if [ ! -n $yearTo ] 
then
	# call ETL adding just one year yearFrom
	echo received just one year
	sh ${spoon_directory}kitchen.sh -file=${job_directory}osha_dvt_barometer_eurostat_data.kjb -param:pIndicatorID=${indicator} -param:pYearFrom=${yearFrom} -param:pYearTo="NULL" -logfile=${logs_directory}SPOON_LOG_${today}.txt
else
	# call ETL adding the year from and year to fields yearFrom yearTo
	echo received year from and year to
	echo calling ${spoon_directory}kitchen.sh -file=${job_directory}osha_dvt_barometer_eurostat_data.kjb -param:pIndicatorID=${indicator} -param:pYearFrom=${yearFrom} -param:pYearTo=${yearTo}
	sh ${spoon_directory}kitchen.sh -file=${job_directory}osha_dvt_barometer_eurostat_data.kjb -param:pIndicatorID=${indicator} -param:pYearFrom=${yearFrom} -param:pYearTo=${yearTo} -logfile=${logs_directory}SPOON_LOG_${today}.txt
fi

echo ====================================================
# sleep
exit