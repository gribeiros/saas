Antigravity Agent Rules - Projeto Java 25

Este arquivo (`AGENTS.md`) define as diretrizes de desenvolvimento, arquitetura e segurança para o agente de IA seguir rigorosamente durante qualquer interação neste repositório.

## 🏗️ Padrões de Arquitetura e Código (Java 25)
1. **Princípios SOLID:** Aplique rigorosamente os 5 princípios em todas as novas classes e refatorações.
   - *Single Responsibility:* Cada classe/método deve ter apenas uma responsabilidade e motivo para mudar.
   - *Open/Closed:* Aberto para extensão, fechado para modificação.
   - *Liskov Substitution:* Subclasses devem ser substituíveis por suas superclasses.
   - *Interface Segregation:* Muitas interfaces específicas são melhores que uma geral.
   - *Dependency Inversion:* Dependa de abstrações (interfaces), não de implementações. Use injeção de dependência.
2. **Recursos Modernos do Java 25:**
   - **Imutabilidade:** Utilize `record` intensamente para DTOs e transferência de dados imutáveis.
   - **Pattern Matching:** Use `switch` expressions e `instanceof` com pattern matching para reduzir boilerplate e casting.
   - **Concorrência Moderna:** Empregue **Virtual Threads** e **Structured Concurrency** para processamento I/O assíncrono seguro, escalável e de alta performance.
   - Evite `null` retornando sempre `Optional` para representar ausência de valor.
3. **Produtividade, Lombok e Reuso de Dependências:**
   - **Lombok:** Utilize as anotações do Lombok de forma exaustiva (`@Builder`, `@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@Slf4j`, etc.) para eliminar todo o código boilerplate em entidades e classes onde o uso de `records` não for aplicável.
   - **Ferramentas do `pom.xml`:** Antes de implementar qualquer solução do zero, **leia e analise o `pom.xml`**. É OBRIGATÓRIO utilizar as bibliotecas, frameworks e utilitários já configurados no projeto para cada novo desenvolvimento, maximizando o reuso e mantendo a padronização do ecossistema.

## 🧪 Desenvolvimento de Testes Unitários em Paralelo
1. **Criação Simultânea:** Todo novo código de produção DEVE obrigatoriamente ser acompanhado do seu respectivo teste unitário desenvolvido em paralelo. Não finalize uma feature sem os testes.
2. **Frameworks Recomendados:** Utilize **JUnit 5**, **Mockito** e **AssertJ** para asserções fluentes.
3. **Padrões de Teste:**
   - Siga o padrão BDD (*Given, When, Then*) ou AAA (*Arrange, Act, Assert*).
   - Testes devem ser rápidos, independentes e não devem depender de ordem de execução ou estado externo (banco de dados real, APIs externas). Use mocks.
   - Teste não apenas o caminho feliz (*happy path*), mas também bordas, exceções e entradas inválidas.

## 🛡️ Segurança e Prevenção de Vulnerabilidades (OWASP Top 10)
A segurança vem em primeiro lugar (Security by Design). Evite proativamente as vulnerabilidades mais comuns:
1. **Injections (SQL, NoSQL, OS Command):** 
   - NUNCA utilize concatenação de strings para construir queries. Use JPA/Hibernate ou `PreparedStatement` para parametrização.
2. **Validação e Sanitização de Entrada:**
   - Trate toda entrada do usuário como "não confiável". Utilize `Bean Validation` (ex: `@NotNull`, `@Size`, `@Email`, `@Pattern`) rigorosamente nos controllers e DTOs.
3. **Autenticação, Autorização e Gestão de Segredos:**
   - Siga o princípio do "menor privilégio".
   - **Nenhum segredo no código:** Credenciais, chaves de API, e tokens devem ser lidos exclusivamente via variáveis de ambiente (`System.getenv()`). NUNCA faça hardcode.
4. **Desserialização Insegura:**
   - Evite desserialização nativa do Java (`ObjectInputStream`). Ao usar bibliotecas como Jackson/Gson, não permita tipagem polimórfica descontrolada e atualize a biblioteca frequentemente contra falhas conhecidas (CVEs).
5. **Cross-Site Scripting (XSS) e CSRF:**
   - Garanta que tokens CSRF estejam habilitados caso forneça sessões para o browser, e faça o encode adequado de qualquer saída HTML/JSON.

## 🚀 Comandos Customizados de Referência
- **Build (Maven):** `./mvnw clean package`
- **Testes (Maven):** `./mvnw test`
- **Execução:** `./mvnw spring-boot:run`