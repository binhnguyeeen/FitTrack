// FitTrack screens — shared bits + Onboarding + Home + Calendar.

const { useState, useEffect, useRef } = React;

// ─────────────────────────────────────────────────────────────
// Buttons
// ─────────────────────────────────────────────────────────────
// Primary CTA. Tiempos Text, 16dp radius, scale(0.97) press, 120ms ease-in-out.
function PrimaryButton({ children, color = FT.orange, full = true, size = 'lg', onClick, leading, disabled }) {
  const press = usePress();
  const padY = size === 'lg' ? 16 : 12;
  const padX = size === 'lg' ? 22 : 16;
  const fs = size === 'lg' ? 15 : 14;
  return (
    <button onClick={disabled ? undefined : onClick} disabled={disabled} {...press.bind}
    style={{
      width: full ? '100%' : 'auto', padding: `${padY}px ${padX}px`, border: 'none',
      background: disabled ? FT.raised : color,
      color: disabled ? FT.text3 : '#FFF',
      borderRadius: 16, fontFamily: FT.font, fontWeight: 500, fontSize: fs,
      cursor: disabled ? 'not-allowed' : 'pointer',
      display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 8,
      letterSpacing: 0.01, lineHeight: 1,
      transform: press.pressed && !disabled ? 'scale(0.97)' : 'scale(1)',
      transition: `transform 120ms ease-in-out, background 200ms ${FT_EASE}`
    }}>
      {leading}{children}
    </button>);

}
window.PrimaryButton = PrimaryButton;

function GhostButton({ children, onClick, full = true, leading }) {
  const press = usePress();
  return (
    <button onClick={onClick} {...press.bind} style={{
      width: full ? '100%' : 'auto', padding: '14px 18px',
      background: 'transparent', color: FT.text,
      border: `1px solid rgba(255,255,255,0.08)`,
      borderRadius: 16, fontFamily: FT.font, fontWeight: 500, fontSize: 14,
      cursor: 'pointer', display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 8,
      transform: press.pressed ? 'scale(0.97)' : 'scale(1)',
      transition: `transform 120ms ease-in-out`
    }}>
      {leading}{children}
    </button>);

}
window.GhostButton = GhostButton;

// ─────────────────────────────────────────────────────────────
// Activity bits
// ─────────────────────────────────────────────────────────────
function ActivityDot({ kind, size = 8 }) {
  return <div style={{ width: size, height: size, borderRadius: size / 2, background: ACT[kind].color, flexShrink: 0 }} />;
}
window.ActivityDot = ActivityDot;

// Vertical color strip — 4px wide, used on template cards (per spec).
function ActivityStrip({ kind, height = 40 }) {
  return <div style={{ width: 4, height, borderRadius: 999, background: ACT[kind].color, flexShrink: 0 }} />;
}
window.ActivityStrip = ActivityStrip;

// Activity glyph chip — used in onboarding & calendar day detail. Soft tint.
function ActivityChip({ kind, size = 36 }) {
  return (
    <div style={{
      width: size, height: size, borderRadius: 14, background: ACT[kind].soft,
      display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0
    }}>
      <Icon name={ACT[kind].icon} size={size * 0.5} color={ACT[kind].color} stroke={1.8} />
    </div>);

}
window.ActivityChip = ActivityChip;

// 10px all-caps activity tag — wide tracking. Color = activity color.
function ActivityTag({ kind, label }) {
  return (
    <span style={{
      fontFamily: FT.font, fontWeight: 500, fontSize: 10,
      letterSpacing: '0.12em', textTransform: 'uppercase',
      color: ACT[kind].color
    }}>{label || ACT[kind].label}</span>);

}
window.ActivityTag = ActivityTag;

