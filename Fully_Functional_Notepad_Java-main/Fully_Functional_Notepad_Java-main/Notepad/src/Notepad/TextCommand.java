
package Notepad;



//TextArea'ya yazılan stringleri TextCommandLarda tutuyoruz. Her klavye tuşuna basılıp bir değer girildiğinde
//TextCommand nesnesi oluşturuluyor. TextCommand nesneleri CommandManager nesnesindeki _commands stack'inde tutuluyor. 
//Geri alma tuşuna bastığımızda ise _commands stackinden pop yapılıyor
public class TextCommand extends UndoableCommand{
    private String textAreaStr;
    private NotepadGui gui;
    public TextCommand(NotepadGui gui){
        this.gui = gui;
    }
    
    
    @Override
    public void Undo() {
        //textArea sıfırlanıp, üzerine textAreaStr içinde tutulan string yazılıyor. Her TextCommand kendi durumunda string tutuyor. 
        //Undo(); yapıldığında bir önceki TextCommand gelip kendi durumunu textArea'ya yazıyor. Böylece geri alma işlemi yapılmış oluyor
        
        getGui().getTextArea().setText(null);
        getGui().getTextArea().setText(getTextAreaStr());
    }

    @Override
    public void Perform() {
        gui.getTextArea().setText(null);
        gui.getTextArea().setText(getTextAreaStr());
    }

    /**
     * @return the textAreaStr
     */
    public String getTextAreaStr() {
        return textAreaStr;
    }

    /**
     * @param textAreaStr the textAreaStr to set
     */
    public void setTextAreaStr(String textAreaStr) {
        this.textAreaStr = textAreaStr;
    }

    /**
     * @return the gui
     */
    public NotepadGui getGui() {
        return gui;
    }

    /**
     * @param gui the gui to set
     */
    public void setGui(NotepadGui gui) {
        this.gui = gui;
    }
    
}
