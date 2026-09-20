import { AnimatePresence, motion } from 'framer-motion';
import type { CSSProperties, ReactNode } from 'react';

const BASE_URL = import.meta.env.BASE_URL;
const LOGO_SRC = `${BASE_URL}assets/td-app-logo.png`;

const EASE = [0.16, 1, 0.3, 1] as const;
const INSET = { left: '7vw', right: '7vw' };

type SceneProps = { currentScene: number };

export function TDAppLoadingScene({ currentScene }: SceneProps) {
  return (
    <div
      className="absolute inset-0 overflow-hidden"
      style={{
        background:
          'radial-gradient(circle at 12% 15%, rgba(255,255,255,.78), transparent 29%), radial-gradient(circle at 92% 85%, rgba(20,184,166,.18), transparent 32%), linear-gradient(145deg, #f7f3ff 0%, #eee6ff 53%, #e4f8f5 100%)',
      }}
    >
      <PersistentAtmosphere currentScene={currentScene} />
      <AnimatePresence mode="sync" initial={false}>
        {currentScene === 0 && <FocusScene key="focus" />}
        {currentScene === 1 && <ProgressScene key="progress" />}
        {currentScene === 2 && <CoinsScene key="coins" />}
        {currentScene === 3 && <MomentumScene key="momentum" />}
        {currentScene === 4 && <ReadyScene key="ready" />}
      </AnimatePresence>
    </div>
  );
}

function PersistentAtmosphere({ currentScene }: SceneProps) {
  const orbPositions = [
    { x: '-24vw', y: '-10vh', scale: 1.2, color: 'rgba(124,58,237,.16)' },
    { x: '63vw', y: '-18vh', scale: 0.92, color: 'rgba(20,184,166,.14)' },
    { x: '54vw', y: '58vh', scale: 1.3, color: 'rgba(251,191,36,.12)' },
    { x: '-27vw', y: '60vh', scale: 0.8, color: 'rgba(124,58,237,.13)' },
    { x: '30vw', y: '14vh', scale: 1.05, color: 'rgba(20,184,166,.13)' },
  ];
  const orbit = orbPositions[currentScene];

  return (
    <>
      <motion.div
        aria-hidden="true"
        animate={{ x: orbit.x, y: orbit.y, scale: orbit.scale, backgroundColor: orbit.color }}
        className="absolute h-[48vmin] w-[48vmin] rounded-full blur-[1px]"
        style={{ filter: 'blur(28px)' }}
        transition={{ duration: 1.35, ease: EASE }}
      />
      <motion.div
        aria-hidden="true"
        animate={{
          x: currentScene % 2 === 0 ? '54vw' : '-16vw',
          y: currentScene === 2 ? '44vh' : '20vh',
          rotate: currentScene * 12 - 10,
        }}
        className="td-orbit h-[76vmin] w-[76vmin]"
        transition={{ duration: 1.65, ease: EASE }}
      />
      <motion.div
        aria-hidden="true"
        animate={{
          x: currentScene === 1 ? '3vw' : '63vw',
          y: currentScene === 3 ? '66vh' : '48vh',
          opacity: currentScene === 4 ? 0.2 : 0.45,
        }}
        className="td-orbit h-[31vmin] w-[31vmin]"
        transition={{ duration: 1.2, ease: EASE }}
      />
      <div aria-hidden="true" className="td-dot-grid absolute inset-x-0 bottom-[-5vh] h-[44vh] opacity-35" />
      <div aria-hidden="true" className="td-grain" />
      <motion.div
        aria-hidden="true"
        animate={{ opacity: [0.25, 0.4, 0.25], rotate: [0, 5, 0] }}
        className="absolute left-[7vw] top-[5vh] h-[2.4vmin] w-[2.4vmin] rounded-full bg-[#14B8A6]"
        transition={{ duration: 4.5, repeat: Infinity, ease: 'easeInOut' }}
      />
      <motion.div
        aria-hidden="true"
        animate={{ y: [0, -1.3 * 16, 0], opacity: [0.5, 1, 0.5] }}
        className="absolute right-[10vw] top-[21vh] h-[1.6vmin] w-[1.6vmin] rounded-full bg-[#FBBF24]"
        transition={{ duration: 3.2, repeat: Infinity, ease: 'easeInOut' }}
      />
    </>
  );
}

