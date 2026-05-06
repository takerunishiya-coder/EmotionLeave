/* global React, ReactDOM */
const { useState, useEffect, useMemo } = React;

// ── Avatar / Title-badge asset paths ────────────────────────────────────────
const AVATAR_SRC = {
  avatar_jacket:     "assets/avatars/avatar_jacket.png",
  avatar_centerpart: "assets/avatars/avatar_centerpart.png",
  avatar_suit:       "assets/avatars/avatar_suit.png",
  avatar_kinniku:    "assets/avatars/avatar_kinniku.png",
};
const FRAME_NAMES = [
  "frame_00_idle",
  "frame_01_fist_ready",
  "frame_02_raise",
  "frame_03_peak",
  "frame_04_settle",
];
const frameSrc = (avatarId, frameName) =>
  `assets/level_up/${avatarId}/${frameName}.png`;

const TITLE_BADGE_SRC = {
  hajimenoippo:       "assets/title_badges/title_hajimenoippo.png",
  kyoukonaishi:       "assets/title_badges/title_kyoukonaishi.png",
  kyousya:            "assets/title_badges/title_kyousya.png",
  nagarewokaeru:      "assets/title_badges/title_nagarewokaeru.png",
  otoko:              "assets/title_badges/title_otoko.png",
  ougonnoseishin:     "assets/title_badges/title_ougonnoseishin.png",
  yowasatonoketsubetsu:"assets/title_badges/title_yowasatonoketsubetsu.png",
};

// Avatar — actual PNG (object-contain, square)
function AvatarSlot({ id, hint }) {
  const src = AVATAR_SRC[id] || AVATAR_SRC.avatar_jacket;
  return (
    <img
      src={src}
      alt={hint || "avatar"}
      style={{ width: "100%", height: "100%", objectFit: "contain", display: "block" }}
      draggable={false}
    />
  );
}

// Avatar with optional gutspose frame sequence, used in Level-up modal.
// state controls which frame is shown:
//   "before"   → idle
//   "reveal"   → idle → fist_ready (auto-advance)
//   "peak"     → frame_03_peak (held)
//   "settled"  → animate idle→peak→settle once, then hold settle
//   "rm"       → static settle (no motion)
//   "multi"    → same as settled
function AvatarFrameStage({ avatarId, state, reducedMotion, size = 200 }) {
  const [idx, setIdx] = React.useState(() => {
    if (state === "before") return 0;
    if (state === "peak") return 3;
    if (state === "rm" || reducedMotion) return 4;
    return 0;
  });
  React.useEffect(() => {
    if (reducedMotion || state === "rm") { setIdx(4); return; }
    if (state === "before") { setIdx(0); return; }
    if (state === "peak")   { setIdx(3); return; }
    // reveal / settled / multi → play 0→4 once, hold last
    let cancelled = false;
    setIdx(0);
    const durations = [120, 110, 110, 220, 140]; // ms per frame; peak held longer
    let i = 0;
    const tick = () => {
      if (cancelled) return;
      if (i >= FRAME_NAMES.length - 1) return;
      i += 1;
      setIdx(i);
      setTimeout(tick, durations[i]);
    };
    const t = setTimeout(tick, durations[0]);
    return () => { cancelled = true; clearTimeout(t); };
  }, [state, reducedMotion, avatarId]);

  return (
    <div style={{ position: "relative", width: size, height: size }}>
      {/* Preload all 5 frames so swaps are instant; only one visible at a time */}
      {FRAME_NAMES.map((name, i) => (
        <img
          key={name}
          src={frameSrc(avatarId, name)}
          alt=""
          draggable={false}
          style={{
            position: "absolute", inset: 0,
            width: "100%", height: "100%",
            objectFit: "contain",
            opacity: i === idx ? 1 : 0,
            transition: reducedMotion ? "opacity .35s" : "opacity .04s linear",
          }}
        />
      ))}
    </div>
  );
}

