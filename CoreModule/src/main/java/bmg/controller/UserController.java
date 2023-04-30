package bmg.controller;
import bmg.model.User;
import bmg.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
