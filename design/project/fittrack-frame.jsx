// FitTrack dark Android shell. Status bar (transparent), camera notch, gesture pill.
// Designed to letterbox a FitTrack screen at 360x780.

const FT = {
  bg: '#0F0F11',
  surface: '#1A1A1C',
  raised: '#242427',
  border: '#2E2E32',
  divider: '#1F1F22',
  text: '#FFFFFF',
  text2: '#A3A3A8',
  text3: '#6E6E73',
  orange: '#FF6B35',
  orangeSoft: 'rgba(255,107,53,0.15)',
  blue: '#00B4D8',
  blueSoft: 'rgba(0,180,216,0.15)',
  green: '#4CAF50',
  greenSoft: 'rgba(76,175,80,0.15)',
  purple: '#9C27B0',
  purpleSoft: 'rgba(156,39,176,0.15)',
  font:        '"Tiempos Text", Georgia, "Times New Roman", serif',
  fontDisplay: '"Tiempos Headline", Georgia, "Times New Roman", serif',
  fontFine:    '"Tiempos Fine", "Tiempos Headline", Georgia, serif',
};
window.FT = FT;

// Activity meta — colors per spec.
window.ACT = {
  treadmill: { color: FT.green,  soft: FT.greenSoft,  label: 'Treadmill', icon: 'treadmill' },
  swim:      { color: FT.blue,   soft: FT.blueSoft,   label: 'Swimming',  icon: 'swim' },
  basket:    { color: FT.orange, soft: FT.orangeSoft, label: 'Basketball', icon: 'basket' },
  routine:   { color: FT.purple, soft: FT.purpleSoft, label: 'Routine',   icon: 'routine' },
};

