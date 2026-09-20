import assert from "node:assert/strict";
import test from "node:test";
import { mergeSnapshots } from "./merge.js";

test("reconciles devices without duplicating economy events", () => {
  const event = { id: "reward-1", delta: 20 };
  const merged = mergeSnapshots(
    { economyEvents: [event], missions: [], purchasedIds: ["a"], voiceNotes: ["one"] },
    { economyEvents: [event, { id: "purchase-1", delta: -10 }], missions: [], purchasedIds: ["b"], voiceNotes: ["two"] },
  );
  assert.equal(merged.coins, 55);
  assert.deepEqual(new Set(merged.purchasedIds), new Set(["a", "b"]));
  assert.equal(merged.economyEvents.length, 2);
});

test("keeps the highest mission progress", () => {
  const merged = mergeSnapshots(
    { missions: [{ id: "m", progress: 3, updatedAt: 1 }] },
    { missions: [{ id: "m", progress: 2, updatedAt: 2 }] },
  );
  assert.equal(merged.missions[0].progress, 3);
});

test("deduplicates the same logical reward and purchase across devices", () => {
  const sharedEvents = [
    { id: "mission-reward-m1", delta: 25 },
    { id: "purchase-item1", delta: -10 },
  ];
  const merged = mergeSnapshots(
    { missions: [], economyEvents: sharedEvents, purchasedIds: ["item1"] },
    { missions: [], economyEvents: sharedEvents, purchasedIds: ["item1"] },
  );
  assert.equal(merged.coins, 60);
  assert.equal(merged.economyEvents.length, 2);
});

test("adds independent pomodoros completed offline on two devices", () => {
  const merged = mergeSnapshots(
    { missions: [], pomodoroBaseline: 4, economyEvents: [{ id: "pomodoro-a", delta: 10 }] },
    { missions: [], pomodoroBaseline: 4, economyEvents: [{ id: "pomodoro-b", delta: 10 }] },
  );
  assert.equal(merged.pomodorosDone, 6);
  assert.equal(merged.coins, 65);
});