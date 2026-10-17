# v1 → v2 port inventory

Snapshot taken 2026-10-17.
- **v1:** `Desktop/nuclear powered`, branch `fix/r1-playable` @ `4dd3bc0` plus uncommitted work.
- **v2:** `mvp` @ `fd07cc9` (M3b) plus uncommitted work.

v1 is reference only. Nothing gets copied across wholesale. Each "Port" row is rebuilt in v2 against `CONTEXT.md`. The **Pile** column is a *suggestion* for you to confirm or change:

- **Port**: in v2's S1+S2 MVP scope, and v1 has something worth learning from.
- **Later**: a good idea that's outside the MVP. It goes to the backlog, not the build.
- **Drop**: contradicts a v2 decision, or was scope creep.
- **Done**: v2 already has it.

Legend for v1 status: ✅ working · ◐ partial · ○ stub.

## Ores, materials, fluids

| Feature | v1 | v2 | Pile | Notes |
|---|---|---|---|---|
| Uranium ore (stone + deepslate) | ✅ | ✅ | Done | |
| Uranium ore (nether + end) | ✅ | — | Drop? | Not in CONTEXT. Easy to add later if SkyGreg/ATM need it. |
| Thorium ores ×4 | ◐ (dead-ends at concentrate) | — | Later | Only matters for S4 LFTR/MSR. |
| Ore chain: raw → yellowcake → UO₂ | ✅ (crusher → washer → furnace → ingot) | ✅ (ore crusher → macerator → smelt) | Done | The routes differ. v2's is canonical. |
| v1 metals (bronze, constantan, electrum, dense steel) | ◐ | — | Drop | The earlier decision is to use forge tags / GT materials, not own plates and wires. |
| v1 crafting components (~30: circuit, pump, vat, nozzle…) | ✅ | — | Drop | Same reason: recipes should use tagged items from other mods. |
| Machine frames T1–T3 | ✅ | — | Later? | Decide whether v2 wants a frame tier gate at all. |
| Steam fluid + GTCEu steam bridge | ✅ | — | **Port** | S2 Magnox/RBMK → Steam Separator needs steam. The v1 `compat/gtceu/SteamCompat.java` is worth reading. |
| Nitric acid / extraction solvent fluids | ✅ | — | Drop? | Part of v1's PUREX chain. v2's reprocessing is Cutter → Separator → Converter (see below). |
| CO₂ coolant fluid | — | ? (`NPFluids` exists) | **Port** | Magnox coolant per CONTEXT. Not in v1. |

## Single-block machines

| Feature | v1 | v2 | Pile | Notes |
|---|---|---|---|---|
| Ore processing machines | ✅ Crusher, Ore Washer, Electric Furnace | ✅ Ore Crusher, Macerator | Done | |
| Fuel pellet / rod assembly | ✅ Fuel Fabricator | ✅ Mixer + Fuel Rod Assembly | Done | |
| Enrichment | — | ✅ Gaseous Diffusion | Done | Centrifuge was deliberately removed (S3, per CONTEXT). |
| Spent-fuel reprocessing | ✅ Shearer → Dissolver → Extraction → Cs Column → Vitrifier, Cladding Recycler | ✅ Cutter → Separator → Converter | Done (v2 shape) | v1's 5-stage PUREX is scope creep for MVP. The advanced stages go to Later. |
| Hot Spent Fuel Rod (hurts while held) | ✅ `HotSpentFuelRodItem` | — (only `spent_fuel_rod`) | **Port** | Decided: two items. Hot rods never self-cool; only the pond or pool cools them, and the Cutter takes only cooled rods. See CONTEXT. |
| Thermocouple | ✅ (zone-efficiency curve, coolant toggle) | — | **Port** | CONTEXT redefines it: one-way bond, fixed FE/t, idle on broken bond. Rebuild to spec; don't copy v1's zone curve. |
| Heater (pre-heats pile) | ✅ | — | Drop | Not in CONTEXT. v2 cold start uses an **Igniter**. |
| Coal Boiler + Steam Engine (Bootstrap tier) | ✅ | — | **Port (reworked)** | ADR 0002: free, no unlock, engine FE/t capped below a Thermocouple. Steam fluid needed from the start. |
| Battery | ✅ | — | Drop | Leave storage to other mods. |
| Macerator/Stamping/Slicer/Wiremill/Squeezer | ○ (no recipes) | — | Drop | v1 T2 stubs, made redundant by tag-based recipes. |
| Creative/infinite sources | ✅ | — | Later | Dev convenience only. Add when testing multiblocks. |
| Sided I/O config + auto in/out | ✅ | ? | **Port** | Check whether `AbstractMachineBlockEntity` already covers this. If not, it's a QoL port. |
| Coolant Synthesizer, Cartridge Loader | — | — | **Build** | In CONTEXT, not in either codebase. |

## Multiblocks & reactors (the big gap)

v2 has **no multiblocks yet**. This is where v1 has the most reusable experience.

