#!/bin/sh
#
# Copyright © 2015-2021 the original authors.
# Licensed under the Apache License, Version 2.0
#

##############################################################################
# Gradle start up script for UN*X
##############################################################################
APP_NAME="Gradle"
APP_BASE_NAME=$(basename "$0")
APP_HOME=$(cd "$(dirname "$0")" && pwd -P)

MAX_FD=maximum
warn() { echo "$*"; }
die() { echo; echo "$*"; echo; exit 1; }

cygwin=false; msys=false; darwin=false; nonstop=false
case "$(uname)" in
  CYGWIN*)  cygwin=true;;
  Darwin*)  darwin=true;;
  MSYS*|MINGW*) msys=true;;
  NONSTOP*) nonstop=true;;
esac

CLASSPATH="$APP_HOME/gradle/wrapper/gradle-wrapper.jar"

if [ -n "$JAVA_HOME" ] ; then
  JAVACMD="$JAVA_HOME/bin/java"
else
  JAVACMD=java
fi

if ! command -v "$JAVACMD" >/dev/null 2>&1; then
  die "ERROR: JAVA_HOME is not set and no 'java' command could be found."
fi

exec "$JAVACMD" \
  "-classpath" "$CLASSPATH" \
  org.gradle.wrapper.GradleWrapperMain \
  "$@"
