import React, { useState, useEffect, useRef, useCallback } from "react";

// Iconos React
import { GoHomeFill } from "react-icons/go";
import { LuTimer } from "react-icons/lu";
import { LuClipboardList } from "react-icons/lu";
import { LuShoppingBag } from "react-icons/lu";
import { LuMic } from "react-icons/lu";
import { IoMicOutline } from "react-icons/io5";
import { FaSquare } from "react-icons/fa";
import { LuRefreshCcw } from "react-icons/lu";
import { CgCoffee } from "react-icons/cg";
import { LuTarget } from "react-icons/lu";
import { FaPlay } from "react-icons/fa6";
import { FaPause } from "react-icons/fa6";

// Imagenes
import logo from "./Imagenes/logo.png";
import pelota from "./Imagenes/pelota.png";
import llavero from "./Imagenes/llavero.png";
import taza from "./Imagenes/taza.png";
import gorra from "./Imagenes/gorra.png";
import playera from "./Imagenes/playera.png";
import funda from "./Imagenes/funda.png";
import mochila from "./Imagenes/mochila.png";
import regalo from "./Imagenes/regalo.png";

// ─── Types ───────────────────────────────────────────────────────────────────
type Tab = "home" | "pomodoro" | "tasks" | "store" | "voice";
type Task = {
  id: string;
  title: string;
  category: string;
  target: number;
  progress: number;
  coins: number;
  completed: boolean;
};
type StoreItem = {
  id: string;
  name: string;
  price: number;
  emoji: React.ReactNode;
  description: string;
  tag: string;
};
type VoiceChallenge = {
  id: string;
  text: string;
  icon: string;
  reminders: string[];
  plan: string[];
};

// ─── Data ────────────────────────────────────────────────────────────────────
const STORE_ITEMS: StoreItem[] = [
  { id: "1", name: "Bola Anti-Estrés TDAH", price: 120, emoji: <img src={pelota} alt="Pelota" />, description: "Bola de silicona con el logo TD-Coins para manejar la ansiedad", tag: "Anti-estrés" },
  { id: "2", name: "Llavero Fuerza Mental", price: 80, emoji: <img src={llavero} alt="Llavero" />, description: "Llavero metálico con el mantra 'Enfoque es mi superpoder'", tag: "Accesorio" },
  { id: "3", name: "Taza Cerebro en Llamas", price: 200, emoji: <img src={taza} alt="Taza" />, description: "Taza de 350ml con frases motivacionales para TDAH", tag: "Lifestyle" },
  { id: "4", name: "Gorra TD-Coins", price: 350, emoji: <img src={gorra} alt="Gorra" />, description: "Gorra snapback bordada con el logo oficial de TD-Coins", tag: "Ropa" },
  { id: "5", name: "Playera Superhéroe TDAH", price: 450, emoji: <img src={playera} alt="Playera" />, description: "Playera unisex 'Mi TDAH es mi superpoder'", tag: "Ropa" },
  { id: "6", name: "Funda Protectora TD-App", price: 180, emoji: <img src={funda} alt="Funda" />, description: "Funda para celular con diseño anti-distracción", tag: "Tech" },
  { id: "7", name: "Mochila Explorador", price: 800, emoji: <img src={mochila} alt="Mochila" />, description: "Mochila con múltiples compartimentos y diseño TD-Coins", tag: "Lifestyle" },
  { id: "8", name: "Pack Inicio Hero", price: 280, emoji: <img src={regalo} alt="Regalo" />, description: "Set llavero + bola antiestres + calcomanías exclusivas", tag: "Pack" },
];

const VOICE_CHALLENGES: VoiceChallenge[] = [
  {
    id: "sleep",
    text: "Se me dificulta dormir",
    icon: "",
    reminders: ["Apaga pantallas 30 min antes de dormir", "Recordatorio a las 9:30 PM: Rutina nocturna", "Alarma suave a las 10:00 PM: Hora de descansar"],
    plan: ["Crea una rutina nocturna de 15 min", "Usa luz tenue por la noche", "Escribe 3 cosas buenas del día antes de dormir", "Prueba respiración 4-7-8 para relajarte"]
  },
  {
    id: "focus",
    text: "Se me dificulta poner atención",
    icon: "",
    reminders: ["Pomodoro activo: 25 min de enfoque puro", "Silencia notificaciones durante el Pomodoro", "Cada hora: Levántate y muévete 2 minutos"],
    plan: ["Usa el método Pomodoro (25-5 min)", "Elimina distracciones del escritorio", "Escribe la tarea actual en un post-it", "Escucha música sin letras mientras trabajas"]
  },
  {
    id: "finish",
    text: "Inicio las cosas y no las termino",
    icon: "",
    reminders: ["Recordatorio: ¿Terminaste la tarea de hoy?", "Check-in a la mitad del día: ¿Cómo vas?", "Celebra cada tarea terminada con una TD-Coin"],
    plan: ["Divide cada tarea en pasos de 10 min", "Comprométete solo con UNA tarea a la vez", "Usa la regla de los 2 minutos: si tarda poco, hazlo ya", "Registra tu progreso visualmente"]
  },
  {
    id: "organize",
    text: "Me cuesta organizarme",
    icon: "",
    reminders: ["Cada mañana: Revisa tus 3 prioridades del día", "Recordatorio de planificación: Domingo 7 PM", "Alerta: No olvides tu lista de tareas"],
    plan: ["Escribe tus tareas la noche anterior", "Usa colores para priorizar tareas", "Pon solo 3 tareas principales por día", "Revisa tu lista cada mañana al despertar"]
  },
  {
    id: "impulsive",
    text: "Soy muy impulsivo/a",
    icon: "",
    reminders: ["Antes de actuar: Respira profundo 5 veces", "Recordatorio: Espera 10 min antes de decidir", "Check emocional: ¿Cómo te sientes ahora?"],
    plan: ["Practica la pausa de 10 segundos", "Escribe antes de responder algo importante", "Identifica tus detonantes emocionales", "Celebra cada vez que pauses antes de reaccionar"]
  },
];