function BrandMark({ small = false }: { small?: boolean }) {
  return (
    <motion.img
      alt="TD-App"
      className={small ? 'h-[8.5vmin] w-[8.5vmin]' : 'h-[37vmin] w-[37vmin]'}
      src={LOGO_SRC}
      style={{ objectFit: 'contain' }}
    />
  );
}

function SceneShell({
  children,
  className = '',
  accent = 'purple',
  style,
}: {
  children: ReactNode;
  className?: string;
  accent?: 'purple' | 'teal' | 'gold';
  style?: CSSProperties;
}) {
  const accentColor = accent === 'teal' ? '#14B8A6' : accent === 'gold' ? '#FBBF24' : '#7C3AED';
  return (
    <motion.section
      className={`absolute inset-0 ${className}`}
      initial={{ opacity: 0, clipPath: 'circle(0% at 50% 50%)', scale: 0.985 }}
      animate={{ opacity: 1, clipPath: 'circle(76% at 50% 50%)', scale: 1 }}
      exit={{ opacity: 0, clipPath: 'circle(145% at 50% 50%)', scale: 1.02 }}
      transition={{ duration: 0.86, ease: EASE }}
      style={{ '--scene-accent': accentColor, ...style } as CSSProperties}
    >
      {children}
    </motion.section>
  );
}

function Wordmark({
  eyebrow,
  title,
  body,
  align = 'left',
}: {
  eyebrow: string;
  title: React.ReactNode;
  body?: string;
  align?: 'left' | 'center';
}) {
  return (
    <div className={`flex flex-col ${align === 'center' ? 'items-center text-center' : 'items-start text-left'}`}>
      <motion.div
        className="mb-[2.4vh] flex items-center gap-[1.6vw] text-[2.6vmin] font-bold uppercase tracking-[0.24em] text-[#7C3AED]"
        initial={{ opacity: 0, y: 16 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.22, duration: 0.6, ease: EASE }}
      >
        <span className="h-[1.2vmin] w-[1.2vmin] rounded-full bg-[#14B8A6]" />
        {eyebrow}
      </motion.div>
      <motion.h1
        className="max-w-[78vw] font-[var(--font-display)] text-[11vmin] font-extrabold leading-[0.97] tracking-[-0.065em] text-[#28164F]"
        initial={{ opacity: 0, y: 30 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.38, duration: 0.84, ease: EASE }}
      >
        {title}
      </motion.h1>
      {body && (
        <motion.p
          className="mt-[2.8vh] max-w-[66vw] text-[4.3vmin] leading-[1.18] text-[#695B82]"
          initial={{ opacity: 0, y: 18 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.68, duration: 0.64, ease: EASE }}
        >
          {body}
        </motion.p>
      )}
    </div>
  );
}

