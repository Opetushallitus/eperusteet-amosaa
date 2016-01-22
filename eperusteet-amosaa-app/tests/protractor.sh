#!/bin/bash
protractor="./node_modules/protractor"
[[ ! -d $protractor ]] && npm install protractor
tsc
$protractor/bin/webdriver-manager update --standalone
$protractor/bin/webdriver-manager start&
sleep 2
$protractor/bin/protractor conf.js
kill $(lsof -i :4444|awk '{print $2}'|tail -n 1)
wait
