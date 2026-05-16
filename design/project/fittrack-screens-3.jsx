// Log Workout (active), Post-Workout Summary, Stats, Settings, Profile menu.

// ─────────────────────────────────────────────────────────────
// LOG WORKOUT
// ─────────────────────────────────────────────────────────────
function LogWorkoutScreen({
  onExit, onNext,
  exerciseIdx = 1, showPB = false, restActive = false,
  kind = 'routine',
  treadmillPhase = 'timing',
  startElapsed = 0, startDistance = '',
}) {
  // Per-activity cardio config — timer → distance → auto-pace + calories.
  const CARDIO = {
    treadmill: { unit: 'km', kcalPerUnit: 62, presets: ['1.0', '2.5', '5.0', '10.0'], paceUnit: '/km', paceDivisor: 1, decimal: true, doneElapsed: 1860, doneDistance: '5.2' },
    swim:      { unit: 'm',  kcalPerUnit: 0.28, presets: ['500', '1000', '1500', '1800', '2000'], paceUnit: '/100m', paceDivisor: 100, decimal: false, doneElapsed: 2520, doneDistance: '1800' },
    basket:    { unit: 'km', kcalPerUnit: 80, presets: ['1.0', '2.0', '3.0', '5.0'], paceUnit: '/km', paceDivisor: 1, decimal: true, doneElapsed: 3720, doneDistance: '5.4' },
  };
  const isCardio = !!CARDIO[kind];
  const cfg = CARDIO[kind];

  const exercisesByKind = {
    swim:      [{ name: 'Warmup lap' }, { name: 'Freestyle swim sets' }, { name: 'Kick drill' }],
    treadmill: [{ name: 'Warmup walk' }, { name: 'Treadmill intervals' }, { name: 'Cooldown' }],
    basket:    [{ name: 'Pickup game' }, { name: 'Sprints' }, { name: 'Shootaround' }],
    routine:   [{ name: 'Pull-ups' }, { name: 'Push-ups' }, { name: 'Dumbbell rows' }, { name: 'Plank' }, { name: 'Jumping jacks' }, { name: 'Burpees' }],
  };
  const exercises = exercisesByKind[kind] || exercisesByKind.routine;
  const ex = exercises[exerciseIdx] || exercises[0];
  const total = exercises.length;
  const pct = (exerciseIdx + 1) / total * 100;

  const [phase, setPhase] = useState(treadmillPhase);
  const [elapsed, setElapsed] = useState(
    startElapsed || (treadmillPhase === 'done' ? (cfg ? cfg.doneElapsed : 0) : (isCardio ? 847 : 1421))
  );
  const [distance, setDistance] = useState(
    startDistance || (treadmillPhase === 'done' && cfg ? cfg.doneDistance : '')
  );
  const [distanceFocused, setDistanceFocused] = useState(false);
  const [pulseProgress, setPulseProgress] = useState(false);
  useEffect(() => {
    if (!isCardio || phase !== 'timing') return;
    const id = setInterval(() => setElapsed(e => e + 1), 1000);
    return () => clearInterval(id);
  }, [isCardio, phase]);

  const formatTime = (s) => {
    const h = Math.floor(s / 3600), m = Math.floor((s % 3600) / 60), ss = Math.floor(s % 60);
    const mm = m.toString().padStart(2, '0'), zss = ss.toString().padStart(2, '0');
    return h ? `${h}:${mm}:${zss}` : `${m}:${zss}`;
  };
  const dist = parseFloat(distance) || 0;
  const paceSec = isCardio && dist > 0 ? elapsed / (dist / cfg.paceDivisor) : 0;
  const paceStr = paceSec > 0 ? `${Math.floor(paceSec / 60)}:${Math.floor(paceSec % 60).toString().padStart(2,'0')}` : '—';
  const calories = isCardio && dist > 0 ? Math.round(dist * cfg.kcalPerUnit) : null;

  // Theme color — follows the activity, not always orange.
  const ACC = ACT[kind].color;

  const handleNext = () => {
    setPulseProgress(true);
    setTimeout(() => setPulseProgress(false), 220);
    onNext && onNext();
  };

  return (
    <div style={{ height: '100%', display: 'flex', flexDirection: 'column', background: FT.bg, position: 'relative', isolation: 'isolate' }}>
      {/* Flashlight overlay — soft activity-tinted glow from top-center */}
      <div aria-hidden="true" style={{
        position: 'absolute', inset: 0, pointerEvents: 'none', zIndex: 0,
        background: `radial-gradient(ellipse 280px 220px at 50% 0%, ${ACC}26 0%, transparent 70%)`,
      }} />
      {/* 2px progress bar — top of screen, animated width 300ms ease, pulse on completion */}
      <div style={{ height: 2, background: 'rgba(255,255,255,0.06)', position: 'relative', zIndex: 1, flexShrink: 0 }}>
        <div style={{
          width: `${pct}%`, height: '100%', background: ACC,
          transition: `width 300ms ${FT_EASE}`,
          transformOrigin: 'left center',
          animation: pulseProgress ? `ft-progress-pulse 220ms ${FT_EASE}` : 'none',
        }} />
      </div>

      {/* Close button */}
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', padding: '8px 16px 0 12px', flexShrink: 0, position: 'relative', zIndex: 1 }}>
        <button onClick={onExit} style={{ width: 40, height: 40, background: 'transparent', border: 'none', cursor: 'pointer', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
          <Icon name="close" size={20} color={FT.text2} />
        </button>
        <div style={{ width: 40 }} />
      </div>

      <div style={{ flex: 1, overflow: 'auto', padding: '0 24px 16px', display: 'flex', flexDirection: 'column', position: 'relative', zIndex: 1 }}>
        {/* PB banner — slides DOWN from top with 300ms ease-out */}
        {showPB && <PBBanner record="Plank · 1:38" delta="+12s vs previous" />}

        {/* Exercise N of M — centered, 11px Tiempos Text tertiary */}
        <div style={{
          marginTop: 16, textAlign: 'center',
          fontFamily: FT.font, fontSize: 11, fontWeight: 400,
          color: FT.text3, letterSpacing: '0.12em', textTransform: 'uppercase',
        }}>
          Exercise {exerciseIdx + 1} of {total}
        </div>

        {/* Big timer — 88px Tiempos Headline, −0.08em, tabular */}
        <div style={{
          textAlign: 'center', marginTop: 12,
          fontFamily: FT.fontDisplay, fontSize: 88, fontWeight: 400,
          letterSpacing: '-0.08em', color: FT.text,
          fontVariantNumeric: 'tabular-nums', lineHeight: 1,
        }}>
          {formatTime(elapsed)}
        </div>

        {/* Exercise name — 28px Fine italic centered */}
        <div style={{
          marginTop: 16, textAlign: 'center',
          fontFamily: FT.fontFine, fontStyle: 'italic', fontWeight: 400,
          fontSize: 28, letterSpacing: '-0.05em', color: FT.text,
          lineHeight: 1.1,
        }}>
          {ex.name}
        </div>

        {/* Body — cardio distance entry, rest timer, or controls */}
        <div style={{ marginTop: 32 }}>
          {isCardio && phase === 'done' ? (
            <CardioDistance
              cfg={cfg} kind={kind} acc={ACC}
              elapsed={elapsed} formatTime={formatTime}
              distance={distance} setDistance={setDistance}
              focused={distanceFocused} setFocused={setDistanceFocused}
              paceStr={paceStr} calories={calories} dist={dist}
            />
          ) : restActive ? (
            <RestTimer total={60} remaining={42} acc={ACC} />
          ) : (
            <CardioRunning
              isCardio={isCardio} acc={ACC}
              onStop={() => isCardio && setPhase('done')}
            />
          )}
        </div>

        <div style={{ flex: 1 }} />
      </div>

      {/* Bottom action */}
      <div style={{ padding: '0 24px 20px', flexShrink: 0, position: 'relative', zIndex: 1 }}>
        <PrimaryButton color={ACC} onClick={handleNext}>
          {exerciseIdx + 1 === total ? 'Finish' : 'Next exercise'}
        </PrimaryButton>
      </div>
    </div>
  );
}
window.LogWorkoutScreen = LogWorkoutScreen;

// PB banner — slides down from top, orange left bar, "New record" + value.
function PBBanner({ record = 'Plank · 1:38', delta }) {
  return (
    <div style={{
      marginTop: 16, padding: '14px 18px',
      background: 'rgba(255,107,53,0.06)',
      borderRadius: 20,
      display: 'flex', alignItems: 'center', gap: 14,
      overflow: 'hidden',
      animation: `ft-pb-slide 300ms ${FT_EASE}`,
    }}>
      <div style={{ width: 4, height: 32, borderRadius: 999, background: FT.orange, flexShrink: 0 }} />
      <div style={{ flex: 1, minWidth: 0 }}>
        <div style={{
          fontFamily: FT.font, fontSize: 11, fontWeight: 400, color: FT.orange,
          letterSpacing: '0.12em', textTransform: 'uppercase',
        }}>New record</div>
        <div style={{
          marginTop: 4,
          fontFamily: FT.fontFine, fontStyle: 'italic', fontWeight: 400,
          fontSize: 15, color: FT.text, letterSpacing: 0.01,
        }}>{record}</div>
      </div>
      {delta && (
        <div style={{ fontFamily: FT.font, fontSize: 11, fontWeight: 400, color: FT.text3, letterSpacing: 0.01 }}>{delta}</div>
      )}
    </div>
  );
}
window.PBBanner = PBBanner;

// Cardio: still timing — shows Stop button & "Tracking" pulse indicator
function CardioRunning({ isCardio, onStop, acc = FT.orange }) {
  return (
    <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 18 }}>
      <div style={{
        display: 'inline-flex', alignItems: 'center', gap: 8,
        fontFamily: FT.font, fontSize: 11, fontWeight: 400,
        color: FT.text3, letterSpacing: '0.12em', textTransform: 'uppercase',
      }}>
        <span style={{
          width: 6, height: 6, borderRadius: 3, background: acc,
          animation: 'ft-pulse-ring 1.4s ease-in-out infinite',
        }} />
        {isCardio ? 'Tracking' : 'In progress'}
      </div>
      {isCardio && (
        <button onClick={onStop} style={{
          padding: '14px 24px', background: 'transparent',
          border: `1px solid rgba(255,255,255,0.08)`, borderRadius: 16,
          color: FT.text, fontFamily: FT.font, fontWeight: 400, fontSize: 14,
          cursor: 'pointer', letterSpacing: 0.01,
          display: 'inline-flex', alignItems: 'center', gap: 8,
        }}>
          <span style={{ width: 10, height: 10, background: FT.text2, borderRadius: 1 }} />
          Stop timer
        </button>
      )}
    </div>
  );
}

