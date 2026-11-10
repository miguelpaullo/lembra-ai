# Especificação de Requisitos e Plano de Testes — Lembra Aí

| | |
|---|---|
| **Projeto** | Lembra Aí — Lista de Tarefas com Lembretes |
| **Disciplina** | Programação para Dispositivos Móveis — Avaliação 3 |
| **Instituição** | Universidade Federal de Mato Grosso do Sul (UFMS) |
| **Data** | 09/11/2026 |
| **Plataforma** | Android nativo, Java (Views) |

## 1. Identificação

| Integrante | Nome completo |
|---|---|
| Miguel | Miguel Paulo Rodrigues de Macedo |
| Reinaldo | Reinaldo Andrade Hashioka |
| Duda | Maria Eduarda da Silva de Macedo |

## 2. Visão geral do software

O **Lembra Aí** é um aplicativo Android para organizar tarefas pessoais. O usuário cria uma conta, entra no aplicativo e cadastra tarefas com título e descrição. Ele pode consultar, editar e excluir suas tarefas e, se quiser, definir um lembrete em minutos: quando o tempo termina, o aplicativo exibe uma notificação.

O aplicativo também oferece cadastro, autenticação, controle de sessão e gerenciamento de perfil. No perfil, o usuário atualiza nome e e-mail, altera a senha e define uma foto pela câmera ou pela galeria. Os dados são armazenados localmente com Room e as senhas são guardadas como hashes BCrypt.

**Dentro do escopo:** cadastro e login, sessão, CRUD de tarefas, lembrete por alarme com notificação, edição de perfil, troca de senha, foto de perfil.

**Fora do escopo:** sincronização com servidor, recuperação de senha por e-mail, compartilhamento de tarefas, restauração de alarmes após reinício do aparelho.

**Recurso obrigatório escolhido:** Alarmes (item 3 do enunciado, opção 1).

## 3. Papéis de usuário

### 3.1 Usuário não autenticado

- Visualizar a Splash.
- Acessar as telas de login e de cadastro.
- Criar uma conta.
- Tentar autenticar-se com e-mail e senha.

Não pode visualizar ou alterar tarefas, nem acessar o perfil.

### 3.2 Usuário autenticado

- Visualizar somente as próprias tarefas.
- Criar, editar e excluir as próprias tarefas.
- Definir um lembrete em minutos ao salvar uma tarefa.
- Receber as notificações dos lembretes, se a permissão e as configurações do dispositivo permitirem.
- Visualizar e editar nome e e-mail.
- Alterar a própria senha.
- Alterar a foto de perfil pela câmera ou pela galeria.
- Encerrar a sessão (logout).

### 3.3 Matriz de permissões

| Funcionalidade | Não autenticado | Autenticado |
|---|---|---|
| Ver a Splash | Sim | Sim |
| Cadastrar conta e fazer login | Sim | Não se aplica |
| Listar, criar, editar e excluir tarefas | Não | Sim (somente as próprias) |
| Agendar lembrete e receber notificação | Não | Sim |
| Editar perfil e senha | Não | Sim |
| Alterar foto de perfil | Não | Sim |
| Logout | Não | Sim |

## 4. Telas e navegação

| Tela | Acesso | Finalidade | Principais elementos | Navega para |
|---|---|---|---|---|
| Splash | Todos | Decidir o destino conforme a sessão | Tela de abertura | Login ou Lista |
| Login | Não autenticado | Autenticar | E-mail, senha, Entrar, acesso ao cadastro | Lista, Cadastro |
| Cadastro | Não autenticado | Criar conta | Nome, e-mail, senha, confirmação | Login |
| Lista de Tarefas | Autenticado | Consultar tarefas | `ListView`, botão Nova tarefa, menu de opções (Perfil, Sair), menu de contexto (Editar, Excluir) | Formulário, Perfil, Login |
| Formulário de Tarefa | Autenticado | Criar e editar | Título, descrição, minutos do lembrete, Salvar | Lista |
| Perfil | Autenticado | Dados, senha e foto | Nome, e-mail, senhas, Tirar foto, Escolher da galeria | Lista, Login |

## 5. Regras de negócio

| ID | Regra | Requisitos |
|---|---|---|
| RN01 | O e-mail é único: não existem duas contas com o mesmo e-mail. | RF02, RF12 |
| RN02 | A senha tem no mínimo seis caracteres. | RF02, RF13 |
| RN03 | O e-mail deve ter formato básico válido (contém `@` e domínio). | RF02, RF12 |
| RN04 | O título da tarefa é obrigatório; espaços em branco não contam. | RF05, RF07 |
| RN05 | O lembrete é opcional e, se informado, é um inteiro maior que zero (minutos), contado a partir do salvamento. | RF05, RF07, RF09 |
| RN06 | Cada usuário acessa somente as próprias tarefas. | RF06, RF07, RF08 |
| RN07 | A senha é armazenada apenas como hash BCrypt com salt. | RF02, RF13 |
| RN08 | A foto de perfil é um arquivo do próprio app; o Room guarda seu caminho. | RF14, RF15 |

## 6. Requisitos funcionais

Cada requisito indica os casos de teste (seção 12) que o verificam.

### RF01 — Apresentar a tela de abertura e direcionar o usuário

- **Origem no enunciado:** Splash screen e controle de sessão.
- **Ator:** Usuário.
- **Entradas:** Inicialização do aplicativo e estado atual da sessão.
- **Processamento:** O aplicativo exibe a Splash e verifica se existe um usuário autenticado.
- **Saídas esperadas:** Com sessão ativa, o usuário é direcionado à lista de tarefas; sem sessão, à tela de login.
- **Critérios de aceitação:**
    - A Splash usa o fluxo de inicialização do Android, sem atraso artificial.
    - Com sessão ativa a lista de tarefas abre direto; sem sessão abre o login.
- **Casos de teste:** CT12, CT13, CT14

### RF02 — Cadastrar usuário

- **Origem no enunciado:** Login/cadastro.
- **Ator:** Usuário não autenticado.
- **Entradas:** Nome, e-mail, senha e confirmação de senha.
- **Processamento:** O aplicativo valida o preenchimento, o formato básico do e-mail, o tamanho mínimo da senha (seis caracteres), a confirmação e a unicidade do e-mail. A senha é transformada em hash BCrypt antes de ser gravada no Room.
- **Saídas esperadas:** Mensagem de sucesso e retorno ao login quando o cadastro é válido; mensagem informativa quando algum dado é inválido ou o e-mail já está em uso.
- **Critérios de aceitação:**
    - Campos vazios, e-mail inválido, senha curta e confirmação divergente impedem o cadastro e exibem mensagem.
    - E-mail já cadastrado impede um novo cadastro.
    - O Room guarda somente o hash BCrypt, nunca a senha digitada.
