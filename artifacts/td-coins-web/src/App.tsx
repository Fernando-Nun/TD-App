import {
  useEffect,
  useRef,
  useState,
  type ElementType,
  type ReactNode,
} from 'react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import {
  Accessibility,
  ArrowDown,
  ArrowRight,
  Check,
  Clock3,
  Coins,
  Download,
  Info,
  Menu,
  Mic,
  ShieldCheck,
  Sparkles,
  Target,
  X,
} from 'lucide-react';
import { ErrorBoundary } from '@/components/error-boundary';
import { Toaster } from '@/components/ui/toaster';
import { TooltipProvider } from '@/components/ui/tooltip';
import NotFound from '@/pages/not-found';
import {
  Route,
  Switch,
  useLocation,
  Router as WouterRouter,
} from 'wouter';

const queryClient = new QueryClient();
const downloadPath = '/downloads/td-app.apk';

const storyScenes = [
  {
    number: '01',
    label: 'ELIGE',
    title: 'Empieza por algo posible.',
    copy: 'Convierte una intención grande en una misión concreta que puedas comenzar ahora.',
  },
  {
    number: '02',
    label: 'AVANZA',
    title: 'Quédate con el siguiente bloque.',
    copy: 'Un temporizador sencillo te ayuda a entrar, estar y cerrar sin perseguir la perfección.',
  },
  {
    number: '03',
    label: 'CELEBRA',
    title: 'Haz visible que sí avanzaste.',
    copy: 'Cada bloque terminado se convierte en una señal clara: lo hiciste, y cuenta.',
  },
  {
    number: '04',
    label: 'REPITE',
    title: 'Construye algo que te acompañe.',
    copy: 'Tus TD-Coins hacen tangible la constancia y te dan un motivo amable para volver.',
  },
];

const featureItems = [
  {
    icon: Clock3,
    title: 'Bloques de enfoque',
    copy: 'Temporizadores sencillos para entrar, estar y cerrar.',
    testId: 'card-feature-missions',
  },
  {
    icon: Check,
    title: 'Misiones y hábitos',
    copy: 'Haz visible la constancia sin pedirte hacerlo perfecto.',
    testId: 'card-feature-habits',
  },
  {
    icon: Mic,
    title: 'Reflexión por voz',
    copy: 'Guarda cómo te fue sin tener que sentarte a escribir.',
    testId: 'card-feature-voice',
  },
  {
    icon: Accessibility,
    title: 'Accesible de verdad',
    copy: 'Controles comprensibles, apoyo visual y menos ruido.',
    testId: 'card-feature-accessibility',
  },
  {
    icon: Sparkles,
    title: 'Planes personalizados',
    copy: 'Convierte cada reto en pasos concretos que puedas comenzar.',
    testId: 'card-feature-plans',
  },
];

const rewardItems = [
  {
    label: 'Pelota antiestrés',
    caption: 'Para volver al presente',
    image: '/assets/pelota.png',
    alt: 'Pelota antiestrés morada de TD-App',
    testId: 'card-reward-ball',
  },
  {
    label: 'Llavero',
    caption: 'Un paso a la vez',
    image: '/assets/llavero.png',
    alt: 'Llavero morado de TD-App',
    testId: 'card-reward-keychain',
  },
  {
    label: 'Taza',
    caption: 'Tu pausa también cuenta',
    image: '/assets/taza.png',
    alt: 'Taza de TD-App',
    testId: 'card-reward-mug',
  },
  {
    label: 'Mochila',
    caption: 'Para lo que viene',
    image: '/assets/mochila.png',
    alt: 'Mochila lila de TD-App',
    testId: 'card-reward-backpack',
  },
];

const faqItems = [
  {
    question: '¿En qué dispositivos funciona?',
    answer:
      'TD-App está preparada para teléfonos Android compatibles con la versión mínima indicada por la aplicación.',
    defaultOpen: true,
  },
  {
    question: '¿Cómo instalo el APK?',
    answer:
      'Descarga el archivo desde esta página. Si Android lo solicita, permite temporalmente la instalación desde esta fuente en Ajustes.',
  },
  {
    question: '¿Necesito hacerlo todo perfecto?',
    answer:
      'No. La app está pensada para reconocer avances pequeños: una misión, un bloque y un paso posible ya cuentan.',
  },
  {
    question: '¿Qué puedo hacer dentro de la app?',
    answer:
      'Puedes crear misiones, usar bloques de enfoque, registrar reflexiones por voz, revisar tus avances y convertirlos en TD-Coins.',
  },
];

