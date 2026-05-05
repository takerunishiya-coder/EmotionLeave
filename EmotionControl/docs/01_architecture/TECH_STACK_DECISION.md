# TECH STACK DECISION

作成日: 2026-05-05

## 結論

StopEmotion の初期実装は **Kotlin + Jetpack Compose** を推奨する。

理由は、MVPの主要機能はどの候補でも実装できる一方、将来のポルノブロッカーが Android OS の強い制約を受けるためである。Accessibility Service、VpnService、UsageStats、Device Admin、通知、ローカル暗号化、バックアップ除外を扱うなら、抽象レイヤーを挟まず Android 標準APIに直接寄せたほうが、実装・デバッグ・審査説明・長期保守のリスクが最も低い。

ただし、ブロッカーをMVPに含めない。MVPでは日数カウンター、daily pledge、振り返り、カレンダー、SOS画面、ローカル保存、データ削除/エクスポートを優先する。ブロッカーは Phase 2 以降に、権限ごとにPoCと審査文言を分けて段階導入する。

## 前提

- 最初の対象は Android。
- 将来 iOS 対応は検討するが、初期品質とブロッカー実現性を優先する。
- 医療効果を断定せず、自己管理・習慣改善支援アプリとして設計する。
- 課金・広告はMVP範囲外。
- 閲覧履歴、画面テキスト、ブロック判定ログなどのセンシティブ情報は原則ローカル優先で扱う。
- Expo単体で OS 権限深部のブロッカーは実現しない。必要なら development build / prebuild / native module が前提になる。

## 候補比較

| 候補 | Androidブロッカー適性 | 開発スピード | 保守性 | iOS対応 | UI品質 | Codex実装容易性 | Play審査リスク | ローカルDB/暗号化 |
|---|---:|---:|---:|---:|---:|---:|---:|---:|
| React Native / Expo | 低 | 高 | 中 | 高 | 中-高 | 高 | 中 | 中 |
| React Native + Android native module | 中 | 中 | 中 | 中-高 | 中-高 | 中 | 中-高 | 中 |
| Kotlin + Jetpack Compose | 高 | 中 | 高 | 低-中 | 高 | 高 | 中 | 高 |
| Flutter + Android native plugin | 中 | 中 | 中 | 高 | 高 | 中 | 中-高 | 中 |

評価はMVPだけでなく、将来ブロッカーまで含めた総合判断である。MVPだけなら React Native / Expo でも十分成立するが、StopEmotion の差別化要素を「Explainable Recovery Copilot + 段階的ブロッカー」と置くなら、Androidネイティブを主軸にするほうがよい。

## 候補1: React Native / Expo

### 向いている範囲

- 日数カウンター
- daily pledge / daily review
- ジャーナル
- カレンダー表示
- SOS画面
- 通知
- 基本的なローカル保存
- 将来の iOS / Web 展開

### 弱い範囲

- Accessibility Service
- VpnService
- UsageStats
- Device Admin
- AndroidManifest やバックアップ除外などの細かい制御
- ネイティブサービスの常駐・復帰・OEM差分対応

Expo Go は Expo SDK に含まれるネイティブライブラリしか使えない。Expo でカスタムネイティブコードを使う場合は development build、Expo Modules API、prebuild/CNG、config plugin が必要になる。つまり「Expoだけでブロッカーまで作る」は不可であり、ブロッカー開始時点で実質的に候補2へ移行する。

### 判断

MVPだけを最速で検証するなら有力。ただし、後からブロッカーを本気で入れるとネイティブ移行コストが発生する。StopEmotion の中核価値が将来ブロッカーに寄るなら、初期から採用する理由は弱い。

## 候補2: React Native + Android native module

### 向いている範囲

- JS/TSでUIとビジネスロジックを速く作る
- Android固有機能を Kotlin/Java module に閉じ込める
- 将来 iOS は同じUI資産を使える

### 弱い範囲

- ブロッカーの失敗原因が JS / bridge / native service / OS / OEM のどこにあるか切り分けにくい
- Accessibility Service や VpnService のような常駐系は、UIフレームワークよりAndroidサービス設計が本体になる
- 権限説明、設定画面遷移、復帰処理、通知連携などでネイティブ実装量が増える

### 判断

「MVPはReact Native、ブロッカーだけAndroid module」は現実的だが、長期的には二重構造になる。開発チームに React Native の強い資産がある場合のみ採用候補。現時点の StopEmotion では、最初から Kotlin に寄せたほうが設計が素直。

## 候補3: Kotlin + Jetpack Compose

### 向いている範囲

- Android OS 権限との直接連携
- Accessibility Service / VpnService / UsageStats / Device Admin のPoC
- Room / DataStore / Jetpack Security / SQLCipher などのローカル保存
- WorkManager / Foreground Service / 通知
- Play審査での権限利用説明と実装証跡の対応
- Compose による高品質な日本語UI
- Android Studio、Gradle、adb、端末ログを使った問題切り分け

