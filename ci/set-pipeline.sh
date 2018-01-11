#!/bin/sh
fly -t jjug sp -p jjug-cfp -c pipeline.yml -l ./credentials.yml
