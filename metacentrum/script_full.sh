cd /storage/brno2/home/souregus/neuro_builds/backprop_fixed/dist/
module add jdk-8

java -jar neurologic.jar -e ../in/muta/examples -r ../in/muta/rules -gr max -s 999999 -rs 3 -le 30 -ls 30 -f 5  # good max
java -jar neurologic.jar -e ../in/muta/examples -r ../in/muta/rules -gr avg -s 999999 -rs 2 -le 0 -ls 5000 -f 5  # good avg