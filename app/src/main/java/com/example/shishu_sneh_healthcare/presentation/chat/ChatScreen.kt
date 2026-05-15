package com.example.shishu_sneh_healthcare.presentation.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.example.shishu_sneh_healthcare.R
import com.example.shishu_sneh_healthcare.ui.theme.*
import com.example.shishu_sneh_healthcare.util.GeminiHelper
import kotlinx.coroutines.launch

data class Message(val text: String, val isUser: Boolean)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(onBackClick: () -> Unit) {
    val initialMessage = stringResource(R.string.namaste_intro)
    var messages by remember { mutableStateOf(listOf(Message(initialMessage, false))) }
    var inputText by remember { mutableStateOf("") }
    var isTyping by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val suggestedQuestions = listOf(
        stringResource(R.string.feeding_tips_suggest),
        stringResource(R.string.sleep_routine_suggest),
        stringResource(R.string.vaccine_advice_suggest)
    )

    Scaffold(
        containerColor = WarmWhite,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.ask_sneh_ai), fontWeight = FontWeight.Bold, color = TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back), tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = WarmWhite)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(messages) { message ->
                    ChatBubble(message)
                }
                if (isTyping) {
                    item {
                        TypingIndicator()
                    }
                }
            }

            // Suggestions and Input Area
            Column(
                modifier = Modifier
                    .background(Color.White)
                    .padding(top = 8.dp)
            ) {
                // Suggestions
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    suggestedQuestions.forEach { question ->
                        SuggestionChip(
                            onClick = {
                                if (!isTyping) {
                                    val userMsg = question.substring(2) // Remove emoji
                                    messages = messages + Message(userMsg, true)
                                    isTyping = true
                                    scope.launch {
                                        val response = GeminiHelper.getResponse(userMsg)
                                        messages = messages + Message(response, false)
                                        isTyping = false
                                    }
                                }
                            },
                            label = { Text(question, fontSize = 12.sp, color = TextPrimary) },
                            shape = RoundedCornerShape(16.dp)
                        )
                    }
                }

                // Input Area
                Surface(
                    color = Color.White,
                    tonalElevation = 8.dp,
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            placeholder = { Text(stringResource(R.string.type_question_placeholder)) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(24.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color.LightGray.copy(alpha = 0.3f),
                                focusedBorderColor = ButtonCTA
                            ),
                            enabled = !isTyping,
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        FloatingActionButton(
                            onClick = {
                                if (inputText.isNotBlank() && !isTyping) {
                                    val userMsg = inputText
                                    messages = messages + Message(userMsg, true)
                                    inputText = ""
                                    isTyping = true
                                    scope.launch {
                                        val response = try {
                                             GeminiHelper.getResponse(userMsg)
                                        } catch (e: Exception) {
                                             android.util.Log.e("ChatScreen", "API Error", e)
                                             "I'm sorry, I encountered an error. Please try again."
                                        }
                                        messages = messages + Message(response, false)
                                        isTyping = false
                                    }
                                }
                            },
                            containerColor = ButtonCTA,
                            contentColor = Color.White,
                            modifier = Modifier.size(48.dp),
                            shape = CircleShape
                        ) {
                            if (isTyping) {
                                 CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                            } else {
                                 Icon(Icons.AutoMirrored.Filled.Send, contentDescription = stringResource(R.string.send), modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TypingIndicator() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(8.dp)
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(16.dp),
            strokeWidth = 2.dp,
            color = SoftLavender
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = stringResource(R.string.sneh_thinking), style = MaterialTheme.typography.labelSmall, color = TextSecondary)
    }
}

@Composable
fun ChatBubble(message: Message) {
    val arrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    val color = if (message.isUser) SoftLavender.copy(alpha = 0.9f) else Color.White
    val shape = if (message.isUser) {
        RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomStart = 20.dp, bottomEnd = 4.dp)
    } else {
        RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomStart = 4.dp, bottomEnd = 20.dp)
    }
    val avatar = if (message.isUser) "👤" else "👩‍⚕️"

    Row(
        modifier = Modifier.fillMaxWidth(), 
        horizontalArrangement = arrangement,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!message.isUser) {
            Text(text = avatar, fontSize = 24.sp, modifier = Modifier.padding(bottom = 4.dp, end = 4.dp))
        }
        
        Card(
            shape = shape,
            colors = CardDefaults.cardColors(containerColor = color),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.widthIn(max = 260.dp)
        ) {
            Text(
                text = message.text,
                modifier = Modifier.padding(12.dp),
                fontSize = 15.sp,
                color = TextPrimary,
                lineHeight = 20.sp
            )
        }

        if (message.isUser) {
            Text(text = avatar, fontSize = 24.sp, modifier = Modifier.padding(bottom = 4.dp, start = 4.dp))
        }
    }
}
