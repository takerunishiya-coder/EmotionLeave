# Screen Flow

```mermaid
flowchart TD
    Splash["Splash"] --> Onboarding["Onboarding"]
    Onboarding --> GoalSetup["Goal Setup"]
    GoalSetup --> FirstPledge["First Daily Pledge"]
    FirstPledge --> Home["Home"]

    Home --> DailyPledge["Daily Pledge"]
    Home --> DailyReview["Daily Review"]
    Home --> SOS["SOS"]
    Home --> ReasonCard["Reason Re-display"]
    Home --> Calendar["Calendar"]
    Home --> Insights["Insights"]
    Home --> Settings["Settings"]

    DailyPledge --> Home
    DailyReview --> Home
    DailyReview --> RelapseLog["Relapse Log"]

    SOS --> Breathing["10s / 60s / 3min Actions"]
    SOS --> RelapseLog
    Breathing --> SOSReflect["Later Reflection"]
    SOSReflect --> DailyReview
    SOSReflect --> Home

    Calendar --> DailyDetail["Daily Detail"]
    Calendar --> DayMemo["Day Memo"]
    DailyDetail --> RelapseLog
    DayMemo --> DailyDetail
    Calendar --> Home

    Insights --> TriggerInsights["Trigger Insights"]
    Insights --> ActionBadges["Action Badges"]
    Insights --> Home

    RelapseLog --> RestartFlow["Restart Flow"]
    RestartFlow --> RePledge["Re-Pledge"]
    RePledge --> Home

    Settings --> PrivacyLock["Privacy Lock"]
    Settings --> DataExportDelete["Data Export/Delete"]
    Settings --> FutureBlocker["Future Blocker Placeholder"]
    Settings --> FutureCommunity["Future Safe Community Placeholder"]
    Settings --> NotificationSettings["Notification Settings"]
    Settings --> AboutPolicy["About / Policy Notes"]

    PrivacyLock --> Settings
    DataExportDelete --> ExportConfirm["Export Confirm"]
    DataExportDelete --> DeleteConfirm["Delete Confirm"]
    ExportConfirm --> DataExportDelete
    DeleteConfirm --> Splash

    FutureBlocker --> BlockerPermissionInfo["Permission Info"]
    FutureBlocker --> BlockerRules["Blocker Rules"]
    FutureBlocker --> AuditLog["Audit Log"]
    BlockerPermissionInfo --> Settings
    BlockerRules --> Settings
    AuditLog --> Settings

    FutureBlocker -. "future" .-> BlockEvent["Block Event"]
    BlockEvent --> SOS
    BlockEvent --> UnlockRequest["Temporary Unlock Request"]
    UnlockRequest --> AuditLog
    UnlockRequest --> BlockEvent

    FutureCommunity -. "later, not MVP" .-> SafeCommunitySpec["Anonymous / Moderated Community Spec"]
```

## Flow Rules

- Onboarding中に課金、広告、評価依頼を出さない。
- Goal Setupは後から変更可能にする。
- SOSはHomeとBottom Navigationから1タップ。
- Relapse Logは任意入力で進める。
- Data Deleteは確認後に初期状態へ戻る。
- Future BlockerはMVPでは権限要求ではなく説明と配置のみ。
- Calendarは `成功/失敗` の採点ではなく、`整った日`, `揺れた日`, `立て直した日`, `記録した日` を扱う。
- 外部コミュニティ導線はMVPに置かず、将来プレースホルダーに留める。
- Homeは競合のような多ボタン集約を避け、SOSと今日のループを最優先する。
