/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import javax.swing.table.DefaultTableModel;
import javax.swing.ImageIcon;
/**
 *
 * @author expertoweb
 */
public class MyTableModel extends DefaultTableModel {
    //public Object getValueAt(int row, int column) {
    //    return "" + (row * column);
    //}
    
    @Override
    public boolean isCellEditable(int row, int column) {
       //all cells false
       return false;
    }
  
    public MyTableModel(int cols, int rows){
        super(cols,rows);
        //super.setColumnIdentifiers(new Vector());
    }
    @Override
    public Class<?> getColumnClass(int column) {
        return ImageIcon.class;
    }
}