// Floating card. No border by default; opt in with `bordered`.
function Card({ children, style = {}, onClick, bordered = false, padded = true }) {
  const press = usePress();
  const interactive = !!onClick;
  return (
    <div onClick={onClick} {...interactive ? press.bind : {}} style={{
      background: FT.surface, borderRadius: 24,
      padding: padded ? 20 : 0,
      border: bordered ? `1px solid rgba(255,255,255,0.05)` : 'none',
      cursor: interactive ? 'pointer' : 'default',
      transform: interactive && press.pressed ? 'scale(0.98)' : 'scale(1)',
      transition: `transform 120ms ease-in-out, background 200ms ${FT_EASE}`,
      ...style
    }}>{children}</div>);

}
window.Card = Card;

// Section label. 10px Tiempos Text, wide tracking, tertiary color.
// 28px top margin, 10px bottom margin (per spec).
function SectionLabel({ children, style = {} }) {
  return (
    <div style={{
      fontFamily: FT.font, fontSize: 10, fontWeight: 500,
      letterSpacing: '0.12em', textTransform: 'uppercase',
      color: FT.text3, marginTop: 28, marginBottom: 10,
      ...style
    }}>{children}</div>);

}
window.SectionLabel = SectionLabel;

// ─────────────────────────────────────────────────────────────
// Streak badge — flame icon + "12 consecutive days"
// rgba(255,107,53,0.10) bg, orange border 0.3 opacity.
// ─────────────────────────────────────────────────────────────
function StreakBadge({ count = 12 }) {
  return (
    <div style={{
      display: 'inline-flex', alignItems: 'center', gap: 8,
      background: 'rgba(255,107,53,0.10)',
      padding: '6px 12px 6px 10px', borderRadius: 999,
      border: '1px solid rgba(255,107,53,0.30)',
      color: FT.orange, fontFamily: FT.font, fontWeight: 500, fontSize: 13,
      letterSpacing: 0.01, lineHeight: 1
    }}>
      <StreakFlame size={14} />
      <span><em style={{ fontFamily: FT.fontFine, fontStyle: 'italic', fontWeight: 400, fontSize: 14, letterSpacing: 0.01 }}>{count}</em> consecutive days</span>
    </div>);

}
window.StreakBadge = StreakBadge;

// Minimal flame — subtle vertical flicker (no scale > 1.04).
function StreakFlame({ size = 14, color = FT.orange }) {
  return (
    <svg width={size} height={size} viewBox="0 0 24 24" style={{
      display: 'block', flexShrink: 0,
      animation: 'ft-flicker 1.4s ease-in-out infinite alternate',
      transformOrigin: '50% 80%'
    }}>
      <path fill={color} d="M13.4 1.2c.6 2.6-.8 4.4-2.4 6.1-1.2 1.3-2.3 2.6-2.3 4.3 0 1.2.7 2 1.7 2.5-2.5-.3-4.2-2.1-4.2-4.7-1.7 2.3-2.7 4.7-2.7 7.3 0 4.2 3.6 7.6 8.2 7.6S20 20.9 20 16.6c0-7.2-6.6-9.3-6.6-15.4z" />
    </svg>);

}
window.StreakFlame = StreakFlame;

