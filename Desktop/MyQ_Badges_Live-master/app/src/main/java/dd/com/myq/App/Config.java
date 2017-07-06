package dd.com.myq.App;

import java.util.ArrayList;
import java.util.List;

public class
Config {

    private static final String host="http://myish.com:10011/";

    public static String LoginAPIUrl = host+"api/validate";
    public static String SignUpAPIUrl = host+"api/users";
    public static String BadgesAPIUrl = host+"api/badges";

    public static String AddQuestionUrl = host+"api/addquestionstouser";

    public static String AddQuestionToUser = host + "api/questions/answer";

    public static String UpdateDetailAPIUrl = host+"api/updateprofile";
    public static String ForgotPasswordAPIUrl = host+"api/forgotpassword";
    public static final String socialLogin=host+"api/sociallogin";
    public static final String changepasswordAPIUrl=host+"api/changepassword";

    public static final String defaultImagePrefix="https://ddmyish.s3-us-west-2.amazonaws.com/";
    public static String token="";
    public static String uid="";
    public static String username="";
    public static String dob=null;
    public static String useremail="";
    public static String profilePic="";
    public static String gender="";
    public static String followingCount="";
    public static String followersCount="";
    public static String aboutMe="";
    public static List<String> usersPosts = new ArrayList<String>();
    public static List<String> userFollowing = new ArrayList<String>();
    public static String currentPostId="";
    public static String currentPostImg="";
    public static String currentPostCat="";
    public static String currentPostTitle="";
    public static String currentPostNoOfComments="";
    public static String currentPostpostedby="";
    public static String currentPostpostedbyname="";
    public static String currentPostpostedbyimg="";
    public static String currentPostTstp="";
    public static String currentPostIdtemp="5639a39ceaf8f17919d11fe5";
    public static String points="0";
    public static String posts="";
    public static String defaultprofilepic="https://ddmyish.s3-us-west-2.amazonaws.com/profile.png";
    public static String currentpostidTwo="";

    public static String previousScreen="";
    public static String searchText="";

    public static String InternetMsg="No Internet Connection Found.";
    public static String InternetMsgTitle="Connection Error";

}