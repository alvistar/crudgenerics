package com.thealvistar.crudgenerics.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.repository.NoRepositoryBean

@NoRepositoryBean
interface JpaExecutor<T, ID> : JpaRepository<T, ID>, JpaSpecificationExecutor<T> {
    fun <R> findById(id: ID, projection: Class<R>): R?
    fun <R> findByIdIn(id: List<ID>, projection: Class<R>): MutableList<R>
}
