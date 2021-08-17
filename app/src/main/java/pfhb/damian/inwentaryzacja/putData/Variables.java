package pfhb.damian.inwentaryzacja.putData;

public class Variables {
    private String result = "";

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public void addResult(String result){
        this.result += "\n~ " + result;
    }
}