// ─────────────────────────────────────────────────────────────
// ONBOARDING — 2 steps. Full bleed, no logo, two-segment indicator.
// ─────────────────────────────────────────────────────────────
function OnboardingScreen({ onSubmit }) {
  const [step, setStep] = useState(1);
  const [name, setName] = useState('Nguyen');
  const [pic, setPic] = useState(null);
  const [nameFocused, setNameFocused] = useState(false);
  const fileRef = useRef(null);

  // Tweaks — bold key words, and the "you can change this in Settings" hint.
  const tweaks = typeof window !== 'undefined' && typeof window.useFTTweaks === 'function' ?
  window.useFTTweaks() : {};
  const boldEmphasis = tweaks.boldEmphasis !== false;
  const showSettingsHint = tweaks.showSettingsHint !== false;
  const emphasisStyle = boldEmphasis ?
  { fontFamily: FT.fontFine, fontStyle: 'normal', fontWeight: 700 } :
  { fontFamily: FT.fontFine, fontStyle: 'italic', fontWeight: 400 };

  const pickFile = () => fileRef.current && fileRef.current.click();
  const onFile = (e) => {
    const f = e.target.files && e.target.files[0];if (!f) return;
    const r = new FileReader();
    r.onload = () => setPic(r.result);
    r.readAsDataURL(f);
  };
  const finish = () => onSubmit && onSubmit({ name, pic });
  const canContinue = name.trim().length >= 2;

  return (
    <div style={{ height: '100%', display: 'flex', flexDirection: 'column', padding: '64px 24px 28px' }}>
      {/* Two-segment step indicator: filled = current step */}
      <div style={{ display: 'flex', alignItems: 'center', gap: 6, marginBottom: 44 }}>
        <div style={{ height: 2, flex: 1, borderRadius: 1, background: step === 1 ? FT.orange : 'rgba(255,255,255,0.10)', transition: `background 300ms ${FT_EASE}` }} />
        <div style={{ height: 2, flex: 1, borderRadius: 1, background: step === 2 ? FT.orange : 'rgba(255,255,255,0.10)', transition: `background 300ms ${FT_EASE}` }} />
      </div>

      <div style={{ flex: 1, minHeight: 0, display: 'flex', flexDirection: 'column' }}>
        {step === 1 ?
        <>
            <h1 style={{
            fontFamily: FT.font, fontSize: 48, fontWeight: 400,
            lineHeight: 1.0, margin: 0, letterSpacing: '-0.05em',
            color: FT.text, textWrap: 'balance'
          }}>
              What's your <em style={{ ...emphasisStyle, fontFamily: "\"Tiempos Headline\"" }}>name?</em>
            </h1>
            <div style={{ marginTop: 56 }}>
              <input
              value={name}
              onChange={(e) => setName(e.target.value)}
              onFocus={() => setNameFocused(true)}
              onBlur={() => setNameFocused(false)}
              placeholder="Your name"
              autoFocus
              style={{
                width: '100%', boxSizing: 'border-box',
                background: 'transparent', border: 'none',
                color: FT.text, fontSize: 36, fontWeight: 400,
                padding: '6px 0', fontFamily: FT.fontDisplay, outline: 'none',
                letterSpacing: '-0.05em', lineHeight: 1.1
              }} />
            
              <Underline focused={nameFocused} />
              {showSettingsHint &&
            <div style={{
              marginTop: 14,
              fontFamily: FT.font, fontSize: 13, fontWeight: 400,
              color: FT.text2, letterSpacing: 0.01, lineHeight: 1.4
            }}>
                  You can always change this in Settings.
                </div>
            }
            </div>
            <div style={{ flex: 1 }} />
          </> :

        <>
            <h1 style={{
            fontFamily: FT.font, fontSize: 48, fontWeight: 400,
            lineHeight: 1.0, margin: 0, letterSpacing: '-0.05em',
            color: FT.text, textWrap: 'balance'
          }}>
              Add a <em style={{ ...emphasisStyle, fontFamily: "\"Tiempos Fine\"" }}>photo</em>
            </h1>
            <div style={{ marginTop: 56, display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 18 }}>
              <button onClick={pickFile} style={{
              width: 120, height: 120, borderRadius: '50%',
              background: pic ? '#000' : 'transparent',
              backgroundImage: pic ? `url(${pic})` : undefined,
              backgroundSize: 'cover', backgroundPosition: 'center',
              border: pic ? `1px solid rgba(255,255,255,0.10)` : `1px dashed rgba(255,107,53,0.4)`,
              cursor: 'pointer', display: 'flex', alignItems: 'center', justifyContent: 'center',
              fontFamily: 'inherit', padding: 0
            }}>
                {!pic && <Icon name="camera" size={20} color={FT.orange} stroke={1.6} />}
              </button>
              <input ref={fileRef} type="file" accept="image/*" onChange={onFile} style={{ display: 'none' }} />
              {pic &&
            <button onClick={() => setPic(null)} style={{ background: 'transparent', border: 'none', color: FT.text3, fontSize: 12, fontWeight: 400, cursor: 'pointer', fontFamily: FT.font, letterSpacing: 0.04 }}>
                  Remove photo
                </button>
            }
              {showSettingsHint &&
            <div style={{
              marginTop: 4, textAlign: 'center',
              fontFamily: FT.font, fontSize: 13, fontWeight: 400,
              color: FT.text2, letterSpacing: 0.01, lineHeight: 1.4
            }}>
                  You can always change this in Settings.
                </div>
            }
            </div>
            <div style={{ flex: 1 }} />
          </>
        }
      </div>

      {step === 1 ?
      <PrimaryButton onClick={() => canContinue && setStep(2)} disabled={!canContinue}>
          Continue
        </PrimaryButton> :

      <div style={{ display: 'flex', gap: 10 }}>
          <button onClick={finish} style={{
          padding: '16px 18px', border: 'none', background: 'transparent',
          color: FT.text2, fontFamily: FT.font, fontWeight: 400, fontSize: 14, cursor: 'pointer'
        }}>Skip</button>
          <div style={{ flex: 1 }}>
            <PrimaryButton onClick={finish}>Done</PrimaryButton>
          </div>
        </div>
      }
    </div>);

}
window.OnboardingScreen = OnboardingScreen;

