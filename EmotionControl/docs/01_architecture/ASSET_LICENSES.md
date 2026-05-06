# Asset Licenses

## Purpose

EmotionLeaveで使うアバター、称号画像、演出素材の出所と利用条件を管理する。MVPではアバター素材をローカルに同梱するため、ストア公開前に権利確認を完了する。

## Current Avatar Assets

| assetId | file path | type | creator / provider | source | license / permission | commercial use | modification allowed | attribution required | status | notes |
|---|---|---|---|---|---|---|---|---|---|---|
| `avatar_jacket` | `assets/avatars/avatar_jacket.png` | avatar | User-provided | `C:/Users/nsyte/Downloads/jacket.png` | Project-owner confirmation required before release | TBD | TBD | TBD | Draft asset | Blue jacket pixel-art person. Background has been made transparent. |
| `avatar_centerpart` | `assets/avatars/avatar_centerpart.png` | avatar | User-provided | `C:/Users/nsyte/Downloads/centerpart.png` | Project-owner confirmation required before release | TBD | TBD | TBD | Draft asset | Brown jacket pixel-art person. Background has been made transparent. |
| `avatar_suit` | `assets/avatars/avatar_suit.png` | avatar | User-provided | `C:/Users/nsyte/Downloads/suit.png` | Project-owner confirmation required before release | TBD | TBD | TBD | Draft asset | Navy suit pixel-art person. Background has been made transparent. |
| `avatar_kinniku` | `assets/avatars/avatar_kinniku.png` | avatar | User-provided | `C:/Users/nsyte/Downloads/kinniku.png` | Project-owner confirmation required before release | TBD | TBD | TBD | Draft asset | Active white-shirt pixel-art person. Background has been made transparent. |

## Release Gate

Before using these assets in a public build:

- Confirm the project has the right to use each image in the app.
- Confirm commercial use is allowed if the app is monetized later.
- Confirm modification and resizing are allowed.
- Confirm whether attribution is required.
- Confirm the assets are not derived from protected characters, games, anime, manga, or competitor app assets.
- Keep original files and edited derivatives separate.

## Naming Rules

- Repo asset file names use lowercase snake case.
- Runtime IDs use the same stem as the file name without `.png`.
- UI labels must be neutral and non-ranking: `ジャケット`, `センターパート`, `スーツ`, `アクティブ`.
- Do not use rarity, rank, power, medical, or personality labels.

## Future Additions

For every new asset, add:

- `assetId`
- `file path`
- `type`
- `creator / provider`
- `source`
- `license / permission`
- `commercial use`
- `modification allowed`
- `attribution required`
- `status`
- `notes`
