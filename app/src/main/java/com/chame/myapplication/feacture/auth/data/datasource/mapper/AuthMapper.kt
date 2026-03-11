package com.chame.myapplication.feacture.auth.data.datasource.mapper

import com.chame.myapplication.feacture.auth.data.datasource.model.AuthResponseDto
import com.chame.myapplication.feacture.auth.domian.entities.AuthResponse


fun AuthResponseDto.toDomain(): AuthResponse = AuthResponse(
    accessToken = accessToken,
    tokenType = tokenType,
    userId = usuario.id,
    email = usuario.email,
    nombre = usuario.nombre,
    rol = usuario.rol
)