// ─────────────────────────────────────────────────────────────
// HOME
// ─────────────────────────────────────────────────────────────
function HomeScreen({ name = 'Nguyen', pic = null, onNavigate, onStartWorkout, onOpenProfile }) {
  const loading = useLoading(1200);

  // Recent activity, per spec
  const recent = [
  { kind: 'swim', name: 'Swimming', detail: '1,800m', when: 'Yesterday', dur: '42 min' },
  { kind: 'treadmill', name: 'Treadmill', detail: '5.2 km', when: '2 days ago', dur: '31 min' },
  { kind: 'routine', name: 'Routine', detail: 'Upper Body', when: '3 days ago', dur: '38 min' }];


  const quick = [
  { k: 'treadmill', label: 'Treadmill' },
  { k: 'swim', label: 'Swimming' },
  { k: 'basket', label: 'Basketball' },
  { k: 'routine', label: 'Routine' }];


  return (
    <Screen>
      {/* Greeting row — two-line */}
      <div style={{ display: 'flex', alignItems: 'flex-start', justifyContent: 'space-between', marginTop: 8, gap: 12 }}>
        <div style={{ flex: 1, minWidth: 0 }}>
          <div style={{
            fontFamily: FT.font, fontSize: 15, fontWeight: 400,
            color: FT.text2, lineHeight: 1.2, letterSpacing: 0.01
          }}>
            {greeting()}
          </div>
          <div style={{
            fontFamily: FT.fontFine, fontStyle: 'italic', fontWeight: 400,
            fontSize: 38, letterSpacing: '-0.05em', color: FT.text,
            lineHeight: 1.05, marginTop: 4
          }}>
            {name}
          </div>
        </div>
        <Avatar pic={pic} name={name} size={40} onClick={onOpenProfile} />
      </div>

      <div style={{ marginTop: 16 }}>
        <StreakBadge count={12} />
      </div>

      {/* Today */}
      <SectionLabel>Today</SectionLabel>
      <Card>
        <ActivityTag kind="routine" label="Upper Body" />
        <div style={{
          marginTop: 8,
          fontFamily: FT.fontDisplay, fontSize: 30, fontWeight: 400,
          letterSpacing: '-0.05em', color: FT.text, lineHeight: 1.05
        }}>
          Upper Body
        </div>
        <div style={{
          marginTop: 8, display: 'flex', alignItems: 'center', gap: 10,
          fontFamily: FT.font, fontSize: 13, fontWeight: 400, color: FT.text2,
          letterSpacing: 0.01
        }}>
          <span>5 exercises</span>
          <span style={{ width: 3, height: 3, borderRadius: 2, background: FT.text3 }} />
          <span style={{ fontVariantNumeric: 'tabular-nums' }}>6:30 AM</span>
        </div>
        <div style={{ marginTop: 18 }}>
          <PrimaryButton onClick={onStartWorkout} size="md">Start</PrimaryButton>
        </div>
      </Card>

      {/* Quick start — 4 horizontal pills, 80×64 */}
      <SectionLabel>Quick start</SectionLabel>
      <div style={{
        display: 'flex', gap: 10, overflowX: 'auto',
        margin: '0 -24px', padding: '0 24px 4px',
        scrollbarWidth: 'none'
      }}>
        {quick.map((q) =>
        <QuickPill key={q.k} kind={q.k} label={q.label} />
        )}
      </div>

      {/* Recent activity */}
      <SectionLabel>Recent activity</SectionLabel>
      <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
        {loading ?
        [0, 1, 2].map((i) => <RecentSkeleton key={i} />) :
        recent.map((r, i) => <RecentRow key={i} {...r} />)
        }
      </div>
    </Screen>);

}
window.HomeScreen = HomeScreen;

