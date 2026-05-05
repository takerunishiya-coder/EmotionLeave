# Project Skills

EmotionLeave のUX設計で使うプロジェクト用Skill定義。各Skillは実装コードを変更せず、入力文書を読み、指定成果物に反映できる粒度で使う。

## competitor-ux-synthesis-skill
- 目的: 競合の不満、強み、レビュー傾向をUX戦略に変換する。
- 入力ファイル: `Stopemotion.md`, `Stopemotion_2.md`, `docs/00_project/PRODUCT_REQUIREMENTS.md`
- 出力成果物: `COMPETITOR_PAIN_TO_UX_STRATEGY.md`, `UX_DEFINITION.md`
- チェックリスト: 初回ペイウォール不信、広告割り込み、誤検知、解除フロー、データ消失、日本語UX、コミュニティ疲れを扱う。
- よくある失敗: 競合機能の羅列で終わる。強みをEmotionLeaveの差別化に翻訳しない。

## ux-definition-skill
- 目的: UXコンセプト、価値、やらないこと、MVP範囲を定義する。
- 入力ファイル: `docs/00_project/PRODUCT_REQUIREMENTS.md`, `docs/00_project/ROADMAP.md`
- 出力成果物: `UX_DEFINITION.md`, `USER_PERSONAS.md`, `USER_JOURNEY.md`
- チェックリスト: 責めない、続け直せる、見られても恥ずかしくない、3分以内開始を満たす。
- よくある失敗: 禁欲を目的化し、生活・集中・自信・時間への接続を忘れる。

## screen-flow-design-skill
- 目的: Android前提の情報設計、画面遷移、画面仕様を作る。
- 入力ファイル: `docs/00_project/PRODUCT_REQUIREMENTS.md`, `docs/02_ux/*.md`
- 出力成果物: `INFORMATION_ARCHITECTURE.md`, `SCREEN_FLOW.md`, `SCREEN_SPEC.md`, `WIREFRAME_TEXT.md`
- チェックリスト: SOS 1タップ、SettingsにPrivacy/Dataを配置、将来ブロッカーは入口のみ設計。
- よくある失敗: Relapseを常設タブにしてユーザーの自己認識を重くする。

## japanese-microcopy-skill
- 目的: 日本語ネイティブで自然かつ心理安全性の高いUI文言を作る。
- 入力ファイル: UX画面仕様、法務/プライバシーメモ
- 出力成果物: `MICROCOPY_GUIDE.md`, 各画面の必要コピー
- チェックリスト: 禁止表現、通知文言、relapse時文言、医療断定回避、ロック画面で見られても安全な表現。
- よくある失敗: 「失敗」「我慢できなかった」「治る」など、責める/断定する語を残す。

## relapse-recovery-flow-skill
- 目的: relapse後に戻れる体験を設計する。
- 入力ファイル: 競合調査、daily loop、行動設計メモ
- 出力成果物: `RELAPSE_RECOVERY_FLOW.md`
- チェックリスト: 最長記録、累計成功時間、再開回数、次の24時間、任意入力を含む。
- よくある失敗: 記録をゼロにするだけで、学びや再開導線を作らない。

## urge-sos-flow-skill
- 目的: 衝動時に即時介入できるSOS体験を作る。
- 入力ファイル: daily loop、画面仕様
- 出力成果物: `URGE_SOS_FLOW.md`
- チェックリスト: 10秒/60秒/3分導線、呼吸、場所移動、理由再表示、メモ、あとで振り返る導線。
- よくある失敗: 高衝動時に長文入力や自己分析を要求する。

## privacy-by-design-skill
- 目的: local-first、最小権限、ロック、通知、削除/エクスポートをUXへ組み込む。
- 入力ファイル: `docs/03_privacy/PRIVACY_AND_PERMISSIONS.md`, 画面仕様
- 出力成果物: `INFORMATION_ARCHITECTURE.md`, `SCREEN_SPEC.md`, `UX_ACCEPTANCE_CRITERIA.md`
- チェックリスト: 通知の秘匿性、アプリロック、表示名、エクスポート警告、全削除範囲。
- よくある失敗: 「ローカル保存」と言うだけで削除・バックアップ・ログ範囲を定義しない。

## legal-ux-review-skill
- 目的: 医療・治療・年齢・性的表現・ストアポリシーのUXリスクを下げる。
- 入力ファイル: 全UX文書、PRD、プライバシー文書
- 出力成果物: `MICROCOPY_GUIDE.md`, `UX_ACCEPTANCE_CRITERIA.md`, PR本文の注意点
- チェックリスト: 医療効果断定なし、18+推奨、Accessibility Service の明示同意、コミュニティMVP外。
- よくある失敗: 「依存症を治す」「再発を防ぐ」などの保証表現を残す。

## ux-qa-checklist-skill
- 目的: 実装前後にUX品質を検証できるチェックリストを作る。
- 入力ファイル: 画面仕様、フロー、マイクロコピー
- 出力成果物: `UX_ACCEPTANCE_CRITERIA.md`
- チェックリスト: オンボーディング、daily loop、SOS、relapse、privacy、data、future blocker の手動テスト観点。
- よくある失敗: 正常系だけを確認し、戻る/スキップ/権限拒否/削除後の状態を見ない。
