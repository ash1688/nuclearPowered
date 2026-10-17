# Nuclear Powered

Glossary for the Nuclear Powered Minecraft Forge mod — a progression-based nuclear energy mod for Minecraft 1.20.1 (Forge). Players progress through five stages of historically-grounded reactor technology, gated by an in-game currency and a quest-driven recipe-unlock system.

## Language

### Currency & progression

**Stage Coin**:
An in-world inventory **item** (one variant per stage: `Stage 1 Coin` … `Stage 5 Coin`). The mod's progression currency. Earned from quest completion and the Fuel Coin Converter; consumed by Shop Entries to grant Recipe Unlocks. A coin's stage is fixed at creation and never substitutes for another stage.
_Avoid_: token, credit, balance (a coin is an item, not a number on a team).

**Coin Balance**:
The total count of Stage Coin items a player (or their team's shared storage) is currently carrying. Lives in **inventory**, not in mod or FTBQ state. The mod owns no ledger of player wealth.
_Avoid_: wallet, account balance.

**Recipe Unlock**:
A boolean per recipe-id × team, stored in **FTB Quests team/player data**, that toggles the recipe from "hidden in JEI/NEI" to "visible and craftable". The mod owns no unlock map of its own — it reads FTBQ on demand.
_Avoid_: tech unlock, research (no research mechanic exists).

**Shop Entry**:
An FTB Quests quest (in a stage's Shop Chapter) whose completion cost is N Stage Coins and whose reward fires a hook that flips one Recipe Unlock. The "coin shop" is the union of all Shop Entries across all Shop Chapters — it is not a placed block.
_Avoid_: shop item, shop slot.

**Pricing rule** (for reactor-type Shop Entries):
The **first** reactor type of stage N+1 is priced in **Stage N** coins — paid by the final-quest reward of Chapter N, which grants exactly that many coins. All **remaining** reactor types of stage N+1 are priced in **Stage N+1** coins — earned during Chapter N+1 via quest rewards and the Fuel Coin Converter (which now accepts stage-N+1 fuel). This is the *only* place lower-stage coins are accepted by a higher stage's Shop Chapter; everywhere else the stage of coin in equals the stage of the entry.

**Stage 5 gate**:
The condition under which the SMR controller's recipe becomes craftable. Two requirements, both must be met:
1. **Recipe Unlock chain** — all 10 S1–S4 reactor type Recipe Unlocks have been purchased via Shop Entries (the normal progression path).
2. **Inventory check** — all 10 reactor type controller items are present across the team's personal inventories (main + hotbar + offhand) at the moment of SMR-recipe-unlock attempt. Solo player: the check is against their own inventory only. Multi-player team: any team member's inventory contributes — one player may hold the Magnox controller, another the Fast Breeder, etc. Team-shared block storage (ender chests, AE2, shared barrels) does **not** count — controllers must be in a player's bag.

The "built one of each reactor as you go" requirement is enforced separately, via FTBQ quest dependencies (each chapter contains a "Build a {ReactorType}" quest that requires multiblock formation), not via a runtime flag in mod code.

**Fuel Coin Converter**:
A 3×3×3 multiblock that consumes water + a Fuel Rod of stage N and outputs Stage N Coin items. Exists to fill the deliberate gap between quest coin rewards and total shop cost. Coin stage is determined entirely by fuel stage — there is no GUI toggle and no cross-stage substitution.
_Avoid_: coin press, mint.

### Bootstrap power

**Bootstrap tier**:
The pre-S1 tier: blocks that are craftable from the start with no Shop Entry or coin. It exists only to power the S1 machine chain before the first Chicago Pile runs. It is deliberately worse per block than Thermocouples, so nuclear is the upgrade. Coal-only play is slow but legal.
_Avoid_: Stage 0, S0 (the mod's stages are 1–5; Bootstrap is not a stage and has no coin).

**Coal Boiler**:
A Bootstrap-tier single-block machine that burns solid fuel to turn water into steam.
_Avoid_: boiler (ambiguous with reactor steam production).

**Steam Engine**:
A Bootstrap-tier single-block generator that turns steam into FE at a low, capped rate per engine. Not the same as the S2 **Turbogenerator**, which is the multiblock that turns reactor steam into FE.
_Avoid_: turbine, generator.

### Reactor simulation (the "bathtub" model)

The reactor is simulated as a **scalar heat bathtub**. Block positions inside the multiblock do not matter to the heat math — only block *counts* and *types*. Each tick the controller computes one delta and updates one heat number. The model has three quantities and two player-controlled rates:

**Heat Capacity**:
The reactor's heat reservoir size — the total amount of heat it can hold before reaching the danger zone. Computed as `sum(casing_heat_cap) + sum(moderator_heat_cap) + sum(upgrade_block_heat_cap)`. Each block type has a fixed heat-capacity contribution defined in config. The bathtub's *volume*.

**Heat Generation**:
The rate at which heat enters the bathtub each tick. Computed as `fuel_burn_rate × (1 − rod_insertion_pct) × enrichment_factor × active_fuel_rod_count`. The faucet.

**Heat Dissipation**:
The rate at which heat leaves the bathtub each tick. Two formulas — S1 piles and S2+ reactors compute it differently:
- **S1 (Chicago Pile)** — `thermocouple_count × thermocouple_extraction_rate`. The heat that thermocouples extract is converted directly to FE/t. No coolant fluid. No flow rate.
- **S2+** — `coolant_flow_rate × coolant_specific_heat × moderator_block_efficiency_bonus`. Heat extracted from the bathtub is transported by the coolant fluid out of the reactor's output ports, where it becomes either steam (water-cooled) or heated gas (gas-cooled) feeding the Steam Separator. The drain.

**Heat Fraction**:
Already defined above — `current_heat / Heat Capacity`. Drives Safe/Danger/Auto-SCRAM band logic.

**Coolant**:
The fluid that flows through an S2+ reactor extracting heat: water (BWR, RBMK), CO₂ gas (Magnox, AGR), heavy water (CANDU), molten salt (MSR, LFTR), liquid sodium (Fast Breeder). Distinct from **Moderator** (a solid block). Sourcing and replenishment of S3+ coolants is deferred to post-MVP design.
_Avoid_: moderator fluid (use "coolant"), reactor fluid.

**Moderator block**:
A solid block (graphite, etc.) placed in the prescribed internal pattern of an S2+ reactor. Contributes a fixed amount to **Heat Capacity** and a multiplier to coolant-side **Heat Dissipation**. Solid, not fluid.
_Avoid_: moderator fluid, moderator material (use "moderator block" unambiguously).

**Coolant Flow Rate**:
The player-controlled rate at which coolant moves through the reactor (units: mB/tick). Adjusted via the controller GUI or a Redstone Control Port. Higher flow = more heat extracted per tick = more steam produced = more water consumed. Capped per stage by the **Coolant Flow Cap**.

**Coolant Flow Cap**:
The per-stage maximum **Coolant Flow Rate**. S1 has no flow rate (thermocouples only). S2 onwards each have a hard cap that scales with stage. The cap is the primary reason higher-stage reactors can sustain higher fuel-rod counts and higher enrichment levels — without the cap raise, the bathtub overflows.

**Controller Upgrade**:
A single-slot upgrade item installed in the reactor controller GUI. Exactly one is active at a time. Three variants exist, each targeting a different reactor bottleneck — they are **sidegrades**, not power-ups. Player chooses based on which bottleneck their reactor is hitting.

- **Extended Burn** — fuel-supply bottleneck. Multiplies `fuel_base_burn_rate` by `(1 − e)` where `e` is the upgrade strength (default 0.20, i.e. fuel duration +25%). Total Fuel Burn Budget unchanged. Per-tick heat output unchanged.
- **Coolant Efficiency** (formerly "Reduced Water Consumption") — coolant-throughput bottleneck. Multiplies coolant flow needed for the same Heat Dissipation by `(1 − e)` (default 0.25, i.e. ~33% more dissipation per mB). Generalizes correctly to gas-cooled reactors — applies to CO₂ in Magnox/AGR as well as water elsewhere.
- **Conversion Efficiency** (formerly "Increased Steam Output") — output-throughput bottleneck. Multiplies the Steam Separator's steam-output-per-coolant-input by `(1 + e)` (default 0.25). Generalizes correctly to gas-cooled reactors — applies to the heated-gas → steam conversion path at the Steam Separator.

All three upgrade strengths are independently configurable, defaults 0.25 / 0.25 / 0.25.

**Fuel Burn Budget**:
The total amount of heat one fuel rod releases over its entire lifetime, fixed at fuel-rod creation time. Burn rate is **flat** over the rod's life (no warm-up curve, no decay curve) and **proportional to `(1 − rod_insertion_pct)`** — control rods scale heat output and fuel consumption by the same factor:

```
heat_per_tick      = fuel_base_heat_rate × (1 − rod_insertion_pct) × enrichment × active_rod_count
fuel_burnt_per_tick = fuel_base_burn_rate × (1 − rod_insertion_pct) × active_rod_count
```

Lifetime heat budget is invariant: a rod at 90% insertion produces 10% heat for 10× wall-clock and burns the same total budget. Hot Shutdown (`rod_insertion_pct = 1.0`) freezes fuel depletion entirely.

The Controller Upgrade **Extended Burn** stretches `fuel_burn_duration` by 25% without changing `burn_budget` — multiplies `fuel_base_burn_rate` by 0.8 (rod burns slower at the same rod %), leaving `fuel_base_heat_rate` unchanged.

### Multiblock validation

**Multiblock Wrench**:
A handheld tool item the player uses to validate a multiblock structure. Right-clicking the controller block with the Wrench triggers a single validation scan. The Wrench is the *only* way validation runs — the controller does not auto-validate on placement or on neighbor change.

**Multiblock validation**:
The process triggered by Wrench-poke on the controller block. The controller scans the expected pattern in its reference frame (based on the face the controller occupies):
- **If the pattern matches** — the structure transitions to *valid*. The controller's `multiblock_valid` flag is set to true. Loaded textures appear on Fuel Chambers, the GUI becomes accessible, and the reactor can be started.
- **If the pattern is wrong** — the controller fails validation and reports the *first* mismatch as a player-visible chat message: `"Multiblock invalid: expected <BlockRole> at (relative x,y,z), found <ActualBlock>"`. A **Mismatch Marker** highlights the offending block position in-world.

**Mismatch Marker**:
A red outline around the first wrong block found by a failed validation. It renders through other blocks, because S2+ mismatches are often in a sealed interior. It is shown only to the player who poked, and clears on their next Wrench-poke or after 30 seconds, whichever comes first.
_Avoid_: highlight, error particles.

A formed reactor's validity is **continuously enforced during operation**. Any external event that breaks structure validity (creeper damage, manual block break, Partial Meltdown side-effect) causes an **instant SCRAM**: reactor halts, runtime state clears, igniter required to restart, GUI displays `"Multiblock invalidated by structural damage"`. Restoring the missing block and re-Wrench-poking re-validates the structure.

### Reactor ports

Reactor ports are blocks that **replace a casing on a non-top layer** and expose a single capability to external mods. Each cargo type has its own port block — there is no multi-cargo port. Each reactor has a prescribed count of each port type in its §3.7 spec.

**Fluid Input Port**:
A block that accepts coolant from external pipes/conduits and feeds it into the reactor's coolant loop. Exposes `IFluidHandler` on all 6 faces. The fluid type the port accepts depends on the reactor's prescribed coolant (water, CO₂, heavy water, molten salt, sodium); mismatched fluids are rejected.

**Fluid Output Port**:
A block that emits the reactor's "hot side" fluid output — steam (water-cooled), heated gas (gas-cooled), or hot coolant for closed-loop primaries (S3+). Exposes `IFluidHandler` on all 6 faces.

**Item Output Port**:
A block that emits Hot Spent Fuel Rod items as they're ejected from the reactor's Fuel Chambers. Exposes `IItemHandler` on all 6 faces. External item conduits route the spent rods downstream (typically to the Spent Fuel Pool for S2+, or to the Cooling Pond for S1 — though S1 uses a specialized variant of this port called the **Hot Spent Fuel Ejector**, which differs only in texture/name/recipe).

**Hot Spent Fuel Ejector**:
The S1-specific variant of the Item Output Port. Functionally identical (`IItemHandler` on 6 faces, ejects Hot Spent Fuel Rod items) but visually distinct (glowing texture indicates the spent rods are still hot, en route to the Cooling Pond). Kept as a separate name for design continuity with v1.1 of the doc and for the lore feel of S1.

Note: an **Item Input Port** for automating fresh fuel insertion is **not in MVP scope** (§5.6 — fuel loaded via controller GUI). Post-MVP, an Item Input Port can be added that feeds Fuel Rod items into the controller's input buffer, with the existing chamber-allocation logic distributing rods to empty chambers.

### Fuel handling

**Fuel Rod (item)**:
The consumable crafted item the player produces via the Fuel Rod Assembly Machine (pellet + cladding). Comes in stage variants (Natural Uranium Fuel Rod, LEU Fuel Rod, MOX Fuel Rod, …). Carries a Fuel Burn Budget. Becomes a **Hot Spent Fuel Rod (item)** when its budget is exhausted.

**Hot Spent Fuel Rod (item)**:
A fuel rod straight out of a reactor (any stage), before cooling. It damages and ignites the player while it is held in the selected hotbar slot. It never cools on its own: only a **Cooling Pond** (S1) or **Spent Fuel Pool** (S2+) turns it into a **Spent Fuel Rod**.
_Avoid_: hot rod, irradiated rod.

**Spent Fuel Rod (item)**:
A cooled spent rod, safe to handle. The input to the reprocessing chain. Only obtainable by cooling a Hot Spent Fuel Rod.
_Avoid_: depleted rod (v1 term).

**Fuel Chamber (block)**:
A block placed in the prescribed multiblock pattern of an S2+ reactor (and inside the Chicago Pile's middle layer for S1) that **holds at most one Fuel Rod item**. The chamber is the physical "slot" in the structure; the rod is the consumable that goes in it. The multiblock validates with all required Fuel Chambers placed, *empty or loaded*. Chambers have distinct rendered models for empty vs loaded states — at-a-glance visual feedback.

A Fuel Rod item is loaded into a Fuel Chamber via the controller GUI: the GUI exposes N inventory slots (one per Fuel Chamber in the structure) and the player drops Fuel Rod items into those slots. The chamber's render updates to "loaded" and the rod begins burning when the reactor is started.

Only **loaded** chambers contribute to Heat Generation — empty chambers count for multiblock validation but contribute nothing. So `active_fuel_rod_count` in the Heat Generation formula = number of Fuel Chambers currently holding a non-spent Fuel Rod item.

Spent fuel auto-ejects: when a rod's Fuel Burn Budget reaches zero, the Fuel Rod item becomes a Hot Spent Fuel Rod item and is routed out of the reactor (via the Hot Spent Fuel Ejector for S1, via output ports for S2+). The chamber becomes empty and ready for the next rod.
_Avoid_: Fuel Rod Slot, fuel slot, rod socket.

**Thermocouple**:
An **external single-block machine** placed adjacent to a Chicago Pile multiblock (S1 only). Not part of the multiblock structure — bonds to the pile at placement time, and exists alongside it. Mechanics:
- **Bonding** — on placement, scans the 6 cardinal neighbours for a Chicago Pile casing block belonging to a valid pile. Exactly one neighbour must match: zero matches or ambiguous (two adjacent piles) → placement fails, item returns to the player.
- **One-way bond** — the thermocouple stores its bonded pile's controller coordinates; the pile stores no list of thermocouples. Each tick, each thermocouple pulls a fixed amount of heat from its bonded pile's heat reservoir and converts it to a fixed FE/tick into its buffer.
- **FE output** — the thermocouple block itself exposes Forge Energy on all 6 faces. External cables and conduits connect directly to thermocouples, never to the pile.
- **Idle on broken bond** — if the bonded pile is invalidated (controller destroyed, multiblock dismantled), the thermocouple goes idle (0 FE/tick) but remains placed; it re-activates if the pile reforms at the same controller location.

S1 total FE output is therefore `thermocouple_count × thermocouple_fe_per_tick`. There is no FE port on the pile itself.

### Reactor failure & control

**Heat fraction**:
The reactor's current heat as a fraction of its meltdown threshold (`current_heat / meltdown_threshold`). The single value the player monitors and that drives all failure logic. Expressed in the GUI as a percentage.

**Safe zone / Danger zone / Auto-SCRAM**:
Three heat-fraction bands.
- **Safe zone** = `heat_fraction < 0.75`. No meltdown risk.
- **Danger zone** = `0.75 ≤ heat_fraction < 0.90`. Each interval, a stochastic roll may trigger a Partial Meltdown; per-roll probability scales upward through the band.
- **Auto-SCRAM threshold** = `heat_fraction ≥ 0.90`. Reactor automatically SCRAMs.

**SCRAM** (manual or auto):
A full emergency shutdown: control rods slammed to 100% insertion, heat generation halts, the reactor's "started" flag is cleared. A subsequent restart of the reactor (cold-start) consumes one stage-appropriate Igniter. Manual SCRAM is player-triggered (AZ-5 button in GUI or redstone); auto-SCRAM fires when the reactor hits the Auto-SCRAM threshold.
_Avoid_: shutdown (ambiguous with Hot Shutdown), emergency stop.

**Hot Shutdown**:
Control rods inserted to 100%, but the reactor remains "started" — pulling rods back resumes power without an igniter. Distinct from SCRAM. S2+ only; S1 piles have no shutdown states.
_Avoid_: idle, parked.

**Partial Meltdown**:
The failure event triggered by a Danger-zone roll, evaluated once per second while the reactor is in the danger zone. A Partial Meltdown **always halts the reactor** — the multiblock's runtime state is reset, the "started" flag clears, all unburnt fuel rods inside are destroyed (not dropped), and fuel queued in the controller's input buffer is destroyed. Restart requires repair plus a stage-appropriate Igniter (treated identically to a cold start after SCRAM). "Partial" refers to the block damage being a *random subset* of the multiblock, not to operational survivability. No environmental damage of any kind.

**Severity tier** (rolled once per meltdown event, for legibility only):
- **Mild** — 60% probability, caps × 0.2. Player sees "MILD MELTDOWN" in the GUI.
- **Moderate** — 30% probability, caps × 0.5. Player sees "MODERATE MELTDOWN".
- **Severe** — 10% probability, caps × 1.0. Player sees "SEVERE MELTDOWN".

Severity does not change operational outcome (reactor halts in all cases) — it only changes repair scope. Tier probabilities and multipliers are configurable.

Block-role damage pool (each role rolls its own `0 to cap × severity_multiplier`):
- **Casings**, **Fuel rods**, **Moderators** — the bulk-loss pool.
- **Control rods** — a small-loss pool. Caps are low even at large sizes.
- **Ports** and **Controller** — a single combined tail-risk slot per event. At most one of them is destroyed per meltdown, and only on a 5% probability roll. Destroying the controller is the maximally-painful outcome (full rebuild, including the controller itself).

Default starting caps (configurable; expressed at *severe* tier, i.e. ×1.0):

| Block role | 3×3×3 (S1) | 5×5×5 (S2) | 7×7×7 (S3) | 9×9×7 (S4) | 13×13×9+ (S5) |
| --- | --- | --- | --- | --- | --- |
| Casings | 0–5 | 0–9 | 0–14 | 0–20 | 0–32 |
| Fuel rods | 0–5 | 0–8 | 0–12 | 0–17 | 0–27 |
| Moderators | 0–4 | 0–7 | 0–11 | 0–15 | 0–24 |
| Control rods | 0–2 | 0–3 | 0–5 | 0–6 | 0–9 |
| Port-or-controller slot | 5% chance | 5% chance | 5% chance | 5% chance | 5% chance |

Cap scaling is **super-proportional** with reactor footprint — bigger reactors burn faster, produce more steam, and bring more drama when they fail. S5 caps are ~5× S1 caps despite the block count being ~20× larger; the meltdown still feels bigger absolutely while leaving more of the reactor intact relatively (which gives Stage 5's SMR a slightly different feel — bigger crater, larger surviving shell).

_Avoid_: meltdown (ambiguous — every meltdown in this design is a Partial Meltdown; severity comes from the tier roll, not from a binary partial/full distinction).

**Igniter**:
A stage-specific consumable item, crafted from that stage's byproduct (Cs for S2, Tritium for S3, …). Consumed on every cold start: first start of a freshly-built reactor, and every restart after SCRAM (manual or auto). Not consumed when resuming from Hot Shutdown.

**Redstone Control Port**:
A port block (replaces a casing slot on any non-top layer) that exposes the reactor to redstone wire. Two inputs are read per side:
- **Analog strength (0–15)** → rod insertion % (signal strength × ~6.67 = rod %).
- **Rising-edge pulse** on a specific side → SCRAM trigger.
Multiple Redstone Control Ports on one reactor are additive — the highest analog strength among them wins; any pulse triggers SCRAM.

### Spent fuel handling

**Cooling Pond** (S1):
A 3×1 multiblock structure that cools one Hot Spent Fuel Rod item at a time into a Spent Fuel Rod. Cooldown: **30 seconds** per rod. Receives rods from the reactor's Hot Spent Fuel Ejector. Acts as a single-slot queue — when a rod finishes cooling and is extracted (via the pond's top-face Item Output), the next queued rod begins cooling. If no extraction conduit is attached, additional spent rods cannot be received (back-pressure to the reactor — see overflow behaviour).

**Spent Fuel Pool** (S2+):
A 3×3×3 multiblock structure that cools **up to 32 Hot Spent Fuel Rod items in parallel** into Spent Fuel Rods. Cooldown: **60 seconds** per rod, flat across S2–S5. Receives rods from the reactor's Item Output Ports. Emits cooled rods on the **top face** as an Item Output Port (`IItemHandler`) — players run item conduits from the top face to the reprocessing chain.

**Cooling-pool overflow behaviour**:
A reactor **refuses to start** if its associated Cooling Pond / Spent Fuel Pool is full at startup time. The controller GUI surfaces the message "Spent Fuel Pool full, cannot start" before the player hits the start button. Pool-full is treated as a *configuration error*, not a *runtime failure* — there is no mid-run drop-rods-on-the-ground or surprise auto-SCRAM caused by a full pool. The reactor's auto-eject can always push to the pool while the pool has space; if the pool fills mid-cycle, the reactor's auto-eject buffer holds the surplus until either pool space frees up or the reactor is shut down. The pool's bounded capacity exists to motivate the player to plumb extraction conduits, not to randomly punish them.

### Coolant sourcing

Closed-loop reactors require a one-time coolant fill at commissioning, replenished only after a meltdown drains the loop. Sourcing happens through three blocks that compose along the fluid system — no special "insert cartridge into reactor" interaction exists, consistent with §1.2's gases-as-fluids principle.

**Coolant Synthesizer** (single-block machine, S2):
A machine that converts raw inputs (e.g. coal/charcoal + water for CO₂; heavy water cells + power for D₂O; …) plus FE into a **filled Coolant Cartridge** item. Each coolant type has its own recipe in the synthesizer. Outputs the filled cartridge into its output slot. Empty Cartridges are inserted into a dedicated input slot. Generalized for S3+ coolants from day one — adding heavy water, molten salt, sodium recipes later is config-only work.
_Avoid_: Gas Synthesizer (too narrow — sodium and salt aren't gases).

**Coolant Cartridge** (item, S2+):
A portable fluid container item, much like a fluid bucket but for coolants. Comes in two states:
- **Empty Cartridge** — reusable container, crafted in a vanilla crafting table from S2-tier materials (iron + glass + rubber). 
- **Filled Cartridge** — variant per coolant type (Filled CO₂ Cartridge, Filled D₂O Cartridge, etc.). Stores a fixed coolant volume (default: 4000 mB, configurable). Drained cartridges become Empty Cartridges, reusable indefinitely.

**Cartridge Loader** (single-block machine, S2):
A machine that bridges item → fluid. Insert a Filled Cartridge into its input slot; the Loader exposes `IFluidHandler` on its faces, dispensing the cartridge's contents at a configurable rate (default: 100 mB/tick). When fully drained, the cartridge becomes an Empty Cartridge and is ejected to the Loader's output slot. The player pipes the Loader's fluid output to the reactor's Fluid Input Port using any mod's fluid pipes. Loader can be detached after the reactor's loop is filled.

The end-to-end fill workflow:
1. Build Coolant Synthesizer; craft Empty Cartridges; produce a Filled CO₂ Cartridge in the Synthesizer.
2. Build a Cartridge Loader near (or on a portable pipe run to) the Magnox.
3. Insert the Filled Cartridge into the Loader.
4. Loader pipes CO₂ to the Magnox's Fluid Input Port; reactor's closed loop fills.
5. Disconnect Loader once full — reactor runs steady-state with no coolant draw until a meltdown.

A reactor with an empty/insufficient closed loop **refuses to start** and surfaces `"Coolant loop empty — fill via Fluid Input Port"` in the GUI.

### Plumbing

**Water Tank**:
A *role* in the S2+ secondary loop, not an NP block. It is the optional water buffer between the Turbogenerator's exhaust and the Steam Separator's water input, filled by any mod's fluid storage. The loop works without one if the exhaust is piped straight back.
_Avoid_: Cooling Loop (the v1 reservoir multiblock, which v2 does not have), reservoir.

**Open-loop boil-off**:
The continuous water loss applied to **open-loop** reactors only (BWR, RBMK). Rate: `boil_off_mb_per_sec_per_rod × active_fuel_rod_count`, applied at the reactor's Fluid Input Port boundary (i.e. the reactor draws that much more water from its input than the steam output accounts for). Default rate: **5 mB/sec/rod**, configurable. Open-loop reactors at full load draw their boil-off topup continuously — a 9-chamber RBMK = ~45 mB/sec = ~2.7 buckets/minute, comfortable for a single mod-pumped water source.

Closed-loop reactors (Magnox, PWR, AGR, CANDU, MSR, LFTR, Fast Breeder) experience **no boil-off** — the primary loop is sealed by definition; only the secondary loop circulates water, and the secondary loop is a closed circuit through the Steam Separator → Turbogenerator → Water Tank with no atmospheric loss. The S1 Chicago Pile has no water loop at all (thermocouple-cooled) and boil-off does not apply.

The boil-off rule exists to make water a *meaningful but non-onerous* resource sink in S2 — annoying enough that the player plumbs a pump, not so harsh that they must do it on day one.

### Enrichment chain

**Gaseous Diffusion Chamber**:
The S2 enrichment machine. Consumes UF4 (item) + FE, outputs UF6 (fluid). Slow but feasible for LEU (≤2%) production at S2. The *only* enrichment machine in S2 — the doc's earlier "OR Centrifuge" fork is closed.

**Centrifuge**:
The S3 enrichment machine, replacing the Gaseous Diffusion Chamber in the fresh-fuel and reprocessing chains. Same I/O (UF4 → UF6) but faster and lower FE cost. Unlocked via an S3 Shop Entry. Historically aligned with the S3 reactor era (BWR/PWR/CANDU/AGR were the 1960s-onward reactors; centrifuge enrichment dominated from the same period). **Not in MVP scope** (MVP is S1+S2 only).

## Flagged ambiguities

**"FTBQ stores coin balances"** (appears in §2.3 and §8.4 of the design doc):
**Resolution** — FTBQ stores **Recipe Unlocks**, NOT Coin Balance. Coin Balance is just inventory. The doc wording is imprecise and should be sharpened in v1.2: "FTB Quests handles persistence of recipe unlock state. Coin items are stored in player inventory like any other item."

## Example dialogue

> **Dev**: When the player completes the "Build Your First Magnox" quest, what changes in the world?
> **Owner**: They get a Stage 2 Coin item dropped into their inventory as the quest reward, and the next quest in that chapter becomes available.
> **Dev**: And when they spend that coin in the shop?
> **Owner**: They go to the S2 Shop Chapter in the quest book, click the entry for, say, "Unlock Magnox Recipe", and FTBQ consumes one Stage 2 Coin from their inventory and fires our hook. We flip the Recipe Unlock for the Magnox controller recipe in FTBQ's team data. Next time they open JEI, the recipe shows up.
> **Dev**: What if a teammate already unlocked it?
> **Owner**: Recipe Unlock is per-team in FTBQ, so it's already visible for the whole team. Coin Balance is per-player inventory though — coins don't pool automatically.
