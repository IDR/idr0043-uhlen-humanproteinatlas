# Example script for reorganizing an HPA run into n datasets, one per antibody

from omero.gateway import BlitzGateway
import omero.model
from omero.rtypes import rstring
import pandas

# Files
annoFile = "../experimentA/hpa_run_01/idr0043-experimentA-annotation.csv"
imageIdsFile = "imageIds.txt"
'''
Create the imageIds.txt with:
psql -h 192.168.53.5 idr omeroreadonly -c 'select child from
    datasetimagelink where parent = 1351;' > output.txt
sed -i 's/[ ]*//' output.txt
sed -i 1,2d output.txt
head -n -2 output.txt > imageIds.txt
'''

# OMERO credentials
user = "root"
password = "ome"
host = "localhost"

# HPA Project id
projectId = 501

##########

conn = BlitzGateway(user, password, host=host)
conn.connect()

project = conn.getObject("Project", projectId)
existingDatasetsByName = {}
for ds in project.listChildren():
    existingDatasetsByName[ds.name] = ds._obj

datasetByImageName = {}
df = pandas.read_csv(annoFile)
for index, row in df.iterrows():
    if row["Image Name"] in datasetByImageName:
        raise Exception(" !!! line %i : %s has already been added"
                        % (index, row["Image Name"]))
    datasetByImageName[row["Image Name"]] = row["Dataset Name"]

datasetByName = {}
for imgName, dsName in datasetByImageName.iteritems():
    if dsName not in existingDatasetsByName:
        if dsName not in datasetByName:
            dataset = omero.model.DatasetI()
            dataset.setName(rstring(dsName))
            dataset = conn.getUpdateService().saveAndReturnObject(dataset)
            datasetByName[dsName] = dataset
            datasetByImageName[imgName] = dataset
        else:
            datasetByImageName[imgName] = datasetByName[dsName]
    else:
        datasetByImageName[imgName] = existingDatasetsByName[dsName]

links = []
for ds in datasetByName.values():
    link = omero.model.ProjectDatasetLinkI()
    link.setParent(project._obj)
    link.setChild(ds)
    links.append(link)
if len(links) > 0:
    conn.getUpdateService().saveAndReturnArray(links)

links = []
done = 0
with open(imageIdsFile) as reader:
    line = reader.readline()
    while line:
        img = conn.getObject("Image", line)
        try:
            dataset = datasetByImageName[img.name]
        except KeyError:
            dataset = None
            print("%s not found, skipping." % img.name)

        if dataset is not None:
            link = omero.model.DatasetImageLinkI()
            link.setParent(dataset)
            link.setChild(img._obj)
            links.append(link)
        if len(links) > 99:
            try:
                conn.getUpdateService().saveAndReturnArray(links)
                done += len(links)
                print("%i images linked." % done)
            except Exception:
                print("Error. Skipping some.")
            finally:
                links = []
        line = reader.readline()

if len(links) > 0:
    try:
        conn.getUpdateService().saveAndReturnArray(links)
        done += len(links)
        print("%i images linked." % done)
    except Exception:
        print("Error. Skipping some.")
