PROJECT_ROOT 	= $(PWD)
BUILD_DIR 		= $(PROJECT_ROOT)/build
ANDROID_DIR 	= $(PROJECT_ROOT)/android
IOS_DIR 		= $(PROJECT_ROOT)/ios
APP_NAME 		= iPlay
VERSION			= v1.0
BUILD_ID 		= $(shell git rev-parse --short HEAD)
VERSION_NAME    = "$(VERSION) $(BUILD_ID)"

all: apk ipa

version:
	@echo $(VERSION_NAME)

apk:
	@echo "📦 apk $(VERSION_NAME)"
	cd $(ANDROID_DIR) && ./gradlew assembleRelease -PversionName=$(VERSION_NAME)
	mkdir -p $(BUILD_DIR)
	cp $(ANDROID_DIR)/app/build/outputs/apk/release/app-release.apk $(BUILD_DIR)/$(APP_NAME).apk

ipa:
	@echo "📦 ipa $(VERSION_NAME)"
	cd $(IOS_DIR) && xcodebuild archive \
		-archivePath $(BUILD_DIR)/iPlay \
		-configuration Release \
		-scheme iPlayClient \
		-sdk iphoneos \
		-workspace iPlayClient.xcworkspace \
		-allowProvisioningUpdates \
		CODE_SIGN_IDENTITY="" CODE_SIGNING_REQUIRED=NO MARKETING_VERSION=$(VERSION_NAME) | xcpretty
	mkdir -p $(BUILD_DIR)/Release/Payload
	cp -r $(BUILD_DIR)/iPlay.xcarchive/Products/Applications/iPlayClient.app $(BUILD_DIR)/Release/Payload
	cp -r $(BUILD_DIR)/iPlay.xcarchive/dSYMs $(BUILD_DIR)/Release/dSYMs
	cd $(BUILD_DIR)/Release && zip -r $(APP_NAME).ipa Payload
	cd $(BUILD_DIR)/Release && zip -r $(APP_NAME).dSYMs.zip dSYMs 
	mv $(BUILD_DIR)/Release/$(APP_NAME).ipa $(BUILD_DIR)/$(APP_NAME).ipa
	mv $(BUILD_DIR)/Release/$(APP_NAME).dSYMs.zip $(BUILD_DIR)/$(APP_NAME).dSYMs.zip

dmg:
	@echo "📦 ipa $(VERSION_NAME)"
	cd $(IOS_DIR) && xcodebuild archive \
		-archivePath $(BUILD_DIR)/iPlay \
		-configuration Release \
		-scheme iPlayClient \
		-sdk macosx \
		-workspace iPlayClient.xcworkspace \
		-allowProvisioningUpdates \
		CODE_SIGN_IDENTITY="" CODE_SIGNING_REQUIRED=NO MARKETING_VERSION=$(VERSION_NAME) | xcpretty
	mkdir -p $(BUILD_DIR)/Release/Payload
	cp -r $(BUILD_DIR)/iPlay.xcarchive/Products/Applications/iPlayClient.app $(BUILD_DIR)/Release/Payload
	cp -r $(BUILD_DIR)/iPlay.xcarchive/dSYMs $(BUILD_DIR)/Release/dSYMs
	cd $(BUILD_DIR)/Release && zip -r $(APP_NAME).ipa Payload
	cd $(BUILD_DIR)/Release && zip -r $(APP_NAME).dSYMs.zip dSYMs 
	mv $(BUILD_DIR)/Release/$(APP_NAME).ipa $(BUILD_DIR)/$(APP_NAME).ipa
	mv $(BUILD_DIR)/Release/$(APP_NAME).dSYMs.zip $(BUILD_DIR)/$(APP_NAME).dSYMs.zip

clean:
	@echo "🧹 clean"
	rm -rf $(BUILD_DIR)
	cd $(ANDROID_DIR) && ./gradlew clean
	# cd $(IOS_DIR) && xcodebuild clean