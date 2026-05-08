# Ads and Subscription Policy Review

## 目的

広告表示、サブスクリプション、AIアドバイスが、ユーザー体験、ストア審査、法務・ポリシー上のリスクを高めないように事前に整理する。

## 広告表示のリスク

- SOS画面に広告を出すと、緊急導線を妨げ、信頼を損なう。
- relapse記録やRestart Flowで全画面広告を出すと、弱っている状態のユーザーから搾取しているように見える。
- 入力欄や主要CTAに広告が被ると、誤タップや離脱が増える。
- 広告SDKにセンシティブな記録やメモが渡ると、プライバシーリスクが高い。

## サブスクリプション表示のリスク

- 初回起動直後の強いペイウォールは、支援アプリとしての信頼を損なう。
- relapse直後の課金誘導は、ユーザーの弱さにつけ込んでいるように見える。
- SOSの完全有料化は、中核価値を課金で遮断しているように見える。
- AI機能の説明が医療効果や治療効果に見えると、ストア審査や法務リスクが高い。

## 初回ペイウォールを避ける方針

初回オンボーディング中は、まずHomeへ到達し、基本価値を体験できることを優先する。課金案内は、週次レポート、詳細インサイト、AI SOS提案など、Plus機能を開いたタイミングに限定する。

## SOS完全有料化を避ける方針

基本SOSはFreeで提供する。EmotionLeaveの中核価値は衝動が来た瞬間の支援であり、ここを完全有料にしない。PlusはAIによる高度な分析、個別化、次回対策に価値を置く。

## relapse直後の課金誘導を避ける方針

relapse記録直後は、責めない再開体験を最優先する。課金誘導、恐怖訴求、比較訴求を表示しない。再開後、落ち着いたタイミングで詳細分析への導線を置く場合も、任意で控えめにする。

## 無料トライアルと解約説明の注意点

- 料金、請求周期、無料トライアル終了後の課金開始を明確に表示する。
- 解約方法を分かりやすく説明する。
- トライアル終了前後の通知や表示は、ストアポリシーに合わせる。
- 解約後もデータ削除、エクスポート、基本SOSを利用できるようにする。
- Plus解約後のAI分析履歴、AI分析用データ削除、広告再表示の扱いを明確にする。

## ストア審査上の注意点

- 広告表示と課金機能の説明をストア表示とアプリ内表示で矛盾させない。
- サブスクリプション価格、期間、自動更新、解約方法を明記する。
- AIアドバイスを医療、治療、診断として表現しない。
- センシティブな内容を扱うアプリであるため、年齢配慮とコンテンツ表現を確認する。
- 広告SDK、分析SDK、AIサービスのデータ利用目的をプライバシーポリシーに反映する。
- 健康・ウェルビーイング領域として審査される可能性を前提に、収集データ、利用目的、第三者提供、削除方法を明確にする。
- 将来のブロッカーでAccessibility Serviceを使う場合は、ユーザーに見える目的、任意性、取得範囲、隠れた追跡に使わないことを説明する。
- 18歳未満の利用を許可するかどうか、性的文脈をどの程度扱うかをリリース前に確定する。

## AIアドバイスの表現リスク

AIの出力や課金訴求で、以下のような表現を避ける。

- 治る。
- 完治。
- 医学的に改善。
- 依存症を診断。
- Plusなら防げます。
- AIがあなたを治します。

推奨する表現は、記録の振り返り、傾向、可能性、次に試せる工夫に限定する。

## legal-policy / privacy-security checklist

- [ ] SOS画面とレベルアップ演出中に広告を出さない。
- [ ] 初回起動直後に強い課金誘導を出さない。
- [ ] relapse直後に課金誘導を出さない。
- [ ] AI分析前に同意を取る。
- [ ] メモとrelapse記録をAI分析対象から外せる。
- [ ] AI分析用データを削除できる。
- [ ] 広告SDKにセンシティブデータを渡さない。
- [ ] 医療・治療・診断表現を使わない。
- [ ] サブスクリプション画面に価格、期間、自動更新、解約方法、Freeで残る機能が表示される。
- [ ] AIプロバイダーのデータ保持、学習利用、ログ処理を確認し、ユーザー向け説明に反映する。
- [ ] ストアメタデータに治療、診断、回復保証、AIセラピスト表現がない。

## Policy References

- Apple App Review Guidelines: https://developer.apple.com/app-store/review/guidelines/
- Apple auto-renewable subscriptions: https://developer.apple.com/app-store/subscriptions/
- Google Play User Data policy: https://support.google.com/googleplay/android-developer/answer/10144311
- Google Play Health Content and Services: https://support.google.com/googleplay/android-developer/answer/12261419
- Google Play AccessibilityService API policy: https://support.google.com/googleplay/android-developer/answer/10964491
- Google Play subscriptions overview: https://support.google.com/googleplay/android-developer/answer/12154973
- OpenAI API data controls: https://platform.openai.com/docs/models/how-we-use-your-data/
