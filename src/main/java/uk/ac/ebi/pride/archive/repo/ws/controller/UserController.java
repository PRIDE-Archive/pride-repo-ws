package uk.ac.ebi.pride.archive.repo.ws.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import uk.ac.ebi.pride.archive.repo.models.project.ProjectSummary;
import uk.ac.ebi.pride.archive.repo.models.user.User;
import uk.ac.ebi.pride.archive.repo.ws.service.UserService;

import java.util.List;


@RestController
@Validated
@RequestMapping("/user")
@Slf4j
@Tag(name = "User")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/findAllProjectAccessionByUserId/{userId}")
    public List<String> findAllProjectAccessionByUserId(@PathVariable Long userId) {
        return userService.findAllProjectAccessionByUserId(userId);
    }

    @GetMapping("/findAllByProjectId/{projectId}")
    public List<User> findAllByProjectId(@PathVariable Long projectId) {
        return userService.findAllByProjectId(projectId);
    }

    @GetMapping("/findByEmail/{email}")
    public User findByEmail(@PathVariable String email) {
        return userService.findByEmail(email);
    }

    @PostMapping("/save")
    public User save(@RequestBody User user) {
        return userService.save(user);
    }

    @GetMapping("/findUsersNotInAAP")
    public List<User> findUsersNotInAAP() {
        return userService.findUsersNotInAAP();
    }

    @GetMapping("/findByUserRef/{userRef}")
    public User findByUserRef(@PathVariable String userRef) {
        return userService.findByUserRef(userRef);
    }

    @PostMapping("/createReviewer")
    public User createReviewerAccount(@RequestBody String projectAccession) {
        return userService.createReviewerAccount(projectAccession);
    }

}
