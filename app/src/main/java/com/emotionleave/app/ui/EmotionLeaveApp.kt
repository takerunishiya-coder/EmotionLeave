package com.emotionleave.app.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.emotionleave.app.data.export.ExportCacheCleaner
import com.emotionleave.app.data.export.ExportFormatters
import com.emotionleave.app.data.local.entity.DailyPledgeEntity
import com.emotionleave.app.data.local.entity.DailyReviewEntity
import com.emotionleave.app.data.local.entity.HabitEntity
import com.emotionleave.app.data.local.entity.RelapseEventEntity
import com.emotionleave.app.data.local.entity.SosSessionEntity
import com.emotionleave.app.data.local.entity.UserProfileEntity
import com.emotionleave.app.data.notification.NotificationScheduler
import com.emotionleave.app.data.repository.LocalDataRepository
import com.emotionleave.app.data.settings.AppSettings
import com.emotionleave.app.domain.StreakCalculator
import java.io.File
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Locale
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun EmotionLeaveApp(repository: LocalDataRepository) {
    val settings by repository.observeSettings().collectAsState(initial = AppSettings())
    val profile by repository.observeUserProfile().collectAsState(initial = null)
    val habits by repository.observeHabits().collectAsState(initial = emptyList())
    val pledges by repository.observeDailyPledges().collectAsState(initial = emptyList())
    val reviews by repository.observeDailyReviews().collectAsState(initial = emptyList())
    val relapses by repository.observeRelapseEvents().collectAsState(initial = emptyList())
    val sosSessions by repository.observeSosSessions().collectAsState(initial = emptyList())

    var screen by remember {
        mutableStateOf(if (settings.onboardingComplete) AppScreen.Home else AppScreen.Onboarding)
    }
    var showLevelUpModal by remember { mutableStateOf(false) }
    var showPledgeTutorial by remember { mutableStateOf(false) }
    var showHomeReviewHint by remember { mutableStateOf(false) }
    BackHandler(enabled = settings.onboardingComplete && screen != AppScreen.Home) {
        screen = AppScreen.Home
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        when {
            !settings.onboardingComplete -> OnboardingScreen(
                repository = repository,
                onStartTutorial = {
                    showPledgeTutorial = true
                    screen = AppScreen.Pledge
                },
            )
            else -> Scaffold(
                bottomBar = {
                    if (!showPledgeTutorial) {
                        BottomNav(
                            current = screen,
                            onSelect = { screen = it },
                        )
                    }
                },
            ) { padding ->
                Box(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize(),
                ) {
                    when (screen) {
                        AppScreen.Onboarding -> OnboardingScreen(
                            repository = repository,
                            onStartTutorial = {
                                showPledgeTutorial = true
                                screen = AppScreen.Pledge
                            },
                        )
                        AppScreen.Home -> HomeScreen(
                            profile = profile,
                            habits = habits,
                            pledges = pledges,
                            reviews = reviews,
                            relapseCount = relapses.size,
                            sosCount = sosSessions.size,
                            onOpen = { screen = it },
                        )
                        AppScreen.Pledge -> PledgeScreen(repository = repository, tutorial = showPledgeTutorial) {
                            if (showPledgeTutorial) {
                                showPledgeTutorial = false
                                showHomeReviewHint = true
                            }
                            screen = AppScreen.Home
                        }
                        AppScreen.Review -> ReviewScreen(
                            repository = repository,
                            currentStreakDays = currentStreakDays(profile = profile, habits = habits),
                        ) {
                            screen = AppScreen.Home
                            val totalBeforeReview = pledges.size + reviews.size
                            val alreadyEarnedSevenDayBadge = earnedBadges(
                                pledges = pledges,
                                reviews = reviews,
                                relapses = relapses,
                                sosSessions = sosSessions,
                            ).contains("強固な意志")
                            showLevelUpModal = !alreadyEarnedSevenDayBadge && totalBeforeReview + 1 >= 7
                        }
                        AppScreen.Sos -> SosScreen(repository = repository) {
                            screen = AppScreen.Home
                        }
                        AppScreen.Relapse -> RelapseScreen(
                            repository = repository,
                            profile = profile,
                            currentHabit = habits.firstOrNull(),
                        ) {
                            screen = AppScreen.Home
                        }
                        AppScreen.Records -> RecordsScreen(
                            pledges = pledges,
                            reviews = reviews,
                            relapses = relapses,
                            sosSessions = sosSessions,
                        )
                        AppScreen.Settings -> SettingsScreen(
                            repository = repository,
                            profile = profile,
                            settings = settings,
                            onDeleted = { screen = AppScreen.Onboarding },
                        )
                    }
                }
                if (showHomeReviewHint && screen == AppScreen.Home) {
                    TutorialPopup(
                        message = "夜に今日の振返りをしてください",
                        onClose = { showHomeReviewHint = false },
                    )
                }
                if (showLevelUpModal) {
                    LevelUpModal(
                        avatarId = profile?.selectedAvatarId.orEmpty().ifEmpty { "avatar_jacket" },
                        reducedMotion = false,
                        onClose = { showLevelUpModal = false },
                        onOpenBadges = {
                            showLevelUpModal = false
                            screen = AppScreen.Records
                        },
                    )
                }
            }
        }
    }
}

private enum class AppScreen(val label: String) {
    Onboarding("開始"),
    Home("ホーム"),
    Pledge("✎\n誓い"),
    Review("振り返り"),
    Sos("SOS"),
    Relapse("リスタート"),
    Records("記録"),
    Settings("設定"),
}

@Composable
private fun BottomNav(current: AppScreen, onSelect: (AppScreen) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        listOf(
            AppScreen.Home,
            AppScreen.Sos,
            AppScreen.Records,
            AppScreen.Settings,
        ).forEach { item ->
            val selected = current == item
            if (selected) {
                Button(
                    modifier = Modifier
                        .weight(1f)
                        .height(64.dp),
                    onClick = { onSelect(item) },
                    shape = RoundedCornerShape(16.dp),
                ) {
                    TabContent(item)
                }
            } else {
                OutlinedButton(
                    modifier = Modifier
                        .weight(1f)
                        .height(64.dp),
                    onClick = { onSelect(item) },
                    shape = RoundedCornerShape(16.dp),
                ) {
                    TabContent(item)
                }
            }
        }
    }
}

