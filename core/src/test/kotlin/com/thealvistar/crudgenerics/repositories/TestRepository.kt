package com.thealvistar.crudgenerics.repositories

import com.thealvistar.crudgenerics.entities.TestEntity
import java.util.UUID

interface TestRepository : JpaExecutor<TestEntity, UUID>
