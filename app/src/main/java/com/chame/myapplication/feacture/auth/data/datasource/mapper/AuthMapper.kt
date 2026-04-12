package com.chame.myapplication.feacture.auth.data.datasource.mapper

import com.chame.myapplication.feacture.auth.data.datasource.model.AuthResponseDto
import com.chame.myapplication.feacture.auth.domian.entities.AuthResponse


fun AuthResponseDto.toDomain(): AuthResponse = AuthResponse(
    access_token = accessToken,
    token_type = tokenType
)
