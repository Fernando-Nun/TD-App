import * as THREE from 'three';

const BODY_WIDTH = 2.3;
const BODY_HEIGHT = 4.55;
const BODY_DEPTH = 0.32;
const BODY_RADIUS = 0.34;
const BODY_BEVEL = 0.02;
// ExtrudeGeometry's bevel pushes the flat front/back caps beyond +/-BODY_DEPTH/2
// by BODY_BEVEL once the geometry is centered — anything meant to sit flush on
// those faces (screen, notch, camera bump, lenses) must use these, not BODY_DEPTH/2.
const BODY_FRONT_Z = BODY_DEPTH / 2 + BODY_BEVEL;
const BODY_BACK_Z = -BODY_FRONT_Z;

const SCREEN_INSET_X = 0.09;
const SCREEN_INSET_TOP = 0.11;
const SCREEN_INSET_BOTTOM = 0.13;
const SCREEN_RADIUS = 0.22;

const CANVAS_W = 512;
const CANVAS_H = 1040;

const BRAND_PURPLE = '#7c3aed';
const BRAND_AMBER = '#a64f0b';

export interface StoryStageContent {
  kicker: string;
  title: string;
  accent: 'target' | 'clock' | 'check' | 'coins' | 'sparkles';
  pill: string;
  pillColor: string;
  pillBg: string;
}

function roundedRectShape(width: number, height: number, radius: number): THREE.Shape {
  const shape = new THREE.Shape();
  const x = -width / 2;
  const y = -height / 2;
  const r = Math.min(radius, width / 2, height / 2);
  shape.moveTo(x + r, y);
  shape.lineTo(x + width - r, y);
  shape.quadraticCurveTo(x + width, y, x + width, y + r);
  shape.lineTo(x + width, y + height - r);
  shape.quadraticCurveTo(x + width, y + height, x + width - r, y + height);
  shape.lineTo(x + r, y + height);
  shape.quadraticCurveTo(x, y + height, x, y + height - r);
  shape.lineTo(x, y + r);
  shape.quadraticCurveTo(x, y, x + r, y);
  return shape;
}

function roundedRectPath(
  ctx: CanvasRenderingContext2D,
  x: number,
  y: number,
  width: number,
  height: number,
  radius: number,
) {
  const r = Math.min(radius, width / 2, height / 2);
  ctx.beginPath();
  ctx.moveTo(x + r, y);
  ctx.lineTo(x + width - r, y);
  ctx.arcTo(x + width, y, x + width, y + r, r);
  ctx.lineTo(x + width, y + height - r);
  ctx.arcTo(x + width, y + height, x + width - r, y + height, r);
  ctx.lineTo(x + r, y + height);
  ctx.arcTo(x, y + height, x, y + height - r, r);
  ctx.lineTo(x, y + r);
  ctx.arcTo(x, y, x + r, y, r);
  ctx.closePath();
}

