cmake_minimum_required(VERSION 3.18.1)
project("whispercpp")

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

find_library(log-lib log)
find_library(z-lib z)

set(GGML_VERSION "0.9.7")
set(GGML_COMMIT "unknown")
set(WHISPER_VERSION "1.6.3")

include_directories(${CMAKE_CURRENT_SOURCE_DIR})
include_directories(${CMAKE_CURRENT_SOURCE_DIR}/ggml/src)
include_directories(${CMAKE_CURRENT_SOURCE_DIR}/ggml/include)
include_directories(${CMAKE_CURRENT_SOURCE_DIR}/ggml/src/ggml-cpu)

set(WHISPER_SOURCES
    whisper.cpp
    whisper-android.cpp
    ggml/src/ggml.c
    ggml/src/ggml.cpp
    ggml/src/ggml-alloc.c
    ggml/src/ggml-backend.cpp
    ggml/src/ggml-backend-reg.cpp
    ggml/src/ggml-backend-dl.cpp
    ggml/src/ggml-quants.c
    ggml/src/ggml-threading.cpp
    ggml/src/ggml-cpu/ggml-cpu.c
    ggml/src/ggml-cpu/ggml-cpu.cpp
    ggml/src/ggml-cpu/quants.c
    ggml/src/ggml-cpu/traits.cpp
    ggml/src/ggml-cpu/vec.cpp
    ggml/src/ggml-cpu/ops.cpp
    ggml/src/ggml-cpu/binary-ops.cpp
    ggml/src/ggml-cpu/unary-ops.cpp
    ggml/src/ggml-cpu/repack.cpp
)

if (CMAKE_ANDROID_ARCH_ABI STREQUAL "arm64-v8a" OR CMAKE_ANDROID_ARCH_ABI STREQUAL "armeabi-v7a")
    list(APPEND WHISPER_SOURCES
        ggml/src/ggml-cpu/arch/arm/quants.c
        ggml/src/ggml-cpu/arch/arm/repack.cpp
        ggml/src/ggml-cpu/arch/arm/cpu-feats.cpp
    )
elseif (CMAKE_ANDROID_ARCH_ABI STREQUAL "x86_64" OR CMAKE_ANDROID_ARCH_ABI STREQUAL "x86")
    list(APPEND WHISPER_SOURCES
        ggml/src/ggml-cpu/arch/x86/quants.c
        ggml/src/ggml-cpu/arch/x86/repack.cpp
        ggml/src/ggml-cpu/arch/x86/cpu-feats.cpp
    )
endif()

add_library(whispercpp SHARED ${WHISPER_SOURCES})

target_compile_definitions(whispercpp PRIVATE
    GGML_VERSION="${GGML_VERSION}"
    GGML_COMMIT="${GGML_COMMIT}"
    WHISPER_VERSION="${WHISPER_VERSION}"
)

target_link_libraries(whispercpp ${log-lib} ${z-lib} dl)

target_link_options(whispercpp PRIVATE "-Wl,-z,max-page-size=16384")

if (CMAKE_ANDROID_ARCH_ABI STREQUAL "arm64-v8a")
    target_compile_options(whispercpp PRIVATE -O3 -funroll-loops -fPIC)
elseif (CMAKE_ANDROID_ARCH_ABI STREQUAL "armeabi-v7a")
    target_compile_options(whispercpp PRIVATE -O3 -funroll-loops -fPIC -mfpu=neon)
elseif (CMAKE_ANDROID_ARCH_ABI STREQUAL "x86_64" OR CMAKE_ANDROID_ARCH_ABI STREQUAL "x86")
    target_compile_options(whispercpp PRIVATE -O3 -fPIC)
endif()

target_compile_options(whispercpp PRIVATE -fno-finite-math-only)