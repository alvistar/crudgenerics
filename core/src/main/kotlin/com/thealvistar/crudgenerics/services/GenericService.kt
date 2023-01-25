package com.thealvistar.crudgenerics.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.thealvistar.crudgenerics.entities.Ownership
import com.thealvistar.crudgenerics.exceptions.ResourceNotFoundException
import com.thealvistar.crudgenerics.mappers.ConverterUpdater
import com.thealvistar.crudgenerics.repositories.JpaExecutor
import com.thealvistar.crudgenerics.utils.resolveGeneric
import com.thealvistar.crudgenerics.utils.throwIfNotEmpty
import jakarta.annotation.PostConstruct
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import jakarta.validation.Validator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.ResolvableType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.projection.SpelAwareProxyProjectionFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.transaction.annotation.Transactional
import java.security.Principal
import java.util.UUID
import kotlin.reflect.KClass

@Suppress("SpringJavaInjectionPointsAutowiringInspection")
abstract class GenericService<T : Any, ID : Any>(
    protected open var securityFilter: SecurityFilter<T>? = null
) {

    @Autowired
    protected open lateinit var repository: JpaExecutor<T, ID>

    @Autowired(required = false)
    protected open lateinit var converterUpdaters: List<ConverterUpdater<*, T>>

    @Autowired
    protected open lateinit var om: ObjectMapper

    @Autowired
    protected open lateinit var validator: Validator

    @PersistenceContext
    protected open lateinit var em: EntityManager

    protected open lateinit var rsqlFilter: RSQLFilter<T, ID>

    @Suppress("UNCHECKED_CAST")
    protected open val entityClass: KClass<T> by lazy {
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

    protected open val pf = SpelAwareProxyProjectionFactory()

    internal fun resolveEntityClass(): Class<T> {
        val type = ResolvableType.forInstance(this).`as`(GenericService::class.java)

        if (type.hasUnresolvableGenerics()) {
            throw IllegalArgumentException("Unable to resolve entity class from generic type $type")
        }

        @Suppress("UNCHECKED_CAST")
        return type.getGeneric(0).resolve() as Class<T>
    }

    private fun <C : Any> getConverter(dto: C): ConverterUpdater<C, T>? {
        if (!::converterUpdaters.isInitialized) {
            return null
        }

        @Suppress("UNCHECKED_CAST")
        return converterUpdaters.firstOrNull() {
            val generics =
                ResolvableType
                    .forClass(it::class.java)
                    .`as`(ConverterUpdater::class.java)
                    .generics

            generics[0].isAssignableFrom(dto::class.java)
        } as ConverterUpdater<C, T>?
    }

    private fun <C : Any> convert(dto: C): T {
        if (entityClass.isInstance(dto)) {
            @Suppress("UNCHECKED_CAST")
            return dto as T
        }

        return getConverter(dto)?.convert(dto)
            ?: throw RuntimeException("Converter returned null for $dto")
    }

    private fun <C : Any> update(dto: C, entity: T): T {
        if (entityClass.isInstance(dto)) {
            @Suppress("UNCHECKED_CAST")
            return dto as T
        }

        val converter = getConverter(dto)

        if (converter != null) {
            getConverter(dto)?.update(dto, entity)
            return entity
        }

        // If they passed as a JSON try to convert it to DTO
        if (dto is String) {
            val updatedDto = om.readerForUpdating(entity).readValue(dto, entityClass.java)
            validator.validate(updatedDto).throwIfNotEmpty()
            return updatedDto
        }

        throw RuntimeException("Unable to covert $dto")
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

    fun <D : Any> createResource(dto: D, principal: Principal? = null): T {
        val converted = convert(dto)
        securityFilter?.canCreate(converted, principal)
        return repository.saveAndFlush(converted)
    }

    @Transactional
    open fun <D : Any, P : Any> createResource(
        dto: D,
        principal: Principal? = null,
        clazz: KClass<P>
    ): P {
        val entity = createResource(dto, principal)

        em.refresh(entity)

        return pf.createProjection(clazz.java, entity)
    }

    fun save(resource: T): T {
        return repository.save(resource)
    }

    fun <D : Any> updateResourceById(id: ID, dto: D, principal: Principal? = null): T {
        val resource = getResourceById(id, principal)
        val updated = update(dto, resource)
        securityFilter?.canUpdate(updated, principal)

        return repository.save(updated)
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

        securityFilter?.canView(resource, principal)

        return resource
    }

    fun <P : Any> getResourceById(id: ID, principal: Principal? = null, clazz: KClass<P>): P {
        val entity = getResourceById(id, principal)
        return pf.createProjection(clazz.java, entity)
    }

    fun getResourcesByIds(ids: List<ID>, principal: Principal?): List<T> {
        val resources = repository.findAllById(ids)

        resources.forEach { securityFilter?.canView(it, principal) }

        return repository.findAllById(ids)
    }

    fun <P : Any> getResourcesByIds(
        ids: List<ID>,
        principal: Principal? = null,
        clazz: KClass<P>
    ): List<P> {
        val resources = repository.findAllById(ids)

        resources.forEach { securityFilter?.canView(it, principal) }

        return repository.findByIdIn(ids, clazz.java)
    }

    fun deleteResourceById(id: ID, principal: Principal? = null) {
        val resource = repository.findByIdOrNull(id) ?: throw ResourceNotFoundException()

        securityFilter?.canView(resource, principal)

        repository.delete(resource)
    }

    fun deleteResourcesByIds(ids: List<ID>, principal: Principal? = null) {
        val resources = repository.findAllById(ids)

        resources.forEach { securityFilter?.canView(it, principal) }

        repository.deleteAll(resources)
    }

    open fun updateOwnership(id: ID, newOwner: UUID, principal: Principal) {
        val resource = repository.findByIdOrNull(id) ?: throw ResourceNotFoundException()

        if (resource !is Ownership) {
            throw RuntimeException("Resource $resource is not an Ownership")
        }

        securityFilter?.canView(resource, principal)

        resource.owner = newOwner
        repository.save(resource)
    }
}
