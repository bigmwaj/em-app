package ca.bigmwaj.emapp.as.builder.common;

public class TestConstant {

    public static final String TEST_USER = "TEST_USER";

    /**
     * This user is supposed to always exist in DB and should not be deleted in commited transaction.
     */
    public static final String TEST_PERMANENT_USER = "TEST_PERMANENT_USER";


    public static final String TEST_PASSWORD = "TEST_PASSWORD";
    public static final String TEST_ROLE = "TEST_ROLE";

    /**
     * This role is supposed to always exist in DB and should not be deleted in commited transaction.
     */
    public static final String TEST_PERMANENT_ROLE = "TEST_PERMANENT_ROLE";

}
