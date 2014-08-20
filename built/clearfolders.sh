#/bin/bash!
echo "Clearing folders..."
find /scratch/metaprot/data/transfer -type f -exec rm -f {} \;
find /scratch/metaprot/data/output -type f -exec rm -f {} \;
echo "Clearing folders finished."