package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.data.database.*
import com.example.network.Content
import com.example.network.GeminiRequest
import com.example.network.Part
import com.example.network.RetrofitClient
import com.example.network.GenerationConfig
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BritishLearningViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val savedWordDao = database.savedWordDao()
    private val chatMessageDao = database.chatMessageDao()
    private val userProgressDao = database.userProgressDao()
    private val userAccountDao = database.userAccountDao()
    private val prefs = application.getSharedPreferences("british_learning_prefs", android.content.Context.MODE_PRIVATE)

    // --- NAVIGATION STATE ---
    var activeTab = MutableStateFlow("lessons") // lessons, chat, money, dictionary

    // --- CURRICULUM STATE ---
    var selectedLesson = MutableStateFlow<LessonItem?>(null)
    var activeQuizIndex = MutableStateFlow(0)
    var selectedQuizAnswer = MutableStateFlow<Int?>(null)
    var isQuizAnswered = MutableStateFlow(false)
    var quizScore = MutableStateFlow(0)
    var quizErrorExplanation = MutableStateFlow("")

    // --- SMART DICTIONARY STATE ---
    val savedWords: StateFlow<List<SavedWord>> = savedWordDao.getAllSavedWords()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    var activeExplainWord = MutableStateFlow<LessonWord?>(null)
    var dictionarySearchQuery = MutableStateFlow("")

    val filteredLessons = dictionarySearchQuery
        .combine(MutableStateFlow(BritishLessonData.lessons)) { query, lessonsList ->
            if (query.isBlank()) {
                lessonsList
            } else {
                lessonsList.filter {
                    it.title.contains(query, ignoreCase = true) ||
                    it.arabicTitle.contains(query, ignoreCase = true) ||
                    it.vocabulary.any { word -> word.word.contains(query, ignoreCase = true) }
                }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), BritishLessonData.lessons)

    // --- CHAT SIMULATION STATE ---
    var activeChatSession = MutableStateFlow("tutor_chat") // tutor_chat, friends_group, london_pub, job_interview, nhs_doctor
    
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val chatMessages: StateFlow<List<ChatMessageEntity>> = activeChatSession
        .flatMapLatest { sessionId ->
            chatMessageDao.getMessagesForSession(sessionId)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    var chatInputText = MutableStateFlow("")
    var isChatApiLoading = MutableStateFlow(false)

    // --- NATURALIZER & CORRECTION AI STATE ---
    var naturalizerInput = MutableStateFlow("")
    var naturalizerOutput = MutableStateFlow("")
    var naturalizerExplanation = MutableStateFlow("")
    var isNaturalizerLoading = MutableStateFlow(false)

    var correctionInput = MutableStateFlow("")
    var correctionFormalOutput = MutableStateFlow("")
    var correctionTextingOutput = MutableStateFlow("")
    var correctionExplanation = MutableStateFlow("")
    var isCorrectionLoading = MutableStateFlow(false)

    // --- MONEY GAME & SUPERMARKET STATE ---
    var activeMoneySubTab = MutableStateFlow("coin_game") // coin_game, supermarket
    var targetPaymentPrice = MutableStateFlow(2.40) // generated in Pounds
    var userPaymentSum = MutableStateFlow(0.0) // current sum paid
    val currentPaymentStatus = MutableStateFlow("") // success, overpaid, or empty
    var itemsName = MutableStateFlow("Biscuit & Coffee")

    // --- SUPERMARKET CHALLENGE STATE ---
    var supermarketStep = MutableStateFlow(0) // 0: Welcome & Bags, 1: Loyalty scanning, 2: Payment options, 3: Completed, 4: Receipt/Finish
    var supermarketBasePrice = MutableStateFlow(9.65) // Scones, Biscuits, Tea, Milk, Crisps
    var supermarketBagsBought = MutableStateFlow(0)
    var supermarketClubcardApplied = MutableStateFlow(false)
    var supermarketTotalToPay = MutableStateFlow(9.65)
    var supermarketPaymentChoice = MutableStateFlow("") // cash, contactless, chip_pin
    var supermarketStatusMsg = MutableStateFlow("Alright mate? Welcome to Sainsbury's self-checkout. Do you need any carrier bags today?")
    var supermarketArabicStatusMsg = MutableStateFlow("مرحباً بك في سوبرماركت سينزبيريز! هل تحتاج إلى أي أكياس تسوق اليوم؟ (سعر الكيس 30p)")

    // --- ACCENT COACH STATE ---
    val accentWords = listOf(
        AccentWord("Water", "ˈwɔː.tər", "WOH-tuh", "ماء", "نبرة غير منطوقة لحرف R في النهاية (Non-rhotic 'r'). انطقها كصدمة ممدودة قصيرة 'وو-تَه' مع حبس مخرج الهواء في الحلق.", "Won't be long, can I grab a glass of water please?"),
        AccentWord("Tuesday", "ˈtjuːz.deɪ", "CHYOOZ-day", "الثلاثاء", "يرتكز النطق البريطاني على قلب حرف T متبوعاً بـ U إلى صوت 'تش'، لتصبح 'تشوزداي' بدلاً من النطق الأمريكي 'توزداي'.", "Shall we meet up for lunch next Tuesday?"),
        AccentWord("Harry Potter", "ˌhær.i ˈpɒt.ər", "HA-ree POH-tuh", "هاري بوتر", "في اللكنة اللندنية العامية يتم استخدام السكتة الحلقية (Glottal Stop) لحرف T في Potter لتصبح 'بو-أه'، والـ R هنا صامتة تماماً.", "Have you read the new Harry Potter book?"),
        AccentWord("Schedule", "ˈʃed.juːl", "SHED-yool", "جدول مواعيد", "في بريطانيا تبدأ الكلمة بصوت 'ش' (Shed) بعكس النطق الأمريكي البادئ بصوت 'سك' (Sked).", "Let me check my study schedule, mate."),
        AccentWord("Can't", "kɑːnt", "KAHNT", "لا أستطيع", "تسمّى (Broad A). يُنطق حرف A ممدوداً مثل 'كآآنت' بعكس الرنين المفلطح بالعامية الأمريكية 'كانت'.", "I'm sorry, I really can't make it to the lecture today."),
        AccentWord("Oryx", "ˈɒr.ɪks", "OH-riks", "المها العربي", "حرف O يتم تدوير الشفاه معه بدقة 'أوه-ريكس' ليكون قصيراً ومؤثراً.", "We saw a majestic Arabian Oryx at the zoo."),
        AccentWord("Schedule Revision", "ˈʃed.juːl rɪˈvɪʒ.ən", "SHED-yool re-VIZH-uhn", "جدولة المراجعة", "استخدام النطق البريطاني SHED-yool مترافقاً مع نطق 'ريفيجن' الصحيح.", "We must schedule revision for our GCSE exam rn!"),
        AccentWord("Cup of Tea", "kʌp əv tiː", "KUP-uh-tee", "كوب شاي", "تسهيل الحروف لتنطق ككلمة واحدة 'كَبّا تِي'. وهي المشروب الوطني البريطاني.", "Fancy a proper cup of tea, bruv?")
    )
    
    var selectedAccentWord = MutableStateFlow(accentWords.first())
    var isRecordingAccent = MutableStateFlow(false)
    var recordingDurationSec = MutableStateFlow(0)
    var isAccentFeedbackLoading = MutableStateFlow(false)
    var accentFeedbackScore = MutableStateFlow<AccentFeedback?>(null)

    // --- USER PROGRESS STATE ---
    val userProgress = userProgressDao.getProgressFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserProgress())

    // --- AUTHENTICATION STATE ---
    val currentUserAccount = MutableStateFlow<UserAccount?>(null)
    val authErrorMessage = MutableStateFlow<String?>(null)
    val isAuthLoading = MutableStateFlow(false)
    val showAuthScreen = MutableStateFlow(true)

    init {
        // Load saved user session
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val savedUser = prefs.getString("logged_in_user", null)
                if (savedUser != null) {
                    val account = userAccountDao.getAccountByUsername(savedUser)
                    if (account != null) {
                        currentUserAccount.value = account
                        showAuthScreen.value = false
                        // Sync local progress table to account progress
                        userProgressDao.saveProgress(
                            UserProgress(
                                id = 1,
                                xpPoints = account.xpPoints,
                                streakDays = account.streakDays,
                                lastActiveTimestamp = account.lastActiveTimestamp,
                                completedLessons = account.completedLessons
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Observe active chat session changes and insert welcome message once if database is empty
        viewModelScope.launch(Dispatchers.IO) {
            try {
                activeChatSession.collectLatest { sessionId ->
                    val hasMessages = chatMessageDao.hasMessagesForSession(sessionId)
                    if (!hasMessages) {
                        try {
                            insertWelcomeMessageForSession(sessionId)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


        try {
            generateNewMoneyTarget()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun registerUser(usernameId: String, emailAddress: String, passwordRaw: String) {
        if (usernameId.isBlank() || emailAddress.isBlank() || passwordRaw.isBlank()) {
            authErrorMessage.value = "يرجى ملء جميع الحقول / Please fill in all fields"
            return
        }
        val trimmedUser = usernameId.trim()
        val trimmedEmail = emailAddress.trim()
        
        viewModelScope.launch(Dispatchers.IO) {
            isAuthLoading.value = true
            authErrorMessage.value = null
            try {
                val exists = userAccountDao.getAccountByUsername(trimmedUser)
                if (exists != null) {
                    authErrorMessage.value = "اسم المستخدم موجود بالفعل / Username already exists"
                    isAuthLoading.value = false
                    return@launch
                }
                
                // Get current guest progress to migrate
                val guestProgress = userProgressDao.getProgress() ?: UserProgress()
                
                val newAccount = UserAccount(
                    username = trimmedUser,
                    email = trimmedEmail,
                    passwordHash = passwordRaw,
                    xpPoints = guestProgress.xpPoints,
                    streakDays = guestProgress.streakDays,
                    lastActiveTimestamp = guestProgress.lastActiveTimestamp,
                    completedLessons = guestProgress.completedLessons
                )
                
                userAccountDao.insertAccount(newAccount)
                prefs.edit().putString("logged_in_user", trimmedUser).apply()
                
                currentUserAccount.value = newAccount
                showAuthScreen.value = false
            } catch (e: Exception) {
                e.printStackTrace()
                authErrorMessage.value = "خطأ أثناء التسجيل: ${e.localizedMessage}"
            } finally {
                isAuthLoading.value = false
            }
        }
    }

    fun loginUser(usernameId: String, passwordRaw: String) {
        if (usernameId.isBlank() || passwordRaw.isBlank()) {
            authErrorMessage.value = "يرجى ملء جميع الحقول / Please fill in all fields"
            return
        }
        val trimmedUser = usernameId.trim()
        
        viewModelScope.launch(Dispatchers.IO) {
            isAuthLoading.value = true
            authErrorMessage.value = null
            try {
                val account = userAccountDao.getAccountByUsername(trimmedUser)
                if (account == null || account.passwordHash != passwordRaw) {
                    authErrorMessage.value = "اسم المستخدم أو كلمة المرور خاطئة / Invalid username or password"
                    isAuthLoading.value = false
                    return@launch
                }
                
                // Sync active user progress with this account
                userProgressDao.saveProgress(
                    UserProgress(
                        id = 1,
                        xpPoints = account.xpPoints,
                        streakDays = account.streakDays,
                        lastActiveTimestamp = account.lastActiveTimestamp,
                        completedLessons = account.completedLessons
                    )
                )
                
                prefs.edit().putString("logged_in_user", trimmedUser).apply()
                currentUserAccount.value = account
                showAuthScreen.value = false
            } catch (e: Exception) {
                e.printStackTrace()
                authErrorMessage.value = "خطأ أثناء تسجيل الدخول: ${e.localizedMessage}"
            } finally {
                isAuthLoading.value = false
            }
        }
    }

    fun logout() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                prefs.edit().remove("logged_in_user").apply()
                currentUserAccount.value = null
                showAuthScreen.value = true
                // Reset active local progress back to clean state
                userProgressDao.saveProgress(UserProgress(id = 1))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun skipAuth() {
        showAuthScreen.value = false
    }

    // --- DATABASE OPERATIONS ---

    fun toggleSavedWord(word: LessonWord) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val exists = savedWordDao.isWordSaved(word.word)
                if (exists) {
                    savedWordDao.deleteWord(
                        SavedWord(
                            word = word.word,
                            arabicMeaning = word.explanationArabic,
                            pronunciation = word.pronun,
                            wordType = word.wordType
                        )
                    )
                } else {
                    savedWordDao.insertWord(
                        SavedWord(
                            word = word.word,
                            arabicMeaning = word.explanationArabic,
                            pronunciation = word.pronun,
                            wordType = word.wordType,
                            synonyms = word.synonyms,
                            antonyms = word.antonyms,
                            britishUsage = word.realUsage,
                            sampleSentence = word.sentenceExample
                        )
                    )
                    addXp(15) // Gain 15 XP for bookmarking vocabulary
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun isWordSaved(word: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                savedWordDao.isWordSaved(word)
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    fun addXp(amount: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val progress = userProgressDao.getProgress() ?: UserProgress()
                val newXp = progress.xpPoints + amount
                val now = System.currentTimeMillis()
                var newStreak = progress.streakDays
                if (progress.lastActiveTimestamp == 0L) {
                    newStreak = 1
                } else {
                    val diffHours = (now - progress.lastActiveTimestamp) / (1000 * 60 * 60)
                    if (diffHours in 18..48) {
                        newStreak += 1
                    } else if (diffHours > 48) {
                        newStreak = 1
                    }
                }
                val updatedProgress = progress.copy(
                    xpPoints = newXp,
                    streakDays = newStreak,
                    lastActiveTimestamp = now
                )
                userProgressDao.saveProgress(updatedProgress)

                currentUserAccount.value?.let { account ->
                    val updatedAccount = account.copy(
                        xpPoints = newXp,
                        streakDays = newStreak,
                        lastActiveTimestamp = now
                    )
                    userAccountDao.insertAccount(updatedAccount)
                    currentUserAccount.value = updatedAccount
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun markLessonCompleted(lessonId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val progress = userProgressDao.getProgress() ?: UserProgress()
                val completed = progress.completedLessons.split(",").filter { it.isNotBlank() }.toMutableSet()
                if (!completed.contains(lessonId)) {
                    completed.add(lessonId)
                    val newListStr = completed.joinToString(",")
                    val updatedProgress = progress.copy(
                        completedLessons = newListStr,
                        xpPoints = progress.xpPoints + 100 // 100 XP bonus for completing whole lesson
                    )
                    userProgressDao.saveProgress(updatedProgress)

                    currentUserAccount.value?.let { account ->
                        val updatedAccount = account.copy(
                            completedLessons = newListStr,
                            xpPoints = account.xpPoints + 100
                        )
                        userAccountDao.insertAccount(updatedAccount)
                        currentUserAccount.value = updatedAccount
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // --- MONEY GAME LOGIC ---

    fun generateNewMoneyTarget() {
        val items = listOf(
            "Biscuit & Brew" to 1.80,
            "London Bus Ticket" to 1.75,
            "Posh Pub Burger" to 12.50,
            "Oyster Card Top-up" to 5.00,
            "Afternoon Tea Scone" to 3.50,
            "Dodgy Umbrella" to 7.99,
            "A Pint of Lager" to 6.20
        )
        val selected = items.random()
        itemsName.value = selected.first
        targetPaymentPrice.value = selected.second
        userPaymentSum.value = 0.0
        currentPaymentStatus.value = ""
    }

    fun addPaymentCoin(value: Double) {
        val nextSum = Math.round((userPaymentSum.value + value) * 100.0) / 100.0
        userPaymentSum.value = nextSum
        checkPaymentStatus()
    }

    fun clearPaymentCoins() {
        userPaymentSum.value = 0.0
        currentPaymentStatus.value = ""
    }

    private fun checkPaymentStatus() {
        val target = targetPaymentPrice.value
        val paid = userPaymentSum.value
        when {
            paid == target -> {
                currentPaymentStatus.value = "success"
                addXp(40) // Correct payment yields 40 XP
            }
            paid > target -> {
                // If they gave note or coins and need change:
                val change = Math.round((paid - target) * 100.0) / 100.0
                currentPaymentStatus.value = "change_needed" // They paid enough but need change
            }
            else -> {
                currentPaymentStatus.value = "insufficient"
            }
        }
    }

    fun completeWithCorrectChange() {
        currentPaymentStatus.value = "success"
        addXp(30)
    }

    // --- RETROFIT / GEMINI NETWORK API LOGIC ---

    private fun getGeminiApiKey(): String {
        return try {
            com.example.BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }
    }

    private suspend fun insertWelcomeMessageForSession(sessionId: String) {
        val welcomeText = when (sessionId) {
            "tutor_chat" -> "Right then, welcome absolute champion! I'm Sir Alistair, your high-class native British Tutor. Let's make you sound like a proper British bloke or lass. Ask me any grammar questions, write a sentence, or say hi!\n\nأهلاً بك يا بطل! أنا سير أليستر، معلمك البريطاني الخاص. اسألني أي سؤال في القواعد، وسأعطيك إجابة ممتعة ممزوجة بالعربية الفصحى العامية الميسرة!"
            "friends_group" -> "Group chat [UK Uni Squad 🇬🇧]\n\nbruv 1: u free tonight? pub rn?\n\nbruv 2: ngl i'm skint tbf... fiver left inside my pocket 😭"
            "school_group" -> "Group chat [London Sixth Form 🎒]\n\nDan: ngl this rev is killing me tbf, who has done the physics hw?? 💀\n\nChloe: ikr!! it's absolute peak. i've got some notes, but idk if they are right. Lewis you revising rn?\n\nLewis: skiving revision tbh, down at the skatepark innit mate 😂"
            "london_pub" -> "Bartender: Alright mate? What can I get for you today? Cash or contactless?\n\nالبارمان: هلاً يا صديقي، ماذا يمكنني أن أقدم لك؟ كاش أم بطاقة؟"
            "job_interview" -> "Interview Panel: Good morning. Thank you for attending this interview at our London office. Could you please introduce your background and why you applied for this position?\n\nلجنة المقابلة: صباح الخير، شكراً لحضورك المقابلة. هل يمكنك تقديم نفسك ولماذا تقدمت لهذه الوظيفة؟"
            "nhs_doctor" -> "NHS GP Doctor: Good afternoon, take a seat. What seems to be the trouble today?\n\nطبيب الصحة الحكومية الـ NHS: مساء الخير، تفضل بالجلوس. من ما تشكو اليوم؟"
            else -> "Hello there, let's learn British English!"
        }
        chatMessageDao.insertMessage(
            ChatMessageEntity(
                sessionId = sessionId,
                role = "model",
                content = welcomeText
            )
        )
    }

    fun sendChatMessage(text: String) {
        if (text.isBlank()) return
        val currentSession = activeChatSession.value
        viewModelScope.launch {
            try {
                // Write user message to DB
                chatMessageDao.insertMessage(
                    ChatMessageEntity(
                        sessionId = currentSession,
                        role = "user",
                        content = text
                    )
                )
                chatInputText.value = ""

                val apiKey = getGeminiApiKey()
                if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
                    // Fallback simulation if no API key is set
                    simulateLocalResponse(currentSession, text)
                    return@launch
                }

                isChatApiLoading.value = true
                try {
                    // Build prompt representing the context
                    val prompt = buildChatPromptForSession(currentSession, text)
                    val responseText = callGeminiRestApi(prompt)

                    chatMessageDao.insertMessage(
                        ChatMessageEntity(
                            sessionId = currentSession,
                            role = "model",
                            content = responseText
                        )
                    )
                    addXp(25) // Interaction reward
                } catch (e: Exception) {
                    simulateLocalResponse(currentSession, text, errorMsg = e.localizedMessage)
                } finally {
                    isChatApiLoading.value = false
                }
            } catch (e: Exception) {
                e.printStackTrace()
                isChatApiLoading.value = false
            }
        }
    }

    private fun buildChatPromptForSession(session: String, userMsg: String): String {
        val baseRule = "You represent a master British English teaching engine. You MUST respond clearly in a mix of authentic British English and Arabic. Follow all rules of British language (use 'colour', 'favourite', 'biscuit', etc.) and explain slang and manners. Respond in under 120 words. User wrote: '$userMsg'."
        return when (session) {
            "tutor_chat" -> "Role: Sir Alistair, a polite but funny native British tutor. Correct any of user's English mistakes. Tell them how British people say it. Be friendly. $baseRule"
            "friends_group" -> "Role: A friend in a UK WhatsApp/Snapchat group Uni chat using texting shortcuts (bruv, mate, rn, idk, ngl, tbf). Respond like a young native British teenager and explain any slang in parenthesis in Arabic. $baseRule"
            "school_group" -> "Role: Dan, Chloe, or Lewis (representing school pupils/students on a London Sixth Form high school group chat). Discuss GCSE/A-Levels, revision, homework, and skiving. Challenge the user to use abbreviations like 'rev' (revision), 'hw' (homework), 'rn' (right now), 'ngl' (not gonna lie), 'tbf' (to be fair), 'tbh' (to be honest), 'ikr' (I know, right?). Respond like a natural informal British teenager texting from London, and explain any slang or abbreviation in standard parenthetical Arabic. $baseRule"
            "london_pub" -> "Role: London Pub Bartender or regular at the pub. Ask user what drink/food they want using prices and terms like 'quid' or contactless payment and manners. Explain in Arabic. $baseRule"
            "job_interview" -> "Role: A professional British corporate Interviewer at a London office. Keep it formal, extremely polite (using terms like 'Could you please', 'delighted'). Help correct their answers into formal professional UK English with Arabic guides of workplace etiquette. $baseRule"
            "nhs_doctor" -> "Role: An NHS GP doctor in London. Polite, helpful, asking about illness, prescribing from the chemist. Show NHS manners and vocabulary. $baseRule"
            else -> baseRule
        }
    }

    private suspend fun callGeminiRestApi(prompt: String): String = withContext(Dispatchers.IO) {
        val request = GeminiRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt)))),
            generationConfig = GenerationConfig(temperature = 0.7f)
        )
        val response = RetrofitClient.apiService.generateContent(getGeminiApiKey(), request)
        response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            ?: "A tourist got lost on the Tube! No connection to London servers. (API Response error)"
    }

    fun submitNaturalizer() {
        val input = naturalizerInput.value
        if (input.isBlank()) return
        viewModelScope.launch {
            isNaturalizerLoading.value = true
            val apiKey = getGeminiApiKey()
            if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
                // Mock native transition or simple static translation
                naturalizerOutput.value = getLocalNaturalizedResult(input)
                naturalizerExplanation.value = "توضيح محلي: تم تحويل عباراتك الرسمية والأمريكية ببراعة إلى لغة الشارع والمقاهي البريطانية مع استبدال الكلمات الرسمية بـ (mate, proper, fiver, biscuit) المناسبة للموقف اليومي."
                isNaturalizerLoading.value = false
                return@launch
            }

            try {
                val prompt = """
                    You are 'The British Spoken Naturalizer'. Convert the following standard textbook English sentence into authentic active British spoken style or British SMS texting:
                    Sentence: "$input"
                    Provide the output in exactly this structured format:
                    Spoken British: (the natural spoken sentence)
                    Arabic Explanation: (Deep detail in Arabic explaining the change, British spelling, slang used, and contractions)
                """.trimIndent()

                val response = callGeminiRestApi(prompt)
                parseNaturalizerResult(response)
                addXp(30)
            } catch (e: Exception) {
                naturalizerOutput.value = "Could I grab some water, mate?"
                naturalizerExplanation.value = "الخادم البريطاني مشغول بتحضير كوب من الشاي! عذراً تعذر الاتصال بـ AI: ${e.localizedMessage}"
            } finally {
                isNaturalizerLoading.value = false
            }
        }
    }

    private fun parseNaturalizerResult(response: String) {
        val spokenPrefix = "Spoken British:"
        val arabicPrefix = "Arabic Explanation:"

        val spokenIndex = response.indexOf(spokenPrefix)
        val arabicIndex = response.indexOf(arabicPrefix)

        if (spokenIndex != -1 && arabicIndex != -1) {
            val spokenPart = response.substring(spokenIndex + spokenPrefix.length, arabicIndex).trim()
            val arabicPart = response.substring(arabicIndex + arabicPrefix.length).trim()
            naturalizerOutput.value = spokenPart
            naturalizerExplanation.value = arabicPart
        } else {
            naturalizerOutput.value = response
            naturalizerExplanation.value = "تمت الصياغة البريطانية بنجاح من المعلم الذكي!"
        }
    }

    fun submitCorrection() {
        val input = correctionInput.value
        if (input.isBlank()) return
        viewModelScope.launch {
            isCorrectionLoading.value = true
            val apiKey = getGeminiApiKey()
            if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
                correctionFormalOutput.value = input.replace("color", "colour").replace("favorite", "favourite").replace("apartment", "flat")
                correctionTextingOutput.value = "proper nice mate innit"
                correctionExplanation.value = "توضيح محلي: تأكد دائماً من كتابة (colour) بحرف U و(favourite) بالـ U، واستبدال الألفاظ الأمريكية بأخرى بريطانية أصيلة لتنال احترام المتحدثين الأصليين بالجزيرة الكبرى."
                isCorrectionLoading.value = false
                return@launch
            }

            try {
                val prompt = """
                    You are the 'British Writing Correction AI Teacher'. Analyse the following sentence for any spelling or grammar mistakes, paying deep attention to American vs British spelling (e.g., color->colour, favorite->favourite, realize->realise) and vocab.
                    Sentence: "$input"
                    Provide the output in exactly this structured format:
                    Formal British: (corrected perfect formal British)
                    Natural Texting: (how a young British native would WhatsApp this sentence using common shortcuts/slang)
                    Arabic Explanation: (Explain in Arabic what mistakes were done and why, highlighting British patterns)
                """.trimIndent()

                val response = callGeminiRestApi(prompt)
                parseCorrectionResult(response)
                addXp(30)
            } catch (e: Exception) {
                correctionFormalOutput.value = "I colour my favourite book."
                correctionTextingOutput.value = "colourin' my fav book rn mate"
                correctionExplanation.value = "تعذر الاتصال بالمعلم البريطاني بسبب مشكلة تقنية: ${e.localizedMessage}"
            } finally {
                isCorrectionLoading.value = false
            }
        }
    }

    private fun parseCorrectionResult(response: String) {
        val formalPrefix = "Formal British:"
        val textingPrefix = "Natural Texting:"
        val arabicPrefix = "Arabic Explanation:"

        val formalIndex = response.indexOf(formalPrefix)
        val textingIndex = response.indexOf(textingPrefix)
        val arabicIndex = response.indexOf(arabicPrefix)

        if (formalIndex != -1 && textingIndex != -1 && arabicIndex != -1) {
            val formalPart = response.substring(formalIndex + formalPrefix.length, textingIndex).trim()
            val textingPart = response.substring(textingIndex + textingPrefix.length, arabicIndex).trim()
            val arabicPart = response.substring(arabicIndex + arabicPrefix.length).trim()

            correctionFormalOutput.value = formalPart
            correctionTextingOutput.value = textingPart
            correctionExplanation.value = arabicPart
        } else {
            correctionFormalOutput.value = response
            correctionTextingOutput.value = "innit mate"
            correctionExplanation.value = "تمت المعالجة البريطانية بدقة."
        }
    }

    private fun getLocalNaturalizedResult(input: String): String {
        val clean = input.lowercase()
        return when {
            clean.contains("cookie") -> "Would you fancy a proper biscuit with your brew, mate?"
            clean.contains("apartment") -> "I've just moved into a grand double-bedroom flat, innit!"
            clean.contains("very tired") -> "I'm absolutely knackered after work today."
            clean.contains("vacation") -> "I'm off on my holidays to Wales next week, cheers!"
            clean.contains("friend") -> "Alright mate? What's going on rn?"
            else -> "Cheers mate! Let's get down to the local pub right now."
        }
    }

    private suspend fun simulateLocalResponse(session: String, userMsg: String, errorMsg: String? = null) {
        delay(1000) // simulated thinking
        val responseText = when (session) {
            "tutor_chat" -> {
                val corrected = userMsg.replace("color", "colour").replace("favorite", "favourite")
                "Alistair: Lovely effort, mate! But watch your language: we spell it with an extra 'u' here in Albion! Let's use '$corrected' naturally in a chat next time. Innit!\n\n(جهد عظيم يا بطل! لكن انتبه للتهجئة البريطانية: نضع حرف U الإضافي دائماً، لتصبح الكلمة تهجئة بريطانية قياسية!)"
            }
            "friends_group" -> "bruv: ngl that sounds mental tbf 💀 but yeah count me in, gonna grab some cheeky biscuits on the way rn mate!"
            "school_group" -> "Chloe: ikr!! Dan always skives physics to be fair, but we rly need to schedule revision for our GCSE exam rn if we wanna pass. tbh I'm proper terrified!\n\n(أنا أعلم، صحيح!! دان يتهرب دائماً من حصة الفيزياء لكي نكون منصفين، ولكننا بحاجة حقاً لجدولة المراجعة لامتحان شهادة الـ GCSE الخاصة بنا الآن إذا أردنا النجاح. سأكون صادقة، أنا خائفة تماماً!)"
            "london_pub" -> "Bartender: That'll be seven quid sixty please, mate. Contactless card or Oyster or what? Cheers!\n\n(الحساب سبعة باوند وستون بنساً يا بطل. هل ستدفع بالكارد أم ماذا؟)"
            "job_interview" -> "Interviewer: Splendid answer. At our company, we highly value queuing etiquette, workplace manners, and showing polite deference with 'Sorry' and 'Excuses'. Can you detail your experience under stress?\n\n(إجابة رائعة ولطيفة. في لندن نُقدّر الانضباط، والتهذيب الشديد ومجاملة الزوار باستمرار.)"
            "nhs_doctor" -> "Doctor: Alright. It seems a bit nasty but don't fret, I'll write a cheeky prescription. Pop down to the local chemist's to pick it up. Drink lots of tea and stay warm!\n\n(حسناً، يظهر أنها وعكة بسيطة. سأكتب لك وصفة طبية اذهب بها للصيدلي المحلي واستعد عافيتك بالشاي الساخن!)"
            else -> "Lovely! Let's keep exploring London mate."
        }
        chatMessageDao.insertMessage(
            ChatMessageEntity(
                sessionId = session,
                role = "model",
                content = if (errorMsg != null) "$responseText\n\n*Note: Simulated offline tutor response.*" else responseText
            )
        )
    }

    fun clearChatHistory() {
        val currentSession = activeChatSession.value
        viewModelScope.launch(Dispatchers.IO) {
            try {
                chatMessageDao.clearSessionChat(currentSession)
                insertWelcomeMessageForSession(currentSession)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // --- NEW SUPERMARKET GAME API ---
    fun selectSupermarketBags(count: Int) {
        supermarketBagsBought.value = count
        recalculateSupermarketTotal()
        
        supermarketStep.value = 1
        supermarketStatusMsg.value = "Splendid. Do you have a Tesco Clubcard or Morrisons loyalty card to scan for discounts today?"
        supermarketArabicStatusMsg.value = "رائع جداً! هل تملك بطاقة ولاء لتسكنها ضوئياً من أجل الحصول على خصومات؟"
    }

    fun applySupermarketClubcard(hasCard: Boolean) {
        supermarketClubcardApplied.value = hasCard
        recalculateSupermarketTotal()
        
        supermarketStep.value = 2
        supermarketStatusMsg.value = "Spot on! That brings your total to £${String.format("%.2f", supermarketTotalToPay.value)}. How would you like to pay today? Contactless, Chip & Pin, or Cash?"
        supermarketArabicStatusMsg.value = "تمام! الحساب الإجمالي يصبح £${String.format("%.2f", supermarketTotalToPay.value)}. كيف تفضل الدفع اليوم؟ الدفع اللاسلكي، البطاقة مع الرقم السري، أم نقداً؟"
    }

    fun chooseSupermarketPayment(method: String) {
        supermarketPaymentChoice.value = method
        if (method == "cash") {
            // Integrate cash screen by prepping the coin game with the exact supermarket price!
            itemsName.value = "Groceries Basket (Scones, Tea, Milk)"
            targetPaymentPrice.value = supermarketTotalToPay.value
            userPaymentSum.value = 0.0
            currentPaymentStatus.value = "insufficient"
            // Switch view internally
            supermarketStatusMsg.value = "Lovely choice. Plonk your coins in the cash terminal below to complete the bill of £${String.format("%.2f", supermarketTotalToPay.value)}!"
            supermarketArabicStatusMsg.value = "اختيار رائع. ضع عملاتك المعدنية والورقية بالأسفل لدفع الحساب الإجمالي المتبقي!"
        } else if (method == "contactless") {
            // Direct Contactless prompt
            supermarketStatusMsg.value = "*Beep!* Contactless payment authorized under £100 limit! Lovely job. Would you like a receipt?"
            supermarketArabicStatusMsg.value = "رنين! تم خصم المبلغ عبر الكارت اللاسلكي بنجاح. هل ترغب في طباعة الإيصال الورقي؟"
            supermarketStep.value = 3
        } else {
            // Chip and pin
            supermarketStatusMsg.value = "Insert your card and type your 4-digit PIN..."
            supermarketArabicStatusMsg.value = "أدخل الكارت في جهاز نقاط البيع واكتب رقمك السري المكون من 4 أرقام بالأسفل..."
        }
    }

    fun submitPinCode(pin: String) {
        if (pin.length == 4) {
            supermarketStatusMsg.value = "*Beep!* PIN accepted. Transaction Approved. Would you like a receipt today?"
            supermarketArabicStatusMsg.value = "رنين! تم قبول الرقم السري والعملية مقبولة بنجاح. هل ترغب في طباعة الإيصال؟"
            supermarketStep.value = 3
        } else {
            supermarketArabicStatusMsg.value = "الرقم السري غير صالح، يرجى كتابة 4 أرقام بالضبط!"
        }
    }

    fun finishSupermarketScenario() {
        supermarketStep.value = 4
        supermarketStatusMsg.value = "There you go mate. Receipt is in the bag. Have a brilliant day, cheers!"
        supermarketArabicStatusMsg.value = "تفضل يا صديقي! الإيصال في الحقيبة. أتمنى لك يوماً سعيداً ومباركاً، في أمان الله دير!"
        addXp(100) // Grant high reward 100 XP
    }

    fun resetSupermarket() {
        supermarketStep.value = 0
        supermarketBagsBought.value = 0
        supermarketClubcardApplied.value = false
        supermarketPaymentChoice.value = ""
        supermarketBasePrice.value = 9.65
        supermarketTotalToPay.value = 9.65
        supermarketStatusMsg.value = "Alright mate? Welcome to Sainsbury's self-checkout. Do you need any carrier bags today?"
        supermarketArabicStatusMsg.value = "مرحباً بك في سوبرماركت سينزبيريز! هل تحتاج إلى أي أكياس تسوق اليوم؟ (سعر الكيس 30p)"
    }

    private fun recalculateSupermarketTotal() {
        var base = supermarketBasePrice.value
        base += supermarketBagsBought.value * 0.30
        if (supermarketClubcardApplied.value) {
            base -= 1.80 // Deduct discount
        }
        supermarketTotalToPay.value = Math.round(base * 100.0) / 100.0
    }

    // --- NEW ACCENT COACH API ---
    fun testAccentFeedback(targetWord: String, spokenAttemptDescription: String) {
        viewModelScope.launch {
            isAccentFeedbackLoading.value = true
            val apiKey = getGeminiApiKey()
            if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
                // Return fun simulated scorecard offline
                delay(1200)
                val isGood = spokenAttemptDescription.isNotBlank() && !spokenAttemptDescription.lowercase().contains("r")
                val scoreVal = if (isGood) (85..97).random() else (50..78).random()
                val gradingVal = if (isGood) "Royal RP Noble" else "Americanized / Needs work"
                accentFeedbackScore.value = AccentFeedback(
                    score = scoreVal,
                    grading = gradingVal,
                    strengthArabic = "رسم مخارج الحروف صحيح وممتاز، خصوصاً عند suppress حرف الـ R أو تدوير الـ O.",
                    feedbackArabic = "نصيحة التدريب: حاول خفض اللسان أكثر في الحلق، وتخفيف نطق الـ T بالوقوف عليها بدلاً من نقرها بقوة. واصل التمرن الصوتي مع ميزة تشغيل النطق المدمجة!",
                    rhythmFeedback = "Good British Cadence, proper stress on the display vowels!"
                )
                isAccentFeedbackLoading.value = false
                addXp(35)
                return@launch
            }

            try {
                val prompt = """
                    You are 'The British Accent Coach'. The user is trying to pronounce the British word/phrase: "$targetWord".
                    They described their phonetic attempt, accent control or feeling as: "$spokenAttemptDescription".
                    Evaluate their pronunciation attempt compared to Received Pronunciation (RP) or standard British English. Keep it encouraging but precise and friendly. Write evaluation in Arabic.
                    Provide the output in exactly this structured, machine-parsable format:
                    Score: (a final score out of 100 as integer)
                    Grading: (a 1-4 word badge e.g. 'Royal RP Noble', 'London Street Cockney', 'Abit Americanized', 'Slightly Muddy')
                    Strengths: (Brief summary in Arabic of what was done nicely)
                    Corrections: (Detailed instructions in Arabic on mouth movements, vowel lengths, glottal stop, or silent 'r' for this word)
                    Rhythm Cadence: (Short sentence in English describing their stress level/timing)
                """.trimIndent()

                val apiOutput = callGeminiRestApi(prompt)
                parseAccentFeedback(apiOutput)
                addXp(35)
            } catch (e: Exception) {
                accentFeedbackScore.value = AccentFeedback(
                    score = 75,
                    grading = "London Regular",
                    strengthArabic = "جهد تدريبي رائع ومحاولة واعدة!",
                    feedbackArabic = "توضيح: واجهنا مشكلة اتصال بالـ AI، لكن تذكر دائماً القواعد الذهبية: سكوت الـ R في نهاية الكلمة ومد الـ A الطويلة.",
                    rhythmFeedback = "Natural flow, keep practicing!"
                )
            } finally {
                isAccentFeedbackLoading.value = false
            }
        }
    }

    private fun parseAccentFeedback(response: String) {
        try {
            var scoreVal = 80
            var gradingVal = "London regular"
            var strengthPart = "نطق الكلمات غني بالمحاولة"
            var correctionsPart = "اضغط على زر التشغيل لتسمع اللفظ مراراً"
            var rhythmPart = "Steady rhythm"

            response.lines().forEach { line ->
                when {
                    line.startsWith("Score:") -> scoreVal = line.removePrefix("Score:").trim().toIntOrNull() ?: 80
                    line.startsWith("Grading:") -> gradingVal = line.removePrefix("Grading:").trim()
                    line.startsWith("Strengths:") -> strengthPart = line.removePrefix("Strengths:").trim()
                    line.startsWith("Corrections:") -> correctionsPart = line.removePrefix("Corrections:").trim()
                    line.startsWith("Rhythm Cadence:") -> rhythmPart = line.removePrefix("Rhythm Cadence:").trim()
                }
            }

            accentFeedbackScore.value = AccentFeedback(
                score = scoreVal,
                grading = gradingVal,
                strengthArabic = strengthPart,
                feedbackArabic = correctionsPart,
                rhythmFeedback = rhythmPart
            )
        } catch (e: Exception) {
            accentFeedbackScore.value = AccentFeedback(
                score = 85,
                grading = "Mundane RP",
                strengthArabic = "مستوى جيد جداً",
                feedbackArabic = "تنقصك بعض التفاصيل البسيطة في التحكم بمجرى الهواء بالحلق.",
                rhythmFeedback = "Excellent timing"
            )
        }
    }
}

data class AccentWord(
    val word: String,
    val ipa: String,
    val simplePronun: String,
    val meaningArabic: String,
    val ruleArabic: String,
    val exampleSentence: String
)

data class AccentFeedback(
    val score: Int,
    val grading: String,
    val strengthArabic: String,
    val feedbackArabic: String,
    val rhythmFeedback: String
)
