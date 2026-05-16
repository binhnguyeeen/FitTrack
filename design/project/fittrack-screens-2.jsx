// Templates list, Create-Template flow, and Add-Exercise picker.

// ─────────────────────────────────────────────────────────────
// TEMPLATES
// ─────────────────────────────────────────────────────────────
function TemplatesScreen({ onCreate, onOpenTemplate }) {
  const loading = useLoading(1200);

  // Real templates per spec.
  const templates = [
    { name: 'Morning Run',     kind: 'treadmill', count: 3 },
    { name: 'Upper Body',      kind: 'routine',   count: 5 },
    { name: 'Swim Intervals',  kind: 'swim',      count: 4 },
    { name: 'Core & Stretch',  kind: 'routine',   count: 6 },
  ];

  return (
    <Screen>
      <div style={{ marginTop: 8 }}>
        <div style={{
          fontFamily: FT.fontDisplay, fontSize: 28, fontWeight: 400,
          letterSpacing: '-0.05em', color: FT.text, lineHeight: 1.1,
        }}>
          Templates
        </div>
      </div>

      <SectionLabel>Saved</SectionLabel>
      <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
        {loading
          ? [0,1,2,3].map(i => <TemplateSkeleton key={i} />)
          : templates.map((t, i) => (
              <TemplateCard key={i} {...t} onClick={() => onOpenTemplate && onOpenTemplate(t)} />
            ))
        }
      </div>

      {/* FAB — 56px, orange, +icon, 24px from bottom-right (above tab bar). */}
      <button onClick={onCreate} aria-label="New template" style={{
        position: 'absolute', right: 24, bottom: 104,
        width: 56, height: 56, borderRadius: '50%', background: FT.orange,
        border: 'none', cursor: 'pointer',
        display: 'flex', alignItems: 'center', justifyContent: 'center',
        boxShadow: '0 8px 24px rgba(255,107,53,0.35)',
        zIndex: 6,
      }}>
        <Icon name="plus" size={22} color="#FFF" stroke={2.2} />
      </button>
    </Screen>
  );
}
window.TemplatesScreen = TemplatesScreen;

// 72px tall, full-width, 24dp radius. Left: 4×40 color strip. No border.
function TemplateCard({ name, kind, count, onClick }) {
  const press = usePress();
  return (
    <div onClick={onClick} {...press.bind} style={{
      background: FT.surface, borderRadius: 24,
      height: 72, padding: '0 20px',
      display: 'flex', alignItems: 'center', gap: 16,
      cursor: 'pointer',
      transform: press.pressed ? 'scale(0.98)' : 'scale(1)',
      transition: `transform 120ms ease-in-out`,
    }}>
      <ActivityChip kind={kind} size={40} />
      <div style={{ flex: 1, minWidth: 0 }}>
        <div style={{
          fontFamily: FT.font, fontSize: 16, fontWeight: 400,
          color: FT.text, letterSpacing: 0.01, lineHeight: 1.2,
        }}>{name}</div>
        <div style={{
          fontFamily: FT.font, fontSize: 12, fontWeight: 400,
          color: FT.text2, marginTop: 4, letterSpacing: 0.01,
        }}>{count} exercises</div>
      </div>
      <Icon name="arrow" size={16} color={FT.text3} />
    </div>
  );
}

function TemplateSkeleton() {
  return (
    <div style={{
      background: FT.surface, borderRadius: 24, height: 72,
      padding: '0 20px', display: 'flex', alignItems: 'center', gap: 16,
    }}>
      <Skeleton w={40} h={40} r={14} />
      <div style={{ flex: 1, display: 'flex', flexDirection: 'column', gap: 6 }}>
        <Skeleton w="50%" h={14} r={7} />
        <Skeleton w="30%" h={10} r={5} />
      </div>
    </div>
  );
}