function QuickPill({ kind, label }) {
  const press = usePress();
  return (
    <button {...press.bind} style={{
      width: 80, height: 64, borderRadius: 20, flexShrink: 0,
      background: ACT[kind].soft, border: 'none', cursor: 'pointer',
      display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', gap: 4,
      fontFamily: FT.font,
      transform: press.pressed ? 'scale(0.98)' : 'scale(1)',
      transition: `transform 80ms ease-in-out`,
      padding: 0
    }}>
      <Icon name={ACT[kind].icon} size={20} color={ACT[kind].color} stroke={1.8} />
      <span style={{ fontSize: 10, fontWeight: 400, color: FT.text, letterSpacing: 0.04 }}>{label}</span>
    </button>);

}

function RecentRow({ kind, name, detail, when, dur }) {
  const press = usePress();
  return (
    <div {...press.bind} style={{
      display: 'flex', alignItems: 'center', gap: 12,
      padding: '14px 4px', cursor: 'pointer',
      transform: press.pressed ? 'scale(0.98)' : 'scale(1)',
      transition: `transform 120ms ease-in-out`
    }}>
      <ActivityChip kind={kind} size={36} />
      <div style={{ flex: 1, minWidth: 0 }}>
        <div style={{
          fontFamily: FT.font, fontSize: 14, fontWeight: 400,
          color: FT.text, letterSpacing: 0.01, lineHeight: 1.2
        }}>
          {name} <span style={{ color: FT.text3 }}>· {detail}</span>
        </div>
        <div style={{
          fontFamily: FT.font, fontSize: 12, fontWeight: 400,
          color: FT.text2, marginTop: 3, letterSpacing: 0.01
        }}>{when}</div>
      </div>
      <div style={{
        fontFamily: FT.fontFine, fontStyle: 'italic', fontWeight: 400,
        fontSize: 14, color: FT.text, fontVariantNumeric: 'tabular-nums'
      }}>{dur}</div>
    </div>);

}

function RecentSkeleton() {
  return (
    <div style={{ display: 'flex', alignItems: 'center', gap: 12, padding: '14px 4px' }}>
      <Skeleton w={36} h={36} r={14} />
      <div style={{ flex: 1, display: 'flex', flexDirection: 'column', gap: 6 }}>
        <Skeleton w="60%" h={12} r={6} />
        <Skeleton w="35%" h={10} r={5} />
      </div>
      <Skeleton w={42} h={12} r={6} />
    </div>);

}

