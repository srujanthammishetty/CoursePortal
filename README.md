# CoursePortal
![Screenshot](app.png)


# Tools and Tech

    1) spring-boot-starter-web 
    2) spring-boot-starter-test
    3) vertx-web
    4) hikariCp  [ Connection Pooling ]
    5) junit
    6) logback-classic
    7) apache-commons
    5) postregsql [ JDBC client ]

# Architecture

  1) All client requests are routed to load balancer. Requests are routed to the application servers based on the configured routing policy.
     
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
        
       
   ## ER Diagram
     
  ![Screenshot](ER.png)

   ## init_script.sql
   
   ## deployment.properties 
   
   ## Build    
       Running mvn clean install command will generate a fat jar in ${project.basedir}/build folder.
        
   ## Running the application 
        
        java -jar {artifactId}-{version}-fat.jar
   
        