// ─────────────────────────────────────────────────────────────
// CREATE TEMPLATE
// ─────────────────────────────────────────────────────────────
function CreateTemplateScreen({ onBack, onAdd }) {
  const [name, setName] = useState('Upper Body');
  const [nameFocused, setNameFocused] = useState(false);
  const [kind, setKind] = useState('routine');
  const [exercises, setExercises] = useState([
    { name: 'Pull-ups',     kind: 'routine', mode: 'Sets · 4 × 8' },
    { name: 'Push-ups',     kind: 'routine', mode: 'Sets · 4 × 15' },
    { name: 'Dumbbell rows', kind: 'routine', mode: 'Sets · 3 × 12' },
    { name: 'Plank',        kind: 'routine', mode: 'Duration · 60s' },
    { name: 'Jumping jacks', kind: 'routine', mode: 'Duration · 60s' },
  ]);

  return (
    <div style={{ height: '100%', display: 'flex', flexDirection: 'column', animation: `ft-push-up 380ms ${FT_EASE}` }}>
      {/* Top bar */}
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', padding: '8px 16px 8px 12px' }}>
        <button onClick={onBack} style={{ width: 40, height: 40, background: 'transparent', border: 'none', color: FT.text, cursor: 'pointer', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
          <Icon name="arrowL" size={20} color={FT.text} />
        </button>
        <div style={{ fontFamily: FT.font, fontSize: 14, fontWeight: 400, color: FT.text2, letterSpacing: 0.04, textTransform: 'uppercase' }}>New template</div>
        <button onClick={onBack} style={{
          padding: '8px 16px', background: FT.orange, border: 'none', borderRadius: 16,
          color: '#FFF', fontFamily: FT.font, fontWeight: 500, fontSize: 13, cursor: 'pointer', letterSpacing: 0.01,
        }}>Save</button>
      </div>

      <div style={{ flex: 1, overflow: 'auto', padding: '12px 24px 32px' }}>
        {/* Name — underline input only */}
        <SectionLabel style={{ marginTop: 12 }}>Template name</SectionLabel>
        <div>
          <input value={name} onChange={e => setName(e.target.value)}
            onFocus={() => setNameFocused(true)} onBlur={() => setNameFocused(false)}
            style={{
              width: '100%', boxSizing: 'border-box',
              background: 'transparent', border: 'none', outline: 'none',
              padding: '4px 0',
              fontFamily: FT.fontDisplay, fontSize: 24, fontWeight: 400,
              letterSpacing: '-0.05em', color: FT.text,
            }} />
          <Underline focused={nameFocused} />
        </div>

        {/* Activity */}
        <SectionLabel>Activity</SectionLabel>
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: 8 }}>
          {['treadmill','swim','basket','routine'].map(k => {
            const on = kind === k;
            return (
              <button key={k} onClick={() => setKind(k)} style={{
                background: on ? ACT[k].soft : 'transparent',
                border: on ? `1px solid ${ACT[k].color}` : `1px solid rgba(255,255,255,0.05)`,
                borderRadius: 16, padding: '10px 0', cursor: 'pointer', fontFamily: FT.font,
                display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 4,
                color: on ? ACT[k].color : FT.text2,
                transition: `background 200ms ${FT_EASE}, border-color 200ms ${FT_EASE}`,
              }}>
                <Icon name={ACT[k].icon} size={18} color={on ? ACT[k].color : FT.text2} stroke={1.8} />
                <span style={{ fontSize: 10, fontWeight: 400, whiteSpace: 'nowrap', letterSpacing: 0.04 }}>{ACT[k].label}</span>
              </button>
            );
          })}
        </div>

        {/* Exercises */}
        <SectionLabel>Exercises</SectionLabel>
        <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
          {exercises.map((ex, i) => (
            <div key={i} style={{
              background: FT.surface, borderRadius: 24, padding: '14px 18px',
              display: 'flex', alignItems: 'center', gap: 14,
            }}>
              <div style={{ color: FT.text3, cursor: 'grab', display: 'flex', alignItems: 'center', flexShrink: 0 }}>
                <Icon name="drag" size={18} color={FT.text3} />
              </div>
              <div style={{ flex: 1, minWidth: 0 }}>
                <div style={{ fontFamily: FT.font, fontSize: 14, fontWeight: 400, color: FT.text, letterSpacing: 0.01 }}>{ex.name}</div>
                <div style={{ fontFamily: FT.font, fontSize: 12, fontWeight: 400, color: FT.text2, marginTop: 3, letterSpacing: 0.01 }}>{ex.mode}</div>
              </div>
              <button onClick={() => setExercises(exs => exs.filter((_, j) => j !== i))} style={{
                width: 28, height: 28, background: 'transparent', border: 'none', color: FT.text3, cursor: 'pointer',
                display: 'flex', alignItems: 'center', justifyContent: 'center',
              }}>
                <Icon name="close" size={14} color={FT.text3} />
              </button>
            </div>
          ))}
        </div>

        <button onClick={onAdd} style={{
          marginTop: 12, width: '100%', padding: 16,
          background: 'transparent', border: `1px dashed rgba(255,255,255,0.10)`,
          borderRadius: 24, color: FT.text2, fontFamily: FT.font, fontWeight: 400, fontSize: 14,
          display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 8, cursor: 'pointer',
          letterSpacing: 0.01,
        }}>
          <Icon name="plus" size={16} color={FT.text2} /> Add exercise
        </button>
      </div>
    </div>
  );
}
window.CreateTemplateScreen = CreateTemplateScreen;

