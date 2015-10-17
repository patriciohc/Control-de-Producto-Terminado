/*
 * Tabla.java
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
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class Tabla extends DefaultTableModel{
    

    public Tabla(Object[][] object, String... string)
    {
        super(object,string);
    } 
    
    public Tabla()
    {
        super();
    } 
    
    
        // llena el DefaultTableModel con el ResultSet apartir del n
    public boolean fill(ResultSet rs )
    {
        int noRow = 0;
        if(rs == null) 
            return false; // retorna false si rs es null
        try {
            ResultSetMetaData rsMD = rs.getMetaData();
            int nc = rsMD.getColumnCount(); // numero de columnas
            String[] labels = new String[nc];
            String[] nuevaFila = new String[nc];
            String[] filaVacia = new String[nc];
            for(int i = 0 ; i < nc; i++) 
                labels[i] = rsMD.getColumnName(i + 1); // nombre de columnas
            this.setColumnIdentifiers(labels);
            while(rs.next()) {
                for(int i = 0;  i< nc; i++) {
                    nuevaFila[i] = rs.getString(i + 1);
                }
                this.addRow(nuevaFila);
                noRow++;
            }
            this.addRow(filaVacia); // agrega dos filas vacias
            this.addRow(filaVacia);
            if(noRow == 0) return false; // retorna 1 si no hay datos
            return true;  // retorna 0 si lleno correctamente
            
        } catch(SQLException e) {
            JOptionPane.showMessageDialog(null, "error SQL");
            return false; 
        }
    }
    

    public void clear(){
        int filas = this.getRowCount();
        for(int i = 1; i<=filas; i++) this.removeRow(0);
    }
    
    
}
