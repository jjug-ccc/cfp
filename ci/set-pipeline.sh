#!/bin/sh
fly -t jjug sp -p jjug-cfp \
    -c `dirname $0`/pipeline.yml \
    -l `dirname $0`/credentials.yml