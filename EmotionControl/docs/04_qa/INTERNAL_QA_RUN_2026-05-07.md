# Internal QA Run: 2026-05-07

## Scope

リリース前チェックのうち、ローカルで実行できる自動テスト、Lint、Debugビルド、実装観点の回帰確認を実施した。

## Machine Checks

- [x] `testDebugUnitTest`
- [x] `lintDebug`
- [x] `assembleDebug`
- [x] `git diff --check`

結果: 成功。

補足:

- `git diff --check` は空白エラーなし。
- 一部docsファイルで LF が CRLF に置き換わる警告が出ているが、差分の空白エラーではない。
- Gradle 9.0 互換性に関する deprecation warning が出ている。申請前ブロッカーではないが、将来のGradle更新時に確認する。

## Added Automated Coverage

- Streak calculation
  - 現行記録を開始日から当日込みで計算する。
  - 現行記録が短い場合も保存済み最長記録を保持する。
  - 未来日などの不正に近い開始日でも0日や負数にしない。
  - epoch millis を指定タイムゾーンの日付へ変換する。
- Relapse / restart calculation
  - relapse記録時、終了した現行記録を最長記録に反映する。
  - relapse回数を1増やす。
- Notification opt-out
  - ユーザーが通知OFFにしている場合は通知を出さない。
  - 通知権限がない場合は通知を出さない。
- Export cache cleanup
  - 全データ削除時に使うエクスポートキャッシュ削除処理が `exports` ディレクトリだけを消す。

## Manual QA Status

一部実施。Pixel_8 AVD で主要MVPフローを確認した。

確認済み:

- [x] 初回起動からHome到達。
- [x] 初回起動でアカウントなし・端末保存の説明が表示される。
- [x] Home初期統計が現行1日、最長1日、累計24時間として表示される。
- [x] Daily Pledge 保存後にHomeへ戻れる。
- [x] Daily Review 保存後にHomeへ戻れ、Homeの今日の状態が「今日の記録は整っています。」へ変わる。
- [x] SOS画面に広告・課金誘導が表示されない。
- [x] SOS完了後、HomeのSOS件数が増える。
- [x] relapse記録画面に責める表現や課金誘導がない。
- [x] relapse記録後、Homeの再開回数が増える。
- [x] 記録一覧にrelapse、SOS、Daily Pledge、Daily Reviewが表示される。
- [x] 通知ON操作でAndroid通知権限ダイアログが表示される。
- [x] 通知権限を拒否してもアプリを継続利用できる。
- [x] エクスポートで共有シートが表示され、アプリ内に生JSONが常時表示されない。
- [x] 全データ削除で確認ダイアログが表示される。
- [x] 修正後、全データ削除でクラッシュせずオンボーディングへ戻る。
- [x] 修正後、クラッシュログに新規クラッシュが残らない。

未実施:

- [ ] 実機での確認。
- [ ] 通知許可ケースでの実通知表示。
- [ ] 画面保護のスクリーンショット抑止確認。
- [ ] ストア用スクリーンショット取得。
- [ ] Android release build / 署名済みビルドでの確認。

## Current Release Risk

- 実機手動QAが未完了。
- Android release build の署名、minify、versionCode運用は未確定。
- Plus、広告、AI分析は方針文書化済みだが、MVP実装には未接続。
- ストアの Data Safety / Privacy Nutrition Label は、最終SDK構成に合わせた更新が必要。

## Emulator QA Finding

全データ削除時に、Room の `clearAllTables()` がメインスレッドから呼ばれてクラッシュする問題を検出した。

対応:

- `LocalDataRepository.deleteAllLocalData()` で `database.clearAllTables()` を `Dispatchers.IO` 上で実行するよう修正した。
- 修正後に再度エミュレーターで削除フローを確認し、オンボーディングへ戻ることと新規クラッシュログがないことを確認した。
