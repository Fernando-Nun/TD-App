import {
  VideoCanvas,
  type VideoAspectRatio,
  useVideoPlayer,
} from '@/lib/video';
import { AnimatePresence } from 'framer-motion';

import { LaunchScene } from './LaunchScenes';

const SCENE_DURATIONS = {
  opening: 8000,
  missions: 8000,
  focus: 8000,
  voice: 8000,
  progress: 8000,
  accessibility: 8000,
  rewards: 8000,
};

const VIDEO_ASPECT_RATIO: VideoAspectRatio = '16:9';

const chapters = [
  {
    eyebrow: 'TD-APP · UN PASO A LA VEZ',
    title: <>A veces, lo más difícil<br />es <em>empezar.</em></>,
    copy: 'Una idea amable para convertir tus pequeños avances en algo que puedes ver y celebrar.',
    scene: 'opening',
    narration: 'A veces, lo más difícil es empezar. TD-App transforma ese primer paso en algo pequeño, claro y posible.',
  },
  {
    eyebrow: '01 · MISIONES Y HÁBITOS',
    title: <>Empieza por algo<br /><em>posible.</em></>,
    copy: 'Elige una misión y llévala a pasos concretos, a tu propio ritmo.',
    scene: 'missions',
    narration: 'Elige una misión o un hábito y conviértelo en un paso concreto que puedas empezar hoy.',
  },
  {
    eyebrow: '02 · BLOQUES DE ENFOQUE',
    title: <>Un bloque.<br />Un momento a la <em>vez.</em></>,
    copy: 'Un temporizador sencillo te ayuda a entrar, estar y cerrar una tarea.',
    scene: 'focus',
    narration: 'Cuando estés listo, inicia un bloque de enfoque. Un momento a la vez, sin exigirte hacerlo perfecto.',
  },
  {
    eyebrow: '03 · REFLEXIÓN POR VOZ',
    title: <>Dale voz a<br />lo que <em>avanzaste.</em></>,
    copy: 'Guarda cómo te fue hablando, sin tener que sentarte a escribir.',
    scene: 'voice',
    narration: 'Al terminar, registra cómo te fue con una reflexión por voz y conserva una señal de tu progreso.',
  },
  {
    eyebrow: '04 · PROGRESO Y TD-COINS',
    title: <>Lo que hiciste<br /><em>sí cuenta.</em></>,
    copy: 'Revisa tus avances y celebra la constancia con TD-Coins.',
    scene: 'progress',
    narration: 'Tus avances se hacen visibles. Cada pequeño logro cuenta y se convierte en TD-Coins para celebrar tu constancia.',
  },
  {
    eyebrow: '05 · A TU MANERA',
    title: <>Apoyos para<br />tu propio <em>ritmo.</em></>,
    copy: 'Accesibilidad y planes personalizados para que el siguiente paso se sienta más claro.',
    scene: 'accessibility',
    narration: 'Encuentra apoyos accesibles y planes personalizados para avanzar de una manera que se adapte a ti.',
  },
  {
    eyebrow: 'TD-APP · DESCUBRE TU SIGUIENTE PASO',
    title: <>Hazlo pequeño.<br />Hazlo <em>posible.</em></>,
    copy: 'Misiones, enfoque y recompensas. TD-App para Android.',
    scene: 'rewards',
    narration: 'Y recuerda: celebrar también es parte del camino. Descarga TD-App para Android y empieza con un paso posible.',
  },
];

export default function VideoTemplate() {
  const { currentScene } = useVideoPlayer({
    durations: SCENE_DURATIONS,
    loop: true,
  });

  return (
    <VideoCanvas
      aspectRatio={VIDEO_ASPECT_RATIO}
      className="td-launch-video"
      style={{ backgroundColor: '#170d29' }}
    >
      <AnimatePresence mode="wait">
        {chapters.map(
          (chapter, index) =>
            currentScene === index && (
              <LaunchScene
                key={chapter.scene}
                index={index}
                chapter={chapter}
                total={chapters.length}
              />
            ),
        )}
      </AnimatePresence>
    </VideoCanvas>
  );
}