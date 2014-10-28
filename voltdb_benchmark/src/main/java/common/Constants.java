package common;

/**
 * Created by sandeep on 7/16/14.
 */
public class Constants {
    // Initialize some common constants and variables
    public static final String BOOK_NAMES_CSV = "Atlas Shrugged, Autbiography of a Yogi, Fountain Head,"
            + " My Experiments with Truth, We The People, Autobiography of Swamy Vivekananda";

    public static final int MAX_REVIEWS_PER_USER = 25;

    // handy, rather than typing this out several times
    public static final String HORIZONTAL_RULE = "----------" + "----------"
            + "----------" + "----------" + "----------" + "----------"
            + "----------" + "----------" + "\n";

    public static final char[] PERMISSIBLE_EMAIL_ID_CHARACTERS = {
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
            's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '_'};

    public static final char[] PERMISSIBLE_REVIEW_CHARACTERS = {
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
            's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '_', '.', ' ', ';'};

    public static final char[] PERMISSIBLE_EMAIL_DOMAIN_CHARACTERS = {
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
            's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};

    public static final char[] PERMISSIBLE_DOMAIN_EXT_CHARACTERS = {
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
            's', 't', 'u', 'v', 'w', 'x', 'y', 'z',};

    // potential return codes
    public static final long REVIEW_SUCCESSFUL = 0;
    public static final long ERR_INVALID_BOOK = 1;
    public static final long ERR_REVIEWER_OVER_REVIEW_LIMIT = 2;
}
