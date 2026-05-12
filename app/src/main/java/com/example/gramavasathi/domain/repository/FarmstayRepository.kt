package com.example.gramavasathi.domain.repository

import com.example.gramavasathi.domain.model.Farmstay
import kotlinx.coroutines.flow.Flow

interface FarmstayRepository {
    fun getFarmstays(filter: String): Flow<List<Farmstay>>
    fun getFarmstayById(id: String): Flow<Farmstay?>
    fun searchFarmstays(query: String, district: String?): Flow<List<Farmstay>>
}