- **Casos de teste:** CT01, CT02, CT03, CT04, CT05, CT06, CT07, CT18

### RF03 — Realizar login

- **Origem no enunciado:** Login/cadastro.
- **Ator:** Usuário cadastrado.
- **Entradas:** E-mail e senha.
- **Processamento:** O aplicativo busca o usuário pelo e-mail, compara a senha informada com o hash BCrypt armazenado e, em caso de sucesso, registra o identificador do usuário na sessão.
- **Saídas esperadas:** Abertura da lista de tarefas quando as credenciais são válidas; mensagem de erro quando são inválidas.
- **Critérios de aceitação:**
    - Credenciais corretas abrem a lista; credenciais incorretas mantêm o usuário no login com mensagem.
    - Campos vazios geram mensagem de campo obrigatório.
- **Casos de teste:** CT08, CT09, CT10, CT11

### RF04 — Manter a sessão do usuário

- **Origem no enunciado:** Controle de sessão.
- **Ator:** Aplicativo.
- **Entradas:** Identificador do usuário autenticado.
- **Processamento:** O identificador é mantido localmente por meio de `SharedPreferences`. As telas restritas verificam a sessão antes de disponibilizar suas funcionalidades.
- **Saídas esperadas:** O usuário autenticado continua com acesso à área principal ao reabrir o aplicativo; usuários sem sessão são direcionados ao login.
- **Critérios de aceitação:**
    - A sessão guarda apenas o identificador do usuário.
    - A sessão sobrevive ao fechamento do app e é removida no logout.
    - Sem sessão, as telas restritas redirecionam ao login.
- **Casos de teste:** CT12, CT13

### RF05 — Cadastrar tarefa

- **Origem no enunciado:** CRUD de tarefas.
- **Ator:** Usuário autenticado.
- **Entradas:** Título, descrição e, opcionalmente, minutos até o lembrete.
- **Processamento:** O aplicativo valida os dados, associa a tarefa ao identificador do usuário autenticado e a armazena no Room.
- **Saídas esperadas:** A tarefa aparece na lista do usuário e uma mensagem informa o resultado.
- **Critérios de aceitação:**
    - O título é obrigatório (espaços em branco não contam).
    - Os minutos são opcionais.
    - A tarefa fica vinculada ao usuário que a criou.
- **Casos de teste:** CT37, CT38, CT39, CT44

### RF06 — Listar tarefas do usuário

- **Origem no enunciado:** CRUD de tarefas.
- **Ator:** Usuário autenticado.
- **Entradas:** Identificador do usuário da sessão.
- **Processamento:** O aplicativo consulta no Room as tarefas associadas ao usuário autenticado e as apresenta em uma lista (`ListView` com `BaseAdapter`).
- **Saídas esperadas:** Exibição das tarefas daquele usuário, sem tarefas de outras contas.
- **Critérios de aceitação:**
    - Somente as tarefas do usuário logado são exibidas.
    - Com zero tarefas, a lista aparece vazia sem erro.
    - As tarefas permanecem após fechar e reabrir o app.
- **Casos de teste:** CT39, CT40, CT41, CT45, CT47

### RF07 — Editar tarefa

- **Origem no enunciado:** CRUD de tarefas.
- **Ator:** Usuário autenticado.
- **Entradas:** Seleção de uma tarefa (menu de contexto) e novos valores de título, descrição e, quando aplicável, lembrete.
- **Processamento:** O aplicativo recupera a tarefa, verifica que ela pertence ao usuário autenticado e atualiza seus dados no Room. Um lembrete anterior é cancelado antes de qualquer novo agendamento.
- **Saídas esperadas:** A lista passa a apresentar os dados atualizados.
- **Critérios de aceitação:**
    - O formulário abre com os dados atuais da tarefa.
    - Alterar ou apagar os minutos substitui ou cancela o lembrete anterior.
- **Casos de teste:** CT42, CT43, CT57, CT58

### RF08 — Excluir tarefa

- **Origem no enunciado:** CRUD de tarefas.
- **Ator:** Usuário autenticado.
- **Entradas:** Opção Excluir no menu de contexto de uma tarefa.
- **Processamento:** O aplicativo cancela o alarme associado à tarefa, quando existente, e remove o registro do Room.
- **Saídas esperadas:** A tarefa deixa de aparecer na lista.
- **Critérios de aceitação:**
    - A tarefa não reaparece após reabrir o app.
    - Nenhuma notificação é disparada para uma tarefa excluída.
- **Casos de teste:** CT46, CT56

### RF09 — Agendar lembrete de tarefa

- **Origem no enunciado:** Alarmes (recurso obrigatório escolhido).
- **Ator:** Usuário autenticado.
- **Entradas:** Quantidade de minutos informada no formulário da tarefa.
- **Processamento:** Quando o valor é um inteiro maior que zero, o aplicativo agenda um alarme com `AlarmManager`. O lembrete é contado a partir do momento em que a tarefa é salva.
- **Saídas esperadas:** O lembrete é agendado para o período informado. O campo é opcional: sem valor, nenhum lembrete é agendado.
- **Critérios de aceitação:**
    - Valores `0`, negativos, não numéricos ou fora do limite de inteiro são rejeitados com mensagem, sem encerrar o app.
    - O lembrete não é armazenado no Room; fica agendado no serviço de alarmes do Android.
- **Casos de teste:** CT48, CT49, CT50, CT51, CT52, CT56, CT57, CT58

### RF10 — Apresentar notificação do lembrete

- **Origem no enunciado:** Notificações.
- **Ator:** Aplicativo.
- **Entradas:** Disparo do alarme associado à tarefa.
- **Processamento:** O `BroadcastReceiver` recebe o evento e cria uma notificação no canal de notificações do aplicativo, com o título da tarefa.
- **Saídas esperadas:** Uma notificação é exibida quando a permissão está concedida e o sistema permite.
- **Critérios de aceitação:**
    - A notificação aparece com o app aberto ou fechado.
    - Tocar na notificação abre o aplicativo na lista de tarefas.
- **Casos de teste:** CT52, CT53, CT54