### 弱い範囲

- iOS版は別実装になる
- Web系のUI開発者には学習コストがある
- KMPを採用しない限り、ドメインロジック共有は限定的

### 判断

StopEmotion の推奨スタック。MVPは Compose で小さく作り、将来ブロッカーは Android 標準APIを直接扱う。iOS対応は、最初からUI共有を狙うのではなく、ドメインモデル、文言、UX仕様、エクスポート形式を共有可能にしておく。

## 候補4: Flutter + Android native plugin

### 向いている範囲

- UI品質とクロスプラットフォーム展開
- MVPの画面開発
- iOS版への横展開
- Android固有処理を plugin に分離する設計

### 弱い範囲

- ブロッカー本体は結局 Kotlin/Java plugin 側に寄る
- platform channel をまたぐ状態管理、ログ、エラー処理が増える
- Android権限の審査説明では Flutter 採用自体の利点は少ない

### 判断

UI中心アプリなら強いが、StopEmotion は将来 Android ブロッカーが重要になる。Flutterを採用してもOS制約は消えず、ネイティブpluginの保守が重くなるため、今回の第一候補にはしない。

## Androidブロッカー機能の現実的な整理

### Accessibility Service

できること:

- 画面上のUIイベントや一部ノード情報を受け取り、ユーザーの操作補助やルールベースの介入を行う。
- 特定アプリや画面状態を検知して、StopEmotion の警告/SOS画面へ誘導するPoCは可能。

難しいこと:

- すべてのブラウザ/アプリ/動画画面を正確に判定すること。
- 画面内容を広く収集してAI判定すること。プライバシーと審査リスクが高い。
- ユーザーに説明しづらい自動操作。Google Play は自律的な操作や意思決定を厳しく見る。

方針:

- MVPでは使わない。
- Phase 2で「ユーザーが明示的に有効化した、限定対象アプリへのルールベース補助」としてPoC。
- 画面テキストの保存は原則しない。保存する場合もマスク済み監査ログに限定する。

### VpnService

できること:

- 端末の通信をVPNインターフェースへ通し、DNS/URL/接続先ベースのフィルタリングを実装する。
- ブラウザ横断のブロック候補として検討できる。

難しいこと:

- HTTPS本文やアプリ内コンテンツを自由に読めるわけではない。
- Androidでは同一ユーザー/プロファイルにつきアクティブVPNは1つ。
- Google Playでは VpnService の利用目的、データ取得、暗号化、同意、ストア記載、宣言フォームが重要になる。
- VPNをアプリの中核機能として説明できない場合、審査リスクが上がる。

方針:

- Phase 3以降のPoC。
- 最初はローカルDNS/ドメインブロックの実現性を検証する。
- リモートVPNサーバーへ閲覧データを送る設計は初期採用しない。

### UsageStats

できること:

- アプリ使用履歴やフォアグラウンド遷移の統計を取得し、使用傾向・リスク時間帯の可視化に使う。

難しいこと:

- ユーザーが設定画面で使用状況アクセスを明示的に許可する必要がある。
- リアルタイムで完全なブロック判定をするAPIではない。
- 取得データは個人の行動履歴に近く、最小化と説明が必要。

方針:

- MVPでは使わない。
- Phase 2で「ユーザーが選んだ対象アプリの利用時間を端末内で集計」までに限定する。

### Device Admin

できること:

- 一部の端末ロックなど、ユーザーが明示的に有効化した管理機能を使える。

難しいこと:

- Android Enterprise では従来の Device Admin の多くが非推奨方向。
- 一般ユーザー向け自己管理アプリで強制力を上げる目的に使うと、信頼・解除しやすさ・審査説明のリスクが高い。
- Device Owner / Profile Owner 前提の機能は、通常のストア配布アプリでは現実的でないことが多い。

方針:

- MVPでは使わない。
- 将来も標準採用しない。研究的PoCまたは企業/保護者管理モードを検討する場合のみ別設計にする。

## Expoだけでできること / できないこと

Expoだけでできる:

- 画面UI
- ナビゲーション
- pledge / review / journal
- カレンダー
- 通知
- 基本的なSQLite/AsyncStorage系の保存
- EAS Buildを使った配布

Expoだけでは不足する:

- Accessibility Service の実装
- VpnService の実装
- UsageStats 権限誘導とネイティブ取得
- Device Admin
- AndroidManifest、foreground service、backup rules、権限宣言の細かい制御
- OEM差分を含む常駐サービスのデバッグ

対応するには:

- Expo development build
- Expo prebuild / CNG
- Expo Modules API
- config plugin
- Kotlin/Java のネイティブ実装

