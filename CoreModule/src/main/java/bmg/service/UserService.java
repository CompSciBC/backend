package bmg.service;
import bmg.model.User;
import bmg.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
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
        List<User> users = new ArrayList<User>();
        if (index.equals("userID")){
            users = USER_REPO.findUsersByUserId(id);
        } else if (index.equals("email")){
            users = USER_REPO.findUsersByIndex(User.Index.EMAIL, id);
        } else if (index.equals("username")){
            users = USER_REPO.findUsersByIndex(User.Index.USERNAME, id);
        }
        return users;
    }
    
}