function FocusScene() {
  return (
    <SceneShell className="flex flex-col items-center justify-center" accent="purple">
      <motion.div
        className="absolute left-[7vw] top-[8vh] flex items-center gap-[2.4vw]"
        initial={{ opacity: 0, x: -18 }}
        animate={{ opacity: 1, x: 0 }}
        transition={{ duration: 0.65, ease: EASE }}
      >
        <BrandMark small />
        <span className="font-[var(--font-display)] text-[3.1vmin] font-bold tracking-[-0.03em] text-[#28164F]">TD-App</span>
      </motion.div>
      <motion.div
        className="relative mb-[5vh]"
        initial={{ scale: 0.66, opacity: 0, rotate: -12 }}
        animate={{ scale: [0.66, 1.04, 1], opacity: 1, rotate: 0 }}
        transition={{ duration: 1.15, ease: EASE }}
      >
        <motion.div
          className="absolute inset-[-8vmin] rounded-full border border-[#7C3AED]/15"
          animate={{ scale: [0.95, 1.12, 0.95], opacity: [0.55, 0.15, 0.55] }}
          transition={{ duration: 3.2, repeat: Infinity, ease: 'easeInOut' }}
        />
        <BrandMark />
      </motion.div>
      <Wordmark
        align="center"
        eyebrow="a clear moment"
        title={
          <>
            Start with
            <br />
            <span className="text-[#7C3AED]">one breath.</span>
          </>
        }
        body="Small practice. Real momentum."
      />
      <motion.div
        className="absolute bottom-[8vh] flex items-center gap-[2vw] text-[2.7vmin] font-semibold tracking-[0.08em] text-[#695B82]"
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        transition={{ delay: 1.2, duration: 0.6 }}
      >
        <span>READY</span>
        <span className="h-[1px] w-[12vw] bg-[#7C3AED]/30" />
        <span className="text-[#7C3AED]">01 / 05</span>
      </motion.div>
    </SceneShell>
  );
}

function ProgressScene() {
  return (
    <SceneShell className="flex flex-col justify-center" accent="teal" style={{ paddingInline: INSET.left }}>
      <motion.div
        className="absolute right-[7vw] top-[8vh] rounded-full bg-[#14B8A6]/12 px-[3vw] py-[1.4vh] text-[2.5vmin] font-bold tracking-[0.12em] text-[#0F766E]"
        initial={{ opacity: 0, x: 22 }}
        animate={{ opacity: 1, x: 0 }}
        transition={{ delay: 0.28, duration: 0.6, ease: EASE }}
      >
        TODAY · 02
      </motion.div>
      <Wordmark eyebrow="keep showing up" title={<>Progress feels<br /><span className="text-[#14B8A6]">better in motion.</span></>} body="A focused check-in, then the next small win." />
      <motion.div
        className="mt-[6.4vh] rounded-[6vmin] border border-white/80 bg-white/58 p-[5vw] shadow-[0_2.5vmin_7vmin_rgba(77,39,128,.12)] backdrop-blur-md"
        initial={{ opacity: 0, y: 32, rotate: 2 }}
        animate={{ opacity: 1, y: 0, rotate: 0 }}
        transition={{ delay: 0.9, duration: 0.82, ease: EASE }}
      >
        <div className="mb-[2.2vh] flex items-end justify-between">
          <span className="text-[2.7vmin] font-semibold uppercase tracking-[0.18em] text-[#695B82]">focus path</span>
          <span className="font-[var(--font-display)] text-[5.5vmin] font-extrabold tracking-[-0.08em] text-[#28164F]">68%</span>
        </div>
        <div className="td-progress-track h-[1.8vmin] w-[64vw]">
          <motion.div
            className="h-full rounded-full bg-[#14B8A6]"
            initial={{ width: '0%' }}
            animate={{ width: '68%' }}
            transition={{ delay: 1.1, duration: 1.3, ease: EASE }}
          />
        </div>
        <div className="mt-[2.2vh] flex justify-between text-[2.8vmin] font-medium text-[#8A7A9F]">
          <span>In progress</span>
          <span className="text-[#0F766E]">12 min left</span>
        </div>
      </motion.div>
      <motion.div
        className="mt-[4.2vh] flex items-center gap-[3vw]"
        initial={{ opacity: 0, x: -20 }}
        animate={{ opacity: 1, x: 0 }}
        transition={{ delay: 1.25, duration: 0.6, ease: EASE }}
      >
        <span className="flex h-[8vmin] w-[8vmin] items-center justify-center rounded-[2.4vmin] bg-[#7C3AED] text-[3.5vmin] font-bold text-white">+</span>
        <span className="text-[3.8vmin] font-semibold text-[#28164F]">One step is still a step.</span>
      </motion.div>
    </SceneShell>
  );
}