// ── Tiny icon set ───────────────────────────────────────────────────────────
const Icon = ({ name, size = 20 }) => {
  const s = size;
  const props = { width: s, height: s, viewBox: "0 0 24 24", fill: "none", stroke: "currentColor", strokeWidth: 1.7, strokeLinecap: "round", strokeLinejoin: "round" };
  switch (name) {
    case "lock": return <svg {...props}><rect x="4" y="11" width="16" height="9" rx="2"/><path d="M8 11V8a4 4 0 1 1 8 0v3"/></svg>;
    case "settings": return <svg {...props}><circle cx="12" cy="12" r="3"/><path d="M19.4 15a1.7 1.7 0 0 0 .3 1.8l.1.1a2 2 0 1 1-2.8 2.8l-.1-.1a1.7 1.7 0 0 0-1.8-.3 1.7 1.7 0 0 0-1 1.5V21a2 2 0 1 1-4 0v-.1a1.7 1.7 0 0 0-1.1-1.5 1.7 1.7 0 0 0-1.8.3l-.1.1A2 2 0 1 1 4.3 17l.1-.1a1.7 1.7 0 0 0 .3-1.8 1.7 1.7 0 0 0-1.5-1H3a2 2 0 1 1 0-4h.1a1.7 1.7 0 0 0 1.5-1 1.7 1.7 0 0 0-.3-1.8l-.1-.1A2 2 0 1 1 7 4.3l.1.1a1.7 1.7 0 0 0 1.8.3H9a1.7 1.7 0 0 0 1-1.5V3a2 2 0 1 1 4 0v.1a1.7 1.7 0 0 0 1 1.5 1.7 1.7 0 0 0 1.8-.3l.1-.1a2 2 0 1 1 2.8 2.8l-.1.1a1.7 1.7 0 0 0-.3 1.8V9a1.7 1.7 0 0 0 1.5 1H21a2 2 0 1 1 0 4h-.1a1.7 1.7 0 0 0-1.5 1z"/></svg>;
    case "back": return <svg {...props}><path d="M15 18l-6-6 6-6"/></svg>;
    case "check": return <svg {...props} strokeWidth="2.5"><polyline points="20 6 9 17 4 12"/></svg>;
    case "chev": return <svg {...props}><path d="M9 18l6-6-6-6"/></svg>;
    case "shield": return <svg {...props}><path d="M12 3l8 3v6c0 5-3.5 8-8 9-4.5-1-8-4-8-9V6z"/></svg>;
    case "leaf": return <svg {...props}><path d="M11 20A7 7 0 0 1 4 13c0-5 4-9 9-9h7v7c0 5-4 9-9 9z"/><path d="M11 20l4-12"/></svg>;
    case "sprout": return <svg {...props}><path d="M7 20h10"/><path d="M12 20V10"/><path d="M12 10c-3 0-5-2-5-5 3 0 5 2 5 5z"/><path d="M12 10c3 0 5-2 5-5-3 0-5 2-5 5z"/></svg>;
    case "moon": return <svg {...props}><path d="M21 12.8A9 9 0 1 1 11.2 3 7 7 0 0 0 21 12.8z"/></svg>;
    case "phone": return <svg {...props}><path d="M5 4h4l2 5-2.5 1.5a11 11 0 0 0 5 5L15 13l5 2v4a2 2 0 0 1-2 2A16 16 0 0 1 3 6a2 2 0 0 1 2-2"/></svg>;
    case "wind": return <svg {...props}><path d="M3 8h12a3 3 0 1 0-3-3"/><path d="M3 12h17a3 3 0 1 1-3 3"/><path d="M3 16h9"/></svg>;
    case "book": return <svg {...props}><path d="M4 4h11a3 3 0 0 1 3 3v13H7a3 3 0 0 1-3-3z"/><path d="M4 17a3 3 0 0 1 3-3h11"/></svg>;
    case "home": return <svg {...props}><path d="M3 12l9-8 9 8"/><path d="M5 10v10h14V10"/></svg>;
    case "chart": return <svg {...props}><path d="M4 20V10"/><path d="M10 20V4"/><path d="M16 20v-8"/><path d="M22 20H2"/></svg>;
    case "plus": return <svg {...props}><path d="M12 5v14M5 12h14"/></svg>;
    case "x": return <svg {...props}><path d="M18 6L6 18M6 6l12 12"/></svg>;
    case "alert": return <svg {...props}><path d="M12 9v4M12 17h.01"/><circle cx="12" cy="12" r="9"/></svg>;
    case "download": return <svg {...props}><path d="M12 3v12"/><path d="M7 10l5 5 5-5"/><path d="M5 21h14"/></svg>;
    case "trash": return <svg {...props}><path d="M3 6h18"/><path d="M8 6V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/><path d="M19 6l-1 14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6"/></svg>;
    default: return null;
  }
};

// ── Status bar + nav bar ───────────────────────────────────────────────────
function StatusBar() {
  return (
    <div className="statusbar">
      <span>9:30</span>
      <div className="right">
        <svg width="14" height="10" viewBox="0 0 14 10"><path d="M7 8.5L.5 4a9 9 0 0 1 13 0L7 8.5z" fill="currentColor"/></svg>
        <svg width="14" height="10" viewBox="0 0 14 10"><path d="M13 9V1L1 9h12z" fill="currentColor"/></svg>
        <svg width="18" height="10" viewBox="0 0 18 10"><rect x="0.5" y="1" width="14" height="8" rx="1.5" stroke="currentColor" fill="none"/><rect x="2" y="2.5" width="9" height="5" fill="currentColor"/><rect x="15" y="3.5" width="2" height="3" rx="0.5" fill="currentColor"/></svg>
      </div>
    </div>
  );
}

function NavBar() {
  return (
    <div style={{ height: 18, display: "flex", alignItems: "center", justifyContent: "center" }}>
      <div style={{ width: 110, height: 4, borderRadius: 2, background: "var(--text-2)", opacity: 0.4 }} />
    </div>
  );
}

// ── App shell with tabs ─────────────────────────────────────────────────────
function TabBar({ active, onChange }) {
  const tabs = [
    { id: "home", icon: "home", label: "ホーム" },
    { id: "achievement", icon: "chart", label: "できたこと" },
    { id: "settings", icon: "settings", label: "設定" },
  ];
  return (
    <div className="tabbar">
      {tabs.map(t => (
        <button key={t.id} className={active === t.id ? "active" : ""} onClick={() => onChange(t.id)}>
          <Icon name={t.icon} size={22} />
          <span>{t.label}</span>
        </button>
      ))}
    </div>
  );
}

// ── Avatars (4 MVP) ─────────────────────────────────────────────────────────
const AVATARS = [
  { id: "avatar_jacket", label: "ジャケット" },
  { id: "avatar_centerpart", label: "センターパート" },
  { id: "avatar_suit", label: "スーツ" },
  { id: "avatar_kinniku", label: "アクティブ" },
];

// ── Badges (記録バッジ / 称号) ───────────────────────────────────────────────
// MVP: 落ち着いた『記録バッジ』中心。強い称号は achievement 一覧の最後の
// 『これから』セクションだけに置く。
const BADGES = [
  { id: "first",  name: "はじめの一歩",   plate: "hajimenoippo",      icon: "sprout", earned: true,  when: "5/1" },
  { id: "sprout", name: "記録の芽",      plate: "hajimenoippo",      icon: "leaf",   earned: true,  when: "5/2" },
  { id: "stop",   name: "立ち止まれた",   plate: "nagarewokaeru",     icon: "shield", earned: true,  when: "5/3" },
  { id: "review", name: "振り返れた",     plate: "nagarewokaeru",     icon: "book",   earned: true,  when: "5/4" },
  { id: "again",  name: "また始められた", plate: "nagarewokaeru",     icon: "sprout", earned: true,  when: "5/5" },
  { id: "d7",     name: "7日分の記録",    plate: "kyoukonaishi",      icon: "leaf",   earned: false, meta: "あと2日"  },
  { id: "d14",    name: "14日分の記録",   plate: "yowasatonoketsubetsu", icon: "leaf", earned: false, meta: "あと9日"  },
  { id: "d30",    name: "30日分の記録",   plate: "ougonnoseishin",    icon: "leaf",   earned: false, meta: "あと25日" },
];

