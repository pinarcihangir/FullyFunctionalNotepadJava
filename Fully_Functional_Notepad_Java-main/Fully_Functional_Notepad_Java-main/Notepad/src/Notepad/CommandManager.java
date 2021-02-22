
package Notepad;

import java.util.ArrayList;
import java.util.Stack;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class CommandManager  {
    private Stack<UndoableCommand> commandStack = new Stack();
    private int _mevcut;

    private NotepadGui gui;
    public CommandManager(NotepadGui gui){
        this.gui = gui;
        _mevcut = 0;
    }
    
    
    void Execute(ICommand command,NotepadGui n){
        //commandList.forEach(ICommand :: Perform);
        
        commandStack.push((UndoableCommand)command);
        //command.Perform(n);
        //System.out.println("stack'e yüklendi, stackSize = "+commandStack.size());
        
        //commandStack.push((UndoableCommand)command);
        _mevcut++;
        //System.out.println("Execute edildi + mevcut = "+_mevcut);
    }
    
    void Undo(NotepadGui n){
        //System.out.println("Undo Edildi + mevcut = "+_mevcut);
        if(_mevcut == 0){
            JOptionPane.showMessageDialog(new JFrame(), "Daha fazla geri alma işlemi yapılamaz!");
            return;
        }
        if(_mevcut>0){
            //n.getTextArea().setText("");
            //ICommand command = (ICommand) _commands.get(--_mevcut);
            
            UndoableCommand comd=  commandStack.pop();
            //System.out.println("Stack size = "+commandStack.size());
            comd.Undo();
        }
        _mevcut--;
    }
}