function CoinsScene() {
  return (
    <SceneShell className="flex flex-col items-center justify-center" accent="gold">
      <motion.div
        className="absolute left-[7vw] top-[8vh] text-[2.7vmin] font-bold uppercase tracking-[0.2em] text-[#A16207]"
        initial={{ opacity: 0, y: -12 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.25, duration: 0.6, ease: EASE }}
      >
        earned today
      </motion.div>
      <motion.div
        className="relative mb-[4.8vh] h-[30vmin] w-[42vmin]"
        initial={{ opacity: 0, scale: 0.7, y: 25 }}
        animate={{ opacity: 1, scale: 1, y: 0 }}
        transition={{ duration: 0.9, ease: EASE }}
      >
        {[0, 1, 2].map((index) => (
          <motion.div
            className="td-coin absolute h-[23vmin] w-[23vmin] text-[8vmin]"
            key={index}
            style={{ left: `${index * 8.2}vmin`, top: `${(2 - index) * 2.3}vmin`, zIndex: index }}
            animate={{ y: [0, -1.6 * (index + 1), 0], rotate: [index * -5, index * -5 + 4, index * -5] }}
            transition={{ delay: 0.8 + index * 0.18, duration: 2.6 + index * 0.25, repeat: Infinity, ease: 'easeInOut' }}
          >
            <span>TD</span>
          </motion.div>
        ))}
        <motion.div
          className="absolute -right-[4vmin] top-[0.5vmin] h-[5vmin] w-[5vmin] rounded-full bg-[#14B8A6]"
          animate={{ y: [0, -6, 0], scale: [1, 1.15, 1] }}
          transition={{ duration: 1.8, repeat: Infinity, ease: 'easeInOut' }}
        />
      </motion.div>
      <Wordmark
        align="center"
        eyebrow="reward the rhythm"
        title={
          <>
            You earned
            <br />
            <span className="text-[#D99012]">24 TD-Coins.</span>
          </>
        }
        body="Every focused minute comes back as energy."
      />
      <motion.div
        className="mt-[4.2vh] flex items-center gap-[2vw] rounded-full border border-[#FBBF24]/35 bg-[#FFF7D6]/70 px-[4vw] py-[1.8vh] text-[3vmin] font-bold text-[#8D5B08]"
        initial={{ opacity: 0, scale: 0.8 }}
        animate={{ opacity: 1, scale: 1 }}
        transition={{ delay: 1.35, duration: 0.55, ease: EASE }}
      >
        <span className="text-[#14B8A6]">+12%</span>
        <span className="h-[2.4vmin] w-px bg-[#D8B45A]" />
        <span>this week</span>
      </motion.div>
    </SceneShell>
  );
}

