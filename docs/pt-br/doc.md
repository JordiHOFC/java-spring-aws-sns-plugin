# **Java Spring AWS SNS Plugin**

Plugin Java Spring AWS SNS é um conjunto de técnologias e metodologia de desenvolvimento que juntos auxiliam na utilização do Amazon SNS serviço de notificação em Aplicações Java Spring Boot. 

Este Plugin possui suporte para projetos criados junto a Stack Java Spring Boot REST API. E Dado a isso também suporta projetos Java Spring Boot que utilizem **Maven** como gerenciador de dependencias e tenham suas configurações de properties no padrão **YAML**.


Nas proximas sessões você encontrará em detalhes informações sobre como utilizar Plugin Java Spring AWS SNS para habilitar a capacidade de utilizar Amazon SNS como broker de notificação em seus projetos. 

Abaixo esta de forma sumariazada cada sessão desta documentação.

1. [Técnologias base da Plugin](#tecnologias-base-da-plugin)
2. [Capacidades Habilitadas ao uso da Plugin](#quais-são-as-capacidades-habilitadas)
3. [Beneficio de utilizar a Plugin](#quais-os-beneficios-de-utilizar-java-spring-aws-sns-plugin)
4. [Aplicando Java Spring AWS SNS Plugin](#aplicando-java-spring-aws-sns-plugin)


## **Tecnologias base da Plugin**

Objetivo desta sessão é informar quais são as técnologias que fazem parte do Java Spring AWS SNS Plugin.

Ao aplicar este plugin em um projeto Java Spring Boot, sua aplicação poderá se beneficiar de toda infraestrutura do Amazon SNS para lidar com notificações em sua aplicação.

### **Composição Técnologica**

A definição deste Plugin foi pensada visando facilitar o uso do Amazon SNS como Publisher e Subscribe de mensagens aplicativos Java.

Entendemos que a qualidade é inegociavel, e olhamos para as técnologias e metodologias como meio para obter a tão desejada qualidade no software. Essa premissa foi o guia para escolha de cada técnologia detalhada abaixo.


- Ambiente de produção
    - Spring Cloud AWS Messaging
    - Amazon SDK SQS
- Ambiente de testes
    - Spring Clud AWS Test
    - Awaitility
    - JUnit
    - Test Containers
        - LocalStack
- Ambiente de desenvolvimento
    - Docker Compose
        - LocalStack Container


## **Quais são as capacidades Habilitadas**

Ao aplicar em seu projeto Java Spring Boot, o Java Spring AWS SNS Plugin, seu projeto será capaz:

1. Criar Publishers SNS
2. Configurar subscribes SNS
3. Criar Topicos em SNS
4. Desenhar e construir componentens de software que utilizem Amazon SNS sem se conectar com AWS.
5. Criar uma suite de testes de integração automatizada junto a TestContainers e LocalStack 
7. Criar Testes de natureza assicrona para Subscribes com Awaitility 
8. Ambiente de desenvolvimento configurado junto ao Docker com Docker-compose.
9. LocalStack para testes e desenvolvimento local

## **Quais os Beneficios de Utilizar Java Spring AWS SNS Plugin**

1. Facilidade na configuração e uso do Amazon SNS em seu projeto através da StackSpot CLI.
2. Codigos de exemplo de como Publisher para Amazon SNS utilizando boas praticas.
3. Codigos de exemplo de como Subscribes para Amazon SNS utilizando boas praticas.
4. Codigos de exemplo de Testes de integração para validar comportamento do Publisher SNS.
5. Codigos de exemplo de Testes de integração para validar comportamento dos Subscribers notificados pelo Amazon SNS.
7. Configuração do ambiente de testes com JUnit e Test Containers.
8. DockerCompose para uso do Amazon SNS com LocalStack em ambiente de desenvolvimento.
9. LOGs habilitados para facilitar troubleshooting


[Assita este video para ver os beneficios de utilizar Java Spring AWS SNS Plugin em seu projeto](https://youtu.be/I6HLbGGGioQ)


## **Aplicando Java Spring AWS SNS Plugin**

Para Aplicar o Java Spring AWS SNS Plugin em  seus projetos e desfrutar de seus beneficios é necessário que você tenha a CLI da StackSpot instalada em sua maquina. [Caso você não tenha siga este tutorial para fazer a instalação](https://docs.stackspot.com/docs/stk-cli/installation/).

### 1. Importe a Stack em sua maquina

```sh
stk import stack https://github.com/zup-academy/java-springboot-restapi-stack
```

### 2. Agora verifique se a Stack foi importada com sucesso

```sh
stk list stack | grep java-springboot
```

### 3. Aplique o Plugin, no diretorio do seu projeto, execute

```sh
stk apply plugin java-springboot-restapi-stack/java-spring-aws-sns-plugin
```   

### 4. Verifque as modificações em seu projeto

```sh
git status
```   



## Suporte

Caso precise de ajuda, por favor abra uma [issue no repositorio do Github da Stack](https://github.com/zup-academy/java-spring-aws-sns-plugin/issues).