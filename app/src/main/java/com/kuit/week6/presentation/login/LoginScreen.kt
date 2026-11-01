package com.kuit.week6.presentation.login

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    state: LoginUiState,
    onIntent: (LoginIntent) -> Unit,
) {
    // 테스트 계정 (https://dummyjson.com/users 의 아무 계정). 입력 중 글자는 화면이 든다.
    var username by remember { mutableStateOf("emilys") }
    var password by remember { mutableStateOf("emilyspass") }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("로그인") })
        }
    ) { padding ->
        Column(
            Modifier.padding(padding).fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            OutlinedTextField(username, { username = it }, label = { Text("username") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(password, { password = it }, label = { Text("password") }, singleLine = true,
                visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
            Button(
                onClick = { onIntent(LoginIntent.Submit(username, password)) },
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth(),
            ) { Text(if (state.isLoading) "로그인 중…" else "로그인") }
            state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            Spacer(Modifier.height(8.dp))
            Text("테스트 계정: emilys / emilyspass", style = MaterialTheme.typography.bodySmall)
        }
    }
}
