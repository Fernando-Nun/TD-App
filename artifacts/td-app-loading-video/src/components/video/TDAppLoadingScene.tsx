import { motion } from 'framer-motion';

const BASE_URL = import.meta.env.BASE_URL;
const LOGO_SRC = `${BASE_URL}assets/td-app-logo.png`;

type SceneProps = { currentScene: number };

export function TDAppLoadingScene(_props: SceneProps) {
  return (
    <div
      className="absolute inset-0 overflow-hidden"
      style={{
        background:
          'radial-gradient(circle at 50% 43%, rgba(255,255,255,.94), transparent 24%), radial-gradient(circle at 12% 10%, rgba(124,58,237,.12), transparent 34%), radial-gradient(circle at 90% 88%, rgba(20,184,166,.16), transparent 38%), linear-gradient(145deg, #f8f5ff 0%, #eee9fb 52%, #e5f4f2 100%)',
      }}
    >
      <LoadingAtmosphere />
      <LoadingCore />
    </div>
  );
}

function LoadingAtmosphere() {
  const convergingElements = [
    { x: 4, y: 17, targetX: 47, targetY: 33, rotate: 22, size: 'w-[1.2vmin] h-[4.4vmin]', color: 'violet' },
    { x: 92, y: 11, targetX: -42, targetY: 37, rotate: -34, size: 'w-[1vmin] h-[3.8vmin]', color: 'teal' },
    { x: 12, y: 74, targetX: 38, targetY: -26, rotate: -42, size: 'w-[1.4vmin] h-[1.4vmin]', color: 'teal' },
    { x: 88, y: 81, targetX: -38, targetY: -29, rotate: 38, size: 'w-[1.2vmin] h-[1.2vmin]', color: 'violet' },
    { x: 21, y: 4, targetX: 28, targetY: 43, rotate: 12, size: 'w-[0.9vmin] h-[2.8vmin]', color: 'violet' },
    { x: 78, y: 3, targetX: -28, targetY: 45, rotate: -16, size: 'w-[0.9vmin] h-[2.8vmin]', color: 'teal' },
    { x: 3, y: 44, targetX: 45, targetY: 3, rotate: 90, size: 'w-[1vmin] h-[3.2vmin]', color: 'teal' },
    { x: 97, y: 52, targetX: -45, targetY: -5, rotate: 90, size: 'w-[1vmin] h-[3.2vmin]', color: 'violet' },
    { x: 31, y: 94, targetX: 18, targetY: -44, rotate: -24, size: 'w-[1.1vmin] h-[3.4vmin]', color: 'violet' },
    { x: 67, y: 96, targetX: -18, targetY: -45, rotate: 24, size: 'w-[1.1vmin] h-[3.4vmin]', color: 'teal' },
    { x: 9, y: 28, targetX: 40, targetY: 22, rotate: 58, size: 'w-[0.85vmin] h-[0.85vmin]', color: 'teal' },
    { x: 91, y: 30, targetX: -40, targetY: 20, rotate: -58, size: 'w-[0.85vmin] h-[0.85vmin]', color: 'violet' },
    { x: 18, y: 88, targetX: 33, targetY: -36, rotate: 18, size: 'w-[0.8vmin] h-[2.5vmin]', color: 'teal' },
    { x: 83, y: 89, targetX: -33, targetY: -37, rotate: -18, size: 'w-[0.8vmin] h-[2.5vmin]', color: 'violet' },
  ];

  return (
    <>
      <motion.div
        aria-hidden="true"
        className="td-loading-aura absolute left-1/2 top-1/2 h-[78vmin] w-[78vmin] rounded-full"
        style={{ marginLeft: '-39vmin', marginTop: '-39vmin' }}
        animate={{ scale: [0.94, 1.04, 0.94], opacity: [0.5, 0.8, 0.5] }}
        transition={{ duration: 4.8, repeat: Infinity, ease: 'easeInOut' }}
      />
      <motion.div
        aria-hidden="true"
        className="td-loading-orbit absolute left-1/2 top-1/2 h-[66vmin] w-[66vmin] rounded-full"
        style={{ marginLeft: '-33vmin', marginTop: '-33vmin' }}
        animate={{ rotate: 360 }}
        transition={{ duration: 16, repeat: Infinity, ease: 'linear' }}
      />
      <motion.div
        aria-hidden="true"
        className="td-loading-orbit td-loading-orbit-secondary absolute left-1/2 top-1/2 h-[48vmin] w-[48vmin] rounded-full"
        style={{ marginLeft: '-24vmin', marginTop: '-24vmin' }}
        animate={{ rotate: -360 }}
        transition={{ duration: 11, repeat: Infinity, ease: 'linear' }}
      />
      <motion.div
        aria-hidden="true"
        className="td-loading-orbit-dot absolute left-1/2 top-1/2 h-[2.1vmin] w-[2.1vmin] rounded-full"
        animate={{ rotate: 360 }}
        transition={{ duration: 16, repeat: Infinity, ease: 'linear' }}
      />
      <motion.div
        aria-hidden="true"
        className="td-loading-orbit-dot td-loading-orbit-dot-secondary absolute left-1/2 top-1/2 h-[1.4vmin] w-[1.4vmin] rounded-full"
        animate={{ rotate: -360 }}
        transition={{ duration: 11, repeat: Infinity, ease: 'linear' }}
      />
      <div aria-hidden="true" className="absolute inset-0">
        {convergingElements.map((element, index) => (
          <motion.span
            className={`td-converging-element ${element.size} td-converging-${element.color} absolute rounded-full`}
            key={`${element.x}-${element.y}`}
            style={{ left: `${element.x}%`, top: `${element.y}%` }}
            animate={{
              x: ['0vw', `${element.targetX}vw`, `${element.targetX}vw`, '0vw'],
              y: ['0vh', `${element.targetY}vh`, `${element.targetY}vh`, '0vh'],
              opacity: [0, 0.35, 0.82, 0],
              scale: [0.55, 0.9, 1, 0.45],
              rotate: [element.rotate - 18, element.rotate, element.rotate + 10, element.rotate + 26],
            }}
            transition={{
              duration: 5,
              delay: index * 0.055,
              repeat: Infinity,
              ease: [0.22, 0.8, 0.26, 1],
              times: [0, 0.58, 0.76, 1],
            }}
          />
        ))}
      </div>
      <div aria-hidden="true" className="td-edge-mark td-edge-mark-top absolute left-[11%] top-[18%] h-[11vmin] w-[11vmin]" />
      <div aria-hidden="true" className="td-edge-mark td-edge-mark-bottom absolute bottom-[16%] right-[10%] h-[9vmin] w-[9vmin]" />
      <div aria-hidden="true" className="td-grain" />
    </>
  );
}