// Cardio: phase=done — distance underline input + auto-pace + calories.
function CardioDistance({ cfg, kind, acc = FT.orange, elapsed, formatTime, distance, setDistance, focused, setFocused, paceStr, calories, dist }) {
  return (
    <div>
      {/* Tracked time, compact label */}
      <div style={{
        fontFamily: FT.font, fontSize: 10, fontWeight: 500,
        letterSpacing: '0.12em', textTransform: 'uppercase', color: FT.text3,
        textAlign: 'center', marginBottom: 6,
      }}>
        Tracked time · {formatTime(elapsed)}
      </div>

      <SectionLabel style={{ marginTop: 28, textAlign: 'center' }}>How far?</SectionLabel>

      {/* Distance input — 32px Headline, underline only */}
      <div style={{ maxWidth: 220, margin: '0 auto' }}>
        <div style={{ display: 'flex', alignItems: 'baseline', justifyContent: 'center', gap: 8 }}>
          <input
            value={distance}
            onChange={e => setDistance(e.target.value.replace(cfg.decimal ? /[^0-9.]/g : /[^0-9]/g, ''))}
            onFocus={() => setFocused(true)} onBlur={() => setFocused(false)}
            placeholder={cfg.decimal ? '0.0' : '0'}
            inputMode="decimal"
            style={{
              width: 130, minWidth: 0, textAlign: 'center',
              background: 'transparent', border: 'none', outline: 'none',
              color: FT.text, fontFamily: FT.fontDisplay, fontWeight: 400,
              fontSize: 32, letterSpacing: '-0.05em', fontVariantNumeric: 'tabular-nums',
              padding: '4px 0',
            }} />
          <div style={{ fontFamily: FT.font, fontSize: 14, color: FT.text3, fontWeight: 400, letterSpacing: 0.01 }}>{cfg.unit}</div>
        </div>
        <Underline focused={focused} color={acc} />
      </div>

      {/* Presets */}
      <div style={{ display: 'flex', gap: 8, justifyContent: 'center', flexWrap: 'wrap', marginTop: 16 }}>
        {cfg.presets.map(v => {
          const on = distance === v;
          return (
            <button key={v} onClick={() => setDistance(v)} style={{
              padding: '8px 14px', borderRadius: 14,
              background: on ? acc : FT.raised,
              border: 'none', color: on ? '#FFF' : FT.text2,
              fontFamily: FT.font, fontVariantNumeric: 'tabular-nums', fontWeight: 400, fontSize: 12,
              cursor: 'pointer', letterSpacing: 0.01,
              transition: `background 200ms ${FT_EASE}`,
            }}>{v}</button>
          );
        })}
      </div>

      {/* Auto-calculated stats */}
      <div style={{
        marginTop: 32, display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 24,
        textAlign: 'center',
      }}>
        <CalcTile label="Pace" unit={cfg.paceUnit} value={paceStr} dim={!dist} />
        <CalcTile label="Calories" unit="kcal" value={dist ? calories : '—'} dim={!dist} />
      </div>
    </div>
  );
}

