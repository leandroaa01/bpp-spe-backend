# Code Smells Identificados

## 1. Duplicate Code
- **Arquivo**:  bpp-frontend/src/components/Login.vue
                bpp-frontend/src/components/DashboardBolsista.vue
                bpp-frontend/src/components/Teste.css
                bpp-frontend/src/App.vue

- **Linha**: 90-184
- **Descrição**: descrição de estilo (css) duplicada em arquivos vue
- **Severidade**: Alta
- **Status**: Corrigido

## 2. Function with multiple roles
- **Arquivos**: bpp-frontend/src/components/DashboardBolsista.vue
- **Linhas**: 47-49 e 70-77
- **Descrição**: Função de registrar ponto também é encarregada de abrir o popup
- **Severidade**: Média
- **Status**: Corrigida

## 2. Function with multiple roles
- **Arquivos**: bpp-spe-backend-java/src/main/java/dimap/ufrn/spe/api/v1/spe/models/Ponto.java
- **Linhas**: 41-45
- **Descrição**: Função com múltiplos papéis e ilegível (complexa)
- **Severidade**: Média
- **Status**: Corrigida
