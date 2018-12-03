#!/bin/bash

## Iterates over the filePaths.tsv, creates all Datasets
## and links them to the Project.

# The project name
projectName=idr0043-uhlen-humanproteinatlas/experimentA 

# The filePaths.tsv
filePaths=../../experimentA/hpa_run_02/idr0043-experimentA-filePaths.tsv

# The path to the omero CLI
export omero=/opt/omero/server/OMERO.server/bin/omero

#######################

# Create a session
$omero login

# Create a list of dataset names
datasets=`cat $filePaths | cut -f1 | cut -d ':' -f3 | uniq`


projectId=`$omero hql --ids-only --limit 1 --style plain "select p from Project p where p.name='$projectName'"`
if [ -z $projectId ]; then
  echo "Project doesn't exist"
  exit 1
else
  projectId=`echo $projectId | cut -d ',' -f2`
  echo "Project id = $projectId"
fi

for dataset in $datasets
do
  datasetId=`$omero hql --ids-only --limit 1 --style plain "select d from Dataset d where d.name='$dataset'"`
  if [ -z $datasetId ]; then
    datasetId=`$omero obj new Dataset name=$dataset`
    echo "Created dataset $dataset , id = $datasetId"
  else
    datasetId=`echo $datasetId | cut -d ',' -f2`
    echo "Dataset $dataset exists, id = $datasetId"
  fi

  linkId=`$omero hql --ids-only --limit 1 --style plain "select l from ProjectDatasetLink l join l.child as ds where ds.name='$dataset'"`
  if [ -z $linkId ]; then
    linkId=`$omero obj new ProjectDatasetLink parent=$projectId child=$datasetId`
    echo "Created ProjectDatasetLink $projectId $datasetId, id = $linkId"
  else
    echo "ProjectDatasetLink $projectId $datasetId exists, id = $linkId"
  fi
done
