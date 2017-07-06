package dd.com.myq.Util.PointsPackage;

public class Points {

    private int WrongAnswers;
    private int CorrectAnswers;
    private int points;

    public Points(int WrongAnswers, int CorrectAnswers,int points){

        this.WrongAnswers = WrongAnswers;
        this.CorrectAnswers=CorrectAnswers;
        this.points=points;
    }
    public int getWrongAnswers() {
        return WrongAnswers;
    }

    public int getCorrectAnswers() {
        return CorrectAnswers;
    }

    public int getPoints() {
        return points;
    }

}