@Composable
private fun TabContent(item: AppScreen) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(3.dp)) {
        AssetImage(
            path = tabIconPath(item),
            modifier = Modifier.size(34.dp),
            contentDescription = item.label,
        )
        Text(
            item.label,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

private val OnboardingBg = Color(0xFF0F1416)
private val OnboardingSurface = Color(0xFF171D20)
private val OnboardingSurface2 = Color(0xFF20272A)
private val OnboardingText = Color(0xFFF1F4F0)
private val OnboardingSubtle = Color(0xFFB5C0BB)
private val OnboardingMuted = Color(0xFF7F8A86)
private val OnboardingLine = Color.White.copy(alpha = 0.08f)
private val OnboardingSos = Color(0xFFE58A7E)

@Composable
private fun OnboardingScreen(repository: LocalDataRepository, onStartTutorial: () -> Unit) {
    val scope = rememberCoroutineScope()
    var page by remember { mutableStateOf(0) }
    var goal by remember { mutableStateOf("") }
    var selectedAvatarId by remember { mutableStateOf("avatar_jacket") }
    val today = remember { LocalDate.now().toString() }
    val totalPages = 7
    val canNext = page != 4 || goal.isNotBlank()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(OnboardingBg)
            .navigationBarsPadding()
            .imePadding(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 22.dp, vertical = 18.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (page > 0) {
                    TextButton(onClick = { page = (page - 1).coerceAtLeast(0) }) {
                        Text("‹", color = OnboardingMuted, style = MaterialTheme.typography.titleLarge)
                    }
                } else {
                    Spacer(Modifier.size(48.dp))
                }
                if (page < totalPages - 1) {
                    TextButton(onClick = { page = totalPages - 1 }) {
                        Text("スキップ", color = OnboardingMuted, style = MaterialTheme.typography.labelMedium)
                    }
                } else {
                    Spacer(Modifier.size(48.dp))
                }
            }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .pointerInput(page, canNext) {
                    var dragTotal = 0f
                    detectHorizontalDragGestures(
                        onDragStart = { dragTotal = 0f },
                        onHorizontalDrag = { _, dragAmount -> dragTotal += dragAmount },
                        onDragEnd = {
                            when {
                                dragTotal < -80f && page < totalPages - 1 && canNext -> page += 1
                                dragTotal > 80f && page > 0 -> page -= 1
                            }
                            dragTotal = 0f
                        },
                        onDragCancel = { dragTotal = 0f },
                    )
                },
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 18.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                when (page) {
                    0 -> OnboardingIntroPage()
                    1 -> OnboardingMorningPage()
                    2 -> OnboardingNightPage()
                    3 -> OnboardingSosPage()
                    4 -> OnboardingGoalPage(goal = goal, onGoalChange = { goal = it })
                    5 -> OnboardingAvatarPage(
                        selectedAvatarId = selectedAvatarId,
                        onSelect = { selectedAvatarId = it },
                    )
                    else -> OnboardingStartPage()
                }
            }
        }
            OnboardingProgress(page = page, total = totalPages)
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(top = 12.dp),
                enabled = canNext,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color(0xFF0D1410),
                    disabledContainerColor = Color(0xFF2A3133),
                    disabledContentColor = OnboardingMuted,
                ),
                onClick = {
                    if (page < totalPages - 1) {
                        page += 1
                    } else {
                        scope.launch {
                            val now = System.currentTimeMillis()
                            repository.saveUserProfile(
                                UserProfileEntity(
                                    createdAt = now,
                                    startDate = today,
                                    locale = "ja-JP",
                                    recoveryGoal = goal.trim(),
                                    reasonValues = listOf(goal.trim()).filter { it.isNotEmpty() },
                                    selectedAvatarId = selectedAvatarId,
                                ),
                            )
                            repository.saveHabit(
                                HabitEntity(
                                    name = goal.trim().ifEmpty { "目標" },
                                    startAt = now,
                                    createdAt = now,
                                    updatedAt = now,
                                ),
                            )
                            repository.markOnboardingComplete()
                            onStartTutorial()
                        }
                    }
                },
            ) {
                Text(if (page == totalPages - 1) "記録を始める" else "次へ")
            }
        }
    }
}

@Composable
private fun OnboardingProgress(page: Int, total: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        repeat(total) { index ->
            Box(
                modifier = Modifier
                    .padding(horizontal = 3.dp)
                    .size(if (index == page) 9.dp else 7.dp)
                    .clip(CircleShape)
                    .background(
                        if (index == page) MaterialTheme.colorScheme.primary
                        else Color.White.copy(alpha = 0.12f),
                    ),
            )
        }
    }
}

@Composable
private fun OnboardingDesignCard(
    eyebrow: String,
    title: String,
    body: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, OnboardingLine, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = OnboardingBg),
        shape = RoundedCornerShape(20.dp),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            SectionLabel(eyebrow)
            Text(
                title,
                color = OnboardingText,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
            )
            Text(body, color = OnboardingSubtle, style = MaterialTheme.typography.bodyMedium)
            content()
        }
    }
}

