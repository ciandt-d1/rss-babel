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
  kubectl create -f ../kubernetes/rc-prd.yaml;
else
  echo "Updating rc and performing rolling update";
  kubectl delete rc rss-babel;
  kubectl create -f ../kubernetes/rc-prd.yaml;
  #kubectl apply -f ../kubernetes/rc-prd.yaml;
  #kubectl rolling-update rss-babel --image=$1;
  if [ $? -ne 0 ]; then
    echo "Could not perform rolling update"
      exit 1;
  fi;
fi
