export function mergeSnapshots(current = {}, incoming = {}) {
  const byId = new Map();
  for (const mission of [...(current.missions ?? []), ...(incoming.missions ?? [])]) {
    const previous = byId.get(mission.id);
    if (!previous || mission.progress > previous.progress ||
        (mission.progress === previous.progress && mission.updatedAt > previous.updatedAt)) {
      byId.set(mission.id, mission);
    }
  }

  const economyEvents = uniqueById([
    ...(current.economyEvents ?? []),
    ...(incoming.economyEvents ?? []),
  ]);
  const coins = 45 + economyEvents.reduce((total, event) => total + Number(event.delta || 0), 0);
  const baselineOf = (snapshot) => snapshot.pomodoroBaseline ??
    Math.max(0, (snapshot.pomodorosDone ?? 0) -
      (snapshot.economyEvents ?? []).filter((event) => event.id?.startsWith("pomodoro-")).length);
  const pomodoroBaseline = Math.max(baselineOf(current), baselineOf(incoming));

  return {
    coins,
    pomodorosDone: pomodoroBaseline + economyEvents.filter((event) => event.id.startsWith("pomodoro-")).length,
    pomodoroBaseline,
    missions: [...byId.values()].sort((a, b) => (b.createdAt ?? 0) - (a.createdAt ?? 0)),
    purchasedIds: [...new Set([...(current.purchasedIds ?? []), ...(incoming.purchasedIds ?? [])])],
    streakDays: Math.max(current.streakDays ?? 0, incoming.streakDays ?? 0),
    lastActiveDate: [current.lastActiveDate ?? "", incoming.lastActiveDate ?? ""].sort().at(-1),
    voiceNotes: [...new Set([...(incoming.voiceNotes ?? []), ...(current.voiceNotes ?? [])])].slice(0, 20),
    economyEvents,
  };
}

function uniqueById(values) {
  return [...new Map(values.filter((value) => value?.id).map((value) => [value.id, value])).values()];
}