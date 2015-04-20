cd /storage/brno2/home/souregus/neuro_builds/backprop_fixed/dist/
module add jdk-8

java -jar neurologic.jar -e ../in/muta/examples -r ../in/muta/rules -gr max -s 999999 -rs 1 -le 20 -ls 30 -f 3  # basic max
java -jar neurologic.jar -e ../in/muta/examples -r ../in/muta/rules -gr avg -s 999999 -rs 1 -le 0 -ls 2000 -f 3  # basic avg

java -jar neurologic.jar -e ../in/muta/examples -r ../in/muta/rules -gr max -s 999999 -lr 0.1 -rs 1 -le 20 -ls 30 -f 3  # max increased learn. rate
java -jar neurologic.jar -e ../in/muta/examples -r ../in/muta/rules -gr avg -s 999999 -lr 0.1 -rs 1 -le 0 -ls 1000 -f 3  # avg increased learn. rate

java -jar neurologic.jar -e ../in/muta/examples -r ../in/muta/rules -gr max -s 999999 -lr 0.01 -rs 1 -le 20 -ls 30 -f 3  # max decreased learn. rate
java -jar neurologic.jar -e ../in/muta/examples -r ../in/muta/rules -gr avg -s 999999 -lr 0.01 -rs 1 -le 0 -ls 1000 -f 3  # avg decreased learn. rate

java -jar neurologic.jar -e ../in/muta/examples -r ../in/muta/rules -gr max -s 999999 -rs 3 -le 30 -ls 30 -f 5  # good max
java -jar neurologic.jar -e ../in/muta/examples -r ../in/muta/rules -gr avg -s 999999 -rs 2 -le 0 -ls 5000 -f 5  # good avg