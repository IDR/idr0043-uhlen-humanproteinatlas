# Script to parse an annotation file and get a list of SNOMED codes as
# well as organism parts

import argparse
import pandas
import os.path

ORGANISM_PART_COLUMN = 'Characteristics [Organism Part]'
SNOMED_COLUMN = 'Term Source 2 Accession'

parser = argparse.ArgumentParser(
    description='Parse an HPA CSV file and extract disease name/accession'
    ' nodes.')
parser.add_argument('csv_file', type=str,
                    help='The annotation file to check (can be gzipped)')
args = parser.parse_args()


df = pandas.read_csv(args.csv_file)
snomed_codes = set()
terms = set()
for term in df[SNOMED_COLUMN]:
    if ";" in term:
        snomed_codes.update(term.split(';'))
    else:
        snomed_codes.add(term)

for term in df[ORGANISM_PART_COLUMN]:
    terms.add(term)

directory = os.path.dirname(args.csv_file)

print "Found %g unique SNOMED codes under %s" % (
    len(snomed_codes), SNOMED_COLUMN)
with open(os.path.join(directory, 'organism_parts.tsv'), 'w') as f:
    f.write('SNOMED Accession\tTerm\n')
    for code in snomed_codes:
        f.write('%s\t\n' % code)

print "Found %g unique terms under %s" % (len(terms), ORGANISM_PART_COLUMN)
with open(os.path.join(directory, 'diseases.tsv'), 'w') as f:
    f.write('SNOMED Accession\tTerm\n')
    for term in terms:
        f.write('\t%s\n' % term)
