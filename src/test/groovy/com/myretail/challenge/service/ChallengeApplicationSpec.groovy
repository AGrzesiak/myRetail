package com.myretail.challenge.service

import org.apache.http.impl.client.CloseableHttpClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest
class ChallengeApplicationSpec extends Specification {

    @Autowired
    CloseableHttpClient httpClient

    def "test able to load bean"() {
        expect:
        httpClient
    }
}
