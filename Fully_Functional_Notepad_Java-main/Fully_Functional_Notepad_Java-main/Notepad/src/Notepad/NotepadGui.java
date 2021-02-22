
package Notepad;


import javax.swing.text.*;
import java.awt.*;
import java.io.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.font.TextAttribute;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.Position;


public class NotepadGui {

    private JFrame frame = new JFrame();
    
    //Creating MenuBar and adding its components
    private JMenuBar menuBar = new JMenuBar();
    private JMenu m1 = new JMenu("Dosya");
    private Icon undoIcon = new ImageIcon("undoIcon.png");
    private JButton undoButton = new JButton(undoIcon);
    private Icon changeStrIcon = new ImageIcon("changeIcon.png");
    private JButton changeStrButton = new JButton(changeStrIcon);
    private JTextField searchField = new JTextField(25);
    private Icon searchIcon = new ImageIcon("SearchIcon.png");
    private JButton searchButton = new JButton(searchIcon);
    private JMenuItem New= new JMenuItem("New");
    private JMenuItem save = new JMenuItem("Save");
    private JMenuItem open = new JMenuItem("Open");
    private JMenuItem Quit = new JMenuItem("Quit");
    private JTextArea textArea = new JTextArea();
    private boolean visibility;
    private boolean hataliKelimeGecildiMi = false;
    private CharStack stack = new CharStack();
    private Highlighter.HighlightPainter painter = 
        new DefaultHighlighter.DefaultHighlightPainter( Color.cyan );
    private ArrayList<String> hataliKelimeler = new ArrayList<String>();
    
    private int lastWritedKeyOffset =0;
    private Object highlightTag;
    
    private int width;
    private int height;
    
    
    //GUI size'ı değiştirirken kod tekrarı yapmamak için yazıldı.
    public void setMenubar(){
        getMenuBar().add(getM1());
        getM1().add(getNew());
        getM1().add(getOpen());
        getM1().add(getSave());
        getM1().add(getQuit());
       
        getMenuBar().add( Box.createHorizontalStrut( 10 ) ); 
        getMenuBar().add(getUndoButton());
        getMenuBar().add( Box.createHorizontalGlue());
        getMenuBar().add(getChangeStrButton());
        getMenuBar().add( Box.createHorizontalStrut( 5 ) ); 
        getMenuBar().add(getSearchField());
        getMenuBar().add( Box.createHorizontalStrut( 5 ) ); 
        getMenuBar().add(getSearchButton());
    }
    
    public void setFrameVisibility(boolean bool){
        setVisibility(bool);
        getFrame().setVisible(isVisibility());
    }
    
    public void settingTextArea(){
        getTextArea().setLineWrap(true);
        JScrollPane areaScrollPane = new JScrollPane(getTextArea());
        areaScrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
       areaScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        areaScrollPane.setPreferredSize(new Dimension(250, 250));
        //JScrollPane scroll = new JScrollPane(textArea);
        getFrame().add(areaScrollPane);
    }

    //NotepadGui nesnesi ile operasyonlar birbirinden ayrıldı. Daha iyi bir nesne yönelimi elde edildi.
    //Ayrıca yazılan text'leri geri almak için Command design pattern uygulandı. Operasyonlar kısmında
    //hazır olarak undo(); metodu bulunmasına rağmen yorum satırına alınarak, command tasarım deseni kullanılarak
    //yapıldı. Geri alma işlemimiz her karakter için çalışıyor fakat kelime bazlı olarak da çok kolay bir şekilde ayarlanabilir.
    //textAreaListener'da space tuşuna duyarlı bir şekilde ayarlanabilir. Hali hazırda hataları boyama işlemleri bu şekilde space
    //tuşuna duyarlı bir şekilde yapılıyor.
    
    //Kullanıcı istediği boyutlarda notepad oluştuabilir.
    public NotepadGui(int width, int height){
       frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       searchField.setMaximumSize(new Dimension(200,36));
       this.height = height;
       this.width = width;
       frame.setSize(width,height); //  width,height - (900-700)
       Operations O = new Operations();
       O.Open(this);
       O.NewMenuItem(this);
       O.Close(this);
       O.Save(this);
       
       //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
       //O.undo(this);
       //O.textAreaListener(this);
       //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
       
       //Görüldüğü üzere command tasarım desenine göre yapıldı.
       CommandManager commandManager = new CommandManager(this);
       TextAreaListener textAreaListenerCommand = new TextAreaListener(this,commandManager);
       textAreaListenerCommand.initiate();
       UndoButtonListener undoButtonListener = new UndoButtonListener(this,commandManager);
       undoButtonListener.initiate();
       O.search(this);
       O.stringChangeFrame(searchField.getText(),this);
       setMenubar();
       settingTextArea();
       frame.getContentPane().add(BorderLayout.NORTH, menuBar);
       setFrameVisibility(true);       

    }
    
    //Getter Setter metotlarımız...

    /**
     * @return the frame
     */
    public JFrame getFrame() {
        return frame;
    }

    /**
     * @param frame the frame to set
     */
    public void setFrame(JFrame frame) {
        this.frame = frame;
    }

    /**
     * @return the menuBar
     */
    public JMenuBar getMenuBar() {
        return menuBar;
    }

    /**
     * @param menuBar the menuBar to set
     */
    public void setMenuBar(JMenuBar menuBar) {
        this.menuBar = menuBar;
    }

    /**
     * @return the m1
     */
    public JMenu getM1() {
        return m1;
    }