function CalcTile({ label, unit, value, dim }) {
  return (
    <div>
      <div style={{
        fontFamily: FT.font, fontSize: 10, fontWeight: 500,
        letterSpacing: '0.12em', textTransform: 'uppercase', color: FT.text3,
      }}>{label}</div>
      <div style={{ marginTop: 8, display: 'flex', alignItems: 'baseline', justifyContent: 'center', gap: 4 }}>
        <div style={{
          fontFamily: FT.fontFine, fontStyle: 'italic', fontWeight: 400,
          fontSize: 24, fontVariantNumeric: 'tabular-nums',
          color: dim ? FT.text3 : FT.text, letterSpacing: '-0.02em',
        }}>{value}</div>
        <div style={{ fontFamily: FT.font, fontSize: 11, color: FT.text3, fontWeight: 400, letterSpacing: 0.01 }}>{unit}</div>
      </div>
    </div>
  );
}

// Circular rest timer ring — 120px diameter, orange stroke.
function RestTimer({ total = 60, remaining = 42, acc = FT.orange }) {
  const size = 120;
  const stroke = 4;
  const r = (size - stroke) / 2;
  const c = 2 * Math.PI * r;
  const pct = (total - remaining) / total;
  return (
    <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 14 }}>
      <SectionLabel style={{ marginTop: 0 }}>Rest</SectionLabel>
      <div style={{ position: 'relative', width: size, height: size }}>
        <svg width={size} height={size} style={{ transform: 'rotate(-90deg)' }}>
          <circle cx={size/2} cy={size/2} r={r} fill="none" stroke="rgba(255,255,255,0.06)" strokeWidth={stroke} />
          <circle cx={size/2} cy={size/2} r={r} fill="none" stroke={acc} strokeWidth={stroke}
            strokeLinecap="round" strokeDasharray={c} strokeDashoffset={c * (1 - pct)}
            style={{ transition: `stroke-dashoffset 300ms ${FT_EASE}` }} />
        </svg>
        <div style={{
          position: 'absolute', inset: 0, display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center',
        }}>
          <div style={{
            fontFamily: FT.fontDisplay, fontSize: 28, fontWeight: 400,
            letterSpacing: '-0.05em', color: FT.text, fontVariantNumeric: 'tabular-nums',
            lineHeight: 1,
          }}>
            0:{remaining.toString().padStart(2,'0')}
          </div>
          <div style={{ fontFamily: FT.font, fontSize: 10, fontWeight: 400, color: FT.text3, letterSpacing: '0.12em', textTransform: 'uppercase', marginTop: 6 }}>
            of 1:00
          </div>
        </div>
      </div>
      <button style={{
        padding: '10px 18px', background: 'transparent', border: `1px solid rgba(255,255,255,0.08)`,
        borderRadius: 14, color: FT.text2, fontFamily: FT.font, fontWeight: 400, fontSize: 13,
        cursor: 'pointer', letterSpacing: 0.01,
      }}>+ 30s</button>
    </div>
  );
}

// ─────────────────────────────────────────────────────────────
// POST-WORKOUT SUMMARY
// ─────────────────────────────────────────────────────────────
function SummaryScreen({ onDone }) {
  // Per spec: Upper Body, 38 min, 6 exercises, 210 kcal
  const stats = [
    { label: 'Time',      value: '38',   unit: 'min' },
    { label: 'Exercises', value: '6',    unit: 'done' },
    { label: 'Calories',  value: '210',  unit: 'kcal' },
    { label: 'Volume',    value: '4.2k', unit: 'kg' },
  ];

  return (
    <div style={{ height: '100%', display: 'flex', flexDirection: 'column', position: 'relative', isolation: 'isolate' }}>
      {/* Flashlight overlay — orange glow (summary follows the routine kind) */}
      <div aria-hidden="true" style={{
        position: 'absolute', inset: 0, pointerEvents: 'none', zIndex: 0,
        background: `radial-gradient(ellipse 280px 220px at 50% 0%, rgba(255,107,53,0.09) 0%, transparent 70%)`,
      }} />
      <div style={{ flex: 1, overflow: 'auto', padding: '40px 24px 16px', position: 'relative', zIndex: 1 }}>
        {/* "Workout complete." — Fine italic 36px, period. */}
        <div style={{ textAlign: 'center' }}>
          <div style={{
            fontFamily: FT.fontFine, fontStyle: 'italic', fontWeight: 400,
            fontSize: 36, letterSpacing: '-0.05em', color: FT.text,
            lineHeight: 1.05,
          }}>
            Workout complete.
          </div>
          <div style={{
            marginTop: 12, display: 'inline-flex', alignItems: 'center', gap: 10,
            fontFamily: FT.font, fontSize: 13, fontWeight: 400,
            color: FT.text2, letterSpacing: 0.01,
          }}>
            <ActivityDot kind="routine" />
            <span>Upper Body</span>
            <span style={{ color: FT.text3 }}>· May 15, 7:08 AM</span>
          </div>
        </div>

        {/* 2×2 stats grid — Headline 48px + 11px label */}
        <div style={{
          marginTop: 40, display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 32,
          textAlign: 'left',
        }}>
          {stats.map((s, i) => (
            <div key={i}>
              <div style={{
                fontFamily: FT.fontDisplay, fontSize: 48, fontWeight: 400,
                letterSpacing: '-0.05em', color: FT.text,
                fontVariantNumeric: 'tabular-nums', lineHeight: 1,
              }}>{s.value}</div>
              <div style={{
                marginTop: 8,
                fontFamily: FT.font, fontSize: 11, fontWeight: 400,
                color: FT.text3, letterSpacing: '0.12em', textTransform: 'uppercase',
              }}>
                {s.label} <span style={{ marginLeft: 4 }}>{s.unit}</span>
              </div>
            </div>
          ))}
        </div>

        {/* PB callout — left orange bar */}
        <div style={{
          marginTop: 40, padding: '14px 18px',
          display: 'flex', alignItems: 'center', gap: 14,
          background: 'rgba(255,107,53,0.06)', borderRadius: 20,
        }}>
          <div style={{ width: 4, height: 36, borderRadius: 999, background: FT.orange, flexShrink: 0 }} />
          <div style={{ flex: 1 }}>
            <div style={{
              fontFamily: FT.font, fontSize: 11, fontWeight: 400, color: FT.orange,
              letterSpacing: '0.12em', textTransform: 'uppercase',
            }}>New record</div>
            <div style={{
              marginTop: 4,
              fontFamily: FT.fontFine, fontStyle: 'italic', fontWeight: 400,
              fontSize: 15, color: FT.text, letterSpacing: 0.01,
            }}>Pull-ups · 12 reps</div>
          </div>
        </div>

        {/* Notes — underline-only textarea */}
        <SectionLabel>Notes</SectionLabel>
        <textarea
          defaultValue="Felt strong on pull-ups. Form held through the last set."
          style={{
            width: '100%', boxSizing: 'border-box', minHeight: 60,
            background: 'transparent', border: 'none', borderBottom: '1px solid rgba(255,255,255,0.08)',
            padding: '6px 0', color: FT.text,
            fontFamily: FT.font, fontWeight: 400, fontSize: 14,
            outline: 'none', resize: 'none', lineHeight: 1.5, letterSpacing: 0.01,
          }} />
      </div>

      <div style={{ padding: '0 24px 20px', position: 'relative', zIndex: 1 }}>
        <PrimaryButton onClick={onDone}>Done</PrimaryButton>
      </div>
    </div>
  );
}
window.SummaryScreen = SummaryScreen;

