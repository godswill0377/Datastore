# DATAEXO
This project has been developed using Java Spring + Thymeleaf. 
Initial development has been completed.

# What do you need?
- JDK 8
- Apache Tomcat 8.5.20
- Maven
- MySQL (or other SQL database)

# Main technique

- Spring && Spring security && Spring boot
- Mybatis
- bootstrap
- flavr
- thymeleaf
- editor.md

# Installation steps 

0 - Download or clone quandl-clone project

1 - Create the database using the **dataexo.sql** file
      
2 - update the database info in resource/application.yml
   
    ssoauth:
      logout: http://localhost:8087/sso/logout
      auth_url: http://localhost:8087/sso/athenticate
      login_url: http://localhost:8087/dataexo/sso/login
      jforum_url: http://localhost:8081/jforum
      
      Especially  , you have to change these configuration.
      logou , auth_url , login_url are for sso-auth website.
      
      jforum_url is for jforum website.
      
3 - Download apache-tomcat-8.5.20.zip and extract files.  

4 - edit **apache/bin/catalina.bat** file

    You have to inert below code before 'rem Execute Java with the applicable properties'
    
    set "JAVA_OPTS=%JAVA_OPTS% -Xms2048m -Xmx2048m -XX:PermSize=512m -XX:MaxPermSize=512m"
    
5 - Copy **dataexo.war** to webapps folder

6 - Run **apache/bin/startup.bat** file

7.- Type **http://localhost:8085** into your browser


## 后台模块
