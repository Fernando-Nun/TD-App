import {
  Accessibility,
  ArrowRight,
  Check,
  Clock3,
  Coins,
  Mic,
  Sparkles,
  Target,
} from 'lucide-react';
import { motion } from 'framer-motion';
import type { ReactNode } from 'react';

const ASSET_BASE = `${import.meta.env.BASE_URL}assets/`;

interface Chapter {
  eyebrow: string;
  title: ReactNode;
  copy: string;
  scene: string;
  narration: string;
}

export function LaunchScene({
  chapter,
  index,
  total,
}: {
  chapter: Chapter;
  index: number;
  total: number;
}) {
  return (
    <motion.main
      className={`launch-scene scene-${chapter.scene}`}
      initial={{ opacity: 0, scale: 1.035, filter: 'blur(8px)' }}
      animate={{ opacity: 1, scale: 1, filter: 'blur(0px)' }}
      exit={{ opacity: 0, scale: 0.97, filter: 'blur(10px)' }}
      transition={{ duration: 0.72, ease: [0.22, 0.8, 0.3, 1] }}
      aria-label={`Escena ${index + 1}: ${chapter.eyebrow}`}
    >
      <div className="scene-grain" aria-hidden="true" />
      <div className="scene-light scene-light-one" aria-hidden="true" />
      <div className="scene-light scene-light-two" aria-hidden="true" />
      <div className="scene-grid" aria-hidden="true" />

      <div className="scene-content">
        <motion.div
          className="scene-copy"
          initial={{ opacity: 0, y: 24 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.18, duration: 0.65, ease: 'easeOut' }}
        >
          <div className="scene-brand">
            <img src={`${ASSET_BASE}td-app-logo.png`} alt="" />
            <span>TD-App</span>
          </div>
          <p className="scene-eyebrow">{chapter.eyebrow}</p>
          <h1>{chapter.title}</h1>
          <p className="scene-description">{chapter.copy}</p>
          <div className="scene-narration">
            <span className="narration-pulse" />
            <span>{chapter.narration}</span>
          </div>
        </motion.div>

        <SceneArtwork scene={chapter.scene} />
      </div>

      <div className="scene-footer">
        <span>TD-APP / PRESENTACIÓN</span>
        <div
          className="scene-progress"
          role="progressbar"
          aria-label="Progreso de la presentación"
          aria-valuemin={1}
          aria-valuemax={total}
          aria-valuenow={index + 1}
        >
          <span style={{ width: `${((index + 1) / total) * 100}%` }} />
        </div>
        <span>{String(index + 1).padStart(2, '0')} — {String(total).padStart(2, '0')}</span>
      </div>
    </motion.main>
  );
}

function SceneArtwork({ scene }: { scene: string }) {
  if (scene === 'missions') return <MissionArtwork />;
  if (scene === 'focus') return <FocusArtwork />;
  if (scene === 'voice') return <VoiceArtwork />;
  if (scene === 'progress') return <ProgressArtwork />;
  if (scene === 'accessibility') return <AccessibilityArtwork />;
  if (scene === 'rewards') return <RewardsArtwork />;
  return <OpeningArtwork />;
}