// 強い称号 — 達成モーダル / 称号一覧の隠しセクションだけで使う
const RARE_TITLES = [
  { id: "kyousya", name: "強者",          plate: "kyousya",     hint: "30日分の記録の先" },
  { id: "otoko",   name: "漢",            plate: "otoko",       hint: "60日分の記録の先" },
  { id: "ketsubetsu", name: "弱さとの決別", plate: "yowasatonoketsubetsu", hint: "90日分の記録の先" },
];

// ── Screens ────────────────────────────────────────────────────────────────

function HomeScreen({ onSOS, onPledge, onReview, onAvatar, onBadgeOpen, selectedAvatar }) {
  return (
    <>
      <div className="app-header">
        <div className="brand">EmotionLeave</div>
        <button className="icon-btn" aria-label="lock"><Icon name="lock" /></button>
      </div>
      <div className="scroll">
        {/* Today status */}
        <div className="card">
          <div className="today">
            <div className="today-text">
              <div className="day-num">5<small>日目</small></div>
              <div className="lede">小さく続いています</div>
            </div>
            <div className="avatar-mini" onClick={onAvatar} role="button">
              <AvatarSlot id={selectedAvatar} hint="アバター" />
            </div>
          </div>
          <div className="stats">
            <div className="stat"><div className="v">18<span style={{ fontSize: 11, color: "var(--text-3)", marginLeft: 2 }}>日</span></div><div className="l">最長記録</div></div>
            <div className="stat"><div className="v">120<span style={{ fontSize: 11, color: "var(--text-3)", marginLeft: 2 }}>時間</span></div><div className="l">累計成功</div></div>
            <div className="stat"><div className="v">3<span style={{ fontSize: 11, color: "var(--text-3)", marginLeft: 2 }}>回</span></div><div className="l">再開回数</div></div>
          </div>
        </div>

        {/* Daily loop */}
        <div className="section-label">今日のループ</div>
        <div className="card card-tight">
          <div className="loop">
            <span className="pill done"><Icon name="check" size={12}/>朝の誓約</span>
            <span className="pill todo"><span className="dot" />夜の振り返り</span>
          </div>
          <div className="btn-row">
            <button className="btn" onClick={onPledge}>誓約を見る</button>
            <button className="btn btn-primary" onClick={onReview}>今日の振り返り</button>
          </div>
        </div>

        {/* SOS */}
        <div style={{ marginTop: 18 }}>
          <button className="btn-sos" onClick={onSOS}>
            <Icon name="shield" size={20}/>
            <span>
              SOS
              <span className="sub">衝動が来たら、まずここをタップ</span>
            </span>
          </button>
        </div>

        {/* Reason */}
        <div className="section-label">今日の理由</div>
        <div className="card">
          <div className="reason">
            <span className="label">REASON</span>
            夜を落ち着いて終える
          </div>
        </div>

        {/* Latest badge */}
        <div className="badge-row" onClick={onBadgeOpen} role="button">
          <div className="badge-plate-mini">
            <img src={TITLE_BADGE_SRC.hajimenoippo} alt="記録の芽" />
          </div>
          <div>
            <div className="name">記録の芽</div>
            <div style={{ fontSize: 11, color: "var(--text-3)", marginTop: 2 }}>最新の記録バッジ</div>
          </div>
          <div className="when">5/2</div>
        </div>
        <div style={{ height: 8 }}/>
      </div>
    </>
  );
}

function OnboardingScreen({ onNext }) {
  return (
    <div className="onboard">
      <div className="top">
        <div style={{ width: 56, height: 56, borderRadius: 16, background: "var(--accent-tint)", display: "flex", alignItems: "center", justifyContent: "center", color: "var(--accent)" }}>
          <Icon name="leaf" size={28}/>
        </div>
        <h1>生活を整えるための、<br/>小さな記録アプリです。</h1>
        <p>毎日10〜30秒のチェックインで、衝動と距離を取りやすくします。記録が途切れても、責められずに再開できます。</p>
      </div>
      <div className="dots"><i className="on"/><i/><i/></div>
      <button className="btn btn-primary" onClick={onNext}>はじめる</button>
    </div>
  );
}

function AvatarSelectionScreen({ onConfirm, onSkip, selected, setSelected }) {
  return (
    <>
      <div className="app-back">
        <button className="icon-btn" onClick={onSkip}><Icon name="back"/></button>
      </div>
      <div className="scroll">
        <div style={{ padding: "8px 4px 18px" }}>
          <h1 style={{ fontSize: 20, fontWeight: 600, margin: "0 0 6px" }}>一緒に進むアイコンを選びましょう</h1>
          <p style={{ fontSize: 13, color: "var(--text-2)", lineHeight: 1.6, margin: 0 }}>
            小さな相棒をあとで選べます。今はこのまま始められます。
          </p>
        </div>

        <div className="av-grid">
          {AVATARS.map(a => (
            <div
              key={a.id}
              className={"av-card" + (selected === a.id ? " selected" : "")}
              onClick={() => setSelected(a.id)}
            >
              <div className="check"><Icon name="check" size={14}/></div>
              <div className="thumb">
                <AvatarSlot id={a.id} hint={a.label} />
              </div>
              <div className="label">{a.label}</div>
            </div>
          ))}
        </div>

        <div style={{ marginTop: 22, padding: "14px 16px", background: "var(--surface-2)", borderRadius: 12, display: "flex", alignItems: "center", gap: 12 }}>
          <div style={{ width: 32, height: 32, borderRadius: 8, background: "var(--surface)", border: "1px solid var(--line)", overflow: "hidden", flexShrink: 0 }}>
            <AvatarSlot id={selected} hint="" />
          </div>
          <div style={{ fontSize: 12, color: "var(--text-2)", lineHeight: 1.5 }}>
            Homeではこのくらい小さく表示されます
          </div>
        </div>

        <div style={{ marginTop: 24, display: "flex", flexDirection: "column", gap: 10 }}>
          <button className="btn btn-primary" onClick={onConfirm}>このアイコンで始める</button>
          <button className="btn btn-ghost" onClick={onSkip}>あとで選ぶ</button>
        </div>
      </div>
    </>
  );
}

