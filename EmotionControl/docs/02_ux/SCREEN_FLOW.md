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
    DailyDetail --> RelapseLog
    Calendar --> Home

    Insights --> TriggerInsights["Trigger Insights"]
    Insights --> Home

    RelapseLog --> RestartFlow["Restart Flow"]
    RestartFlow --> RePledge["Re-Pledge"]
    RePledge --> Home

    Settings --> PrivacyLock["Privacy Lock"]
    Settings --> DataExportDelete["Data Export/Delete"]
    Settings --> FutureBlocker["Future Blocker Placeholder"]
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
```

## Flow Rules

- Onboarding中に課金、広告、評価依頼を出さない。
- Goal Setupは後から変更可能にする。
- SOSはHomeとBottom Navigationから1タップ。
- Relapse Logは任意入力で進める。
- Data Deleteは確認後に初期状態へ戻る。
- Future BlockerはMVPでは権限要求ではなく説明と配置のみ。

