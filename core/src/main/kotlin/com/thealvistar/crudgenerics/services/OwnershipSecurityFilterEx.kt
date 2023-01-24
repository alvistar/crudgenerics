package com.thealvistar.crudgenerics.services

import com.thealvistar.crudgenerics.entities.Ownership
import com.thealvistar.crudgenerics.exceptions.ForbiddenException
import jakarta.persistence.EntityManager
import jakarta.persistence.metamodel.Attribute
import java.security.Principal
import java.util.UUID
import kotlin.reflect.KClass

class OwnershipSecurityFilterEx<T : Any>(val em: EntityManager, val entityClass: KClass<T>) :
    OwnershipSecurityFilter<T>() {
    var relationships: List<Attribute<in T, *>>? = null

    init {
        // Get all many-to-one relationships on the entity
        relationships = em.metamodel
            .managedType(entityClass.java)
            .attributes.filter {
                it.isAssociation &&
                    it.persistentAttributeType in
                    listOf(
                        Attribute.PersistentAttributeType.MANY_TO_ONE,
                        Attribute.PersistentAttributeType.ONE_TO_ONE
                    )
            }
    }

    override fun canCreate(resource: T, principal: Principal?) {
        for (relationship in relationships!!) {
            // If relationship class is not Ownership, skip
            if (!Ownership::class.java.isAssignableFrom(relationship.javaType)) {
                continue
            }

            val re =
                resource.javaClass.getMethod("get${relationship.name.replaceFirstChar(Char::titlecase)}")
                    .invoke(resource)

            @Suppress("FoldInitializerAndIfToElvis")
            if (re == null) {
                return
            }

            // Get id value of re entity
            val id = em.entityManagerFactory.persistenceUnitUtil.getIdentifier(re) ?: return

            val relatedEntity = em.getReference(
                relationship.javaType,
                id
            )

            val relatedEntityOwner = (relatedEntity as Ownership).owner

            if (relatedEntityOwner != UUID.fromString(principal!!.name)) {
                throw ForbiddenException()
            }
        }
    }

    override fun canUpdate(resource: T, principal: Principal?) {
        canCreate(resource, principal)
    }
}
