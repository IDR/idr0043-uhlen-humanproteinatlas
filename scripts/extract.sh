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
N=${N:-10}

#
find $PREFIX*.tar > files.txt
parallel -a files.txt --jobs 10 --joblog log --results results "cat md5sum.txt | grep ' {}' | md5sum -c && tar xvf {} && rm {} && gunzip {/.}/*.gz"
rm files.txt
