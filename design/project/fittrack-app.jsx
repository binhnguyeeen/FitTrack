// FitTrackApp — interactive shell. Routes, profile state, transition animation.
//
// Animation system (rewrite):
//   • All visited screens stay mounted; transitions toggle opacity/transform on
//     absolutely-positioned layers using CSS transitions (NOT keyframe anims).
//   • Tab nav → directional slide+scale (320ms FT_EASE) with double-rAF pattern.
//   • Non-tab nav (Start, Done, Back arrows…) → orange ripple expanding from the
//     tapped button to fill the frame, then a cut to the new screen.
//   • Sheet/overlay routes (templates-create, templates-add, edit-profile,
//     settings) → soft push-up: 0.7 opacity + translateY(24px) → settled.

const { useState: useStateA, useRef: useRefA, useEffect: useEffectA } = React;

const TAB_IDX = { home: 0, calendar: 1, templates: 2, stats: 3 };
const OVERLAY_ROUTES = new Set(['templates-create', 'templates-add', 'edit-profile', 'settings']);
const FT_EASE_RIPPLE = 'cubic-bezier(0.4, 0, 0.2, 1)';

function FitTrackApp({ initial = 'home' }) {
  const [route, setRoute] = useStateA(initial);
  const [name, setName] = useStateA('Nguyen');
  const [pic, setPic] = useStateA(null);
  const [use24h, setUse24h] = useStateA(false);
  const [profileOpen, setProfileOpen] = useStateA(false);

  // Workout flow
  const [exIdx, setExIdx] = useStateA(0);
  const [workoutKind, setWorkoutKind] = useStateA('routine');
  const [showPB, setShowPB] = useStateA(false);
  const [restActive, setRestActive] = useStateA(false);

  // Ripple state: { active, x, y, scale, color }
  const [ripple, setRipple] = useStateA({ active: false, x: 0, y: 0, scale: 8, color: FT.orange });
  const phoneFrameRef = useRefA(null);

  // Per-route layer visual state: { opacity, transform, duration }.
  // duration === 0 → transition: none (instant). Otherwise → CSS transition.
  const [layerState, setLayerState] = useStateA(() => ({
    [initial]: { opacity: 1, transform: 'none', duration: 0 },
  }));
  // Once a route is mounted, it stays mounted (so we never remount-jank).
  const [mounted, setMounted] = useStateA(() => ({ [initial]: true }));

  const tabFor = {
    home: 'home', calendar: 'calendar',
    templates: 'templates', 'templates-create': 'templates', 'templates-add': 'templates',
    stats: 'stats',
  };
  const showTabBar = !!tabFor[route];

  // ── Tab nav: directional slide + scale ─────────────────────────────────
  const navTab = (t) => {
    if (t === route) return;
    const oldTab = tabFor[route] ?? route;
    const newIdx = TAB_IDX[t] ?? 0;
    const oldIdx = TAB_IDX[oldTab] ?? 0;
    const dir = Math.sign(newIdx - oldIdx) || 1;
    const fromRoute = route;

    // Mount incoming if needed; set its start state instantly.
    setMounted(m => m[t] ? m : { ...m, [t]: true });
    setLayerState(prev => ({
      ...prev,
      [t]: { opacity: 0, transform: `translateX(${dir * 32}px) scale(0.96)`, duration: 0 },
    }));

    // Double-rAF so the browser paints the start state before transitioning.
    requestAnimationFrame(() => {
      requestAnimationFrame(() => {
        setRoute(t);
        setLayerState(prev => ({
          ...prev,
          [t]:        { opacity: 1, transform: 'none', duration: 320 },
          [fromRoute]:{ opacity: 0, transform: `translateX(${-dir * 32}px) scale(0.96)`, duration: 320 },
        }));
      });
    });
  };

  // ── Non-tab nav: ripple cut OR overlay push-up ─────────────────────────
  const goRoute = (r, originEvent) => {
    if (r === route) return;
    const fromRoute = route;
    const fromOverlay = OVERLAY_ROUTES.has(fromRoute);
    const toOverlay = OVERLAY_ROUTES.has(r);

    // Overlay enter (push-up): incoming starts at 0.7 / translateY(24px), settles.
    if (toOverlay) {
      setMounted(m => m[r] ? m : { ...m, [r]: true });
      setLayerState(prev => ({
        ...prev,
        [r]: { opacity: 0.7, transform: 'translateY(24px)', duration: 0 },
      }));
      requestAnimationFrame(() => {
        requestAnimationFrame(() => {
          setRoute(r);
          setLayerState(prev => ({
            ...prev,
            [r]: { opacity: 1, transform: 'none', duration: 380 },
          }));
        });
      });
      return;
    }

    // Overlay exit (pop): outgoing fades + translateY(24px) over 280ms.
    if (fromOverlay) {
      setMounted(m => m[r] ? m : { ...m, [r]: true });
      // Ensure destination is already settled-visible underneath (it was the
      // previous active before the overlay opened, but reset just in case).
      setLayerState(prev => ({
        ...prev,
        [r]: { opacity: 1, transform: 'none', duration: 0 },
      }));
      setRoute(r);
      // Kick the outgoing overlay to its exit state.
      requestAnimationFrame(() => {
        requestAnimationFrame(() => {
          setLayerState(prev => ({
            ...prev,
            [fromRoute]: { opacity: 0, transform: 'translateY(24px)', duration: 280 },
          }));
        });
      });
      return;
    }

    // Regular non-tab navigation → ripple expand from button, then cut.
    if (originEvent && originEvent.currentTarget && phoneFrameRef.current) {
      const rect = originEvent.currentTarget.getBoundingClientRect();
      const frameRect = phoneFrameRef.current.getBoundingClientRect();
      const x = rect.left + rect.width / 2 - frameRect.left;
      const y = rect.top + rect.height / 2 - frameRect.top;
      const W = frameRect.width  || 360;
      const H = frameRect.height || 780;
      const maxRadius = Math.ceil(Math.sqrt(
        Math.pow(Math.max(x, W - x), 2) +
        Math.pow(Math.max(y, H - y), 2)
      )) + 10;
      const scale = (maxRadius * 2) / 10;
      setRipple({ active: true, x, y, scale, color: FT.orange });
      setTimeout(() => {
        // Reveal the new screen instantly underneath the (now fading) ripple.
        setMounted(m => m[r] ? m : { ...m, [r]: true });
        setLayerState(prev => ({
          ...prev,
          [r]:         { opacity: 1, transform: 'none', duration: 0 },
          [fromRoute]: { opacity: 0, transform: 'none', duration: 0 },
        }));
        setRoute(r);
        setRipple(s => ({ ...s, active: false }));
      }, 380);
      return;
    }

    // No event → instant cut (e.g. callbacks that pass data instead of an event).
    setMounted(m => m[r] ? m : { ...m, [r]: true });
    setLayerState(prev => ({
      ...prev,
      [r]:         { opacity: 1, transform: 'none', duration: 0 },
      [fromRoute]: { opacity: 0, transform: 'none', duration: 0 },
    }));
    setRoute(r);
  };

  // ── Render a single screen by route id ─────────────────────────────────
  const renderScreen = (r) => {
    switch (r) {
      case 'onboarding':
        return <OnboardingScreen onSubmit={(d) => { setName(d.name || 'Nguyen'); setPic(d.pic); navTab('home'); }} />;
      case 'home':
        return <HomeScreen name={name} pic={pic}
                  onStartWorkout={(e) => { setExIdx(0); setWorkoutKind('routine'); setShowPB(false); setRestActive(false); goRoute('workout', e); }}
                  onNavigate={navTab}
                  onOpenProfile={() => setProfileOpen(true)} />;
      case 'calendar':
        return <CalendarScreen />;
      case 'templates':
        return <TemplatesScreen onCreate={(e) => goRoute('templates-create', e)} />;
      case 'templates-create':
        return <CreateTemplateScreen onBack={(e) => goRoute('templates', e)} onAdd={(e) => goRoute('templates-add', e)} />;
      case 'templates-add':
        return <AddExerciseScreen onBack={(e) => goRoute('templates-create', e)} onAdded={(e) => goRoute('templates-create', e)} />;
      case 'workout':
        return <LogWorkoutScreen kind={workoutKind} exerciseIdx={exIdx} showPB={showPB} restActive={restActive}
                  onExit={(e) => goRoute('summary', e)}
                  onNext={() => setExIdx(i => i + 1 >= 6 ? 5 : i + 1)} />;
      case 'summary':
        return <SummaryScreen onDone={(e) => goRoute('home', e)} />;
      case 'stats':
        return <StatsScreen />;
      case 'settings':
        return <SettingsScreen name={name} pic={pic}
                  onBack={(e) => goRoute('home', e)}
                  onEditProfile={(e) => goRoute('edit-profile', e)} />;
      case 'edit-profile':
        return <EditProfileScreen name={name} pic={pic}
                  onBack={(e) => goRoute('home', e)}
                  onSave={(d) => { setName(d.name || 'Nguyen'); setPic(d.pic); goRoute('home'); }} />;
      default: return null;
    }
  };

  const mountedRoutes = Object.keys(mounted);

  return (
    <PhoneFrame use24h={use24h}>
      <div ref={phoneFrameRef} style={{ height: '100%', position: 'relative', overflow: 'hidden', isolation: 'isolate' }}>
        {/* Screen layer stack */}
        {mountedRoutes.map(r => {
          const st = layerState[r] || { opacity: 0, transform: 'none', duration: 0 };
          const isActive = r === route;
          const overlay = OVERLAY_ROUTES.has(r);
          return (
            <div key={r} style={{
              position: 'absolute', inset: 0,
              opacity: st.opacity,
              transform: st.transform,
              transition: st.duration === 0
                ? 'none'
                : `opacity ${st.duration}ms ${FT_EASE}, transform ${st.duration}ms ${FT_EASE}`,
              pointerEvents: isActive ? 'auto' : 'none',
              willChange: 'transform, opacity',
              backfaceVisibility: 'hidden',
              WebkitBackfaceVisibility: 'hidden',
              // Overlays paint above non-overlay layers so push-up reads correctly.
              zIndex: overlay ? 2 : 1,
            }}>
              {renderScreen(r)}
            </div>
          );
        })}

        {/* Ripple — one-shot keyframe expand from tap point */}
        {ripple.active && (
          <div style={{
            position: 'absolute',
            left: ripple.x, top: ripple.y,
            width: 10, height: 10,
            borderRadius: '50%',
            background: ripple.color,
            transform: 'translate(-50%, -50%) scale(0)',
            animation: `ft-ripple-expand 380ms ${FT_EASE_RIPPLE} forwards`,
            pointerEvents: 'none',
            zIndex: 50,
            opacity: 0.92,
            '--ripple-scale': ripple.scale,
          }} />
        )}

        {showTabBar && <TabBar active={tabFor[route]} onSelect={navTab} />}
        {profileOpen && (
          <ProfileMenu
            name={name} pic={pic} use24h={use24h}
            onUse24h={() => setUse24h(v => !v)}
            onClose={() => setProfileOpen(false)}
            onEditProfile={() => { setProfileOpen(false); goRoute('edit-profile'); }}
            onOpenSettings={() => { setProfileOpen(false); goRoute('settings'); }}
          />
        )}
      </div>
    </PhoneFrame>
  );
}
window.FitTrackApp = FitTrackApp;

