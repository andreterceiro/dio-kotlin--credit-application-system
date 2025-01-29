package me.dio.credit.application.system.controller

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.validation.constraints.Future
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import me.dio.credit.application.system.dto.request.CreditDto
import me.dio.credit.application.system.dto.request.CustomerDto
import me.dio.credit.application.system.dto.request.CustomerUpdateDto
import me.dio.credit.application.system.entity.Customer
import me.dio.credit.application.system.repository.CreditRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.math.BigDecimal
import java.time.LocalDate
import java.util.Random

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@ContextConfiguration
class CreditResourceTest {
    @Autowired
    private lateinit var creditRepository: CreditRepository

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    companion object {
        const val URL: String = "/api/customers"
    }

    @BeforeEach
    fun setup() = creditRepository.deleteAll()

    @AfterEach
    fun tearDown() = creditRepository.deleteAll()

    private fun builderCreditDto(
        creditValue: BigDecimal = 1000.toBigDecimal(),
        dayFirstOfInstallment: LocalDate = LocalDate.now().plusDays(1),
        numberOfInstalments: Int = 2,
        customerId: Long = 1L
    ) = CreditDto(
        creditValue = creditValue,
        dayFirstOfInstallment = dayFirstOfInstallment,
        numberOfInstallments = numberOfInstalments,
        customerId = customerId,
    )
}