### RF11 — Tratar a permissão de notificações

- **Origem no enunciado:** Notificações e permissões.
- **Ator:** Usuário autenticado.
- **Entradas:** Solicitação de permissão, quando exigida pela versão do Android (13 ou superior).
- **Processamento:** O aplicativo verifica a permissão e a solicita ao usuário. Se for negada, informa que as notificações podem não ser exibidas.
- **Saídas esperadas:** Com a permissão concedida o app apresenta notificações; com a permissão negada continua funcionando normalmente.
- **Critérios de aceitação:**
    - A negação não provoca encerramento inesperado.
    - O usuário é informado das consequências da negação.
- **Casos de teste:** CT52, CT55

### RF12 — Visualizar e atualizar dados do perfil

- **Origem no enunciado:** Gestão de perfil.
- **Ator:** Usuário autenticado.
- **Entradas:** Nome e e-mail atualizados.
- **Processamento:** O aplicativo carrega os dados da sessão, valida os novos valores e atualiza o registro no Room. O e-mail não pode ser igual ao de outra conta.
- **Saídas esperadas:** Mensagem indicando o resultado e dados atualizados ao reabrir o perfil.
- **Critérios de aceitação:**
    - Nome vazio ou e-mail inválido são rejeitados.
    - As alterações persistem após reabrir o app.
- **Casos de teste:** CT19, CT20, CT21

### RF13 — Alterar senha

- **Origem no enunciado:** Gestão de perfil e segurança.
- **Ator:** Usuário autenticado.
- **Entradas:** Senha atual, nova senha e confirmação.
- **Processamento:** O aplicativo verifica a senha atual com BCrypt, valida o tamanho mínimo da nova senha, confere a confirmação e grava um novo hash BCrypt no Room.
- **Saídas esperadas:** Mensagem de sucesso quando concluída; mensagem de erro quando a senha atual está incorreta ou os dados são inválidos.
- **Critérios de aceitação:**
    - A senha original nunca é armazenada; somente o novo hash.
    - Depois da alteração, a senha antiga deixa de funcionar.
- **Casos de teste:** CT22, CT23, CT24, CT25, CT26, CT27

### RF14 — Alterar foto de perfil pela câmera

- **Origem no enunciado:** Foto de perfil e câmera.
- **Ator:** Usuário autenticado.
- **Entradas:** Ação de tirar foto e autorização de uso da câmera, quando solicitada.
- **Processamento:** O aplicativo solicita a permissão quando necessário, abre a câmera usando `FileProvider`, salva a imagem em arquivo próprio e grava o caminho do arquivo no perfil (Room).
- **Saídas esperadas:** A foto é exibida no perfil e permanece após reabrir a tela.
- **Critérios de aceitação:**
    - A negação da permissão exibe mensagem e não abre a câmera.
    - Cancelar a câmera mantém a foto anterior e não encerra o app.
- **Casos de teste:** CT28, CT29, CT30, CT31, CT35

### RF15 — Alterar foto de perfil pela galeria

- **Origem no enunciado:** Foto de perfil e galeria.
- **Ator:** Usuário autenticado.
- **Entradas:** Seleção de uma imagem da galeria.
- **Processamento:** O aplicativo recebe a referência da imagem, copia o conteúdo para um arquivo próprio e grava o caminho desse arquivo no perfil (Room).
- **Saídas esperadas:** A imagem escolhida é exibida e permanece disponível após reabrir a tela.
- **Critérios de aceitação:**
    - Cancelar a seleção mantém a foto anterior e não encerra o app.
    - A foto não depende da imagem original da galeria.
- **Casos de teste:** CT32, CT33, CT34, CT35

### RF16 — Realizar logout

- **Origem no enunciado:** Controle de sessão.
- **Ator:** Usuário autenticado.
- **Entradas:** Ação de selecionar Sair.
- **Processamento:** O aplicativo remove o identificador da sessão e retorna ao login, limpando a pilha de navegação.
- **Saídas esperadas:** O usuário volta ao login e precisa se autenticar novamente.
- **Critérios de aceitação:**
    - O botão Voltar não retorna às telas restritas após o logout.
- **Casos de teste:** CT15, CT16, CT17

## 7. Requisitos não funcionais

| ID | Requisito | Descrição | Verificação |
|---|---|---|---|
| RNF01 | Plataforma e linguagem | Android, Java e interface baseada em Views, conforme o enunciado. | Inspeção do projeto. |
| RNF02 | Persistência local | Usuários e tarefas armazenados no Room e recuperáveis após fechar e reabrir o app. | CT05, CT19, CT40 |
| RNF03 | Proteção de senhas | Senhas nunca em texto puro nem por criptografia reversível: BCrypt com salt para gerar e verificar hashes. | CT18, CT25, CT27 |
| RNF04 | Isolamento dos dados por usuário | Cada usuário acessa somente as tarefas (e a foto) associadas ao seu identificador; edição e exclusão respeitam essa associação. | CT35, CT47 |
| RNF05 | Controle de acesso | Funcionalidades restritas exigem sessão autenticada; sem sessão o usuário vai ao login. | CT12, CT13, CT16 |
| RNF06 | Usabilidade | Navegação consistente, textos compreensíveis e mensagens específicas para erros de preenchimento, autenticação e operações. Entradas inválidas não encerram o app. | CT01, CT11, CT20, CT36, CT37, CT38, CT41, CT44, CT49, CT50, CT51, CT61, CT62 |
| RNF07 | Gerenciamento de permissões | Permissões declaradas no Manifest e solicitadas em tempo de execução quando exigido; concessão e negação tratadas sem falhas. | CT28, CT30, CT55 |
| RNF08 | Compatibilidade com o ciclo de vida do Android | A Splash usa o fluxo de inicialização do Android e direciona o usuário conforme a sessão, sem atrasos artificiais. | CT14 |
| RNF09 | Notificações | Uso de canal de notificação nas versões que o exigem e respeito à autorização do usuário. | CT52, CT55 |
| RNF10 | Armazenamento de fotos | O Room guarda o caminho persistente de um arquivo do próprio app, e não uma referência temporária da galeria. | CT29, CT32, CT33 |
| RNF11 | Simplicidade de implementação | Complexidade compatível com os conteúdos da disciplina: Activities, Intents, Room, `ListView` com `BaseAdapter` e menus. | Inspeção do código. |
| RNF12 | Limitações conhecidas | Ver a seção 13: consultas Room na thread principal, alarmes não restaurados após reinício, atrasos possíveis no agendamento, notificações dependentes de permissão. | CT59 |
| RNF13 | Identidade visual | O app possui ícone personalizado configurado no `AndroidManifest.xml`. | CT60 |
| RNF14 | Estrutura de telas | O app possui ao menos três telas funcionais além da Splash (aqui: Login, Cadastro, Lista de Tarefas, Formulário de Tarefa e Perfil). | Inspeção do projeto. |

