# Runs through all directories with numercial directory name
# and checks the image dimensions of the *.tif files. 
# Prints out max width and height. Also reports broken tif files.
# Needs one argument: The directory where the tif directories are, 
# e. g. run it with
# ./max_plane_size.sh /uod/idr/filesets/idr0043-uhlen-humanproteinatlas/20180624-ftp

maxwidth=0
maxheight=0

cd $1

total=`find [0-9]* -maxdepth 0 -type d | wc -l`
count=0

for d in `find [0-9]* -maxdepth 0 -type d`
do
        for f in `ls $d | grep -i .tif`
        do
                tmp=`tiffinfo $d/$f`
                if [ $? -ne 0 ]
                then
                        echo "Invalid tif file: $d/$f"
                else
                        tmp=`echo "$tmp" | grep "Image Width" | tr -s ' ' | cut -d ' ' -f 4,7`
                        dims=(${tmp// / })
                        if [[ "${dims[0]}" -gt "$maxwidth" ]]
                        then
                                maxwidth=${dims[0]}
                                echo "New max width " $maxwidth "(" $d/$f ")"
                        fi
                        if [[ "${dims[1]}" -gt "$maxheight" ]]
                        then
                                maxheight=${dims[1]}
                                echo "New max height " $maxheight "(" $d/$f ")"
                        fi
                fi
        done
        count=$((count+1))
        echo "( Progress: $count / $total )"
done

echo "Max width: " $maxwidth ", max height: " $maxheight