@Composable
private fun OnboardingDarkRow(label: String, value: String = "") {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = OnboardingSurface),
        shape = RoundedCornerShape(12.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
            )
            Text(label, color = OnboardingText, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
            if (value.isNotBlank()) {
                Text(value, color = OnboardingSos, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun OnboardingIntroPage() {
    OnboardingDesignCard(
        eyebrow = "01 起動・コンセプト",
        title = "本来なりたい自分に近づくためのアプリです",
        body = "EmotionLeaveは、衝動と上手に距離を取るための、静かな記録アプリです。",
    ) {
        OnboardingDarkRow("毎日10秒のチェックイン")
        OnboardingDarkRow("衝動が来たらSOS")
        OnboardingDarkRow("夜に短く振り返る")
    }
}

@Composable
private fun OnboardingMorningPage() {
    OnboardingDesignCard(
        eyebrow = "02 なぜ毎日記録するのか",
        title = "誓いの設定",
        body = "今日の誓いは、衝動が来た時のために予め自分の行動をデザインしておく記録です。",
    ) {
        OnboardingDarkRow("スマホを遠ざける")
        OnboardingDarkRow("水を飲んで落ち着く")
        OnboardingDarkRow("別の部屋へ移動する")
        Text("完璧に書く必要はありません。10秒で終わります。", color = OnboardingMuted, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun OnboardingNightPage() {
    OnboardingDesignCard(
        eyebrow = "03 なぜ夜に振り返るのか",
        title = "夜の振り返り",
        body = "毎日の自分の行動を振り返り、衝動に駆られやすい時間やきっかけを客観的に見直します。",
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.Bottom) {
            listOf(62.dp, 48.dp, 72.dp, 42.dp).forEachIndexed { index, height ->
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(height)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.72f)),
                    )
                    Text(listOf("夜", "SNS", "疲れ", "休日")[index], color = OnboardingMuted, style = MaterialTheme.typography.labelSmall)
                }
            }
        }
        Text("明日以降の対策にします。", color = OnboardingMuted, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun OnboardingSosPage() {
    OnboardingDesignCard(
        eyebrow = "04 SOSの説明",
        title = "SOSは自分に歯止めをかける支援をします",
        body = "衝動が来たと感じたら、自分が落ち着くために必要な時間を取りましょう。",
    ) {
        OnboardingDarkRow("とりあえずスマホを置き、深呼吸をする", "30秒")
        OnboardingDarkRow("スマホを手から離し、ゆっくり深呼吸する", "1分")
        OnboardingDarkRow("別の部屋・玄関・ベランダへ移動する", "3分")
        Text("SOSは無料で、いつでも使えます。", color = OnboardingMuted, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun OnboardingGoalPage(goal: String, onGoalChange: (String) -> Unit) {
    OnboardingDesignCard(
        eyebrow = "05 目標",
        title = "あなたの目標は？",
        body = "1行で大丈夫です。あとから何度でも変えられます。",
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = goal,
            onValueChange = onGoalChange,
            label = { Text("目標") },
            placeholder = { Text("時間を有効活用し出世・成績を上げる など") },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = OnboardingText,
                unfocusedTextColor = OnboardingText,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = OnboardingLine,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = OnboardingMuted,
                focusedPlaceholderColor = OnboardingMuted,
                unfocusedPlaceholderColor = OnboardingMuted,
                cursorColor = MaterialTheme.colorScheme.primary,
            ),
        )
    }
}

@Composable
private fun OnboardingAvatarPage(selectedAvatarId: String, onSelect: (String) -> Unit) {
    SectionLabel("06 目指す自分像")
    Text("自分のなりたい姿に近いアバターを選択しましょう。", color = OnboardingText, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
    Text("禁欲の果てに目指す姿を共有してください。", color = OnboardingSubtle, style = MaterialTheme.typography.bodyMedium)
    AvatarSelectionGrid(selectedAvatarId = selectedAvatarId, onSelect = onSelect)
}

@Composable
private fun OnboardingStartPage() {
    OnboardingDesignCard(
        eyebrow = "07 開始",
        title = "準備ができました。",
        body = "まずは、今日の誓いを1つだけ入れてみましょう。10秒で終わります。",
    ) {
        OnboardingDarkRow("朝: 今日の誓いを1つ入れる")
        OnboardingDarkRow("日中: 衝動が来たらSOS")
        OnboardingDarkRow("夜: 短く振り返る")
    }
}

@Composable
private fun HomeScreen(
    profile: UserProfileEntity?,
    habits: List<HabitEntity>,
    pledges: List<DailyPledgeEntity>,
    reviews: List<DailyReviewEntity>,
    relapseCount: Int,
    sosCount: Int,
    onOpen: (AppScreen) -> Unit,
) {
    val today = LocalDate.now()
    val habit = habits.firstOrNull()
    val startDate = habit?.startAt?.let { StreakCalculator.epochMillisToLocalDate(it) }
        ?: profile?.startDate?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
    val streakStats = StreakCalculator.homeStats(
        startDate = startDate,
        storedLongestStreakDays = habit?.longestStreakDays ?: 0,
        today = today,
    )
    val pledgeDoneToday = pledges.any { it.date == today.toString() }
    val reviewDoneToday = reviews.any { it.date == today.toString() }
    var nowMillis by remember { mutableStateOf(System.currentTimeMillis()) }
    LaunchedEffect(startDate) {
        while (true) {
            nowMillis = System.currentTimeMillis()
            delay(1_000)
        }
    }
    val reason = profile?.recoveryGoal.orEmpty()
        .ifEmpty { profile?.reasonValues?.firstOrNull().orEmpty() }
        .ifEmpty { "目標" }
    val avatarId = profile?.selectedAvatarId.orEmpty().ifEmpty { "avatar_jacket" }
    val todayPledge = pledges.firstOrNull { it.date == today.toString() }
    val dailyCta = if (pledgeDoneToday) AppScreen.Review else AppScreen.Pledge
    val dailyCtaText = if (pledgeDoneToday) "今日を振り返る" else "今日の誓いを書く"

    ScreenColumn {
        TopBar(title = "EmotionLeave", trailing = if (profile?.displayName?.isNotBlank() == true) profile.displayName else "local")
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)),
            shape = RoundedCornerShape(18.dp),
        ) {
            Row(
                modifier = Modifier.padding(18.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("連続 ${streakStats.currentDays}日継続", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.SemiBold)
                    Text(elapsedCounterText(startDate, nowMillis), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(
                        "連続${streakStats.currentDays}日目 ・ 最長${streakStats.longestDays}日 ・ 累計${streakStats.currentDays}日 ・ 再起回数${habit?.relapseCount ?: relapseCount}回",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                AvatarBadge(avatarId = avatarId, size = 108)
            }
        }

        Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("今日の誓い", fontWeight = FontWeight.SemiBold)
                if (todayPledge == null) {
                    Text("まず1つだけ、危なくなった時の行動を決めます。")
                } else {
                    Text(todayPledge.pledgeText)
                    if (todayPledge.avoidancePlan.isNotBlank()) {
                        BadgePill("危なくなったら", todayPledge.avoidancePlan)
                    }
                    if (todayPledge.note.isNotBlank()) {
                        Text(todayPledge.note, style = MaterialTheme.typography.bodyMedium)
                    }
                }
                StatusRow("夜の振り返り", if (reviewDoneToday) "完了" else "まだ")
                Button(modifier = Modifier.fillMaxWidth(), onClick = { onOpen(dailyCta) }) {
                    Text(dailyCtaText)
                }
            }
        }

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(92.dp),
            onClick = { onOpen(AppScreen.Sos) },
            shape = RoundedCornerShape(18.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp, Alignment.Start),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AssetImage(
                    path = "assets/sos/sos_red_transparent.png",
                    modifier = Modifier.size(68.dp),
                    contentDescription = "SOS",
                )
                Column {
                    Text("SOS", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                    Text("衝動が来たら、まずここをタップ", style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        StatCard(title = "目標", value = reason, body = "衝動が来た時も、この目標だけ短く思い出せます。")
        LatestBadgeRow(
            badgeName = if (sosCount > 0) "立ち止まれた" else "記録の芽",
            plate = if (sosCount > 0) "nagarewokaeru" else "hajimenoippo",
            onClick = { onOpen(AppScreen.Records) },
        )

        OutlinedButton(modifier = Modifier.fillMaxWidth(), onClick = { onOpen(AppScreen.Relapse) }) {
            Text("リスタート")
        }
        Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("ブロッカー（今後実装予定）", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun PledgeScreen(repository: LocalDataRepository, tutorial: Boolean, onDone: () -> Unit) {
    val scope = rememberCoroutineScope()
    var pledge by remember { mutableStateOf("") }
    var plan by remember { mutableStateOf("") }
    var mood by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var selectedPlan by remember { mutableStateOf("水を飲む") }
    var selectedMood by remember { mutableStateOf("落ち着いている") }

    ScreenColumn {
        TopBar(title = "今日の誓い", trailing = "閉じる")
        if (tutorial) {
            TutorialCard("今日の誓いを入れてみましょう、どのようにセルフコントロールしますか？")
        }
        Text("今日の自分に、ひとつ約束します", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = pledge,
            onValueChange = { pledge = it },
            label = { Text("今日の誓い") },
            placeholder = { Text("例: 衝動が来たら一度スマホを置く") },
        )
        SectionLabel("今日の回避プラン")
        ChoiceChips(
            options = listOf("30秒呼吸する", "場所を変える", "水を飲む", "スマホを置く", "SOSを開く"),
            selected = selectedPlan,
            onSelect = {
                selectedPlan = it
                plan = it
            },
        )
        SectionLabel("今の気分 任意")
        MoodChips(
            options = listOf("🙂 落ち着いている", "😟 少し不安", "😴 疲れている", "🌱 前向き"),
            selected = selectedMood,
            onSelect = {
                selectedMood = it
                mood = it
            },
        )
        OutlinedTextField(modifier = Modifier.fillMaxWidth(), value = note, onValueChange = { note = it }, label = { Text("メモ（任意）") })
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                scope.launch {
                    val now = System.currentTimeMillis()
                    repository.saveDailyPledge(
                        DailyPledgeEntity(
                            date = LocalDate.now().toString(),
                            pledgeText = pledge.ifBlank { "衝動が来たら一度スマホを置く" },
                            avoidancePlan = plan.ifBlank { selectedPlan },
                            mood = mood,
                            note = note,
                            createdAt = now,
                            updatedAt = now,
                        ),
                    )
                    onDone()
                }
            },
        ) {
            Text("保存してホームへ")
        }
    }
}

@Composable
private fun ReviewScreen(repository: LocalDataRepository, currentStreakDays: Int, onDone: () -> Unit) {
    val scope = rememberCoroutineScope()
    var urgeOccurred by remember { mutableStateOf(false) }
    var urgeLevel by remember { mutableStateOf("") }
    var triggers by remember { mutableStateOf<Set<String>>(emptySet()) }
    var actions by remember { mutableStateOf<Set<String>>(emptySet()) }
    var note by remember { mutableStateOf("") }
    var completionMessage by remember { mutableStateOf<String?>(null) }

    completionMessage?.let { message ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                Text(message, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
                Button(modifier = Modifier.fillMaxWidth(), onClick = onDone) {
                    Text("閉じる")
                }
            }
        }
        return
    }

    ScreenColumn {
        Text("今日の振り返り", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
        Text("続いた日も、揺れた日も材料になります。")
        SectionLabel("今日は衝動がありましたか？")
        ChoiceChips(
            options = listOf("なし", "少しあり", "強かった"),
            selected = when {
                urgeLevel == "5" -> "強かった"
                urgeOccurred -> "少しあり"
                else -> "なし"
            },
            onSelect = {
                urgeOccurred = it != "なし"
                urgeLevel = if (it == "強かった") "5" else if (it == "少しあり") "2" else ""
            },
        )
        SectionLabel("きっかけに近いもの")
        MultiChoiceChips(
            options = listOf("疲れ", "孤独", "SNS", "夜更かし", "ストレス", "退屈"),
            selected = triggers,
            onChange = { triggers = it },
        )
        SectionLabel("役に立った行動")
        MultiChoiceChips(
            options = listOf("呼吸", "場所を移動", "水を飲む", "メモ", "SOS"),
            selected = actions,
            onChange = { actions = it },
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = note,
            onValueChange = { note = it },
            label = { Text("メモ（任意）") },
            placeholder = { Text("明日やること、決意 など") },
        )
        if (containsCrisisTerm(note)) {
            CrisisHelpCard()
        }
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                scope.launch {
                    val now = System.currentTimeMillis()
                    repository.saveDailyReview(
                        DailyReviewEntity(
                            date = LocalDate.now().toString(),
                            quickStatus = if (urgeOccurred) "揺れた日" else "整った日",
                            urgeOccurred = urgeOccurred,
                            urgeLevel = urgeLevel.toIntOrNull(),
                            triggerTags = triggers.toList(),
                            copingActions = actions.toList(),
                            tomorrowAction = "",
                            note = note,
                            createdAt = now,
                            updatedAt = now,
                        ),
                    )
                    completionMessage = "登録完了！累計連続${currentStreakDays}日の入力"
                }
            },
        ) {
            Text("記録する")
        }
        OutlinedButton(modifier = Modifier.fillMaxWidth(), onClick = onDone) {
            Text("あとで")
        }
    }
}

@Composable
private fun SosScreen(repository: LocalDataRepository, onDone: () -> Unit) {
    val scope = rememberCoroutineScope()
    var selectedActions by remember { mutableStateOf<Set<String>>(emptySet()) }
    var activeTimer by remember { mutableStateOf<SosTimerSpec?>(null) }
    var secondsLeft by remember { mutableStateOf(0) }
    var elapsedMessage by remember { mutableStateOf("") }
    var done by remember { mutableStateOf(false) }
    val timerOptions = listOf(
        SosTimerSpec(
            title = "30秒",
            seconds = 30,
            focusTitle = "まずは手を止めて、深呼吸をしましょう",
            focusBody = "スマホを置き、肩の力を抜いて、呼吸を3回だけ数えます。",
        ),
        SosTimerSpec(
            title = "1分",
            seconds = 60,
            focusTitle = "スマホから少し離れましょう",
            focusBody = "スマホを手から離し、ゆっくり深呼吸してください。",
        ),
        SosTimerSpec(
            title = "3分",
            seconds = 180,
            focusTitle = "場所を変えて、流れを切り替えましょう",
            focusBody = "スマホを置いて、別の部屋・玄関・ベランダへ移動してください。気持ちが落ち着いたら戻ってきましょう。",
        ),
    )

    LaunchedEffect(activeTimer) {
        activeTimer?.let { timer ->
            elapsedMessage = ""
            secondsLeft = timer.seconds
            while (secondsLeft > 0) {
                delay(1_000)
                secondsLeft -= 1
            }
            elapsedMessage = "${timer.title}経過しました"
        }
    }

    if (done) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                Text(
                    "衝動が来たことに気が付き、回避ができましたね！",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                )
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    scope.launch {
                        val now = System.currentTimeMillis()
                        repository.saveSosSession(
                            SosSessionEntity(
                                startedAt = now,
                                endedAt = now,
                                completedStep = selectedActions.lastOrNull().orEmpty(),
                                selectedActions = selectedActions.toList(),
                                memo = "",
                                outcome = "立ち止まれた",
                                createdAt = now,
                                updatedAt = now,
                            ),
                        )
                        onDone()
                    }
                },
            ) {
                Text("ホームに戻る")
            }
            }
        }
        return
    }

    activeTimer?.let { timer ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF111817))
                .padding(20.dp),
            contentAlignment = Alignment.Center,
        ) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .then(if (elapsedMessage.isNotBlank()) Modifier.blur(6.dp) else Modifier),
                ) {
                    FocusTimerCard(
                        secondsLeft = secondsLeft,
                        title = timer.focusTitle,
                        body = timer.focusBody,
                        elapsedMessage = elapsedMessage,
                        onStop = {
                            selectedActions = selectedActions + timer.title
                            activeTimer = null
                            done = true
                        },
                    )
                }
                if (elapsedMessage.isNotBlank()) {
                    TimerElapsedPopup(elapsedMessage)
                }
            }
        }
        return
    }

    ScreenColumn {
        Text("SOS", style = MaterialTheme.typography.labelLarge)
        Text("大丈夫です、自分を取り戻しましょう", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
        Text("自分を取り戻すために必要な時間をタップしてください")
        timerOptions.forEach { option ->
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                onClick = {
                    elapsedMessage = ""
                    activeTimer = option
                },
            ) {
                Text(option.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun RelapseScreen(
    repository: LocalDataRepository,
    profile: UserProfileEntity?,
    currentHabit: HabitEntity?,
    onDone: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    var feeling by remember { mutableStateOf("") }
    var triggers by remember { mutableStateOf("") }
    var reflection by remember { mutableStateOf("") }
    val today = LocalDate.now()
    val previousStart = currentHabit?.startAt?.let { StreakCalculator.epochMillisToLocalDate(it) }
        ?: profile?.startDate?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
    val restartPreview = StreakCalculator.relapseRestartStats(
        previousStartDate = previousStart,
        storedLongestStreakDays = currentHabit?.longestStreakDays ?: 0,
        storedRelapseCount = currentHabit?.relapseCount ?: 0,
        today = today,
    )

    ScreenColumn {
        Text("再スタート", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
        Text("ここまで続けたことは残っています。")
        Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                StatusRow("最長記録", "${restartPreview.newLongestStreakDays}日")
                StatusRow("累計", "${restartPreview.finishedStreakDays}日")
                StatusRow("再開回数", "${restartPreview.newRelapseCount}回")
                StatusRow("新しい記録", "今日から")
                Text("ここまでの記録バッジは残っています", style = MaterialTheme.typography.bodySmall)
            }
        }
        SectionLabel("起きたことを、責めずに記録します")
        Text("これは採点ではありません。今の状態を残すためのメモです。")
        SectionLabel("今の感情")
        ChoiceChips(
            options = listOf("悔しい", "疲れた", "焦っている", "ほっとした", "よく分からない"),
            selected = feeling,
            onSelect = { feeling = it },
        )
        SectionLabel("きっかけ")
        ChoiceChips(
            options = listOf("夜更かし", "SNS", "一人の時間", "ストレス", "退屈", "その他"),
            selected = triggers,
            onSelect = { triggers = it },
        )
        OutlinedTextField(modifier = Modifier.fillMaxWidth(), value = reflection, onValueChange = { reflection = it }, label = { Text("短く振り返る（任意）") })
        if (containsCrisisTerm(feeling) || containsCrisisTerm(reflection)) {
            CrisisHelpCard()
        }
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                scope.launch {
                    val now = System.currentTimeMillis()
                    val restartStats = StreakCalculator.relapseRestartStats(
                        previousStartDate = previousStart,
                        storedLongestStreakDays = currentHabit?.longestStreakDays ?: 0,
                        storedRelapseCount = currentHabit?.relapseCount ?: 0,
                        today = today,
                    )
                    repository.saveRelapseEvent(
                        RelapseEventEntity(
                            occurredAt = now,
                            feeling = feeling,
                            triggerTags = splitTags(triggers),
                            reflection = reflection,
                            nextAction = "",
                            rePledgedAt = now,
                            createdAt = now,
                            updatedAt = now,
                        ),
                    )
                    currentHabit?.let { habit ->
                        repository.saveHabit(
                            habit.copy(
                                startAt = now,
                                relapseCount = restartStats.newRelapseCount,
                                longestStreakDays = restartStats.newLongestStreakDays,
                                updatedAt = now,
                            ),
                        )
                    }
                    profile?.let {
                        repository.saveUserProfile(it.copy(startDate = today.toString()))
                    }
                    onDone()
                }
            },
        ) {
            Text("再誓約する")
        }
    }
}

@Composable
private fun RecordsScreen(
    pledges: List<DailyPledgeEntity>,
    reviews: List<DailyReviewEntity>,
    relapses: List<RelapseEventEntity>,
    sosSessions: List<SosSessionEntity>,
) {
    var selectedEntry by remember { mutableStateOf<RecordEntry?>(null) }
    var showSummary by remember { mutableStateOf(false) }
    val entries = remember(pledges, reviews, relapses, sosSessions) {
        buildRecordEntries(
            pledges = pledges,
            reviews = reviews,
            relapses = relapses,
            sosSessions = sosSessions,
        )
    }

    ScreenColumn {
        Text("記録", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
        Text("獲得した記録バッジと、日々の記録を見返せます。")
        val badges = earnedBadges(
                pledges = pledges,
                reviews = reviews,
                relapses = relapses,
                sosSessions = sosSessions,
            )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SummaryCell("獲得済み", "${badges.size}", Modifier.weight(1f))
            SummaryCell("記録", "${entries.size}", Modifier.weight(1f))
            SummaryCell("SOS", "${sosSessions.size}", Modifier.weight(1f))
        }
        SectionLabel("獲得済み(${badges.size})")
        AchievementGrid(
            badges = badges,
        )
        SectionLabel("これから")
        AchievementGrid(
            badges = upcomingBadges(badges),
            locked = true,
        )
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showSummary = !showSummary },
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("記録のサマリ", fontWeight = FontWeight.SemiBold)
                    Text(if (showSummary) "閉じる" else "開く")
                }
                if (showSummary) {
                    if (entries.isEmpty()) {
                        Text("誓い、振り返り、SOS、リスタートの記録がここに並びます。")
                    } else {
                        entries.forEach { entry ->
                            Card(
                                Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedEntry = entry },
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp),
                                ) {
                                    Text(entry.dateLabel, style = MaterialTheme.typography.labelLarge)
                                    Text(entry.title, fontWeight = FontWeight.SemiBold)
                                    if (entry.detail.isNotBlank()) {
                                        Text(entry.detail, style = MaterialTheme.typography.bodyMedium)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    selectedEntry?.let { entry ->
        AlertDialog(
            onDismissRequest = { selectedEntry = null },
            title = { Text(entry.title) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(entry.dateLabel)
                    Text(entry.detail.ifBlank { "短い記録だけが残っています。" })
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedEntry = null }) {
                    Text("閉じる")
                }
            },
        )
    }
}

@Composable
private fun ProfileEditorDialog(
    profile: UserProfileEntity?,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit,
) {
    var selectedAvatarId by remember { mutableStateOf(profile?.selectedAvatarId.orEmpty().ifEmpty { "avatar_jacket" }) }
    var goal by remember { mutableStateOf(profile?.reasonValues?.firstOrNull().orEmpty().ifEmpty { "集中" }) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("プロフィールを変更") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SectionLabel("目指す自分像")
                AvatarSelectionGrid(
                    selectedAvatarId = selectedAvatarId,
                    onSelect = { selectedAvatarId = it },
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = goal,
                    onValueChange = { goal = it },
                    label = { Text("目標") },
                    placeholder = { Text("例: 彼女を作りたい") },
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onSave(selectedAvatarId, goal) }) {
                Text("保存")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("キャンセル")
            }
        },
    )
}

