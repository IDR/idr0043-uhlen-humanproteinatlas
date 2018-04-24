#!/bin/bash

### BEGIN Settings
export omero=/opt/omero/server/OMERO.server/bin/omero
filePaths=../../experimentA/idr0043-experimentA-filePaths.tsv
nJobs=10
### END Settings

import_image() {
	input=`echo $1 | expand -t1`
    $omero import -c --transfer "ln_s" --exclude "clientpath" --checksum-algorithm "File-Size-64" --logprefix "logs/" --output "yaml" --target $input
}
export -f import_image


parallel -a $filePaths --jobs $nJobs --results rslt --joblog log import_image

