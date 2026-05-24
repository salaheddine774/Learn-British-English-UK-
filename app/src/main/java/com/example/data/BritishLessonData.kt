package com.example.data

data class LessonWord(
    val word: String,
    val wordType: String,
    val explanationArabic: String,
    val pronun: String,
    val synonyms: String = "",
    val antonyms: String = "",
    val realUsage: String = "",
    val sentenceExample: String = "",
    val isGrammarContracted: Boolean = false
)

data class LessonQuiz(
    val question: String,
    val options: List<String>,
    val correctAnswerIndex: Int,
    val explanationArabic: String
)

data class LessonItem(
    val id: String,
    val title: String,
    val arabicTitle: String,
    val category: String, // "Grammar" | "Slang" | "Texting & Chat" | "Money & Life" | "Workplace & CV"
    val arabicOverview: String,
    val formulaEnglish: String = "",
    val rulesList: List<String> = emptyList(),
    val britishNativeTips: String = "",
    val vocabulary: List<LessonWord> = emptyList(),
    val quizzes: List<LessonQuiz> = emptyList()
)

object BritishLessonData {
    val lessons: List<LessonItem> = listOf(
        // ================= NOUNS & ARTICLES =================
        LessonItem(
            id = "nouns_articles",
            title = "Nouns & Articles (The, A, An)",
            arabicTitle = "الأسماء وأدوات التعريف والتنكير",
            category = "Grammar",
            arabicOverview = "سنتعلم في هذا الدرس كيفية صياغة الأسماء الجمع والمفرد، الأسماء المعدودة وغير المعدودة، وطريقة التعبير عن الملكية. سنشرح أيضاً الأدوات (A, An, The) بالتفصيل وكيفية توظيفها بالطريقة البريطانية الطبيعية مع تجنب الأخطاء الشائعة.",
            formulaEnglish = "Singular -> Plural | Possession: Owner + 's + Object",
            rulesList = listOf(
                "أداة التنكير 'a' تُستخدم قبل الأسماء المفردة المعدودة التي تبدأ بصوت ساكن (بما في ذلك الحروف الصامتة مثل 'a hotel' حيث يُنطق الـ h).",
                "أداة التنكير 'an' تُستخدم قبل الأسماء المفردة المعدودة التي تبدأ بصوت متحرك (مثل 'an hour' لأن حرف h صامت).",
                "أداة التعريف 'the' تُستخدم للإشارة لشيء محدد ومعروف للسامع. في بريطانيا، لا نستخدمها مع المستشفيات أو المدارس إذا كان الشخص يذهب بغرض الخدمة الأساسية (مثال: 'He is in hospital' وليس 'in the hospital').",
                "الجمع الشاذ والأسماء غير المعدودة: كلمات مثل 'news' و 'information' و 'advice' هي أسماء غير معدودة في الإنجليزية البريطانية، ولا نستخدم معها 'a' (نقول: 'a piece of advice')."
            ),
            britishNativeTips = "في اللهجة البريطانية العامية، كثيراً ما يختصر المتحدثون أدوات التنكير أو يشددون على نطق 'the' كـ 'the' (vocalised) قبل الحروف الصوتية.",
            vocabulary = listOf(
                LessonWord(
                    word = "Biscuit",
                    wordType = "Noun (Countable)",
                    explanationArabic = "بسكويت (تُسمى في الإنجليزية الأمريكية Cookie). جمعها biscuits.",
                    pronun = "بِسْكِتْ /ˈbɪs.kɪt/",
                    synonyms = "Cookie (US), Cracker",
                    antonyms = "N/A",
                    realUsage = "يُستخدم يومياً في بريطانيا مع شرب الشاي (Cup of tea).",
                    sentenceExample = "Would you like a biscuit with your tea, mate?"
                ),
                LessonWord(
                    word = "Flat",
                    wordType = "Noun (Countable)",
                    explanationArabic = "شقة سكنية (تُسمى في الإنجليزية الأمريكية Apartment).",
                    pronun = "فْلَاتْ /flæt/",
                    synonyms = "Apartment (US), Suite",
                    antonyms = "House, Mansion",
                    realUsage = "تُستخدم لوصف الشقق في المدن البريطانية مثل لندن.",
                    sentenceExample = "I've just moved into a lovely double-bedroom flat rn."
                )
            ),
            quizzes = listOf(
                LessonQuiz(
                    question = "أي جملة هي الصحيحة بالأسلوب البريطاني لوصف مريض يتلقى العلاج؟",
                    options = listOf(
                        "My brother is in the hospital.",
                        "My brother is in hospital.",
                        "My brother is in a hospital."
                    ),
                    correctAnswerIndex = 1,
                    explanationArabic = "في الإنجليزية البريطانية، عندما يتلقى شخص العلاج في المستشفى نقول 'in hospital' بدون أداة التعريف 'the'، وهي من الفروقات الشهيرة عن الإنجليزية الأمريكية."
                )
            )
        ),

        // ================= VERBS & TENSES =================
        LessonItem(
            id = "present_simple_cont",
            title = "Present Simple & Continuous with Slang Contractions",
            arabicTitle = "المضارع البسيط والمستمر مع الاختصارات البريطانية",
            category = "Grammar",
            arabicOverview = "يتناول هذا الدرس زمن المضارع البسيط (للعادات والحقائق) والمستمر (للأحداث الجارية حالياً) مستعرضاً الفروقات الصوتية البريطانية وطريقة كتابة الاختصارات في المحادثات اليومية.",
            formulaEnglish = "Simple: S + V(s/es) | Continuous: S + am/is/are + V-ing",
            rulesList = listOf(
                "المضارع البسيط: تُضاف 's' للمفرد الغائب (He, She, It). يستخدم البريطانيون بكثرة ظروف التكرار مثل 'reckon' للتعبير عن الرأي الشخصي.",
                "المضارع المستمر: نستخدمه للأفعال التي تحدث الآن. في المحادثة البريطانية العامية، غالباً لا يُنطق حرف 'g' الأخير في صيغة '-ing' (مثال: 'running' تُنطق 'runnin').",
                "الاختصار والتسهيل الصوتي: يتحول فعل 'going to' في الحديث السريع إلى 'gonna' و 'want to' إلى 'wanna'."
            ),
            britishNativeTips = "في النص والرسائل السريعة البريطانية، يستعيض الشباب بكلمات مثل 'gonna' و'wanna' و'lemme' (let me) كطريقة كتابة معيارية غير رسمية.",
            vocabulary = listOf(
                LessonWord(
                    word = "Reckon",
                    wordType = "Verb",
                    explanationArabic = "يعتقد أو يظن (شائعة جداً في لغة الشارع البريطاني كبديل لـ Think).",
                    pronun = "رِيكُونْ /ˈrɛk.ən/",
                    synonyms = "Think, Believe, Assume",
                    antonyms = "Doubt",
                    realUsage = "تُستخدم في صياغة جمل السؤال وإبداء الآراء الشخصية اليومية.",
                    sentenceExample = "Do you reckon it's gonna rain today? Innit!"
                ),
                LessonWord(
                    word = "Knackered",
                    wordType = "Adjective (Slang)",
                    explanationArabic = "متعب للغاية / منهك جسدياً (بديل لـ Very Tired).",
                    pronun = "نَاكِرْدْ /ˈnæk.əd/",
                    synonyms = "Exhausted, Worn out",
                    antonyms = "Energised, Fresh",
                    realUsage = "تُستعمل للتعبير عن التعب الشديد بعد عناء العمل أو السفر.",
                    sentenceExample = "I've been working all day in London, I'm absolutely knackered!"
                )
            ),
            quizzes = listOf(
                LessonQuiz(
                    question = "كيف يحول البريطاني الجملة الرسمية 'I am going to get some tea' إلى أسلوب كتابة غير رسمي سريع؟",
                    options = listOf(
                        "I am going to get tea mate.",
                        "gonna get some tea rn mate",
                        "I will getting some tea"
                    ),
                    correctAnswerIndex = 1,
                    explanationArabic = "باستخدام 'gonna' بدلاً من 'going to'، واختصار الوقت بـ 'rn' (right now)، وإضافة الصديق 'mate'. هذا هو الأسلوب البريطاني الطبيعي في الرسائل!"
                )
            )
        ),

        // ================= PAST & PERFECT TENSES =================
        LessonItem(
            id = "past_and_perfect",
            title = "Past Simple & Present Perfect (British Nuances)",
            arabicTitle = "الماضي البسيط والمضارع التام والفروق الدقيقة البريطانية",
            category = "Grammar",
            arabicOverview = "في هذا الدرس ستتعلم كيف يعبّر المتحدث البريطاني عن الماضي. الفارق الرئيسي والخطأ الكبير هو الخلط بين الماضي البسيط والتام. البريطانيون يستخدمون المضارع التام بكثرة مع كلمات مثل 'just' و 'already' و 'yet'.",
            formulaEnglish = "Past: Sub + V-ed/Irr | Perfect: Sub + have/has + V3",
            rulesList = listOf(
                "الماضي البسيط: يعبر عن حدث انتهى تماماً في وقت محدد بالماضي. (مثال: 'I went to London yesterday').",
                "المضارع التام: يعبر عن حدث وقع حديثاً وله تأثير على الحاضر. يحب البريطانيون صياغته دوماً مع 'just' (مثال: 'I've just had a biscuit' بدلاً من التعبير الأمريكي 'I just ate a cookie').",
                "التصريف الثالث الشاذ: الأفعال مثل (learn, smell, spell) تتحول بالبريطاني إلى (learnt, smelt, spelt) بانتهاء بحرف 't' عوضاً عن 'ed'."
            ),
            britishNativeTips = "انتبه للفظ التحذيري لـ 'have not' والتي تُختصر لفظاً بالعامية إلى 'haven't' أو تُستبدل بـ 'ain't' في بعض لهجات لندن كـ Cockney.",
            vocabulary = listOf(
                LessonWord(
                    word = "Learnt",
                    wordType = "Verb (Past Form)",
                    explanationArabic = "تعلّم (الصيغة البريطانية القياسية المنتهية بـ t، بينما الأمريكية learned).",
                    pronun = "لِرْنْتْ /lɜːnt/",
                    synonyms = "Learned",
                    antonyms = "Forgot",
                    realUsage = "صيغة الماضي والشرط الثالث للفعل learn.",
                    sentenceExample = "I learnt some proper British slang yesterday, cheers!"
                ),
                LessonWord(
                    word = "Gutted",
                    wordType = "Adjective (Slang)",
                    explanationArabic = "محطم النفسية / خائب الأمل بشدة (تعبير عاطفي بريطاني بامتياز).",
                    pronun = "غَاتِدْ /ˈɡʌt.ɪd/",
                    synonyms = "Devastated, Disappointed",
                    antonyms = "Thrilled, Joyful",
                    realUsage = "تُستخدم عندما يفشل فريقك المفضل في الفوز، أو عندما تُلغى الإجازة.",
                    sentenceExample = "He was absolutely gutted when he failed his driving test."
                )
            ),
            quizzes = listOf(
                LessonQuiz(
                    question = "اختر التهجئة البريطانية الصحيحة للماضي من الفعل 'Spelling' والتركيب الصحيح للتعبير عن حدث وقع للتو:",
                    options = listOf(
                        "I just spelled it.",
                        "I have just spelt it.",
                        "I just spelt it."
                    ),
                    correctAnswerIndex = 1,
                    explanationArabic = "البريطانيون يفضلون التهجئة المنتهية بـ 't' مثل 'spelt'، ويستخدمون المضارع التام 'have just V3' للتعبير عن الأحداث التي وقعت لتوها."
                )
            )
        ),

        // ================= PREPOSITIONS & NEGATION =================
        LessonItem(
            id = "prepositions_negation",
            title = "Polite Prepositions & Spoken Negation",
            arabicTitle = "حروف الجر المهذبة ونظام النفي المحكي",
            category = "Grammar",
            arabicOverview = "يشرح هذا الدرس حروف الجر (In, On, At, By, For, To) بالأسلوب البريطاني التعبيري. سنرى أيضاً أدوات النفي واختصاراتها العامية مثل (won't, shouldn't, did not, shouldn't) وكيف تختلف النبرة البريطانية لتكون شديدة اللطف أو ساخرة.",
            formulaEnglish = "Subject + negative auxiliary + main verb",
            rulesList = listOf(
                "حرف الجر 'At': البريطانيون يفضلون استخدام 'at the weekend' بينما الأمريكيون يقولون 'on the weekend'.",
                "حرف الجر مع المواصلات: نقول 'by train' أو 'by bus' ولكن نقول 'on the Tube' لوصف استخدام مترو أنفاق لندن الشهير.",
                "النفي العادي والمختصر: (do not -> don't), (does not -> doesn't), (did not -> didn't). النفي الأكثر تأدباً هو تلطيف الرفض بعبارة 'I'm afraid I can't' (أخشى أنني لا أستطيع) بدلاً من 'I can't'.",
                "نفي النصيحة: 'shouldn't' تُستخدم لتقديم النصح المهذب بالابتعاد عن فعل شيء."
            ),
            britishNativeTips = "في بريطانيا، استخدام كلمة 'Sorry' هو جزء أساسي ومستمر من الحديث، حتى عند نفي أو رفض عرض ما كنوع من التهذيب القومي المستقر.",
            vocabulary = listOf(
                LessonWord(
                    word = "Dodgy",
                    wordType = "Adjective (Slang)",
                    explanationArabic = "مريب / مشبوه / غير موثوق به (سواء كان شخصاً، مكاناً، أو حتى طعاماً!).",
                    pronun = "دُودْجِي /ˈdɒdʒ.i/",
                    synonyms = "Suspicious, Sketchy, Unreliable",
                    antonyms = "Trustworthy, Safe",
                    realUsage = "تُقال لوصف الأجهزة الخربانة أو الأماكن الخطرة ليلاً أو الصفقات المشبوهة.",
                    sentenceExample = "That corner shop looks a bit dodgy, shouldn't go in there mate."
                ),
                LessonWord(
                    word = "Cheers",
                    wordType = "Interjection",
                    explanationArabic = "شكراً لك / في صحتك (أكثر كلمة مستخدمة يومياً في الشارع البريطاني للشكر الودود).",
                    pronun = "تْشِيرْزْ /tʃɪəz/",
                    synonyms = "Thank you, Thanks, Goodbye",
                    antonyms = "N/A",
                    realUsage = "تُستخدم كبديل سريع ولطيف لـ Thank you عند شراء شيء، أو شكر شخص فتح لك الباب.",
                    sentenceExample = "Cheers for the cup of tea, lovely!"
                )
            ),
            quizzes = listOf(
                LessonQuiz(
                    question = "كيف تقول 'سأخرج للاستجمام في عطلة نهاية الأسبوع' بأسلوب وحروف جر بريطانية أصيلة؟",
                    options = listOf(
                        "I am going out on the weekend.",
                        "I am going out at the weekend.",
                        "I go out during the weekend."
                    ),
                    correctAnswerIndex = 1,
                    explanationArabic = "يصيغ البريطانيون أوقات العطلة الأسبوعية بـ 'at the weekend' حصراً وهي علامة لغوية وطنية راسخة."
                )
            )
        ),

        // ================= BRITISH WRITING & TEXTING =================
        LessonItem(
            id = "british_writing_master",
            title = "UK Chats, SMS Shortcuts & Emoji Culture",
            arabicTitle = "المحادثات واختصارات الرسائل وثقافة الإيموجي",
            category = "Texting & Chat",
            arabicOverview = "سنتعلم في هذا الدرس كيف يكتب ويتراسل الشباب البريطانيون في برامج وتطبيقات المحادثة (WhatsApp, Snapchat, TikTok). كيف يتم اختصار الكلمات الطويلة واستعمال مصطلحات الشارع الإنجليزي السريعة والتحول من الكتابة المدرسية المعقدة إلى الرسائل البرقّية الذكية.",
            formulaEnglish = "Texting: Short spelling + slang particles (rn, idk, tbf, innit)",
            rulesList = listOf(
                "أهم الاختصارات: u (you), ur (your), rn (right now), idk (I don't know).",
                "أدوات ربط الرأي الشائعة: ngl (not gonna lie / لن أكذب)، tbf (to be fair / لكي نكون منصفين)، imo (in my opinion / في رأيي).",
                "الكلمة السحرية للتوكيد: 'innit' وهي اختصار لـ 'isn't it' وتُستخدم في نهاية كل جملة تسعى لتأكيد فكرتك أو نيل موافقة الطرف الآخر.",
                "ثقافة النداء: الـ 'bruv' (أخي) والـ 'mate' (صاحبي) يتكرران في كل رسالة نصية قصيرة تقريباً لتجنب الجمود اللغوي."
            ),
            britishNativeTips = "في بريطانيا، إرسال إيموجي 😭 أو 💀 يُستخدم للتعبير عن الضحك على مواقف محرجة أو سخرية لطيفة بدلاً من الضحك العادي المبالغ فيه.",
            vocabulary = listOf(
                LessonWord(
                    word = "bruv",
                    wordType = "Noun (Slang)",
                    explanationArabic = "أخي / يا صاحبي (اختصار عامي لـ Brother، تتردد بكثرة مع لهجات الشارع في لندن والمدن الكبرى).",
                    pronun = "بْرَافْ /brʌv/",
                    synonyms = "Bro, Brother, Mate",
                    antonyms = "N/A",
                    realUsage = "نداء الشباب لبعضهم خاصة في ألعاب الفيديو ووسائل التواصل والمناطق السكنية الحيوية.",
                    sentenceExample = "idk what's happening rn bruv, ngl!"
                ),
                LessonWord(
                    word = "innit",
                    wordType = "Tag Question (Slang)",
                    explanationArabic = "أليس كذلك؟ (من أشهر علامات الإنجليزية البريطانية على الإطلاق لتأكيد الحديث).",
                    pronun = "إِنِتْ /ˈɪn.ɪt/",
                    synonyms = "Isn't it, Right?",
                    antonyms = "N/A",
                    realUsage = "توضع دائماً في نهاية الجمل لإقرار الحقائق الودية اليومية.",
                    sentenceExample = "This weather is proper rubbish, innit?"
                )
            ),
            quizzes = listOf(
                LessonQuiz(
                    question = "ما معنى جملة 'ngl he is active rn bruv' في لغة الشات البريطانية؟",
                    options = listOf(
                        "لا تكذب هو كسول حالياً وهو أخي.",
                        "لن أكذب، إنه نشط في هذه اللحظة يا صاحبي.",
                        "سعيد بلقائه في الشارع الآن."
                    ),
                    correctAnswerIndex = 1,
                    explanationArabic = "'ngl' تعني 'Not gonna lie' (لن أكذب)، و 'rn' تعني 'Right now' (الآن)، و 'bruv' تعني (يا أخي/يا صاحبي). بالتالي المعنى الدقيق هو خيارنا الثاني!"
                )
            )
        ),

        // ================= PAYMENTS, PRICE & MONEY =================
        LessonItem(
            id = "british_money_system",
            title = "Pounds, Pence & Pub Payments (£ and p)",
            arabicTitle = "الجنيه والإسترليني والعملات وطريقة الدفع",
            category = "Money & Life",
            arabicOverview = "يشرح هذا الدرس كيفية التعامل بنظام العملات النقدية والورقية البريطانية والحديث عنها كالمواطن الأصلي. ستتعرف على كيفية قراءة الأسعار بالصوت العامي السريع وطريقة طلب الحساب في الكافيهات والمقاهي ودفع تذاكر القطارات السريعة وتجنب المواقف الحرجة.",
            formulaEnglish = "£ Price Read: £x.yy -> x Pounds yy (e.g., £3.50 -> three pounds fifty)",
            rulesList = listOf(
                "العملة الوطنية: الجنيه الإسترليني (£ Pound Sterling) ومفرده جنيه، وقسمه البنس (Pence واختصارها p).",
                "العملات النقدية (Coins): تبدأ من 1p, 2p, 5p, 10p, 20p, 50p ثم العملات الذهبية الكبيرة £1, £2.",
                "الأوراق النقدية (Banknotes): هي £5, £10, £20, £50 وتصنع من البلاستيك المتين المقاوم للتلف.",
                "السعر السريع باللفظ: £5.99 تُنطق عامياً 'five pounds ninety-nine' أو بسرعة البرق 'five ninety-nine'.",
                "تعبيرات الشارع المالية: يُطلق المواطن البريطاني كلمة 'quid' كمرادف للجنيه (مثال: '20 quid' تعني 20 جنيهاً). والـ £5 تسمى 'fiver'، والـ £10 تسمى 'tenner'."
            ),
            britishNativeTips = "في بريطانيا، الدفع لا تلامسياً (Contactless) بواسطة الهاتف (Apple/Google Pay) أو الكارد هو النظام السائد بنسبة 95٪، وقلّما تجد مكاناً يشترط الكاش، لدرجة أن الباصات في لندن ترفض النقد نهائياً وتتطلب بطاقة المواصلات Oyster أو كارد البنك.",
            vocabulary = listOf(
                LessonWord(
                    word = "Quid",
                    wordType = "Noun (Slang)",
                    explanationArabic = "جنيه إسترليني (المصطلح العامي الأكثر تداولاً بالمال، ولا يُجمع بـ s، نقول 10 quid).",
                    pronun = "كْوِيدْ /kwɪd/",
                    synonyms = "Pound, Sterling",
                    antonyms = "N/A",
                    realUsage = "تُستخدم في كافة عمليات البيع والشراء غير الرسمية بالشارع والسوق والسينما.",
                    sentenceExample = "That posh burger cost me fifteen quid! Absolute rip-off."
                ),
                LessonWord(
                    word = "Skint",
                    wordType = "Adjective (Slang)",
                    explanationArabic = "مفلس / لا يملك قرشاً واحداً (مرادف لـ Broke).",
                    pronun = "سْكِنْتْ /skɪnt/",
                    synonyms = "Broke, Penniless",
                    antonyms = "Rich, Wealthy, Loaded",
                    realUsage = "تُقال للاعتذار عن مرافقة الأصدقاء للخارج لقلة الميزانية.",
                    sentenceExample = "I can't go to the pub tonight mate, I'm completely skint."
                )
            ),
            quizzes = listOf(
                LessonQuiz(
                    question = "كيف يقرأ البائع الإنجليزي الحساب إذا كان الإجمالي '£12.50' باللغة السريعة؟",
                    options = listOf(
                        "twelve pounds fifty",
                        "twelve point fifty sterling",
                        "twelve quid and fifty halfs"
                    ),
                    correctAnswerIndex = 0,
                    explanationArabic = "يقرأ السعر برقم الجنيهات متبوعاً بالسنتات 'twelve pounds fifty' بدون فواصل لفظية معقدة تسهيلاً للمشتري."
                )
            )
        ),

        // ================= LIFE IN UK & MANNERS =================
        LessonItem(
            id = "uk_life_manners",
            title = "Queuing, British Tea Code & Sarcasm",
            arabicTitle = "طوابير الانتظار، بروتوكول الشاي والتهكم",
            category = "Money & Life",
            arabicOverview = "الحياة في المملكة المتحدة تحكمها قواعد اجتماعية صارمة وغير مكتوبة. في هذا الدرس، ندخل قلب المجتمع البريطاني لشرح ثقافة طوابير الانتظار (Queuing)، قواعد الاعتذار اللانهائية، تحضير الشاي المثالي، وكيفية استخدام السخرية والتهكم الودي (Sarcasm) لكسر الجليد مع السكان المحليين.",
            formulaEnglish = "Social Etiquette: Sarcasm + Apology + Polite Distance",
            rulesList = listOf(
                "طابور الانتظار (The Queue): يعتبر البريطانيون تخطي الطابور خطيئة قومية لا تُغتفر. يجب عليك الوقوف دوماً خلف آخر شخص بانتظام وهدوء.",
                "قاعدة شاي الحليب (English Breakfast Tea): يُقدم الشاي الساخن بإضافة دفقات من الحليب الطازج بعد صب الماء المغلي وتركه يتخمر (Brewing) لدقيقتين على الأقل.",
                "الاعتذار التلقائي (Sorry Culture): يُقال 'Sorry' حتى لو كان غيرك من اصطدم بك في المترو، كشكل من أشكال النبل المتبادل والمحافظة على اللباقة.",
                "السخرية والتهكم (Sarcasm): الأسلوب اللغوي البريطاني يعتمد بكثرة على قول العكس تماماً للتسلية. مثل قول 'Oh brilliant' (أوه يا للروعة) بنظرة مستاءة عند تساقط المطر فجأة."
            ),
            britishNativeTips = "في المقليات واللقاءات الودية، من اللطيف جداً تجنب الأسئلة المباشرة للغاية والمحرجة مثل الراتب أو الوزن، والتركيز عوضاً عن ذلك على حالة الطقس المتقلبة في بريطانيا.",
            vocabulary = listOf(
                LessonWord(
                    word = "Lovely",
                    wordType = "Adjective / Interjection",
                    explanationArabic = "رائع / جميل جداً (الكلمة البريطانية المفضلة لوصف أي شيء مبهج أو موافقة لطيفة على خبر ممتاز).",
                    pronun = "لَافْلِي /ˈlʌv.li/",
                    synonyms = "Wonderful, Great, Beautiful",
                    antonyms = "Horrible, Awful",
                    realUsage = "تُردد مئات المرات لتدعيم المحادثة بالود والمشاعر الإيجابية الخفيفة.",
                    sentenceExample = "Your cup of tea is ready. - Oh, lovely! Cheers, mate."
                ),
                LessonWord(
                    word = "Bloke",
                    wordType = "Noun (Informal)",
                    explanationArabic = "رجل / شاب عادي (مكافئ لكلمة Guy بالإنجليزية الأمريكية).",
                    pronun = "بْلُوكْ /bləʊk/",
                    synonyms = "Guy, Man, Fellow",
                    antonyms = "N/A",
                    realUsage = "تُستخدم لوصف أي رجل مجهول أو معروف في سياقات القصص والحديث مع رفاقك.",
                    sentenceExample = "He is a decent bloke actually, always holds the door for you."
                )
            ),
            quizzes = listOf(
                LessonQuiz(
                    question = "إذا نظر إليك صديقك البريطاني أثناء العواصف والأمطار الغزيرة وقال بابتسامة باردة: 'Brilliant weather today, isn't it?'، فما هو مقصده؟",
                    options = listOf(
                        "هو سعيد جداً بالعواصف وينصحك بالخروج واللعب.",
                        "هو يتهكم ويسخر من الطقس السيئ باستخدام النكتة الجافة والسخرية (Sarcasm).",
                        "يريد التحقق من معرفتك للأرصاد الجوية الإنجليزية."
                    ),
                    correctAnswerIndex = 1,
                    explanationArabic = "البريطانيون أساتذة السخرية الجافة (Sarcasm). إنه يكره الجو العاصف تماماً ولكنه يصفه ساخراً بـ 'Brilliant' وهي طريقة التمسك بروح الفكاهة وسط الغيوم المستمرة."
                )
            )
        ),

        // ================= WORKPLACE & EDUCATION =================
        LessonItem(
            id = "workplace_cv",
            title = "UK Careers, CV Styles & Professional Politeness",
            arabicTitle = "الوظائف في بريطانيا وتصميم السيرة الذاتية المهذب",
            category = "Workplace & CV",
            arabicOverview = "في هذا الدرس سوف تتعلم كيفية شق طريقك في بيئة العمل والتعليم في المملكة المتحدة. سنشرح كيفية صياغة سيرة ذاتية (CV) بهيكل بريطاني مميز، اللياقة اللفظية المطلوبة في مقابلات التوظيف ومخاطبة الأساتذة والمدراء بجدية من غير جلافة.",
            formulaEnglish = "UK CV Layout: Personal Details -> Profile -> Experience -> Education -> Skills -> Refs",
            rulesList = listOf(
                "بنية السيرة الذاتية البريطانية (CV): يفضل البريطانيون سيرة ذاتية واضحة ومباشرة من صفحتين كحد أقصى، وخالية تماماً من صورتك الشخصية وتاريخ ميلادك وجنسيتك لتجنب التحيز وقوانين تكافؤ الفرص.",
                "مخاطبة الأساتذة والمعلمين: في المدارس البريطانية ينادون الأستاذ بـ 'Sir' والأستاذة بـ 'Miss' كدليل احترام كلاسيكي رفيع.",
                "اللياقة في المقابلات الوظيفية: استخدم لغة غير مباشرة وشديدة التهذيب مثل 'I would be delighted to represent' بدلاً من 'I want this job'."
            ),
            britishNativeTips = "في رسائل البريد الإلكتروني الرسمية البريطانية، من اللياقة صياغة التحية الختامية بـ 'Kind regards' أو 'Yours sincerely' إذا كنت تعرف اسم المستلم بدقة.",
            vocabulary = listOf(
                LessonWord(
                    word = "Position",
                    wordType = "Noun (Professional)",
                    explanationArabic = "شاغر وظيفي / منصب عمل مهني.",
                    pronun = "بُوزِيشِنْ /pəˈzɪʃ.ən/",
                    synonyms = "Job vacancy, Role",
                    antonyms = "N/A",
                    realUsage = "تُستخدم في صياغة خطابات التقدم والمراسلات المهنية.",
                    sentenceExample = "I am writing to express my interest in the analyst position at your London office."
                ),
                LessonWord(
                    word = "Miss",
                    wordType = "Noun / Form of Address",
                    explanationArabic = "أستاذة / آنسة (الأداة الرسمية لمناداة المعلمة في المدارس والكليات ببريطانيا).",
                    pronun = "مِسْ /mɪs/",
                    synonyms = "Teacher, Madam",
                    antonyms = "Sir",
                    realUsage = "لخطاب المعلمات باحترام بالبيئة التعليمية.",
                    sentenceExample = "Excuse me Miss, could you explain the exercise on past perfect please?"
                )
            ),
            quizzes = listOf(
                LessonQuiz(
                    question = "أي من التفاصيل التالية يجب تجنب تضمينها في سيرتك الذاتية البريطانية (UK CV) التزاماً بنظام التوظيف الإنجليزي العام؟",
                    options = listOf(
                        "تاريخ وسنوات خبراتك السابقة.",
                        "مستواك التعليمي والمهارات التقنية.",
                        "صورتك الشخصية والنوع الاجتماعي وتاريخ الميلاد."
                    ),
                    correctAnswerIndex = 2,
                    explanationArabic = "قوانين العمل والتوظيف بالمملكة المتحدة تمنع التمييز لذا يُنصح بشدة استبعاد الصور والتواريخ الشخصية والنوع الاجتماعي لضمان تصفية عادلة تعتمد فقط على كفاءتك."
                )
            )
        )
    )
}
