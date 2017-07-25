#!/bin/sh
echo y | fly -t home sp -p jjug-cfp -c pipeline.yml -l ./credentials.yml