| Feature | v1 | v2 | Pile | Notes |
|---|---|---|---|---|
| Shared 3×3×3 validator | ✅ `SolidCubeCheck` | — | **Port (idea)** | v2 needs one validator that every multiblock uses. Design it fresh around the Wrench-only rule. |
| Mismatch Marker (chat + through-wall outline) | ✅ (uncommitted: `BlockHighlightRenderer`, `HighlightBlockPacket`) | — | **Port** | Decided: through-wall, shown only to the poker, clears on re-poke or after 30 s. See CONTEXT. |
| Multiblock Wrench validation | ◐ (wrench item exists; v1 auto-validates) | — | **Build** | CONTEXT: Wrench is the *only* trigger, plus instant SCRAM on damage. |
| S1 Chicago / Graphite Pile | ✅ 3×3×3, can't melt down | — | **Port (reworked)** | v2 uses the bathtub model, meltdown per ADR 0001, Hot Spent Fuel Ejector and Igniter. Reuse only the structure scan. |
| S2 Magnox | ✅ 5×5×5, 12 channels, control rods, steam ports, MOX | — | **Port (reworked)** | v2 adds CO₂ closed loop, Fuel Chambers (empty/loaded models), controller upgrades and meltdown. v1's channel pattern and control-rod tab are good references. |
| S2 RBMK | ✅ 15 channels, uranium only | — | **Port (reworked)** | Open loop with boil-off per CONTEXT. |
| Control rods 0–100 % | ✅ UI tab | — | **Port** | Plus Redstone Control Port and Hot Shutdown vs SCRAM (v2 only). |
| Partial meltdown / SCRAM / severity | — | — | **Build** | ADR 0001. Not in v1 at all. |
| Steam Separator | ✅ but a pure buffer | — | **Port (reworked)** | v2 gives it a real job: hot coolant → steam, plus the Conversion Efficiency upgrade. |
| Turbine Generator + Power Tap | ✅ (Magnox/RBMK modes, water return) | — | **Port** | Check CONTEXT for the name (Turbogenerator). The water-return back-pressure is a nice touch to keep. |
| Cooling Loop (water reservoir) | ✅ | — | Drop | Water Tank is a role filled by any mod's tank, not an NP block. See CONTEXT. |
| Cooling Pond | ✅ row of 3+, 60 s | — | **Port (reworked)** | v2: 3×1, 30 s, single-slot queue, back-pressure. |
| Spent Fuel Pool (S2) | — (planned only) | — | **Build** | 3×3×3, 32 rods, 60 s. |
| Fuel Coin Converter | — | — | **Build** | 3×3×3: water + rod → coin. |
| Port blocks (fluid in/out, item out, power) | ✅ flood-fill to controller, colour rings | — | **Port** | v2: one cargo per port, non-top layer only. v1's uncommitted capability-at-construction fix is the lesson to carry over. |

## Progression, integrations, client

| Feature | v1 | v2 | Pile | Notes |
|---|---|---|---|---|
| Stage coins | ◐ (single `fermi_iii_coin`) | ✅ Stage 1/2 coin items | Done | |
| FTB Quests chapters | ✅ tier1 (65) + Fermi Exchange shop (51) | — | **Port (content)** | Rewrite to the v2 Shop Entry / Recipe Unlock model. Many v1 quests are reusable as text. |
| Recipe Unlock via FTBQ (hidden in JEI) | — | — | **Build** | Core v2 mechanic. |
| Shop upgrade items (speed cards, reagent savers…) | ✅ | — | Drop | v2 has exactly three sidegrade Controller Upgrades. |
| Controller Upgrades (Extended Burn / Coolant Eff. / Conversion Eff.) | ◐ (Extended Burn only, for the pile) | ◐ items registered | **Build** | Items are in v2. Behaviour waits on the reactors. |
| Blueprints / deployers | ◐ (blueprint is just an ingredient) | — | Later | |
| GTCEu recipe addon (GT machines run NP recipes) | ✅ `NuclearPoweredGTAddon` | — | Later | Memory note: step 2 is tag-based recipes first, with GT variants as a bonus. |
| JEI categories | ✅ 9 | ✅ plugin | Done | Extend as machines land. |
| LDLib ModularUI | ✅ all GUIs | — (vanilla screens) | Drop | ADR 0003: vanilla screens for MVP, then grow NP's own GUI toolkit. |
| Advancements | ✅ 12 | — | Later | Cheap to re-author once S1 is playable. |
| Config | ○ (one flag) | ✅ `NPConfig` | Done | ADR 0001 lists the meltdown knobs to add. |
| Recipe-builder HTML tool | ✅ (out of game) | — | Later? | Could be handy for authoring v2 recipe JSONs. |

## v1 ideas → backlog (Later), not MVP

From the v1 design docs v3–v5. These are worth parking as issues so they stop living in your head:

- **Enrichment & reactor tiers:** LEU tiers above 2 % and HALEU (S3 centrifuge).
- **Thorium chain:** for S4.
- **Advanced reprocessing:** Sr-90/Tc-99/Am/Np/Cm columns, DIAMEX/SANEX, THORP, Pu-238.
- **Radiation & waste:** radioactive decay to lead, radiation and shielding, RTGs.
- **RBMK depth:** positive void coefficient, online refuelling.
- **Reactor size & tuning:** T2 7×7×5 modifier-zone slots; pile growth to 6×3×3 with air or water cooling; thermal MW layer.
- **Failure modes:** contained-meltdown variants (graphite fire, steam explosion, sodium fire).
- **Progression systems:** research points / laboratory, startup-block commissioning, blueprint deployers.
- **Polish:** custom textures and BlockEntity renderers.

## Decisions taken (2026-10-17)

All five open questions are resolved:

1. **Early-game power.** Port Coal Boiler + Steam Engine as a free, low-capped Bootstrap tier. See [ADR 0002](adr/0002-bootstrap-coal-power.md).
2. **LDLib.** Not adopted. Vanilla screens for the MVP, then NP's own GUI toolkit. See [ADR 0003](adr/0003-vanilla-screens-no-ldlib.md).
3. **Hot spent rod.** Two items: Hot Spent Fuel Rod hurts while held and never self-cools; Spent Fuel Rod is the cooled one. See CONTEXT.
4. **Diagnostic outline.** Mismatch Marker: through-wall, only for the player who poked, clears on re-poke or after 30 s. See CONTEXT.
5. **Cooling Loop vs Water Tank.** Water Tank is a role, not a block. Cooling Loop dropped. See CONTEXT.
