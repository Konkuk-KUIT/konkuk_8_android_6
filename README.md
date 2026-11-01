# KUIT 8기 Android 6주차 실습 — 토큰 인증 (15분)

`git clone` → Android Studio 로 열기 → Sync → `// TODO` 4개를 채운다. 정답은 `answer` 브랜치.

목표: 로그인 → 토큰 저장(DataStore) → 모든 요청에 자동 첨부(Interceptor) → 401 이면 재발급 후 재시도(Authenticator).
`// TODO` 4개. 전부 `data/` 안. 화면 · ViewModel 은 제공 코드이며 토큰을 모른다.

서버: **DummyJSON** (https://dummyjson.com/docs/auth) — 세팅 없음, 실제 JWT 발급 · 만료 · 재발급 동작.
- `POST /auth/login` `{ username, password, expiresInMins }` → `{ accessToken, refreshToken, ... }`
- `GET /auth/me` — `Authorization: Bearer <access>` 필수. 없거나 만료면 **401** `{ "message": "Invalid/Expired Token!" }`
- `POST /auth/refresh` `{ refreshToken }` → 새 토큰 2개. 틀리면 **403**
- 테스트 계정: `emilys` / `emilyspass` (https://dummyjson.com/users 의 아무 계정)
- 실습 코드는 `expiresInMins = 1` 로 로그인한다 → 1분 뒤 401 이 실제로 난다

이 저장소는 실습에 필요한 것만 있다 (Google Books · 북마크 없음). 화면은 Login · Profile 2개.

## 0. 의존성

전부 세팅되어 있다 (Retrofit · Hilt · DataStore). Sync 만 통과하면 된다.

## 1. 파일 구성

```
com/kuit/week6/
├── KuitApp.kt · MainActivity.kt      // @HiltAndroidApp · @AndroidEntryPoint (5주차 완료 상태)
├── App.kt                            // Login → Profile 2화면 (제공)
├── di/
│   ├── NetworkModule.kt              // [TODO 4] OkHttp 에 Interceptor · Authenticator 연결
│   └── RepositoryModule.kt           // AuthRepository @Binds (제공)
├── data/auth/
│   ├── AuthDtos.kt · AuthService.kt  // 요청 · 응답 DTO, login · me · refresh (제공)
│   ├── AuthRepositoryImpl.kt         // 로그인 시 토큰 저장 (제공)
│   ├── TokenStore.kt                 // [TODO 1] DataStore 읽기 · 쓰기 · 삭제
│   ├── AuthInterceptor.kt            // [TODO 2] Authorization 헤더 첨부
│   └── TokenAuthenticator.kt         // [TODO 3] 401 → refresh → 재시도
├── domain/auth/                      // User · AuthRepository · UseCase 4개 (제공)
└── presentation/
    ├── login/                        // LoginScreen · ViewModel · Contract (제공)
    └── profile/                      // ProfileScreen · ViewModel · Contract (제공)
```

`presentation/` · `domain/` 은 손대지 않는다. TODO 는 전부 `data/auth/` 와 `di/NetworkModule.kt` 에 있다.

## 2. 진행 순서 (15분)

| 시간 | 단계 | 파일 | 확인 |
|---|---|---|---|
| 0–4분 | TODO 1: `accessToken` · `refreshToken` Flow, `save`, `clear` | TokenStore.kt | 빌드 통과 |
| 4–7분 | TODO 2: 토큰 있으면 `Authorization: Bearer` 헤더 | AuthInterceptor.kt | — |
| 7–11분 | TODO 3: `priorResponse` 가드 → refresh → 저장 → 새 요청 반환 | TokenAuthenticator.kt | — |
| 11–13분 | TODO 4: OkHttp 에 Interceptor · Authenticator 연결 | di/NetworkModule.kt | 빌드 통과 |
| 13–15분 | 실행: 로그인 → 내 정보 → **토큰 망가뜨리기 → 새로고침** | — | Logcat: `401` → `POST /auth/refresh 200` → `GET /auth/me 200` |

## 3. 완료 조건

1. 로그인 → 내 정보 화면에 이름 · 이메일이 보인다
2. "토큰 망가뜨리기" → "새로고침" 해도 화면이 정상 (사용자는 401 을 못 느낀다). Logcat 에 요청 3개 순서 확인
3. 앱을 완전히 종료 후 재실행해도 DataStore 에 토큰이 남아 있다 (Device Explorer → `data/data/com.kuit.week6/files/datastore/auth.preferences_pb`). 시작 화면 분기는 미션 B
4. 로그아웃 → 내 정보 새로고침 시 401 에러 문구 (토큰 없음)
5. `presentation/` 어디에도 `token` 이라는 단어가 없다

## 4. 막혔을 때

- `Dependency cycle` 에러 → TokenAuthenticator 의 `authService` 타입이 `Provider<AuthService>` 인지 확인
- 로그인은 되는데 내 정보가 401 → TODO 2 에서 헤더를 안 붙였거나, `Bearer ` 뒤 공백 누락
- 토큰 망가뜨린 뒤 새로고침이 401 로 끝남 → TODO 3 에서 새 Request 대신 null 을 반환하고 있음, 또는 OkHttp 에 `.authenticator()` 누락 (TODO 4)
- 401 이 두 번 연속 찍히고 멈춤 → 정상. `priorResponse` 가드가 두 번째를 막은 것. refresh 가 실패했는지 로그 확인
- `NotImplementedError` → TokenStore 의 `TODO()` 가 남아 있음
- Logcat 에 `Authorization: ██` 로 보임 → 정상. `redactHeader` 가 가린 것
- `preferencesDataStore` 를 두 파일에서 선언 → 같은 이름이면 크래시. 파일 하나(TokenStore)에만