// ─────────────────────────────────────────────────────────────
// STATS / HISTORY
// ─────────────────────────────────────────────────────────────
function StatsScreen({ chartMode = 'line' }) {
  const [chart, setChart] = useState(chartMode);
  const loading = useLoading(1200);

  // Realistic 8-week heatmap — Monday-start. 3–4×/week with occasional gaps.
  // Levels 0–4: 0=rest, 1–4 increasing.
  // Pattern reads M-T-W-T-F-S-S per column (week).
  const heat = [
    // older weeks → recent
    [2,0,3,0,2,1,0],
    [3,0,2,0,3,0,0],
    [0,2,0,3,0,4,1],
    [3,0,2,2,0,3,0],
    [2,0,3,0,2,0,0],
    [4,0,2,0,3,2,0],
    [0,3,0,2,0,4,0],
    [3,2,0,3,1,0,0],
  ];
  const heatColor = (lvl) => {
    if (lvl === 0) return FT.surface;
    if (lvl === 1) return 'rgba(255,107,53,0.15)';
    if (lvl === 2) return 'rgba(255,107,53,0.35)';
    if (lvl === 3) return 'rgba(255,107,53,0.60)';
    return 'rgba(255,107,53,1.0)';
  };

  const pbs = [
    { kind: 'swim',      label: 'Swimming',  detail: '2.1 km · 44 min', date: 'May 10' },
    { kind: 'treadmill', label: 'Treadmill', detail: '6.0 km · 35 min', date: 'May 8' },
    { kind: 'basket',    label: 'Basketball', detail: '5.4 km · 62 min', date: 'May 3' },
  ];

  const sessions = [
    { k: 'routine',   n: 'Upper Body',     d: 'May 14', t: '38 min' },
    { k: 'swim',      n: 'Swim Intervals', d: 'May 13', t: '42 min' },
    { k: 'treadmill', n: 'Morning Run',    d: 'May 12', t: '31 min' },
    { k: 'routine',   n: 'Core & Stretch', d: 'May 10', t: '24 min' },
  ];

  return (
    <Screen>
      <div style={{ marginTop: 8 }}>
        <div style={{
          fontFamily: FT.fontDisplay, fontSize: 28, fontWeight: 400,
          letterSpacing: '-0.05em', color: FT.text, lineHeight: 1.1,
        }}>
          History
        </div>
      </div>

      {/* Heatmap — orange intensity only, 10px cells, 3px gap */}
      <SectionLabel>Last 8 weeks</SectionLabel>
      <Card>
        <div style={{ display: 'flex', gap: 3 }}>
          {heat.map((col, ci) => (
            <div key={ci} style={{ display: 'flex', flexDirection: 'column', gap: 2, flex: 1 }}>
              {col.map((lvl, di) => (
                <div key={di} title={lvl > 0 ? `Week ${ci+1} · ${['Mon','Tue','Wed','Thu','Fri','Sat','Sun'][di]} · ${[0,15,35,55,80][lvl]} min` : 'Rest'}
                  style={{
                    width: '100%', height: 7, borderRadius: 3,
                    background: heatColor(lvl),
                    transition: `background 150ms ${FT_EASE}`,
                  }} />
              ))}
            </div>
          ))}
        </div>
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'flex-end', gap: 5, marginTop: 14 }}>
          <span style={{ fontFamily: FT.font, fontSize: 10, color: FT.text3, letterSpacing: 0.04 }}>Less</span>
          {[0,1,2,3,4].map(l => (
            <div key={l} style={{ width: 7, height: 7, borderRadius: 3, background: heatColor(l) }} />
          ))}
          <span style={{ fontFamily: FT.font, fontSize: 10, color: FT.text3, letterSpacing: 0.04 }}>More</span>
        </div>
      </Card>

      {/* Chart toggle */}
      <div style={{
        display: 'flex', background: FT.raised, borderRadius: 14, padding: 4, marginTop: 28,
      }}>
        {[['line', 'Duration'], ['bar', 'Volume']].map(([k, lbl]) => {
          const on = chart === k;
          return (
            <button key={k} onClick={() => setChart(k)} style={{
              flex: 1, padding: '10px 0', background: on ? FT.surface : 'transparent', border: 'none',
              borderRadius: 12, color: on ? FT.text : FT.text2,
              fontFamily: FT.font, fontWeight: 400, fontSize: 13, cursor: 'pointer', letterSpacing: 0.01,
              transition: `background 200ms ${FT_EASE}, color 200ms ${FT_EASE}`,
            }}>{lbl}</button>
          );
        })}
      </div>
      <div style={{ marginTop: 16 }}>
        {chart === 'line' ? <LineChartCard /> : <BarChartCard />}
      </div>

      {/* Personal bests */}
      <SectionLabel>Personal bests</SectionLabel>
      <div style={{ display: 'flex', flexDirection: 'column', gap: 0 }}>
        {pbs.map((p, i) => (
          <div key={i} style={{
            display: 'flex', alignItems: 'center', gap: 12,
            padding: '12px 4px',
          }}>
            <ActivityChip kind={p.kind} size={24} />
            <div style={{ flex: 1, minWidth: 0 }}>
              <div style={{ fontFamily: FT.font, fontSize: 13, fontWeight: 400, color: FT.text, letterSpacing: 0.01 }}>{p.label}</div>
            </div>
            <div style={{
              fontFamily: FT.fontFine, fontStyle: 'italic', fontWeight: 400,
              fontSize: 14, color: FT.text, letterSpacing: 0.01,
            }}>{p.detail}</div>
            <div style={{ fontFamily: FT.font, fontSize: 11, fontWeight: 400, color: FT.text3, letterSpacing: 0.01, marginLeft: 10, minWidth: 50, textAlign: 'right' }}>{p.date}</div>
          </div>
        ))}
      </div>

      {/* Past sessions */}
      <SectionLabel>Past sessions</SectionLabel>
      <div style={{ display: 'flex', flexDirection: 'column' }}>
        {loading
          ? [0,1,2,3].map(i => (
              <div key={i} style={{ display: 'flex', alignItems: 'center', gap: 12, padding: '12px 4px' }}>
                <Skeleton w={24} h={24} r={12} />
                <Skeleton w="50%" h={12} r={6} />
                <div style={{ flex: 1 }} />
                <Skeleton w={40} h={10} r={5} />
                <Skeleton w={42} h={12} r={6} />
              </div>
            ))
          : sessions.map((r, i) => (
              <div key={i} style={{ display: 'flex', alignItems: 'center', gap: 12, padding: '12px 4px' }}>
                <ActivityChip kind={r.k} size={24} />
                <div style={{ flex: 1, fontFamily: FT.font, fontSize: 14, fontWeight: 400, color: FT.text, letterSpacing: 0.01 }}>{r.n}</div>
                <div style={{ fontFamily: FT.font, fontSize: 12, color: FT.text3, letterSpacing: 0.01 }}>{r.d}</div>
                <div style={{
                  fontFamily: FT.fontFine, fontStyle: 'italic', fontWeight: 400,
                  fontSize: 14, color: FT.text, fontVariantNumeric: 'tabular-nums',
                  minWidth: 56, textAlign: 'right', letterSpacing: 0.01,
                }}>{r.t}</div>
              </div>
            ))
        }
      </div>
    </Screen>
  );
}
window.StatsScreen = StatsScreen;

