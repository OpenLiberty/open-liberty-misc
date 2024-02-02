#!/bin/bash

if [ $# -ne 2 ]; then
    echo "Exactly 2 arguments are required"
    exit 0
fi

path=$(readlink -f $1) && echo "Input path: $path"
ext=$2                 && echo "Input ext:  $ext"

function repackage() {
    lib=$(dirname $path)      && echo "Extract lib:  $lib"
    file=$(basename $path)    && echo "Extract file: $file"
    name="${file%.*}"         && echo "Extract name: $name"
    
    cp $lib/$file $lib/$name$ext
    ls -la $lib
}

repackage
