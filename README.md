# robsso-hex

Este projeto é uma Prova de Conceito (POC) para autenticação SSO utilizando arquitetura hexagonal. Por ser uma POC, não foram implementados testes unitários. 

## Importância dos Testes Unitários

Apesar de não haver testes unitários nesta POC, é fundamental ressaltar que testes automatizados são essenciais para garantir a qualidade, segurança e evolução sustentável de sistemas em produção. Eles ajudam a prevenir regressões, facilitam refatorações e aumentam a confiança nas entregas.

## Dependências Externas

### Dependência do Microserviço de E-mail

Este projeto depende do microserviço [email-service](https://github.com/robsonroch/email-service) para envio de e-mails relacionados a autenticação e alteração de senha. Certifique-se de que o serviço esteja em execução e corretamente configurado para integração.

### DatabaseProperties e AWS Secrets Manager

A configuração do banco de dados (`DatabaseProperties`) depende da busca de segredos no AWS Secrets Manager. Para facilitar o desenvolvimento local, utilize o [LocalStack](https://github.com/localstack/localstack) via Docker Compose.

#### Como simular o Secrets Manager e Redis com Docker Compose

1. Suba os serviços necessários (LocalStack e Redis) usando o Docker Compose já configurado no projeto:
   ```bash
   docker-compose -f docker-compose-infra.yml -p localstack up -d
   ```
2. Crie um segredo no LocalStack:
   ```bash
   aws --endpoint-url=http://localhost:4566 secretsmanager create-secret \
     --name mysql-secret \
     --secret-string '{"url":"jdbc:mysql://localhost:3306/db","username":"user","password":"pass"}'
   ```
3. O projeto irá buscar o segredo usando o endpoint do LocalStack já configurado em `SecretManagerConfiguration`.

### Redis

O projeto utiliza Redis para armazenamento temporário de tokens. O serviço Redis já será iniciado junto com o LocalStack pelo Docker Compose:

- Host: `localhost`
- Porta: `6380` (mapeada para a porta padrão `6379` do container Redis)

## Observações

- Este projeto é apenas uma POC e não deve ser utilizado em produção sem os devidos testes e validações.
- Para mais detalhes sobre a configuração de LocalStack, consulte a [documentação oficial](https://docs.localstack.cloud/).
- Para parar e remover os containers, utilize:
  ```bash
  docker-compose -f docker-compose-infra.yml -p localstack down
  ```
