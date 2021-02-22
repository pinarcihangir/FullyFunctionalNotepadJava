package Notepad;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.util.Iterator;


public class Operations {
    
    //NotepadGui'nin temel operasyonları Operations() class'ı altında toplandı.
    //Upgrade etmek istenildiğinde çok kolay bir şekilde yapılabilir.
    //Tüm operasyonlar bir interface'de toplanabilirdi fakat bu tercih edilmedi.
    //İstenirse her biri ayrı bir nesne şeklinde olup bir interface'e bağlanabilir.
    
    
    //Kullanıcı bir text dosyası açmak istediğinde bu komut çalışıyor. 
    public void Open(NotepadGui n){
        n.getOpen().addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent ae) {
                JFileChooser fc=new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("TEXT FILES", "txt", "text");
                fc.setFileFilter(filter);
                int returnVal = fc.showOpenDialog(n.getM1());
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    //Scanner zaten kendisi Iterator interface'ini implement ettiği için burada iterator kullanmama gerek yok.
                    Scanner reader;
                    try {
                        reader = new Scanner(file);
                        while(reader.hasNextLine()){
                            String line = reader.nextLine();
                            //StyledDocument doc = textArea.getStyledDocument(); 
                            //doc.insertString(returnVal, line, as); 
                            n.getTextArea().append(line);
                            n.getTextArea().append("\n");
                        }
                        reader.close();
                    } catch (FileNotFoundException ex) {
                        n.getTextArea().setText("Dosya açılamadı...");
                        System.out.println("Dosya açılamadı...");
                    }

                }
            }
        });
        
    }
    
    //Notepad textArea'yı sıfırlayan metot.
    public void NewMenuItem(NotepadGui n){
        n.getNew().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                n.getTextArea().setText("");
                n.getStack().clear();
            }
        });
    }
    
    //Kullanıcı menüden Quit yapmak isterse programı kapatan metot.
    public void Close(NotepadGui n){
        n.getQuit().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                System.exit(0);
            }
        });
    }
    
    //Kullanıcı yazdığı text dosyasını kaydetmek istediğinde çalışan metot.
    public void Save(NotepadGui n){
        n.getSave().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                try{
                    JTextArea inputroute=new JTextArea("");
                    String fileName = JOptionPane.showInputDialog("Enter file name");//String finalFileName = fileName.getText();
                    if(fileName.isEmpty()){
                        JOptionPane.showMessageDialog(new JFrame(), "Kaydetmek için bir dosya adı giriniz!");
                    }
                    else{
                        FileWriter outFile = new FileWriter(fileName +".txt",true);
                        outFile.write(inputroute.getText());
                        n.getTextArea().write(outFile);
                        outFile.close();
                    }
                } catch(FileNotFoundException e){
                    Component f = null;
                    JOptionPane.showMessageDialog(f,"File not found.");
                } catch(IOException e){
                    Component f = null;
                    JOptionPane.showMessageDialog(f,"Error.");
                } 
            }
        });
    }
    //Normalde textArea'yı dinleyen metot burada. Fakat command tasarım deseni istendiği için buradaki kod kullanılmadı. (NotepadGui constructor'una bakabilirsiniz)
    // Backspace ve space tuşlarına duyarlı bir metot. Yani siz hata yaptığınızda düzeltmek için silme tuşuna bastığınızda üzeri boyanmış olan hatalı kelimelerinizin
    //boyalarını kaldırarak sizin daha rahat görmenizi sağlıyor. Ayrıca hata kontrolünü ise space tuşuna duyarlı yaptığımız için siz kelimenizi tamamlayıp space
    //tuşuna basmadan hata kontrolünü gerçekleştirmiyoruz. Diğer türlüsü kaynak israfı olurdu ve hatalara sebebiyet verirdi.
    //Bununla birlikte hata boyama işlemi de space tuşuna duyarlı bir şekilde yapılmıştır.
    public void textAreaListener(NotepadGui n){
        
        n.getTextArea().addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent ke) {
                n.getStack().push((char)ke.getKeyChar());
                //Kullanıcı yanlış kelimeyi düzeltirken kelimedeki highlight ı kaldırır. Görülmesi kolaylaşır
                if((int)ke.getKeyChar()==8){
                    //System.out.println("backspace!!!!");
                    n.getTextArea().getHighlighter().removeAllHighlights();
                    n.getStack().clear();
                    
                    for(int i=0;i<n.getTextArea().getText().length();i++){
                        n.getStack().push(n.getTextArea().getText().charAt(i));
                    }
                }
                
                hatalariBoya(n);
                //System.out.println(highlightTag);
                if(n.getHighlightTag()!= null){
                    n.getTextArea().getHighlighter().removeHighlight(n.getHighlightTag()); // Kullanıcı tekrar yazmaya başladığında arama yapılmış olan mavi highlightları siler
                }
                char ch = ke.getKeyChar();
                InputMap imap = n.getTextArea().getInputMap(JComponent.WHEN_FOCUSED);
                imap.put(KeyStroke.getKeyStroke("SPACE"), "spaceAction");
                ActionMap amap = n.getTextArea().getActionMap();
                amap.put("spaceAction", new AbstractAction(){
                    public void actionPerformed(ActionEvent e) {
                        //System.out.println("Space Pressed: " + textArea.getText());
                        try {
                            errorCheck(n);
                        } catch (IOException ex) {
                            System.out.println("Bilinmeyen bir hata meydana geldi.");
                        }
                    }
                });
                
            }

            @Override
            public void keyPressed(KeyEvent ke) {
                     
            }

            @Override
            public void keyReleased(KeyEvent ke) {
               
            }
        });
        
    }
    
    //Bu metot çağırıldığında textArea içindeki yazılı metini bir String olarak alıp üzerinde sözlük ile kontrol edilip hataliKelimeler listesinde
    //tutulan kelimeleri aldığı string üzerindeki kelime offset'ine göre textArea'da boyama yapıyor. 
    public void hatalariBoya(NotepadGui n){
        String tempTextAreaStr = new String(n.getTextArea().getText());
        Highlighter.HighlightPainter painter3 = 
            new DefaultHighlighter.DefaultHighlightPainter( new Color(255, 192, 203));
        if(!n.getHataliKelimeler().isEmpty()){
            
            //Iterator kullanıldı-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
            Iterator<String> it = n.getHataliKelimeler().iterator();
            int indexForIterator =0;
            while(it.hasNext()){
                String temp = it.next();
                int offset = tempTextAreaStr.indexOf(n.getHataliKelimeler().get(indexForIterator));
                int length = n.getHataliKelimeler().get(indexForIterator).length();
                while(offset != -1){
                    try {
                        //Burada böyle bir if bulunmöasının sebebi : highlight'ı bir geriye uygulamayınca highlight takılı kalıyor 
                        //ve ne yazılırsa yazılsın kurtulamıyorsunuz. Bu yüzden 2 geriden geliyor. İlk başta kelimenin hepsini kaplamıyor ama siz bir karakter daha
                        //yazdığınızda, son harfi haghlight olmamış kelimeyi de kapatmış oluyoruz :)
                        if(tempTextAreaStr.length() >=offset+length+1){
                            n.getTextArea().getHighlighter().addHighlight(offset, offset + length, painter3);
                            offset = tempTextAreaStr.indexOf(n.getHataliKelimeler().get(indexForIterator), offset+1); 
                        }
                        else{
                            n.getTextArea().getHighlighter().addHighlight(offset, offset + length-1, painter3);
                            offset = tempTextAreaStr.indexOf(n.getHataliKelimeler().get(indexForIterator), offset+1);
                        }
                        
                    } catch (BadLocationException ex) {
                    //System.out.println("HATA!");
                    }
                   
                }
                
                indexForIterator++;    
            }
        }
    }
    //Girilen karakterin integer olup olmadığını kontrol ediyor.
    static boolean isInt(String s)  // assuming integer is in decimal number system
    {
        for(int a=0;a<s.length();a++)
        {
            if(a==0 && s.charAt(a) == '-') continue;
            if( !Character.isDigit(s.charAt(a)) ) return false;
        }
        return true;
    }
    
    //TextArea'daki kelimeleri alıp sözlükteki kelimelerle karşılaştırıyor. Single transposition varsa düzeltiyor.
    public void errorCheck(NotepadGui n) throws IOException{
        String text = new String(n.getTextArea().getText());
        String[] dizi = text.split("\\s");
        //Array Collections interface'ini implement etmediği için burada iterator
        //kullanmak mantıksız olur.
        for (int i = 0; i < dizi.length; i++) {
            if (dizi[i].contains(".")) {
                dizi[i] = dizi[i].replace(".", "");
            }
            if (dizi[i].contains(",")) {
                dizi[i] = dizi[i].replace(",", "");
            }
            if (dizi[i].contains(";")) {
                dizi[i] = dizi[i].replace(";", "");
            }
            if (dizi[i].contains(":")) {
                dizi[i] = dizi[i].replace(":", "");
            }
            if (dizi[i].contains("!")) {
                dizi[i] = dizi[i].replace("!", "");
            }
            if (dizi[i].contains("?")) {
                dizi[i] = dizi[i].replace("\\?", "");
            }

            // dizi listesinde textteki bütün kelimeler tek tek mevcut, noktalama işaretlerinden arındırıldı
        }      
        
        int uzunluk = dizi.length;
        for (int t = 0; t < uzunluk; t++) {
            dizi[t] = dizi[t].toLowerCase();
        }
        
        ArrayList<String> words = new ArrayList<String>();
        
        //BufferedReader'ı iterable yapmış birkaç örnek gördüm fakat o zaman
        //kendi custom BufferedReaderIterable class'ımızı oluşturmamuz gerekirdi.
        //
        BufferedReader oku;
        try {
            oku = new BufferedReader(new FileReader("sözlük.txt"));
            boolean state = true;
            while (state) {
                String satir = oku.readLine();
                if (satir != null) {
                    words.add(satir);
                } else {
                    state = false;
                } // while döngüsü ile words.txt dosyasındaki kelimeler arrayliste atıldı
            }
            int hatayazdirSayisi =1;
            //Burada array gezildiği için bunu iterable yapmıyorum.
            for (int a = 0; a < uzunluk; a++) {
                if (words.contains(dizi[a]) || isInt(dizi[a]) == true) {
                    //System.out.println(dizi[a] + "  kelimesi sözlük.txt dosyasında vardır (veya integer değerdir).");
                    //Performans iyileştirmesi için notepad açık olduğu sürece bir static final bir değişkende tutulabilirdi.
                    //Çok uzun bir dosya olmadığı için gerek duyulmadı.
                } else {
                    ArrayList<String> kontrollist = new ArrayList<String>();
                    for (int b = 0; b < words.size(); b++) {
                        if (words.get(b).length() == dizi[a].length()) {
                            kontrollist.add(words.get(b));
                        }
                    }//aynı uzunlukta kelimeler kontrollistte
                    //karmaşıklık üstel olarak artmasın diye önce önce aynı uzunluktaki kelimeler bulundu.
                    
                    //Iterator kullanımı-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
                    Iterator<String> it = kontrollist.iterator();
                    int indexForIterator=0;
                    while(it.hasNext()){
                        String tempForIterate = it.next();
                        int hatasay=0;
                        for (int m=0; m<kontrollist.get(indexForIterator).length();m++){
                            if(kontrollist.get(indexForIterator).charAt(m) != dizi[a].charAt(m)){
                                hatasay++;
                            }
                        }
                        if(hatasay==2){
                            //Hata sayısı single tranposition olabilecek stringler olabilir fakat gerçekte bambaşka kelime olabilir.
                            //Örnek olarak Today. otday şeklinde yazılırsa 2 hatalı char bulunur ve bu single transpositiondır ve otomatik düzeltilir.
                            //peki modey gibi bir string olsaydı ne olurdu. Bu da 2 hatadan dolayı single tranposition olarak görülebilirdi.
                            //Bunu engellemek için stringlerdeki karakterler sıralandı ve bir kontrol daha yapıldı.
                            char[] hatalikelime = dizi[a].toCharArray();
                            Arrays.sort(hatalikelime);
                            char[] dogrukelime = kontrollist.get(indexForIterator).toCharArray();
                            Arrays.sort(dogrukelime);
                            int sayac=0;
                            for(int p=0;p<dogrukelime.length;p++){
                                if(dogrukelime[p]==hatalikelime[p]){
                                    sayac++;
                                }
                            }

                            if(sayac==dogrukelime.length){
                                String dogruKelimeStr = kontrollist.get(indexForIterator);
                                String eskiKelime = dizi[a];
                                String tempTextAreaStr = new String(n.getTextArea().getText());
                                tempTextAreaStr = tempTextAreaStr.replace(eskiKelime, dogruKelimeStr);
                                
                                n.getTextArea().setText(tempTextAreaStr);
                                hatalariBoya(n);
                            }

                        }
                        indexForIterator++;
                        
                    }
                    n.getHataliKelimeler().add(dizi[a]);
                    hatalariBoya(n);
                    
                    
                }
            }
            
            
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(new JFrame(), "Sözlük dosyası okunurken hata meydana geldi!");
        }
        // words.txt dosyasındaki kelimelerin atılacağı arraylist tanımlandı

    }
    
    
    //geri alma işlemi için kullanılan metot. Stack kullanılıyor bunun için
    // Command tasarım deseniyle gerçekleştirimi için yorum satırına alıyorum.
    public void undo(NotepadGui n){
        n.getUndoButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if(n.getTextArea().getText().isEmpty()){
                    JOptionPane.showMessageDialog(new JFrame(), "Daha fazla geri alma işlemi yapılamaz!");
                }
                else{
                    n.getStack().pop();
                    String temp= new String(n.getStack().realCopy());
                    n.getTextArea().setText(temp);
                }
            }
        });
    }
    
    //String change tuşuna bastığınızda yeni bir frame açıyor. oradaki işlemler için bu frame kullanılıyor. Tüm olası hata kontrolleri handle edildi.
    public void stringChangeFrame(String str,NotepadGui n){
        n.getChangeStrButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                JDialog changeDialog = new JDialog(n.getFrame());
                changeDialog.setSize(570, 100);
                changeDialog.setResizable(false);
                BorderLayout layout = new BorderLayout();
                Container panel = new Container();
                changeDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                JLabel textLabel = new JLabel("String Değiştirme İşlemi");
                JLabel textLabel2 = new JLabel("Değiştiren (Yeni) Str");
                JLabel textLabel3 = new JLabel("Değişen (Eski) Str");
                JTextField changeInputField = new JTextField(10);
                changeInputField.setMaximumSize(new Dimension(72, 36));
                Icon changeIcon = new ImageIcon("changeIcon.png");
                JButton changeButton = new JButton(changeIcon);
                JTextField changingInputField = new JTextField(10);
                changingInputField.setMaximumSize(new Dimension(90, 36));
                if(!n.getSearchField().getText().isEmpty()){
                    String st = n.getSearchField().getText();
                    changingInputField.setText(st);
                }
                FlowLayout flow = new FlowLayout();
                panel.setLayout(flow);
                panel.add(textLabel2);
                panel.add(changeInputField);
                panel.add( Box.createHorizontalStrut( 5 ) ); 
                panel.add(changeButton);
                panel.add( Box.createHorizontalStrut( 5 ) ); 
                panel.add(textLabel3);
                panel.add(changingInputField);
                changeDialog.add(textLabel,BorderLayout.NORTH);
                changeDialog.add(panel);
                
                changeButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent ae) {
                        String textAreaStr = n.getTextArea().getText().toLowerCase();
                        String searchStr  = changeInputField.getText().toLowerCase();
                        if(searchStr.isEmpty()){
                            JOptionPane.showMessageDialog(new JFrame(), "Değiştirme işlemi için bir Değiştiren (Yeni) String giriniz!");
                        }
                        else if(changingInputField.getText().isEmpty()){
                            JOptionPane.showMessageDialog(new JFrame(), "Değiştirme işlemi için bir Değişen (Eski) String giriniz!");
                        }
                        else if(textAreaStr.isEmpty()){
                            JOptionPane.showMessageDialog(new JFrame(), "Değiştirme işlemi için bir bir metin olması gerekmektedir!");
                        }
                        else{
                            String replaced = textAreaStr.replace(changingInputField.getText(), searchStr);
                            n.getTextArea().setText(replaced);
                            String t = changeInputField.getText();
                            changeInputField.setText(changingInputField.getText());
                            changingInputField.setText(t);
                        }
                        hatalariBoya(n);
                        
                    }
                });
                
                changeDialog.setVisible(true);
            }
        });
    }
    //TextArea içinde bir string aramak istendiğinde searchButton'ı dinleyen metot. Yukarıdaki searchField'e yazılan string'i aramayı sağlıyor. Bulduğu kelimeleri
    //kullanıcı rahat görebilsin diye boyuyor.
    public void search(NotepadGui n){
        n.getSearchButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                
                //Önceki aramalarda kullanılan highlightlar temizlenir.
                n.getTextArea().getHighlighter().removeAllHighlights();
                //Arama kısmı boş olursa uyarı mesajı döndürür.
                if(n.getSearchField().getText().isEmpty() ==true){
                    JOptionPane.showMessageDialog(new JFrame(), "Aramak için bir String giriniz!");
                    
                }
                else{
                    String str = n.getSearchField().getText().toLowerCase();
                    //Kullanıcı olurda string ararken arama kısmında aranacak kelimenin sonuna boşluk koyarsa onları stringten temizlemeye yarıyor.
                    char emptyChar=' ';
                    while(str.charAt(str.length()-1)==emptyChar){
                        str = str.substring(0,str.length()-2);
                    }
                    
                    String textAreaStr = n.getTextArea().getText().toLowerCase();


                    int offset = textAreaStr.indexOf(str);
                    int length = str.length();

                    while(offset != -1){
                        try {
                            n.setHighlightTag(n.getTextArea().getHighlighter().addHighlight(offset, offset + length, n.getPainter()));
                            offset = textAreaStr.indexOf(str, offset+1);
                        } catch (BadLocationException ble) {
                            System.out.println("Exception Occur!");
                        }
                    }                    
                }

            }
        });
    }
}
