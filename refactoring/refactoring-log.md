# Log de Refatorações
## Refatoração #1: Extract Method

- **Data**: 2025-10-08
- **Code Smell**: Code duplicate em Login.vue e DashboardBolsista.vue
- **Técnica Aplicada**: Relocating and deleting code
- **Arquivos Afetados**:
                        bpp-frontend/src/components/Login.vue
                        bpp-frontend/src/components/DashboardBolsista.vue
                        bpp-frontend/src/components/Teste.css
                        bpp-frontend/src/App.vue

- **Justificativa**: descrição de estilo (css) duplicada em arquivos vue
- **Resultado**:
- Style.css → arquivo criado para 
- App → Agora implementa o style.css para todos os arquivos de css
- Login.vue e DashboardBolsista.vue → redução de código
- **Impacto**: Melhor organização e clareza no código
- **Testes**: Realiza a sua devida função quando 

## Refatoração #2: Function with multiple roles
- **Data**: 2025-10-10
- **Code Smell**: Função encarregada de mais de um papel
- **Técnica Aplicada**: Extract method
- **Justificativa**: modularização das funções para um melhor funcionamento
- **Arquivos Afetados**:
                        bpp-frontend/src/components/DashboardBolsista.vue
- **Mudanças**:
- ativar_button(): 3 linhas
- marcarPonto(): 6 linhas
- changePopup(): 2 linhas


## Refatoração #3: Function with multiple roles (also)
- **Data**: 2025-10-14
- **Code Smell**: Função ilegível e encarregada de mais de um papel
- **Técnica Aplicada**: Extract method
- **Arquivos Afetados**:
                        bpp-spe-backend-java/src/main/java/dimap/ufrn/spe/api/v1/spe/models/Ponto.java
- **Justificativa**: função de linhas muito longas e encarregadas de muitos passos
- **Mudanças**:
calcularHorasFeitas(): 4 linhas
isHorarioValido(): 4 linhas
calcularDiferencaHoras(): 4 linhas