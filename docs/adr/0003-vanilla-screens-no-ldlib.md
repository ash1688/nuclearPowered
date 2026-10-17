# Vanilla screens for the MVP; no LDLib dependency

v1 built every GUI on LDLib ModularUI. v2 deliberately does not. All MVP GUIs, including the reactor controllers (Fuel Chamber slots, control-rod slider, upgrade slot, heat bar, meltdown status, AZ-5), extend v2's own vanilla-screen base classes. LDLib is not guaranteed in every target pack (SkyGreg has it via GTCEu; ATM9 is unconfirmed). A GUI library is also the kind of lock-in that touches every screen. After the MVP, the plan is to grow NP's own small GUI toolkit from those base classes rather than adopt LDLib.

## Consequences

- v1's `NPMachineUI` / `NPTabs` are idea references only. No code is ported from them.
- Reactor controller GUIs cost more hand-written widget code during the MVP. Accepted.
