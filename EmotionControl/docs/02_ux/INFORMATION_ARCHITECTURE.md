# Information Architecture

## Top Level Navigation

Android MVPは Bottom Navigation を基本にする。

| タブ | 目的 | 主な画面 |
|---|---|---|
| ホーム | 今日の状態と最短導線 | Home, Today Status, Quick Actions |
| 誓約 | 朝の約束と夜の振り返り | Daily Pledge, Daily Review |
| SOS | 衝動時の即時介入 | SOS, Breathing, Quick Memo |
| 記録 | 日別履歴と傾向 | Calendar, Insights, Relapse History |
| 設定 | 信頼、権限、データ管理 | Settings, Privacy Lock, Export/Delete |

Relapse Log は常設タブにしない。Home、Daily Review、SOS から文脈的に入る。常設すると「自分は戻る人」という自己認識を強めるため。

## Main Screens

- Splash
- Onboarding
- Goal Setup
- First Pledge
- Home
- Daily Pledge
- Daily Review
- Calendar
- Insights
- SOS
- Relapse Log
- Restart Flow
- Settings
- Privacy Lock
- Data Export/Delete
- Future Blocker Placeholder

## Settings Structure

1. ローカルプロフィール
   - 表示名（任意、ローカルのみ）
   - 目標
   - 開始日

2. 日次ループ
   - 通知
   - 誓約文
   - 振り返りタグ

3. プライバシー
   - アプリロック
   - ロック画面通知の表示
   - アプリ切替画面のぼかし

4. データ管理
   - エクスポート（JSON/CSV）
   - すべてのデータを削除
   - 保存場所と削除範囲の説明

5. 将来のブロッカー
   - ブロックルール
   - 権限と状態
   - 監査ログ
   - 一時許可履歴
   - Accessibility/VPN/UsageStats の説明

6. ヘルプ
   - このアプリについて
   - 医療アプリではない旨
   - 危機時の相談先

## Data Management

MVPで扱うデータ:

- 目標
- 誓約
- 振り返り
- トリガータグ
- SOSメモ
- relapse記録
- 統計値
- 通知設定
- アプリロック設定

将来ブロッカーで扱う可能性:

- URL/キーワードルール
- ブロックイベント
- 一時許可理由
- 監査ログ
- 権限状態

原則:

- センシティブ記録はlocal-first。
- 通知には内容を出さない。
- エクスポートはユーザー操作時のみ。
- 全削除は記録、設定、ログ、通知ジョブ、アプリ内生成ファイルを対象にする。

## Future Blocker Placement

MVPでは `設定 > 将来のブロッカー` と Home の小さな状態エリアに配置だけ行う。初回オンボーディングでは「必要になったら後で設定できます」と説明し、Accessibility/VPNなどの重い権限要求はしない。

## Future Community Placement

MVPでは入れない。将来は `記録 > Insights` の下に「同じ時期の人の匿名ヒント」程度から検討する。公開タイムライン、詳細な体験談、ランキングは比較疲れやトリガーになり得るため、別途安全設計が必要。

