Visão geral da Análise de Cobertura (JaCoCo)

Estado atual do projeto (resultado dos testes locais)
- O `jacoco-maven-plugin` está configurado no `pom.xml` e gerou o relatório em `target/site/jacoco/index.html` durante a execução de `./mvnw clean test`.
- Os testes executados localmente foram: **10 tests, 0 failures, 0 errors** (resultado do Maven Surefire). O log também mostra: "Usuário administrador criado automaticamente." durante a inicialização dos testes de integração.

Resumo da cobertura observada (HTML do JaCoCo)
- **Cobertura total de linhas:** 68% (344 linhas cobertas de 1.106)
- **Cobertura de ramos (branches):** 47% (21 de 40)

Cobertura por pacote (valores aproximados extraídos do relatório):
- `dimap.ufrn.spe.api.v1.config` — 100%
- `dimap.ufrn.spe.api.v1` — ~89%
- `dimap.ufrn.spe.api.v1.security` — 75% (branch ~50%)
- `dimap.ufrn.spe.api.v1.models` — 75% (branch ~40%)
- `dimap.ufrn.spe.api.v1.controllers` — 64% (branch ~50%)
- `dimap.ufrn.spe.api.v1.dtos` — 58% (geralmente DTOs são simples, por isso menor prioridade)
- `dimap.ufrn.spe.api.v1.services` — 21% (área mais crítica a melhorar)

Observações importantes retiradas da execução
- Durante a execução dos testes houve avisos de instrumentação do JaCoCo relacionados a "Unsupported class file major version 69" — o relatório foi gerado apesar do aviso. Isso indica que a JVM usada para rodar os testes provavelmente é mais nova do que a versão do agente JaCoCo suportada; recomendo alinhar a versão do JDK (usar Java 17 conforme `pom.xml`), ou atualizar o `jacoco-maven-plugin` para uma versão que suporte sua JVM atual.


Onde encontrar o relatório
- Arquivo HTML: `target/site/jacoco/index.html` (após `./mvnw test`).
- Dados binários: `target/jacoco.exec` (usado por ferramentas externas).

Como gerar localmente
```
./mvnw clean test
# abrir relatório no Linux
xdg-open target/site/jacoco/index.html
```

Interpretação prática dos resultados
- **Cobertura de linhas (line coverage):** indica percentagem de linhas executadas por testes. Priorizar classes com cobertura baixa e alta criticidade.
- **Cobertura de ramo (branch coverage):** importante para lógica condicional; valores baixos indicam caminhos não testados.
- **Complexidade e cobertura:** focar testes em métodos com alta complexidade ciclomática.

Metas sugeridas
- Projeto: mínimo 70% de cobertura de linhas como objetivo inicial.
- Pacotes críticos (serviços, regras de negócio): alvo ≥ 80%.
- Documentar exceções (código gerado, DTOs simples sem lógica podem ser excluídos).

Checks automáticos (opcional)
- É possível adicionar `jacoco:check` ao `pom.xml` para falhar o build caso a cobertura fique abaixo de um limite. Exemplo de trecho (colocar dentro do `jacoco-maven-plugin`):

```xml
<execution>
  <id>check</id>
  <goals>
    <goal>check</goal>
  </goals>
  <configuration>
    <rules>
      <rule>
        <element>BUNDLE</element>
        <limits>
          <limit>
            <counter>LINE</counter>
            <value>COVERED_RATIO</value>
            <minimum>0.70</minimum>
          </limit>
        </limits>
      </rule>
    </rules>
  </configuration>
</execution>
```

CI / Publicação de relatórios
- GitHub Actions (exemplo breve): executar `./mvnw test` e publicar `target/site/jacoco` como artifact para inspeção no run. Também é possível enviar cobertura para Codecov ou SonarCloud.

Possíveis problemas e soluções
- **Erro: Unsupported class file major version** — indica incompatibilidade entre a versão do plugin JaCoCo e o bytecode (JDK). Solução: atualizar `jacoco-maven-plugin` para versão compatível (ex.: 0.8.10+ ou 0.8.11) ou executar testes com JDK suportado.
- **Mockito / ByteBuddy instrumentação:** mocks gerados dinamicamente podem gerar warnings; atualizar JaCoCo/Mockito/ByteBuddy reduz a incidência.


Checklist para etapa 2 (Análise de Cobertura) — status do projeto
- [x] Configurar JaCoCo no `pom.xml` (feito).
- [x] Executar `./mvnw clean test` e gerar `target/site/jacoco/index.html` (feito) — relatório disponível em `target/site/jacoco/index.html`.
- [x] Revisar pacotes com cobertura baixa e priorizar testes — **áreas prioritárias identificadas abaixo**.
- [ ] Opcional: configurar `jacoco:check` para impor meta mínima (sugiro 70% global inicialmente).

Prioridades de melhoria (próximos testes a adicionar)
- **Services (21%):** criar testes unitários para `PontoService` (mock de `PontoRepository`) cobrindo mapeamentos para `BolsistaPontoDTO` e casos com listas vazias / nulas.
- **Controllers (64%):** adicionar `@WebMvcTest` para endpoints principais; testar respostas e códigos HTTP para cenários autorizados e não autorizados.
- **DTOs (58%):** adicionar testes simples de serialização/deserialização se houver lógica de conversão.
- **Security (75%):** adicionar testes para geração/validação de token (ex.: `TokenService`) e cenários de permissões.

Exemplo de próximos testes a implementar (prioridade alta → baixa)
1. `PontoServiceTest` — mock `PontoRepository`, testar `listarTodosOsPontos()` com 0, 1 e múltiplos pontos.
2. `AdmControllerTest` (`@WebMvcTest`) — testar `GET /spe/api/admin/pontos` com role admin mockada.
3. `AuthenticationControllerTest` — testar `POST /auth/login` com credenciais válidas/inválidas.

