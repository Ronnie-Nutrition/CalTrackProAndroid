package com.easyaiflows.caltrackpro.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.easyaiflows.caltrackpro.data.local.entity.ScannedBarcodeEntity

/**
 * Data Access Object for cached barcode scan results.
 */
@Dao
interface ScannedBarcodeDao {

    /**
     * Insert or update a scanned barcode result.
     * If the barcode already exists, replace with new data.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(scannedBarcode: ScannedBarcodeEntity)

    /**
     * Get a cached barcode result by barcode string.
     */
    @Query("SELECT * FROM scanned_barcodes WHERE barcode = :barcode")
    suspend fun getByBarcode(barcode: String): ScannedBarcodeEntity?

    /**
     * Delete a cached barcode result.
     */
    @Query("DELETE FROM scanned_barcodes WHERE barcode = :barcode")
    suspend fun delete(barcode: String)

    /**
     * Delete all cached barcode results.
     */
    @Query("DELETE FROM scanned_barcodes")
    suspend fun deleteAll()

    /**
     * Delete cached entries older than the specified timestamp.
     * Used for cache cleanup.
     */
    @Query("DELETE FROM scanned_barcodes WHERE cachedAt < :olderThan")
    suspend fun deleteOlderThan(olderThan: Long)

    /**
     * Get the count of cached barcodes.
     */
    @Query("SELECT COUNT(*) FROM scanned_barcodes")
    suspend fun getCount(): Int
}
