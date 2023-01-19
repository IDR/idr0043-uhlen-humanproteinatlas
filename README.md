## Tissue-based map of the human proteome

IDR: [idr0043-uhlen-humanproteinatlas/experimentA](https://idr.openmicroscopy.org/webclient/?show=project-501)

### Authors

Mathias Uhlén, Linn Fagerberg, Björn M. Hallström, Cecilia Lindskog, Per Oksvold, Adil Mardinoglu, Åsa Sivertsson, Caroline Kampf, Evelina Sjöstedt, Anna Asplund, IngMarie Olsson, Karolina Edlund, Emma Lundberg, Sanjay Navani, Cristina Al-Khalili Szigyarto, Jacob Odeberg, Dijana Djureinovic, Jenny Ottosson Takanen, Sophia Hober, Tove Alm, Per-Henrik Edqvist, Holger Berling, Hanna Tegel, Jan Mulder, Johan Rockberg, Peter Nilsson, Jochen M. Schwenk, Marica Hamsten, Kalle von Feilitzen, Mattias Forsberg, Lukas Persson, Fredric Johansson, Martin Zwahlen, Gunnar von Heijne, Jens Nielsen, Fredrik Pontén

### Description

Resolving the molecular details of proteome variation in the different tissues and organs of the human body will greatly increase our knowledge of human biology and disease. Here, we present a map of the human tissue proteome based on an integrated omics approach that involves quantitative transcriptomics at the tissue and organ level, combined with tissue microarray–based immunohistochemistry, to achieve spatial localization of proteins down to the single-cell level. Our tissue-based analysis detected more than 90% of the putative protein-coding genes. We used this approach to explore the human secretome, the membrane proteome, the druggable proteome, the cancer proteome, and the metabolic functions in 32 different tissues and organs. All the data are integrated in an interactive Web-based database that allows exploration of individual proteins, as well as navigation of global expression patterns, in all major tissues and organs in the human body.

### About this repository

- The data which was used for importing the images and annotations 
is in `experimentA`, divided into the different batches `hpa_run_xx` as we received the data.
- `scripts/annotations` (especially `IDR0043Workflow.java`): A Java workflow to generate
the filePaths.tsv and annotation.csv for a batch from the assays.txt. **Note: This
got harder and harder to maintain and currently doesn't work any longer without
previous manually editing of the assays.txt. Please use the Python script instead
from batch 13 onwards.**
- `py_scripts` Contains a script to generate the filePaths.tsv and annotation.csv (see previous point);
and a script which performs a sanity check of the whole HPA import .
- `scripts` Contains various utility scripts. Rather historic. The only one
which is still used is `util/need_pyramids.sh` which uses tiffinfo to check 
all tif files to make sure they're a smaller than 3k x 3k pixels.