// state: "before" | "reveal" | "peak" | "settled" | "rm" | "multi"
function LevelUpModal({
  onClose, reducedMotion, selectedAvatar,
  state = "settled",
  badge = "7日分の記録",
  badgePlate = "kyoukonaishi",
}) {
  const confettiPieces = useMemo(() => {
    return Array.from({ length: 14 }, (_, i) => ({
      left: Math.random() * 100,
      delay: Math.random() * 0.35,
      color: ["oklch(0.82 0.10 80)", "oklch(0.74 0.08 165)", "oklch(0.78 0.07 200)", "oklch(0.86 0.06 60)"][i % 4]
    }));
  }, []);

  const isRM = reducedMotion || state === "rm";
  const showConfetti = !isRM && (state === "reveal" || state === "peak" || state === "settled");
  const showGlow     = state !== "before";
  const titleOpacity = state === "before" ? 0 : 1;

  // Headline copy
  const headline =
    state === "before" ? "\u00A0" :
    state === "rm"     ? "7日分の記録が残りました" :
                         "7日間達成!!";

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className={"modal lvlmodal " + (isRM ? "rm" : "") + " state-" + state} onClick={e => e.stopPropagation()}>
        <button className="skip" onClick={onClose}>スキップ</button>

        {showConfetti && (
          <div className="confetti">
            {confettiPieces.map((p, i) => (
              <i key={i} style={{ left: p.left + "%", background: p.color, animationDelay: p.delay + "s" }} />
            ))}
          </div>
        )}

        {/* Headline (small lead) */}
        <div className="lvl-lead" style={{ opacity: titleOpacity }}>
          記録バッジが増えました
        </div>
        <h2 className="lvl-headline" style={{ opacity: titleOpacity }}>
          {headline}
        </h2>

        {/* Title-badge plate (hero image) */}
        <div className="title-plate" style={{ opacity: state === "before" ? 0.35 : 1 }}>
          <img
            src={TITLE_BADGE_SRC[badgePlate] || TITLE_BADGE_SRC.kyoukonaishi}
            alt={badge}
            className="plate-img"
          />
        </div>

        {/* Avatar — gutspose frame stage */}
        <div className={"av-stage " + (isRM ? "rm" : "") + " s-" + state}>
          {showGlow && <div className="glow"/>}
          <AvatarFrameStage
            avatarId={selectedAvatar}
            state={state}
            reducedMotion={isRM}
            size={180}
          />
        </div>

        <p className="lvl-sub" style={{ opacity: titleOpacity }}>
          新しい記録バッジを手に入れました。<br/>
          <span style={{ color: "var(--text-3)" }}>ここまでの記録は残っています。</span>
        </p>

        <div className="actions">
          <button className="btn btn-ghost" onClick={onClose}>閉じる</button>
          <button className="btn btn-primary" onClick={onClose}>できたことを見る</button>
        </div>
      </div>
    </div>
  );
}

function AchievementScreen() {
  const [tab, setTab] = useState("badge");
  const earned = BADGES.filter(b => b.earned);
  return (
    <>
      <div className="app-header">
        <div className="brand">できたこと</div>
      </div>
      <div className="scroll">
        <div className="tabs">
          <button className={tab === "badge" ? "active" : ""} onClick={() => setTab("badge")}>記録バッジ</button>
          <button className={tab === "insight" ? "active" : ""} onClick={() => setTab("insight")}>振り返り</button>
        </div>

        <div className="summary">
          <div className="cell"><div className="v">5</div><div className="l">獲得済み</div></div>
          <div className="cell"><div className="v">18<span style={{fontSize:11,color:"var(--text-3)"}}>日</span></div><div className="l">最長</div></div>
          <div className="cell"><div className="v">120<span style={{fontSize:11,color:"var(--text-3)"}}>h</span></div><div className="l">累計</div></div>
        </div>

        <div className="section-label">獲得済み({earned.length})</div>
        <div className="badge-grid">
          {earned.map(b => (
            <div key={b.id} className="badge-tile">
              <div className="plate-thumb">
                <img src={TITLE_BADGE_SRC[b.plate]} alt={b.name} />
              </div>
              <div className="name">{b.name}</div>
              <div className="meta">{b.when}</div>
            </div>
          ))}
        </div>

        <div className="section-label">これから</div>
        <div className="badge-grid">
          {BADGES.filter(b => !b.earned).map(b => (
            <div key={b.id} className="badge-tile locked">
              <div className="plate-thumb locked">
                <img src={TITLE_BADGE_SRC[b.plate]} alt="" />
              </div>
              <div className="name">{b.name}</div>
              <div className="meta">{b.meta}</div>
            </div>
          ))}
        </div>

        <div className="section-label">遠い節目<span style={{ color: "var(--text-3)", fontWeight: 400, marginLeft: 8, textTransform: "none", letterSpacing: 0 }}>達成時のみ表示</span></div>
        <div className="rare-list">
          {RARE_TITLES.map(r => (
            <div key={r.id} className="rare-row">
              <div className="rare-thumb">
                <img src={TITLE_BADGE_SRC[r.plate]} alt="" />
              </div>
              <div className="rare-meta">
                <div className="name">？？？</div>
                <div className="hint">{r.hint}</div>
              </div>
              <div className="rare-lock"><Icon name="lock" size={14}/></div>
            </div>
          ))}
        </div>
        <div className="note">強い印象の称号は、達成モーダル内と一覧でのみ表示します。通知やHomeには出ません。</div>
        <div style={{ height: 8 }}/>
      </div>
    </>
  );
}