function drawAccentIcon(
  ctx: CanvasRenderingContext2D,
  accent: StoryStageContent['accent'],
  cx: number,
  cy: number,
  size: number,
  color: string,
) {
  ctx.save();
  ctx.strokeStyle = color;
  ctx.fillStyle = color;
  ctx.lineWidth = size * 0.11;
  ctx.lineCap = 'round';
  ctx.lineJoin = 'round';

  if (accent === 'target') {
    for (const factor of [1, 0.62, 0.24]) {
      ctx.beginPath();
      ctx.arc(cx, cy, (size / 2) * factor, 0, Math.PI * 2);
      ctx.stroke();
    }
  } else if (accent === 'clock') {
    ctx.beginPath();
    ctx.arc(cx, cy, size / 2, 0, Math.PI * 2);
    ctx.stroke();
    ctx.beginPath();
    ctx.moveTo(cx, cy);
    ctx.lineTo(cx, cy - size * 0.32);
    ctx.moveTo(cx, cy);
    ctx.lineTo(cx + size * 0.22, cy + size * 0.1);
    ctx.stroke();
  } else if (accent === 'check') {
    ctx.beginPath();
    ctx.arc(cx, cy, size / 2, 0, Math.PI * 2);
    ctx.stroke();
    ctx.beginPath();
    ctx.moveTo(cx - size * 0.22, cy);
    ctx.lineTo(cx - size * 0.05, cy + size * 0.2);
    ctx.lineTo(cx + size * 0.26, cy - size * 0.22);
    ctx.stroke();
  } else if (accent === 'coins') {
    ctx.beginPath();
    ctx.arc(cx - size * 0.1, cy, size * 0.34, 0, Math.PI * 2);
    ctx.stroke();
    ctx.beginPath();
    ctx.arc(cx + size * 0.15, cy - size * 0.12, size * 0.34, 0, Math.PI * 2);
    ctx.stroke();
  } else {
    ctx.beginPath();
    ctx.moveTo(cx, cy - size / 2);
    ctx.lineTo(cx + size * 0.12, cy - size * 0.12);
    ctx.lineTo(cx + size / 2, cy);
    ctx.lineTo(cx + size * 0.12, cy + size * 0.12);
    ctx.lineTo(cx, cy + size / 2);
    ctx.lineTo(cx - size * 0.12, cy + size * 0.12);
    ctx.lineTo(cx - size / 2, cy);
    ctx.lineTo(cx - size * 0.12, cy - size * 0.12);
    ctx.closePath();
    ctx.fill();
  }
  ctx.restore();
}

function drawScreen(ctx: CanvasRenderingContext2D, content: StoryStageContent) {
  ctx.clearRect(0, 0, CANVAS_W, CANVAS_H);

  const bg = ctx.createRadialGradient(
    CANVAS_W, 0, 0,
    CANVAS_W * 0.5, CANVAS_H * 0.5, CANVAS_H * 0.75,
  );
  bg.addColorStop(0, 'rgba(124,58,237,0.16)');
  bg.addColorStop(1, '#f7f3ff');
  ctx.fillStyle = bg;
  roundedRectPath(ctx, 0, 0, CANVAS_W, CANVAS_H, 64);
  ctx.fill();

  ctx.fillStyle = '#8e7ba8';
  ctx.font = '700 20px "DM Sans", sans-serif';
  ctx.textBaseline = 'middle';
  ctx.textAlign = 'left';
  ctx.letterSpacing = '2px';
  ctx.fillText('TD-APP', 42, 64);
  ctx.textAlign = 'right';
  ctx.fillText('HOY', CANVAS_W - 42, 64);
  ctx.letterSpacing = '0px';

  ctx.textAlign = 'left';
  ctx.fillStyle = '#8e7ba8';
  ctx.font = '700 19px "DM Sans", sans-serif';
  ctx.fillText('TU SIGUIENTE PASO', 42, 132);
  ctx.fillStyle = '#1a0a2e';
  ctx.font = '700 44px "Space Grotesk", sans-serif';
  ctx.fillText('Hazlo posible.', 40, 178);

  const cardX = 40;
  const cardY = 236;
  const cardW = CANVAS_W - 80;
  const cardH = 560;
  ctx.fillStyle = 'rgba(255,255,255,0.86)';
  roundedRectPath(ctx, cardX, cardY, cardW, cardH, 40);
  ctx.fill();
  ctx.strokeStyle = '#dfd5f2';
  ctx.lineWidth = 2;
  ctx.stroke();

  const iconCx = cardX + 90;
  const iconCy = cardY + 110;
  ctx.beginPath();
  ctx.fillStyle = 'rgba(124,58,237,0.12)';
  ctx.arc(iconCx, iconCy, 56, 0, Math.PI * 2);
  ctx.fill();
  drawAccentIcon(ctx, content.accent, iconCx, iconCy, 56, BRAND_PURPLE);

  ctx.fillStyle = BRAND_PURPLE;
  ctx.font = '800 18px "DM Sans", sans-serif';
  ctx.textAlign = 'left';
  ctx.fillText(content.kicker, cardX + 40, cardY + 210);

  ctx.fillStyle = '#1a0a2e';
  ctx.font = '700 38px "Space Grotesk", sans-serif';
  wrapText(ctx, content.title, cardX + 40, cardY + 264, cardW - 80, 44);

  const pillY = cardY + cardH - 96;
  ctx.font = '800 20px "DM Sans", sans-serif';
  const pillTextWidth = ctx.measureText(content.pill).width;
  const pillW = pillTextWidth + 88;
  ctx.fillStyle = content.pillBg;
  roundedRectPath(ctx, cardX + 40, pillY, pillW, 64, 32);
  ctx.fill();
  drawAccentIcon(ctx, content.accent, cardX + 76, pillY + 32, 26, content.pillColor);
  ctx.fillStyle = content.pillColor;
  ctx.textAlign = 'left';
  ctx.fillText(content.pill, cardX + 100, pillY + 33);

  ctx.fillStyle = '#d7caee';
  const dotY = CANVAS_H - 56;
  const dotSpacing = 22;
  const dotStartX = CANVAS_W / 2 - dotSpacing;
  for (let i = 0; i < 3; i += 1) {
    ctx.beginPath();
    ctx.arc(dotStartX + i * dotSpacing, dotY, i === 0 ? 7 : 5, 0, Math.PI * 2);
    ctx.fillStyle = i === 0 ? BRAND_PURPLE : '#d7caee';
    ctx.fill();
  }
}

