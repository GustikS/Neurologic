cd /storage/brno2/home/souregus/neuro_builds/backprop_fixed/dist/
module add jdk-8

java -jar neurologic.jar -e ../in/muta/examples -r ../in/muta/rules -gr max -s 999999 -rs 1 -le 20 -ls 30 -f 3  # basic max
java -jar neurologic.jar -e ../in/muta/examples -r ../in/muta/rules -gr avg -s 999999 -rs 1 -le 0 -ls 2000 -f 3  # basic avg
