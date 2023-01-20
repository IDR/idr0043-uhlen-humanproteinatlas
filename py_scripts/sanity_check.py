import gzip
import sys
import re
import csv
import os
import argparse
import omero.cli
import omero.gateway


PROJECT = "idr0043-uhlen-humanproteinatlas/experimentA"
REPO = "/uod/idr/metadata/idr0043-uhlen-humanproteinatlas"


# Potential problems
PROBLEM_NO_BA = "No bulkannotations"
PROBLEM_MORE_BA = "More than one bulkannotations"
PROBLEM_NO_A = "No map annotations"
PROBLEM_NO_IMAGE = "Image missing"
PROBLEM_NO_DATASET = "Dataset missing"

# All problems encountered will be stored here:
problems = {}


def get_n_datasets(project):
    """
    Get the number of all imported datasets
    :param project:
    :return: See above
    """
    return len(list(project.listChildren()))


def get_dataset(project):
    """
    Generator for the datasets of a project
    :param project: The project
    :return: See above
    """
    for dataset in project.listChildren():
        yield dataset


def get_image(dataset):
    """
    Generator for the images of a dataset
    :param project: The dataset
    :return: See above
    """
    for image in dataset.listChildren():
        yield image


def get_annotations(obj):
    """
    Get all annotations of an object as list
    :param obj:
    :return:
    """
    return list(obj.listAnnotations())


def parse_assays(repo_path):
    """
    Get all dataset and images names from the assay files
    :param repo_path: The path to the local repository
    :return: Tuple (Image map (key: Dataset name; value: List of image names),
             Batch map (key: Dataset name; value: batch (hpa_run_xx))
    """
    image_map = {}
    batch_map = {}
    for entry in os.walk(f"{repo_path}/experimentA"):
        if "hpa_run" in entry[0]:
            found = False
            for file in entry[2]:
                if "assay" in file:
                    found = True
                    batch = re.sub(r".*/", "", entry[0])
                    assay_map = read_assay(f"{entry[0]}/{file}")
                    image_map.update(assay_map)
                    for ds in assay_map.keys():
                        batch_map[ds] = batch
            if not found:
                print(f"WARNING: No assays.txt for {entry[0]}!", file=sys.stderr)
    return (image_map, batch_map)


def read_assay(path):
    """
    Get dataset and image names from a gzipped assays file
    :param path: The path to the assay file
    :return: Image map (key: Dataset name; value: List of image names)
    """
    image_map = {}
    print(f"Parsing {path}.", file=sys.stderr)
    with gzip.open(path, mode='rt', encoding="latin1") as csvfile:
        reader = csv.DictReader(csvfile, delimiter="\t")
        for row in reader:
            ds_key = "Dataset Name"
            if not ds_key in row:
                ds_key = "Antibody identifier"
            ds = row[ds_key]
            img = row["Image File"]
            if ds not in image_map:
                image_map[ds] = []
            image_map[ds].append(img)
    return image_map


def check_annotations(dataset, check_images):
    """
    Check the dataset if it has a bulkannotation table attached
    :param dataset: The dataset
    :param check_images: Flag to check all images for map annotations
    :return: None
    """
    n_bulkanns = len(get_annotations(dataset))
    if n_bulkanns == 0:
        print(f"ERROR: Missing bulkannotations!", file=sys.stderr)
        if PROBLEM_NO_BA not in problems:
            problems[PROBLEM_NO_BA] = []
        problems[PROBLEM_NO_BA].append(f"{dataset.getId()} | {dataset.getName()}")

    elif n_bulkanns > 1:
        print(f"ERROR: More than one bulkannotations!", file=sys.stderr)
        if PROBLEM_MORE_BA not in problems:
            problems[PROBLEM_MORE_BA] = []
        problems[PROBLEM_MORE_BA].append(f"{dataset.getId()} | {dataset.getName()}")

    if check_images:
        for image in get_image(dataset):
            n_anns = len(get_annotations(image))
        if n_anns == 0:
            print(f"ERROR: No map annotations {image.getName()}!", file=sys.stderr)
            if PROBLEM_NO_A not in problems:
                problems[PROBLEM_NO_A] = []
            problems[PROBLEM_NO_A].append(f"{dataset.getId()} | {dataset.getName()} / {image.getId()} | {image.getName()}")


def check_images(dataset_name, expected_images, batch):
    """
    Check that the dataset contains all expected images
    :param dataset_name: The dataset name
    :param expected_images: The list of expected image names
    :param batch: The batch to which the dataset belongs
    :return: None
    """
    dataset = conn.getObject('Dataset', attributes={'name': dataset_name})
    if dataset is None:
        print(f"WARNING: Could not load dataset {dataset_name} ({batch})!", file=sys.stderr)
        if PROBLEM_NO_DATASET not in problems:
            problems[PROBLEM_NO_DATASET] = []
        problems[PROBLEM_NO_DATASET].append(f"{dataset_name} ({batch})")
        return
    imported_images = set()
    for img in get_image(dataset):
        imported_images.add(img.getName())
    for image in expected_images:
        if image not in imported_images:
            print(f"ERROR: Image {dataset_name} / {image} not imported! ({batch})", file=sys.stderr)
        if PROBLEM_NO_IMAGE not in problems:
            problems[PROBLEM_NO_IMAGE] = []
        problems[PROBLEM_NO_IMAGE].append(f"{dataset_name} / {image} ({batch})")


def main(conn, args):
    print(f"Loading project {PROJECT}", file=sys.stderr)
    project = conn.getObject('Project', attributes={'name': PROJECT})
    total = get_n_datasets(project)
    if args.b or args.m:
        c = 0
        for dataset in get_dataset(project):
            c += 1
            print(f"Checking annotations for {dataset.getName()} ({c}/{total} done)", file=sys.stderr)
            check_annotations(dataset, args.m)

    if args.a:
        c = 0
        image_map, batch_map = parse_assays(args.repo)
        for dataset, images in image_map.items():
            c += 1
            print(f"Checking imports {dataset} ({batch_map[dataset]}) ({c}/{len(image_map)} done)", file=sys.stderr)
            check_images(dataset, images, batch_map[dataset])

    if len(problems) > 0:
        print("\nProblems detected:\n")
        for k,v in problems.items():
            print(f"{k}:")
            for e in v:
                print(e)
            print()


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="HPA sanity check tool (StdErr: Progress information; StdOut: Summary)")
    parser.add_argument('-b', action="store_true", default=False,
                        help='Check that datasets have bulkannotations')
    parser.add_argument('-m', action="store_true", default=False,
                        help='Check that images have map annotations')
    parser.add_argument('-a', action="store_true", default=False,
                        help='Check that all images referenced in the assay.txt files are imported')
    parser.add_argument('--repo', default=REPO, help="Path to the local repository (default: /uod/idr/metadata/idr0043-uhlen-humanproteinatlas)")
    args = parser.parse_args()
    with omero.cli.cli_login() as c:
        conn = omero.gateway.BlitzGateway(client_obj=c.get_client())
        main(conn, args)
