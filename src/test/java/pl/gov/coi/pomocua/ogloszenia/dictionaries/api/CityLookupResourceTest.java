package pl.gov.coi.pomocua.ogloszenia.dictionaries.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.gov.coi.pomocua.ogloszenia.dictionaries.domain.City;
import pl.gov.coi.pomocua.ogloszenia.dictionaries.domain.CityRepository;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CityLookupResourceTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CityRepository cityRepository;

    @Test
    void shouldReturnEmptyResponse() {
        // when:
        ResponseEntity<CityLookupResponse> response = restTemplate.getForEntity("/api/dictionaries/cities/mazowieckie/x", CityLookupResponse.class);

        // then:
        assertThat(response.hasBody()).isFalse();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void shouldReturnListOfMatchingCities() {
        // given:
        givenFollowingCitiesExists("mazowieckie/war1", "mazowieckie/war2");

        // when:
        ResponseEntity<CityLookupResponse> response = restTemplate.getForEntity("/api/dictionaries/cities/mazowieckie/War", CityLookupResponse.class);

        // then:
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().cities()).contains("war1", "war2");
    }

    @Test
    void shouldImportCitiesFromFile() {
        // when:
        ResponseEntity<CityLookupResponse> response = restTemplate.getForEntity("/api/dictionaries/cities/ŚLĄSKIE/Bi", CityLookupResponse.class);

        // then:
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().cities()).contains("bieruń", "bielsko-biała");
    }

    // ---

    private void givenFollowingCitiesExists(String... values) {
        Stream.of(values).forEach(value -> {
            String[] parts = value.split("/");
            cityRepository.save(new City(parts[1], parts[0]));
        });
    }
}