function LineChartCard() {
  const W = 296, H = 160;
  const topPad = 24, bottomPad = 32, leftPad = 24, rightPad = 24;
  const series = [
    { k: 'swim',      pts: [28, 32, 30, 38, 35, 42, 40, 44] },
    { k: 'treadmill', pts: [25, 28, 26, 31, 30, 29, 33, 31] },
    { k: 'routine',   pts: [22, 24, 28, 25, 32, 30, 36, 38] },
  ];
  const max = 50;
  const n = series[0].pts.length;
  const x = (i) => leftPad + i / (n - 1) * (W - leftPad - rightPad);
  const y = (v) => (H - bottomPad) - v / max * (H - topPad - bottomPad);

  return (
    <div>
      <svg width="100%" viewBox={`0 0 ${W} ${H}`} style={{ display: 'block' }}>
        {/* gridlines */}
        {[0, 10, 20, 30, 40, 50].map(v => (
          <g key={v}>
            <line x1={leftPad} x2={W - rightPad} y1={y(v)} y2={y(v)} stroke="rgba(255,255,255,0.04)" strokeWidth="1" />
            <text x={2} y={y(v) + 3} fill={FT.text3} fontSize="8" fontFamily={FT.font}>{v}</text>
          </g>
        ))}
        {/* lines — 1.5px, no fill */}
        {series.map(s => {
          const d = s.pts.map((v, i) => `${i === 0 ? 'M' : 'L'} ${x(i)} ${y(v)}`).join(' ');
          return (
            <path key={s.k} d={d} fill="none" stroke={ACT[s.k].color} strokeWidth="1.5"
              strokeLinecap="round" strokeLinejoin="round" />
          );
        })}
        {/* End-of-line activity labels */}
        {series.map(s => {
          const last = s.pts[s.pts.length - 1];
          return (
            <text key={s.k} x={x(n - 1) + 4} y={y(last)} fill={ACT[s.k].color}
              fontSize="8" fontFamily={FT.font} dominantBaseline="middle" textAnchor="start">
              {ACT[s.k].label}
            </text>
          );
        })}
        {/* Week labels W1..W8 */}
        {Array.from({ length: n }, (_, i) => (
          <text key={i} x={x(i)} y={H - 4} fill={FT.text3} fontSize="8"
            fontFamily={FT.font} textAnchor="middle">W{i + 1}</text>
        ))}
      </svg>
      <div style={{ display: 'flex', gap: 16, marginTop: 10, flexWrap: 'wrap' }}>
        {series.map(s => (
          <div key={s.k} style={{ display: 'flex', alignItems: 'center', gap: 6, fontFamily: FT.font, fontSize: 11, color: FT.text2, letterSpacing: 0.01 }}>
            <div style={{ width: 12, height: 1.5, background: ACT[s.k].color, borderRadius: 1 }} />
            {ACT[s.k].label}
          </div>
        ))}
      </div>
    </div>
  );
}

