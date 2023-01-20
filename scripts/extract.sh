#! /bin/bash
# Script to extract a batch upload of HPA antibodies transferred via FTP
# Each upload is structured as follows
# - assays.txt       the assays file for the upload
# - md5sum.txt       a checksums file
# - 101.tar          the indidivual TAR files containing the imaging data
# - 102.tar
# Each TAR file will be untarred into a directory containing GZIP'ed TIFF
# files

PREFIX=${PREFIX:-}

for file in $PREFIX*.tar; do
    echo "Extracting $file"
    if [ -f md5sum.txt ]
    then
        cat md5sum.txt | grep " $file" | md5sum -c
    fi
    tar xvf ${file}
    if [ $? -eq 0 ]
    then
        rm $file
        for f in ${file%.*}/*.gz; do
            gunzip $f
        done
    fi
done