const INITIAL_TASKS: Task[] = [
  { id: "t1", title: "Completar 3 Pomodoros hoy", category: "Enfoque", target: 3, progress: 0, coins: 30, completed: false },
  { id: "t2", title: "Rutina matutina completa", category: "Hábitos", target: 7, progress: 2, coins: 50, completed: false },
  { id: "t3", title: "Leer 20 minutos sin distracción", category: "Enfoque", target: 5, progress: 5, coins: 40, completed: false },
  { id: "t4", title: "Ejercicio 15 minutos", category: "Salud", target: 10, progress: 3, coins: 35, completed: false },
  { id: "t5", title: "Organizar escritorio", category: "Orden", target: 1, progress: 1, coins: 20, completed: false },
];

// ─── Components ──────────────────────────────────────────────────────────────

function MemphisShape({ className }: { className: string }) {
  return <div className={`absolute pointer-events-none ${className}`} />;
}

function CoinBadge({ amount, small }: { amount: number; small?: boolean }) {
  return (
    <div className={`flex items-center gap-1 bg-amber-400 text-amber-900 font-bold rounded-full ${small ? "px-2 py-0.5 text-xs" : "px-3 py-1 text-sm"}`}>
      <span>🪙</span>
      <span>{amount}</span>
    </div>
  );
}

