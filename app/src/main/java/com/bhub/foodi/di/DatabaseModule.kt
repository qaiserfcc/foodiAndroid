package com.bhub.foodi.di

import android.content.Context
import com.bhub.foodi.R
import com.bhub.foodi.data.UserManager
import com.bhub.foodi.utilities.RSA
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.bhub.foodi.networkservice.ApiService
import com.bhub.foodi.networkservice.RetrofitService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {
    @Singleton
    @Provides
    fun provideFirebaseApp(@ApplicationContext context: Context): FirebaseApp {
        var result = FirebaseApp.initializeApp(context)
        while (result == null) {
            result = FirebaseApp.initializeApp(context)
        }
        return result
    }

    @Singleton
    @Provides
    fun provideFirebaseFireStore(): FirebaseFirestore {
        return Firebase.firestore
    }

    @Singleton
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Singleton
    @Provides
    fun provideUserManager(@ApplicationContext context: Context): UserManager {
        return UserManager.getInstance(context)
    }

    @Singleton
    @Provides
    fun provideApiService(@ApplicationContext context: Context): ApiService {
        return RetrofitService().buildRetrofit(context)
    }

    @Singleton
    @Provides
    fun provideGoogleSignInClient(@ApplicationContext context: Context): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(context, gso)
    }

    @Singleton
    @Provides
    fun provideKeyStoreWrapper(): RSA {
        return RSA()
    }
}