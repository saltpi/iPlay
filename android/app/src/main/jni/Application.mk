PREFIX64=/Users/ourfor/Code/iPlayClient/android/buildscripts/prefix/arm64
PREFIX32=/Users/ourfor/Code/iPlayClient/android/buildscripts/prefix/armv7l
PREFIX_X64=""
PREFIX_X86=""
APP_ABI :=
ifneq ($(PREFIX32),)
APP_ABI += armeabi-v7a
endif
ifneq ($(PREFIX64),)
APP_ABI += arm64-v8a
endif
ifneq ($(PREFIX_X64),)
APP_ABI += x86_64
endif
ifneq ($(PREFIX_X86),)
APP_ABI += x86
endif

APP_PLATFORM := android-21
APP_STL := c++_shared