function BarChartCard() {
  const weeks = ['W1','W2','W3','W4','W5','W6','W7','W8'];
  const data = [
    { treadmill: 30, swim: 20, basket: 0,  routine: 25 },
    { treadmill: 25, swim: 30, basket: 0,  routine: 30 },
    { treadmill: 0,  swim: 40, basket: 20, routine: 25 },
    { treadmill: 35, swim: 30, basket: 0,  routine: 30 },
    { treadmill: 30, swim: 0,  basket: 25, routine: 35 },
    { treadmill: 40, swim: 45, basket: 0,  routine: 30 },
    { treadmill: 30, swim: 35, basket: 25, routine: 40 },
    { treadmill: 35, swim: 42, basket: 0,  routine: 38 },
  ];
  const W = 296, H = 160, P = 24;
  const max = 150;
  // Stack order from bottom → top: routine, swim, treadmill, basket
  const stack = ['routine', 'swim', 'treadmill', 'basket'];
  const bw = (W - P * 2) / weeks.length;

  // Compute per-bar geometry + gradient stops
  const bars = data.map((wk, i) => {
    const cx = P + i * bw + bw / 2;
    const segs = stack
      .map(k => ({ k, color: ACT[k].color, h: (wk[k] / max) * (H - P * 2) }))
      .filter(s => s.h > 0);
    if (segs.length === 0) return { i, cx, empty: true };
    const totalH = segs.reduce((s, x) => s + x.h, 0);
    const yBottom = H - P;
    const yTop = yBottom - totalH;
    const stops = [];
    let acc = 0;
    segs.forEach((seg, idx) => {
      const startFrac = acc / totalH;
      acc += seg.h;
      const endFrac = acc / totalH;
      // start-of-segment stop (overlap by 0.03 after a boundary)
      stops.push({ offset: idx === 0 ? 0 : Math.min(1, startFrac + 0.03), color: seg.color });
      // end-of-segment stop (overlap by 0.03 before next boundary)
      stops.push({ offset: idx === segs.length - 1 ? 1 : Math.max(0, endFrac - 0.03), color: seg.color });
    });
    return { i, cx, yBottom, yTop, totalH, stops, empty: false };
  });

  return (
    <div>
      <svg width="100%" viewBox={`0 0 ${W} ${H}`} style={{ display: 'block' }}>
        <defs>
          {bars.filter(b => !b.empty).map(b => (
            <linearGradient key={b.i} id={`grad-${b.i}`} gradientUnits="userSpaceOnUse"
              x1={b.cx} y1={b.yBottom} x2={b.cx} y2={b.yTop}>
              {b.stops.map((s, j) => (
                <stop key={j} offset={s.offset} stopColor={s.color} />
              ))}
            </linearGradient>
          ))}
        </defs>
        {/* gridlines */}
        {[0, 50, 100, 150].map(v => (
          <g key={v}>
            <line x1={P} x2={W - P} y1={H - P - v / max * (H - P * 2)} y2={H - P - v / max * (H - P * 2)} stroke="rgba(255,255,255,0.04)" strokeWidth="1" />
            <text x={2} y={H - P - v / max * (H - P * 2) + 3} fill={FT.text3} fontSize="8" fontFamily={FT.font}>{v}</text>
          </g>
        ))}
        {/* bars */}
        {bars.map(b => (
          <g key={b.i}>
            {!b.empty && (
              <rect x={b.cx - 9} y={b.yTop} width={18} height={b.totalH} rx={2} fill={`url(#grad-${b.i})`} />
            )}
            <text x={b.cx} y={H - 6} fill={FT.text3} fontSize="8" textAnchor="middle" fontFamily={FT.font}>{weeks[b.i]}</text>
          </g>
        ))}
      </svg>
      <div style={{ display: 'flex', gap: 14, marginTop: 10, flexWrap: 'wrap' }}>
        {stack.map(k => (
          <div key={k} style={{ display: 'flex', alignItems: 'center', gap: 6, fontFamily: FT.font, fontSize: 11, color: FT.text2, letterSpacing: 0.01 }}>
            <div style={{ width: 8, height: 8, background: ACT[k].color, borderRadius: 2 }} />
            {ACT[k].label}
          </div>
        ))}
      </div>
    </div>
  );
}

