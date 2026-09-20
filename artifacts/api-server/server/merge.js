export function mergeSnapshots(current = {}, incoming = {}) {
  const legacyMissionIds = new Set(["t1", "t2", "t3", "t4", "t5"]);
  const deletedMissionIds = [...new Set([
    ...(current.deletedMissionIds ?? []),
    ...(incoming.deletedMissionIds ?? []),
    ...legacyMissionIds,
  ])];
  const byId = new Map();
  for (const mission of [...(current.missions ?? []), ...(incoming.missions ?? [])]) {
    if (!mission?.id || deletedMissionIds.includes(mission.id) || legacyMissionIds.has(mission.id)) continue;
    const previous = byId.get(mission.id);
    if (!previous || mission.progress > previous.progress ||
        (mission.progress === previous.progress && mission.updatedAt > previous.updatedAt)) {
      byId.set(mission.id, mission);
    }
  }
  const deletedChallengeIds = [...new Set([
    ...(current.deletedChallengeIds ?? []),
    ...(incoming.deletedChallengeIds ?? []),
  ])];
  const challengesById = new Map();
  for (const challenge of [...(current.challenges ?? []), ...(incoming.challenges ?? [])]) {
    if (!challenge?.id || deletedChallengeIds.includes(challenge.id)) continue;
    const previous = challengesById.get(challenge.id);
    if (!previous || (challenge.updatedAt ?? 0) > (previous.updatedAt ?? 0)) challengesById.set(challenge.id, challenge);
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
    deletedMissionIds,
    challenges: [...challengesById.values()].sort((a, b) => (b.createdAt ?? 0) - (a.createdAt ?? 0)),
    deletedChallengeIds,
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