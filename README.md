# API Order Management B2B

- [Java](https://openjdk.org)
- [Spring Boot](https://spring.io)
- [PostgreSQL](https://postgresql.org)
- [Docker](https://docker.com)

Microsserviço de alta performance projetado para o ecossistema B2B, encarregado pelo recebimento, processamento, gerenciamento de pedidos e controle transacional do limite de crédito de parceiros comerciais.

---

## 1. Como Inicializar o Projeto

### Pré-requisitos e Instalação do Docker

*   **Windows:**
  1. Baixe o instalador oficial do [Docker Desktop para Windows](https://docker.com).
  2. Reinicie o computador e abra o Docker Desktop para iniciar o serviço.

*   **Linux (Ubuntu/Debian):**
    Abra o terminal e execute os comandos abaixo para instalar o motor do Docker e o Docker Compose:
    ```bash
    sudo apt update && sudo apt install docker.io docker-compose -y
    sudo systemctl enable --now docker
    ```

### Subir a Infraestrutura Inteira
Abra o terminal na pasta raiz do projeto (onde está o arquivo `docker-compose.yaml`) e execute o comando abaixo para realizar o build e subir os containers em segundo plano:
```bash
docker-compose up -d
```

*   **Verificar Logs:** `docker-compose logs -f app`
*   **Derrubar os Serviços:** `docker-compose down`

---

## 2. Mapa de Endpoints e cURLS

### 1. OrderController (`/api/orders`)
Gerencia o fluxo de criação, transição de estados, cancelamento e relatórios de auditoria dos pedidos de venda.

*   **Endpoint 1: Criar um Novo Pedido (Fluxo Idempotente):**
    ```bash
        curl --request POST \
            --url http://localhost:8000/api/orders \
            --header 'Content-Type: application/json' \
            --header 'User-Agent: insomnia/12.6.0' \
            --header 'x-idempotency-key: 1dd564fd-7e83-441b-aef4-70be9558f950' \
            --data '{
                    "partnerId": 1,
                    "items": [
                    {
                        "product": "Notebook Dell Latitude",
                        "quantity": 2,
                        "unitPrice": 4500.00
                    },
                    {
                        "product": "Monitor LG 29",
                        "quantity": 1,
                        "unitPrice": 1200.00    
                    }
                            ]
                    }'
    ```

*   **Endpoint 2: Atualizar Status do Pedido:**
    ```bash
         curl --request PATCH \
          --url http://localhost:8000/api/orders/1/status \
          --header 'Content-Type: application/json' \
          --header 'User-Agent: insomnia/12.6.0' \
          --data '{
              "status": "APPROVED"
            }'
    ```

*   **Endpoint 3: Cancelar um Pedido:**
    ```bash
        curl --request POST \
          --url http://localhost:8000/api/orders/1/cancel \
          --header 'Content-Type: application/json' \
          --header 'User-Agent: insomnia/12.6.0'
    ```

*   **Endpoint 4: Buscar Pedido por ID com Itens:**
    ```bash
        curl --request GET \
          --url http://localhost:8000/api/orders/2 \
          --header 'Content-Type: application/json' \
          --header 'User-Agent: insomnia/12.6.0'
    ```

*   **Endpoint 5: Listar Pedidos Filtrados por Status (Com Paginação):**
    ```bash
        curl --request GET \
          --url 'http://localhost:8000/api/orders/period?start=2026-06-04T00%3A00%3A00&end=2026-06-09T23%3A59%3A59&page=0&size=2&sort=createdAt%2Cdesc' \
          --header 'Content-Type: application/json' \
          --header 'User-Agent: insomnia/12.6.0'
    ```

*   **Endpoint 6: Buscar Pedidos por Período de Data/Hora (Com Paginação):**
    ```bash
      curl --request GET \
          --url 'http://localhost:8000/api/orders/status/CANCELED?page=0&size=2&sort=createdAt%2Cdesc' \
          --header 'Content-Type: application/json' \
          --header 'User-Agent: insomnia/12.6.0'
    ```

---

### 2. PartnerController (`/api/partners`)
Endpoints encarregados pelo gerenciamento cadastral e consulta de saldos dos parceiros comerciais.

*   **Cadastrar um Novo Parceiro Comercial B2B:**
    ```bash
        curl --request POST \
            --url http://localhost:8000/api/partners \
            --header 'Content-Type: application/json' \
            --header 'User-Agent: insomnia/12.6.0' \
            --data '{
            "name": "Empresa X",
            "creditLimit": 75000.00
            }'
    ```

*   **Consultar Dados e Limite de Crédito por ID:**
    ```bash
    curl --request GET \
        --url http://localhost:8000/api/partners/1 \
        --header 'Content-Type: application/json' \
        --header 'User-Agent: insomnia/12.6.0'
    ```

*   **Listar Todos os Parceiros Cadastrados:**
    ```bash
    curl --request GET \
        --url http://localhost:8000/api/partners \
        --header 'Content-Type: application/json' \
        --header 'User-Agent: insomnia/12.6.0'
    ```

---

## 3. Motivações Técnicas e Arquitetura

O sistema foi desenhado para sustentar cenários de concorrência massiva e garantir a integridade financeira das transações.

*   **Lock Pessimista (`PESSIMISTIC_WRITE`):** Para evitar que requisições simultâneas efetuadas no mesmo milissegundo pelo mesmo parceiro burlem o saldo de crédito (condição de corrida), utilizamos o mecanismo `SELECT ... FOR UPDATE` via Hibernate. A primeira thread bloqueia a linha do parceiro no PostgreSQL, colocando as requisições concorrentes em fila ordenada.
*   **Estratégia de Carga Eficiente (`FetchType.LAZY` / `JOIN FETCH`):** Mapeamos os itens dos pedidos como `LAZY` para evitar consumo desnecessário de memória JVM. Quando precisamos consultar os detalhes de um pedido, utilizamos consultas customizadas com `JOIN FETCH`, reduzindo as idas ao banco de dados para apenas uma viagem de rede (evitando o problema N+1).
*   **Mecanismo de Idempotência:** A aplicação obriga o envio do cabeçalho `X-Idempotency-Key` no fluxo de criação de pedidos. Chaves duplicadas disparam uma `BusinessException`, blindando o sistema contra reenvios acidentais e pagamentos duplicados.
*   **Mensageria e Eventos Assíncronos (`@Async`):** O disparo de notificações de atualização de status para sistemas terceiros ocorre de forma não-bloqueante através de pools de threads secundárias. Isso libera o ciclo de vida principal da requisição HTTP de imediato, reduzindo o tempo de resposta percebido pelo cliente.
