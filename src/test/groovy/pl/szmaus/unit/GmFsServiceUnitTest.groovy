package pl.szmaus.unit

import pl.szmaus.secondary.repository.GmFsRepository
import pl.szmaus.secondary.service.GmFsService
import spock.lang.Specification
import spock.lang.Unroll

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