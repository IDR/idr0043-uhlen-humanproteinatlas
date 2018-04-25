#!/bin/bash

###
# Use GNU parallel tool to perform the image import in parallel.
# Runs an import job for each entry (line) in the filePaths.tsv file.
# 
# Usage: Adjust the filePaths setting and as appropriate the number 
# of parallel jobs (nJobs). The other options are usually fine as 
# they are. Check that the import options (line 28) match the options
# specified in the bulk.yml file (unfortunately this script doesn't 
# use the bulk.yml!)
#
# IMPORTANT: Log in first before launching the script!
# ( /opt/omero/server/OMERO.server/bin/omero login )
###

### BEGIN Settings
export omero=/opt/omero/server/OMERO.server/bin/omero
filePaths=../../experimentA/idr0043-experimentA-filePaths.tsv
jobslogfile=log
jobsresultdir=rslt
nJobs=15
### END Settings

import_image() {
  input=`echo $1 | expand -t1`
  # --> Check that the import options are the same as in the bulk.yml
  $omero import -c --transfer "ln_s" --exclude "clientpath" --checksum-algorithm "File-Size-64" --logprefix "logs/" --output "yaml" --target $input
}
export -f import_image


parallel -a $filePaths --jobs $nJobs --results $jobsresultdir --joblog $jobslogfile import_image