// ─────────────────────────────────────────────────────────────
// CALENDAR
// ─────────────────────────────────────────────────────────────
function CalendarScreen({ selected = 3, onSelect, initialPlanning = false }) {
  const days = [
  { d: 'Mon', n: 12, status: 'done', kind: 'treadmill' },
  { d: 'Tue', n: 13, status: 'done', kind: 'routine' },
  { d: 'Wed', n: 14, status: 'done', kind: 'swim' },
  { d: 'Thu', n: 15, status: 'planned', kind: 'routine' }, // today
  { d: 'Fri', n: 16, status: 'planned', kind: 'basket' },
  { d: 'Sat', n: 17, status: 'planned', kind: 'treadmill' },
  { d: 'Sun', n: 18, status: 'none', kind: null }];

  const [sel, setSel] = useState(selected);
  const [planning, setPlanning] = useState(initialPlanning);
  const day = days[sel];

  return (
    <Screen>
      {/* Month label — Headline 28px, "May" regular + "2026" Fine italic */}
      <div style={{ marginTop: 8, marginBottom: 4 }}>
        <div style={{
          fontFamily: FT.fontDisplay, fontSize: 28, fontWeight: 400,
          letterSpacing: '-0.05em', color: FT.text, lineHeight: 1.1
        }}>
          May <em style={{ fontFamily: FT.fontFine, fontStyle: 'italic', fontWeight: 400 }}>2026</em>
        </div>
      </div>

      {/* Week strip — 40×56 */}
      <div style={{
        display: 'flex', justifyContent: 'space-between',
        marginTop: 20
      }}>
        {days.map((d, i) => {
          const isSel = i === sel;
          return (
            <WeekDay key={i} day={d} selected={isSel}
            onClick={() => {setSel(i);setPlanning(false);onSelect && onSelect(i);}} />);

        })}
      </div>

      {/* Activity-chip row — one per day column. Single source of truth for icons. */}
      <div style={{
        display: 'flex', justifyContent: 'space-between',
        marginTop: 6,
      }}>
        {days.map((d, i) => (
          <div key={i} style={{
            width: 40, display: 'flex', alignItems: 'center', justifyContent: 'center',
          }}>
            {d.status !== 'none' && d.kind
              ? <ActivityChip kind={d.kind} size={20} />
              : <div style={{ width: 20, height: 20 }} />}
          </div>
        ))}
      </div>

      {/* Day detail */}
      <SectionLabel>{day.d}, May {day.n}{sel === 3 ? ' · Today' : ''}</SectionLabel>
      {day.kind ?
      <Card>
          <ActivityTag kind={day.kind} />
          <div style={{
          marginTop: 8,
          fontFamily: FT.fontDisplay, fontSize: 24, fontWeight: 400,
          letterSpacing: '-0.05em', color: FT.text, lineHeight: 1.1
        }}>
            {day.kind === 'swim' ? 'Swim Intervals' :
          day.kind === 'basket' ? 'Pickup Game' :
          day.kind === 'treadmill' ? 'Morning Run' :
          'Upper Body'}
          </div>
          <div style={{
          marginTop: 8, display: 'flex', alignItems: 'center', gap: 10,
          fontSize: 13, color: FT.text2, fontFamily: FT.font, letterSpacing: 0.01
        }}>
            <span>{day.kind === 'swim' ? '4 sets' : day.kind === 'basket' ? '2 drills' : day.kind === 'treadmill' ? '3 intervals' : '5 exercises'}</span>
            <span style={{ width: 3, height: 3, borderRadius: 2, background: FT.text3 }} />
            <span style={{ fontVariantNumeric: 'tabular-nums' }}>6:30 AM</span>
          </div>
          {day.status === 'done' ?
        <div style={{
          marginTop: 16, display: 'inline-flex', alignItems: 'center', gap: 6,
          fontSize: 12, color: ACT[day.kind].color, fontFamily: FT.font, letterSpacing: 0.04, textTransform: 'uppercase'
        }}>
              <Icon name="check" size={12} color={ACT[day.kind].color} stroke={2.4} /> Completed
            </div> :

        <div style={{ marginTop: 16, display: 'flex', gap: 10 }}>
              <div style={{ flex: 1 }}><PrimaryButton size="md">Start</PrimaryButton></div>
              <button style={{
            padding: '14px 18px', background: 'transparent',
            color: FT.text2, border: 'none', fontFamily: FT.font, fontSize: 14,
            cursor: 'pointer', letterSpacing: 0.01
          }}>Reschedule</button>
            </div>
        }
        </Card> :

      <button onClick={() => setPlanning(true)} style={{
        width: '100%', padding: 22, background: 'transparent',
        border: `1px dashed rgba(255,255,255,0.10)`,
        borderRadius: 24, color: FT.text2, fontFamily: FT.font, fontWeight: 400, fontSize: 14,
        display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 8, cursor: 'pointer',
        letterSpacing: 0.01
      }}>
          <Icon name="plus" size={16} color={FT.text2} /> Plan workout
        </button>
      }

      {/* Plan workout — slides up as a bottom sheet (80% height) */}
      {planning && <PlanWorkoutSheet day={day} onCancel={() => setPlanning(false)} onSave={() => setPlanning(false)} />}
    </Screen>);

}
window.CalendarScreen = CalendarScreen;

