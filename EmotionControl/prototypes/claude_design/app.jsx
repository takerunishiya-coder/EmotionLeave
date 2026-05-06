/* global React, ReactDOM */
const { useState, useEffect, useMemo } = React;

const ASSET_ROOT = "../../assets";
const avatarSrc = (id) => `${ASSET_ROOT}/avatars/${id}.png`;
const levelFrameSrc = (id, frame) => `${ASSET_ROOT}/avatars/level_up/${id}/${frame}`;
const titleBadgeSrc = (file) => `${ASSET_ROOT}/title_badges/${file}`;

// ── Avatar assets ───────────────────────────────────────────────────────────
function AvatarSlot({ id, hint, className = "" }) {
  return (
    <img
      className={`avatar-img ${className}`}
      src={avatarSrc(id)}
      alt={hint || "アバター画像"}
      loading="lazy"
      draggable="false"
    />
  );
}

function TitleBadgeImage({ file, name, className = "" }) {
  return <img className={`title-badge-img ${className}`} src={titleBadgeSrc(file)} alt={name} loading="lazy" draggable="false" />;
}

const LEVEL_FRAMES = {
  avatar_jacket: ["frame_00_idle.png", "frame_01_fist_ready.png", "frame_02_raise.png", "frame_03_peak.png", "frame_04_settle.png"],
  avatar_centerpart: ["frame_00_idle.png", "frame_01_fist_ready.png", "frame_02_raise.png", "frame_03_peak.png", "frame_04_settle.png"],
  avatar_suit: ["frame_00_idle.png", "frame_01_fist_ready.png", "frame_02_raise.png", "frame_03_peak.png", "frame_04_settle.png"],
  avatar_kinniku: ["frame_00_idle.png", "frame_01_fist_ready.png", "frame_02_raise.png", "frame_03_peak.png", "frame_04_settle.png"],
};

