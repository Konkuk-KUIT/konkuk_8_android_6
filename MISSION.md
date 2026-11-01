# 6주차 미션 — 페이징 · 로그인 상태 유지 · 응답 래퍼

실습(토큰 인증)을 끝낸 코드에 이어서 한다. (필수)는 꼭, (도전)은 시간이 되면.

| 미션 | 주제 | 바뀌는 파일 |
|---|---|---|
| A 상품 목록 페이징 | 페이징 (offset) + Interceptor 실사용 | 신규 data/product, domain/product, presentation/products, App.kt, NetworkModule |
| B 로그인 상태 유지 | DataStore Flow + 시작 화면 분기 | App.kt, 신규 presentation/splash |
| C 응답 래퍼 (도전) | ApiResponse\<T\> · unwrap | 신규 data/ApiResponse.kt + Fake |
| D 상품 검색 (도전) | flatMapLatest + 페이징 초기화 | products/ |

## 0. 준비

- [ ] 브랜치 `본인GitHub아이디/week6`
- [ ] 실습 TODO 1~4 완료, "토큰 망가뜨리기 → 새로고침" 이 정상 동작 (week6-answer 로 맞춰도 됨)

## A. 상품 목록 페이징 (필수)

DummyJSON 의 **인증 필요** 목록 API 를 쓴다. 토큰이 없으면 401 → 실습의 Interceptor · Authenticator 가 그대로 동작해야 한다.

- `GET /auth/products?limit=20&skip=0` → `{ "products": [ { id, title, brand, price, thumbnail, ... } ], "total": 194, "skip": 0, "limit": 20 }`
- 서버가 `hasNext` 를 주지 않는다 → `skip + products.size < total` 로 계산한다

체크리스트

- [ ] `data/product/ProductDtos.kt` — `ProductsPage(products, total, skip, limit)`, `ProductDto(id, title, brand?, price, thumbnail)`. `@Serializable`, `ignoreUnknownKeys` 는 이미 켜져 있음
- [ ] `data/product/ProductService.kt` — `@GET("auth/products") suspend fun getProducts(@Query("limit") limit: Int = 20, @Query("skip") skip: Int): ProductsPage`. `NetworkModule` 에 `@Provides fun provideProductService(retrofit: Retrofit)` 추가
- [ ] `domain/product/Product.kt`, `ProductRepository` — `suspend fun getProducts(page: Int): ProductPage` (`ProductPage(items, hasNext)`). Impl 에서 `skip = page * 20`, `hasNext` 계산
- [ ] `presentation/products/ProductsContract.kt` — `ProductsUiState(items, page, hasNext = true, isLoadingMore, isLoading, error)`, `ProductsIntent.LoadMore / Retry`
- [ ] `ProductsViewModel` — `loadMore()` 에 가드 2개 (`isLoadingMore || !hasNext` → return). 성공 시 `items + 새 목록`, `page + 1`, `hasNext` 갱신. `init` 에서 첫 페이지
- [ ] `ProductsScreen` — `rememberLazyListState` + `derivedStateOf { 마지막 보이는 index >= items.size - 3 }` + `LaunchedEffect(reachedEnd)` → `LoadMore`. 목록 끝에 `isLoadingMore` 일 때 `CircularProgressIndicator` 셀
- [ ] `App.kt` — `Products` 키 추가. Profile 화면에 "상품 보기" 버튼
- [ ] 확인: 끝까지 스크롤하면 20개씩 붙고 194개에서 멈춘다 (더 요청 안 함). 비행기 모드에서 `LoadMore` → 에러 문구 + 재시도, 중복 아이템 없음
- [ ] 확인: 로그아웃 상태에서 상품 화면 진입 → 401 → 에러 문구 (크래시 없음)

> `LazyColumn(items(..., key = { it.id }))` 로 key 를 주면 중복이 생겼을 때 바로 크래시로 알 수 있다. 일부러 켜 둘 것.

## B. 로그인 상태 유지 (필수)

지금은 앱을 다시 켜면 토큰이 남아 있어도 항상 Login 화면부터 시작한다.

- [ ] `presentation/splash/SplashViewModel` — `AuthRepository.isLoggedIn: Flow<Boolean>` 을 `stateIn` 으로 노출 (초기값 `null` = 아직 모름)
- [ ] `App.kt` — 시작 키를 `Splash` 로. 값이 결정되면 `true` → Profile, `false` → Login 으로 교체 (`backStack.clear()` 후 `add`)
- [ ] Profile 로그아웃 → `tokenStore.clear()` → `isLoggedIn` 이 `false` 방출. 이 Flow 하나로 로그아웃 이동까지 처리할 수 있는지 시도 (`ProfileUiState.loggedOut` 제거)
- [ ] 확인: 로그인 → 앱 강제 종료 → 재실행 → Profile 이 바로 뜬다. 로그아웃 → 재실행 → Login

## C. 도전 — 응답 래퍼

DummyJSON 에는 봉투가 없다. 서버 파트 규약(`{ isSuccess, code, message, result }`) 을 코드로만 준비해 둔다.

- [ ] `data/ApiResponse.kt` — `@Serializable data class ApiResponse<T>(isSuccess, code, message, result: T? = null)`, `class ApiException(val code: Int, message: String) : Exception(message)`, `fun <T> ApiResponse<T>.unwrap(): T`
- [ ] `presentation/UserMessage.kt` 에 `fun Throwable.toUserMessage(): String` — `IOException` · `HttpException` · `ApiException(code 별)` 분기. Login · Profile ViewModel 의 catch 블록을 이 함수로 통일
- [ ] 테스트: `FakeWrappedService` 를 만들어 `ApiResponse(isSuccess = false, code = 2001, ...)` 를 돌려주게 하고, Repository 가 `ApiException(2001)` 을 던지는지 단위 테스트 1개 (`app/src/test`)
- [ ] 워크북에: "200 인데 isSuccess=false 인 응답을 unwrap 없이 쓰면 어디서 어떤 크래시가 나는가" 1줄

## D. 도전 — 상품 검색

- [ ] `GET /auth/products/search?q=phone&limit=20&skip=0` (응답 모양 동일)
- [ ] `ProductsUiState.query`, `ProductsIntent.Search(query)`. 검색어가 바뀌면 `items` 비우고 `page = 0`, `hasNext = true` 로 초기화한 뒤 첫 페이지
- [ ] `MutableStateFlow<String>` + `flatMapLatest` 로 구현하면 이전 검색의 응답이 늦게 와도 덮어쓰지 않는다. 왜 그런지 워크북에 2줄

## 완료 조건

1. 로그인 → 상품 목록 끝까지 스크롤 → 194개 → 뒤로 → 로그아웃 → 앱 재실행, 전 구간 크래시 없음
2. 상품 목록에서 "토큰 망가뜨리기" 후 스크롤해도 목록이 계속 붙는다 (Authenticator 가 조용히 처리)
3. `presentation/` 에 `token` · `Bearer` 가 없다
4. `page` · `hasNext` · `isLoadingMore` 가 전부 UiState 에 있다 (화면 `remember` 에 없다)