function usePrefersReducedMotion() {
  const [reduced, setReduced] = useState(false);

  useEffect(() => {
    const query = window.matchMedia('(prefers-reduced-motion: reduce)');
    setReduced(query.matches);
    const listener = (event: MediaQueryListEvent) => setReduced(event.matches);
    query.addEventListener('change', listener);
    return () => query.removeEventListener('change', listener);
  }, []);

  return reduced;
}

function Reveal({
  as = 'div',
  children,
  className = '',
  delay = 0,
  ...rest
}: {
  as?: ElementType;
  children: ReactNode;
  className?: string;
  delay?: number;
} & Record<string, unknown>) {
  const ref = useRef<HTMLElement | null>(null);
  const [visible, setVisible] = useState(false);

  useEffect(() => {
    const node = ref.current;
    if (!node) return;

    if (window.matchMedia('(prefers-reduced-motion: reduce)').matches) {
      setVisible(true);
      return;
    }

    const observer = new IntersectionObserver(
      (entries) => {
        const entry = entries[0];
        if (entry?.isIntersecting) {
          setVisible(true);
          observer.disconnect();
        }
      },
      { threshold: 0.18, rootMargin: '0px 0px -8% 0px' },
    );

    observer.observe(node);
    return () => observer.disconnect();
  }, []);

  const Component = as as unknown as 'div';

  return (
    <Component
      ref={ref as never}
      className={`td-reveal-io${visible ? ' is-visible' : ''}${className ? ` ${className}` : ''}`}
      style={{ transitionDelay: `${delay}ms` }}
      {...rest}
    >
      {children}
    </Component>
  );
}

function DownloadLink({
  children,
  className = 'td-primary-button',
  testId = 'link-download-apk',
}: {
  children: ReactNode;
  className?: string;
  testId?: string;
}) {
  return (
    <a
      className={className}
      data-testid={testId}
      href={downloadPath}
      download="td-app.apk"
      aria-label="Descargar TD-App para Android"
    >
      {children}
    </a>
  );
}

