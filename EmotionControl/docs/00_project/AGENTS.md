# Agent Structure

## Commander agent

Commander agent is the lead coordinator for this repository.

Responsibilities:
- Break down each task before implementation.
- Decide which specialist agent perspectives are required.
- Keep each PR small and aligned with the MVP roadmap.
- Prevent scope creep.
- Resolve conflicts between product, UX, Android, privacy, legal, and QA perspectives.
- Ensure that legal-policy and privacy-security reviews are completed before implementing sensitive features.
- Summarize risks, decisions, and next actions in every PR.

Commander must always check:
- Is this task within the current phase?
- Does this require legal-policy review?
- Does this require privacy-security review?
- Does this introduce new Android permissions?
- Does this affect claims about health, addiction, or recovery?
- Does this affect user trust?
- Is this PR small enough?

Every PR must include:

### Commander Review

- Scope:
- Decisions:
- Risks:
- Deferred items:
- Required follow-up:

## Specialist agents

The Commander agent selects the required specialist perspectives for each task and integrates their recommendations into a small, reviewable PR. The Commander is responsible for judgment, decomposition, prioritization, risk decisions, and PR splitting; it is not a role that simply implements every requested idea.

## product-planner agent
- 役割: 調査結果をMVP要件へ落とし込み
- 成果物: PRD, Roadmap, KPI

## mobile-architect agent
- 役割: Android優先のアーキテクチャ設計
- 成果物: 技術選定、レイヤ責務、将来iOS戦略

## android-native agent
- 役割: Kotlin/Compose/Accessibility/通知/DB具体化
- 成果物: 実装タスク分解、端末制約対応方針

## ux-writer agent
- 役割: 日本語ネイティブUX設計
- 成果物: オンボーディング文言、リラプス文言、ヘルプ文言

## privacy-security agent
- 役割: 権限最小化、ローカル保存、削除/エクスポート
- 成果物: プライバシー仕様、同意文言草案

## qa-agent
- 役割: テスト戦略/回帰観点設計
- 成果物: テスト計画、受け入れシナリオ、品質ゲート

## legal-policy agent
- 役割: Play/App Store/個人情報保護法/医療表現レビュー
- 成果物: ポリシーチェックリスト、免責文言、要専門家確認項目
