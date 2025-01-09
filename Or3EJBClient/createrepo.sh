#!/bin/bash

describe=`git describe`
echo "${describe}">"ejbModule/META-INF/version.info"
