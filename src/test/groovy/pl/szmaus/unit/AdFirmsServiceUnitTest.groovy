package pl.szmaus.unit

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.annotation.DirtiesContext.ClassMode
import org.springframework.test.context.ActiveProfiles
import pl.szmaus.primary.entity.AdFirms
import pl.szmaus.primary.repository.AdFirmsRepository
import pl.szmaus.primary.service.AdFirmsService
import pl.szmaus.secondary.entity.AdditionlFilesReceivedDocuments
import pl.szmaus.secondary.repository.AdditionlFilesReceivedDocumentsRepository
import pl.szmaus.secondary.service.AdditionlFilesReceivedDocumentsService
import spock.lang.Specification
import spock.lang.Unroll

import static java.time.LocalDate.now

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)

class AdFirmsServiceUnitTest extends Specification {

	AdFirmsService adFirmsService
	AdFirmsRepository adFirmsRepository=Mock()

	def setup() {
		adFirmsService = new AdFirmsService(
				adFirmsRepository
		)
	}

  	@Unroll
	def "VerificationIfTaxIdIsValid"()
	{
		when: 'put data'
				adFirmsService.verificationIfTaxIdIsValid()
		then: 1 *  adFirmsRepository.findAll() >> Arrays.asList(AdFirms.builder()
						.id(1)
						.number(100)
						.fullname("Test1")
						.firmEmailAddress("test1@interia.pl")
						.taxId("5555555555")
						.build(), AdFirms.builder()
						.id(2)
						.number(200)
						.fullname("Test2")
						.firmEmailAddress("test2@interia.pl")
						.taxId("222222222")
						.build())
	}

//	@Unroll
//	def "VerificationIfTaxIdIsValid where taxId=null"() {
//		given:
//		adFirmsRepository.findAll() >> Arrays.asList(AdFirms.builder()
//				.id(1)
//				.number(100)
//				.fullname("Test1")
//				.firmEmailAddress("test1@interia.pl")
//				.taxId(taxId)
//				.build())
//		when: 'put data'
//				adFirmsService.verificationIfTaxIdIsValid()
//		then:
//				thrown IllegalArgumentException
//		where:
//				taxId = null
//	}

	@Unroll
	def "ifEmailAdressExists where adFirm=#adFirm"()
	{
		given:
		AdFirms adFirm = AdFirms.builder()
				.id(id)
				.number(number)
				.fullname(fullname)
				.firmEmailAddress(firmEmailAddress)
				.taxId(taxId)
				.build()
		when: 'put data'
		def item = adFirmsService.ifEmailAdressExists(adFirm)
		then: 'result is not null'
		item==result
		where:
		id		|		number	|		fullname		|		firmEmailAddress		|		taxId		|		result
		1		|		100		|		"Test1"			|		null					|		"test1"		|		false
		1		|		100		|		"Test1"			|		"test1@interia.pl"		|		"taxIdtest1"|		true
	}

	def "ifSizeOfAdFirmsListIsMoreThenOne should return true where firmsList=#firmsList and taxId=#taxId"() {
		given:
		List<AdFirms> listOfFirms = Arrays.asList(AdFirms.builder()
				.id(1)
				.number(100)
				.fullname("Test1")
				.firmEmailAddress("test1@gmil.com")
				.taxId("111111111")
				.build(), AdFirms.builder()
				.id(2)
				.number(200)
				.fullname("test2")
				.firmEmailAddress("test2@gmail.com")
				.taxId("111111111")
				.build())

		when: 'put data'
			def item = adFirmsService.ifSizeOfAdFirmsListIsMoreThenOne(listOfFirms)
		then: 'result is not null'
			item == true
	}

	def "ifSizeOfAdFirmsListIsMoreThenOne should return false where firmsList=#firmsList and taxId=#taxId"() {
		given:
		List<AdFirms> listOfFirms = Arrays.asList(AdFirms.builder()
				.id(1)
				.number(100)
				.fullname("Test1")
				.firmEmailAddress("test1@gmil.com")
				.taxId("111111111")
				.build())
		when: 'put data'
		def item = adFirmsService.ifSizeOfAdFirmsListIsMoreThenOne(listOfFirms)
		then: 'result is not null'
		item == false
	}

}