    /**
     * @param m1 the m1 to set
     */
    public void setM1(JMenu m1) {
        this.m1 = m1;
    }

    /**
     * @return the undoIcon
     */
    public Icon getUndoIcon() {
        return undoIcon;
    }

    /**
     * @param undoIcon the undoIcon to set
     */
    public void setUndoIcon(Icon undoIcon) {
        this.undoIcon = undoIcon;
    }

    /**
     * @return the undoButton
     */
    public JButton getUndoButton() {
        return undoButton;
    }

    /**
     * @param undoButton the undoButton to set
     */
    public void setUndoButton(JButton undoButton) {
        this.undoButton = undoButton;
    }

    /**
     * @return the changeStrIcon
     */
    public Icon getChangeStrIcon() {
        return changeStrIcon;
    }

    /**
     * @param changeStrIcon the changeStrIcon to set
     */
    public void setChangeStrIcon(Icon changeStrIcon) {
        this.changeStrIcon = changeStrIcon;
    }

    /**
     * @return the changeStrButton
     */
    public JButton getChangeStrButton() {
        return changeStrButton;
    }

    /**
     * @param changeStrButton the changeStrButton to set
     */
    public void setChangeStrButton(JButton changeStrButton) {
        this.changeStrButton = changeStrButton;
    }

    /**
     * @return the searchField
     */
    public JTextField getSearchField() {
        return searchField;
    }

    /**
     * @param searchField the searchField to set
     */
    public void setSearchField(JTextField searchField) {
        this.searchField = searchField;
    }

    /**
     * @return the searchIcon
     */
    public Icon getSearchIcon() {
        return searchIcon;
    }

    /**
     * @param searchIcon the searchIcon to set
     */
    public void setSearchIcon(Icon searchIcon) {
        this.searchIcon = searchIcon;
    }

    /**
     * @return the searchButton
     */
    public JButton getSearchButton() {
        return searchButton;
    }

    /**
     * @param searchButton the searchButton to set
     */
    public void setSearchButton(JButton searchButton) {
        this.searchButton = searchButton;
    }

    /**
     * @return the New
     */
    public JMenuItem getNew() {
        return New;
    }

    /**
     * @param New the New to set
     */
    public void setNew(JMenuItem New) {
        this.New = New;
    }

    /**
     * @return the save
     */
    public JMenuItem getSave() {
        return save;
    }

    /**
     * @param save the save to set
     */
    public void setSave(JMenuItem save) {
        this.save = save;
    }

    /**
     * @return the open
     */
    public JMenuItem getOpen() {
        return open;
    }

    /**
     * @param open the open to set
     */
    public void setOpen(JMenuItem open) {
        this.open = open;
    }

    /**
     * @return the Quit
     */
    public JMenuItem getQuit() {
        return Quit;
    }

    /**
     * @param Quit the Quit to set
     */
    public void setQuit(JMenuItem Quit) {
        this.Quit = Quit;
    }

    /**
     * @return the textArea
     */
    public JTextArea getTextArea() {
        return textArea;
    }

    /**
     * @param textArea the textArea to set
     */
    public void setTextArea(JTextArea textArea) {
        this.textArea = textArea;
    }

    /**
     * @return the visibility
     */
    public boolean isVisibility() {
        return visibility;
    }

    /**
     * @param visibility the visibility to set
     */
    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
    }

    /**
     * @return the hataliKelimeGecildiMi
     */
    public boolean isHataliKelimeGecildiMi() {
        return hataliKelimeGecildiMi;
    }

    /**
     * @param hataliKelimeGecildiMi the hataliKelimeGecildiMi to set
     */
    public void setHataliKelimeGecildiMi(boolean hataliKelimeGecildiMi) {
        this.hataliKelimeGecildiMi = hataliKelimeGecildiMi;
    }

    /**
     * @return the stack
     */
    public CharStack getStack() {
        return stack;
    }

    /**
     * @param stack the stack to set
     */
    public void setStack(CharStack stack) {
        this.stack = stack;
    }

    /**
     * @return the painter
     */
    public Highlighter.HighlightPainter getPainter() {
        return painter;
    }

    /**
     * @param painter the painter to set
     */
    public void setPainter(Highlighter.HighlightPainter painter) {
        this.painter = painter;
    }

    /**
     * @return the hataliKelimeler
     */
    public ArrayList<String> getHataliKelimeler() {
        return hataliKelimeler;
    }

    /**
     * @param hataliKelimeler the hataliKelimeler to set
     */
    public void setHataliKelimeler(ArrayList<String> hataliKelimeler) {
        this.hataliKelimeler = hataliKelimeler;
    }

    /**
     * @return the lastWritedKeyOffset
     */
    public int getLastWritedKeyOffset() {
        return lastWritedKeyOffset;
    }

    /**
     * @param lastWritedKeyOffset the lastWritedKeyOffset to set
     */
    public void setLastWritedKeyOffset(int lastWritedKeyOffset) {
        this.lastWritedKeyOffset = lastWritedKeyOffset;
    }

    /**
     * @return the highlightTag
     */
    public Object getHighlightTag() {
        return highlightTag;
    }

    /**
     * @param highlightTag the highlightTag to set
     */
    public void setHighlightTag(Object highlightTag) {
        this.highlightTag = highlightTag;
    }

    /**
     * @return the width
     */
    public int getWidth() {
        return width;
    }

    /**
     * @param width the width to set
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * @return the height
     */
    public int getHeight() {
        return height;
    }

    /**
     * @param height the height to set
     */
    public void setHeight(int height) {
        this.height = height;
    }

    
    
}