function Home() {
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const [storyStage, setStoryStage] = useState(0);
  const [scrolled, setScrolled] = useState(false);
  const [scrollProgress, setScrollProgress] = useState(0);
  const storyRefs = useRef<(HTMLElement | null)[]>([]);
  const heroArtRef = useRef<HTMLDivElement | null>(null);
  const reducedMotion = usePrefersReducedMotion();

  useEffect(() => {
    document.title = 'TD-App | Un paso a la vez';
    const description =
      'TD-App convierte tus pequeños avances en monedas para ayudarte a empezar, enfocarte y celebrar.';
    let meta = document.querySelector('meta[name="description"]');
    if (!meta) {
      meta = document.createElement('meta');
      meta.setAttribute('name', 'description');
      document.head.appendChild(meta);
    }
    meta.setAttribute('content', description);
    document.documentElement.lang = 'es';
  }, []);

  useEffect(() => {
    const observer = new IntersectionObserver(
      (entries) => {
        const visible = entries
          .filter((entry) => entry.isIntersecting)
          .sort((a, b) => b.intersectionRatio - a.intersectionRatio)[0];
        if (!visible) return;
        const nextStage = Number((visible.target as HTMLElement).dataset.storyStage);
        if (!Number.isNaN(nextStage)) setStoryStage(nextStage);
      },
      { rootMargin: '-30% 0px -45% 0px', threshold: [0.15, 0.45, 0.75] },
    );

    storyRefs.current.forEach((scene) => {
      if (scene) observer.observe(scene);
    });

    return () => observer.disconnect();
  }, []);

  useEffect(() => {
    let ticking = false;

    const updateScrollState = () => {
      const scrollTop = window.scrollY;
      const docHeight = document.documentElement.scrollHeight - window.innerHeight;
      setScrolled(scrollTop > 24);
      setScrollProgress(docHeight > 0 ? Math.min(1, Math.max(0, scrollTop / docHeight)) : 0);
      ticking = false;
    };

    const onScroll = () => {
      if (!ticking) {
        window.requestAnimationFrame(updateScrollState);
        ticking = true;
      }
    };

    updateScrollState();
    window.addEventListener('scroll', onScroll, { passive: true });
    return () => window.removeEventListener('scroll', onScroll);
  }, []);

  useEffect(() => {
    const node = heroArtRef.current;
    if (!node) return;
    if (reducedMotion) return;
    if (window.matchMedia('(pointer: coarse)').matches) return;

    const handleMove = (event: PointerEvent) => {
      const rect = node.getBoundingClientRect();
      const relX = (event.clientX - rect.left) / rect.width - 0.5;
      const relY = (event.clientY - rect.top) / rect.height - 0.5;
      node.style.setProperty('--parallax-x', relX.toFixed(3));
      node.style.setProperty('--parallax-y', relY.toFixed(3));
    };

    const handleLeave = () => {
      node.style.setProperty('--parallax-x', '0');
      node.style.setProperty('--parallax-y', '0');
    };

    node.addEventListener('pointermove', handleMove);
    node.addEventListener('pointerleave', handleLeave);
    return () => {
      node.removeEventListener('pointermove', handleMove);
      node.removeEventListener('pointerleave', handleLeave);
    };
  }, [reducedMotion]);

  const closeMenu = () => setMobileMenuOpen(false);

  return (
    <main className="td-page">
      <div
        className="td-progress"
        style={{ transform: `scaleX(${scrollProgress})` }}
        aria-hidden="true"
      />

      <header
        className={`td-container td-nav${scrolled ? ' is-scrolled' : ''}`}
        data-testid="header-navigation"
      >
        <a className="td-brand" href="#inicio" data-testid="link-brand">
          <img
            src="/assets/td-coins-logo.png"
            alt="Logo de TD-App"
            data-testid="img-brand-logo"
          />
          <span>TD-App</span>
        </a>

        <nav className="td-nav-links" aria-label="Navegación principal">
          <a href="#como-funciona" data-testid="link-how-it-works">Cómo funciona</a>
          <a href="#funciones" data-testid="link-features">Funciones</a>
          <a href="#recompensas" data-testid="link-rewards">Recompensas</a>
            <a href="#preguntas" data-testid="link-faq">Preguntas</a>
        </nav>

        <DownloadLink
          className="td-nav-download"
          testId="link-header-download"
        >
          <Download size={15} strokeWidth={2.5} />
          Descargar APK
        </DownloadLink>

        <button
          className="td-mobile-toggle"
          type="button"
          aria-expanded={mobileMenuOpen}
          aria-label={mobileMenuOpen ? 'Cerrar menú' : 'Abrir menú'}
          data-testid="button-mobile-menu"
          onClick={() => setMobileMenuOpen((open) => !open)}
        >
          {mobileMenuOpen ? <X size={20} /> : <Menu size={20} />}
        </button>

        {mobileMenuOpen && (
          <nav className="td-mobile-menu" aria-label="Navegación móvil">
            <a href="#como-funciona" onClick={closeMenu} data-testid="link-mobile-how-it-works">
              Cómo funciona
            </a>
            <a href="#funciones" onClick={closeMenu} data-testid="link-mobile-features">
              Funciones
            </a>
            <a href="#recompensas" onClick={closeMenu} data-testid="link-mobile-rewards">
              Recompensas
            </a>
            <a href="#preguntas" onClick={closeMenu} data-testid="link-mobile-faq">
              Preguntas
            </a>
            <DownloadLink
              className="td-primary-button"
              testId="link-mobile-download"
            >
              <Download size={17} />
              Descargar para Android
            </DownloadLink>
          </nav>
        )}
      </header>

      <section className="td-container td-hero" id="inicio">
        <div className="td-hero-content td-reveal">
          <div className="td-kicker">Tu siguiente pequeño paso</div>
          <h1 className="td-display">
            Hazlo pequeño.
            <br />
            Hazlo <em>posible.</em>
          </h1>
          <p className="td-hero-copy">
            TD-App es el compañero Android que convierte tus momentos de enfoque,
            misiones y hábitos en avances que puedes ver y celebrar.
          </p>
          <div className="td-hero-actions">
            <DownloadLink testId="link-hero-download">
              <Download size={19} />
              Descargar APK
              <ArrowRight size={17} />
            </DownloadLink>
            <a
              className="td-quiet-button"
              href="#como-funciona"
              data-testid="link-hero-learn-more"
            >
              Conoce la idea
              <ArrowDown size={16} />
            </a>
          </div>
          <div className="td-hero-note" data-testid="text-installation-note">
            <ShieldCheck size={15} />
            <span>
              APK directo y gratuito. Android puede pedirte permiso para instalar
              aplicaciones de esta fuente.
            </span>
          </div>
        </div>

        <div
          className="td-hero-art td-reveal td-delay-2"
          aria-label="Identidad visual de TD-App"
        >
          <div ref={heroArtRef} className="td-hero-art-tilt">
            <div className="td-coin-orbit">
              <img
                className="td-hero-logo"
                src="/assets/td-coins-logo.png"
                alt="Identidad de TD-App"
                data-testid="img-hero-logo"
              />
            </div>
            <div className="td-floating-card td-float-one" data-testid="card-focus-win">
              <strong>+12 TD-Coins</strong>
              <span>Bloque completado</span>
            </div>
            <div className="td-floating-card td-float-two" data-testid="card-next-step">
              <strong>Un paso</strong>
              <span>También cuenta</span>
            </div>
          </div>
        </div>
      </section>

      <section className="td-story-section" id="como-funciona">
        <Reveal as="div" className="td-container td-story-intro">
          <div>
            <div className="td-section-label">Una pequeña victoria, paso a paso</div>
            <h2 className="td-display">
              La app no te pide más.
              <br />
              Te ayuda a <em>empezar.</em>
            </h2>
          </div>
          <p>
            Desliza para ver cómo un momento de enfoque se convierte en algo que
            puedes reconocer, guardar y repetir.
          </p>
        </Reveal>

        <div className="td-container td-story-layout">
          <div className="td-story-visual" data-stage={storyStage} aria-hidden="true">
            <div className="td-story-orbit" />
            <div className="td-phone">
              <div className="td-phone-speaker" />
              <div className="td-phone-screen">
                <div className="td-phone-status">
                  <span>TD-App</span>
                  <span>Hoy</span>
                </div>
                <div className="td-phone-heading">
                  <span>Tu siguiente paso</span>
                  <strong>Hazlo posible.</strong>
                </div>
                <div className="td-story-panels">
                  <div className="td-story-panel" data-panel="0">
                    <span className="td-panel-kicker">MISIÓN DE HOY</span>
                    <strong>Preparar mi presentación</strong>
                    <div className="td-panel-action">
                      <Target size={16} />
                      Empezar pequeño
                    </div>
                  </div>
                  <div className="td-story-panel" data-panel="1">
                    <span className="td-panel-kicker">BLOQUE DE ENFOQUE</span>
                    <strong className="td-timer">25:00</strong>
                    <div className="td-panel-progress"><span /></div>
                    <span className="td-panel-caption">Un momento a la vez.</span>
                  </div>
                  <div className="td-story-panel" data-panel="2">
                    <span className="td-panel-kicker">BLOQUE COMPLETADO</span>
                    <strong className="td-check-mark"><Check size={22} /> Bien hecho</strong>
                    <div className="td-panel-coins"><Coins size={17} /> +12 TD-Coins</div>
                  </div>
                  <div className="td-story-panel" data-panel="3">
                    <span className="td-panel-kicker">TU RECOMPENSA</span>
                    <strong>También cuenta volver a ti.</strong>
                    <div className="td-panel-reward"><Sparkles size={16} /> Pelota antiestrés</div>
                  </div>
                </div>
                <div className="td-phone-tabs">
                  <span className="is-active" />
                  <span />
                  <span />
                </div>
              </div>
            </div>
            <div className="td-story-badge td-story-badge-one"><Coins size={17} /> +12</div>
            <div className="td-story-badge td-story-badge-two"><Check size={16} /> Listo</div>
          </div>

          <div className="td-story-scenes">
            {storyScenes.map((scene, index) => (
              <article
                key={scene.number}
                ref={(node) => {
                  storyRefs.current[index] = node;
                }}
                className={`td-story-scene ${storyStage === index ? 'is-active' : ''}`}
                data-story-stage={index}
              >
                <span className="td-story-number">{scene.number} / {scene.label}</span>
                <div className="td-story-scene-icon">
                  {index === 0 && <Target size={23} />}
                  {index === 1 && <Clock3 size={23} />}
                  {index === 2 && <Check size={23} />}
                  {index === 3 && <Coins size={23} />}
                </div>
                <h3>{scene.title}</h3>
                <p>{scene.copy}</p>
              </article>
            ))}
          </div>
        </div>
      </section>

      <section className="td-section" id="funciones">
        <div className="td-container">
          <Reveal as="div" className="td-section-header">
            <div>
              <div className="td-section-label">Diseñado para tu atención</div>
              <h2 className="td-display">Menos exigencia. Más movimiento.</h2>
            </div>
            <p>
              Apoyos claros para esos días en los que empezar ya es una victoria.
            </p>
          </Reveal>

          <div className="td-feature-layout">
            <Reveal
              as="article"
              className="td-feature-main"
              data-testid="card-feature-focus"
            >
              <div className="td-feature-icon">
                <Target size={27} strokeWidth={2.2} />
              </div>
              <div>
                <h3>Tu atención, a tu ritmo.</h3>
                <p>
                  Divide lo que tienes delante en bloques posibles y recibe una
                  señal clara cuando termines cada uno.
                </p>
              </div>
            </Reveal>
            <div className="td-feature-grid">
              {featureItems.map((item, index) => {
                const Icon = item.icon;
                return (
                  <Reveal
                    key={item.testId}
                    as="article"
                    className="td-feature-item"
                    data-testid={item.testId}
                    delay={90 * (index + 1)}
                  >
                    <Icon size={23} />
                    <h3>{item.title}</h3>
                    <p>{item.copy}</p>
                  </Reveal>
                );
              })}
            </div>
          </div>
        </div>
      </section>

      <section className="td-section td-rewards" id="recompensas">
        <div className="td-container">
          <Reveal as="div" className="td-section-header">
            <div>
              <div className="td-section-label">Lo que estás construyendo</div>
              <h2 className="td-display">Tus monedas, tus motivos.</h2>
            </div>
            <p>
              Las recompensas no son el objetivo. Son una forma tangible de
              recordar que sí avanzaste.
            </p>
          </Reveal>
          <div className="td-reward-grid">
            {rewardItems.map((item, index) => (
              <Reveal
                key={item.testId}
                as="article"
                className="td-reward-card"
                data-testid={item.testId}
                delay={80 * index}
              >
                <span className="td-reward-label">
                  {item.label}
                  <small>{item.caption}</small>
                </span>
                <img src={item.image} alt={item.alt} />
              </Reveal>
            ))}
          </div>
        </div>
      </section>

      <section className="td-section td-faq" id="preguntas">
        <div className="td-container td-faq-layout">
          <Reveal as="div" className="td-faq-intro">
            <div className="td-section-label">Antes de empezar</div>
            <h2 className="td-display">Lo importante, sin rodeos.</h2>
            <p>
              Todo lo necesario para descargar TD-App y dar tu primer paso con
              calma.
            </p>
          </Reveal>
          <Reveal as="div" className="td-faq-list" delay={90}>
            {faqItems.map((item) => (
              <details key={item.question} open={item.defaultOpen}>
                <summary>{item.question}</summary>
                <div className="td-faq-panel">
                  <p>{item.answer}</p>
                </div>
              </details>
            ))}
          </Reveal>
        </div>
      </section>

      <section className="td-download" id="descargar">
        <div className="td-container">
          <Reveal as="div" className="td-download-card">
            <div>
              <div className="td-section-label">Listo para empezar</div>
              <h2 className="td-display">El siguiente paso está aquí.</h2>
              <p>
                Descarga el APK oficial de TD-App para Android y empieza con una
                misión pequeña. La versión inicial está pensada para acompañarte
                sin saturarte.
              </p>
            </div>
            <div>
              <DownloadLink
                className="td-download-button"
                testId="link-final-download"
              >
                <Download size={22} />
                Descargar APK
              </DownloadLink>
              <div className="td-download-meta">
                <span>Android · APK</span>
                <span><ShieldCheck size={13} /> Fuente oficial</span>
              </div>
              <div className="td-hero-note" data-testid="text-final-installation-note">
                <Info size={15} />
                <span>
                  Si Android lo solicita, habilita temporalmente la instalación
                  desde esta fuente en Ajustes.
                </span>
              </div>
            </div>
          </Reveal>
        </div>
      </section>

      <footer className="td-footer">
        <div className="td-container td-footer-row">
          <a className="td-brand" href="#inicio" data-testid="link-footer-brand">
            <img src="/assets/td-coins-logo.png" alt="" aria-hidden="true" />
            <span>TD-App</span>
          </a>
          <p data-testid="text-footer-copy">Pequeñas victorias. Un paso a la vez.</p>
          <div className="td-footer-links">
            <a href="#funciones" data-testid="link-footer-features">Funciones</a>
            <a href="#descargar" data-testid="link-footer-download">Descargar</a>
          </div>
        </div>
      </footer>
    </main>
  );
}

function Router() {
  return (
    <RoutedErrorBoundary>
      <Switch>
        <Route path="/" component={Home} />
        <Route component={NotFound} />
      </Switch>
    </RoutedErrorBoundary>
  );
}

function RoutedErrorBoundary({ children }: { children: ReactNode }) {
  const [location] = useLocation();
  return <ErrorBoundary resetKey={location}>{children}</ErrorBoundary>;
}

function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <TooltipProvider>
        <WouterRouter base={import.meta.env.BASE_URL.replace(/\/$/, '')}>
          <Router />
        </WouterRouter>
        <Toaster />
      </TooltipProvider>
    </QueryClientProvider>
  );
}

export default App;