function RestartScreen({ onBack }) {
  return (
    <>
      <div className="app-back">
        <button className="icon-btn" onClick={onBack}><Icon name="back"/></button>
        <div className="title">再開</div>
      </div>
      <div className="scroll">
        <div className="restart-hero">
          <h2>ここまで続けたことは、<br/>残っています。</h2>
          <p>記録は消えません。次の24時間、できそうな一手から始めましょう。</p>
          <div className="kept-list">
            <div className="kept-row"><span className="l">最長記録</span><span className="v">18 日</span></div>
            <div className="kept-row"><span className="l">累計成功時間</span><span className="v">120 時間</span></div>
            <div className="kept-row"><span className="l">再開回数</span><span className="v">3 回</span></div>
            <div className="kept-row"><span className="l">保持中の記録バッジ</span><span className="v">5 個</span></div>
          </div>
        </div>

        <div className="section-label">次の24時間の一手</div>
        <div style={{ display: "flex", flexDirection: "column", gap: 8 }}>
          {[
            { t: "夜の振り返りだけ書く", s: "30秒。今日の状態に丸をつけるだけ。" },
            { t: "朝の誓約を1つ選ぶ", s: "明日の自分に小さな約束を。" },
            { t: "理由を1行だけ書き直す", s: "最近の自分に合った言葉に。" },
          ].map((x, i) => (
            <div key={i} className="sos-action">
              <div className="ic"><Icon name={["book","sprout","leaf"][i]} size={18}/></div>
              <div className="body"><div className="t">{x.t}</div><div className="s">{x.s}</div></div>
            </div>
          ))}
        </div>

        <div className="note" style={{ marginTop: 18 }}>
          ここまでの記録バッジは保持されます。
        </div>
      </div>
    </>
  );
}

function SettingsScreen({ onAvatarChange, reducedMotion, setReducedMotion, onRestart, onExport, onPrivacy, onGoal, onBlockers }) {
  return (
    <>
      <div className="app-header">
        <div className="brand">設定</div>
      </div>
      <div className="scroll">
        <div className="section-label">ローカルプロフィール</div>
        <div className="row profile" onClick={onAvatarChange} style={{ borderBottom: "1px solid var(--line)", borderRadius: "var(--radius) var(--radius) 0 0"}}>
          <div className="av"><AvatarSlot id="avatar_jacket" hint="" /></div>
          <div className="meta">
            <div className="name">ジャケット</div>
            <div className="goal">目標: 集中を取り戻す</div>
          </div>
          <Icon name="chev" />
        </div>
        <div className="row" style={{ borderBottom: "1px solid var(--line)" }} onClick={onAvatarChange}>
          <div className="label">アバターを変更</div>
          <Icon name="chev" />
        </div>
        <div className="row" style={{ borderRadius: "0 0 var(--radius) var(--radius)" }} onClick={onGoal}>
          <div className="label">目標と理由を編集</div>
          <Icon name="chev" />
        </div>
        <div className="note">獲得済みバッジ 5個</div>

        <div className="section-label">表示と演出</div>
        <div className="row" style={{ borderRadius: "var(--radius)", borderBottom: "1px solid var(--line)" }} onClick={() => setReducedMotion(!reducedMotion)}>
          <div className="label">演出を控えめにする<div style={{fontSize:11,color:"var(--text-3)",marginTop:4,fontWeight:400}}>ジャンプや紙吹雪を無効化</div></div>
          <div className={"toggle" + (reducedMotion ? " on" : "")} />
        </div>

        <div className="section-label">プライバシー</div>
        <div className="row" style={{ borderRadius: "var(--radius) var(--radius) 0 0", borderBottom: "1px solid var(--line)" }} onClick={onPrivacy}>
          <Icon name="lock" /><div className="label">プライバシーロック<div style={{fontSize:11,color:"var(--text-3)",marginTop:4,fontWeight:400}}>起動時のPIN/生体認証</div></div>
          <Icon name="chev" />
        </div>
        <div className="row" style={{ borderRadius: "0 0 var(--radius) var(--radius)" }} onClick={onBlockers}>
          <Icon name="shield" /><div className="label">きっかけブロッカー<div style={{fontSize:11,color:"var(--text-3)",marginTop:4,fontWeight:400}}>準備中</div></div>
          <Icon name="chev" />
        </div>

        <div className="section-label">データ管理</div>
        <div className="row" style={{ borderRadius: "var(--radius)" }} onClick={onExport}>
          <Icon name="download" /><div className="label">データをエクスポート / 削除</div><Icon name="chev"/>
        </div>
        <div className="note">エクスポートにはアバター選択と記録バッジ状態が含まれます。</div>

        <div style={{ height: 8 }}/>
      </div>
    </>
  );
}

function PledgeScreen({ onBack, onSubmit }) {
  const [picked, setPicked] = useState([0]);
  const opts = [
    "夜を落ち着いて終える",
    "通知を切って深呼吸する",
    "誰かに一行だけメッセージを送る",
    "10分だけ歩く",
  ];
  return (
    <>
      <div className="app-back">
        <button className="icon-btn" onClick={onBack}><Icon name="back"/></button>
        <div className="title">朝の誓約</div>
      </div>
      <div className="scroll">
        <p style={{ fontSize: 14, color: "var(--text-2)", margin: "4px 4px 0", lineHeight: 1.7 }}>
          今日、自分に小さく約束することを1つ以上選んでください。
        </p>
        <div className="pledge-list">
          {opts.map((o, i) => (
            <div key={i} className={"pledge-opt" + (picked.includes(i) ? " checked" : "")} onClick={() => setPicked(picked.includes(i) ? picked.filter(x => x !== i) : [...picked, i])}>
              <div className="checkbox">{picked.includes(i) && <Icon name="check" size={14}/>}</div>
              <div>{o}</div>
            </div>
          ))}
        </div>
        <div style={{ marginTop: 20 }}>
          <button className="btn btn-primary" onClick={onSubmit}>今日の誓約にする</button>
        </div>
      </div>
    </>
  );
}

function ReviewScreen({ onBack, onSubmit, onRelapse }) {
  const [mood, setMood] = useState(2);
  return (
    <>
      <div className="app-back">
        <button className="icon-btn" onClick={onBack}><Icon name="back"/></button>
        <div className="title">夜の振り返り</div>
      </div>
      <div className="scroll">
        <p style={{ fontSize: 14, color: "var(--text-2)", margin: "4px 4px 0", lineHeight: 1.7 }}>
          今日の自分はどうでしたか。1タップで大丈夫です。
        </p>
        <div className="mood-row">
          {["😣","😕","😐","🙂","😌"].map((m, i) => (
            <div key={i} className={"mood" + (mood === i ? " selected" : "")} onClick={() => setMood(i)}>{m}</div>
          ))}
        </div>
        <div className="section-label">できたこと(任意)</div>
        <div className="card">
          <div style={{ fontSize: 13, color: "var(--text-2)", lineHeight: 1.7, minHeight: 80 }}>
            一行だけ。書かなくても大丈夫です。
          </div>
        </div>
        <div style={{ marginTop: 20 }}>
          <button className="btn btn-primary" onClick={onSubmit}>記録する</button>
        </div>
        <div style={{ marginTop: 14, textAlign: "center" }}>
          <button className="link-btn" onClick={onRelapse}>リラプスを記録する</button>
        </div>
        <div className="note" style={{ textAlign: "center" }}>責めずに、事実だけ残せます。</div>
      </div>
    </>
  );
}

