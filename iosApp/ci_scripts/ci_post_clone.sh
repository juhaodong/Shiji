#!/usr/bin/env bash
brew install cocoapods
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk install java 19.0.1-tem
export JAVA_OPTS="-Xms6144m -Xmx6144m"

# cd into actual project root
cd ../..
ls
./gradlew :shared:podInstall