PROJECT_ROOT = $(PWD)
BUILD_DIR = $(PROJECT_ROOT)/build
ANDROID_DIR = $(PROJECT_ROOT)/android
IOS_DIR = $(PROJECT_ROOT)/ios
APP_NAME = "iPlay"

all: apk ipa

apk:
	@echo "📦 apk"
	cd $(ANDROID_DIR) && ./gradlew assembleRelease
	mkdir -p $(BUILD_DIR)
	cp $(ANDROID_DIR)/app/build/outputs/apk/release/app-release.apk $(BUILD_DIR)/$(APP_NAME).apk

ipa:
	@echo "📦 ipa"
	mkdir -p $(BUILD_DIR)/Release
	cd $(IOS_DIR) && xcodebuild -workspace iPlayClient.xcworkspace -scheme iPlayClient -configuration Release -archivePath $(BUILD_DIR)/iPlay.xcarchive -allowProvisioningUpdates CODE_SIGN_IDENTITY="" CODE_SIGNING_REQUIRED=NO archive | xcpretty
	cp -r $(BUILD_DIR)/iPlay.xcarchive/Products/Applications/iPlayClient.app $(BUILD_DIR)/Release/Payload
	cd $(BUILD_DIR)/Release && zip -r iPlay.ipa Payload
	mv $(BUILD_DIR)/Release/iPlay.ipa $(BUILD_DIR)/$(APP_NAME).ipa

clean:
	@echo "🧹 clean"
	rm -rf $(BUILD_DIR)
	cd $(ANDROID_DIR) && ./gradlew clean
	# cd $(IOS_DIR) && xcodebuild clean