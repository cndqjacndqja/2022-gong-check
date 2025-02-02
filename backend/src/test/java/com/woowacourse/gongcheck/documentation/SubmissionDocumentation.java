package com.woowacourse.gongcheck.documentation;

import static com.woowacourse.gongcheck.fixture.FixtureFactory.Host_생성;
import static com.woowacourse.gongcheck.fixture.FixtureFactory.Job_아이디_지정_생성;
import static com.woowacourse.gongcheck.fixture.FixtureFactory.Space_아이디_지정_생성;
import static com.woowacourse.gongcheck.fixture.FixtureFactory.Submission_아이디_지정_생성;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;

import com.woowacourse.gongcheck.auth.domain.Authority;
import com.woowacourse.gongcheck.core.application.response.SubmissionsResponse;
import com.woowacourse.gongcheck.core.domain.host.Host;
import com.woowacourse.gongcheck.core.domain.job.Job;
import com.woowacourse.gongcheck.core.domain.space.Space;
import com.woowacourse.gongcheck.core.domain.submission.Submission;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.restdocs.payload.JsonFieldType;

class SubmissionDocumentation extends DocumentationTest {

    @Nested
    class Submission_목록_조회 {

        @Test
        void Submission_목록_조회에_성공한다() {
            Host host = Host_생성("1234", 1234L);
            Space space = Space_아이디_지정_생성(1L, host, "잠실");
            Job job1 = Job_아이디_지정_생성(1L, space, "청소");
            Job job2 = Job_아이디_지정_생성(2L, space, "마감");
            Submission submission1 = Submission_아이디_지정_생성(1L, job1);
            Submission submission2 = Submission_아이디_지정_생성(2L, job2);
            SubmissionsResponse response = SubmissionsResponse.of(List.of(submission1, submission2), true);

            when(submissionService.findPage(anyLong(), anyLong(), any())).thenReturn(response);
            when(jwtTokenProvider.extractAuthority(anyString())).thenReturn(Authority.HOST);
            when(authenticationContext.getPrincipal()).thenReturn(String.valueOf(anyLong()));

            docsGiven
                    .header(AUTHORIZATION, "Bearer jwt.token.here")
                    .queryParam("page", 0)
                    .queryParam("size", 2)
                    .when().get("/api/spaces/{spaceId}/submissions", 1)
                    .then().log().all()
                    .apply(document("submissions/list",
                            pathParameters(
                                    parameterWithName("spaceId").description("Submission 목록을 조회할 Space Id")),
                            responseFields(
                                    fieldWithPath("submissions.[].submissionId").type(JsonFieldType.NUMBER)
                                            .description("Submission Id"),
                                    fieldWithPath("submissions.[].jobId").type(JsonFieldType.NUMBER)
                                            .description("Job Id"),
                                    fieldWithPath("submissions.[].jobName").type(JsonFieldType.STRING)
                                            .description("Job 이름"),
                                    fieldWithPath("submissions.[].author").type(JsonFieldType.STRING)
                                            .description("제출자"),
                                    fieldWithPath("submissions.[].createdAt").type(JsonFieldType.STRING)
                                            .description("제출 날짜"),
                                    fieldWithPath("hasNext").type(JsonFieldType.BOOLEAN)
                                            .description("다음 페이지 존재 여부")
                            )))
                    .statusCode(HttpStatus.OK.value());
        }
    }
}
