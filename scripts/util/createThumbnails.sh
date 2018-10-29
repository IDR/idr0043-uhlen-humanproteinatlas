#!/bin/bash
#
# Calls the 'test' command of the render plugin on each image
# within the specified dataset. This will create the thumbnails,
# if the import was done with 'skip thumbnails' option.

export omero=/opt/omero/server/OMERO.server/bin/omero

if [ "$#" -ne 1 ]
then
    echo "Call this skript with the dataset id as parameter."
    exit 1
fi

datasetId=$1

$omero login

shopt -s extglob

ids=`$omero hql --ids-only --style plain "select link.child from DatasetImageLink link where link.parent.id=$datasetId"`

if [[ -z $ids ]]
then
    echo "No images found for dataset id $datasetId ."
    exit 0
fi

ids=${ids//+([[:digit:]]),ImageI:/}
ids=${ids//[[:space:]]/,}

$omero render test Image:$ids
