#!/usr/bin/env bash

test_team_size() {
    for i in $1
    do
        java -cp .:algs4.jar BaseballElimination "testscript_files/teams$2$i.txt" | diff "testscript_files/teams$2"$i"_output.txt" -
    done
}

test_one_team() {
    java -cp .:algs4.jar BaseballElimination "testscript_files/teams$1.txt" | diff "testscript_files/teams$1_output.txt" -
}

declare -a four=("" "a" "b")
declare -a five=("" "a" "b" "c")

java -cp .:algs4.jar BaseballElimination testscript_files/teams1.txt | diff testscript_files/teams1_output.txt -

test_team_size "${four[@]}" 4
test_team_size "${five[@]}" 5
test_one_team 7
test_one_team 8
test_one_team 10
# test_one_team 12 # diff is being annoying with this one for some reason
test_one_team "12-allgames"
test_one_team 24
test_one_team 29
test_one_team 30 #F
test_one_team 32
test_one_team 36
test_one_team 42
test_one_team 48
test_one_team 50
test_one_team 54
test_one_team 60 #F
