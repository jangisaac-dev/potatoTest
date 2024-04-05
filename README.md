//todo
:: jwt token expired check

//yml example
---

spring:
  application:
    name: potatoTest
  datasource:
    driver-class-name: org.mariadb.jdbc.Driver
    url: jdbc:mariadb://[URL]/potato
    username: [dbUserName]
    password: [dbPassword]

  jpa:
    database-platform: org.hibernate.dialect.MySQL8Dialect
    hibernate:
      ddl-auto: none
    properties:
      hibernate:
        format_sql: true #To beautify or pretty print the SQL
        show_sql: true #show sql
        default_schema: potato
  mvc:
    pathmatch:
      matching-strategy: ant_path_matcher

logging:
  level:
    org.hibernate:
      type.descriptor.sql: trace #show parameter binding
      SQL: DEBUG


jwt:
  secret: [jwt key]
  access_valid_time: 300
  refresh_valid_time: 3000

hsu:
  verify:
    random_seed: 940714
    time_second: 300
    admin_password: [adminPassword For Server]
  aes:
    key: [AES 32Bit KEY]