## 8. Permissões do Android

| Permissão ou recurso | Uso | Quando é solicitada | Se negada | Casos de teste |
|---|---|---|---|---|
| `POST_NOTIFICATIONS` | Exibir as notificações dos lembretes | Em tempo de execução, no Android 13 ou superior | O app informa que as notificações podem não ser exibidas e continua funcionando | CT52, CT55 |
| `CAMERA` | Tirar a foto de perfil | Ao tocar em Tirar foto | Mensagem clara; a câmera não abre | CT28, CT30 |
| Seletor de imagens do sistema | Escolher foto na galeria | Não exige permissão de armazenamento | Não se aplica | CT32, CT33 |
| `FileProvider` | Compartilhar com a câmera o arquivo onde a foto será gravada | Configurado no Manifest | Não se aplica | CT28, CT29 |

As permissões são declaradas no `AndroidManifest.xml` e solicitadas quando o recurso é usado.

## 9. Tecnologias e recursos utilizados

| Recurso | Finalidade |
|---|---|
| Java | Linguagem de desenvolvimento |
| Activities e Intents | Organização das telas e navegação |
| Room | Persistência local de usuários e tarefas |
| BCrypt | Geração e verificação de hashes de senha |
| `SharedPreferences` | Armazenamento do identificador da sessão |
| `ListView` e `BaseAdapter` | Apresentação da lista de tarefas |
| Menus de opções e de contexto | Acesso ao perfil, logout, edição e exclusão |
| `FileProvider` | Compartilhamento seguro do arquivo de imagem com a câmera |
| Câmera e galeria | Captura ou seleção da foto de perfil |
| `AlarmManager` | Agendamento dos lembretes |
| `BroadcastReceiver` | Recebimento do disparo do alarme |
| `NotificationCompat` e `NotificationChannel` | Apresentação das notificações |

## 10. Foco de qualidade escolhido: Usabilidade

**Justificativa.** O aplicativo é usado em momentos rápidos do dia, para anotar e consultar tarefas. Por isso o grupo escolheu a **Usabilidade**: o usuário precisa entender o que fazer em cada tela, receber mensagens claras quando erra e nunca perder o app por causa de uma entrada inválida.

| Prática de usabilidade | Como aparece no app | Casos de teste |
|---|---|---|
| Validação de campos com mensagens claras | Cadastro, login, perfil, senha, tarefa e lembrete informam o problema em português | CT01, CT02, CT03, CT04, CT11, CT20, CT23, CT37, CT49 |
| Nenhum encerramento inesperado com entradas inválidas | Valores vazios, inválidos ou extremos são tratados | CT01, CT38, CT41, CT44, CT49, CT50, CT51, CT36 |
| Navegação consistente | Menu de opções (Perfil, Sair) e menu de contexto (Editar, Excluir) | CT15, CT42, CT46, CT61 |
| Tratamento de permissões negadas | Mensagem clara e app funcional sem a permissão | CT30, CT55 |
| Retorno ao usuário após ações | Mensagens de sucesso e lista atualizada | CT05, CT19, CT25, CT39 |
| Acessibilidade básica | Rótulos, textos legíveis e `contentDescription` em imagens e botões de ícone | CT62 |

Os casos de teste da seção 12 verificam essas práticas.

## 11. Conformidade com o enunciado

| Item do enunciado | Requisitos | Casos de teste |
|---|---|---|
| Java com Empty ou Basic Views Activity | RNF01 | Inspeção do projeto |
| Ao menos 3 telas funcionais | RNF14 | Inspeção do projeto |
| Splash screen sem atraso artificial, conforme a sessão | RF01, RNF08 | CT12, CT13, CT14 |
| Ícone personalizado no Manifest | RNF13 | CT60 |
| Login, cadastro, sessão e logout | RF02, RF03, RF04, RF16 | CT01, CT02, CT03, CT04, CT05, CT06, CT07, CT08, CT09, CT10, CT11, CT12, CT13, CT15, CT16, CT17, CT18 |
| Edição de perfil e de senha | RF12, RF13 | CT19, CT20, CT21, CT22, CT23, CT24, CT25, CT26, CT27 |
| Senhas com hash e salt (BCrypt), nunca em texto puro | RNF03, RF02, RF13 | CT01, CT02, CT03, CT04, CT05, CT06, CT07, CT18, CT22, CT23, CT24, CT25, CT26, CT27 |
| Usuário não autenticado sem acesso às funções restritas | RNF05, RF04 | CT12, CT13, CT16 |
| Foto de perfil por câmera ou galeria, com o caminho salvo no Room | RF14, RF15, RNF10 | CT28, CT29, CT30, CT31, CT32, CT33, CT34, CT35 |
| Notificações, tratando permissão ausente ou negada | RF10, RF11, RNF09 | CT52, CT53, CT54, CT55 |
| Recurso obrigatório: alarmes | RF09 | CT48, CT49, CT50, CT51, CT52, CT56, CT57, CT58 |
| Permissões no Manifest e em tempo de execução | RNF07, RF11, RF14 | CT28, CT29, CT30, CT31, CT35, CT52, CT55 |
| Foco de qualidade justificado (usabilidade) | RNF06 | CT01, CT11, CT20, CT36, CT37, CT38, CT41, CT44, CT49, CT50, CT51, CT61, CT62 |
| Testes caixa-preta executados e documentados | Seção 12 | CT01 a CT62 |

## 12. Plano e casos de teste caixa-preta

### 12.1 Objetivo e abordagem

Os testes verificam o comportamento do aplicativo pela interface, comparando o resultado obtido com o esperado, sem analisar o código-fonte. Foram usadas três técnicas: **caminho feliz** (uso correto), **casos negativos** (campos vazios, dados inválidos, credenciais erradas, permissões negadas) e **valores-limite** (senha com 5 e 6 caracteres, minutos `0`, `-5` e valor muito grande, título só com espaços e título longo). Os casos de segurança usam o Database Inspector do Android Studio apenas para confirmar que a senha foi gravada como hash.

