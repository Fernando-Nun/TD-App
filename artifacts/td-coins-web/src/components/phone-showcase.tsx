import { useEffect, useRef, useState, type MutableRefObject } from 'react';
import {
  createPhoneScene,
  STAGE_CONTENT,
  type PhoneSceneHandle,
} from '@/lib/phone-scene';

export function PhoneShowcase({
  progressRef,
  stage,
  stageCount,
  disableSpin,
}: {
  progressRef: MutableRefObject<number>;
  stage: number;
  stageCount: number;
  disableSpin: boolean;
}) {
  const canvasRef = useRef<HTMLCanvasElement | null>(null);
  const sceneRef = useRef<PhoneSceneHandle | null>(null);
  const [webglUnavailable, setWebglUnavailable] = useState(false);

  useEffect(() => {
    const canvas = canvasRef.current;
    const wrap = canvas?.parentElement;
    if (!canvas || !wrap) return;

    const probe = document.createElement('canvas');
    let context: WebGLRenderingContext | WebGL2RenderingContext | null = null;
    try {
      context = probe.getContext('webgl2') ?? probe.getContext('webgl');
    } catch {
      context = null;
    }
    if (!context) {
      setWebglUnavailable(true);
      return;
    }

    let handle: PhoneSceneHandle | null = null;
    try {
      handle = createPhoneScene({
        canvas,
        getProgress: () => progressRef.current,
        stageCount,
        disableSpin,
      });
      sceneRef.current = handle;
    } catch {
      setWebglUnavailable(true);
      return;
    }
    setWebglUnavailable(false);

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
  }, [stageCount, disableSpin]);

  useEffect(() => {
    sceneRef.current?.setStage(stage);
  }, [stage]);

  if (webglUnavailable) {
    const content = STAGE_CONTENT[stage] ?? STAGE_CONTENT[0];
    return (
      <div
        className="td-phone-fallback"
        role="img"
        aria-label={`Vista de TD-App: ${content.title}`}
      >
        <div className="td-phone-fallback-notch" />
        <div className="td-phone-fallback-screen">
          <div className="td-phone-fallback-status">
            <span>TD-App</span>
            <span>Hoy</span>
          </div>
          <div className="td-phone-fallback-heading">
            <span>{content.kicker}</span>
            <strong>{content.title}</strong>
          </div>
          <div
            className="td-phone-fallback-pill"
            style={{ color: content.pillColor, backgroundColor: content.pillBg }}
          >
            {content.pill}
          </div>
          <div className="td-phone-fallback-dots">
            {STAGE_CONTENT.map((_, index) => (
              <span key={index} className={index === stage ? 'is-active' : ''} />
            ))}
          </div>
        </div>
      </div>
    );
  }

  return <canvas ref={canvasRef} className="td-phone-3d-canvas" aria-hidden="true" />;
}
