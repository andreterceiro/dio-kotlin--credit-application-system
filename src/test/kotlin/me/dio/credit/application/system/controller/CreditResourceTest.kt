package me.dio.credit.application.system.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.InternalPlatformDsl.toArray
import jakarta.validation.constraints.Future
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import me.dio.credit.application.system.controller.CustomerResourceTest.Companion
import me.dio.credit.application.system.dto.request.CreditDto
import me.dio.credit.application.system.dto.request.CustomerDto
import me.dio.credit.application.system.dto.request.CustomerUpdateDto
import me.dio.credit.application.system.entity.Address
import me.dio.credit.application.system.entity.Customer
import me.dio.credit.application.system.repository.CreditRepository
import me.dio.credit.application.system.repository.CustomerRepository
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
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
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.Assert

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@ContextConfiguration
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CreditResourceTest {
    @Autowired
    private lateinit var creditRepository: CreditRepository

    @Autowired
    private lateinit var customerRepository: CustomerRepository

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private var customerId: Long = 0

    companion object {
        const val URL: String = "/api/credits"
    }

    @BeforeEach
    @Transactional
    fun setup() {
        creditRepository.deleteAll()
        customerRepository.deleteAll()
    }

    @AfterEach
    fun tearDown() {
        creditRepository.deleteAll()
        customerRepository.deleteAll()
    }

    @Test
    fun `should create a credit and return 201 status`() {
        //given
        val customer = customerRepository.save(builderCustomerDto().toEntity())
        this.customerId = customer.id!!
        val creditDto: CreditDto = builderCreditDto(customerId = this.customerId)
        val valueAsString: String = objectMapper.writeValueAsString(creditDto)

        //when

        //then
        mockMvc.perform(
            MockMvcRequestBuilders.post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(valueAsString)
        ).andExpect(MockMvcResultMatchers.status().isCreated)
    }

    @Test
    fun `should not save a credit with a creditValue = 0 and return 400 status`() {
        //given
        val creditDto: CreditDto = builderCreditDto(creditValue = 0.toBigDecimal())
        val valueAsString: String = objectMapper.writeValueAsString(creditDto)
        //when
        //then
        mockMvc.perform(
            MockMvcRequestBuilders.post(me.dio.credit.application.system.controller.CustomerResourceTest.URL)
                .content(valueAsString)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    fun `find all by customerId`() {
        //given
        val customer = customerRepository.save(builderCustomerDto().toEntity())
        this.customerId = customer.id!!
        val creditDto1: CreditDto = builderCreditDto(customerId = this.customerId)
        val creditDto2: CreditDto = builderCreditDto(customerId = this.customerId)
        creditRepository.save(creditDto1.toEntity())
        creditRepository.save(creditDto2.toEntity())

        //when

        //then
        mockMvc.perform(
            MockMvcRequestBuilders.get("${URL}?customerId=${customerId}")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(MockMvcResultHandlers.print())
        //Assertions.assertThat(MockMvcResultMatchers.content().toString().toArray().size == 2)
    }

    @Test
    fun `must find using a credit code`(){
        // given
        val customer = customerRepository.save(builderCustomerDto().toEntity())
        this.customerId = customer.id!!
        val creditDto: CreditDto = builderCreditDto(customerId = this.customerId)
        val credit = creditDto.toEntity()
        creditRepository.save(credit)
        val creditCode = credit.creditCode

        // when

        // then
        mockMvc.perform(
            MockMvcRequestBuilders.get("${URL}/${creditCode}?customerId=${customerId}")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(MockMvcResultHandlers.print())
    }

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

    private fun builderCustomerDto(
        firstName: String = "Cami",
        lastName: String = "Cavalcante",
        cpf: String = "28475934625",
        email: String = "camila@email.com",
        income: BigDecimal = BigDecimal.valueOf(1000.0),
        password: String = "1234",
        zipCode: String = "000000",
        street: String = "Rua da Cami, 123",
    ) = CustomerDto(
        firstName = firstName,
        lastName = lastName,
        cpf = cpf,
        email = email,
        income = income,
        password = password,
        zipCode = zipCode,
        street = street
    )
}
