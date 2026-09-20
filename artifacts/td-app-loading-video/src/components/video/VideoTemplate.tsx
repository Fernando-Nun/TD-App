import {
  SafeFrame,
  VideoCanvas,
  type VideoAspectRatio,
  useVideoPlayer,
} from '@/lib/video';
import { TDAppLoadingScene } from './TDAppLoadingScene';

const SCENE_DURATIONS = { loading: 5000 };

const VIDEO_ASPECT_RATIO: VideoAspectRatio = '9:16';

export default function VideoTemplate() {
  const { currentScene } = useVideoPlayer({
    durations: SCENE_DURATIONS,
  });

  return (
    <VideoCanvas
      aspectRatio={VIDEO_ASPECT_RATIO}
      style={{ backgroundColor: 'var(--color-bg-light)' }}
    >
      <SafeFrame className="absolute inset-0">
        <TDAppLoadingScene currentScene={currentScene} />
      </SafeFrame>
    </VideoCanvas>
  );
}
