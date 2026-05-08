package com.emotionleave.app.data.export

import com.emotionleave.app.data.repository.ExportSnapshot
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ExportFormatters {
    private val json = Json {
        prettyPrint = true
        encodeDefaults = true
    }

    fun toJson(snapshot: ExportSnapshot): String =
        json.encodeToString(snapshot.toSerializable())

    fun toCsvFiles(snapshot: ExportSnapshot): Map<String, String> =
        mapOf(
            "daily_pledges.csv" to dailyPledgesCsv(snapshot),
            "daily_reviews.csv" to dailyReviewsCsv(snapshot),
            "relapse_events.csv" to relapseEventsCsv(snapshot),
            "sos_sessions.csv" to sosSessionsCsv(snapshot),
        )

    private fun dailyPledgesCsv(snapshot: ExportSnapshot): String =
        csv(
            header = listOf("id", "date", "pledgeText", "avoidancePlan", "mood", "note", "createdAt", "updatedAt"),
            rows = snapshot.dailyPledges.map {
                listOf(
                    it.id,
                    it.date,
                    it.pledgeText,
                    it.avoidancePlan,
                    it.mood,
                    it.note,
                    it.createdAt.toString(),
                    it.updatedAt.toString(),
                )
            },
        )

    private fun dailyReviewsCsv(snapshot: ExportSnapshot): String =
        csv(
            header = listOf(
                "id",
                "date",
                "quickStatus",
                "urgeOccurred",
                "urgeLevel",
                "triggerTags",
                "copingActions",
                "tomorrowAction",
                "note",
                "createdAt",
                "updatedAt",
            ),
            rows = snapshot.dailyReviews.map {
                listOf(
                    it.id,
                    it.date,
                    it.quickStatus,
                    it.urgeOccurred.toString(),
                    it.urgeLevel?.toString().orEmpty(),
                    it.triggerTags.joinToString("|"),
                    it.copingActions.joinToString("|"),
                    it.tomorrowAction,
                    it.note,
                    it.createdAt.toString(),
                    it.updatedAt.toString(),
                )
            },
        )

    private fun relapseEventsCsv(snapshot: ExportSnapshot): String =
        csv(
            header = listOf("id", "occurredAt", "feeling", "triggerTags", "reflection", "nextAction", "rePledgedAt", "createdAt", "updatedAt"),
            rows = snapshot.relapseEvents.map {
                listOf(
                    it.id,
                    it.occurredAt.toString(),
                    it.feeling,
                    it.triggerTags.joinToString("|"),
                    it.reflection,
                    it.nextAction,
                    it.rePledgedAt?.toString().orEmpty(),
                    it.createdAt.toString(),
                    it.updatedAt.toString(),
                )
            },
        )

    private fun sosSessionsCsv(snapshot: ExportSnapshot): String =
        csv(
            header = listOf("id", "startedAt", "endedAt", "completedStep", "selectedActions", "memo", "outcome", "linkedReviewDate", "createdAt", "updatedAt"),
            rows = snapshot.sosSessions.map {
                listOf(
                    it.id,
                    it.startedAt.toString(),
                    it.endedAt?.toString().orEmpty(),
                    it.completedStep,
                    it.selectedActions.joinToString("|"),
                    it.memo,
                    it.outcome,
                    it.linkedReviewDate.orEmpty(),
                    it.createdAt.toString(),
                    it.updatedAt.toString(),
                )
            },
        )

    private fun csv(header: List<String>, rows: List<List<String>>): String =
        buildString {
            appendLine(header.joinToString(",") { it.csvEscaped() })
            rows.forEach { row ->
                appendLine(row.joinToString(",") { it.csvEscaped() })
            }
        }

    internal fun String.csvEscaped(): String {
        val needsQuoting = any { it == ',' || it == '"' || it == '\n' || it == '\r' }
        val escaped = replace("\"", "\"\"")
        return if (needsQuoting) "\"$escaped\"" else escaped
    }
}

@Serializable
private data class SerializableExportSnapshot(
    val exportedAt: Long,
    val userProfiles: List<SerializableUserProfile>,
    val habits: List<SerializableHabit>,
    val dailyPledges: List<SerializableDailyPledge>,
    val dailyReviews: List<SerializableDailyReview>,
    val relapseEvents: List<SerializableRelapseEvent>,
    val sosSessions: List<SerializableSosSession>,
    val settings: SerializableSettings,
)

@Serializable
private data class SerializableUserProfile(
    val id: String,
    val createdAt: Long,
    val startDate: String,
    val locale: String,
    val recoveryGoal: String,
    val displayName: String,
    val reasonValues: List<String>,
    val selectedAvatarId: String,
)

@Serializable
private data class SerializableHabit(
    val id: String,
    val name: String,
    val startAt: Long,
    val relapseCount: Int,
    val longestStreakDays: Int,
    val createdAt: Long,
    val updatedAt: Long,
)

