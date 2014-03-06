package ffts.android.moefmdroid.modules;

/**
 * Created by ffts on 13-9-15.
 * Email:ffts133@gmail.com
 */
public class User {
    private long uid;
    private String user_name;
    private String user_nickname;
    private UserAvatar user_avatar;

    public long getUid() {
        return uid;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getUser_nickname() {
        return user_nickname;
    }

    public UserAvatar getUser_avatar() {
        return user_avatar;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public void setUser_nickname(String user_nickname) {
        this.user_nickname = user_nickname;
    }

    public void setUser_avatar(UserAvatar user_avatar) {
        this.user_avatar = user_avatar;
    }

    class UserAvatar {
        private String small;
        private String middle;
        private String large;

        public String getSmall() {
            return small;
        }

        public String getMiddle() {
            return middle;
        }

        public String getLarge() {
            return large;
        }

        public void setSmall(String small) {
            this.small = small;
        }

        public void setMiddle(String middle) {
            this.middle = middle;
        }

        public void setLarge(String large) {
            this.large = large;
        }
    }
}