この時点で、StopEmotion は「Expoアプリ」ではなく「React Native + Android native module」案件として扱うべきである。

## 採用スタック

### Android

- Language: Kotlin
- UI: Jetpack Compose
- Architecture: feature-based packages + MVVM/MVI寄りの単方向データフロー
- Async: Coroutines + Flow
- DI: Hilt
- Local DB: Room
- Preferences: DataStore
- Encryption: Android Keystore + Jetpack Security または SQLCipher for Android をPoCで比較
- Background: WorkManager、必要時のみ Foreground Service
- Notifications: AndroidX Core + runtime notification permission handling
- Testing: JUnit、Robolectric、Compose UI Test、必要に応じて adb 手動シナリオ

### Data / Privacy

- MVPデータは端末内保存を標準にする。
- バックアップは初期状態で慎重に扱い、センシティブDBは自動バックアップ対象から外す。
- エクスポートはユーザー操作によるJSON/CSVを検討する。
- 削除はアプリ内から全データ削除できるようにする。

### Future iOS

- 初期は iOS アプリを作らない。
- ただし、以下は将来共有しやすい形で管理する。
  - データモデル
  - UXフロー
  - 日本語コピー
  - エクスポート形式
  - habit/recovery domain rules
- 将来の選択肢は SwiftUI ネイティブ、または Kotlin Multiplatform による一部ロジック共有。

## 段階的な実装方針

### Phase 1: MVP

- 日数カウンター
- daily pledge
- daily review
- カレンダー
- SOS画面
- relapse記録
- ローカルDB
- データ削除/エクスポート

この段階では Accessibility Service / VpnService / UsageStats / Device Admin を入れない。初期の信頼形成を優先する。

### Phase 2: 軽量ブロッカーPoC

- UsageStats による対象アプリの利用傾向表示
- Accessibility Service による限定的・ルールベースの介入
- 権限説明画面と同意ログ
- 誤検知時の解除/報告フロー

### Phase 3: ネットワークブロックPoC

- VpnService または DNS ベースのブロック検証
- ローカル処理優先
- Play Console 宣言、ストア文言、プライバシーポリシー草案の先行作成
- 審査リスクが高い場合は Play配布版とサイドロード/研究版を分ける判断も残す

### Phase 4: 強制力の追加検討

- accountability partner
- 遅延解除
- 設定変更のクールダウン
- Device Admin は原則避け、必要なら別モードとして法務・審査レビュー後に判断する

## Google Play審査リスク

- Accessibility Service は、障害者支援ツールとして宣言できない場合、目立つ開示と同意、Play Console の権限申告、限定された目的説明が必要になる。
- VpnService は、VPN利用が中核機能であるか、許可された例外に当たるか、取得データと利用目的を明確に説明する必要がある。
- UsageStats は行動履歴に近いため、オンボーディングで目的・保存場所・削除方法を明示する。
- Device Admin は強制力が強く、解除困難に見える設計は信頼を損なう。初期採用しない。
- ブロッカーは「完璧に防ぐ」と表現しない。OS制約、ブラウザ差分、アプリ内コンテンツ判定の限界を明記する。

## 参照した公式情報

- Android Developers: Accessibility Service は画面内容の検査や操作補助ができるが、通常のアクセシビリティ改善APIではなく特殊なサービスとして扱われる。
  https://developer.android.google.cn/guide/topics/ui/accessibility/service
- Google Play Help: AccessibilityService API は Android 12 以降の対象アプリで宣言と承認が必要。障害者支援ツールでない場合は目立つ開示と同意が必要。
  https://support.google.com/googleplay/android-developer/answer/10964491
- Android Developers: VpnService はシステムのVPNサービスとして宣言し、ユーザー許可を得てTUNインターフェースを確立する。
  https://developer.android.com/develop/connectivity/vpn
- Google Play Help: VpnService 利用アプリは宣言が必要で、データ取得・利用目的・同意・ストア記載が審査対象になる。
  https://support.google.com/googleplay/android-developer/answer/12564964
- Android Developers: UsageStatsManager は端末使用履歴・統計へのアクセスを提供する。
  https://developer.android.google.cn/reference/android/app/usage/UsageStatsManager
- Android Developers: 従来の Device Admin の一部ポリシーは非推奨で、Android 10 以降で利用できないものがある。
  https://developers.google.com/android/work/device-admin-deprecation
- Expo Docs: Expo Go はSDK同梱ネイティブライブラリに制限され、カスタムネイティブコードには development build / Expo Modules API / prebuild / config plugin が必要。
  https://docs.expo.dev/workflow/customizing/
- Android Developers: DataStore は小規模データ向けで、複雑なデータや参照整合性が必要な場合は Room が推奨される。
  https://developer.android.com/topic/libraries/architecture/datastore
