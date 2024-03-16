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
	cd $(IOS_DIR) && xcodebuild archive \
		-archivePath $(BUILD_DIR)/iPlay \
		-configuration Release \
		-scheme iPlayClient \
		-workspace iPlayClient.xcworkspace \
		-allowProvisioningUpdates \
		CODE_SIGN_IDENTITY="" CODE_SIGNING_REQUIRED=NO | xcpretty
	mkdir -p $(BUILD_DIR)/Release/Payload
	cp -r $(BUILD_DIR)/iPlay.xcarchive/Products/Applications/iPlayClient.app $(BUILD_DIR)/Release/Payload
	cd $(BUILD_DIR)/Release && zip -r $(APP_NAME).ipa Payload
	mv $(BUILD_DIR)/Release/$(APP_NAME).ipa $(BUILD_DIR)/$(APP_NAME).ipa

clean:
	@echo "🧹 clean"
	rm -rf $(BUILD_DIR)
	cd $(ANDROID_DIR) && ./gradlew clean
	# cd $(IOS_DIR) && xcodebuild clean