// Tiny stroke icon set used across screens.
function Icon({ name, size = 20, color = 'currentColor', stroke = 1.8 }) {
  const p = (d) => <path d={d} />;
  const common = { width: size, height: size, viewBox: '0 0 24 24', fill: 'none', stroke: color, strokeWidth: stroke, strokeLinecap: 'round', strokeLinejoin: 'round' };
  switch (name) {
    case 'home':     return <svg {...common}>{p('M3 11l9-7 9 7v9a2 2 0 0 1-2 2h-4v-7h-6v7H5a2 2 0 0 1-2-2z')}</svg>;
    case 'calendar': return <svg {...common}>{p('M3 6a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z')}{p('M3 10h18M8 2v4M16 2v4')}</svg>;
    case 'clip':     return <svg {...common}>{p('M9 4h6a1 1 0 0 1 1 1v1h2a2 2 0 0 1 2 2v12a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h2V5a1 1 0 0 1 1-1z')}{p('M9 13h6M9 17h4')}</svg>;
    case 'chart':    return <svg {...common}>{p('M3 21h18M6 17V9M11 17V5M16 17v-6M21 17v-3')}</svg>;
    case 'plus':     return <svg {...common}>{p('M12 5v14M5 12h14')}</svg>;
    case 'play':     return <svg {...common} fill={color} stroke="none"><path d="M8 5l12 7-12 7z"/></svg>;
    case 'arrow':    return <svg {...common}>{p('M5 12h14M13 5l7 7-7 7')}</svg>;
    case 'arrowL':   return <svg {...common}>{p('M19 12H5M11 5l-7 7 7 7')}</svg>;
    case 'check':    return <svg {...common}>{p('M5 13l4 4 10-10')}</svg>;
    case 'flame':    return <svg {...common} fill={color} stroke="none"><path d="M12 2s4 4 4 8a4 4 0 0 1-8 0c0-1 .3-2 .8-2.7-.4 1-1.8 2.5-1.8 4.7a5 5 0 0 0 10 0c0-5-5-10-5-10z"/></svg>;
    case 'trophy':   return <svg {...common}>{p('M8 4h8v4a4 4 0 0 1-8 0z')}{p('M5 4h3v3a3 3 0 0 1-3-3zM19 4h-3v3a3 3 0 0 0 3-3zM10 12v3M14 12v3M8 18h8M9 15h6v3H9z')}</svg>;
    case 'gear':     return <svg {...common}>{p('M12 8a4 4 0 1 0 0 8 4 4 0 0 0 0-8z')}{p('M19 12a7 7 0 0 0-.1-1.2l2-1.5-2-3.5-2.4.8a7 7 0 0 0-2.1-1.2L14 3h-4l-.4 2.4a7 7 0 0 0-2.1 1.2L5.1 5.8l-2 3.5 2 1.5A7 7 0 0 0 5 12c0 .4 0 .8.1 1.2l-2 1.5 2 3.5 2.4-.8c.6.5 1.3.9 2.1 1.2L10 21h4l.4-2.4a7 7 0 0 0 2.1-1.2l2.4.8 2-3.5-2-1.5c.1-.4.1-.8.1-1.2z')}</svg>;
    case 'bell':     return <svg {...common}>{p('M6 8a6 6 0 0 1 12 0c0 7 3 8 3 8H3s3-1 3-8M10 21a2 2 0 0 0 4 0')}</svg>;
    case 'download': return <svg {...common}>{p('M12 4v12M6 12l6 6 6-6M4 20h16')}</svg>;
    case 'close':    return <svg {...common}>{p('M6 6l12 12M6 18L18 6')}</svg>;
    case 'timer':    return <svg {...common}>{p('M12 7v6l4 2')}<circle cx="12" cy="13" r="8"/>{p('M9 2h6M12 5v2')}</svg>;
    case 'drag':     return <svg {...common} stroke={color} strokeWidth="2">{p('M9 6h.01M9 12h.01M9 18h.01M15 6h.01M15 12h.01M15 18h.01')}</svg>;
    case 'search':   return <svg {...common}><circle cx="11" cy="11" r="7"/>{p('M21 21l-4.3-4.3')}</svg>;
    case 'edit':     return <svg {...common}>{p('M12 20h9M16.5 3.5a2.1 2.1 0 1 1 3 3L7 19l-4 1 1-4z')}</svg>;
    case 'star':     return <svg {...common} fill={color} stroke="none"><path d="M12 2l3 6.9 7.5.7-5.6 5 1.7 7.4L12 18l-6.6 4 1.7-7.4L1.5 9.6 9 8.9z"/></svg>;
    case 'twitter':  return <svg {...common} fill={color} stroke="none"><path d="M17.53 3H20.5l-6.49 7.42L21.65 21h-5.96l-4.67-6.11L5.7 21H2.73l6.95-7.94L1.97 3h6.11l4.22 5.58L17.53 3zm-1.04 16.17h1.66L7.6 4.73H5.83l10.66 14.44z"/></svg>;
    case 'github':   return <svg {...common} fill={color} stroke="none"><path d="M12 2A10 10 0 0 0 2 12c0 4.42 2.87 8.17 6.84 9.5.5.08.66-.22.66-.48v-1.7c-2.78.6-3.37-1.34-3.37-1.34-.46-1.16-1.11-1.47-1.11-1.47-.9-.62.07-.6.07-.6 1 .07 1.53 1.03 1.53 1.03.9 1.52 2.34 1.08 2.91.83.09-.65.35-1.09.63-1.34-2.22-.25-4.55-1.11-4.55-4.94 0-1.1.39-2 1.03-2.7-.1-.25-.45-1.27.1-2.65 0 0 .84-.27 2.75 1.02a9.6 9.6 0 0 1 5 0c1.91-1.3 2.75-1.02 2.75-1.02.55 1.38.2 2.4.1 2.65.64.7 1.03 1.6 1.03 2.7 0 3.84-2.34 4.68-4.57 4.93.36.31.68.92.68 1.85V21c0 .27.16.57.67.48A10 10 0 0 0 22 12 10 10 0 0 0 12 2z"/></svg>;
    case 'discord':  return <svg {...common} fill={color} stroke="none"><path d="M20.32 4.45a18.4 18.4 0 0 0-4.55-1.4.07.07 0 0 0-.07.03c-.2.35-.41.81-.55 1.16a16.9 16.9 0 0 0-5.16 0c-.15-.36-.37-.81-.56-1.16a.07.07 0 0 0-.07-.03c-1.63.28-3.18.77-4.55 1.4a.06.06 0 0 0-.03.03C1.9 8.65 1.13 12.74 1.5 16.78a.08.08 0 0 0 .03.05 18.5 18.5 0 0 0 5.6 2.85.07.07 0 0 0 .08-.03c.43-.59.81-1.21 1.14-1.86a.07.07 0 0 0-.04-.1c-.6-.23-1.18-.51-1.74-.83a.07.07 0 0 1 0-.12l.35-.27a.07.07 0 0 1 .07 0 13.2 13.2 0 0 0 11.21 0 .07.07 0 0 1 .07 0l.35.27a.07.07 0 0 1 0 .12c-.56.32-1.14.6-1.74.83a.07.07 0 0 0-.04.1c.33.65.71 1.27 1.14 1.86a.07.07 0 0 0 .08.03 18.46 18.46 0 0 0 5.6-2.85.07.07 0 0 0 .03-.05c.44-4.7-.74-8.75-3.13-12.3a.05.05 0 0 0-.03-.03zM8.52 14.32c-1.1 0-2-1.02-2-2.26 0-1.25.88-2.26 2-2.26 1.13 0 2.03 1.02 2 2.26 0 1.24-.88 2.26-2 2.26zm7.42 0c-1.1 0-2-1.02-2-2.26 0-1.25.88-2.26 2-2.26 1.13 0 2.03 1.02 2 2.26 0 1.24-.87 2.26-2 2.26z"/></svg>;
    case 'telegram': return <svg {...common} fill={color} stroke="none"><path d="M21.94 3.13a.9.9 0 0 0-.95-.16L2.37 10.16a.9.9 0 0 0 .08 1.7l4.55 1.39 2.05 6.43a.9.9 0 0 0 1.42.39l2.6-2.13 4.3 3.18a.9.9 0 0 0 1.42-.55l3-15.5a.9.9 0 0 0-.85-.94zM9.4 14.23l-.5 4.62-1.8-5.66 9.62-7.31-7.32 8.35z"/></svg>;
    case 'youtube':  return <svg {...common} fill={color} stroke="none"><path d="M21.58 7.2a2.5 2.5 0 0 0-1.76-1.77C18.25 5 12 5 12 5s-6.25 0-7.82.43A2.5 2.5 0 0 0 2.42 7.2C2 8.78 2 12 2 12s0 3.22.42 4.8a2.5 2.5 0 0 0 1.76 1.77C5.75 19 12 19 12 19s6.25 0 7.82-.43a2.5 2.5 0 0 0 1.76-1.77C22 15.22 22 12 22 12s0-3.22-.42-4.8zM10 15V9l5.2 3L10 15z"/></svg>;
    case 'gmail':    return <svg {...common} fill={color} stroke="none"><path d="M22 7.3v9.4a2 2 0 0 1-2 2h-2.5v-9L12 13.7 6.5 9.7v9H4a2 2 0 0 1-2-2V7.3a2 2 0 0 1 .9-1.7L4 5l8 5.8L20 5l1.1.6A2 2 0 0 1 22 7.3z"/></svg>;
    case 'camera':   return <svg {...common}>{p('M3 8a2 2 0 0 1 2-2h2.5l1.5-2h6l1.5 2H19a2 2 0 0 1 2 2v10a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z')}<circle cx="12" cy="13" r="4"/></svg>;
    case 'user':     return <svg {...common}><circle cx="12" cy="8" r="4"/>{p('M4 21a8 8 0 0 1 16 0')}</svg>;
    case 'image':    return <svg {...common}><rect x="3" y="3" width="18" height="18" rx="2"/><circle cx="9" cy="9" r="2"/>{p('M21 15l-5-5L5 21')}</svg>;
    case 'logout':   return <svg {...common}>{p('M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4M16 17l5-5-5-5M21 12H9')}</svg>;
    case 'treadmill':return <svg {...common}>{p('M3 19l3-9 4 1 6-2 5 1v3l-5 1-3 6')}<circle cx="14" cy="4" r="2"/></svg>;
    case 'swim':     return <svg {...common}>{p('M2 17c2 0 2-1.5 4-1.5s2 1.5 4 1.5 2-1.5 4-1.5 2 1.5 4 1.5 2-1.5 4-1.5')}{p('M2 21c2 0 2-1.5 4-1.5s2 1.5 4 1.5 2-1.5 4-1.5 2 1.5 4 1.5 2-1.5 4-1.5')}<circle cx="17" cy="6" r="2"/>{p('M7 14l4-7 4 3')}</svg>;
    case 'basket':   return <svg {...common}><circle cx="12" cy="12" r="9"/>{p('M3 12h18M12 3v18M5.6 5.6l12.8 12.8M18.4 5.6L5.6 18.4')}</svg>;
    case 'routine':  return <svg {...common}>{p('M6 5v14M18 5v14M3 9h3M3 15h3M18 9h3M18 15h3M6 12h12')}</svg>;
    default: return null;
  }
}
window.Icon = Icon;

