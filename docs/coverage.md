Visão geral da Análise de Cobertura (JaCoCo)

# Relatório Final

## Evolução da Cobertura

### Primeira análise

- Cobertura de linhas: 20%
- Cobertura de branchs: 15%
- Linhas não cobertas: 965

### Análise Final

Estado atual do projeto (resultado dos testes locais)
- O `jacoco-maven-plugin` está configurado no `pom.xml` e gerou o relatório em `target/site/jacoco/index.html` durante a execução de `./mvnw clean test`.
- Os testes executados localmente foram: **10 tests, 0 failures, 0 errors** (resultado do Maven Surefire). O log também mostra: "Usuário administrador criado automaticamente." durante a inicialização dos testes de integração.

Resumo da cobertura observada (HTML do JaCoCo)
- **Cobertura total de linhas:** 70% (320 linhas cobertas de 1.055)
- **Cobertura de ramos (branches):** 53% (21 de 40)

Cobertura por pacote (valores aproximados extraídos do relatório):
- `dimap.ufrn.spe.api.v1.config` — 100%
- `dimap.ufrn.spe.api.v1` — ~89%
- `dimap.ufrn.spe.api.v1.security` — 75% (branch ~50%)
- `dimap.ufrn.spe.api.v1.models` — 75% (branch ~40%)
- `dimap.ufrn.spe.api.v1.controllers` — 67% (branch ~50%)
- `dimap.ufrn.spe.api.v1.dtos` — 58% (geralmente DTOs são simples, por isso menor prioridade)
- `dimap.ufrn.spe.api.v1.services` — 21% (área mais crítica a melhorar)


Onde encontrar o relatório
- Arquivo HTML: `target/site/jacoco/index.html` (após `./mvnw test`).
- Dados binários: `target/jacoco.exec` (usado por ferramentas externas).

Como gerar localmente
```
./mvnw clean test
# abrir relatório no Linux
xdg-open target/site/jacoco/index.html
```

## Análise de Memória
- Estruturas eficientes são utilizadas ao longo do projeto, visando a uma maior otimização ao longo de todo o programa