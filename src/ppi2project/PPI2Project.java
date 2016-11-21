/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ppi2project;

/**
 *
 * @author joseje
 */
public class PPI2Project {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        SQLiteConn database = new SQLiteConn();

        for (int i = 50; i < 100; i++) {
            database.delete(i);
        }

        }
    }

