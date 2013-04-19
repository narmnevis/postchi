#!/bin/bash

mode="-Dmode=production"
java -classpath $1 $mode -Dpostchi.source=/nobeh/dev/workspaces/narmnevis/ws-narmnevis/all_6pp_short.csv -Dpostchi.cities=/nobeh/dev/workspaces/narmnevis/ws-narmnevis/cities-regions.csv -DcontextPath=postchi com.narmnevis.postchi.rest.PostchiServer

