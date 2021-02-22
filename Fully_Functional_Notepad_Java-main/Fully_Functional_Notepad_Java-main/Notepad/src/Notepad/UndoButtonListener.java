
package Notepad;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


//UndoButton'ı dinleyen metot. Command design pattern'nine uygun şekilde tıklandığında cmd (CommandManager nesnesi) üzerinden Undo(); metodunu çağırıyor

public class UndoButtonListener {
    private NotepadGui gui;
    private CommandManager cmd;
    public UndoButtonListener(NotepadGui gui, CommandManager cmd){
        this.gui = gui;
        this.cmd =cmd;

    }
    
    public void initiate(){
        gui.getUndoButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                cmd.Undo(gui);
            }
        });
    }
    
}