// ─── Pomodoro Screen ─────────────────────────────────────────────────────────
function PomodoroScreen({ onComplete }: { onComplete: () => void }) {
  const [isWork, setIsWork] = useState(true);
  const [seconds, setSeconds] = useState(25 * 60);
  const [running, setRunning] = useState(false);
  const [sessions, setSessions] = useState(0);
  const [showCelebration, setShowCelebration] = useState(false);
  const intervalRef = useRef<ReturnType<typeof setInterval> | null>(null);

  const total = isWork ? 25 * 60 : 5 * 60;
  const progress = 1 - seconds / total;
  const radius = 110;
  const circumference = 2 * Math.PI * radius;
  const dashOffset = circumference * (1 - progress);

  const minutes = Math.floor(seconds / 60);
  const secs = seconds % 60;

  const tick = useCallback(() => {
    setSeconds((s) => {
      if (s <= 1) {
        setRunning(false);
        if (isWork) {
          setSessions((n) => n + 1);
          setShowCelebration(true);
          setTimeout(() => {
            setShowCelebration(false);
            setIsWork(false);
            setSeconds(5 * 60);
            onComplete();
          }, 2000);
        } else {
          setIsWork(true);
          setSeconds(25 * 60);
        }
        return 0;
      }
      return s - 1;
    });
  }, [isWork, onComplete]);

  useEffect(() => {
    if (running) {
      intervalRef.current = setInterval(tick, 1000);
    } else {
      if (intervalRef.current) clearInterval(intervalRef.current);
    }
    return () => { if (intervalRef.current) clearInterval(intervalRef.current); };
  }, [running, tick]);

  const reset = () => {
    setRunning(false);
    setSeconds(isWork ? 25 * 60 : 5 * 60);
  };

  const switchMode = () => {
    setRunning(false);
    setIsWork(!isWork);
    setSeconds(isWork ? 5 * 60 : 25 * 60);
  };

  const color = isWork ? "#7C3AED" : "#14B8A6";
  const bgColor = isWork ? "#EDE9F8" : "#CCFBF1";

  return (
    <div className="flex flex-col items-center gap-6 p-6 relative">
      {/* Memphis decorations */}
      <MemphisShape className="w-16 h-16 rounded-full bg-amber-300 opacity-30 -top-2 -left-4 float-anim" />
      <MemphisShape className="w-8 h-8 bg-pink-400 opacity-20 top-10 right-2 rotate-45" />

      <div className="text-center">
        <h2 className="text-2xl font-black" style={{ fontFamily: "var(--font-display)" }}>
          {isWork ? "Tiempo de Enfoque" : "Tiempo de Descanso"}
        </h2>
        <p className="text-sm text-muted-foreground mt-1">
          {isWork ? "Concéntrate. Puedes hacerlo." : "Respira, te lo mereces."}
        </p>
      </div>

      {/* Timer circle */}
      <div className="relative" style={{ width: 260, height: 260 }}>
        <svg width="260" height="260" style={{ transform: "rotate(-90deg)" }}>
          <circle cx="130" cy="130" r={radius} fill="none" stroke={bgColor} strokeWidth="14" />
          <circle
            cx="130" cy="130" r={radius}
            fill="none"
            stroke={color}
            strokeWidth="14"
            strokeLinecap="round"
            strokeDasharray={circumference}
            strokeDashoffset={dashOffset}
            className="timer-ring"
          />
        </svg>
        <div className="absolute inset-0 flex flex-col items-center justify-center gap-1">
          <span className="text-5xl font-black" style={{ fontFamily: "var(--font-mono)", color }}>
            {String(minutes).padStart(2, "0")}:{String(secs).padStart(2, "0")}
          </span>
          <span className="text-xs font-semibold uppercase tracking-widest text-muted-foreground">
            {isWork ? "ENFOQUE" : "DESCANSO"}
          </span>
        </div>
      </div>

      {/* Controls */}
      <div className="flex gap-3 items-center">
        <button
          onClick={reset}
          className="w-12 h-12 rounded-full border-2 border-border flex items-center justify-center text-lg hover:bg-muted transition-colors"
        >
          <LuRefreshCcw />
        </button>
        <button
          onClick={() => setRunning(!running)}
          className="w-20 h-20 rounded-full text-white text-3xl font-black shadow-lg flex items-center justify-center transition-transform active:scale-95"
          style={{ backgroundColor: color, boxShadow: `0 6px 24px ${color}55` }}
        >
          {running ? <FaPause /> : <FaPlay />}
        </button>
        <button
          onClick={switchMode}
          className="w-12 h-12 rounded-full border-2 border-border flex items-center justify-center text-lg hover:bg-muted transition-colors"
        >
          {isWork ? <CgCoffee /> : <LuTarget />}
        </button>
      </div>

      {/* Sessions */}
      <div className="flex flex-col items-center gap-2">
        
        
        
      </div>

      {/* Tips */}
      <div className="w-full rounded-2xl p-4 text-sm" style={{ backgroundColor: bgColor }}>
        <p className="font-bold mb-1" style={{ color }}>Tip para este bloque</p>
        <p className="text-foreground opacity-80">
          {isWork
            ? "Cierra redes sociales. Pon el celular boca abajo. Solo tú y esta tarea."
            : "Levántate, estírate, toma agua. Tu cerebro lo necesita."}
        </p>
      </div>

      {/* Celebration overlay */}
      {showCelebration && (
        <div className="fixed inset-0 flex items-center justify-center z-50 bg-black/40 backdrop-blur-sm">
          <div className="bg-white rounded-3xl p-8 text-center bounce-in shadow-2xl">
            <div className="text-6xl mb-3">🎉</div>
            <h3 className="text-2xl font-black text-primary">¡Pomodoro completo!</h3>
            <p className="text-muted-foreground mt-1">+10 TD-Coins ganadas</p>
            <CoinBadge amount={10} />
          </div>
        </div>
      )}
    </div>
  );
}

