# P10BibliothequeV2

Projet 10 du parcours de développeur d'application JAVA d'OC


#Prérequis :

Avoir un client muni de JAVA 15 et d'un JDK. 
Avoir un IDE (IntelliJ, Eclipse, NetBeans...) installé sur le client. 
Savoir monter un serveur SQL et créer une BDD SQL (des scripts sont fournis dans le livrable sur Open Classrooms)


# Exécuter l'application (IDE).

Cloner le projet depuis GitHub dans votre IDE en utilisant le lien https:
https://github.com/vbiasin/OCP10BibliothequeV2.git

Dans le module BibliothequeSpringCloudConfig, configurer le fichier "application.properties":

management.endpoints.web.exposure.include=*
server.port=9101
spring.cloud.config.server.git.uri=https://github.com/vbiasin/OCP7Bibliotheque
spring.cloud.config.server.git.search-paths=config-server-repo
spring.cloud.config.server.git.default-label=master
spring.cloud.config.server.bootstrap=true
spring.profiles.active= default, git

Dans le dossier config-server-repo:
Configurer les fichiers  "BibliothequeAdministration.properties",  "BibliothequeBatchMail.properties",  "BibliothequeBook.properties",  "BibliothequeWeb.properties":

server.port:XXXX #8080 ----> BibliothequeWeb
server.port:XXXX #8280 ----> BibliothequeAdministration
server.port:XXXX #8380 ----> BibliothequeBatchMail
server.port:XXXX #8180 ----> BibliothequeBook
server.port:XXXX #8480 ----> BibliothequeReservation

server.port:XXXX
management.endpoints.web.exposure.include=*
spring.datasource.url = jdbc:mysql://192.168.1.62:3306/p7bibliotheque?serverTimezone=UTC (URL de votre BDD)
spring.datasource.username= <votreUtilisateur>
spring.datasource.password= <motDePasseUtilisateur>
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.jpa.show-sql=true
logging.level.org.springframework.boot.autoconfigure=INFO
spring.jpa.generate-ddl=true
eureka.client.serviceUrl.defaultZone: http://localhost:9102/eureka/

Ainsi que le fichier "BibliothequeEureka.properties":
server.port:9102
management.endpoints.web.exposure.include=*
eureka.instance.hostname=localhost
eureka.client.serviceUrl.defaultZone:http://localhost:9102/eureka/
eureka.client.registerWithEureka:false
eureka.client.fetchRegistry:false

Vous pouvez exécuter l'application dans votre IDE en respectant l'ordre de démarrage des modules ci-dessous:
	1) BibliothequeSpringCloudConfigApplication
	2) BibliothequeEurekaApplication
	3) BibliothequeAdministrationApplication, BibliothequeBatchMailApplication, BibliothequeBookApplication, BibliothequeReservation et BibliothequeWebApplication (pour ces modules l'ordre n'a pas d'importance)

ATTENTION: Pour que l'application fonctionne correctement, TOUS les modules doivent être en cours d'exécution !!
