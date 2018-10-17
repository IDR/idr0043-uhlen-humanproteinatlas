from omero.gateway import BlitzGateway
import omero.model
from omero.rtypes import rstring
import pandas

'''
This script simply gets the IDs of the datasets referenced in the annotation.csv
and prints them to std out.
'''

# Files
annoFile = "../experimentA/hpa_run_01/idr0043-experimentA-annotation.csv"

# OMERO credentials
user = "root"
password = "xxx"
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

project = conn.getObject("Project", projectId)
for ds in project.listChildren():
    if ds.name in datasets:
        print ds.id
