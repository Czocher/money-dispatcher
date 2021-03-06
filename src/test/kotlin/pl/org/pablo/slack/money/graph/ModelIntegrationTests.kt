package pl.org.pablo.slack.money.graph

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.neo4j.ogm.session.Session
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.neo4j.DataNeo4jTest
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@DataNeo4jTest
class ModelIntegrationTests {

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var moneyRelRepository: MoneyRelationshipRepository

    @Autowired
    lateinit var session: Session

    @Test
    fun basicPaymentTest() {
        val u1 = UserEntity("u1")
        val u2 = UserEntity("u2")

        val rel = PayRelationship(u1, u2, 10, "desc")
        moneyRelRepository.save(rel)

        session.clear()

        val result = userRepository.findByName("u1") ?: throw NullPointerException()
        val payRel = result.payed[0]
        assertEquals(u1.name, payRel.payer.name)
        assertEquals(u2.name, payRel.receiver.name)
        assertNotNull(payRel.date)
        assertNotNull(payRel.description)
    }

}