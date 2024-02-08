#!/bin/bash

src_dir="Sources"
out_dir="out"

mkdir -p "$out_dir"

find "$src_dir" -name "*.java" -exec javac -d "$out_dir" {} +


echo "Compilation and copy completed."
