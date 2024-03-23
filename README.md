//yml example
---

spring:
application:
name: potatoTest
datasource:
driver-class-name: org.mariadb.jdbc.Driver
url: [url with db]
username: [id]
password: [pw]

jpa:
database-platform: org.hibernate.dialect.MySQL8Dialect
hibernate:
ddl-auto: none
properties:
hibernate:
format_sql: true #To beautify or pretty print the SQL
show_sql: true #show sql
default_schema: [default schema name]
mvc:
pathmatch:
matching-strategy: ant_path_matcher

logging:
level:
org.hibernate:
type.descriptor.sql: trace #show parameter binding
SQL: DEBUG