package com.woowacourse.gongcheck.core.application;

import static com.woowacourse.gongcheck.fixture.FixtureFactory.Host_생성;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.woowacourse.gongcheck.ApplicationTest;
import com.woowacourse.gongcheck.SupportRepository;
import com.woowacourse.gongcheck.auth.application.EntranceCodeProvider;
import com.woowacourse.gongcheck.core.domain.host.Host;
import com.woowacourse.gongcheck.core.presentation.request.SpacePasswordChangeRequest;
import com.woowacourse.gongcheck.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@ApplicationTest
@DisplayName("HostService 클래스")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HostServiceTest {

    @Autowired
    private HostService hostService;

    @Autowired
    private SupportRepository repository;

    @Autowired
    private EntranceCodeProvider entranceCodeProvider;

    @Nested
    class changeSpacePassword_메소드는 {

        @Nested
        class 존재하는_Host의_id와_수정할_패스워드를_받는_경우 {

            private static final String ORIGIN_PASSWORD = "1234";
            private static final String CHANGING_PASSWORD = "4567";
            private static final long GITHUB_ID = 1234L;

            private SpacePasswordChangeRequest spacePasswordChangeRequest;
            private Long hostId;

            @BeforeEach
            void setUp() {
                spacePasswordChangeRequest = new SpacePasswordChangeRequest(CHANGING_PASSWORD);
                hostId = repository.save(Host_생성(ORIGIN_PASSWORD, GITHUB_ID))
                        .getId();
            }

            @Test
            void 패스워드를_수정한다() {
                hostService.changeSpacePassword(hostId, spacePasswordChangeRequest);
                Host actual = repository.getById(Host.class, hostId);

                assertThat(actual.getSpacePassword().getValue()).isEqualTo(CHANGING_PASSWORD);
            }
        }

        @Nested
        class 존재하지_않는_Host의_id를_받는_경우 {

            private static final String CHANGING_PASSWORD = "4567";

            private SpacePasswordChangeRequest spacePasswordChangeRequest;
            private Long hostId;

            @BeforeEach
            void setUp() {
                spacePasswordChangeRequest = new SpacePasswordChangeRequest(CHANGING_PASSWORD);
                hostId = 0L;
            }

            @Test
            void 예외를_발생시킨다() {
                assertThatThrownBy(() -> hostService.changeSpacePassword(hostId, spacePasswordChangeRequest))
                        .isInstanceOf(NotFoundException.class)
                        .hasMessage("존재하지 않는 호스트입니다.");
            }
        }
    }

    @Nested
    class createEntranceCode_메소드는 {

        @Nested
        class 존재하는_Host의_id를_받는_경우 {

            private Long hostId;
            private String expected;

            @BeforeEach
            void setUp() {
                hostId = repository.save(Host_생성("1234", 1111L))
                        .getId();
                expected = entranceCodeProvider.createEntranceCode(hostId);
            }

            @Test
            void 입장코드를_반환한다() {
                String actual = hostService.createEntranceCode(hostId);
                assertThat(actual).isEqualTo(expected);
            }
        }

        @Nested
        class 존재하지_않는_Host의_id를_받는_경우 {

            @Test
            void 예외를_발생시킨다() {
                assertThatThrownBy(() -> hostService.createEntranceCode(0L))
                        .isInstanceOf(NotFoundException.class)
                        .hasMessage("존재하지 않는 호스트입니다.");
            }
        }
    }
}