// ─── Tasks Screen ─────────────────────────────────────────────────────────────
function TasksScreen({ coins, setCoins }: { coins: number; setCoins: React.Dispatch<React.SetStateAction<number>> }) {
  const [tasks, setTasks] = useState<Task[]>(INITIAL_TASKS);
  const [showAdd, setShowAdd] = useState(false);
  const [newTask, setNewTask] = useState({ title: "", category: "Enfoque", target: 5, coins: 30 });
  const [celebrated, setCelebrated] = useState<string | null>(null);

  const increment = (id: string) => {
    setTasks((prev) =>
      prev.map((t) => {
        if (t.id !== id || t.completed) return t;
        const next = t.progress + 1;
        const done = next >= t.target;
        if (done && !t.completed) {
          setCoins((c) => c + t.coins);
          setCelebrated(id);
          setTimeout(() => setCelebrated(null), 2000);
        }
        return { ...t, progress: Math.min(next, t.target), completed: done };
      })
    );
  };

  const addTask = () => {
    if (!newTask.title.trim()) return;
    const task: Task = {
      id: `t${Date.now()}`,
      title: newTask.title,
      category: newTask.category,
      target: newTask.target,
      progress: 0,
      coins: newTask.coins,
      completed: false,
    };
    setTasks((prev) => [task, ...prev]);
    setShowAdd(false);
    setNewTask({ title: "", category: "Enfoque", target: 5, coins: 30 });
  };

  const categories = ["Enfoque", "Hábitos", "Salud", "Orden", "Social"];
  const catColors: Record<string, string> = {
    Enfoque: "#7C3AED", Hábitos: "#F97316", Salud: "#14B8A6", Orden: "#3B82F6", Social: "#EC4899",
  };

  return (
    <div className="p-5 flex flex-col gap-4">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-2xl font-black" style={{ fontFamily: "var(--font-display)" }}>Mis Misiones</h2>
          <p className="text-sm text-muted-foreground">Completa y gana TD-Coins 🪙</p>
        </div>
        <button
          onClick={() => setShowAdd(!showAdd)}
          className="w-10 h-10 rounded-full bg-primary text-white flex items-center justify-center text-xl font-bold shadow-md hover:opacity-90 transition-opacity"
        >
          +
        </button>
      </div>

      {showAdd && (
        <div className="bg-white rounded-2xl p-4 shadow-lg border border-border bounce-in">
          <h3 className="font-bold mb-3 text-sm uppercase tracking-wide text-muted-foreground">Nueva Misión</h3>
          <input
            value={newTask.title}
            onChange={(e) => setNewTask({ ...newTask, title: e.target.value })}
            placeholder="¿Qué quieres lograr?"
            className="w-full border border-border rounded-xl px-3 py-2 text-sm mb-2 focus:outline-none focus:ring-2 focus:ring-primary"
          />
          <div className="flex gap-2 mb-2 flex-wrap">
            {categories.map((c) => (
              <button
                key={c}
                onClick={() => setNewTask({ ...newTask, category: c })}
                className="px-3 py-1 rounded-full text-xs font-semibold border transition-all"
                style={{
                  backgroundColor: newTask.category === c ? catColors[c] : "transparent",
                  borderColor: catColors[c],
                  color: newTask.category === c ? "white" : catColors[c],
                }}
              >
                {c}
              </button>
            ))}
          </div>
          <div className="flex gap-2">
            <div className="flex-1">
              <label className="text-xs text-muted-foreground">Pasos meta</label>
              <input
                type="number"
                min={1}
                max={30}
                value={newTask.target}
                onChange={(e) => setNewTask({ ...newTask, target: Number(e.target.value) })}
                className="w-full border border-border rounded-xl px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-primary"
              />
            </div>
            <div className="flex-1">
              <label className="text-xs text-muted-foreground">Recompensa 🪙</label>
              <input
                type="number"
                min={10}
                max={500}
                value={newTask.coins}
                onChange={(e) => setNewTask({ ...newTask, coins: Number(e.target.value) })}
                className="w-full border border-border rounded-xl px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-primary"
              />
            </div>
          </div>
          <button
            onClick={addTask}
            className="w-full mt-3 bg-primary text-white rounded-xl py-2 text-sm font-bold hover:opacity-90 transition-opacity"
          >
            Agregar Misión
          </button>
        </div>
      )}

      {tasks.map((task) => {
        const pct = Math.round((task.progress / task.target) * 100);
        const col = catColors[task.category] || "#7C3AED";
        return (
          <div
            key={task.id}
            className="bg-white rounded-2xl p-4 shadow-sm border border-border relative overflow-hidden"
          >
            {task.completed && (
              <div className="absolute inset-0 bg-green-50 pointer-events-none" />
            )}
            {celebrated === task.id && (
              <div className="absolute inset-0 flex items-center justify-center bg-amber-400/20 z-10 bounce-in rounded-2xl">
                <span className="text-4xl">🎉</span>
              </div>
            )}
            <div className="flex items-start justify-between gap-2 mb-3">
              <div className="flex-1">
                <div className="flex items-center gap-2 flex-wrap">
                  <span
                    className="text-xs font-bold px-2 py-0.5 rounded-full text-white"
                    style={{ backgroundColor: col }}
                  >
                    {task.category}
                  </span>
                  {task.completed && (
                    <span className="text-xs font-bold px-2 py-0.5 rounded-full bg-green-100 text-green-700">
                      ✓ Completada
                    </span>
                  )}
                </div>
                <p className={`font-bold mt-1 text-sm ${task.completed ? "line-through text-muted-foreground" : ""}`}>
                  {task.title}
                </p>
              </div>
              <CoinBadge amount={task.coins} small />
            </div>

            <div className="flex items-center gap-3">
              <div className="flex-1">
                <div className="flex justify-between text-xs text-muted-foreground mb-1">
                  <span>{task.progress}/{task.target} pasos</span>
                  <span>{pct}%</span>
                </div>
                <div className="w-full h-2.5 rounded-full bg-muted overflow-hidden">
                  <div
                    className="h-full rounded-full transition-all duration-500"
                    style={{ width: `${pct}%`, backgroundColor: col }}
                  />
                </div>
              </div>
              {!task.completed && (
                <button
                  onClick={() => increment(task.id)}
                  className="w-9 h-9 rounded-full text-white text-sm font-bold flex items-center justify-center flex-shrink-0 transition-transform active:scale-90"
                  style={{ backgroundColor: col }}
                >
                  +1
                </button>
              )}
            </div>
          </div>
        );
      })}
    </div>
  );
}