function OpeningArtwork() {
  return (
    <div className="artwork artwork-opening">
      <div className="opening-orbit orbit-a" />
      <div className="opening-orbit orbit-b" />
      <motion.div
        className="opening-logo-coin"
        animate={{ rotateY: [0, 18, 0, -18, 0], y: [0, -12, 0] }}
        transition={{ duration: 7, repeat: Infinity, ease: 'easeInOut' }}
      >
        <span className="coin-side coin-side-back" />
        <img src={`${ASSET_BASE}td-app-logo.png`} alt="Logotipo de TD-App" />
      </motion.div>
      <div className="opening-phone-position">
        <motion.div
          className="opening-phone"
          initial={{ opacity: 0, rotateY: -35, rotateX: 14, y: 50 }}
          animate={{ opacity: 1, rotateY: -17, rotateX: 7, y: 0 }}
          transition={{ delay: 0.32, duration: 1.15, ease: [0.16, 1, 0.3, 1] }}
        >
          <div className="phone-frame">
            <div className="phone-notch" />
            <div className="phone-screen">
              <div className="phone-mini-brand">
                <img src={`${ASSET_BASE}td-app-logo.png`} alt="" />
                <span>TD-App</span>
              </div>
              <span className="screen-kicker">TU SIGUIENTE PASO</span>
              <strong>Hazlo<br />posible.</strong>
              <div className="screen-task">
                <Target size={17} />
                <span>Una misión a la vez</span>
              </div>
              <div className="screen-progress">
                <span />
              </div>
              <span className="screen-small">Hoy también cuenta.</span>
            </div>
          </div>
          <div className="phone-side-button" />
        </motion.div>
      </div>
      <motion.div
        className="floating-stat opening-stat"
        animate={{ y: [0, -7, 0], rotateZ: [0, 1, 0] }}
        transition={{ duration: 4.5, repeat: Infinity, ease: 'easeInOut' }}
      >
        <Sparkles size={16} />
        <span>Un paso a la vez</span>
      </motion.div>
    </div>
  );
}

function MissionArtwork() {
  return (
    <div className="artwork artwork-missions">
      <div className="mission-orbit mission-orbit-back" />
      <div className="mission-card-position">
        <motion.div
          className="mission-card mission-card-main"
          initial={{ opacity: 0, rotateY: 24, rotateX: -9, x: 45 }}
          animate={{ opacity: 1, rotateY: 13, rotateX: -5, x: 0 }}
          transition={{ delay: 0.18, duration: 0.8, ease: 'easeOut' }}
        >
          <div className="mission-card-top">
            <span className="mission-icon"><Target size={21} /></span>
            <span className="mission-label">MISIÓN DE HOY</span>
            <span className="mission-menu">•••</span>
          </div>
          <h2>Preparar mi<br />presentación</h2>
          <p>Un objetivo claro. Un primer paso posible.</p>
          <div className="mission-divider" />
          <div className="mission-check-row"><span className="empty-check" />Abrir mis notas</div>
          <div className="mission-check-row"><span className="empty-check" />Elegir una idea</div>
          <div className="mission-check-row done"><span className="filled-check"><Check size={13} /></span>Empezar sin prisa</div>
          <div className="mission-card-foot"><span>Tu ritmo · Tu misión</span><ArrowRight size={17} /></div>
        </motion.div>
      </div>
      <motion.div
        className="mission-floating-note"
        animate={{ y: [0, -10, 0], rotateZ: [-5, -2, -5] }}
        transition={{ duration: 5.2, repeat: Infinity, ease: 'easeInOut' }}
      >
        <span>01</span>
        <strong>Hazlo pequeño.</strong>
      </motion.div>
      <div className="artwork-orbit-dot mission-dot" />
    </div>
  );
}

function FocusArtwork() {
  return (
    <div className="artwork artwork-focus">
      <div className="focus-halo" />
      <div className="focus-ring-position">
        <motion.div
          className="focus-ring"
          animate={{ rotate: 360 }}
          transition={{ duration: 28, repeat: Infinity, ease: 'linear' }}
        >
          <span className="ring-dot ring-dot-a" />
          <span className="ring-dot ring-dot-b" />
        </motion.div>
      </div>
      <div className="focus-timer-card">
        <div className="timer-icon"><Clock3 size={18} /></div>
        <span className="timer-label">BLOQUE DE ENFOQUE</span>
        <strong>25<span>:</span>00</strong>
        <div className="timer-progress"><span /></div>
        <p>Un momento a la vez.</p>
        <div className="timer-state"><span /> EN MARCHA</div>
      </div>
      <motion.div
        className="timer-orb"
        animate={{ y: [0, -13, 0], scale: [1, 1.04, 1] }}
        transition={{ duration: 4, repeat: Infinity, ease: 'easeInOut' }}
      >
        <span />
      </motion.div>
      <div className="focus-caption">PRESENTE<br />EN LO QUE IMPORTA</div>
    </div>
  );
}