@Composable
private fun NotificationSettingsCard(
    settings: AppSettings,
    onNotificationsChanged: (Boolean) -> Unit,
    onMorningTimeChanged: (String) -> Unit,
    onEveningTimeChanged: (String) -> Unit,
    onNeutralPreviewChanged: (Boolean) -> Unit,
) {
    SectionLabel("通知管理")
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            LabeledCheckbox("通知を使う", settings.notificationsEnabled, onNotificationsChanged)
            Text("朝の誓いと夜の振り返りを、端末の通知で思い出せます。")
            ReminderTimeRow(
                title = "朝の誓い",
                current = settings.morningReminderTime.ifBlank { "08:00" },
                options = listOf("07:00", "08:00", "09:00"),
                onSelect = onMorningTimeChanged,
            )
            ReminderTimeRow(
                title = "夜の振り返り",
                current = settings.eveningReminderTime.ifBlank { "21:00" },
                options = listOf("20:00", "21:00", "22:00"),
                onSelect = onEveningTimeChanged,
            )
            LabeledCheckbox(
                label = "通知文を具体的に表示する",
                checked = settings.neutralNotificationPreviewEnabled,
                onCheckedChange = onNeutralPreviewChanged,
            )
            Text(
                "OFFにすると通知文は「今日の記録を残せます」だけになります。端末の状態により配信時刻は前後します。",
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
private fun ReminderTimeRow(
    title: String,
    current: String,
    options: List<String>,
    onSelect: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("$title: $current", fontWeight = FontWeight.SemiBold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            options.forEach { option ->
                val selected = option == current
                if (selected) {
                    Button(modifier = Modifier.weight(1f), onClick = { onSelect(option) }) {
                        Text(option)
                    }
                } else {
                    OutlinedButton(modifier = Modifier.weight(1f), onClick = { onSelect(option) }) {
                        Text(option)
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsScreen(
    repository: LocalDataRepository,
    profile: UserProfileEntity?,
    settings: AppSettings,
    onDeleted: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showProfileEditor by remember { mutableStateOf(false) }
    var exportStatus by remember { mutableStateOf("") }
    val formatter = remember { ExportFormatters() }
    val notificationScheduler = remember(context) { NotificationScheduler(context) }
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        scope.launch {
            repository.updateNotificationsEnabled(granted)
            if (granted) {
                notificationScheduler.scheduleDailyReminders(settings.copy(notificationsEnabled = true))
            } else {
                notificationScheduler.cancelDailyReminder()
            }
        }
    }

    ScreenColumn {
        Text("設定", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
        Text("データはこの端末に保存されます。")
        Card(
            Modifier
                .fillMaxWidth()
                .clickable { showProfileEditor = true },
            shape = RoundedCornerShape(16.dp),
        ) {
            Row(
                Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AvatarBadge(avatarId = profile?.selectedAvatarId.orEmpty().ifEmpty { "avatar_jacket" }, size = 86)
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("ローカルプロフィール", fontWeight = FontWeight.SemiBold)
                    Text("目指す自分像: ${avatarLabel(profile?.selectedAvatarId.orEmpty().ifEmpty { "avatar_jacket" })}")
                    Text("目標: ${profile?.reasonValues?.firstOrNull() ?: "集中"}を取り戻す", style = MaterialTheme.typography.bodySmall)
                }
                Text("›", style = MaterialTheme.typography.titleLarge)
            }
        }
        SectionLabel("表示と演出")
        LabeledCheckbox("画面保護を使う", settings.secureScreenEnabled) {
            scope.launch { repository.updateSecureScreenEnabled(it) }
        }
        NotificationSettingsCard(
            settings = settings,
            onNotificationsChanged = { enabled ->
                if (enabled) {
                    if (context.canPostNotifications()) {
                        scope.launch {
                            val updated = settings.copy(notificationsEnabled = true)
                            repository.updateNotificationsEnabled(true)
                            notificationScheduler.scheduleDailyReminders(updated)
                        }
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                } else {
                    scope.launch {
                        repository.updateNotificationsEnabled(false)
                        notificationScheduler.cancelDailyReminder()
                    }
                }
            },
            onMorningTimeChanged = { time ->
                scope.launch {
                    val updated = settings.copy(morningReminderTime = time)
                    repository.updateMorningReminderTime(time)
                    if (updated.notificationsEnabled) notificationScheduler.scheduleDailyReminders(updated)
                }
            },
            onEveningTimeChanged = { time ->
                scope.launch {
                    val updated = settings.copy(eveningReminderTime = time)
                    repository.updateEveningReminderTime(time)
                    if (updated.notificationsEnabled) notificationScheduler.scheduleDailyReminders(updated)
                }
            },
            onNeutralPreviewChanged = { enabled ->
                scope.launch {
                    repository.updateNeutralNotificationPreviewEnabled(enabled)
                }
            },
        )
        SectionLabel("データ管理")
        Text("エクスポートを選んだ時だけJSON/CSVファイルを作成します。")
        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                scope.launch {
                    val snapshot = repository.createExportSnapshot(System.currentTimeMillis())
                    val json = formatter.toJson(snapshot)
                    shareExport(context = context, json = json)
                    exportStatus = "エクスポートファイルを作成しました。共有先での扱いに注意してください。"
                }
            },
        ) {
            Text("データをエクスポート")
        }
        if (exportStatus.isNotBlank()) {
            Text("作成したファイルはアプリのロック対象外です。保存先での扱いに注意してください。")
            Card(Modifier.fillMaxWidth()) {
                Text(
                    modifier = Modifier.padding(12.dp),
                    text = exportStatus,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
        OutlinedButton(modifier = Modifier.fillMaxWidth(), onClick = { showDeleteDialog = true }) {
            Text("すべてのデータを削除")
        }
        Text("このアプリは自己管理と習慣改善を支援するためのものです。診断、治療、医療上の助言を行うものではありません。")
    }

    if (showProfileEditor) {
        ProfileEditorDialog(
            profile = profile,
            onDismiss = { showProfileEditor = false },
            onSave = { avatarId, goal ->
                scope.launch {
                    val current = profile ?: UserProfileEntity(
                        createdAt = System.currentTimeMillis(),
                        locale = "ja-JP",
                    )
                    repository.saveUserProfile(
                        current.copy(
                            selectedAvatarId = avatarId,
                            reasonValues = listOf(goal).filter { it.isNotBlank() },
                        ),
                    )
                    showProfileEditor = false
                }
            },
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("すべてのデータを削除しますか？") },
            text = { Text("端末内の記録と設定を削除します。この操作は取り消せません。") },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            notificationScheduler.cancelDailyReminder()
                            clearExportCache(context)
                            repository.deleteAllLocalData()
                            exportStatus = ""
                            showDeleteDialog = false
                            onDeleted()
                        }
                    },
                ) {
                    Text("削除する")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("キャンセル")
                }
            },
        )
    }
}

@Composable
private fun ScreenColumn(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        content = content,
    )
}

@Composable
private fun TopBar(title: String, trailing: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Text(trailing, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.SemiBold,
    )
}

@Composable
private fun TutorialCard(message: String) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(message) {
        delay(120)
        visible = true
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = if (visible) 0.16f else 0.08f),
        ),
        shape = RoundedCornerShape(18.dp),
    ) {
        Text(
            modifier = Modifier.padding(16.dp),
            text = message,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun TutorialPopup(message: String, onClose: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.18f))
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(message, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
                Button(modifier = Modifier.fillMaxWidth(), onClick = onClose) {
                    Text("閉じる")
                }
            }
        }
    }
}

