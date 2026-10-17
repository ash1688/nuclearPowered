# Stochastic Partial Meltdown with Mandatory Halt

Reactor failure is modelled as a **stochastic Partial Meltdown** in a `0.75–0.90` heat-fraction danger zone, with a quadratic per-second roll, an auto-SCRAM at `0.90`, mandatory reactor halt on every meltdown (regardless of severity), and a **severity tier** (mild/moderate/severe) that scales a packet of per-block-role damage caps. Caps scale super-proportionally with reactor footprint, and there is a flat 5% chance per meltdown of destroying a port — or, on the worst luck, the controller itself.

The shape exists to make the danger zone feel *tense rather than punishing-or-trivial*: a clean threshold (heat > X → meltdown) makes the game a hard-coded ceiling players park beneath; an auto-SCRAM-only model with no meltdown removes the consequence of recklessness entirely. The stochastic zone preserves "true catastrophe" as a tail outcome while making most failures legible and recoverable.

## Considered options

- **Clean threshold** (meltdown at heat > X, otherwise safe). Rejected: encourages threshold-camping at X−ε, removes risk as a design lever.
- **Auto-SCRAM only, no meltdown** (Model B in the design grilling). Rejected: makes igniters a one-time wall instead of a recurring sink, and removes the consequence of negligence — players will deliberately AFK reactors knowing they cannot lose blocks.
- **Safety logic as a controller upgrade** (Model C). Rejected: hostile trade-off ("+25% steam" vs "your reactor survives the night"), and crowds the existing three-upgrade slot which is meant for efficiency tuning, not survivability.
- **Uniform `0 to cap` distribution** with no severity tier. Rejected: every meltdown averages 50% of cap, which made S1 meltdowns existential and S5 meltdowns trivial relative to total block count. Severity tiers let "mild meltdowns" exist as warning shots.
- **Sub-proportional cap scaling** (bigger reactors safer per event). Rejected: the lore argument (more fuel = bigger drama when it goes) and the gameplay argument (endgame reactors should feel like more is at stake, not less) both point the other way.

## Consequences

- The mod owns the meltdown roll loop, but **no persistent meltdown state** — when a meltdown halts the reactor, runtime state is wiped via the normal SCRAM path. No new save data is introduced.
- The controller is no longer meltdown-immune (a revision from the earlier glossary draft). On a 5% port-or-controller roll landing on the controller, the player must rebuild the controller block — which means re-crafting it, which means the recipe is still unlocked but the materials investment recurs. This is the "true catastrophe" outcome.
- Severity tier copy ("MILD MELTDOWN", "SEVERE MELTDOWN") becomes user-facing language and should be treated as part of the glossary, not as ephemeral GUI strings.
- Tuning knobs are exposed in the config file (Milestone 10): `danger_zone_lower`, `danger_zone_upper`, `max_roll_chance_per_second`, `roll_interval_ticks`, severity tier probabilities and multipliers, the per-stage cap table, and the port-or-controller chance. The shape of the curve (quadratic) is **not** configurable — that's the design commitment recorded here.
