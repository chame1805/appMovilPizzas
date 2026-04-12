package com.chame.myapplication.feacture.register.data.datasource.mapper

import com.chame.myapplication.feacture.register.data.datasource.model.RegisterResponseDto
import com.chame.myapplication.feacture.register.domain.entities.RegisteResponse


fun RegisterResponseDto.toDomain(): RegisteResponse = RegisteResponse(
    access_token = accessToken,
    token_type = tokenType
)
