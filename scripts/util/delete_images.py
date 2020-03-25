from __future__ import print_function
from omero.gateway import BlitzGateway
import pandas
import sys
import os


def printusage():
    print('''
This script deletes the images specified by the 'ID' column in a CSV file.

Usage: python delete_images.py [path to images.csv]

Environment variables OMERO_USER and OMERO_PASSWORD need to be set.
(OMERO_HOST and OMERO_PORT are supported but optional)
          ''')
    sys.exit(1)


if len(sys.argv) < 2:
    printusage()
else:
    annoFile = sys.argv[1]

if not (os.environ.get('OMERO_USER') and
        os.environ.get('OMERO_PASSWORD')):
    printusage()

host = os.environ.get('OMERO_HOST', 'localhost')
port = int(os.environ.get('OMERO_PORT', '4064'))

ids = []
data = pandas.read_csv(annoFile)
for index, row in data.iterrows():
    imgId = row["ID"]
    ids.append(imgId)

conn = BlitzGateway(os.environ['OMERO_USER'], os.environ['OMERO_PASSWORD'],
                    host=host, port=port)
conn.connect()
print("Deleting Images...")
print(ids)
conn.deleteObjects("Image", ids, wait=True)
print("Done.")
