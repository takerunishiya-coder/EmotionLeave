# ClaudeDesign Brief

## Purpose

EmotionLeaveのAndroid向け主要画面イメージを作るための依頼書。既存UX文書を前提に、アバター、記録バッジ、称号、軽い演出の挿入箇所を画面単位で指定する。

## Product Context

EmotionLeaveは、禁欲そのものを目的化せず、衝動コントロール、習慣回復、生活の立て直しを支援するアプリである。

最重要UX:

1. HomeからSOSへ迷わず1タップで行ける。
2. 記録が途切れても、責められずに再開できる。
3. 毎日10〜30秒でチェックインできる。
4. 人に見られても恥ずかしくない。

デザイン上の優先順位:

1. SOS導線
2. 今日の誓約/振り返り
3. 今日の理由
4. 現在記録、最長記録、累計成功時間、再開回数
5. アバター、記録バッジ、演出

アバターや称号は主役にしない。日数競争、煽り、ランキング、ガチャ、強いゲーム感は避ける。

## Assets

MVPアバター候補:

| avatarId | file | UI label |
|---|---|---|
| `avatar_jacket` | `assets/avatars/avatar_jacket.png` | ジャケット |
| `avatar_centerpart` | `assets/avatars/avatar_centerpart.png` | センターパート |
| `avatar_suit` | `assets/avatars/avatar_suit.png` | スーツ |
| `avatar_kinniku` | `assets/avatars/avatar_kinniku.png` | アクティブ |

注意:

- 人物が切れないように正方形サムネイルで `fit` 表示する。
- Home表示では小さく扱い、画面の主役にしない。
- ラベルは通知、ロック画面、共有画像には出さない。
- レア度、能力値、ランク、強さ、性格診断のような見せ方にしない。

## Requested Screens

ClaudeDesignには、まず以下の6画面を依頼する。

1. Home
2. Avatar Selection
3. Level-up / Record Badge Modal
4. Achievement / Insights
5. Restart Flow
6. Settings Profile

必要なら追加で Daily Pledge、Daily Review、SOSも作る。ただしSOSはアバターより行動ボタンを優先する。

## Avatar / Title Placement Decisions

結論: 挿入箇所は決まっている。詳細なビジュアル密度、余白、サイズはClaudeDesignで検討する。

| Area | Avatar placement | Title / badge placement | Priority |
|---|---|---|---|
| Onboarding / Avatar Selection | 4体の選択グリッド。各カードに全身サムネイルと中立ラベル。 | なし。初回で称号説明を重くしない。 | High |
| Home | 今日ステータス付近の右側、または理由カード横に小さく表示。 | 最新の記録バッジを1つだけ小さく表示可能。常時大きくしない。 | Medium |
| Level-up Modal | モーダル中央上部。選択中アバターのみ。 | 見出しとバッジ名を中央。`記録バッジ` 表現を優先。 | High |
| Insights / Achievement | なし、または空状態の小アイコン程度。 | 獲得済みバッジ一覧、進捗、解放日時。 | High |
| Restart Flow | 原則表示しない。表示する場合は小さな横顔/静止のみ。 | `ここまでの称号は残っています` を静かに表示。祝祭演出は出さない。 | Medium |
| Settings Profile | ローカルプロフィールの行に小さく表示。変更導線あり。 | 獲得済み件数のみ表示可能。 | Medium |
| SOS | 原則表示しない。理由再表示の邪魔をしない。 | SOS完了後に小さく `立ち止まれた` を表示可能。開始時は出さない。 | Low |
| Notifications / Lock Screen | 表示しない。 | 表示しない。 | Required |

## Home Design Requirements

Homeはアバターを足しても、以下の第一表示範囲を守る。

- 今日の日数または今日の状態
- 最長記録、累計成功時間、再開回数
- 今日の誓約/振り返りCTA
- SOSボタン
- 今日の理由

推奨レイアウト:

```text
EmotionLeave                                      [lock]

[今日ステータスカード]
今日 5日目                       [小アバター]
最長 18日 ・ 累計 120時間 ・ 再開 3回
小さく続いています

[今日のループ]
朝の誓約 完了 / 夜の振り返り まだ
[今日の振り返り]

[SOS]

[今日の理由]
夜を落ち着いて終える

[最新の記録バッジ: 7日分の記録]  optional
```

