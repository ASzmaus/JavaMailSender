package pl.szmaus.unit

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.annotation.DirtiesContext.ClassMode
import org.springframework.test.context.ActiveProfiles
import pl.szmaus.exception.EntityNotFoundException
import pl.szmaus.primary.entity.AdFirms
import pl.szmaus.primary.repository.AdFirmsRepository
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

class AdditionlFilesReceivedDocumentsUnitTest extends Specification {

	AdditionlFilesReceivedDocumentsService additionlFilesReceivedDocumentsService
	AdFirmsRepository adFirmsRepository=Mock()
	AdditionlFilesReceivedDocumentsRepository additionlFilesReceivedDocumentsRepository=Mock()

	def setup() {
		additionlFilesReceivedDocumentsService = new AdditionlFilesReceivedDocumentsService(
				adFirmsRepository,
				additionlFilesReceivedDocumentsRepository
		)
	}

  	@Unroll
	def "CheckIfRecivedDocumentFromFirebird should return false where idFirm=#id and numberRaks=#numberRaks and month is not correct"()
	{
		given:
		adFirmsRepository.findById(id) >> Optional.of(AdFirms.builder()
				.id(id)
				.number(numberRaks.toInteger())
				.fullname("Test")
				.firmEmailAddress("test@interia.pl")
				.taxId("5555555555")
				.build())
		when: 'put data'
				def item = additionlFilesReceivedDocumentsService.checkIfRecivedDocumentFromFirebird(id)
		then: 'result is not null'
				item==false
				1 *  additionlFilesReceivedDocumentsRepository.findByNumber(numberRaks) >> AdditionlFilesReceivedDocuments.builder()
				.id(213)
				.idAdditionlFilesReceivedDocumentsStatus(201)
				.number(numberRaks)
				.name(now().toString().substring(0,7))
				.currency("PLN")
				.description("Test")
				.build()
		where:
				id		|		numberRaks
				358		|		"87"
	}

	@Unroll
	def "CheckIfRecivedDocumentFromFirebird should return false where idFirm=#id and numberRaks=#numberRaks and additionlFilesReceivedDocuments is null"()
	{
		given:
		additionlFilesReceivedDocumentsRepository.findByNumber(numberRaks) >> null

		adFirmsRepository.findById(id) >> Optional.of(AdFirms.builder()
				.id(358)
				.number(numberRaks.toInteger())
				.fullname("Test")
				.firmEmailAddress("test@interia.pl")
				.taxId("5555555555")
				.build())
		when: 'put data'
				def item = additionlFilesReceivedDocumentsService.checkIfRecivedDocumentFromFirebird(id)
		then: 'result is not null'
				item==false
				1 *  additionlFilesReceivedDocumentsRepository.findByNumber(numberRaks)
		where:
				id		|		numberRaks
				358		|		"87"
	}

	@Unroll
	def "CheckIfRecivedDocumentFromFirebird should return true where idFirm=#id and numberRaks#numberRaks"()
	{
		given:
				adFirmsRepository.findById(id) >> Optional.of(AdFirms.builder()
				.id(id)
				.number(numberRaks.toInteger())
				.fullname("Test")
				.firmEmailAddress("test@interia.pl")
				.taxId("5555555555")
				.build())
		when: 'put data'
				def item = additionlFilesReceivedDocumentsService.checkIfRecivedDocumentFromFirebird(id)
		then: 'result is not null'
				item==true
				1 *  additionlFilesReceivedDocumentsRepository.findByNumber(numberRaks) >> AdditionlFilesReceivedDocuments.builder()
						.id(213)
						.idAdditionlFilesReceivedDocumentsStatus(201)
						.number(numberRaks)
						.name(now().minusMonths(1).toString().substring(0,7))
						.currency("PLN")
						.description("Test")
						.build()
		where:
			id		|		numberRaks
			358		|		"87"
	}

	@Unroll
	def "CheckIfRecivedDocumentFromFirebird where Id is null and dFirm=#id and numberRaks#numberRaks"()
	{
		given:
		adFirmsRepository.findById(id) >> Optional.of(AdFirms.builder()
				.id(id)
				.number(numberRaks.toInteger())
				.fullname("Test")
				.firmEmailAddress("test@interia.pl")
				.taxId("5555555555")
				.build())
		additionlFilesReceivedDocumentsRepository.findByNumber(numberRaks) >> AdditionlFilesReceivedDocuments.builder()
				.id(213)
				.idAdditionlFilesReceivedDocumentsStatus(201)
				.number(numberRaks)
				.name(now().toString().substring(0,7))
				.currency("PLN")
				.description("Test")
				.build()
		when: 'put data'
				def item = additionlFilesReceivedDocumentsService.checkIfRecivedDocumentFromFirebird(id)
		then: 'thrown exception'
				thrown IllegalArgumentException
		where:
				id		|		numberRaks
				null	|		"87"
	}

	@Unroll
	def "CheckIfRecivedDocumentFromFirebird  numberRaks is null and dFirm=#id and numberRaks#numberRaks"()
	{
		given:
		adFirmsRepository.findById(id) >> Optional.of(AdFirms.builder()
				.id(id)
				.number(numberRaks)
				.fullname("Test")
				.firmEmailAddress("test@interia.pl")
				.taxId("5555555555")
				.build())
		when: 'put data'
		def item = additionlFilesReceivedDocumentsService.checkIfRecivedDocumentFromFirebird(id)
		then: 'thrown exception'
				thrown IllegalArgumentException
		where:
				id		|		numberRaks
				358		|		null
				null	|		null
	}
}