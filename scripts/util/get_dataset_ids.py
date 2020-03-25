from omero.gateway import BlitzGateway
import pandas

import sys

if len(sys.argv) < 2:
    print("""
This script simply gets the IDs of the datasets referenced in the
annotation.csv and prints them to std out, e.g.
Dataset:1
Dataset:2
...

Usage:
python get_dataset_ids.py annotations.csv [-c]

-c  Concatenate image ids instead of outputting them line by line (will
    print out "Dataset:1,2,3,...") (must be last argument)
""")
    exit(1)

annoFile = sys.argv[1]

concat = False
if len(sys.argv) > 2:
    concat = sys.argv[2] == '-c'

# OMERO credentials
user = "public"
password = "public"
host = "localhost"

# HPA Project id
projectId = 501

##########

datasets = set()
df = pandas.read_csv(annoFile)
for index, row in df.iterrows():
    datasets.add(row["Dataset Name"])

conn = BlitzGateway(user, password, host=host)
conn.connect()

if concat:
    result = "Dataset:"

project = conn.getObject("Project", projectId)
for ds in project.listChildren():
    if ds.name in datasets:
        if concat:
            result += str(ds.id)+","
        else:
            print('Dataset:%s' % ds.id)

if concat:
    print(result[:-1])
