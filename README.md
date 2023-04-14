# TimeControl

TimeControl is a [Bukkit](https://dev.bukkit.org/) / [Spigot](https://www.spigotmc.org/wiki/about-spigot/) server plugin that lets you configure the duration of each section of the [Minecraft daylight cycle](https://minecraft.fandom.com/wiki/Daylight_cycle).

It originally started as a fork of [foncused's "LongerDays"](https://github.com/foncused/LongerDays). At this point there isn't much left of the original code, but it still deserves an honorable mention.

## Features

- allows setting a custom duration for each section of the daylight cycle individually: day, night, sunset and sunrise
- both global and per-world configuration is possible
- optional: skips through the night if enough players go to sleep

## Installation

1. Copy the `TimeControl.jar` file into your server's `plugins` directory
2. Start your server
3. Edit the [config.yml](src/main/resources/config.yml) in `plugins/TimeControl/`
4. Restart (or reload) your server

## Configuration

Depending on your needs or preferences, you can configure TimeControl globally (for every world), per-world or a mix.

**Important**: In any case, you have to set the worlds to enable the plugin for if you want the plugin to do something.

### Configuring Defaults / Global Settings

```yaml
# This section defines the settings for every world
# that doesn't explicitly override them.
defaults:
  durations:
    # Daytime duration in minutes
    day: 10

    # Nighttime duration in minutes
    night: 8

    # Sunset and sunrise can either be configured in minutes by providing a numeric value
    # or set to pass as the same speed as "day" or "night".
    sunset: "night" # or "day" or a number (e.g. 2 for 2 minutes)
    sunrise: "night" # same options as sunset

  # With night-skipping enabled, players can skip the night by sleeping.
  # This functionality honors the "playersSleepingPercentage" game rule if it's available,
  # otherwise the percentage should be configured via "players-sleeping-percentage" below.
  night-skipping:
    enabled: true

  # If your server doesn't have the "playersSleepingPercentage" game rule,
  # you can still control the percentage by enabling this.
  players-sleeping-percentage:
    enabled: false
    percentage: 100
```

### Configuring Worlds

```yaml
# Worlds to enable TimeControl for.
# You can override the settings defined in "defaults" per world.
worlds:
  # Enable custom time control for "world". All settings taken from the "defaults" config.
  - name: "world"
  # Enable custom time control for "world2" and override some durations.
  # All other settings are taken from the "defaults" config.
  - name: "world2"
    durations:
      day: 20
      night: 10
      sunrise: 5
  # Enable custom time control for "world3". Use values from "defaults",
  # but disable night skipping for this world.
  - name: "world3"
    night-skipping:
      enabled: false
```

### Durations

Here's an overview of the default durations of the daylight cycle sections. If you want to keep a section at its original speed/duration, use the corresponding configuration value.

| Section   | Vanilla Duration | Configuration value |
| --------- | ---------------- | ------------------- |
| Daytime   | 10 min           | `10`                |
| Nighttime | 8:20 min         | `8.3333333`         |
| Sunset    | 0:50 min         | `0.8333333`         |
| Sunrise   | 0:50 min         | `0.8333333`         |
|           |                  |                     |
| **Total** | 20 min           |                     |

More details can be found in the documentation for [Minecraft's daylight cycle](https://minecraft.fandom.com/wiki/Daylight_cycle).

## How It Works

<details>
<summary>Time</summary>

TimeControl takes over the daylight cycle. To do that, it disables the game rule `doDaylightCycle` (and requires it to stay disabled). It will then progress time according to the configuration and the world's time.

</details>

<details>
<summary>Night Skipping</summary>

If night skipping is enabled, it tries to leverage the `playersSleepingPercentage` game rule to decide when to skip the night.

This rule was added in 1.17 and should be preferred over the `players-sleeping-percentage` configuration provided by the plugin because Minecraft players might get kicked out of their beds by it.
Weird things may happen when using the configuration while the game rule is set.

In Minecraft <= 1.16, the `playersSleepingPercentage` game rule isn't available, but skipping the night should still work fine when using the `players-sleeping-percentage` setting in the config.

Disabling night skipping won't prevent players from using their beds. They can still sleep and depending on your configuration, may or may not be kicked out of bed (by Minecraft), but the time won't skip to the next morning.

</details>

## Support

If you run into any server performance problems, or if the plugin is not working as advertised (console errors, bugs, etc.), please do not hesitate to contact me, post in the discussion thread, or open an issue on GitHub.

### Diagnosing

Should you run into issues, you can try to enable the debug mode by adding

```yaml
debug: true
```

at the top level (next to `defaults` and `worlds`) of your `config.yml`.

It's no silver bullet and it won't magically fix any issues, but it might lead you to find issues or even solutions yourself.

## Links

- [TimeControl on SpigotMC](https://www.spigotmc.org/resources/timecontrol.108829/)
- [Plugin statistics](https://bstats.org/plugin/bukkit/Time%20Control/18202)
