package ru.biderman.s1000dpmviewer.services;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.acls.domain.EhCacheBasedAclCache;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.jdbc.LookupStrategy;
import org.springframework.security.acls.model.AclDataAccessException;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import ru.biderman.s1000dpmviewer.domain.Publication;
import ru.biderman.s1000dpmviewer.domain.PublicationDetails;
import ru.biderman.s1000dpmviewer.domain.PublicationViewAuthorizations;
import ru.biderman.s1000dpmviewer.exceptions.PublicationNotFoundException;
import ru.biderman.s1000dpmviewer.testutils.TestConsts;
import ru.biderman.s1000dpmviewer.testutils.WithMockAdmin;
import ru.biderman.s1000dpmviewer.testutils.WithMockEditor;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.biderman.s1000dpmviewer.testutils.TestConsts.START_PUBLICATION_ID;
import static ru.biderman.s1000dpmviewer.testutils.TestUtils.getDataFile;

@SpringBootTest(properties = {"spring.main.allow-bean-definition-overriding=true"})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@DisplayName("Интеграционный тест назначения прав на просмотр")
public class AuthorizationServiceIntegrationTest {
    @Autowired
    private PublicationService publicationService;

    @Autowired
    private PublicationDetailsService detailsService;

    @Autowired
    private AuthorizationService authorizationService;

    private static final String USER_WITH_VIEW_AUTHORIZATION = "userView";

    @TestConfiguration
    static class TestConfig {
        @Primary
        @Bean
        public EhCacheManagerFactoryBean aclCacheManager() {
            EhCacheManagerFactoryBean factoryBean = new EhCacheManagerFactoryBean();
            factoryBean.setShared(false);
            factoryBean.setAcceptExisting(true);
            return factoryBean;
        }

        @Primary
        @Bean
        public JdbcMutableAclService aclService(DataSource dataSource, LookupStrategy lookupStrategy, EhCacheBasedAclCache aclCache) {
            return new JdbcMutableAclService(dataSource, lookupStrategy, aclCache);
        }
    }

    @Order(0)
    @DisplayName("должен создавать публикацию")
    @Test
    @WithMockEditor
    void shouldCreatePublication() throws Exception{
        try(
                FileInputStream fileInputStream = new FileInputStream(getDataFile(TestConsts.TEST_PUBLICATION_FILENAME))
        ) {
            Publication publication = publicationService.add(fileInputStream);
            assertThat(publication)
                    .satisfies(p -> assertThat(p.getId()).isGreaterThan(0));
        }
    }

    @Order(1)
    @DisplayName("должен показывать созданную публикацию админу")
    @Test
    @WithMockAdmin
    void shouldShowPublicationToAdmin() {
        List<PublicationDetails> detailsList = detailsService.findAll();
        assertThat(detailsList)
                .hasSize(1);
    }

    @Order(2)
    @DisplayName("не должен показывать созданную публикацию обычному пользователю")
    @Test
    @WithMockUser
    void shouldNotShowToSimpleUser() {
        List<PublicationDetails> detailsList = detailsService.findAll();
        assertThat(detailsList)
                .hasSize(0);
    }

    @Order(3)
    @DisplayName("не должен позволять менять права обычному пользователю")
    @Test
    @WithMockUser
    void shouldNotAllowChangeRightsToSimpleUser() {
        assertThrows(AclDataAccessException.class,
                () -> authorizationService.setViewAuthorizations(START_PUBLICATION_ID, PublicationViewAuthorizations.createForAll()));
    }

    @Order(4)
    @DisplayName("должен менять права на просмотр для всех")
    @Test
    @WithMockAdmin
    void shouldSetAnonymousAuthorization() throws Exception{
        authorizationService.setViewAuthorizations(START_PUBLICATION_ID, PublicationViewAuthorizations.createForAll());
    }

    @Order(5)
    @DisplayName("должен показывать публикацию анонимному пользователю")
    @Test
    @WithAnonymousUser
    void shouldShowToAnonymous() {
        List<PublicationDetails> detailsList = detailsService.findAll();
        assertThat(detailsList)
                .hasSize(1);
    }

    @Order(6)
    @DisplayName("должен показывать публикацию обычному пользователю")
    @Test
    @WithMockUser
    void shouldShowToSimpleUser() {
        List<PublicationDetails> detailsList = detailsService.findAll();
        assertThat(detailsList)
                .hasSize(1);
    }

    @Order(7)
    @DisplayName("должен считывать права для всех")
    @Test
    @WithMockAdmin
    void shouldGetAuthorizationsForAll() throws Exception{
        PublicationViewAuthorizations authorizations = authorizationService.getViewAuthorizations(START_PUBLICATION_ID);
        assertThat(authorizations)
                .hasFieldOrPropertyWithValue("permitAll", true)
                .extracting(a -> a.getUsernameList().size())
                .isEqualTo(0);
    }

    @Order(8)
    @DisplayName("должен назначать права для просмотра отдельному пользователю")
    @Test
    @WithMockAdmin
    void shouldSetAuthorizations() throws Exception{
        authorizationService.setViewAuthorizations(START_PUBLICATION_ID,
                PublicationViewAuthorizations.createForUsers(
                    Collections.singletonList(USER_WITH_VIEW_AUTHORIZATION)));
    }

    @Order(9)
    @DisplayName("должен показывать публикацию пользователю, получившему права")
    @Test
    @WithMockUser(username = USER_WITH_VIEW_AUTHORIZATION)
    void shouldShowToUserWithAuthorization() {
        List<PublicationDetails> detailsList = detailsService.findAll();
        assertThat(detailsList)
                .hasSize(1);
    }

    @Order(10)
    @DisplayName("должен не показывать публикацию пользователю без прав")
    @Test
    @WithMockUser
    void shouldNotShowToUserWithoutAuthorization() {
        List<PublicationDetails> detailsList = detailsService.findAll();
        assertThat(detailsList)
                .hasSize(0);
    }

    @Order(11)
    @DisplayName("должен не показывать публикацию анонимному пользователю")
    @Test
    @WithAnonymousUser
    void shouldNotShowToAnonymousUser() {
        List<PublicationDetails> detailsList = detailsService.findAll();
        assertThat(detailsList)
                .hasSize(0);
    }

    @Order(12)
    @DisplayName("должен считывать права для пользователей")
    @Test
    @WithMockAdmin
    void shouldGetAuthorizationsForUsers() throws Exception {
        PublicationViewAuthorizations authorizations = authorizationService.getViewAuthorizations(START_PUBLICATION_ID);
        assertThat(authorizations)
                .hasFieldOrPropertyWithValue("permitAll", false)
                .satisfies(a -> assertThat(a.getUsernameList()).containsExactly(USER_WITH_VIEW_AUTHORIZATION));
    }

    @Order(50)
    @DisplayName("должен выбрасывать исключение, если публикации нет")
    @Test
    @WithMockAdmin
    void shouldThrowExceptionIfPublicationNotFound() {
        assertThrows(PublicationNotFoundException.class,
                () -> authorizationService.getViewAuthorizations(START_PUBLICATION_ID + 1));
    }

    @Order(51)
    @DisplayName("должен выбрасывать исключение при сохранении, если публикации нет")
    @Test
    @WithMockAdmin
    void shouldThrowExceptionIfSetAuthorizationAndPublicationNotFound() {
        assertThrows(PublicationNotFoundException.class,
                () -> authorizationService.setViewAuthorizations(START_PUBLICATION_ID + 1,
                        PublicationViewAuthorizations.createForAll()));
    }
}
