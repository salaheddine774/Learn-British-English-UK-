package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.platform.LocalContext
import android.speech.tts.TextToSpeech
import com.example.viewmodel.AccentWord
import com.example.viewmodel.AccentFeedback
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.*
import com.example.viewmodel.BritishLearningViewModel

// Palette: Royal Navy Dark Theme
val NavyDeep = Color(0xFF0F172A)
val CharcoalSlate = Color(0xFF1E293B)
val GoldenAmber = Color(0xFFF59E0B)
val LightSlate = Color(0xFF94A3B8)
val PlatinumClean = Color(0xFFF8FAFC)
val AccentCrimson = Color(0xFFE11D48)
val BritishBlue = Color(0xFF2563EB)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BritishLearningApp(
    viewModel: BritishLearningViewModel,
    modifier: Modifier = Modifier
) {
    val activeTab by viewModel.activeTab.collectAsStateWithLifecycle()
    val userProgress by viewModel.userProgress.collectAsStateWithLifecycle()
    val activeExplainWord by viewModel.activeExplainWord.collectAsStateWithLifecycle()
    val showAuthScreen by viewModel.showAuthScreen.collectAsStateWithLifecycle()
    val currentUserAccount by viewModel.currentUserAccount.collectAsStateWithLifecycle()

    if (showAuthScreen) {
        AuthLoadingOrScreen(viewModel, modifier)
    } else {
        Scaffold(
            modifier = modifier.testTag("british_learning_scaffold"),
            containerColor = NavyDeep,
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth().padding(end = 16.dp)
                        ) {
                            Column {
                                Text(
                                    text = "Speak British AI",
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PlatinumClean,
                                    fontFamily = FontFamily.SansSerif
                                )
                                Text(
                                    text = "مُعلمك البريطاني الخاص",
                                    fontSize = 11.sp,
                                    color = LightSlate
                                )
                            }
                            // Stats Badges & Profile
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Profile / Sign-out Button
                                var showProfileDialog by remember { mutableStateOf(false) }
                                Box {
                                    Row(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(GoldenAmber.copy(alpha = 0.15f))
                                            .border(1.dp, GoldenAmber.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                            .clickable { showProfileDialog = true }
                                            .padding(horizontal = 8.dp, vertical = 5.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("👤", fontSize = 11.sp)
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = currentUserAccount?.username ?: "Guest",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GoldenAmber,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.widthIn(max = 60.dp)
                                        )
                                    }

                                    if (showProfileDialog) {
                                        Dialog(onDismissRequest = { showProfileDialog = false }) {
                                            Card(
                                                colors = CardDefaults.cardColors(containerColor = CharcoalSlate),
                                                shape = RoundedCornerShape(16.dp),
                                                modifier = Modifier.fillMaxWidth().padding(16.dp)
                                            ) {
                                                Column(
                                                    modifier = Modifier.padding(16.dp),
                                                    horizontalAlignment = Alignment.CenterHorizontally,
                                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                                ) {
                                                    Text(
                                                        text = "الملف الشخصي / User Profile 🇬🇧",
                                                        fontSize = 15.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = GoldenAmber
                                                    )
                                                    Divider(color = LightSlate.copy(alpha = 0.2f))
                                                    
                                                    if (currentUserAccount != null) {
                                                        Text("اسم المستخدم: ${currentUserAccount?.username}", fontSize = 12.sp, color = PlatinumClean)
                                                        Text("البريد الإلكتروني: ${currentUserAccount?.email}", fontSize = 11.sp, color = LightSlate)
                                                        Text("مجموع النقاط: ${currentUserAccount?.xpPoints} XP", fontSize = 14.sp, color = GoldenAmber, fontWeight = FontWeight.Bold)
                                                        Text("الأيام المتتالية: ${currentUserAccount?.streakDays} Days", fontSize = 14.sp, color = BritishBlue, fontWeight = FontWeight.Bold)
                                                        
                                                        Spacer(modifier = Modifier.height(10.dp))
                                                        
                                                        Button(
                                                            onClick = {
                                                                showProfileDialog = false
                                                                viewModel.logout()
                                                            },
                                                            colors = ButtonDefaults.buttonColors(containerColor = AccentCrimson),
                                                            modifier = Modifier.fillMaxWidth().height(38.dp)
                                                        ) {
                                                            Text("تسجيل الخروج / Sign Out 👤", fontSize = 11.sp, color = PlatinumClean)
                                                        }
                                                    } else {
                                                        Text("أنت مسجل حالياً كزائر. قم بإنشاء حساب لحفظ نقاطك وتقدّمك ومستوياتك البريطانية إلى الأبد!", fontSize = 11.sp, color = PlatinumClean, textAlign = TextAlign.Center)
                                                        Spacer(modifier = Modifier.height(10.dp))
                                                        Button(
                                                            onClick = {
                                                                showProfileDialog = false
                                                                viewModel.showAuthScreen.value = true
                                                            },
                                                            colors = ButtonDefaults.buttonColors(containerColor = GoldenAmber),
                                                            modifier = Modifier.fillMaxWidth().height(38.dp)
                                                        ) {
                                                            Text("إنشاء حساب أو تسجيل الدخول", fontSize = 11.sp, color = NavyDeep, fontWeight = FontWeight.Bold)
                                                        }
                                                    }
                                                    
                                                    TextButton(onClick = { showProfileDialog = false }) {
                                                        Text("إغلاق / Close", fontSize = 11.sp, color = LightSlate)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                // Streak badge
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(GoldenAmber.copy(alpha = 0.2f))
                                        .border(1.dp, GoldenAmber, RoundedCornerShape(12.dp))
                                        .padding(horizontal = 8.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = "Streak",
                                        tint = GoldenAmber,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "${userProgress?.streakDays ?: 0} Days",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldenAmber
                                    )
                                }
                                // XP Badge
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(BritishBlue.copy(alpha = 0.2f))
                                        .border(1.dp, BritishBlue, RoundedCornerShape(12.dp))
                                        .padding(horizontal = 8.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "🇬🇧 ${userProgress?.xpPoints ?: 0} XP",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BritishBlue
                                    )
                                }
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = CharcoalSlate)
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = CharcoalSlate,
                    tonalElevation = 8.dp,
                    modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
                ) {
                    NavigationBarItem(
                        selected = activeTab == "lessons",
                        onClick = { viewModel.activeTab.value = "lessons" },
                        label = { Text("Curriculum", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Curriculum") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NavyDeep,
                            selectedTextColor = GoldenAmber,
                            indicatorColor = GoldenAmber,
                            unselectedIconColor = LightSlate,
                            unselectedTextColor = LightSlate
                        ),
                        modifier = Modifier.testTag("nav_item_lessons")
                    )
                    NavigationBarItem(
                        selected = activeTab == "chat",
                        onClick = { viewModel.activeTab.value = "chat" },
                        label = { Text("AI Tutor", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        icon = { Icon(Icons.Default.Face, contentDescription = "AI Tutor") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NavyDeep,
                            selectedTextColor = GoldenAmber,
                            indicatorColor = GoldenAmber,
                            unselectedIconColor = LightSlate,
                            unselectedTextColor = LightSlate
                        ),
                        modifier = Modifier.testTag("nav_item_chat")
                    )
                    NavigationBarItem(
                        selected = activeTab == "money",
                        onClick = { viewModel.activeTab.value = "money" },
                        label = { Text("UK Life & £", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        icon = { Icon(Icons.Default.Star, contentDescription = "UK Life & Money") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NavyDeep,
                            selectedTextColor = GoldenAmber,
                            indicatorColor = GoldenAmber,
                            unselectedIconColor = LightSlate,
                            unselectedTextColor = LightSlate
                        ),
                        modifier = Modifier.testTag("nav_item_money")
                    )
                    NavigationBarItem(
                        selected = activeTab == "dictionary",
                        onClick = { viewModel.activeTab.value = "dictionary" },
                        label = { Text("Glossary", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        icon = { Icon(Icons.Default.Favorite, contentDescription = "Glossary") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NavyDeep,
                            selectedTextColor = GoldenAmber,
                            indicatorColor = GoldenAmber,
                            unselectedIconColor = LightSlate,
                            unselectedTextColor = LightSlate
                        ),
                        modifier = Modifier.testTag("nav_item_dictionary")
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (activeTab) {
                    "lessons" -> LessonsScreen(viewModel)
                    "chat" -> ChatScreen(viewModel)
                    "money" -> MoneyLifeScreen(viewModel)
                    "dictionary" -> DictionaryScreen(viewModel)
                }

                // Word Explanation Dialog
                activeExplainWord?.let { word ->
                    WordExplanationDialog(
                        word = word,
                        viewModel = viewModel,
                        onDismiss = { viewModel.activeExplainWord.value = null }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthLoadingOrScreen(viewModel: BritishLearningViewModel, modifier: Modifier = Modifier) {
    val isAuthLoading by viewModel.isAuthLoading.collectAsStateWithLifecycle()
    val authErrorMessage by viewModel.authErrorMessage.collectAsStateWithLifecycle()

    var isRegisterMode by remember { mutableStateOf(false) }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(NavyDeep)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 450.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // App Branding & Decorative Badge
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(GoldenAmber.copy(alpha = 0.15f))
                        .border(2.dp, GoldenAmber, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🇬🇧", fontSize = 48.sp)
                }
                Text(
                    text = "Speak British AI",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = PlatinumClean
                )
                Text(
                    text = "بوابة تعلّم اللهجة البريطانية العريقة",
                    fontSize = 13.sp,
                    color = LightSlate,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Navigation selector: Log In vs Register
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(CharcoalSlate)
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1.5f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (!isRegisterMode) GoldenAmber else Color.Transparent)
                        .clickable { isRegisterMode = false }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "تسجيل الدخول / Log In",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (!isRegisterMode) NavyDeep else LightSlate
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1.5f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isRegisterMode) GoldenAmber else Color.Transparent)
                        .clickable { isRegisterMode = true }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "حساب جديد / Register",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isRegisterMode) NavyDeep else LightSlate
                    )
                }
            }

            // Input Fields Card
            Card(
                colors = CardDefaults.cardColors(containerColor = CharcoalSlate),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Username ID Input
                    Text(
                        text = "اسم المستخدم (خط إنجليزي) / Username",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldenAmber
                    )
                    TextField(
                        value = username,
                        onValueChange = { username = it },
                        placeholder = { Text("مثال: ahmed_uk", color = LightSlate, fontSize = 13.sp) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = NavyDeep,
                            unfocusedContainerColor = NavyDeep,
                            focusedTextColor = PlatinumClean,
                            unfocusedTextColor = PlatinumClean,
                            focusedIndicatorColor = GoldenAmber
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).testTag("auth_username_input")
                    )

                    // Email Input (only if RegisterMode)
                    if (isRegisterMode) {
                        Text(
                            text = "البريد الإلكتروني / Email Address",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldenAmber
                        )
                        TextField(
                            value = email,
                            onValueChange = { email = it },
                            placeholder = { Text("email@example.com", color = LightSlate, fontSize = 13.sp) },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = NavyDeep,
                                unfocusedContainerColor = NavyDeep,
                                focusedTextColor = PlatinumClean,
                                unfocusedTextColor = PlatinumClean,
                                focusedIndicatorColor = GoldenAmber
                            ),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).testTag("auth_email_input")
                        )
                    }

                    // Password Input
                    Text(
                        text = "كلمة المرور / Password",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldenAmber
                    )
                    TextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = { Text("••••••••", color = LightSlate, fontSize = 13.sp) },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val iconText = if (passwordVisible) "👁️" else "🔒"
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Text(iconText, fontSize = 14.sp)
                            }
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = NavyDeep,
                            unfocusedContainerColor = NavyDeep,
                            focusedTextColor = PlatinumClean,
                            unfocusedTextColor = PlatinumClean,
                            focusedIndicatorColor = GoldenAmber
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).testTag("auth_password_input")
                    )
                }
            }

            // Error display
            authErrorMessage?.let { errMsg ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(AccentCrimson.copy(alpha = 0.2f))
                        .border(1.dp, AccentCrimson, RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = errMsg,
                        fontSize = 12.sp,
                        color = AccentCrimson,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Main Submission CTA
            Button(
                onClick = {
                    if (isRegisterMode) {
                        viewModel.registerUser(username, email, password)
                    } else {
                        viewModel.loginUser(username, password)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldenAmber),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("auth_submit_btn"),
                enabled = !isAuthLoading
            ) {
                if (isAuthLoading) {
                    CircularProgressIndicator(color = NavyDeep, modifier = Modifier.size(20.dp))
                } else {
                    Text(
                        text = if (isRegisterMode) "إنشاء الحساب والبدء / Create Account" else "دخول الحساب / Log In",
                        color = NavyDeep,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            // Guest Continue bypass
            Row(
                modifier = Modifier
                    .clickable { viewModel.skipAuth() }
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "متابعة كزائر لحين إنشاء حساب 👤 Continuation as Guest",
                    fontSize = 11.sp,
                    color = LightSlate,
                    textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "💡 تسجيل الدخول يحفظ تقدّم مستواك ونقاط الـ XP ونشاط الأيام المتتالية.",
                fontSize = 11.sp,
                color = LightSlate.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
        }
    }
}

// ================= 1. LESSONS / CURRICULUM =================

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun LessonsScreen(viewModel: BritishLearningViewModel) {
    val selectedLesson by viewModel.selectedLesson.collectAsStateWithLifecycle()

    AnimatedContent(
        targetState = selectedLesson,
        transitionSpec = {
            slideInHorizontally { it } with slideOutHorizontally { -it }
        },
        label = "lessons_nav"
    ) { lesson ->
        if (lesson == null) {
            LessonList(viewModel)
        } else {
            LessonDetailView(lesson, viewModel)
        }
    }
}

@Composable
fun LessonList(viewModel: BritishLearningViewModel) {
    val lessonsList by viewModel.filteredLessons.collectAsStateWithLifecycle()
    val searchQuery by viewModel.dictionarySearchQuery.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Welcome banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Brush.horizontalGradient(listOf(BritishBlue, CharcoalSlate)))
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = "Learn Proper British English! 🇬🇧",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = PlatinumClean
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "تعلّم الإنجليزية بمذاقها البريطاني ولهجة الشارع اللندني من الصفر للاحتراف بأسلوب سهل وباللغة العربية.",
                    fontSize = 12.sp,
                    color = LightSlate,
                    lineHeight = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search bar
        TextField(
            value = searchQuery,
            onValueChange = { viewModel.dictionarySearchQuery.value = it },
            placeholder = { Text("البحث في الدروس والقاموس...", color = LightSlate) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = LightSlate) },
            trailingIcon = if (searchQuery.isNotEmpty()) {
                {
                    IconButton(onClick = { viewModel.dictionarySearchQuery.value = "" }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear search", tint = LightSlate)
                    }
                }
            } else null,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .testTag("curriculum_search_tf"),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = CharcoalSlate,
                unfocusedContainerColor = CharcoalSlate,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = PlatinumClean,
                unfocusedTextColor = PlatinumClean
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "تصنيفات المنهج الدراسي / Curriculum Units",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = PlatinumClean,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize().testTag("lessons_lazy_column")
        ) {
            items(lessonsList) { item ->
                LessonCard(item) {
                    viewModel.selectedLesson.value = item
                    viewModel.activeQuizIndex.value = 0
                    viewModel.isQuizAnswered.value = false
                    viewModel.selectedQuizAnswer.value = null
                    viewModel.quizScore.value = 0
                }
            }
        }
    }
}

@Composable
fun LessonCard(item: LessonItem, onClick: () -> Unit) {
    val categoryColor = when (item.category) {
        "Grammar" -> BritishBlue
        "Slang" -> GoldenAmber
        "Texting & Chat" -> Color(0xFF8B5CF6)
        "Money & Life" -> AccentCrimson
        else -> Color(0xFF14B8A6)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("lesson_card_${item.id}"),
        colors = CardDefaults.cardColors(containerColor = CharcoalSlate),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(categoryColor)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = item.category.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = categoryColor
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = PlatinumClean
                )
                Text(
                    text = item.arabicTitle,
                    fontSize = 12.sp,
                    color = LightSlate,
                    fontFamily = FontFamily.SansSerif
                )
            }

            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Start Lesson",
                tint = GoldenAmber,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun LessonDetailView(lesson: LessonItem, viewModel: BritishLearningViewModel) {
    var activeSubTab by remember { mutableStateOf("rules") } // rules, vocab, quiz

    Column(modifier = Modifier.fillMaxSize()) {
        // Hero bar with back navigation
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(CharcoalSlate)
                .padding(horizontal = 4.dp, vertical = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = { viewModel.selectedLesson.value = null },
                    modifier = Modifier.testTag("lesson_back_button")
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back to list", tint = PlatinumClean)
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = lesson.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = PlatinumClean,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = lesson.arabicTitle,
                        fontSize = 11.sp,
                        color = GoldenAmber,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        // Tab Row switchers (Rules, Words, Quiz Workout)
        TabRow(
            selectedTabIndex = when (activeSubTab) {
                "rules" -> 0
                "vocab" -> 1
                else -> 2
            },
            containerColor = CharcoalSlate,
            contentColor = GoldenAmber,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[when (activeSubTab) {
                        "rules" -> 0
                        "vocab" -> 1
                        else -> 2
                    }]),
                    color = GoldenAmber
                )
            }
        ) {
            Tab(
                selected = activeSubTab == "rules",
                onClick = { activeSubTab = "rules" },
                text = { Text("الشرح / Explanation", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("lesson_subtab_rules")
            )
            Tab(
                selected = activeSubTab == "vocab",
                onClick = { activeSubTab = "vocab" },
                text = { Text("مفردات ذكية / Words", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("lesson_subtab_vocab")
            )
            Tab(
                selected = activeSubTab == "quiz",
                onClick = { activeSubTab = "quiz" },
                text = { Text("اختبر نفسك / Quiz", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("lesson_subtab_quiz")
            )
        }

        // Content
        Box(modifier = Modifier.fillMaxSize().weight(1f)) {
            when (activeSubTab) {
                "rules" -> LessonContentRules(lesson)
                "vocab" -> LessonContentVocab(lesson, viewModel)
                "quiz" -> LessonContentQuiz(lesson, viewModel)
            }
        }
    }
}

@Composable
fun LessonContentRules(lesson: LessonItem) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Arabic Overview Card
        Card(
            colors = CardDefaults.cardColors(containerColor = CharcoalSlate),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "نظرة عامة / Summary",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = GoldenAmber
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = lesson.arabicOverview,
                    fontSize = 14.sp,
                    color = PlatinumClean,
                    lineHeight = 20.sp,
                    textAlign = TextAlign.Right
                )
            }
        }

        // Grammar Formula (if present)
        if (lesson.formulaEnglish.isNotBlank()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = BritishBlue.copy(alpha = 0.2f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.border(1.dp, BritishBlue, RoundedCornerShape(12.dp))
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "القاعدة الرياضية الصياغية",
                        fontSize = 11.sp,
                        color = LightSlate
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = lesson.formulaEnglish,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = PlatinumClean,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Rules List
        Text(
            text = "القواعد الذهبية / Golden Rules",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = PlatinumClean
        )

        lesson.rulesList.forEachIndexed { i, rule ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(CharcoalSlate)
                    .padding(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(BritishBlue)
                        .align(Alignment.Top),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${i + 1}",
                        fontSize = 11.sp,
                        color = PlatinumClean,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = rule,
                    fontSize = 13.sp,
                    color = PlatinumClean,
                    lineHeight = 18.sp,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // British Spoken Tips
        if (lesson.britishNativeTips.isNotBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(GoldenAmber.copy(alpha = 0.1f))
                    .border(1.dp, GoldenAmber, RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, contentDescription = "Native speaking tips", tint = GoldenAmber, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "نصيحة المتحدث الأصلي (British Native Tip)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldenAmber
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = lesson.britishNativeTips,
                        fontSize = 13.sp,
                        color = PlatinumClean,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
fun LessonContentVocab(lesson: LessonItem, viewModel: BritishLearningViewModel) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(BritishBlue.copy(alpha = 0.1f))
                .padding(12.dp)
        ) {
            Text(
                text = "💡 اضغط على أي كلمة لعرض معانيها وتصريفاتها ونطقها بالصوت البريطاني الحقيقي وحفظها بالقاموس!",
                fontSize = 12.sp,
                color = PlatinumClean,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Right
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(lesson.vocabulary) { word ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(CharcoalSlate)
                        .clickable { viewModel.activeExplainWord.value = word }
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = word.word,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldenAmber
                        )
                        Text(
                            text = word.pronun,
                            fontSize = 11.sp,
                            color = LightSlate,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = word.wordType,
                            fontSize = 10.sp,
                            color = BritishBlue,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = word.explanationArabic,
                            fontSize = 12.sp,
                            color = PlatinumClean,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LessonContentQuiz(lesson: LessonItem, viewModel: BritishLearningViewModel) {
    val quizIndex by viewModel.activeQuizIndex.collectAsStateWithLifecycle()
    val isAnswered by viewModel.isQuizAnswered.collectAsStateWithLifecycle()
    val selectedAns by viewModel.selectedQuizAnswer.collectAsStateWithLifecycle()
    val score by viewModel.quizScore.collectAsStateWithLifecycle()

    if (lesson.quizzes.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("لا تتوفر اختبارات لموضوع الدرس هذا حالياً.", color = LightSlate)
        }
        return
    }

    val currentQuiz = lesson.quizzes.getOrNull(quizIndex)

    if (currentQuiz == null) {
        // Quiz complete screen
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(GoldenAmber.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Done, contentDescription = "Success", tint = GoldenAmber, modifier = Modifier.size(48.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "جولتك في بريطانيا اكتملت! 🎉",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = PlatinumClean
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "لقد حصلت على أكثر من 100 XP لإتمامك الكويز والعمليات التدريبية لهذه الوحدة بنجاح.",
                fontSize = 13.sp,
                color = LightSlate,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    viewModel.markLessonCompleted(lesson.id)
                    viewModel.selectedLesson.value = null
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldenAmber),
                modifier = Modifier.fillMaxWidth().height(48.dp).testTag("quiz_finish_button")
            ) {
                Text("إغلاق والعودة للقائمة الرئيسية", color = NavyDeep, fontWeight = FontWeight.Bold)
            }
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            // Header progress
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "كويز سريع: سؤال ${quizIndex + 1} من ${lesson.quizzes.size}",
                    fontSize = 12.sp,
                    color = LightSlate
                )
                Text(
                    text = "مجموع النقاط: $score",
                    fontSize = 12.sp,
                    color = GoldenAmber,
                    fontWeight = FontWeight.Bold
                )
            }

            // Progress Bar
            LinearProgressIndicator(
                progress = { (quizIndex + 1).toFloat() / lesson.quizzes.size },
                modifier = Modifier.fillMaxWidth(),
                color = GoldenAmber,
                trackColor = CharcoalSlate
            )

            // Question Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(CharcoalSlate)
                    .padding(18.dp)
            ) {
                Text(
                    text = currentQuiz.question,
                    fontSize = 16.sp,
                    color = PlatinumClean,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Options List
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                currentQuiz.options.forEachIndexed { optIndex, option ->
                    val isSelected = selectedAns == optIndex
                    val isCorrect = optIndex == currentQuiz.correctAnswerIndex

                    val cardColor = when {
                        isAnswered && isCorrect -> Color(0xFF15803D).copy(alpha = 0.2f) // correct is green
                        isAnswered && isSelected && !isCorrect -> AccentCrimson.copy(alpha = 0.2f) // wrong is red
                        isSelected -> GoldenAmber.copy(alpha = 0.2f) // selected before answer
                        else -> CharcoalSlate
                    }

                    val borderColor = when {
                        isAnswered && isCorrect -> Color(0xFF22C55E)
                        isAnswered && isSelected && !isCorrect -> AccentCrimson
                        isSelected -> GoldenAmber
                        else -> Color.Transparent
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(cardColor)
                            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                            .clickable(enabled = !isAnswered) {
                                viewModel.selectedQuizAnswer.value = optIndex
                            }
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = {
                                if (!isAnswered) viewModel.selectedQuizAnswer.value = optIndex
                            },
                            enabled = !isAnswered,
                            colors = RadioButtonDefaults.colors(selectedColor = GoldenAmber)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = option,
                            fontSize = 14.sp,
                            color = PlatinumClean,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Detailed explanation if answered
            if (isAnswered) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(CharcoalSlate)
                        .border(1.dp, LightSlate.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Text(
                            text = "💡 التفسير البريطاني / Why?",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldenAmber
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = currentQuiz.explanationArabic,
                            fontSize = 13.sp,
                            color = PlatinumClean,
                            lineHeight = 18.sp,
                            textAlign = TextAlign.Right
                        )
                    }
                }
            }
        }

        // Bottom CTA block
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (!isAnswered) {
                Button(
                    onClick = {
                        val selected = selectedAns
                        if (selected != null) {
                            viewModel.isQuizAnswered.value = true
                            if (selected == currentQuiz.correctAnswerIndex) {
                                viewModel.quizScore.value = score + 50
                                viewModel.addXp(50) // gain 50 XP
                            }
                        }
                    },
                    enabled = selectedAns != null,
                    colors = ButtonDefaults.buttonColors(containerColor = GoldenAmber),
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("quiz_submit_btn")
                ) {
                    Text("تأكيد الإجابة", color = NavyDeep, fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = {
                        viewModel.activeQuizIndex.value = quizIndex + 1
                        viewModel.isQuizAnswered.value = false
                        viewModel.selectedQuizAnswer.value = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BritishBlue),
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("quiz_next_btn")
                ) {
                    Text("السؤال التالي / Next", color = PlatinumClean, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ================= SMART WORD EXPLANATION DIALOG =================

@Composable
fun WordExplanationDialog(
    word: LessonWord,
    viewModel: BritishLearningViewModel,
    onDismiss: () -> Unit
) {
    var isSaved by remember { mutableStateOf(false) }

    LaunchedEffect(word) {
        isSaved = viewModel.isWordSaved(word.word)
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(2.dp, GoldenAmber, RoundedCornerShape(16.dp))
                .shadow(12.dp)
                .testTag("word_dialog_card"),
            colors = CardDefaults.cardColors(containerColor = CharcoalSlate)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            viewModel.toggleSavedWord(word)
                            isSaved = !isSaved
                        },
                        modifier = Modifier.testTag("word_dialog_save")
                    ) {
                        Icon(
                            imageVector = if (isSaved) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Save word to glossary",
                            tint = if (isSaved) AccentCrimson else LightSlate
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = PlatinumClean)
                    }
                }

                // Title and Phonetic
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = word.word,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldenAmber
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = word.pronun,
                        fontSize = 13.sp,
                        color = LightSlate,
                        fontFamily = FontFamily.Monospace,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = word.wordType,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BritishBlue
                    )
                }

                Divider(color = LightSlate.copy(alpha = 0.2f))

                // Meaning in Arabic
                Column {
                    Text(
                        text = "المعنى باللغة العربية / Meaning",
                        fontSize = 11.sp,
                        color = LightSlate
                    )
                    Text(
                        text = word.explanationArabic,
                        fontSize = 14.sp,
                        color = PlatinumClean,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Synonyms & Antonyms
                if (word.synonyms.isNotBlank() && word.synonyms != "N/A") {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("المرادفات / Synonyms", fontSize = 10.sp, color = LightSlate)
                            Text(word.synonyms, fontSize = 12.sp, color = PlatinumClean, fontWeight = FontWeight.Medium)
                        }
                        if (word.antonyms.isNotBlank() && word.antonyms != "N/A") {
                            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                                Text("المضادات / Antonyms", fontSize = 10.sp, color = LightSlate)
                                Text(word.antonyms, fontSize = 12.sp, color = PlatinumClean, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }

                // Spoken Usage Context
                if (word.realUsage.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(BritishBlue.copy(alpha = 0.1f))
                            .padding(10.dp)
                    ) {
                        Column {
                            Text(
                                text = "الاستعمال البريطاني الحقيقي / British Spoken Context",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = BritishBlue
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = word.realUsage,
                                fontSize = 12.sp,
                                color = PlatinumClean,
                                lineHeight = 16.sp,
                                textAlign = TextAlign.Right,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                // Real Sentences Example
                if (word.sentenceExample.isNotBlank()) {
                    Column {
                        Text(
                            text = "مثال حي في المحادثة / Example",
                            fontSize = 10.sp,
                            color = LightSlate
                        )
                        Text(
                            text = word.sentenceExample,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = PlatinumClean,
                            fontFamily = FontFamily.SansSerif
                        )
                    }
                }
            }
        }
    }
}

// ================= 2. AI TUTOR CHATS AND EXPLAINERS =================

@Composable
fun ChatScreen(viewModel: BritishLearningViewModel) {
    var activeModuleTab by remember { mutableStateOf("talk") } // talk, naturalizer, corrector, accent

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = when (activeModuleTab) {
                "talk" -> 0
                "naturalizer" -> 1
                "corrector" -> 2
                else -> 3
            },
            containerColor = CharcoalSlate,
            contentColor = GoldenAmber,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[when (activeModuleTab) {
                        "talk" -> 0
                        "naturalizer" -> 1
                        "corrector" -> 2
                        else -> 3
                    }]),
                    color = GoldenAmber
                )
            }
        ) {
            Tab(
                selected = activeModuleTab == "talk",
                onClick = { activeModuleTab = "talk" },
                text = { Text("المحاكاة / Chat", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("chat_tab_selector")
            )
            Tab(
                selected = activeModuleTab == "naturalizer",
                onClick = { activeModuleTab = "naturalizer" },
                text = { Text("البريطاني العامي / Naturalizer", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("naturalizer_tab_selector")
            )
            Tab(
                selected = activeModuleTab == "corrector",
                onClick = { activeModuleTab = "corrector" },
                text = { Text("مصحح الأخطاء / Correction AI", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("corrector_tab_selector")
            )
            Tab(
                selected = activeModuleTab == "accent",
                onClick = { activeModuleTab = "accent" },
                text = { Text("مُدرّب اللكنة / Accent Coach", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("accent_tab_selector")
            )
        }

        Box(modifier = Modifier.fillMaxSize().weight(1f)) {
            when (activeModuleTab) {
                "talk" -> InteractiveChatLayout(viewModel)
                "naturalizer" -> NaturalizerLayout(viewModel)
                "corrector" -> CorrectorLayout(viewModel)
                "accent" -> AccentTrainerLayout(viewModel)
            }
        }
    }
}

@Composable
fun InteractiveChatLayout(viewModel: BritishLearningViewModel) {
    val activeSession by viewModel.activeChatSession.collectAsStateWithLifecycle()
    val chatHistory by viewModel.chatMessages.collectAsStateWithLifecycle()
    val textInput by viewModel.chatInputText.collectAsStateWithLifecycle()
    val isApiLoading by viewModel.isChatApiLoading.collectAsStateWithLifecycle()

    val sessions = listOf(
        "tutor_chat" to "🎓 Private Tutor (سير أليستر)",
        "friends_group" to "💬 UK WhatsApp (شات أصدقاء)",
        "school_group" to "🏫 School Group (قروب المدرسة)",
        "london_pub" to "🍻 Ordering Pub (المقهى اللندني)",
        "job_interview" to "👔 Job Interview (مقابلة لندن)",
        "nhs_doctor" to "🏥 NHS GP Visit (طبيب العيادة)"
    )

    Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        // Horizontal simulator selector
        Column {
            Text(
                text = "اختر محاكاة للحديث مع الـ AI / Choose Simulation:",
                fontSize = 11.sp,
                color = LightSlate,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                sessions.forEach { (id, label) ->
                    val isSelected = activeSession == id
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) GoldenAmber else CharcoalSlate)
                            .clickable { viewModel.activeChatSession.value = id }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) NavyDeep else PlatinumClean,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Chat bubble terminal list
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .padding(vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            reverseLayout = false
        ) {
            items(chatHistory) { msg ->
                val isModel = msg.role == "model"
                val bubbleBg = if (isModel) CharcoalSlate else BritishBlue
                val alignSide = if (isModel) Alignment.Start else Alignment.End

                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = alignSide) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(
                                topStart = 16.dp,
                                topEnd = 16.dp,
                                bottomStart = if (isModel) 0.dp else 16.dp,
                                bottomEnd = if (isModel) 16.dp else 0.dp
                            ))
                            .background(bubbleBg)
                            .padding(12.dp)
                            .widthIn(max = 280.dp)
                    ) {
                        Text(
                            text = msg.content,
                            fontSize = 13.sp,
                            color = PlatinumClean,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            if (isApiLoading) {
                item {
                    Text(
                        text = "Sir Alistair is thinking... 🇬🇧🍵",
                        color = LightSlate,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }

        // SMS & Texting Abbreviations Selector
        if (activeSession == "school_group" || activeSession == "friends_group") {
            Column(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                Text(
                    text = "اختصارات شات بريطانية - انقر للإدراج / Click to insert slag:",
                    fontSize = 10.sp,
                    color = GoldenAmber,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val shortcuts = listOf(
                        "rev" to "revision (مذاكرة)",
                        "hw" to "homework (واجب)",
                        "ngl" to "not gonna lie (بصراحة)",
                        "tbf" to "to be fair (للإنصاف)",
                        "tbh" to "to be honest (للأمانة)",
                        "rn" to "right now (الآن)",
                        "ikr" to "I know, right (صح؟)",
                        "skint" to "skint (مفلس/طفران)",
                        "skive" to "skive (يهرب من الدرس)",
                        "bruv" to "bruv (يا خوي/صديقي)"
                    )
                    shortcuts.forEach { (code, desc) ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(CharcoalSlate)
                                .border(1.dp, LightSlate.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                .clickable {
                                    val current = viewModel.chatInputText.value
                                    viewModel.chatInputText.value = if (current.isBlank()) code else "$current $code"
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(code, color = GoldenAmber, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text(desc, color = LightSlate, fontSize = 8.sp)
                            }
                        }
                    }
                }
            }
        }

        // Input Block
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextField(
                value = textInput,
                onValueChange = { viewModel.chatInputText.value = it },
                placeholder = { Text("اكتب محادثة بالإنجليزية...", color = LightSlate) },
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .testTag("chat_input_tf"),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = {
                    viewModel.sendChatMessage(textInput)
                }),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = CharcoalSlate,
                    unfocusedContainerColor = CharcoalSlate,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = PlatinumClean,
                    unfocusedTextColor = PlatinumClean
                )
            )

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(GoldenAmber)
                    .clickable { viewModel.sendChatMessage(textInput) },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send text", tint = NavyDeep, modifier = Modifier.size(20.dp))
            }

            IconButton(onClick = { viewModel.clearChatHistory() }) {
                Icon(Icons.Default.Refresh, contentDescription = "Clear History", tint = LightSlate)
            }
        }
    }
}

@Composable
fun NaturalizerLayout(viewModel: BritishLearningViewModel) {
    val input by viewModel.naturalizerInput.collectAsStateWithLifecycle()
    val output by viewModel.naturalizerOutput.collectAsStateWithLifecycle()
    val explanation by viewModel.naturalizerExplanation.collectAsStateWithLifecycle()
    val isLoading by viewModel.isNaturalizerLoading.collectAsStateWithLifecycle()
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(colors = CardDefaults.cardColors(containerColor = CharcoalSlate)) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "الذوق اليومي: تحويل الإنجليزية المدرسية لعامية الشارع البريطاني",
                    fontSize = 13.sp,
                    color = GoldenAmber,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "اكتب أي جملة رسمية أو تركيبة أمريكية عادية، وسوف يُغيرها الـ AI لتناسب التعبير والنطق اللطيف المعتاد في المقاهي اللندنية وشوارع بريطانيا اليومية.",
                    fontSize = 11.sp,
                    color = LightSlate,
                    textAlign = TextAlign.Right
                )
            }
        }

        TextField(
            value = input,
            onValueChange = { viewModel.naturalizerInput.value = it },
            placeholder = { Text("مثال الأمريكي: I am very hungry, where is the apartment?", color = LightSlate) },
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .clip(RoundedCornerShape(12.dp))
                .testTag("naturalizer_tf"),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = CharcoalSlate,
                unfocusedContainerColor = CharcoalSlate,
                focusedTextColor = PlatinumClean,
                unfocusedTextColor = PlatinumClean
            )
        )

        Button(
            onClick = {
                viewModel.submitNaturalizer()
                keyboardController?.hide()
            },
            enabled = input.isNotBlank() && !isLoading,
            colors = ButtonDefaults.buttonColors(containerColor = GoldenAmber),
            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("naturalizer_submit_btn")
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = NavyDeep, modifier = Modifier.size(20.dp))
            } else {
                Text("إضفاء اللمسة البريطانية / Naturalise! 🇬🇧", color = NavyDeep, fontWeight = FontWeight.Bold)
            }
        }

        if (output.isNotBlank()) {
            Text("اللفظ البريطاني المقترح / Spoken British spoken:", fontSize = 12.sp, color = GoldenAmber, fontWeight = FontWeight.Bold)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(BritishBlue.copy(alpha = 0.2f))
                    .padding(14.dp)
            ) {
                Text(
                    text = output,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = PlatinumClean
                )
            }

            Text("تفسير اللغويات / Explanation:", fontSize = 12.sp, color = GoldenAmber, fontWeight = FontWeight.Bold)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(CharcoalSlate)
                    .padding(14.dp)
            ) {
                Text(
                    text = explanation,
                    fontSize = 13.sp,
                    color = PlatinumClean,
                    lineHeight = 18.sp,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun CorrectorLayout(viewModel: BritishLearningViewModel) {
    val input by viewModel.correctionInput.collectAsStateWithLifecycle()
    val formalOut by viewModel.correctionFormalOutput.collectAsStateWithLifecycle()
    val textingOut by viewModel.correctionTextingOutput.collectAsStateWithLifecycle()
    val explanation by viewModel.correctionExplanation.collectAsStateWithLifecycle()
    val isLoading by viewModel.isCorrectionLoading.collectAsStateWithLifecycle()
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(colors = CardDefaults.cardColors(containerColor = CharcoalSlate)) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "مصحح الأخطاء الإملائية والتهجئة البريطانية الرسمية",
                    fontSize = 13.sp,
                    color = GoldenAmber,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "قارن الكلمات الأمريكية والصيغ الخاطئة، وسنصححها لك بصورة بريطانية كلاسيكية مهذبة ومع ثقافة الاختصارات للدردشات والمراسلات السريعة.",
                    fontSize = 11.sp,
                    color = LightSlate,
                    textAlign = TextAlign.Right
                )
            }
        }

        TextField(
            value = input,
            onValueChange = { viewModel.correctionInput.value = it },
            placeholder = { Text("مثال الأمريكي: I realize my favorite color was wrong", color = LightSlate) },
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .clip(RoundedCornerShape(12.dp))
                .testTag("corrector_tf"),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = CharcoalSlate,
                unfocusedContainerColor = CharcoalSlate,
                focusedTextColor = PlatinumClean,
                unfocusedTextColor = PlatinumClean
            )
        )

        Button(
            onClick = {
                viewModel.submitCorrection()
                keyboardController?.hide()
            },
            enabled = input.isNotBlank() && !isLoading,
            colors = ButtonDefaults.buttonColors(containerColor = GoldenAmber),
            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("corrector_submit_btn")
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = NavyDeep, modifier = Modifier.size(20.dp))
            } else {
                Text("تحليل وتصحيح الكتابة / Verify Spelling", color = NavyDeep, fontWeight = FontWeight.Bold)
            }
        }

        if (formalOut.isNotBlank()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("التهجئة الرسمية البريطانية / UK Formal Spelling:", fontSize = 11.sp, color = LightSlate)
                Box(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(Color(0xFF065F46).copy(alpha = 0.2f)).padding(10.dp)
                ) {
                    Text(text = formalOut, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Colors.GreenAccent ?: PlatinumClean)
                }

                Text("لغة الرسائل والدردشة / WhatsApp SMS Style:", fontSize = 11.sp, color = LightSlate)
                Box(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(CharcoalSlate).padding(10.dp)
                ) {
                    Text(text = textingOut, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = GoldenAmber)
                }

                Text("شرح أستاذ اللغة بالعربية / Teacher correction rules:", fontSize = 11.sp, color = LightSlate)
                Box(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(CharcoalSlate).padding(10.dp)
                ) {
                    Text(
                        text = explanation,
                        fontSize = 13.sp,
                        color = PlatinumClean,
                        lineHeight = 18.sp,
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

// Minimal colors bridge
object Colors {
    val GreenAccent = Color(0xFF10B981)
}

// ================= 3. UK COINS, PAYMENTS & LIFE GAME =================

@Composable
fun MoneyLifeScreen(viewModel: BritishLearningViewModel) {
    val activeSubTab by viewModel.activeMoneySubTab.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = if (activeSubTab == "coin_game") 0 else 1,
            containerColor = CharcoalSlate,
            contentColor = GoldenAmber,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[if (activeSubTab == "coin_game") 0 else 1]),
                    color = GoldenAmber
                )
            }
        ) {
            Tab(
                selected = activeSubTab == "coin_game",
                onClick = { viewModel.activeMoneySubTab.value = "coin_game" },
                text = { Text("لعبة النقود / Local Coin Pay", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("coin_game_tab")
            )
            Tab(
                selected = activeSubTab == "supermarket",
                onClick = { viewModel.activeMoneySubTab.value = "supermarket" },
                text = { Text("السوبرماركت / Grocery Checkout", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("supermarket_checkout_tab")
            )
        }

        Box(modifier = Modifier.fillMaxSize().weight(1f)) {
            if (activeSubTab == "coin_game") {
                CoinGameLayout(viewModel)
            } else {
                SupermarketCheckoutLayout(viewModel)
            }
        }
    }
}

@Composable
fun CoinGameLayout(viewModel: BritishLearningViewModel) {
    val targetPrice by viewModel.targetPaymentPrice.collectAsStateWithLifecycle()
    val paidSum by viewModel.userPaymentSum.collectAsStateWithLifecycle()
    val status by viewModel.currentPaymentStatus.collectAsStateWithLifecycle()
    val itemName by viewModel.itemsName.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Explanatory badge
        Card(colors = CardDefaults.cardColors(containerColor = CharcoalSlate)) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "المحاسبة بالبنس والجنيه الإسترليني (£ & p)",
                    fontSize = 13.sp,
                    color = GoldenAmber,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "تعلّم العملات البريطانية الحقيقية! انقر على العملات المعدنية والورقية لتأدية الفاتورة بالقيمة الصحيحة لتنال نقاط الـ XP والمكافأة التفاعلية.",
                    fontSize = 11.sp,
                    color = LightSlate,
                    textAlign = TextAlign.Right
                )
            }
        }

        // Terminal Visual Counter
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(CharcoalSlate)
                .border(2.dp, if (status == "success") Colors.GreenAccent else LightSlate.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                .padding(20.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Pub & Shop Pay terminal 🇬🇧",
                    fontSize = 11.sp,
                    color = LightSlate,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "سعر السلعة: $itemName",
                    fontSize = 14.sp,
                    color = PlatinumClean
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "القيمة المطلوبة: £${String.format("%.2f", targetPrice)}",
                    fontSize = 24.sp,
                    color = GoldenAmber,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.height(10.dp))
                Divider(color = LightSlate.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "المبلغ المدفوع حتي الآن / Paid Sum:",
                    fontSize = 11.sp,
                    color = LightSlate
                )
                Text(
                    text = "£${String.format("%.2f", paidSum)}",
                    fontSize = 24.sp,
                    color = if (status == "success") Colors.GreenAccent else PlatinumClean,
                    fontWeight = FontWeight.Bold
                )

                // Status Message display
                Spacer(modifier = Modifier.height(8.dp))
                when (status) {
                    "success" -> {
                        Text(
                            text = "Lovely job mate! Paid exactly and got +40 XP!",
                            color = Colors.GreenAccent,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    "change_needed" -> {
                        val change = Math.round((paidSum - targetPrice) * 100.0) / 100.0
                        Text(
                            text = "Paid! Remaining change needed: £${String.format("%.2f", change)}",
                            color = GoldenAmber,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Button(
                            onClick = { viewModel.completeWithCorrectChange() },
                            colors = ButtonDefaults.buttonColors(containerColor = BritishBlue),
                            modifier = Modifier.clip(RoundedCornerShape(8.dp)).testTag("take_change_button")
                        ) {
                            Text("استلم الباقي (Take change)!", color = PlatinumClean, fontSize = 11.sp)
                        }
                    }
                    "insufficient" -> {
                        Text(
                            text = "Insufficient, select more coins. (أقل من الفاتورة)",
                            color = AccentCrimson,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Coins and notes layout
        Text("انقر لدفع النقود / Tap to pay cash:", fontSize = 12.sp, color = PlatinumClean, fontWeight = FontWeight.Bold)

        // Pence Coins Row
        Text("الأقراص المعدنية الصغيرة (Pence Coins):", fontSize = 10.sp, color = LightSlate)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val pCoins = listOf(0.01 to "1p", 0.02 to "2p", 0.05 to "5p", 0.10 to "10p", 0.20 to "20p", 0.50 to "50p")
            pCoins.forEach { (valDouble, name) ->
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(CharcoalSlate)
                        .border(1.dp, GoldenAmber, CircleShape)
                        .clickable { viewModel.addPaymentCoin(valDouble) }
                        .testTag("pay_coin_$name"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = name, color = GoldenAmber, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Pound Coins Row
        Text("العملات المعدنية الكبيرة (Pounds):", fontSize = 10.sp, color = LightSlate)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val poundCoins = listOf(1.00 to "£1", 2.00 to "£2")
            poundCoins.forEach { (valDouble, name) ->
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFD97706)) // golden pound
                        .clickable { viewModel.addPaymentCoin(valDouble) }
                        .testTag("pay_coin_$name"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = name, color = NavyDeep, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Notes Row
        Text("الأوراق النقدية البلاستيكية (Banknotes):", fontSize = 10.sp, color = LightSlate)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val notes = listOf(5.00 to "£5 Note", 10.00 to "£10 Note", 20.00 to "£20 Note")
            notes.forEach { (valDouble, name) ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF047857)) // green money notes
                        .clickable { viewModel.addPaymentCoin(valDouble) }
                        .testTag("pay_coin_$name"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = name, color = PlatinumClean, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Actions: Clear & Next Round
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = { viewModel.clearPaymentCoins() },
                colors = ButtonDefaults.buttonColors(containerColor = AccentCrimson),
                modifier = Modifier.weight(1f).height(44.dp).testTag("clear_payment_btn")
            ) {
                Text("إلغاء وسحب النقود", color = PlatinumClean)
            }

            Button(
                onClick = { viewModel.generateNewMoneyTarget() },
                colors = ButtonDefaults.buttonColors(containerColor = GoldenAmber),
                modifier = Modifier.weight(1f).height(44.dp).testTag("next_terminal_btn")
            ) {
                Text("فاتورة جديدة / Next round", color = NavyDeep)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Split Bill tool
        Divider(color = LightSlate.copy(alpha = 0.2f))
        Text("مقسم الحساب اللندني للمجموعات (Split Bill Calculator):", fontSize = 12.sp, color = GoldenAmber, fontWeight = FontWeight.Bold)

        var billAmount by remember { mutableStateOf("15.00") }
        var headCount by remember { mutableStateOf("3") }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = billAmount,
                onValueChange = { billAmount = it },
                label = { Text("قيمة الفاتورة (£)") },
                modifier = Modifier.weight(1f).clip(RoundedCornerShape(8.dp)),
                colors = TextFieldDefaults.colors(focusedTextColor = PlatinumClean, unfocusedTextColor = PlatinumClean)
            )

            TextField(
                value = headCount,
                onValueChange = { headCount = it },
                label = { Text("عدد الأشخاص") },
                modifier = Modifier.weight(1f).clip(RoundedCornerShape(8.dp)),
                colors = TextFieldDefaults.colors(focusedTextColor = PlatinumClean, unfocusedTextColor = PlatinumClean)
            )
        }

        val parsedAmount = billAmount.toDoubleOrNull() ?: 0.0
        val parsedHeads = headCount.toIntOrNull() ?: 1
        val eachDolphin = if (parsedHeads > 0) parsedAmount / parsedHeads else 0.0

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(CharcoalSlate)
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "كل شخص يدفع (Each person pays): £${String.format("%.2f", eachDolphin)} quid!",
                color = PlatinumClean,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun SupermarketCheckoutLayout(viewModel: BritishLearningViewModel) {
    val step by viewModel.supermarketStep.collectAsStateWithLifecycle()
    val basePrice by viewModel.supermarketBasePrice.collectAsStateWithLifecycle()
    val bagsCount by viewModel.supermarketBagsBought.collectAsStateWithLifecycle()
    val cardApplied by viewModel.supermarketClubcardApplied.collectAsStateWithLifecycle()
    val totalToPay by viewModel.supermarketTotalToPay.collectAsStateWithLifecycle()
    val method by viewModel.supermarketPaymentChoice.collectAsStateWithLifecycle()
    val engStatus by viewModel.supermarketStatusMsg.collectAsStateWithLifecycle()
    val araStatus by viewModel.supermarketArabicStatusMsg.collectAsStateWithLifecycle()
    
    // For cash integration
    val paidSum by viewModel.userPaymentSum.collectAsStateWithLifecycle()
    val paymentStatus by viewModel.currentPaymentStatus.collectAsStateWithLifecycle()

    var pinCodeInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Welcome Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Brush.horizontalGradient(listOf(Color(0xFFEA580C), Color(0xFFF97316)))) // Sainsbury's orange!
                .padding(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = PlatinumClean, modifier = Modifier.size(34.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("Sainsbury's Self-Checkout 🛒", color = PlatinumClean, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                    Text("محاكاة المحاسبة في سوبرماركت بريطاني حقيقي", color = PlatinumClean.copy(alpha = 0.9f), fontSize = 10.sp)
                }
            }
        }

        // Shopping Basket list
        Card(
            colors = CardDefaults.cardColors(containerColor = CharcoalSlate),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("سلة المقاضي البريطانية / Groceries Basket:", fontSize = 11.sp, color = GoldenAmber, fontWeight = FontWeight.Bold)
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("• Scones & Clotted Cream (كعك بالمربى والقشطة)", fontSize = 11.sp, color = PlatinumClean)
                    Text("£3.20", fontSize = 11.sp, color = PlatinumClean, fontWeight = FontWeight.Bold)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("• Chocolate Digestives (بسكويت دايجستيف)", fontSize = 11.sp, color = PlatinumClean)
                    Text("£1.50", fontSize = 11.sp, color = PlatinumClean, fontWeight = FontWeight.Bold)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("• Teabags PG Tips (شاي بريطاني أوراق)", fontSize = 11.sp, color = PlatinumClean)
                    Text("£2.25", fontSize = 11.sp, color = PlatinumClean, fontWeight = FontWeight.Bold)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("• Pint of Milk (نصف لتر حليب طازج)", fontSize = 11.sp, color = PlatinumClean)
                    Text("£0.90", fontSize = 11.sp, color = PlatinumClean, fontWeight = FontWeight.Bold)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("• Walkers Salt & Vinegar Crisps (رقائق شيبس)", fontSize = 11.sp, color = PlatinumClean)
                    Text("£1.80", fontSize = 11.sp, color = PlatinumClean, fontWeight = FontWeight.Bold)
                }
                
                if (bagsCount > 0) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("• Carrier Bags (أكياس بلاستيكية) x$bagsCount", fontSize = 11.sp, color = GoldenAmber)
                        Text("£${String.format("%.2f", bagsCount * 0.30)}", fontSize = 11.sp, color = GoldenAmber, fontWeight = FontWeight.Bold)
                    }
                }
                if (cardApplied) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("• Clubcard Loyalty Discount (خصم كارت الولاء)", fontSize = 11.sp, color = Color(0xFF10B981))
                        Text("-£1.80", fontSize = 11.sp, color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
                    }
                }

                Divider(color = LightSlate.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("الحساب الإجمالي المستحق / Total Bill: ", fontSize = 13.sp, color = PlatinumClean, fontWeight = FontWeight.Bold)
                    Text("£${String.format("%.2f", totalToPay)}", fontSize = 15.sp, color = GoldenAmber, fontWeight = FontWeight.ExtraBold)
                }
            }
        }

        // Self-Checkout Digital Terminal Dialogue Screen
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(CharcoalSlate)
                .border(2.dp, if (step == 4) Color(0xFF10B981) else GoldenAmber, RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text("Sainsbury's Audio-Terminal 🔊", color = GoldenAmber, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Box(modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(if (step >= 3) Color(0xFF065F46) else Color(0xFF991B1B)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                        Text(if (step >= 3) "PAID/APPROVED" else "WAITING INPUT", color = PlatinumClean, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Divider(color = LightSlate.copy(alpha = 0.15f))
                
                // Dialog Output (Speaker voice text)
                Text(
                    text = "\"$engStatus\"",
                    color = PlatinumClean,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                
                Text(
                    text = araStatus,
                    color = LightSlate,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Interactive Steps Interface
        when (step) {
            0 -> {
                // Choose Bags Step
                Text("الخطوة 1: اختيار عدد أكياس التسوق / Step 1: Carrier Bags:", fontSize = 11.sp, color = LightSlate)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { viewModel.selectSupermarketBags(0) },
                        colors = ButtonDefaults.buttonColors(containerColor = CharcoalSlate),
                        modifier = Modifier.weight(1f).height(38.dp).testTag("bags_0_btn")
                    ) {
                        Text("بدون أكياس / No bags", fontSize = 10.sp, color = PlatinumClean)
                    }
                    Button(
                        onClick = { viewModel.selectSupermarketBags(1) },
                        colors = ButtonDefaults.buttonColors(containerColor = BritishBlue),
                        modifier = Modifier.weight(1.5f).height(38.dp).testTag("bags_1_btn")
                    ) {
                        Text("1 كيس (+30p)", fontSize = 10.sp, color = PlatinumClean)
                    }
                    Button(
                        onClick = { viewModel.selectSupermarketBags(2) },
                        colors = ButtonDefaults.buttonColors(containerColor = BritishBlue),
                        modifier = Modifier.weight(1.5f).height(38.dp).testTag("bags_2_btn")
                    ) {
                        Text("2 كيس (+60p)", fontSize = 10.sp, color = PlatinumClean)
                    }
                }
            }
            1 -> {
                // Loyalty Card
                Text("الخطوة 2: كارت نقاط الولاء / Step 2: Loyalty Card Discount:", fontSize = 11.sp, color = LightSlate)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { viewModel.applySupermarketClubcard(true) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                        modifier = Modifier.weight(1.5f).height(40.dp).testTag("clubcard_yes_btn")
                    ) {
                        Text("مسح الكارت / Scan Clubcard", fontSize = 10.sp, color = PlatinumClean, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = { viewModel.applySupermarketClubcard(false) },
                        colors = ButtonDefaults.buttonColors(containerColor = CharcoalSlate),
                        modifier = Modifier.weight(1f).height(40.dp).testTag("clubcard_no_btn")
                    ) {
                        Text("لا أملك / No card", fontSize = 10.sp, color = PlatinumClean)
                    }
                }
            }
            2 -> {
                // Payment Method selections
                Text("الخطوة 3: اختر وسيلة محاسبة / Step 3: Payment Options:", fontSize = 11.sp, color = LightSlate)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button(
                        onClick = { viewModel.chooseSupermarketPayment("contactless") },
                        colors = ButtonDefaults.buttonColors(containerColor = BritishBlue),
                        modifier = Modifier.weight(1.3f).height(42.dp).testTag("pay_contactless_btn")
                    ) {
                        Text("لاسلكي Contactless 📱", fontSize = 10.sp, color = PlatinumClean)
                    }
                    Button(
                        onClick = { viewModel.chooseSupermarketPayment("chip_pin") },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldenAmber),
                        modifier = Modifier.weight(1.1f).height(42.dp).testTag("pay_chippin_btn")
                    ) {
                        Text("رقم سري Chip&PIN 💳", fontSize = 10.sp, color = NavyDeep)
                    }
                    Button(
                        onClick = { viewModel.chooseSupermarketPayment("cash") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF047857)),
                        modifier = Modifier.weight(1f).height(42.dp).testTag("pay_cash_btn")
                    ) {
                        Text("نقدي Cash 💷", fontSize = 10.sp, color = PlatinumClean)
                    }
                }

                // If chip and pin requested, let them write code
                if (method == "chip_pin") {
                    Spacer(modifier = Modifier.height(10.dp))
                    Card(colors = CardDefaults.cardColors(containerColor = CharcoalSlate)) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("أدخل الرمز السري للبطاقة (أكتب 4 خانات مثلاً: 1234):", fontSize = 11.sp, color = GoldenAmber)
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                TextField(
                                    value = pinCodeInput,
                                    onValueChange = { if (it.length <= 4) pinCodeInput = it },
                                    placeholder = { Text("PIN") },
                                    modifier = Modifier.width(100.dp).clip(RoundedCornerShape(8.dp)),
                                    colors = TextFieldDefaults.colors(focusedTextColor = PlatinumClean, unfocusedTextColor = PlatinumClean)
                                )
                                Button(
                                    onClick = { viewModel.submitPinCode(pinCodeInput) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                    enabled = pinCodeInput.length == 4,
                                    modifier = Modifier.height(38.dp)
                                ) {
                                    Text("أدخل الرمز / Enter", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }

                // If cash requested, show the coin drawer inline
                if (method == "cash") {
                    Spacer(modifier = Modifier.height(10.dp))
                    Card(colors = CardDefaults.cardColors(containerColor = CharcoalSlate)) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("ماكينة الكاشير الذكية / Coins Collector Drawer:", fontSize = 11.sp, color = GoldenAmber, fontWeight = FontWeight.Bold)
                            
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("المتبقي: £${String.format("%.2f", totalToPay)}", fontSize = 11.sp, color = PlatinumClean)
                                Text("المدفوع كاش: £${String.format("%.2f", paidSum)}", fontSize = 11.sp, color = Color(0xFF10B981))
                            }

                            // Coins Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                val coins = listOf(0.10 to "10p", 0.50 to "50p", 1.00 to "£1", 2.00 to "£2")
                                coins.forEach { (valDouble, name) ->
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(CharcoalSlate)
                                            .border(1.dp, GoldenAmber, CircleShape)
                                            .clickable { viewModel.addPaymentCoin(valDouble) }
                                            .testTag("supermarket_pay_coin_$name"),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = name, color = GoldenAmber, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            
                            // Notes Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val notes = listOf(5.00 to "£5 Note", 10.00 to "£10 Note")
                                notes.forEach { (valDouble, name) ->
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(34.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFF047857)) // green notes
                                            .clickable { viewModel.addPaymentCoin(valDouble) }
                                            .testTag("supermarket_pay_note_$name"),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = name, color = PlatinumClean, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            if (paymentStatus == "success" || paidSum >= totalToPay) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF065F46).copy(alpha = 0.2f))
                                        .padding(8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("تم سداد المبلغ المطلوب كاش بالكامل! / Paid Cash Fully", color = Color(0xFF10B981), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Button(
                                            onClick = { viewModel.finishSupermarketScenario() },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                            modifier = Modifier.height(36.dp)
                                        ) {
                                            Text("طباعة الفاتورة واستلام الباقي / Get Receipt", fontSize = 11.sp)
                                        }
                                    }
                                }
                            } else {
                                Button(
                                    onClick = { viewModel.clearPaymentCoins() },
                                    colors = ButtonDefaults.buttonColors(containerColor = AccentCrimson),
                                    modifier = Modifier.fillMaxWidth().height(36.dp)
                                ) {
                                    Text("سحب الكاش الملغى / Eject Cash", fontSize = 11.sp, color = PlatinumClean)
                                }
                            }
                        }
                    }
                }
            }
            3 -> {
                // Complete Approved - choice of receipt
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("الخطوة 4: طباعة الفاتورة والرحيل / Step 4: Receipt choices:", fontSize = 11.sp, color = LightSlate)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { viewModel.finishSupermarketScenario() },
                            colors = ButtonDefaults.buttonColors(containerColor = BritishBlue),
                            modifier = Modifier.weight(1f).height(40.dp).testTag("receipt_yes_btn")
                        ) {
                            Text("طباعة الإيصال / Print Receipt", fontSize = 11.sp)
                        }
                        Button(
                            onClick = { viewModel.finishSupermarketScenario() },
                            colors = ButtonDefaults.buttonColors(containerColor = CharcoalSlate),
                            modifier = Modifier.weight(1f).height(40.dp).testTag("receipt_no_btn")
                        ) {
                            Text("لا داعي / No Receipt", fontSize = 11.sp)
                        }
                    }
                }
            }
            4 -> {
                // Completed State
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF065F46).copy(alpha = 0.2f))
                        .border(1.dp, Color(0xFF10B981), RoundedCornerShape(12.dp))
                        .padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(40.dp))
                        Text("Lovely Job, Mate! Challenge Completed!", color = Color(0xFF10B981), fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
                        Text("نلت مكافأة ممتازة +100 XP لمهارات الكاشير والتسوق البريطاني!", color = PlatinumClean, fontSize = 10.sp)
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Button(
                            onClick = { viewModel.resetSupermarket() },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldenAmber),
                            modifier = Modifier.height(40.dp)
                        ) {
                            Text("جولة تسوق جديدة / Next Shopping Trip", color = NavyDeep, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Sainsbury's checkout vocabulary sheet
        Card(
            colors = CardDefaults.cardColors(containerColor = CharcoalSlate),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "مصطلحات السوبرماركت الأساسية / Supermarket Vocab:",
                    fontSize = 12.sp,
                    color = GoldenAmber,
                    fontWeight = FontWeight.Bold
                )
                
                val VocabList = listOf(
                    "Carrier Bag" to "كيس بلاستيكي لحمل المشتريات (يُباع بسعر رمزى كأكياس قابلة للتحلل).",
                    "Clubcard / Nectar Card" to "بطاقة ولاء مجانية تمنحك فحصاً لخصومات هائلة وفورية للمنتج.",
                    "Contactless card payment" to "دفع لاسلكي غير ملامس سريع عبر الهاتف أو الكارت (بحد أقصى £100).",
                    "Chip and PIN payment" to "إدراج البطاقة في الماكينة وكتابة رقمك السري لإتمام الدفع لكافة المبالغ.",
                    "Cashback?" to "سؤال روتيني من الكاشير يسألك لو كنت ترغب بسحب نقود ورقية من الكاشير نفسه.",
                    "Receipt in the bag?" to "سؤال يسألك الكاشير إذا كنت يريد وضع إيصال الشراء داخل أكياسك مباشرة."
                )

                VocabList.forEach { (word, desc) ->
                    Column {
                        Text(text = "🇬🇧 $word", fontSize = 11.sp, color = PlatinumClean, fontWeight = FontWeight.Bold)
                        Text(text = desc, fontSize = 10.sp, color = LightSlate)
                        Divider(color = LightSlate.copy(alpha = 0.1f), modifier = Modifier.padding(vertical = 4.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun AccentTrainerLayout(viewModel: BritishLearningViewModel) {
    val context = LocalContext.current
    val accentWords = viewModel.accentWords
    val selectedWord by viewModel.selectedAccentWord.collectAsStateWithLifecycle()
    val isRecording by viewModel.isRecordingAccent.collectAsStateWithLifecycle()
    val secDuration by viewModel.recordingDurationSec.collectAsStateWithLifecycle()
    val isFeedbackLoading by viewModel.isAccentFeedbackLoading.collectAsStateWithLifecycle()
    val scoreCard by viewModel.accentFeedbackScore.collectAsStateWithLifecycle()
    
    val scope = rememberCoroutineScope()
    
    // Fail-safe robust TTS initialization
    var ttsInitialized by remember { mutableStateOf(false) }
    val tts = remember {
        var textToSpeech: TextToSpeech? = null
        try {
            textToSpeech = TextToSpeech(context.applicationContext) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    ttsInitialized = true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        textToSpeech
    }

    LaunchedEffect(ttsInitialized) {
        if (ttsInitialized) {
            try {
                tts?.language = java.util.Locale.UK
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            try {
                tts?.stop()
                tts?.shutdown()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    var spokenDescriptionInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Welcome and intro banner
        Card(
            colors = CardDefaults.cardColors(containerColor = CharcoalSlate),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "مُدرّب اللكنة والنطق البريطاني / RP Accent Coach 🗣️",
                    fontSize = 13.sp,
                    color = GoldenAmber,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "تدرّب على النطق البريطاني الفاخر ومخارج الحروف مع ميزة الاستماع الصوتي التفاعلي لتجربة اللكنة بنفسك والحصول على تقييم نطق ذكي بالذكاء الاصطناعي!",
                    fontSize = 11.sp,
                    color = LightSlate,
                    lineHeight = 16.sp,
                    textAlign = TextAlign.Right
                )
            }
        }

        // Horizontal selections of words
        Text("اختر كلمة للتدريب والنطق / Choose a Training Word:", fontSize = 11.sp, color = LightSlate)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            accentWords.forEach { item ->
                val isSelected = selectedWord.word == item.word
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) GoldenAmber else CharcoalSlate)
                        .clickable {
                            viewModel.selectedAccentWord.value = item
                            spokenDescriptionInput = ""
                            viewModel.accentFeedbackScore.value = null
                        }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = item.word,
                        color = if (isSelected) NavyDeep else PlatinumClean,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Selected word details
        Card(
            colors = CardDefaults.cardColors(containerColor = CharcoalSlate),
            modifier = Modifier.fillMaxWidth().border(1.dp, GoldenAmber.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(selectedWord.word, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = PlatinumClean)
                        Text("Phonetics: ${selectedWord.ipa}", fontSize = 12.sp, color = LightSlate)
                        Text("Simulated English phonetic: ${selectedWord.simplePronun}", fontSize = 12.sp, color = GoldenAmber, fontFamily = FontFamily.Monospace)
                    }
                    
                    // TTS Audio Speaker Button
                    IconButton(
                        onClick = {
                            try {
                                tts?.speak(selectedWord.word, TextToSpeech.QUEUE_FLUSH, null, null)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        },
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(GoldenAmber)
                            .testTag("speak_tts_btn")
                    ) {
                        Text("🔊", fontSize = 24.sp)
                    }
                }

                Divider(color = LightSlate.copy(alpha = 0.15f))
                
                // Meaning and details
                Text("المعنى بالعربية: ${selectedWord.meaningArabic}", fontSize = 12.sp, color = PlatinumClean)
                
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("قاعدة ومخرج الحروف البريطاني / Accent Rule:", fontSize = 11.sp, color = GoldenAmber, fontWeight = FontWeight.Bold)
                    Text(
                        text = selectedWord.ruleArabic,
                        fontSize = 11.sp,
                        color = PlatinumClean,
                        textAlign = TextAlign.Right,
                        lineHeight = 16.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("جملة تدريبية للتكرار / Example Practice sentence:", fontSize = 11.sp, color = GoldenAmber, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "\"${selectedWord.exampleSentence}\"",
                            fontSize = 11.sp,
                            color = LightSlate,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.weight(1f).padding(end = 6.dp)
                        )
                        IconButton(
                            onClick = {
                                try {
                                    tts?.speak(selectedWord.exampleSentence, TextToSpeech.QUEUE_FLUSH, null, null)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Text("🔊", fontSize = 16.sp)
                        }
                    }
                }
            }
        }

        // Mic recorder UI
        Text("تدرب وسجل نطقك / Record Yourself Speaking:", fontSize = 11.sp, color = LightSlate)
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(CharcoalSlate)
                .padding(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (!isRecording) {
                    IconButton(
                        onClick = {
                            scope.launch {
                                viewModel.isRecordingAccent.value = true
                                viewModel.recordingDurationSec.value = 0
                                repeat(5) {
                                    delay(1000)
                                    viewModel.recordingDurationSec.value += 1
                                }
                            }
                        },
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(AccentCrimson)
                            .testTag("mic_record_btn")
                    ) {
                        Text("🎙️", fontSize = 28.sp)
                    }
                    Text("انقر على الميكروفون لبدء التسجيل والممارسة الصوتي", fontSize = 11.sp, color = LightSlate)
                } else {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Box(modifier = Modifier.size(4.dp, 28.dp).clip(CircleShape).background(AccentCrimson))
                        Box(modifier = Modifier.size(4.dp, 16.dp).clip(CircleShape).background(AccentCrimson))
                        Box(modifier = Modifier.size(4.dp, 40.dp).clip(CircleShape).background(AccentCrimson))
                        Box(modifier = Modifier.size(4.dp, 22.dp).clip(CircleShape).background(AccentCrimson))
                        Box(modifier = Modifier.size(4.dp, 32.dp).clip(CircleShape).background(AccentCrimson))
                    }
                    Text("ميكروفون نشط يسجل الآن... / Recording [00:0$secDuration]", color = AccentCrimson, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    
                    Button(
                        onClick = {
                            viewModel.isRecordingAccent.value = false
                            spokenDescriptionInput = "I've tried saying '${selectedWord.word}' using pure British Received Pronunciation guidelines!"
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentCrimson),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text("وقف التسجيل / Stop Recording", color = PlatinumClean, fontSize = 10.sp)
                    }
                }

                if (spokenDescriptionInput.isNotBlank()) {
                    Divider(color = LightSlate.copy(alpha = 0.15f))
                    Text("كيف كان نطقك الصوتي؟ (تعديل تفاصيل نطقك لمحاكاة ذكاء التقييم):", fontSize = 11.sp, color = LightSlate, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Right)
                    TextField(
                        value = spokenDescriptionInput,
                        onValueChange = { spokenDescriptionInput = it },
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)),
                        colors = TextFieldDefaults.colors(focusedTextColor = PlatinumClean, unfocusedTextColor = PlatinumClean)
                    )
                    
                    Button(
                        onClick = {
                            viewModel.testAccentFeedback(selectedWord.word, spokenDescriptionInput)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldenAmber),
                        modifier = Modifier.fillMaxWidth().height(42.dp),
                        enabled = !isFeedbackLoading
                    ) {
                        if (isFeedbackLoading) {
                            CircularProgressIndicator(color = NavyDeep, modifier = Modifier.size(20.dp))
                        } else {
                            Text("تحقق من تقييم لكنك الأصلي / Evaluation Scorecard", color = NavyDeep, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Scorecard feedback
        scoreCard?.let { scorecard ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0F172A))
                    .border(2.dp, GoldenAmber, RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("بطاقة التقييم الصوتي الذكي / Speech Scorecard 🏆", color = GoldenAmber, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text("Grading: ${scorecard.grading}", color = PlatinumClean, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        
                        Box(
                            modifier = Modifier
                                .size(58.dp)
                                .clip(CircleShape)
                                .background(GoldenAmber),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${scorecard.score}%",
                                color = NavyDeep,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }

                    Divider(color = LightSlate.copy(alpha = 0.15f))
                    
                    Text("مكامن القوة بنطقك / Strengths:", fontSize = 11.sp, color = GoldenAmber, fontWeight = FontWeight.Bold)
                    Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(CharcoalSlate).padding(8.dp)) {
                        Text(scorecard.strengthArabic, fontSize = 11.sp, color = PlatinumClean, textAlign = TextAlign.Right, modifier = Modifier.fillMaxWidth())
                    }

                    Text("نصيحة وخطة التصحيح والتدريب / Corrections:", fontSize = 11.sp, color = AccentCrimson, fontWeight = FontWeight.Bold)
                    Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(CharcoalSlate).padding(8.dp)) {
                        Text(scorecard.feedbackArabic, fontSize = 11.sp, color = PlatinumClean, textAlign = TextAlign.Right, modifier = Modifier.fillMaxWidth(), lineHeight = 16.sp)
                    }

                    Text("Rhythm Metric: ${scorecard.rhythmFeedback}", fontSize = 11.sp, color = LightSlate)
                }
            }
        }
    }
}

// ================= 4. DICTIONARY GLOSSARY SCREEN =================

@Composable
fun DictionaryScreen(viewModel: BritishLearningViewModel) {
    val savedList by viewModel.savedWords.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(GoldenAmber.copy(alpha = 0.2f))
                .padding(14.dp)
        ) {
            Column {
                Text(
                    text = "قاموسي البريطاني الخاص / Bookmarked Words 📖",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldenAmber
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "هنا تجد كافة المفردات والتعبيرات العامية البريطانية التي قمت بحفظها أثناء تصفح المنهج لمراجعتها واستذكار نطقها وتوظيفها الحياتي.",
                    fontSize = 11.sp,
                    color = PlatinumClean,
                    lineHeight = 16.sp,
                    textAlign = TextAlign.Right
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (savedList.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().weight(1f).testTag("dictionary_empty_state"),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Favorite, contentDescription = "Empty dictionary", tint = LightSlate, modifier = Modifier.size(56.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "قاموسك فارغ حالياً!",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = PlatinumClean
                    )
                    Text(
                        text = "انتقل إلى الوحدات واضغط على الكلمات لحفظها هنا لمراجعتها في أي وقت.",
                        fontSize = 11.sp,
                        color = LightSlate,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxSize()) {
                items(savedList) { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(CharcoalSlate)
                            .clickable {
                                // Convert database SavedWord back to temporary LessonWord for display
                                viewModel.activeExplainWord.value = LessonWord(
                                    word = item.word,
                                    wordType = item.wordType,
                                    explanationArabic = item.arabicMeaning,
                                    pronun = item.pronunciation,
                                    synonyms = item.synonyms,
                                    antonyms = item.antonyms,
                                    realUsage = item.britishUsage,
                                    sentenceExample = item.sampleSentence
                                )
                            }
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = item.word,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldenAmber
                            )
                            Text(
                                text = item.pronunciation,
                                fontSize = 11.sp,
                                color = LightSlate
                            )
                            Text(
                                text = item.arabicMeaning,
                                fontSize = 12.sp,
                                color = PlatinumClean
                            )
                        }

                        IconButton(
                            onClick = {
                                viewModel.toggleSavedWord(
                                    LessonWord(
                                        word = item.word,
                                        wordType = item.wordType,
                                        explanationArabic = item.arabicMeaning,
                                        pronun = item.pronunciation
                                    )
                                )
                            },
                            modifier = Modifier.testTag("delete_word_${item.word}")
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Remove word", tint = AccentCrimson)
                        }
                    }
                }
            }
        }
    }
}
