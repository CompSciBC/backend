package bmg.service;
import bmg.model.User;
import bmg.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository USER_REPO;

    /**
     * Finds list of users by the index
     * @param index
     * @param id
     * @return List of User Objects
     */
    public List<User> findUsersByIndex(String index, String id) {
        // List<User> users = new ArrayList<User>();
        return switch(index.toLowerCase()) {
            case "userid" -> USER_REPO.findUsersByUserId(id);
            case "email" -> USER_REPO.findUsersByIndex(User.Index.EMAIL, id);
            case "username" -> USER_REPO.findUsersByIndex(User.Index.USERNAME, id);
            case "role" -> USER_REPO.findUsersByRole(id);
            default -> throw new IllegalArgumentException("Index=" + index + " is not valid");
        };
    }
}
