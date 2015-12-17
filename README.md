ePerusteet-amosaa
=================

Ammatillisen koulutuksen paikallisten opetussuunnitelmien laadintatyökalu.

Kehitysympäristön vaatimukset
-----------------------------

## Backend
- JDK 8
- Maven 3
- PostgreSQL 9.3 (luo tietokanta paikallista kehitystä varten)
- Tomcat [7.0.42,8)

## Frontend
- Asenna npm
- Asenna riippupvuudet
```sh
npm -g install typescript
npm -g install gulp
npm -g install tslint
```

Riippuvuuksien takia käännösaikana tarvitaan pääsy sisäiseen pakettien hallintaan, koska osa paketeista (lähinnä build-parent) ei ole julkisissa repoissa.

Ajoaikana riippuu mm. keskitetystä autentikaatiosta (CAS), käyttäjähallinnasta, organisaatiopalvelusta, koodistosta ja eperusteista joihin täytyy olla ajoympäristöstä pääsy.


Ajaminen paikallisesti
----------------------

eperusteet-amosaa-app: 

```sh
cd eperusteet-amosaa-app
npm install
gulp
```

eperusteet-amosaa-service: 

```sh
mvn install
cd eperusteet-amosaa-service
# jos muisti loppuu: MAVEN_OPTS="-Xmx2048m"
mvn tomcat7:run -Deperusteet-amosaa.devdb.user=<user> -Deperusteet-amosaa.devdb.password=<password> -Deperusteet-amosaa.devdb.jdbcurl=<jdbcurl>
```
    
Sovelluksen voi myös kääntää kahdeksi eri war-paketiksi joita voi aja erillisessä Tomcatissa. 

Konfiguraatioon tarvitaan seuraavat muutokset:

  - URIEncoding="UTF-8": 
```
    <Connector port="8080" protocol="HTTP/1.1" (...) URIEncoding="UTF-8"/>
```
  - PostgreSQL 9.3 JDBC-ajuri lib-hakemistoon
  - Kehityskannan resurssi:    
```
    <GlobalNamingResources>
    ...
    <Resource name="jdbc/eperusteet-amosaa" auth="Container" type="javax.sql.DataSource"
                 maxActive="100" maxIdle="30" maxWait="10000"
                 username="..." password="..." driverClassName="org.postgresql.Driver"
                 url="jdbc:postgresql://localhost:5432/..."/>
    ...
    </GlobalNamingResources>
```

Web-sovelluksen (app) osalta maven käyttää yeoman-maven-plugin:ia joka tarvitsee nodejs:n ja yo:n toimiakseen.
