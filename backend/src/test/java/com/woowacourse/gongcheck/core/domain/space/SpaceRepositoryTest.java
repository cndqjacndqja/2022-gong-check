package com.woowacourse.gongcheck.core.domain.space;

import static com.woowacourse.gongcheck.fixture.FixtureFactory.Host_생성;
import static com.woowacourse.gongcheck.fixture.FixtureFactory.Space_생성;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.woowacourse.gongcheck.config.JpaConfig;
import com.woowacourse.gongcheck.core.domain.host.Host;
import com.woowacourse.gongcheck.core.domain.host.HostRepository;
import com.woowacourse.gongcheck.core.domain.vo.Name;
import com.woowacourse.gongcheck.exception.NotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(JpaConfig.class)
@DisplayName("SpaceRepository 클래스")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class SpaceRepositoryTest {

    @Autowired
    private HostRepository hostRepository;

    @Autowired
    private SpaceRepository spaceRepository;


    @Nested
    class save_메서드는 {

        @Nested
        class 입력받은_Space를_저장할_때 {

            private Space space;

            @BeforeEach
            void setUp() {
                Host host = hostRepository.save(Host_생성("1234", 1234L));
                space = Space_생성(host, "잠실");
            }

            @Test
            void 생성_시간도_함께_저장한다() {
                LocalDateTime timeThatBeforeSave = LocalDateTime.now();
                Space savedSpace = spaceRepository.save(space);
                assertThat(savedSpace.getCreatedAt()).isAfter(timeThatBeforeSave);
            }
        }
    }

    @Nested
    class findAllByHost_메서드는 {

        @Nested
        class 입력받은_Host가_Space를_가지고_있을_때 {

            private Host host;
            private List<Space> expected;

            @BeforeEach
            void setUp() {
                host = hostRepository.save(Host_생성("1234", 1234L));
                expected = spaceRepository.saveAll(List.of(
                        Space_생성(host, "잠실 캠퍼스"),
                        Space_생성(host, "선릉 캠퍼스"),
                        Space_생성(host, "양평같은방"))
                );
            }

            @Test
            void 가지고_있는_Space를_모두_조회한다() {
                List<Space> actual = spaceRepository.findAllByHost(host);

                assertThat(actual).isEqualTo(expected);
            }
        }
    }

    @Nested
    class getByHostAndId_메서드는 {

        @Nested
        class 다른_Host가_소유한_Space_id를_입력받은_경우 {

            private Long spaceId;
            private Host anotherHost;

            @BeforeEach
            void setUp() {
                Host host = hostRepository.save(Host_생성("1234", 1234L));
                spaceId = spaceRepository.save(Space_생성(host, "잠실 캠퍼스")).getId();
                anotherHost = hostRepository.save(Host_생성("4567", 4567L));

            }

            @Test
            void 예외를_발생시킨다() {
                assertThatThrownBy(() ->
                        spaceRepository.getByHostAndId(anotherHost, spaceId))
                        .isInstanceOf(NotFoundException.class)
                        .hasMessageContaining("존재하지 않는 공간입니다.");
            }
        }

        @Nested
        class 존재하지_않는_Space_id를_입력받은_경우 {

            private static final long NON_EXIST_SPACE_ID = 0L;

            private Host host;

            @BeforeEach
            void setUp() {
                host = hostRepository.save(Host_생성("1234", 1234L));
            }

            @Test
            void 예외를_발생시킨다() {
                assertThatThrownBy(() ->
                        spaceRepository.getByHostAndId(host, NON_EXIST_SPACE_ID))
                        .isInstanceOf(NotFoundException.class)
                        .hasMessageContaining("존재하지 않는 공간입니다.");
            }
        }

        @Nested
        class 입력받은_Host가_입력받은_Space_id를_가지고_있는_경우 {

            private Space space;
            private Long spaceId;
            private Host host;

            @BeforeEach
            void setUp() {
                host = hostRepository.save(Host_생성("1234", 1234L));
                space = spaceRepository.save(Space_생성(host, "잠실 캠퍼스"));
                spaceId = space.getId();
            }

            @Test
            void Space를_반환한다() {
                Space actual = spaceRepository.getByHostAndId(host, spaceId);
                assertThat(actual).isEqualTo(space);
            }
        }
    }

    @Nested
    class existsByHostAndName_메서드는 {
        @Nested
        class 입력받은_이름과_같은_이름을_가진_Space를_가지고_있는_경우 {

            private static final String EXIST_SPACE_NAME = "잠실 캠퍼스";

            private Host host;
            private Name name;

            @BeforeEach
            void setUp() {
                host = hostRepository.save(Host_생성("1234", 1234L));
                spaceRepository.save(Space_생성(host, EXIST_SPACE_NAME));
                name = new Name(EXIST_SPACE_NAME);
            }

            @Test
            void 참을_반환한다() {
                boolean actual = spaceRepository.existsByHostAndName(host, name);
                assertThat(actual).isTrue();
            }
        }

        @Nested
        class 입력받은_이름과_같은_이름을_가진_Space를_가지고_있지_않은_경우 {

            private Host host;
            private Name name;

            @BeforeEach
            void setUp() {
                host = hostRepository.save(Host_생성("1234", 1234L));
                name = new Name("없는Space이름");
            }

            @Test
            void 거짓을_반환한다() {
                boolean actual = spaceRepository.existsByHostAndName(host, name);
                assertThat(actual).isFalse();
            }
        }
    }
}
