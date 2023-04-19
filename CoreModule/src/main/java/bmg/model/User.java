package bmg.model;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@DynamoDBTable(tableName = "users")
@Data
public class User {
    public static final String USERNAME_INDEX = "username-index";
    public static final String EMAIL_INDEX = "email-index";

    // available indexes in the user table
    @RequiredArgsConstructor
    @Getter
    public enum Index {
        USERNAME (USERNAME_INDEX, "username"),
        EMAIL (EMAIL_INDEX, "email");

        private final String NAME;
        private final String KEY;
    }

    @DynamoDBHashKey(attributeName = "userID")
    private String userID;

    @DynamoDBIndexHashKey(attributeName = "email", globalSecondaryIndexName = EMAIL_INDEX)
    private String email;

    @DynamoDBAttribute(attributeName = "firstName")
    private String firstName;

    @DynamoDBAttribute(attributeName = "joinedOn")
    private String joinedOn;

    @DynamoDBAttribute(attributeName = "lastName")
    private String lastName;

    @DynamoDBAttribute(attributeName = "phone")
    private String phone;

    @DynamoDBAttribute(attributeName = "role")
    private String role;

    @DynamoDBIndexHashKey(attributeName = "username", globalSecondaryIndexName = USERNAME_INDEX)
    private String username;
    
}
