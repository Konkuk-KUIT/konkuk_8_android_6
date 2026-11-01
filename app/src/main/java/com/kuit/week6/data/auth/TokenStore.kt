package com.kuit.week6.data.auth

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
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
    //            힌트: context.dataStore.data.map { it[ACCESS] }
    //            (flowOf(null) 은 "토큰 없음" 을 흘려보내는 임시값. 지금은 로그인해도 저장되지 않는다)
    val accessToken: Flow<String?> = flowOf(null)
    val refreshToken: Flow<String?> = flowOf(null)

    // [TODO 1-b] 쓰기는 suspend. context.dataStore.edit { } 안에서 두 키를 넣는다.
    suspend fun save(access: String, refresh: String) {
    }

    // [TODO 1-c] 로그아웃: edit { it.clear() }
    suspend fun clear() {
    }
}