function SOSScreen({ onClose, onRelapse, onReview }) {
  const [stage, setStage] = useState("active"); // active | done
  if (stage === "done") {
    return (
      <>
        <div className="app-back">
          <button className="icon-btn" onClick={onClose}><Icon name="x"/></button>
        </div>
        <div className="scroll" style={{ display: "flex", flexDirection: "column", justifyContent: "center", textAlign: "center", padding: "0 22px" }}>
          <div style={{ width: 60, height: 60, borderRadius: 16, background: "var(--accent-tint)", color: "var(--accent)", display: "flex", alignItems: "center", justifyContent: "center", margin: "0 auto 18px" }}>
            <Icon name="check" size={28}/>
          </div>
          <h2 style={{ fontSize: 20, fontWeight: 500, margin: "0 0 8px" }}>立ち止まれた</h2>
          <p style={{ fontSize: 14, color: "var(--text-2)", lineHeight: 1.7, margin: "0 0 20px" }}>
            波が来たことに気づけたのは、立派な記録です。
          </p>
          <button className="btn btn-primary" onClick={onClose}>ホームに戻る</button>
        </div>
      </>
    );
  }
  return (
    <div className="sos-screen">
      <div className="app-back">
        <button className="icon-btn" onClick={onClose}><Icon name="x"/></button>
        <div className="title" style={{ color: "var(--text-2)" }}>SOS</div>
      </div>
      <div className="scroll">
        <div className="sos-reason">
          <div className="label">今日の理由</div>
          <div className="text">夜を落ち着いて<br/>終える</div>
        </div>

        <div className="sos-step-label">10秒</div>
        <div className="sos-actions">
          <button className="sos-action" onClick={() => setStage("done")}>
            <div className="ic"><Icon name="shield" size={18}/></div>
            <div className="body"><div className="t">まず止まる</div><div className="s">手を止めて、目を閉じる</div></div>
          </button>
          <button className="sos-action" onClick={() => setStage("done")}>
            <div className="ic"><Icon name="wind" size={18}/></div>
            <div className="body"><div className="t">深呼吸</div><div className="s">3回、ゆっくり</div></div>
          </button>
        </div>

        <div className="sos-step-label">60秒</div>
        <div className="sos-actions">
          <button className="sos-action" onClick={() => setStage("done")}>
            <div className="ic"><Icon name="phone" size={18}/></div>
            <div className="body"><div className="t">スマホを置く</div><div className="s">机の上、見えない位置に</div></div>
          </button>
          <button className="sos-action" onClick={() => setStage("done")}>
            <div className="ic"><Icon name="moon" size={18}/></div>
            <div className="body"><div className="t">場所を変える</div><div className="s">別の部屋に移動する</div></div>
          </button>
          <button className="sos-action" onClick={() => setStage("done")}>
            <div className="ic"><Icon name="leaf" size={18}/></div>
            <div className="body"><div className="t">水を飲む</div><div className="s">一杯、ゆっくり</div></div>
          </button>
        </div>

        <div className="sos-step-label">3分</div>
        <div className="sos-actions">
          <button className="sos-action" onClick={() => setStage("done")}>
            <div className="ic"><Icon name="sprout" size={18}/></div>
            <div className="body"><div className="t">外に出る</div><div className="s">玄関、ベランダ、近所</div></div>
          </button>
          <button className="sos-action" onClick={() => setStage("done")}>
            <div className="ic"><Icon name="book" size={18}/></div>
            <div className="body"><div className="t">短くメモする</div><div className="s">今の状態を一行だけ</div></div>
          </button>
          <button className="sos-action" onClick={() => setStage("done")}>
            <div className="ic"><Icon name="leaf" size={18}/></div>
            <div className="body"><div className="t">理由を読み返す</div><div className="s">今日の理由カードへ</div></div>
          </button>
        </div>

        <div className="sos-secondary">
          <button className="link-btn" onClick={onReview}>あとで振り返る</button>
          <span className="dotsep"/>
          <button className="link-btn" onClick={onRelapse}>リラプスを記録する</button>
        </div>
        <div style={{ height: 12 }}/>
      </div>
    </div>
  );
}

// ── New screens ─────────────────────────────────────────────────────────────

function RelapseLogScreen({ onBack, onSubmit }) {
  const [trigger, setTrigger] = useState(null);
  const triggers = ["疲れ", "孤独", "退屈", "不安", "睡眠不足", "夜の時間", "SNS", "その他"];
  return (
    <>
      <div className="app-back">
        <button className="icon-btn" onClick={onBack}><Icon name="back"/></button>
        <div className="title">リラプスを記録</div>
      </div>
      <div className="scroll">
        <p style={{ fontSize: 14, color: "var(--text-2)", margin: "4px 4px 14px", lineHeight: 1.7 }}>
          責めずに、事実だけ残せます。記録は次の自分のヒントになります。
        </p>

        <div className="section-label">きっかけ(任意・複数可)</div>
        <div className="chip-grid">
          {triggers.map((t, i) => (
            <button key={i}
              className={"chip" + (trigger === i ? " on" : "")}
              onClick={() => setTrigger(trigger === i ? null : i)}>{t}</button>
          ))}
        </div>

        <div className="section-label">いまの自分に</div>
        <div className="card">
          <div style={{ fontSize: 13, color: "var(--text-2)", lineHeight: 1.7, minHeight: 60 }}>
            一行だけ。「次にできそうな小さな一手」を書いてみる。
          </div>
        </div>

        <div className="info-row">
          <Icon name="shield" size={16}/>
          <span>ここまでの記録バッジは保持されます。</span>
        </div>

        <div style={{ marginTop: 18, display: "flex", flexDirection: "column", gap: 10 }}>
          <button className="btn btn-primary" onClick={onSubmit}>記録して再開</button>
          <button className="btn btn-ghost" onClick={onBack}>あとで書く</button>
        </div>
      </div>
    </>
  );
}

