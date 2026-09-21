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
      <div aria-hidden="true" className="td-grain" />
    </>
  );
}

function LoadingCore() {
  return (
    <main className="absolute inset-0 flex flex-col items-center justify-center">
      <motion.div
        className="relative flex h-[42vmin] w-[42vmin] items-center justify-center"
        animate={{ scale: [0.985, 1.02, 0.985] }}
        transition={{ duration: 3.8, repeat: Infinity, ease: 'easeInOut' }}
      >
        <motion.div
          aria-hidden="true"
          className="td-loading-halo absolute inset-[-4vmin] rounded-full"
          animate={{ scale: [0.9, 1.1, 0.9], opacity: [0.45, 0.16, 0.45] }}
          transition={{ duration: 3.1, repeat: Infinity, ease: 'easeInOut' }}
        />
        <img
          alt="TD-App"
          className="relative z-10 h-full w-full object-contain drop-shadow-[0_2.5vmin_4vmin_rgba(58,36,112,.16)]"
          src={LOGO_SRC}
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