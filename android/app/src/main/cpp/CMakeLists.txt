cmake_minimum_required(VERSION 3.22.1)
project("player")

add_library(${CMAKE_PROJECT_NAME} SHARED
    # List C/C++ source files with relative paths to this CMakeLists.txt.
        Player.cpp)

include_directories(headers)

find_library(MPV_LIB_PATH
             mpv
             PATHS ${CMAKE_SOURCE_DIR}/../jniLibs/${ANDROID_ABI}
             NO_CMAKE_FIND_ROOT_PATH)

set(AVCODEC_LIB_PATH ${CMAKE_SOURCE_DIR}/../jniLibs/${ANDROID_ABI}/libavcodec.so)

target_link_libraries(${CMAKE_PROJECT_NAME}
    android
    log
    GLESv3
    EGL
    atomic
    ${MPV_LIB_PATH}
    ${AVCODEC_LIB_PATH}
)
