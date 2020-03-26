from omero.gateway import BlitzGateway
import pandas

'''
This script simply deletes all bulk_annotation attachements from the datasets
referenced in the annotation.csv.
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

datasetIds = set()
project = conn.getObject("Project", projectId)
for ds in project.listChildren():
    if ds.name in datasets:
        ds.removeAnnotations("openmicroscopy.org/omero/bulk_annotations")
        print("Deleted bulk_annotations attachment for %s" % ds.name)
