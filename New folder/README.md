# Item Inspect (Fabric 1.21.11)

Press **G** (rebindable under Controls > Item Inspect) to inspect the item in your main hand.
Swords, axes, maces, spears/tridents, pickaxes/shovels/hoes and everything else get their own
twirls, flips, slams and lunges. Each press picks a random move, never the same one twice in a row.
Big moves have swish and thud sounds.

### Punchy animations (1.3.0)

- Moves are about 40% shorter (1.4 to 2.1 s) with a fast snap-up and a tiny overshoot.
- Impacts (axe chop, mace slam, spear stab, catches) arrive at full speed and rebound, with
  squash/stretch pops and a short **hit-stop** freeze for weight.
- Press the key again during the second half of a move to **chain** straight into a new one.
- Cancelling (attack / use) eases back to rest in 2 ticks instead of snapping.

First-person hands are hidden at all times except during an inspection.
Inspection stops if you attack, use an item, open a menu or change the held item.

## Build

Needs JDK 25 to run the build (the mod itself targets Java 21).

    ./gradlew build        (Windows: gradlew.bat build)

The jar is in `build/libs/` (use the one without `-sources`). To test in game: `./gradlew runClient`.

## Config

`config/inspectitems.json` is created on first launch:

- `animationSpeed`: 1.0 normal, 2.0 twice as fast
- `hideHandsOutsideInspect`: set false to get vanilla hands back
- `sounds`: set false to mute the swishes and thuds
- `hitStop`: set false to disable the freeze on big impacts
- `squashStretch`: set false to disable the size pops
- `handOffsetX/Y/Z`, `handFollow`: where the hand sits on the item. Tune these if the hand is off the grip.
- `pivotX/Y`: the point the item spins around. Lower Y moves it toward the grip.

## Add or change animations

Everything is in `anim/Animations.java`. Each animation is a few keyframe tracks
(`Track.smooth(time, value, ...)` for soft keys, or `Track.builder()` with `k`, `launch`, `hit`,
`land` for kicks and impacts; time 0..1). `.impact(time, sfx, pitch, hitStopTicks)` adds a freeze. Item categories are matched by
registry name in `categoryOf`.

## Status

Written against the 1.21.11 Mojang-mapped sources, but not yet compiled or run in game.
If the game crashes at launch with a Mixin error, the method named in the error is the one to fix.