// Shared motion easing — slow, cinematic. No overshoot.
window.FT_EASE = 'cubic-bezier(0.25, 0.46, 0.45, 0.94)';

// Respect prefers-reduced-motion across the app.
function useReducedMotion() {
  const [reduced, setReduced] = React.useState(false);
  React.useEffect(() => {
    if (typeof window === 'undefined' || !window.matchMedia) return;
    const m = window.matchMedia('(prefers-reduced-motion: reduce)');
    const update = () => setReduced(!!m.matches);
    update();
    m.addEventListener ? m.addEventListener('change', update) : m.addListener(update);
    return () => { m.removeEventListener ? m.removeEventListener('change', update) : m.removeListener(update); };
  }, []);
  return reduced;
}
window.useReducedMotion = useReducedMotion;

// Press-scale hook: returns { bind, pressed } — spread `bind` onto DOM,
// read `pressed` separately. Avoids leaking a non-DOM "pressed" attribute.
function usePress() {
  const [pressed, setPressed] = React.useState(false);
  return {
    bind: {
      onPointerDown:  () => setPressed(true),
      onPointerUp:    () => setPressed(false),
      onPointerLeave: () => setPressed(false),
      onPointerCancel:() => setPressed(false),
    },
    pressed,
  };
}
window.usePress = usePress;

