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
  return (
    <>
      <motion.div
        aria-hidden="true"
        className="td-loading-aura absolute left-1/2 top-1/2 h-[78vmin] w-[78vmin] rounded-full"
        style={{ marginLeft: '-39vmin', marginTop: '-39vmin' }}
        animate={{ scale: [0.92, 0.98, 1.05, 0.98, 0.92], opacity: [0, 0.12, 0.2, 0.08, 0] }}
        transition={{ duration: 5, repeat: Infinity, ease: 'easeInOut', times: [0, 0.22, 0.72, 0.88, 1] }}
      />
      <div aria-hidden="true" className="td-edge-mark td-edge-mark-top absolute left-[11%] top-[18%] h-[11vmin] w-[11vmin]" />
      <div aria-hidden="true" className="td-edge-mark td-edge-mark-bottom absolute bottom-[16%] right-[10%] h-[9vmin] w-[9vmin]" />
      <div aria-hidden="true" className="td-edge-corner td-edge-corner-left absolute left-[5%] top-[42%] h-[12vmin] w-[4vmin]" />
      <div aria-hidden="true" className="td-edge-corner td-edge-corner-right absolute right-[5%] top-[53%] h-[12vmin] w-[4vmin]" />
      <div aria-hidden="true" className="td-grain" />
    </>
  );
}

type LogoFragment = {
  key: string;
  clipPath?: string;
  maskImage?: string;
  startX: number;
  startY: number;
  curveX: number;
  curveY: number;
  rotate: number;
  finalRotate: number;
  start: number;
  settle: number;
};

const logoFragments: LogoFragment[] = [
  {
    key: 'outer-ring-sweep',
    clipPath: 'circle(50% at 50% 50%)',
    maskImage:
      'radial-gradient(circle at 50% 50%, transparent 0 39%, #000 40% 51%, transparent 52%), conic-gradient(from 206deg at 50% 50%, transparent 0 11%, #000 12% 29%, transparent 30% 100%)',
    startX: -56,
    startY: -8,
    curveX: -26,
    curveY: 15,
    rotate: -58,
    finalRotate: 0,
    start: 0.08,
    settle: 0.73,
  },
  {
    key: 'outer-ring-crown',
    clipPath: 'circle(50% at 50% 50%)',
    maskImage:
      'radial-gradient(circle at 50% 50%, transparent 0 39%, #000 40% 51%, transparent 52%), conic-gradient(from 260deg at 50% 50%, transparent 0 11%, #000 12% 25%, transparent 26% 100%)',
    startX: 17,
    startY: -59,
    curveX: -13,
    curveY: -30,
    rotate: 38,
    finalRotate: 0,
    start: 0.2,
    settle: 0.77,
  },
  {
    key: 'outer-ring-low',
    clipPath: 'circle(50% at 50% 50%)',
    maskImage:
      'radial-gradient(circle at 50% 50%, transparent 0 39%, #000 40% 51%, transparent 52%), conic-gradient(from 73deg at 50% 50%, transparent 0 10%, #000 11% 28%, transparent 29% 100%)',
    startX: 53,
    startY: 52,
    curveX: 28,
    curveY: 31,
    rotate: 116,
    finalRotate: 0,
    start: 0.26,
    settle: 0.82,
  },
  {
    key: 'inner-arcs',
    clipPath: 'inset(16% 17% 39% 17%)',
    startX: -48,
    startY: 18,
    curveX: -22,
    curveY: -16,
    rotate: -72,
    finalRotate: 0,
    start: 0.12,
    settle: 0.7,
  },
  {
    key: 'brain-left',
    clipPath: 'polygon(31% 29%, 49% 27%, 50% 49%, 32% 48%)',
    startX: -48,
    startY: -18,
    curveX: -25,
    curveY: 10,
    rotate: -34,
    finalRotate: 0,
    start: 0.23,
    settle: 0.78,
  },
  {
    key: 'brain-right',
    clipPath: 'polygon(50% 27%, 69% 29%, 68% 49%, 50% 49%)',
    startX: 50,
    startY: -7,
    curveX: 23,
    curveY: -21,
    rotate: 51,
    finalRotate: 0,
    start: 0.17,
    settle: 0.75,
  },
  {
    key: 'head-face',
    clipPath: 'circle(18% at 50% 45%)',
    startX: -52,
    startY: 29,
    curveX: -18,
    curveY: 11,
    rotate: -40,
    finalRotate: 0,
    start: 0.3,
    settle: 0.8,
  },
  {
    key: 'body-linework',
    clipPath: 'polygon(25% 47%, 76% 47%, 78% 75%, 21% 75%)',
    startX: 52,
    startY: 30,
    curveX: 23,
    curveY: -9,
    rotate: 47,
    finalRotate: 0,
    start: 0.34,
    settle: 0.84,
  },
  {
    key: 'star',
    clipPath: 'polygon(46% 22%, 50% 14%, 54% 22%, 61% 25%, 54% 28%, 50% 36%, 46% 28%, 39% 25%)',
    startX: 40,
    startY: -49,
    curveX: 19,
    curveY: -17,
    rotate: 93,
    finalRotate: 0,
    start: 0.25,
    settle: 0.72,
  },
  {
    key: 'ticks',
    clipPath: 'polygon(21% 20%, 31% 20%, 31% 29%, 21% 29%, 69% 20%, 79% 20%, 79% 29%, 69% 29%, 45% 16%, 55% 16%, 55% 27%, 45% 27%)',
    startX: -7,
    startY: -56,
    curveX: 27,
    curveY: -26,
    rotate: -84,
    finalRotate: 0,
    start: 0.39,
    settle: 0.8,
  },
  {
    key: 'button',
    clipPath: 'circle(4.5% at 50% 65%)',
    startX: 52,
    startY: 10,
    curveX: 20,
    curveY: 28,
    rotate: 76,
    finalRotate: 0,
    start: 0.42,
    settle: 0.82,
  },
  {
    key: 'wordmark',
    clipPath: 'inset(74% 25% 5% 25%)',
    startX: 5,
    startY: 58,
    curveX: -24,
    curveY: 21,
    rotate: -28,
    finalRotate: 0,
    start: 0.47,
    settle: 0.87,
  },
];

