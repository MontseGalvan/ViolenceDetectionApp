#include <jni.h>
#include <string>
#include <vector>
#include "whisper.h"
#include "ggml-cpu.h"
#include "ggml-backend.h"

/**
 * @file whisper-android.cpp
 * @brief Implementación JNI para la integración de Whisper (GGML) en Android
 *
 * Este archivo conecta la capa de Kotlin y la librería nativa Whisper C++ para la transcripción de audio utilizando modelos GGML
 */

extern "C" {

/**
 * Función que recibe audio desde Kotlin, lo procesa con IA y devuelve el texto
 */
JNIEXPORT jstring JNICALL
Java_com_example_deteccionviolencia_data_repository_WhisperTranscriptionRepository_transcribeNative(
        JNIEnv* env,
        jobject,
        jstring model_path,
        jfloatArray audio_data) {

    // 1. Registro manual del backend
    auto cpu_reg = ggml_backend_cpu_reg();
    ggml_backend_register(cpu_reg);

    // 2. Convertir ruta del modelo de Java String a C++ string
    const char* model_path_cstr = env->GetStringUTFChars(model_path, nullptr);
    std::string model_path_str(model_path_cstr);
    env->ReleaseStringUTFChars(model_path, model_path_cstr);

    // 3. Obtener datos de audio y convertirlos a vector de floats
    jfloat* audio_data_ptr = env->GetFloatArrayElements(audio_data, nullptr);
    jsize audio_data_len = env->GetArrayLength(audio_data);
    std::vector<float> pcmf32(audio_data_ptr, audio_data_ptr + audio_data_len);
    env->ReleaseFloatArrayElements(audio_data, audio_data_ptr, JNI_ABORT);

    // 4. Inicialización del motor whisper
    struct whisper_context_params cparams = whisper_context_default_params();
    cparams.use_gpu = false;

    struct whisper_context *ctx = whisper_init_from_file_with_params(model_path_str.c_str(), cparams);
    if (!ctx) {
        return env->NewStringUTF("Error: El modelo no pudo ser inicializado.");
    }

    // 5. Parámetros de transcripción
    whisper_full_params wparams = whisper_full_default_params(WHISPER_SAMPLING_GREEDY);
    wparams.print_realtime   = false;
    wparams.print_progress   = false;
    wparams.print_timestamps = false;
    wparams.language         = "es";
    wparams.n_threads        = 4;
    wparams.no_context       = true;

    // 6. Ejecución de la trasncripción
    if (whisper_full(ctx, wparams, pcmf32.data(), pcmf32.size()) != 0) {
        whisper_free(ctx);
        return env->NewStringUTF("Error: Fallo en la transcripción.");
    }

    // 7. Recopilar resultado concatenando todos los segmentos detectados
    std::string result;
    int n_segments = whisper_full_n_segments(ctx);
    for (int i = 0; i < n_segments; ++i) {
        const char* text = whisper_full_get_segment_text(ctx, i);
        result += text;
        if (i < n_segments - 1) result += " ";
    }

    // 8. Limpieza y liberación de recursos
    whisper_free(ctx);

    return env->NewStringUTF(result.c_str());
}

}