function MomentumScene() {
  return (
    <SceneShell className="flex flex-col justify-center" accent="teal" style={{ paddingInline: INSET.left }}>
      <motion.div
        className="absolute right-[8vw] top-[9vh] flex items-center gap-[1.8vw] text-[2.6vmin] font-bold uppercase tracking-[0.18em] text-[#0F766E]"
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        transition={{ delay: 0.25, duration: 0.5 }}
      >
        <span className="h-[1.8vmin] w-[1.8vmin] rounded-full bg-[#14B8A6]" />
        momentum
      </motion.div>
      <Wordmark eyebrow="make it yours" title={<>Keep the streak<br /><span className="text-[#14B8A6]">moving forward.</span></>} body="A gentle nudge for the version of you that keeps going." />
      <motion.div
        className="relative mt-[7vh] h-[21vh] w-full"
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        transition={{ delay: 0.72, duration: 0.5 }}
      >
        <svg className="absolute inset-0 h-full w-full overflow-visible" viewBox="0 0 330 130" fill="none" preserveAspectRatio="none" aria-hidden="true">
          <motion.path d="M8 110 C 70 22, 115 100, 168 45 S 242 18, 320 30" stroke="#14B8A6" strokeWidth="4" strokeLinecap="round" strokeDasharray="10 14" initial={{ pathLength: 0, opacity: 0 }} animate={{ pathLength: 1, opacity: 1 }} transition={{ delay: 0.86, duration: 1.3, ease: EASE }} />
          <motion.path d="M8 110 C 70 22, 115 100, 168 45 S 242 18, 320 30" stroke="#14B8A6" strokeWidth="18" strokeLinecap="round" opacity=".08" initial={{ pathLength: 0 }} animate={{ pathLength: 1 }} transition={{ delay: 0.86, duration: 1.3, ease: EASE }} />
        </svg>
        {[['8%', '76%', '01'], ['48%', '27%', '02'], ['94%', '12%', '03']].map(([left, top, label], index) => (
          <motion.div
            className="absolute flex h-[9vmin] w-[9vmin] items-center justify-center rounded-[3vmin] border border-white/80 bg-[#F5F0FF] font-[var(--font-display)] text-[2.8vmin] font-extrabold text-[#7C3AED] shadow-[0_1.6vmin_4vmin_rgba(77,39,128,.12)]"
            key={label}
            style={{ left, top }}
            initial={{ opacity: 0, scale: 0.6, rotate: -8 }}
            animate={{ opacity: 1, scale: 1, rotate: 0 }}
            transition={{ delay: 1.15 + index * 0.2, duration: 0.55, ease: EASE }}
          >
            {label}
          </motion.div>
        ))}
        <motion.div
          className="absolute bottom-[0.5vh] left-[22%] flex items-center gap-[2vw] rounded-full bg-[#28164F] px-[3.3vw] py-[1.35vh] text-[2.8vmin] font-semibold text-[#F5F0FF]"
          initial={{ opacity: 0, x: -18 }}
          animate={{ opacity: 1, x: 0 }}
          transition={{ delay: 1.55, duration: 0.6, ease: EASE }}
        >
          <span className="h-[1.8vmin] w-[1.8vmin] rounded-full bg-[#FBBF24]" />
          3 days in a row
        </motion.div>
      </motion.div>
    </SceneShell>
  );
}

function ReadyScene() {
  return (
    <SceneShell className="flex flex-col items-center justify-center" accent="purple">
      <motion.div
        className="absolute right-[7vw] top-[8vh] flex items-center gap-[1.8vw] text-[2.6vmin] font-bold uppercase tracking-[0.18em] text-[#7C3AED]"
        initial={{ opacity: 0, x: 18 }}
        animate={{ opacity: 1, x: 0 }}
        transition={{ delay: 0.25, duration: 0.55, ease: EASE }}
      >
        <span className="text-[#14B8A6]">05</span>
        / 05
      </motion.div>
      <motion.div
        className="mb-[5vh]"
        initial={{ scale: 0.75, opacity: 0, rotate: 8 }}
        animate={{ scale: [0.75, 1.06, 1], opacity: 1, rotate: 0 }}
        transition={{ duration: 1.05, ease: EASE }}
      >
        <BrandMark />
      </motion.div>
      <Wordmark align="center" eyebrow="your next clear moment" title={<>Make room<br /><span className="text-[#7C3AED]">for better.</span></>} body="TD-App is ready when you are." />
      <motion.div
        className="mt-[5.5vh] flex items-center gap-[2.4vw] text-[2.7vmin] font-bold uppercase tracking-[0.17em] text-[#695B82]"
        initial={{ opacity: 0, y: 16 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 1.18, duration: 0.7, ease: EASE }}
      >
        <span className="h-[1.2vmin] w-[1.2vmin] rounded-full bg-[#14B8A6]" />
        focus · reflect · grow
      </motion.div>
      <motion.div
        className="absolute bottom-[7vh] h-[0.8vmin] w-[24vw] overflow-hidden rounded-full bg-[#7C3AED]/12"
        initial={{ opacity: 0, scaleX: 0 }}
        animate={{ opacity: 1, scaleX: 1 }}
        transition={{ delay: 0.8, duration: 0.8, ease: EASE }}
      >
        <motion.div
          className="h-full w-[42%] rounded-full bg-[#14B8A6]"
          animate={{ x: ['0%', '140%'] }}
          transition={{ duration: 2.4, repeat: Infinity, ease: 'easeInOut' }}
        />
      </motion.div>
    </SceneShell>
  );
}
