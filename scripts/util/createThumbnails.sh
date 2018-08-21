#!/bin/bash
#
# Calls the 'test' command of the render plugin on each image
# within the specified dataset. This will create the thumbnails,
# if the import was done with 'skip thumbnails' option.

datasetId=1802
batchSize=25

export omero=/opt/omero/server/OMERO.server/bin/omero

$omero login

shopt -s extglob

count=0

while :
do
	ids=`$omero hql --ids-only --limit $batchSize --style plain "select link.child from DatasetImageLink link where link.parent.id=$datasetId"`

	if [[ -z $ids ]]
	then
		break
	fi

	ids=${ids//+([[:digit:]]),ImageI:/}
	ids=${ids//[[:space:]]/,}
	
	$omero render test Image:$ids

	count=$((count+$batchSize))
	
	d=`date`
	echo "$d : Checked $count images so far."
	
done
