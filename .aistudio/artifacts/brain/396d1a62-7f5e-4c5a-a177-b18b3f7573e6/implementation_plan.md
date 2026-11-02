# Plano de Desenvolvimento: Bíblia King James Bilíngue (KJV 1611 & Português)

Aplicativo bilíngue de alta performance para leitura e estudo das Escrituras Sagradas, integrando o texto original da Authorized King James Version (1611) com tradução em português, síntese de voz alternada de alta naturalidade, controle de progresso e persistência local, com distribuição híbrida inteligente (App Android Nativo de máxima fluidez + versão Web HTML exportável para GitHub Pages).

---

### Decisões Críticas e Validação de Conceito

> [!IMPORTANT]
> **Fluidez e Naturalidade do Áudio:** Para garantir a máxima fluidez em qualquer aparelho Android (de modelos básicos a topos de linha) e o áudio mais natural possível, o aplicativo utiliza arquitetura nativa em **Jetpack Compose com Android Native Google TTS**, acompanhado de uma versão **HTML5/JS completa e autocontida** para publicação no GitHub Pages com LocalStorage.

- **Fluidez Superior em Android:** O motor Jetpack Compose renderiza a 60/120fps nativos sem o overhead de memória de WebViews em aparelhos modestos, garantindo rolagem suave mesmo em livros extensos como Salmos ou Gênesis.
- **Áudio Mais Natural:** O motor nativo `android.speech.tts.TextToSpeech` com engine do Google integra as vozes neurais instaladas no sistema operacional (`pt-BR` e `en-US`), permitindo alternância instantânea entre versículos sem latência e sem consumir pacote de dados.
- **Portabilidade GitHub Pages:** O projeto inclui um arquivo `index.html` autônomo e responsivo com persistência em `localStorage` e `window.speechSynthesis` para hospedagem imediata no GitHub.

---

## 1. Visão Geral e Conceito Central

- **O que faz:** Exibe a Bíblia capítulo a capítulo no formato bilíngue verso a verso (KJV 1611 em inglês com tipografia clássica / itálico no topo e tradução em português logo abaixo), com sumário histórico do capítulo (*The Argument of 1611*), áudio sincronizado que destaca o versículo falado, métricas de leitura e customização visual completa.
- **Público-alvo:** Estudantes da Bíblia, leitores devocionais e estudantes de inglês que desejam praticar o idioma através do texto bíblico clássico com áudio fluído e estudo lado a lado.
- **Valor Principal:** Experiência de leitura sem distrações, 100% offline após instalação, com dicção perfeita em ambos os idiomas e sincronia visual em tempo real.

---

## 2. Experiência do Usuário e Design Visual

### Fluxos Principais do Usuário
1. **Navegação de Livros e Capítulos:** Seletor ágil entre os 66 livros (Antigo e Novo Testamento), agrupados por categorias (Lei, História, Poesia, Profetas, Evangelhos, Epístolas) com indicador de progresso percentual.
2. **Leitor Bilíngue com Destaque:** Exibição elegante do versículo numerado, KJV 1611 em destaque suave e português em leitura direta.
3. **Controle de Áudio Flutuante:** Barra inferior de reprodução com botões Play/Pause, velocidade (0.5x, 0.75x, 1.0x, 1.25x, 1.5x, 2.0x), alternância de voz (Inglês primeiro -> Português em seguida, ou somente um dos idiomas) e destaque com rolagem suave automática para o versículo ativo.
4. **Painel de Configurações e Temas:** Alternância em tempo real entre temas (Claro, Escuro e Sépia Bíblico), ajuste de escala tipográfica (14sp a 28sp) e seleção de modo de voz.
5. **Estatísticas e Progresso:** Dashboard com porcentagem de leitura total, capítulos concluídos, tempo estimado e histórico de leituras recentes.

### Identidade Visual e Temas
- **Tema Sépia (Padrão Bíblico):** Fundo suave pergaminho (`#FBF7EE`), superfícies de leitura (`#F3EBD8`), texto em ébano clássico (`#2B241C`) e acentos dourados/âmbar (`#B4833E`).
- **Tema Escuro:** Fundo OLED profundo (`#121214`), superfícies elevadas (`#1C1B1F`), texto claro de alto contraste e realce dourado suave (`#E6C280`).
- **Tema Claro:** Branco límpido editorial com contraste otimizado para luz do dia.

---

## 3. Decisões Técnicas e Arquitetura

```
┌─────────────────────────────────────────────────────────────┐
│                 Bíblia Bilíngue KJV 1611                    │
├──────────────────────────────┬──────────────────────────────┤
│      Camada Android Nativa   │   Versão Web (GitHub Pages)  │
│  (Jetpack Compose + Kotlin)  │     (HTML5 + Vanilla JS)     │
├──────────────────────────────┼──────────────────────────────┤
│ • UI declarativa Material 3  │ • Layout responsivo flexbox  │
│ • Rolagem LazyColumn fluida  │ • LocalStorage / IndexedDB   │
│ • Google TextToSpeech nativo │ • Web Speech API bilíngue    │
│ • Room / SQLite Local        │ • Offline via PWA Ready      │
│ • Destaque em tempo real     │ • Publicação direta no git   │
└──────────────────────────────┴──────────────────────────────┘
```

### Estrutura de Dados
- **Capítulo e Versículos:**
  - `bookId`: identificador padronizado (ex: `GEN`, `MAT`, `JHN`, `PSA`).
  - `chapter`: número do capítulo.
  - `argument1611`: texto do sumário/argumento histórico da edição KJV 1611.
  - `verses`: lista ordenada com `verseNumber`, `textEn` (KJV 1611) e `textPt` (Português).
- **Progresso de Leitura:**
  - Estado de conclusão por capítulo, data da última leitura e timestamp do versículo atual.

### Motor de Síntese de Voz (TTS)
1. **No Android:** Inicialização de `android.speech.tts.TextToSpeech`. Para cada versículo, enfileira a fala em `Locale.US` e imediatamente após em `Locale("pt", "BR")` via `UtteranceProgressListener`, notificando a UI para aplicar a classe de realce visual no versículo correspondente.
2. **Na Versão HTML:** Utilização da `window.speechSynthesis` com instâncias de `SpeechSynthesisUtterance` alternando `lang = 'en-US'` e `lang = 'pt-BR'` sincronizadas com eventos `onstart` e `onend`.

---

## 4. Etapas de Implementação

1. **Etapa 1 - Configuração e Identidade:** Configuração do manifesto Android, nome do aplicativo, ícone adaptativo e cores dos temas.
2. **Etapa 2 - Repositório de Conteúdo Bíblico:** Implementação da estrutura de dados com introduções clássicas de 1611, textos KJV em inglês e português para leitura offline imediata e suporte a download sob demanda de novos livros.
3. **Etapa 3 - Leitor e Interface Compose:** Desenvolvimento da tela de leitura com alternância de temas (Sépia/Claro/Escuro), controle de fonte e seletor rápido de capítulos.
4. **Etapa 4 - Motor de Áudio Sincronizado:** Implementação do reprodutor com destaque sincronizado do versículo atual, controle de velocidade e alternância bilíngue de voz.
5. **Etapa 5 - Versão HTML5 Standalone para GitHub Pages:** Criação do arquivo completo `index.html` em assets / exportável com persistência via `localStorage`, leitor idêntico e suporte a publicação no GitHub Pages.
6. **Etapa 6 - Validação e Compilação:** Compilação do applet Android com `compile_applet` garantindo compatibilidade e fluidez.
