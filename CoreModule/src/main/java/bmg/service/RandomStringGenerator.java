package bmg.service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;

/**
 * Generates a random string
 */
@Service
public class RandomStringGenerator {
    private final SecureRandom RANDOM = new SecureRandom();

    /**
     * Generates a random size length string using lowercase letters and numbers
     *
     * @param size The desired size of the random string
     * @return A random string
     */
    public String generate(int size) {
        String alphabet = "abcdefghijklmnopqrstuvwxyz0123456789";
        return generate(size, alphabet);
    }

    /**
     * Generates a random size length string using the given alphabet
     *
     * @param size The desired size of the random string
     * @param alphabet The characters to generate the string from
     * @return A random string
     */
    public String generate(int size, String alphabet) {
        StringBuilder code = new StringBuilder();

        for (int i = 0; i < size; i++) {
            int index = RANDOM.nextInt(alphabet.length());
            code.append(alphabet.charAt(index));
        }
        return code.toString();
    }
}
