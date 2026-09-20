import { useEffect, useState, type ReactNode } from 'react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import {
  Accessibility,
  ArrowDown,
  ArrowRight,
  Check,
  Clock3,
  Download,
  Info,
  Menu,
  Mic,
  ShieldCheck,
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
const downloadPath = '/downloads/td-coins.apk';

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
      download="td-coins.apk"
      aria-label="Descargar TD-Coins para Android"
    >
      {children}
    </a>
  );
}

function Home() {
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  useEffect(() => {
    document.title = 'TD-Coins | Un paso a la vez';
    const description =
      'TD-Coins convierte tus pequeños avances en monedas para ayudarte a empezar, enfocarte y celebrar.';
    let meta = document.querySelector('meta[name="description"]');
    if (!meta) {
      meta = document.createElement('meta');
      meta.setAttribute('name', 'description');
      document.head.appendChild(meta);
    }
    meta.setAttribute('content', description);
    document.documentElement.lang = 'es';
  }, []);

  const closeMenu = () => setMobileMenuOpen(false);

  return (
    <main className="td-page">
      <header className="td-container td-nav" data-testid="header-navigation">
        <a className="td-brand" href="#inicio" data-testid="link-brand">
          <img
            src="/assets/td-coins-logo.png"
            alt="Logo de TD-Coins"
            data-testid="img-brand-logo"
          />
          <span>TD-Coins</span>
        </a>

        <nav className="td-nav-links" aria-label="Navegación principal">
          <a href="#como-funciona" data-testid="link-how-it-works">Cómo funciona</a>
          <a href="#funciones" data-testid="link-features">Funciones</a>
          <a href="#recompensas" data-testid="link-rewards">Recompensas</a>
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
            TD-Coins es el compañero Android que convierte tus momentos de enfoque,
            misiones y hábitos en avances que puedes ver y celebrar.
          </p>
          <div className="td-hero-actions">
            <DownloadLink testId="link-hero-download">
              <Download size={19} />
              Descargar TD-Coins
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

        <div className="td-hero-art td-reveal td-delay-2" aria-label="Identidad visual de TD-Coins">
          <div className="td-coin-orbit">
            <img
              className="td-hero-logo"
              src="/assets/td-coins-logo.png"
              alt="Identidad de TD-Coins"
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
      </section>

      <section className="td-section" id="funciones">
        <div className="td-container">
          <div className="td-section-header">
            <div>
              <div className="td-section-label">Diseñado para tu atención</div>
              <h2 className="td-display">Menos exigencia. Más movimiento.</h2>
            </div>
            <p>
              Apoyos claros para esos días en los que empezar ya es una victoria.
            </p>
          </div>

          <div className="td-feature-layout">
            <article className="td-feature-main" data-testid="card-feature-focus">
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
            </article>
            <div className="td-feature-grid">
              <article className="td-feature-item" data-testid="card-feature-missions">
                <Clock3 size={23} />
                <h3>Bloques de enfoque</h3>
                <p>Temporizadores sencillos para entrar, estar y cerrar.</p>
              </article>
              <article className="td-feature-item" data-testid="card-feature-habits">
                <Check size={23} />
                <h3>Misiones y hábitos</h3>
                <p>Haz visible la constancia sin pedirte hacerlo perfecto.</p>
              </article>
              <article className="td-feature-item" data-testid="card-feature-voice">
                <Mic size={23} />
                <h3>Reflexión por voz</h3>
                <p>Guarda cómo te fue sin tener que sentarte a escribir.</p>
              </article>
              <article className="td-feature-item" data-testid="card-feature-accessibility">
                <Accessibility size={23} />
                <h3>Accesible de verdad</h3>
                <p>Controles comprensibles, apoyo visual y menos ruido.</p>
              </article>
            </div>
          </div>
        </div>
      </section>

      <section className="td-section" id="como-funciona">
        <div className="td-container">
          <div className="td-section-header">
            <div>
              <div className="td-section-label">La mecánica</div>
              <h2 className="td-display">Un sistema que te devuelve la prueba.</h2>
            </div>
            <p>
              No tienes que cambiar tu vida hoy. Solo completar lo que toca ahora.
            </p>
          </div>
          <div className="td-steps">
            <article className="td-step" data-testid="step-one">
              <span className="td-step-number">01 / ELIGE</span>
              <h3>Escoge una misión</h3>
              <p>Algo concreto, con un comienzo que puedas reconocer.</p>
            </article>
            <article className="td-step" data-testid="step-two">
              <span className="td-step-number">02 / AVANZA</span>
              <h3>Haz un bloque</h3>
              <p>Enfócate durante el tiempo que tenga sentido para ti.</p>
            </article>
            <article className="td-step" data-testid="step-three">
              <span className="td-step-number">03 / CELEBRA</span>
              <h3>Gana TD-Coins</h3>
              <p>Acumula pequeñas victorias y canjéalas por recompensas.</p>
            </article>
          </div>
        </div>
      </section>

      <section className="td-section td-rewards" id="recompensas">
        <div className="td-container">
          <div className="td-section-header">
            <div>
              <div className="td-section-label">Lo que estás construyendo</div>
              <h2 className="td-display">Tus monedas, tus motivos.</h2>
            </div>
            <p>
              Las recompensas no son el objetivo. Son una forma tangible de
              recordar que sí avanzaste.
            </p>
          </div>
          <div className="td-reward-grid">
            <article className="td-reward-card" data-testid="card-reward-ball">
              <span className="td-reward-label">
                Pelota antiestrés
                <small>Para volver al presente</small>
              </span>
              <img src="/assets/pelota.png" alt="Pelota antiestrés morada de TD-Coins" />
            </article>
            <article className="td-reward-card" data-testid="card-reward-keychain">
              <span className="td-reward-label">
                Llavero
                <small>Un paso a la vez</small>
              </span>
              <img src="/assets/llavero.png" alt="Llavero morado de TD-Coins" />
            </article>
            <article className="td-reward-card" data-testid="card-reward-mug">
              <span className="td-reward-label">
                Taza
                <small>Tu pausa también cuenta</small>
              </span>
              <img src="/assets/taza.png" alt="Taza de TD-Coins" />
            </article>
            <article className="td-reward-card" data-testid="card-reward-backpack">
              <span className="td-reward-label">
                Mochila
                <small>Para lo que viene</small>
              </span>
              <img src="/assets/mochila.png" alt="Mochila lila de TD-Coins" />
            </article>
          </div>
        </div>
      </section>

      <section className="td-download" id="descargar">
        <div className="td-container">
          <div className="td-download-card">
            <div>
              <div className="td-section-label">Listo para empezar</div>
              <h2 className="td-display">El siguiente paso está aquí.</h2>
              <p>
                Descarga el APK oficial de TD-Coins para Android y empieza con una
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
                Descargar TD-Coins.apk
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
          </div>
        </div>
      </section>

      <footer className="td-footer">
        <div className="td-container td-footer-row">
          <a className="td-brand" href="#inicio" data-testid="link-footer-brand">
            <img src="/assets/td-coins-logo.png" alt="" aria-hidden="true" />
            <span>TD-Coins</span>
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