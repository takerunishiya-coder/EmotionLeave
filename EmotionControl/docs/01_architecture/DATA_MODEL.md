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
- App Store / Google Play公開時も、MVPではアカウントなし、クラウド同期なし、暗号化ローカルDB保存を基本とする。
- relapse理由、triggerTags、Daily Review note、SOS memo、BlockEvent、UnlockRequest reasonはセンシティブデータとして扱う。
- AI分析、広告、分析SDK、クラッシュログへセンシティブデータを渡さない。AI分析で使う場合は明示同意と最小送信を必須にする。
- 詳細方針は `../03_privacy/APP_RELEASE_DATA_INFRASTRUCTURE_SPEC.md` を参照する。

## Data lifecycle
- 即時削除: 全データ削除機能
- 保持期間: ユーザー管理（自動削除は将来オプション）
- AI分析用の要約、特徴量、プロンプト、レスポンス、キャッシュはAI分析用データ削除の対象に含める。
- 将来アカウントを導入する場合は、アプリ内からアカウント削除を開始できるようにする。