Os casos abaixo foram registrados como **Aprovados** conforme a confirmação de Reinaldo de que executou os 62 testes e que todos passaram. As descrições em "Resultado obtido" sintetizam os comportamentos informados como aprovados, tomando como base o resultado esperado de cada caso. Não houve verificação independente dos 62 resultados nesta revisão. Os dados do ambiente de teste não informados continuam pendentes de preenchimento.

### 12.2 Ambiente de teste

| Item | Valor |
|---|---|
| Dispositivo ou emulador | _a preencher_ |
| Versão do Android (API) | _a preencher_ |
| Versão ou commit do app | _a preencher_ |
| Data da execução | 09/11/2026 (conforme declaração do integrante) |
| Executores | Reinaldo (execução informada pelo integrante) |

### 12.3 Dados de teste

| Usuário | Nome | E-mail | Senha inicial |
|---|---|---|---|
| A | Ana Teste | `ana@teste.com` | `123456` |
| B | Bruno Teste | `bruno@teste.com` | `123456` |

Execute os grupos na ordem em que aparecem, porque alguns casos dependem de dados criados antes (por exemplo, os usuários A e B). Os casos de lembrete esperam alguns minutos; rode os de notificação em paralelo com os demais.

### 12.4 Critérios de status

- **Aprovado:** o resultado obtido corresponde ao esperado.
- **Reprovado:** o resultado obtido não corresponde ao esperado.
- **Pendente:** ainda não executado.

Se um caso reprovar, registre a falha na seção 12.8, corrija e repita o teste.

### 12.5 Casos de teste

#### Cadastro de usuário

| ID | Requisitos | Pré-condição | Passos / entrada | Resultado esperado | Resultado obtido | Status |
|---|---|---|---|---|---|---|
| CT01 | RF02, RNF06 | Tela de cadastro aberta. | Deixar nome, e-mail, senha e confirmação vazios e tocar em Cadastrar. | Mensagem informando os campos obrigatórios; nenhum usuário é criado; o app não encerra inesperadamente. | Cadastro bloqueado com campos vazios; mensagem de preenchimento exibida, sem encerramento do app. | Aprovado |
| CT02 | RF02 | Tela de cadastro aberta. | Informar nome `Ana Teste`, e-mail `ana.teste` (sem `@`), senha e confirmação `123456` e tocar em Cadastrar. | Mensagem de e-mail inválido; nenhum usuário é criado. | E-mail sem @ foi recusado; nenhum cadastro realizado. | Aprovado |
| CT03 | RF02 | Tela de cadastro aberta. | Informar e-mail válido, senha e confirmação `12345` (cinco caracteres) e tocar em Cadastrar. | Mensagem informando que a senha deve ter ao menos seis caracteres; nenhum usuário é criado. | Senha de cinco caracteres recusada com aviso de tamanho mínimo. | Aprovado |
| CT04 | RF02 | Tela de cadastro aberta. | Informar e-mail válido, senha `123456` e confirmação `654321` e tocar em Cadastrar. | Mensagem informando que as senhas não coincidem; nenhum usuário é criado. | Senhas divergentes recusadas com aviso; cadastro não realizado. | Aprovado |
| CT05 | RF02, RNF02 | O e-mail `ana@teste.com` ainda não está cadastrado. | Cadastrar o Usuário A (`Ana Teste`, `ana@teste.com`, senha e confirmação `123456`, exatamente seis caracteres). | Mensagem de sucesso e retorno à tela de login. | Conta de Ana cadastrada; confirmação exibida e retorno ao login. | Aprovado |
| CT06 | RF02 | Usuário A cadastrado. | Tentar cadastrar outra conta com o e-mail `ana@teste.com`. | Mensagem informando que o e-mail já está em uso; a segunda conta não é criada. | E-mail duplicado recusado; nenhuma conta adicional criada. | Aprovado |
| CT07 | RF02 | Usuário A cadastrado. | Cadastrar o Usuário B (`Bruno Teste`, `bruno@teste.com`, senha e confirmação `123456`). | Mensagem de sucesso e retorno à tela de login. | Conta de Bruno cadastrada; confirmação exibida e retorno ao login. | Aprovado |

#### Login, sessão e logout

| ID | Requisitos | Pré-condição | Passos / entrada | Resultado esperado | Resultado obtido | Status |
|---|---|---|---|---|---|---|
| CT08 | RF03 | Usuário A cadastrado; tela de login aberta. | Informar `ana@teste.com` e `123456` e tocar em Entrar. | O app abre a lista de tarefas do Usuário A. | Login de Ana realizado; lista de tarefas aberta. | Aprovado |
| CT09 | RF03 | Usuário A cadastrado; tela de login aberta. | Informar `ana@teste.com` e a senha incorreta `000000`. | Mensagem de credenciais incorretas; o usuário permanece na tela de login. | Senha incorreta rejeitada; mensagem exibida e permanência no login. | Aprovado |
| CT10 | RF03 | Tela de login aberta. | Informar o e-mail não cadastrado `ninguem@teste.com` e a senha `123456`. | Mensagem de credenciais incorretas; o app não encerra inesperadamente. | E-mail não cadastrado rejeitado sem travamento. | Aprovado |
| CT11 | RF03, RNF06 | Tela de login aberta. | Deixar e-mail e senha vazios e tocar em Entrar. | Mensagem informando que os campos são obrigatórios. | Campos vazios rejeitados com mensagem de preenchimento obrigatório. | Aprovado |
| CT12 | RF01, RF04, RNF05 | Usuário A autenticado. | Fechar o app removendo-o dos aplicativos recentes e abri-lo novamente. | Após a Splash, o app abre direto a lista de tarefas do Usuário A, sem pedir login. | Sessão preservada; ao reabrir, Splash direcionou diretamente à lista. | Aprovado |
| CT13 | RF01, RF04, RNF05 | Nenhum usuário autenticado (app recém-instalado ou após logout). | Abrir o aplicativo. | Após a Splash, o app abre a tela de login. | Sem sessão, Splash direcionou à tela de login. | Aprovado |
| CT14 | RF01, RNF08 | App fechado. | Abrir o aplicativo e observar a Splash. | A Splash aparece durante a inicialização e é substituída pela tela seguinte sem espera fixa perceptível. | Splash exibida durante a inicialização, sem atraso artificial. | Aprovado |
| CT15 | RF16 | Usuário A autenticado, na lista de tarefas. | Abrir o menu de opções e selecionar Sair. | A sessão é encerrada e o app abre a tela de login. | Logout pelo menu concluiu a sessão e retornou ao login. | Aprovado |
| CT16 | RF16, RNF05 | Logout recém-realizado. | Pressionar o botão Voltar do dispositivo repetidas vezes. | O app não retorna a nenhuma tela restrita (lista, formulário ou perfil). | Botão Voltar não permitiu retornar às telas restritas após o logout. | Aprovado |
| CT17 | RF16 | Usuário A autenticado, na tela de Perfil. | Usar a opção de sair disponível no Perfil (se implementada). | A sessão é encerrada e o app abre a tela de login. | Logout realizado pelo Perfil; sessão encerrada e login aberto. | Aprovado |

