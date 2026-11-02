# 📖 Bíblia King James 1611 Bilíngue (Bilingual Bible)

[![Android](https://img.shields.io/badge/Platform-Android%20%7C%20Web-blue.svg)](https://github.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0+-purple.svg)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose%20M3-4285F4.svg)](https://developer.android.com/jetpack/compose)
[![GitHub Pages](https://img.shields.io/badge/Deploy-GitHub%20Pages-success.svg)](https://pages.github.com)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

Uma aplicação bíblica moderna, bilíngue (Inglês e Português) e completa, desenvolvida com **Jetpack Compose (Android)** e uma versão web pronta para **GitHub Pages (`index.html`)**.

O projeto compara versículo a versículo a consagrada tradução **King James Version (KJV 1611)** com a versão em Português (**João Ferreira de Almeida**), trazendo sintetizador de voz (TTS) sincronizado, gerenciador de downloads para leitura 100% offline, salvamento automático de progresso de leitura e materiais históricos originais de 1611.

---

## 🌟 Principais Recursos

### 1. 📖 Leitura Bilíngue Comparativa
- **Versículo a Versículo**: Exibição simultânea do texto em Inglês (KJV) e Português (Almeida).
- **Livros e Capítulos**: Acesso rápido a todos os 66 livros canônicos do Antigo e Novo Testamento (com introduções históricas e notas dos apócrifos de 1611).
- **Tipografia Adaptável**: Ajuste em tempo real do tamanho da fonte (14px a 28px) e fontes com serifa clássica (*Georgia / Baskerville*) para máxima legibilidade.

### 2. 🔊 Áudio Sincronizado (TTS - Text-To-Speech)
- **Modos de Narração**:
  - *Bilíngue Alternado*: Narra o versículo em Inglês e em seguida em Português.
  - *Somente Português*.
  - *Somente Inglês*.
- **Controle de Velocidade**: Ajustes de reprodução em **0.75x**, **1.0x**, **1.25x** e **1.5x**.
- **Destaque Visual**: O versículo narrado no momento recebe realce com animação suave.

### 3. 💾 Modo 100% Offline e Gerenciador de Downloads
- **Download por Capítulo**: Baixe capítulos individuais instantaneamente para a memória local.
- **Download de Livro Completo**: Baixe livros inteiros em segundo plano (ex: todos os 50 capítulos de Gênesis de uma só vez), com barra de progresso em tempo real e cancelamento seguro.
- **Persistência Confiável**:
  - Na versão Web: persistido via **LocalStorage** com serialização JSON otimizada.
  - Na versão Android: armazenamento em cache local com Room/armazenamento estruturado.

### 4. 📊 Progresso de Leitura e Histórico
- **Progresso Real**: Inicia do zero e calcula a porcentagem lida com base nos capítulos marcados como lidos.
- **Retomar Leitura**: Botão "Continuar Leitura" na tela inicial que abre diretamente o último livro e capítulo acessado.
- **Lista de Livros Concluídos**: Identificação visual com checkmark nos capítulos e livros finalizados.

### 5. 🎨 Temas e Acessibilidade
- **Paletas de Cores Material Design 3**:
  - 📜 **Sépia**: Visual aconchegante imitando páginas antigas de livros.
  - ☀️ **Claro (Light)**: Alto contraste para leitura diurna.
  - 🌙 **Escuro (Dark / OLED)**: Fundo escuro suave com economia de energia em telas AMOLED.
- **Responsivo e Ergonômico**: Projetado tanto para telas de smartphones quanto para tablets e navegadores desktop.

---

## 📁 Estrutura do Repositório

```text
├── index.html                  # Aplicação Web completa (SPA/PWA) para GitHub Pages
├── metadata.json               # Metadados do projeto no AI Studio Build
├── build.gradle.kts            # Configuração do Gradle (Root)
├── settings.gradle.kts         # Definições dos módulos do projeto
├── gradle/
│   └── libs.versions.toml      # Catálogo centralizado de dependências
├── app/
│   ├── build.gradle.kts        # Configurações do módulo Android (Compose, SDK, Plugins)
│   ├── src/
│   │   ├── main/
│   │   │   ├── AndroidManifest.xml
│   │   │   ├── java/com/example/
│   │   │   │   ├── MainActivity.kt        # Ponto de entrada Android / Jetpack Compose
│   │   │   │   └── ui/theme/              # Tema Material 3 (Color, Theme, Type)
│   │   │   └── res/                       # Recursos XML, ícones e strings
│   │   └── test/java/com/example/         # Testes de unidade e Robolectric
└── README.md                   # Documentação do projeto
```

---

## 🚀 Como Executar o Projeto

Você pode utilizar este repositório de duas formas: como uma **página Web pública via GitHub Pages** ou compilando o **Aplicativo Nativo Android**.

### Opção 1: Publicando no GitHub Pages (Web)

O arquivo `index.html` na raiz do repositório é uma aplicação autocontida, com zero dependências externas de compilação (HTML5, CSS3, JavaScript ES6+ e Web Speech API).

1. Faça o **Fork** ou envie este repositório para a sua conta no GitHub.
2. No repositório no GitHub, acesse **Settings** > **Pages**.
3. Em **Build and deployment**:
   - **Source**: selecione `Deploy from a branch`.
   - **Branch**: selecione `main` (ou `master`) e pasta `/ (root)`.
4. Clique em **Save**.
5. Em poucos instantes, o seu leitor bíblico estará online e acessível em:
   ```
   https://<seu-usuario>.github.io/<nome-do-repositorio>/
   ```

---

### Opção 2: Compilando o Aplicativo Android

#### Pré-requisitos
- **Android Studio** (versão Ladybug / 2024.2 ou superior)
- **JDK 17** ou superior instalado e configurado no `JAVA_HOME`
- **Android SDK** com API 34 ou 36 instalada

#### Passos para compilar:

1. Clone o repositório localmente:
   ```bash
   git clone https://github.com/<seu-usuario>/<nome-do-repositorio>.git
   cd <nome-do-repositorio>
   ```

2. Para compilar o APK de depuração (Debug):
   ```bash
   gradle assembleDebug
   ```
   O arquivo APK gerado estará em:
   `app/build/outputs/apk/debug/app-debug.apk`

3. Para executar os testes de unidade e Robolectric:
   ```bash
   gradle :app:testDebugUnitTest
   ```

4. Para abrir no Android Studio:
   - Abra o Android Studio e selecione **Open**.
   - Selecione a pasta raiz deste repositório e aguarde a sincronização do Gradle.
   - Pressione **Run** (`Shift + F10`) com um emulador ou dispositivo físico conectado.

---

## 🌐 APIs e Serviços Utilizados

- **API de Textos Bíblicos**: [bible-api.com](https://bible-api.com)
  - Versão em Inglês: `https://bible-api.com/<livro>+<capitulo>?translation=kjv`
  - Versão em Português: `https://bible-api.com/<livro>+<capitulo>?translation=almeida`
- **Síntese de Voz (TTS)**:
  - Versão Web: `window.speechSynthesis` (Web Speech API) nativa dos navegadores.
  - Versão Android: `android.speech.tts.TextToSpeech`.
- **Armazenamento**:
  - Web: `window.localStorage` estruturado em chaves nomeadas (`biblia_1611_*`).
  - Android: Cache local e Room.

---

## 🛠️ Tecnologias Utilizadas

- **Kotlin 2.0+**
- **Jetpack Compose** & **Material Design 3**
- **Coroutines & Flow** para requisições assíncronas e download em lote
- **HTML5, CSS Moderno (CSS Custom Properties), JavaScript (ES2022)**
- **Web Speech Synthesis API**
- **Robolectric & JUnit 4** para testes automatizados

---

## 📄 Licença

Este projeto é disponibilizado sob a licença [MIT](LICENSE). O texto bíblico King James Version de 1611 é de domínio público.

---

✨ *Desenvolvido para estudo e meditação das Escrituras Sagradas de forma bilíngue e acessível.*
