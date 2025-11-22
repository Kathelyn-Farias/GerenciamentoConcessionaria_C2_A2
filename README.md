# Gerenciamento de Concessionária — Guia de Execução (Ubuntu)

Aplicação de console em **Java 17** com **MySQL 8**.
Siga os passos abaixo **na VM Ubuntu** para preparar o ambiente, criar o banco, compilar e executar o sistema.

Link do video: https://youtu.be/VS1joG4_mq4
---

## 1) Atualizar e instalar dependências

```bash
sudo apt update
sudo apt install -y git openjdk-17-jdk mysql-server
```

## 2) Iniciar e habilitar o MySQL

```bash
sudo systemctl enable --now mysql
sudo systemctl status mysql --no-pager
mysql --version
sudo mysql -e "SELECT VERSION();"
```

## 3) Clonar o projeto e entrar na pasta

```bash
git clone https://github.com/Kathelyn-Farias/GerenciamentoConcessionaria_C2_A2
cd GerenciamentoConcessionaria_C2_A2

# (checar nomes dos scripts para evitar erro de caminho)
ls -l sql
```

## 4) Criar schema/usuário e permissões (como root)

```bash
sudo mysql <<'SQL'
CREATE DATABASE IF NOT EXISTS teste CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
CREATE USER IF NOT EXISTS 'app'@'localhost' IDENTIFIED BY 'app123';
GRANT ALL PRIVILEGES ON teste.* TO 'app'@'localhost';
FLUSH PRIVILEGES;
SQL
```

## 5) Aplicar os scripts SQL do projeto
*(como root para evitar problema de DEFINER/trigger)*

```bash
sudo mysql -D teste < sql/create_tables.sql
sudo mysql -D teste < sql/insert_samples_records.sql

# (opcional: conferir rapidamente)
mysql -u app -papp123 -D teste -e "SHOW TABLES;"
```

## 6) Criar o arquivo de configuração do banco (a partir do template)

```bash
cp src/conexion/db_example.properties src/conexion/db.properties
```

## 7) Compilar todas as classes Java

```bash
find src -name "*.java" > sources.txt
mkdir -p out
javac -encoding UTF-8 -cp "lib/mysql-connector-j-9.4.0.jar" -d out @sources.txt
```

## 8) Executar a aplicação

```bash
java -Dfile.encoding=UTF-8 -cp "out:lib/mysql-connector-j-9.4.0.jar" Main
```

---

### Observações

* Use o template **`db_example.properties`**.
* Se aparecer **“No suitable driver”**, confira o classpath do JAR: `lib/mysql-connector-j-9.4.0.jar`.
* Se aparecer **“Access denied”**, verifique usuário/senha do `db.properties` e repita o passo **4**.
* Se usar DBeaver e houver erro com `DELIMITER` nas *triggers*, rode os scripts via CLI (passo **5**).