function VoiceArtwork() {
  const bars = [22, 34, 52, 38, 66, 84, 48, 31, 72, 94, 58, 36, 63, 43, 78, 51, 28, 56, 36];
  return (
    <div className="artwork artwork-voice">
      <div className="voice-sound-orbit" />
      <div className="voice-mic-position">
        <motion.div
          className="voice-mic-disc"
          animate={{ rotateY: [0, 14, 0, -14, 0], y: [0, -8, 0] }}
          transition={{ duration: 6, repeat: Infinity, ease: 'easeInOut' }}
        >
          <div className="mic-icon-wrap"><Mic size={32} /></div>
          <span>HABLA A TU RITMO</span>
        </motion.div>
      </div>
      <div className="voice-wave-card-position">
        <motion.div
          className="voice-wave-card"
          initial={{ opacity: 0, rotateY: -25, x: 50 }}
          animate={{ opacity: 1, rotateY: -10, x: 0 }}
          transition={{ delay: 0.26, duration: 0.8, ease: 'easeOut' }}
        >
          <div className="wave-card-header">
            <span className="wave-live"><i /> REFLEXIÓN GUARDADA</span>
            <span>HOY</span>
          </div>
          <div className="waveform" aria-hidden="true">
            {bars.map((height, index) => (
              <motion.span
                key={index}
                style={{ height: `${height}%` }}
                animate={{ scaleY: [0.72, 1, 0.82] }}
                transition={{ duration: 1.2 + (index % 4) * 0.15, repeat: Infinity, delay: index * 0.035, ease: 'easeInOut' }}
              />
            ))}
          </div>
          <p>“Hoy avancé a mi ritmo.<br />Eso también cuenta.”</p>
          <div className="wave-card-foot"><span>Tu reflexión · Tu momento</span><Check size={16} /></div>
        </motion.div>
      </div>
      <div className="voice-note-bubble">Tu voz también es progreso</div>
    </div>
  );
}

function ProgressArtwork() {
  return (
    <div className="artwork artwork-progress">
      <div className="coin-orbit-path coin-path-one" />
      <div className="coin-orbit-path coin-path-two" />
      <motion.div
        className="td-coin-3d"
        animate={{ rotateY: [0, 360], rotateZ: [0, 4, 0] }}
        transition={{ rotateY: { duration: 8, repeat: Infinity, ease: 'linear' }, rotateZ: { duration: 3.8, repeat: Infinity, ease: 'easeInOut' } }}
      >
        <div className="coin-face">
          <img src={`${ASSET_BASE}td-app-logo.png`} alt="" />
          <span>TD-COIN</span>
        </div>
        <div className="coin-edge" />
      </motion.div>
      <div className="progress-card-position">
        <motion.div
          className="progress-card progress-card-left"
          initial={{ opacity: 0, rotateY: 22, x: 35 }}
          animate={{ opacity: 1, rotateY: 9, x: 0 }}
          transition={{ delay: 0.2, duration: 0.7 }}
        >
          <span className="progress-card-label">TU CONSTANCIA</span>
          <strong>Se nota.</strong>
          <div className="progress-dots"><i /><i /><i /><i /><i /><i /><i /></div>
          <span className="progress-card-caption">Un avance a la vez</span>
        </motion.div>
      </div>
      <motion.div
        className="progress-pill"
        animate={{ y: [0, -7, 0], rotateZ: [2, 0, 2] }}
        transition={{ duration: 4.3, repeat: Infinity, ease: 'easeInOut' }}
      >
        <Coins size={17} /> TD-Coins
      </motion.div>
      <div className="progress-spark spark-a">✦</div>
      <div className="progress-spark spark-b">✦</div>
    </div>
  );
}

