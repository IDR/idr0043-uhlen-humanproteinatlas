#! /bin/bash
# Script to extract a batch upload of HPA antibodies transferred via FTP
# Each upload is structured as follows
# - assays.txt       the assays file for the upload
# - md5sum.txt       a checksums file
# - 101.tar          the indidivual TRA files containing the imaging data
# - 102.tar
# Each TAR file will be untarred into a directory containing GZIP'ed TIFF
# files

PREFIX=${PREFIX:-}

# Verify checksums
md5sum -c md5sum.txt
#
for file in $PREFIX*.tar; do
    tar xvf ${file}
    id="${file%.*}"
    gunzip $id/*.gz
done