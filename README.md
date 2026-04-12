<p align="center">
  <img src="logo.jpg" width="200" alt="TelegramKt Logo">
</p>

<h1 align="center">TelegramKt</h1>

<p align="center">
  <strong>Modern, type-safe, coroutine-first Telegram Bot API library for Kotlin</strong>
</p>

<div align="center">

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0%2B-purple?logo=kotlin)](https://kotlinlang.org)
[![Ktor](https://img.shields.io/badge/Ktor-3.4%2B-blue?logo=ktor)](https://ktor.io)
[![Status](https://img.shields.io/badge/status-work--in--progress-orange)](https://github.com/telegram-kt/telegram-kt)
[![Stars](https://img.shields.io/github/stars/telegram-kt/telegram-kt?style=social)](https://github.com/telegram-kt/telegram-kt)

</div>

---

## ⭐ Support the Project

**If you find this library useful, please consider giving it a star!** ⭐

It helps us grow, attract contributors, and shows that the Kotlin community needs a modern Telegram Bot library.

[![Star this repo](https://img.shields.io/github/stars/telegram-kt/telegram-kt?style=social)](https://github.com/telegram-kt/telegram-kt/stargazers)

---

## 🚧 Work in Progress

This library is currently in active development. APIs may change until we reach v1.0.0.

**Current status:**
- ✅ Core API methods (messaging, files, updates)
- ✅ Type-safe DSL for handlers
- ✅ Long-polling support
- ✅ Multipart file uploads
- 🔄 Webhook support (coming soon)
- 🔄 More API methods coverage

**We'd love your help!** See [Contributing](https://github.com/telegram-kt/telegram-kt/tree/master?tab=readme-ov-file#-contributing) section below.

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
### All examples places in [examples](https://github.com/telegram-kt/telegram-kt/tree/master/examples) path

## 🤝 Contributing
We welcome contributions! Areas where help is needed:
* More API methods coverage (stickers, payments, etc.)
* Additional examples and documentation
* Webhook support (currently only polling)
* Performance benchmarks
  
## 📄 License
### This project is licensed under the Apache License 2.0 - see the [LICENSE](https://github.com/telegram-kt/telegram-kt/blob/master/LICENSE) file for details.

## 📬 Contact
* ### GitHub Issues: [GitHub Issues](https://github.com/telegram-kt/telegram-kt/issues)
* ### Discussions: [Discussions](https://github.com/telegram-kt/telegram-kt/discussions)

---

<div align="center">
 <h3> Made with ❤️ for the Kotlin community </h3>
</div>
