# Prompt Profissional (GCCD) para Criação do App no Android Studio / Google Studio

Copie e cole o texto abaixo diretamente no Google Studio, Gemini Code Assist ou IA de desenvolvimento no Android Studio:

---

### **[GOAL / OBJETIVO]**
Desenvolver um aplicativo Android nativo completo, moderno e robusto da **Bíblia King James (Versão Bilíngue: KJV 1611 em inglês no topo e tradução em português abaixo de cada versículo)**, incluindo banco de dados SQLite local (via Room ou sql.js/IndexedDB se construído em WebView/Capacitor), controle de progresso de leitura, estatísticas, personalização de fontes, temas visuais (Escuro, Sépia, Claro), introduções baseadas no argumento original de 1611 para cada capítulo, ajuste de velocidade de voz e síntese de voz nativa bilíngue separando corretamente os idiomas (`en-US` e `pt-BR`) com destaque visual sincronizado durante a narração.

### **[CONTEXT / CONTEXTO]**
O aplicativo é voltado para leitores cristãos, estudantes das Escrituras e entusiastas do idioma inglês que desejam estudar o texto clássico da Authorized King James Version lado a lado com o português de forma offline, integrada e fluida no Android. A arquitetura deve ser leve, resiliente a falhas de rede (funcionando 100% offline após o primeiro carregamento) e altamente responsiva.

### **[CONSTRAINTS / RESTRIÇÕES E REQUISITOS TÉCNICOS]**
1. **Estrutura Bilíngue por Versículo:** Cada versículo deve exibir obrigatoriamente o texto em inglês (KJV) em itálico e a respectiva tradução em português logo abaixo, com numeração clara.
2. **Banco de Dados SQLite:** Utilizar SQLite (ou IndexedDB/sql-asm em WebView) para salvar o histórico de leitura, progresso por capítulo e estatísticas de versículos lidos.
3. **Introduções KJV 1611:** Cada capítulo deve exibir o sumário original (*The Argument*) característico da edição histórica de 1611.
4. **Leitura em Voz Alta Bilíngue & Sincronização:** 
   - A narração deve alternar automaticamente o idioma da voz (`en-US` para o texto em inglês e `pt-BR` para o português).
   - O aplicativo deve incluir controle de velocidade da fala (0.5x a 2.0x) e suporte a seleção de vozes neurais/do sistema.
   - Deve haver um destaque visual (*verse-highlight*) no versículo ativo enquanto a narração estiver acontecendo.
5. **Customização de UI:** Ajuste dinâmico do tamanho da fonte (14px a 28px) e alternância de temas (Escuro, Sépia, Claro).
6. **Empacotamento Android:** O código deve estar pronto para compilação nativa ou para integração via Capacitor/WebView compatível com Android Studio.

### **[DELIVERABLES / ENTREGÁVEIS ESPERADOS]**
1. O código-fonte completo, limpo e estruturado em um único arquivo HTML/JS otimizado ou projeto modular Android pronto para build.
2. A implementação funcional de todas as telas (Índice de 66 livros, Leitor de Capítulos, Modal de Configurações, Modal de Estatísticas do Banco SQLite).
3. A lógica completa de Web Speech API com enfileiramento de áudio bilíngue e destaque visual em tempo real.