function LevelUpAvatar({ id, reducedMotion, state = "animated" }) {
  const frames = LEVEL_FRAMES[id] || LEVEL_FRAMES.avatar_jacket;
  const stateIndex = state === "before" ? 0 : state === "peak" ? 3 : state === "settled" ? 4 : 0;
  const [frame, setFrame] = useState(reducedMotion ? 0 : stateIndex);

  useEffect(() => {
    if (reducedMotion || state !== "animated") {
      setFrame(stateIndex);
      return;
    }
    let i = 0;
    const timer = window.setInterval(() => {
      i = Math.min(i + 1, frames.length - 1);
      setFrame(i);
      if (i === frames.length - 1) window.clearInterval(timer);
    }, 130);
    return () => window.clearInterval(timer);
  }, [id, reducedMotion, state, stateIndex, frames.length]);

  return (
    <img
      className="levelup-avatar-frame"
      src={levelFrameSrc(id, frames[frame])}
      alt="ガッツポーズするアバター"
      draggable="false"
    />
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

// ── Badges ──────────────────────────────────────────────────────────────────
const BADGES = [
  { id: "first", name: "はじめの一歩", file: "title_hajimenoippo.png", earned: true, when: "5/1" },
  { id: "flow", name: "流れを変える者", file: "title_nagarewokaeru.png", earned: true, when: "5/2" },
  { id: "will", name: "強固な意思", file: "title_kyoukonaishi.png", earned: true, when: "5/3" },
  { id: "d7", name: "強者", file: "title_kyousya.png", earned: true, when: "5/7" },
  { id: "gold", name: "黄金の精神", file: "title_ougonnoseishin.png", earned: false, meta: "あと23日" },
  { id: "parting", name: "弱さとの決別", file: "title_yowasatonoketsubetsu.png", earned: false, meta: "あと9日" },
  { id: "otoko", name: "漢", file: "title_otoko.png", earned: false, meta: "あと25日" },
];

// ── Screens ────────────────────────────────────────────────────────────────

function HomeScreen({ onSOS, onPledge, onReview, onAvatar, onBadgeOpen, selectedAvatar, showLatestBadge = true }) {
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
        {showLatestBadge && (
          <div className="badge-row badge-row-with-image" onClick={onBadgeOpen} role="button">
            <TitleBadgeImage file="title_kyoukonaishi.png" name="強固な意思" />
            <div>
              <div className="name">強固な意思</div>
              <div style={{ fontSize: 11, color: "var(--text-3)", marginTop: 2 }}>最新の記録バッジ</div>
            </div>
            <div className="when">5/7</div>
          </div>
        )}
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
        <p>毎日10〜30秒のチェックインで、気持ちの波と距離を取りやすくします。記録が途切れても、責められずに再開できます。</p>
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

function LevelUpModal({ onClose, reducedMotion, selectedAvatar, motionState = "animated" }) {
  const confettiPieces = useMemo(() => {
    return Array.from({ length: 12 }, (_, i) => ({
      left: Math.random() * 100,
      delay: Math.random() * 0.3,
      color: ["oklch(0.78 0.08 70)", "oklch(0.68 0.06 165)", "oklch(0.7 0.05 200)"][i % 3]
    }));
  }, []);

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className={`modal levelup state-${motionState} ${reducedMotion ? "rm" : ""}`} onClick={e => e.stopPropagation()}>
        <button className="skip" onClick={onClose}>スキップ</button>
        {!reducedMotion && (
          <div className="confetti">
            {confettiPieces.map((p, i) => (
              <i key={i} style={{ left: p.left + "%", background: p.color, animationDelay: p.delay + "s" }} />
            ))}
          </div>
        )}
        <h2>7日間達成!!</h2>
        <TitleBadgeImage file="title_kyousya.png" name="強者" className="levelup-title-badge" />
        <div className="av-large">
          <div className="glow"/>
          <LevelUpAvatar id={selectedAvatar} reducedMotion={reducedMotion} state={motionState} />
        </div>
        <div className="badge-name">
          7日分の記録
        </div>
        <p>新しい記録バッジを手に入れました。<br/>ここまでの記録は残っています。</p>
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
              <TitleBadgeImage file={b.file} name={b.name} />
              <div className="badge-copy">
                <div className="name">{b.name}</div>
                <div className="meta">{b.when}</div>
              </div>
            </div>
          ))}
        </div>

        <div className="section-label">これから</div>
        <div className="badge-grid">
          {BADGES.filter(b => !b.earned).map(b => (
            <div key={b.id} className="badge-tile locked">
              <TitleBadgeImage file={b.file} name={b.name} />
              <div className="badge-copy">
                <div className="name">{b.name}</div>
                <div className="meta">{b.meta}</div>
              </div>
            </div>
          ))}
        </div>
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

function SettingsScreen({ selectedAvatar, onAvatarChange, reducedMotion, setReducedMotion, onRestart }) {
  const avatarLabel = AVATARS.find(a => a.id === selectedAvatar)?.label || "ジャケット";
  return (
    <>
      <div className="app-header">
        <div className="brand">設定</div>
      </div>
      <div className="scroll">
        <div className="section-label">ローカルプロフィール</div>
        <div className="row profile" onClick={onAvatarChange} style={{ borderBottom: "1px solid var(--line)", borderRadius: "var(--radius) var(--radius) 0 0"}}>
          <div className="av"><AvatarSlot id={selectedAvatar} hint="" /></div>
          <div className="meta">
            <div className="name">{avatarLabel}</div>
            <div className="goal">目標: 集中を取り戻す</div>
          </div>
          <Icon name="chev" />
        </div>
        <div className="row" style={{ borderRadius: "0 0 var(--radius) var(--radius)" }} onClick={onAvatarChange}>
          <div className="label">アバターを変更</div>
          <Icon name="chev" />
        </div>
        <div className="note">獲得済みバッジ 5個</div>

        <div className="section-label">表示と演出</div>
        <div className="row" style={{ borderRadius: "var(--radius)", borderBottom: "1px solid var(--line)" }} onClick={() => setReducedMotion(!reducedMotion)}>
          <div className="label">演出を控えめにする<div style={{fontSize:11,color:"var(--text-3)",marginTop:4,fontWeight:400}}>ジャンプや紙吹雪を無効化</div></div>
          <div className={"toggle" + (reducedMotion ? " on" : "")} />
        </div>

        <div className="section-label">データ管理</div>
        <div className="row" style={{ borderRadius: "var(--radius) var(--radius) 0 0" }}>
          <Icon name="download" /><div className="label">データをエクスポート</div><Icon name="chev"/>
        </div>
        <div className="row" style={{ borderRadius: "0 0 var(--radius) var(--radius)" }}>
          <Icon name="trash" /><div className="label" style={{ color: "oklch(0.62 0.14 28)" }}>すべてのデータを削除</div><Icon name="chev"/>
        </div>
        <div className="note">エクスポートにはアバター選択と記録バッジ状態が含まれます。</div>

        <div className="section-label">テスト</div>
        <div className="row" style={{ borderRadius: "var(--radius)", borderBottom: "1px solid var(--line)" }} onClick={onRestart}>
          <Icon name="alert" /><div className="label">再開フローを表示<div style={{fontSize:11,color:"var(--text-3)",marginTop:4,fontWeight:400}}>記録が途切れた場合の画面</div></div>
          <Icon name="chev" />
        </div>
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

function ReviewScreen({ onBack, onSubmit }) {
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
      </div>
    </>
  );
}

function SOSScreen({ onClose }) {
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

        <div className="sos-actions">
          <button className="sos-action" onClick={() => setStage("done")}>
            <div className="ic"><Icon name="wind" size={18}/></div>
            <div className="body"><div className="t">10秒だけ深呼吸</div><div className="s">息を吐いて、肩の力を抜く</div></div>
          </button>
          <button className="sos-action" onClick={() => setStage("done")}>
            <div className="ic"><Icon name="sprout" size={18}/></div>
            <div className="body"><div className="t">60秒だけ立ち上がる</div><div className="s">スマホを置いて、水を飲む</div></div>
          </button>
          <button className="sos-action" onClick={() => setStage("done")}>
            <div className="ic"><Icon name="moon" size={18}/></div>
            <div className="body"><div className="t">3分だけ場所を変える</div><div className="s">外気に当たる・理由を読み返す</div></div>
          </button>
        </div>
      </div>
    </div>
  );
}

// ── Main app — single-frame stateful prototype ──────────────────────────────
function App({ initial = "home", showModal = false, reducedMotionInit = false, theme = "dark", showLatestBadge = true, modalState = "animated" }) {
  const [screen, setScreen] = useState(initial);
  const [tab, setTab] = useState("home");
  const [modal, setModal] = useState(showModal ? "levelup" : null);
  const [reducedMotion, setReducedMotion] = useState(reducedMotionInit);
  const [selectedAvatar, setSelectedAvatar] = useState("avatar_jacket");

  useEffect(() => {
    document.documentElement.setAttribute("data-theme", theme);
  }, [theme]);

  // map tab change -> screen
  const goTab = (t) => {
    setTab(t);
    if (t === "home") setScreen("home");
    else if (t === "achievement") setScreen("achievement");
    else if (t === "settings") setScreen("settings");
  };

  let body;
  switch (screen) {
    case "onboarding":
      body = <OnboardingScreen onNext={() => setScreen("avatar")} />;
      break;
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
        selectedAvatar={selectedAvatar}
        onAvatarChange={() => setScreen("avatar")}
        reducedMotion={reducedMotion}
        setReducedMotion={setReducedMotion}
        onRestart={() => setScreen("restart")} />;
      break;
    case "restart":
      body = <RestartScreen onBack={() => { setScreen("home"); setTab("home"); }} />; break;
    case "pledge":
      body = <PledgeScreen onBack={() => setScreen("home")} onSubmit={() => setScreen("home")} />; break;
    case "review":
      body = <ReviewScreen onBack={() => setScreen("home")} onSubmit={() => { setScreen("home"); setModal("levelup"); }} />; break;
    case "sos":
      body = <SOSScreen onClose={() => setScreen("home")} />; break;
    case "home":
    default:
      body = <HomeScreen
        onSOS={() => setScreen("sos")}
        onPledge={() => setScreen("pledge")}
        onReview={() => setScreen("review")}
        onAvatar={() => setScreen("avatar")}
        onBadgeOpen={() => setModal("levelup")}
        selectedAvatar={selectedAvatar}
        showLatestBadge={showLatestBadge}
      />;
  }

  const showTabBar = ["home", "achievement", "settings"].includes(screen);

  return (
    <div className="app">
      <StatusBar/>
      {body}
      {showTabBar && <TabBar active={tab} onChange={goTab} />}
      <NavBar/>
      {modal === "levelup" && <LevelUpModal onClose={() => setModal(null)} reducedMotion={reducedMotion} selectedAvatar={selectedAvatar} motionState={modalState} />}
    </div>
  );
}

window.App = App;
window.OnboardingScreen = OnboardingScreen;
