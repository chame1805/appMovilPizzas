package com.chame.myapplication.core.database.strategy

import com.chame.myapplication.core.database.entity.OrderEntity

/**
 * Sync Strategy interface for conflict resolution
 * Defines how to handle local vs remote data conflicts
 */
interface SyncStrategy {
    suspend fun sync(local: OrderEntity, remote: OrderEntity): OrderEntity
    suspend fun mergeConflicts(local: OrderEntity, remote: OrderEntity): OrderEntity
}

/**
 * Last Write Wins Strategy
 * Remote data wins if it's more recent
 */
class LastWriteWinsStrategy : SyncStrategy {
    override suspend fun sync(local: OrderEntity, remote: OrderEntity): OrderEntity {
        return if (remote.serverUpdatedAt > local.serverUpdatedAt) {
            remote.copy(syncStatus = "SYNCED", conflictResolved = true)
        } else {
            local.copy(syncStatus = "SYNCED", conflictResolved = true)
        }
    }

    override suspend fun mergeConflicts(local: OrderEntity, remote: OrderEntity): OrderEntity {
        return sync(local, remote)
    }
}

/**
 * Local First Strategy
 * Local changes are preserved
 */
class LocalFirstStrategy : SyncStrategy {
    override suspend fun sync(local: OrderEntity, remote: OrderEntity): OrderEntity {
        return local.copy(
            syncStatus = "SYNCED",
            conflictResolved = true,
            serverUpdatedAt = remote.serverUpdatedAt
        )
    }

    override suspend fun mergeConflicts(local: OrderEntity, remote: OrderEntity): OrderEntity {
        return sync(local, remote)
    }
}

/**
 * Remote First Strategy
 * Remote changes always win
 */
class RemoteFirstStrategy : SyncStrategy {
    override suspend fun sync(local: OrderEntity, remote: OrderEntity): OrderEntity {
        return remote.copy(syncStatus = "SYNCED", conflictResolved = true)
    }

    override suspend fun mergeConflicts(local: OrderEntity, remote: OrderEntity): OrderEntity {
        return sync(local, remote)
    }
}
