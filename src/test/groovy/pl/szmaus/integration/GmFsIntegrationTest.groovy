package pl.szmaus.integration

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles
import org.springframework.security.test.context.support.WithUserDetails
import pl.szmaus.secondary.repository.GmFsRepository
import pl.szmaus.secondary.service.GmFsService
import spock.lang.Specification;
import org.springframework.test.annotation.DirtiesContext.ClassMode;


@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
class GmFsIntegrationTest extends Specification {
	   
	@Autowired
	GmFsService gmFsService
	

	def "get list of gmFs" () {
		when: 'get data '
			def item = gmFsService.currentMonthGmFsList();
		then: 'result is not null'
			item!=null
		and: 'items contains a proper club'
			item[0].number=="01/03/2022"
		and: 'items contains a proper club'
			 item[1].number=="02/03/2022"
	}
}