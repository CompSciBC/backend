package bmg.controller;
import bmg.model.User;
import bmg.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Log4j2
public class UserController extends Controller<User> {
    private final UserService userService;

    /**
     * Finds all users submitted by their userid, username or email
     * @param index
     * @param id
     * @return List of User Objects
     */
    @GetMapping("")
    public ResponseEntity<Response<User>> findUsersByIndex(@RequestParam(required = true) String index, @RequestParam(required = true) String id) {
        log.info("Find user with {}={}", index, id);

        List<User> users = userService.findUsersByIndex(index, id);
        return responseCodeOk(users); 
    }

    /**
     * Update a user
     * @param index
     * @param id
     * @return List of User Objects
     */
    @PostMapping("/update")
    public ResponseEntity<Response<User>> updateOne(@RequestParam(required = true) String firstName, @RequestParam(required = true) String lastName, @RequestParam(required = true) String phone, @RequestParam(required = true) String userId) {
        log.info("Find user with userId={}", userId);
        User updatedUser = userService.findUsersByIndex("userid", userId).get(0);
        updatedUser.setFirstName(firstName);
        updatedUser.setLastName(lastName);
        updatedUser.setPhone(phone);
        updatedUser.setUserID(userId);
        // return responseCodeOk(List.of(updatedUser)); 
        User modifiedUserRecord = userService.updateUser(updatedUser);
        return responseCodeOk(List.of(modifiedUserRecord)); 
    }
}
