package dd.com.myq.Fragment.Groups;

/**
 * Created by absolutelysaurabh on 29/6/17.
 */

public class Group {

    private String name;
    private String since;
    private String count;


    public Group(String name, String count, String since){

        this.name = name;
        this.count=count;
        this.since = since;


    }
    //We need to make the setters and getters
    public String getName(){
        return name;
    }

    public String getCount(){
        return count;
    }

    public String getSince(){
        return since;
    }


}