function LoadingCore() {
  return (
    <main className="absolute inset-0 flex flex-col items-center justify-center">
      <motion.div
        className="td-logo-stage relative flex h-[44vmin] w-[44vmin] items-center justify-center"
        animate={{ scale: [0.96, 0.98, 1.02, 0.98, 0.96] }}
        transition={{ duration: 5, repeat: Infinity, ease: 'easeInOut', times: [0, 0.42, 0.72, 0.86, 1] }}
      >
        <motion.div
          aria-hidden="true"
          className="td-loading-halo absolute inset-[-3vmin] rounded-full"
          animate={{ scale: [0.9, 0.96, 1.12, 1.03, 0.9], opacity: [0.16, 0.22, 0.7, 0.18, 0.16] }}
          transition={{ duration: 5, repeat: Infinity, ease: 'easeInOut', times: [0, 0.42, 0.72, 0.84, 1] }}
        />
        <motion.div
          aria-hidden="true"
          className="td-logo-sketch absolute inset-0 z-[1]"
          animate={{ opacity: [0.42, 0.58, 0.22, 0.08, 0.42], scale: [0.98, 1, 1.01, 1.01, 0.98] }}
          transition={{ duration: 5, repeat: Infinity, ease: 'easeInOut', times: [0, 0.42, 0.72, 0.86, 1] }}
        >
          <img alt="" className="h-full w-full object-contain" src={LOGO_SRC} />
        </motion.div>
        <motion.div
          aria-hidden="true"
          className="td-logo-trace absolute inset-[-1.2vmin] z-[2] rounded-full"
          animate={{ opacity: [0.4, 0.62, 0.1, 0.08, 0.4], rotate: [0, 4, 0, -3, 0] }}
          transition={{ duration: 5, repeat: Infinity, ease: 'easeInOut', times: [0, 0.42, 0.72, 0.86, 1] }}
        />
        <img
          alt="TD-App"
          className="td-logo-final relative z-10 h-full w-full object-contain"
          src={LOGO_SRC}
          style={{ animation: 'td-logo-reveal 5s cubic-bezier(.22,.8,.26,1) infinite' }}
        />
        <motion.div
          aria-hidden="true"
          className="td-logo-highlight pointer-events-none absolute inset-[-2vmin] z-20 rounded-full"
          animate={{ opacity: [0, 0, 0.9, 0, 0], scale: [0.82, 0.94, 1.12, 1.2, 0.82] }}
          transition={{ duration: 5, repeat: Infinity, ease: 'easeOut', times: [0, 0.56, 0.72, 0.84, 1] }}
        />
      </motion.div>
      <motion.div
        aria-label="Loading"
        className="td-loading-bar mt-[7vh] h-[0.85vmin] w-[24vw] overflow-hidden rounded-full"
        initial={{ opacity: 0, y: 8 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.8, ease: [0.16, 1, 0.3, 1] }}
      >
        <motion.div
          className="td-loading-shimmer h-full w-[38%] rounded-full"
          animate={{ x: ['-125%', '300%'] }}
          transition={{ duration: 1.65, repeat: Infinity, ease: 'easeInOut' }}
        />
      </motion.div>
      <div aria-hidden="true" className="mt-[2.2vh] flex gap-[1.4vmin]">
        {[0, 1, 2].map((dot) => (
          <motion.span
            className="h-[1.25vmin] w-[1.25vmin] rounded-full bg-[#7c3aed]/55"
            key={dot}
            animate={{ opacity: [0.3, 1, 0.3], scale: [0.8, 1, 0.8] }}
            transition={{ duration: 1.2, delay: dot * 0.18, repeat: Infinity, ease: 'easeInOut' }}
          />
        ))}
      </div>
    </main>
  );
}