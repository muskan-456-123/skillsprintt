package com.example.skilltracker.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Stub FirebaseModule – Firebase providers are disabled.
 * TODO: Re-enable once google-services.json is added and Firebase dependencies are restored.
 */
@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {
    // FirebaseAuth and FirebaseFirestore providers removed temporarily.
    // FirestoreService now uses a no-arg @Inject constructor (no Firebase deps needed).
}