Homeで避けること:

- 大きなアバターでSOSを下へ押し出す。
- 称号をランキングや強さのように見せる。
- `XX日達成！` を常時大きく表示する。
- 広告枠のような横長バナーを作る。

## Avatar Selection Design Requirements

目的:

初回3分以内を保ったまま、ユーザーが軽く選べる画面にする。

画面構成:

```text
一緒に進むアイコンを選びましょう
小さな相棒をあとで選べます。今はこのまま始められます。

[ジャケット] [センターパート]
[スーツ]     [アクティブ]

Homeではこのくらい小さく表示されます
[small preview]

[このアイコンで始める]
[あとで選ぶ]
```

デザイン方針:

- 選択カードは同じサイズ。
- 選択状態は枠線、チェック、軽い背景で表現する。
- レア度、ロック、未解放、能力差を見せない。
- `あとで選ぶ` は逃げ道として見える位置に置く。

## Level-Up / Record Badge Modal Requirements

目的:

節目を短く祝う。演出は1.2〜2.0秒、最大3秒以内。必ずスキップできる。

画面構成:

```text
[スキップ]

[選択中アバター]

7日分の記録が残りました
新しい記録バッジを手に入れました
ここまでの記録は残っています

[閉じる]
[できたことを見る]
```

演出:

- アバターが8〜12dpだけ軽くジャンプ。
- 背面に薄い円形グロー。
- 紙吹雪は少量。画面全体を覆わない。
- Reduced Motion時はジャンプ/紙吹雪なしでフェードのみ。

更新方針:

- 参考画像のように、節目モーダルだけはもう少し派手にしてよい。
- `7日間達成!!` の大きな見出し、称号/記録バッジの装飾プレート、アバターのポーズ変化、青緑系の縁光、中量の紙吹雪を許可する。
- ただしガチャ、ランキング、課金演出、勝利/敗北の煽りには見せない。
- Homeや通知では派手な文言を出さず、祝祭感はモーダル内だけに閉じる。

避けること:

- フルスクリーン動画。
- 大音量、振動、長い演出。
- `レベルダウン`, `失敗`, `勝利`, `完全達成`。
- relapse直後の派手な祝祭。

### Level-Up Image Direction For ClaudeDesign

ClaudeDesignには、単一モーダルだけでなく、レベルアップ演出の「画面イメージ」を複数状態で作ってもらう。

作ってほしい状態:

1. `Before Reveal`
   - Home上、またはReview完了後に小さくモーダルが出る直前。
   - 背景は暗くしすぎず、操作が奪われすぎない印象。
   - まだ紙吹雪は出さない。

2. `Reveal`
   - モーダル中央上部に選択中アバター。
   - アバター背面に薄い円形グロー。
   - 見出し: `7日分の記録が残りました`
   - サブコピー: `新しい記録バッジを手に入れました`
   - 補足: `ここまでの記録は残っています`
   - 右上に `スキップ`。

3. `Avatar Jump Peak`
   - アバターが上に浮き、ポーズが変化した状態を静止画で表現。
   - `kinniku` は拳を上げる参考イメージを使用。
   - 他アバターも、手を上げる、胸を張る、軽くジャンプするなどのポーズ差分を作る。
   - 紙吹雪は中量まで許可するが、画面全体を覆わない。

4. `Settled`
   - アバターが元の位置に戻り、グローが薄く残る。
   - CTA `閉じる` と `できたことを見る` が読みやすい。
   - ユーザーが次の操作へ戻れる落ち着いた状態。

5. `Reduced Motion`
   - ジャンプ、拡大、紙吹雪なし。
   - アバターと記録バッジがフェードで現れたような静かな見た目。
   - 見た目は通常版と同じ情報量にする。

6. `Multiple Badges`
   - 複数バッジが同時に解放された場合のまとめ表示。
   - 見出し例: `2つの記録バッジが増えました`
   - バッジ名を2〜3件まで並べ、それ以上は `ほか1件` のようにまとめる。

表現ルール:

