# Ship a low-capped coal Bootstrap tier instead of relying on the pack for early FE

Every NP machine needs FE, but NP's own first FE source (Thermocouples on a Chicago Pile) only exists after those machines have made fuel. NP therefore ships a free, unlocked **Bootstrap tier**: a Coal Boiler and a Steam Engine, ported in spirit from v1. The Steam Engine's FE/t is deliberately capped well below a Thermocouple's, so coal can power the S1 chain but never outscales nuclear. NP stays playable without a power mod. The cap, not a hard block, is what pushes players to reactors.

## Considered options

- **Pack supplies early FE; NP ships no generator.** Rejected: it leaves NP unplayable standalone or in a pack without early power. Both target packs have FE, so this was close.
- **Keep v1's ~500 FE/t engine.** Rejected: coal outscales the S1 pile and removes the reason to build one.
- **Engine self-disables once a pile exists.** Rejected: gamey, and extra state for little gain.
- **Gate the boiler behind a Stage 1 Shop Entry.** Rejected: chicken-and-egg. Coins need fuel, fuel needs power.

## Consequences

- Steam exists as a fluid from the very start, not only from S2. The S2 Steam Separator / Turbogenerator chain uses the same steam fluid.
- The Steam Engine cap is a balance knob and goes in config alongside the Thermocouple rate. The rule "engine FE/t < thermocouple FE/t" is the design commitment.
