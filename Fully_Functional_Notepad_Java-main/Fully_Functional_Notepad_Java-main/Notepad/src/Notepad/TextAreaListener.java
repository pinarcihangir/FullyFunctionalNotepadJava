
package Notepad;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;



//

public class TextAreaListener {
    
    private NotepadGui n;
    private String currentStr;
    private CommandManager cmd;
    public TextAreaListener(NotepadGui gui,CommandManager cmd){
        n = gui;
        currentStr = "";
        this.cmd = cmd;
    }
    
    public void initiate(){
        getN().getTextArea().addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent ke) {
                
                getN().getStack().push((char)ke.getKeyChar());
                //Kullanıcı yanlış kelimeyi düzeltirken kelimedeki highlight ı kaldırır. Görülmesi kolaylaşır
                if((int)ke.getKeyChar()==8){
                    //System.out.println("backspace!!!!");
                    getN().getTextArea().getHighlighter().removeAllHighlights();
                    getN().getStack().clear();
                    for(int i=0;i<getN().getTextArea().getText().length();i++){
                        getN().getStack().push(getN().getTextArea().getText().charAt(i));
                    }
                }
                Operations op = new Operations();
                op.hatalariBoya(getN());
                //System.out.println(highlightTag);
                if(getN().getHighlightTag()!= null){
                    getN().getTextArea().getHighlighter().removeHighlight(getN().getHighlightTag()); // Kullanıcı tekrar yazmaya başladığında arama yapılmış olan mavi highlightları siler
                }
                char ch = ke.getKeyChar();
                setCurrentStr(getN().getTextArea().getText());
                
                //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
                //Burada TextCommand nesneleri oluşturulup cmd.Execute(); fonksiyonuna gönderiliyor. (gönderime işlemi 71. satırda)
                TextCommand text = new TextCommand(getN());
                text.setTextAreaStr(currentStr);
                //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
                InputMap imap = getN().getTextArea().getInputMap(JComponent.WHEN_FOCUSED);
                imap.put(KeyStroke.getKeyStroke("SPACE"), "spaceAction");
                ActionMap amap = getN().getTextArea().getActionMap();
                amap.put("spaceAction", new AbstractAction(){
                    public void actionPerformed(ActionEvent e) {
                        //System.out.println("Space Pressed: " + textArea.getText());
                        try {
                            op.errorCheck(getN());
                        } catch (IOException ex) {
                            System.out.println("Bilinmeyen bir hata meydana geldi.");
                        }
                    }
                });
                //Burada execute ediliyor. Böylece TextCommand'lar commandManager içindeki stack'te tutulabilecek.
                getCmd().Execute(text, getN());
            }
            
            @Override
            public void keyPressed(KeyEvent ke) {
                     
            }

            @Override
            public void keyReleased(KeyEvent ke) {
               
            }
        });
    }


    /**
     * @return the n
     */
    public NotepadGui getN() {
        return n;
    }

    /**
     * @param n the n to set
     */
    public void setN(NotepadGui n) {
        this.n = n;
    }

    /**
     * @return the previousStr
     */
    public String getCurrentStr() {
        return currentStr;
    }

    /**
     * @param previousStr the previousStr to set
     */
    public void setCurrentStr(String previousStr) {
        this.currentStr = previousStr;
    }

    /**
     * @return the cmd
     */
    public CommandManager getCmd() {
        return cmd;
    }

    /**
     * @param cmd the cmd to set
     */
    public void setCmd(CommandManager cmd) {
        this.cmd = cmd;
    }
}