function ExportScreen({ onBack }) {
  const [confirming, setConfirming] = useState(false);
  return (
    <>
      <div className="app-back">
        <button className="icon-btn" onClick={onBack}><Icon name="back"/></button>
        <div className="title">データ管理</div>
      </div>
      <div className="scroll">
        <div className="section-label">エクスポート</div>
        <div className="card">
          <div style={{ fontSize: 14, fontWeight: 500, marginBottom: 6 }}>すべてのデータをJSONで書き出す</div>
          <div style={{ fontSize: 12, color: "var(--text-2)", lineHeight: 1.7, marginBottom: 14 }}>
            記録、誓約、振り返り、リラプスログ、アバター選択(<code>avatarId</code>)と記録バッジ状態が含まれます。すべてローカル保存です。
          </div>
          <div className="kv-list">
            <div className="kv"><span>含まれるもの</span><b>記録 / 誓約 / 振り返り</b></div>
            <div className="kv"><span></span><b>リラプスログ</b></div>
            <div className="kv"><span></span><b>アバターID・記録バッジ</b></div>
            <div className="kv"><span>含まれないもの</span><b>外部送信されるデータはなし</b></div>
          </div>
          <button className="btn btn-primary" style={{ marginTop: 14 }}>JSONをエクスポート</button>
        </div>

        <div className="section-label">削除</div>
        <div className="card">
          <div style={{ fontSize: 14, fontWeight: 500, marginBottom: 6 }}>すべてのデータを削除</div>
          <div style={{ fontSize: 12, color: "var(--text-2)", lineHeight: 1.7, marginBottom: 14 }}>
            この端末のEmotionLeaveデータをすべて消去します。元には戻せません。
          </div>
          {!confirming ? (
            <button className="btn" style={{ color: "oklch(0.62 0.14 28)" }} onClick={() => setConfirming(true)}>削除を続ける</button>
          ) : (
            <div className="danger-confirm">
              <p>本当に削除しますか?</p>
              <div className="btn-row">
                <button className="btn btn-ghost" onClick={() => setConfirming(false)}>戻る</button>
                <button className="btn" style={{ background: "oklch(0.62 0.14 28)", color: "#fff", borderColor: "transparent" }}>削除する</button>
              </div>
            </div>
          )}
        </div>
        <div style={{ height: 12 }}/>
      </div>
    </>
  );
}

function PrivacyLockScreen({ onBack }) {
  const [pin, setPin] = useState(true);
  const [bio, setBio] = useState(false);
  const [hideContent, setHideContent] = useState(true);
  return (
    <>
      <div className="app-back">
        <button className="icon-btn" onClick={onBack}><Icon name="back"/></button>
        <div className="title">プライバシーロック</div>
      </div>
      <div className="scroll">
        <p style={{ fontSize: 14, color: "var(--text-2)", margin: "4px 4px 14px", lineHeight: 1.7 }}>
          人に見られても恥ずかしくない設計です。さらに強くしたい時はここで設定できます。
        </p>

        <div className="section-label">起動時の認証</div>
        <div className="row" style={{ borderRadius: "var(--radius) var(--radius) 0 0", borderBottom: "1px solid var(--line)" }} onClick={() => setPin(!pin)}>
          <div className="label">PINコード<div style={{fontSize:11,color:"var(--text-3)",marginTop:4,fontWeight:400}}>4桁の数字</div></div>
          <div className={"toggle" + (pin ? " on" : "")}/>
        </div>
        <div className="row" style={{ borderRadius: "0 0 var(--radius) var(--radius)" }} onClick={() => setBio(!bio)}>
          <div className="label">生体認証<div style={{fontSize:11,color:"var(--text-3)",marginTop:4,fontWeight:400}}>指紋 / 顔</div></div>
          <div className={"toggle" + (bio ? " on" : "")}/>
        </div>

        <div className="section-label">表示の隠し方</div>
        <div className="row" style={{ borderRadius: "var(--radius)", borderBottom: "1px solid var(--line)" }} onClick={() => setHideContent(!hideContent)}>
          <div className="label">通知に内容を出さない<div style={{fontSize:11,color:"var(--text-3)",marginTop:4,fontWeight:400}}>アバター名・バッジ名を隠す</div></div>
          <div className={"toggle" + (hideContent ? " on" : "")}/>
        </div>

        <div className="info-row" style={{ marginTop: 16 }}>
          <Icon name="lock" size={16}/>
          <span>通知・ロック画面・共有画像にアバター名や称号は出ません。</span>
        </div>
      </div>
    </>
  );
}

function FutureBlockerScreen({ onBack }) {
  return (
    <>
      <div className="app-back">
        <button className="icon-btn" onClick={onBack}><Icon name="back"/></button>
        <div className="title">きっかけブロッカー</div>
      </div>
      <div className="scroll" style={{ display: "flex", flexDirection: "column", justifyContent: "center", padding: "0 22px", textAlign: "center" }}>
        <div style={{ width: 68, height: 68, borderRadius: 18, background: "var(--surface-2)", border: "1px solid var(--line)", color: "var(--text-3)", display: "flex", alignItems: "center", justifyContent: "center", margin: "20px auto 18px" }}>
          <Icon name="shield" size={32}/>
        </div>
        <h2 style={{ fontSize: 18, fontWeight: 500, margin: "0 0 8px" }}>準備中の機能です</h2>
        <p style={{ fontSize: 13, color: "var(--text-2)", lineHeight: 1.8, margin: "0 0 22px" }}>
          時間帯ロック、特定アプリの一時停止、夜間のショートカット制限などを検討しています。<br/>必要だと思うものを教えてください。
        </p>
        <div className="planned-list">
          <div className="planned"><Icon name="moon" size={16}/><span>夜間モード(時間帯で制限)</span></div>
          <div className="planned"><Icon name="phone" size={16}/><span>特定アプリの一時停止</span></div>
          <div className="planned"><Icon name="alert" size={16}/><span>衝動が来やすい時間に小さな声かけ</span></div>
        </div>
        <button className="btn btn-ghost" style={{ marginTop: 22 }}>欲しい機能を投票する</button>
      </div>
    </>
  );
}

