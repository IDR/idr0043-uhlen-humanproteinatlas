
# Script to generate the filePaths.tsv for the image files 
# which have to be reimported. It needs to be figured 
# out via the original filePaths.tsv which image has to go 
# into which datasets.

new_path = "/uod/idr/filesets/idr0043-uhlen-humanproteinatlas/20210108-ftp"

datasets = {}
file = open('idr0043-experimentA-filePaths.tsv', 'r') 
for line in file.readlines():
    # Dataset:name:HPA039079	/uod/idr/filesets/idr0043-uhlen-humanproteinatlas/20201116-s3/39079
    tmp = line.split('\t')[0]
    ds = tmp.split(':')[2]
    tmp = line.split('\t')[1]
    dry = tmp.split('/')[6].strip()
    datasets[dry] = ds
file.close() 

out_file = open('reimport-filePaths.tsv', 'w')

file = open('to_replace.paths', 'r') 
for line in file.readlines(): 
     # uod/idr/filesets/idr0043-uhlen-humanproteinatlas/20201116-s3/37048/82735_B_1_1.tif
     dry = line.split('/')[5]
     fname = line.split('/')[6].strip()
     ds = datasets[dry]
     path = "{}/{}/{}".format(new_path,dry,fname)
     out_file.write("Dataset:name:{}\t{}\n".format(ds, path))
file.close()

out_file.close()