// ─────────────────────────────────────────────────────────────
// ADD EXERCISE — preset library with X of Y counter.
// ─────────────────────────────────────────────────────────────
function AddExerciseScreen({ onBack, onAdded }) {
  const [q, setQ] = useState('');
  const [searchFocused, setSearchFocused] = useState(false);

  const presets = [
    { name: 'Pull-ups',           kind: 'routine',   mode: 'Sets · reps' },
    { name: 'Push-ups',           kind: 'routine',   mode: 'Sets · reps' },
    { name: 'Dumbbell rows',      kind: 'routine',   mode: 'Sets · reps' },
    { name: 'Plank',              kind: 'routine',   mode: 'Duration · 60s' },
    { name: 'Jumping jacks',      kind: 'routine',   mode: 'Duration' },
    { name: 'Burpees',            kind: 'routine',   mode: 'Reps' },
    { name: 'Treadmill intervals', kind: 'treadmill', mode: 'Cardio metrics' },
    { name: 'Freestyle swim sets', kind: 'swim',      mode: 'Sets · duration' },
    { name: 'Free throws',        kind: 'basket',    mode: 'Reps' },
    { name: 'Mountain climbers',  kind: 'routine',   mode: 'Duration' },
  ];
  const filtered = presets.filter(p => p.name.toLowerCase().includes(q.toLowerCase()));

  return (
    <div style={{ height: '100%', display: 'flex', flexDirection: 'column', animation: `ft-push-up 380ms ${FT_EASE}` }}>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', padding: '8px 16px 8px 12px' }}>
        <button onClick={onBack} style={{ width: 40, height: 40, background: 'transparent', border: 'none', color: FT.text, cursor: 'pointer', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
          <Icon name="close" size={20} color={FT.text} />
        </button>
        <div style={{ fontFamily: FT.font, fontSize: 14, fontWeight: 400, color: FT.text2, letterSpacing: 0.04, textTransform: 'uppercase' }}>Add exercise</div>
        <div style={{ width: 40 }} />
      </div>

      <div style={{ padding: '8px 24px 0' }}>
        {/* Underline search */}
        <div style={{ display: 'flex', alignItems: 'center', gap: 10, padding: '8px 0' }}>
          <Icon name="search" size={16} color={FT.text3} />
          <input value={q} onChange={e => setQ(e.target.value)}
            onFocus={() => setSearchFocused(true)} onBlur={() => setSearchFocused(false)}
            placeholder="Search exercises"
            style={{
              flex: 1, background: 'transparent', border: 'none', outline: 'none',
              color: FT.text, fontFamily: FT.font, fontWeight: 400, fontSize: 15, letterSpacing: 0.01,
            }} />
        </div>
        <Underline focused={searchFocused} />
      </div>

      <div style={{ flex: 1, overflow: 'auto', padding: '20px 24px 24px', position: 'relative' }}>
        <div style={{ display: 'flex', alignItems: 'baseline', justifyContent: 'space-between', marginBottom: 10 }}>
          <div style={{
            fontFamily: FT.font, fontSize: 10, fontWeight: 500,
            letterSpacing: '0.12em', textTransform: 'uppercase', color: FT.text3,
          }}>Preset library</div>
          {/* X of Y counter — Tiempos Text 11px tertiary */}
          <div style={{ fontFamily: FT.font, fontSize: 11, fontWeight: 400, color: FT.text3, fontVariantNumeric: 'tabular-nums', letterSpacing: 0.01 }}>
            {filtered.length} of {presets.length}
          </div>
        </div>

        <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
          {filtered.map((p, i) => (
            <PresetRow key={i} {...p} onClick={onAdded} />
          ))}
          {filtered.length === 0 && (
            <div style={{
              padding: 40, textAlign: 'center',
              color: FT.text3, fontFamily: FT.font, fontSize: 13,
              letterSpacing: 0.01,
            }}>No matching exercises</div>
          )}
        </div>
      </div>
    </div>
  );
}
window.AddExerciseScreen = AddExerciseScreen;

function PresetRow({ name, kind, mode, onClick }) {
  const press = usePress();
  return (
    <button onClick={onClick} {...press.bind} style={{
      background: FT.surface, border: 'none', borderRadius: 24,
      padding: '14px 18px', display: 'flex', alignItems: 'center', gap: 14,
      cursor: 'pointer', fontFamily: FT.font, textAlign: 'left',
      transform: press.pressed ? 'scale(0.98)' : 'scale(1)',
      transition: `transform 120ms ease-in-out`,
    }}>
      <ActivityChip kind={kind} size={36} />
      <div style={{ flex: 1, minWidth: 0 }}>
        <div style={{ fontSize: 14, fontWeight: 400, color: FT.text, letterSpacing: 0.01 }}>{name}</div>
        <div style={{ fontSize: 12, fontWeight: 400, color: FT.text2, marginTop: 3, letterSpacing: 0.01 }}>{mode}</div>
      </div>
      <Icon name="plus" size={16} color={FT.text3} />
    </button>
  );
}
