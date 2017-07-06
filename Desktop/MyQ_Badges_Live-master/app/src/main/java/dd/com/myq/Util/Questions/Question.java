package dd.com.myq.Util.Questions;

public class Question {

    private String id;
    private String text;
    private String level;
    private String correctAnswer;
  //  private String imageUrl;

    public Question(String text,String level,String correctAnswere, String id){

        this.id = id;
        this.text = text;
       // this.imageUrl=imageUrl;
        this.level =level;
        this.correctAnswer = correctAnswere;

    }

    //We need to make the setters and getters
    public String getText(){
        return text;
    }

    public String getLevel(){
        return level;
    }

    public String getId(){

        return id;
    }

    public String  getCorrectAnswere(){

        return correctAnswer;
    }
//   // public String getImageUrl(){
//        return imageUrl;
//    }

}
