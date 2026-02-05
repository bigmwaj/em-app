## App Architecture

### List Value Object

LVO component is a list of static value that is used all over the application.
<br/>
The common LVO are defined in smart-cm-project-dm(Data Model Module).
<br/>
You can extend that module and add you own LVO.

### Entity, Data Transfer Object and Mapper

Entity components are business domain objects.
They can also be technical specific objects. <br/>
They are designed to help store data in a DB and can have correspondent table.
<br/>
The [Lombok](https://projectlombok.org/) library is used to generate some attributes

---
DTO components are used to transfer data over application components.
The common DTO are defined in smart-cm-project-dm(Data Model Module).
You can extend that module and add you own DTO.
<br/>
The [Lombok](https://projectlombok.org/) library is used to generate some attributes

---
Mapper components are used to map DTO to Entity and vice versa.
The mapping strategy is based on [MapStruct](https://mapstruct.org/),
a java plugin that generate java class mapper on compile

### API

API components are used to expose service to application client.
They are documented with [swagger](https://springdoc.org/)
---
[API Documentation](./docs/api.md)

---

### Service

Service components are used to design the application's business logic.

### Data Access Object(dao)

DAO components are used de design the application's persistence logic.


