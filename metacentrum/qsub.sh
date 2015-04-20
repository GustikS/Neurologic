qsub -l walltime=1d -l mem=10000mb -l scratch=400mb -l nodes=1:ppn=4 script_basic.sh
qsub -l walltime=2d -l mem=10000mb -l scratch=400mb -l nodes=1:ppn=4 script_learnRate.sh
qsub -l walltime=3d -l mem=10000mb -l scratch=400mb -l nodes=1:ppn=4 script_full.sh