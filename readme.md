# Java Developer Technical Skills Test

This is my technical skills test submission for the Java Developer position at Clarity Software. This project aims to showcase my Java programming skills, particularly in building a RESTful API and working with data persistence.

## Technologies Used
For this project, I utilized the following technologies and frameworks:

* Java
* Spring Boot
* Spring Data JPA / Hibernate
* PostgreSQL (as the chosen database)
* Docker (for containerization)

# Getting Started
To run the project locally, follow these steps (code is written using JDK 8):

* Ensure you have Docker installed on your system.
* Start the docker compose file by running `docker compose up` via cmd while in the project root directory. This will start postgres running on port 5432 with the default database "clarity".
* Set spring >  jpa >  hibernate >  ddl-auto to `create` in `src/main/resources/application.yaml` to create the initial tables - this should changed back after first boot.
* Start the application using maven: `mvn spring-boot:run`

The API endpoints will now be available on port 8080, i.e. `http://localhost:8080/metrics/1` would retrieve the first metric (if this existed)