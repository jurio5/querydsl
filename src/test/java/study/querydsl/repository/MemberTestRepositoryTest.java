package study.querydsl.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.entity.Member;
import study.querydsl.entity.Team;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MemberTestRepositoryTest {

    @Autowired
    JPAQueryFactory query;

    @Autowired
    EntityManager em;

    @Autowired
    MemberTestRepository memberTestRepository;

    @BeforeEach
    void before() {
        query = new JPAQueryFactory(em);

        Team teamA = Team.of("teamA");
        Team teamB = Team.of("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = createMember("member1", 10, teamA);
        Member member2 = createMember("member2", 20, teamA);
        Member member3 = createMember("member3", 30, teamB);
        Member member4 = createMember("member4", 40, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
    }

    @Test
    void customTest() {
        MemberSearchCondition condition = new MemberSearchCondition();
        condition.setUsername("member1");
        condition.setTeamName("teamA");
        condition.setAgeGoe(10);
        condition.setAgeLoe(40);

        PageRequest pageable = PageRequest.of(0, 3, Sort.by("username").descending());

        Page<Member> findMember = memberTestRepository.applyPaginationCountQuery(condition, pageable);

        assertThat(findMember).hasSize(1);
        assertThat(findMember.getContent()).isNotEmpty();
        assertThat(findMember.getTotalElements()).isGreaterThan(0);
        assertThat(findMember.getContent().getFirst().getUsername()).isEqualTo("member1");
    }

    private Member createMember(String username, int age, Team teamA) {
        return Member.builder()
                .username(username)
                .age(age)
                .team(teamA)
                .build();
    }
}