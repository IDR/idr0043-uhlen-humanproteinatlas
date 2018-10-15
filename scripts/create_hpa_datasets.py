# Example script for reorganizing an HPA run into n datasets, one per antibody

from omero.gateway import BlitzGateway
import omero.model
from omero.rtypes import rstring

# Create a BlitzGateway connection
user = "public"
password = "public"
host = "idr.openmicroscopy.org"
conn = BlitzGateway(user, password, host=host)

# Load HPA dataset with 500K images
datasetId = 4528768
dataset = conn.getObject("Dataset", datasetId)
print "Number of images: %g" % dataset.countChildren()

# Create a dictonary of antibodies. Each key is an antibody ID and the
# values are the associated image IDs
antibodies = {}

# TODO: looping over this generator timed out. Probably need to use pagination
# to load images in batches rather than 500K at once
for i in dataset.listChildren():
    # TODO: This code used the annotation for testing - the mapping should
    # probably either use the imported file paths or the
    # assays.txt/annotation.csv
    ann = i.getAnnotation('openmicroscopy.org/mapr/antibody')
    kv = ann.getValue()[0]
    if kv[0] != 'Antibody Identifier':
        print 'Missing antibody for image %g' % i.id
    antibody = kv[1]
    antibodies.setdefault(kv[1], []).append(i.id)
print "Found %g antibodies" % len(antibodies)


# Create dataset and dataset/image links in batches
for antibodyName, imageIds in antibodies.iteritems():
    # Create new dataset using the antibody ID as the name
    # This is not idempotent and will recreate 1K datasets
    dataset = omero.model.DatasetI()
    dataset.setName(rstring(antibodyName))

    # Create an array of links
    links = []
    for imageId in imageIds:
        link = omero.model.DatasetImageLinkI()
        link.parent = dataset
        link.child.id = imageId
        links.append(links)

    # Create the array of links
    links = conn.getUpdateService().saveAndReturnArray(links)
    print "Created dataset %s with %g images" % (antibodyName, len(imageIds))

    # TODO: return the dataset ID and create a project/dataset link. Otherwise
    # all datasets will be orphaned
