# Achievement And Title System

## Concept

称号は、ユーザーを序列化するものではなく、積み重ねた行動を静かに残すための記録である。画面上では `称号` よりも `記録バッジ`, `できたこと`, `行動の記録` を優先して使う。日数だけでなく、SOS、振り返り、再開などEmotionLeaveが重視する行動も称える。

## Title Policy

- 人に見られても恥ずかしくない名称にする。
- 指定された称号名はバッジアセットと一致させる。ただし、ユーザー同士の上下比較、人格評価、責めの文脈では使わない。
- 競合の称号名、階級名、動物名をコピーしない。
- relapse後も一度獲得した称号は残す。
- 称号は医療効果を示さない。
- 称号をユーザーの人格評価にしない。
- 連続記録の称号だけでなく、SOS、振り返り、再開などの行動記録もPhase 2以降で扱う。

## MVP Titles

| ID | 称号 | 解放条件 | 表示タイミング | 意味 |
|---|---|---|---|---|
| title_1_day | はじめの一歩 | 1日達成 | Home/Level-up modal | 始めたことを肯定する |
| title_3_days | 流れを変える者 | 3日連続達成 | Home/Level-up modal | 生活の流れが変わり始めた節目 |
| title_7_days | 強固な意思 | 7日連続達成 | Level-up modal | 1週間の節目 |
| title_14_days | 弱さとの決別 | 14日連続達成 | Level-up modal | 2週間の節目 |
| title_30_days | 強者 | 30日連続達成 | Level-up modal | 1か月の節目 |
| title_60_days | 漢 | 60日連続達成 | Level-up modal | 長期継続の節目 |
| title_90_days | 黄金の精神 | 90日連続達成 | Level-up modal | 3か月の節目 |

## Phase 2 Titles

| ID | 称号 | 解放条件 |
|---|---|---|
| title_reason | 理由を見返せた | Reason Card表示/閲覧 5回 |
| title_calm_week | 整えた週 | 7日間でReview 5回以上 |
| title_after_urge | 波を越えた | SOS後にHomeへ戻る 3回 |
| title_export_ready | 自分で管理できる | Data Export画面を確認 |
| title_privacy_ready | 守る準備 | App Lock有効化 |
| title_sos_used | 立ち止まれた | SOS完了1回 |
| title_restart | また始められた | Restart Flow完了1回 |
| title_review | 振り返れた | Daily Review完了5回 |

## Display Timing

称号解放は次のタイミングで表示できる。

- Daily Pledge保存後
- Daily Review保存後
- SOS完了後
- Restart Flow完了後
- Home起動時の未表示キュー

注意:

- SOS開始直後には称号を表示しない。
- relapse記録中に称号演出を割り込ませない。
- relapse完了直後は派手に祝わず、`再開できた` を静かに記録する。
- 夜のReview完了直後など、ユーザーが落ち着いている場面で表示する。

## Relapse Handling

relapse時:

- 獲得済み称号を剥奪しない。
- 称号一覧に `保持中` として残す。
- 現行記録に基づく次の称号までの日数は再計算する。
- 最長記録に基づく称号は保持する。
- `レベルダウン`, `称号を失いました`, `やり直し` は使わない。
- アバターの表情や見た目を落ち込ませない。

表示例:

`ここまでの称号は残っています。次の24時間をまた整えましょう。`

## Data Model Notes

MVPで必要な状態:

- selectedAvatarId
- unlockedTitleIds
- titleUnlockedAt
- lastTitleModalShownAt
- motionPreference

実装時の注意:

- 称号はローカル保存。
- エクスポート対象にする場合は、称号IDと解放日時のみ。
- 全削除時は称号状態も削除。

## Acceptance Criteria

- 1日、3日、7日、14日、30日、60日、90日の節目で指定称号が解放される。
- Phase 2ではSOSやRestartなど日数以外の行動称号を追加できる。
- 一度獲得した称号はrelapse後も残る。
- 称号名が恥ずかしくない。
- 称号に医療効果や性的表現がない。
- ガチャやランダム報酬がない。
- missed check-inで称号を剥奪しない。
