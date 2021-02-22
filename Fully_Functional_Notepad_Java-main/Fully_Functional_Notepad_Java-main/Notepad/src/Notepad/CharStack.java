
package Notepad;



class CharStack {
    final StringBuilder sb = new StringBuilder();
    
    public Boolean isEmpty(){
        if(sb.length()==0){
            return true;
        }
        else{
            return false;
        }
    }
    
    public void clear(){
        sb.setLength(0);
    }

    public void push(char ch) {
        sb.append(ch);
    }

    public char pop() {
        int last = sb.length() -1;
        char ch= sb.charAt(last);
        sb.setLength(last);
        return ch;
    }

    public int size() {
        return sb.length();
    }
    
    //Stack içindekileri sondan (pop'da ilk çıkacak olan) başa doğru sıralar.
    public String copy(){
        String str="";
        for(int i=sb.length()-1;i>=0;i--){
            str+=sb.charAt(i);
        }
        
        return str;
    }
    
    //Stack içine atılmış karakterleri ilk atılandan son atılana doğru sıralar.
    public String realCopy(){
        
        String temp="";
        for(int i=0;i<sb.length();i++){
            temp+= sb.charAt(i);
        }
        return temp;
    }
}



