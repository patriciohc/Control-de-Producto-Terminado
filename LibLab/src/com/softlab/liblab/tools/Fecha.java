/*
 * Fecha.java
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

package com.softlab.liblab.tools;

import java.util.Calendar;

public class Fecha
{
    private int dia;
    private int mes;
    private int año;
    private String lote;

    public Fecha(int año, int mes, int dia)
    {
        this.dia = dia;
        this.mes = mes;
        if (año < 99) {
            this.año = año + 2000;
        } else if(año > 2000) {
            this.año = año;
        } else { 
            this.año = 0;
        }
        lote =  null;
    }
 /*este constructor tomara el valor del año actual por default*/
    public Fecha(int dia, int mes)
    {
        this(dia,mes,0);
        Calendar c = Calendar.getInstance();
        año = c.get(Calendar.YEAR);
    }
/*este constructor tomara el valor del año y mes actual por default si es que es
menor que 31 para mayor a 31 creara un fecha con el primer dia del año indicado*/
    public Fecha(int dia_año)
    {
        if (dia_año < 31) {
            Calendar c = Calendar.getInstance();
            this.dia = dia_año;
            mes = c.get(Calendar.MONTH) + 1;
            año = c.get(Calendar.YEAR);
            lote = null;
        } else { // se asume que año mayor que 2000
            this.dia = 1;
            this.mes = 1;
            this.año = dia_año;
            lote =  null;
        }
    }

/*este constructor tomara fecha de hoy por default*/
    public Fecha()
    {
        Calendar c = Calendar.getInstance();
        dia = c.get(Calendar.DATE);
        mes = c.get(Calendar.MONTH) + 1;
        año = c.get(Calendar.YEAR);
        lote =  null;
    }