- `レベルアップ` という言葉を大きく出さない。画面上は `記録バッジ`, `できたこと`, `記録が増えました` を優先する。
- `7日間達成!!` はレベルアップモーダル内の見出しとして使ってよい。
- アバターは笑顔/前向きでもよい。強すぎる煽り顔、攻撃的ポーズ、勝利/敗北を連想させる表現は避ける。
- 紙吹雪は最大でもモーダル周辺に留め、背景のHome/SOS/Reviewを覆わない。
- relapse直後にはこの演出を出さない。Restart Flowでは `ここまでの記録は残っています` の静かなカードにする。
- スキップしても記録バッジは保存されることが伝わる余白を残す。

## Achievement / Insights Requirements

目的:

ユーザーを序列化せず、できた行動を見返せるようにする。

表示:

- `記録バッジ` を基本表現にする。
- 獲得済み、未獲得を分ける場合、未獲得を暗くしすぎない。
- `次まであとX日` は小さく扱う。
- 行動ベースバッジを日数バッジと同じ重さで扱う。

MVPバッジ:

- はじめの一歩
- 記録の芽
- 7日分の記録
- 14日分の記録
- 30日分の記録
- 立ち止まれた
- また始められた
- 振り返れた

## Restart Flow Requirements

目的:

記録が途切れたあとに、責めずに再開できるようにする。

アバター/称号:

- 派手な称号演出は出さない。
- `ここまでの称号は残っています` を小さく安心材料として表示。
- アバターを悲しませる、暗くする、壊す表現は禁止。

推奨表示:

```text
ここまで続けたことは残っています。

最長記録        18日
累計成功時間    120時間
再開回数        3回
保持中の記録バッジ  5個

次の24時間の一手
...
```

## Settings Profile Requirements

目的:

アバター変更、演出控えめ設定、データ管理へ自然につなげる。

表示:

```text
ローカルプロフィール
[小アバター] ジャケット
目標: 集中を取り戻す
[アバターを変更]

表示と演出
演出を控えめにする

データ管理
データをエクスポート
すべてのデータを削除
```

注意:

- アバター変更に課金、条件解放、ロックを付けない。
- エクスポート/削除対象にアバターIDと記録バッジ状態が含まれることを説明できる余地を作る。

## Visual Tone

方向性:

- Calm
- Japanese-native
- Trustworthy
- Quietly encouraging
- Not medical
- Not adult-content-looking
- Not game-heavy

避ける方向:

- 派手なソシャゲ風
- ランキング/称号競争
- 黒背景にネオンだけの強い中毒対策感
- 宗教的、修行的、罰ゲーム的な雰囲気
- 露骨な性的文脈

## Microcopy Rules

使ってよい:

- `記録バッジ`
- `できたこと`
- `ここまでの記録は残っています`
- `小さく続いています`
- `今日も整える`
- `一緒に進むアイコンを選びましょう`

避ける:

- `失敗`
- `我慢できなかった`
- `リセット`
- `レベルダウン`
- `勝利`
- `完全達成`
- `治る`
- `治療`
- `医学的に証明`

## Deliverables Requested From ClaudeDesign

1. Android mobile screen mockups for the 6 requested screens.
2. Home layout with avatar and latest badge inserted without weakening SOS.
3. Avatar Selection grid using the 4 provided avatars.
4. Level-up / Record Badge modal, including reduced-motion version.
5. Achievement / Insights screen with badge list.
6. Restart Flow showing retained badges without blame.
7. Settings Profile with avatar change and motion preference.
8. Notes on spacing, component hierarchy, and what should remain above the fold.

## References

- `docs/02_ux/GAMIFICATION_UX.md`
- `docs/02_ux/AVATAR_SELECTION_FLOW.md`
- `docs/02_ux/ACHIEVEMENT_AND_TITLE_SYSTEM.md`
- `docs/02_ux/LEVEL_UP_MOTION_SPEC.md`
- `docs/02_ux/GAMIFICATION_MICROCOPY.md`
- `docs/02_ux/SCREEN_SPEC.md`
- `docs/02_ux/WIREFRAME_TEXT.md`
- `docs/01_architecture/ANIMATION_ASSET_STRATEGY.md`
- `docs/01_architecture/ASSET_LICENSES.md`
