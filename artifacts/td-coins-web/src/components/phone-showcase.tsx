import { useEffect, useRef, type MutableRefObject } from 'react';
import { createPhoneScene, type PhoneSceneHandle } from '@/lib/phone-scene';

export function PhoneShowcase({
  progressRef,
  stage,
  stageCount,
  reducedMotion,
}: {
  progressRef: MutableRefObject<number>;
  stage: number;
  stageCount: number;
  reducedMotion: boolean;
}) {
  const canvasRef = useRef<HTMLCanvasElement | null>(null);
  const sceneRef = useRef<PhoneSceneHandle | null>(null);

  useEffect(() => {
    const canvas = canvasRef.current;
    const wrap = canvas?.parentElement;
    if (!canvas || !wrap) return;

    let handle: PhoneSceneHandle | null = null;
    try {
      handle = createPhoneScene({
        canvas,
        getProgress: () => progressRef.current,
        stageCount,
        reducedMotion,
      });
      sceneRef.current = handle;
    } catch {
      return;
    }

    const resizeObserver = new ResizeObserver((entries) => {
      const entry = entries[0];
      if (!entry || !handle) return;
      const { width, height } = entry.contentRect;
      handle.resize(width, height);
    });
    resizeObserver.observe(wrap);

    const intersectionObserver = new IntersectionObserver(
      (entries) => {
        const entry = entries[0];
        handle?.setPaused(!entry?.isIntersecting);
      },
      { threshold: 0.05 },
    );
    intersectionObserver.observe(wrap);

    return () => {
      resizeObserver.disconnect();
      intersectionObserver.disconnect();
      handle?.dispose();
      sceneRef.current = null;
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [stageCount, reducedMotion]);

  useEffect(() => {
    sceneRef.current?.setStage(stage);
  }, [stage]);

  return <canvas ref={canvasRef} className="td-phone-3d-canvas" aria-hidden="true" />;
}
