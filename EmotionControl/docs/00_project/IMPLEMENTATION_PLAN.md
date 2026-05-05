# IMPLEMENTATION PLAN (実装前計画)

## 1. 実装原則
- 小さなPR単位で進める。
- Androidを最優先。
- 先に継続体験と信頼性を作る。

## 2. PR分割案
1. docs整備（本PR）
2. Androidプロジェクト基盤（Compose + DI + DB）
3. デイリーループ画面（pledge/review）
4. blockerルールと説明UI
5. リラプス回復導線
6. データ削除/エクスポート
7. QA自動化と手動E2Eシナリオ

## 3. 実装順序
- Domain model -> Local DB -> UseCase -> UI -> Instrumentation Test

## 4. リスクと緩和
- Accessibility挙動の端末差: 端末別チェックリスト作成
- 権限離脱: 段階的説明と後から再設定導線
- 誤検知不信: ブロック理由表示 + 手動調整

## 5. Definition of Done
- 受け入れ基準を満たす
- ユニットテスト/主要UIテスト通過
- コピーレビュー完了
- プライバシー/権限文言レビュー完了
