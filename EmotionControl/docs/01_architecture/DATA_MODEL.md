# DATA MODEL (MVP)

## Entities
- UserProfile(id, createdAt, locale, recoveryGoal)
- Habit(id, name, startAt, relapseCount, longestStreakDays)
- DailyPledge(id, date, note, mood)
- DailyReview(id, date, urgeLevel, triggerTags, copingActions, note)
- RelapseEvent(id, occurredAt, triggerTags, reflection, recoveryPlan)
- BlockRule(id, type[url|keyword], pattern, enabled)
- BlockEvent(id, occurredAt, ruleId, reasonTag, actionTaken)
- UnlockRequest(id, requestedAt, durationType, reason, approved)
- AuditLog(id, at, category, payloadMasked)

## Storage policy
- 原則ローカルDB保存
- エクスポート時のみJSON/CSV生成
- 機微情報はマスクしてログ化

## Data lifecycle
- 即時削除: 全データ削除機能
- 保持期間: ユーザー管理（自動削除は将来オプション）
