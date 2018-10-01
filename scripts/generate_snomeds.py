# Script to get a list of SNOMED codes

import argparse
import pandas

SNOMED_COLUMN = 'Term Source 2 Accession'

parser = argparse.ArgumentParser(description='List unique SNOMED code.')
parser.add_argument('csv_file', type=str,
                    help='The annotation file to check (can be gzipped)')
args = parser.parse_args()


df = pandas.read_csv(args.csv_file)
snomed_codes = set()
for term in df[SNOMED_COLUMN]:
    if ";" in term:
        snomed_codes.update(term.split(';'))
    else:
        snomed_codes.add(term)

print "Found %g unique SNOMED code: %s" % (
    len(snomed_codes), ", ".join(snomed_codes))