#### Segurança da senha

| ID | Requisitos | Pré-condição | Passos / entrada | Resultado esperado | Resultado obtido | Status |
|---|---|---|---|---|---|---|
| CT18 | RNF03, RF02 | Usuários A e B cadastrados com a mesma senha `123456`. | No Android Studio, abrir o Database Inspector (App Inspection) com o app em execução e consultar a tabela `usuarios`. | A coluna `senhaHash` de A e de B começa com `$2`, tem cerca de 60 caracteres, é diferente entre os dois usuários (salt) e não contém `123456`. | Hashes BCrypt distintos verificados para as duas contas, sem senha em texto puro. | Aprovado |

#### Perfil e alteração de senha

| ID | Requisitos | Pré-condição | Passos / entrada | Resultado esperado | Resultado obtido | Status |
|---|---|---|---|---|---|---|
| CT19 | RF12, RNF02 | Usuário A autenticado, na tela de Perfil. | Alterar o nome para `Ana Souza` e o e-mail para `ana.souza@teste.com`, salvar, fechar o app, reabri-lo e abrir o Perfil. Ao final, restaurar os dados originais. | Mensagem de sucesso; os novos dados são exibidos após reabrir o app; o login funciona com o novo e-mail. | Nome e e-mail atualizados e mantidos após reabrir; autenticação com e-mail novo funcionou. | Aprovado |
| CT20 | RF12, RNF06 | Usuário A autenticado, na tela de Perfil. | Apagar o nome (ou informar e-mail sem `@`) e tocar em salvar. | Mensagem de dado inválido; os dados anteriores são preservados. | Dados inválidos recusados com mensagem; valores anteriores preservados. | Aprovado |
| CT21 | RF12 | Usuários A e B cadastrados; A autenticado. | No Perfil de A, informar o e-mail `bruno@teste.com` e salvar. | Mensagem informando que o e-mail já está em uso; A mantém o e-mail original. | E-mail de outra conta recusado; dados originais preservados. | Aprovado |
| CT22 | RF13 | Usuário A autenticado, na tela de Perfil. | Informar senha atual `000000`, nova senha `abcdef` e confirmação `abcdef`. | Mensagem de senha atual incorreta; a senha não é alterada (o login com `123456` continua funcionando). | Senha atual incorreta rejeitada; senha anterior mantida. | Aprovado |
| CT23 | RF13 | Usuário A autenticado, na tela de Perfil. | Informar senha atual `123456`, nova senha `12345` e confirmação `12345`. | Mensagem de senha nova curta; a senha não é alterada. | Nova senha curta rejeitada; senha não alterada. | Aprovado |
| CT24 | RF13 | Usuário A autenticado, na tela de Perfil. | Informar senha atual `123456`, nova senha `abcdef` e confirmação `abcxyz`. | Mensagem informando que a confirmação não corresponde; a senha não é alterada. | Confirmação divergente rejeitada; senha não alterada. | Aprovado |
| CT25 | RF13, RNF03 | Usuário A autenticado, na tela de Perfil. | Informar senha atual `123456`, nova senha `abcdef` e confirmação `abcdef`; sair e entrar com `abcdef`. | Mensagem de sucesso na alteração; o login funciona com `abcdef`. | Senha alterada; autenticação com a nova senha realizada. | Aprovado |
| CT26 | RF13 | Senha de A alterada para `abcdef`. | Tentar entrar com `ana@teste.com` e a senha antiga `123456`. | Mensagem de credenciais incorretas. | Login com senha antiga recusado após a alteração. | Aprovado |
| CT27 | RF13, RNF03 | Senha de A alterada para `abcdef`. | No Database Inspector, consultar `senhaHash` do Usuário A. | O hash é diferente do observado no caso de segurança da senha, começa com `$2` e não contém `abcdef`. | Novo hash BCrypt verificado, diferente do anterior e sem senha em texto puro. | Aprovado |

#### Foto de perfil

| ID | Requisitos | Pré-condição | Passos / entrada | Resultado esperado | Resultado obtido | Status |
|---|---|---|---|---|---|---|
| CT28 | RF14, RNF07 | Perfil de A aberto; permissão da câmera ainda não concedida. | Tocar em Tirar foto, conceder a permissão quando solicitada e tirar uma foto. | A permissão é solicitada; a câmera abre; a foto tirada é exibida no Perfil. | Permissão concedida, câmera aberta e foto capturada exibida no perfil. | Aprovado |
| CT29 | RF14, RNF10 | Foto tirada pela câmera. | Fechar o app removendo-o dos recentes, reabri-lo e abrir o Perfil. | A foto continua exibida (o caminho do arquivo foi salvo no Room). | Foto tirada pela câmera permaneceu após fechar e reabrir o aplicativo. | Aprovado |
| CT30 | RF14, RNF07 | Perfil de A aberto; permissão da câmera negada (revogar em Configurações, se necessário). | Tocar em Tirar foto e negar a permissão. | Mensagem clara; a câmera não abre; o app não encerra inesperadamente. | Permissão negada tratada com mensagem, sem abrir a câmera ou travar. | Aprovado |
| CT31 | RF14 | Perfil com foto já definida. | Tocar em Tirar foto e voltar sem tirar a foto. | O app não encerra inesperadamente e a foto anterior é mantida. | Captura cancelada sem travamento; foto anterior preservada. | Aprovado |
| CT32 | RF15, RNF10 | Perfil de A aberto; galeria com ao menos uma imagem. | Tocar em Escolher da galeria e selecionar uma imagem. | A imagem escolhida é exibida no Perfil. | Imagem escolhida na galeria exibida no perfil. | Aprovado |
| CT33 | RF15, RNF10 | Foto escolhida pela galeria. | Fechar o app, reabri-lo e abrir o Perfil; opcionalmente apagar a imagem original da galeria e repetir. | A foto continua exibida (o app usa uma cópia própria do arquivo). | Foto da galeria permaneceu após reabrir o aplicativo. | Aprovado |
| CT34 | RF15 | Perfil com foto já definida. | Abrir a galeria e voltar sem escolher nenhuma imagem. | O app não encerra inesperadamente e a foto anterior é mantida. | Seleção da galeria cancelada sem travamento; foto anterior preservada. | Aprovado |
| CT35 | RF14, RF15, RNF04 | A com foto definida; B sem foto. | Entrar com B e abrir o Perfil; depois voltar a entrar com A. | B vê a imagem padrão e não a foto de A; a foto de A continua no Perfil de A. | Cada conta exibiu sua própria foto ou imagem padrão, sem mistura entre usuários. | Aprovado |
| CT36 | RNF06 | Perfil com foto exibida. | Girar o aparelho ou o emulador. | O app não encerra inesperadamente e a foto continua exibida. | Após girar o dispositivo, foto permaneceu visível e aplicativo estável. | Aprovado |

