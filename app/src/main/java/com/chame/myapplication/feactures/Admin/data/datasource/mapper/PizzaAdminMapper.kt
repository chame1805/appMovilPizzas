package com.chame.myapplication.feactures.Admin.data.datasource.mapper

import com.chame.myapplication.feactures.Admin.data.datasource.model.PizzaDto
import com.chame.myapplication.feactures.Admin.domain.entities.Pizza

fun PizzaDto.toDomain(): Pizza = Pizza(
    id = id,
    nombre = nombre,
    precio = precio
)

fun Pizza.toDto(): PizzaDto = PizzaDto(
    id = id,
    nombre = nombre,
    precio = precio
)
