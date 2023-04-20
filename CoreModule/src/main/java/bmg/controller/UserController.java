package bmg.controller;
import bmg.model.User;
import bmg.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/users")
@RequiredArgsConstructor
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
        List<User> users = userService.findUsersByIndex(index, id);
        return responseCodeOk(users); 
    }
}
