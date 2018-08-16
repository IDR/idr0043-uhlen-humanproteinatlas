# Runs through all directories with numercial directory name
# and checks the image dimensions of the *.tif files.
#
# Prints out a CSV with the different dimensions and the number of
# images with these dimensions
# 
# Prints out progress information on stderr
#
# Needs one argument: The directory where the tif directories are, e. g. run it with
# ./image_size_distribution.sh /uod/idr/filesets/idr0043-uhlen-humanproteinatlas/20180624-ftp > output.csv

cd $1

declare -A sizes

total=`find [0-9]* -maxdepth 0 -type d | wc -l`
count=0

for d in `find [0-1]* -maxdepth 0 -type d`
do
        for f in `ls $d | grep .tif`
        do
                tmp=`tiffinfo $d/$f`
                if [ $? -ne 0 ]
                then
                        echo "Invalid tif file: $d/$f"
                else
                        tmp=`echo "$tmp" | grep "Image Width" | tr -s ' ' | cut -d ' ' -f 4,7`
                        dims=(${tmp// / })

                        key="${dims[0]}x${dims[1]}"

                        if [ ${sizes[$key]+_} ]
                        then 
                                sizes[$key]=$((${sizes[$key]}+1))
                        else 
                                sizes[$key]="1"
                        fi
                fi
        done
        count=$((count+1))
        (>&2 echo "( Progress: $count / $total )")
done

echo "Image size (10^6 pixels),Image dimensions,Number of images"

for K in "${!sizes[@]}"
do
        w=`echo $K | cut -d 'x' -f 1`
        h=`echo $K | cut -d 'x' -f 2`
        s=$(($w*$h/1000000))
        echo "$s,$K,${sizes[$K]}"
done

