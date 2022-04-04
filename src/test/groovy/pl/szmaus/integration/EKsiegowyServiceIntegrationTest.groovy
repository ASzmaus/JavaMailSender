package pl.szmaus.integration

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.annotation.DirtiesContext.ClassMode
import org.springframework.test.context.ActiveProfiles
import pl.szmaus.third.service.EKsiegowyService
import spock.lang.Specification

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
class EKsiegowyServiceIntegrationTest extends Specification {
	   
	@Autowired
	EKsiegowyService eKsiegowyService
	

	def "get list of eKsiegowy" () {
		when: 'get data '
			def item = eKsiegowyService.eKsiegowyList()
		then: 'result is not null'
			item!=null
		and: 'items contains a proper eKsiegowy status'
			item[0].getIdEKsiegowyStatus()==4
		and: 'items contains a proper eksiegowy status'
			 item[1].getIdEKsiegowyStatus()==4
	}
}