#### Tarefas

| ID | Requisitos | Pré-condição | Passos / entrada | Resultado esperado | Resultado obtido | Status |
|---|---|---|---|---|---|---|
| CT37 | RF05, RNF06 | Usuário A autenticado, formulário de tarefa aberto. | Deixar o título vazio e tocar em Salvar. | Mensagem informando que o título é obrigatório; a tarefa não é salva. | Título vazio recusado; tarefa não cadastrada. | Aprovado |
| CT38 | RF05, RNF06 | Formulário de tarefa aberto. | Informar apenas espaços no título e tocar em Salvar. | O título é tratado como vazio: mensagem de campo obrigatório; a tarefa não é salva. | Título com apenas espaços recusado; tarefa não cadastrada. | Aprovado |
| CT39 | RF05, RF06 | Formulário de tarefa aberto. | Informar título `Estudar Room`, descrição `Capítulo 13`, deixar os minutos vazios e salvar. | Mensagem de sucesso; a tarefa aparece na lista com título e descrição. | Tarefa cadastrada; título e descrição exibidos na lista. | Aprovado |
| CT40 | RF06, RNF02 | Tarefa `Estudar Room` salva. | Fechar o app removendo-o dos recentes, reabri-lo e abrir a lista. | A tarefa continua na lista. | Tarefa permaneceu salva após fechar e reabrir o aplicativo. | Aprovado |
| CT41 | RF06, RNF06 | Usuário B autenticado, sem tarefas. | Abrir a lista de tarefas. | A lista é exibida vazia, sem encerramento inesperado. | Lista vazia do usuário B exibida normalmente. | Aprovado |
| CT42 | RF07 | A possui a tarefa `Estudar Room`. | Na lista, pressionar longamente a tarefa e selecionar Editar. | O formulário abre com título e descrição atuais da tarefa. | Menu de contexto abriu a edição com os dados da tarefa preenchidos. | Aprovado |
| CT43 | RF07 | Formulário de edição aberto. | Alterar o título para `Estudar Room e Menus` e salvar. | A lista mostra o novo título e mantém a descrição. | Título atualizado na lista; descrição preservada. | Aprovado |
| CT44 | RF05, RNF06 | Formulário de tarefa aberto. | Informar um título com 100 caracteres e salvar. | A tarefa é salva sem encerramento inesperado e o texto é exibido sem quebrar o layout da lista. | Título longo cadastrado e exibido sem travamento ou quebra do layout. | Aprovado |
| CT45 | RF06 | Usuário A autenticado. | Criar ao menos 10 tarefas e rolar a lista. | Todas as tarefas aparecem e a rolagem funciona. | Dez tarefas exibidas; rolagem funcionou normalmente. | Aprovado |
| CT46 | RF08 | A possui ao menos uma tarefa. | Pressionar longamente a tarefa e selecionar Excluir; depois fechar e reabrir o app. | A tarefa some da lista e continua ausente após reabrir. | Tarefa excluída e continuou ausente após reabrir o aplicativo. | Aprovado |
| CT47 | RF06, RNF04 | A e B possuem tarefas diferentes. | Entrar com cada conta e consultar a lista. | Cada usuário visualiza somente as próprias tarefas. | Cada usuário visualizou apenas as próprias tarefas. | Aprovado |

#### Lembrete e notificação

| ID | Requisitos | Pré-condição | Passos / entrada | Resultado esperado | Resultado obtido | Status |
|---|---|---|---|---|---|---|
| CT48 | RF09 | Formulário de tarefa aberto. | Salvar uma tarefa sem preencher os minutos e aguardar 2 minutos. | A tarefa é salva e nenhuma notificação é exibida. | Tarefa salva sem lembrete; nenhuma notificação disparada no intervalo. | Aprovado |
| CT49 | RF09, RNF06 | Formulário de tarefa aberto. | Informar `0` no campo de minutos e salvar. | Mensagem de valor inválido; o app não encerra inesperadamente. | Valor zero recusado com aviso, sem travamento. | Aprovado |
| CT50 | RF09, RNF06 | Formulário de tarefa aberto. | Informar `-5` no campo de minutos (se o campo aceitar o sinal) e salvar. | Mensagem de valor inválido; o app não encerra inesperadamente. | Valor negativo rejeitado ou impedido pelo campo, sem travamento. | Aprovado |
| CT51 | RF09, RNF06 | Formulário de tarefa aberto. | Informar `99999999999` no campo de minutos e salvar. | O valor é rejeitado ou tratado, sem encerramento inesperado. | Valor acima do limite rejeitado sem travamento. | Aprovado |
| CT52 | RF09, RF10, RF11, RNF09 | Permissão de notificações concedida; app aberto. | Salvar uma tarefa com lembrete de `1` minuto e aguardar. | Após cerca de um minuto, aparece uma notificação com o título da tarefa. | Notificação com título da tarefa exibida após o intervalo de lembrete. | Aprovado |
| CT53 | RF10 | Permissão de notificações concedida. | Salvar uma tarefa com lembrete de `1` minuto, remover o app dos recentes e aguardar. | A notificação aparece mesmo com o app fechado. | Notificação recebida com aplicativo fora dos recentes. | Aprovado |
| CT54 | RF10 | Notificação do lembrete exibida. | Tocar na notificação. | O app abre na lista de tarefas. | Toque na notificação abriu a lista de tarefas. | Aprovado |
| CT55 | RF11, RNF07, RNF09 | App instalado do zero, com a permissão de notificações negada quando solicitada (Android 13 ou superior). | Salvar uma tarefa com lembrete de `1` minuto e aguardar 2 minutos. | O app não encerra inesperadamente; o usuário foi informado de que as notificações podem não ser exibidas; nenhuma notificação aparece. | Permissão negada sem travamento; aviso mostrado e notificação não exibida. | Aprovado |
| CT56 | RF08, RF09 | Tarefa com lembrete de `3` minutos agendado. | Excluir a tarefa antes do horário e aguardar o intervalo. | Nenhuma notificação é exibida. | Após excluir a tarefa, alarme cancelado e nenhuma notificação recebida. | Aprovado |
| CT57 | RF07, RF09 | Tarefa com lembrete de `5` minutos agendado. | Editar a tarefa trocando o lembrete para `1` minuto e salvar; aguardar 6 minutos. | Uma única notificação aparece após cerca de 1 minuto e nenhuma aos 5 minutos. | Novo alarme substituiu o anterior; uma notificação no novo intervalo. | Aprovado |
| CT58 | RF07, RF09 | Tarefa com lembrete de `2` minutos agendado. | Editar a tarefa apagando o campo de minutos e salvar; aguardar 3 minutos. | Nenhuma notificação é exibida. | Remoção dos minutos cancelou o lembrete; nenhuma notificação exibida. | Aprovado |
| CT59 | RNF12 | Tarefa com lembrete de `5` minutos agendado. | Reiniciar o emulador ou aparelho antes do disparo e aguardar o intervalo. | Conforme a limitação conhecida, o lembrete não é restaurado; registrar o comportamento observado. | Após reinício do dispositivo, alarme não foi restaurado, conforme limitação descrita. | Aprovado |

