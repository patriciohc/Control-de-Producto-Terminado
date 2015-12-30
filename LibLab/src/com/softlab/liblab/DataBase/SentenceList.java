/*
 * SentenceList.java
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

package com.softlab.liblab.DataBase;

public class SentenceList
{   
    // busca coincidencias en la tabla producto con respecto al nombre
    //de los productos regitrados
    public static String prediccion(String nombre)
    {
        String query = "SELECT NOMBRE"
                  + " FROM producto"
                  + " WHERE NOMBRE LIKE '"+nombre+"%'";
        return query;   
    }
 
    //elimina registro
    public static String eliminarDato(String tabla, String nameKey, int key)
    {
        return String.format("DELETE FROM %s WHERE %s = %d", tabla, nameKey, key);
    }
    
// regresa lo registrado con la fecha indicada
    public static String getRegistrosFecha(String f)
    {  
        return String.format(
            "SELECT ID_ANALISIS, LOTE, CANTIDAD, registro.CLIENTE_PROVEEDOR, "
            +"producto.NOMBRE, CERTIFICADO "
            +"FROM registro INNER JOIN producto ON producto.ID = registro.PRODUCTO "
            +"WHERE FECHA LIKE '%s'  ORDER BY ID_ANALISIS", f);
    }
    
    // regresa lo registrado con el nombre indicado
    public static String getRegistrosNombre(String nombre, int n)
    {     
        return String.format("SELECT ID_ANALISIS, LOTE, CANTIDAD, CLIENTE_PROVEEDOR, NOMBRE, CERTIFICADO"
                            +" FROM registro WHERE NOMBRE LIKE '%s' and ID_ANALISIS >=(select max(ID_ANALISIS)-%d from registro)"
                            +" ORDER BY ID_ANALISIS", nombre, n);
    }
    
  // regresa el numero de elementos 
    public static String getCount(String tabla)
    {       
        return String.format("SELECT COUNT(*) AS n FROM %s",tabla);
    }
    
}