function AccessibilityArtwork() {
  return (
    <div className="artwork artwork-accessibility">
      <motion.div
        className="access-phone"
        initial={{ opacity: 0, rotateY: -28, rotateX: 10, y: 24 }}
        animate={{ opacity: 1, rotateY: -14, rotateX: 5, y: 0 }}
        transition={{ delay: 0.15, duration: 0.8 }}
      >
        <div className="access-phone-notch" />
        <div className="access-screen">
          <div className="access-brand"><img src={`${ASSET_BASE}td-app-logo.png`} alt="" /><span>TD-App</span></div>
          <span className="access-kicker">TU PLAN DE HOY</span>
          <strong>Pasos que<br />se adaptan a ti.</strong>
          <div className="access-plan-row"><span className="access-plan-icon"><Target size={15} /></span><span><b>Misión posible</b><small>Empieza cuando quieras</small></span><Check size={16} /></div>
          <div className="access-plan-row"><span className="access-plan-icon teal"><Accessibility size={15} /></span><span><b>Apoyos visuales</b><small>Claridad en cada paso</small></span><Check size={16} /></div>
        </div>
      </motion.div>
      <motion.div
        className="accessibility-float-card access-float-one"
        animate={{ y: [0, -8, 0], rotateZ: [-4, -2, -4] }}
        transition={{ duration: 4.8, repeat: Infinity, ease: 'easeInOut' }}
      >
        <Accessibility size={17} /> Accesible
      </motion.div>
      <motion.div
        className="accessibility-float-card access-float-two"
        animate={{ y: [0, 7, 0], rotateZ: [3, 1, 3] }}
        transition={{ duration: 5.2, repeat: Infinity, ease: 'easeInOut' }}
      >
        <Sparkles size={17} /> Personalizado
      </motion.div>
      <div className="access-orbit" />
    </div>
  );
}

function RewardsArtwork() {
  return (
    <div className="artwork artwork-rewards">
      <div className="reward-showcase-glow" />
      <div className="reward-platform platform-back" />
      <div className="reward-platform platform-front" />
      <motion.div
        className="reward-product reward-ball"
        initial={{ opacity: 0, y: 30, rotateY: -25 }}
        animate={{ opacity: 1, y: 0, rotateY: -12 }}
        transition={{ delay: 0.1, duration: 0.8 }}
      >
        <img src={`${ASSET_BASE}pelota.png`} alt="Pelota antiestrés TD-App" />
      </motion.div>
      <motion.div
        className="reward-product reward-mug"
        initial={{ opacity: 0, y: 35, rotateY: 20 }}
        animate={{ opacity: 1, y: 0, rotateY: 12 }}
        transition={{ delay: 0.3, duration: 0.8 }}
      >
        <img src={`${ASSET_BASE}taza.png`} alt="Taza de TD-App" />
      </motion.div>
      <motion.div
        className="reward-product reward-backpack"
        initial={{ opacity: 0, y: 28, rotateY: -16 }}
        animate={{ opacity: 1, y: 0, rotateY: -8 }}
        transition={{ delay: 0.45, duration: 0.8 }}
      >
        <img src={`${ASSET_BASE}mochila.png`} alt="Mochila TD-App" />
      </motion.div>
      <div className="reward-keychain">
        <img src={`${ASSET_BASE}llavero.png`} alt="Llavero TD-App" />
      </div>
      <motion.div
        className="reward-final-badge"
        animate={{ y: [0, -8, 0], rotateZ: [3, 0, 3] }}
        transition={{ duration: 4.3, repeat: Infinity, ease: 'easeInOut' }}
      >
        <Sparkles size={17} />
        <span>CELEBRA CADA PASO</span>
      </motion.div>
    </div>
  );
}