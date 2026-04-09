package com.example.deteccionviolencia.di

import android.content.Context
import androidx.room.Room
import com.example.deteccionviolencia.data.audio.AudioRecorder
import com.example.deteccionviolencia.data.local.dao.AnalysisDao
import com.example.deteccionviolencia.data.local.database.AppDatabase
import com.example.deteccionviolencia.data.remote.api.ClassificationApi
import com.example.deteccionviolencia.data.repository.*
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * Módulo de Hilt que proporciona las dependencias principales a nivel de aplicación
 */

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /** Proporciona la instancia única de la base de datos Room */
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "violence_detection_db"
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    /** Proporciona el DAO para acceder a los datos de análisis de la base de datos local */
    @Provides
    fun provideAnalysisDao(database: AppDatabase): AnalysisDao {
        return database.analysisDao()
    }

    /** Proporciona la instancia de Firebase Firestore */
    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return Firebase.firestore
    }

    /** Proporciona la instancia de Firebase Authentication */
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    /** Proporciona la utilidad para la grabación de audio */
    @Provides
    @Singleton
    fun provideAudioRecorder(@ApplicationContext context: Context): AudioRecorder {
        return AudioRecorder(context)
    }

    /** Configura y proporciona el cliente OkHttp */
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    /** Configura y proporciona la interfaz de la API de clasificación  */
    @Provides
    @Singleton
    fun provideClassificationApi(client: OkHttpClient): ClassificationApi {
        return Retrofit.Builder()
            .baseUrl("https://violencedetectionapp.onrender.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ClassificationApi::class.java)
    }

    /** Proporciona el repositorio para gestionar el historial de análisis  */
    @Provides
    @Singleton
    fun provideHistoryRepository(dao: AnalysisDao): HistoryRepository {
        return RoomHistoryRepository(dao)
    }

    /**  Proporciona el repositorio para la transcripción de audio */
    @Provides
    @Singleton
    fun provideTranscriptionRepository(@ApplicationContext context: Context): TranscriptionRepository {
        return WhisperTranscriptionRepository(context)
    }

    /** Proporciona el repositorio para gestionar el perfil del usuario */
    @Provides
    @Singleton
    fun provideProfileRepository(firestore: FirebaseFirestore): ProfileRepository {
        return FirestoreProfileRepository(firestore)
    }

    /** Proporciona el repositorio para obtener recursos de ayuda  */
    @Provides
    @Singleton
    fun provideResourcesRepository(firestore: FirebaseFirestore): ResourcesRepository {
        return FirestoreResourcesRepository(firestore)
    }

    /** Proporciona el repositorio para realizar la clasificación de audio  */
    @Provides
    @Singleton
    fun provideClassificationRepository(api: ClassificationApi): ClassificationRepository {
        return RemoteClassificationRepository(api)
    }

    /**
     * Proporciona el repositorio para gestionar la autenticación
     */
    @Provides
    @Singleton
    fun provideAuthRepository(auth: FirebaseAuth): AuthRepository {
        return FirebaseAuthRepository(auth)
    }
}
