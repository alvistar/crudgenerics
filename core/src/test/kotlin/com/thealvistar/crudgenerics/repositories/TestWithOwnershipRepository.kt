package com.thealvistar.crudgenerics.repositories

import com.thealvistar.crudgenerics.entities.TestEntityWithOwnership
import java.util.UUID

interface TestWithOwnershipRepository : JpaExecutor<TestEntityWithOwnership, UUID>
