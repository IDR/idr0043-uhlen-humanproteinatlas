#!/bin/bash

if [ "$#" -ne 1 ]
then
  echo "
Runs through all directories with numercial directory name,
checks the image dimensions of the *.tif files and prints out 
the filenames of all images which require pixel pyramid generation.
Progress information is printed out to stderr.

Needs one argument: The directory where the tif directories are, e. g. run it with

./need_pyramids.sh /uod/idr/filesets/idr0043-uhlen-humanproteinatlas/20180624-ftp > pyramid_images.txt
"
  exit 1
fi

cd $1

pyramidSize=3192

total=`find [0-9]* -maxdepth 0 -type d | wc -l`
count=0

for d in `find [0-9]* -maxdepth 0 -type d`
do
  count=$((count+1))
  for f in `ls $d | grep -i .tif`
  do
    tmp=`tiffinfo $d/$f`
    if [ $? -ne 0 ]
    then
      echo "Invalid tif file: $d/$f"
    else
      tmp=`echo "$tmp" | grep "Image Width" | tr -s ' ' | cut -d ' ' -f 4,7`
      dims=(${tmp// / })
      if [ ${dims[0]} -gt ${pyramidSize} ] || [ ${dims[1]} -gt ${pyramidSize} ]
      then
        echo "$d/$f ${dims[0]}x${dims[1]}"
      fi
    fi
  done
  (>&2 echo "( Progress: $count / $total directories scanned)")
done
