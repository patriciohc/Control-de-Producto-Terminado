/*
 * TablaAnalisis.java
 * This file is part of products-control-Prosid
 *
 * Copyright (C) 2015 J.Patricio Hijuitl
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses/>.
 */

package GUI;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;

public class TablaAnalisis extends Tabla
{       
    private int nRows;
    private HashMap<String,Integer> pos;
    
    public TablaAnalisis(Object[][] object, String... string)
    {
        super(object,string);
        nRows = 0;
        pos = new HashMap<String, Integer>();
    }
    
    @Override
    public boolean fill(ResultSet rs) 
    {
        if(rs == null )
            return false; // retorna 2  si rs o dtm es null
        try {
            ResultSetMetaData rsMD = rs.getMetaData();
            int nc = rsMD.getColumnCount(); // numero de columnas
            String[] labels = new String[nc];
            String[] nuevaFila = new String[2];
            for (int i = 0 ; i < nc; i++) 
                labels[i] = rsMD.getColumnName(i + 1);
            if (rs.next()) {
                for(int i = 0;  i< nc; i++) {
                    nuevaFila[0] = labels[i];
                    nuevaFila[1] = rs.getString(i + 1);
                    //System.out.println(nuevaFila[0]+" "+nRows);
                    pos.put(nuevaFila[0], new Integer(nRows));
                    this.addRow(nuevaFila);
                    nRows++;
                }
            }
            return true;  // retorna 0 si lleno correctamente       
        } catch (Exception e) {
            System.out.println("error en funcion llenarTableModel: "+e);
            return false; // 3 en caso de un error desconocido
        }
    }
    
    @Override
    public void clear(){
        int filas = this.getRowCount();
        for (int i = 1; i<=filas; i++)
            this.removeRow(0);
        nRows = 0;
        pos.clear();
    }
     
    public Float getDato(String elemento)
    {
        Float dato;
        try {
            //System.out.println("sacando dato .."+elemento+" = "+pos.get(elemento));
            dato = Float.parseFloat((String)getValueAt(pos.get(elemento), 1));
        } catch (NumberFormatException ex){
            return null;
        }
        return dato;
    }
    
    public ArrayList<Object[]> getDatos()
    {
        int size = getRowCount();
        ArrayList<Object[]> datos = new ArrayList<Object[]>();
        for (int i = 0; i<size; i++) {
            Object[] dato = new Object[2]; 
            Float valor;
            try {
                valor = Float.parseFloat((String)getValueAt(i, 1));
            } catch(NumberFormatException e) {
                continue;
            } catch(NullPointerException e) {
                continue;
            }
            dato[0] = (String)getValueAt(i, 0);
            dato[1] = valor;
            datos.add(dato);
        }
        return datos;
    }
    
   @Override
   public boolean isCellEditable (int row, int column)
   {
        if (column == 1){ 
            return true;
        } else {  
            return false;
        }
   }
      
}
