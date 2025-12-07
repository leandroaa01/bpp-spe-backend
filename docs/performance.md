Análise de Desempenho - guia rápido

Objetivo: identificar e corrigir gargalos em endpoints e serviços críticos do projeto SPE (Sistema de Ponto).

1) Testes de carga leves
- Usar `curl` em loop para endpoints simples ou ferramentas como `wrk`/`ab`/`hey`/`jmeter`.
- Exemplo (10 requisições concorrentemente por 1000 requisições):
```
hey -n 1000 -c 10 http://localhost:8080/api/v1/algum-endpoint
```

2) Profiler e Flight Recorder
- Iniciar aplicação com Java Flight Recorder (JFR):
```
./mvnw -DskipTests spring-boot:run -Dspring-boot.run.jvmArguments="-XX:StartFlightRecording=duration=60s,filename=recording.jfr"
```
- Abrir `recording.jfr` com Java Mission Control (JMC) para analisar CPU, alocações e bloqueios.

3) VisualVM / YourKit
- Conectar VisualVM a processo local para inspecionar CPU/heap e tirar heap dumps.

4) Métricas e Actuator
- O projeto agora inclui `spring-boot-starter-actuator` e `micrometer-registry-prometheus`.
- Endpoints úteis (após rodar a aplicação):
	- `/actuator/health` — estado da aplicação
	- `/actuator/metrics` — lista de métricas disponíveis
	- `/actuator/metrics/{metric.name}` — detalhes de uma métrica (ex.: `jvm.memory.used`)
	- `/actuator/prometheus` — formato Prometheus para scraping

Exemplos de consulta (projeto SPE)
 - Actuator (já configurado no `application.properties`):
```
# verificar saúde da aplicação
curl http://localhost:8080/actuator/health

# listar métricas disponíveis
curl http://localhost:8080/actuator/metrics

# métrica específica
curl http://localhost:8080/actuator/metrics/jvm.memory.used

# formato Prometheus (use como scrape target)
curl http://localhost:8080/actuator/prometheus
```

 - Endpoints REST principais do SPE (protegidos por segurança — exigem token JWT obtido via `POST /auth/login`):
```
# Autenticação (obter token)
POST http://localhost:8080/auth/login  { "username": "<user>", "password": "<pass>" }

# Endpoints do bolsista (necessitam Authorization: Bearer <token>)
POST http://localhost:8080/spe/api/bolsista/entrada
POST http://localhost:8080/spe/api/bolsista/saida
GET  http://localhost:8080/spe/api/bolsista/meus-pontos
GET  http://localhost:8080/spe/api/bolsista/total-horas
GET  http://localhost:8080/spe/api/bolsista/meus-dados

# Endpoints do admin (necessitam permissão adequada)
POST http://localhost:8080/spe/api/admin/register
GET  http://localhost:8080/spe/api/admin/pontos
PUT  http://localhost:8080/spe/api/admin/mudar-senha/bolsista/{id}
PUT  http://localhost:8080/spe/api/admin/mudar-dados/bolsista/{id}
```
```
# status
curl http://localhost:8080/actuator/health

# listar métricas
curl http://localhost:8080/actuator/metrics

# consultar métrica específica
curl http://localhost:8080/actuator/metrics/jvm.memory.used

# endpoint prometheus
curl http://localhost:8080/actuator/prometheus
```

Coleta de métricas com Prometheus (exemplo simples)
- Em `prometheus.yml` adicione um scrape job apontando para `http://<host>:8080/actuator/prometheus`.

Como perfilar endpoints críticos (passo a passo) — aplicado ao SPE
1. Identifique endpoints com maior latência via Actuator ou logs.
2. Reproduza carga sobre o endpoint com `hey`/`wrk` para gerar amostras:
```
hey -n 5000 -c 50 http://localhost:8080/spe/api/bolsista/meus-pontos
```
3. Durante a carga, execute JFR por 60–120s e abra o `.jfr` no JMC para analisar hotspots de CPU, alocações e bloqueios.
4. Use VisualVM ou Flight Recorder para inspeção mais detalhada (thread dumps, heap snapshots).

Prioridade de correções
- Queries lentas: analisar e criar índices no banco (ver `EXPLAIN` no MySQL).
- Serialização/deserialização: reduzir campos pesados em DTOs ou usar streaming.
- Operações síncronas custosas: considerar async / fila.

Exemplos de consulta:
```
# status
curl http://localhost:8080/actuator/health

# listar métricas
curl http://localhost:8080/actuator/metrics

# consultar métrica específica
curl http://localhost:8080/actuator/metrics/jvm.memory.used

# endpoint prometheus
curl http://localhost:8080/actuator/prometheus
```

5) Priorizar correções
- Queries lentas: adicionar índices no banco
- Serializações pesadas: revisar DTOs e mapeamentos
- Bloqueios: identificar sincronizações desnecessárias
