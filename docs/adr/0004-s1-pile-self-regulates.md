# S1 Chicago Piles self-regulate and cannot melt down

ADR 0001's stochastic Partial Meltdown applied to every reactor, S1 included. It now applies to **S2+ only**. A Chicago Pile instead has **Pile Self-Regulation**: Heat Generation falls as Heat Fraction rises, so the pile settles at an equilibrium below its Heat Capacity. It has no Danger zone, Auto-SCRAM, Partial Meltdown or Igniter. S1 is where players learn the heat/thermocouple loop. Losing blocks before they've ever seen a control rod is punishing rather than tense, and danger arrives with S2's control rods and SCRAM tools.

## Consequences

- The S1 column of ADR 0001's cap table is unused, and `igniter_s2` stays the only Igniter in the MVP.
- The equilibrium curve's exact shape is a tuning choice made when the pile is implemented. The commitment is only that it is monotonic (hotter → less heat) and converges below capacity.
- Thermocouple count is the S1 power lever: more thermocouples pull heat out, keep the pile cooler and so keep generation higher.
