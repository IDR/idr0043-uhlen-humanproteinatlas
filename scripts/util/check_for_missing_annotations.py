from __future__ import print_function
from omero.gateway import BlitzGateway
import pandas
import sys
import os


def printusage():
    print ('''
This script checks for missing annotations and prints the dataset name,
image name and image id of images which don't have any annotations to stdout.
Progress information is printed to stderr.

Usage: python check_for_missing_annotations.py [path to annotation.csv] \
[Project ID] >> no_annotations.csv

Environment variables OMERO_USER, OMERO_PASSWORD, OMERO_HOST and OMERO_PORT
are not necessary but are taken into account if set.
          ''')
    sys.exit(1)


if len(sys.argv) < 3:
    printusage()
else:
    annoFile = sys.argv[1]
    projectId = sys.argv[2]

host = os.environ.get('OMERO_HOST', 'localhost')
port = int(os.environ.get('OMERO_PORT', '4064'))

datasets = set()
df = pandas.read_csv(annoFile)
for index, row in df.iterrows():
    datasets.add(row["Dataset Name"])

conn = BlitzGateway(os.environ.get('OMERO_USER', 'public'),
                    os.environ.get('OMERO_PASSWORD', 'public'),
                    host=host, port=port)
conn.connect()

print("Dataset,Image,ID")
project = conn.getObject("Project", projectId)
total = len(datasets)
count = 0
for ds in project.listChildren():
    if ds.name in datasets:
        print("Checking dataset %d / %d" % (count, total), file=sys.stderr)
        count = count + 1
        for img in ds.listChildren():
            if len(list(img.listAnnotations())) == 0:
                print("%s,%s,%d" % (ds.name, img.getName(), img.getId()))
