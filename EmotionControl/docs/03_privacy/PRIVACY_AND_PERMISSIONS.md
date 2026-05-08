# PRIVACY AND PERMISSIONS

## 基本方針
- Local-first
- 最小権限
- 目的外利用禁止
- MVPではアカウントなし、クラウド同期なし、記録データは暗号化ローカルDB保存を基本とする。
- 外部送信は、課金検証、AI分析、クラッシュ診断など目的ごとに分け、ユーザーへの説明と必要な同意を前提にする。

## 想定権限（MVP）
- Accessibility Service（ブロック補助）
- Notification（daily reminder）
- Foreground service（必要時のみ）

## 収集データ
- ユーザー入力データ（誓約、振り返り、ルール）
- 技術ログ（クラッシュ、最小限の診断情報）
- App Store / Google Play公開時のデータ分類、端末内保存、AI/広告/分析SDK、課金インフラ方針は `APP_RELEASE_DATA_INFRASTRUCTURE_SPEC.md` を参照する。

## 提供機能
- データ全削除
- エクスポート（JSON/CSV）
- 権限オフ時の影響明示
- AI分析用データ削除
- 将来アカウントを導入する場合のアカウント削除

## ポリシー注意
- Google PlayのAccessibility利用ポリシー適合を要確認
- 医療効果の断定表現禁止
- 法的確定が必要な項目は「要専門家確認」とする
- App Store Privacy Nutrition Label / Google Play Data Safety / プライバシーポリシーの内容は、実装されたSDKと実際のデータフローに合わせて一致させる。
- 広告SDK、分析SDK、AIプロバイダー、課金検証サーバーを導入する場合は、第三者提供・委託・国外移転・保持期間を確認する。
