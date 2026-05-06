# Animation Asset Strategy

## Goal

EmotionLeaveのアバター/称号/レベルアップ演出を、MVPで安全に実装し、将来の動画素材や高度なアニメーションへ拡張できるようにする。

前提:

- Android first
- Kotlin + Jetpack Compose
- MVPは軽量、オフライン、local-first
- 素材は自作または利用許可済みのみ
- ガチャ、射幸性、課金優位性は入れない

## Options

| 方式 | 概要 | メリット | デメリット | 推奨 |
|---|---|---|---|---|
| 静止PNG + UIアニメーション | PNGをCompose側でscale/offset/alpha/confetti表示 | 実装が軽い。権利管理が分かりやすい。オフラインで安定。MVP向き | 表情や複雑な動きは弱い | MVP採用 |
| スプライトシート | 連続フレーム画像を1枚にまとめて再生 | キャラ表現を細かく制御できる。動画より透明背景に強い | 容量が増えやすい。フレーム管理が必要 | Phase 2候補 |
| Lottie | JSONベースのベクターアニメーション | 軽量。UI演出に強い。デザイナー連携しやすい | 複雑な画像/キャラ表現には限界。ライブラリ依存 | Phase 2候補 |
| Rive | 状態機械つきインタラクティブアニメーション | 表情/状態変化/インタラクションに強い | ランタイム導入、学習、審査時説明が増える | Phase 2/3候補 |
| mp4 | 動画ファイルを再生 | 表現力が高い。制作済み動画を使いやすい | 容量、透過、ループ、端末負荷、スキップ制御に注意 | Phase 3以降 |

## MVP Recommendation

MVPでは `静止PNG + Compose UIアニメーション` を採用する。

理由:

- 既存のAndroid推奨スタックと整合する。
- 実装コストが低い。
- アセット権利管理が単純。
- オフライン利用に強い。
- Reduced Motion対応がしやすい。
- SOS/日次記録を邪魔しない短い演出を作りやすい。

MVP構成:

- `/assets/avatars/avatar_jacket.png`
- `/assets/avatars/avatar_centerpart.png`
- `/assets/avatars/avatar_suit.png`
- `/assets/avatars/avatar_kinniku.png`
- UI animation: scale, offset, alpha, glow
- confetti: Compose Canvas or lightweight local particle implementation
- no audio
- no video

MVP avatar source:

- 上記4枚はユーザー提供の人物ピクセルアートをアバター候補として配置する。
- 実装時はAndroid resource命名規則に合わせ、必要なら `res/drawable-nodpi/` または `res/drawable/` へコピー/変換する。
- 元画像は正方形PNGのため、Homeや選択画面ではリサイズ表示し、人物が切れないように `ContentScale.Fit` を基本にする。
- 背景透過が必要になった場合は、元画像を直接破壊せず、派生ファイルを別名で作る。

## Phase 2 Strategy

候補:

- Lottie for confetti/glow
- Rive for avatar expression/state
- Sprite sheet for simple jump/blink/wave

導入判断:

- APK/AABサイズ
- 初回起動速度
- Compose統合の安定性
- Reduced Motion対応
- アセット制作体制
- ライセンスと編集可能性

## Phase 3 Strategy

mp4/短尺動画は次の場合のみ検討する。

- 自作または利用許可済み素材がある。
- 透過や背景合成が不要、または仕様が明確。
- ファイルサイズを許容できる。
- 低スペック端末で再生が安定する。
- スキップ、無効化、代替静止画がある。

## Asset Rights Policy

- すべてのアバター、称号画像、演出素材は自作または利用許可済み素材のみ。
- 生成AI素材を使う場合は、生成元、プロンプト、利用条件、編集履歴を記録する。
- 他アプリ、ゲーム、漫画、アニメ、既存キャラクターに類似しすぎる素材を使わない。
- 外部素材はライセンス、作者、改変可否、商用利用可否、クレジット要否を記録する。
- リポジトリには素材出所メモを置く。
- 競合スクリーンショット、競合の称号体系、UI配置、色、アイコンをプロダクト素材として使わない。
- LINE等の外部サービス名を素材や称号名に含める場合は、提携/推奨と誤認されない表現にする。

## Asset Register

実装前に `docs/01_architecture/ASSET_LICENSES.md` または同等の管理表を作る。MVPで使う4体の初期台帳は同ファイルに記録する。

必須項目:

- assetId
- file path
- type: avatar / badge / motion / sound / video
- creator
- source
- license
- commercial use
- modification allowed
- attribution required
- expiration / renewal
- notes

## Runtime Requirements

- Level-up演出は3秒以内。
- いつでもスキップ可能。
- Reduced Motion有効時はjump/scale/confettiを止める。
- アセット読込失敗時はデフォルト静止アイコンへフォールバック。
- SOS/Relapse/Privacy Lock中は演出を表示せずキューに積む。
- 動画/高度アニメーション導入時も、静止PNG fallbackを必ず残す。

## Data / Storage Notes

保存するもの:

- selectedAvatarId
- unlockedTitleIds
- titleUnlockedAt
- lastShownAchievementIds
- motionPreference

保存しないもの:

- 外部プロフィール画像
- 実名
- 公開ユーザーID
- ランダム報酬履歴

エクスポート:

- アバターIDと称号IDは含めてもよい。
- 画像ファイル本体は含めない。
- エクスポート前に含まれるカテゴリを表示する。

削除:

- 全削除時にアバター選択、称号状態、演出表示履歴、motionPreferenceを削除する。

## Implementation Notes For Compose

- Avatar rendering: `Image` with fixed size and contentDescription.
- Motion: `animateFloatAsState`, `Animatable`, `updateTransition`.
- Confetti: simple Canvas particles or static decorative burst in MVP.
- Accessibility: animation is decorative; screen readerには称号テキストだけを読む。
- Reduced Motion: system setting and app settingを両方参照する。
- MVPではリアル写真アバター、外部プロフィール画像、クラウド同期を扱わない。

## Open Questions

- MVPでconfettiをCanvas実装にするか、静的burst画像にするか。
- アバターPNGの推奨サイズを 256px / 512px のどちらにするか。
- 現在の元画像は 1254px 正方形のため、実装時に原寸同梱するか 512px 派生版を作るか。
- Lottie/Rive導入時のライブラリ選定をいつ行うか。
