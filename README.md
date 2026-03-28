<p align="center">
  <img src="logo.jpg" width="200" alt="TelegramKt Logo">
</p>

<h1 align="center">TelegramKt</h1>

<p align="center">
  <strong>Modern, type-safe, coroutine-first Telegram Bot API library for Kotlin</strong>
</p>

<p align="center">
  <a href="LICENSE"><img src="https://img.shields.io/badge/License-Apache%202.0-blue.svg" alt="License"></a>
  <img src="https://img.shields.io/badge/Kotlin-2.0%2B-purple?logo=kotlin" alt="Kotlin">
  <img src="https://img.shields.io/badge/Ktor-3.4%2B-blue?logo=ktor" alt="Ktor">
  <img src="https://img.shields.io/badge/status-work--in--progress-orange" alt="WIP">
</p>

---

## 🚧 Work in Progress

This library is currently in active development. APIs may change until we reach v1.0.0.

**We'd love your help!** See [Contributing](#contributing) section below.

## ✨ Features

- **Coroutine-native** — Built with `suspend` functions and `Flow` for reactive updates
- **Type-safe DSL** — Intuitive handler syntax with smart parameter extraction
- **Multipart uploads** — Send photos, documents, videos with `kotlinx.io`
- **Zero boilerplate** — `onPhoto { photo, caption -> ... }` instead of manual parsing
- **Ktor 3.4+** — Modern HTTP client with CIO engine

## 🚀 Quick Start

```kotlin
fun main() {
    val bot = telegramBot(System.getenv("BOT_TOKEN")) {
        onCommand("start") { args ->
            reply("Welcome! 🎉")
        }
        
        onPhoto { photo, caption ->
            reply("Nice pic! Resolution: ${photo.width}x${photo.height}")
        }
        
        onMessage { text ->
            reply("Echo: $text")
        }
    }
    
    bot.startPolling()
}
```

## 📚 Examples
### All examples places in [examples](#examples) path

## 🤝 Contributing
We welcome contributions! Areas where help is needed:
* More API methods coverage (stickers, payments, etc.)
* Additional examples and documentation
* Webhook support (currently only polling)
* Performance benchmarks
  
## 📄 License