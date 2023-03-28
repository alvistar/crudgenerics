# Crudgenerics
A Kotlin library for Spring enabling to create CRUD operations for any entity.
It was created to be less invasive and with narrower scope than Spring Data Rest, while still providing a lot of the same functionality.

## Features
- Create new resource
- Update existing resource
- Delete existing resource
- Support plain entities and DTOs in body of requests
- Support for pageable, sorting and filtering through flexible RSQL queries
- Support validation
- Support for mapstruct for mapping between entities and DTOs
- Customizable security filters for filtering (db side) and creating resources (example resource ownership)
- Openapi documentation generated automatically

## Creating a basic CRUD controller
To create a basic CRUD controller you need to create a class that extends one of the following classes
- BasicGenericController - If not using DTOs or projections
- DtoGenericController - If using DTOs
- GenericController - If using projections and DTOs
``` kotlin
@RestController
@RequestMapping("/orders")
class OrderController : BasicGenericController<Order, UUID>()
```

You need also to provide the service for the controller. 
Service will be autowired by the controller if deriving from GenericService<T>, otherwise you can specify it 
in the constructor. 
```kotlin
@Service
class OrderService : GenericService<Order, UUID>()
```

You will also need a repository for the entity derived from JpaExecutor
```kotlin
interface OrderRepository : JpaExecutor<Order, UUID>
```

## Opeanapi documentation
The library will generate openapi documentation for the controller.
You can access it by going to /swagger-ui/index.html

Library is doing some openapi customization in order to provide dynamic configuration.
Specifically it sets responseBody of some methods to be a reference of existing schemas.

In order to avoid name clashes you can consider to set springdoc.use-fqn=true.