// Skeleton block with shimmer sweep (left → right, 1.6s loop).
function Skeleton({ w = '100%', h = 14, r = 8, style = {} }) {
  return (
    <div style={{
      width: w, height: h, borderRadius: r, background: FT.raised,
      position: 'relative', overflow: 'hidden', flexShrink: 0, ...style,
    }}>
      <div style={{
        position: 'absolute', inset: 0,
        background: 'linear-gradient(90deg, transparent 0%, rgba(255,255,255,0.04) 50%, transparent 100%)',
        animation: 'ft-shimmer 1.6s linear infinite',
      }} />
    </div>
  );
}
window.Skeleton = Skeleton;

// useFirstMountSkeleton — true for `ms` ms after mount, then false.
function useLoading(ms = 1200) {
  const [loading, setLoading] = React.useState(true);
  React.useEffect(() => { const id = setTimeout(() => setLoading(false), ms); return () => clearTimeout(id); }, [ms]);
  return loading;
}
window.useLoading = useLoading;

// 360 x 780 dark phone frame with status bar + gesture pill.
// Children render inside the safe content area (320px tall enough for nav).
// Inject app-wide keyframes (shimmer, fade, slide-up, flicker, pulse).
function FTKeyframes() {
  return (
    <style>{`
      @keyframes ft-shimmer { 0% { transform: translateX(-100%); } 100% { transform: translateX(100%); } }
      @keyframes ft-fade-in { from { opacity: 0; } to { opacity: 1; } }
      @keyframes ft-push-up { from { opacity: 0.7; transform: translateY(24px); } to { opacity: 1; transform: none; } }
      @keyframes ft-sheet-up { from { transform: translateY(100%); } to { transform: none; } }
      @keyframes ft-sheet-down { from { transform: translateY(0); } to { transform: translateY(100%); } }
      @keyframes ft-fade-out { from { opacity: 1; } to { opacity: 0; } }
      @keyframes ft-pb-slide { from { opacity: 0; transform: translateY(-8px); max-height: 0; } to { opacity: 1; transform: none; max-height: 120px; } }
      @keyframes ft-flicker { 0%,100% { transform: translateY(0) scaleY(1); opacity: 0.9; } 50% { transform: translateY(-0.6px) scaleY(1.04); opacity: 1; } }
      @keyframes ft-pulse-ring { 0% { opacity: 0.55; } 50% { opacity: 0.2; } 100% { opacity: 0.55; } }
      @keyframes ft-progress-pulse { 0%,100% { transform: scaleY(1); } 50% { transform: scaleY(2); } }
      @keyframes ft-ripple-expand {
        0%   { transform: translate(-50%, -50%) scale(0); opacity: 0.92; }
        70%  { opacity: 0.92; }
        100% { transform: translate(-50%, -50%) scale(var(--ripple-scale, 8)); opacity: 0; }
      }
      @media (prefers-reduced-motion: reduce) {
        * { animation-duration: 0.001ms !important; animation-iteration-count: 1 !important; transition-duration: 0.001ms !important; }
      }
    `}</style>
  );
}
window.FTKeyframes = FTKeyframes;

