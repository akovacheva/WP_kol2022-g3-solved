package mk.ukim.finki.wp.kol2022.g3.repository;

import mk.ukim.finki.wp.kol2022.g3.model.ForumUser;
import mk.ukim.finki.wp.kol2022.g3.model.Interest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ForumUserRepository extends JpaRepository<ForumUser, Long> {
    ForumUser findByEmail(String email);
    List<ForumUser> findByInterestsContaining(Interest interest);
    List<ForumUser> findByBirthdayBefore(LocalDate date);
    List<ForumUser> findByInterestsContainingAndBirthdayBefore(Interest interest, LocalDate date);
}