@Composable
private fun StatCard(title: String, value: String, body: String) {
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(title, style = MaterialTheme.typography.labelLarge)
            Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.SemiBold)
            Text(body, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun InfoCard(title: String, body: String) {
    Card(
        Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.08f)),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(title, fontWeight = FontWeight.SemiBold)
            Text(body, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun SummaryCell(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
    ) {
        Column(
            Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(value, fontWeight = FontWeight.SemiBold)
            Text(label, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun StatusRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label)
        Text(value, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun SosActionRow(title: String, body: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
    ) {
        Row(
            Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center,
            ) {
                Text("•", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(title, fontWeight = FontWeight.SemiBold)
                Text(body, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun MoodChips(
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
) {
    ChoiceChips(options = options, selected = selected, onSelect = onSelect)
}

@Composable
private fun ChoiceChips(
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        options.chunked(2).forEach { rowOptions ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                rowOptions.forEach { option ->
                    val isSelected = selected == option
                    if (isSelected) {
                        Button(
                            modifier = Modifier.weight(1f),
                            onClick = { onSelect(option) },
                            shape = RoundedCornerShape(14.dp),
                        ) {
                            Text(option, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    } else {
                        OutlinedButton(
                            modifier = Modifier.weight(1f),
                            onClick = { onSelect(option) },
                            shape = RoundedCornerShape(14.dp),
                        ) {
                            Text(option, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }
                }
                if (rowOptions.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun MultiChoiceChips(
    options: List<String>,
    selected: Set<String>,
    onChange: (Set<String>) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        options.chunked(2).forEach { rowOptions ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                rowOptions.forEach { option ->
                    val isSelected = option in selected
                    val onClick = {
                        onChange(if (isSelected) selected - option else selected + option)
                    }
                    if (isSelected) {
                        Button(
                            modifier = Modifier.weight(1f),
                            onClick = onClick,
                            shape = RoundedCornerShape(14.dp),
                        ) {
                            Text(option, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    } else {
                        OutlinedButton(
                            modifier = Modifier.weight(1f),
                            onClick = onClick,
                            shape = RoundedCornerShape(14.dp),
                        ) {
                            Text(option, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }
                }
                if (rowOptions.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun FocusTimerCard(
    secondsLeft: Int,
    title: String,
    body: String,
    elapsedMessage: String,
    onStop: () -> Unit,
) {
    val context = LocalContext.current
    LaunchedEffect(elapsedMessage) {
        if (elapsedMessage.isNotBlank()) {
            context.vibrateBriefly()
        }
    }
    Card(
        Modifier.fillMaxSize(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF111817)),
        shape = RoundedCornerShape(0.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(title, color = Color(0xFFDDEBE7), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(24.dp))
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF182320))
                    .border(22.dp, Color(0xFF2F9E7E).copy(alpha = 0.55f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(formatTimer(secondsLeft), style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.SemiBold, color = Color(0xFFEAF5F2))
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(body, color = Color(0xFFB9C8C4), style = MaterialTheme.typography.titleMedium)
            if (elapsedMessage.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0xFFEAF5F2).copy(alpha = 0.16f))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(elapsedMessage, color = Color(0xFFEAF5F2), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(modifier = Modifier.fillMaxWidth(), onClick = onStop) {
                Text("終了する")
            }
        }
    }
}

@Composable
private fun TimerElapsedPopup(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.32f))
            .padding(28.dp),
        contentAlignment = Alignment.Center,
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFEAF5F2).copy(alpha = 0.92f)),
            shape = RoundedCornerShape(24.dp),
        ) {
            Text(
                modifier = Modifier.padding(22.dp),
                text = message,
                color = Color(0xFF111817),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun AchievementGrid(badges: List<String>, locked: Boolean = false) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        badges.chunked(2).forEach { rowBadges ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                rowBadges.forEach { badge ->
                    BadgeTile(
                        modifier = Modifier.weight(1f),
                        badgeName = badge,
                        plate = badgePlateForName(badge),
                        locked = locked,
                    )
                }
                if (rowBadges.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun BadgeTile(
    modifier: Modifier,
    badgeName: String,
    plate: String,
    locked: Boolean,
) {
    Card(modifier = modifier, shape = RoundedCornerShape(16.dp)) {
        Column(
            Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            AssetImage(
                path = "assets/title_badges/title_${plate}.png",
                modifier = Modifier
                    .height(62.dp)
                    .fillMaxWidth(),
                contentDescription = badgeName,
            )
            Text(if (locked) "？？？" else badgeName, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(badgeCondition(badgeName), style = MaterialTheme.typography.labelSmall, color = if (locked) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
private fun LatestBadgeRow(
    badgeName: String,
    plate: String,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
    ) {
        Row(
            Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AssetImage(
                path = "assets/title_badges/title_${plate}.png",
                modifier = Modifier.size(56.dp),
                contentDescription = badgeName,
            )
            Column(Modifier.weight(1f)) {
                Text(badgeName, fontWeight = FontWeight.SemiBold)
                Text("最新の記録バッジ", style = MaterialTheme.typography.bodySmall)
            }
            Text("›", style = MaterialTheme.typography.titleLarge)
        }
    }
}

@Composable
private fun AvatarSelectionGrid(
    selectedAvatarId: String,
    onSelect: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        avatarOptions.chunked(2).forEach { rowOptions ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                rowOptions.forEach { option ->
                    AvatarOptionCard(
                        modifier = Modifier.weight(1f),
                        option = option,
                        selected = selectedAvatarId == option.id,
                        onSelect = { onSelect(option.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun AvatarOptionCard(
    modifier: Modifier,
    option: AvatarOption,
    selected: Boolean,
    onSelect: () -> Unit,
) {
    val borderColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)
    OutlinedButton(
        modifier = modifier
            .height(132.dp)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp)),
        onClick = onSelect,
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            AvatarBadge(avatarId = option.id, size = 72)
            Text(option.label, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun SmallAvatarPreview(selectedAvatarId: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AvatarBadge(avatarId = selectedAvatarId.ifEmpty { "avatar_jacket" }, size = 44)
        Text("Homeではこのくらい小さく表示されます", style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun AvatarBadge(avatarId: String, size: Int, modifier: Modifier = Modifier) {
    val option = avatarOptions.firstOrNull { it.id == avatarId } ?: avatarOptions.first()
    Box(
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(option.color.copy(alpha = 0.18f))
            .border(1.dp, option.color.copy(alpha = 0.45f), CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        AssetImage(
            path = "assets/avatars/${option.fileName}",
            modifier = Modifier
                .size((size - 8).coerceAtLeast(32).dp)
                .clip(CircleShape),
            contentDescription = option.label,
        )
    }
}

@Composable
private fun BadgePill(label: String, value: String, modifier: Modifier = Modifier.fillMaxWidth()) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.10f))
            .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = MaterialTheme.typography.labelLarge)
        Text(value, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun AssetImage(
    path: String,
    modifier: Modifier,
    contentDescription: String?,
) {
    val context = LocalContext.current
    val image = remember(path) {
        runCatching {
            context.assets.open(path).use { stream ->
                BitmapFactory.decodeStream(stream)?.asImageBitmap()
            }
        }.getOrNull()
    }
    if (image != null) {
        Image(
            bitmap = image,
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = ContentScale.Fit,
        )
    } else {
        Box(
            modifier = modifier.background(MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)),
            contentAlignment = Alignment.Center,
        ) {
            Text("画像", style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun LevelUpModal(
    avatarId: String,
    reducedMotion: Boolean,
    onClose: () -> Unit,
    onOpenBadges: () -> Unit,
) {
    var frameIndex by remember(avatarId, reducedMotion) { mutableStateOf(if (reducedMotion) 4 else 0) }
    LaunchedEffect(avatarId, reducedMotion) {
        if (!reducedMotion) {
            val delays = listOf(120L, 110L, 110L, 220L, 140L)
            frameIndex = 0
            delays.forEachIndexed { index, delayMillis ->
                delay(delayMillis)
                frameIndex = (index + 1).coerceAtMost(levelUpFrames.lastIndex)
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.28f))
            .clickable(onClick = onClose),
        contentAlignment = Alignment.Center,
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .clickable(enabled = false) {},
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Text("記録バッジが増えました", style = MaterialTheme.typography.labelLarge)
                Text("7日間達成!!", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.SemiBold)
                AssetImage(
                    path = "assets/title_badges/title_kyoukonaishi.png",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentDescription = "7日分の記録",
                )
                Box(contentAlignment = Alignment.Center) {
                    Box(
                        modifier = Modifier
                            .size(210.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)),
                    )
                    AssetImage(
                        path = "assets/level_up/$avatarId/${levelUpFrames[frameIndex]}.png",
                        modifier = Modifier.size(200.dp),
                        contentDescription = "レベルアップアバター",
                    )
                }
                Text("新しい記録バッジを手に入れました。", fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(modifier = Modifier.weight(1f), onClick = onClose) {
                        Text("閉じる")
                    }
                    Button(modifier = Modifier.weight(1f), onClick = onOpenBadges) {
                        Text("記録へ")
                    }
                }
            }
        }
    }
}

@Composable
private fun LabeledCheckbox(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(checked = checked, onCheckedChange = onCheckedChange)
        Text(label)
    }
}

private fun splitTags(value: String): List<String> =
    value.split(",")
        .map { it.trim() }
        .filter { it.isNotEmpty() }

private fun elapsedCounterText(startDate: LocalDate?, nowMillis: Long): String {
    val zone = ZoneId.systemDefault()
    val startMillis = (startDate ?: LocalDate.now())
        .atStartOfDay(zone)
        .toInstant()
        .toEpochMilli()
    val totalSeconds = ((nowMillis - startMillis) / 1_000).coerceAtLeast(0)
    val days = totalSeconds / 86_400
    val hours = (totalSeconds % 86_400) / 3_600
    val minutes = (totalSeconds % 3_600) / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.JAPAN, "%d日 %02d:%02d:%02d", days, hours, minutes, seconds)
}

private fun earnedBadges(
    pledges: List<DailyPledgeEntity>,
    reviews: List<DailyReviewEntity>,
    relapses: List<RelapseEventEntity>,
    sosSessions: List<SosSessionEntity>,
): List<String> {
    val totalRecords = pledges.size + reviews.size
    val badges = mutableListOf("はじめの一歩")
    if (sosSessions.isNotEmpty()) badges += "流れを変える"
    if (totalRecords >= 7) badges += "強固な意志"
    if (totalRecords >= 14) badges += "弱さとの決別"
    if (totalRecords >= 30) badges += "黄金の精神"
    if (relapses.isNotEmpty()) badges += "強者"
    return badges.distinct()
}

private fun upcomingBadges(earned: List<String>): List<String> =
    listOf("強固な意志", "弱さとの決別", "黄金の精神", "漢")
        .filterNot { it in earned }

private fun badgePlateForName(name: String): String =
    when (name) {
        "はじめの一歩" -> "hajimenoippo"
        "強固な意志" -> "kyoukonaishi"
        "強者" -> "kyousya"
        "流れを変える" -> "nagarewokaeru"
        "漢" -> "otoko"
        "黄金の精神" -> "ougonnoseishin"
        "弱さとの決別" -> "yowasatonoketsubetsu"
        else -> "hajimenoippo"
    }

private fun badgeCondition(name: String): String =
    when (name) {
        "はじめの一歩" -> "初回設定を完了"
        "流れを変える" -> "SOSで立ち止まった"
        "強固な意志" -> "7日分の記録"
        "弱さとの決別" -> "14日分の記録"
        "黄金の精神" -> "30日分の記録"
        "強者" -> "リスタートを記録"
        "漢" -> "さらに長い継続の節目"
        else -> "達成条件"
    }

private fun tabIconPath(item: AppScreen): String =
    when (item) {
        AppScreen.Home -> "assets/tab_icons/icon_home_transparent.png"
        AppScreen.Sos -> "assets/sos/sos_red_transparent.png"
        AppScreen.Records -> "assets/tab_icons/icon_kiroku_transparent.png"
        AppScreen.Settings -> "assets/tab_icons/setting_transparent.png"
        else -> "assets/tab_icons/icon_home_transparent.png"
    }

private fun currentStreakDays(profile: UserProfileEntity?, habits: List<HabitEntity>): Int {
    val startDate = habits.firstOrNull()?.startAt?.let { StreakCalculator.epochMillisToLocalDate(it) }
        ?: profile?.startDate?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
    return StreakCalculator.homeStats(
        startDate = startDate,
        storedLongestStreakDays = habits.firstOrNull()?.longestStreakDays ?: 0,
    ).currentDays
}

private fun Context.vibrateBriefly() {
    runCatching {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            manager.defaultVibrator.vibrate(VibrationEffect.createOneShot(180, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(180, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(180)
            }
        }
    }
}

private fun formatTimer(seconds: Int): String =
    String.format(Locale.JAPAN, "%02d:%02d", seconds / 60, seconds % 60)

private data class AvatarOption(
    val id: String,
    val label: String,
    val initial: String,
    val color: Color,
    val fileName: String,
)

private data class SosTimerSpec(
    val title: String,
    val seconds: Int,
    val focusTitle: String,
    val focusBody: String,
)

private val avatarOptions = listOf(
    AvatarOption("avatar_jacket", "ジャケット", "J", Color(0xFF2F6B5F), "avatar_jacket.png"),
    AvatarOption("avatar_centerpart", "センターパート", "C", Color(0xFF456A8A), "avatar_centerpart.png"),
    AvatarOption("avatar_suit", "スーツ", "S", Color(0xFF6A5A7A), "avatar_suit.png"),
    AvatarOption("avatar_kinniku", "アクティブ", "A", Color(0xFF8A6B3E), "avatar_kinniku.png"),
)

private val levelUpFrames = listOf(
    "frame_00_idle",
    "frame_01_fist_ready",
    "frame_02_raise",
    "frame_03_peak",
    "frame_04_settle",
)

private fun avatarLabel(avatarId: String): String =
    avatarOptions.firstOrNull { it.id == avatarId }?.label ?: "ジャケット"

private data class RecordEntry(
    val sortKey: Long,
    val dateLabel: String,
    val title: String,
    val detail: String,
)

private fun buildRecordEntries(
    pledges: List<DailyPledgeEntity>,
    reviews: List<DailyReviewEntity>,
    relapses: List<RelapseEventEntity>,
    sosSessions: List<SosSessionEntity>,
): List<RecordEntry> {
    val pledgeEntries = pledges.map { pledge ->
        RecordEntry(
            sortKey = localDateSortKey(pledge.date),
            dateLabel = pledge.date,
            title = "今日の誓い",
            detail = listOf(pledge.pledgeText, pledge.avoidancePlan)
                .filter { it.isNotBlank() }
                .joinToString(" / "),
        )
    }
    val reviewEntries = reviews.map { review ->
        RecordEntry(
            sortKey = localDateSortKey(review.date),
            dateLabel = review.date,
            title = review.quickStatus.ifBlank { "今日の振り返り" },
            detail = listOf(
                review.tomorrowAction,
                review.triggerTags.takeIf { it.isNotEmpty() }?.joinToString(prefix = "きっかけ: "),
            )
                .filterNotNull()
                .filter { it.isNotBlank() }
                .joinToString(" / "),
        )
    }
    val relapseEntries = relapses.map { relapse ->
        RecordEntry(
            sortKey = relapse.occurredAt,
            dateLabel = epochMillisDateLabel(relapse.occurredAt),
            title = "リスタート記録",
            detail = relapse.reflection.ifBlank { "今の状態を残した記録です。" },
        )
    }
    val sosEntries = sosSessions.map { session ->
        RecordEntry(
            sortKey = session.startedAt,
            dateLabel = epochMillisDateLabel(session.startedAt),
            title = "SOSを使えた日",
            detail = session.selectedActions.joinToString(" / ").ifBlank { session.outcome },
        )
    }

    return (pledgeEntries + reviewEntries + relapseEntries + sosEntries)
        .sortedByDescending { it.sortKey }
}

private fun localDateSortKey(value: String): Long =
    runCatching { LocalDate.parse(value).toEpochDay() * MILLIS_PER_DAY }
        .getOrDefault(0L)

private fun epochMillisDateLabel(value: Long): String =
    java.time.Instant.ofEpochMilli(value)
        .atZone(java.time.ZoneId.systemDefault())
        .toLocalDate()
        .toString()

private const val MILLIS_PER_DAY = 86_400_000L

@Composable
private fun CrisisHelpCard() {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("今はひとりで抱え込まないでください", fontWeight = FontWeight.SemiBold)
            Text("強い不安や自分を傷つけたい気持ちがある場合は、地域の緊急窓口や信頼できる人に連絡してください。")
            Text("このアプリは医療的な診断や治療ではありません。")
        }
    }
}

private fun containsCrisisTerm(value: String): Boolean {
    val terms = listOf("死にたい", "消えたい", "自殺", "自傷", "傷つけたい", "もう無理")
    return terms.any { value.contains(it, ignoreCase = true) }
}

private fun shareExport(context: Context, json: String) {
    val exportDir = File(context.cacheDir, "exports").apply { mkdirs() }
    val file = File(exportDir, "emotionleave-export-${System.currentTimeMillis()}.json")
    file.writeText(json, Charsets.UTF_8)
    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file,
    )
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "application/json"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(
        Intent.createChooser(intent, "エクスポート先を選択"),
    )
}

private fun clearExportCache(context: Context) {
    ExportCacheCleaner.clearFromCacheDir(context.cacheDir)
}

private fun Context.canPostNotifications(): Boolean =
    Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
