# PR Body Draft: Monetization, Ads, and AI Plus Policy

## Summary

This PR documents EmotionLeave's monetization direction and includes the current MVP app foundation work needed for internal testing. The proposed model keeps the core recovery loop free, shows ads to Free users only in non-sensitive contexts, and places Plus value on AI-powered reflection, trend analysis, personalized advice, and ad removal.

## Monetization Policy

- Free users can use the core app: counter, avatar, home avatar, achievements, level-up modal, light motion effects, Daily Pledge, Daily Review, basic SOS, relapse log, calendar, and ads.
- Basic SOS remains Free because it is the core emergency support experience.
- Plus users get ad removal and AI-powered weekly/monthly reports, relapse trend analysis, time-of-day analysis, trigger analysis, SOS usage analysis, personalized advice, AI SOS suggestions, and custom SOS templates.
- The app must not show a strong paywall immediately after first launch, during SOS, immediately after relapse logging, or during level-up animation.
- Data deletion, export, privacy controls, basic SOS, and core records must not be paywalled.

## Ads Policy

- Free users may see banner ads on eligible screens such as Home, Calendar, Insights, Achievements, Settings, Daily Pledge, Daily Review, relapse log, and Restart Flow.
- Ads must not appear on the SOS screen or during level-up animation.
- Ads must not cover inputs, primary CTAs, save buttons, restart actions, or privacy/data controls.
- Full-screen ads, video ads, forced-wait ads, and reward ads are out of scope for MVP.
- Ads must not be personalized from sensitive records such as relapse history, urges, sexual behavior, notes, trigger tags, blocker history, or health-like self reports.

## AI Plus Policy

- AI analysis is Plus-only and opt-in.
- AI output is positioned as record-based reflection support, not medical diagnosis, treatment, prevention, addiction assessment, emergency care, or guaranteed recovery.
- Users can choose whether notes and relapse records are included in AI analysis.
- Users can delete AI analysis data, including summaries, prompts, responses, features, and caches.
- AI must use language such as "記録から見える傾向です" and avoid claims such as "治ります", "依存症です", or "必ず克服できます".

## Risk Review

- UX risk: monetization could damage trust if introduced before users experience value.
- Ethical risk: paywalling SOS or showing upgrade prompts after relapse may feel coercive.
- Privacy risk: AI and ad SDKs may touch highly sensitive habit, urge, relapse, and trigger data unless minimized and consented.
- Legal/policy risk: medical, treatment, addiction, or recovery-guarantee claims may create store review and legal risk.
- Store risk: subscriptions require clear pricing, renewal, cancellation, trial, and entitlement details.
- Ads risk: personalized ads based on sensitive behavior or health-adjacent signals must be avoided.

## Implementation Progress

- Added Android MVP foundation with local-first encrypted persistence.
- Added onboarding, Home, Daily Pledge, Daily Review, basic SOS, relapse/restart, records, settings, export, delete, and optional daily reminder flows.
- Updated Home statistics to use saved start date, habit state, pledges, reviews, SOS, and relapse records instead of placeholder-only copy.
- Updated relapse/restart flow so current streak restarts from the new pledge date while the prior streak can contribute to longest streak.
- Updated local data deletion to cancel reminders and clear export cache.
- Updated notification worker so it exits quietly when notifications are disabled.
- Added manual QA checklist and store listing copy draft for release preparation.
- Added automated coverage for streak calculation, relapse/restart calculation, notification opt-out policy, and export cache cleanup.

## Next Minimal Implementation Tasks

1. Complete `docs/04_qa/MANUAL_QA_CHECKLIST.md` on a real device or emulator before store submission.
2. Add release signing, versionCode/versionName policy, and release build verification.
3. Add entitlement flags for `free` and `plus` without changing core flows.
4. Add a screen/state-level ad eligibility helper that always returns false for SOS and level-up animation.
5. Add a quiet Plus entry point in Settings and locked AI Insight surfaces only.
6. Add AI analysis consent settings: AI on/off, include notes on/off, include relapse records on/off, delete AI analysis data.
7. Add AI disclaimer copy to AI report and AI SOS surfaces.
8. Prepare App Store Privacy Nutrition Label, Google Play Data Safety, and the public privacy policy from the actual SDK/data flow inventory.

## Commander Review

- Scope: policy docs plus Android MVP foundation for internal testing.
- Decisions: Free keeps core recovery and basic SOS; Plus monetizes ad removal and AI personalization.
- Risks: sensitive data processing, medical overclaiming, store review, shame/fear-based upsell, intrusive ads.
- Deferred items: SDK selection, implementation, pricing experiments, Pro plan, on-device AI, full-screen/reward ads.
- Required follow-up: implementation PRs must include privacy-security and legal-policy review before shipping AI or ads.
