#!/bin/bash
#
# Deletes all images of the specified dataset in batches.
# Useful if there are 100 thousands of images in a dataset, because
# deleting the dataset directly doesn't work in these cases.

datasetId=1753
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
	
	$omero delete Image:$ids

	count=$((count+$batchSize))
	
	d=`date`
	echo "$d : Deleted $count images so far."
	
done
