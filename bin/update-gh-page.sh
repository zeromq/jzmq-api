#!/bin/bash

git checkout gh-pages
rm -r javadocs
cp -r target/apidocs javadocs
git add -u javadocs
git add javadocs
git commit -m "Updating javadocs"
git push origin gh-pages
git checkout master