function PhoneFrame({ children, statusDark = true, time, noNav = false, use24h = false }) {
  // If time not supplied, build current time string (honoring 24h toggle).
  const t = time || (() => {
    const now = new Date(); let h = now.getHours(); const m = now.getMinutes();
    const mm = m.toString().padStart(2, '0');
    if (use24h) return `${h.toString().padStart(2, '0')}:${mm}`;
    const ampm = h >= 12 ? '' : '';
    const h12 = ((h + 11) % 12) + 1;
    return `${h12}:${mm}`;
  })();
  const tint = statusDark ? '#FFFFFF' : '#000';
  return (
    <div style={{
      width: 360, height: 780, background: FT.bg,
      borderRadius: 28, overflow: 'hidden',
      fontFamily: FT.font, color: FT.text,
      position: 'relative', display: 'flex', flexDirection: 'column',
      border: '1px solid #2A2A2D',
    }}>
      <FTKeyframes />
      {/* Status bar */}
      <div style={{
        height: 32, display: 'flex', alignItems: 'center',
        justifyContent: 'space-between', padding: '0 22px',
        fontSize: 13, fontWeight: 500, color: tint, position: 'relative', flexShrink: 0,
        fontFamily: FT.font,
      }}>
        <span style={{ fontVariantNumeric: 'tabular-nums' }}>{t}</span>
        <div style={{
          position: 'absolute', left: '50%', top: 8, transform: 'translateX(-50%)',
          width: 90, height: 22, background: '#000', borderRadius: 16,
        }} />
        <div style={{ display: 'flex', gap: 5, alignItems: 'center' }}>
          {/* signal */}
          <svg width="15" height="11" viewBox="0 0 15 11" fill={tint}><rect x="0" y="7" width="2.5" height="4" rx=".5"/><rect x="4" y="5" width="2.5" height="6" rx=".5"/><rect x="8" y="2" width="2.5" height="9" rx=".5"/><rect x="12" y="0" width="2.5" height="11" rx=".5" opacity=".4"/></svg>
          {/* wifi */}
          <svg width="14" height="11" viewBox="0 0 14 11" fill={tint}><path d="M7 11l-1.5-1.7a2 2 0 0 1 3 0L7 11zM2 5.5a8 8 0 0 1 10 0l-1.2 1.4a6 6 0 0 0-7.6 0L2 5.5zM0 3a11 11 0 0 1 14 0l-1.2 1.4a9 9 0 0 0-11.6 0L0 3z"/></svg>
          {/* battery */}
          <svg width="22" height="11" viewBox="0 0 22 11" fill="none"><rect x=".5" y=".5" width="18" height="10" rx="2.5" stroke={tint} opacity=".5"/><rect x="2" y="2" width="13" height="7" rx="1.2" fill={tint}/><rect x="19.5" y="3.5" width="1.5" height="4" rx=".5" fill={tint} opacity=".5"/></svg>
        </div>
      </div>

      {/* Content */}
      <div style={{ flex: 1, overflow: 'hidden', position: 'relative' }}>
        {children}
      </div>

      {/* Gesture pill */}
      {!noNav && (
        <div style={{ height: 22, display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0 }}>
          <div style={{ width: 110, height: 4, borderRadius: 2, background: 'rgba(255,255,255,0.5)' }} />
        </div>
      )}
    </div>
  );
}
window.PhoneFrame = PhoneFrame;

// Bottom tab bar — floating island, pill-shaped, sliding active indicator.
function TabBar({ active = 'home', onSelect }) {
  const tabs = [
    { id: 'home', icon: 'home', label: 'Home' },
    { id: 'calendar', icon: 'calendar', label: 'Calendar' },
    { id: 'templates', icon: 'clip', label: 'Templates' },
    { id: 'stats', icon: 'chart', label: 'Stats' },
  ];
  const activeIdx = Math.max(0, tabs.findIndex(t => t.id === active));
  return (
    <div style={{
      position: 'absolute', left: 14, right: 14, bottom: 14, height: 64,
      background: 'rgba(26,26,28,0.92)',
      backdropFilter: 'blur(20px) saturate(140%)',
      WebkitBackdropFilter: 'blur(20px) saturate(140%)',
      border: `1px solid rgba(255,255,255,0.05)`,
      borderRadius: 1000,
      boxShadow: '0 12px 32px rgba(0,0,0,0.55), 0 2px 6px rgba(0,0,0,0.3)',
      display: 'flex', alignItems: 'stretch', padding: 6,
      zIndex: 5, fontFamily: FT.font,
    }}>
      {/* Sliding active-pill indicator */}
      <div style={{
        position: 'absolute', top: 6, bottom: 6,
        left: `calc(6px + ${activeIdx} * ((100% - 12px) / 4))`,
        width: 'calc((100% - 12px) / 4)',
        background: 'rgba(255,107,53,0.12)',
        borderRadius: 1000,
        transition: `left 250ms ${FT_EASE}`,
        pointerEvents: 'none',
      }} />
      {tabs.map(t => {
        const on = t.id === active;
        return (
          <button key={t.id} onClick={() => onSelect && onSelect(t.id)}
            style={{
              flex: 1, background: 'transparent',
              border: 'none', cursor: 'pointer', borderRadius: 1000,
              display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', gap: 3,
              color: on ? FT.orange : FT.text3, fontFamily: 'inherit',
              padding: 0, position: 'relative', zIndex: 1,
              transition: `color 250ms ${FT_EASE}`,
            }}>
            <Icon name={t.icon} size={20} color={on ? FT.orange : FT.text3} stroke={on ? 2 : 1.8} />
            <span style={{ fontSize: 10, fontWeight: 500, letterSpacing: 0.2, fontFamily: FT.font }}>{t.label}</span>
          </button>
        );
      })}
    </div>
  );
}
window.TabBar = TabBar;