function LoadingCore() {
  return (
    <main className="absolute inset-0 flex flex-col items-center justify-center">
      <motion.div
        className="td-logo-stage relative flex h-[44vmin] w-[44vmin] items-center justify-center"
        animate={{ scale: [0.98, 0.99, 1.01, 1.02, 0.98] }}
        transition={{ duration: 5, repeat: Infinity, ease: 'easeInOut', times: [0, 0.45, 0.72, 0.86, 1] }}
      >
        {logoFragments.map((fragment) => (
          <motion.div
            aria-hidden="true"
            className="td-logo-fragment absolute inset-0"
            key={fragment.key}
            style={{
              clipPath: fragment.clipPath,
              maskImage: fragment.maskImage,
              WebkitMaskImage: fragment.maskImage,
            }}
            animate={{
              x: [`${fragment.startX}vw`, `${fragment.startX}vw`, `${fragment.curveX}vw`, '0vw', '0vw', `${fragment.startX}vw`],
              y: [`${fragment.startY}vh`, `${fragment.startY}vh`, `${fragment.curveY}vh`, '0vh', '0vh', `${fragment.startY}vh`],
              opacity: [0, 0, 0.82, 1, 1, 0],
              scale: [0.7, 0.7, 1.08, 1, 1.01, 0.7],
              rotate: [fragment.rotate, fragment.rotate, fragment.rotate * -0.4, fragment.finalRotate, fragment.finalRotate, fragment.rotate],
            }}
            transition={{
              duration: 5,
              repeat: Infinity,
              ease: [0.2, 0.76, 0.22, 1],
              times: [0, fragment.start, fragment.start + 0.12, fragment.settle, 0.88, 1],
            }}
          >
            <img alt="" className="h-full w-full object-contain" src={LOGO_SRC} />
          </motion.div>
        ))}
        <motion.img
          alt="TD-App"
          className="td-logo-complete pointer-events-none absolute inset-0 z-10 h-full w-full object-contain"
          src={LOGO_SRC}
          animate={{
            opacity: [0, 0, 0, 1, 1, 0],
            scale: [0.94, 0.94, 0.97, 1, 1.012, 0.94],
            filter: [
              'grayscale(1) brightness(1.2) drop-shadow(0 0 0 rgba(112,70,201,0))',
              'grayscale(1) brightness(1.2) drop-shadow(0 0 0 rgba(112,70,201,0))',
              'grayscale(0.5) brightness(1.05) drop-shadow(0 0 0 rgba(112,70,201,0))',
              'grayscale(0) brightness(1) drop-shadow(0 2.5vmin 4vmin rgba(58,36,112,.16))',
              'grayscale(0) brightness(1.07) drop-shadow(0 2.5vmin 4.8vmin rgba(112,70,201,.24))',
              'grayscale(1) brightness(1.2) drop-shadow(0 0 0 rgba(112,70,201,0))',
            ],
          }}
          transition={{
            duration: 5,
            repeat: Infinity,
            ease: [0.2, 0.76, 0.22, 1],
            times: [0, 0.62, 0.71, 0.76, 0.86, 1],
          }}
        />
        <motion.div
          aria-hidden="true"
          className="td-loading-halo pointer-events-none absolute inset-[-3vmin] z-20 rounded-full"
          animate={{ scale: [0.88, 0.92, 1, 1.12, 0.88], opacity: [0, 0, 0, 0.64, 0] }}
          transition={{ duration: 5, repeat: Infinity, ease: 'easeOut', times: [0, 0.62, 0.73, 0.84, 1] }}
        />
        <motion.div
          aria-hidden="true"
          className="td-logo-highlight pointer-events-none absolute inset-[-2vmin] z-20 rounded-full"
          animate={{ opacity: [0, 0, 0, 0.9, 0], scale: [0.82, 0.9, 0.98, 1.14, 0.82] }}
          transition={{ duration: 5, repeat: Infinity, ease: 'easeOut', times: [0, 0.62, 0.73, 0.84, 1] }}
        />
      </motion.div>
      <motion.div
        aria-label="Loading"
        className="td-loading-bar mt-[7vh] h-[0.85vmin] w-[24vw] overflow-hidden rounded-full"
        animate={{ opacity: [0, 0, 0.5, 0.5, 0], y: [8, 8, 0, 0, 8] }}
        transition={{ duration: 5, repeat: Infinity, ease: 'easeInOut', times: [0, 0.5, 0.68, 0.88, 1] }}
      >
        <motion.div
          className="td-loading-shimmer h-full w-[38%] rounded-full"
          animate={{ x: ['-125%', '300%'] }}
          transition={{ duration: 1.65, repeat: Infinity, ease: 'easeInOut' }}
        />
      </motion.div>
      <motion.div
        aria-hidden="true"
        className="mt-[2.2vh] flex gap-[1.4vmin]"
        animate={{ opacity: [0, 0, 0.7, 0.7, 0] }}
        transition={{ duration: 5, repeat: Infinity, ease: 'easeInOut', times: [0, 0.56, 0.7, 0.88, 1] }}
      >
        {[0, 1, 2].map((dot) => (
          <motion.span
            className="h-[1.25vmin] w-[1.25vmin] rounded-full bg-[#7c3aed]/55"
            key={dot}
            animate={{ opacity: [0.3, 1, 0.3], scale: [0.8, 1, 0.8] }}
            transition={{ duration: 1.2, delay: dot * 0.18, repeat: Infinity, ease: 'easeInOut' }}
          />
        ))}
      </motion.div>
    </main>
  );
}