function WeekDay({ day, selected, onClick }) {
  const press = usePress();
  return (
    <button onClick={onClick} {...press.bind} style={{
      width: 40, height: 56, background: 'transparent', border: 'none', cursor: 'pointer',
      display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'flex-start',
      fontFamily: FT.font, padding: 0,
      transform: press.pressed ? 'scale(0.95)' : 'scale(1)',
      transition: `transform 80ms ease-in-out`
    }}>
      <span style={{
        fontSize: 10, fontWeight: 400, color: FT.text3,
        letterSpacing: '0.12em', textTransform: 'uppercase'
      }}>{day.d}</span>
      <div style={{
        width: 32, height: 32, marginTop: 4, borderRadius: 16,
        display: 'flex', alignItems: 'center', justifyContent: 'center',
        background: selected ? FT.orange : 'transparent',
        transition: `background 200ms ${FT_EASE}`
      }}>
        <span style={{
          fontFamily: FT.fontDisplay, fontSize: 16, fontWeight: 400,
          fontVariantNumeric: 'tabular-nums', color: selected ? '#FFF' : FT.text
        }}>{day.n}</span>
      </div>
    </button>);

}

// Bottom sheet — 80% height, 28dp top corners, 400ms ease-out.
function PlanWorkoutSheet({ day, onCancel, onSave }) {
  const [kind, setKind] = useState('routine');
  const [tplIdx, setTplIdx] = useState(0);
  const [time, setTime] = useState('6:30 AM');

  const tplOptions = {
    treadmill: ['Morning Run', 'Tempo intervals', 'Long Run'],
    swim: ['Swim Intervals', 'Recovery swim'],
    basket: ['Pickup Game', 'Free Throws'],
    routine: ['Upper Body', 'Core & Stretch', 'Leg Day']
  };
  const times = ['5:30 AM', '6:00 AM', '6:30 AM', '7:00 AM', '7:30 AM', '12:00 PM', '5:00 PM', '6:00 PM'];

  return (
    <div style={{
      position: 'absolute', left: 0, right: 0, bottom: 0,
      height: '80%', background: FT.surface,
      borderTopLeftRadius: 28, borderTopRightRadius: 28,
      animation: `ft-sheet-up 400ms ${FT_EASE}`,
      display: 'flex', flexDirection: 'column', zIndex: 20,
      boxShadow: '0 -16px 40px rgba(0,0,0,0.5)'
    }}>
      <div style={{ width: 36, height: 4, borderRadius: 2, background: 'rgba(255,255,255,0.10)', margin: '12px auto 4px', flexShrink: 0 }} />
      <div style={{ padding: '12px 24px 0', display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexShrink: 0 }}>
        <div style={{
          fontFamily: FT.fontDisplay, fontSize: 22, fontWeight: 400,
          letterSpacing: '-0.05em', color: FT.text
        }}>
          Plan for {day.d}, May {day.n}
        </div>
        <button onClick={onCancel} style={{ width: 32, height: 32, background: 'transparent', border: 'none', cursor: 'pointer', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
          <Icon name="close" size={18} color={FT.text2} />
        </button>
      </div>

      <div style={{ flex: 1, overflow: 'auto', padding: '8px 24px 16px' }}>
        <SectionLabel style={{ marginTop: 20 }}>Activity</SectionLabel>
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: 8 }}>
          {['treadmill', 'swim', 'basket', 'routine'].map((k) => {
            const on = kind === k;
            return (
              <button key={k} onClick={() => {setKind(k);setTplIdx(0);}} style={{
                background: on ? ACT[k].soft : 'transparent',
                border: on ? `1px solid ${ACT[k].color}` : `1px solid rgba(255,255,255,0.05)`,
                borderRadius: 16, padding: '10px 0', cursor: 'pointer', fontFamily: FT.font,
                display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 4,
                color: on ? ACT[k].color : FT.text2,
                transition: `background 200ms ${FT_EASE}, border-color 200ms ${FT_EASE}`
              }}>
                <Icon name={ACT[k].icon} size={18} color={on ? ACT[k].color : FT.text2} stroke={1.8} />
                <span style={{ fontSize: 10, fontWeight: 400, letterSpacing: 0.04, whiteSpace: 'nowrap' }}>{ACT[k].label}</span>
              </button>);

          })}
        </div>

        <SectionLabel>Template</SectionLabel>
        <div style={{ display: 'flex', gap: 8, flexWrap: 'wrap' }}>
          {tplOptions[kind].map((t, i) => {
            const on = tplIdx === i;
            return (
              <button key={t} onClick={() => setTplIdx(i)} style={{
                padding: '10px 14px', borderRadius: 14, height: 40,
                background: on ? FT.orange : FT.raised,
                border: 'none', color: on ? '#FFF' : FT.text,
                fontFamily: FT.font, fontWeight: on ? 500 : 400, fontSize: 13, cursor: 'pointer',
                transition: `background 200ms ${FT_EASE}`
              }}>{t}</button>);

          })}
        </div>

        <SectionLabel>Reminder</SectionLabel>
        <div style={{ display: 'flex', gap: 8, overflowX: 'auto', margin: '0 -24px', padding: '0 24px', scrollbarWidth: 'none' }}>
          {times.map((t) => {
            const on = time === t;
            return (
              <button key={t} onClick={() => setTime(t)} style={{
                padding: '0 16px', height: 44, borderRadius: 14,
                background: on ? FT.orange : FT.raised,
                border: 'none', color: on ? '#FFF' : FT.text,
                fontFamily: FT.fontDisplay, fontVariantNumeric: 'tabular-nums',
                fontWeight: 400, fontSize: 14, cursor: 'pointer', flexShrink: 0,
                letterSpacing: '-0.02em',
                transition: `background 200ms ${FT_EASE}`
              }}>{t}</button>);

          })}
        </div>
      </div>

      <div style={{ padding: '12px 24px 24px', display: 'flex', gap: 10, flexShrink: 0 }}>
        <button onClick={onCancel} style={{
          padding: '14px 22px', background: 'transparent', border: 'none',
          color: FT.text2, fontFamily: FT.font, fontWeight: 400, fontSize: 14, cursor: 'pointer',
          letterSpacing: 0.01
        }}>Cancel</button>
        <div style={{ flex: 1 }}>
          <PrimaryButton onClick={onSave}>Save plan</PrimaryButton>
        </div>
      </div>
    </div>);

}
window.PlanWorkoutSheet = PlanWorkoutSheet;
window.PlanWorkoutPanel = PlanWorkoutSheet; // back-compat