# Top Down Survival Game

A Java Swing top-down survival game where the player fights waves of enemies, collects power-ups, and tries to survive as long as possible.

## Overview

This project is a 2D arcade-style survival game built with Java. The player moves around the screen, shoots enemies, collects power-ups, and advances through increasingly difficult waves.

The game uses a simple object-oriented structure with separate classes for the player, enemies, bullets, power-ups, and the game panel.

## Features

- Main menu screen
- Player movement using `W`, `A`, `S`, and `D`
- Shooting with the arrow keys
- Wave-based enemy spawning
- Multiple enemy types
  - Normal enemy
  - Fast enemy
  - Tank enemy
- Enemy health scaling by wave
- Enemy health bars
- Player health system
- Sprint/stamina system
- Dash mechanic
- Power-ups
  - Health power-up
  - Rapid-fire power-up
- Bullet collision with enemies
- Enemy collision damage to player
- Score system
- Game-over screen
- Restart option
- Sprite-based graphics
- Background grid

## Controls

| Key | Action |
|---|---|
| `ENTER` | Start the game from the menu |
| `W` | Move up |
| `A` | Move left |
| `S` | Move down |
| `D` | Move right |
| `SHIFT` | Sprint |
| `SPACE` | Dash |
| `UP ARROW` | Shoot up |
| `DOWN ARROW` | Shoot down |
| `LEFT ARROW` | Shoot left |
| `RIGHT ARROW` | Shoot right |
| `R` | Restart after game over |
| `P` | Pause / resume, if pause has been added |

## Project Structure

```text
src/
├── TopDownGame.java
├── GamePanel.java
├── Player.java
├── Enemy.java
├── Bullet.java
├── PowerUp.java
├── FloatingText.java
└── assets/
    ├── sprite_hero.png
    ├── sprite_enemy_normal.png
    ├── sprite_enemy_light.png
    ├── sprite_enemy_heavy.png
    ├── sprite_bullet.png
    ├── sprite_health_powerup.png
    └── sprite_rapid_fire.png
```

## Class Descriptions

### `TopDownGame.java`

Starts the application, creates the main `JFrame`, adds the `GamePanel`, and starts the game loop.

### `GamePanel.java`

Handles the main game loop, game states, input, drawing, enemy spawning, collision checks, score tracking, power-up drops, and wave progression.

### `Player.java`

Represents the player. Handles movement, health, stamina, sprinting, dashing, staying on screen, and drawing the player sprite.

### `Enemy.java`

Represents enemies. Handles enemy movement toward the player, health, damage, enemy types, health bars, and drawing enemy sprites.

### `Bullet.java`

Represents bullets fired by the player. Handles bullet movement, enemy collision checks, off-screen detection, and drawing the bullet sprite.

### `PowerUp.java`

Represents collectible power-ups. Handles power-up type, collision with the player, and drawing the correct power-up sprite.

### `FloatingText.java`

Displays temporary floating text, such as score pop-ups when an enemy is defeated.

## How to Run

1. Open the project in IntelliJ IDEA or another Java IDE.
2. Make sure all `.java` files are inside the `src` folder.
3. Make sure the sprite images are inside:

```text
src/assets/
```

4. Run:

```text
TopDownGame.java
```

## Game Rules

- The player starts with 100 health.
- Enemies chase the player.
- If an enemy touches the player, the player takes damage.
- The player shoots enemies using the arrow keys.
- Defeating enemies increases the score.
- When all enemies are defeated, the next wave begins.
- Later waves spawn more enemies and stronger enemies.
- Some enemies may drop power-ups.
- The game ends when the player's health reaches 0.

## Power-Ups

### Health

Restores some player health when collected.

### Rapid Fire

Temporarily lowers the shooting cooldown, allowing the player to fire faster.

## Enemy Types

### Normal Enemy

Balanced speed and health.

### Fast Enemy

Moves faster but has lower health.

### Tank Enemy

Moves slower but has much higher health.

## Future Improvements

Possible features to add later:

- Boss waves every 5 waves
- Upgrade choices between waves
- Sound effects
- High score saving
- Animated sprites
- Better tiled background
- Player damage flash
- Enemy hit flash
- Screen shake
- More power-ups
- More enemy attack types
- Difficulty selection

## Notes

This project is still in development and is being built step by step as a Java game programming practice project.
