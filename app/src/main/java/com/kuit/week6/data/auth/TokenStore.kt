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

// 파일 하나("auth")에 키-값으로 저장. Context 확장 프로퍼티는 파일 최상단에 1개만.
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

@Singleton
class TokenStore @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val ACCESS = stringPreferencesKey("access")
    private val REFRESH = stringPreferencesKey("refresh")

    // [TODO 1-a] 읽기는 Flow. context.dataStore.data 를 map 해서 키의 값을 꺼낸다.
    val accessToken: Flow<String?> = TODO("context.dataStore.data.map { it[ACCESS] }")
    val refreshToken: Flow<String?> = TODO()

    // [TODO 1-b] 쓰기는 suspend. context.dataStore.edit { } 안에서 두 키를 넣는다.
    suspend fun save(access: String, refresh: String) {
        TODO()
    }

    // [TODO 1-c] 로그아웃: edit { it.clear() }
    suspend fun clear() {
        TODO()
    }
}
