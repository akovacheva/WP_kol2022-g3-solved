package mk.ukim.finki.wp.kol2022.g3.service.impl;

import mk.ukim.finki.wp.kol2022.g3.model.ForumUser;
import mk.ukim.finki.wp.kol2022.g3.model.ForumUserType;
import mk.ukim.finki.wp.kol2022.g3.model.Interest;
import mk.ukim.finki.wp.kol2022.g3.model.exceptions.InvalidForumUserIdException;
import mk.ukim.finki.wp.kol2022.g3.repository.ForumUserRepository;
import mk.ukim.finki.wp.kol2022.g3.repository.InterestRepository;
import mk.ukim.finki.wp.kol2022.g3.service.ForumUserService;
import mk.ukim.finki.wp.kol2022.g3.service.InterestService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ForumUserServiceImpl implements ForumUserService {

    private final ForumUserRepository forumUserRepository;
    private final InterestService interestService;
    private final PasswordEncoder passwordEncoder;

    public ForumUserServiceImpl(ForumUserRepository forumUserRepository, InterestService interestService, PasswordEncoder passwordEncoder) {
        this.forumUserRepository = forumUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.interestService = interestService;
    }


    @Override
    public List<ForumUser> listAll() {
        return this.forumUserRepository.findAll();
    }

    @Override
    public ForumUser findById(Long id) {
        return this.forumUserRepository.findById(id).orElseThrow(InvalidForumUserIdException::new);
    }

    @Override
    public ForumUser create(String name, String email, String password, ForumUserType type, List<Long> interestId, LocalDate birthday) {
        List<Interest> interests = interestId.stream()
                .map(id -> interestService.findById(id))
                .collect(Collectors.toList());
        ForumUser forumUser = new ForumUser(name, email, passwordEncoder.encode(password), type, interests, birthday);
        return this.forumUserRepository.save(forumUser);
    }

    @Override
    public ForumUser update(Long id, String name, String email, String password, ForumUserType type, List<Long> interestId, LocalDate birthday) {
        ForumUser forumUser = this.findById(id);
        forumUser.setName(name);
        forumUser.setEmail(email);
        forumUser.setPassword(this.passwordEncoder.encode(password));
        forumUser.setType(type);
        List<Interest> interests = interestId.stream()
                .map(Lid -> interestService.findById(Lid))
                .collect(Collectors.toList());
        forumUser.setInterests(interests);
        forumUser.setBirthday(birthday);

        return this.forumUserRepository.save(forumUser);
    }

    @Override
    public ForumUser delete(Long id) {
        ForumUser forumUser = this.findById(id);
        this.forumUserRepository.delete(forumUser);
        return forumUser;
    }

    @Override
    public List<ForumUser> filter(Long interestId, Integer age) {
        if (age != null && interestId != null)
        {
            Interest interest = this.interestService.findById(interestId);
            LocalDate dateBefore = LocalDate.now().minusYears(age);
            return this.forumUserRepository.findByInterestsContainingAndBirthdayBefore(interest, dateBefore);
        } else if (age != null) {
            LocalDate dateBefore = LocalDate.now().minusYears(age);
            return this.forumUserRepository.findByBirthdayBefore(dateBefore);
        } else if (interestId != null) {
            Interest interest = this.interestService.findById(interestId);
            return this.forumUserRepository.findByInterestsContaining(interest);
        }
        return this.forumUserRepository.findAll();
    }
}
