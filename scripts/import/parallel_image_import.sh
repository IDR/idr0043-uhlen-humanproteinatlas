#!/bin/bash

###
# Use GNU parallel tool to perform the image import in parallel.
# 
# Usage: Adjust the bulkFile location and as appropriate the number of 
# parallel jobs (nJobs). The other options are usually fine as they are.  
#
# Note: Check the log afterwards for errors, e.g.
# cat log  | cut -f 7,9 | grep -v "^0"
# Prints out the jobs which exited with an error code
###

### BEGIN Settings
bulkFile=../../experimentA/hpa_run_01/idr0043-experimentA-bulk.yml
nJobs=10
jobslogfile=log
jobsresultdir=rslt
export omero=/opt/omero/server/OMERO.server/bin/omero
### END Settings

$omero login

##  Preparation
###############

# Determine experiment directory
expDir=`dirname $bulkFile`

# Determine project directory and name
projectName=idr0043-uhlen-humanproteinatlas/experimentA 

# Determine path to filePaths.tsv
filePaths=$expDir/`grep "path:" $bulkFile | cut -d " " -f2 | tr -d "\""`

# Create a list of dataset names
datasets=`cat $filePaths | cut -d '	' -f1 | cut -d ':' -f3 | uniq`

# Create a temporary bulk.yml with dry_run option set
tmpBulk=$expDir/tmp.yml
cp $bulkFile $tmpBulk
echo 'dry_run: "true"' >> $tmpBulk

# Use tmp bulk.yml to generate the list of import commands
$omero import --bulk $tmpBulk > commands.txt

rm $tmpBulk


##  Create and link all datasets first
######################################
projectId=`$omero hql --ids-only --limit 1 --style plain "select p from Project p where p.name='$projectName'"`
if [ -z $projectId ]; then
  projectId=`$omero obj new Project name=$projectName`
else
  projectId=`echo $projectId | cut -d ',' -f2`
fi

for dataset in $datasets
do
  datasetId=`$omero hql --ids-only --limit 1 --style plain "select d from Dataset d where d.name='$dataset'"`
  if [ -z $datasetId ]; then
  	datasetId=`$omero obj new Dataset name=$dataset`
  else
  	datasetId=`echo $datasetId | cut -d ',' -f2`
  fi

  linkId=`$omero hql --ids-only --limit 1 --style plain "select l from ProjectDatasetLink l join l.child as ds where ds.name='$dataset'"`
  if [ -z $linkId ]; then
    $omero obj new ProjectDatasetLink parent=$projectId child=$datasetId
  fi
done


##  Perform parallel image import
#################################

import_image() {
  options=${1//\"/}
  $omero import $options
}
export -f import_image

parallel -a commands.txt --delay 3 --jobs $nJobs --results $jobsresultdir --joblog $jobslogfile import_image

rm commands.txt

$omero logout