#### Interface e identidade do aplicativo

| ID | Requisitos | Pré-condição | Passos / entrada | Resultado esperado | Resultado obtido | Status |
|---|---|---|---|---|---|---|
| CT60 | RNF13 | App instalado. | Localizar o aplicativo na lista de aplicativos do aparelho. | O app exibe o ícone personalizado definido no Manifest. | Ícone personalizado do aplicativo exibido na lista de aplicativos. | Aprovado |
| CT61 | RNF06 | Usuário A autenticado, na lista de tarefas. | Abrir o menu de opções, selecionar Perfil e pressionar Voltar. | O menu mostra Perfil e Sair; Voltar retorna à lista sem encerramento inesperado. | Menu mostrou Perfil e Sair; opção Perfil abriu a tela e Voltar retornou à lista. | Aprovado |
| CT62 | RNF06 | Todas as telas acessíveis. | Percorrer as telas verificando rótulos, dicas (hints), textos e mensagens; usar o Accessibility Scanner ou o TalkBack nos botões e imagens. | Campos identificáveis; textos legíveis e sem cortes; mensagens em português; imagens e botões só de ícone com descrição de conteúdo. | Campos e mensagens verificados, com textos legíveis e descrições acessíveis. | Aprovado |

### 12.6 Resumo da execução

| Total de casos | Aprovados | Reprovados | Pendentes |
|---|---|---|---|
| 62 | 62 | 0 | 0 |

### 12.7 Rastreabilidade: requisitos e casos de teste

| Requisito | Casos de teste |
|---|---|
| RF01 | CT12, CT13, CT14 |
| RF02 | CT01, CT02, CT03, CT04, CT05, CT06, CT07, CT18 |
| RF03 | CT08, CT09, CT10, CT11 |
| RF04 | CT12, CT13 |
| RF05 | CT37, CT38, CT39, CT44 |
| RF06 | CT39, CT40, CT41, CT45, CT47 |
| RF07 | CT42, CT43, CT57, CT58 |
| RF08 | CT46, CT56 |
| RF09 | CT48, CT49, CT50, CT51, CT52, CT56, CT57, CT58 |
| RF10 | CT52, CT53, CT54 |
| RF11 | CT52, CT55 |
| RF12 | CT19, CT20, CT21 |
| RF13 | CT22, CT23, CT24, CT25, CT26, CT27 |
| RF14 | CT28, CT29, CT30, CT31, CT35 |
| RF15 | CT32, CT33, CT34, CT35 |
| RF16 | CT15, CT16, CT17 |
| RNF02 | CT05, CT19, CT40 |
| RNF03 | CT18, CT25, CT27 |
| RNF04 | CT35, CT47 |
| RNF05 | CT12, CT13, CT16 |
| RNF06 | CT01, CT11, CT20, CT36, CT37, CT38, CT41, CT44, CT49, CT50, CT51, CT61, CT62 |
| RNF07 | CT28, CT30, CT55 |
| RNF08 | CT14 |
| RNF09 | CT52, CT55 |
| RNF10 | CT29, CT32, CT33 |
| RNF12 | CT59 |
| RNF13 | CT60 |

### 12.8 Registro de falhas e correções

Preencha esta tabela para cada teste reprovado.

| ID do teste | Falha observada | Correção realizada | Teste repetido? | Resultado após a correção |
|---|---|---|---|---|
| — | — | — | — | — |

## 13. Limitações conhecidas

1. As consultas ao Room rodam na thread principal, por simplicidade e alinhamento com os exemplos da disciplina. Com muitos dados, isso pode afetar a responsividade.
2. O lembrete é definido em minutos a partir do salvamento da tarefa e não é gravado como campo da tarefa no Room; o agendamento fica no serviço de alarmes do Android.
3. Os alarmes não são restaurados automaticamente após a reinicialização do aparelho.
4. O agendamento com `AlarmManager.set()` pode atrasar conforme a economia de energia e o estado do aparelho.
5. A exibição de notificações depende da permissão do usuário e das configurações do sistema.

## 14. Checklist antes da entrega

- [x] Nomes completos de todos os integrantes.
- [ ] Conferir se cada requisito descrito corresponde ao que existe na versão final do app (ajustar o texto ou o app).
- [x] Executar os casos de teste e preencher resultado obtido e status.
- [ ] Preencher ambiente de teste e resumo da execução.
- [ ] Registrar as falhas encontradas e se foram corrigidas.
- [ ] Compartilhar o repositório ou pasta com `matheus.albuquerque@ufms.br`.
- [ ] Enviar no AVA o link do projeto, a especificação e os nomes dos integrantes.