// ─────────────────────────────────────────────────────────────
// SETTINGS
// ─────────────────────────────────────────────────────────────
// No section title that says "Settings" — page starts with the first group.
function SettingsScreen({ onBack, name = 'Nguyen', pic = null, onEditProfile }) {
  const [units, setUnits] = useState('metric');
  const [notif, setNotif] = useState(true);
  const [reminders, setReminders] = useState(true);
  const [clearConfirm, setClearConfirm] = useState(false);

  return (
    <div style={{ height: '100%', display: 'flex', flexDirection: 'column', position: 'relative' }}>
      {/* Top bar — just back + page-tag label */}
      <div style={{ display: 'flex', alignItems: 'center', padding: '8px 16px 0 12px' }}>
        <button onClick={onBack} style={{ width: 40, height: 40, background: 'transparent', border: 'none', cursor: 'pointer', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
          <Icon name="arrowL" size={20} color={FT.text} />
        </button>
      </div>

      <div style={{ flex: 1, overflow: 'auto', padding: '12px 24px 32px' }}>
        {/* Profile card — borderless tap row */}
        <div onClick={onEditProfile} style={{
          display: 'flex', alignItems: 'center', gap: 14,
          padding: '8px 0', cursor: 'pointer',
        }}>
          <Avatar pic={pic} name={name} size={56} />
          <div style={{ flex: 1, minWidth: 0 }}>
            <div style={{
              fontFamily: FT.fontFine, fontStyle: 'italic', fontWeight: 400,
              fontSize: 22, letterSpacing: '-0.02em', color: FT.text, lineHeight: 1.1,
            }}>{name}</div>
            <div style={{
              fontFamily: FT.font, fontSize: 12, fontWeight: 400, color: FT.text2,
              marginTop: 4, letterSpacing: 0.01,
            }}>Joined March 2026</div>
          </div>
          <Icon name="edit" size={14} color={FT.text3} />
        </div>

        <SGroupLabel>Preferences</SGroupLabel>
        <SRow label="Units" rightControl={<Segmented value={units} onChange={setUnits} options={[['metric','Metric'],['imperial','Imperial']]} />} />
        <SRow label="Workout reminders" rightControl={<Toggle on={reminders} onChange={() => setReminders(!reminders)} />} />
        <SRow label="Notifications" rightControl={<Toggle on={notif} onChange={() => setNotif(!notif)} />} />

        <SGroupLabel>Data</SGroupLabel>
        <SRow label="Export data" value="CSV" onClick={() => {}} />
        <SRow label="Clear history" onClick={() => setClearConfirm(true)} />

        <SGroupLabel>About</SGroupLabel>
        <SRow label="Version" value="1.0.0" />
        <SRow label="View app on GitHub" onClick={() => {}} />

        <SGroupLabel>Connect</SGroupLabel>
        <SocialsGrid />
      </div>

      {clearConfirm && (
        <ClearHistorySheet onClose={() => setClearConfirm(false)} />
      )}
    </div>
  );
}
window.SettingsScreen = SettingsScreen;

// Confirmation sheet — slides up, ft-sheet-down close. Muted red destructive CTA.
function ClearHistorySheet({ onClose }) {
  const [closing, setClosing] = useState(false);
  const [flashing, setFlashing] = useState(false);
  const dismiss = () => {
    if (closing) return;
    setClosing(true);
    setTimeout(() => onClose && onClose(), 360);
  };
  const confirm = () => {
    setFlashing(true);
    setTimeout(() => { setFlashing(false); dismiss(); }, 120);
  };
  return (
    <div onClick={dismiss} style={{
      position: 'absolute', inset: 0, zIndex: 30,
      background: 'rgba(0,0,0,0.55)',
      backdropFilter: 'blur(6px)', WebkitBackdropFilter: 'blur(6px)',
      display: 'flex', flexDirection: 'column', justifyContent: 'flex-end',
      animation: closing
        ? `ft-fade-out 360ms ${FT_EASE} forwards`
        : `ft-fade-in 200ms ${FT_EASE}`,
    }}>
      <div onClick={e => e.stopPropagation()} style={{
        background: FT.surface,
        borderTopLeftRadius: 28, borderTopRightRadius: 28,
        paddingBottom: 4,
        animation: closing
          ? `ft-sheet-down 360ms ${FT_EASE} forwards`
          : `ft-sheet-up 400ms ${FT_EASE}`,
      }}>
        <div style={{ width: 36, height: 4, background: 'rgba(255,255,255,0.10)', borderRadius: 2, margin: '12px auto 8px' }} />
        <div style={{
          padding: '12px 24px 0',
          fontFamily: FT.fontDisplay, fontSize: 22, fontWeight: 400,
          letterSpacing: '-0.05em', color: FT.text, lineHeight: 1.15,
        }}>
          Clear all history
        </div>
        <div style={{
          padding: '10px 24px 24px',
          fontFamily: FT.font, fontSize: 14, fontWeight: 400,
          color: FT.text2, lineHeight: 1.5, letterSpacing: 0.01,
        }}>
          This removes all past sessions and personal bests. It cannot be undone.
        </div>
        <div style={{ display: 'flex', gap: 10, padding: '0 24px 28px' }}>
          <button onClick={dismiss} style={{
            padding: '14px 22px', background: 'transparent', border: 'none',
            color: FT.text2, fontFamily: FT.font, fontWeight: 400, fontSize: 14,
            cursor: 'pointer', letterSpacing: 0.01,
          }}>Cancel</button>
          <div style={{ flex: 1 }}>
            <PrimaryButton color={flashing ? FT.surface : '#C0392B'} onClick={confirm}>
              Clear history
            </PrimaryButton>
          </div>
        </div>
      </div>
    </div>
  );
}
window.ClearHistorySheet = ClearHistorySheet;

// 10px Tiempos Text, wide tracking +0.12em, #6E6E73, uppercase.
// 24px top margin, no bottom margin overrides — sits above its group.
function SGroupLabel({ children }) {
  return (
    <div style={{
      fontFamily: FT.font, fontSize: 10, fontWeight: 500,
      letterSpacing: '0.12em', textTransform: 'uppercase',
      color: FT.text3, marginTop: 24, marginBottom: 8,
    }}>{children}</div>
  );
}

// 52px tall, 15px label, 13px secondary value right-aligned. No separators.
function SRow({ label, value, rightControl, onClick }) {
  const press = usePress();
  const interactive = !!onClick;
  return (
    <div onClick={onClick} {...(interactive ? press.bind : {})} style={{
      height: 52, display: 'flex', alignItems: 'center', gap: 12,
      cursor: interactive ? 'pointer' : 'default',
      transform: interactive && press.pressed ? 'scale(0.99)' : 'scale(1)',
      transition: `transform 120ms ease-in-out`,
    }}>
      <div style={{
        flex: 1, fontFamily: FT.font, fontSize: 15, fontWeight: 400,
        color: FT.text, letterSpacing: 0.01,
      }}>{label}</div>
      {rightControl
        ? rightControl
        : value
          ? <div style={{
              fontFamily: FT.font, fontSize: 13, fontWeight: 400,
              color: FT.text2, letterSpacing: 0.01,
            }}>{value}</div>
          : onClick && <Icon name="arrow" size={14} color={FT.text3} />}
    </div>
  );
}

// Toggle — knob animates with 200ms ease.
function Toggle({ on, onChange }) {
  return (
    <button onClick={onChange} style={{
      width: 44, height: 26, borderRadius: 13,
      background: on ? FT.orange : FT.raised,
      border: 'none', position: 'relative', cursor: 'pointer',
      transition: `background 200ms ${FT_EASE}`,
      padding: 0,
    }}>
      <div style={{
        position: 'absolute', top: 3, left: on ? 21 : 3,
        width: 20, height: 20, borderRadius: 10, background: '#FFF',
        transition: `left 200ms ${FT_EASE}`,
        boxShadow: '0 1px 3px rgba(0,0,0,0.4)',
      }} />
    </button>
  );
}
window.Toggle = Toggle;

function Segmented({ value, onChange, options }) {
  const activeIdx = Math.max(0, options.findIndex(([k]) => k === value));
  const n = options.length || 1;
  return (
    <div style={{ display: 'flex', background: FT.raised, borderRadius: 10, padding: 3, position: 'relative' }}>
      <div style={{
        position: 'absolute',
        top: 3, bottom: 3,
        left: `calc(3px + ${activeIdx} * ((100% - 6px) / ${n}))`,
        width: `calc((100% - 6px) / ${n})`,
        background: FT.orange,
        borderRadius: 8,
        transition: `left 250ms ${FT_EASE}`,
        pointerEvents: 'none',
      }} />
      {options.map(([k, lbl]) => {
        const on = value === k;
        return (
          <button key={k} onClick={() => onChange(k)} style={{
            flex: 1,
            padding: '6px 12px', background: 'transparent', border: 'none',
            borderRadius: 8, color: on ? '#FFF' : FT.text2,
            fontFamily: FT.font, fontWeight: 400, fontSize: 12,
            cursor: 'pointer', letterSpacing: 0.01,
            transition: `color 250ms ${FT_EASE}`,
            position: 'relative', zIndex: 1,
          }}>{lbl}</button>
        );
      })}
    </div>
  );
}
window.Segmented = Segmented;

// Socials grid — 4 icons, 48px each, rgba(255,255,255,0.06) bg, 14dp radius. No label.
function SocialsGrid() {
  return (
    <div style={{ display: 'flex', gap: 10 }}>
      {SOCIALS.map(s => {
        const disabled = !s.url;
        const Tag = disabled ? 'div' : 'a';
        return (
          <Tag key={s.k}
            {...(disabled ? {} : { href: s.url, target: '_blank', rel: 'noreferrer' })}
            style={{
              width: 48, height: 48, borderRadius: 14,
              background: 'rgba(255,255,255,0.06)',
              display: 'flex', alignItems: 'center', justifyContent: 'center',
              textDecoration: 'none', cursor: disabled ? 'default' : 'pointer',
              opacity: disabled ? 0.4 : 1, flexShrink: 0,
              transition: `background 200ms ${FT_EASE}`,
            }}>
            {s.image
              ? <img src={s.image} alt={s.label} style={{ width: 20, height: 20, objectFit: 'contain', display: 'block' }} />
              : <Icon name={s.k} size={20} color={FT.text} stroke={1.6} />}
          </Tag>
        );
      })}
    </div>
  );
}
window.SocialsGrid = SocialsGrid;

// ─────────────────────────────────────────────────────────────
// PROFILE MENU — bottom sheet, 80% height.
// ─────────────────────────────────────────────────────────────
function ProfileMenu({ name = 'Nguyen', pic = null, use24h = false, onUse24h, onClose, onEditProfile, onOpenSettings }) {
  const [closing, setClosing] = useState(false);
  // Delay unmount so the close keyframes can play to completion.
  const dismiss = (after) => {
    if (closing) return;
    setClosing(true);
    setTimeout(() => {
      if (after) after();
      else onClose && onClose();
    }, 360);
  };
  return (
    <div onClick={() => dismiss()} style={{
      position: 'absolute', inset: 0, zIndex: 30,
      background: 'rgba(0,0,0,0.55)',
      backdropFilter: 'blur(6px)', WebkitBackdropFilter: 'blur(6px)',
      display: 'flex', flexDirection: 'column', justifyContent: 'flex-end',
      animation: closing
        ? `ft-fade-out 360ms ${FT_EASE} forwards`
        : `ft-fade-in 200ms ${FT_EASE}`,
    }}>
      <div onClick={e => e.stopPropagation()} style={{
        background: FT.surface,
        borderTopLeftRadius: 28, borderTopRightRadius: 28,
        padding: '14px 24px 28px',
        animation: closing
          ? `ft-sheet-down 360ms ${FT_EASE} forwards`
          : `ft-sheet-up 400ms ${FT_EASE}`,
      }}>
        <div style={{ width: 36, height: 4, background: 'rgba(255,255,255,0.10)', borderRadius: 2, margin: '0 auto 18px' }} />
        <div style={{ display: 'flex', alignItems: 'center', gap: 14, marginBottom: 8 }}>
          <Avatar pic={pic} name={name} size={56} />
          <div style={{ flex: 1, minWidth: 0 }}>
            <div style={{
              fontFamily: FT.fontFine, fontStyle: 'italic', fontWeight: 400,
              fontSize: 22, letterSpacing: '-0.02em', color: FT.text, lineHeight: 1.1,
            }}>{name}</div>
            <div style={{ fontFamily: FT.font, fontSize: 12, color: FT.text2, marginTop: 4, letterSpacing: 0.01 }}>12 consecutive days</div>
          </div>
        </div>

        <SGroupLabel>Account</SGroupLabel>
        <SRow label="Edit profile" onClick={() => dismiss(onEditProfile)} />
        <SRow label="24-hour clock" rightControl={<Toggle on={use24h} onChange={onUse24h} />} />
        <SRow label="Settings" onClick={() => dismiss(onOpenSettings)} />
      </div>
    </div>
  );
}
window.ProfileMenu = ProfileMenu;

// ─────────────────────────────────────────────────────────────
// EDIT PROFILE
// ─────────────────────────────────────────────────────────────
function EditProfileScreen({ name: initialName = 'Nguyen', pic: initialPic = null, onBack, onSave }) {
  const [name, setName] = useState(initialName);
  const [pic, setPic] = useState(initialPic);
  const [focused, setFocused] = useState(false);
  const fileRef = useRef(null);
  const onFile = (e) => {
    const f = e.target.files && e.target.files[0]; if (!f) return;
    const r = new FileReader();
    r.onload = () => setPic(r.result);
    r.readAsDataURL(f);
  };
  return (
    <div style={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', padding: '8px 16px 0 12px' }}>
        <button onClick={onBack} style={{ width: 40, height: 40, background: 'transparent', border: 'none', cursor: 'pointer', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
          <Icon name="arrowL" size={20} color={FT.text} />
        </button>
        <div style={{ fontFamily: FT.font, fontSize: 14, fontWeight: 400, color: FT.text2, letterSpacing: 0.04, textTransform: 'uppercase' }}>Edit profile</div>
        <button onClick={() => onSave && onSave({ name, pic })} style={{
          padding: '8px 16px', background: FT.orange, border: 'none', borderRadius: 16,
          color: '#FFF', fontFamily: FT.font, fontWeight: 500, fontSize: 13, cursor: 'pointer',
        }}>Save</button>
      </div>

      <div style={{ flex: 1, overflow: 'auto', padding: '24px 24px 32px' }}>
        <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 14 }}>
          <button onClick={() => fileRef.current && fileRef.current.click()} style={{
            position: 'relative',
            width: 120, height: 120, borderRadius: '50%',
            background: pic ? '#000' : FT.raised,
            backgroundImage: pic ? `url(${pic})` : undefined,
            backgroundSize: 'cover', backgroundPosition: 'center',
            border: pic ? '1px solid rgba(255,255,255,0.08)' : `1px dashed rgba(255,107,53,0.4)`,
            cursor: 'pointer', overflow: 'hidden',
            display: 'flex', alignItems: 'center', justifyContent: 'center',
            fontFamily: FT.fontFine, fontStyle: 'italic', color: FT.text, fontSize: 44, fontWeight: 400, letterSpacing: '-0.02em',
            padding: 0,
          }}>
            {!pic && <Icon name="camera" size={20} color={FT.orange} stroke={1.6} />}
          </button>
          <input ref={fileRef} type="file" accept="image/*" onChange={onFile} style={{ display: 'none' }} />
          {pic && (
            <button onClick={() => setPic(null)} style={{ background: 'transparent', border: 'none', color: FT.text3, fontSize: 12, fontWeight: 400, cursor: 'pointer', fontFamily: FT.font, letterSpacing: 0.01 }}>
              Remove photo
            </button>
          )}
        </div>

        <SectionLabel>Display name</SectionLabel>
        <div>
          <input value={name} onChange={e => setName(e.target.value)}
            onFocus={() => setFocused(true)} onBlur={() => setFocused(false)}
            style={{
              width: '100%', boxSizing: 'border-box',
              background: 'transparent', border: 'none', outline: 'none',
              padding: '4px 0',
              color: FT.text, fontFamily: FT.fontDisplay, fontSize: 24, fontWeight: 400,
              letterSpacing: '-0.05em',
            }} />
          <Underline focused={focused} />
        </div>
      </div>
    </div>
  );
}
window.EditProfileScreen = EditProfileScreen;
