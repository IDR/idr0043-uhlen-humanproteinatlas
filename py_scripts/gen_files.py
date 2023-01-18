import csv
import gzip
import argparse


BASE_DIR = "/uod/idr/filesets/idr0043-uhlen-humanproteinatlas"
GENEID_COL = "Comment [Gene Identifier]"
GENESYMBOL_COL = "Comment [Gene Symbol]"
GENE_SPLIT_CHAR = ";"
DATASET_COL = "Dataset Name"
FILEPATH_COL = "Comment [Image File Path]"
IMAGE_COL = "Image File"


def gen_filepaths(path, run_dir, enc):
    print("Creating idr0043-experimentA-filePaths.tsv...")
    with open_file(path, enc) as infile:
        with open("idr0043-experimentA-filePaths.tsv", "w") as outfile:
            reader = csv.DictReader(infile, delimiter="\t")
            writer = csv.writer(outfile, delimiter="\t")
            done = set()
            for row in reader:
                ds = row[DATASET_COL]
                ds = f"Dataset:name:{ds}"
                path = row[FILEPATH_COL]
                path = path.split("/")[0]
                if ds not in done:
                    path = f"{BASE_DIR}/{run_dir}/{path}"
                    writer.writerow([ds, path])
                    done.add(ds)


def gen_annotations(path, enc):
    print("Creating idr0043-experimentA-annotation.csv...")
    n_genes = 0
    with open_file(path, enc) as infile:
        reader = csv.DictReader(infile, delimiter="\t")
        for i, row in enumerate(reader):
            gis = row[GENEID_COL].split(GENE_SPLIT_CHAR)
            gss = row[GENESYMBOL_COL].split(GENE_SPLIT_CHAR)
            if len(gis) != len(gss):
                print(f"ERROR: Number of gene ids and symbols differ! (line {i})")
                return
            n_genes = max(n_genes, len(gis))

    empty_cols = []
    with open_file(path, enc) as infile:
        reader = csv.DictReader(infile, delimiter="\t")
        row = next(reader)
        for k in row.keys():
            if not row[k]:
                empty_cols.append(k)
        for row in reader:
            for k in row.keys():
                if row[k] and k in empty_cols:
                    empty_cols.remove(k)


    headers = ["Dataset name", "Image name"]
    excludes = [GENEID_COL, GENESYMBOL_COL, IMAGE_COL]
    excludes.extend(empty_cols)
    with open_file(path, enc) as infile:
        reader = csv.DictReader(infile, delimiter="\t")
        row = next(reader)
        for k in row.keys():
            if k not in excludes:
                headers.append(k)
    for i in range(0, n_genes):
        headers.append(f"Comment [Gene Identifier] {i+1}")
        headers.append(f"Comment [Gene Symbol] {i+1}")


    with open_file(path, enc) as infile:
        with open("idr0043-experimentA-annotation.csv", "w") as outfile:
            reader = csv.DictReader(infile, delimiter="\t")
            writer = csv.DictWriter(outfile, delimiter=',', fieldnames=headers)
            writer.writeheader()
            for in_row in reader:
                out_row = {}
                for k in in_row.keys():
                    if k == DATASET_COL:
                        ds = in_row[DATASET_COL]
                        out_row["Dataset name"] = ds
                    if k == IMAGE_COL:
                        img = in_row[IMAGE_COL]
                        out_row["Image name"] = img
                    if k == GENEID_COL:
                        gis = in_row[GENEID_COL].split(GENE_SPLIT_CHAR)
                        for i, gi in enumerate(gis):
                            out_row[f"Comment [Gene Identifier] {i+1}"] = gi
                    if k == GENESYMBOL_COL:
                        gss = in_row[GENESYMBOL_COL].split(GENE_SPLIT_CHAR)
                        for i, gs in enumerate(gss):
                            out_row[f"Comment [Gene Symbol] {i+1}"] = gs
                    elif k not in excludes:
                        out_row[k] = in_row[k]
                writer.writerow(out_row)


def open_file(path, e):
    if path.endswith(".gz"):
        return gzip.open(path, mode='rt', encoding=e)
    else:
        return open(path, 'r', encoding=e)


def main(args):
    if args.f:
        gen_filepaths(args.file, args.dir, args.e)
    if args.a:
        gen_annotations(args.file, args.e)


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Generate annotations and filepath from assays.txt")
    parser.add_argument('-a', action="store_true", default=False,
                        help='Generate idr0043-experimentA-annotation.csv')
    parser.add_argument('-f', action="store_true", default=False,
                        help='Generate idr0043-experimentA-filePaths.tsv')
    parser.add_argument('-e', default="utf-8", help="Encoding (default: utf-8, but you might have to try latin1)")
    parser.add_argument("file", help="Path to assays.txt(.gz)")
    parser.add_argument("dir", help="Directory of the 'batch', e.g. 20220128-ftp")
    args = parser.parse_args()
    main(args)
