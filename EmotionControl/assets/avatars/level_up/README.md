# Level-Up Avatar Frames

This directory contains transparent PNG frame sequences for the level-up / record badge modal.

## Structure

```text
assets/avatars/level_up/
  avatar_jacket/
    frame_00_idle.png
    frame_01_fist_ready.png
    frame_02_raise.png
    frame_03_peak.png
    frame_04_settle.png
    sprite_sheet.png
  avatar_centerpart/
    frame_00_idle.png
    frame_01_fist_ready.png
    frame_02_raise.png
    frame_03_peak.png
    frame_04_settle.png
    sprite_sheet.png
  avatar_suit/
    frame_00_idle.png
    frame_01_fist_ready.png
    frame_02_raise.png
    frame_03_peak.png
    frame_04_settle.png
    sprite_sheet.png
  avatar_kinniku/
```

## Runtime Use

- Play `frame_*.png` in filename order.
- Recommended interval: 80-140ms per frame.
- Loop count: 1.
- After playback, hold the final frame or return to idle.
- If a frame is missing, fall back to the base avatar PNG plus Compose scale/offset animation.
- Reduced Motion should not play frame animation; use one static representative frame with fade.

## Current Frame Notes

- `avatar_jacket` uses a user-provided fist-pump sequence as the reference.
- `avatar_centerpart` uses a user-provided fist-pump sequence as the reference.
- `avatar_suit` uses a user-provided fist-pump sequence as the reference.
- `avatar_kinniku` uses a user-provided fist-raise sequence as the reference.
- Future hand-drawn or generated pose frames can replace the current files as long as filenames stay stable.

## Visual Guardrails

- Use only inside the level-up / record badge modal.
- Do not play during SOS start, Relapse Log, Privacy Lock, or immediately after relapse.
- Keep the animation skippable.
- Do not add sound, vibration, gacha-like effects, or level-down states.
