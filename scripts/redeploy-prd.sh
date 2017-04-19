#!/usr/bin/env bash

bash ./prod.sh

ECHO Deploying image $1

if [ $# -eq 0 ]
  then
    echo "You must inform the tag to be deployed"
    exit 1
fi

kubectl get rc rss-babel > /dev/null 2>&1;
if [ $? -ne 0 ]; then
  echo "Replication controller does not exist, creating.";
  kubectl create -f ../deploy/rc-prd.yaml;
else
  echo "Updating rc and performing rolling update";
  kubectl delete rc rss-babel;
  kubectl create -f ../deploy/rc-prd.yaml;
  if [ $? -ne 0 ]; then
    echo "Could not perform rolling update"
      exit 1;
  fi;
fi
