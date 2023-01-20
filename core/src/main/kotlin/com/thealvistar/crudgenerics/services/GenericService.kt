package com.thealvistar.crudgenerics.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.thealvistar.crudgenerics.entities.Ownership
import com.thealvistar.crudgenerics.exceptions.ResourceNotFoundException
import com.thealvistar.crudgenerics.mappers.ConverterUpdater
import com.thealvistar.crudgenerics.repositories.JpaExecutor
import com.thealvistar.crudgenerics.utils.resolveGeneric
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.ResolvableType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.projection.SpelAwareProxyProjectionFactory
import org.springframework.data.repository.findByIdOrNull
import java.security.Principal
import java.util.UUID
import kotlin.reflect.KClass

@Suppress("SpringJavaInjectionPointsAutowiringInspection")
abstract class GenericService<T : Any, ID : Any>(
    protected val securityFilter: SecurityFilter<T>? = null
) {

    @Autowired
    lateinit var repository: JpaExecutor<T, ID>

    @Autowired(required = false)
    lateinit var converterUpdaters: List<ConverterUpdater<*, T>>

    @Autowired
    lateinit var om: ObjectMapper

    private lateinit var rsqlFilter: RSQLFilter<T, ID>

    @Suppress("UNCHECKED_CAST")
    private val entityClass: KClass<T> by lazy {
        resolveGeneric<T>(
            this,
            GenericService::class,
            0
        ).kotlin
    }

    @PostConstruct
    fun setup() {
        rsqlFilter = RSQLFilter(repository, securityFilter)
    }

    private val pf = SpelAwareProxyProjectionFactory()

    internal fun resolveEntityClass(): Class<T> {
        val type = ResolvableType.forInstance(this).`as`(GenericService::class.java)

        if (type.hasUnresolvableGenerics()) {
            throw IllegalArgumentException("Unable to resolve entity class from generic type $type")
        }

        @Suppress("UNCHECKED_CAST")
        return type.getGeneric(0).resolve() as Class<T>
    }

    private fun <C : Any> getConverter(dto: C): ConverterUpdater<C, T> {
        @Suppress("UNCHECKED_CAST")

        return converterUpdaters.first {
            val generics =
                ResolvableType
                    .forClass(it::class.java)
                    .`as`(ConverterUpdater::class.java)
                    .generics

            generics[0].isAssignableFrom(dto::class.java)
        } as ConverterUpdater<C, T>
    }

    private fun <C : Any> convert(dto: C): T {
        if (entityClass.isInstance(dto)) {
            @Suppress("UNCHECKED_CAST")
            return dto as T
        }

        return getConverter(dto).convert(dto)
            ?: throw RuntimeException("Converter returned null for $dto")
    }

    private fun <C : Any> update(dto: C, entity: T): T {
        if (entityClass.isInstance(dto)) {
            @Suppress("UNCHECKED_CAST")
            return dto as T
        }

        getConverter(dto).update(dto, entity)
        return entity
    }

    fun <P : Any> listResources(
        filter: String? = null,
        pageable: Pageable = Pageable.unpaged(),
        principal: Principal? = null,
        projection: KClass<P>
    ): Page<P> =
        rsqlFilter.filterResourcesProjection(
            filter = filter,
            pageable = pageable,
            principal = principal,
            projection = projection
        )

    open fun listResources(
        filter: String? = null,
        pageable: Pageable = Pageable.unpaged(),
        principal: Principal? = null
    ) = rsqlFilter.filterResources(
        filter = filter,
        pageable = pageable,
        principal = principal
    )

    fun <D : Any, P : Any> createResource(dto: D, clazz: KClass<P>): P {
        val entity = repository.save(convert(dto))
        return pf.createProjection(clazz.java, entity)
    }

    fun <D : Any> createResource(dto: D): T {
        return repository.save(convert(dto))
    }

    fun save(resource: T): T {
        return repository.save(resource)
    }

    fun <D : Any> updateResourceById(id: ID, dto: D, principal: Principal? = null): T {
        val resource = getResourceById(id, principal)

        return repository.save(update(dto, resource))
    }

    fun <D : Any, P : Any> updateResourceById(
        id: ID,
        dto: D,
        principal: Principal? = null,
        clazz: KClass<P>
    ): P {
        val resource = updateResourceById(id, dto, principal)
        return pf.createProjection(clazz.java, resource)
    }

    fun getResourceById(id: ID, principal: Principal? = null): T {
        val resource = repository.findByIdOrNull(id) ?: throw ResourceNotFoundException()

        securityFilter?.checkPermissions(resource, principal)

        return resource
    }

    fun <P : Any> getResourceById(id: ID, principal: Principal? = null, clazz: KClass<P>): P {
        val entity = getResourceById(id, principal)
        return pf.createProjection(clazz.java, entity)
    }

    fun getResourcesByIds(ids: List<ID>, principal: Principal?): List<T> {
        val resources = repository.findAllById(ids)

        resources.forEach { securityFilter?.checkPermissions(it, principal) }

        return repository.findAllById(ids)
    }

    fun <P : Any> getResourcesByIds(
        ids: List<ID>,
        principal: Principal? = null,
        clazz: KClass<P>
    ): List<P> {
        val resources = repository.findAllById(ids)

        resources.forEach { securityFilter?.checkPermissions(it, principal) }

        return repository.findByIdIn(ids, clazz.java)
    }

    fun deleteResourceById(id: ID, principal: Principal? = null) {
        val resource = repository.findByIdOrNull(id) ?: throw ResourceNotFoundException()

        securityFilter?.checkPermissions(resource, principal)

        repository.delete(resource)
    }

    fun deleteResourcesByIds(ids: List<ID>, principal: Principal? = null) {
        val resources = repository.findAllById(ids)

        resources.forEach { securityFilter?.checkPermissions(it, principal) }

        repository.deleteAll(resources)
    }

    open fun updateOwnership(id: ID, newOwner: UUID, principal: Principal) {
        val resource = repository.findByIdOrNull(id) ?: throw ResourceNotFoundException()

        if (resource !is Ownership) {
            throw RuntimeException("Resource $resource is not an Ownership")
        }

        securityFilter?.checkPermissions(resource, principal)

        resource.owner = newOwner
        repository.save(resource)
    }
}
