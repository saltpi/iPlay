#!/bin/sh

curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.7/install.sh | bash
export NVM_DIR="$HOME/.nvm"
[ -s "$NVM_DIR/nvm.sh" ] && \. "$NVM_DIR/nvm.sh"
[ -s "$NVM_DIR/bash_completion" ] && \. "$NVM_DIR/bash_completion"

nvm install 21
nvm use 21
nvm alias default 21

npm install --global yarn
ln -s $(which node) /usr/local/bin/node


brew install cocoapods

# Install JS dependencies
cd /Volumes/workspace/repository && yarn
# Install pods under the ios folder of your react native project
cd /Volumes/workspace/repository/ios && pod install