function wrapText(
  ctx: CanvasRenderingContext2D,
  text: string,
  x: number,
  y: number,
  maxWidth: number,
  lineHeight: number,
) {
  const words = text.split(' ');
  let line = '';
  let lineY = y;
  for (const word of words) {
    const testLine = line ? `${line} ${word}` : word;
    if (ctx.measureText(testLine).width > maxWidth && line) {
      ctx.fillText(line, x, lineY);
      line = word;
      lineY += lineHeight;
    } else {
      line = testLine;
    }
  }
  if (line) ctx.fillText(line, x, lineY);
}

export const STAGE_CONTENT: StoryStageContent[] = [
  {
    kicker: 'MISIÓN DE HOY',
    title: 'Preparar mi presentación',
    accent: 'target',
    pill: 'Empezar pequeño',
    pillColor: BRAND_PURPLE,
    pillBg: '#eee7ff',
  },
  {
    kicker: 'BLOQUE DE ENFOQUE',
    title: '25:00 — un momento a la vez',
    accent: 'clock',
    pill: 'En marcha',
    pillColor: '#14a994',
    pillBg: '#dbf6f1',
  },
  {
    kicker: 'BLOQUE COMPLETADO',
    title: 'Bien hecho, lo lograste',
    accent: 'check',
    pill: '+12 TD-Coins',
    pillColor: BRAND_AMBER,
    pillBg: '#fff0d9',
  },
  {
    kicker: 'TU RECOMPENSA',
    title: 'También cuenta volver a ti',
    accent: 'sparkles',
    pill: 'Pelota antiestrés',
    pillColor: BRAND_PURPLE,
    pillBg: '#eee7ff',
  },
];

export interface PhoneSceneHandle {
  setStage: (stage: number) => void;
  resize: (width: number, height: number) => void;
  setPaused: (paused: boolean) => void;
  dispose: () => void;
}

export interface CreatePhoneSceneOptions {
  canvas: HTMLCanvasElement;
  getProgress: () => number;
  stageCount: number;
  disableSpin: boolean;
}

