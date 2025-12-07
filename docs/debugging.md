Técnicas de Depuração e Registro de Bugs

Objetivo
- Fornecer um processo reproduzível para encontrar, registrar e corrigir bugs. Integrar cada correção com um teste automatizado sempre que possível.

1) Como documentar um bug (modelo)
- **Título:** resumo curto e objetivo (ex.: "NPE ao salvar Ponto sem matrícula").
- **Ambiente:** OS, JDK, versão da API, banco de dados, branch.
- **Passos para reproduzir:** requisição exata (URL, método, headers, payload) ou comandos.
- **Resultado esperado:** descrever comportamento correto.
- **Resultado atual:** incluir status HTTP, mensagem de erro e stacktrace.
- **Logs relevantes:** anexar trecho de logs (ou caminho do arquivo `target/*.log`).
- **Teste de regressão:** nome do teste JUnit a ser adicionado (ex.: `PontoServiceTest.shouldRejectMissingMatricula`).
- **Prioridade:** crítica / alta / média / baixa.


2) Ferramentas e comandos úteis (contexto do projeto)
- Logs do Spring Boot / testes:
```
# Relatórios do Surefire: target/surefire-reports/ (arquivos de .txt e .xml com saída dos testes)
# Logs gerados durante execução local/integration tests: ver o console do mvn ou arquivos em target/ (se configurado)
# Exemplo para acompanhar execução de testes em tempo real:
./mvnw -DtrimStackTrace=false test
```
- Executar testes com maior verbosidade (útil para reproduzir falhas):
```
./mvnw -DtrimStackTrace=false -Dtest=NomeDoTeste test
```
- Debug de controllers: usar `@WebMvcTest` + MockMvc na IDE (breakpoints) ou `spring-boot:run` em modo debug.
- Mockito / MockBean: os mocks são criados automaticamente em alguns testes de integração; ver `src/test/java` para exemplos.

Resultados dos testes locais (observados)
- **Total de testes executados:** 10
- **Failures / Errors:** 0
- Mensagem do log: "Usuário administrador criado automaticamente." — indica que os testes de integração inicializam dados (útil para reproduzir cenários).
- Local dos relatórios: `target/surefire-reports/` e o relatório de cobertura: `target/site/jacoco/index.html`.

Observações encontradas durante a execução
- Houve mensagens de advertência do JaCoCo relacionadas a instrumentação dinâmica ("Unsupported class file major version 69"). Isso não quebrou os testes, porém recomenda-se alinhar JDK/plugin para evitar falhas futuras.

Como transformar resultados em tarefas de correção (exemplos aplicáveis ao projeto)
- Se um teste falhar ao acessar um endpoint (ex.: `AdmController`), adicionar um teste que reproduza a requisição e inspecionar o controller e o service relacionado.
- Para problemas de autorização: inspecionar `security` package e `TokenService` (testar geração/validação de token). Adicionar testes de integração que autenticam via `POST /auth/login` e reutilizam o token em chamadas subsequentes.

Modelo de bug específico para este projeto (arquivo em `docs/bugs/`)
```
---
ID: 001
Título: Exemplo - Endpoint /spe/api/bolsista/entrada retorna 403 sem token
Ambiente: Linux, JDK 17 (ou JDK do ambiente), branch main
Passos:
 - POST /spe/api/bolsista/entrada sem header Authorization
Resultado esperado: 401/403 (acesso negado)
Resultado atual: 403
Logs: anexar trecho do console de testes em target/surefire-reports/
Teste adicionado: BolsistaControllerTest.shouldReturn403WhenNoAuth()
Status: Em aberto
---
```


3) Exemplo de entrada em `docs/bugs/001-npe-salvar-ponto.md` (modelo preenchido)

---
Título: NPE ao salvar Ponto sem matrícula
Ambiente: Linux, JDK 17, branch `main`, MySQL local
Passos:
 - POST /api/v1/pontos com payload `{ "horario": "2025-12-01T08:00" }` (sem matrícula)
Resultado esperado: 400 Bad Request (campo matrícula obrigatório)
Resultado atual: 500 Internal Server Error - NullPointerException
Stacktrace: (copiar stacktrace relevante)
Logs: ver `logs/application-2025-12-08.log` linhas 120-145
Correção aplicada: validação do DTO e teste `PontoControllerTest.shouldReturn400WhenMatriculaMissing()` adicionado
Status: Corrigido
---

4) Workflow recomendado para cada bug
- Reproduzir localmente e criar issue (ou arquivo em `docs/bugs/`).
- Escrever teste que falha reproduzindo o bug.
- Implementar correção no código.
- Rodar testes e garantir que o novo teste passa.
- Commit + PR com descrição do bug, fix e link para o teste.

5) Onde salvar e organização
- Criar pasta `docs/bugs/` e adicionar um arquivo markdown por bug (usar prefixo numérico para ordenação, ex.: `001-...md`).

---
Título: Exemplo - NPE ao salvar Ponto
Passos:
 - Autenticar como bolsista X
 - POST /api/v1/ponto com payload {...}
Resultado atual: 500 - NullPointerException
Stacktrace: (copiar stacktrace)
Correção aplicada: checar nulo antes de acessar campo Y
Teste acrescentado: `PontoServiceTest.shouldNotThrowWhenMissingCampoY()`
Status: Corrigido / Em andamento
---

4) Onde salvar
- Criar um arquivo por bug em `docs/bugs/` ou manter um `docs/debugging-log.md` com entradas cronológicas.
