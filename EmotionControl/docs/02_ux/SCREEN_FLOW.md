# Screen Flow

```mermaid
flowchart TD
    Splash["Splash"] --> Onboarding["Onboarding"]
    Onboarding --> GoalSetup["Goal Setup"]
    GoalSetup --> LifeValue["Life Value: 取り戻したいもの"]
    LifeValue --> FirstPlan["First If-Then Plan"]
    FirstPlan --> FirstPledge["First Daily Pledge"]
    FirstPledge --> Home["Home"]

    Home --> DailyPledge["Daily Pledge"]
    Home --> DailyReview["Daily Review"]
    Home == "one tap / first viewport" ==> SOS["SOS"]
    Home --> ReasonCard["Reason Re-display"]
    Home --> Calendar["Calendar"]
    Home --> Insights["Insights"]
    Home --> Settings["Settings"]

    DailyPledge --> Home
    DailyReview --> Home
    DailyReview --> RelapseLog["Relapse Log"]

    SOS --> AutoPause["10s Auto Pause"]
    AutoPause --> Breathing["30s Breathing"]
    Breathing --> BodyAction["60s Body Action"]
    BodyAction --> ThreeMinAction["3min Action"]
    ThreeMinAction --> SOSReflect["Later Reflection"]
    SOS --> RelapseLog
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
- SOSはHomeの第一表示範囲とBottom Navigationから1タップ。通知許可、日次完了状態、スクロール位置に邪魔されない。
- Relapse Logは任意入力で進める。
- Data Deleteは確認後に初期状態へ戻る。
- Future BlockerはMVPでは権限要求ではなく説明と配置のみ。
- Calendarは `成功/失敗` の採点ではなく、`整った日`, `揺れた日`, `立て直した日`, `記録した日` を扱う。
- 外部コミュニティ導線はMVPに置かず、将来プレースホルダーに留める。
- Homeは競合のような多ボタン集約を避け、SOSと今日のループを最優先する。
- Daily Pledge/Reviewには10〜30秒のquick path、1分以内のstandard path、2分以内のdetail pathを用意する。
- `戻った日を記録` はHomeの主CTAにせず、文脈的な副導線にする。