function GoalSetupScreen({ onBack, onSubmit }) {
  const [goal, setGoal] = useState(0);
  const [reason, setReason] = useState(0);
  const goals = [
    "集中を取り戻す",
    "睡眠を整える",
    "自分との約束を守る",
    "自分で選びたい",
  ];
  const reasons = [
    "夜を落ち着いて終える",
    "朝、頭がはっきり起きたい",
    "大切な人との時間を増やす",
    "自分で書く",
  ];
  return (
    <>
      <div className="app-back">
        <button className="icon-btn" onClick={onBack}><Icon name="back"/></button>
        <div className="title">目標と理由</div>
      </div>
      <div className="scroll">
        <p style={{ fontSize: 14, color: "var(--text-2)", margin: "4px 4px 14px", lineHeight: 1.7 }}>
          今日のあなたに合うものを選んでください。あとから何度でも変えられます。
        </p>

        <div className="section-label">目標</div>
        <div className="pledge-list">
          {goals.map((g, i) => (
            <div key={i} className={"pledge-opt" + (goal === i ? " checked" : "")} onClick={() => setGoal(i)}>
              <div className="checkbox">{goal === i && <Icon name="check" size={14}/>}</div>
              <div>{g}</div>
            </div>
          ))}
        </div>

        <div className="section-label">今日の理由</div>
        <div className="pledge-list">
          {reasons.map((r, i) => (
            <div key={i} className={"pledge-opt" + (reason === i ? " checked" : "")} onClick={() => setReason(i)}>
              <div className="checkbox">{reason === i && <Icon name="check" size={14}/>}</div>
              <div>{r}</div>
            </div>
          ))}
        </div>

        <div style={{ marginTop: 20 }}>
          <button className="btn btn-primary" onClick={onSubmit}>これで始める</button>
        </div>
        <div className="note" style={{ textAlign: "center" }}>あとからいつでも変更できます。</div>
      </div>
    </>
  );
}

// ── Main app — single-frame stateful prototype ──────────────────────────────
function App({ initial = "home", showModal = false, modalState = "settled", reducedMotionInit = false, theme = "dark" }) {
  const [screen, setScreen] = useState(initial);
  const [tab, setTab] = useState("home");
  const [modal, setModal] = useState(showModal ? "levelup" : null);
  const [reducedMotion, setReducedMotion] = useState(reducedMotionInit);
  const [selectedAvatar, setSelectedAvatar] = useState("avatar_jacket");

  useEffect(() => {
    document.documentElement.setAttribute("data-theme", theme);
  }, [theme]);

  const goTab = (t) => {
    setTab(t);
    if (t === "home") setScreen("home");
    else if (t === "achievement") setScreen("achievement");
    else if (t === "settings") setScreen("settings");
  };

  let body;
  switch (screen) {
    case "onboarding":
      body = <OnboardingScreen onNext={() => setScreen("goal")} />; break;
    case "goal":
      body = <GoalSetupScreen onBack={() => setScreen("onboarding")} onSubmit={() => setScreen("avatar")} />; break;
    case "avatar":
      body = <AvatarSelectionScreen
        selected={selectedAvatar} setSelected={setSelectedAvatar}
        onConfirm={() => { setModal("levelup"); setScreen("home"); setTab("home"); }}
        onSkip={() => { setScreen("home"); setTab("home"); }} />;
      break;
    case "achievement":
      body = <AchievementScreen />; break;
    case "settings":
      body = <SettingsScreen
        onAvatarChange={() => setScreen("avatar")}
        reducedMotion={reducedMotion}
        setReducedMotion={setReducedMotion}
        onExport={() => setScreen("export")}
        onPrivacy={() => setScreen("privacy")}
        onGoal={() => setScreen("goal")}
        onBlockers={() => setScreen("blockers")} />;
      break;
    case "restart":
      body = <RestartScreen onBack={() => { setScreen("home"); setTab("home"); }} />; break;
    case "pledge":
      body = <PledgeScreen onBack={() => setScreen("home")} onSubmit={() => setScreen("home")} />; break;
    case "review":
      body = <ReviewScreen
        onBack={() => setScreen("home")}
        onSubmit={() => { setScreen("home"); setModal("levelup"); }}
        onRelapse={() => setScreen("relapse")} />; break;
    case "sos":
      body = <SOSScreen
        onClose={() => setScreen("home")}
        onRelapse={() => setScreen("relapse")}
        onReview={() => setScreen("review")} />; break;
    case "relapse":
      body = <RelapseLogScreen onBack={() => setScreen("home")} onSubmit={() => setScreen("restart")} />; break;
    case "export":
      body = <ExportScreen onBack={() => setScreen("settings")} />; break;
    case "privacy":
      body = <PrivacyLockScreen onBack={() => setScreen("settings")} />; break;
    case "blockers":
      body = <FutureBlockerScreen onBack={() => setScreen("settings")} />; break;
    case "home":
    default:
      body = <HomeScreen
        onSOS={() => setScreen("sos")}
        onPledge={() => setScreen("pledge")}
        onReview={() => setScreen("review")}
        onAvatar={() => setScreen("avatar")}
        onBadgeOpen={() => setModal("levelup")}
        selectedAvatar={selectedAvatar}
      />;
  }

  // タブバーは原則常時表示。モーダル状態(SOSなど没入画面)だけ非表示。
  const hideTabBar = screen === "sos";

  return (
    <div className="app">
      <StatusBar/>
      {body}
      {!hideTabBar && <TabBar active={tab} onChange={goTab} />}
      <NavBar/>
      {modal === "levelup" && <LevelUpModal onClose={() => setModal(null)} reducedMotion={reducedMotion} selectedAvatar={selectedAvatar} state={modalState} />}
    </div>
  );
}

window.App = App;
window.OnboardingScreen = OnboardingScreen;
