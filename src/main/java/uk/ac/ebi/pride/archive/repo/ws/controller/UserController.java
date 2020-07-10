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

    @GetMapping("/findAllProjectsById/{id}")
    public List<ProjectSummary> findAllProjectsById(@PathVariable Long id) throws Exception {
        return userService.findAllProjectsById(id);
    }

    @GetMapping("/findAllByProjectId/{id}")
    public List<User> findAllByProjectId(@PathVariable Long id) throws Exception {
        return userService.findAllByProjectId(id);
    }

    @GetMapping("/findByEmail/{email}")
    public User findByEmail(@PathVariable String email) throws Exception {
        return userService.findByEmail(email);
    }

    @PostMapping("/save")
    public User save(@RequestBody User user) throws Exception {
        return userService.save(user);
    }

    @GetMapping("/findUsersNotInAAP")
    public List<User> findUsersNotInAAP() {
        return userService.findUsersNotInAAP();
    }

    @GetMapping("/findByUserRef")
    public User findByUserRef(String userRef) {
        return userService.findByUserRef(userRef);
    }

    @PostMapping("/createReviewer")
    public User createReviewerAccount(@RequestBody String projectAccession) {
        return userService.createReviewerAccount(projectAccession);
    }

}
