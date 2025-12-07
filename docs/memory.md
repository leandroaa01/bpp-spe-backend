Gerenciamento de Memória - checklist e comandos

1) Flags úteis para executar a aplicação (para análise):
```
# Habilitar dump de heap em OOM
java -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=./heapdump.hprof -jar target/spe-0.0.1-SNAPSHOT.jar

# Ou via mvn spring-boot:run
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=./heapdump.hprof"
```

2) Coletando heap dump manualmente
- `jmap -dump:live,format=b,file=heapdump.hprof <pid>`

3) Análise
- Abrir `heapdump.hprof` com Eclipse MAT para identificar dominator tree e objetos que mais consomem memória.

4) Dicas práticas
- Evitar coleções globais não limitadas (ex.: Map mantendo sessões em memória)
- Usar `@Transactional` apropriadamente para evitar carregamento excessivo de entidades
- Revisar uso de caches (limite de tamanho/time-to-live)

5) Logs de GC
- Rodar com `-Xlog:gc*:file=gc.log:time` (Java 17) e analisar pausas.

---
Passo a passo sugerido para investigação de vazamentos ou alto consumo

1) Reproduzir carga/condição que causa alto consumo
- Iniciar a aplicação localmente com flags de dump e GC:
```
./mvnw -DskipTests spring-boot:run -Dspring-boot.run.jvmArguments="-Xmx1G -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=./heapdump.hprof -Xlog:gc*:file=gc.log:time"
```

2) Coletar heap dump se a memória subir além do esperado
- Usar `jcmd <pid> GC.heap_info` / `jcmd <pid> GC.heap_dump heapdump.hprof` ou `jmap`:
```
jcmd <pid> GC.heap_dump ./heapdump.hprof
```

3) Analisar heap dump
- Abrir `heapdump.hprof` com Eclipse MAT e procurar por:
	- Dominator tree (objetos que retêm memória)
	- Classes com mais instâncias
	- Suspeitas de collections crescendo indefinidamente

4) Capturar thread dumps durante o pico (útil para deadlocks ou threads ocupadas):
```
jstack -l <pid> > threaddump.txt
```

5) Ferramentas úteis
- Eclipse MAT (analisar heap dumps)
- VisualVM (inspeção em tempo real, snapshot heap, CPU sampler)
- Java Mission Control / Flight Recorder (JFR) para amostras com baixo overhead

6) Boas práticas para reduzir uso de memória
- Evitar cargas EAGER em relacionamentos JPA quando não necessário (`FetchType.LAZY`).
- Limitar tamanho de caches (use Caffeine/Guava com políticas de remoção).
- Fechar conexões e streams explicitamente.

Checklist para etapa 5 (Gerenciamento de Memória)
- [x] Documentação das flags e comandos (feito).
- [x] Instruções para coletar heap dumps e GC logs (feito).
- [ ] Realizar análise prática em ambiente com dados reais (requer execução no ambiente do usuário).

Observações específicas do SPE e recomendações
- O pacote `services` apresentou baixa cobertura de testes (≈21%) — escreva testes unitários para serviços como `PontoService` para validar lógica de mapeamento e reduzir regressões que podem causar vazamentos indiretos.
- Pontos a verificar em runtime:
	- Ver se há coleções estáticas/únicas que armazenam objetos (ex.: caches mal configurados).
	- Verificar se entidades JPA carregam relacionamentos EAGER que possam manter objetos em memória desnecessariamente.

Passos práticos sugeridos para este projeto
1) Instrumentar a aplicação localmente com limites razoáveis de heap (ex.: -Xmx1G) e rodar carga: verificar crescimento de heap ao longo do tempo.
```
./mvnw -DskipTests spring-boot:run -Dspring-boot.run.jvmArguments="-Xmx1G -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=./heapdump.hprof -Xlog:gc*:file=gc.log:time"
```
2) Se observar crescimento contínuo, coletar heap dump com `jcmd` e analisar com Eclipse MAT.
3) Escrever um teste de integração que executa operações comuns (criar usuário, registrar ponto, listar pontos) em loop e monitorar uso de memória — ajuda a reproduzir vazamentos em ambiente controlado.

Ferramentas e locais úteis no projeto
- Relatórios de GC/heap: gerar `heapdump.hprof` e analisar com Eclipse MAT.
- Logs de testes: `target/surefire-reports/` (para ver se algum teste causa alocação incomum durante execução em lote).


