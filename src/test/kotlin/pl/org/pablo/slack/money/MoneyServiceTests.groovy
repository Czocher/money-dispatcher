package pl.org.pablo.slack.money

import pl.org.pablo.slack.money.graph.BalanceRelationship
import pl.org.pablo.slack.money.graph.MoneyRelationshipRepository
import pl.org.pablo.slack.money.graph.PayRelationship
import pl.org.pablo.slack.money.graph.UserEntity
import pl.org.pablo.slack.money.money.AddDto
import pl.org.pablo.slack.money.money.MoneyServiceImpl
import pl.org.pablo.slack.money.user.UserService
import spock.lang.Specification

import java.time.LocalDateTime

class MoneyServiceTests extends Specification {

    def userR = Mock(UserService)
    def moneyR = Mock(MoneyRelationshipRepository)
    def cut = new MoneyServiceImpl(userR, moneyR)

    def "When new users are making payment create one pay and balance relationships"() {
        given:
        def param = new AddDto("1", "2", 10, null)
        def u1 = new UserEntity("1", [], [], [], [], 1)
        def u2 = new UserEntity("2", [], [], [], [], 2)
        when:
        userR.getOrCreate("1") >> u1
        userR.getOrCreate("2") >> u2
        cut.addMoney(param)
        then:
        1 * moneyR.save(new PayRelationship(u1, u2, 10, null, LocalDateTime.now(), null))
        1 * moneyR.save(new BalanceRelationship(u2, u1, 10, null))
        0 * _
    }

    def "When new users are making payment with negative value create pay and balance from u2 to u1"() {
        given:
        def param = new AddDto("1", "2", -10, null)
        def u1 = new UserEntity("1", [], [], [], [], 1)
        def u2 = new UserEntity("2", [], [], [], [], 2)
        when:
        userR.getOrCreate("1") >> u1
        userR.getOrCreate("2") >> u2
        cut.addMoney(param)
        then:
        1 * moneyR.save(new PayRelationship(u2, u1, 10, null, LocalDateTime.now(), null))
        1 * moneyR.save(new BalanceRelationship(u1, u2, 10, null))
        0 * _
    }

}
