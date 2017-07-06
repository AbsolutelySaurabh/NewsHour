package dd.com.myq.Fragment.Friends;

public class Friend {

    private String name;
    private String points;

    public Friend(String name, String points){

        this.name = name;
        this.points=points;


    }
    //We need to make the setters and getters
    public String getName(){
        return name;
    }

    public String getPoints(){
        return points;
    }



}