// A scrollable content area. Leaves room at the bottom for the floating tab bar island.
// 24px horizontal padding per spec.
function Screen({ children, padded = true, noTab = false, scroll = true, style = {} }) {
  return (
    <div style={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
      <div style={{
        flex: 1,
        overflow: scroll ? 'auto' : 'hidden',
        padding: padded ? `14px 24px ${noTab ? 24 : 104}px` : 0,
        ...style,
      }}>
        {children}
      </div>
    </div>
  );
}
window.Screen = Screen;

// Soft underline input — borderless, only an orange underline on focus that
// animates from 0% to 100% width with 200ms ease.
function Underline({ focused, color = FT.orange }) {
  return (
    <div style={{ position: 'relative', height: 1, background: 'rgba(255,255,255,0.08)' }}>
      <div style={{
        position: 'absolute', left: 0, top: 0, height: 2,
        width: focused ? '100%' : '0%',
        background: color,
        transition: `width 200ms ${FT_EASE}`,
        transformOrigin: 'left center',
      }} />
    </div>
  );
}
window.Underline = Underline;

// ─────────────────────────────────────────────────────────────
// Avatar — circular, falls back to monogram on a gradient
// ─────────────────────────────────────────────────────────────
function Avatar({ pic, name = 'A', size = 40, ring = false, onClick, ringColor }) {
  const initial = ((name || 'A').trim()[0] || 'A').toUpperCase();
  const Tag = onClick ? 'button' : 'div';
  return (
    <Tag onClick={onClick} style={{
      width: size, height: size, borderRadius: '50%',
      background: pic ? '#000' : FT.raised,
      backgroundImage: pic ? `url(${pic})` : undefined,
      backgroundSize: 'cover', backgroundPosition: 'center',
      border: ring ? `1px solid ${ringColor || 'rgba(255,255,255,0.08)'}` : 'none',
      cursor: onClick ? 'pointer' : 'default',
      display: 'flex', alignItems: 'center', justifyContent: 'center',
      color: FT.text, fontWeight: 500, fontSize: size * 0.40,
      fontFamily: FT.fontDisplay, letterSpacing: -0.5,
      padding: 0, overflow: 'hidden', flexShrink: 0,
      transition: 'transform .15s', outline: 'none',
    }}>
      {!pic && initial}
    </Tag>
  );
}
window.Avatar = Avatar;

// Time-of-day greeting — sentence case, with trailing comma.
function greeting(now = new Date()) {
  const h = now.getHours();
  if (h < 5)  return 'Good evening,';
  if (h < 12) return 'Good morning,';
  if (h < 17) return 'Good afternoon,';
  return 'Good evening,';
}
window.greeting = greeting;

// Shared socials config (used in Settings + ProfileMenu)
window.SOCIALS = [
  { k: 'twitter',  label: 'Twitter',  url: 'https://x.com/TrnhQucBnhNguy1',     color: '#FFFFFF' },
  { k: 'github',   label: 'GitHub',   url: 'https://github.com/binhnguyeeen',   color: '#FFFFFF' },
  { k: 'youtube',  label: 'YouTube',  url: 'https://www.youtube.com/@stfcurry', color: '#FF0033' },
  { k: 'gmail',    label: 'Email',    url: 'mailto:trinhquocbinhnguyen@gmail.com', color: '#EA4335', image: 'assets/gmail.png' },
];
