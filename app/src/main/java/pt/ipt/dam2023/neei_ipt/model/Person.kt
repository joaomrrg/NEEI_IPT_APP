package pt.ipt.dam2023.neei_ipt.model

import java.util.Date

/**
 *  Dados pessoais de um utilizador
 */
class Person (
    val id: Int,
    val name: String,
    val surname: String,
    val birthDate: Date?,
    val gender: String?,
    val linkedIn: String?,
    val github: String?,
    val numAluno: String?,
    val groupId: Int,
    val userId: Int
)