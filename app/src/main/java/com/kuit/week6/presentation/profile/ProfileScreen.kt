package com.kuit.week6.presentation.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    state: ProfileUiState,
    onIntent: (ProfileIntent) -> Unit,
    onOpenProducts: () -> Unit,
) {
    LaunchedEffect(Unit) { onIntent(ProfileIntent.Load) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("내 정보") },
                actions = { TextButton(onClick = { onIntent(ProfileIntent.Load) }) { Text("새로고침") } },
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            when {
                state.isLoading && state.user == null -> CircularProgressIndicator()
                state.error != null -> Text(state.error, color = MaterialTheme.colorScheme.error)
            }
            state.user?.let { u ->
                Text(u.name, style = MaterialTheme.typography.headlineSmall)
                Text("@${u.username}", style = MaterialTheme.typography.bodyLarge)
                Text(u.email, style = MaterialTheme.typography.bodyMedium)
            }
            Button(onClick = onOpenProducts, modifier = Modifier.fillMaxWidth()) { Text("상품 보기") }
            Spacer(Modifier.height(8.dp))
            HorizontalDivider()
            Text("실습 확인", style = MaterialTheme.typography.titleMedium)
            Text("① 토큰 망가뜨리기 → ② 새로고침. Logcat 에서 401 → POST /auth/refresh → GET /auth/me 200 순서를 확인.", style = MaterialTheme.typography.bodySmall)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { onIntent(ProfileIntent.BreakToken) }) { Text("토큰 망가뜨리기") }
                OutlinedButton(onClick = { onIntent(ProfileIntent.Logout) }) { Text("로그아웃") }
            }
            state.lastEvent?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
        }
    }
}
