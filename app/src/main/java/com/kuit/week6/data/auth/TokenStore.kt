package com.kuit.week6.data.auth

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

@Singleton
class TokenStore @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val ACCESS = stringPreferencesKey("access")
    private val REFRESH = stringPreferencesKey("refresh")

    // [정답 1-a] 값이 바뀔 때마다 새 값을 방출한다. 미션 B 의 isLoggedIn 이 이 Flow 를 쓴다.
    val accessToken: Flow<String?> = context.dataStore.data.map { it[ACCESS] }
    val refreshToken: Flow<String?> = context.dataStore.data.map { it[REFRESH] }

    // [정답 1-b] edit 는 트랜잭션. 둘 다 바뀌거나 둘 다 안 바뀐다.
    suspend fun save(access: String, refresh: String) {
        context.dataStore.edit { it[ACCESS] = access; it[REFRESH] = refresh }
    }

    // [정답 1-c]
    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}