/*constructor recibe como parametro una fecha con el formato ddmmaa o lote */
    public Fecha(String fecha)
    {
        try {
            if (fecha.length() == 6 ||fecha.length() == 8 ) {
                lote = fecha;
                dia = Integer.parseInt(fecha.substring(0,2));
                mes = Integer.parseInt(fecha.substring(2,4));
                año = Integer.parseInt(fecha.substring(4,6)) + 2000;
            } else if(fecha.length() == 10) { // para formato dd/mm/aa
                año = Integer.parseInt(fecha.substring(0,4));
                mes = Integer.parseInt(fecha.substring(5,7));
                dia = Integer.parseInt(fecha.substring(8,10));
                lote = null;        
            }
        } catch(NumberFormatException e){
            System.out.println("fecha no valida");
        }
    }

    // si la fecha f1 es mayor que f2 retorna false,
    // si la fecha 2 es mayor que la fecha actual retorna flase
    // f1 deve ser menor a f2 y deven se ser fechas menores a la fecha actual 
    public static boolean validarFechas(Fecha f1, Fecha f2)
    {
        if(totalDias(f1) > totalDias(f2)) return false;
        if(totalDias(f2) > totalDias(new Fecha())) return false;
        return true;
    }

    // retorna el total de dias apartir del año 2011 has la fecha recivida
    private static int totalDias(Fecha f)
    {
        return ((f.getAño()-2011) * 360) + ((f.getMes()-1) * 30) + f.getDia();
    }

    // compara esta fecha con la fecha recivida retorna true si son iguales
    // false si son diferentes
    public boolean equals(Fecha f2)
    {
        if(totalDias(this) == totalDias(f2)) return true;
        else return false;
    }
    
    public static String mesToNum(String mes)
    {
        if(mes.equalsIgnoreCase("enero")) return "01";
        else if(mes.equalsIgnoreCase("febrero")) return "02";
        else if(mes.equalsIgnoreCase("marzo")) return "03";
        else if(mes.equalsIgnoreCase("abril")) return "04";
        else if(mes.equalsIgnoreCase("mayo")) return "05";
        else if(mes.equalsIgnoreCase("junio")) return "06";
        else if(mes.equalsIgnoreCase("julio")) return "07";
        else if(mes.equalsIgnoreCase("agosto")) return "08";
        else if(mes.equalsIgnoreCase("septiembre")) return "09";
        else if(mes.equalsIgnoreCase("octubre")) return "10";
        else if(mes.equalsIgnoreCase("noviembre")) return "11";
        else if(mes.equalsIgnoreCase("diciembre")) return "12";
        else return "0";  
    }
 
    public static int mesToInt(String mes)
    {
        if(mes.equalsIgnoreCase("enero")) return 1;
        else if(mes.equalsIgnoreCase("febrero")) return 2;
        else if(mes.equalsIgnoreCase("marzo")) return 3;
        else if(mes.equalsIgnoreCase("abril")) return 4;
        else if(mes.equalsIgnoreCase("mayo")) return 5;
        else if(mes.equalsIgnoreCase("junio")) return 6;
        else if(mes.equalsIgnoreCase("julio")) return 7;
        else if(mes.equalsIgnoreCase("agosto")) return 8;
        else if(mes.equalsIgnoreCase("septiembre")) return 9;
        else if(mes.equalsIgnoreCase("octubre")) return 10;
        else if(mes.equalsIgnoreCase("noviembre")) return 11;
        else if(mes.equalsIgnoreCase("diciembre")) return 12;
        else return 0;  
    }
        
    public static String intToMes(int mes)
    {
        if(mes == 1) return "Enero";
        else if(mes == 2) return "Febrero";
        else if(mes == 3) return "Marzo";
        else if(mes == 4) return "Abril";
        else if(mes == 5) return "Mayo";
        else if(mes == 6) return "Junio";
        else if(mes == 7) return "Julio";
        else if(mes == 8) return "Agosto";
        else if(mes == 9) return "Septiembre";
        else if(mes == 10) return "Octubre";
        else if(mes == 11) return "Noviembre";
        else if(mes == 12) return "Diciembre";
        else return null;  
    }

    /*----------metodos get -------------------------*/
    public int getAño() 
    {
        return año;
    }

    public int getDia()
    {
        return dia;
    }

    public int getMes()
    {
        return mes;
    }

    // retorna el dia en un string de dos digitos
    public String getDiaS()
    {
        if(dia < 10) return "0"+dia;
        else  return String.valueOf(dia);
    }

       // retorna el mes en un string de dos digitos
    public String getMesS()
    {
        if(mes < 10) return "0"+mes;
        else return String.valueOf(mes);
    }

    // retorna los dos ultimos digitos del año como string
    public String getAñoS()
    {
        int año = this.año - 2000; 
        return String.valueOf(año);
    }

    public String getFormFecha()
    {
        return dia+"-"+mes+"-"+año;
    }
    
    public String getFecha()
    {
        return dia+" de "+intToMes(mes)+" de "+año+" ";
    }

    // regresa la fecha en un String en el formato que maneja MySQL
    public String getFormMySQL()
    {
        return año+"-"+getMesS()+"-"+getDiaS();
    }
    
    public void setMes(int mes)
    {
        this.mes = mes;
    }
    
    public void setAño(int año)
    {
        this.año = año;
    }

    public void setDia(int dia)
    {
        this.dia = dia;
    }

    // regresa la fecha en el formato de lote que normalmente se utilza
    public String getLote()
    {
        if (lote ==  null) {
            String diaS = getDiaS();
            String mesS = getMesS();
            return diaS+mesS+(año-2000);
        } else { 
            return lote;
        }
    }

    // incremen la fecha un dia
    public void incrementarDia()
    {
        if (dia >= 30) {
            if(mes >= 12) {
                dia = 1;
                mes = 1;
                año++;
            } else {
                dia = 1;
                mes++;
            }
        } else { 
            dia++;
        }
    }

    // incrementa la fecha n meses
    public void incrementarMes(int n)
    {
        int años = 0;
        mes = mes + n;
        if (mes > 12) {
            años = mes / 12;
            mes = mes % 12;
            if(mes == 0) {
                mes = 12;
                años = años -1;
            }
        }
        año = año + años;
    }
    
    public String getFechaSinDiaMySQL()
    {
        return año+"-"+getMesS()+"%";
    }

    // decrementa fecha un mes
    public void decrementarMes(int n)
    {
        mes = mes - n;
        while(mes <= 0) {
            mes = 12 - mes;
            año = año -1;
        }
    }
    
    @Override
    public String toString()
    {
        return año+"/"+getMesS()+"/"+getDiaS();
    }
    
}