export function createPhoneScene(options: CreatePhoneSceneOptions): PhoneSceneHandle {
  const { canvas, getProgress, stageCount, disableSpin } = options;

  const scene = new THREE.Scene();
  const camera = new THREE.PerspectiveCamera(30, 1, 0.1, 100);
  camera.position.set(0, 0, 10.5);

  const renderer = new THREE.WebGLRenderer({ canvas, alpha: true, antialias: true });
  renderer.setPixelRatio(Math.min(window.devicePixelRatio || 1, 2));
  renderer.outputColorSpace = THREE.SRGBColorSpace;

  scene.add(new THREE.AmbientLight(0xffffff, 0.65));

  const keyLight = new THREE.DirectionalLight(0xffffff, 1.1);
  keyLight.position.set(3, 4, 6);
  scene.add(keyLight);

  const rimLight = new THREE.DirectionalLight(0x9d7bff, 0.55);
  rimLight.position.set(-4, -2, -3);
  scene.add(rimLight);

  const fillLight = new THREE.DirectionalLight(0x62ddca, 0.25);
  fillLight.position.set(-3, 3, 4);
  scene.add(fillLight);

  const phoneGroup = new THREE.Group();
  scene.add(phoneGroup);

  const bodyGeo = new THREE.ExtrudeGeometry(
    roundedRectShape(BODY_WIDTH, BODY_HEIGHT, BODY_RADIUS),
    { depth: BODY_DEPTH, bevelEnabled: true, bevelThickness: BODY_BEVEL, bevelSize: BODY_BEVEL, bevelSegments: 4, curveSegments: 16 },
  );
  bodyGeo.center();
  const bodyMat = new THREE.MeshStandardMaterial({ color: 0x1c1b22, metalness: 0.55, roughness: 0.32 });
  const bodyMesh = new THREE.Mesh(bodyGeo, bodyMat);
  phoneGroup.add(bodyMesh);

  const canvasEl = document.createElement('canvas');
  canvasEl.width = CANVAS_W;
  canvasEl.height = CANVAS_H;
  const screenCtx = canvasEl.getContext('2d');
  const texture = new THREE.CanvasTexture(canvasEl);
  texture.colorSpace = THREE.SRGBColorSpace;
  texture.generateMipmaps = false;
  texture.minFilter = THREE.LinearFilter;
  texture.magFilter = THREE.LinearFilter;
  texture.wrapS = THREE.ClampToEdgeWrapping;
  texture.wrapT = THREE.ClampToEdgeWrapping;

  const screenWidth = BODY_WIDTH - SCREEN_INSET_X * 2;
  const screenHeight = BODY_HEIGHT - SCREEN_INSET_TOP - SCREEN_INSET_BOTTOM;
  const screenGeo = new THREE.PlaneGeometry(screenWidth, screenHeight);
  const screenMat = new THREE.MeshBasicMaterial({ map: texture, toneMapped: false });
  const screenMesh = new THREE.Mesh(screenGeo, screenMat);
  screenMesh.position.set(0, (SCREEN_INSET_BOTTOM - SCREEN_INSET_TOP) / 2, BODY_FRONT_Z + 0.01);
  phoneGroup.add(screenMesh);

  const notchGeo = new THREE.ShapeGeometry(roundedRectShape(0.42, 0.1, 0.05));
  const notchMat = new THREE.MeshBasicMaterial({ color: 0x0a0710 });
  const notchMesh = new THREE.Mesh(notchGeo, notchMat);
  notchMesh.position.set(0, BODY_HEIGHT / 2 - SCREEN_INSET_TOP + 0.02, BODY_FRONT_Z + 0.016);
  phoneGroup.add(notchMesh);

  const bumpGeo = new THREE.ExtrudeGeometry(
    roundedRectShape(0.92, 0.92, 0.24),
    { depth: 0.045, bevelEnabled: true, bevelThickness: 0.012, bevelSize: 0.012, bevelSegments: 3, curveSegments: 10 },
  );
  bumpGeo.center();
  const bumpMat = new THREE.MeshStandardMaterial({ color: 0x2a2a33, metalness: 0.5, roughness: 0.4 });
  const bumpMesh = new THREE.Mesh(bumpGeo, bumpMat);
  bumpMesh.position.set(-BODY_WIDTH / 2 + 0.62, BODY_HEIGHT / 2 - 0.72, BODY_BACK_Z - 0.02);
  bumpMesh.rotation.y = Math.PI;
  phoneGroup.add(bumpMesh);

  const lensMat = new THREE.MeshStandardMaterial({ color: 0x05050a, metalness: 0.85, roughness: 0.18 });
  const lensGeo = new THREE.CylinderGeometry(0.16, 0.16, 0.05, 24);
  const lensOffsets: [number, number][] = [[-0.22, 0.22], [0.24, 0.22], [0.01, -0.24]];
  for (const [ox, oy] of lensOffsets) {
    const lens = new THREE.Mesh(lensGeo, lensMat);
    lens.rotation.x = Math.PI / 2;
    lens.position.set(bumpMesh.position.x + ox, bumpMesh.position.y + oy, BODY_BACK_Z - 0.05);
    phoneGroup.add(lens);
  }

  let currentStage = 0;
  let currentRotationDeg = 0;
  let paused = false;
  let disposed = false;

  const applyStageContent = (stage: number) => {
    if (!screenCtx) return;
    const content = STAGE_CONTENT[stage] ?? STAGE_CONTENT[0];
    drawScreen(screenCtx, content);
    texture.needsUpdate = true;
  };

  applyStageContent(0);
  if (document.fonts?.ready) {
    document.fonts.ready.then(() => applyStageContent(currentStage)).catch(() => {});
  }

  const renderOnce = () => renderer.render(scene, camera);

  const resize = (width: number, height: number) => {
    if (width <= 0 || height <= 0) return;
    camera.aspect = width / height;
    camera.updateProjectionMatrix();
    renderer.setSize(width, height, false);
    if (disableSpin) renderOnce();
  };

  let lastTime = performance.now();

  const animateFrame = () => {
    if (disposed) return;
    const now = performance.now();
    const dt = Math.min((now - lastTime) / 1000, 0.1);
    lastTime = now;
    if (!paused) {
      const progress = Math.min(1, Math.max(0, getProgress()));
      const stageFloat = progress * (stageCount - 1);
      const damping = 1 - Math.exp(-dt * 6);
      currentRotationDeg += (stageFloat * 360 - currentRotationDeg) * damping;
      phoneGroup.rotation.y = THREE.MathUtils.degToRad(currentRotationDeg);
      phoneGroup.rotation.x = THREE.MathUtils.degToRad(Math.sin(stageFloat * Math.PI) * 2.5);
      renderOnce();
    }
    requestAnimationFrame(animateFrame);
  };

  // With spin disabled (reduced motion, or a small screen where a rotating,
  // drifting phone reads as distracting rather than showcase-y) the phone
  // never moves, so there's no need for a continuous render loop — render
  // once up front and again only when the stage or size actually changes.
  if (disableSpin) {
    renderOnce();
  } else {
    requestAnimationFrame(animateFrame);
  }

  return {
    setStage: (stage: number) => {
      if (stage === currentStage) return;
      currentStage = stage;
      applyStageContent(stage);
      if (disableSpin) renderOnce();
    },
    resize,
    setPaused: (value: boolean) => {
      paused = value;
      if (disableSpin && !value) renderOnce();
    },
    dispose: () => {
      disposed = true;
      bodyGeo.dispose();
      screenGeo.dispose();
      notchGeo.dispose();
      bumpGeo.dispose();
      lensGeo.dispose();
      bodyMat.dispose();
      screenMat.dispose();
      notchMat.dispose();
      bumpMat.dispose();
      lensMat.dispose();
      texture.dispose();
      renderer.dispose();
    },
  };
}