// ─── Store Screen ─────────────────────────────────────────────────────────────
function StoreScreen({ coins, setCoins }: { coins: number; setCoins: React.Dispatch<React.SetStateAction<number>> }) {
  const [filter, setFilter] = useState("Todos");
  const [purchased, setPurchased] = useState<string[]>([]);
  const [showConfirm, setShowConfirm] = useState<StoreItem | null>(null);

  const tags = ["Todos", "Anti-estrés", "Accesorio", "Lifestyle", "Ropa", "Tech", "Pack"];
  const filtered = filter === "Todos" ? STORE_ITEMS : STORE_ITEMS.filter((i) => i.tag === filter);

  const buy = (item: StoreItem) => {
    if (coins < item.price) return;
    setCoins((c) => c - item.price);
    setPurchased((p) => [...p, item.id]);
    setShowConfirm(null);
  };

  return (
    <div className="p-5 flex flex-col gap-4">
      <div>
        <h2 className="text-2xl font-black" style={{ fontFamily: "var(--font-display)" }}>Tienda TD-Coins</h2>
        <p className="text-sm text-muted-foreground">Canjea tus monedas por mercancía real</p>
      </div>

      {/* Balance */}
      <div className="rounded-2xl p-4 flex items-center gap-4" style={{ background: "linear-gradient(135deg, #7C3AED, #A855F7)" }}>
        <div className="text-4xl">🪙</div>
        <div className="text-white">
          <p className="text-xs font-semibold opacity-80 uppercase tracking-wider">Tu saldo</p>
          <p className="text-3xl font-black" style={{ fontFamily: "var(--font-mono)" }}>{coins}</p>
          <p className="text-xs opacity-70">TD-Coins disponibles</p>
        </div>
      </div>

      {/* Category filter */}
      <div className="flex gap-2 overflow-x-auto pb-1 -mx-1 px-1">
        {tags.map((t) => (
          <button
            key={t}
            onClick={() => setFilter(t)}
            className="px-3 py-1.5 rounded-full text-xs font-semibold whitespace-nowrap border transition-all flex-shrink-0"
            style={{
              backgroundColor: filter === t ? "#7C3AED" : "transparent",
              borderColor: filter === t ? "#7C3AED" : "#D8CCF0",
              color: filter === t ? "white" : "#6B5B8A",
            }}
          >
            {t}
          </button>
        ))}
      </div>

      {/* Grid */}
      <div className="grid grid-cols-2 gap-3">
        {filtered.map((item) => {
          const owned = purchased.includes(item.id);
          const canAfford = coins >= item.price;
          return (
            <div
              key={item.id}
              className="bg-white rounded-2xl p-3 shadow-sm border border-border flex flex-col gap-2"
            >
              <div
                className="w-full aspect-square rounded-xl flex items-center justify-center text-5xl"
                style={{ background: "linear-gradient(135deg, #F5F0FF, #EDE9F8)" }}
              >
                {item.emoji}
              </div>
              <div>
                <span className="text-xs text-muted-foreground font-medium">{item.tag}</span>
                <p className="text-sm font-bold leading-tight">{item.name}</p>
              </div>
              <div className="flex items-center justify-between mt-auto">
                <CoinBadge amount={item.price} small />
                {owned ? (
                  <span className="text-xs font-bold text-green-600">✓ Tuyo</span>
                ) : (
                  <button
                    onClick={() => setShowConfirm(item)}
                    disabled={!canAfford}
                    className="text-xs font-bold px-2.5 py-1 rounded-full text-white transition-opacity"
                    style={{
                      backgroundColor: canAfford ? "#7C3AED" : "#D8CCF0",
                      color: canAfford ? "white" : "#9CA3AF",
                    }}
                  >
                    Canjear
                  </button>
                )}
              </div>
            </div>
          );
        })}
      </div>

      {/* Confirm modal */}
      {showConfirm && (
        <div className="fixed inset-0 bg-black/40 backdrop-blur-sm flex items-end justify-center z-50">
          <div className="bg-white rounded-t-3xl p-6 w-full max-w-md bounce-in">
            <div className="text-center mb-4">
              <div className="text-6xl mb-2">{showConfirm.emoji}</div>
              <h3 className="text-xl font-black">{showConfirm.name}</h3>
              <p className="text-sm text-muted-foreground mt-1">{showConfirm.description}</p>
            </div>
            <div className="flex justify-between items-center bg-muted rounded-xl p-3 mb-4">
              <span className="text-sm font-medium">Costo</span>
              <CoinBadge amount={showConfirm.price} />
            </div>
            <div className="flex justify-between items-center bg-muted rounded-xl p-3 mb-4">
              <span className="text-sm font-medium">Saldo después</span>
              <CoinBadge amount={coins - showConfirm.price} />
            </div>
            <div className="flex gap-3">
              <button
                onClick={() => setShowConfirm(null)}
                className="flex-1 py-3 rounded-xl border border-border font-bold text-sm"
              >
                Cancelar
              </button>
              <button
                onClick={() => buy(showConfirm)}
                className="flex-1 py-3 rounded-xl bg-primary text-white font-bold text-sm"
              >
                ¡Canjear! 🎉
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

// ─── Voice Screen ─────────────────────────────────────────────────────────────
function VoiceScreen() {
  const [recording, setRecording] = useState(false);
  const [selected, setSelected] = useState<VoiceChallenge[]>([]);
  const [step, setStep] = useState<"select" | "plan">("select");
  const [showVoiceAnim, setShowVoiceAnim] = useState(false);

  const toggle = (c: VoiceChallenge) => {
    setSelected((prev) =>
      prev.find((s) => s.id === c.id) ? prev.filter((s) => s.id !== c.id) : [...prev, c]
    );
  };

  const simulateVoice = () => {
    setRecording(true);
    setShowVoiceAnim(true);
    setTimeout(() => {
      setRecording(false);
      setShowVoiceAnim(false);
    }, 3000);
  };

  if (step === "plan" && selected.length > 0) {
    return (
      <div className="p-5 flex flex-col gap-4">
        <div className="flex items-center gap-3">
          <button
            onClick={() => setStep("select")}
            className="w-9 h-9 rounded-full bg-muted flex items-center justify-center text-sm"
          >
            ←
          </button>
          <div>
            <h2 className="text-xl font-black" style={{ fontFamily: "var(--font-display)" }}>Tu Plan Personalizado</h2>
            <p className="text-xs text-muted-foreground">Basado en tus desafíos</p>
          </div>
        </div>

        {selected.map((c) => (
          <div key={c.id} className="bg-white rounded-2xl p-4 shadow-sm border border-border">
            <div className="flex items-center gap-2 mb-3">
              <span className="text-2xl">{c.icon}</span>
              <h3 className="font-black text-sm">{c.text}</h3>
            </div>

            <div className="mb-3">
              <p className="text-xs font-bold uppercase tracking-wider text-primary mb-2">Recordatorios activados</p>
              {c.reminders.map((r, i) => (
                <div key={i} className="flex items-start gap-2 py-1.5 border-b border-border last:border-0">
                  <span className="text-xs mt-0.5"></span>
                  <p className="text-xs">{r}</p>
                </div>
              ))}
            </div>

            <div>
              <p className="text-xs font-bold uppercase tracking-wider text-secondary mb-2">Plan de Acción</p>
              {c.plan.map((p, i) => (
                <div key={i} className="flex items-start gap-2 py-1.5 border-b border-border last:border-0">
                  <span className="text-xs font-bold text-secondary mt-0.5">{i + 1}.</span>
                  <p className="text-xs">{p}</p>
                </div>
              ))}
            </div>
          </div>
        ))}

        <div className="rounded-2xl p-4 text-sm" style={{ background: "linear-gradient(135deg, #14B8A6, #0D9488)" }}>
          <p className="font-black text-white mb-1">Recuerda</p>
          <p className="text-white opacity-90 text-xs">
            El TDAH es una diferencia, no un defecto. Con las herramientas correctas, puedes lograr todo lo que te propones. ¡Vamos!
          </p>
        </div>
      </div>
    );
  }

  return (
    <div className="p-5 flex flex-col gap-4">
      <div>
        <h2 className="text-2xl font-black" style={{ fontFamily: "var(--font-display)" }}>Personaliza tu App</h2>
        <p className="text-sm text-muted-foreground">Cuéntame tus retos o elige los que aplican</p>
      </div>

      {/* Voice button */}
      <div className="bg-white rounded-2xl p-5 shadow-sm border border-border flex flex-col items-center gap-3">
        <p className="text-sm font-semibold text-center text-muted-foreground">
          Presiona y habla sobre tus dificultades
        </p>
        <button
          onClick={simulateVoice}
          className="w-20 h-20 rounded-full flex items-center justify-center text-white text-3xl shadow-lg transition-transform active:scale-95 relative"
          style={{ backgroundColor: recording ? "#EF4444" : "#7C3AED" }}
        >
          {recording ? <FaSquare /> : <IoMicOutline />}
          {recording && (
            <span className="absolute inset-0 rounded-full animate-ping" style={{ backgroundColor: "#EF444430" }} />
          )}
        </button>

        {showVoiceAnim && (
          <div className="flex items-end gap-1 h-8">
            {[1, 2, 3, 4, 5].map((i) => (
              <div
                key={i}
                className="wave-bar w-2 rounded-full bg-primary"
                style={{ height: `${Math.random() * 20 + 10}px` }}
              />
            ))}
          </div>
        )}

        {recording && (
          <p className="text-xs text-red-500 font-semibold animate-pulse">Escuchando...</p>
        )}
        {!recording && (
          <p className="text-xs text-muted-foreground">o selecciona tus retos abajo</p>
        )}
      </div>

      {/* Challenge cards */}
      <p className="text-xs font-bold uppercase tracking-wider text-muted-foreground">Mis retos principales</p>
      <div className="flex flex-col gap-2">
        {VOICE_CHALLENGES.map((c) => {
          const isSelected = selected.some((s) => s.id === c.id);
          return (
            <button
              key={c.id}
              onClick={() => toggle(c)}
              className="flex items-center gap-3 p-4 rounded-2xl border-2 text-left transition-all"
              style={{
                borderColor: isSelected ? "#7C3AED" : "#D8CCF0",
                backgroundColor: isSelected ? "#EDE9F8" : "white",
              }}
            >
              <span className="text-2xl flex-shrink-0">{c.icon}</span>
              <p className="text-sm font-semibold flex-1">{c.text}</p>
              <div
                className="w-5 h-5 rounded-full border-2 flex items-center justify-center flex-shrink-0 transition-all"
                style={{
                  borderColor: isSelected ? "#7C3AED" : "#D8CCF0",
                  backgroundColor: isSelected ? "#7C3AED" : "transparent",
                }}
              >
                {isSelected && <span className="text-white text-xs">✓</span>}
              </div>
            </button>
          );
        })}
      </div>

      {selected.length > 0 && (
        <button
          onClick={() => setStep("plan")}
          className="w-full py-4 rounded-2xl text-white font-black text-base shadow-lg transition-transform active:scale-95"
          style={{ background: "linear-gradient(135deg, #7C3AED, #A855F7)" }}
        >
          Ver mi Plan Personalizado ({selected.length} retos) →
        </button>
      )}
    </div>
  );
}

// ─── Home Screen ──────────────────────────────────────────────────────────────
function HomeScreen({
  coins,
  pomodorosDone,
  onNavigate,
}: {
  coins: number;
  pomodorosDone: number;
  onNavigate: (t: Tab) => void;
}) {
  const tasksCompleted = INITIAL_TASKS.filter((t) => t.completed).length;
  const hour = new Date().getHours();
  const greeting = hour < 12 ? "Buenos días" : hour < 18 ? "Buenas tardes" : "Buenas noches";

  const stats = [
    { label: "TD-Coins", value: coins, icon: "🪙", color: "#F59E0B" },
    { label: "Pomodoros", value: pomodorosDone, icon: "🍅", color: "#7C3AED" },
    { label: "Misiones", value: tasksCompleted, icon: "✅", color: "#14B8A6" },
  ];

  const shortcuts = [
    { tab: "pomodoro" as Tab, icon: <LuTimer/>, label: "Pomodoro", desc: "Inicia un bloque de enfoque", color: "#7C3AED" },
    { tab: "tasks" as Tab, icon: <LuClipboardList/>, label: "Misiones", desc: "Revisa tu progreso", color: "#F97316" },
    { tab: "voice" as Tab, icon: <LuMic/>, label: "Personalizar", desc: "Cuéntame tus retos", color: "#14B8A6" },
    { tab: "store" as Tab, icon: <LuShoppingBag/>, label: "Tienda", desc: "Canjea tus monedas", color: "#EC4899" },
  ];

  const tipOfDay = [
    "El TDAH no es falta de atención — es atención desregulada. Hay una gran diferencia.",
    "Los pequeños pasos cuentan. Cada Pomodoro completado es una victoria real.",
    "Tu cerebro con TDAH es creativo, apasionado y único. Úsalo a tu favor.",
    "No necesitas motivación para empezar — solo necesitas dar el primer paso.",
  ][new Date().getDay() % 4];

  return (
    <div className="p-5 flex flex-col gap-5 relative overflow-hidden">
      {/* Memphis bg shapes */}
      <div className="absolute -top-8 -right-8 w-32 h-32 rounded-full opacity-10" style={{ backgroundColor: "#F97316" }} />
      <div className="absolute top-20 -left-6 w-20 h-20 rotate-12 opacity-10" style={{ backgroundColor: "#7C3AED" }} />

      {/* Header */}
      <div className="flex items-start justify-between">
        <div>
          <p className="text-sm text-muted-foreground font-medium">{greeting} </p>
          <h1 className="text-3xl font-black leading-tight" style={{ fontFamily: "var(--font-display)" }}>
            ¡Hoy puedes<br />lograrlo! 
          </h1>
        </div>
        <div className="flex flex-col items-end gap-1">
          <div className="flex items-center gap-1.5 bg-amber-400 rounded-full px-3 py-1.5">
            <span className="text-sm">🪙</span>
            <span className="font-black text-amber-900" style={{ fontFamily: "var(--font-mono)" }}>{coins}</span>
          </div>
          <p className="text-xs text-muted-foreground">TD-Coins</p>
        </div>
      </div>

      {/* Stats row */}
      <div className="grid grid-cols-3 gap-3">
        {stats.map((s) => (
          <div key={s.label} className="bg-white rounded-2xl p-3 shadow-sm border border-border text-center">
            <div className="text-2xl mb-1">{s.icon}</div>
            <div className="text-xl font-black" style={{ color: s.color, fontFamily: "var(--font-mono)" }}>{s.value}</div>
            <div className="text-xs text-muted-foreground">{s.label}</div>
          </div>
        ))}
      </div>

      {/* Daily quote */}
      <div
        className="rounded-2xl p-4 relative overflow-hidden"
        style={{ background: "linear-gradient(135deg, #1A0A2E)" }}
      >
        <div className="absolute top-0 right-0 w-24 h-24 rounded-full opacity-10 -translate-y-4 translate-x-4" style={{ backgroundColor: "#F97316" }} />
        <p className="text-xs font-bold uppercase tracking-wider text-purple-300 mb-1">Frase del día</p>
        <p className="text-white font-semibold text-sm leading-relaxed">{tipOfDay}</p>
      </div>

      {/* Quick actions */}
      <div>
        <p className="text-xs font-bold uppercase tracking-wider text-muted-foreground mb-3">Acceso rápido</p>
        <div className="grid grid-cols-2 gap-3">
          {shortcuts.map((s) => (
            <button
              key={s.tab}
              onClick={() => onNavigate(s.tab)}
              className="bg-white rounded-2xl p-4 shadow-sm border border-border text-left hover:shadow-md transition-all active:scale-95"
            >
              <div
                className="w-10 h-10 rounded-xl flex items-center justify-center text-xl mb-2"
                style={{ backgroundColor: `${s.color}18` }}
              >
                {s.icon}
              </div>
              <p className="font-bold text-sm">{s.label}</p>
              <p className="text-xs text-muted-foreground">{s.desc}</p>
            </button>
          ))}
        </div>
      </div>

      {/* Progress bar to next reward */}
      <div className="bg-white rounded-2xl p-4 shadow-sm border border-border">
        <div className="flex justify-between items-center mb-2">
          <p className="text-sm font-bold">Próxima recompensa</p>
          <CoinBadge amount={80} small />
        </div>
        <div className="w-full h-3 rounded-full bg-muted overflow-hidden">
          <div
            className="h-full rounded-full transition-all duration-700"
            style={{ width: `${Math.min((coins / 80) * 100, 100)}%`, background: "linear-gradient(90deg, #7C3AED)" }}
          />
        </div>
        <p className="text-xs text-muted-foreground mt-1.5">
          {coins >= 80 ? "¡Ya puedes canjear el Llavero!" : `Te faltan ${80 - coins} monedas para el Llavero Fuerza Mental`}
        </p>
      </div>
    </div>
  );
}

// ─── Bottom Nav ───────────────────────────────────────────────────────────────
function BottomNav({ active, onNavigate }: { active: Tab; onNavigate: (t: Tab) => void }) {
  const tabs: { id: Tab; icon: React.ReactNode; label: string }[] = [
  { id: "home", icon: <GoHomeFill />, label: "Inicio" },
  { id: "pomodoro", icon: <LuTimer />, label: "Pomodoro" },
  { id: "tasks", icon: <LuClipboardList />, label: "Misiones" },
  { id: "store", icon: <LuShoppingBag />, label: "Tienda" },
  { id: "voice", icon: <LuMic />, label: "Mi Perfil" },
];

  return (
    <nav className="fixed bottom-0 left-0 right-0 bg-white border-t border-border safe-area-inset-bottom z-40"
      style={{ paddingBottom: "env(safe-area-inset-bottom, 8px)" }}
    >
      <div className="flex">
        {tabs.map((t) => (
          <button
            key={t.id}
            onClick={() => onNavigate(t.id)}
            className="flex-1 flex flex-col items-center justify-center py-2 gap-0.5 transition-all"
          >
            <span className={`text-xl transition-transform ${active === t.id ? "scale-125" : "scale-100"}`}>
              {t.icon}
            </span>
            <span
              className="text-xs font-semibold transition-colors"
              style={{ color: active === t.id ? "#7C3AED" : "#9CA3AF" }}
            >
              {t.label}
            </span>
            {active === t.id && (
              <div className="w-1 h-1 rounded-full bg-primary" />
            )}
          </button>
        ))}
      </div>
    </nav>
  );
}

// ─── App Root ─────────────────────────────────────────────────────────────────
export default function App() {
  const [tab, setTab] = useState<Tab>("home");
  const [coins, setCoins] = useState(45);
  const [pomodorosDone, setPomodorosDone] = useState(0);

  const handlePomodoroComplete = () => {
    setCoins((c) => c + 10);
    setPomodorosDone((n) => n + 1);
  };

  return (
    <div className="size-full flex flex-col" style={{ fontFamily: "var(--font-body)" }}>
      {/* App header */}
      <header
        className="flex items-center justify-between px-5 py-3 border-b border-border sticky top-0 z-30"
        style={{ backgroundColor: "var(--background)" }}
      >
        <div className="flex items-center gap-2">
          <div
            className="w-10 h-10 rounded-xl flex items-center justify-center text-white text-sm font-black"
          >
            <img src={logo} alt="Logo" />
          </div>
        </div>
        <div className="flex items-center gap-1.5 bg-amber-100 rounded-full px-2.5 py-1">
          <span className="text-xs">🪙</span>
          <span className="font-black text-amber-700 text-xs" style={{ fontFamily: "var(--font-mono)" }}>{coins}</span>
        </div>
      </header>

      {/* Scrollable content */}
      <main className="flex-1 overflow-y-auto" style={{ paddingBottom: "5rem" }}>
        {tab === "home" && (
          <HomeScreen coins={coins} pomodorosDone={pomodorosDone} onNavigate={setTab} />
        )}
        {tab === "pomodoro" && <PomodoroScreen onComplete={handlePomodoroComplete} />}
        {tab === "tasks" && <TasksScreen coins={coins} setCoins={setCoins} />}
        {tab === "store" && <StoreScreen coins={coins} setCoins={setCoins} />}
        {tab === "voice" && <VoiceScreen />}
      </main>

      <BottomNav active={tab} onNavigate={setTab} />
    </div>
  );
}
