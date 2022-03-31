package pl.szmaus.unit

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.annotation.DirtiesContext.ClassMode
import org.springframework.test.context.ActiveProfiles
import pl.szmaus.secondary.entity.GmFs
import pl.szmaus.secondary.repository.GmFsRepository
import pl.szmaus.secondary.service.GmFsService
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDate

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)

class GmFsServiceUnitTest extends Specification {

	GmFsService gmFsService

	GmFsRepository gmFsRepository = Mock()

	def setup() {
		gmFsService = new GmFsService(
				gmFsRepository
		)
	}

  	@Unroll
	def "paymentForInvoices transfer"()
	{
			when: 'put data'
				def item = gmFsService.paymentForInvoices(s)
			then:
				item=="</strong>Numer konta bankowego: <strong> Bank XX XXXX XXXX XXXX XXXX XXXX XXXX </strong>";
			where:
				s="transfer";
	}

	@Unroll
	def "paymentForInvoices cash"()
	{
		when: 'put data'
		def item = gmFsService.paymentForInvoices(s)
		then:
		item=="</strong>Sposób płatności: <strong>Gotowka</strong>";
		where:
		s="Gotowka";
	}

}