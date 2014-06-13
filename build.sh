#/bin/bash
set -e

ant clean
ant dist 
ant test

exit 0;