@Serializable
private data class SerializableDailyPledge(
    val id: String,
    val date: String,
    val pledgeText: String,
    val avoidancePlan: String,
    val note: String,
    val mood: String,
    val createdAt: Long,
    val updatedAt: Long,
)

@Serializable
private data class SerializableDailyReview(
    val id: String,
    val date: String,
    val quickStatus: String,
    val urgeOccurred: Boolean,
    val urgeLevel: Int?,
    val triggerTags: List<String>,
    val copingActions: List<String>,
    val tomorrowAction: String,
    val note: String,
    val createdAt: Long,
    val updatedAt: Long,
)

@Serializable
private data class SerializableRelapseEvent(
    val id: String,
    val occurredAt: Long,
    val feeling: String,
    val triggerTags: List<String>,
    val reflection: String,
    val nextAction: String,
    val rePledgedAt: Long?,
    val createdAt: Long,
    val updatedAt: Long,
)

@Serializable
private data class SerializableSosSession(
    val id: String,
    val startedAt: Long,
    val endedAt: Long?,
    val completedStep: String,
    val selectedActions: List<String>,
    val memo: String,
    val outcome: String,
    val linkedReviewDate: String?,
    val createdAt: Long,
    val updatedAt: Long,
)

@Serializable
private data class SerializableSettings(
    val privacyLockEnabled: Boolean,
    val secureScreenEnabled: Boolean,
    val notificationsEnabled: Boolean,
    val morningReminderTime: String,
    val eveningReminderTime: String,
    val neutralNotificationPreviewEnabled: Boolean,
    val aiAnalysisEnabled: Boolean,
    val includeNotesInAiAnalysis: Boolean,
    val includeRelapseRecordsInAiAnalysis: Boolean,
    val preferredExportFormat: String,
)

private fun ExportSnapshot.toSerializable(): SerializableExportSnapshot =
    SerializableExportSnapshot(
        exportedAt = exportedAt,
        userProfiles = userProfiles.map {
            SerializableUserProfile(
                id = it.id,
                createdAt = it.createdAt,
                startDate = it.startDate,
                locale = it.locale,
                recoveryGoal = it.recoveryGoal,
                displayName = it.displayName,
                reasonValues = it.reasonValues,
                selectedAvatarId = it.selectedAvatarId,
            )
        },
        habits = habits.map {
            SerializableHabit(
                id = it.id,
                name = it.name,
                startAt = it.startAt,
                relapseCount = it.relapseCount,
                longestStreakDays = it.longestStreakDays,
                createdAt = it.createdAt,
                updatedAt = it.updatedAt,
            )
        },
        dailyPledges = dailyPledges.map {
            SerializableDailyPledge(
                id = it.id,
                date = it.date,
                pledgeText = it.pledgeText,
                avoidancePlan = it.avoidancePlan,
                note = it.note,
                mood = it.mood,
                createdAt = it.createdAt,
                updatedAt = it.updatedAt,
            )
        },
        dailyReviews = dailyReviews.map {
            SerializableDailyReview(
                id = it.id,
                date = it.date,
                quickStatus = it.quickStatus,
                urgeOccurred = it.urgeOccurred,
                urgeLevel = it.urgeLevel,
                triggerTags = it.triggerTags,
                copingActions = it.copingActions,
                tomorrowAction = it.tomorrowAction,
                note = it.note,
                createdAt = it.createdAt,
                updatedAt = it.updatedAt,
            )
        },
        relapseEvents = relapseEvents.map {
            SerializableRelapseEvent(
                id = it.id,
                occurredAt = it.occurredAt,
                feeling = it.feeling,
                triggerTags = it.triggerTags,
                reflection = it.reflection,
                nextAction = it.nextAction,
                rePledgedAt = it.rePledgedAt,
                createdAt = it.createdAt,
                updatedAt = it.updatedAt,
            )
        },
        sosSessions = sosSessions.map {
            SerializableSosSession(
                id = it.id,
                startedAt = it.startedAt,
                endedAt = it.endedAt,
                completedStep = it.completedStep,
                selectedActions = it.selectedActions,
                memo = it.memo,
                outcome = it.outcome,
                linkedReviewDate = it.linkedReviewDate,
                createdAt = it.createdAt,
                updatedAt = it.updatedAt,
            )
        },
        settings = SerializableSettings(
            privacyLockEnabled = settings.privacyLockEnabled,
            secureScreenEnabled = settings.secureScreenEnabled,
            notificationsEnabled = settings.notificationsEnabled,
            morningReminderTime = settings.morningReminderTime,
            eveningReminderTime = settings.eveningReminderTime,
            neutralNotificationPreviewEnabled = settings.neutralNotificationPreviewEnabled,
            aiAnalysisEnabled = settings.aiAnalysisEnabled,
            includeNotesInAiAnalysis = settings.includeNotesInAiAnalysis,
            includeRelapseRecordsInAiAnalysis = settings.includeRelapseRecordsInAiAnalysis,
            preferredExportFormat = settings.preferredExportFormat,
        ),
    )
