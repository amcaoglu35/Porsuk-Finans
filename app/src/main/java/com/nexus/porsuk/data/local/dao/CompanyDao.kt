package com.nexus.porsuk.data.local.dao

import androidx.room.*
import com.nexus.porsuk.data.local.entity.CompanyEntity
import kotlinx.coroutines.flow.Flow

/**
 * Porsuk Finans — Şirketler / Hisse Senetleri DAO
 */
@Dao
interface CompanyDao {

    @Query("SELECT * FROM db_companies WHERE is_active = 1 ORDER BY symbol ASC")
    fun getAllCompanies(): Flow<List<CompanyEntity>>

    @Query("SELECT * FROM db_companies WHERE symbol = :symbol LIMIT 1")
    fun getCompanyBySymbol(symbol: String): Flow<CompanyEntity?>

    @Query("SELECT * FROM db_companies WHERE isin = :isin LIMIT 1")
    suspend fun getCompanyByIsin(isin: String): CompanyEntity?

    @Query("SELECT * FROM db_companies WHERE sector = :sector ORDER BY market_cap DESC")
    fun getCompaniesBySector(sector: String): Flow<List<CompanyEntity>>

    @Query("SELECT * FROM db_companies WHERE symbol LIKE '%' || :query || '%' OR company_name LIKE '%' || :query || '%'")
    fun searchCompanies(query: String): Flow<List<CompanyEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompany(company: CompanyEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompanies(companies: List<CompanyEntity>)

    @Update
    suspend fun updateCompany(company: CompanyEntity)

    @Delete
    suspend fun deleteCompany(company: CompanyEntity)

    @Query("DELETE FROM db_companies")
    suspend fun deleteAllCompanies()
}