// ─────────────────────────────────────────────────────────────
// Static artboards
// ─────────────────────────────────────────────────────────────
function withTab(node, active) {
  return (
    <PhoneFrame>
      <div style={{ height: '100%', position: 'relative' }}>
        {node}
        <TabBar active={active} />
      </div>
    </PhoneFrame>
  );
}

window.AB = {
  Onboarding:    () => <PhoneFrame><OnboardingScreen onSubmit={() => {}} /></PhoneFrame>,

  Home:          () => withTab(<HomeScreen name="Nguyen" />, 'home'),
  HomePhoto:     () => withTab(<HomeScreen name="Nguyen" pic="assets/logo.png" />, 'home'),
  ProfileMenuOpen: () => (
    <PhoneFrame>
      <div style={{ height: '100%', position: 'relative' }}>
        <HomeScreen name="Nguyen" />
        <TabBar active="home" />
        <ProfileMenu name="Nguyen" onClose={() => {}} onEditProfile={() => {}} onOpenSettings={() => {}} />
      </div>
    </PhoneFrame>
  ),
  EditProfile:   () => <PhoneFrame><EditProfileScreen name="Nguyen" onBack={() => {}} onSave={() => {}} /></PhoneFrame>,

  Calendar:          () => withTab(<CalendarScreen />, 'calendar'),
  CalendarPlanning:  () => withTab(<CalendarScreen selected={6} initialPlanning={true} />, 'calendar'),

  Templates:      () => withTab(<TemplatesScreen />, 'templates'),
  CreateTemplate: () => <PhoneFrame><CreateTemplateScreen onBack={()=>{}} onAdd={()=>{}} /></PhoneFrame>,
  AddExercise:    () => <PhoneFrame><AddExerciseScreen onBack={()=>{}} onAdded={()=>{}} /></PhoneFrame>,

  Workout:              () => <PhoneFrame><LogWorkoutScreen kind="routine" exerciseIdx={1} /></PhoneFrame>,
  WorkoutSwimDone:      () => <PhoneFrame><LogWorkoutScreen kind="swim" exerciseIdx={1} treadmillPhase="done" /></PhoneFrame>,
  WorkoutTreadmill:     () => <PhoneFrame><LogWorkoutScreen kind="treadmill" exerciseIdx={1} treadmillPhase="timing" /></PhoneFrame>,
  WorkoutTreadmillDone: () => <PhoneFrame><LogWorkoutScreen kind="treadmill" exerciseIdx={1} treadmillPhase="done" /></PhoneFrame>,
  WorkoutBasket:        () => <PhoneFrame><LogWorkoutScreen kind="basket" exerciseIdx={0} treadmillPhase="timing" /></PhoneFrame>,
  WorkoutBasketDone:    () => <PhoneFrame><LogWorkoutScreen kind="basket" exerciseIdx={0} treadmillPhase="done" /></PhoneFrame>,
  WorkoutPB:            () => <PhoneFrame><LogWorkoutScreen kind="routine" exerciseIdx={3} showPB /></PhoneFrame>,
  WorkoutRest:          () => <PhoneFrame><LogWorkoutScreen kind="routine" exerciseIdx={2} restActive /></PhoneFrame>,

  Summary:  () => <PhoneFrame><SummaryScreen onDone={()=>{}} /></PhoneFrame>,

  Stats:    () => withTab(<StatsScreen />, 'stats'),
  StatsBar: () => withTab(<StatsScreen chartMode="bar" />, 'stats'),

  Settings: () => <PhoneFrame><SettingsScreen name="Nguyen" onBack={()=>{}} onEditProfile={()=>{}} /></PhoneFrame>,
};
