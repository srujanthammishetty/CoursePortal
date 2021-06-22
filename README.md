# CoursePortal
![Screenshot](app.png)

***
# Tools and Tech

   > spring-boot-starter-web <br>
   > spring-boot-starter-test <br>
   > vertx-web <br>
   > hikariCp  [ Connection Pooling ] <br>
   > junit <br>
   > logback-classic <br>
   > apache-commons <br>
   > postregsql [ JDBC client ] <br> 
***
# Architecture

  1) All client requests are routed to load balancer. Requests are routed from the load balancer to the application servers. 
  
     Load balancer takes care of   distributing the incoming traffic across all the application servers based on the configured routing policy.
     
      ### Advantages of LoadBalancer
      
         1) Provides single point of access to our application.
         
         2) Incoming traffic is evenly distributed accross all the application servers.
         
         3) If any of the server is down, it will automatically redirect the requests to healthy servers.
         
         4) SSL offloading, which would free the application server from the burden of decrypting and/or encrypting traffic sent via SSL.
         
         5) As the application servers are inside private network, communication between loadBalancer and application servers happens via http protocol.
    
        
  2)  All the Application servers connects to a relational database cluster for storing and retrieving application data.
  
  3)  As the application is read-intensive application, we deploy a database cluster which follows master-slave architecture.
      
        ### Advantages of master-slave cluster
       
         1) All write requests are directed to master node.
         
         2) All read requests are directed to slave nodes, to reduce latency of read requests.
         
         3) All the read traffic is evenly distributed across slave nodes.
  
  4) Can integrate with caching systems like **Redis** to reduce request latency.
  
       ### Advantages of Caching Systems
       
        1) Can reduce database calls by caching frequenctly accessed data.
        
        2) Reduce request latency by avoiding database call and fetching data directly from cache if it exists
        
   ***   
   ## ER Diagram
     
  ![Screenshot](ER.png)
  ***
  ## Prerequisites 
  
      
  * Create Tables
  
       **init_script.sql**
    
          1) This file contains CREATE statements of all the tables required for the application. 
          2) CREATE statements are tested on PostgreSQL database. 
          3) Based on relation database type we need to update the CREATE statements. 
   
  * DB Properties and Application port number
           
       **deployment.properties**
      
      
       - Need to configure below properties in deployment.properties file.
          
          > server.port=8091 <br>
          > db.url=          <br>
          > db.username=     <br>
          > db.password =    <br>
          > db.driver.class.name=   <br>
          > db.connection.pool.size=    <br>
          
       - deployment.properties files is present in resources folder.
              
   * JDBC Driver
        
        - Need to add required JDBC driver dependency in pom.xml file. By default application is configured with postgresql driver.
        
   ***
   ## Build    
         
   - **mvn clean install**
            
         This command will generate the application fat jar in ${project.basedir}/build folder.
            
   
    
   ***
   ## Running the application 
        
        java -jar {artifactId}-{version}-fat.jar
   
        
