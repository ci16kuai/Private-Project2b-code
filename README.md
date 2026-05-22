# SWEN20003 Semester 1, 2026
# Project 2b
# Shadow Aliens

## Running Instructions

Open the project in IntelliJ IDEA as a Maven project. Use Java 25.

Run configuration:
- Main class: `game.ShadowAliens`
- Working directory: project root
- VM options: `-DgameData=gameData.properties`

Alternatively, run the project using the IntelliJ Maven-generated run configuration.

## Assumptions

- The game data file is named `gameData.properties` unless a different path is provided via the `-DgameData` JVM argument.
- Pressing `ESCAPE` switches between the Battle Screen and Pause Screen.
- Pressing `R` resets the game and returns to the Start Screen.
- Pressing `I` toggles developer invincibility mode.
- Pressing `G` increases the game speed and pressing `F` decreases it.
- Pressing `N` skips the current wave (awards wave score, clears all entities, no explosions generated).
- When the player has zero lives remaining, the game transitions to the End Screen (lose state).
- When all waves are completed, the game transitions to the End Screen (win state).
- Collision is checked using rectangular image bounds with no transparency threshold.
- The `LifePowerup` is applied instantly and does not replace any currently active powerup.
- The player's life count is capped at `initialLives`; collecting a life powerup at full health has no effect.
- Hit invincibility (triggered by taking damage) is visually identical to the shield powerup but is independent of it; both can be active simultaneously without conflict.
- The `StrafingEnemy` begins moving toward the nearest screen edge at spawn and bounces off screen boundaries without going off-screen.
- The `ShootingEnemy` fires its first shot at `arrivalTime + firingRate` frames.
- Enemy projectile firing rate is not affected by `timeScale`; the cooldown decrements by 1 each frame regardless of speed level.
- Score cannot go below zero.
- The End Screen player ship can move left and right but cannot shoot.
- The Start Screen player ship can move left and right but cannot shoot.

## AI Statement

I used AI tools (Manus) to assist in planning the broad implementation steps for this project. This included outlining the class hierarchy, and discussing the design of interfaces (`Shootable`, `Moveable`).

I also used AI assistance to help review several methods and classes, including `Wave.java`， the `Enemy` subclasses (`RegularEnemy`, `StrafingEnemy`, `ShootingEnemy`), and `Projectile` subclasses (`PlayerProjectile`, `EnemyProjectile`). I reviewed and adapted all generated code so that it worked correctly within my own project structure.

No code was copied from external sources other than the above.

## Code References

* None

## Design Report

### Extension: Introducing the `Wave` Class

In Project 1, `BattleScreen` directly managed a flat `ArrayList<Enemy>` and read all enemy configuration from a single set of numbered property keys (e.g., `enemy.0.arrivalTime`). There was no concept of waves — all enemies existed in a single pool for the entire game session.

In Project 2, the specification requires sequential, non-overlapping waves, each with their own enemies and powerups. To implement this, a dedicated `Wave` class was introduced. `Wave` encapsulates its own `ArrayList<Enemy>` and `ArrayList<Powerup>`, reads its configuration dynamically using `wave.%d.enemy.%d.*` property keys, and exposes `update()`, `draw()`, `deleteInactiveEnemies()`, and `isCompleted()` methods. `BattleScreen` now holds an `ArrayList<Wave>` and a `currentWaveIndex` integer, advancing the index when `isCompleted()` returns true and `enemyProjectiles` is empty.

The changes required to implement this extension were moderately deep. `BattleScreen.initialiseObjects()` was rewritten to construct `Wave` objects rather than raw enemies. `BattleScreen.update()` was updated to delegate enemy and powerup updates to the current `Wave` instance. `checkCollisions()` was updated to iterate over `currentWave.getEnemies()` and `currentWave.getPowerups()` instead of direct fields. The `Enemy` class was made abstract with an abstract `update(double frameCount, double timeScale)` method, and three concrete subclasses (`RegularEnemy`, `StrafingEnemy`, `ShootingEnemy`) were created to replace the single concrete `Enemy` class from Project 1.

The Project 1 code did not apply the **Open/Closed Principle** to enemies — the single `Enemy` class was not designed to be extended, so adding new enemy types required modifying the existing class rather than extending it. Making `Enemy` abstract in Project 2 resolved this, allowing each subclass to override `update()` independently without touching the parent class.

### Outcome

The introduction of `Wave` and the abstract `Enemy` hierarchy has left the codebase in a significantly more extensible. Adding a new wave now requires only new entries in `gameData.properties` — no Java code changes are needed, because `Wave.loadEnemies()` reads configuration dynamically. Adding a new enemy type requires only a new subclass of `Enemy` that overrides `update()`, plus a new `case` in the `switch` statement inside `Wave.loadEnemies()`.

The `Powerup` class similarly benefits from the **polymorphism** established in Project 2: `Powerup` is abstract and implements `Moveable`, with `apply(Player player)` left abstract for each subclass. This means adding a new powerup type in the future requires only a new subclass and a new `case` in `Wave.loadPowerups()`, with no changes to `BattleScreen` or `Player` needed for the movement and collection logic.

The main area of remaining complexity is `BattleScreen.checkCollisions()`, which has grown to handle multiple collision types (player vs. enemy, player vs. enemy projectile, player projectile vs. enemy, player projectile vs. enemy projectile, player vs. powerup). This method could be further decomposed into smaller private helper methods to improve readability, but the current structure remains functional and correct.
